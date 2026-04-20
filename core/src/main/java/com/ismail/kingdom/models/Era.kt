// PATH: core/src/main/java/com/ismail/kingdom/models/Era.kt
package com.ismail.kingdom.models

import kotlinx.serialization.Serializable

// Represents a progression era with buildings and resources
@Serializable
data class Era(
    val id: Int,
    val name: String,
    val displayName: String,
    val primaryResource: ResourceType,
    val secondaryResource: ResourceType? = null,
    val buildings: List<String> = emptyList(),
    var isUnlocked: Boolean = false,
    val backgroundAsset: String
)
