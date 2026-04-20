// PATH: core/src/main/java/com/ismail/kingdom/models/Resource.kt
package com.ismail.kingdom.models

import kotlinx.serialization.Serializable

// Enum defining all resource types in the game
@Serializable
enum class ResourceType {
    GOLD,
    STONE,
    IRON,
    MANA,
    GLORY
}

// Represents a game resource with amount and unlock requirements
@Serializable
data class Resource(
    val id: String,
    val name: String,
    var amount: Double = 0.0,
    val eraRequired: Int
)
