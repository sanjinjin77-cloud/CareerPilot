Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$script:ProjectRoot = [System.IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..'))
$script:BackendDir = Join-Path $script:ProjectRoot 'backend'
$script:FrontendDir = Join-Path $script:ProjectRoot 'career-pilot-frontend'
$script:RuntimeDir = Join-Path $script:ProjectRoot '.dev'
$script:LogDir = Join-Path $script:RuntimeDir 'logs'

function Initialize-DevRuntime {
    New-Item -ItemType Directory -Path $script:LogDir -Force | Out-Null
}

function Get-ListeningProcessId {
    param([Parameter(Mandatory = $true)][int]$Port)

    try {
        $connection = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction Stop |
            Select-Object -First 1
        if ($null -ne $connection) {
            return [int]$connection.OwningProcess
        }
    } catch {
    }

    $match = netstat -ano | Select-String -Pattern "^\s*TCP\s+\S+:$Port\s+\S+\s+LISTENING\s+(\d+)\s*$" | Select-Object -First 1
    if ($null -ne $match -and $match.Line -match '\sLISTENING\s+(\d+)\s*$') {
        return [int]$Matches[1]
    }
    return $null
}

function Test-TcpPort {
    param(
        [string]$HostName = '127.0.0.1',
        [Parameter(Mandatory = $true)][int]$Port,
        [int]$TimeoutMilliseconds = 1000
    )

    $client = [System.Net.Sockets.TcpClient]::new()
    try {
        $asyncResult = $client.BeginConnect($HostName, $Port, $null, $null)
        if (-not $asyncResult.AsyncWaitHandle.WaitOne($TimeoutMilliseconds, $false)) {
            return $false
        }
        $client.EndConnect($asyncResult)
        return $true
    } catch {
        return $false
    } finally {
        $client.Close()
    }
}

function Test-HttpEndpoint {
    param(
        [Parameter(Mandatory = $true)][string]$Url,
        [int]$TimeoutSeconds = 3
    )

    try {
        $response = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec $TimeoutSeconds
        return $response.StatusCode -ge 200 -and $response.StatusCode -lt 400
    } catch {
        return $false
    }
}

function Wait-HttpEndpoint {
    param(
        [Parameter(Mandatory = $true)][string]$Url,
        [int]$TimeoutSeconds = 60
    )

    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    do {
        if (Test-HttpEndpoint -Url $Url) {
            return $true
        }
        Start-Sleep -Milliseconds 750
    } while ((Get-Date) -lt $deadline)
    return $false
}

function Import-DotEnv {
    param([Parameter(Mandatory = $true)][string]$Path)

    if (-not (Test-Path -LiteralPath $Path)) {
        Write-Host "[WARN] $Path was not found; application.yml defaults will be used." -ForegroundColor Yellow
        return
    }

    foreach ($rawLine in Get-Content -LiteralPath $Path -Encoding UTF8) {
        $line = $rawLine.Trim()
        if (-not $line -or $line.StartsWith('#')) {
            continue
        }
        $parts = $line -split '=', 2
        if ($parts.Count -ne 2) {
            continue
        }
        $key = $parts[0].Trim()
        $value = $parts[1].Trim().Trim('"').Trim("'")
        if ($key -match '^[A-Za-z_][A-Za-z0-9_]*$') {
            Set-Item -Path "Env:$key" -Value $value
        }
    }
}

function Write-ManagedProcess {
    param(
        [Parameter(Mandatory = $true)][string]$Name,
        [Parameter(Mandatory = $true)][int]$ProcessId,
        [Parameter(Mandatory = $true)][int]$Port
    )

    Initialize-DevRuntime
    [pscustomobject]@{
        processId = $ProcessId
        port = $Port
        startedAt = (Get-Date).ToString('o')
        projectRoot = $script:ProjectRoot
    } | ConvertTo-Json | Set-Content -LiteralPath (Join-Path $script:RuntimeDir "$Name.json") -Encoding UTF8
}

function Read-ManagedProcess {
    param([Parameter(Mandatory = $true)][string]$Name)

    $path = Join-Path $script:RuntimeDir "$Name.json"
    if (-not (Test-Path -LiteralPath $path)) {
        return $null
    }
    try {
        return Get-Content -LiteralPath $path -Raw -Encoding UTF8 | ConvertFrom-Json
    } catch {
        Remove-Item -LiteralPath $path -Force -ErrorAction SilentlyContinue
        return $null
    }
}

function Remove-ManagedProcessFile {
    param([Parameter(Mandatory = $true)][string]$Name)

    $path = Join-Path $script:RuntimeDir "$Name.json"
    Remove-Item -LiteralPath $path -Force -ErrorAction SilentlyContinue
}

function Stop-ManagedProcess {
    param([Parameter(Mandatory = $true)][string]$Name)

    $record = Read-ManagedProcess -Name $Name
    if ($null -eq $record) {
        return $false
    }

    $processId = [int]$record.processId
    $port = [int]$record.port
    $listenerId = Get-ListeningProcessId -Port $port
    if ($null -ne $listenerId -and $listenerId -eq $processId) {
        Stop-Process -Id $processId -Force -ErrorAction SilentlyContinue
        $deadline = (Get-Date).AddSeconds(10)
        while ((Get-ListeningProcessId -Port $port) -eq $processId -and (Get-Date) -lt $deadline) {
            Start-Sleep -Milliseconds 300
        }
    }
    Remove-ManagedProcessFile -Name $Name
    return $true
}

function Show-LogTail {
    param(
        [Parameter(Mandatory = $true)][string]$Path,
        [int]$Lines = 50
    )

    if (Test-Path -LiteralPath $Path) {
        Get-Content -LiteralPath $Path -Tail $Lines -Encoding UTF8
    }
}
