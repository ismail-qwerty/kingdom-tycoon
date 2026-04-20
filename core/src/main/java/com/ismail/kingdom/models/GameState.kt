// PATH: core/src/main/java/com/ismail/kingdom/models/GameState.kt
package com.ismail.kingdom.models

import kotlinx.serialization.Serializable
import kotlin.math.pow

// Central game state with all persistent data
@Serializable
data class GameState(
    // Core progression
    var currentGold: Double = 10.0,
    var currentEra: Int = 1,
    var prestigeLayer: Int = 0,
    var crownShards: Int = 0,
    var incomeMultiplier: Double = 1.0,

    // Statistics
    var tapCount: Long = 0,
    var totalLifetimeGold: Double = 0.0,
    var totalGoldEarned: Double = 0.0,
    var totalTaps: Long = 0,
    var totalBuildingsBought: Long = 0,
    var totalPrestigesPerformed: Int = 0,
    var playtimeSeconds: Long = 0,

    // Collections
    var buildings: MutableList<Building> = mutableListOf(),
    var advisors: MutableList<Advisor> = mutableListOf(),
    var heroes: MutableList<Hero> = mutableListOf(),
    var activeQuests: MutableList<Quest> = mutableListOf(),
    var mapTiles: MutableList<MapTile> = mutableListOf(),
    var unlockedHeroes: MutableList<String> = mutableListOf(),
    var activePrestigeBonuses: MutableList<String> = mutableListOf(),
    var permanentHeroPassives: MutableList<String> = mutableListOf(),

    // Resources
    var resources: MutableMap<String, Resource> = mutableMapOf(),

    // Events
    var currentEvent: KingdomEvent? = null,
    var nextEventTimer: Float = 0f,

    // Shadow Kingdom
    var shadowKingdomUnlocked: Boolean = false,
    var shadowGold: Double = 0.0,
    var shadowBuildings: MutableList<Building> = mutableListOf(),

    // Tutorial
    var tutorialCompleted: Boolean = false,
    var currentTutorialStep: String = "TAP_KINGDOM_HALL",
    var completedTutorialSteps: MutableList<String> = mutableListOf(),

    // Session data
    var lastSaveTime: Long = System.currentTimeMillis(),
    var sessionStartTime: Long = System.currentTimeMillis()
) {
    // Legacy compatibility aliases for older callers
    var gold: Double
        get() = currentGold
        set(value) { currentGold = value }

    var eraIndex: Int
        get() = (currentEra - 1).coerceAtLeast(0)
        set(value) { currentEra = value + 1 }

    var prestigeCount: Int
        get() = totalPrestigesPerformed
        set(value) { totalPrestigesPerformed = value }

    val ownedBuildings: MutableMap<String, Int>
        get() = buildings.associate { it.id to it.count }.toMutableMap()

    val advisorsUnlocked: MutableSet<String>
        get() = advisors.filter { it.isHired }.map { it.id }.toMutableSet()

    val prestigeThreshold: Double
        get() = 1_000_000.0 * 10.0.pow(prestigeCount.toDouble())

    // Calculate total income multiplier from all sources
    fun getTotalIncomeMultiplier(): Double {
        var multiplier = 1.0
        multiplier *= (1.0 + crownShards * 0.002)
        multiplier *= incomeMultiplier
        return multiplier
    }

    // Adds gold and tracks lifetime total
    fun addGold(amount: Double) {
        currentGold += amount
        totalGoldEarned += amount
        totalLifetimeGold += amount
        resources.getOrPut("gold") { Resource("gold", "Gold", 0.0, 1) }.amount += amount
    }

    // Spends gold if available
    fun spendGold(amount: Double): Boolean {
        if (currentGold < amount) return false
        currentGold -= amount
        resources.getOrPut("gold") { Resource("gold", "Gold", 0.0, 1) }.amount = currentGold
        return true
    }

    fun canPrestige(): Boolean = totalGoldEarned >= prestigeThreshold

    // Reset for prestige
    fun performPrestigeReset(keepHeroes: Boolean = false) {
        currentGold = 10.0
        currentEra = 1
        buildings.forEach { it.count = 0 }
        advisors.forEach { it.isHired = false; it.level = 0 }
        activeQuests.clear()
        mapTiles.clear()
        resources.clear()
        resources["gold"] = Resource("gold", "Gold", currentGold, 1)
        if (!keepHeroes) heroes.forEach { it.isUnlocked = false; it.level = 0 }
    }

    fun prestige() {
        totalPrestigesPerformed++
        performPrestigeReset()
    }

    // Full reset for settings screen
    fun reset() {
        currentGold = 10.0
        currentEra = 1
        prestigeLayer = 0
        crownShards = 0
        incomeMultiplier = 1.0
        tapCount = 0
        totalLifetimeGold = 0.0
        totalGoldEarned = 0.0
        totalTaps = 0
        totalBuildingsBought = 0
        totalPrestigesPerformed = 0
        playtimeSeconds = 0
        buildings.forEach { it.count = 0; it.isUnlocked = false }
        advisors.forEach { it.isHired = false; it.level = 0 }
        heroes.forEach { it.isUnlocked = false; it.level = 0 }
        activeQuests.clear()
        mapTiles.clear()
        unlockedHeroes.clear()
        activePrestigeBonuses.clear()
        permanentHeroPassives.clear()
        resources.clear()
        shadowKingdomUnlocked = false
        shadowGold = 0.0
        shadowBuildings.clear()
        tutorialCompleted = false
        currentTutorialStep = "TAP_KINGDOM_HALL"
        completedTutorialSteps.clear()
        lastSaveTime = System.currentTimeMillis()
        sessionStartTime = System.currentTimeMillis()
    }
}
