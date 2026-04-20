// PATH: core/src/main/java/com/ismail/kingdom/systems/PrestigeManager.kt
package com.ismail.kingdom.systems

import com.ismail.kingdom.models.GameState
import com.ismail.kingdom.models.Hero
import com.ismail.kingdom.models.HeroPassiveType
import kotlin.math.floor
import kotlin.math.pow

// Manages prestige system and progression layers
object PrestigeManager {
    
    // Prestige layer thresholds (total lifetime gold earned)
    private const val LAYER_1_THRESHOLD = 1_000_000_000.0      // 1 Billion
    private const val LAYER_2_THRESHOLD = 1_000_000_000_000.0  // 1 Trillion
    private const val LAYER_3_THRESHOLD = 1_000_000_000_000_000.0 // 1 Quadrillion
    
    // Crown shard rewards per prestige
    private const val BASE_CROWN_SHARDS = 10
    private const val SHARDS_PER_BILLION = 1 // 1 shard per billion gold earned
    
    // Checks if player can prestige to the next layer
    fun canPrestige(state: GameState): Boolean {
        val nextLayer = state.prestigeLayer + 1
        if (nextLayer > 3) return false // Max 3 prestige layers
        
        val requirement = getPrestigeRequirement(nextLayer)
        return state.totalLifetimeGold >= requirement
    }
    
    // Returns the gold requirement for a specific prestige layer
    fun getPrestigeRequirement(layer: Int): Double {
        return when (layer) {
            1 -> LAYER_1_THRESHOLD
            2 -> LAYER_2_THRESHOLD
            3 -> LAYER_3_THRESHOLD
            else -> Double.MAX_VALUE
        }
    }
    
    // Calculates crown shards earned from prestiging
    fun calculateCrownShardsEarned(state: GameState): Int {
        val billionsEarned = floor(state.totalLifetimeGold / 1_000_000_000.0).toInt()
        val bonusShards = billionsEarned * SHARDS_PER_BILLION
        
        // Base shards + bonus shards + layer multiplier
        val layerMultiplier = state.prestigeLayer + 1
        val totalShards = (BASE_CROWN_SHARDS + bonusShards) * layerMultiplier
        
        // Apply Cleopatra's crown shard bonus if unlocked
        val cleopatraBonus = getHeroMultiplier(state, HeroPassiveType.CROWN_SHARDS_BONUS)
        
        return (totalShards * cleopatraBonus).toInt()
    }
    
    // Performs prestige: resets progress, awards crown shards, advances layer
    fun performPrestige(state: GameState, unlockedHeroes: List<Hero>): GameState {
        if (!canPrestige(state)) {
            return state // Cannot prestige yet
        }
        
        // Calculate and award crown shards
        val shardsEarned = calculateCrownShardsEarned(state)
        
        // Create new game state with prestige bonuses
        val newState = GameState(
            currentEra = 1,
            prestigeLayer = state.prestigeLayer + 1,
            currentGold = 0.0,
            resources = mutableMapOf(),
            buildings = mutableListOf(),
            advisors = mutableListOf(),
            heroes = state.heroes.toMutableList(), // Preserve heroes
            activeQuests = mutableListOf(),
            totalLifetimeGold = 0.0,
            tapCount = 0,
            crownShards = state.crownShards + shardsEarned,
            incomeMultiplier = calculatePrestigeMultiplier(state.prestigeLayer + 1),
            lastSaveTime = System.currentTimeMillis()
        )
        
        return newState
    }
    
    // Calculates the permanent income multiplier from prestige layers
    private fun calculatePrestigeMultiplier(layer: Int): Double {
        // Each prestige layer gives +50% income
        return 1.0 + (layer * 0.5)
    }
    
    // Gets the total multiplier from a specific hero passive type
    fun getHeroMultiplier(state: GameState, type: HeroPassiveType): Double {
        // This would check unlocked heroes in the actual game state
        // For now, returns base multiplier of 1.0
        // TODO: Integrate with actual hero unlock system
        return 1.0
    }
    
    // Calculates total income multiplier including all bonuses
    fun getTotalIncomeMultiplier(state: GameState, unlockedHeroes: List<Hero>): Double {
        var multiplier = state.incomeMultiplier
        
        // Apply Merlin's income multiplier if unlocked
        val merlin = unlockedHeroes.find { it.passiveType == HeroPassiveType.INCOME_MULTIPLIER }
        if (merlin?.isUnlocked == true) {
            multiplier *= merlin.passiveValue
        }
        
        // Apply da Vinci's legend buff to all other hero bonuses
        val daVinci = unlockedHeroes.find { it.passiveType == HeroPassiveType.LEGEND_BUFF }
        val legendBuff = if (daVinci?.isUnlocked == true) daVinci.passiveValue else 1.0
        
        // Apply other hero multipliers with legend buff
        for (hero in unlockedHeroes) {
            if (hero.isUnlocked && hero.passiveType == HeroPassiveType.RESOURCE_MULTIPLIER) {
                multiplier *= (hero.passiveValue * legendBuff)
            }
        }
        
        return multiplier
    }
    
    // Calculates tap multiplier including hero bonuses
    fun getTapMultiplier(state: GameState, unlockedHeroes: List<Hero>): Double {
        var multiplier = 1.0
        
        // Apply King Arthur's tap multiplier if unlocked
        val arthur = unlockedHeroes.find { it.passiveType == HeroPassiveType.TAP_MULTIPLIER }
        if (arthur?.isUnlocked == true) {
            multiplier *= arthur.passiveValue
        }
        
        // Apply da Vinci's legend buff
        val daVinci = unlockedHeroes.find { it.passiveType == HeroPassiveType.LEGEND_BUFF }
        if (daVinci?.isUnlocked == true) {
            multiplier *= daVinci.passiveValue
        }
        
        return multiplier
    }
    
    // Calculates building cost multiplier (for Robin Hood's cost reduction)
    fun getCostMultiplier(unlockedHeroes: List<Hero>): Double {
        val robin = unlockedHeroes.find { it.passiveType == HeroPassiveType.COST_REDUCTION }
        if (robin?.isUnlocked == true) {
            return robin.passiveValue // 0.75 = 25% cost reduction
        }
        return 1.0
    }
    
    // Calculates offline earnings multiplier
    fun getOfflineMultiplier(unlockedHeroes: List<Hero>): Double {
        var multiplier = 1.0
        
        val lancelot = unlockedHeroes.find { it.passiveType == HeroPassiveType.OFFLINE_MULTIPLIER }
        if (lancelot?.isUnlocked == true) {
            multiplier *= lancelot.passiveValue
        }
        
        // Apply da Vinci's legend buff
        val daVinci = unlockedHeroes.find { it.passiveType == HeroPassiveType.LEGEND_BUFF }
        if (daVinci?.isUnlocked == true) {
            multiplier *= daVinci.passiveValue
        }
        
        return multiplier
    }
    
    // Calculates quest reward multiplier
    fun getQuestRewardMultiplier(unlockedHeroes: List<Hero>): Double {
        var multiplier = 1.0
        
        val guinevere = unlockedHeroes.find { it.passiveType == HeroPassiveType.QUEST_REWARDS }
        if (guinevere?.isUnlocked == true) {
            multiplier *= guinevere.passiveValue
        }
        
        // Apply da Vinci's legend buff
        val daVinci = unlockedHeroes.find { it.passiveType == HeroPassiveType.LEGEND_BUFF }
        if (daVinci?.isUnlocked == true) {
            multiplier *= daVinci.passiveValue
        }
        
        return multiplier
    }
    
    // Calculates advisor speed multiplier
    fun getAdvisorSpeedMultiplier(unlockedHeroes: List<Hero>): Double {
        var multiplier = 1.0
        
        val morgana = unlockedHeroes.find { it.passiveType == HeroPassiveType.ADVISOR_SPEED }
        if (morgana?.isUnlocked == true) {
            multiplier *= morgana.passiveValue
        }
        
        // Apply da Vinci's legend buff
        val daVinci = unlockedHeroes.find { it.passiveType == HeroPassiveType.LEGEND_BUFF }
        if (daVinci?.isUnlocked == true) {
            multiplier *= daVinci.passiveValue
        }
        
        return multiplier
    }
    
    // Returns prestige layer name
    fun getPrestigeLayerName(layer: Int): String {
        return when (layer) {
            0 -> "None"
            1 -> "Ascension"
            2 -> "Rift"
            3 -> "Legend"
            else -> "Unknown"
        }
    }
    
    // Returns progress towards next prestige (0.0 to 1.0)
    fun getPrestigeProgress(state: GameState): Double {
        if (state.prestigeLayer >= 3) return 1.0 // Max prestige reached
        
        val nextLayer = state.prestigeLayer + 1
        val requirement = getPrestigeRequirement(nextLayer)
        
        return (state.totalLifetimeGold / requirement).coerceIn(0.0, 1.0)
    }
}
