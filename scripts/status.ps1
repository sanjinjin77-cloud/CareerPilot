. (Join-Path $PSScriptRoot 'common.ps1')

function Get-ProcessSource {
    param(
        [Parameter(Mandatory = $true)][string]$Name,
        [int]$ProcessId
    )

    if ($ProcessId -le 0) {
        return '-'
    }
    $record = Read-ManagedProcess -Name $Name
    if ($null -ne $record -and [int]$record.processId -eq $ProcessId) {
        return 'unified-script'
    }
    return 'external'
}

$mysqlPid = Get-ListeningProcessId -Port 3306
$backendPort = 8081
$frontendPort = 5174
$backendPid = Get-ListeningProcessId -Port $backendPort
$frontendPid = Get-ListeningProcessId -Port $frontendPort

$rows = @(
    [pscustomobject]@{
        Service = 'MySQL'
        Status = if (Test-TcpPort -HostName '127.0.0.1' -Port 3306) { 'UP' } else { 'DOWN' }
        Port = 3306
        PID = $mysqlPid
        Source = 'system'
    },
    [pscustomobject]@{
        Service = 'Backend'
        Status = if (Test-HttpEndpoint -Url "http://127.0.0.1:$backendPort/api/health") { 'UP' } else { 'DOWN' }
        Port = $backendPort
        PID = $backendPid
        Source = Get-ProcessSource -Name 'backend' -ProcessId $backendPid
    },
    [pscustomobject]@{
        Service = 'Frontend'
        Status = if (Test-HttpEndpoint -Url "http://127.0.0.1:$frontendPort/") { 'UP' } else { 'DOWN' }
        Port = $frontendPort
        PID = $frontendPid
        Source = Get-ProcessSource -Name 'frontend' -ProcessId $frontendPid
    }
)

$rows | Format-Table -AutoSize
