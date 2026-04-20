// PATH: core/src/main/java/com/ismail/kingdom/factories/EraFactory.kt
package com.ismail.kingdom.factories

import com.ismail.kingdom.models.Building
import com.ismail.kingdom.models.Era
import com.ismail.kingdom.models.ResourceType

// Factory for creating all eras and buildings with balanced progression
object EraFactory {
    
    private const val COST_MULTIPLIER = 1.15
    private const val INCOME_RATIO = 0.1 // Income = 10% of cost (10s payoff)
    
    // Creates all 5 eras with their buildings
    fun buildAllEras(): List<Era> {
        return listOf(
            createEra1(),
            createEra2(),
            createEra3(),
            createEra4(),
            createEra5()
        )
    }
    
    // Creates all 50 buildings across all eras
    fun buildAllBuildings(): List<Building> {
        return buildEra1Buildings() +
               buildEra2Buildings() +
               buildEra3Buildings() +
               buildEra4Buildings() +
               buildEra5Buildings()
    }
    
    // Era 1: Dirt Village (GOLD only)
    private fun createEra1(): Era {
        return Era(
            id = 1,
            name = "dirt_village",
            displayName = "Dirt Village",
            primaryResource = ResourceType.GOLD,
            secondaryResource = null,
            buildings = listOf(
                "wheat_farm", "chicken_coop", "lumber_mill", "clay_pit", "tanner",
                "blacksmith", "tavern", "market", "town_hall", "village_cathedral"
            ),
            isUnlocked = true,
            backgroundAsset = "eras/dirt_village_bg.png"
        )
    }
    
    private fun buildEra1Buildings(): List<Building> {
        return listOf(
            Building("wheat_farm", "Wheat Farm", 10.0, 1.0, 1, 0),
            Building("chicken_coop", "Chicken Coop", 100.0, 10.0, 1, 0),
            Building("lumber_mill", "Lumber Mill", 500.0, 50.0, 1, 0),
            Building("clay_pit", "Clay Pit", 2_000.0, 200.0, 1, 0),
            Building("tanner", "Tanner", 8_000.0, 800.0, 1, 0),
            Building("blacksmith", "Blacksmith", 40_000.0, 4_000.0, 1, 0),
            Building("tavern", "Tavern", 200_000.0, 20_000.0, 1, 0),
            Building("market", "Market", 1_000_000.0, 100_000.0, 1, 0),
            Building("town_hall", "Town Hall", 5_000_000.0, 500_000.0, 1, 0),
            Building("village_cathedral", "Village Cathedral", 25_000_000.0, 2_500_000.0, 1, 0)
        )
    }
    
    // Era 2: Stone Town (GOLD + STONE)
    private fun createEra2(): Era {
        return Era(
            id = 2,
            name = "stone_town",
            displayName = "Stone Town",
            primaryResource = ResourceType.GOLD,
            secondaryResource = ResourceType.STONE,
            buildings = listOf(
                "stone_quarry", "mason", "stone_bridge", "watchtower", "granary",
                "trade_post", "stone_keep", "library", "harbor", "city_hall"
            ),
            isUnlocked = false,
            backgroundAsset = "eras/stone_town_bg.png"
        )
    }
    
    private fun buildEra2Buildings(): List<Building> {
        val baseMultiplier = 100.0 // Era 2 starts 100x more expensive
        return listOf(
            Building("stone_quarry", "Stone Quarry", 10.0 * baseMultiplier, 1.0 * baseMultiplier, 2, 0),
            Building("mason", "Mason", 100.0 * baseMultiplier, 10.0 * baseMultiplier, 2, 0),
            Building("stone_bridge", "Stone Bridge", 500.0 * baseMultiplier, 50.0 * baseMultiplier, 2, 0),
            Building("watchtower", "Watchtower", 2_000.0 * baseMultiplier, 200.0 * baseMultiplier, 2, 0),
            Building("granary", "Granary", 8_000.0 * baseMultiplier, 800.0 * baseMultiplier, 2, 0),
            Building("trade_post", "Trade Post", 40_000.0 * baseMultiplier, 4_000.0 * baseMultiplier, 2, 0),
            Building("stone_keep", "Stone Keep", 200_000.0 * baseMultiplier, 20_000.0 * baseMultiplier, 2, 0),
            Building("library", "Library", 1_000_000.0 * baseMultiplier, 100_000.0 * baseMultiplier, 2, 0),
            Building("harbor", "Harbor", 5_000_000.0 * baseMultiplier, 500_000.0 * baseMultiplier, 2, 0),
            Building("city_hall", "City Hall", 25_000_000.0 * baseMultiplier, 2_500_000.0 * baseMultiplier, 2, 0)
        )
    }
    
    // Era 3: Iron Kingdom (GOLD + STONE + IRON)
    private fun createEra3(): Era {
        return Era(
            id = 3,
            name = "iron_kingdom",
            displayName = "Iron Kingdom",
            primaryResource = ResourceType.GOLD,
            secondaryResource = ResourceType.IRON,
            buildings = listOf(
                "iron_mine", "armory", "barracks", "cavalry_stable", "siege_workshop",
                "castle_gate", "royal_mint", "war_council", "knight_academy", "iron_throne_room"
            ),
            isUnlocked = false,
            backgroundAsset = "eras/iron_kingdom_bg.png"
        )
    }
    
    private fun buildEra3Buildings(): List<Building> {
        val baseMultiplier = 10_000.0 // Era 3 starts 10,000x more expensive
        return listOf(
            Building("iron_mine", "Iron Mine", 10.0 * baseMultiplier, 1.0 * baseMultiplier, 3, 0),
            Building("armory", "Armory", 100.0 * baseMultiplier, 10.0 * baseMultiplier, 3, 0),
            Building("barracks", "Barracks", 500.0 * baseMultiplier, 50.0 * baseMultiplier, 3, 0),
            Building("cavalry_stable", "Cavalry Stable", 2_000.0 * baseMultiplier, 200.0 * baseMultiplier, 3, 0),
            Building("siege_workshop", "Siege Workshop", 8_000.0 * baseMultiplier, 800.0 * baseMultiplier, 3, 0),
            Building("castle_gate", "Castle Gate", 40_000.0 * baseMultiplier, 4_000.0 * baseMultiplier, 3, 0),
            Building("royal_mint", "Royal Mint", 200_000.0 * baseMultiplier, 20_000.0 * baseMultiplier, 3, 0),
            Building("war_council", "War Council", 1_000_000.0 * baseMultiplier, 100_000.0 * baseMultiplier, 3, 0),
            Building("knight_academy", "Knight Academy", 5_000_000.0 * baseMultiplier, 500_000.0 * baseMultiplier, 3, 0),
            Building("iron_throne_room", "Iron Throne Room", 25_000_000.0 * baseMultiplier, 2_500_000.0 * baseMultiplier, 3, 0)
        )
    }
    
    // Era 4: Mage Realm (GOLD + STONE + IRON + MANA)
    private fun createEra4(): Era {
        return Era(
            id = 4,
            name = "mage_realm",
            displayName = "Mage Realm",
            primaryResource = ResourceType.MANA,
            secondaryResource = ResourceType.GOLD,
            buildings = listOf(
                "mana_well", "arcane_library", "crystal_tower", "spell_forge", "enchanter_guild",
                "astral_observatory", "shadow_gate", "dragon_perch", "void_conduit", "arcane_sanctum"
            ),
            isUnlocked = false,
            backgroundAsset = "eras/mage_realm_bg.png"
        )
    }
    
    private fun buildEra4Buildings(): List<Building> {
        val baseMultiplier = 1_000_000.0 // Era 4 starts 1M x more expensive
        return listOf(
            Building("mana_well", "Mana Well", 10.0 * baseMultiplier, 1.0 * baseMultiplier, 4, 0),
            Building("arcane_library", "Arcane Library", 100.0 * baseMultiplier, 10.0 * baseMultiplier, 4, 0),
            Building("crystal_tower", "Crystal Tower", 500.0 * baseMultiplier, 50.0 * baseMultiplier, 4, 0),
            Building("spell_forge", "Spell Forge", 2_000.0 * baseMultiplier, 200.0 * baseMultiplier, 4, 0),
            Building("enchanter_guild", "Enchanter's Guild", 8_000.0 * baseMultiplier, 800.0 * baseMultiplier, 4, 0),
            Building("astral_observatory", "Astral Observatory", 40_000.0 * baseMultiplier, 4_000.0 * baseMultiplier, 4, 0),
            Building("shadow_gate", "Shadow Gate", 200_000.0 * baseMultiplier, 20_000.0 * baseMultiplier, 4, 0),
            Building("dragon_perch", "Dragon Perch", 1_000_000.0 * baseMultiplier, 100_000.0 * baseMultiplier, 4, 0),
            Building("void_conduit", "Void Conduit", 5_000_000.0 * baseMultiplier, 500_000.0 * baseMultiplier, 4, 0),
            Building("arcane_sanctum", "Arcane Sanctum", 25_000_000.0 * baseMultiplier, 2_500_000.0 * baseMultiplier, 4, 0)
        )
    }
    
    // Era 5: Legendary Realm (ALL resources + GLORY)
    private fun createEra5(): Era {
        return Era(
            id = 5,
            name = "legendary_realm",
            displayName = "Legendary Realm",
            primaryResource = ResourceType.GLORY,
            secondaryResource = ResourceType.GOLD,
            buildings = listOf(
                "hall_of_legends", "dragon_roost", "hero_forge", "celestial_spire", "glory_vault",
                "titan_barracks", "eternal_flame", "mythril_foundry", "cosmic_observatory", "world_tree"
            ),
            isUnlocked = false,
            backgroundAsset = "eras/legendary_realm_bg.png"
        )
    }
    
    private fun buildEra5Buildings(): List<Building> {
        val baseMultiplier = 100_000_000.0 // Era 5 starts 100M x more expensive
        return listOf(
            Building("hall_of_legends", "Hall of Legends", 10.0 * baseMultiplier, 1.0 * baseMultiplier, 5, 0),
            Building("dragon_roost", "Dragon Roost", 100.0 * baseMultiplier, 10.0 * baseMultiplier, 5, 0),
            Building("hero_forge", "Hero's Forge", 500.0 * baseMultiplier, 50.0 * baseMultiplier, 5, 0),
            Building("celestial_spire", "Celestial Spire", 2_000.0 * baseMultiplier, 200.0 * baseMultiplier, 5, 0),
            Building("glory_vault", "Glory Vault", 8_000.0 * baseMultiplier, 800.0 * baseMultiplier, 5, 0),
            Building("titan_barracks", "Titan Barracks", 40_000.0 * baseMultiplier, 4_000.0 * baseMultiplier, 5, 0),
            Building("eternal_flame", "Eternal Flame", 200_000.0 * baseMultiplier, 20_000.0 * baseMultiplier, 5, 0),
            Building("mythril_foundry", "Mythril Foundry", 1_000_000.0 * baseMultiplier, 100_000.0 * baseMultiplier, 5, 0),
            Building("cosmic_observatory", "Cosmic Observatory", 5_000_000.0 * baseMultiplier, 500_000.0 * baseMultiplier, 5, 0),
            Building("world_tree", "World Tree", 25_000_000.0 * baseMultiplier, 2_500_000.0 * baseMultiplier, 5, 0)
        )
    }
}
