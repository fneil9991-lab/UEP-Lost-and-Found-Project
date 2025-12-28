@echo off
echo =============================================
echo Opening UEP Lost and Found Frontend
echo =============================================
echo.
echo This will open the frontend in your default browser.
echo Make sure the backend server is running first!
echo.
echo Frontend URL: file:///c:/Users/Admin/IdeaProjects/UEP_Lost_and_Found_WS/frontend/index.html
echo.

REM Open the frontend in the default browser
start "" "file:///c:/Users/Admin/IdeaProjects/UEP_Lost_and_Found_WS/frontend/index.html"

echo Frontend opened in your browser!
echo.
echo If you get any connection errors, make sure the backend is running:
echo gradlew bootRun
echo.
pause
