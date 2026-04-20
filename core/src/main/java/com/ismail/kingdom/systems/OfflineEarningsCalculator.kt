// PATH: core/src/main/java/com/ismail/kingdom/systems/OfflineEarningsCalculator.kt
package com.ismail.kingdom.systems

import com.ismail.kingdom.models.GameState
import kotlin.math.min

// Result of offline earnings calculation
data class OfflineEarningsResult(
    val goldEarned: Double,
    val secondsCalculated: Long,
    val wasCapped: Boolean,
    val capExtendedByAd: Boolean,
    val formattedTime: String,
    val formattedGold: String
)

// Calculates offline earnings when game is loaded
class OfflineEarningsCalculator(
    private val incomeSystem: IncomeSystem,
    private val eventSystem: EventSystem
) {
    
    private val DEFAULT_OFFLINE_CAP_HOURS = 8
    private val HARVEST_MOON_CAP_HOURS = 16 // 2x with Harvest Moon
    
    // Calculates offline earnings based on time since last save
    fun calculate(state: GameState, currentTimeMillis: Long): OfflineEarningsResult {
        val lastSaveTime = state.lastSaveTime
        
        // Calculate seconds offline
        val secondsOffline = ((currentTimeMillis - lastSaveTime) / 1000).coerceAtLeast(0)
        
        // Determine offline cap
        val baseCapHours = DEFAULT_OFFLINE_CAP_HOURS
        val eventCapMultiplier = eventSystem.getEventOfflineCapMultiplier()
        val effectiveCapHours = (baseCapHours * eventCapMultiplier).toInt()
        val capExtendedByEvent = eventCapMultiplier > 1.0
        
        // Cap seconds to maximum
        val capSeconds = (effectiveCapHours * 3600).toLong()
        val cappedSeconds = min(secondsOffline, capSeconds)
        val wasCapped = secondsOffline > capSeconds
        
        // Calculate gold earned
        val goldEarned = if (cappedSeconds >= 60) {
            incomeSystem.calculateOfflineEarnings(cappedSeconds)
        } else {
            0.0 // Less than 1 minute, no offline earnings
        }
        
        // Format time
        val formattedTime = formatOfflineTime(cappedSeconds)
        
        // Format gold
        val formattedGold = com.ismail.kingdom.utils.Formatters.formatGold(goldEarned)
        
        return OfflineEarningsResult(
            goldEarned = goldEarned,
            secondsCalculated = cappedSeconds,
            wasCapped = wasCapped,
            capExtendedByAd = false, // Will be set by AdManager if player watches ad
            formattedTime = formattedTime,
            formattedGold = formattedGold
        )
    }
    
    // Formats offline time in human-readable format
    private fun formatOfflineTime(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        
        return when {
            hours >= 24 -> {
                val days = hours / 24
                val remainingHours = hours % 24
                if (remainingHours > 0) {
                    "${days}d ${remainingHours}h"
                } else {
                    "${days}d"
                }
            }
            hours > 0 -> {
                if (minutes > 0) {
                    "${hours}h ${minutes}m"
                } else {
                    "${hours}h"
                }
            }
            minutes > 0 -> "${minutes}m"
            else -> "${seconds}s"
        }
    }
    
    // Gets the effective offline cap in hours
    fun getEffectiveOfflineCap(): Int {
        val baseCapHours = DEFAULT_OFFLINE_CAP_HOURS
        val eventCapMultiplier = eventSystem.getEventOfflineCapMultiplier()
        return (baseCapHours * eventCapMultiplier).toInt()
    }
    
    // Checks if Harvest Moon event is extending the cap
    fun isCapExtendedByEvent(): Boolean {
        return eventSystem.getEventOfflineCapMultiplier() > 1.0
    }
}
