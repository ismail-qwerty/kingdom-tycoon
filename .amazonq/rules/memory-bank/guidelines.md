# Development Guidelines

## Code Quality Standards

### File Header Convention
Every Kotlin file MUST start with a PATH comment on the first line:
```kotlin
// PATH: core/src/main/java/com/ismail/kingdom/systems/IncomeSystem.kt
```
This is a strict project convention for file identification and navigation.

### Single-Line Function Comments
Every function MUST have a single-line comment explaining its purpose:
```kotlin
// Calculates total income per second from all sources
fun calculateTotalIncome(): Double { ... }

// Advances to the next tutorial step
fun advance(step: TutorialStep) { ... }

// Opens Play Store listing for rating
fun rateApp(context: Context) { ... }
```

### Naming Conventions
- **Classes**: PascalCase (e.g., `TutorialSystem`, `GameState`, `SaveManager`)
- **Functions**: camelCase (e.g., `calculateTotalIncome`, `performPrestigeReset`, `isConnectedToInternet`)
- **Properties**: camelCase (e.g., `currentGold`, `totalLifetimeGold`, `isActive`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_GAME_VALUE`, `AUTO_SAVE_INTERVAL`, `COST_GROWTH_RATE`)
- **Private Properties**: camelCase with `private` modifier (e.g., `private val completedSteps`)

### Package Structure
Follow domain-driven package organization:
- `models/` - Data classes and entities
- `systems/` - Game logic processors
- `screens/` - LibGDX Screen implementations
- `ui/` - Reusable UI components
- `utils/` - Helper functions and extensions
- `data/` - Persistence and session management
- `factories/` - Object creation patterns
- `ads/` - Advertisement integration

## Kotlin Language Patterns

### Data Classes for Models
Use data classes for all game entities and state containers:
```kotlin
@Serializable
data class GameState(
    var currentGold: Double = 10.0,
    var currentEra: Int = 1,
    var buildings: MutableList<Building> = mutableListOf()
)

@Serializable
data class Building(
    val id: String,
    val name: String,
    val baseCost: Double,
    val baseIncome: Double,
    val era: Int,
    var count: Int = 0
)
```

### Enum Classes for Type Safety
Use enums for fixed sets of states or types:
```kotlin
enum class TutorialStep {
    TAP_KINGDOM_HALL,
    BUY_FIRST_BUILDING,
    BUY_SECOND_BUILDING,
    HIRE_FIRST_ADVISOR,
    TUTORIAL_COMPLETE
}
```

### Object Singletons for Utilities
Use `object` for stateless utility classes:
```kotlin
object SafeMath {
    const val MAX_GAME_VALUE = 1e308
    
    fun safeAdd(a: Double, b: Double): Double { ... }
    fun safeMultiply(a: Double, b: Double): Double { ... }
}

object Formatters {
    fun formatGold(amount: Double): String { ... }
    fun formatTime(seconds: Int): String { ... }
}

object GooglePlayHelper {
    fun rateApp(context: Context) { ... }
    fun shareGame(context: Context) { ... }
}
```

### Companion Objects for Static Members
Use companion objects for class-level constants and factory methods:
```kotlin
data class Building(...) {
    companion object {
        const val COST_GROWTH_RATE = 1.15
    }
}

class SaveManager(private val prefs: Preferences) {
    companion object {
        private const val SAVE_KEY = "save_data"
        const val AUTO_SAVE_INTERVAL = 30f
        
        fun save(state: GameState, prefs: Preferences) { ... }
        fun load(prefs: Preferences): GameState? { ... }
    }
}
```

### Property Delegation and Custom Getters/Setters
Use computed properties for derived values:
```kotlin
data class GameState(...) {
    // Legacy compatibility aliases
    var gold: Double
        get() = currentGold
        set(value) { currentGold = value }
    
    val ownedBuildings: MutableMap<String, Int>
        get() = buildings.associate { it.id to it.count }.toMutableMap()
    
    val prestigeThreshold: Double
        get() = 1_000_000.0 * 10.0.pow(prestigeCount.toDouble())
}
```

### Extension Functions
Use extension functions for utility operations (implied by `Extensions.kt`):
```kotlin
// Typical patterns in utils/Extensions.kt
fun Double.format(): String = Formatters.formatGold(this)
fun Int.toTimeString(): String = Formatters.formatTime(this)
```

## Architectural Patterns

### System-Based Architecture (ECS-Inspired)
Systems operate on GameState and are responsible for specific game logic:
```kotlin
class IncomeSystem(private val gameState: GameState) {
    fun update(delta: Float) {
        val incomePerSecond = calculateTotalIncome()
        val goldToAdd = SafeMath.safeMultiply(incomePerSecond, delta.toDouble())
        gameState.currentGold = SafeMath.safeAdd(gameState.currentGold, goldToAdd)
    }
    
    fun calculateTotalIncome(): Double {
        var totalIncome = 0.0
        gameState.buildings.forEach { building ->
            totalIncome = SafeMath.safeAdd(totalIncome, building.totalIncome())
        }
        return SafeMath.safeMultiply(totalIncome, gameState.incomeMultiplier)
    }
}
```

### Dependency Injection via Constructor
Pass dependencies through constructors, not global state:
```kotlin
class TutorialSystem(private val gameState: GameState) { ... }
class IncomeSystem(private val gameState: GameState) { ... }
class SaveManager(private val prefs: Preferences) { ... }
```

### Optional System References
Systems can optionally reference other systems for cross-cutting concerns:
```kotlin
class IncomeSystem(private val gameState: GameState) {
    private var eventSystem: EventSystem? = null
    
    fun setEventSystem(system: EventSystem) {
        eventSystem = system
    }
    
    fun calculateTotalIncome(): Double {
        var totalIncome = 0.0
        // ... calculate base income ...
        val eventMultiplier = eventSystem?.getEventIncomeMultiplier() ?: 1.0
        return SafeMath.safeMultiply(totalIncome, eventMultiplier)
    }
}
```

### Factory Pattern for Content Creation
Centralize object creation in factory classes:
```kotlin
// Implied pattern from factories/EraFactory.kt
object EraFactory {
    fun createEra1Buildings(): List<Building> {
        return listOf(
            Building("era1_farm", "Farm", 100.0, 8.0, 1),
            Building("era1_mine", "Mine", 500.0, 40.0, 1)
        )
    }
}
```

## LibGDX Integration Patterns

### Asset Loading via Gdx.files.internal()
Always use LibGDX's file API for asset loading:
```kotlin
// Correct pattern
val texture = Texture(Gdx.files.internal("buildings/era1_farm.png"))
val sound = Gdx.audio.newSound(Gdx.files.internal("audio/sfx/coin.ogg"))

// NEVER use Java File API directly
```

### Configuration with apply() Scope Function
Use Kotlin's `apply` for fluent configuration:
```kotlin
val config = Lwjgl3ApplicationConfiguration().apply {
    setTitle("Kingdom Tycoon")
    setWindowedMode(1280, 720)
    setForegroundFPS(60)
    useVsync(true)
    setWindowIcon("ui/icon.png")
}
```

### Null Safety for Platform Services
Handle platform-specific features with nullable types:
```kotlin
class KingdomGame(private val adsInterface: AdsInterface?) : Game() {
    fun showRewardedAd() {
        adsInterface?.showRewardedAd() // Safe call - no-op on desktop
    }
}
```

## Safety and Validation Patterns

### SafeMath for All Numeric Operations
Use SafeMath utility for all game calculations to prevent overflow:
```kotlin
// Always use SafeMath for gold operations
gameState.currentGold = SafeMath.safeAdd(gameState.currentGold, goldToAdd)
totalIncome = SafeMath.safeMultiply(totalIncome, multiplier)

// For exponential calculations (building costs)
val cost = SafeMath.safeExp(COST_GROWTH_RATE, count.toDouble())

// For clamping values
val clampedGold = SafeMath.clampGold(amount)
```

### Validation Before Operations
Validate inputs and state before performing operations:
```kotlin
fun isValidGameNumber(value: Double): Boolean {
    return !value.isNaN() && !value.isInfinite() && value >= 0.0
}

fun safeAdd(a: Double, b: Double): Double {
    if (!isValidGameNumber(a) || !isValidGameNumber(b)) {
        return 0.0
    }
    // ... perform operation ...
}
```

### Try-Catch for External Operations
Wrap external operations (file I/O, network, platform APIs) in try-catch:
```kotlin
fun rateApp(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("market://details?id=$PACKAGE_NAME")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback to browser
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/apps/details?id=$PACKAGE_NAME")
        }
        context.startActivity(intent)
    }
}
```

### Sanitization for User-Influenced Data
Sanitize data that could be manipulated:
```kotlin
fun calculateOfflineEarnings(secondsOffline: Long): Double {
    // Sanitize offline time first
    val sanitizedSeconds = AntiCheatSystem.sanitizeOfflineTime(secondsOffline)
    val incomePerSecond = calculateTotalIncome()
    return SafeMath.safeMultiply(incomePerSecond, sanitizedSeconds.toDouble())
}
```

## Serialization Patterns

### kotlinx.serialization Annotations
Use `@Serializable` annotation for all persistent data classes:
```kotlin
@Serializable
data class GameState(
    var currentGold: Double = 10.0,
    var currentEra: Int = 1,
    var buildings: MutableList<Building> = mutableListOf()
)

@Serializable
data class Building(
    val id: String,
    val name: String,
    val baseCost: Double,
    val baseIncome: Double,
    val era: Int,
    var count: Int = 0
)
```

### Versioned Save Format
Wrap save data in versioned container for migration support:
```kotlin
@Serializable
data class VersionedSave(
    val version: Int,
    val state: GameState
)

fun save(state: GameState, prefs: Preferences) {
    val versionedSave = VersionedSave(SAVE_VERSION, state)
    val jsonString = json.encodeToString(versionedSave)
    prefs.putString(SAVE_KEY, jsonString)
    prefs.flush()
}
```

### JSON Configuration
Configure JSON serializer with appropriate settings:
```kotlin
private val json = Json {
    prettyPrint = false           // Compact for storage
    ignoreUnknownKeys = true      // Forward compatibility
    encodeDefaults = true          // Include default values
}
```

### Backup Before Save
Create backup of existing save before overwriting:
```kotlin
fun save(state: GameState, prefs: Preferences) {
    if (prefs.contains(SAVE_KEY)) {
        val existing = prefs.getString(SAVE_KEY, "")
        if (existing.isNotEmpty()) {
            prefs.putString(BACKUP_KEY, existing)
        }
    }
    prefs.putString(SAVE_KEY, jsonString)
    prefs.flush()
}
```

## State Management Patterns

### Mutable State in GameState
GameState is the single source of truth with mutable properties:
```kotlin
data class GameState(
    var currentGold: Double = 10.0,
    var currentEra: Int = 1,
    var buildings: MutableList<Building> = mutableListOf()
)
```

### State Modification Methods
Provide methods on GameState for common state changes:
```kotlin
data class GameState(...) {
    fun addGold(amount: Double) {
        currentGold += amount
        totalGoldEarned += amount
        totalLifetimeGold += amount
    }
    
    fun spendGold(amount: Double): Boolean {
        if (currentGold < amount) return false
        currentGold -= amount
        return true
    }
    
    fun performPrestigeReset(keepHeroes: Boolean = false) {
        currentGold = 10.0
        currentEra = 1
        buildings.forEach { it.count = 0 }
        if (!keepHeroes) heroes.forEach { it.isUnlocked = false }
    }
}
```

### Timestamp Tracking
Track timestamps for session management and offline calculations:
```kotlin
data class GameState(
    var lastSaveTime: Long = System.currentTimeMillis(),
    var sessionStartTime: Long = System.currentTimeMillis()
)

fun save(state: GameState, prefs: Preferences) {
    state.lastSaveTime = System.currentTimeMillis()
    // ... save logic ...
}
```

## Android Platform Patterns

### API Level Compatibility
Handle different Android API levels with version checks:
```kotlin
fun isConnectedToInternet(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    } else {
        @Suppress("DEPRECATION")
        val networkInfo = connectivityManager.activeNetworkInfo
        networkInfo?.isConnected == true
    }
}
```

### Intent Flags for New Tasks
Add FLAG_ACTIVITY_NEW_TASK for intents launched from non-Activity contexts:
```kotlin
val intent = Intent(Intent.ACTION_VIEW).apply {
    data = Uri.parse("market://details?id=$PACKAGE_NAME")
    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
}
context.startActivity(intent)
```

### Chooser for Share Intents
Use createChooser for share functionality:
```kotlin
val intent = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_TEXT, shareText)
}
val chooser = Intent.createChooser(intent, "Share Kingdom Tycoon")
chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
context.startActivity(chooser)
```

## Common Code Idioms

### Coercion for Range Limiting
Use `coerceIn` and `coerceAtLeast`/`coerceAtMost` for range constraints:
```kotlin
val eraIndex: Int
    get() = (currentEra - 1).coerceAtLeast(0)

val cappedCount = count.coerceIn(0, 10000)

fun getProgress(): Float {
    return (completedSteps.size.toFloat() / totalSteps).coerceIn(0f, 1f)
}
```

### When Expressions for State Mapping
Use `when` for state-based logic:
```kotlin
fun advanceToNext() {
    val nextStep = when (currentStep) {
        TutorialStep.TAP_KINGDOM_HALL -> TutorialStep.BUY_FIRST_BUILDING
        TutorialStep.BUY_FIRST_BUILDING -> TutorialStep.BUY_SECOND_BUILDING
        TutorialStep.TUTORIAL_COMPLETE -> TutorialStep.TUTORIAL_COMPLETE
    }
    advance(nextStep)
}

fun formatGold(amount: Double): String {
    return when {
        value >= 1e15 -> "%.2fQa".format(value / 1e15)
        value >= 1e12 -> "%.2fT".format(value / 1e12)
        value >= 1e9 -> "%.2fB".format(value / 1e9)
        else -> "%.0f".format(value)
    }
}
```

### Elvis Operator for Defaults
Use `?:` for default values with nullables:
```kotlin
val eventMultiplier = eventSystem?.getEventIncomeMultiplier() ?: 1.0

val jsonString = prefs.getString(SAVE_KEY, "")
if (jsonString.isEmpty()) return null
```

### Safe Calls and Let for Null Handling
Use safe call operator `?.` and `let` for null-safe operations:
```kotlin
fun showRewardedAd() {
    adsInterface?.showRewardedAd()
}

eventSystem?.let { system ->
    totalIncome *= system.getEventIncomeMultiplier()
}
```

### Collection Operations
Use Kotlin collection functions for data transformations:
```kotlin
val ownedBuildings: MutableMap<String, Int>
    get() = buildings.associate { it.id to it.count }.toMutableMap()

val advisorsUnlocked: MutableSet<String>
    get() = advisors.filter { it.isHired }.map { it.id }.toMutableSet()

val totalBuildingCount = gameState.buildings.sumOf { it.count }

gameState.buildings.forEach { building ->
    totalIncome += building.totalIncome()
}
```

### String Templates
Use string templates for formatting:
```kotlin
fun formatIPS(ips: Double): String {
    return "${formatGold(ips)}/s"
}

val shareText = "Check out Kingdom Tycoon! Build your kingdom and become a legend!\\n" +
        "https://play.google.com/store/apps/details?id=$PACKAGE_NAME"
```

## Testing and Validation Patterns

### Auto-Advance Checks
Check game state conditions to auto-advance tutorial or quests:
```kotlin
fun checkAutoAdvance() {
    if (!isActive) return
    when (currentStep) {
        TutorialStep.TAP_KINGDOM_HALL -> if (gameState.tapCount > 0) advanceToNext()
        TutorialStep.BUY_FIRST_BUILDING -> if (gameState.buildings.any { it.count > 0 }) advanceToNext()
        else -> {}
    }
}
```

### Milestone Checks
Separate methods for checking milestone conditions:
```kotlin
fun checkMilestone() {
    if (!isActive || isStepComplete(TutorialStep.FIRST_MILESTONE)) return
    if (gameState.buildings.sumOf { it.count } >= 10) {
        advance(TutorialStep.FIRST_MILESTONE)
    }
}
```

### Early Returns for Guard Clauses
Use early returns to handle invalid states:
```kotlin
fun advance(step: TutorialStep) {
    if (!isActive) return
    // ... proceed with logic ...
}

fun spendGold(amount: Double): Boolean {
    if (currentGold < amount) return false
    currentGold -= amount
    return true
}
```

## Performance Patterns

### Object Pooling (Implied)
Use object pooling for frequently created objects like particles:
```kotlin
// Implied by ParticleManager.kt
class ParticleManager {
    private val particlePool = mutableListOf<CoinParticle>()
    
    fun getParticle(): CoinParticle {
        return particlePool.removeLastOrNull() ?: CoinParticle()
    }
    
    fun returnParticle(particle: CoinParticle) {
        particlePool.add(particle)
    }
}
```

### Lazy Initialization
Use lazy initialization for expensive resources:
```kotlin
// Implied pattern for asset loading
val texture: Texture by lazy {
    Texture(Gdx.files.internal("buildings/farm.png"))
}
```

### Caching Calculated Values
Cache expensive calculations when possible:
```kotlin
class Building(...) {
    private var cachedIncome: Double? = null
    
    fun totalIncome(): Double {
        if (cachedIncome == null) {
            cachedIncome = baseIncome * count
        }
        return cachedIncome!!
    }
}
```

## Documentation Patterns

### Single-Line Comments for Public APIs
Every public function has a single-line comment:
```kotlin
// Calculates total income per second from all sources
fun calculateTotalIncome(): Double { ... }
```

### Inline Comments for Complex Logic
Use inline comments to explain non-obvious logic:
```kotlin
// Check for potential overflow before multiplication
if (a != 0.0 && b > MAX_GAME_VALUE / a) {
    return MAX_GAME_VALUE
}

// Cap exponent to prevent overflow
val cappedExponent = exponent.coerceIn(-1000.0, 1000.0)
```

### TODO Comments for Future Work
Use TODO comments for planned features:
```kotlin
// TODO: Add cloud save support
// TODO: Implement leaderboards
```

## Constants and Configuration

### Companion Object Constants
Define constants in companion objects:
```kotlin
companion object {
    const val COST_GROWTH_RATE = 1.15
    const val MAX_GAME_VALUE = 1e308
    const val AUTO_SAVE_INTERVAL = 30f
    private const val SAVE_KEY = "save_data"
}
```

### Object-Level Constants
Use object singletons for global constants:
```kotlin
object SafeMath {
    const val MAX_GAME_VALUE = 1e308
    const val MIN_GAME_VALUE = 0.0
}
```

### Private Constants for Internal Use
Use `private const val` for implementation details:
```kotlin
companion object {
    private const val SAVE_KEY = "save_data"
    private const val BACKUP_KEY = "save_data_backup"
    private const val SAVE_VERSION = 1
}
```
