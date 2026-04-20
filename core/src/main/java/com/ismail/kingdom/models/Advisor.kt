// PATH: core/src/main/java/com/ismail/kingdom/models/Advisor.kt
package com.ismail.kingdom.models

import kotlinx.serialization.Serializable

// Advisor model for building automation
@Serializable
data class Advisor(
    val id: String,
    val name: String,
    val buildingId: String,
    val cost: Double,
    val description: String,
    var isHired: Boolean = false,
    var isUnlocked: Boolean = true,
    var level: Int = 0
) {
    val unlockCost: Double get() = cost // Alias for compatibility
    val values: List<String> get() = listOf(id)
    val keys: List<String> get() = listOf(id)
}
