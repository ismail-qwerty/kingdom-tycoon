// PATH: core/src/main/java/com/ismail/kingdom/systems/PrestigeSystem.kt
package com.ismail.kingdom.systems

import com.ismail.kingdom.models.GameState
import com.ismail.kingdom.utils.SafeMath
import kotlin.math.pow

// Manages prestige mechanics with balanced progression
class PrestigeSystem(private val gameState: GameState) {

    companion object {
        // Tuned prestige constants
        private const val CROWN_SHARD_BONUS_PER_SHARD = 0.002 // 0.2% per shard
        private const val PRESTIGE_BASE_REQUIREMENT = 1e9 // 1 billion gold
        private const val PRESTIGE_SCALING_EXPONENT = 1.5 // Exponential scaling

        // Prestige layer thresholds
        private const val LAYER_2_PRESTIGE_COUNT = 10 // Rift unlocks at 10 prestiges
        private const val LAYER_3_PRESTIGE_COUNT = 25 // Legend unlocks at 25 prestiges

        // Crown shard caps per prestige
        private const val MAX_SHARDS_PER_PRESTIGE = 1000
    }

    // Calculates crown shards earned from prestige using SafeMath
    fun calculateCrownShards(): Int {
        // Formula: sqrt(totalLifetimeGold / 1B) * layerMultiplier
        val lifetimeGold = SafeMath.clampGold(gameState.totalLifetimeGold)

        // Divide by 1 billion
        val goldInBillions = SafeMath.safeDivide(lifetimeGold, PRESTIGE_BASE_REQUIREMENT)

        // Square root using safe exponentiation
        val sqrtValue = SafeMath.safeExp(goldInBillions, 0.5)

        // Apply prestige layer multiplier
        val layerMultiplier = getPrestigeLayerMultiplier()
        val shardsDouble = SafeMath.safeMultiply(sqrtValue, layerMultiplier)

        // Convert to int and validate
        val shards = shardsDouble.toInt().coerceIn(0, MAX_SHARDS_PER_PRESTIGE)

        return shards
    }

    // Gets prestige layer multiplier
    private fun getPrestigeLayerMultiplier(): Double {
        return when (gameState.prestigeLayer) {
            0 -> 1.0 // Base prestige
            1 -> 1.5 // Rift (50% bonus)
            2 -> 2.0 // Legend (100% bonus)
            else -> 1.0
        }
    }

    // Performs prestige and resets game state
    fun performPrestige(): Boolean {
        val shardsEarned = calculateCrownShards()

        if (shardsEarned < 1) {
            return false // Not enough progress to prestige
        }

        // Add crown shards using safe addition
        val newShards = SafeMath.safeAdd(gameState.crownShards.toDouble(), shardsEarned.toDouble()).toInt()
        gameState.crownShards = AntiCheatSystem.validateIntValue(
            newShards,
            "crownShards",
            min = 0,
            max = 100000
        )

        // Increment prestige count
        gameState.totalPrestigesPerformed++

        // Check for prestige layer unlocks
        checkPrestigeLayerUnlock()

        // Reset game state
        resetGameState()

        return true
    }

    // Checks and unlocks prestige layers
    private fun checkPrestigeLayerUnlock() {
        when {
            gameState.totalPrestigesPerformed >= LAYER_3_PRESTIGE_COUNT && gameState.prestigeLayer < 2 -> {
                gameState.prestigeLayer = 2
                println("Prestige Layer 3 (Legend) unlocked!")
            }
            gameState.totalPrestigesPerformed >= LAYER_2_PRESTIGE_COUNT && gameState.prestigeLayer < 1 -> {
                gameState.prestigeLayer = 1
                gameState.shadowKingdomUnlocked = true
                println("Prestige Layer 2 (Rift) unlocked! Shadow Kingdom available!")
            }
        }
    }

    // Resets game state for prestige
    private fun resetGameState() {
        gameState.currentGold = 0.0
        gameState.totalLifetimeGold = 0.0
        gameState.tapCount = 0
        gameState.currentEra = 1

        // Reset buildings
        gameState.buildings.forEach { it.count = 0 }

        // Clear active content
        gameState.activeQuests.clear()
        gameState.currentEvent = null

        // Keep permanent unlocks: heroes, advisors, crown shards
    }

    // Calculates prestige bonus multiplier from crown shards
    fun calculatePrestigeBonus(): Double {
        val totalBonus = SafeMath.safeMultiply(
            gameState.crownShards.toDouble(),
            CROWN_SHARD_BONUS_PER_SHARD
        )

        // Add 1.0 for base multiplier
        return SafeMath.safeAdd(1.0, totalBonus)
    }

    // Checks if player can prestige
    fun canPrestige(): Boolean {
        return calculateCrownShards() >= 1
    }

    // Gets prestige requirement for a specific layer
    fun getPrestigeRequirement(layer: Int): Double {
        val multiplier = when (layer) {
            1 -> 1.0
            2 -> 1.5
            3 -> 2.0
            else -> 1.0
        }
        val minShards = 1.0 / multiplier
        val goldNeeded = SafeMath.safeExp(minShards, 2.0) * PRESTIGE_BASE_REQUIREMENT
        return SafeMath.clampGold(goldNeeded)
    }

    // Gets prestige requirement for display (current layer)
    fun getPrestigeRequirement(): Double {
        return getPrestigeRequirement(gameState.prestigeLayer + 1)
    }

    // Calculates expected crown shards for a state and layer
    fun calculateCrownShardsPreview(state: com.ismail.kingdom.models.GameState, layer: Int): Int {
        val multiplier = when (layer) {
            1 -> 1.0
            2 -> 1.5
            3 -> 2.0
            else -> 1.0
        }
        val goldInBillions = SafeMath.safeDivide(state.totalLifetimeGold, PRESTIGE_BASE_REQUIREMENT)
        val sqrtValue = SafeMath.safeExp(goldInBillions, 0.5)
        val shardsDouble = SafeMath.safeMultiply(sqrtValue, multiplier)
        return shardsDouble.toInt().coerceIn(0, MAX_SHARDS_PER_PRESTIGE)
    }

    fun canAscend(state: com.ismail.kingdom.models.GameState): Boolean {
        return calculateCrownShardsPreview(state, 1) >= 1
    }

    fun canRift(state: com.ismail.kingdom.models.GameState): Boolean {
        return state.prestigeLayer >= 1 && calculateCrownShardsPreview(state, 2) >= 1 && state.currentEra >= 3
    }

    fun canLegend(state: com.ismail.kingdom.models.GameState): Boolean {
        return state.prestigeLayer >= 2 && calculateCrownShardsPreview(state, 3) >= 1 && state.currentEra >= 5
    }

    // Gets prestige layer name
    fun getPrestigeLayerName(layer: Int): String {
        return when (layer) {
            0 -> "None"
            1 -> "Ascension"
            2 -> "Rift"
            3 -> "Legend"
            else -> "Unknown"
        }
    }

    fun getPrestigeLayerName(): String {
        return getPrestigeLayerName(gameState.prestigeLayer)
    }

    // Gets progress to next prestige layer
    fun getProgressToNextLayer(): Double {
        val nextLayerThreshold = when (gameState.prestigeLayer) {
            0 -> LAYER_2_PRESTIGE_COUNT
            1 -> LAYER_3_PRESTIGE_COUNT
            else -> return 1.0 // Max layer reached
        }

        return (gameState.totalPrestigesPerformed.toDouble() / nextLayerThreshold).coerceIn(0.0, 1.0)
    }

    fun isShadowKingdomUnlocked(): Boolean {
        return gameState.shadowKingdomUnlocked
    }

    fun getShadowKingdomBuildings(): List<com.ismail.kingdom.models.Building> {
        return gameState.shadowBuildings.toList()
    }

    fun getPermanentHeroPassives(): List<String> {
        return gameState.permanentHeroPassives.toList()
    }

    // Gets available prestige layer for the current state
    fun getAvailablePrestigeLayer(): com.ismail.kingdom.models.PrestigeLayer {
        return when {
            canLegend(gameState) -> com.ismail.kingdom.models.PrestigeLayer.LEGEND
            canRift(gameState) -> com.ismail.kingdom.models.PrestigeLayer.RIFT
            canAscend(gameState) -> com.ismail.kingdom.models.PrestigeLayer.ASCENSION
            else -> com.ismail.kingdom.models.PrestigeLayer.NONE
        }
    }
}
