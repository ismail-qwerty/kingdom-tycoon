// PATH: core/src/main/java/com/ismail/kingdom/utils/SafeMath.kt
package com.ismail.kingdom.utils

import kotlin.math.pow

// Safe mathematical operations for game calculations to prevent overflow and invalid values
object SafeMath {
    
    const val MAX_GAME_VALUE = 1e308 // Maximum safe game value
    const val MIN_GAME_VALUE = 0.0 // Minimum game value (no negatives)
    
    // Safely adds two doubles, handling infinity and overflow
    fun safeAdd(a: Double, b: Double): Double {
        if (!isValidGameNumber(a) || !isValidGameNumber(b)) {
            return 0.0
        }
        
        // Check for potential overflow
        if (a > MAX_GAME_VALUE - b) {
            return MAX_GAME_VALUE
        }
        
        val result = a + b
        
        return when {
            result.isInfinite() -> MAX_GAME_VALUE
            result.isNaN() -> 0.0
            result > MAX_GAME_VALUE -> MAX_GAME_VALUE
            result < MIN_GAME_VALUE -> MIN_GAME_VALUE
            else -> result
        }
    }
    
    // Safely multiplies two doubles, capping at MAX_GAME_VALUE
    fun safeMultiply(a: Double, b: Double): Double {
        if (!isValidGameNumber(a) || !isValidGameNumber(b)) {
            return 0.0
        }
        
        // Check for potential overflow before multiplication
        if (a != 0.0 && b > MAX_GAME_VALUE / a) {
            return MAX_GAME_VALUE
        }
        
        val result = a * b
        
        return when {
            result.isInfinite() -> MAX_GAME_VALUE
            result.isNaN() -> 0.0
            result > MAX_GAME_VALUE -> MAX_GAME_VALUE
            result < MIN_GAME_VALUE -> MIN_GAME_VALUE
            else -> result
        }
    }
    
    // Safely calculates exponentiation, preventing overflow in cost formulas
    fun safeExp(base: Double, exponent: Double): Double {
        if (!isValidGameNumber(base) || !isValidGameNumber(exponent)) {
            return 1.0
        }
        
        // Prevent negative base with non-integer exponent
        if (base < 0 && exponent % 1.0 != 0.0) {
            return 1.0
        }
        
        // Cap exponent to prevent overflow
        val cappedExponent = exponent.coerceIn(-1000.0, 1000.0)
        
        return try {
            val result = base.pow(cappedExponent)
            
            when {
                result.isInfinite() -> MAX_GAME_VALUE
                result.isNaN() -> 1.0
                result > MAX_GAME_VALUE -> MAX_GAME_VALUE
                result < MIN_GAME_VALUE -> MIN_GAME_VALUE
                else -> result
            }
        } catch (e: Exception) {
            1.0
        }
    }
    
    // Clamps gold amount to valid range
    fun clampGold(amount: Double): Double {
        return when {
            !isValidGameNumber(amount) -> 0.0
            amount < MIN_GAME_VALUE -> MIN_GAME_VALUE
            amount > MAX_GAME_VALUE -> MAX_GAME_VALUE
            else -> amount
        }
    }
    
    // Checks if a value is a valid game number (not NaN, not Infinity, not negative)
    fun isValidGameNumber(value: Double): Boolean {
        return !value.isNaN() && !value.isInfinite() && value >= 0.0
    }
    
    // Safely divides two doubles
    fun safeDivide(a: Double, b: Double): Double {
        if (!isValidGameNumber(a) || !isValidGameNumber(b) || b == 0.0) {
            return 0.0
        }
        
        val result = a / b
        
        return when {
            result.isInfinite() -> MAX_GAME_VALUE
            result.isNaN() -> 0.0
            result > MAX_GAME_VALUE -> MAX_GAME_VALUE
            result < MIN_GAME_VALUE -> MIN_GAME_VALUE
            else -> result
        }
    }
    
    // Safely subtracts two doubles
    fun safeSubtract(a: Double, b: Double): Double {
        if (!isValidGameNumber(a) || !isValidGameNumber(b)) {
            return 0.0
        }
        
        val result = a - b
        
        return when {
            result < MIN_GAME_VALUE -> MIN_GAME_VALUE
            result > MAX_GAME_VALUE -> MAX_GAME_VALUE
            result.isNaN() -> 0.0
            else -> result
        }
    }
    
    // Calculates percentage safely
    fun safePercentage(value: Double, percentage: Double): Double {
        if (!isValidGameNumber(value) || !isValidGameNumber(percentage)) {
            return 0.0
        }
        
        return safeMultiply(value, percentage / 100.0)
    }
    
    // Safely calculates building cost with exponential growth
    fun safeBuildingCost(baseCost: Double, count: Int, growthRate: Double = 1.15): Double {
        if (!isValidGameNumber(baseCost) || count < 0) {
            return baseCost
        }
        
        // Cap count to prevent overflow
        val cappedCount = count.coerceIn(0, 10000)
        
        val multiplier = safeExp(growthRate, cappedCount.toDouble())
        return safeMultiply(baseCost, multiplier)
    }
    
    // Formats large numbers with suffixes
    fun formatNumber(value: Double): String {
        if (!isValidGameNumber(value)) {
            return "0"
        }
        
        return when {
            value >= 1e308 -> "MAX"
            value >= 1e15 -> "%.2fQa".format(value / 1e15)
            value >= 1e12 -> "%.2fT".format(value / 1e12)
            value >= 1e9 -> "%.2fB".format(value / 1e9)
            value >= 1e6 -> "%.2fM".format(value / 1e6)
            value >= 1e3 -> "%.2fK".format(value / 1e3)
            else -> "%.0f".format(value)
        }
    }
}
