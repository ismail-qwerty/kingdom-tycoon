// PATH: core/src/main/java/com/ismail/kingdom/utils/Formatters.kt
package com.ismail.kingdom.utils

import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow

// Utility object for formatting numbers, time, and percentages
object Formatters {
    
    // Formats gold amount with suffixes
    fun formatGold(amount: Double): String {
        if (amount < 0) return "-${formatGold(abs(amount))}"
        if (amount < 1000) return amount.toInt().toString()
        
        val suffixes = listOf(
            "", "K", "M", "B", "T",           // Thousand, Million, Billion, Trillion
            "Qa", "Qi", "Sx", "Sp", "Oc", "No", // Quadrillion, Quintillion, Sextillion, Septillion, Octillion, Nonillion
            "Dc"                               // Decillion
        )
        
        // Calculate the order of magnitude
        val exp = log10(amount).toInt()
        val index = exp / 3
        
        // Use scientific notation for very large numbers (beyond Decillion)
        if (index >= suffixes.size) {
            return "%.2e".format(amount)
        }
        
        val divisor = 10.0.pow(index * 3)
        val value = amount / divisor
        
        val suffix = suffixes[index]
        
        // Format with appropriate decimal places
        return when {
            value < 10.0 -> "%.2f%s".format(value, suffix)
            value < 100.0 -> "%.1f%s".format(value, suffix)
            else -> "%.0f%s".format(value, suffix)
        }
    }
    
    // Formats income per second with /s suffix
    fun formatIPS(ips: Double): String {
        return "${formatGold(ips)}/s"
    }
    
    // Formats time in seconds to human-readable format
    fun formatTime(seconds: Int): String {
        if (seconds < 0) return "0s"
        
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        
        return when {
            hours > 0 -> {
                if (minutes > 0) {
                    "${hours}h ${minutes}m"
                } else {
                    "${hours}h"
                }
            }
            minutes > 0 -> {
                if (secs > 0) {
                    "${minutes}m ${secs}s"
                } else {
                    "${minutes}m"
                }
            }
            else -> "${secs}s"
        }
    }
    
    // Formats percentage value
    fun formatPercent(value: Double): String {
        val percent = value * 100
        return when {
            percent < 10.0 -> "%.2f%%".format(percent)
            percent < 100.0 -> "%.1f%%".format(percent)
            else -> "%.0f%%".format(percent)
        }
    }
    
    // Formats crown shards with crown symbol
    fun formatCrownShards(shards: Int): String {
        return "${formatNumber(shards)} ♛"
    }
    
    // Formats integer with thousand separators
    private fun formatNumber(number: Int): String {
        return number.toString().reversed().chunked(3).joinToString(",").reversed()
    }
    
    // Formats multiplier (e.g., 2.5x)
    fun formatMultiplier(multiplier: Double): String {
        return when {
            multiplier < 10.0 -> "%.2fx".format(multiplier)
            multiplier < 100.0 -> "%.1fx".format(multiplier)
            else -> "%.0fx".format(multiplier)
        }
    }
    
    // Formats building cost with detailed precision
    fun formatCost(cost: Double): String {
        return formatGold(cost)
    }
    
    // Formats large numbers with compact notation
    fun formatCompact(value: Double): String {
        return when {
            value < 1000 -> value.toInt().toString()
            value < 1_000_000 -> "%.0fK".format(value / 1000)
            value < 1_000_000_000 -> "%.0fM".format(value / 1_000_000)
            value < 1_000_000_000_000 -> "%.0fB".format(value / 1_000_000_000)
            else -> "%.0fT".format(value / 1_000_000_000_000)
        }
    }
    
    // Formats duration in milliseconds to readable format
    fun formatDuration(millis: Long): String {
        val seconds = (millis / 1000).toInt()
        return formatTime(seconds)
    }
    
    // Formats progress as percentage (0.0 to 1.0 -> "0%" to "100%")
    fun formatProgress(progress: Float): String {
        val percent = (progress * 100).toInt().coerceIn(0, 100)
        return "$percent%"
    }
}
