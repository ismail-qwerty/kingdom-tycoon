// PATH: core/src/test/kotlin/com/ismail/kingdom/systems/IncomeSystemTest.kt
package com.ismail.kingdom.systems

import com.ismail.kingdom.models.*

// Simple assert function
fun assert(condition: Boolean, message: String = "Assertion failed") {
    if (!condition) {
        throw AssertionError(message)
    }
}

fun assertApprox(actual: Double, expected: Double, tolerance: Double = 0.01, message: String = "") {
    val diff = kotlin.math.abs(actual - expected)
    if (diff > tolerance) {
        throw AssertionError("$message Expected $expected but got $actual (diff: $diff)")
    }
}

// Test 1: Base income calculation
fun testBaseIncome() {
    val incomeSystem = IncomeSystem()
    val state = GameState()
    
    // Add 1 Wheat Farm with 1.0 income/s
    val building = Building("wheat_farm", "Wheat Farm", 1, 10.0, 1.15, 1.0, 1, true, false)
    state.buildings.add(building)
    
    // Update for 1 second
    val result = incomeSystem.update(state, 1.0f)
    
    assertApprox(result.goldEarned, 1.0, 0.1, "Test 1: Base income should be ~1.0 gold after 1 second")
    println("✓ Test 1 passed: Base income")
}

// Test 2: Multiplier stacking
fun testMultiplierStacking() {
    val incomeSystem = IncomeSystem()
    val state = GameState()
    
    // Add building
    val building = Building("wheat_farm", "Wheat Farm", 1, 10.0, 1.15, 1.0, 1, true, false)
    state.buildings.add(building)
    
    // Add crown shards (10 shards = 1 + 10 × 0.02 = 1.2× multiplier)
    state.crownShards = 10
    
    // Add income multiplier
    state.incomeMultiplier = 2.0
    
    // Expected: 1.0 (base) × 1.2 (crown) × 2.0 (multiplier) = 2.4 gold/s
    val ips = incomeSystem.calculateTotalIPS(state)
    assertApprox(ips, 2.4, 0.01, "Test 2: IPS with multipliers")
    
    val result = incomeSystem.update(state, 1.0f)
    assertApprox(result.goldEarned, 2.0, 0.5, "Test 2: Gold earned with multipliers")
    println("✓ Test 2 passed: Multiplier stacking")
}

// Test 3: Offline cap at 8 hours
fun testOfflineCapAt8Hours() {
    val incomeSystem = IncomeSystem()
    val state = GameState()
    
    // Add building with 1.0 income/s
    val building = Building("wheat_farm", "Wheat Farm", 1, 10.0, 1.15, 1.0, 1, true, false)
    state.buildings.add(building)
    
    // 10 hours offline (36,000 seconds)
    val offlineGold = incomeSystem.calculateOfflineEarnings(state, 36000L)
    
    // Should be capped at 8 hours = 8 × 3600 = 28,800 seconds
    // 1.0 gold/s × 28,800s = 28,800 gold
    assertApprox(offlineGold, 28800.0, 100.0, "Test 3: Offline earnings capped at 8 hours")
    println("✓ Test 3 passed: Offline cap at 8 hours")
}

// Test 4: Offline cap extended to 24 hours
fun testOfflineCapExtendedTo24Hours() {
    val incomeSystem = IncomeSystem()
    incomeSystem.setOfflineCapHours(24) // Extend cap to 24 hours
    
    val state = GameState()
    
    // Add building with 1.0 income/s
    val building = Building("wheat_farm", "Wheat Farm", 1, 10.0, 1.15, 1.0, 1, true, false)
    state.buildings.add(building)
    
    // 30 hours offline (108,000 seconds)
    val offlineGold = incomeSystem.calculateOfflineEarnings(state, 108000L, 24)
    
    // Should be capped at 24 hours = 24 × 3600 = 86,400 seconds
    // 1.0 gold/s × 86,400s = 86,400 gold
    assertApprox(offlineGold, 86400.0, 100.0, "Test 4: Offline earnings capped at 24 hours")
    println("✓ Test 4 passed: Offline cap extended to 24 hours")
}

// Test 5: Milestone detection
fun testMilestoneDetection() {
    val incomeSystem = IncomeSystem()
    val state = GameState()
    
    // Add building
    val building = Building("wheat_farm", "Wheat Farm", 1, 10.0, 1.15, 1.0, 0, true, false)
    state.buildings.add(building)
    
    // Initialize milestone tracking
    incomeSystem.resetMilestoneTracking(state)
    
    // Buy 9 buildings (no milestone)
    for (i in 1..9) {
        building.count = i
        val result = incomeSystem.update(state, 0.1f)
        assert(result.milestonesHit.isEmpty(), "Test 5: No milestone before count 10")
    }
    
    // Buy 10th building (milestone!)
    building.count = 10
    val result10 = incomeSystem.update(state, 0.1f)
    assert(result10.milestonesHit.size == 1, "Test 5: Milestone at count 10")
    assert(result10.milestonesHit[0] == "wheat_farm", "Test 5: Correct building milestone")
    
    // Buy 11-19 (no milestone)
    for (i in 11..19) {
        building.count = i
        val result = incomeSystem.update(state, 0.1f)
        assert(result.milestonesHit.isEmpty(), "Test 5: No milestone between 11-19")
    }
    
    // Buy 20th building (milestone!)
    building.count = 20
    val result20 = incomeSystem.update(state, 0.1f)
    assert(result20.milestonesHit.size == 1, "Test 5: Milestone at count 20")
    
    println("✓ Test 5 passed: Milestone detection")
}

// Test 6: Advisor bonus
fun testAdvisorBonus() {
    val incomeSystem = IncomeSystem()
    val state = GameState()
    
    // Add building
    val building = Building("wheat_farm", "Wheat Farm", 1, 10.0, 1.15, 1.0, 1, true, false)
    state.buildings.add(building)
    
    // Add advisor (not unlocked)
    val advisor = Advisor("adv_wheat_farm", "Farmer John", "wheat_farm", 1000.0, false, "Test", 1)
    state.advisors.add(advisor)
    
    // IPS without advisor
    val ipsWithout = incomeSystem.calculateTotalIPS(state)
    assertApprox(ipsWithout, 1.0, 0.01, "Test 6: IPS without advisor")
    
    // Unlock advisor
    advisor.isUnlocked = true
    
    // IPS with advisor (should be 2x)
    val ipsWith = incomeSystem.calculateTotalIPS(state)
    assertApprox(ipsWith, 2.0, 0.01, "Test 6: IPS with advisor should be 2x")
    
    println("✓ Test 6 passed: Advisor bonus")
}

// Test 7: Shadow Kingdom income (Prestige Layer 2)
fun testShadowKingdomIncome() {
    val incomeSystem = IncomeSystem()
    val state = GameState()
    
    // Add building with 10.0 income/s
    val building = Building("wheat_farm", "Wheat Farm", 1, 10.0, 1.15, 10.0, 1, true, false)
    state.buildings.add(building)
    
    // Prestige Layer 1 (no shadow kingdom)
    state.prestigeLayer = 1
    val ipsLayer1 = incomeSystem.calculateTotalIPS(state)
    assertApprox(ipsLayer1, 10.0, 0.01, "Test 7: IPS at Prestige Layer 1")
    
    // Prestige Layer 2 (shadow kingdom active = +50%)
    state.prestigeLayer = 2
    val ipsLayer2 = incomeSystem.calculateTotalIPS(state)
    assertApprox(ipsLayer2, 15.0, 0.01, "Test 7: IPS at Prestige Layer 2 with Shadow Kingdom")
    
    println("✓ Test 7 passed: Shadow Kingdom income")
}

// Test 8: Zero income with no buildings
fun testZeroIncomeWithNoBuildings() {
    val incomeSystem = IncomeSystem()
    val state = GameState()
    
    // No buildings
    val ips = incomeSystem.calculateTotalIPS(state)
    assertApprox(ips, 0.0, 0.01, "Test 8: IPS should be 0 with no buildings")
    
    val result = incomeSystem.update(state, 1.0f)
    assertApprox(result.goldEarned, 0.0, 0.01, "Test 8: Gold earned should be 0")
    
    println("✓ Test 8 passed: Zero income with no buildings")
}

// Run all tests
fun main() {
    println("Running IncomeSystem tests...")
    println()
    
    try {
        testBaseIncome()
        testMultiplierStacking()
        testOfflineCapAt8Hours()
        testOfflineCapExtendedTo24Hours()
        testMilestoneDetection()
        testAdvisorBonus()
        testShadowKingdomIncome()
        testZeroIncomeWithNoBuildings()
        
        println()
        println("✅ All tests passed!")
    } catch (e: AssertionError) {
        println()
        println("❌ Test failed: ${e.message}")
        throw e
    }
}
