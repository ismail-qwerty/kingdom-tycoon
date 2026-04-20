// PATH: core/src/main/java/com/ismail/kingdom/data/EraFactory.kt
package com.ismail.kingdom.data

import com.ismail.kingdom.models.Building

// Factory for creating era-specific buildings with balanced costs and income
object EraFactory {
    
    // Building cost growth rate (exponential)
    const val COST_GROWTH_RATE = 1.15
    
    // Creates all buildings for a specific era
    fun createEraBuildings(era: Int): List<Building> {
        return when (era) {
            1 -> createEra1Buildings()
            2 -> createEra2Buildings()
            3 -> createEra3Buildings()
            4 -> createEra4Buildings()
            5 -> createEra5Buildings()
            else -> emptyList()
        }
    }
    
    // Era 1: Medieval Village (Starting era)
    private fun createEra1Buildings(): List<Building> {
        return listOf(
            Building(
                id = "era1_farm",
                name = "Farm",
                baseCost = 15.0, // Tuned: affordable in ~30 seconds of tapping
                baseIncome = 1.0,
                era = 1,
                count = 0
            ),
            Building(
                id = "era1_lumber_mill",
                name = "Lumber Mill",
                baseCost = 100.0, // Tuned: ~2 minutes with first farm
                baseIncome = 8.0,
                era = 1,
                count = 0
            ),
            Building(
                id = "era1_quarry",
                name = "Quarry",
                baseCost = 600.0, // Tuned: ~5 minutes with passive income
                baseIncome = 50.0,
                era = 1,
                count = 0
            ),
            Building(
                id = "era1_mine",
                name = "Mine",
                baseCost = 3000.0, // Tuned: ~10 minutes
                baseIncome = 250.0,
                era = 1,
                count = 0
            ),
            Building(
                id = "era1_market",
                name = "Market",
                baseCost = 15000.0, // Tuned: ~20 minutes (era capstone)
                baseIncome = 1200.0,
                era = 1,
                count = 0
            )
        )
    }
    
    // Era 2: Medieval Kingdom
    private fun createEra2Buildings(): List<Building> {
        return listOf(
            Building(
                id = "era2_blacksmith",
                name = "Blacksmith",
                baseCost = 75000.0, // Tuned: smooth transition from Era 1
                baseIncome = 6000.0,
                era = 2,
                count = 0
            ),
            Building(
                id = "era2_barracks",
                name = "Barracks",
                baseCost = 400000.0,
                baseIncome = 30000.0,
                era = 2,
                count = 0
            ),
            Building(
                id = "era2_temple",
                name = "Temple",
                baseCost = 2000000.0,
                baseIncome = 150000.0,
                era = 2,
                count = 0
            ),
            Building(
                id = "era2_library",
                name = "Library",
                baseCost = 10000000.0,
                baseIncome = 750000.0,
                era = 2,
                count = 0
            ),
            Building(
                id = "era2_castle",
                name = "Castle",
                baseCost = 50000000.0, // Tuned: ~30 minutes of Era 2 play
                baseIncome = 3750000.0,
                era = 2,
                count = 0
            )
        )
    }
    
    // Era 3: Renaissance Empire
    private fun createEra3Buildings(): List<Building> {
        return listOf(
            Building(
                id = "era3_academy",
                name = "Academy",
                baseCost = 250000000.0,
                baseIncome = 18750000.0,
                era = 3,
                count = 0
            ),
            Building(
                id = "era3_observatory",
                name = "Observatory",
                baseCost = 1250000000.0,
                baseIncome = 93750000.0,
                era = 3,
                count = 0
            ),
            Building(
                id = "era3_workshop",
                name = "Workshop",
                baseCost = 6250000000.0,
                baseIncome = 468750000.0,
                era = 3,
                count = 0
            ),
            Building(
                id = "era3_guild_hall",
                name = "Guild Hall",
                baseCost = 31250000000.0,
                baseIncome = 2343750000.0,
                era = 3,
                count = 0
            ),
            Building(
                id = "era3_cathedral",
                name = "Cathedral",
                baseCost = 156250000000.0, // Tuned: balanced for mid-game
                baseIncome = 11718750000.0,
                era = 3,
                count = 0
            )
        )
    }
    
    // Era 4: Industrial Revolution
    private fun createEra4Buildings(): List<Building> {
        return listOf(
            Building(
                id = "era4_arcane_tower",
                name = "Arcane Tower",
                baseCost = 781250000000.0,
                baseIncome = 58593750000.0,
                era = 4,
                count = 0
            ),
            Building(
                id = "era4_royal_palace",
                name = "Royal Palace",
                baseCost = 3906250000000.0,
                baseIncome = 292968750000.0,
                era = 4,
                count = 0
            ),
            Building(
                id = "era4_grand_arena",
                name = "Grand Arena",
                baseCost = 19531250000000.0,
                baseIncome = 1464843750000.0,
                era = 4,
                count = 0
            ),
            Building(
                id = "era4_wonder",
                name = "Wonder",
                baseCost = 97656250000000.0,
                baseIncome = 7324218750000.0,
                era = 4,
                count = 0
            ),
            Building(
                id = "era4_nexus",
                name = "Nexus",
                baseCost = 488281250000000.0, // Tuned: late-game challenge
                baseIncome = 36621093750000.0,
                era = 4,
                count = 0
            )
        )
    }
    
    // Era 5: Legendary Age
    private fun createEra5Buildings(): List<Building> {
        return listOf(
            Building(
                id = "era5_celestial_forge",
                name = "Celestial Forge",
                baseCost = 2441406250000000.0,
                baseIncome = 183105468750000.0,
                era = 5,
                count = 0
            ),
            Building(
                id = "era5_eternal_vault",
                name = "Eternal Vault",
                baseCost = 12207031250000000.0,
                baseIncome = 915527343750000.0,
                era = 5,
                count = 0
            ),
            Building(
                id = "era5_cosmic_spire",
                name = "Cosmic Spire",
                baseCost = 61035156250000000.0,
                baseIncome = 4577636718750000.0,
                era = 5,
                count = 0
            ),
            Building(
                id = "era5_infinity_gate",
                name = "Infinity Gate",
                baseCost = 305175781250000000.0,
                baseIncome = 22888183593750000.0,
                era = 5,
                count = 0
            ),
            Building(
                id = "era5_legendary_monument",
                name = "Legendary Monument",
                baseCost = 1525878906250000000.0, // Tuned: ultimate endgame goal
                baseIncome = 114440917968750000.0,
                era = 5,
                count = 0
            )
        )
    }
    
    // Gets tap gold value for an era
    fun getEraTapGold(era: Int): Double {
        return when (era) {
            1 -> 1.0
            2 -> 150.0 // Tuned: smooth transition
            3 -> 20000.0
            4 -> 2500000.0
            5 -> 300000000.0
            else -> 1.0
        }
    }
    
    // Gets era unlock requirement (total lifetime gold)
    fun getEraUnlockRequirement(era: Int): Double {
        return when (era) {
            1 -> 0.0 // Starting era
            2 -> 100000.0 // 100K gold
            3 -> 100000000.0 // 100M gold
            4 -> 100000000000.0 // 100B gold
            5 -> 100000000000000.0 // 100T gold
            else -> 0.0
        }
    }
}
