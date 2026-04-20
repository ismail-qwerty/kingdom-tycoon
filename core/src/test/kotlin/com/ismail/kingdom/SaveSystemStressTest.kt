// PATH: core/src/test/kotlin/com/ismail/kingdom/SaveSystemStressTest.kt
package com.ismail.kingdom

import com.badlogic.gdx.Preferences
import com.ismail.kingdom.data.SaveManager
import com.ismail.kingdom.models.*
import com.ismail.kingdom.systems.KingdomEvent
import com.ismail.kingdom.systems.MapTile
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

// Exhaustive save/load testing for SaveManager
class SaveSystemStressTest {
    
    private lateinit var mockPrefs: MockPreferences
    
    @Before
    fun setup() {
        mockPrefs = MockPreferences()
        SaveManager.resetAutoSaveTimer()
    }
    
    @Test
    fun test1_SaveEra1WithZeroBuildings_LoadVerifyIdentical() {
        // Create minimal state
        val originalState = GameState(
            currentEra = 1,
            currentGold = 100.0,
            totalLifetimeGold = 100.0,
            tapCount = 0L,
            crownShards = 0
        )
        
        // Save
        SaveManager.saveGame(originalState, mockPrefs)
        
        // Load
        val loadedState = SaveManager.loadGame(mockPrefs)
        
        // Verify
        assertNotNull("Loaded state should not be null", loadedState)
        assertEquals("Era should match", 1, loadedState!!.currentEra)
        assertEquals("Gold should match", 100.0, loadedState.currentGold, 0.01)
        assertEquals("Lifetime gold should match", 100.0, loadedState.totalLifetimeGold, 0.01)
        assertEquals("Tap count should match", 0L, loadedState.tapCount)
        assertEquals("Buildings should be empty", 0, loadedState.buildings.size)
        
        println("✓ Test 1 passed: Era 1 with zero buildings")
    }
    
    @Test
    fun test2_SaveEra3With30Buildings5Advisors3Quests_LoadVerify() {
        // Create complex state
        val originalState = GameState(
            currentEra = 3,
            currentGold = 50000.0,
            totalLifetimeGold = 1000000.0,
            tapCount = 5000L,
            crownShards = 25
        )
        
        // Add 30 buildings
        repeat(30) { i ->
            originalState.buildings.add(
                Building(
                    id = "building_era${(i % 3) + 1}_farm_$i",
                    name = "Farm $i",
                    count = i + 1,
                    baseCost = 100.0 * (i + 1),
                    baseIncome = 10.0 * (i + 1)
                )
            )
        }
        
        // Add 5 advisors
        repeat(5) { i ->
            originalState.advisors.add(
                Advisor(
                    id = "advisor_$i",
                    name = "Advisor $i",
                    isHired = true,
                    level = i + 1
                )
            )
        }
        
        // Add 3 active quests
        repeat(3) { i ->
            originalState.activeQuests.add(
                Quest(
                    id = "quest_$i",
                    title = "Quest $i",
                    description = "Test quest",
                    progress = i * 10,
                    target = 100,
                    isCompleted = false
                )
            )
        }
        
        // Save
        SaveManager.saveGame(originalState, mockPrefs)
        
        // Load
        val loadedState = SaveManager.loadGame(mockPrefs)
        
        // Verify
        assertNotNull("Loaded state should not be null", loadedState)
        assertEquals("Era should be 3", 3, loadedState!!.currentEra)
        assertEquals("Should have 30 buildings", 30, loadedState.buildings.size)
        assertEquals("Should have 5 advisors", 5, loadedState.advisors.size)
        assertEquals("Should have 3 active quests", 3, loadedState.activeQuests.size)
        assertEquals("Crown shards should match", 25, loadedState.crownShards)
        
        // Verify building details
        assertEquals("First building count", 1, loadedState.buildings[0].count)
        assertEquals("Last building count", 30, loadedState.buildings[29].count)
        
        // Verify advisor details
        assertTrue("First advisor should be hired", loadedState.advisors[0].isHired)
        assertEquals("Last advisor level", 5, loadedState.advisors[4].level)
        
        // Verify quest details
        assertEquals("First quest progress", 0, loadedState.activeQuests[0].progress)
        assertEquals("Last quest progress", 20, loadedState.activeQuests[2].progress)
        
        println("✓ Test 2 passed: Era 3 with 30 buildings, 5 advisors, 3 quests")
    }
    
    @Test
    fun test3_SaveMidEvent_LoadVerifyTimerContinues() {
        // Create state with active event
        val originalState = GameState(
            currentEra = 2,
            currentGold = 1000.0,
            currentEvent = KingdomEvent(
                id = "event_gold_rush",
                name = "Gold Rush",
                description = "2x gold for 1 hour",
                durationSeconds = 3600f,
                remainingSeconds = 3000f,
                multiplier = 2.0
            ),
            nextEventTimer = 3000f
        )
        
        // Save
        SaveManager.saveGame(originalState, mockPrefs)
        
        // Load
        val loadedState = SaveManager.loadGame(mockPrefs)
        
        // Verify
        assertNotNull("Loaded state should not be null", loadedState)
        assertNotNull("Event should be active", loadedState!!.currentEvent)
        assertEquals("Event ID should match", "event_gold_rush", loadedState.currentEvent!!.id)
        assertEquals("Event remaining time should match", 3000f, loadedState.currentEvent!!.remainingSeconds, 1f)
        assertEquals("Event multiplier should match", 2.0, loadedState.currentEvent!!.multiplier, 0.01)
        assertEquals("Next event timer should match", 3000f, loadedState.nextEventTimer, 1f)
        
        println("✓ Test 3 passed: Mid-event save with timer continuation")
    }
    
    @Test
    fun test4_SaveWithPrestigeIIAndShadowKingdom_LoadVerifyShadowState() {
        // Create state with Prestige II (Shadow Kingdom unlocked)
        val originalState = GameState(
            currentEra = 4,
            prestigeLayer = 2,
            currentGold = 100000.0,
            crownShards = 50,
            shadowKingdomUnlocked = true
        )
        
        // Add some buildings for shadow mirroring
        repeat(10) { i ->
            originalState.buildings.add(
                Building(
                    id = "building_era3_mine_$i",
                    name = "Mine $i",
                    count = (i + 1) * 2,
                    baseCost = 500.0,
                    baseIncome = 50.0
                )
            )
        }
        
        // Save
        SaveManager.saveGame(originalState, mockPrefs)
        
        // Load
        val loadedState = SaveManager.loadGame(mockPrefs)
        
        // Verify
        assertNotNull("Loaded state should not be null", loadedState)
        assertEquals("Prestige layer should be 2", 2, loadedState!!.prestigeLayer)
        assertTrue("Shadow kingdom should be unlocked", loadedState.shadowKingdomUnlocked)
        assertEquals("Should have 10 buildings", 10, loadedState.buildings.size)
        assertEquals("Crown shards should match", 50, loadedState.crownShards)
        
        println("✓ Test 4 passed: Prestige II with Shadow Kingdom")
    }
    
    @Test
    fun test5_SaveWithAll12HeroesUnlocked_LoadVerifyHeroesAndPassives() {
        // Create state with all heroes
        val originalState = GameState(
            currentEra = 5,
            prestigeLayer = 3,
            currentGold = 1000000.0,
            crownShards = 200
        )
        
        // Add all 12 heroes
        val heroNames = listOf(
            "Warrior", "Mage", "Rogue", "Paladin", "Druid", "Necromancer",
            "Archer", "Berserker", "Cleric", "Warlock", "Monk", "Bard"
        )
        
        heroNames.forEachIndexed { index, name ->
            originalState.heroes.add(
                Hero(
                    id = "hero_$index",
                    name = name,
                    level = index + 1,
                    isUnlocked = true,
                    passiveBonus = 1.1 + (index * 0.05)
                )
            )
            originalState.permanentHeroPassives.add("hero_${index}_passive")
        }
        
        // Save
        SaveManager.saveGame(originalState, mockPrefs)
        
        // Load
        val loadedState = SaveManager.loadGame(mockPrefs)
        
        // Verify
        assertNotNull("Loaded state should not be null", loadedState)
        assertEquals("Should have 12 heroes", 12, loadedState!!.heroes.size)
        assertEquals("Should have 12 permanent passives", 12, loadedState.permanentHeroPassives.size)
        
        // Verify all heroes are unlocked
        loadedState.heroes.forEach { hero ->
            assertTrue("Hero ${hero.name} should be unlocked", hero.isUnlocked)
        }
        
        // Verify hero details
        assertEquals("First hero name", "Warrior", loadedState.heroes[0].name)
        assertEquals("Last hero name", "Bard", loadedState.heroes[11].name)
        assertEquals("First hero level", 1, loadedState.heroes[0].level)
        assertEquals("Last hero level", 12, loadedState.heroes[11].level)
        
        println("✓ Test 5 passed: All 12 heroes unlocked with passives")
    }
    
    @Test
    fun test6_CorruptSaveFile_LoadVerifyGracefulFallbackToBackup() {
        // Create valid state and save
        val validState = GameState(
            currentEra = 2,
            currentGold = 5000.0,
            tapCount = 1000L
        )
        
        SaveManager.saveGame(validState, mockPrefs)
        
        // Corrupt the primary save
        mockPrefs.putString("save_data", "{invalid json corrupt data !!!")
        mockPrefs.flush()
        
        // Try to load - should fall back to backup
        val loadedState = SaveManager.loadGame(mockPrefs)
        
        // Verify backup was loaded
        assertNotNull("Should load from backup", loadedState)
        assertEquals("Era should match backup", 2, loadedState!!.currentEra)
        assertEquals("Gold should match backup", 5000.0, loadedState.currentGold, 0.01)
        assertEquals("Tap count should match backup", 1000L, loadedState.tapCount)
        
        println("✓ Test 6 passed: Corrupted save gracefully fell back to backup")
    }
    
    @Test
    fun test7_DeleteBothSaveAndBackup_LoadVerifyCleanNewGameState() {
        // Create and save a state
        val state = GameState(currentEra = 3, currentGold = 10000.0)
        SaveManager.saveGame(state, mockPrefs)
        
        // Delete both saves
        SaveManager.deleteSave(mockPrefs)
        
        // Try to load
        val loadedState = SaveManager.loadGame(mockPrefs)
        
        // Verify no save exists
        assertNull("Should return null when no save exists", loadedState)
        assertFalse("hasSave should return false", SaveManager.hasSave(mockPrefs))
        
        println("✓ Test 7 passed: Both saves deleted, clean state")
    }
    
    @Test
    fun test8_SaveVersion0Format_LoadWithVersion1Code_VerifyMigration() {
        // Create legacy save (version 0 - no version wrapper)
        val legacyState = GameState(
            currentEra = 2,
            currentGold = 3000.0,
            tapCount = 500L
            // Note: crownShards and shadowKingdomUnlocked would be missing in real v0
        )
        
        // Manually serialize as legacy format (no version wrapper)
        val legacyJson = """
            {
                "currentEra": 2,
                "prestigeLayer": 0,
                "currentGold": 3000.0,
                "resources": {},
                "buildings": [],
                "advisors": [],
                "heroes": [],
                "activeQuests": [],
                "totalLifetimeGold": 3000.0,
                "tapCount": 500,
                "incomeMultiplier": 1.0,
                "lastSaveTime": 0,
                "currentEvent": null,
                "nextEventTimer": 0.0,
                "mapTiles": [],
                "permanentHeroPassives": []
            }
        """.trimIndent()
        
        // Save legacy format
        mockPrefs.putString("save_data", legacyJson)
        mockPrefs.flush()
        
        // Load with version 1 code
        val loadedState = SaveManager.loadGame(mockPrefs)
        
        // Verify migration succeeded
        assertNotNull("Should load legacy save", loadedState)
        assertEquals("Era should match", 2, loadedState!!.currentEra)
        assertEquals("Gold should match", 3000.0, loadedState.currentGold, 0.01)
        assertEquals("Tap count should match", 500L, loadedState.tapCount)
        
        // Verify new fields have defaults
        assertEquals("Crown shards should default to 0", 0, loadedState.crownShards)
        assertFalse("Shadow kingdom should default to false", loadedState.shadowKingdomUnlocked)
        
        println("✓ Test 8 passed: Legacy save migrated to version 1")
    }
    
    @Test
    fun test9_AutoSaveFiresEvery30Seconds() {
        val state = GameState(currentEra = 1, currentGold = 100.0)
        
        // Simulate 29 seconds - should not save
        SaveManager.updateAutoSave(29f, state, mockPrefs)
        assertFalse("Should not auto-save before 30s", SaveManager.hasSave(mockPrefs))
        
        // Simulate 1 more second - should trigger save
        SaveManager.updateAutoSave(1f, state, mockPrefs)
        assertTrue("Should auto-save at 30s", SaveManager.hasSave(mockPrefs))
        
        // Verify save is valid
        val loadedState = SaveManager.loadGame(mockPrefs)
        assertNotNull("Auto-saved state should load", loadedState)
        assertEquals("Gold should match", 100.0, loadedState!!.currentGold, 0.01)
        
        println("✓ Test 9 passed: Auto-save fires exactly at 30 seconds")
    }
    
    @Test
    fun test10_AutoSaveDoesNotFireMultipleTimes() {
        val state = GameState(currentEra = 1, currentGold = 100.0)
        
        // First auto-save at 30s
        SaveManager.updateAutoSave(30f, state, mockPrefs)
        val firstSaveTime = mockPrefs.getString("save_data", "")
        
        // Update gold
        state.currentGold = 200.0
        
        // Simulate 29 more seconds - should not save again
        SaveManager.updateAutoSave(29f, state, mockPrefs)
        val secondCheck = mockPrefs.getString("save_data", "")
        
        assertEquals("Should not save before next 30s interval", firstSaveTime, secondCheck)
        
        // Simulate 1 more second - should save again
        SaveManager.updateAutoSave(1f, state, mockPrefs)
        val thirdCheck = mockPrefs.getString("save_data", "")
        
        assertNotEquals("Should save at next 30s interval", firstSaveTime, thirdCheck)
        
        // Verify new gold value
        val loadedState = SaveManager.loadGame(mockPrefs)
        assertEquals("Should have updated gold", 200.0, loadedState!!.currentGold, 0.01)
        
        println("✓ Test 10 passed: Auto-save respects 30s interval")
    }
}

// Mock Preferences implementation for testing
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
    override fun flush() {} // No-op for mock
}

// Mock model classes for testing
@kotlinx.serialization.Serializable
data class Building(
    val id: String,
    val name: String,
    var count: Int,
    val baseCost: Double,
    val baseIncome: Double
)

@kotlinx.serialization.Serializable
data class Advisor(
    val id: String,
    val name: String,
    var isHired: Boolean,
    var level: Int
)

@kotlinx.serialization.Serializable
data class Hero(
    val id: String,
    val name: String,
    var level: Int,
    var isUnlocked: Boolean,
    val passiveBonus: Double
)

@kotlinx.serialization.Serializable
data class Quest(
    val id: String,
    val title: String,
    val description: String,
    var progress: Int,
    val target: Int,
    var isCompleted: Boolean
)

@kotlinx.serialization.Serializable
data class Resource(
    val id: String,
    var amount: Double
)
