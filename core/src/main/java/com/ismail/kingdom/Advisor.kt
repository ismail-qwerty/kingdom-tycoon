// PATH: core/src/main/java/com/ismail/kingdom/Advisor.kt
package com.ismail.kingdom

// Defines an advisor that auto-purchases a specific building
data class Advisor(
    val id: String,
    val displayName: String,
    val targetBuildingId: String,
    val unlockCost: Double,
    val purchaseIntervalSeconds: Float
)

// Registry of all advisors
object AdvisorRegistry {
    // Returns the full list of advisors, one per key building
    val all: List<Advisor> = listOf(
        Advisor("steward",   "Steward",   "farm",        1_000.0,    10f),
        Advisor("treasurer", "Treasurer", "market",      50_000.0,   15f),
        Advisor("engineer",  "Engineer",  "factory",     5_000_000.0,20f),
        Advisor("chancellor","Chancellor","bank",        100_000_000.0,30f)
    )

    // Finds an advisor by id
    fun find(id: String): Advisor? = all.find { it.id == id }
}
