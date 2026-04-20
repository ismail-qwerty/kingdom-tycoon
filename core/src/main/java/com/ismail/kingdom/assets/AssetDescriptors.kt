// PATH: core/src/main/java/com/ismail/kingdom/assets/AssetDescriptors.kt
package com.ismail.kingdom.assets

// Asset path constants for all game resources
object AssetDescriptors {

    // Base paths
    private const val TEXTURES_PATH = "textures/"
    const val UI_PATH = "${TEXTURES_PATH}ui/"
    private const val BUILDINGS_PATH = "textures/buildings/"
    private const val ERAS_PATH = "textures/eras/"
    private const val HEROES_PATH = "textures/heroes/"
    private const val EFFECTS_PATH = "textures/effects/"
    private const val FONTS_PATH = "fonts/"

    // Common UI icon paths
    const val COIN_ICON = "${UI_PATH}coin.png"
    const val CROWN_ICON = "${UI_PATH}crown.png"
    const val SETTINGS_ICON = "${UI_PATH}settings.png"

    // UI Assets
    object UI {
        const val HUD_BACKGROUND = "${UI_PATH}hud_background.png"
        const val BUTTON_GOLD = "${UI_PATH}button_gold.png"
        const val BUTTON_GREY = "${UI_PATH}button_grey.png"
        const val PANEL_BACKGROUND = "${UI_PATH}panel_background.png"
        const val COIN_SPRITE = "${UI_PATH}coin_sprite.png"
        const val PROGRESS_BAR_BG = "${UI_PATH}progress_bar_bg.png"
        const val PROGRESS_BAR_FILL = "${UI_PATH}progress_bar_fill.png"
    }

    // Building Assets (50 buildings across 5 eras)
    object Buildings {
        // Era 1 - Forest Village (10 buildings)
        const val ERA1_WHEAT_FARM = "${BUILDINGS_PATH}building_era1_wheat_farm.png"
        const val ERA1_CHICKEN_COOP = "${BUILDINGS_PATH}building_era1_chicken_coop.png"
        const val ERA1_LUMBER_MILL = "${BUILDINGS_PATH}building_era1_lumber_mill.png"
        const val ERA1_HUNTERS_LODGE = "${BUILDINGS_PATH}building_era1_hunters_lodge.png"
        const val ERA1_BLACKSMITH = "${BUILDINGS_PATH}building_era1_blacksmith.png"
        const val ERA1_TAVERN = "${BUILDINGS_PATH}building_era1_tavern.png"
        const val ERA1_MARKET = "${BUILDINGS_PATH}building_era1_market.png"
        const val ERA1_CHURCH = "${BUILDINGS_PATH}building_era1_church.png"
        const val ERA1_TOWN_HALL = "${BUILDINGS_PATH}building_era1_town_hall.png"
        const val ERA1_CASTLE = "${BUILDINGS_PATH}building_era1_castle.png"

        // Era 2 - Stone Town (10 buildings)
        const val ERA2_QUARRY = "${BUILDINGS_PATH}building_era2_quarry.png"
        const val ERA2_STONE_MASON = "${BUILDINGS_PATH}building_era2_stone_mason.png"
        const val ERA2_BANK = "${BUILDINGS_PATH}building_era2_bank.png"
        const val ERA2_LIBRARY = "${BUILDINGS_PATH}building_era2_library.png"
        const val ERA2_UNIVERSITY = "${BUILDINGS_PATH}building_era2_university.png"
        const val ERA2_CATHEDRAL = "${BUILDINGS_PATH}building_era2_cathedral.png"
        const val ERA2_MERCHANT_GUILD = "${BUILDINGS_PATH}building_era2_merchant_guild.png"
        const val ERA2_COURTHOUSE = "${BUILDINGS_PATH}building_era2_courthouse.png"
        const val ERA2_PALACE = "${BUILDINGS_PATH}building_era2_palace.png"
        const val ERA2_MONUMENT = "${BUILDINGS_PATH}building_era2_monument.png"

        // Era 3 - Iron Kingdom (10 buildings)
        const val ERA3_IRON_MINE = "${BUILDINGS_PATH}building_era3_iron_mine.png"
        const val ERA3_FOUNDRY = "${BUILDINGS_PATH}building_era3_foundry.png"
        const val ERA3_ARMORY = "${BUILDINGS_PATH}building_era3_armory.png"
        const val ERA3_BARRACKS = "${BUILDINGS_PATH}building_era3_barracks.png"
        const val ERA3_FORTRESS = "${BUILDINGS_PATH}building_era3_fortress.png"
        const val ERA3_WAR_FACTORY = "${BUILDINGS_PATH}building_era3_war_factory.png"
        const val ERA3_SIEGE_WORKSHOP = "${BUILDINGS_PATH}building_era3_siege_workshop.png"
        const val ERA3_MILITARY_ACADEMY = "${BUILDINGS_PATH}building_era3_military_academy.png"
        const val ERA3_CITADEL = "${BUILDINGS_PATH}building_era3_citadel.png"
        const val ERA3_IMPERIAL_FORTRESS = "${BUILDINGS_PATH}building_era3_imperial_fortress.png"

        // Era 4 - Mage Realm (10 buildings)
        const val ERA4_MANA_WELL = "${BUILDINGS_PATH}building_era4_mana_well.png"
        const val ERA4_ARCANE_LIBRARY = "${BUILDINGS_PATH}building_era4_arcane_library.png"
        const val ERA4_SPELL_FORGE = "${BUILDINGS_PATH}building_era4_spell_forge.png"
        const val ERA4_WIZARD_TOWER = "${BUILDINGS_PATH}building_era4_wizard_tower.png"
        const val ERA4_ENCHANTMENT_CHAMBER = "${BUILDINGS_PATH}building_era4_enchantment_chamber.png"
        const val ERA4_ALCHEMY_LAB = "${BUILDINGS_PATH}building_era4_alchemy_lab.png"
        const val ERA4_SUMMONING_CIRCLE = "${BUILDINGS_PATH}building_era4_summoning_circle.png"
        const val ERA4_ARCANE_NEXUS = "${BUILDINGS_PATH}building_era4_arcane_nexus.png"
        const val ERA4_MAGE_ACADEMY = "${BUILDINGS_PATH}building_era4_mage_academy.png"
        const val ERA4_ASTRAL_SPIRE = "${BUILDINGS_PATH}building_era4_astral_spire.png"

        // Era 5 - Legendary Empire (10 buildings)
        const val ERA5_COSMIC_SHRINE = "${BUILDINGS_PATH}building_era5_cosmic_shrine.png"
        const val ERA5_DIVINE_TEMPLE = "${BUILDINGS_PATH}building_era5_divine_temple.png"
        const val ERA5_CELESTIAL_FORGE = "${BUILDINGS_PATH}building_era5_celestial_forge.png"
        const val ERA5_TITAN_WORKSHOP = "${BUILDINGS_PATH}building_era5_titan_workshop.png"
        const val ERA5_MYTHIC_VAULT = "${BUILDINGS_PATH}building_era5_mythic_vault.png"
        const val ERA5_LEGENDARY_ARENA = "${BUILDINGS_PATH}building_era5_legendary_arena.png"
        const val ERA5_GODFORGE = "${BUILDINGS_PATH}building_era5_godforge.png"
        const val ERA5_PANTHEON = "${BUILDINGS_PATH}building_era5_pantheon.png"
        const val ERA5_WORLD_TREE = "${BUILDINGS_PATH}building_era5_world_tree.png"
        const val ERA5_THRONE_OF_ETERNITY = "${BUILDINGS_PATH}building_era5_throne_of_eternity.png"
    }

    // Era Background Assets
    object Eras {
        const val ERA1_BACKGROUND = "${ERAS_PATH}era1_background.png"
        const val ERA2_BACKGROUND = "${ERAS_PATH}era2_background.png"
        const val ERA3_BACKGROUND = "${ERAS_PATH}era3_background.png"
        const val ERA4_BACKGROUND = "${ERAS_PATH}era4_background.png"
        const val ERA5_BACKGROUND = "${ERAS_PATH}era5_background.png"
    }

    // Hero Assets
    object Heroes {
        const val MERLIN = "${HEROES_PATH}hero_merlin.png"
        const val KING_ARTHUR = "${HEROES_PATH}hero_arthur.png"
        const val GUINEVERE = "${HEROES_PATH}hero_guinevere.png"
        const val LANCELOT = "${HEROES_PATH}hero_lancelot.png"
        const val MORGANA = "${HEROES_PATH}hero_morgana.png"
        const val ROBIN_HOOD = "${HEROES_PATH}hero_robin_hood.png"
        const val JOAN_OF_ARC = "${HEROES_PATH}hero_joan_of_arc.png"
        const val HERCULES = "${HEROES_PATH}hero_hercules.png"
        const val CLEOPATRA = "${HEROES_PATH}hero_cleopatra.png"
        const val SUN_TZU = "${HEROES_PATH}hero_sun_tzu.png"
        const val TESLA = "${HEROES_PATH}hero_tesla.png"
        const val DA_VINCI = "${HEROES_PATH}hero_davinci.png"
    }

    // Effect Assets
    object Effects {
        const val COIN_PARTICLE = "${EFFECTS_PATH}coin_particle.png"
        const val SPARKLE_PARTICLE = "${EFFECTS_PATH}sparkle_particle.png"
        const val MILESTONE_BURST = "${EFFECTS_PATH}milestone_burst.png"
        const val CRITICAL_HIT = "${EFFECTS_PATH}critical_hit.png"
        const val LEVEL_UP = "${EFFECTS_PATH}level_up.png"
    }

    // Font Assets
    object Fonts {
        const val GOLD_LARGE = "${FONTS_PATH}font_gold_large.fnt"
        const val GOLD_SMALL = "${FONTS_PATH}font_gold_small.fnt"
        const val BODY = "${FONTS_PATH}font_body.fnt"
    }

    // Texture Atlas
    const val TEXTURE_ATLAS = "${TEXTURES_PATH}textures.atlas"
}
