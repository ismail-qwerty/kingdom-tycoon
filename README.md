# Kingdom Tycoon 👑

A LibGDX-based idle clicker game for Android where players build a legendary kingdom across 5 epic eras, featuring a deep prestige system, 12 heroes, and a shadow dimension.

![Version](https://img.shields.io/badge/version-1.0.0-blue)
![Platform](https://img.shields.io/badge/platform-Android-green)
![LibGDX](https://img.shields.io/badge/LibGDX-1.14.0-red)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-purple)

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [Prerequisites](#prerequisites)
- [Setup](#setup)
- [Architecture](#architecture)
- [Adding Content](#adding-content)
- [AdMob Setup](#admob-setup)
- [Asset Management](#asset-management)
- [Balance Tuning](#balance-tuning)
- [Testing](#testing)
- [Release Process](#release-process)
- [Known Issues](#known-issues)
- [Contributing](#contributing)

## 🎮 Project Overview

**Kingdom Tycoon** is an idle clicker/tycoon game where players:
- Tap to earn gold and build structures
- Progress through 5 distinct eras (Medieval → Legendary)
- Prestige to earn Crown Shards for permanent bonuses
- Unlock 12 legendary heroes with unique abilities
- Access a Shadow Kingdom dimension for doubled income
- Complete 50 achievements across 10 categories

### Tech Stack

- **Language:** Kotlin 1.9+
- **Game Framework:** LibGDX 1.14.0
- **Platform:** Android (minSdk 21, targetSdk 35)
- **Build System:** Gradle 8.x
- **Serialization:** kotlinx.serialization
- **Ads:** Google AdMob
- **Architecture:** ECS-inspired with system-based design

### Key Features

✅ 5 eras with 25 unique buildings  
✅ 3-tier prestige system (Ascension, Rift, Legend)  
✅ 12 legendary heroes with passive bonuses  
✅ Shadow Kingdom (parallel dimension)  
✅ 50 achievements  
✅ Offline earnings  
✅ Weekly events  
✅ Quest system  
✅ Map exploration  
✅ Raid system  

## 🔧 Prerequisites

### Required Software

- **Android Studio:** Hedgehog (2023.1.1) or newer
- **Java Development Kit:** JDK 17 (bundled with Android Studio)
- **Android SDK:** API Level 35 (Android 15)
- **Gradle:** 8.0+ (wrapper included)
- **Python:** 3.8+ (for asset generation scripts)

### Recommended

- **Git:** For version control
- **Device/Emulator:** Android 5.0+ (API 21+) for testing
- **RAM:** 8GB minimum, 16GB recommended
- **Storage:** 5GB free space

### Verify Installation

```bash
# Check Java version
java -version  # Should show 17.x

# Check Android SDK
echo $ANDROID_SDK_ROOT  # Should point to SDK location

# Check Python
python3 --version  # Should show 3.8+
```

## 🚀 Setup

### Step 1: Clone Repository

```bash
git clone https://github.com/yourusername/kingdom-tycoon.git
cd kingdom-tycoon
```

### Step 2: Open in Android Studio

1. Launch Android Studio
2. Select **File → Open**
3. Navigate to the cloned `kingdom-tycoon` folder
4. Click **OK**
5. Wait for Gradle sync to complete

### Step 3: Generate Placeholder Assets

```bash
# Install Python dependencies
pip install pillow

# Generate placeholder graphics
python3 generate_placeholder_assets.py

# Generate store assets
python3 generate_store_assets.py
```

This creates:
- `android/assets/` - Game textures and sounds
- `store_assets/` - Play Store graphics

### Step 4: Sync Gradle

```bash
# From project root
./gradlew clean build

# Or in Android Studio
# Click "Sync Project with Gradle Files" button
```

### Step 5: Run the Game

**Option A: Android Studio**
1. Select `android` run configuration
2. Choose device/emulator
3. Click **Run** (▶️)

**Option B: Command Line**
```bash
# Install to connected device
./gradlew android:installDebug

# Run on device
./gradlew android:run
```

### Troubleshooting Setup

**Gradle sync fails:**
```bash
# Clear Gradle cache
./gradlew clean --refresh-dependencies
```

**SDK not found:**
- Create `local.properties` in project root:
```properties
sdk.dir=/path/to/Android/Sdk
```

**Build fails with "Java version" error:**
- Set JDK 17 in Android Studio:
  - **File → Project Structure → SDK Location → JDK location**

## 🏗️ Architecture

### System Overview

```
┌─────────────────────────────────────────────────────────┐
│                      GameScreen                         │
│  (Main game loop, renders all systems)                  │
└────────────┬────────────────────────────────────────────┘
             │
    ┌────────┴────────┐
    │   GameState     │  (Central data model)
    │  - gold         │
    │  - buildings    │
    │  - heroes       │
    │  - prestige     │
    └────────┬────────┘
             │
    ┌────────┴────────────────────────────────────────┐
    │                                                  │
┌───▼────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐
│Income  │  │Building  │  │Prestige  │  │Achievement│
│System  │  │System    │  │System    │  │System     │
└────────┘  └──────────┘  └──────────┘  └──────────┘
    │            │              │              │
┌───▼────┐  ┌───▼──────┐  ┌───▼──────┐  ┌───▼──────┐
│Quest   │  │Hero      │  │Shadow    │  │Statistics│
│System  │  │System    │  │Kingdom   │  │Tracker   │
└────────┘  └──────────┘  └──────────┘  └──────────┘
    │            │              │              │
    └────────────┴──────────────┴──────────────┘
                     │
              ┌──────▼──────┐
              │ SaveManager │  (Persistence)
              └─────────────┘
```

### Core Systems

| System | Responsibility | Update Frequency |
|--------|---------------|------------------|
| **IncomeSystem** | Calculates passive gold income | Every frame |
| **BuildingSystem** | Manages building purchases | On demand |
| **PrestigeSystem** | Handles prestige mechanics | On demand |
| **AchievementSystem** | Tracks and unlocks achievements | On events |
| **QuestSystem** | Manages active quests | Every frame |
| **HeroSystem** | Applies hero bonuses | On unlock |
| **ShadowKingdomSystem** | Parallel dimension income | Every frame |
| **StatisticsTracker** | Records lifetime stats | Every frame |
| **AntiCheatSystem** | Validates game state | On load/save |
| **SaveManager** | Serializes/deserializes state | Every 30s |

### Data Flow

```
User Input → GameScreen → System → GameState → SaveManager → Preferences
                ↓
         UI Rendering ← GameState
```

### File Structure

```
kingdom-tycoon/
├── android/                    # Android launcher
│   ├── src/main/java/         # Android-specific code
│   │   └── com/ismail/kingdom/android/
│   │       ├── AndroidLauncher.kt
│   │       └── AndroidAdsManager.kt
│   ├── assets/                # Game assets (textures, sounds)
│   ├── res/                   # Android resources
│   └── AndroidManifest.xml
├── core/                      # Core game logic
│   └── src/main/java/com/ismail/kingdom/
│       ├── models/            # Data models
│       │   ├── GameState.kt
│       │   ├── Building.kt
│       │   └── Hero.kt
│       ├── systems/           # Game systems
│       │   ├── IncomeSystem.kt
│       │   ├── BuildingSystem.kt
│       │   ├── PrestigeSystem.kt
│       │   └── ...
│       ├── screens/           # Game screens
│       │   ├── GameScreen.kt
│       │   ├── MapScreen.kt
│       │   └── ...
│       ├── data/              # Data factories
│       │   ├── EraFactory.kt
│       │   └── SaveManager.kt
│       ├── utils/             # Utilities
│       │   └── SafeMath.kt
│       └── KingdomGame.kt     # Main game class
├── store_listing/             # Play Store metadata
├── generate_store_assets.py   # Asset generator
└── README.md
```

## 📦 Adding Content

### Adding a New Building

**Step 1: Define Building in EraFactory.kt**

```kotlin
// PATH: core/src/main/java/com/ismail/kingdom/data/EraFactory.kt

private fun createEra1Buildings(): List<Building> {
    return listOf(
        // ... existing buildings ...
        Building(
            id = "era1_tavern",           // Unique ID
            name = "Tavern",              // Display name
            baseCost = 25000.0,           // Initial cost
            baseIncome = 1500.0,          // Income per second
            era = 1,                      // Era number
            count = 0                     // Starting count
        )
    )
}
```

**Step 2: Add Building Icon**

```bash
# Add texture to assets
android/assets/buildings/era1_tavern.png  # 128x128 pixels
```

**Step 3: Update Building Renderer**

```kotlin
// PATH: core/src/main/java/com/ismail/kingdom/ui/BuildingCard.kt

private fun getBuildingTexture(buildingId: String): Texture {
    return when (buildingId) {
        "era1_tavern" -> Texture(Gdx.files.internal("buildings/era1_tavern.png"))
        // ... other buildings ...
    }
}
```

**Step 4: Test Balance**

```bash
# Run balance simulator
./gradlew test --tests BalanceSimulator
```

**Step 5: Add Achievement (Optional)**

```kotlin
// PATH: core/src/main/java/com/ismail/kingdom/systems/AchievementSystem.kt

achievements.add(Achievement(
    "building_tavern_10",
    "Tavern Master",
    "Own 10 taverns",
    AchievementType.BUILDINGS_BOUGHT,
    10.0,
    RewardType.GOLD_MULTIPLIER,
    1.05,
    "ui/achievement_tavern.png"
))
```

### Adding a New Era

**Complete Checklist:**

- [ ] **1. Define Buildings** in `EraFactory.kt`
  - 5 buildings per era
  - Exponential cost scaling (5x per tier)
  - Exponential income scaling (5x per tier)

- [ ] **2. Set Era Unlock Requirement**
  ```kotlin
  fun getEraUnlockRequirement(era: Int): Double {
      return when (era) {
          6 -> 1e18 // 1 Quintillion gold
          else -> 0.0
      }
  }
  ```

- [ ] **3. Add Era Tap Gold**
  ```kotlin
  fun getEraTapGold(era: Int): Double {
      return when (era) {
          6 -> 50000000000.0 // 50 billion per tap
          else -> 1.0
      }
  }
  ```

- [ ] **4. Create Building Textures**
  - 5 textures at 128x128 pixels
  - Save to `android/assets/buildings/`
  - Naming: `era6_building_name.png`

- [ ] **5. Add Era Background**
  - Background texture at 1920x1080
  - Save to `android/assets/backgrounds/era6_bg.png`

- [ ] **6. Update Era Transition**
  ```kotlin
  // PATH: core/src/main/java/com/ismail/kingdom/screens/GameScreen.kt
  
  private fun getEraName(era: Int): String {
      return when (era) {
          6 -> "Futuristic Age"
          else -> "Unknown Era"
      }
  }
  ```

- [ ] **7. Add Era-Specific Quests**
  - 5 quests per era
  - Add to `QuestFactory.kt`

- [ ] **8. Update Balance Simulator**
  ```kotlin
  // Add Era 6 to simulation
  6 -> listOf(/* buildings */)
  ```

- [ ] **9. Test Progression**
  - Verify unlock requirement is achievable
  - Check building costs are balanced
  - Ensure smooth transition from Era 5

- [ ] **10. Update Documentation**
  - Add to README.md
  - Update store description
  - Add to release notes

## 📱 AdMob Setup

### Step 1: Get AdMob App ID

1. Go to [AdMob Console](https://apps.admob.com/)
2. Create new app or select existing
3. Copy App ID (format: `ca-app-pub-XXXXXXXXXXXXXXXX~YYYYYYYYYY`)

### Step 2: Add App ID to AndroidManifest.xml

```xml
<!-- PATH: android/AndroidManifest.xml -->

<application>
    <meta-data
        android:name="com.google.android.gms.ads.APPLICATION_ID"
        android:value="ca-app-pub-XXXXXXXXXXXXXXXX~YYYYYYYYYY"/>
</application>
```

### Step 3: Create Ad Units

Create 4 ad units in AdMob Console:
1. **Rewarded Video** - Double Offline Earnings
2. **Rewarded Video** - Speed Boost
3. **Interstitial** - Era Transition
4. **Banner** - Map Screen

### Step 4: Add Ad Unit IDs to build.gradle

```gradle
// PATH: android/build.gradle

buildTypes {
    debug {
        // Test ad IDs (keep these for development)
        buildConfigField "String", "REWARDED_OFFLINE_ID", "\"ca-app-pub-3940256099942544/5224354917\""
        buildConfigField "String", "REWARDED_BOOST_ID", "\"ca-app-pub-3940256099942544/5224354917\""
        buildConfigField "String", "INTERSTITIAL_ID", "\"ca-app-pub-3940256099942544/1033173712\""
        buildConfigField "String", "BANNER_ID", "\"ca-app-pub-3940256099942544/6300978111\""
    }
    
    release {
        // Production ad IDs (replace with your real IDs)
        buildConfigField "String", "REWARDED_OFFLINE_ID", "\"ca-app-pub-XXXXXXXXXXXXXXXX/YYYYYYYYYY\""
        buildConfigField "String", "REWARDED_BOOST_ID", "\"ca-app-pub-XXXXXXXXXXXXXXXX/YYYYYYYYYY\""
        buildConfigField "String", "INTERSTITIAL_ID", "\"ca-app-pub-XXXXXXXXXXXXXXXX/YYYYYYYYYY\""
        buildConfigField "String", "BANNER_ID", "\"ca-app-pub-XXXXXXXXXXXXXXXX/YYYYYYYYYY\""
    }
}
```

### Step 5: Update AndroidAdsManager.kt

```kotlin
// PATH: android/src/main/java/com/ismail/kingdom/android/AndroidAdsManager.kt

companion object {
    private val REWARDED_AD_DOUBLE_OFFLINE = BuildConfig.REWARDED_OFFLINE_ID
    private val REWARDED_AD_SPEED_BOOST = BuildConfig.REWARDED_BOOST_ID
    private val INTERSTITIAL_AD_ID = BuildConfig.INTERSTITIAL_ID
    private val BANNER_AD_ID = BuildConfig.BANNER_ID
}
```

### Testing Ads

```bash
# Debug build uses test ads automatically
./gradlew android:installDebug

# Verify ads load in logcat
adb logcat | grep "AdMob"
```

## 🎨 Asset Management

### Replacing Placeholder Art

**Current Structure:**
```
android/assets/
├── buildings/          # Building icons (128x128)
├── ui/                 # UI elements (various sizes)
├── backgrounds/        # Era backgrounds (1920x1080)
├── heroes/             # Hero portraits (256x256)
├── tiles/              # Map tiles (64x64)
└── audio/              # Sound effects and music
    ├── sfx/
    └── music/
```

### Using Texture Atlas (Recommended)

**Step 1: Install TexturePacker**
- Download from [codeandweb.com/texturepacker](https://www.codeandweb.com/texturepacker)

**Step 2: Create Atlas**
```bash
# Pack all UI textures
TexturePacker \
    --format libgdx \
    --data android/assets/ui.atlas \
    --sheet android/assets/ui.png \
    ui_source_images/
```

**Step 3: Load Atlas in Code**
```kotlin
// PATH: core/src/main/java/com/ismail/kingdom/KingdomGame.kt

class KingdomGame : Game() {
    lateinit var atlas: TextureAtlas
    
    override fun create() {
        atlas = TextureAtlas(Gdx.files.internal("ui.atlas"))
    }
    
    fun getTexture(name: String): TextureRegion {
        return atlas.findRegion(name)
    }
}
```

### Asset Guidelines

| Asset Type | Size | Format | Notes |
|------------|------|--------|-------|
| Buildings | 128x128 | PNG | Transparent background |
| Heroes | 256x256 | PNG | Portrait style |
| UI Icons | 64x64 | PNG | Flat design |
| Backgrounds | 1920x1080 | JPG | Compressed |
| Sounds | - | OGG | 44.1kHz, mono |
| Music | - | OGG | 44.1kHz, stereo |

### Optimizing Assets

```bash
# Compress PNG files
pngquant --quality=65-80 android/assets/**/*.png

# Convert audio to OGG
ffmpeg -i input.mp3 -c:a libvorbis -q:a 4 output.ogg
```

## ⚖️ Balance Tuning

### Quick Tuning Guide

**Make progression faster:**
```kotlin
// PATH: core/src/main/java/com/ismail/kingdom/data/EraFactory.kt

// Reduce building costs by 50%
baseCost = 50.0  // was 100.0

// Increase building income by 2x
baseIncome = 16.0  // was 8.0

// Increase tap gold
fun getEraTapGold(era: Int): Double {
    return when (era) {
        1 -> 2.0  // was 1.0
    }
}
```

**Make progression slower:**
```kotlin
// Increase building costs by 2x
baseCost = 200.0  // was 100.0

// Decrease building income by 50%
baseIncome = 4.0  // was 8.0
```

**Adjust prestige rewards:**
```kotlin
// PATH: core/src/main/java/com/ismail/kingdom/systems/PrestigeSystem.kt

// More generous crown shards
private const val CROWN_SHARD_BONUS_PER_SHARD = 0.003  // was 0.002 (0.3% per shard)

// Faster prestige unlocks
private const val PRESTIGE_BASE_REQUIREMENT = 5e8  // was 1e9 (500M instead of 1B)
```

**Adjust offline earnings:**
```kotlin
// PATH: core/src/main/java/com/ismail/kingdom/systems/AntiCheatSystem.kt

// Increase offline cap
private const val MAX_OFFLINE_HOURS_CAPPED = 48  // was 24 (48 hours max)
```

### Balance Testing

```bash
# Run balance simulator
./gradlew test --tests BalanceSimulator

# Check output for warnings:
# - Buildings too cheap/expensive
# - Prestige too fast/slow
# - Crown shard multipliers
```

### Key Balance Constants

| Constant | Location | Default | Effect |
|----------|----------|---------|--------|
| `COST_GROWTH_RATE` | EraFactory.kt | 1.15 | Building cost scaling |
| `CROWN_SHARD_BONUS_PER_SHARD` | PrestigeSystem.kt | 0.002 | Prestige power |
| `PRESTIGE_BASE_REQUIREMENT` | PrestigeSystem.kt | 1e9 | Gold needed to prestige |
| `MAX_OFFLINE_HOURS_CAPPED` | AntiCheatSystem.kt | 24 | Offline earnings cap |
| `AUTO_SAVE_INTERVAL` | SaveManager.kt | 30s | Save frequency |

## 🧪 Testing

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test
./gradlew test --tests SaveSystemStressTest

# Run with coverage
./gradlew testDebugUnitTest jacocoTestReport
```

### Test Suites

- **SaveSystemStressTest.kt** - Save/load validation
- **BalanceSimulator.kt** - Progression balance
- **AdQAChecklist.kt** - Ad integration
- **ReleaseChecklist.kt** - Production readiness

### Manual Testing Checklist

- [ ] Fresh install works (no save file)
- [ ] Save/load preserves all data
- [ ] Offline earnings calculate correctly
- [ ] All 5 eras accessible
- [ ] Prestige resets properly
- [ ] Ads load and reward correctly
- [ ] No crashes on low memory
- [ ] Performance: 60 FPS on mid-range device

## 🚀 Release Process

### Pre-Release

1. **Run Release Checklist**
   ```bash
   ./gradlew test --tests ReleaseChecklist
   ```

2. **Update Version**
   ```gradle
   // android/build.gradle
   versionCode 2
   versionName "1.0.1"
   ```

3. **Update Release Notes**
   - Edit `store_listing/release_notes.txt`

4. **Generate Signed APK**
   ```bash
   ./gradlew assembleRelease
   ```

5. **Test Release Build**
   ```bash
   adb install android/build/outputs/apk/release/android-release.apk
   ```

### Release Checklist

See [ReleaseChecklist.kt](core/src/test/kotlin/com/ismail/kingdom/ReleaseChecklist.kt) for complete checklist.

**Quick Summary:**
- ✅ All crash safety checks pass
- ✅ All gameplay features complete
- ✅ Ads configured correctly
- ✅ Performance targets met
- ✅ Monetization ethics verified

### Play Store Submission

1. **Build AAB**
   ```bash
   ./gradlew bundleRelease
   ```

2. **Upload to Play Console**
   - Go to [Play Console](https://play.google.com/console)
   - Create new release
   - Upload AAB
   - Add release notes

3. **Submit for Review**
   - Typical review time: 1-7 days

## 🐛 Known Issues

### Current Issues

- [ ] **Shadow Kingdom UI** - Minor visual glitch on some devices
- [ ] **Offline Earnings** - Popup sometimes shows twice on first launch
- [ ] **Achievement Popup** - Text overflow on long achievement names

### Planned Features

- [ ] Cloud save support (Google Play Games)
- [ ] Leaderboards
- [ ] Daily login rewards
- [ ] Seasonal events
- [ ] More heroes (expand to 20)
- [ ] Era 6: Space Age

### Performance Notes

- **Target:** 60 FPS on Snapdragon 665 or equivalent
- **Memory:** < 150MB RAM usage
- **Battery:** Optimized for idle gameplay

## 📚 Additional Resources

### Documentation

- [LibGDX Wiki](https://libgdx.com/wiki/)
- [Kotlin Documentation](https://kotlinlang.org/docs/)
- [AdMob Integration Guide](https://developers.google.com/admob/android/quick-start)
- [Play Store Policies](https://play.google.com/about/developer-content-policy/)

### Tools

- [TexturePacker](https://www.codeandweb.com/texturepacker) - Texture atlas creation
- [Hiero](https://libgdx.com/wiki/tools/hiero) - Bitmap font generator
- [Audacity](https://www.audacityteam.org/) - Audio editing
- [GIMP](https://www.gimp.org/) - Image editing

### Community

- [LibGDX Discord](https://discord.gg/6pgDK9F)
- [r/libgdx](https://reddit.com/r/libgdx)
- [Stack Overflow](https://stackoverflow.com/questions/tagged/libgdx)

## 🤝 Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for detailed contribution guidelines.

**Quick Start:**
1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## 📄 License

This project is licensed under the MIT License - see [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Ismail** - Initial work

## 🙏 Acknowledgments

- LibGDX community for excellent framework
- AdMob for monetization support
- All playtesters and contributors

---

**Built with ❤️ using LibGDX and Kotlin**

For support: support@kingdomtycoon.com
