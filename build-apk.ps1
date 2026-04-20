# Build Kingdom Tycoon APK
Write-Host "Building Kingdom Tycoon APK..." -ForegroundColor Cyan
Write-Host ""

# Build debug APK
Write-Host "Building debug APK..." -ForegroundColor Yellow
.\gradlew.bat assembleDebug

# Check if build succeeded
if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "✓ APK built successfully!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Location: android\build\outputs\apk\debug\android-debug.apk" -ForegroundColor Cyan
    
    # Open folder
    $apkPath = "android\build\outputs\apk\debug"
    if (Test-Path $apkPath) {
        Write-Host ""
        Write-Host "Opening APK folder..." -ForegroundColor Yellow
        explorer.exe $apkPath
    }
} else {
    Write-Host ""
    Write-Host "✗ Build failed!" -ForegroundColor Red
    Write-Host "Check the error messages above." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Press any key to exit..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
