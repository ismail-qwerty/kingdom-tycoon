// PATH: core/src/test/kotlin/com/ismail/kingdom/BalanceSimulator.kt
package com.ismail.kingdom

import com.ismail.kingdom.utils.SafeMath
import org.junit.Test
import kotlin.math.pow

// Simulates game balance and progression for a skilled player
class BalanceSimulator {
    
    // Player behavior constants
    private val TAPS_PER_SECOND = 3.0 // Active tapping
    private val ACTIVE_PLAY_HOURS_PER_DAY = 2.0
    private val ADS_WATCHED_PER_DAY = 3
    private val AD_BOOST_MULTIPLIER = 4.0
    private val AD_BOOST_DURATION_MINUTES = 30
    
    // Balance targets
    private val TARGET_FIRST_PRESTIGE_HOURS = 2.5 // 2-3 hours
    private val TARGET_ERA_5_HOURS = 20.0
    private val TARGET_LEGEND_PRESTIGE_HOURS = 40.0
    
    // Building balance targets
    private val TARGET_FIRST_BUILDING_MINUTES = 2.0
    private val TARGET_MOST_EXPENSIVE_MINUTES = 30.0
    
    @Test
    fun runFullBalanceSimulation() {
        println("\n========== KINGDOM TYCOON BALANCE SIMULATION ==========\n")
        
        // Simulate progression through all eras
        val eraResults = mutableListOf<EraSimulationResult>()
        
        for (era in 1..5) {
            println("--- ERA $era SIMULATION ---")
            val result = simulateEra(era)
            eraResults.add(result)
            printEraResults(result)
            println()
        }
        
        // Simulate prestige progression
        println("--- PRESTIGE PROGRESSION SIMULATION ---")
        val prestigeResults = simulatePrestigeProgression()
        printPrestigeResults(prestigeResults)
        
        // Analyze and recommend fixes
        println("\n--- BALANCE ANALYSIS ---")
        analyzeBalance(eraResults, prestigeResults)
        
        println("\n========== SIMULATION COMPLETE ==========\n")
    }
    
    // Simulates progression through a single era
    private fun simulateEra(era: Int): EraSimulationResult {
        val buildings = getEraBuildings(era)
        val tapGold = getEraTapGold(era)
        
        var currentGold = 0.0
        var totalIncome = 0.0
        var timeElapsed = 0.0 // in seconds
        
        val buildingPurchaseTimes = mutableMapOf<String, Double>()
        
        // Simulate buying buildings in order
        buildings.forEach { building ->
            val cost = building.baseCost
            
            // Calculate time to afford this building
            val timeToAfford = if (totalIncome > 0) {
                // Use passive income
                val goldNeeded = cost - currentGold
                goldNeeded / totalIncome
            } else {
                // Use tapping
                val goldNeeded = cost - currentGold
                goldNeeded / (tapGold * TAPS_PER_SECOND)
            }
            
            timeElapsed += timeToAfford
            currentGold = cost // Spent on building
            
            // Add this building's income
            totalIncome += building.baseIncome
            
            buildingPurchaseTimes[building.name] = timeElapsed / 60.0 // Convert to minutes
        }
        
        return EraSimulationResult(
            era = era,
            buildings = buildings,
            purchaseTimes = buildingPurchaseTimes,
            totalTimeMinutes = timeElapsed / 60.0,
            finalIncomePerSecond = totalIncome
        )
    }
    
    // Simulates prestige progression
    private fun simulatePrestigeProgression(): PrestigeSimulationResult {
        val prestigeTimes = mutableListOf<Double>()
        val crownShardsEarned = mutableListOf<Int>()
        val incomeMultipliers = mutableListOf<Double>()
        
        var totalPlaytimeHours = 0.0
        var lifetimeGold = 0.0
        var totalCrownShards = 0
        
        // Simulate 15 prestiges
        for (prestigeNum in 1..15) {
            // Calculate income multiplier from crown shards
            val incomeMultiplier = calculateIncomeMultiplier(totalCrownShards)
            incomeMultipliers.add(incomeMultiplier)
            
            // Simulate earning gold until prestige threshold
            val goldNeeded = calculatePrestigeRequirement(prestigeNum)
            val baseIncomePerSecond = 1000.0 * prestigeNum // Scales with prestige
            val effectiveIncome = baseIncomePerSecond * incomeMultiplier
            
            val hoursToPrestige = (goldNeeded / effectiveIncome) / 3600.0
            totalPlaytimeHours += hoursToPrestige
            prestigeTimes.add(totalPlaytimeHours)
            
            lifetimeGold += goldNeeded
            
            // Calculate crown shards earned
            val shards = calculateCrownShardsEarned(lifetimeGold, 0)
            crownShardsEarned.add(shards)
            totalCrownShards += shards
        }
        
        return PrestigeSimulationResult(
            prestigeTimes = prestigeTimes,
            crownShardsEarned = crownShardsEarned,
            incomeMultipliers = incomeMultipliers
        )
    }
    
    // Prints era simulation results
    private fun printEraResults(result: EraSimulationResult) {
        println("  Total buildings: ${result.buildings.size}")
        println("  First building time: %.2f minutes".format(result.purchaseTimes.values.first()))
        println("  Last building time: %.2f minutes".format(result.purchaseTimes.values.last()))
        println("  Total era time: %.2f minutes (%.2f hours)".format(result.totalTimeMinutes, result.totalTimeMinutes / 60.0))
        println("  Final IPS: ${SafeMath.formatNumber(result.finalIncomePerSecond)}/s")
        
        // Check for balance issues
        val firstTime = result.purchaseTimes.values.first()
        val lastTime = result.purchaseTimes.values.last()
        
        if (firstTime > TARGET_FIRST_BUILDING_MINUTES) {
            println("  ⚠ WARNING: First building takes too long (${firstTime.format(2)}m > ${TARGET_FIRST_BUILDING_MINUTES}m)")
        }
        
        if (lastTime > TARGET_MOST_EXPENSIVE_MINUTES) {
            println("  ⚠ WARNING: Most expensive building takes too long (${lastTime.format(2)}m > ${TARGET_MOST_EXPENSIVE_MINUTES}m)")
        }
    }
    
    // Prints prestige simulation results
    private fun printPrestigeResults(result: PrestigeSimulationResult) {
        println("  Prestige 1: %.2f hours, %d shards, %.2fx multiplier".format(
            result.prestigeTimes[0],
            result.crownShardsEarned[0],
            result.incomeMultipliers[0]
        ))
        
        println("  Prestige 5: %.2f hours, %d shards, %.2fx multiplier".format(
            result.prestigeTimes[4],
            result.crownShardsEarned[4],
            result.incomeMultipliers[4]
        ))
        
        println("  Prestige 10: %.2f hours, %d shards, %.2fx multiplier".format(
            result.prestigeTimes[9],
            result.crownShardsEarned[9],
            result.incomeMultipliers[9]
        ))
    }
    
    // Analyzes balance and recommends fixes
    private fun analyzeBalance(eraResults: List<EraSimulationResult>, prestigeResults: PrestigeSimulationResult) {
        println("\nBuilding Balance Issues:")
        
        eraResults.forEach { result ->
            val firstTime = result.purchaseTimes.values.first()
            val lastTime = result.purchaseTimes.values.last()
            
            if (firstTime < 0.5) {
                println("  Era ${result.era}: First building TOO CHEAP (${firstTime.format(2)}m) → increase cost 3x")
            }
            
            if (lastTime < 1.0) {
                println("  Era ${result.era}: Most expensive TOO CHEAP (${lastTime.format(2)}m) → increase cost 3x")
            }
            
            if (lastTime > 60.0) {
                println("  Era ${result.era}: Most expensive TOO EXPENSIVE (${lastTime.format(2)}m) → reduce cost 3x")
            }
        }
        
        println("\nPrestige Balance Issues:")
        
        val firstPrestigeTime = prestigeResults.prestigeTimes[0]
        if (firstPrestigeTime < 1.0) {
            println("  First prestige TOO FAST (${firstPrestigeTime.format(2)}h) → increase requirement")
        } else if (firstPrestigeTime > 5.0) {
            println("  First prestige TOO SLOW (${firstPrestigeTime.format(2)}h) → decrease requirement")
        }
        
        val firstMultiplier = prestigeResults.incomeMultipliers[0]
        if (firstMultiplier < 1.10) {
            println("  First prestige multiplier TOO LOW (${firstMultiplier.format(2)}x) → increase shard bonus")
        } else if (firstMultiplier > 1.30) {
            println("  First prestige multiplier TOO HIGH (${firstMultiplier.format(2)}x) → decrease shard bonus")
        }
        
        val tenthMultiplier = prestigeResults.incomeMultipliers[9]
        if (tenthMultiplier < 2.0) {
            println("  10th prestige multiplier TOO LOW (${tenthMultiplier.format(2)}x) → increase shard bonus")
        } else if (tenthMultiplier > 4.0) {
            println("  10th prestige multiplier TOO HIGH (${tenthMultiplier.format(2)}x) → decrease shard bonus")
        }
        
        println("\nRecommended Tuning:")
        printRecommendedTuning(eraResults, prestigeResults)
    }
    
    // Prints recommended tuning values
    private fun printRecommendedTuning(eraResults: List<EraSimulationResult>, prestigeResults: PrestigeSimulationResult) {
        println("\n// EraFactory.kt - Recommended Building Costs")
        println("// Adjust these values based on simulation results:")
        
        eraResults.forEach { result ->
            println("\n// Era ${result.era}")
            result.buildings.forEachIndexed { index, building ->
                val purchaseTime = result.purchaseTimes[building.name] ?: 0.0
                val adjustment = when {
                    purchaseTime < 1.0 -> " // TOO CHEAP - increase 3x"
                    purchaseTime > 60.0 -> " // TOO EXPENSIVE - reduce 3x"
                    else -> ""
                }
                println("${building.name}: baseCost = ${building.baseCost.toLong()}, baseIncome = ${building.baseIncome.toLong()}$adjustment")
            }
        }
        
        println("\n// PrestigeSystem.kt - Recommended Values")
        println("private const val CROWN_SHARD_BONUS_PER_SHARD = 0.002 // 0.2% per shard")
        println("private const val PRESTIGE_BASE_REQUIREMENT = 1e9 // 1 billion gold")
    }
    
    // Gets buildings for an era
    private fun getEraBuildings(era: Int): List<SimulatedBuilding> {
        return when (era) {
            1 -> listOf(
                SimulatedBuilding("Farm", 10.0, 1.0),
                SimulatedBuilding("Lumber Mill", 50.0, 5.0),
                SimulatedBuilding("Quarry", 250.0, 25.0),
                SimulatedBuilding("Mine", 1000.0, 100.0),
                SimulatedBuilding("Market", 5000.0, 500.0)
            )
            2 -> listOf(
                SimulatedBuilding("Blacksmith", 25000.0, 2500.0),
                SimulatedBuilding("Barracks", 100000.0, 10000.0),
                SimulatedBuilding("Temple", 500000.0, 50000.0),
                SimulatedBuilding("Library", 2000000.0, 200000.0),
                SimulatedBuilding("Castle", 10000000.0, 1000000.0)
            )
            3 -> listOf(
                SimulatedBuilding("Academy", 50000000.0, 5000000.0),
                SimulatedBuilding("Observatory", 250000000.0, 25000000.0),
                SimulatedBuilding("Workshop", 1000000000.0, 100000000.0),
                SimulatedBuilding("Guild Hall", 5000000000.0, 500000000.0),
                SimulatedBuilding("Cathedral", 25000000000.0, 2500000000.0)
            )
            4 -> listOf(
                SimulatedBuilding("Arcane Tower", 100000000000.0, 10000000000.0),
                SimulatedBuilding("Royal Palace", 500000000000.0, 50000000000.0),
                SimulatedBuilding("Grand Arena", 2500000000000.0, 250000000000.0),
                SimulatedBuilding("Wonder", 10000000000000.0, 1000000000000.0),
                SimulatedBuilding("Nexus", 50000000000000.0, 5000000000000.0)
            )
            5 -> listOf(
                SimulatedBuilding("Celestial Forge", 250000000000000.0, 25000000000000.0),
                SimulatedBuilding("Eternal Vault", 1000000000000000.0, 100000000000000.0),
                SimulatedBuilding("Cosmic Spire", 5000000000000000.0, 500000000000000.0),
                SimulatedBuilding("Infinity Gate", 25000000000000000.0, 2500000000000000.0),
                SimulatedBuilding("Legendary Monument", 100000000000000000.0, 10000000000000000.0)
            )
            else -> emptyList()
        }
    }
    
    // Gets tap gold for an era
    private fun getEraTapGold(era: Int): Double {
        return when (era) {
            1 -> 1.0
            2 -> 100.0
            3 -> 10000.0
            4 -> 1000000.0
            5 -> 100000000.0
            else -> 1.0
        }
    }
    
    // Calculates prestige requirement
    private fun calculatePrestigeRequirement(prestigeNum: Int): Double {
        val baseRequirement = 1e9 // 1 billion
        return baseRequirement * prestigeNum.toDouble().pow(1.5)
    }
    
    // Calculates crown shards earned from prestige
    private fun calculateCrownShardsEarned(lifetimeGold: Double, prestigeLayer: Int): Int {
        val goldInBillions = lifetimeGold / 1e9
        val sqrtValue = kotlin.math.sqrt(goldInBillions)
        val layerMultiplier = 1.0 + (prestigeLayer * 0.5)
        return (sqrtValue * layerMultiplier).toInt().coerceIn(1, 100000)
    }
    
    // Calculates income multiplier from crown shards
    private fun calculateIncomeMultiplier(crownShards: Int): Double {
        val bonusPerShard = 0.002 // 0.2% per shard
        return 1.0 + (crownShards * bonusPerShard)
    }
    
    // Extension function for formatting doubles
    private fun Double.format(decimals: Int): String = "%.${decimals}f".format(this)
}

// Simulated building data
data class SimulatedBuilding(
    val name: String,
    val baseCost: Double,
    val baseIncome: Double
)

// Era simulation result
data class EraSimulationResult(
    val era: Int,
    val buildings: List<SimulatedBuilding>,
    val purchaseTimes: Map<String, Double>,
    val totalTimeMinutes: Double,
    val finalIncomePerSecond: Double
)

// Prestige simulation result
data class PrestigeSimulationResult(
    val prestigeTimes: List<Double>,
    val crownShardsEarned: List<Int>,
    val incomeMultipliers: List<Double>
)
