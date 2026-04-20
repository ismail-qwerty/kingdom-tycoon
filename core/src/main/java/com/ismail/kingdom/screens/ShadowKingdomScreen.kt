// PATH: core/src/main/java/com/ismail/kingdom/screens/ShadowKingdomScreen.kt
package com.ismail.kingdom.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.ismail.kingdom.GameState
import com.ismail.kingdom.effects.PortalTransition
import com.ismail.kingdom.systems.ShadowKingdomSystem

// The shadow dimension screen with dark inverted theme and passive income
class ShadowKingdomScreen(
    private val gameState: GameState,
    private val shadowSystem: ShadowKingdomSystem,
    private val onReturnToMain: () -> Unit
) : Screen {
    
    private val batch = SpriteBatch()
    private val shapeRenderer = ShapeRenderer()
    private val font = BitmapFont()
    private val portalTransition = PortalTransition()
    
    private var scrollOffset = 0f
    private val buildingCards = mutableListOf<ShadowBuildingCard>()
    
    private val returnButtonBounds = ButtonBounds(20f, Gdx.graphics.height - 70f, 120f, 50f)
    private val boostButtonBounds = ButtonBounds(Gdx.graphics.width - 200f, Gdx.graphics.height - 70f, 180f, 50f)
    
    override fun show() {
        updateBuildingCards()
    }
    
    override fun render(delta: Float) {
        shadowSystem.update(delta)
        portalTransition.update(delta)
        
        Gdx.gl.glClearColor(0.02f, 0.0f, 0.08f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        
        if (!portalTransition.isActive()) {
            handleInput()
        }
        
        renderBackground()
        renderHeader()
        renderShadowStats()
        renderBuildingCards()
        renderButtons()
        
        portalTransition.render(shapeRenderer)
    }
    
    // Handles touch input for scrolling and buttons
    private fun handleInput() {
        if (Gdx.input.justTouched()) {
            val touchX = Gdx.input.x.toFloat()
            val touchY = Gdx.graphics.height - Gdx.input.y.toFloat()
            
            // Return button
            if (returnButtonBounds.contains(touchX, touchY)) {
                portalTransition.start(reverse = true) {
                    onReturnToMain()
                }
                return
            }
            
            // Shadow boost button
            if (boostButtonBounds.contains(touchX, touchY) && !shadowSystem.shadowState.shadowBoostActive) {
                // Trigger ad watch (placeholder)
                shadowSystem.activateShadowBoost()
            }
        }
        
        // Handle scrolling
        if (Gdx.input.isTouched) {
            val deltaY = Gdx.input.deltaY.toFloat()
            val maxScroll = (buildingCards.size * 130f - Gdx.graphics.height + 300f).coerceAtLeast(0f)
            scrollOffset = (scrollOffset - deltaY * 2f).coerceIn(-maxScroll, 0f)
        }
    }
    
    // Renders dark background with subtle effects
    private fun renderBackground() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        
        // Dark gradient background
        shapeRenderer.color = Color(0.02f, 0.0f, 0.08f, 1f)
        shapeRenderer.rect(0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        
        // Subtle dark vignette
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        
        val centerX = Gdx.graphics.width / 2f
        val centerY = Gdx.graphics.height / 2f
        val maxRadius = Gdx.graphics.width.coerceAtLeast(Gdx.graphics.height) * 0.8f
        
        for (i in 0 until 5) {
            val radius = maxRadius * (1f - i * 0.2f)
            val alpha = 0.05f * i
            shapeRenderer.color = Color(0.1f, 0.0f, 0.2f, alpha)
            shapeRenderer.circle(centerX, centerY, radius, 64)
        }
        
        shapeRenderer.end()
        Gdx.gl.glDisable(GL20.GL_BLEND)
    }
    
    // Renders the header with shadow kingdom title
    private fun renderHeader() {
        batch.begin()
        
        font.color = Color(0.6f, 0.3f, 0.9f, 1f) // Dark purple
        font.data.setScale(2f)
        font.draw(batch, "Shadow Kingdom", Gdx.graphics.width / 2f - 150f, Gdx.graphics.height - 20f)
        font.data.setScale(1f)
        
        font.color = Color(0.5f, 0.5f, 0.7f, 1f)
        font.draw(batch, "The Mirrored Dimension", Gdx.graphics.width / 2f - 100f, Gdx.graphics.height - 55f)
        
        batch.end()
    }
    
    // Renders shadow gold and IPS stats
    private fun renderShadowStats() {
        val statsY = Gdx.graphics.height - 120f
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0.1f, 0.05f, 0.15f, 0.9f)
        shapeRenderer.rect(20f, statsY - 80f, Gdx.graphics.width - 40f, 70f)
        
        shapeRenderer.color = Color(0.4f, 0.2f, 0.6f, 1f)
        shapeRenderer.rect(20f, statsY - 80f, Gdx.graphics.width - 40f, 3f)
        shapeRenderer.end()
        
        batch.begin()
        
        font.color = Color(0.7f, 0.5f, 0.9f, 1f)
        font.draw(batch, "Shadow Gold: ${formatNumber(shadowSystem.shadowState.shadowGold)}", 40f, statsY - 20f)
        
        font.color = Color(0.5f, 0.7f, 0.9f, 1f)
        val ips = shadowSystem.getCurrentShadowIPS()
        font.draw(batch, "Shadow IPS: ${formatNumber(ips)}/s", 40f, statsY - 50f)
        
        if (shadowSystem.shadowState.shadowBoostActive) {
            font.color = Color.CYAN
            val remaining = shadowSystem.getBoostTimeRemaining()
            val minutes = remaining / 60
            val seconds = remaining % 60
            font.draw(batch, "Boost Active: ${minutes}m ${seconds}s", 40f, statsY - 70f)
        }
        
        batch.end()
    }
    
    // Updates building card positions
    private fun updateBuildingCards() {
        buildingCards.clear()
        
        val cardWidth = Gdx.graphics.width - 40f
        val cardHeight = 100f
        val spacing = 20f
        val startY = Gdx.graphics.height - 220f
        
        shadowSystem.shadowState.shadowBuildings.entries.forEachIndexed { index, (buildingId, count) ->
            val y = startY - index * (cardHeight + spacing) + scrollOffset
            buildingCards.add(ShadowBuildingCard(buildingId, count, 20f, y, cardWidth, cardHeight))
        }
    }
    
    // Renders shadow building cards
    private fun renderBuildingCards() {
        updateBuildingCards()
        
        buildingCards.forEach { card ->
            if (card.y + card.height < 0 || card.y > Gdx.graphics.height) return@forEach
            
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            shapeRenderer.color = Color(0.08f, 0.05f, 0.12f, 0.95f)
            shapeRenderer.rect(card.x, card.y, card.width, card.height)
            
            shapeRenderer.color = Color(0.3f, 0.15f, 0.5f, 1f)
            shapeRenderer.rect(card.x, card.y + card.height - 2f, card.width, 2f)
            shapeRenderer.end()
            
            batch.begin()
            
            font.color = Color(0.6f, 0.4f, 0.8f, 1f)
            val buildingName = getShadowBuildingName(card.buildingId)
            font.draw(batch, buildingName, card.x + 15f, card.y + card.height - 15f)
            
            font.color = Color(0.5f, 0.5f, 0.7f, 1f)
            font.draw(batch, "Count: ${card.count}", card.x + 15f, card.y + card.height - 45f)
            
            val income = shadowSystem.getCurrentShadowIPS() / shadowSystem.shadowState.shadowBuildings.size
            font.color = Color(0.4f, 0.6f, 0.8f, 1f)
            font.draw(batch, "Income: ${formatNumber(income)}/s", card.x + 15f, card.y + 20f)
            
            batch.end()
        }
    }
    
    // Renders control buttons
    private fun renderButtons() {
        // Return button
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0.15f, 0.1f, 0.2f, 1f)
        shapeRenderer.rect(returnButtonBounds.x, returnButtonBounds.y, returnButtonBounds.width, returnButtonBounds.height)
        shapeRenderer.end()
        
        batch.begin()
        font.color = Color(0.7f, 0.5f, 0.9f, 1f)
        font.draw(batch, "← Return", returnButtonBounds.x + 20f, returnButtonBounds.y + 30f)
        batch.end()
        
        // Shadow boost button
        val boostActive = shadowSystem.shadowState.shadowBoostActive
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = if (boostActive) Color(0.1f, 0.3f, 0.4f, 1f) else Color(0.2f, 0.1f, 0.3f, 1f)
        shapeRenderer.rect(boostButtonBounds.x, boostButtonBounds.y, boostButtonBounds.width, boostButtonBounds.height)
        shapeRenderer.end()
        
        batch.begin()
        font.color = if (boostActive) Color.GRAY else Color.CYAN
        val buttonText = if (boostActive) "Boost Active" else "Shadow Boost (Ad)"
        font.draw(batch, buttonText, boostButtonBounds.x + 15f, boostButtonBounds.y + 30f)
        batch.end()
    }
    
    // Returns shadow version of building name
    private fun getShadowBuildingName(buildingId: String): String {
        val baseName = buildingId.split("_").lastOrNull()?.capitalize() ?: "Building"
        return "Shadow $baseName"
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

// Represents a shadow building card in the list
data class ShadowBuildingCard(
    val buildingId: String,
    val count: Int,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
)

// Represents button bounds for hit detection
data class ButtonBounds(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
) {
    fun contains(px: Float, py: Float): Boolean {
        return px >= x && px <= x + width && py >= y && py <= y + height
    }
}
