// PATH: core/src/main/java/com/ismail/kingdom/models/Building.kt
package com.ismail.kingdom.models
 
import kotlinx.serialization.Serializable
import kotlin.math.pow
 
// Building model with income calculation
@Serializable
data class Building(
    val id: String,
    val name: String,
    val baseCost: Double,
    val baseIncome: Double,
    val era: Int,
    var count: Int = 0,
    var isUnlocked: Boolean = false,
    var hasAdvisor: Boolean = false
) {
    companion object {
        const val COST_GROWTH_RATE = 1.15
    }
    
    // Calculate current cost for next purchase
    fun currentCost(): Double {
        return baseCost * COST_GROWTH_RATE.pow(count.toDouble())
    }
    
    // Calculate total income per second
    fun totalIncome(): Double {
        return baseIncome * count
    }
    
    // Calculate cost for specific quantity
    fun getCostForQuantity(quantity: Int): Double {
        if (quantity <= 0) return 0.0
        val r = COST_GROWTH_RATE
        return baseCost * (r.pow(count.toDouble()) * (r.pow(quantity.toDouble()) - 1.0) / (r - 1.0))
    }
}
