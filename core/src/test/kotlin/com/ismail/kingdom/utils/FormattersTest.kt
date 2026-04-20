// PATH: core/src/test/kotlin/com/ismail/kingdom/utils/FormattersTest.kt
package com.ismail.kingdom.utils

import org.junit.Assert.assertEquals
import org.junit.Test

// Unit tests for Formatters utility
class FormattersTest {
    
    @Test
    fun testFormatGold_Zero() {
        val result = Formatters.formatGold(0.0)
        assertEquals("0", result)
    }
    
    @Test
    fun testFormatGold_One() {
        val result = Formatters.formatGold(1.0)
        assertEquals("1", result)
    }
    
    @Test
    fun testFormatGold_999() {
        val result = Formatters.formatGold(999.0)
        assertEquals("999", result)
    }
    
    @Test
    fun testFormatGold_1000() {
        val result = Formatters.formatGold(1000.0)
        assertEquals("1.00K", result)
    }
    
    @Test
    fun testFormatGold_1500() {
        val result = Formatters.formatGold(1500.0)
        assertEquals("1.50K", result)
    }
    
    @Test
    fun testFormatGold_999999() {
        val result = Formatters.formatGold(999999.0)
        assertEquals("1000K", result) // 999.999K rounds to 1000K
    }
    
    @Test
    fun testFormatGold_1Million() {
        val result = Formatters.formatGold(1_000_000.0)
        assertEquals("1.00M", result)
    }
    
    @Test
    fun testFormatGold_1Billion() {
        val result = Formatters.formatGold(1e9)
        assertEquals("1.00B", result)
    }
    
    @Test
    fun testFormatGold_1Trillion() {
        val result = Formatters.formatGold(1e12)
        assertEquals("1.00T", result)
    }
    
    @Test
    fun testFormatGold_1Quadrillion() {
        val result = Formatters.formatGold(1e15)
        assertEquals("1.00Qa", result)
    }
    
    @Test
    fun testFormatGold_1Quintillion() {
        val result = Formatters.formatGold(1e18)
        assertEquals("1.00Qi", result)
    }
    
    @Test
    fun testFormatGold_1e33_ScientificNotation() {
        val result = Formatters.formatGold(1e33)
        // Should use scientific notation for very large numbers
        assertEquals("1.00e+33", result)
    }
    
    @Test
    fun testFormatGold_SmallDecimals() {
        val result = Formatters.formatGold(1234.56)
        assertEquals("1.23K", result)
    }
    
    @Test
    fun testFormatGold_LargeDecimals() {
        val result = Formatters.formatGold(123456.78)
        assertEquals("123K", result)
    }
    
    @Test
    fun testFormatGold_Negative() {
        val result = Formatters.formatGold(-1500.0)
        assertEquals("-1.50K", result)
    }
    
    @Test
    fun testFormatGold_Sextillion() {
        val result = Formatters.formatGold(1e21)
        assertEquals("1.00Sx", result)
    }
    
    @Test
    fun testFormatGold_Septillion() {
        val result = Formatters.formatGold(1e24)
        assertEquals("1.00Sp", result)
    }
    
    @Test
    fun testFormatGold_Octillion() {
        val result = Formatters.formatGold(1e27)
        assertEquals("1.00Oc", result)
    }
    
    @Test
    fun testFormatGold_Nonillion() {
        val result = Formatters.formatGold(1e30)
        assertEquals("1.00No", result)
    }
    
    @Test
    fun testFormatGold_Decillion() {
        val result = Formatters.formatGold(1e33)
        // Decillion is the last named suffix, beyond that uses scientific notation
        assertEquals("1.00e+33", result)
    }
    
    @Test
    fun testFormatIPS() {
        val result = Formatters.formatIPS(1500.0)
        assertEquals("1.50K/s", result)
    }
    
    @Test
    fun testFormatTime_Seconds() {
        val result = Formatters.formatTime(30)
        assertEquals("30s", result)
    }
    
    @Test
    fun testFormatTime_Minutes() {
        val result = Formatters.formatTime(120)
        assertEquals("2m", result)
    }
    
    @Test
    fun testFormatTime_MinutesAndSeconds() {
        val result = Formatters.formatTime(150)
        assertEquals("2m 30s", result)
    }
    
    @Test
    fun testFormatTime_Hours() {
        val result = Formatters.formatTime(3600)
        assertEquals("1h", result)
    }
    
    @Test
    fun testFormatTime_HoursAndMinutes() {
        val result = Formatters.formatTime(9000)
        assertEquals("2h 30m", result)
    }
    
    @Test
    fun testFormatTime_Zero() {
        val result = Formatters.formatTime(0)
        assertEquals("0s", result)
    }
    
    @Test
    fun testFormatTime_Negative() {
        val result = Formatters.formatTime(-100)
        assertEquals("0s", result)
    }
    
    @Test
    fun testFormatPercent_Small() {
        val result = Formatters.formatPercent(0.0523)
        assertEquals("5.23%", result)
    }
    
    @Test
    fun testFormatPercent_Medium() {
        val result = Formatters.formatPercent(0.456)
        assertEquals("45.6%", result)
    }
    
    @Test
    fun testFormatPercent_Large() {
        val result = Formatters.formatPercent(1.25)
        assertEquals("125%", result)
    }
    
    @Test
    fun testFormatCrownShards() {
        val result = Formatters.formatCrownShards(1234)
        assertEquals("1,234 ♛", result)
    }
    
    @Test
    fun testFormatCrownShards_Large() {
        val result = Formatters.formatCrownShards(1234567)
        assertEquals("1,234,567 ♛", result)
    }
    
    @Test
    fun testFormatMultiplier_Small() {
        val result = Formatters.formatMultiplier(2.5)
        assertEquals("2.50x", result)
    }
    
    @Test
    fun testFormatMultiplier_Medium() {
        val result = Formatters.formatMultiplier(15.7)
        assertEquals("15.7x", result)
    }
    
    @Test
    fun testFormatMultiplier_Large() {
        val result = Formatters.formatMultiplier(125.0)
        assertEquals("125x", result)
    }
    
    @Test
    fun testFormatCompact() {
        assertEquals("500", Formatters.formatCompact(500.0))
        assertEquals("2K", Formatters.formatCompact(1500.0))
        assertEquals("3M", Formatters.formatCompact(2_500_000.0))
        assertEquals("5B", Formatters.formatCompact(4_500_000_000.0))
        assertEquals("7T", Formatters.formatCompact(6_500_000_000_000.0))
    }
    
    @Test
    fun testFormatProgress() {
        assertEquals("0%", Formatters.formatProgress(0.0f))
        assertEquals("25%", Formatters.formatProgress(0.25f))
        assertEquals("50%", Formatters.formatProgress(0.5f))
        assertEquals("75%", Formatters.formatProgress(0.75f))
        assertEquals("100%", Formatters.formatProgress(1.0f))
    }
}
