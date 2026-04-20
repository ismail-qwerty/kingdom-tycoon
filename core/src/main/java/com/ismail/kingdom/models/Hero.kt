// PATH: core/src/main/java/com/ismail/kingdom/models/Hero.kt
package com.ismail.kingdom.models

import kotlinx.serialization.Serializable

@Serializable
enum class HeroPassiveType {
    NONE,
    INCOME_MULTIPLIER,
    TAP_MULTIPLIER,
    QUEST_REWARDS,
    OFFLINE_MULTIPLIER,
    ADVISOR_SPEED,
    COST_REDUCTION,
    MILESTONE_BONUS,
    RESOURCE_MULTIPLIER,
    CROWN_SHARDS_BONUS,
    EVENT_DURATION,
    MAP_SPEED,
    LEGEND_BUFF
}

@Serializable
data class Hero(
    val id: String,
    val name: String,
    val description: String,
    val passiveDescription: String = description,
    val passiveBonus: Double = 1.0,
    val unlockCost: Int = 0, // Crown shards
    var isUnlocked: Boolean = false,
    val title: String = "",
    val passiveType: HeroPassiveType = HeroPassiveType.NONE,
    val passiveValue: Double = passiveBonus,
    val portraitAsset: String = "",
    var level: Int = 0
)
