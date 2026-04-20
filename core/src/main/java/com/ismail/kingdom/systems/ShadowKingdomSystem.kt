// PATH: core/src/main/java/com/ismail/kingdom/systems/ShadowKingdomSystem.kt
package com.ismail.kingdom.systems

import com.ismail.kingdom.models.GameState
import com.ismail.kingdom.models.Building

// Represents the shadow dimension state
data class ShadowState(
    var shadowGold: Double = 0.0,
    var shadowIncomePerSecond: Double = 0.0,
    val shadowBuildings: MutableMap<String, Int> = mutableMapOf(),
    var shadowBoostActive: Boolean = false,
    var shadowBoostEndTime: Long = 0L,
    var isUnlocked: Boolean = false
)

// Manages the shadow kingdom dimension unlocked at Prestige Layer 2
class ShadowKingdomSystem(private val gameState: GameState) {
    
    val shadowState = ShadowState()
    
    // Unlocks the shadow kingdom after Rift prestige
    fun unlockShadowKingdom() {
        shadowState.isUnlocked = true
        mirrorMainKingdom()
    }
    
    // Mirrors the main kingdom buildings at 50% count
    fun mirrorMainKingdom() {
        shadowState.shadowBuildings.clear()
        
        // Mirror each building type from main kingdom at 50%
        gameState.buildings.forEach { building: Building ->
            val shadowCount = (building.count / 2).coerceAtLeast(0)
            shadowState.shadowBuildings[building.id] = shadowCount
        }
        
        recalculateShadowIncome()
    }
    
    // Updates shadow buildings when main kingdom buildings change
    fun onMainBuildingBought(buildingId: String, newCount: Int) {
        if (!shadowState.isUnlocked) return
        
        val shadowCount = (newCount / 2).coerceAtLeast(0)
        shadowState.shadowBuildings[buildingId] = shadowCount
        recalculateShadowIncome()
    }
    
    // Recalculates shadow income based on shadow buildings
    private fun recalculateShadowIncome() {
        var totalIncome = 0.0
        
        shadowState.shadowBuildings.forEach { (buildingId, count) ->
            val baseIncome = getBuildingBaseIncome(buildingId)
            totalIncome += baseIncome * count
        }
        
        shadowState.shadowIncomePerSecond = totalIncome
    }
    
    // Returns base income for a building (placeholder - should match building definitions)
    private fun getBuildingBaseIncome(buildingId: String): Double {
        return when {
            buildingId.contains("farm") -> 5.0
            buildingId.contains("mine") -> 20.0
            buildingId.contains("lumber") -> 15.0
            buildingId.contains("quarry") -> 50.0
            buildingId.contains("market") -> 100.0
            buildingId.contains("temple") -> 500.0
            buildingId.contains("castle") -> 2000.0
            else -> 10.0
        }
    }
    
    // Updates shadow kingdom income per frame
    fun update(delta: Float) {
        if (!shadowState.isUnlocked) return
        
        var income = shadowState.shadowIncomePerSecond * delta
        
        // Apply shadow boost multiplier if active
        if (shadowState.shadowBoostActive) {
            if (System.currentTimeMillis() < shadowState.shadowBoostEndTime) {
                income *= 2.0
            } else {
                shadowState.shadowBoostActive = false
            }
        }
        
        shadowState.shadowGold += income
        
        // Transfer shadow gold to main gold pool
        gameState.gold += income
    }
    
    // Activates shadow boost after watching ad
    fun activateShadowBoost() {
        shadowState.shadowBoostActive = true
        shadowState.shadowBoostEndTime = System.currentTimeMillis() + (30 * 60 * 1000) // 30 minutes
    }
    
    // Returns remaining boost time in seconds
    fun getBoostTimeRemaining(): Int {
        if (!shadowState.shadowBoostActive) return 0
        val remaining = (shadowState.shadowBoostEndTime - System.currentTimeMillis()) / 1000
        return remaining.toInt().coerceAtLeast(0)
    }
    
    // Checks if shadow kingdom is unlocked
    fun isUnlocked(): Boolean = shadowState.isUnlocked
    
    // Returns shadow income per second with boost applied
    fun getCurrentShadowIPS(): Double {
        return if (shadowState.shadowBoostActive && System.currentTimeMillis() < shadowState.shadowBoostEndTime) {
            shadowState.shadowIncomePerSecond * 2.0
        } else {
            shadowState.shadowIncomePerSecond
        }
    }
}
