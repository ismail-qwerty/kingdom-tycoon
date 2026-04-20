// PATH: core/src/main/java/com/ismail/kingdom/factories/AdvisorFactory.kt
package com.ismail.kingdom.factories

import com.ismail.kingdom.models.Advisor

// Factory for creating all advisors (one per building)
object AdvisorFactory {

    private const val COST_MULTIPLIER = 100.0 // Advisor costs 100x building base cost

    // Creates all 50 advisors
    fun buildAllAdvisors(): List<Advisor> {
        return buildEra1Advisors() +
               buildEra2Advisors() +
               buildEra3Advisors() +
               buildEra4Advisors() +
               buildEra5Advisors()
    }

    // Era 1: Dirt Village Advisors
    private fun buildEra1Advisors(): List<Advisor> {
        return listOf(
            Advisor("adv_wheat_farm", "Farmer John", "wheat_farm", 10.0 * COST_MULTIPLIER, "Automatically plants wheat fields"),
            Advisor("adv_chicken_coop", "Stable Master", "chicken_coop", 100.0 * COST_MULTIPLIER, "Manages the chicken coops"),
            Advisor("adv_lumber_mill", "Mill Keeper", "lumber_mill", 500.0 * COST_MULTIPLIER, "Oversees lumber production"),
            Advisor("adv_clay_pit", "Clay Foreman", "clay_pit", 2_000.0 * COST_MULTIPLIER, "Supervises clay extraction"),
            Advisor("adv_tanner", "Master Tanner", "tanner", 8_000.0 * COST_MULTIPLIER, "Runs the tannery operations"),
            Advisor("adv_blacksmith", "Forge Master", "blacksmith", 40_000.0 * COST_MULTIPLIER, "Commands the forge"),
            Advisor("adv_tavern", "Tavern Keeper", "tavern", 200_000.0 * COST_MULTIPLIER, "Manages the tavern business"),
            Advisor("adv_market", "Trade Master", "market", 1_000_000.0 * COST_MULTIPLIER, "Controls market operations"),
            Advisor("adv_town_hall", "Town Steward", "town_hall", 5_000_000.0 * COST_MULTIPLIER, "Administers town affairs"),
            Advisor("adv_village_cathedral", "High Priest", "village_cathedral", 25_000_000.0 * COST_MULTIPLIER, "Leads the cathedral")
        )
    }

    // Era 2: Stone Town Advisors
    private fun buildEra2Advisors(): List<Advisor> {
        val baseMultiplier = 100.0
        return listOf(
            Advisor("adv_stone_quarry", "Quarry Master", "stone_quarry", 10.0 * baseMultiplier * COST_MULTIPLIER, "Manages stone extraction"),
            Advisor("adv_mason", "Master Mason", "mason", 100.0 * baseMultiplier * COST_MULTIPLIER, "Oversees stonework"),
            Advisor("adv_stone_bridge", "Bridge Engineer", "stone_bridge", 500.0 * baseMultiplier * COST_MULTIPLIER, "Maintains bridges"),
            Advisor("adv_watchtower", "Tower Captain", "watchtower", 2_000.0 * baseMultiplier * COST_MULTIPLIER, "Commands the watchtower"),
            Advisor("adv_granary", "Grain Keeper", "granary", 8_000.0 * baseMultiplier * COST_MULTIPLIER, "Manages food storage"),
            Advisor("adv_trade_post", "Trade Envoy", "trade_post", 40_000.0 * baseMultiplier * COST_MULTIPLIER, "Handles trade routes"),
            Advisor("adv_stone_keep", "Keep Commander", "stone_keep", 200_000.0 * baseMultiplier * COST_MULTIPLIER, "Defends the keep"),
            Advisor("adv_library", "Head Librarian", "library", 1_000_000.0 * baseMultiplier * COST_MULTIPLIER, "Curates knowledge"),
            Advisor("adv_harbor", "Harbor Master", "harbor", 5_000_000.0 * baseMultiplier * COST_MULTIPLIER, "Controls the harbor"),
            Advisor("adv_city_hall", "City Chancellor", "city_hall", 25_000_000.0 * baseMultiplier * COST_MULTIPLIER, "Governs the city")
        )
    }

    // Era 3: Iron Kingdom Advisors
    private fun buildEra3Advisors(): List<Advisor> {
        val baseMultiplier = 10_000.0
        return listOf(
            Advisor("adv_iron_mine", "Mine Overseer", "iron_mine", 10.0 * baseMultiplier * COST_MULTIPLIER, "Supervises iron mining"),
            Advisor("adv_armory", "Master Armorer", "armory", 100.0 * baseMultiplier * COST_MULTIPLIER, "Crafts weapons and armor"),
            Advisor("adv_barracks", "Drill Sergeant", "barracks", 500.0 * baseMultiplier * COST_MULTIPLIER, "Trains soldiers"),
            Advisor("adv_cavalry_stable", "Cavalry Marshal", "cavalry_stable", 2_000.0 * baseMultiplier * COST_MULTIPLIER, "Commands cavalry units"),
            Advisor("adv_siege_workshop", "Siege Engineer", "siege_workshop", 8_000.0 * baseMultiplier * COST_MULTIPLIER, "Builds siege weapons"),
            Advisor("adv_castle_gate", "Gate Warden", "castle_gate", 40_000.0 * baseMultiplier * COST_MULTIPLIER, "Guards the castle gate"),
            Advisor("adv_royal_mint", "Royal Treasurer", "royal_mint", 200_000.0 * baseMultiplier * COST_MULTIPLIER, "Mints royal currency"),
            Advisor("adv_war_council", "War Strategist", "war_council", 1_000_000.0 * baseMultiplier * COST_MULTIPLIER, "Plans military campaigns"),
            Advisor("adv_knight_academy", "Knight Commander", "knight_academy", 5_000_000.0 * baseMultiplier * COST_MULTIPLIER, "Trains elite knights"),
            Advisor("adv_iron_throne_room", "Royal Advisor", "iron_throne_room", 25_000_000.0 * baseMultiplier * COST_MULTIPLIER, "Counsels the throne")
        )
    }

    // Era 4: Mage Realm Advisors
    private fun buildEra4Advisors(): List<Advisor> {
        val baseMultiplier = 1_000_000.0
        return listOf(
            Advisor("adv_mana_well", "Well Keeper", "mana_well", 10.0 * baseMultiplier * COST_MULTIPLIER, "Channels mana flows"),
            Advisor("adv_arcane_library", "Arcane Scholar", "arcane_library", 100.0 * baseMultiplier * COST_MULTIPLIER, "Studies ancient magic"),
            Advisor("adv_crystal_tower", "Crystal Sage", "crystal_tower", 500.0 * baseMultiplier * COST_MULTIPLIER, "Attunes crystal energy"),
            Advisor("adv_spell_forge", "Spell Weaver", "spell_forge", 2_000.0 * baseMultiplier * COST_MULTIPLIER, "Forges magical spells"),
            Advisor("adv_enchanter_guild", "Grand Enchanter", "enchanter_guild", 8_000.0 * baseMultiplier * COST_MULTIPLIER, "Leads the enchanters"),
            Advisor("adv_astral_observatory", "Star Gazer", "astral_observatory", 40_000.0 * baseMultiplier * COST_MULTIPLIER, "Reads the stars"),
            Advisor("adv_shadow_gate", "Shadow Walker", "shadow_gate", 200_000.0 * baseMultiplier * COST_MULTIPLIER, "Guards the shadow realm"),
            Advisor("adv_dragon_perch", "Dragon Tamer", "dragon_perch", 1_000_000.0 * baseMultiplier * COST_MULTIPLIER, "Bonds with dragons"),
            Advisor("adv_void_conduit", "Void Channeler", "void_conduit", 5_000_000.0 * baseMultiplier * COST_MULTIPLIER, "Harnesses void energy"),
            Advisor("adv_arcane_sanctum", "Archmage", "arcane_sanctum", 25_000_000.0 * baseMultiplier * COST_MULTIPLIER, "Masters all magic")
        )
    }

    // Era 5: Legendary Realm Advisors
    private fun buildEra5Advisors(): List<Advisor> {
        val baseMultiplier = 100_000_000.0
        return listOf(
            Advisor("adv_hall_of_legends", "Legend Keeper", "hall_of_legends", 10.0 * baseMultiplier * COST_MULTIPLIER, "Chronicles legendary deeds"),
            Advisor("adv_dragon_roost", "Dragon Lord", "dragon_roost", 100.0 * baseMultiplier * COST_MULTIPLIER, "Commands dragon flights"),
            Advisor("adv_hero_forge", "Hero Mentor", "hero_forge", 500.0 * baseMultiplier * COST_MULTIPLIER, "Trains legendary heroes"),
            Advisor("adv_celestial_spire", "Celestial Oracle", "celestial_spire", 2_000.0 * baseMultiplier * COST_MULTIPLIER, "Communes with the heavens"),
            Advisor("adv_glory_vault", "Glory Warden", "glory_vault", 8_000.0 * baseMultiplier * COST_MULTIPLIER, "Safeguards eternal glory"),
            Advisor("adv_titan_barracks", "Titan General", "titan_barracks", 40_000.0 * baseMultiplier * COST_MULTIPLIER, "Leads titan armies"),
            Advisor("adv_eternal_flame", "Flame Guardian", "eternal_flame", 200_000.0 * baseMultiplier * COST_MULTIPLIER, "Protects the eternal flame"),
            Advisor("adv_mythril_foundry", "Mythril Smith", "mythril_foundry", 1_000_000.0 * baseMultiplier * COST_MULTIPLIER, "Forges mythril artifacts"),
            Advisor("adv_cosmic_observatory", "Cosmic Seer", "cosmic_observatory", 5_000_000.0 * baseMultiplier * COST_MULTIPLIER, "Observes cosmic forces"),
            Advisor("adv_world_tree", "Tree Shepherd", "world_tree", 25_000_000.0 * baseMultiplier * COST_MULTIPLIER, "Nurtures the World Tree")
        )
    }
}
