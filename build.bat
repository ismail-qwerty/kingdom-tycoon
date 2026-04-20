@echo off
REM Kingdom Tycoon - CI/CD Build Script (Windows)
REM This script automates the build process for fresh developers

setlocal enabledelayedexpansion

echo =========================================
echo Kingdom Tycoon - Automated Build Script
echo =========================================
echo.

REM Step 1: Check prerequisites
echo Step 1: Checking prerequisites...
echo [INFO] Checking Python installation...
python --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Python not found. Please install Python 3.8+
    exit /b 1
)
echo [OK] Python found

echo [INFO] Checking Java installation...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java not found. Please install JDK 17
    exit /b 1
)
echo [OK] Java found

echo [INFO] Checking Gradle wrapper...
if not exist "gradlew.bat" (
    echo [ERROR] Gradle wrapper not found
    exit /b 1
)
echo [OK] Gradle wrapper found
echo.

REM Step 2: Install Python dependencies
echo Step 2: Installing Python dependencies...
echo [INFO] Installing Pillow for asset generation...
python -m pip install --quiet pillow
if %errorlevel% neq 0 (
    echo [ERROR] Failed to install Pillow
    exit /b 1
)
echo [OK] Python dependencies installed
echo.

REM Step 3: Generate placeholder assets
echo Step 3: Generating placeholder assets...
if not exist "generate_placeholder_assets.py" (
    echo [ERROR] generate_placeholder_assets.py not found
    exit /b 1
)
echo [INFO] Running asset generation script...
python generate_placeholder_assets.py
if %errorlevel% neq 0 (
    echo [ERROR] Asset generation failed
    exit /b 1
)
echo [OK] Placeholder assets generated
echo.

REM Step 4: Clean previous builds
echo Step 4: Cleaning previous builds...
echo [INFO] Running Gradle clean...
call gradlew.bat clean --quiet
if %errorlevel% neq 0 (
    echo [ERROR] Gradle clean failed
    exit /b 1
)
echo [OK] Build cleaned
echo.

REM Step 5: Run unit tests
echo Step 5: Running unit tests...
echo [INFO] Running test suite...

set TEST_FAILED=0

echo [INFO] Running SaveSystemStressTest...
call gradlew.bat test --tests com.ismail.kingdom.SaveSystemStressTest --quiet
if %errorlevel% neq 0 (
    echo [ERROR] SaveSystemStressTest failed
    set TEST_FAILED=1
) else (
    echo [OK] SaveSystemStressTest passed
)

echo [INFO] Running BalanceSimulator...
call gradlew.bat test --tests com.ismail.kingdom.BalanceSimulator --quiet
if %errorlevel% neq 0 (
    echo [ERROR] BalanceSimulator failed
    set TEST_FAILED=1
) else (
    echo [OK] BalanceSimulator passed
)

echo [INFO] Running AdQAChecklist...
call gradlew.bat test --tests com.ismail.kingdom.AdQAChecklist --quiet
if %errorlevel% neq 0 (
    echo [ERROR] AdQAChecklist failed
    set TEST_FAILED=1
) else (
    echo [OK] AdQAChecklist passed
)

echo [INFO] Running ReleaseChecklist...
call gradlew.bat test --tests com.ismail.kingdom.ReleaseChecklist --quiet
if %errorlevel% neq 0 (
    echo [ERROR] ReleaseChecklist failed
    set TEST_FAILED=1
) else (
    echo [OK] ReleaseChecklist passed
)

if !TEST_FAILED! equ 1 (
    echo [ERROR] Some tests failed. Check test reports in build\reports\tests\
    exit /b 1
)

echo [OK] All tests passed
echo.

REM Step 6: Build debug APK
echo Step 6: Building debug APK...
echo [INFO] Running assembleDebug...
call gradlew.bat android:assembleDebug
if %errorlevel% neq 0 (
    echo [ERROR] Debug build failed
    exit /b 1
)
echo [OK] Debug APK built successfully
echo.

REM Step 7: Verify APK
echo Step 7: Verifying APK...
set APK_PATH=android\build\outputs\apk\debug\android-debug.apk
if not exist "%APK_PATH%" (
    echo [ERROR] APK not found at expected location
    exit /b 1
)
echo [OK] APK found: %APK_PATH%
echo.

REM Step 8: Run lint checks (optional)
echo Step 8: Running lint checks...
echo [INFO] Running Android lint...
call gradlew.bat android:lint --quiet
if %errorlevel% neq 0 (
    echo [WARN] Lint checks failed (non-critical)
)
echo [OK] Lint checks completed
echo.

REM Summary
echo =========================================
echo Build Summary
echo =========================================
echo [OK] Prerequisites: OK
echo [OK] Assets generated: OK
echo [OK] Tests passed: OK
echo [OK] Debug APK built: OK
echo.
echo APK Location: %APK_PATH%
echo.
echo Next steps:
echo   1. Install APK: adb install %APK_PATH%
echo   2. Run on device: gradlew.bat android:run
echo   3. View test reports: start build\reports\tests\test\index.html
echo.
echo [OK] Build completed successfully!
echo =========================================

endlocal
