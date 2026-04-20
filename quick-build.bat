@echo off
REM Quick build script for Kingdom Tycoon

echo Building Debug APK...
call gradlew clean assembleDebug

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo BUILD SUCCESSFUL!
    echo ========================================
    echo.
    echo APK Location:
    echo %CD%\android\build\outputs\apk\debug\android-debug.apk
    echo.
    echo You can now install this APK on your Android device
    echo.
) else (
    echo.
    echo ========================================
    echo BUILD FAILED!
    echo ========================================
    echo Check the error messages above
    echo.
)

pause
