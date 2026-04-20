// PATH: core/src/main/kotlin/com/kingdomtycoon/utils/Constants.kt
package com.kingdomtycoon.utils

// Central configuration for all game constants
object Constants {
    
    // Era names
    const val ERA_DIRT_VILLAGE = "Dirt Village"
    const val ERA_STONE_TOWN = "Stone Town"
    const val ERA_IRON_KINGDOM = "Iron Kingdom"
    const val ERA_MAGE_REALM = "Mage Realm"
    const val ERA_LEGENDARY_REALM = "Legendary Realm"
    
    val ERA_NAMES = listOf(
        ERA_DIRT_VILLAGE,
        ERA_STONE_TOWN,
        ERA_IRON_KINGDOM,
        ERA_MAGE_REALM,
        ERA_LEGENDARY_REALM
    )
    
    // Income constants
    const val BASE_TAP_INCOME = 1.0
    const val BASE_BUILDING_MULTIPLIER = 1.15 // Cost scaling per purchase
    
    // Era multipliers
    const val ERA_0_MULTIPLIER = 1.0
    const val ERA_1_MULTIPLIER = 10.0
    const val ERA_2_MULTIPLIER = 100.0
    const val ERA_3_MULTIPLIER = 1000.0
    const val ERA_4_MULTIPLIER = 10000.0
    
    val ERA_MULTIPLIERS = listOf(
        ERA_0_MULTIPLIER,
        ERA_1_MULTIPLIER,
        ERA_2_MULTIPLIER,
        ERA_3_MULTIPLIER,
        ERA_4_MULTIPLIER
    )
    
    // Building configuration
    const val BUILDINGS_PER_ERA = 10
    const val TOTAL_BUILDINGS = 50 // 5 eras × 10 buildings
    
    // Prestige system
    const val PRESTIGE_BASE_REQUIREMENT = 1_000_000.0
    const val PRESTIGE_MULTIPLIER = 10.0
    const val PRESTIGE_BONUS_PER_LEVEL = 0.1 // 10% bonus per prestige
    
    // Offline progress
    const val OFFLINE_CAP_HOURS = 8
    const val OFFLINE_CAP_SECONDS = OFFLINE_CAP_HOURS * 3600
    
    // Save system
    const val AUTOSAVE_INTERVAL = 30f // seconds
    const val SAVE_FILE_NAME = "kingdom_save.json"
    
    // Advisor system
    const val ADVISOR_BASE_COST = 10_000.0
    const val ADVISOR_COST_MULTIPLIER = 100.0
    const val ADVISOR_PURCHASE_INTERVAL = 10f // seconds
    
    // Quest system
    const val MAX_ACTIVE_QUESTS = 3
    const val QUEST_REFRESH_TIME = 3600 // 1 hour in seconds
    
    // Hero system
    const val MAX_HEROES = 5
    const val HERO_BASE_COST = 100_000.0
    const val HERO_INCOME_BONUS = 0.25 // 25% per hero
    
    // UI constants
    const val SCREEN_WIDTH = 1920f
    const val SCREEN_HEIGHT = 1080f
    const val UI_SCALE = 1f
    
    // Animation constants
    const val FLOATING_TEXT_DURATION = 0.8f
    const val FLOATING_TEXT_RISE_SPEED = 75f
    const val COIN_ANIMATION_DURATION = 0.5f
    
    // Ad rewards
    const val AD_REWARD_MULTIPLIER = 2.0 // 2x income for 30 seconds
    const val AD_REWARD_DURATION = 30f
    const val AD_COOLDOWN = 300f // 5 minutes
    
    // Map constants
    const val MAP_TILE_WIDTH = 128f
    const val MAP_TILE_HEIGHT = 64f
    const val MAP_SIZE = 10
    
    // Resource types
    const val RESOURCE_GOLD = "gold"
    const val RESOURCE_STONE = "stone"
    const val RESOURCE_IRON = "iron"
    const val RESOURCE_MANA = "mana"
    const val RESOURCE_GEMS = "gems"
    
    // Debug flags
    const val DEBUG_MODE = false
    const val SHOW_FPS = true
    const val FAST_INCOME = false // 10x income for testing
}
