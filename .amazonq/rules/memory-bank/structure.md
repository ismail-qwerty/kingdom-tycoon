# Project Structure

## Directory Organization

### Root Structure
```
kingdom-tycoon/
├── android/          # Android platform launcher and resources
├── core/             # Core game logic (platform-agnostic)
├── desktop/          # Desktop launcher for testing
├── assets/           # Raw asset source files
├── gradle/           # Gradle wrapper and configuration
└── [build scripts]   # Build automation and asset generation
```

### Android Module (`android/`)
Platform-specific Android implementation:
- `src/main/java/com/ismail/kingdom/android/` - Android launcher and platform services
  - `AndroidLauncher.kt` - Entry point for Android app
  - `AndroidAdsManager.kt` - AdMob integration
  - `GooglePlayHelper.kt` - Google Play Services integration
- `assets/` - Game assets (textures, audio, data files)
  - `buildings/` - Building icons (128x128 PNG)
  - `ui/` - UI elements and icons
  - `tiles/` - Isometric map tiles (64x64)
  - `audio/` - Sound effects and music (OGG format)
- `res/` - Android resources (launcher icons, strings)
- `AndroidManifest.xml` - App configuration and permissions

### Core Module (`core/src/main/java/com/ismail/kingdom/`)
Platform-agnostic game logic organized by responsibility:

#### Models (`models/`)
Data structures representing game entities:
- `GameState.kt` - Central game state container
- `Building.kt` - Building entity definition
- `Hero.kt` - Hero entity with abilities
- `Advisor.kt` - Advisor automation entity
- `Era.kt` - Era configuration data
- `PrestigeLayer.kt` - Prestige tier definitions
- `Quest.kt` - Quest objectives and rewards
- `KingdomEvent.kt` - Timed event data
- `MapTile.kt` - Map exploration tiles
- `Resource.kt` - Resource types and amounts
- `FinalGameState.kt` - Serializable save state
- `Results.kt` - Operation result wrappers

#### Systems (`systems/`)
Game logic processors (ECS-inspired architecture):
- `IncomeSystem.kt` - Calculates passive gold income per frame
- `BuildingSystem.kt` - Handles building purchases and upgrades
- `TapSystem.kt` - Processes tap input and gold generation
- `PrestigeSystem.kt` - Manages prestige mechanics and rewards
- `PrestigeManager.kt` - Prestige state coordination
- `AchievementSystem.kt` - Tracks and unlocks achievements
- `QuestManager.kt` - Quest progression and completion
- `AdvisorSystem.kt` - Automated building purchases
- `HeroSystem.kt` (implied) - Hero ability application
- `ShadowKingdomSystem.kt` - Parallel dimension mechanics
- `EventSystem.kt` - Weekly event management
- `MapSystem.kt` - Map exploration logic
- `WarSystem.kt` - Raid combat system
- `SpellSystem.kt` - Spell casting mechanics
- `StatisticsTracker.kt` - Lifetime stats recording
- `AntiCheatSystem.kt` - Save validation and integrity checks
- `OfflineEarningsCalculator.kt` - Calculates income while away
- `TutorialSystem.kt` - Tutorial flow management
- `HallOfLegendsSystem.kt` - Prestige milestone tracking

#### Screens (`screens/`)
LibGDX Screen implementations for different game views:
- `GameScreen.kt` - Main gameplay screen (root level)
- `MainMenuScreen.kt` - Entry menu
- `LoadingScreen.kt` - Asset loading screen
- `MapScreen.kt` - Isometric map exploration
- `PrestigeScreen.kt` - Prestige interface
- `AchievementsScreen.kt` - Achievement gallery
- `StatisticsScreen.kt` - Player statistics display
- `ShadowKingdomScreen.kt` - Shadow dimension view
- `HallOfLegendsScreen.kt` - Prestige hall
- `EventScreen.kt` - Event participation
- `WarScreen.kt` - Raid combat interface
- `SettingsScreen.kt` - Game settings

#### UI Components (`ui/`)
Reusable UI widgets and panels:
- `HUD.kt` - Main heads-up display
- `BuildingsPanel.kt` - Building purchase list
- `BuildingRow.kt` - Individual building entry
- `AdvisorsPanel.kt` - Advisor management UI
- `AdvisorRow.kt` - Individual advisor entry
- `QuestsPanel.kt` - Quest tracking display
- `QuestCard.kt` - Individual quest card
- `PrestigeUI.kt` - Prestige interface components
- `HeroSelectionGrid.kt` - Hero unlock grid
- `SpellPanel.kt` - Spell casting UI
- `EventBanner.kt` - Event notification banner
- `OfflineEarningsPopup.kt` - Offline earnings dialog
- `MapTileActor.kt` - Map tile rendering
- `MapTilePopup.kt` - Tile interaction popup
- `ShadowKingdomToggle.kt` - Dimension switch button
- `TutorialOverlay.kt` - Tutorial instruction overlay
- `CoinParticle.kt` - Gold particle effects
- `VirtualScrollList.kt` - Optimized scrolling list

#### Factories (`factories/`)
Object creation and configuration:
- `EraFactory.kt` - Creates era definitions with buildings
- `HeroFactory.kt` - Generates hero configurations
- `AdvisorFactory.kt` - Creates advisor definitions

#### Data Management (`data/`)
Persistence and session management:
- `SaveManager.kt` - Save/load game state to preferences
- `SessionManager.kt` - Session tracking and analytics
- `SettingsManager.kt` - User preferences management

#### Assets (`assets/`)
Asset loading and management:
- `AssetDescriptors.kt` - Asset path definitions
- `GameAssets.kt` - Asset loading and caching

#### Utilities (`utils/`)
Helper functions and extensions:
- `SafeMath.kt` - Overflow-safe math operations
- `Formatters.kt` - Number and time formatting
- `Extensions.kt` - Kotlin extension functions
- `LoreStrings.kt` - Game text and lore
- `PerformanceMonitor.kt` - FPS and memory tracking

#### Core Systems (Root Level)
- `KingdomTycoonGame.kt` - Main game class (LibGDX Application)
- `GameEngine.kt` - Core game loop coordinator
- `ScreenNavigator.kt` - Screen transition management
- `ResourceManager.kt` - Resource loading and disposal
- `AudioSystem.kt` - Sound and music playback
- `AnimationSystem.kt` - Animation state management
- `ThemeSystem.kt` - Visual theme switching
- `IsometricRenderer.kt` - Isometric rendering engine
- `IsometricMap.kt` - Map data structure
- `ParticleManager.kt` - Particle effect pooling
- `FloatingText.kt` - Floating damage/gold text
- `HudRenderer.kt` - HUD rendering logic

#### Ads Integration (`ads/`)
Advertisement abstraction layer:
- `AdsInterface.kt` - Platform-agnostic ad interface
- `AdManager.kt` - Ad loading and display coordination

#### Effects (`effects/`)
Visual effects and transitions:
- `PortalTransition.kt` - Screen transition effects

### Desktop Module (`desktop/`)
Desktop launcher for development testing:
- `src/main/java/com/ismail/kingdom/desktop/DesktopLauncher.kt` - Desktop entry point

## Architectural Patterns

### ECS-Inspired System Architecture
The game uses a system-based architecture inspired by Entity-Component-System (ECS):
- **GameState**: Central data container (entity storage)
- **Systems**: Logic processors that operate on GameState
- **Screens**: View layer that renders GameState and delegates to systems

### Data Flow
```
User Input → Screen → System → GameState → SaveManager → Preferences
                ↓
         UI Rendering ← GameState
```

### System Update Cycle
```
GameScreen.render(delta)
  ├─> IncomeSystem.update(gameState, delta)
  ├─> QuestManager.update(gameState, delta)
  ├─> StatisticsTracker.update(gameState, delta)
  ├─> AdvisorSystem.update(gameState, delta)
  └─> SaveManager.autoSave(gameState) [every 30s]
```

### Screen Management
LibGDX Screen pattern with custom navigation:
- `ScreenNavigator` manages screen transitions
- Each screen owns its UI components and delegates logic to systems
- Screens share GameState reference for data access

### Factory Pattern
Factories centralize object creation:
- `EraFactory` creates all era and building definitions
- `HeroFactory` generates hero configurations
- `AdvisorFactory` creates advisor definitions
- Ensures consistent data structure and easy balance tuning

### Save/Load Pattern
- `SaveManager` serializes GameState to JSON
- `AntiCheatSystem` validates save integrity on load
- Auto-save every 30 seconds + manual save on pause
- Preferences API for Android persistence

## Component Relationships

### Core Game Loop
`KingdomTycoonGame` → `GameScreen` → Systems → `GameState`

### Building Purchase Flow
`BuildingsPanel` → `BuildingSystem.purchaseBuilding()` → `GameState.buildings` → `SaveManager`

### Prestige Flow
`PrestigeScreen` → `PrestigeSystem.performPrestige()` → `GameState.reset()` → `GameState.crownShards++`

### Offline Earnings
`LoadingScreen` → `OfflineEarningsCalculator.calculate()` → `OfflineEarningsPopup` → `GameState.gold`

### Ad Reward Flow
`UI Button` → `AdManager.showRewardedAd()` → `AndroidAdsManager` → Callback → `GameState.applyBoost()`
