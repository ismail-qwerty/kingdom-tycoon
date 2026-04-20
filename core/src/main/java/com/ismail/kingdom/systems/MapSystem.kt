// PATH: core/src/main/java/com/ismail/kingdom/systems/MapSystem.kt
package com.ismail.kingdom.systems

import com.ismail.kingdom.models.GameState
import com.ismail.kingdom.models.MapTile
import com.ismail.kingdom.models.TileType
import com.ismail.kingdom.models.TileReward
import com.ismail.kingdom.models.TileRevealResult
import com.ismail.kingdom.utils.LoreStrings
import kotlin.random.Random
import kotlin.math.abs
import kotlin.math.pow

// Manages kingdom exploration map with fog of war
class MapSystem {

    companion object {
        const val MAP_WIDTH = 15
        const val MAP_HEIGHT = 15
        const val CENTER_X = MAP_WIDTH / 2
        const val CENTER_Y = MAP_HEIGHT / 2
    }

    val currentMap: MutableList<MapTile> = mutableListOf()

    // Generates a new map for the specified era
    fun generateMapForEra(eraId: Int, state: GameState): List<MapTile> {
        val tiles = mutableListOf<MapTile>()

        for (x in 0 until MAP_WIDTH) {
            for (y in 0 until MAP_HEIGHT) {
                val isCenter = (x == CENTER_X && y == CENTER_Y)
                val id = "tile_${eraId}_${x}_${y}"
                val tile = generateTile(id, x, y, eraId, isCenter, state)
                tiles.add(tile)
            }
        }

        currentMap.clear()
        currentMap.addAll(tiles)
        updateAdjacentFlags()
        return tiles
    }

    // Generates a single tile with appropriate type and rewards
    private fun generateTile(
        id: String,
        x: Int,
        y: Int,
        eraId: Int,
        isCenter: Boolean,
        state: GameState
    ): MapTile {
        // Center tile is always revealed and empty
        if (isCenter) {
            return MapTile(
                id = id,
                x = x,
                y = y,
                isRevealed = true,
                type = TileType.EMPTY,
                revealCost = 0.0,
                reward = null,
                loreText = "Your kingdom's heart. From here, you shall expand your realm.",
                eraId = eraId
            )
        }

        val type = rollTileType()
        val distance = abs(x - CENTER_X) + abs(y - CENTER_Y)
        val revealCost = calculateRevealCost(distance, eraId, state)
        val reward = generateReward(type, eraId, state)
        val loreText = LoreStrings.getLoreText(type, eraId)

        return MapTile(
            id = id,
            x = x,
            y = y,
            isRevealed = false,
            type = type,
            revealCost = revealCost,
            reward = reward,
            loreText = loreText,
            eraId = eraId
        )
    }

    // Rolls a random tile type based on weighted probabilities
    private fun rollTileType(): TileType {
        val roll = Random.nextDouble()

        return when {
            roll < 0.35 -> TileType.EMPTY // 35%
            roll < 0.55 -> TileType.RESOURCE_DEPOSIT // 20%
            roll < 0.70 -> TileType.ANCIENT_RUINS // 15%
            roll < 0.83 -> TileType.ENEMY_CAMP // 13%
            roll < 0.93 -> TileType.MERCHANT // 10%
            roll < 0.98 -> TileType.QUEST_SITE // 5%
            else -> TileType.LEGENDARY_SPOT // 2%
        }
    }

    // Calculates reveal cost based on distance and era
    private fun calculateRevealCost(distance: Int, eraId: Int, state: GameState): Double {
        val baseMultiplier = when (eraId) {
            1 -> 100.0
            2 -> 10000.0
            3 -> 1000000.0
            4 -> 100000000.0
            5 -> 10000000000.0
            else -> 100.0
        }

        // Cost increases exponentially with distance
        val distanceCost = baseMultiplier * 1.5.pow(distance)

        return distanceCost
    }

    // Generates reward for a tile based on type and era
    private fun generateReward(type: TileType, eraId: Int, state: GameState): TileReward? {
        return when (type) {
            TileType.EMPTY -> null

            TileType.RESOURCE_DEPOSIT -> {
                val goldBonus = when (eraId) {
                    1 -> Random.nextDouble(500.0, 2000.0)
                    2 -> Random.nextDouble(50000.0, 200000.0)
                    3 -> Random.nextDouble(5000000.0, 20000000.0)
                    4 -> Random.nextDouble(500000000.0, 2000000000.0)
                    5 -> Random.nextDouble(50000000000.0, 200000000000.0)
                    else -> 1000.0
                }
                TileReward(goldBonus = goldBonus)
            }

            TileType.ANCIENT_RUINS -> {
                // Small permanent income multiplier
                val multiplier = 1.0 + Random.nextDouble(0.01, 0.05) // 1-5% bonus
                TileReward(incomeMultiplier = multiplier)
            }

            TileType.ENEMY_CAMP -> {
                // Gold bonus for defeating enemies
                val goldBonus = when (eraId) {
                    1 -> Random.nextDouble(1000.0, 5000.0)
                    2 -> Random.nextDouble(100000.0, 500000.0)
                    3 -> Random.nextDouble(10000000.0, 50000000.0)
                    4 -> Random.nextDouble(1000000000.0, 5000000000.0)
                    5 -> Random.nextDouble(100000000000.0, 500000000000.0)
                    else -> 2000.0
                }
                TileReward(goldBonus = goldBonus)
            }

            TileType.MERCHANT -> {
                // Unlock a random building from current era
                val eraBuildings = state.buildings.filter { it.era == eraId && !it.isUnlocked }
                val buildingToUnlock = eraBuildings.randomOrNull()?.id
                TileReward(buildingUnlock = buildingToUnlock)
            }

            TileType.QUEST_SITE -> {
                // Unlock a special quest
                TileReward(questUnlock = "map_quest_era${eraId}_${Random.nextInt(1000)}")
            }

            TileType.LEGENDARY_SPOT -> {
                // Large income multiplier + gold bonus
                val multiplier = 1.0 + Random.nextDouble(0.05, 0.15) // 5-15% bonus
                val goldBonus = when (eraId) {
                    1 -> Random.nextDouble(5000.0, 10000.0)
                    2 -> Random.nextDouble(500000.0, 1000000.0)
                    3 -> Random.nextDouble(50000000.0, 100000000.0)
                    4 -> Random.nextDouble(5000000000.0, 10000000000.0)
                    5 -> Random.nextDouble(500000000000.0, 1000000000000.0)
                    else -> 10000.0
                }
                TileReward(
                    goldBonus = goldBonus,
                    incomeMultiplier = multiplier
                )
            }
        }
    }

    // Reveals a tile if player can afford it and it's adjacent to revealed tiles
    fun revealTile(tileId: String, state: GameState): TileRevealResult {
        val tile = currentMap.find { it.id == tileId }
            ?: return TileRevealResult(false, null, 0.0, "Tile not found")

        if (tile.isRevealed) {
            return TileRevealResult(false, tile, 0.0, "Tile already revealed")
        }

        // Check if tile is adjacent to any revealed tile
        if (!tile.isAdjacentToRevealed) {
            return TileRevealResult(false, tile, 0.0, "Tile not adjacent to explored area")
        }

        // Check if player can afford it
        if (!state.spendGold(tile.revealCost)) {
            return TileRevealResult(false, tile, 0.0, "Not enough gold (Need ${tile.revealCost})")
        }

        tile.isRevealed = true
        updateAdjacentFlags()
        val rewardMsg = applyReward(tile, state)

        return TileRevealResult(true, tile, tile.revealCost, rewardMsg)
    }

    // Checks if a tile is adjacent to any revealed tile
    private fun isAdjacentToRevealed(tile: MapTile): Boolean {
        for (other in currentMap) {
            if (other.isRevealed) {
                val dx = abs(tile.x - other.x)
                val dy = abs(tile.y - other.y)
                if ((dx == 1 && dy == 0) || (dx == 0 && dy == 1)) {
                    return true
                }
            }
        }
        return false
    }

    // Updates isAdjacentToRevealed property for all tiles
    private fun updateAdjacentFlags() {
        for (tile in currentMap) {
            tile.isAdjacentToRevealed = isAdjacentToRevealed(tile)
        }
    }

    // Applies reward from a tile to the game state
    private fun applyReward(tile: MapTile, state: GameState): String {
        val reward = tile.reward ?: return "Nothing found here."
        val sb = StringBuilder()

        reward.goldBonus?.let {
            state.addGold(it)
            sb.append("Found ${it.toInt()} gold! ")
        }

        reward.incomeMultiplier?.let {
            state.incomeMultiplier *= it
            val percent = ((it - 1.0) * 100).toInt()
            sb.append("Boosted income by $percent%! ")
        }

        reward.buildingUnlock?.let { buildingId ->
            state.buildings.find { it.id == buildingId }?.let {
                it.isUnlocked = true
                sb.append("Unlocked ${it.name}! ")
            }
        }

        reward.questUnlock?.let {
            sb.append("Discovered a new quest! ")
        }

        return sb.toString().trim()
    }

    fun getRevealableTiles(): List<MapTile> {
        return currentMap.filter { !it.isRevealed && it.isAdjacentToRevealed }
    }

    fun getRevealedTiles(): List<MapTile> {
        return currentMap.filter { it.isRevealed }
    }

    fun getTileAt(x: Int, y: Int): MapTile? {
        return currentMap.find { it.x == x && it.y == y }
    }

    fun getTileById(id: String): MapTile? {
        return currentMap.find { it.id == id }
    }

    fun getAllTiles(): List<MapTile> = currentMap

    fun getExplorationProgress(): Float {
        if (currentMap.isEmpty()) return 0f
        val revealed = currentMap.count { it.isRevealed }
        return revealed.toFloat() / currentMap.size.toFloat()
    }

    fun getTotalExplorationCost(): Double {
        return currentMap.filter { !it.isRevealed && it.isAdjacentToRevealed }.sumOf { it.revealCost }
    }

    fun isMapFullyExplored(): Boolean {
        return currentMap.all { it.isRevealed }
    }

    fun getRevealedTileTypeCounts(): Map<TileType, Int> {
        return currentMap
            .filter { it.isRevealed }
            .groupBy { it.type }
            .mapValues { it.value.size }
    }

    fun getCheapestRevealableTile(): MapTile? {
        return getRevealableTiles().minByOrNull { it.revealCost }
    }

    fun getMostExpensiveRevealableTile(): MapTile? {
        return getRevealableTiles().maxByOrNull { it.revealCost }
    }

    fun canAffordAnyTile(state: GameState): Boolean {
        val cheapest = getCheapestRevealableTile() ?: return false
        return state.currentGold >= cheapest.revealCost
    }

    fun getTilesByType(type: TileType): List<MapTile> {
        return currentMap.filter { it.type == type }
    }

    fun getUnrevealedTilesCount(): Int {
        return currentMap.count { !it.isRevealed }
    }

    private fun formatGold(amount: Double): String {
        return when {
            amount >= 1e12 -> String.format("%.2fT", amount / 1e12)
            amount >= 1e9 -> String.format("%.2fB", amount / 1e9)
            amount >= 1e6 -> String.format("%.2fM", amount / 1e6)
            amount >= 1e3 -> String.format("%.2fK", amount / 1e3)
            else -> amount.toInt().toString()
        }
    }

    fun resetMap() {
        currentMap.clear()
    }

    fun loadMap(tiles: List<MapTile>) {
        currentMap.clear()
        currentMap.addAll(tiles)
        updateAdjacentFlags()
    }

    fun getMapDimensions(): Pair<Int, Int> {
        return Pair(MAP_WIDTH, MAP_HEIGHT)
    }

    fun getCenterPosition(): Pair<Int, Int> {
        return Pair(CENTER_X, CENTER_Y)
    }
}
