Write-Host "===================================================" -ForegroundColor Cyan
Write-Host "   AI Career Copilot - Starting Local Development   " -ForegroundColor Green
Write-Host "===================================================" -ForegroundColor Cyan

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$backendPath = Join-Path $projectRoot "backend"
$frontendPath = Join-Path $projectRoot "frontend"

Start-Process powershell -ArgumentList "-NoExit", "-NoProfile", "-Command", "Set-Location -LiteralPath '$backendPath'; mvn spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit", "-NoProfile", "-Command", "Set-Location -LiteralPath '$frontendPath'; npm.cmd run dev"

Write-Host "`nServices launching in dedicated windows:" -ForegroundColor Yellow
Write-Host " - Backend API:  http://localhost:8080" -ForegroundColor White
Write-Host " - Frontend Web: http://localhost:5173" -ForegroundColor White
Write-Host "`nDefault Demo Credentials:" -ForegroundColor Yellow
Write-Host " Email:    demo@aicopilot.com" -ForegroundColor White
Write-Host " Password: password123" -ForegroundColor White
Write-Host "===================================================" -ForegroundColor Cyan
