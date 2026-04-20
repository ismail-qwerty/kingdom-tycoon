# Technology Stack

## Programming Languages

### Kotlin 2.1.0
- **Primary Language**: All game logic written in Kotlin
- **Target JVM**: Java 17 bytecode
- **Features Used**:
  - Data classes for models
  - Sealed classes for type-safe states
  - Extension functions for utilities
  - Coroutines (implied for async operations)
  - kotlinx.serialization for save/load

### Java 17
- **Source Compatibility**: Java 17
- **Target Compatibility**: Java 17
- **Usage**: Required by LibGDX and Android toolchain

## Game Framework

### LibGDX 1.14.0
Cross-platform game development framework:
- **Graphics**: OpenGL ES 2.0/3.0 rendering
- **Input**: Touch, keyboard, mouse handling
- **Audio**: Sound effects and music playback (OGG format)
- **File I/O**: Asset loading via `Gdx.files.internal()`
- **Preferences**: Key-value storage for save data
- **Scene2D**: UI framework for widgets and layouts
- **Math**: Vector, matrix, and geometry utilities

**Key LibGDX Classes Used**:
- `Game` - Application lifecycle
- `Screen` - Screen management
- `Stage` - Scene2D UI container
- `SpriteBatch` - 2D rendering
- `Texture` - Image loading
- `Sound` / `Music` - Audio playback
- `Preferences` - Data persistence
- `OrthographicCamera` - 2D camera

## Android Platform

### Android SDK
- **Min SDK**: API 21 (Android 5.0 Lollipop)
- **Target SDK**: API 35 (Android 15)
- **Compile SDK**: API 35

### Android Gradle Plugin
- **Version**: 8.9.3
- **Build Tools**: Latest from SDK

### Google Play Services
- **AdMob**: Rewarded video, interstitial, and banner ads
- **Play Games Services**: Cloud save and leaderboards (implied by GooglePlayHelper.kt)

## Build System

### Gradle 9.4.0
- **Wrapper**: Included in project (`gradlew` / `gradlew.bat`)
- **Build Script Language**: Groovy
- **Multi-module Project**: `core`, `android`, `desktop`

### Gradle Plugins
```groovy
// Root build.gradle
classpath "com.android.tools.build:gradle:8.9.3"
classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.0"
classpath "org.jetbrains.kotlin:kotlin-serialization:2.1.0"
```

### Gradle Configuration
- **Incremental Compilation**: Enabled for faster builds
- **Asset Generation**: Custom task `generateAssetList` creates `assets.txt`
- **Build Variants**: `debug` (test ads) and `release` (production ads)

## Dependencies

### Core Module
```gradle
// Implied from project structure
implementation "com.badlogicgames.gdx:gdx:1.14.0"
implementation "org.jetbrains.kotlin:kotlin-stdlib:2.1.0"
implementation "org.jetbrains.kotlinx:kotlinx-serialization-json:1.x.x"
```

### Android Module
```gradle
// Implied from project structure
implementation "com.badlogicgames.gdx:gdx-backend-android:1.14.0"
implementation "com.google.android.gms:play-services-ads:21.x.x"
implementation "com.google.android.gms:play-services-games:23.x.x"
```

### Desktop Module
```gradle
// Implied from project structure
implementation "com.badlogicgames.gdx:gdx-backend-lwjgl3:1.14.0"
implementation "com.badlogicgames.gdx:gdx-platform:1.14.0:natives-desktop"
```

## Serialization

### kotlinx.serialization
- **Format**: JSON
- **Usage**: Serialize `FinalGameState` to Preferences
- **Features**:
  - Type-safe serialization
  - Automatic null handling
  - Custom serializers for complex types

## Asset Pipeline

### Asset Formats
- **Images**: PNG (transparent), JPG (backgrounds)
- **Audio**: OGG Vorbis (44.1kHz)
- **Fonts**: TTF/OTF (converted to bitmap fonts)
- **Data**: JSON (configurations)

### Asset Generation Scripts
Python 3.8+ scripts for placeholder assets:
- `generate_placeholder_assets.py` - Creates placeholder textures
- `generate_placeholder_audio.py` - Generates silent audio files
- `generate_store_assets.py` - Creates Play Store graphics
- `generate-minimal-assets.ps1` - PowerShell asset generator

**Python Dependencies**:
```bash
pip install pillow  # Image manipulation
```

### Recommended Tools
- **TexturePacker**: Texture atlas creation (not currently used)
- **Hiero**: Bitmap font generation
- **Audacity**: Audio editing
- **GIMP**: Image editing

## Development Commands

### Build Commands
```bash
# Clean build
./gradlew clean build

# Build Android APK (debug)
./gradlew android:assembleDebug

# Build Android APK (release)
./gradlew android:assembleRelease

# Build Android App Bundle (release)
./gradlew android:bundleRelease

# Install to connected device
./gradlew android:installDebug

# Run on device
./gradlew android:run
```

### Testing Commands
```bash
# Run all tests
./gradlew test

# Run specific test
./gradlew test --tests SaveSystemStressTest

# Run with coverage
./gradlew testDebugUnitTest jacocoTestReport
```

### Asset Commands
```bash
# Generate placeholder assets
python3 generate_placeholder_assets.py

# Generate store assets
python3 generate_store_assets.py

# Generate asset list
./gradlew generateAssetList
```

### Desktop Testing
```bash
# Run desktop version (faster iteration)
./gradlew desktop:run

# Or use batch file
run-desktop.bat
```

### Gradle Maintenance
```bash
# Refresh dependencies
./gradlew clean --refresh-dependencies

# Show dependency tree
./gradlew android:dependencies

# Check for updates
./gradlew dependencyUpdates
```

## IDE Configuration

### Android Studio
- **Version**: Hedgehog (2023.1.1) or newer
- **JDK**: 17 (bundled with Android Studio)
- **Kotlin Plugin**: Bundled
- **Run Configuration**: `android` module

### IntelliJ IDEA
- **Compatibility**: Full support
- **Build Setting**: "Build and run using IntelliJ IDEA" enabled
- **Output Directories**: Configured in root `build.gradle`

## Version Control

### Git
- **Ignore Patterns**: `.gitignore` excludes build artifacts, IDE files, local properties
- **Attributes**: `.gitattributes` for line ending normalization

## Performance Targets

### Runtime Performance
- **Target FPS**: 60 FPS
- **Target Device**: Snapdragon 665 or equivalent
- **Memory Usage**: < 150MB RAM
- **Battery**: Optimized for idle gameplay

### Build Performance
- **Incremental Compilation**: Enabled
- **Gradle Daemon**: Enabled
- **Parallel Builds**: Supported

## Deployment

### Android App Bundle (AAB)
- **Format**: AAB for Play Store submission
- **Signing**: Keystore-based signing (keystore.properties.template)
- **ProGuard**: Configured in `proguard-rules.pro`

### APK
- **Format**: APK for direct installation
- **Split APKs**: Not configured (universal APK)

## Configuration Files

### Project Configuration
- `build.gradle` - Root build configuration
- `settings.gradle` - Module inclusion
- `gradle.properties` - Gradle JVM settings
- `local.properties` - SDK location (not in VCS)

### Android Configuration
- `android/build.gradle` - Android module build config
- `android/AndroidManifest.xml` - App manifest
- `android/proguard-rules.pro` - Code obfuscation rules
- `keystore.properties.template` - Signing config template

### Module Configuration
- `core/build.gradle` - Core module dependencies
- `desktop/build.gradle` - Desktop module dependencies

## Environment Variables

### Required
- `ANDROID_SDK_ROOT` - Android SDK location (or `local.properties`)

### Optional
- `JAVA_HOME` - JDK 17 location (usually auto-detected)

## External Services

### AdMob
- **Integration**: Google Mobile Ads SDK
- **Ad Types**: Rewarded video, interstitial, banner
- **Test Ads**: Enabled in debug builds
- **Production Ads**: Configured via BuildConfig fields

### Google Play Games
- **Integration**: Play Games Services SDK
- **Features**: Cloud save, leaderboards (implied)
- **Configuration**: Via GooglePlayHelper.kt
