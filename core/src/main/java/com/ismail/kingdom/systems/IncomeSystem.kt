// PATH: core/src/main/java/com/ismail/kingdom/systems/IncomeSystem.kt
package com.ismail.kingdom.systems

import com.ismail.kingdom.models.GameState
import com.ismail.kingdom.utils.SafeMath

// Manages passive income generation from buildings
class IncomeSystem(private val gameState: GameState) {
    
    // Updates income and adds gold to game state
    fun update(delta: Float) {
        val incomePerSecond = calculateTotalIncome()
        
        // Use SafeMath for all additions
        val goldToAdd = SafeMath.safeMultiply(incomePerSecond, delta.toDouble())
        gameState.currentGold = SafeMath.safeAdd(gameState.currentGold, goldToAdd)
        gameState.totalLifetimeGold = SafeMath.safeAdd(gameState.totalLifetimeGold, goldToAdd)
        
        // Clamp gold to valid range
        gameState.currentGold = SafeMath.clampGold(gameState.currentGold)
        gameState.totalLifetimeGold = SafeMath.clampGold(gameState.totalLifetimeGold)
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
        
        // Apply income multiplier
        totalIncome = SafeMath.safeMultiply(totalIncome, gameState.incomeMultiplier)
        
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
