@echo off
echo ===================================================
echo   AI Career Copilot - Starting Local Development
echo ===================================================

start "AI Copilot Backend" /D "%~dp0backend" cmd /k "mvn spring-boot:run"
start "AI Copilot Frontend" /D "%~dp0frontend" cmd /k "npm.cmd run dev"

echo.
echo Services launched!
echo - Backend API:  http://localhost:8080
echo - Frontend Web: http://localhost:5173
echo.
echo Default Demo Credentials:
echo Email:    demo@aicopilot.com
echo Password: password123
echo ===================================================
pause
