// PATH: core/src/main/java/com/ismail/kingdom/models/FinalGameState.kt
package com.ismail.kingdom.models

import kotlinx.serialization.Serializable

/**
 * FINAL CONSOLIDATED GAME STATE
 * 
 * This is the authoritative GameState that includes ALL fields added throughout
 * the entire development process. Use this as the single source of truth.
 * 
 * Includes state for:
 * - Core progression (gold, era, prestige)
 * - Buildings and advisors
 * - Heroes and quests
 * - Map exploration and raids
 * - Events and spells
 * - Shadow Kingdom
 * - Achievements and statistics
 * - Tutorial progress
 * - Settings and preferences
 */
@Serializable
data class FinalGameState(
    // ===== CORE PROGRESSION =====
    var currentEra: Int = 1,
    var prestigeLayer: Int = 0,
    var totalPrestigesPerformed: Int = 0,
    var currentGold: Double = 0.0,
    var totalLifetimeGold: Double = 0.0,
    var tapCount: Long = 0,
    var crownShards: Int = 0,
    
    // ===== MULTIPLIERS =====
    var incomeMultiplier: Double = 1.0,
    var tapMultiplier: Double = 1.0,
    var offlineMultiplier: Double = 1.0,
    
    // ===== BUILDINGS =====
    val buildings: MutableList<Building> = mutableListOf(),
    var totalBuildingsBought: Int = 0,
    var totalGoldSpentOnBuildings: Double = 0.0,
    
    // ===== ADVISORS =====
    val advisors: MutableList<Advisor> = mutableListOf(),
    var totalAdvisorsHired: Int = 0,
    var totalGoldSpentOnAdvisors: Double = 0.0,
    
    // ===== HEROES =====
    val heroes: MutableList<Hero> = mutableListOf(),
    val unlockedHeroes: MutableList<String> = mutableListOf(),
    val permanentHeroPassives: MutableList<String> = mutableListOf(),
    var activeHeroId: String? = null,
    
    // ===== QUESTS =====
    val activeQuests: MutableList<Quest> = mutableListOf(),
    var totalQuestsCompleted: Int = 0,
    var currentQuestSlots: Int = 3,
    
    // ===== MAP EXPLORATION =====
    val mapTiles: MutableList<MapTile> = mutableListOf(),
    var totalMapTilesRevealed: Int = 0,
    val exploredTileIds: MutableList<String> = mutableListOf(),
    
    // ===== RAIDS & WAR =====
    var totalRaidsCompleted: Int = 0,
    val defeatedRaidCamps: MutableList<String> = mutableListOf(),
    var raidEnergy: Int = 100,
    var lastRaidTime: Long = 0L,
    
    // ===== EVENTS =====
    var currentEvent: KingdomEvent? = null,
    var nextEventTimer: Float = 0f,
    var totalEventsCompleted: Int = 0,
    val completedEventIds: MutableList<String> = mutableListOf(),
    
    // ===== SPELLS & MAGIC =====
    var totalSpellsCast: Int = 0,
    val unlockedSpells: MutableList<String> = mutableListOf(),
    val activeSpellEffects: MutableMap<String, Long> = mutableMapOf(), // spellId -> endTime
    
    // ===== SHADOW KINGDOM =====
    var shadowKingdomUnlocked: Boolean = false,
    var shadowGold: Double = 0.0,
    var shadowIncomePerSecond: Double = 0.0,
    val shadowBuildings: MutableMap<String, Int> = mutableMapOf(),
    var shadowBoostActive: Boolean = false,
    var shadowBoostEndTime: Long = 0L,
    
    // ===== ACHIEVEMENTS =====
    val unlockedAchievements: MutableList<String> = mutableListOf(),
    val achievementProgress: MutableMap<String, Double> = mutableMapOf(),
    
    // ===== STATISTICS =====
    var highestIPS: Double = 0.0,
    var totalPlaytimeSeconds: Long = 0L,
    var totalAdsWatched: Int = 0,
    var favoriteBuilding: String = "",
    val buildingPurchaseCounts: MutableMap<String, Int> = mutableMapOf(),
    
    // ===== PRESTIGE BONUSES =====
    val activePrestigeBonuses: MutableList<String> = mutableListOf(),
    var prestigeBonusMultiplier: Double = 1.0,
    
    // ===== TUTORIAL =====
    var tutorialCompleted: Boolean = false,
    var currentTutorialStep: String = "TAP_KINGDOM_HALL",
    val completedTutorialSteps: MutableList<String> = mutableListOf(),
    
    // ===== SESSION & SAVE =====
    var lastSaveTime: Long = 0L,
    var lastOnlineTime: Long = System.currentTimeMillis(),
    var sessionStartTime: Long = System.currentTimeMillis(),
    var saveVersion: Int = 1,
    
    // ===== SETTINGS (persisted) =====
    var musicVolume: Float = 0.7f,
    var sfxVolume: Float = 0.8f,
    var musicEnabled: Boolean = true,
    var sfxEnabled: Boolean = true,
    var notificationsEnabled: Boolean = true,
    var hapticFeedbackEnabled: Boolean = true,
    var showFPS: Boolean = false,
    var autoSaveEnabled: Boolean = true,
    
    // ===== RESOURCES (generic system) =====
    val resources: MutableMap<String, Resource> = mutableMapOf(),
    
    // ===== MILESTONES =====
    val reachedMilestones: MutableList<String> = mutableListOf(),
    var lastMilestoneReward: Long = 0L,
    
    // ===== DAILY REWARDS =====
    var lastDailyRewardClaim: Long = 0L,
    var consecutiveDailyLogins: Int = 0,
    
    // ===== HALL OF LEGENDS =====
    var hallOfLegendsUnlocked: Boolean = false,
    val legendaryHeroes: MutableList<String> = mutableListOf(),
    var legendPoints: Int = 0
) {
    init {
        // Initialize lastSaveTime to current time if not set during deserialization
        if (lastSaveTime == 0L) {
            lastSaveTime = System.currentTimeMillis()
        }
        
        // Initialize session start time
        if (sessionStartTime == 0L) {
            sessionStartTime = System.currentTimeMillis()
        }
    }
    
    // Adds gold and tracks lifetime total
    fun addGold(amount: Double) {
        currentGold += amount
        totalLifetimeGold += amount
    }
    
    // Spends gold if available
    fun spendGold(amount: Double): Boolean {
        if (currentGold < amount) return false
        currentGold -= amount
        return true
    }
    
    // Calculates total income multiplier from all sources
    fun getTotalIncomeMultiplier(): Double {
        var multiplier = incomeMultiplier
        
        // Prestige bonus
        multiplier *= prestigeBonusMultiplier
        
        // Crown shards bonus (0.2% per shard)
        multiplier *= (1.0 + crownShards * 0.002)
        
        // Active hero bonus
        activeHeroId?.let { heroId ->
            heroes.find { it.id == heroId }?.let { hero ->
                if (hero.isUnlocked) {
                    multiplier *= hero.passiveBonus
                }
            }
        }
        
        // Permanent hero passives
        permanentHeroPassives.forEach { passiveId ->
            multiplier *= 1.05 // Each permanent passive adds 5%
        }
        
        // Active event bonus
        currentEvent?.let { event ->
            if (event.type == KingdomEventType.DOUBLE_INCOME || event.type == KingdomEventType.GOLD_RUSH) {
                multiplier *= event.bonusMultiplier
            }
        }
        
        // Active spell effects
        activeSpellEffects.forEach { (spellId, endTime) ->
            if (System.currentTimeMillis() < endTime) {
                when (spellId) {
                    "gold_rush" -> multiplier *= 2.0
                    "prosperity" -> multiplier *= 1.5
                    // Add other spell multipliers
                }
            }
        }
        
        return multiplier
    }
    
    // Calculates offline time in seconds
    fun getOfflineTimeSeconds(): Long {
        val currentTime = System.currentTimeMillis()
        val offlineMs = currentTime - lastOnlineTime
        return (offlineMs / 1000).coerceAtLeast(0)
    }
    
    // Updates last online time
    fun updateOnlineTime() {
        lastOnlineTime = System.currentTimeMillis()
    }
    
    // Checks if player can prestige
    fun canPrestige(): Boolean {
        val requirement = 1e9 // 1 billion
        return totalLifetimeGold >= requirement
    }
    
    // Resets state for prestige (keeps permanent progress)
    fun performPrestigeReset() {
        // Reset temporary progress
        currentGold = 0.0
        totalLifetimeGold = 0.0
        tapCount = 0
        currentEra = 1
        
        // Reset buildings
        buildings.forEach { it.count = 0 }
        
        // Reset advisors (keep unlocked but reset automation)
        advisors.forEach { it.isHired = false }
        
        // Clear active content
        activeQuests.clear()
        currentEvent = null
        
        // Keep permanent progress:
        // - crownShards
        // - unlockedHeroes
        // - permanentHeroPassives
        // - prestigeLayer
        // - totalPrestigesPerformed
        // - achievements
        // - statistics
    }
}
