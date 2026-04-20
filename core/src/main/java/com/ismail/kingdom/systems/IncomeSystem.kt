// PATH: core/src/main/java/com/ismail/kingdom/systems/IncomeSystem.kt
package com.ismail.kingdom.systems

import com.ismail.kingdom.models.GameState
import com.ismail.kingdom.utils.SafeMath

// Manages passive income generation from buildings
class IncomeSystem(private val gameState: GameState) {

    // Optional event system reference for applying event income multipliers
    private var eventSystem: EventSystem? = null

    fun setEventSystem(system: EventSystem) {
        eventSystem = system
    }
    
    // Updates income and adds gold to game state
    fun update(delta: Float) {
        val incomePerSecond = calculateTotalIncome()
        
        // Use SafeMath for all additions
        val goldToAdd = SafeMath.safeMultiply(incomePerSecond, delta.toDouble())
        gameState.currentGold = SafeMath.clampGold(SafeMath.safeAdd(gameState.currentGold, goldToAdd))
        gameState.totalLifetimeGold = SafeMath.clampGold(SafeMath.safeAdd(gameState.totalLifetimeGold, goldToAdd))
        // Also track totalGoldEarned so statistics and prestige checks stay in sync
        gameState.totalGoldEarned = SafeMath.clampGold(SafeMath.safeAdd(gameState.totalGoldEarned, goldToAdd))
    }
    
    // Calculates total income per second from all sources
    fun calculateTotalIncome(): Double {
        var totalIncome = 0.0
        
        // Sum income from all buildings
        gameState.buildings.forEach { building ->
            val buildingIncome = SafeMath.safeMultiply(
                building.baseIncome,
                building.count.toDouble()
            )
            totalIncome = SafeMath.safeAdd(totalIncome, buildingIncome)
        }
        
        // Apply base income multiplier from game state
        totalIncome = SafeMath.safeMultiply(totalIncome, gameState.incomeMultiplier)

        // Apply active event income multiplier (e.g. GOBLIN_RAID 3×, DOUBLE_INCOME 2×)
        val eventMultiplier = eventSystem?.getEventIncomeMultiplier() ?: 1.0
        if (eventMultiplier != 1.0) {
            totalIncome = SafeMath.safeMultiply(totalIncome, eventMultiplier)
        }
        
        return totalIncome
    }

    // Alias for compatibility
    fun calculateTotalIPS(): Double = calculateTotalIncome()
    
    // Calculates offline earnings safely
    fun calculateOfflineEarnings(secondsOffline: Long): Double {
        // Sanitize offline time first
        val sanitizedSeconds = AntiCheatSystem.sanitizeOfflineTime(secondsOffline)
        
        val incomePerSecond = calculateTotalIncome()
        val offlineEarnings = SafeMath.safeMultiply(incomePerSecond, sanitizedSeconds.toDouble())
        
        return SafeMath.clampGold(offlineEarnings)
    }
}
