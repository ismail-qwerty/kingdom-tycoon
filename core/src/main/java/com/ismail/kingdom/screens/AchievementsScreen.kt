// PATH: core/src/main/java/com/ismail/kingdom/screens/AchievementsScreen.kt
package com.ismail.kingdom.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.ismail.kingdom.GameState
import com.ismail.kingdom.systems.Achievement
import com.ismail.kingdom.systems.AchievementSystem
import com.ismail.kingdom.systems.AchievementType

// Displays all achievements in a scrollable grid with category filtering
class AchievementsScreen(
    private val gameState: GameState,
    private val achievementSystem: AchievementSystem,
    private val onBack: () -> Unit
) : Screen {
    
    private val batch = SpriteBatch()
    private val shapeRenderer = ShapeRenderer()
    private val font = BitmapFont()
    
    private var selectedCategory: AchievementType? = null
    private var scrollOffset = 0f
    private val maxScroll get() = (getFilteredAchievements().size / 2 * 180f).coerceAtLeast(0f)
    
    private val categoryButtons = mutableListOf<CategoryButton>()
    private val achievementCards = mutableListOf<AchievementCard>()
    
    init {
        setupCategoryButtons()
    }
    
    // Creates category filter buttons at the top
    private fun setupCategoryButtons() {
        val categories = listOf(
            null to "All",
            AchievementType.TAPPING to "Tapping",
            AchievementType.GOLD_EARNED to "Gold",
            AchievementType.BUILDINGS_BOUGHT to "Buildings",
            AchievementType.PRESTIGE_COUNT to "Prestige",
            AchievementType.ADVISORS_HIRED to "Advisors",
            AchievementType.QUESTS_COMPLETED to "Quests",
            AchievementType.TILES_EXPLORED to "Map",
            AchievementType.RAIDS_COMPLETED to "War",
            AchievementType.SPELLS_CAST to "Magic",
            AchievementType.HEROES_UNLOCKED to "Legacy"
        )
        
        val buttonWidth = 120f
        val buttonHeight = 50f
        val spacing = 10f
        val startX = 20f
        var currentX = startX
        var currentY = Gdx.graphics.height - 80f
        
        categories.forEach { (type, label) ->
            if (currentX + buttonWidth > Gdx.graphics.width - 20f) {
                currentX = startX
                currentY -= buttonHeight + spacing
            }
            
            categoryButtons.add(CategoryButton(type, label, currentX, currentY, buttonWidth, buttonHeight))
            currentX += buttonWidth + spacing
        }
    }
    
    // Returns achievements filtered by selected category
    private fun getFilteredAchievements(): List<Achievement> {
        return if (selectedCategory == null) {
            achievementSystem.getAllAchievements()
        } else {
            achievementSystem.getAchievementsByType(selectedCategory!!)
        }
    }
    
    // Updates achievement card positions based on scroll
    private fun updateAchievementCards() {
        achievementCards.clear()
        val achievements = getFilteredAchievements()
        
        val cardWidth = 350f
        val cardHeight = 150f
        val spacing = 20f
        val columns = 2
        val startX = (Gdx.graphics.width - (cardWidth * columns + spacing)) / 2
        val startY = Gdx.graphics.height - 200f
        
        achievements.forEachIndexed { index, achievement ->
            val col = index % columns
            val row = index / columns
            
            val x = startX + col * (cardWidth + spacing)
            val y = startY - row * (cardHeight + spacing) + scrollOffset
            
            achievementCards.add(AchievementCard(achievement, x, y, cardWidth, cardHeight))
        }
    }
    
    override fun show() {}
    
    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        
        handleInput()
        updateAchievementCards()
        
        renderBackground()
        renderCategoryButtons()
        renderAchievementCards()
        renderHeader()
        renderBackButton()
    }
    
    // Handles touch input for scrolling and button clicks
    private fun handleInput() {
        if (Gdx.input.justTouched()) {
            val touchX = Gdx.input.x.toFloat()
            val touchY = Gdx.graphics.height - Gdx.input.y.toFloat()
            
            // Check back button
            if (touchX < 100f && touchY > Gdx.graphics.height - 60f) {
                onBack()
                return
            }
            
            // Check category buttons
            categoryButtons.forEach { button ->
                if (button.contains(touchX, touchY)) {
                    selectedCategory = button.category
                    scrollOffset = 0f
                }
            }
        }
        
        // Handle scrolling
        if (Gdx.input.isTouched) {
            val deltaY = Gdx.input.deltaY.toFloat()
            scrollOffset = (scrollOffset - deltaY * 2f).coerceIn(-maxScroll, 0f)
        }
    }
    
    // Renders the background
    private fun renderBackground() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0.05f, 0.05f, 0.1f, 1f)
        shapeRenderer.rect(0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        shapeRenderer.end()
    }
    
    // Renders the header title
    private fun renderHeader() {
        batch.begin()
        font.color = Color.GOLD
        font.data.setScale(2f)
        font.draw(batch, "Achievements", Gdx.graphics.width / 2f - 100f, Gdx.graphics.height - 20f)
        font.data.setScale(1f)
        
        val unlocked = achievementSystem.getAllAchievements().count { it.isUnlocked }
        val total = achievementSystem.getAllAchievements().size
        font.color = Color.WHITE
        font.draw(batch, "$unlocked / $total Unlocked", Gdx.graphics.width / 2f - 80f, Gdx.graphics.height - 50f)
        batch.end()
    }
    
    // Renders the back button
    private fun renderBackButton() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0.2f, 0.2f, 0.3f, 1f)
        shapeRenderer.rect(20f, Gdx.graphics.height - 60f, 80f, 40f)
        shapeRenderer.end()
        
        batch.begin()
        font.color = Color.WHITE
        font.draw(batch, "Back", 40f, Gdx.graphics.height - 30f)
        batch.end()
    }
    
    // Renders category filter buttons
    private fun renderCategoryButtons() {
        categoryButtons.forEach { button ->
            val isSelected = button.category == selectedCategory
            
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            shapeRenderer.color = if (isSelected) Color(0.3f, 0.5f, 0.8f, 1f) else Color(0.2f, 0.2f, 0.3f, 1f)
            shapeRenderer.rect(button.x, button.y, button.width, button.height)
            shapeRenderer.end()
            
            batch.begin()
            font.color = if (isSelected) Color.WHITE else Color.LIGHT_GRAY
            font.draw(batch, button.label, button.x + 10f, button.y + button.height / 2 + 5f)
            batch.end()
        }
    }
    
    // Renders achievement cards in a grid
    private fun renderAchievementCards() {
        achievementCards.forEach { card ->
            if (card.y + card.height < 0 || card.y > Gdx.graphics.height) return@forEach
            
            val achievement = card.achievement
            val isUnlocked = achievement.isUnlocked
            val bgColor = if (isUnlocked) Color(0.15f, 0.25f, 0.15f, 1f) else Color(0.15f, 0.15f, 0.2f, 1f)
            
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            shapeRenderer.color = bgColor
            shapeRenderer.rect(card.x, card.y, card.width, card.height)
            
            if (isUnlocked) {
                shapeRenderer.color = Color.GOLD
                shapeRenderer.rect(card.x, card.y + card.height - 3f, card.width, 3f)
            }
            shapeRenderer.end()
            
            batch.begin()
            font.color = if (isUnlocked) Color.GOLD else Color.GRAY
            font.draw(batch, achievement.title, card.x + 15f, card.y + card.height - 20f)
            
            font.color = Color.LIGHT_GRAY
            val description = if (achievement.isSecret && !isUnlocked) "???" else achievement.description
            font.draw(batch, description, card.x + 15f, card.y + card.height - 50f)
            
            font.color = Color.CYAN
            val rewardText = when (achievement.rewardType) {
                com.ismail.kingdom.systems.RewardType.GOLD_MULTIPLIER -> "Gold x${achievement.rewardValue}"
                com.ismail.kingdom.systems.RewardType.CROWN_SHARDS -> "+${achievement.rewardValue.toInt()} Shards"
                com.ismail.kingdom.systems.RewardType.COSMETIC -> "Cosmetic Unlock"
            }
            font.draw(batch, "Reward: $rewardText", card.x + 15f, card.y + 30f)
            
            if (isUnlocked) {
                font.color = Color.GREEN
                font.draw(batch, "✓ UNLOCKED", card.x + card.width - 120f, card.y + 30f)
            }
            batch.end()
        }
    }
    
    override fun resize(width: Int, height: Int) {}
    override fun pause() {}
    override fun resume() {}
    override fun hide() {}
    
    override fun dispose() {
        batch.dispose()
        shapeRenderer.dispose()
        font.dispose()
    }
}

// Represents a category filter button
data class CategoryButton(
    val category: AchievementType?,
    val label: String,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
) {
    // Checks if a point is inside the button
    fun contains(px: Float, py: Float): Boolean {
        return px >= x && px <= x + width && py >= y && py <= y + height
    }
}

// Represents an achievement card in the grid
data class AchievementCard(
    val achievement: Achievement,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
)
