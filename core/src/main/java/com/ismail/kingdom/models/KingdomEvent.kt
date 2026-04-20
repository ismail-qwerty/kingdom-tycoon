package com.ismail.kingdom.models

import kotlinx.serialization.Serializable

@Serializable
enum class KingdomEventType {
    // Basic types
    GOLD_RUSH,
    DOUBLE_INCOME,
    TAP_BONUS,
    BUILDING_DISCOUNT,
    CROWN_SHARD_BONUS,
    SPECIAL_QUEST,

    // System types
    GOBLIN_RAID,
    ROYAL_FESTIVAL,
    MERCHANT_CARAVAN,
    DRAGON_SIGHTING,
    PLAGUE_OF_FROGS,
    HARVEST_MOON
}

@Serializable
data class KingdomEvent(
    val id: String,
    val name: String,
    val description: String,
    val type: KingdomEventType,
    val multiplier: Double,
    val durationSeconds: Int,
    var remainingSeconds: Int = durationSeconds,
    var isActive: Boolean = false
) {
    // Compatibility alias for bonusMultiplier
    val bonusMultiplier: Double get() = multiplier

    // Compatibility alias for duration
    val duration: Int get() = durationSeconds
}
