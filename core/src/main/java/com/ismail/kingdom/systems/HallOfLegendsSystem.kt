// PATH: core/src/main/java/com/ismail/kingdom/systems/HallOfLegendsSystem.kt
package com.ismail.kingdom.systems

import com.ismail.kingdom.models.GameState
import kotlinx.serialization.Serializable

// Buff effect types
@Serializable
enum class BuffEffect {
    INCOME_PERCENT,
    COST_REDUCTION,
    TAP_INCOME,
    CROWN_SHARDS,
    GLORY_GEN,
    OFFLINE_CAP,
    ADVISOR_COST,
    QUEST_REWARDS,
    EVENT_DURATION,
    INCOME_MULTIPLIER,
    PRESTIGE_MULTIPLIER,
    TIER_DOUBLE
}

// Legendary buff data
@Serializable
data class LegendaryBuff(
    val id: String,
    val name: String,
    val description: String,
    val cost: Double,
    val effect: BuffEffect,
    val value: Double,
    var isUnlocked: Boolean = false,
    val tier: Int
)

// Manages Hall of Legends buffs and Glory resource (Era 5)
class HallOfLegendsSystem {
    
    private val buffs = mutableMapOf<String, LegendaryBuff>()
    
    init {
        initializeBuffs()
    }
    
    // Initializes all 20 legendary buffs
    private fun initializeBuffs() {
        // TIER 1 - Early Era 5 (cheap)
        buffs["ancient_vault"] = LegendaryBuff(
            id = "ancient_vault",
            name = "Ancient Vault",
            description = "+5% all income permanently",
            cost = 1000.0,
            effect = BuffEffect.INCOME_PERCENT,
            value = 0.05,
            tier = 1
        )
        
        buffs["eternal_market"] = LegendaryBuff(
            id = "eternal_market",
            name = "Eternal Market",
            description = "-10% all building costs",
            cost = 1500.0,
            effect = BuffEffect.COST_REDUCTION,
            value = 0.10,
            tier = 1
        )
        
        buffs["dragon_pact"] = LegendaryBuff(
            id = "dragon_pact",
            name = "Dragon Pact",
            description = "+10% tap income",
            cost = 1200.0,
            effect = BuffEffect.TAP_INCOME,
            value = 0.10,
            tier = 1
        )
        
        buffs["mystic_seal"] = LegendaryBuff(
            id = "mystic_seal",
            name = "Mystic Seal",
            description = "+5% Crown Shards per prestige",
            cost = 2000.0,
            effect = BuffEffect.CROWN_SHARDS,
            value = 0.05,
            tier = 1
        )
        
        buffs["glory_tribute"] = LegendaryBuff(
            id = "glory_tribute",
            name = "Glory Tribute",
            description = "+10% Glory generation",
            cost = 1000.0,
            effect = BuffEffect.GLORY_GEN,
            value = 0.10,
            tier = 1
        )
        
        buffs["hero_blessing"] = LegendaryBuff(
            id = "hero_blessing",
            name = "Hero's Blessing",
            description = "+8% all income permanently",
            cost = 1800.0,
            effect = BuffEffect.INCOME_PERCENT,
            value = 0.08,
            tier = 1
        )
        
        buffs["swift_construction"] = LegendaryBuff(
            id = "swift_construction",
            name = "Swift Construction",
            description = "-15% all building costs",
            cost = 2500.0,
            effect = BuffEffect.COST_REDUCTION,
            value = 0.15,
            tier = 1
        )
        
        // TIER 2 - Mid Era 5 (expensive)
        buffs["titans_grasp"] = LegendaryBuff(
            id = "titans_grasp",
            name = "Titan's Grasp",
            description = "+25% all income",
            cost = 10000.0,
            effect = BuffEffect.INCOME_PERCENT,
            value = 0.25,
            tier = 2
        )
        
        buffs["celestial_clock"] = LegendaryBuff(
            id = "celestial_clock",
            name = "Celestial Clock",
            description = "+20% offline earnings cap",
            cost = 8000.0,
            effect = BuffEffect.OFFLINE_CAP,
            value = 0.20,
            tier = 2
        )
        
        buffs["world_tree_root"] = LegendaryBuff(
            id = "world_tree_root",
            name = "World Tree Root",
            description = "Advisors 25% cheaper",
            cost = 12000.0,
            effect = BuffEffect.ADVISOR_COST,
            value = 0.25,
            tier = 2
        )
        
        buffs["cosmic_knowledge"] = LegendaryBuff(
            id = "cosmic_knowledge",
            name = "Cosmic Knowledge",
            description = "Quest rewards +50%",
            cost = 15000.0,
            effect = BuffEffect.QUEST_REWARDS,
            value = 0.50,
            tier = 2
        )
        
        buffs["eternal_flame"] = LegendaryBuff(
            id = "eternal_flame",
            name = "Eternal Flame",
            description = "Events last +50% longer",
            cost = 10000.0,
            effect = BuffEffect.EVENT_DURATION,
            value = 0.50,
            tier = 2
        )
        
        buffs["divine_fortune"] = LegendaryBuff(
            id = "divine_fortune",
            name = "Divine Fortune",
            description = "+30% all income",
            cost = 18000.0,
            effect = BuffEffect.INCOME_PERCENT,
            value = 0.30,
            tier = 2
        )
        
        buffs["legendary_crafting"] = LegendaryBuff(
            id = "legendary_crafting",
            name = "Legendary Crafting",
            description = "-20% all building costs",
            cost = 14000.0,
            effect = BuffEffect.COST_REDUCTION,
            value = 0.20,
            tier = 2
        )
        
        // TIER 3 - Endgame (very expensive)
        buffs["legendary_ascension"] = LegendaryBuff(
            id = "legendary_ascension",
            name = "Legendary Ascension",
            description = "All Tier 1+2 buffs stack double",
            cost = 100000.0,
            effect = BuffEffect.TIER_DOUBLE,
            value = 2.0,
            tier = 3
        )
        
        buffs["dragon_emperor"] = LegendaryBuff(
            id = "dragon_emperor",
            name = "Dragon Emperor",
            description = "×3 income permanently",
            cost = 150000.0,
            effect = BuffEffect.INCOME_MULTIPLIER,
            value = 3.0,
            tier = 3
        )
        
        buffs["time_anchor"] = LegendaryBuff(
            id = "time_anchor",
            name = "Time Anchor",
            description = "Offline cap becomes 48 hours",
            cost = 120000.0,
            effect = BuffEffect.OFFLINE_CAP,
            value = 48.0,
            tier = 3
        )
        
        buffs["hall_of_eternity"] = LegendaryBuff(
            id = "hall_of_eternity",
            name = "Hall of Eternity",
            description = "All prestige multipliers stack ×1.5",
            cost = 200000.0,
            effect = BuffEffect.PRESTIGE_MULTIPLIER,
            value = 1.5,
            tier = 3
        )
        
        buffs["cosmic_dominion"] = LegendaryBuff(
            id = "cosmic_dominion",
            name = "Cosmic Dominion",
            description = "×5 income permanently",
            cost = 250000.0,
            effect = BuffEffect.INCOME_MULTIPLIER,
            value = 5.0,
            tier = 3
        )
        
        buffs["infinite_glory"] = LegendaryBuff(
            id = "infinite_glory",
            name = "Infinite Glory",
            description = "+100% Glory generation",
            cost = 180000.0,
            effect = BuffEffect.GLORY_GEN,
            value = 1.0,
            tier = 3
        )
    }
    
    // Gets all buffs
    fun getAllBuffs(): List<LegendaryBuff> {
        return buffs.values.sortedBy { it.tier }
    }
    
    // Gets buffs by tier
    fun getBuffsByTier(tier: Int): List<LegendaryBuff> {
        return buffs.values.filter { it.tier == tier }.sortedBy { it.cost }
    }
    
    // Gets unlocked buffs
    fun getUnlockedBuffs(): List<LegendaryBuff> {
        return buffs.values.filter { it.isUnlocked }
    }
    
    // Checks if player can unlock a buff
    fun canUnlock(buffId: String, state: GameState): Boolean {
        val buff = buffs[buffId] ?: return false
        
        if (buff.isUnlocked) return false
        
        val currentGlory = state.resources["glory"]?.amount ?: 0.0
        return currentGlory >= buff.cost
    }
    
    // Unlocks a buff
    fun unlockBuff(buffId: String, state: GameState): Boolean {
        val buff = buffs[buffId] ?: return false
        
        if (!canUnlock(buffId, state)) return false
        
        // Consume Glory
        state.resources["glory"]?.let { it.amount -= buff.cost }
        
        // Unlock buff
        buff.isUnlocked = true
        
        return true
    }
    
    // Calculates total income multiplier from buffs
    fun getIncomeMultiplier(): Double {
        var multiplier = 1.0
        val tierDouble = buffs["legendary_ascension"]?.isUnlocked == true
        
        // Percentage bonuses
        for (buff in buffs.values) {
            if (buff.isUnlocked && buff.effect == BuffEffect.INCOME_PERCENT) {
                var bonus = buff.value
                if (tierDouble && buff.tier <= 2) {
                    bonus *= 2.0
                }
                multiplier += bonus
            }
        }
        
        // Flat multipliers
        for (buff in buffs.values) {
            if (buff.isUnlocked && buff.effect == BuffEffect.INCOME_MULTIPLIER) {
                multiplier *= buff.value
            }
        }
        
        return multiplier
    }
    
    // Calculates cost reduction from buffs
    fun getCostReduction(): Double {
        var reduction = 0.0
        val tierDouble = buffs["legendary_ascension"]?.isUnlocked == true
        
        for (buff in buffs.values) {
            if (buff.isUnlocked && buff.effect == BuffEffect.COST_REDUCTION) {
                var bonus = buff.value
                if (tierDouble && buff.tier <= 2) {
                    bonus *= 2.0
                }
                reduction += bonus
            }
        }
        
        return reduction.coerceAtMost(0.9) // Max 90% reduction
    }
    
    // Calculates tap income bonus from buffs
    fun getTapIncomeBonus(): Double {
        var bonus = 0.0
        val tierDouble = buffs["legendary_ascension"]?.isUnlocked == true
        
        for (buff in buffs.values) {
            if (buff.isUnlocked && buff.effect == BuffEffect.TAP_INCOME) {
                var value = buff.value
                if (tierDouble && buff.tier <= 2) {
                    value *= 2.0
                }
                bonus += value
            }
        }
        
        return 1.0 + bonus
    }
    
    // Calculates Crown Shards bonus from buffs
    fun getCrownShardsBonus(): Double {
        var bonus = 0.0
        val tierDouble = buffs["legendary_ascension"]?.isUnlocked == true
        
        for (buff in buffs.values) {
            if (buff.isUnlocked && buff.effect == BuffEffect.CROWN_SHARDS) {
                var value = buff.value
                if (tierDouble && buff.tier <= 2) {
                    value *= 2.0
                }
                bonus += value
            }
        }
        
        return 1.0 + bonus
    }
    
    // Calculates Glory generation multiplier from buffs
    fun getGloryGenMultiplier(): Double {
        var multiplier = 1.0
        val tierDouble = buffs["legendary_ascension"]?.isUnlocked == true
        
        for (buff in buffs.values) {
            if (buff.isUnlocked && buff.effect == BuffEffect.GLORY_GEN) {
                var bonus = buff.value
                if (tierDouble && buff.tier <= 2) {
                    bonus *= 2.0
                }
                multiplier += bonus
            }
        }
        
        return multiplier
    }
    
    // Calculates offline cap bonus from buffs
    fun getOfflineCapHours(): Int {
        // Check for Time Anchor (Tier 3)
        if (buffs["time_anchor"]?.isUnlocked == true) {
            return 48
        }
        
        // Calculate percentage bonuses
        var bonus = 0.0
        val tierDouble = buffs["legendary_ascension"]?.isUnlocked == true
        
        for (buff in buffs.values) {
            if (buff.isUnlocked && buff.effect == BuffEffect.OFFLINE_CAP && buff.tier < 3) {
                var value = buff.value
                if (tierDouble && buff.tier <= 2) {
                    value *= 2.0
                }
                bonus += value
            }
        }
        
        return (8.0 * (1.0 + bonus)).toInt()
    }
    
    // Calculates advisor cost reduction from buffs
    fun getAdvisorCostReduction(): Double {
        var reduction = 0.0
        val tierDouble = buffs["legendary_ascension"]?.isUnlocked == true
        
        for (buff in buffs.values) {
            if (buff.isUnlocked && buff.effect == BuffEffect.ADVISOR_COST) {
                var value = buff.value
                if (tierDouble && buff.tier <= 2) {
                    value *= 2.0
                }
                reduction += value
            }
        }
        
        return reduction.coerceAtMost(0.9) // Max 90% reduction
    }
    
    // Calculates quest reward multiplier from buffs
    fun getQuestRewardMultiplier(): Double {
        var multiplier = 1.0
        val tierDouble = buffs["legendary_ascension"]?.isUnlocked == true
        
        for (buff in buffs.values) {
            if (buff.isUnlocked && buff.effect == BuffEffect.QUEST_REWARDS) {
                var bonus = buff.value
                if (tierDouble && buff.tier <= 2) {
                    bonus *= 2.0
                }
                multiplier += bonus
            }
        }
        
        return multiplier
    }
    
    // Calculates event duration multiplier from buffs
    fun getEventDurationMultiplier(): Double {
        var multiplier = 1.0
        val tierDouble = buffs["legendary_ascension"]?.isUnlocked == true
        
        for (buff in buffs.values) {
            if (buff.isUnlocked && buff.effect == BuffEffect.EVENT_DURATION) {
                var bonus = buff.value
                if (tierDouble && buff.tier <= 2) {
                    bonus *= 2.0
                }
                multiplier += bonus
            }
        }
        
        return multiplier
    }
    
    // Calculates prestige multiplier bonus from buffs
    fun getPrestigeMultiplierBonus(): Double {
        if (buffs["hall_of_eternity"]?.isUnlocked == true) {
            return 1.5
        }
        return 1.0
    }
    
    // Checks if Hall of Legends is unlocked
    fun isUnlocked(eraId: Int): Boolean {
        return eraId >= 5
    }
    
    // Gets total unlocked buffs count
    fun getUnlockedCount(): Int {
        return buffs.values.count { it.isUnlocked }
    }
    
    // Gets total buffs count
    fun getTotalCount(): Int {
        return buffs.size
    }
}
