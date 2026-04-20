// PATH: core/src/main/java/com/ismail/kingdom/screens/StatisticsScreen.kt
package com.ismail.kingdom.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.ismail.kingdom.models.GameState
import com.ismail.kingdom.systems.StatisticsTracker

// Tab types for statistics screen
enum class StatTab {
    GENERAL, INCOME, BUILDINGS, PRESTIGE
}

// Displays comprehensive game statistics with tabbed interface
class StatisticsScreen(
    private val gameState: GameState,
    private val statsTracker: StatisticsTracker,
    private val onBack: () -> Unit
) : Screen {
    
    private val batch = SpriteBatch()
    private val shapeRenderer = ShapeRenderer()
    private val font = BitmapFont()
    
    private var currentTab = StatTab.GENERAL
    private val tabButtons = mutableListOf<TabButton>()
    
    init {
        setupTabButtons()
    }
    
    // Creates tab buttons at the top
    private fun setupTabButtons() {
        val tabs = listOf(
            StatTab.GENERAL to "General",
            StatTab.INCOME to "Income",
            StatTab.BUILDINGS to "Buildings",
            StatTab.PRESTIGE to "Prestige"
        )
        
        val tabWidth = 150f
        val tabHeight = 50f
        val spacing = 10f
        val totalWidth = tabs.size * tabWidth + (tabs.size - 1) * spacing
        val startX = (Gdx.graphics.width - totalWidth) / 2
        val y = Gdx.graphics.height - 120f
        
        tabs.forEachIndexed { index, (tab, label) ->
            val x = startX + index * (tabWidth + spacing)
            tabButtons.add(TabButton(tab, label, x, y, tabWidth, tabHeight))
        }
    }
    
    override fun show() {}
    
    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        
        handleInput()
        
        renderBackground()
        renderHeader()
        renderTabs()
        renderTabContent()
        renderBackButton()
    }
    
    // Handles touch input for tabs and back button
    private fun handleInput() {
        if (Gdx.input.justTouched()) {
            val touchX = Gdx.input.x.toFloat()
            val touchY = Gdx.graphics.height - Gdx.input.y.toFloat()
            
            // Check back button
            if (touchX < 100f && touchY > Gdx.graphics.height - 60f) {
                onBack()
                return
            }
            
            // Check tab buttons
            tabButtons.forEach { button ->
                if (button.contains(touchX, touchY)) {
                    currentTab = button.tab
                }
            }
        }
    }
    
    // Renders background
    private fun renderBackground() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0.05f, 0.05f, 0.1f, 1f)
        shapeRenderer.rect(0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        shapeRenderer.end()
    }
    
    // Renders header title
    private fun renderHeader() {
        batch.begin()
        font.color = Color.CYAN
        font.data.setScale(2f)
        font.draw(batch, "Statistics", Gdx.graphics.width / 2f - 80f, Gdx.graphics.height - 20f)
        font.data.setScale(1f)
        batch.end()
    }
    
    // Renders back button
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
    
    // Renders tab buttons
    private fun renderTabs() {
        tabButtons.forEach { button ->
            val isSelected = button.tab == currentTab
            
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            shapeRenderer.color = if (isSelected) Color(0.3f, 0.5f, 0.7f, 1f) else Color(0.2f, 0.2f, 0.3f, 1f)
            shapeRenderer.rect(button.x, button.y, button.width, button.height)
            
            if (isSelected) {
                shapeRenderer.color = Color.CYAN
                shapeRenderer.rect(button.x, button.y + button.height - 3f, button.width, 3f)
            }
            shapeRenderer.end()
            
            batch.begin()
            font.color = if (isSelected) Color.WHITE else Color.LIGHT_GRAY
            font.draw(batch, button.label, button.x + 30f, button.y + 30f)
            batch.end()
        }
    }
    
    // Renders content based on selected tab
    private fun renderTabContent() {
        when (currentTab) {
            StatTab.GENERAL -> renderGeneralTab()
            StatTab.INCOME -> renderIncomeTab()
            StatTab.BUILDINGS -> renderBuildingsTab()
            StatTab.PRESTIGE -> renderPrestigeTab()
        }
    }
    
    // Renders general statistics tab
    private fun renderGeneralTab() {
        val startY = Gdx.graphics.height - 200f
        val lineHeight = 35f
        
        batch.begin()
        font.color = Color.GOLD
        font.draw(batch, "General Statistics", 50f, startY)
        
        font.color = Color.WHITE
        var y = startY - 50f
        
        font.draw(batch, "Total Playtime: ${statsTracker.getFormattedPlaytime()}", 50f, y)
        y -= lineHeight
        
        font.draw(batch, "Session Time: ${statsTracker.getFormattedSessionTime()}", 50f, y)
        y -= lineHeight
        
        font.draw(batch, "Total Gold Earned: ${formatNumber(statsTracker.stats.totalGoldEarned)}", 50f, y)
        y -= lineHeight
        
        font.draw(batch, "Total Taps: ${formatNumber(statsTracker.stats.totalTaps.toDouble())}", 50f, y)
        y -= lineHeight
        
        font.draw(batch, "Total Buildings Bought: ${statsTracker.stats.totalBuildingsBought}", 50f, y)
        y -= lineHeight
        
        font.draw(batch, "Total Quests Completed: ${statsTracker.stats.totalQuestsCompleted}", 50f, y)
        y -= lineHeight
        
        font.draw(batch, "Map Tiles Revealed: ${statsTracker.stats.totalMapTilesRevealed}", 50f, y)
        y -= lineHeight
        
        font.draw(batch, "Ads Watched: ${statsTracker.stats.totalAdsWatched}", 50f, y)
        y -= lineHeight
        
        font.color = Color.CYAN
        font.draw(batch, "Favorite Building: ${getBuildingDisplayName(statsTracker.stats.favoriteBuilding)}", 50f, y)
        
        batch.end()
    }
    
    // Renders income statistics tab with bar chart
    private fun renderIncomeTab() {
        val startY = Gdx.graphics.height - 200f
        
        batch.begin()
        font.color = Color.GOLD
        font.draw(batch, "Income Statistics", 50f, startY)
        
        font.color = Color.WHITE
        var y = startY - 50f
        
        val currentIPS = statsTracker.stats.highestIPS
        font.draw(batch, "Current IPS: ${formatNumber(currentIPS)}/s", 50f, y)
        y -= 35f
        
        font.draw(batch, "Best IPS Ever: ${formatNumber(statsTracker.stats.highestIPS)}/s", 50f, y)
        y -= 35f
        
        font.color = Color.CYAN
        font.draw(batch, "Income Breakdown:", 50f, y)
        
        batch.end()
        
        // Render income breakdown bar chart
        renderIncomeChart(y - 50f)
    }
    
    // Renders income breakdown as a bar chart
    private fun renderIncomeChart(startY: Float) {
        val breakdown = statsTracker.getIncomeBreakdown()
        if (breakdown.isEmpty()) return
        
        val chartX = 50f
        val chartWidth = Gdx.graphics.width - 100f
        val barHeight = 30f
        val spacing = 10f
        val maxIncome = breakdown.values.maxOrNull() ?: 1.0
        
        breakdown.entries.forEachIndexed { index, (buildingId, income) ->
            val y = startY - index * (barHeight + spacing)
            val barWidth = (income / maxIncome * (chartWidth - 200f)).toFloat()
            
            // Draw bar
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            shapeRenderer.color = getEraColor(buildingId)
            shapeRenderer.rect(chartX + 150f, y, barWidth, barHeight)
            shapeRenderer.end()
            
            // Draw labels
            batch.begin()
            font.color = Color.WHITE
            font.draw(batch, getBuildingDisplayName(buildingId), chartX, y + 20f)
            
            font.color = Color.CYAN
            font.draw(batch, "${formatNumber(income)}/s", chartX + 160f + barWidth, y + 20f)
            batch.end()
        }
    }
    
    // Renders buildings statistics tab
    private fun renderBuildingsTab() {
        val startY = Gdx.graphics.height - 200f
        
        batch.begin()
        font.color = Color.GOLD
        font.draw(batch, "Building Statistics", 50f, startY)
        
        font.color = Color.WHITE
        var y = startY - 50f
        
        font.draw(batch, "Total Buildings Bought: ${statsTracker.stats.totalBuildingsBought}", 50f, y)
        y -= 35f
        
        font.draw(batch, "Total Gold Spent: ${formatNumber(statsTracker.stats.totalGoldSpentOnBuildings)}", 50f, y)
        y -= 35f
        
        font.draw(batch, "Gold on Advisors: ${formatNumber(statsTracker.stats.totalGoldSpentOnAdvisors)}", 50f, y)
        y -= 50f
        
        font.color = Color.CYAN
        font.draw(batch, "Top 5 Buildings:", 50f, y)
        y -= 40f
        
        val topBuildings = statsTracker.getTopBuildings(5)
        font.color = Color.WHITE
        topBuildings.forEachIndexed { index, (buildingId, count) ->
            font.draw(batch, "${index + 1}. ${getBuildingDisplayName(buildingId)}: $count", 70f, y)
            y -= 35f
        }
        
        batch.end()
    }
    
    // Renders prestige statistics tab
    private fun renderPrestigeTab() {
        val startY = Gdx.graphics.height - 200f
        
        batch.begin()
        font.color = Color.GOLD
        font.draw(batch, "Prestige Statistics", 50f, startY)
        
        font.color = Color.WHITE
        var y = startY - 50f
        
        font.draw(batch, "Total Prestiges: ${statsTracker.stats.totalPrestigesPerformed}", 50f, y)
        y -= 35f
        
        font.draw(batch, "Current Era: ${gameState.currentEra}", 50f, y)
        y -= 35f
        
        font.draw(batch, "Crown Shards: ${gameState.crownShards}", 50f, y)
        y -= 35f
        
        val heroesUnlocked = gameState.unlockedHeroes.size
        font.draw(batch, "Heroes Unlocked: $heroesUnlocked / 12", 50f, y)
        y -= 35f
        
        font.color = Color.CYAN
        font.draw(batch, "Prestige Bonuses Active: ${gameState.activePrestigeBonuses.size}", 50f, y)
        y -= 50f
        
        font.color = Color.LIGHT_GRAY
        font.draw(batch, "Next prestige will reset your kingdom", 50f, y)
        font.draw(batch, "but grant powerful permanent bonuses!", 50f, y - 25f)
        
        batch.end()
    }
    
    // Returns display name for building ID
    private fun getBuildingDisplayName(buildingId: String): String {
        if (buildingId.isEmpty()) return "None"
        return buildingId.split("_").lastOrNull()?.capitalize() ?: buildingId
    }
    
    // Returns era-themed color for building
    private fun getEraColor(buildingId: String): Color {
        return when {
            buildingId.contains("era1") -> Color(0.4f, 0.6f, 0.3f, 1f) // Green
            buildingId.contains("era2") -> Color(0.6f, 0.5f, 0.3f, 1f) // Brown
            buildingId.contains("era3") -> Color(0.5f, 0.5f, 0.6f, 1f) // Gray
            buildingId.contains("era4") -> Color(0.7f, 0.5f, 0.2f, 1f) // Gold
            buildingId.contains("era5") -> Color(0.6f, 0.3f, 0.7f, 1f) // Purple
            else -> Color(0.5f, 0.5f, 0.5f, 1f)
        }
    }
    
    // Formats large numbers with suffixes
    private fun formatNumber(value: Double): String {
        return when {
            value >= 1e15 -> "%.2fQa".format(value / 1e15)
            value >= 1e12 -> "%.2fT".format(value / 1e12)
            value >= 1e9 -> "%.2fB".format(value / 1e9)
            value >= 1e6 -> "%.2fM".format(value / 1e6)
            value >= 1e3 -> "%.2fK".format(value / 1e3)
            else -> "%.0f".format(value)
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

// Represents a tab button
data class TabButton(
    val tab: StatTab,
    val label: String,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
) {
    fun contains(px: Float, py: Float): Boolean {
        return px >= x && px <= x + width && py >= y && py <= y + height
    }
}
