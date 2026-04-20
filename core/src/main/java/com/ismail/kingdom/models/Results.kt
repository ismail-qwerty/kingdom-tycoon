// PATH: core/src/main/java/com/ismail/kingdom/models/Results.kt
package com.ismail.kingdom.models

import kotlinx.serialization.Serializable

// Prestige tier enum — single source of truth
@Serializable
enum class PrestigeLayer { NONE, ASCENSION, RIFT, LEGEND }

// Result types for game operations
data class BuyResult(
    val success: Boolean,
    val message: String = "",
    val goldSpent: Double = 0.0
)

data class PrestigeResult(
    val success: Boolean,
    val crownShardsGained: Int = 0,
    val message: String = ""
)

data class TileRevealResult(
    val success: Boolean,
    val tile: com.ismail.kingdom.models.MapTile? = null,
    val goldSpent: Double = 0.0,
    val message: String = ""
)

data class PrestigeProgress(
    val currentGold: Double,
    val requiredGold: Double,
    val progress: Double
)
