// PATH: core/src/main/java/com/ismail/kingdom/systems/SpellSystem.kt
package com.ismail.kingdom.systems

import com.ismail.kingdom.models.GameState
import kotlinx.serialization.Serializable

// Spell types
@Serializable
enum class SpellType {
    INCOME_BOOST,
    TIME_SKIP,
    MANA_BOOST,
    ADVISOR_BOOST,
    QUEST_COMPLETE,
    TAP_BURST,
    WAR_BOOST,
    PERMANENT_SEAL
}

// Spell data
@Serializable
data class Spell(
    val id: String,
    val name: String,
    val description: String,
    val manaCost: Double,
    val cooldownSeconds: Int,
    val type: SpellType,
    var remainingCooldown: Int = 0,
    var isActive: Boolean = false,
    var activeTimeRemaining: Float = 0f
)

// Result of casting a spell
data class SpellResult(
    val success: Boolean,
    val message: String,
    val animationType: String = "none"
)

// Manages spell casting and mana system (unlocked Era 4+)
class SpellSystem {
    
    private val spells = mutableMapOf<String, Spell>()
    private var legendarySealUses = 0
    private val MAX_LEGENDARY_SEAL_USES = 10
    
    init {
        initializeSpells()
    }
    
    // Initializes all 8 spells
    private fun initializeSpells() {
        spells["gold_rush"] = Spell(
            id = "gold_rush",
            name = "Gold Rush",
            description = "×10 income for 30 seconds",
            manaCost = 100.0,
            cooldownSeconds = 600, // 10 minutes
            type = SpellType.INCOME_BOOST
        )
        
        spells["time_warp"] = Spell(
            id = "time_warp",
            name = "Time Warp",
            description = "Skip 1 hour of offline earnings instantly",
            manaCost = 500.0,
            cooldownSeconds = 3600, // 1 hour
            type = SpellType.TIME_SKIP
        )
        
        spells["mana_surge"] = Spell(
            id = "mana_surge",
            name = "Mana Surge",
            description = "×2 Mana generation for 5 minutes",
            manaCost = 50.0,
            cooldownSeconds = 300, // 5 minutes
            type = SpellType.MANA_BOOST
        )
        
        spells["enchant_all"] = Spell(
            id = "enchant_all",
            name = "Enchant All",
            description = "×2 all advisor speeds for 2 minutes",
            manaCost = 200.0,
            cooldownSeconds = 900, // 15 minutes
            type = SpellType.ADVISOR_BOOST
        )
        
        spells["crystal_clear"] = Spell(
            id = "crystal_clear",
            name = "Crystal Clear",
            description = "Instantly complete current quests",
            manaCost = 1000.0,
            cooldownSeconds = 7200, // 2 hours
            type = SpellType.QUEST_COMPLETE
        )
        
        spells["arcane_storm"] = Spell(
            id = "arcane_storm",
            name = "Arcane Storm",
            description = "Summons 100 critical taps instantly",
            manaCost = 300.0,
            cooldownSeconds = 1800, // 30 minutes
            type = SpellType.TAP_BURST
        )
        
        spells["void_drain"] = Spell(
            id = "void_drain",
            name = "Void Drain",
            description = "Drain enemy camp for ×10 rewards",
            manaCost = 800.0,
            cooldownSeconds = 3600, // 1 hour
            type = SpellType.WAR_BOOST
        )
        
        spells["legendary_seal"] = Spell(
            id = "legendary_seal",
            name = "Legendary Seal",
            description = "Seal current income permanently +10%",
            manaCost = 5000.0,
            cooldownSeconds = 0, // No cooldown
            type = SpellType.PERMANENT_SEAL
        )
    }
    
    // Gets all available spells
    fun getSpellsAvailable(state: GameState): List<Spell> {
        return spells.values.toList()
    }
    
    // Gets a specific spell
    fun getSpell(spellId: String): Spell? {
        return spells[spellId]
    }
    
    // Checks if player can cast a spell
    fun canCast(spellId: String, state: GameState): Boolean {
        val spell = spells[spellId] ?: return false
        
        // Check mana
        val currentMana = state.resources["mana"]?.amount ?: 0.0
        if (currentMana < spell.manaCost) return false
        
        // Check cooldown
        if (spell.remainingCooldown > 0) return false
        
        // Check legendary seal uses
        if (spell.id == "legendary_seal" && legendarySealUses >= MAX_LEGENDARY_SEAL_USES) {
            return false
        }
        
        return true
    }
    
    // Casts a spell
    fun castSpell(spellId: String, state: GameState): SpellResult {
        val spell = spells[spellId] ?: return SpellResult(
            success = false,
            message = "Spell not found"
        )
        
        if (!canCast(spellId, state)) {
            return SpellResult(
                success = false,
                message = when {
                    spell.remainingCooldown > 0 -> "Spell on cooldown: ${spell.remainingCooldown}s"
                    (state.resources["mana"]?.amount ?: 0.0) < spell.manaCost -> "Not enough mana"
                    spell.id == "legendary_seal" && legendarySealUses >= MAX_LEGENDARY_SEAL_USES -> "Maximum uses reached"
                    else -> "Cannot cast spell"
                }
            )
        }
        
        // Consume mana
        state.resources["mana"]?.let { it.amount -= spell.manaCost }
        
        // Apply spell effect
        val result = applySpellEffect(spell, state)
        
        // Set cooldown
        spell.remainingCooldown = spell.cooldownSeconds
        
        return result
    }
    
    // Applies spell effect
    private fun applySpellEffect(spell: Spell, state: GameState): SpellResult {
        return when (spell.type) {
            SpellType.INCOME_BOOST -> {
                spell.isActive = true
                spell.activeTimeRemaining = 30f
                SpellResult(
                    success = true,
                    message = "Income ×10 for 30 seconds!",
                    animationType = "gold_burst"
                )
            }
            
            SpellType.TIME_SKIP -> {
                // Calculate 1 hour of offline earnings
                val hourlyIncome = state.resources["gold"]?.amount ?: 0.0 // Placeholder - use IncomeSystem
                val earned = hourlyIncome * 3600.0
                state.currentGold += earned
                
                SpellResult(
                    success = true,
                    message = "Skipped 1 hour! Earned ${earned.toLong()} gold",
                    animationType = "time_warp"
                )
            }
            
            SpellType.MANA_BOOST -> {
                spell.isActive = true
                spell.activeTimeRemaining = 300f // 5 minutes
                SpellResult(
                    success = true,
                    message = "Mana generation ×2 for 5 minutes!",
                    animationType = "mana_surge"
                )
            }
            
            SpellType.ADVISOR_BOOST -> {
                spell.isActive = true
                spell.activeTimeRemaining = 120f // 2 minutes
                SpellResult(
                    success = true,
                    message = "All advisors ×2 speed for 2 minutes!",
                    animationType = "enchant"
                )
            }
            
            SpellType.QUEST_COMPLETE -> {
                // Complete all active quests
                var completedCount = 0
                for (quest in state.activeQuests) {
                    if (!quest.isCompleted) {
                        quest.currentValue = quest.targetValue
                        quest.isCompleted = true
                        completedCount++
                    }
                }
                
                SpellResult(
                    success = true,
                    message = "Completed $completedCount quests!",
                    animationType = "crystal_clear"
                )
            }
            
            SpellType.TAP_BURST -> {
                // Apply 100 critical taps instantly
                val tapValue = 1.0 // Placeholder - use TapSystem
                val totalGold = tapValue * 100.0 * 10.0 // 100 taps × 10 (critical)
                state.currentGold += totalGold
                
                SpellResult(
                    success = true,
                    message = "100 critical taps! Earned ${totalGold.toLong()} gold",
                    animationType = "arcane_storm"
                )
            }
            
            SpellType.WAR_BOOST -> {
                spell.isActive = true
                spell.activeTimeRemaining = 0f // Instant effect, but mark as active for next raid
                SpellResult(
                    success = true,
                    message = "Next raid will yield ×10 rewards!",
                    animationType = "void_drain"
                )
            }
            
            SpellType.PERMANENT_SEAL -> {
                // Permanently increase income multiplier by 10%
                state.incomeMultiplier *= 1.1
                legendarySealUses++
                
                SpellResult(
                    success = true,
                    message = "Income permanently increased by 10%! (${legendarySealUses}/$MAX_LEGENDARY_SEAL_USES)",
                    animationType = "legendary_seal"
                )
            }
        }
    }
    
    // Updates spell cooldowns and active effects
    fun update(delta: Float, state: GameState) {
        for (spell in spells.values) {
            // Update cooldowns
            if (spell.remainingCooldown > 0) {
                spell.remainingCooldown -= delta.toInt()
                if (spell.remainingCooldown < 0) {
                    spell.remainingCooldown = 0
                }
            }
            
            // Update active effects
            if (spell.isActive && spell.activeTimeRemaining > 0f) {
                spell.activeTimeRemaining -= delta
                
                if (spell.activeTimeRemaining <= 0f) {
                    spell.isActive = false
                    spell.activeTimeRemaining = 0f
                }
            }
        }
    }
    
    // Calculates mana generation rate per second
    fun manaGenRate(state: GameState): Double {
        var rate = 0.0
        
        // Mana Well: 1 mana/s per building
        state.buildings.find { it.id == "mana_well" }?.let {
            rate += it.count * 1.0
        }
        
        // Crystal Tower: 5 mana/s per building
        state.buildings.find { it.id == "crystal_tower" }?.let {
            rate += it.count * 5.0
        }
        
        // Arcane Sanctum: 20 mana/s per building
        state.buildings.find { it.id == "arcane_sanctum" }?.let {
            rate += it.count * 20.0
        }
        
        // Apply Mana Surge bonus
        if (spells["mana_surge"]?.isActive == true) {
            rate *= 2.0
        }
        
        return rate
    }
    
    // Gets income multiplier from active spells
    fun getIncomeMultiplier(): Double {
        return if (spells["gold_rush"]?.isActive == true) 10.0 else 1.0
    }
    
    // Gets advisor speed multiplier from active spells
    fun getAdvisorSpeedMultiplier(): Double {
        return if (spells["enchant_all"]?.isActive == true) 2.0 else 1.0
    }
    
    // Gets war reward multiplier from active spells
    fun getWarRewardMultiplier(): Double {
        val voidDrain = spells["void_drain"]
        return if (voidDrain?.isActive == true) {
            // Consume the effect after use
            voidDrain.isActive = false
            10.0
        } else {
            1.0
        }
    }
    
    // Checks if spell system is unlocked
    fun isUnlocked(eraId: Int): Boolean {
        return eraId >= 4
    }
    
    // Gets legendary seal uses remaining
    fun getLegendarySealUsesRemaining(): Int {
        return MAX_LEGENDARY_SEAL_USES - legendarySealUses
    }
}
