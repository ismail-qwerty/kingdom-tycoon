// PATH: core/src/main/java/com/ismail/kingdom/systems/StatisticsTracker.kt
package com.ismail.kingdom.systems

import com.ismail.kingdom.models.GameState
import com.ismail.kingdom.models.Building

// Tracks all lifetime statistics for the player
data class Statistics(
    var totalGoldEarned: Double = 0.0,
    var totalTaps: Long = 0L,
    var totalBuildingsBought: Int = 0,
    var totalPrestigesPerformed: Int = 0,
    var totalQuestsCompleted: Int = 0,
    var totalMapTilesRevealed: Int = 0,
    var totalAdsWatched: Int = 0,
    var totalPlaytimeSeconds: Long = 0L,
    var highestIPS: Double = 0.0,
    var totalGoldSpentOnBuildings: Double = 0.0,
    var totalGoldSpentOnAdvisors: Double = 0.0,
    var favoriteBuilding: String = "",
    var currentSessionStart: Long = System.currentTimeMillis(),
    val buildingPurchaseCounts: MutableMap<String, Int> = mutableMapOf()
)

// Manages tracking of all game statistics
class StatisticsTracker(private val gameState: GameState) {
    
    val stats = Statistics()
    
    // Updates playtime and dynamic stats
    fun update(delta: Float) {
        stats.totalPlaytimeSeconds += delta.toLong()
        
        // Update highest IPS
        val currentIPS = calculateCurrentIPS()
        if (currentIPS > stats.highestIPS) {
            stats.highestIPS = currentIPS
        }
        
        // Update favorite building
        updateFavoriteBuilding()
    }
    
    // Calculates current income per second
    private fun calculateCurrentIPS(): Double {
        var totalIPS = 0.0
        gameState.buildings.forEach { building: Building ->
            val count: Double = building.count.toDouble()
            totalIPS += building.baseIncome * count
        }
        return totalIPS
    }
    
    // Updates the favorite building based on purchase counts
    private fun updateFavoriteBuilding() {
        val maxEntry = stats.buildingPurchaseCounts.maxByOrNull { it.value }
        if (maxEntry != null) {
            stats.favoriteBuilding = maxEntry.key
        }
    }
    
    // Records a tap event
    fun recordTap(goldEarned: Double) {
        stats.totalTaps++
        stats.totalGoldEarned += goldEarned
    }
    
    // Records a building purchase
    fun recordBuildingPurchase(buildingId: String, cost: Double) {
        stats.totalBuildingsBought++
        stats.totalGoldSpentOnBuildings += cost
        
        val currentCount = stats.buildingPurchaseCounts.getOrDefault(buildingId, 0)
        stats.buildingPurchaseCounts[buildingId] = currentCount + 1
        
        updateFavoriteBuilding()
    }
    
    // Records a prestige event
    fun recordPrestige() {
        stats.totalPrestigesPerformed++
    }
    
    // Records a quest completion
    fun recordQuestCompleted() {
        stats.totalQuestsCompleted++
    }
    
    // Records a map tile reveal
    fun recordMapTileRevealed() {
        stats.totalMapTilesRevealed++
    }
    
    // Records an ad watch
    fun recordAdWatched() {
        stats.totalAdsWatched++
    }
    
    // Records advisor hire
    fun recordAdvisorHire(cost: Double) {
        stats.totalGoldSpentOnAdvisors += cost
    }
    
    // Records passive gold earned
    fun recordPassiveGold(amount: Double) {
        stats.totalGoldEarned += amount
    }
    
    // Returns total playtime formatted as string
    fun getFormattedPlaytime(): String {
        val totalSeconds = stats.totalPlaytimeSeconds
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return "${hours}h ${minutes}m ${seconds}s"
    }
    
    // Returns current session time formatted
    fun getFormattedSessionTime(): String {
        val sessionSeconds = (System.currentTimeMillis() - stats.currentSessionStart) / 1000
        val hours = sessionSeconds / 3600
        val minutes = (sessionSeconds % 3600) / 60
        val seconds = sessionSeconds % 60
        return "${hours}h ${minutes}m ${seconds}s"
    }
    
    // Returns top N buildings by purchase count
    fun getTopBuildings(n: Int): List<Pair<String, Int>> {
        return stats.buildingPurchaseCounts.entries
            .sortedByDescending { it.value }
            .take(n)
            .map { it.key to it.value }
    }
    
    // Returns income breakdown by building type
    fun getIncomeBreakdown(): Map<String, Double> {
        val breakdown = mutableMapOf<String, Double>()
        
        gameState.buildings.forEach { building: Building ->
            val count: Double = building.count.toDouble()
            val totalIncome = building.baseIncome * count
            breakdown[building.id] = totalIncome
        }
        
        return breakdown.entries
            .sortedByDescending { it.value }
            .take(10)
            .associate { it.key to it.value }
    }
    
    // Resets session timer
    fun startNewSession() {
        stats.currentSessionStart = System.currentTimeMillis()
    }
}
