@echo off
echo =============================================
echo Starting UEP Lost and Found Backend Server
echo =============================================
echo.
echo This will start the Spring Boot backend server.
echo Make sure your MySQL database is running first!
echo.
echo Server will start on: http://localhost:8080
echo.
pause

echo.
echo Starting backend server...
echo This may take 30-60 seconds to fully load...
echo.

gradlew bootRun

echo.
echo Backend server has stopped.
echo.
pause
