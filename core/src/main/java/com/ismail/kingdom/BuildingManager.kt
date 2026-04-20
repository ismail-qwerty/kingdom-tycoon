// PATH: core/src/main/java/com/ismail/kingdom/BuildingManager.kt
package com.ismail.kingdom

// Handles auto-income ticks for all owned buildings
class BuildingManager(private val state: GameState) {

    // Accumulates delta time and awards income each second
    fun update(delta: Float) {
        accum += delta
        if (accum >= 1f) {
            accum -= 1f
            tickIncome()
        }
    }

    // Sums income from all owned buildings and adds to state
    private fun tickIncome() {
        var total = 0.0
        for ((id, count) in state.ownedBuildings) {
            val building = BuildingRegistry.find(id) ?: continue
            total += building.totalIncome(count, state.era)
        }
        if (total > 0) state.addGold(total)
    }

    // Attempts to purchase one of the given building; returns true on success
    fun purchase(building: Building): Boolean {
        val owned = state.ownedBuildings.getOrDefault(building.id, 0)
        val cost = building.costAt(owned)
        if (!state.spendGold(cost)) return false
        state.ownedBuildings[building.id] = owned + 1
        return true
    }

    private var accum = 0f
}
