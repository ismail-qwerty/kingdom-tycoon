@echo off
echo ========================================
echo Kingdom Tycoon APK Builder
echo ========================================
echo.
echo Select build type:
echo 1. Debug APK (for testing)
echo 2. Release APK (unsigned)
echo 3. Release APK (signed)
echo 4. Android App Bundle (AAB for Play Store)
echo 5. Clean build
echo.
set /p choice="Enter your choice (1-5): "

if "%choice%"=="1" goto debug
if "%choice%"=="2" goto release
if "%choice%"=="3" goto signed
if "%choice%"=="4" goto bundle
if "%choice%"=="5" goto clean
goto end

:debug
echo.
echo Building Debug APK...
call gradlew assembleDebug
echo.
echo Debug APK location:
echo android\build\outputs\apk\debug\android-debug.apk
goto end

:release
echo.
echo Building Release APK (unsigned)...
call gradlew assembleRelease
echo.
echo Release APK location:
echo android\build\outputs\apk\release\android-release-unsigned.apk
goto end

:signed
echo.
echo Building Signed Release APK...
echo Make sure you have configured signing in android/build.gradle
call gradlew assembleRelease
echo.
echo Signed Release APK location:
echo android\build\outputs\apk\release\android-release.apk
goto end

:bundle
echo.
echo Building Android App Bundle (AAB)...
call gradlew bundleRelease
echo.
echo AAB location:
echo android\build\outputs\bundle\release\android-release.aab
goto end

:clean
echo.
echo Cleaning build...
call gradlew clean
echo Clean complete!
goto end

:end
echo.
echo ========================================
echo Build process complete!
echo ========================================
pause
