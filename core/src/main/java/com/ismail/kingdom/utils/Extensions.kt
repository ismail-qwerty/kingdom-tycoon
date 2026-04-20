// PATH: core/src/main/java/com/ismail/kingdom/utils/Extensions.kt
package com.ismail.kingdom.utils

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.abs
import kotlin.math.round

// Extension function to format Double as gold
fun Double.toFormattedGold(): String {
    return Formatters.formatGold(this)
}

// Extension function to format Double as income per second
fun Double.toFormattedIPS(): String {
    return Formatters.formatIPS(this)
}

// Extension function to format Int as time
fun Int.toFormattedTime(): String {
    return Formatters.formatTime(this)
}

// Extension function to format Double as percentage
fun Double.toFormattedPercent(): String {
    return Formatters.formatPercent(this)
}

// Extension function to format Int as crown shards
fun Int.toFormattedCrownShards(): String {
    return Formatters.formatCrownShards(this)
}

// Extension function to format Double as multiplier
fun Double.toFormattedMultiplier(): String {
    return Formatters.formatMultiplier(this)
}

// Linear interpolation for smooth animations
fun Float.lerp(target: Float, speed: Float): Float {
    return this + (target - this) * speed
}

// Linear interpolation for Double
fun Double.lerp(target: Double, speed: Double): Double {
    return this + (target - this) * speed
}

// Extension function to clamp Double
fun Double.clamp(min: Double, max: Double): Double {
    return max(min, min(max, this))
}

// Extension function to clamp Float
fun Float.clamp(min: Float, max: Float): Float {
    return max(min, min(max, this))
}

// Extension function to clamp Int
fun Int.clamp(min: Int, max: Int): Int {
    return max(min, min(max, this))
}

// Checks if a Double is approximately equal to another (within epsilon)
fun Double.approxEquals(other: Double, epsilon: Double = 0.0001): Boolean {
    return kotlin.math.abs(this - other) < epsilon
}

// Checks if a Float is approximately equal to another (within epsilon)
fun Float.approxEquals(other: Float, epsilon: Float = 0.0001f): Boolean {
    return kotlin.math.abs(this - other) < epsilon
}

// Extension function for range mapping
fun Double.mapRange(fromMin: Double, fromMax: Double, toMin: Double, toMax: Double): Double {
    val normalized = (this - fromMin) / (fromMax - fromMin)
    return toMin + normalized * (toMax - toMin)
}

// Converts seconds to milliseconds
fun Int.secondsToMillis(): Long {
    return this * 1000L
}

// Converts milliseconds to seconds
fun Long.millisToSeconds(): Int {
    return (this / 1000).toInt()
}

// Checks if a number is a power of 10
fun Int.isPowerOfTen(): Boolean {
    if (this <= 0) return false
    var n = this
    while (n % 10 == 0) {
        n /= 10
    }
    return n == 1
}

// Rounds to specified decimal places
fun Double.roundTo(decimals: Int): Double {
    val multiplier = 10.0.pow(decimals)
    return round(this * multiplier) / multiplier
}

// Rounds to specified decimal places
fun Float.roundTo(decimals: Int): Float {
    val multiplier = 10.0f.pow(decimals)
    return round(this * multiplier) / multiplier
}

// Converts Boolean to Int (1 or 0)
fun Boolean.toInt(): Int {
    return if (this) 1 else 0
}

// Converts Boolean to Float (1.0 or 0.0)
fun Boolean.toFloat(): Float {
    return if (this) 1.0f else 0.0f
}

// Converts Boolean to Double (1.0 or 0.0)
fun Boolean.toDouble(): Double {
    return if (this) 1.0 else 0.0
}

// Safe division that returns 0 if divisor is 0
fun Double.safeDivide(divisor: Double): Double {
    return if (divisor == 0.0) 0.0 else this / divisor
}

// Safe division that returns 0 if divisor is 0
fun Float.safeDivide(divisor: Float): Float {
    return if (divisor == 0.0f) 0.0f else this / divisor
}

// Safe division that returns 0 if divisor is 0
fun Int.safeDivide(divisor: Int): Int {
    return if (divisor == 0) 0 else this / divisor
}

// Percentage of a value
fun Double.percentOf(total: Double): Double {
    return (this / total) * 100.0
}

// Percentage of a value
fun Int.percentOf(total: Int): Double {
    return (this.toDouble() / total.toDouble()) * 100.0
}

// Calculates percentage increase
fun Double.percentIncrease(newValue: Double): Double {
    if (this == 0.0) return 0.0
    return ((newValue - this) / this) * 100.0
}

// Checks if value is within range (inclusive)
fun Double.inRange(min: Double, max: Double): Boolean {
    return this >= min && this <= max
}

// Checks if value is within range (inclusive)
fun Int.inRange(min: Int, max: Int): Boolean {
    return this >= min && this <= max
}

// Converts to abbreviated string (1000 -> "1K")
fun Int.toAbbreviated(): String {
    return Formatters.formatCompact(this.toDouble())
}

// Converts to abbreviated string (1000.0 -> "1K")
fun Double.toAbbreviated(): String {
    return Formatters.formatCompact(this)
}
