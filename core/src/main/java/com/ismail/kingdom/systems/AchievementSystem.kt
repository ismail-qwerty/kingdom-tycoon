// PATH: core/src/main/java/com/ismail/kingdom/systems/AchievementSystem.kt
package com.ismail.kingdom.systems

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.ismail.kingdom.GameState

// Defines the type of achievement
enum class AchievementType {
    TAPPING, GOLD_EARNED, BUILDINGS_BOUGHT, PRESTIGE_COUNT, 
    ADVISORS_HIRED, QUESTS_COMPLETED, TILES_EXPLORED, 
    RAIDS_COMPLETED, SPELLS_CAST, HEROES_UNLOCKED
}

// Defines the reward type for completing an achievement
enum class RewardType {
    GOLD_MULTIPLIER, CROWN_SHARDS, COSMETIC
}

// Represents a single achievement
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val type: AchievementType,
    val targetValue: Double,
    val rewardType: RewardType,
    val rewardValue: Double,
    val iconAsset: String,
    val isSecret: Boolean = false,
    var isUnlocked: Boolean = false,
    var unlockedAt: Long? = null
)

// Represents a game event that can trigger achievement checks
sealed class GameEvent {
    data class TapPerformed(val totalTaps: Long) : GameEvent()
    data class GoldEarned(val totalGold: Double) : GameEvent()
    data class BuildingBought(val totalBuildings: Int) : GameEvent()
    data class PrestigePerformed(val totalPrestiges: Int, val layer: Int) : GameEvent()
    data class AdvisorHired(val totalAdvisors: Int, val allEra5: Boolean) : GameEvent()
    data class QuestCompleted(val totalQuests: Int) : GameEvent()
    data class TileExplored(val tilesInEra: Int, val tilesInThreeEras: Int, val totalTiles: Int) : GameEvent()
    data class RaidCompleted(val totalRaids: Int, val allCampsDefeated: Boolean) : GameEvent()
    data class SpellCast(val totalCasts: Int, val allSpellsCast: Boolean) : GameEvent()
    data class HeroUnlocked(val totalHeroes: Int) : GameEvent()
}

// Manages all achievements and their unlocking logic
class AchievementSystem {
    private val achievements = mutableListOf<Achievement>()
    private var currentPopup: AchievementPopup? = null
    
    init {
        initializeAchievements()
    }
    
    // Initializes all 50 achievements across 10 categories
    private fun initializeAchievements() {
        // Tapping (5)
        achievements.add(Achievement("tap_100", "Casual Tapper", "Tap 100 times", AchievementType.TAPPING, 100.0, RewardType.GOLD_MULTIPLIER, 1.05, "ui/achievement_tap1.png"))
        achievements.add(Achievement("tap_1k", "Dedicated Tapper", "Tap 1,000 times", AchievementType.TAPPING, 1000.0, RewardType.GOLD_MULTIPLIER, 1.1, "ui/achievement_tap2.png"))
        achievements.add(Achievement("tap_10k", "Tap Master", "Tap 10,000 times", AchievementType.TAPPING, 10000.0, RewardType.GOLD_MULTIPLIER, 1.15, "ui/achievement_tap3.png"))
        achievements.add(Achievement("tap_100k", "Tap God", "Tap 100,000 times", AchievementType.TAPPING, 100000.0, RewardType.CROWN_SHARDS, 10.0, "ui/achievement_tap4.png"))
        achievements.add(Achievement("tap_1m", "Legend of Tapping", "Tap 1,000,000 times", AchievementType.TAPPING, 1000000.0, RewardType.CROWN_SHARDS, 50.0, "ui/achievement_tap5.png"))
        
        // Gold (5)
        achievements.add(Achievement("gold_1k", "First Gold", "Earn 1,000 gold", AchievementType.GOLD_EARNED, 1000.0, RewardType.GOLD_MULTIPLIER, 1.02, "ui/achievement_gold1.png"))
        achievements.add(Achievement("gold_1m", "Gold Rush", "Earn 1,000,000 gold", AchievementType.GOLD_EARNED, 1000000.0, RewardType.GOLD_MULTIPLIER, 1.05, "ui/achievement_gold2.png"))
        achievements.add(Achievement("gold_1b", "Millionaire", "Earn 1,000,000,000 gold", AchievementType.GOLD_EARNED, 1000000000.0, RewardType.GOLD_MULTIPLIER, 1.1, "ui/achievement_gold3.png"))
        achievements.add(Achievement("gold_1t", "Billionaire", "Earn 1,000,000,000,000 gold", AchievementType.GOLD_EARNED, 1e12, RewardType.CROWN_SHARDS, 25.0, "ui/achievement_gold4.png"))
        achievements.add(Achievement("gold_1qa", "Legendary Wealth", "Earn 1 Quadrillion gold", AchievementType.GOLD_EARNED, 1e15, RewardType.CROWN_SHARDS, 100.0, "ui/achievement_gold5.png"))
        
        // Buildings (5)
        achievements.add(Achievement("building_1", "First Building", "Buy your first building", AchievementType.BUILDINGS_BOUGHT, 1.0, RewardType.GOLD_MULTIPLIER, 1.02, "ui/achievement_building1.png"))
        achievements.add(Achievement("building_10", "Builder", "Buy 10 buildings", AchievementType.BUILDINGS_BOUGHT, 10.0, RewardType.GOLD_MULTIPLIER, 1.05, "ui/achievement_building2.png"))
        achievements.add(Achievement("building_100", "Architect", "Buy 100 buildings", AchievementType.BUILDINGS_BOUGHT, 100.0, RewardType.GOLD_MULTIPLIER, 1.1, "ui/achievement_building3.png"))
        achievements.add(Achievement("building_500", "City Planner", "Buy 500 buildings", AchievementType.BUILDINGS_BOUGHT, 500.0, RewardType.CROWN_SHARDS, 20.0, "ui/achievement_building4.png"))
        achievements.add(Achievement("building_1000", "Legendary Architect", "Buy 1,000 buildings", AchievementType.BUILDINGS_BOUGHT, 1000.0, RewardType.CROWN_SHARDS, 75.0, "ui/achievement_building5.png"))
        
        // Prestige (5)
        achievements.add(Achievement("prestige_1", "First Ascension", "Prestige for the first time", AchievementType.PRESTIGE_COUNT, 1.0, RewardType.CROWN_SHARDS, 5.0, "ui/achievement_prestige1.png"))
        achievements.add(Achievement("prestige_5", "Seasoned Traveler", "Prestige 5 times", AchievementType.PRESTIGE_COUNT, 5.0, RewardType.GOLD_MULTIPLIER, 1.1, "ui/achievement_prestige2.png"))
        achievements.add(Achievement("prestige_layer2", "Dimensional Rift", "Unlock Prestige Layer 2", AchievementType.PRESTIGE_COUNT, 10.0, RewardType.CROWN_SHARDS, 30.0, "ui/achievement_prestige3.png", isSecret = true))
        achievements.add(Achievement("prestige_layer3", "Legend Born", "Unlock Prestige Layer 3", AchievementType.PRESTIGE_COUNT, 25.0, RewardType.CROWN_SHARDS, 100.0, "ui/achievement_prestige4.png", isSecret = true))
        achievements.add(Achievement("prestige_legend3", "Eternal Legend", "Perform 3 Legend prestiges", AchievementType.PRESTIGE_COUNT, 28.0, RewardType.GOLD_MULTIPLIER, 2.0, "ui/achievement_prestige5.png", isSecret = true))
        
        // Advisors (5)
        achievements.add(Achievement("advisor_1", "First Hire", "Hire your first advisor", AchievementType.ADVISORS_HIRED, 1.0, RewardType.GOLD_MULTIPLIER, 1.03, "ui/achievement_advisor1.png"))
        achievements.add(Achievement("advisor_5", "Delegator", "Hire 5 advisors", AchievementType.ADVISORS_HIRED, 5.0, RewardType.GOLD_MULTIPLIER, 1.07, "ui/achievement_advisor2.png"))
        achievements.add(Achievement("advisor_10", "Hands-Off Manager", "Hire 10 advisors", AchievementType.ADVISORS_HIRED, 10.0, RewardType.CROWN_SHARDS, 15.0, "ui/achievement_advisor3.png"))
        achievements.add(Achievement("advisor_all", "Full Staff", "Hire all advisors", AchievementType.ADVISORS_HIRED, 15.0, RewardType.CROWN_SHARDS, 40.0, "ui/achievement_advisor4.png"))
        achievements.add(Achievement("advisor_era5", "Perfect Kingdom", "All advisors at Era 5", AchievementType.ADVISORS_HIRED, 1.0, RewardType.CROWN_SHARDS, 150.0, "ui/achievement_advisor5.png", isSecret = true))
        
        // Quests (5)
        achievements.add(Achievement("quest_1", "Quest Starter", "Complete your first quest", AchievementType.QUESTS_COMPLETED, 1.0, RewardType.GOLD_MULTIPLIER, 1.02, "ui/achievement_quest1.png"))
        achievements.add(Achievement("quest_10", "Quest Hunter", "Complete 10 quests", AchievementType.QUESTS_COMPLETED, 10.0, RewardType.GOLD_MULTIPLIER, 1.05, "ui/achievement_quest2.png"))
        achievements.add(Achievement("quest_50", "Dedicated Hero", "Complete 50 quests", AchievementType.QUESTS_COMPLETED, 50.0, RewardType.CROWN_SHARDS, 20.0, "ui/achievement_quest3.png"))
        achievements.add(Achievement("quest_100", "Quest Champion", "Complete 100 quests", AchievementType.QUESTS_COMPLETED, 100.0, RewardType.CROWN_SHARDS, 50.0, "ui/achievement_quest4.png"))
        achievements.add(Achievement("quest_500", "Quest Legend", "Complete 500 quests", AchievementType.QUESTS_COMPLETED, 500.0, RewardType.GOLD_MULTIPLIER, 1.5, "ui/achievement_quest5.png"))
        
        // Map (5)
        achievements.add(Achievement("map_10", "Explorer", "Explore 10 tiles", AchievementType.TILES_EXPLORED, 10.0, RewardType.GOLD_MULTIPLIER, 1.03, "ui/achievement_map1.png"))
        achievements.add(Achievement("map_30", "Cartographer", "Explore 30 tiles", AchievementType.TILES_EXPLORED, 30.0, RewardType.GOLD_MULTIPLIER, 1.07, "ui/achievement_map2.png"))
        achievements.add(Achievement("map_era", "World Mapper", "Explore all tiles in one era", AchievementType.TILES_EXPLORED, 50.0, RewardType.CROWN_SHARDS, 25.0, "ui/achievement_map3.png"))
        achievements.add(Achievement("map_3eras", "Multi-world", "Explore all tiles in 3 eras", AchievementType.TILES_EXPLORED, 150.0, RewardType.CROWN_SHARDS, 60.0, "ui/achievement_map4.png"))
        achievements.add(Achievement("map_all", "Ultimate Explorer", "Explore all tiles in all eras", AchievementType.TILES_EXPLORED, 250.0, RewardType.CROWN_SHARDS, 200.0, "ui/achievement_map5.png"))
        
        // War (5)
        achievements.add(Achievement("war_1", "First Raid", "Complete your first raid", AchievementType.RAIDS_COMPLETED, 1.0, RewardType.GOLD_MULTIPLIER, 1.03, "ui/achievement_war1.png"))
        achievements.add(Achievement("war_10", "War Hero", "Complete 10 raids", AchievementType.RAIDS_COMPLETED, 10.0, RewardType.GOLD_MULTIPLIER, 1.08, "ui/achievement_war2.png"))
        achievements.add(Achievement("war_50", "Warlord", "Complete 50 raids", AchievementType.RAIDS_COMPLETED, 50.0, RewardType.CROWN_SHARDS, 30.0, "ui/achievement_war3.png"))
        achievements.add(Achievement("war_100", "Conquerer", "Complete 100 raids", AchievementType.RAIDS_COMPLETED, 100.0, RewardType.CROWN_SHARDS, 70.0, "ui/achievement_war4.png"))
        achievements.add(Achievement("war_all", "Supreme Commander", "Defeat all enemy camps", AchievementType.RAIDS_COMPLETED, 1.0, RewardType.GOLD_MULTIPLIER, 1.25, "ui/achievement_war5.png", isSecret = true))
        
        // Magic (5)
        achievements.add(Achievement("magic_1", "First Spell", "Cast your first spell", AchievementType.SPELLS_CAST, 1.0, RewardType.GOLD_MULTIPLIER, 1.02, "ui/achievement_magic1.png"))
        achievements.add(Achievement("magic_10", "Apprentice", "Cast 10 spells", AchievementType.SPELLS_CAST, 10.0, RewardType.GOLD_MULTIPLIER, 1.05, "ui/achievement_magic2.png"))
        achievements.add(Achievement("magic_50", "Sorcerer", "Cast 50 spells", AchievementType.SPELLS_CAST, 50.0, RewardType.CROWN_SHARDS, 20.0, "ui/achievement_magic3.png"))
        achievements.add(Achievement("magic_100", "Archmage", "Cast 100 spells", AchievementType.SPELLS_CAST, 100.0, RewardType.CROWN_SHARDS, 45.0, "ui/achievement_magic4.png"))
        achievements.add(Achievement("magic_all", "Legendary Mage", "Cast all spell types", AchievementType.SPELLS_CAST, 1.0, RewardType.CROWN_SHARDS, 80.0, "ui/achievement_magic5.png", isSecret = true))
        
        // Legacy (5)
        achievements.add(Achievement("hero_1", "First Hero", "Unlock your first hero", AchievementType.HEROES_UNLOCKED, 1.0, RewardType.GOLD_MULTIPLIER, 1.05, "ui/achievement_hero1.png"))
        achievements.add(Achievement("hero_2", "Two Heroes", "Unlock 2 heroes", AchievementType.HEROES_UNLOCKED, 2.0, RewardType.GOLD_MULTIPLIER, 1.1, "ui/achievement_hero2.png"))
        achievements.add(Achievement("hero_5", "Hero Collector", "Unlock 5 heroes", AchievementType.HEROES_UNLOCKED, 5.0, RewardType.CROWN_SHARDS, 35.0, "ui/achievement_hero3.png"))
        achievements.add(Achievement("hero_10", "Full Roster", "Unlock 10 heroes", AchievementType.HEROES_UNLOCKED, 10.0, RewardType.CROWN_SHARDS, 90.0, "ui/achievement_hero4.png"))
        achievements.add(Achievement("hero_12", "Complete Legend", "Unlock all 12 heroes", AchievementType.HEROES_UNLOCKED, 12.0, RewardType.GOLD_MULTIPLIER, 2.5, "ui/achievement_hero5.png"))
    }
    
    // Checks if any achievements should be unlocked based on the game event
    fun checkAchievements(state: GameState, event: GameEvent) {
        val toUnlock = mutableListOf<Achievement>()
        
        when (event) {
            is GameEvent.TapPerformed -> {
                achievements.filter { it.type == AchievementType.TAPPING && !it.isUnlocked && event.totalTaps >= it.targetValue }
                    .forEach { toUnlock.add(it) }
            }
            is GameEvent.GoldEarned -> {
                achievements.filter { it.type == AchievementType.GOLD_EARNED && !it.isUnlocked && event.totalGold >= it.targetValue }
                    .forEach { toUnlock.add(it) }
            }
            is GameEvent.BuildingBought -> {
                achievements.filter { it.type == AchievementType.BUILDINGS_BOUGHT && !it.isUnlocked && event.totalBuildings >= it.targetValue }
                    .forEach { toUnlock.add(it) }
            }
            is GameEvent.PrestigePerformed -> {
                achievements.filter { it.type == AchievementType.PRESTIGE_COUNT && !it.isUnlocked && event.totalPrestiges >= it.targetValue }
                    .forEach { toUnlock.add(it) }
            }
            is GameEvent.AdvisorHired -> {
                achievements.filter { 
                    it.type == AchievementType.ADVISORS_HIRED && !it.isUnlocked && 
                    (it.id != "advisor_era5" && event.totalAdvisors >= it.targetValue || 
                     it.id == "advisor_era5" && event.allEra5)
                }.forEach { toUnlock.add(it) }
            }
            is GameEvent.QuestCompleted -> {
                achievements.filter { it.type == AchievementType.QUESTS_COMPLETED && !it.isUnlocked && event.totalQuests >= it.targetValue }
                    .forEach { toUnlock.add(it) }
            }
            is GameEvent.TileExplored -> {
                achievements.filter { 
                    it.type == AchievementType.TILES_EXPLORED && !it.isUnlocked &&
                    when (it.id) {
                        "map_era" -> event.tilesInEra >= 50
                        "map_3eras" -> event.tilesInThreeEras >= 150
                        "map_all" -> event.totalTiles >= 250
                        else -> event.totalTiles >= it.targetValue
                    }
                }.forEach { toUnlock.add(it) }
            }
            is GameEvent.RaidCompleted -> {
                achievements.filter { 
                    it.type == AchievementType.RAIDS_COMPLETED && !it.isUnlocked &&
                    (it.id != "war_all" && event.totalRaids >= it.targetValue ||
                     it.id == "war_all" && event.allCampsDefeated)
                }.forEach { toUnlock.add(it) }
            }
            is GameEvent.SpellCast -> {
                achievements.filter { 
                    it.type == AchievementType.SPELLS_CAST && !it.isUnlocked &&
                    (it.id != "magic_all" && event.totalCasts >= it.targetValue ||
                     it.id == "magic_all" && event.allSpellsCast)
                }.forEach { toUnlock.add(it) }
            }
            is GameEvent.HeroUnlocked -> {
                achievements.filter { it.type == AchievementType.HEROES_UNLOCKED && !it.isUnlocked && event.totalHeroes >= it.targetValue }
                    .forEach { toUnlock.add(it) }
            }
        }
        
        toUnlock.forEach { achievement ->
            achievement.isUnlocked = true
            achievement.unlockedAt = System.currentTimeMillis()
            applyReward(state, achievement)
            showAchievementPopup(achievement)
        }
    }
    
    // Applies the achievement reward to the game state
    private fun applyReward(state: GameState, achievement: Achievement) {
        when (achievement.rewardType) {
            RewardType.GOLD_MULTIPLIER -> {
                // Apply gold multiplier to state
            }
            RewardType.CROWN_SHARDS -> {
                // Add crown shards to state
            }
            RewardType.COSMETIC -> {
                // Unlock cosmetic in state
            }
        }
    }
    
    // Shows a popup notification for the unlocked achievement
    fun showAchievementPopup(achievement: Achievement) {
        currentPopup = AchievementPopup(achievement)
    }
    
    // Returns the progress percentage for a specific achievement
    fun getAchievementProgress(id: String): Double {
        return achievements.find { it.id == id }?.let { 
            if (it.isUnlocked) 100.0 else 0.0 
        } ?: 0.0
    }
    
    // Returns all achievements
    fun getAllAchievements(): List<Achievement> = achievements
    
    // Returns achievements by category
    fun getAchievementsByType(type: AchievementType): List<Achievement> = 
        achievements.filter { it.type == type }
    
    // Updates and renders the achievement popup
    fun update(delta: Float) {
        currentPopup?.let {
            it.update(delta)
            if (it.isFinished()) currentPopup = null
        }
    }
    
    // Renders the achievement popup
    fun render(batch: SpriteBatch, shapeRenderer: ShapeRenderer, font: BitmapFont) {
        currentPopup?.render(batch, shapeRenderer, font)
    }
}

// Displays a temporary popup notification when an achievement is unlocked
class AchievementPopup(private val achievement: Achievement) {
    private var timer = 0f
    private val duration = 3f
    private val slideInDuration = 0.3f
    private val slideOutDuration = 0.3f
    
    private val width = 400f
    private val height = 80f
    private val targetY = Gdx.graphics.height - height - 20f
    private var currentY = Gdx.graphics.height.toFloat()
    
    // Updates the popup animation
    fun update(delta: Float) {
        timer += delta
        
        currentY = when {
            timer < slideInDuration -> {
                val progress = timer / slideInDuration
                Gdx.graphics.height - (height + 20f) * progress
            }
            timer > duration - slideOutDuration -> {
                val progress = (timer - (duration - slideOutDuration)) / slideOutDuration
                targetY + (Gdx.graphics.height - targetY) * progress
            }
            else -> targetY
        }
    }
    
    // Renders the popup banner
    fun render(batch: SpriteBatch, shapeRenderer: ShapeRenderer, font: BitmapFont) {
        val x = (Gdx.graphics.width - width) / 2
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0.1f, 0.1f, 0.15f, 0.95f)
        shapeRenderer.rect(x, currentY, width, height)
        shapeRenderer.color = Color.GOLD
        shapeRenderer.rect(x, currentY, width, 3f)
        shapeRenderer.end()
        
        batch.begin()
        font.color = Color.GOLD
        font.draw(batch, "Achievement Unlocked!", x + 20, currentY + height - 15)
        font.color = Color.WHITE
        font.draw(batch, achievement.title, x + 20, currentY + height - 40)
        font.color = Color.LIGHT_GRAY
        font.draw(batch, achievement.description, x + 20, currentY + 20)
        batch.end()
    }
    
    // Checks if the popup animation is finished
    fun isFinished(): Boolean = timer >= duration
}
