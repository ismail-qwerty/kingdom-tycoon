// PATH: core/src/main/java/com/ismail/kingdom/systems/AdvisorSystem.kt
package com.ismail.kingdom.systems

import com.ismail.kingdom.models.Advisor
import com.ismail.kingdom.models.GameState

// Manages advisor unlocking and automation
class AdvisorSystem {
    
    // Attempts to unlock an advisor
    fun unlockAdvisor(advisorId: String, state: GameState): Boolean {
        val advisor = state.advisors.find { it.id == advisorId } ?: return false
        
        // Use isHired (not isUnlocked) — isUnlocked defaults to true and means "visible"
        if (advisor.isHired) return false
        
        if (!state.spendGold(advisor.unlockCost)) return false
        
        advisor.isHired = true
        
        // Mark building as having advisor
        val building = state.buildings.find { it.id == advisor.buildingId }
        building?.hasAdvisor = true
        
        return true
    }
    
    // Gets the advisor for a specific building
    fun getAdvisorForBuilding(buildingId: String, state: GameState): Advisor? {
        return state.advisors.find { it.buildingId == buildingId }
    }
    
    // Checks if a building is automated (advisor has been hired)
    fun isAutomated(buildingId: String, state: GameState): Boolean {
        val advisor = state.advisors.find { it.buildingId == buildingId }
        return advisor?.isHired == true
    }
    
    // Gets automation status for all buildings
    fun getAutomationStatus(state: GameState): Map<String, Boolean> {
        val status = mutableMapOf<String, Boolean>()
        
        for (building in state.buildings) {
            status[building.id] = isAutomated(building.id, state)
        }
        
        return status
    }
    
    // Gets all hired advisors
    fun getUnlockedAdvisors(state: GameState): List<Advisor> {
        return state.advisors.filter { it.isHired }
    }
    
    // Gets all available advisors for purchase (building unlocked, advisor not yet hired)
    fun getAvailableAdvisors(state: GameState): List<Advisor> {
        return state.advisors.filter { advisor ->
            !advisor.isHired && 
            state.buildings.any { it.id == advisor.buildingId && it.isUnlocked }
        }
    }
    
    // Checks if player can afford an advisor
    fun canAffordAdvisor(advisorId: String, state: GameState): Boolean {
        val advisor = state.advisors.find { it.id == advisorId } ?: return false
        return state.currentGold >= advisor.unlockCost
    }
    
    // Gets the cheapest affordable advisor
    fun getCheapestAffordableAdvisor(state: GameState): Advisor? {
        return getAvailableAdvisors(state)
            .filter { canAffordAdvisor(it.id, state) }
            .minByOrNull { it.unlockCost }
    }
}
