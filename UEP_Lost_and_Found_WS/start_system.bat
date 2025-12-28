@echo off
echo =============================================
echo UEP Lost and Found System - Quick Start
echo =============================================
echo.
echo This script will start the backend server and then open the frontend.
echo Make sure your MySQL database is running first!
echo.
echo Default login credentials:
echo Username: admin
echo Password: uep123
echo.
pause

echo.
echo =============================================
echo Starting Backend Server...
echo =============================================
echo.
echo The backend server will start on http://localhost:8080
echo Once it's running, your browser will automatically open.
echo.

REM Start the Spring Boot backend
gradlew bootRun

echo.
echo Backend server started! Opening frontend in browser...
echo.

REM Open the frontend in the default browser
start "" "file:///c:/Users/Admin/IdeaProjects/UEP_Lost_and_Found_WS/frontend/index.html"

echo.
echo =============================================
echo UEP Lost and Found System is Ready!
echo =============================================
echo Backend: http://localhost:8080
echo Frontend: file:///c:/Users/Admin/IdeaProjects/UEP_Lost_and_Found_WS/frontend/index.html
echo.
echo Default Admin Login:
echo Username: admin
echo Password: uep123
echo.
pause
