// PATH: core/src/main/java/com/ismail/kingdom/AdvisorManager.kt
package com.ismail.kingdom

// Drives advisor automation — each advisor auto-buys its target building on a timer
class AdvisorManager(
    private val state: GameState,
    private val buildingManager: BuildingManager
) {
    // Per-advisor elapsed time tracker
    private val timers = mutableMapOf<String, Float>()

    // Ticks all unlocked advisors and triggers purchases when interval elapses
    fun update(delta: Float) {
        for (advisorId in state.advisorsUnlocked) {
            val advisor = AdvisorRegistry.find(advisorId) ?: continue
            val elapsed = timers.getOrDefault(advisorId, 0f) + delta
            if (elapsed >= advisor.purchaseIntervalSeconds) {
                timers[advisorId] = 0f
                tryAutoPurchase(advisor)
            } else {
                timers[advisorId] = elapsed
            }
        }
    }

    // Attempts to auto-buy the advisor's target building
    private fun tryAutoPurchase(advisor: Advisor) {
        val building = BuildingRegistry.find(advisor.targetBuildingId) ?: return
        buildingManager.purchase(building)
    }

    // Unlocks an advisor if player can afford it; returns true on success
    fun unlock(advisor: Advisor): Boolean {
        if (advisor.id in state.advisorsUnlocked) return false
        if (!state.spendGold(advisor.unlockCost)) return false
        state.advisorsUnlocked.add(advisor.id)
        return true
    }
}
