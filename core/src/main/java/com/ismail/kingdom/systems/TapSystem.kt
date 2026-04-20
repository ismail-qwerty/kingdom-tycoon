// PATH: core/src/main/java/com/ismail/kingdom/systems/TapSystem.kt
package com.ismail.kingdom.systems

import com.badlogic.gdx.math.Vector2
import com.ismail.kingdom.models.GameState
import com.ismail.kingdom.models.HeroPassiveType
import kotlin.math.max
import kotlin.random.Random

// Represents a tap event with earned gold and visual data
data class TapEvent(
    val goldEarned: Double,
    val position: Vector2,
    val isCritical: Boolean
)

// Manages tap mechanics, critical hits, and combo system
class TapSystem(private val incomeSystem: IncomeSystem) {
    
    private val BASE_TAP_INCOME = 1.0
    private val IPS_TAP_RATIO = 0.01 // Tap income = 1% of IPS
    private val CRITICAL_CHANCE = 0.05 // 5% chance
    private val CRITICAL_MULTIPLIER = 10.0 // 10x gold on critical
    private val CROWN_SHARD_BONUS_PER_SHARD = 0.02 // Same as income system
    
    // Combo system
    private val COMBO_WINDOW_MS = 500L // 500ms between taps to maintain combo
    private val COMBO_DECAY_SECONDS = 2.0f // Reset after 2 seconds of no tapping
    
    private var comboCount = 0
    private var lastTapTime = 0L
    private var comboDecayTimer = 0f
    
    // Reference to EventSystem for event multipliers
    private var eventSystem: EventSystem? = null
    
    // Sets the event system reference
    fun setEventSystem(system: EventSystem) {
        eventSystem = system
    }
    
    // Processes a tap and returns the tap event
    fun tap(position: Vector2, state: GameState): TapEvent {
        val now = System.currentTimeMillis()
        
        // Update combo
        if (now - lastTapTime <= COMBO_WINDOW_MS) {
            comboCount++
        } else {
            comboCount = 1
        }
        lastTapTime = now
        comboDecayTimer = 0f // Reset decay timer
        
        // Calculate base tap income
        val totalIPS = incomeSystem.calculateTotalIPS()
        val baseTapIncome = max(BASE_TAP_INCOME, totalIPS * IPS_TAP_RATIO)
        
        // Apply crown shard bonus
        val crownShardBonus = 1.0 + (state.crownShards * CROWN_SHARD_BONUS_PER_SHARD)
        var tapIncome = baseTapIncome * crownShardBonus
        
        // Apply hero multipliers
        val unlockedHeroes = state.heroes.filter { it.isUnlocked }
        val heroMultiplier = getTapHeroMultiplier(unlockedHeroes)
        tapIncome *= heroMultiplier
        
        // Apply combo multiplier
        val comboMultiplier = getCurrentComboMultiplier()
        tapIncome *= comboMultiplier
        
        // Apply event tap multiplier
        tapIncome *= (eventSystem?.getEventTapMultiplier() ?: 1.0)
        
        // Check for critical hit
        val isCritical = Random.nextDouble() < CRITICAL_CHANCE
        if (isCritical) {
            tapIncome *= CRITICAL_MULTIPLIER
        }
        
        return TapEvent(
            goldEarned = tapIncome,
            position = position.cpy(),
            isCritical = isCritical
        )
    }
    
    // Updates combo decay timer
    fun update(delta: Float) {
        if (comboCount > 0) {
            comboDecayTimer += delta
            
            // Reset combo after decay period
            if (comboDecayTimer >= COMBO_DECAY_SECONDS) {
                comboCount = 0
                comboDecayTimer = 0f
            }
        }
    }
    
    // Returns current combo multiplier based on combo count
    fun getCurrentComboMultiplier(): Double {
        return when {
            comboCount < 5 -> 1.0
            comboCount < 10 -> 1.5
            comboCount < 20 -> 2.0
            else -> 3.0
        }
    }
    
    // Returns current combo count for UI display
    fun getComboCount(): Int = comboCount
    
    // Returns time remaining before combo resets (0.0 to 1.0)
    fun getComboDecayProgress(): Float {
        if (comboCount == 0) return 0f
        return (comboDecayTimer / COMBO_DECAY_SECONDS).coerceIn(0f, 1f)
    }
    
    // Returns preview of tap income for UI (without critical or combo)
    fun getTapIncomePreview(state: GameState): Double {
        val totalIPS = incomeSystem.calculateTotalIPS()
        val baseTapIncome = max(BASE_TAP_INCOME, totalIPS * IPS_TAP_RATIO)
        
        // Apply crown shard bonus
        val crownShardBonus = 1.0 + (state.crownShards * CROWN_SHARD_BONUS_PER_SHARD)
        var tapIncome = baseTapIncome * crownShardBonus
        
        // Apply hero multipliers
        val unlockedHeroes = state.heroes.filter { it.isUnlocked }
        val heroMultiplier = getTapHeroMultiplier(unlockedHeroes)
        tapIncome *= heroMultiplier
        
        return tapIncome
    }
    
    // Returns tap income with current combo multiplier (for UI display)
    fun getTapIncomeWithCombo(state: GameState): Double {
        val baseIncome = getTapIncomePreview(state)
        val comboMultiplier = getCurrentComboMultiplier()
        return baseIncome * comboMultiplier
    }
    
    // Calculates hero tap multiplier with da Vinci's legend buff
    private fun getTapHeroMultiplier(heroes: List<com.ismail.kingdom.models.Hero>): Double {
        var multiplier = 1.0
        
        // King Arthur: TAP_MULTIPLIER
        val arthur = heroes.find { it.passiveType == HeroPassiveType.TAP_MULTIPLIER }
        if (arthur?.isUnlocked == true) {
            multiplier *= arthur.passiveValue
        }
        
        // da Vinci: LEGEND_BUFF (amplifies other hero bonuses)
        val daVinci = heroes.find { it.passiveType == HeroPassiveType.LEGEND_BUFF }
        if (daVinci?.isUnlocked == true) {
            // Apply legend buff to the bonus portion only
            val bonusMultiplier = multiplier - 1.0
            multiplier = 1.0 + (bonusMultiplier * daVinci.passiveValue)
        }
        
        return multiplier
    }
    
    // Gets a breakdown of all tap multipliers for UI display
    fun getTapMultiplierBreakdown(state: GameState): Map<String, Double> {
        val breakdown = mutableMapOf<String, Double>()
        
        // Base
        breakdown["Base"] = 1.0
        
        // Crown Shards
        val crownShardBonus = 1.0 + (state.crownShards * CROWN_SHARD_BONUS_PER_SHARD)
        if (crownShardBonus > 1.0) {
            breakdown["Crown Shards"] = crownShardBonus
        }
        
        // Heroes
        val unlockedHeroes = state.heroes.filter { it.isUnlocked }
        val arthur = unlockedHeroes.find { it.passiveType == HeroPassiveType.TAP_MULTIPLIER }
        if (arthur != null) {
            breakdown["King Arthur"] = arthur.passiveValue
        }
        
        val daVinci = unlockedHeroes.find { it.passiveType == HeroPassiveType.LEGEND_BUFF }
        if (daVinci != null) {
            breakdown["da Vinci"] = daVinci.passiveValue
        }
        
        // Combo
        val comboMultiplier = getCurrentComboMultiplier()
        if (comboMultiplier > 1.0) {
            breakdown["Combo x$comboCount"] = comboMultiplier
        }
        
        return breakdown
    }
    
    // Resets combo (for testing or special events)
    fun resetCombo() {
        comboCount = 0
        comboDecayTimer = 0f
    }
    
    // Gets combo tier name for UI display
    fun getComboTierName(): String {
        return when {
            comboCount < 5 -> ""
            comboCount < 10 -> "Good!"
            comboCount < 20 -> "Great!"
            else -> "Amazing!"
        }
    }
    
    // Checks if combo is active
    fun isComboActive(): Boolean = comboCount > 0
    
    // Gets the next combo tier threshold
    fun getNextComboThreshold(): Int {
        return when {
            comboCount < 5 -> 5
            comboCount < 10 -> 10
            comboCount < 20 -> 20
            else -> -1 // Max tier reached
        }
    }
}
