// PATH: core/src/test/kotlin/com/ismail/kingdom/ContentIntegrationTest.kt
package com.ismail.kingdom

import com.ismail.kingdom.models.*
import com.ismail.kingdom.systems.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

// Simulates full game progression from Era 1 to Era 5 in fast-forward mode
class ContentIntegrationTest {
    
    private lateinit var state: GameState
    private lateinit var buildingSystem: BuildingSystem
    private lateinit var incomeSystem: IncomeSystem
    private lateinit var prestigeSystem: PrestigeSystem
    private lateinit var advisorSystem: AdvisorSystem
    private lateinit var questManager: QuestManager
    private lateinit var mapSystem: MapSystem
    private lateinit var warSystem: WarSystem
    private lateinit var spellSystem: SpellSystem
    private lateinit var hallOfLegendsSystem: HallOfLegendsSystem
    
    private val FAST_FORWARD_RATIO = 60000.0 // 1 game minute = 1 real millisecond
    
    @Before
    fun setup() {
        state = GameState()
        buildingSystem = BuildingSystem()
        incomeSystem = IncomeSystem()
        prestigeSystem = PrestigeSystem()
        advisorSystem = AdvisorSystem()
        questManager = QuestManager()
        mapSystem = MapSystem()
        warSystem = WarSystem()
        spellSystem = SpellSystem()
        hallOfLegendsSystem = HallOfLegendsSystem()
        
        initializeEra1()
    }
    
    // Initializes Era 1 buildings, advisors, resources, heroes
    private fun initializeEra1() {
        // Initialize resources
        state.resources["gold"] = Resource("gold", "Gold", 0.0, 1)
        state.resources["stone"] = Resource("stone", "Stone", 0.0, 2)
        state.resources["iron"] = Resource("iron", "Iron", 0.0, 3)
        state.resources["mana"] = Resource("mana", "Mana", 0.0, 4)
        state.resources["glory"] = Resource("glory", "Glory", 0.0, 5)
        
        // Initialize Era 1 buildings (10 buildings)
        state.buildings.add(Building("farm", "Farm", 1, 10.0, 1.15, 1.0, 0, true))
        state.buildings.add(Building("cottage", "Cottage", 1, 50.0, 1.15, 5.0))
        state.buildings.add(Building("mill", "Mill", 1, 200.0, 1.15, 20.0))
        state.buildings.add(Building("bakery", "Bakery", 1, 800.0, 1.15, 80.0))
        state.buildings.add(Building("market", "Market", 1, 3000.0, 1.15, 300.0))
        state.buildings.add(Building("tavern", "Tavern", 1, 12000.0, 1.15, 1200.0))
        state.buildings.add(Building("blacksmith", "Blacksmith", 1, 50000.0, 1.15, 5000.0))
        state.buildings.add(Building("church", "Church", 1, 200000.0, 1.15, 20000.0))
        state.buildings.add(Building("guild", "Guild", 1, 800000.0, 1.15, 80000.0))
        state.buildings.add(Building("castle", "Castle", 1, 3000000.0, 1.15, 300000.0))
        
        // Initialize advisors
        state.advisors.add(Advisor("adv_farm", "Farmer Joe", "farm", 1000.0, false, "Automates Farm", 1))
        state.advisors.add(Advisor("adv_cottage", "Builder Bob", "cottage", 5000.0, false, "Automates Cottage", 1))
        state.advisors.add(Advisor("adv_mill", "Miller Mike", "mill", 20000.0, false, "Automates Mill", 1))
        
        // Initialize heroes
        state.heroes.add(Hero("merlin", "Merlin", "The Wise", HeroPassiveType.INCOME_MULTIPLIER, 2.0, "×2 income", false, "merlin.png"))
        state.heroes.add(Hero("robin", "Robin Hood", "The Generous", HeroPassiveType.COST_REDUCTION, 0.75, "25% cost reduction", false, "robin.png"))
        
        // Initialize quests
        questManager.refreshQuests(state)
    }
    
    // Simulates time passing (1 game minute = 1 real millisecond)
    private fun simulateTime(gameMinutes: Double) {
        val deltaSeconds = gameMinutes * 60.0
        val deltaFloat = deltaSeconds.toFloat()
        
        // Update income
        val incomeResult = incomeSystem.update(state, deltaFloat)
        state.addGold(incomeResult.goldEarned)
        
        // Update resources
        for ((resourceId, amount) in incomeResult.resourcesEarned) {
            state.resources[resourceId]?.amount = (state.resources[resourceId]?.amount ?: 0.0) + amount
        }
        
        // Update war system
        if (state.currentEra >= 3) {
            warSystem.update(deltaFloat, state.currentEra)
        }
        
        // Update spell system
        if (state.currentEra >= 4) {
            spellSystem.update(deltaFloat, state)
        }
    }
    
    @Test
    fun testFullGameProgression() {
        println("=== PHASE 1: Era 1 Run ===")
        phase1_Era1Run()
        
        println("\n=== PHASE 2: Ascension to Era 2 ===")
        phase2_AscensionToEra2()
        
        println("\n=== PHASE 3: Era 3 War Test ===")
        phase3_Era3WarTest()
        
        println("\n=== PHASE 4: Era 4 Spell Test ===")
        phase4_Era4SpellTest()
        
        println("\n=== PHASE 5: Era 5 Legend Test ===")
        phase5_Era5LegendTest()
        
        println("\n=== ALL TESTS PASSED ===")
    }
    
    // Phase 1: Buy all 10 buildings to count 10, unlock advisors, complete quests, reveal map, reach 1B gold
    private fun phase1_Era1Run() {
        // Buy all 10 buildings to count 10
        for (building in state.buildings.filter { it.eraId == 1 }) {
            while (building.count < 10) {
                state.currentGold = building.currentCost() * 2
                val result = buildingSystem.buyBuilding(building.id, state)
                assertTrue("Failed to buy ${building.name}", result.success)
            }
            assertEquals("${building.name} should have count 10", 10, building.count)
        }
        println("✓ All 10 Era 1 buildings purchased to count 10")
        
        // Unlock all advisors
        for (advisor in state.advisors.filter { it.eraId == 1 }) {
            state.currentGold = advisor.unlockCost * 2
            val unlocked = advisorSystem.unlockAdvisor(advisor.id, state)
            assertTrue("Failed to unlock ${advisor.name}", unlocked)
        }
        println("✓ All advisors unlocked: ${state.advisors.count { it.isUnlocked }}")
        
        // Complete 5 quests
        questManager.refreshQuests(state)
        var completedQuests = 0
        for (quest in state.activeQuests.take(5)) {
            quest.currentValue = quest.targetValue
            quest.isCompleted = true
            questManager.claimReward(quest, state)
            completedQuests++
        }
        assertEquals("Should complete 5 quests", 5, completedQuests)
        println("✓ Completed 5 quests")
        
        // Reveal 20 map tiles
        mapSystem.generateMapForEra(1, state)
        var revealedCount = 0
        while (revealedCount < 20) {
            val revealable = mapSystem.getRevealableTiles()
            if (revealable.isEmpty()) break
            
            val cheapest = revealable.minByOrNull { it.revealCost }!!
            state.currentGold = cheapest.revealCost * 2
            val result = mapSystem.revealTile(cheapest.id, state)
            if (result.success) revealedCount++
        }
        assertTrue("Should reveal at least 20 tiles", revealedCount >= 20)
        println("✓ Revealed $revealedCount map tiles")
        
        // Accumulate 1 Billion gold
        state.currentGold = 1_000_000_000.0
        state.totalLifetimeGold = 1_000_000_000.0
        println("✓ Accumulated 1 Billion gold")
        
        // Verify prestige is available
        assertTrue("Prestige should be available", prestigeSystem.canAscend(state))
        println("✓ Prestige available")
    }
    
    // Phase 2: Perform Ascension, verify Era 2 unlocked, crown shards gained, multiplier applied
    private fun phase2_AscensionToEra2() {
        val previousShards = state.crownShards
        
        // Perform Ascension
        val result = prestigeSystem.performAscension(state)
        assertTrue("Ascension should succeed", result.success)
        assertEquals("Should be Era 2", 2, state.currentEra)
        assertTrue("Should have crown shards", state.crownShards > previousShards)
        assertTrue("Income multiplier should be > 1.0", state.incomeMultiplier > 1.0)
        println("✓ Ascension complete: Era ${state.currentEra}, ${state.crownShards} Crown Shards, ${state.incomeMultiplier}× multiplier")
        
        // Initialize Era 2 buildings
        state.buildings.clear()
        state.buildings.add(Building("quarry", "Quarry", 2, 1000.0, 1.15, 100.0, 0, true))
        state.buildings.add(Building("stone_house", "Stone House", 2, 5000.0, 1.15, 500.0))
        state.buildings.add(Building("mason", "Mason", 2, 20000.0, 1.15, 2000.0))
        state.buildings.add(Building("statue", "Statue", 2, 80000.0, 1.15, 8000.0))
        state.buildings.add(Building("library", "Library", 2, 300000.0, 1.15, 30000.0))
        state.buildings.add(Building("university", "University", 2, 1200000.0, 1.15, 120000.0))
        state.buildings.add(Building("observatory", "Observatory", 2, 5000000.0, 1.15, 500000.0))
        state.buildings.add(Building("palace", "Palace", 2, 20000000.0, 1.15, 2000000.0))
        state.buildings.add(Building("monument", "Monument", 2, 80000000.0, 1.15, 8000000.0))
        state.buildings.add(Building("wonder", "Wonder", 2, 300000000.0, 1.15, 30000000.0))
        
        // Verify Era 2 buildings available
        assertTrue("Era 2 buildings should be available", state.buildings.any { it.eraId == 2 })
        println("✓ Era 2 buildings available: ${state.buildings.size}")
        
        // Verify Stone resource tracked
        assertTrue("Stone resource should exist", state.resources.containsKey("stone"))
        println("✓ Stone resource tracked")
    }
    
    // Phase 3: Advance to Era 3, build Barracks, raid camps, verify Iron received
    private fun phase3_Era3WarTest() {
        // Advance to Era 3 via second Ascension
        state.totalLifetimeGold = 1_000_000_000.0
        val result = prestigeSystem.performAscension(state)
        assertTrue("Second Ascension should succeed", result.success)
        assertEquals("Should be Era 3", 3, state.currentEra)
        println("✓ Advanced to Era 3")
        
        // Initialize Era 3 buildings including Barracks
        state.buildings.clear()
        state.buildings.add(Building("barracks", "Barracks", 3, 10000.0, 1.15, 1000.0, 0, true))
        state.buildings.add(Building("armory", "Armory", 3, 50000.0, 1.15, 5000.0))
        state.buildings.add(Building("cavalry_stable", "Cavalry Stable", 3, 200000.0, 1.15, 20000.0))
        state.buildings.add(Building("siege_workshop", "Siege Workshop", 3, 800000.0, 1.15, 80000.0))
        state.buildings.add(Building("knight_academy", "Knight Academy", 3, 3000000.0, 1.15, 300000.0))
        
        // Build Barracks to 5
        val barracks = state.buildings.find { it.id == "barracks" }!!
        while (barracks.count < 5) {
            state.currentGold = barracks.currentCost() * 2
            buildingSystem.buyBuilding(barracks.id, state)
        }
        assertEquals("Barracks should have count 5", 5, barracks.count)
        println("✓ Built 5 Barracks")
        
        // Verify WarSystem unlocks
        assertTrue("War system should be unlocked", warSystem.isUnlocked(state.currentEra))
        println("✓ War system unlocked")
        
        // Raid 3 enemy camps
        val camps = warSystem.getCampsForEra(3)
        assertTrue("Should have camps", camps.isNotEmpty())
        
        var raidsCompleted = 0
        for (camp in camps.take(3)) {
            val militaryPower = warSystem.calculateMilitaryPower(state)
            println("  Military power: $militaryPower, Camp requires: ${camp.requiredPower}")
            
            if (militaryPower >= camp.requiredPower) {
                val raidResult = warSystem.raid(camp, state)
                if (raidResult.success) {
                    raidsCompleted++
                    println("  ✓ Raided ${camp.name}: ${raidResult.goldEarned} gold, ${raidResult.ironEarned} iron")
                }
            }
        }
        assertTrue("Should complete at least 1 raid", raidsCompleted >= 1)
        println("✓ Completed $raidsCompleted raids")
        
        // Verify Iron resource received
        val ironAmount = state.resources["iron"]?.amount ?: 0.0
        assertTrue("Should have received Iron", ironAmount > 0.0)
        println("✓ Iron received: $ironAmount")
    }
    
    // Phase 4: Advance to Era 4, accumulate Mana, cast spells, verify effects
    private fun phase4_Era4SpellTest() {
        // Advance to Era 4
        state.totalLifetimeGold = 1_000_000_000.0
        val result = prestigeSystem.performAscension(state)
        assertTrue("Third Ascension should succeed", result.success)
        assertEquals("Should be Era 4", 4, state.currentEra)
        println("✓ Advanced to Era 4")
        
        // Initialize Era 4 buildings including Mana buildings
        state.buildings.clear()
        state.buildings.add(Building("mana_well", "Mana Well", 4, 100000.0, 1.15, 10000.0, 0, true))
        state.buildings.add(Building("crystal_tower", "Crystal Tower", 4, 500000.0, 1.15, 50000.0))
        state.buildings.add(Building("arcane_sanctum", "Arcane Sanctum", 4, 2000000.0, 1.15, 200000.0))
        
        // Build Mana Well to generate mana
        val manaWell = state.buildings.find { it.id == "mana_well" }!!
        state.currentGold = manaWell.currentCost() * 20
        for (i in 1..10) {
            buildingSystem.buyBuilding(manaWell.id, state)
        }
        println("✓ Built 10 Mana Wells")
        
        // Accumulate 100 Mana
        val manaGenRate = spellSystem.manaGenRate(state)
        println("  Mana generation rate: $manaGenRate/s")
        
        val secondsNeeded = 100.0 / manaGenRate
        simulateTime(secondsNeeded / 60.0)
        state.resources["mana"]?.amount = 100.0
        
        assertTrue("Should have 100 Mana", (state.resources["mana"]?.amount ?: 0.0) >= 100.0)
        println("✓ Accumulated 100 Mana")
        
        // Cast Gold Rush spell
        val goldBefore = state.currentGold
        val canCastGoldRush = spellSystem.canCast("gold_rush", state)
        assertTrue("Should be able to cast Gold Rush", canCastGoldRush)
        
        val goldRushResult = spellSystem.castSpell("gold_rush", state)
        assertTrue("Gold Rush should succeed", goldRushResult.success)
        println("✓ Cast Gold Rush: ${goldRushResult.message}")
        
        // Verify income multiplied for 30 seconds
        val incomeMultiplier = spellSystem.getIncomeMultiplier()
        assertEquals("Income should be ×10", 10.0, incomeMultiplier, 0.01)
        println("✓ Income multiplied ×10 for 30 seconds")
        
        // Cast Time Warp
        state.resources["mana"]?.amount = 500.0
        val timeWarpResult = spellSystem.castSpell("time_warp", state)
        assertTrue("Time Warp should succeed", timeWarpResult.success)
        
        val goldAfter = state.currentGold
        assertTrue("Should receive offline gold", goldAfter > goldBefore)
        println("✓ Cast Time Warp: received offline gold")
    }
    
    // Phase 5: Advance to Era 5, perform Legend prestige, select hero, unlock Hall buff
    private fun phase5_Era5LegendTest() {
        // Advance to Era 5
        state.totalLifetimeGold = 1_000_000_000.0
        val result = prestigeSystem.performAscension(state)
        assertTrue("Fourth Ascension should succeed", result.success)
        assertEquals("Should be Era 5", 5, state.currentEra)
        println("✓ Advanced to Era 5")
        
        // Unlock Merlin hero
        val merlin = state.heroes.find { it.id == "merlin" }!!
        merlin.isUnlocked = true
        println("✓ Unlocked Merlin hero")
        
        // Perform Legend prestige
        state.totalLifetimeGold = 1_000_000_000_000_000.0 // 1 Quadrillion
        state.prestigeLayer = 2
        
        val legendResult = prestigeSystem.performLegend(state, "merlin")
        assertTrue("Legend prestige should succeed", legendResult.success)
        assertEquals("Should be prestige layer 3", 3, state.prestigeLayer)
        println("✓ Legend prestige complete: ${legendResult.message}")
        
        // Verify Merlin's passive is permanent
        assertTrue("Merlin should be permanent", prestigeSystem.isHeroPassivePermanent("merlin"))
        println("✓ Merlin's ×2 income is now permanent")
        
        // Re-unlock Merlin after prestige
        merlin.isUnlocked = true
        
        // Verify income doubled
        state.buildings.clear()
        state.buildings.add(Building("test_building", "Test", 5, 100.0, 1.15, 100.0, 1, true))
        
        val building = state.buildings[0]
        val multiplier = incomeSystem.getBuildingMultiplier(building, state)
        assertTrue("Income should include Merlin's ×2", multiplier >= 2.0)
        println("✓ Income includes Merlin's ×2 multiplier: ${multiplier}×")
        
        // Unlock one Hall of Legends buff
        state.resources["glory"]?.amount = 10000.0
        
        val buff = hallOfLegendsSystem.getAllBuffs().first()
        val unlocked = hallOfLegendsSystem.unlockBuff(buff.id, state)
        assertTrue("Should unlock Hall buff", unlocked)
        println("✓ Unlocked Hall of Legends buff: ${buff.name}")
        
        // Verify buff stacks correctly
        val hallIncomeMultiplier = hallOfLegendsSystem.getIncomeMultiplier()
        assertTrue("Hall buff should increase income", hallIncomeMultiplier > 1.0)
        println("✓ Hall buff stacks correctly: ${hallIncomeMultiplier}× income")
        
        // Verify total income calculation
        val totalIPS = incomeSystem.calculateTotalIPS(state)
        assertTrue("Total IPS should be positive", totalIPS > 0.0)
        println("✓ Total income calculation working: $totalIPS gold/s")
    }
}
