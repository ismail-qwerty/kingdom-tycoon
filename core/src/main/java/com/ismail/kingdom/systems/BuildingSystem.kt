// PATH: core/src/main/java/com/ismail/kingdom/systems/BuildingSystem.kt
package com.ismail.kingdom.systems

import com.ismail.kingdom.models.GameState
import com.ismail.kingdom.utils.SafeMath

// Manages building purchases and upgrades
class BuildingSystem(private val gameState: GameState) {
    
    private val COST_GROWTH_RATE = 1.15
    
    // Calculates current cost for a building using SafeMath
    fun currentCost(buildingId: String): Double {
        val building = gameState.buildings.find { it.id == buildingId } ?: return 0.0
        
        // Use SafeMath for exponential cost calculation
        return SafeMath.safeBuildingCost(
            baseCost = building.baseCost,
            count = building.count,
            growthRate = COST_GROWTH_RATE
        )
    }
    
    // Purchases a building if player can afford it
    fun purchaseBuilding(buildingId: String): Boolean {
        val cost = currentCost(buildingId)
        
        // Validate cost is reasonable
        if (!SafeMath.isValidGameNumber(cost)) {
            println("BuildingSystem: Invalid cost calculated for $buildingId")
            return false
        }
        
        if (gameState.currentGold < cost) {
            return false
        }
        
        // Deduct cost using SafeMath
        gameState.currentGold = SafeMath.safeSubtract(gameState.currentGold, cost)
        gameState.currentGold = SafeMath.clampGold(gameState.currentGold)
        
        // Increment building count
        val building = gameState.buildings.find { it.id == buildingId }
        if (building != null) {
            building.count++
            
            // Validate building count
            building.count = AntiCheatSystem.validateIntValue(
                building.count,
                "building_$buildingId",
                min = 0,
                max = 10000
            )
        }
        
        return true
    }
    
    // Calculates bulk purchase cost for N buildings
    fun bulkPurchaseCost(buildingId: String, quantity: Int): Double {
        val building = gameState.buildings.find { it.id == buildingId } ?: return 0.0
        
        var totalCost = 0.0
        val currentCount = building.count
        
        for (i in 0 until quantity) {
            val cost = SafeMath.safeBuildingCost(
                baseCost = building.baseCost,
                count = currentCount + i,
                growthRate = COST_GROWTH_RATE
            )
            totalCost = SafeMath.safeAdd(totalCost, cost)
        }
        
        return totalCost
    }

    // Alias for purchaseBuilding
    fun buyBuilding(buildingId: String): Boolean = purchaseBuilding(buildingId)

    // Gets affordable quantity for bulk buy
    fun getAffordableQuantity(buildingId: String, maxQuantity: Int): Int {
        var affordable = 0
        var totalCost = 0.0
        
        for (i in 1..maxQuantity) {
            val cost = bulkPurchaseCost(buildingId, i)
            if (cost <= gameState.currentGold) {
                affordable = i
                totalCost = cost
            } else {
                break
            }
        }
        
        return affordable
    }

    // Bulk buy buildings
    fun bulkBuy(buildingId: String, quantity: Int): Boolean {
        val cost = bulkPurchaseCost(buildingId, quantity)
        
        if (!SafeMath.isValidGameNumber(cost)) {
            return false
        }
        
        if (gameState.currentGold < cost) {
            return false
        }
        
        gameState.currentGold = SafeMath.safeSubtract(gameState.currentGold, cost)
        gameState.currentGold = SafeMath.clampGold(gameState.currentGold)
        
        val building = gameState.buildings.find { it.id == buildingId }
        if (building != null) {
            building.count += quantity
            building.count = AntiCheatSystem.validateIntValue(
                building.count,
                "building_$buildingId",
                min = 0,
                max = 10000
            )
        }
        
        return true
    }
}
