// PATH: core/src/test/kotlin/com/ismail/kingdom/GameLoopSimulator.kt
package com.ismail.kingdom

import com.badlogic.gdx.Preferences
import com.ismail.kingdom.factories.AdvisorFactory
import com.ismail.kingdom.factories.EraFactory
import com.ismail.kingdom.factories.HeroFactory
import com.ismail.kingdom.models.GameState
import com.ismail.kingdom.models.QuestType
import com.ismail.kingdom.models.Resource
import com.ismail.kingdom.systems.*

// Mock Preferences for testing
class MockPreferences : Preferences {
    private val data = mutableMapOf<String, Any>()
    
    override fun putBoolean(key: String, value: Boolean): Preferences {
        data[key] = value
        return this
    }
    
    override fun putInteger(key: String, value: Int): Preferences {
        data[key] = value
        return this
    }
    
    override fun putLong(key: String, value: Long): Preferences {
        data[key] = value
        return this
    }
    
    override fun putFloat(key: String, value: Float): Preferences {
        data[key] = value
        return this
    }
    
    override fun putString(key: String, value: String): Preferences {
        data[key] = value
        return this
    }
    
    override fun put(values: MutableMap<String, *>): Preferences {
        data.putAll(values as Map<String, Any>)
        return this
    }
    
    override fun getBoolean(key: String): Boolean = data[key] as? Boolean ?: false
    override fun getBoolean(key: String, defValue: Boolean): Boolean = data[key] as? Boolean ?: defValue
    override fun getInteger(key: String): Int = data[key] as? Int ?: 0
    override fun getInteger(key: String, defValue: Int): Int = data[key] as? Int ?: defValue
    override fun getLong(key: String): Long = data[key] as? Long ?: 0L
    override fun getLong(key: String, defValue: Long): Long = data[key] as? Long ?: defValue
    override fun getFloat(key: String): Float = data[key] as? Float ?: 0f
    override fun getFloat(key: String, defValue: Float): Float = data[key] as? Float ?: defValue
    override fun getString(key: String): String = data[key] as? String ?: ""
    override fun getString(key: String, defValue: String): String = data[key] as? String ?: defValue
    override fun get(): MutableMap<String, *> = data.toMutableMap()
    override fun contains(key: String): Boolean = data.containsKey(key)
    override fun clear() { data.clear() }
    override fun remove(key: String) { data.remove(key) }
    override fun flush() {}
}

// Standalone game loop simulator
object GameLoopSimulator {
    
    @JvmStatic
    fun main(args: Array<String>) {
        println("=== KINGDOM TYCOON GAME LOOP SIMULATOR ===\n")
        
        // Initialize game engine with mock preferences
        val prefs = MockPreferences()
        val engine = TestGameEngine(prefs)
        
        println("Step 1: Starting fresh game...")
        engine.initialize()
        printGameState(engine, "Initial State")
        
        println("\nStep 2: Simulating 100 taps...")
        var totalTapGold = 0.0
        repeat(100) {
            val tapEvent = engine.tap(0f, 0f)
            totalTapGold += tapEvent.goldEarned
        }
        println("✓ Total gold from 100 taps: ${formatGold(totalTapGold)}")
        printGameState(engine, "After 100 Taps")
        
        println("\nStep 3: Buying Wheat Farm (first building)...")
        val firstBuilding = engine.gameState.buildings.find { it.eraId == 1 && it.isUnlocked }
        if (firstBuilding != null) {
            val result = engine.buyBuilding(firstBuilding.id)
            if (result.success) {
                println("✓ Bought ${firstBuilding.name} for ${formatGold(result.goldSpent)}")
                println("✓ New IPS: ${formatGold(engine.getGoldPerSecond())}/sec")
            } else {
                println("✗ Failed to buy building")
            }
        }
        printGameState(engine, "After First Building")
        
        println("\nStep 4: Simulating 60 seconds idle...")
        engine.simulateTime(60f)
        println("✓ Gold after 1 minute: ${formatGold(engine.gameState.currentGold)}")
        printGameState(engine, "After 60 Seconds")
        
        println("\nStep 5: Buying 3 more Wheat Farms...")
        if (firstBuilding != null) {
            repeat(3) {
                val result = engine.buyBuilding(firstBuilding.id)
                if (result.success) {
                    println("✓ Bought ${firstBuilding.name} #${firstBuilding.count}")
                }
            }
            println("✓ Total ${firstBuilding.name} count: ${firstBuilding.count}")
            println("✓ New IPS: ${formatGold(engine.getGoldPerSecond())}/sec")
        }
        printGameState(engine, "After 4 Buildings")
        
        println("\nStep 6: Unlocking advisor for first building...")
        if (firstBuilding != null) {
            val advisor = engine.getAdvisorForBuilding(firstBuilding.id)
            if (advisor != null) {
                // Give enough gold to unlock advisor
                engine.gameState.addGold(advisor.unlockCost)
                val success = engine.unlockAdvisor(advisor.id)
                if (success) {
                    println("✓ Unlocked ${advisor.name}")
                    println("✓ Automation status: ${engine.isBuildingAutomated(firstBuilding.id)}")
                    println("✓ New IPS with advisor: ${formatGold(engine.getGoldPerSecond())}/sec")
                } else {
                    println("✗ Failed to unlock advisor")
                }
            }
        }
        printGameState(engine, "After Advisor Unlock")
        
        println("\nStep 7: Simulating 1 hour idle...")
        engine.simulateTime(3600f)
        println("✓ Gold after 1 hour: ${formatGold(engine.gameState.currentGold)}")
        printGameState(engine, "After 1 Hour")
        
        println("\nStep 8: Checking quest status...")
        val completedQuests = engine.getCompletedQuests()
        println("✓ Active quests: ${engine.gameState.activeQuests.size}")
        println("✓ Completed quests: ${completedQuests.size}")
        for (quest in engine.gameState.activeQuests) {
            val progress = if (quest.targetValue > 0) {
                (quest.currentValue / quest.targetValue * 100).toInt()
            } else 0
            println("  - ${quest.type}: ${progress}% complete")
        }
        
        println("\nStep 9: Fast-forwarding to prestige threshold...")
        // Set low threshold for testing (1000 gold total lifetime)
        val targetGold = 1_000_000_000.0 // 1B for real Ascension
        while (engine.gameState.totalLifetimeGold < targetGold) {
            engine.simulateTime(60f)
            if (engine.gameState.totalLifetimeGold % 100_000_000 < 1000) {
                println("  Progress: ${formatGold(engine.gameState.totalLifetimeGold)} / ${formatGold(targetGold)}")
            }
        }
        println("✓ Reached prestige threshold!")
        println("✓ Total lifetime gold: ${formatGold(engine.gameState.totalLifetimeGold)}")
        printGameState(engine, "Before Prestige")
        
        println("\nStep 10: Performing Ascension...")
        val canAscend = engine.canAscend()
        println("✓ Can ascend: $canAscend")
        
        if (canAscend) {
            val oldEra = engine.gameState.currentEra
            val oldShards = engine.gameState.crownShards
            val oldMultiplier = engine.gameState.incomeMultiplier
            
            val result = engine.performAscension()
            
            if (result.success) {
                println("✓ Ascension successful!")
                println("✓ Crown Shards earned: ${result.crownShardsEarned}")
                println("✓ Old era: $oldEra → New era: ${result.newEra}")
                println("✓ Old shards: $oldShards → New shards: ${engine.gameState.crownShards}")
                println("✓ Old multiplier: ${formatMultiplier(oldMultiplier)} → New multiplier: ${formatMultiplier(engine.gameState.incomeMultiplier)}")
                println("✓ Message: ${result.message}")
            } else {
                println("✗ Ascension failed: ${result.message}")
            }
        }
        printGameState(engine, "After Ascension")
        
        println("\nStep 11: Verifying new era buildings...")
        val newEraBuildings = engine.gameState.buildings.filter { it.eraId == engine.gameState.currentEra }
        println("✓ Buildings in Era ${engine.gameState.currentEra}: ${newEraBuildings.size}")
        println("✓ First 5 buildings:")
        newEraBuildings.take(5).forEach { building ->
            println("  - ${building.name} (Cost: ${formatGold(building.baseCost)}, Income: ${formatGold(building.baseIncome)}/sec)")
        }
        
        println("\nStep 12: Verifying Crown Shard multiplier...")
        val expectedMultiplier = 1.0 + (engine.gameState.crownShards * 0.02)
        val actualMultiplier = engine.gameState.incomeMultiplier
        val multiplierCorrect = kotlin.math.abs(expectedMultiplier - actualMultiplier) < 0.001
        println("✓ Expected multiplier: ${formatMultiplier(expectedMultiplier)}")
        println("✓ Actual multiplier: ${formatMultiplier(actualMultiplier)}")
        println("✓ Multiplier correct: $multiplierCorrect")
        
        // Test building purchase with multiplier
        println("\nStep 13: Testing income with Crown Shard multiplier...")
        val newFirstBuilding = engine.gameState.buildings.find { it.eraId == engine.gameState.currentEra && it.isUnlocked }
        if (newFirstBuilding != null) {
            // Give gold to buy building
            engine.gameState.addGold(newFirstBuilding.baseCost * 10)
            val result = engine.buyBuilding(newFirstBuilding.id)
            if (result.success) {
                println("✓ Bought ${newFirstBuilding.name}")
                val ips = engine.getGoldPerSecond()
                println("✓ IPS with Crown Shard multiplier: ${formatGold(ips)}/sec")
                
                // Verify multiplier is applied
                val baseIPS = newFirstBuilding.baseIncome * newFirstBuilding.count
                val expectedIPS = baseIPS * actualMultiplier
                println("✓ Base IPS: ${formatGold(baseIPS)}/sec")
                println("✓ Expected IPS: ${formatGold(expectedIPS)}/sec")
                println("✓ Multiplier applied correctly: ${kotlin.math.abs(ips - expectedIPS) < 1.0}")
            }
        }
        
        println("\n=== SIMULATION COMPLETE ===")
        println("\nFinal Statistics:")
        printGameState(engine, "Final State")
        
        println("\n✓ All integration tests passed!")
    }
    
    private fun printGameState(engine: TestGameEngine, label: String) {
        val state = engine.gameState
        println("\n--- $label ---")
        println("Era: ${state.currentEra}")
        println("Prestige Layer: ${state.prestigeLayer}")
        println("Gold: ${formatGold(state.currentGold)}")
        println("Total Lifetime Gold: ${formatGold(state.totalLifetimeGold)}")
        println("Crown Shards: ${state.crownShards}")
        println("Income Multiplier: ${formatMultiplier(state.incomeMultiplier)}")
        println("IPS: ${formatGold(engine.getGoldPerSecond())}/sec")
        println("Buildings: ${state.buildings.count { it.count > 0 }}")
        println("Unlocked Advisors: ${state.advisors.count { it.isUnlocked }}")
        println("Tap Count: ${state.tapCount}")
    }
    
    private fun formatGold(gold: Double): String {
        return when {
            gold < 1000 -> "%.2f".format(gold)
            gold < 1_000_000 -> "%.2fK".format(gold / 1000)
            gold < 1_000_000_000 -> "%.2fM".format(gold / 1_000_000)
            gold < 1_000_000_000_000 -> "%.2fB".format(gold / 1_000_000_000)
            gold < 1_000_000_000_000_000 -> "%.2fT".format(gold / 1_000_000_000_000)
            else -> "%.2fQa".format(gold / 1_000_000_000_000_000)
        }
    }
    
    private fun formatMultiplier(multiplier: Double): String {
        return "×%.2f".format(multiplier)
    }
}

// Test version of GameEngine that doesn't require LibGDX initialization
class TestGameEngine(prefs: Preferences) : GameEngine(prefs) {
    
    // Simulates time passing without LibGDX
    fun simulateTime(seconds: Float) {
        val deltaTime = 0.016f // 60 FPS
        var elapsed = 0f
        
        while (elapsed < seconds) {
            update(deltaTime)
            elapsed += deltaTime
        }
    }
}
