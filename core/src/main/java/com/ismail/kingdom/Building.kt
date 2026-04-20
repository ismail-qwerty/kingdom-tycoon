// PATH: core/src/main/java/com/ismail/kingdom/Building.kt
package com.ismail.kingdom

// Defines a building type with cost and income properties
data class Building(
    val id: String,
    val displayName: String,
    val baseCost: Double,
    val baseIncomePerSecond: Double,
    val eraIndex: Int,
    val assetPath: String
) {
    // Cost scales by 1.15 per owned count
    fun costAt(owned: Int): Double = baseCost * Math.pow(1.15, owned.toDouble())

    // Income scales linearly with count, multiplied by era multiplier
    fun totalIncome(owned: Int, era: Era): Double =
        baseIncomePerSecond * owned * era.goldMultiplier
}

// Registry of all buildings across all eras
object BuildingRegistry {
    // Returns the full ordered list of all buildings
    val all: List<Building> = listOf(
        Building("farm",       "Farm",        15.0,        0.1,  0, "buildings/farm.png"),
        Building("mine",       "Mine",        100.0,       0.5,  0, "buildings/mine.png"),
        Building("market",     "Market",      500.0,       2.0,  1, "buildings/market.png"),
        Building("barracks",   "Barracks",    2_000.0,     7.0,  1, "buildings/barracks.png"),
        Building("cathedral",  "Cathedral",   10_000.0,    25.0, 2, "buildings/cathedral.png"),
        Building("university", "University",  50_000.0,    80.0, 2, "buildings/university.png"),
        Building("factory",    "Factory",     250_000.0,  250.0, 3, "buildings/factory.png"),
        Building("railroad",   "Railroad",    1_000_000.0,750.0, 3, "buildings/railroad.png"),
        Building("bank",       "Bank",        5_000_000.0,2000.0,4, "buildings/bank.png"),
        Building("skyscraper", "Skyscraper",  20_000_000.0,6000.0,4,"buildings/skyscraper.png")
    )

    // Returns buildings available in the given era or earlier
    fun availableFor(era: Era): List<Building> = all.filter { it.eraIndex <= era.index }

    // Finds a building by id
    fun find(id: String): Building? = all.find { it.id == id }
}
