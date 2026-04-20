// PATH: core/src/main/java/com/ismail/kingdom/models/Quest.kt
package com.ismail.kingdom.models

import kotlinx.serialization.Serializable

// Quest types
@Serializable
enum class QuestType {
    TAP_GOLD,
    EARN_GOLD,
    BUY_BUILDINGS,
    UNLOCK_ERA,
    PRESTIGE,
    UNLOCK_HERO,
    EXPLORE_MAP,
    COMPLETE_RAID,
    REACH_INCOME,
    TAP_COUNT
}

// Quest model
@Serializable
data class Quest(
    val id: String,
    val title: String,
    val description: String,
    val type: QuestType,
    val targetValue: Double,
    var currentProgress: Double = 0.0,
    val goldReward: Double = 0.0,
    val crownShardReward: Int = 0,
    val timeLimit: Int? = null,
    var timeRemaining: Int? = null,
    var isComplete: Boolean = false,
    var isActive: Boolean = true
) {
    var isCompleted: Boolean
        get() = isComplete
        set(value) {
            isComplete = value
        }

    var currentValue: Double
        get() = currentProgress
        set(value) {
            currentProgress = value
            if (currentProgress >= targetValue) {
                isComplete = true
            }
        }

    val reward: Double
        get() = goldReward

    fun updateProgress(value: Double) {
        currentValue = value.coerceAtMost(targetValue)
    }
}
