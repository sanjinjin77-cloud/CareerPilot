. (Join-Path $PSScriptRoot 'common.ps1')

$frontendStopped = Stop-ManagedProcess -Name 'frontend'
$backendStopped = Stop-ManagedProcess -Name 'backend'

if ($frontendStopped) {
    Write-Host '[OK] Frontend stopped.' -ForegroundColor Green
} else {
    Write-Host '[INFO] No frontend process is managed by the unified launcher.' -ForegroundColor Yellow
}

if ($backendStopped) {
    Write-Host '[OK] Backend stopped.' -ForegroundColor Green
} else {
    Write-Host '[INFO] No backend process is managed by the unified launcher.' -ForegroundColor Yellow
}
