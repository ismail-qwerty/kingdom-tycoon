// PATH: core/src/main/java/com/ismail/kingdom/models/MapTile.kt
package com.ismail.kingdom.models

import kotlinx.serialization.Serializable

@Serializable
enum class TileType {
    EMPTY,
    RESOURCE_DEPOSIT,
    ANCIENT_RUINS,
    ENEMY_CAMP,
    MERCHANT,
    QUEST_SITE,
    LEGENDARY_SPOT
}

@Serializable
data class TileReward(
    val goldBonus: Double? = null,
    val buildingUnlock: String? = null,
    val incomeMultiplier: Double? = null,
    val questUnlock: String? = null
)

@Serializable
data class MapTile(
    val id: String,
    val x: Int,
    val y: Int,
    var isRevealed: Boolean = false,
    val type: TileType,
    val revealCost: Double,
    val reward: TileReward? = null,
    val loreText: String,
    val eraId: Int
) {
    val exploreCost: Double get() = revealCost
    val goldReward: Double? get() = reward?.goldBonus
    var isAdjacentToRevealed: Boolean = false
}
