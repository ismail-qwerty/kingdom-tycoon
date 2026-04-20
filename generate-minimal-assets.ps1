# Generate Minimal Assets for Kingdom Tycoon
Write-Host "Generating minimal assets..." -ForegroundColor Cyan

# Create directories
$assetsDir = "android\assets"
New-Item -ItemType Directory -Force -Path "$assetsDir\ui" | Out-Null
New-Item -ItemType Directory -Force -Path "$assetsDir\buildings" | Out-Null
New-Item -ItemType Directory -Force -Path "$assetsDir\backgrounds" | Out-Null
New-Item -ItemType Directory -Force -Path "$assetsDir\heroes" | Out-Null
New-Item -ItemType Directory -Force -Path "$assetsDir\audio\sfx" | Out-Null
New-Item -ItemType Directory -Force -Path "$assetsDir\audio\music" | Out-Null

Write-Host "Assets directories created" -ForegroundColor Green

# Create a simple 1x1 pixel PNG (base64 encoded)
$pngBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg=="
$pngBytes = [Convert]::FromBase64String($pngBase64)

# Create placeholder images
$files = @(
    "ui\button.png",
    "ui\panel.png",
    "ui\coin.png",
    "buildings\placeholder.png",
    "backgrounds\era1.png",
    "heroes\hero1.png"
)

foreach ($file in $files) {
    $path = Join-Path $assetsDir $file
    [System.IO.File]::WriteAllBytes($path, $pngBytes)
}

Write-Host "Placeholder images created" -ForegroundColor Green

# Create empty audio files
New-Item -ItemType File -Force -Path "$assetsDir\audio\sfx\click.ogg" | Out-Null
New-Item -ItemType File -Force -Path "$assetsDir\audio\music\theme.ogg" | Out-Null

Write-Host "Audio placeholders created" -ForegroundColor Green
Write-Host ""
Write-Host "Assets generated successfully!" -ForegroundColor Green
Write-Host "Now rebuild: .\gradlew.bat clean assembleDebug" -ForegroundColor Yellow
