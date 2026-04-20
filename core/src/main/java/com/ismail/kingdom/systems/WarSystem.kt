// PATH: core/src/main/java/com/ismail/kingdom/systems/WarSystem.kt
package com.ismail.kingdom.systems

import com.ismail.kingdom.models.GameState

// Enemy camp data
data class EnemyCamp(
    val id: String,
    val name: String,
    val level: Int,
    val goldReward: Double,
    val ironReward: Double,
    val requiredPower: Double,
    var isDefeated: Boolean = false,
    var respawnSeconds: Int = 0
)

// Result of a raid attempt
data class RaidResult(
    val success: Boolean,
    val goldEarned: Double,
    val ironEarned: Double,
    val animationType: String,
    val message: String = ""
)

// Manages war camps and raid mechanics (unlocked Era 3+)
class WarSystem {
    
    private val RESPAWN_TIME = 300 // 5 minutes
    
    private val camps = mutableMapOf<Int, List<EnemyCamp>>()
    
    init {
        // Generate camps for war-enabled eras
        camps[3] = generateCamps(3) // Era 3: Iron Kingdom
        camps[4] = generateCamps(4) // Era 4: Mage Realm
        camps[5] = generateCamps(5) // Era 5: Legendary Realm
    }
    
    // Generates 5 enemy camps for specified era
    fun generateCamps(eraId: Int): List<EnemyCamp> {
        val baseMultiplier = when (eraId) {
            3 -> 1.0      // Iron Kingdom
            4 -> 100.0    // Mage Realm
            5 -> 10000.0  // Legendary Realm
            else -> 1.0
        }
        
        return listOf(
            EnemyCamp(
                id = "camp_${eraId}_1",
                name = getCampName(eraId, 1),
                level = 1,
                goldReward = 5000.0 * baseMultiplier,
                ironReward = 100.0 * baseMultiplier,
                requiredPower = 500.0 * baseMultiplier
            ),
            EnemyCamp(
                id = "camp_${eraId}_2",
                name = getCampName(eraId, 2),
                level = 3,
                goldReward = 25000.0 * baseMultiplier,
                ironReward = 500.0 * baseMultiplier,
                requiredPower = 2500.0 * baseMultiplier
            ),
            EnemyCamp(
                id = "camp_${eraId}_3",
                name = getCampName(eraId, 3),
                level = 5,
                goldReward = 100000.0 * baseMultiplier,
                ironReward = 2000.0 * baseMultiplier,
                requiredPower = 10000.0 * baseMultiplier
            ),
            EnemyCamp(
                id = "camp_${eraId}_4",
                name = getCampName(eraId, 4),
                level = 7,
                goldReward = 500000.0 * baseMultiplier,
                ironReward = 10000.0 * baseMultiplier,
                requiredPower = 50000.0 * baseMultiplier
            ),
            EnemyCamp(
                id = "camp_${eraId}_5",
                name = getCampName(eraId, 5),
                level = 10,
                goldReward = 2000000.0 * baseMultiplier,
                ironReward = 50000.0 * baseMultiplier,
                requiredPower = 200000.0 * baseMultiplier
            )
        )
    }
    
    // Gets thematic camp name based on era and level
    private fun getCampName(eraId: Int, level: Int): String {
        return when (eraId) {
            3 -> when (level) {
                1 -> "Bandit Hideout"
                2 -> "Orc Warcamp"
                3 -> "Goblin Fortress"
                4 -> "Troll Stronghold"
                5 -> "Dragon's Lair"
                else -> "Enemy Camp"
            }
            4 -> when (level) {
                1 -> "Dark Cultists"
                2 -> "Necromancer Tower"
                3 -> "Demon Portal"
                4 -> "Void Rift"
                5 -> "Lich King's Throne"
                else -> "Dark Forces"
            }
            5 -> when (level) {
                1 -> "Titan Outpost"
                2 -> "Ancient Colossus"
                3 -> "Celestial Guardians"
                4 -> "Primordial Beast"
                5 -> "World Eater"
                else -> "Legendary Foe"
            }
            else -> "Enemy Camp $level"
        }
    }
    
    // Gets camps for current era
    fun getCampsForEra(eraId: Int): List<EnemyCamp> {
        return camps[eraId] ?: emptyList()
    }
    
    // Checks if player can raid a camp
    fun canRaid(camp: EnemyCamp, state: GameState): Boolean {
        if (camp.isDefeated) return false
        
        val militaryPower = calculateMilitaryPower(state)
        return militaryPower >= camp.requiredPower
    }
    
    // Attempts to raid an enemy camp
    fun raid(camp: EnemyCamp, state: GameState): RaidResult {
        if (camp.isDefeated) {
            return RaidResult(
                success = false,
                goldEarned = 0.0,
                ironEarned = 0.0,
                animationType = "none",
                message = "Camp already defeated. Respawns in ${camp.respawnSeconds}s"
            )
        }
        
        val militaryPower = calculateMilitaryPower(state)
        
        if (militaryPower < camp.requiredPower) {
            return RaidResult(
                success = false,
                goldEarned = 0.0,
                ironEarned = 0.0,
                animationType = "defeat",
                message = "Not enough military power! Need ${camp.requiredPower.toInt()}"
            )
        }
        
        // Success - mark as defeated and set respawn timer
        camp.isDefeated = true
        camp.respawnSeconds = RESPAWN_TIME
        
        // Apply rewards
        state.currentGold += camp.goldReward
        state.resources["iron"]?.let { it.amount += camp.ironReward }
        
        return RaidResult(
            success = true,
            goldEarned = camp.goldReward,
            ironEarned = camp.ironReward,
            animationType = "victory",
            message = "Victory! Earned ${camp.goldReward.toInt()} gold and ${camp.ironReward.toInt()} iron"
        )
    }
    
    // Calculates total military power from buildings
    fun calculateMilitaryPower(state: GameState): Double {
        var power = 0.0
        
        // Barracks: 10 power per building
        state.buildings.find { it.id == "barracks" }?.let {
            power += it.count * 10.0
        }
        
        // Cavalry Stable: 50 power per building
        state.buildings.find { it.id == "cavalry_stable" }?.let {
            power += it.count * 50.0
        }
        
        // Siege Workshop: 100 power per building
        state.buildings.find { it.id == "siege_workshop" }?.let {
            power += it.count * 100.0
        }
        
        // Knight Academy: 200 power per building
        state.buildings.find { it.id == "knight_academy" }?.let {
            power += it.count * 200.0
        }
        
        // Apply hero bonuses (placeholder - integrate with HeroSystem)
        // TODO: Add hero military power multipliers
        
        return power
    }
    
    // Updates respawn timers
    fun update(delta: Float, eraId: Int) {
        val eraCamps = camps[eraId] ?: return
        
        for (camp in eraCamps) {
            if (camp.isDefeated && camp.respawnSeconds > 0) {
                camp.respawnSeconds -= delta.toInt()
                
                if (camp.respawnSeconds <= 0) {
                    camp.isDefeated = false
                    camp.respawnSeconds = 0
                }
            }
        }
    }
    
    // Gets total raids completed
    fun getTotalRaidsCompleted(eraId: Int): Int {
        // Track in GameState - placeholder
        return 0
    }
    
    // Checks if war system is unlocked
    fun isUnlocked(eraId: Int): Boolean {
        return eraId >= 3
    }
}
