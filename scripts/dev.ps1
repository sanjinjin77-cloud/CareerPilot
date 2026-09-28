[CmdletBinding()]
param(
    [switch]$OpenBrowser,
    [switch]$SkipInstall
)

. (Join-Path $PSScriptRoot 'common.ps1')
Initialize-DevRuntime

$backendStarted = $false
$frontendStarted = $false
$backendPort = 8081
$frontendPort = 5174
$backendOut = Join-Path $script:LogDir 'backend.out.log'
$backendErr = Join-Path $script:LogDir 'backend.err.log'
$frontendOut = Join-Path $script:LogDir 'frontend.out.log'
$frontendErr = Join-Path $script:LogDir 'frontend.err.log'

try {
    if (-not (Test-TcpPort -HostName '127.0.0.1' -Port 3306)) {
        throw 'MySQL is not listening on port 3306. Start the local MySQL service first.'
    }

    $maven = Get-Command mvn.cmd -ErrorAction Stop
    $npm = Get-Command npm.cmd -ErrorAction Stop

    if (-not (Test-Path -LiteralPath (Join-Path $script:FrontendDir 'node_modules'))) {
        if ($SkipInstall) {
            throw 'Frontend dependencies are missing. Remove -SkipInstall or run npm install first.'
        }
        Write-Host '[SETUP] Installing frontend dependencies...' -ForegroundColor Cyan
        Push-Location $script:FrontendDir
        try {
            & $npm.Source install
            if ($LASTEXITCODE -ne 0) {
                throw 'Frontend dependency installation failed.'
            }
        } finally {
            Pop-Location
        }
    }

    $backendPid = Get-ListeningProcessId -Port $backendPort
    if ($null -ne $backendPid) {
        if (-not (Test-HttpEndpoint -Url "http://127.0.0.1:$backendPort/api/health")) {
            throw "Port $backendPort is owned by process $backendPid and does not expose a healthy CareerPilot backend."
        }
        Write-Host "[OK] Backend is already running (PID $backendPid)" -ForegroundColor Green
    } else {
        Write-Host '[START] Starting Spring Boot...' -ForegroundColor Cyan
        Import-DotEnv -Path (Join-Path $script:BackendDir '.env.local')
        Start-Process -FilePath $maven.Source `
            -ArgumentList 'spring-boot:run' `
            -WorkingDirectory $script:BackendDir `
            -RedirectStandardOutput $backendOut `
            -RedirectStandardError $backendErr `
            -WindowStyle Hidden | Out-Null

        if (-not (Wait-HttpEndpoint -Url "http://127.0.0.1:$backendPort/api/health" -TimeoutSeconds 75)) {
            Show-LogTail -Path $backendErr
            Show-LogTail -Path $backendOut
            throw 'Spring Boot startup timed out.'
        }
        $backendPid = Get-ListeningProcessId -Port $backendPort
        Write-ManagedProcess -Name 'backend' -ProcessId $backendPid -Port $backendPort
        $backendStarted = $true
        Write-Host "[OK] Backend started (PID $backendPid)" -ForegroundColor Green
    }

    $frontendPid = Get-ListeningProcessId -Port $frontendPort
    if ($null -ne $frontendPid) {
        if (-not (Test-HttpEndpoint -Url "http://127.0.0.1:$frontendPort/")) {
            throw "Port $frontendPort is owned by process $frontendPid and does not expose a healthy frontend."
        }
        Write-Host "[OK] Frontend is already running (PID $frontendPid)" -ForegroundColor Green
    } else {
        Write-Host '[START] Starting Vite...' -ForegroundColor Cyan
        Start-Process -FilePath $npm.Source `
            -ArgumentList @('run', 'dev', '--', '--host', '127.0.0.1', '--port', "$frontendPort", '--strictPort') `
            -WorkingDirectory $script:FrontendDir `
            -RedirectStandardOutput $frontendOut `
            -RedirectStandardError $frontendErr `
            -WindowStyle Hidden | Out-Null

        if (-not (Wait-HttpEndpoint -Url "http://127.0.0.1:$frontendPort/" -TimeoutSeconds 45)) {
            Show-LogTail -Path $frontendErr
            Show-LogTail -Path $frontendOut
            throw 'Vite startup timed out.'
        }
        $frontendPid = Get-ListeningProcessId -Port $frontendPort
        Write-ManagedProcess -Name 'frontend' -ProcessId $frontendPid -Port $frontendPort
        $frontendStarted = $true
        Write-Host "[OK] Frontend started (PID $frontendPid)" -ForegroundColor Green
    }

    Write-Host ''
    Write-Host 'CareerPilot is ready:' -ForegroundColor Green
    Write-Host "  Web:     http://127.0.0.1:$frontendPort"
    Write-Host "  Backend: http://127.0.0.1:$backendPort/api/health"
    Write-Host '  Logs:    .dev/logs/'
    Write-Host ''
    Write-Host 'Stop services: npm run dev:stop'

    if ($OpenBrowser) {
        Start-Process "http://127.0.0.1:$frontendPort"
    }
} catch {
    Write-Host "[ERROR] $($_.Exception.Message)" -ForegroundColor Red
    if ($frontendStarted) {
        Stop-ManagedProcess -Name 'frontend' | Out-Null
    }
    if ($backendStarted) {
        Stop-ManagedProcess -Name 'backend' | Out-Null
    }
    exit 1
}
