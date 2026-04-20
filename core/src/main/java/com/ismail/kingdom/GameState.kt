// PATH: core/src/main/java/com/ismail/kingdom/GameState.kt
package com.ismail.kingdom

// Central mutable state shared across all game systems
data class GameState(
    var gold: Double = 0.0,
    var totalGoldEarned: Double = 0.0,
    var prestigeCount: Int = 0,
    var eraIndex: Int = 0,
    var ownedBuildings: MutableMap<String, Int> = mutableMapOf(),
    var advisorsUnlocked: MutableSet<String> = mutableSetOf()
) {
    // Current era derived from eraIndex
    val era: Era get() = Era.fromIndex(eraIndex)

    // Prestige threshold: 1 million gold × (10 ^ prestigeCount)
    val prestigeThreshold: Double get() = 1_000_000.0 * Math.pow(10.0, prestigeCount.toDouble())

    // Returns true if player can prestige
    fun canPrestige(): Boolean = totalGoldEarned >= prestigeThreshold

    // Resets gold and buildings, advances prestige counter and era
    fun prestige() {
        prestigeCount++
        eraIndex = minOf(eraIndex + 1, Era.all.lastIndex)
        gold = 0.0
        totalGoldEarned = 0.0
        ownedBuildings.clear()
        advisorsUnlocked.clear()
    }

    // Adds gold and tracks lifetime total
    fun addGold(amount: Double) {
        gold += amount
        totalGoldEarned += amount
    }

    // Deducts gold; returns false if insufficient
    fun spendGold(amount: Double): Boolean {
        if (gold < amount) return false
        gold -= amount
        return true
    }
}
