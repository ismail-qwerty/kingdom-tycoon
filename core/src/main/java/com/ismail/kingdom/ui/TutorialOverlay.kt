// PATH: core/src/main/java/com/ismail/kingdom/ui/TutorialOverlay.kt
package com.ismail.kingdom.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Rectangle
import com.ismail.kingdom.systems.TutorialStep
import com.ismail.kingdom.systems.TutorialSystem
import kotlin.math.sin

// Tutorial overlay that highlights UI elements and shows instructions
class TutorialOverlay(private val tutorialSystem: TutorialSystem) {
    
    private val shapeRenderer = ShapeRenderer()
    private val batch = SpriteBatch()
    private val font = BitmapFont()
    
    private var animationTimer = 0f
    private var highlightBounds: Rectangle? = null
    private var skipButtonBounds = Rectangle(0f, 0f, 0f, 0f)
    
    // Animation constants
    private val ARROW_BOUNCE_SPEED = 2f
    private val ARROW_BOUNCE_DISTANCE = 20f
    private val PULSE_SPEED = 1.5f
    
    // UI constants
    private val OVERLAY_ALPHA = 0.7f
    private val HIGHLIGHT_PADDING = 20f
    private val SPEECH_BUBBLE_WIDTH = 400f
    private val SPEECH_BUBBLE_HEIGHT = 150f
    private val SKIP_BUTTON_WIDTH = 120f
    private val SKIP_BUTTON_HEIGHT = 50f
    
    // Updates animation timers
    fun update(delta: Float) {
        if (!tutorialSystem.isActive) return
        
        animationTimer += delta
        
        // Update skip button position
        skipButtonBounds.set(
            Gdx.graphics.width - SKIP_BUTTON_WIDTH - 20f,
            Gdx.graphics.height - SKIP_BUTTON_HEIGHT - 20f,
            SKIP_BUTTON_WIDTH,
            SKIP_BUTTON_HEIGHT
        )
    }
    
    // Renders the tutorial overlay
    fun render() {
        if (!tutorialSystem.isActive) return
        
        val stepData = tutorialSystem.getCurrentStepData() ?: return
        
        // Get highlight bounds for current step
        highlightBounds = getHighlightBounds(stepData.highlightTarget)
        
        // Render overlay
        renderDarkOverlay()
        
        // Render highlight cutout
        highlightBounds?.let { bounds ->
            renderHighlightCutout(bounds)
            renderArrow(bounds)
        }
        
        // Render speech bubble
        renderSpeechBubble(stepData.title, stepData.instruction, stepData.isInteractive)
        
        // Render skip button
        renderSkipButton()
    }
    
    // Renders semi-transparent dark overlay
    private fun renderDarkOverlay() {
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0f, 0f, 0f, OVERLAY_ALPHA)
        shapeRenderer.rect(0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        shapeRenderer.end()
        
        Gdx.gl.glDisable(GL20.GL_BLEND)
    }
    
    // Renders highlight cutout (clear area around target)
    private fun renderHighlightCutout(bounds: Rectangle) {
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        
        // Pulsing glow effect
        val pulse = (sin(animationTimer * PULSE_SPEED) * 0.3f + 0.7f).toFloat()
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        
        // Outer glow
        for (i in 0 until 5) {
            val glowPadding = HIGHLIGHT_PADDING + i * 10f
            val alpha = (0.3f - i * 0.05f) * pulse
            shapeRenderer.color = Color(1f, 1f, 0f, alpha)
            shapeRenderer.rect(
                bounds.x - glowPadding,
                bounds.y - glowPadding,
                bounds.width + glowPadding * 2,
                bounds.height + glowPadding * 2
            )
        }
        
        // Clear center (no overlay)
        shapeRenderer.color = Color(0f, 0f, 0f, 0f)
        shapeRenderer.rect(
            bounds.x - HIGHLIGHT_PADDING,
            bounds.y - HIGHLIGHT_PADDING,
            bounds.width + HIGHLIGHT_PADDING * 2,
            bounds.height + HIGHLIGHT_PADDING * 2
        )
        
        // Highlight border
        shapeRenderer.end()
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        Gdx.gl20.glLineWidth(3f)
        shapeRenderer.color = Color(1f, 1f, 0f, pulse)
        shapeRenderer.rect(
            bounds.x - HIGHLIGHT_PADDING,
            bounds.y - HIGHLIGHT_PADDING,
            bounds.width + HIGHLIGHT_PADDING * 2,
            bounds.height + HIGHLIGHT_PADDING * 2
        )
        shapeRenderer.end()
        
        Gdx.gl.glDisable(GL20.GL_BLEND)
    }
    
    // Renders bouncing arrow pointing to target
    private fun renderArrow(bounds: Rectangle) {
        val centerX = bounds.x + bounds.width / 2
        val centerY = bounds.y + bounds.height / 2
        
        // Calculate arrow position with bounce
        val bounce = sin(animationTimer * ARROW_BOUNCE_SPEED) * ARROW_BOUNCE_DISTANCE
        val arrowY = centerY + bounds.height / 2 + HIGHLIGHT_PADDING + 50f + bounce
        
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color.YELLOW
        
        // Arrow pointing down
        shapeRenderer.triangle(
            centerX, arrowY - 30f,
            centerX - 20f, arrowY,
            centerX + 20f, arrowY
        )
        
        shapeRenderer.end()
        Gdx.gl.glDisable(GL20.GL_BLEND)
    }
    
    // Renders speech bubble with instructions
    private fun renderSpeechBubble(title: String, instruction: String, isInteractive: Boolean) {
        val bubbleX = (Gdx.graphics.width - SPEECH_BUBBLE_WIDTH) / 2
        val bubbleY = Gdx.graphics.height - SPEECH_BUBBLE_HEIGHT - 100f
        
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        
        // Bubble background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0.1f, 0.1f, 0.15f, 0.95f)
        shapeRenderer.rect(bubbleX, bubbleY, SPEECH_BUBBLE_WIDTH, SPEECH_BUBBLE_HEIGHT)
        
        // Bubble border
        shapeRenderer.color = Color.GOLD
        shapeRenderer.end()
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        Gdx.gl20.glLineWidth(3f)
        shapeRenderer.rect(bubbleX, bubbleY, SPEECH_BUBBLE_WIDTH, SPEECH_BUBBLE_HEIGHT)
        shapeRenderer.end()
        
        Gdx.gl.glDisable(GL20.GL_BLEND)
        
        // Text
        batch.begin()
        
        // Title
        font.color = Color.GOLD
        font.data.setScale(1.2f)
        font.draw(batch, title, bubbleX + 20f, bubbleY + SPEECH_BUBBLE_HEIGHT - 20f)
        
        // Instruction
        font.color = Color.WHITE
        font.data.setScale(1f)
        drawWrappedText(batch, font, instruction, bubbleX + 20f, bubbleY + SPEECH_BUBBLE_HEIGHT - 60f, SPEECH_BUBBLE_WIDTH - 40f)
        
        // "TAP TO CONTINUE" hint for non-interactive steps
        if (!isInteractive) {
            val pulse = (sin(animationTimer * 3f) * 0.3f + 0.7f).toFloat()
            font.color = Color(1f, 1f, 1f, pulse)
            font.draw(batch, "TAP TO CONTINUE", bubbleX + 20f, bubbleY + 25f)
        }
        
        batch.end()
    }
    
    // Renders skip tutorial button
    private fun renderSkipButton() {
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0.3f, 0.3f, 0.4f, 0.9f)
        shapeRenderer.rect(skipButtonBounds.x, skipButtonBounds.y, skipButtonBounds.width, skipButtonBounds.height)
        shapeRenderer.end()
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        Gdx.gl20.glLineWidth(2f)
        shapeRenderer.color = Color.LIGHT_GRAY
        shapeRenderer.rect(skipButtonBounds.x, skipButtonBounds.y, skipButtonBounds.width, skipButtonBounds.height)
        shapeRenderer.end()
        
        Gdx.gl.glDisable(GL20.GL_BLEND)
        
        batch.begin()
        font.color = Color.WHITE
        font.draw(batch, "Skip Tutorial", skipButtonBounds.x + 15f, skipButtonBounds.y + 30f)
        batch.end()
    }
    
    // Draws wrapped text within a width constraint
    private fun drawWrappedText(batch: SpriteBatch, font: BitmapFont, text: String, x: Float, y: Float, maxWidth: Float) {
        val words = text.split(" ")
        var currentLine = ""
        var currentY = y
        val lineHeight = 25f
        
        words.forEach { word ->
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            val bounds = font.draw(batch, testLine, 0f, 0f)
            
            if (bounds.width > maxWidth && currentLine.isNotEmpty()) {
                font.draw(batch, currentLine, x, currentY)
                currentLine = word
                currentY -= lineHeight
            } else {
                currentLine = testLine
            }
        }
        
        if (currentLine.isNotEmpty()) {
            font.draw(batch, currentLine, x, currentY)
        }
    }
    
    // Gets highlight bounds for a target element
    private fun getHighlightBounds(target: String): Rectangle? {
        return when (target) {
            "kingdom_hall" -> {
                // Center of screen (tap area)
                Rectangle(
                    Gdx.graphics.width / 2f - 100f,
                    Gdx.graphics.height / 2f - 100f,
                    200f,
                    200f
                )
            }
            "building_0" -> {
                // First building in list
                Rectangle(50f, Gdx.graphics.height - 300f, 300f, 80f)
            }
            "building_1" -> {
                // Second building in list
                Rectangle(50f, Gdx.graphics.height - 400f, 300f, 80f)
            }
            "advisors_tab" -> {
                // Advisors tab button
                Rectangle(Gdx.graphics.width - 150f, Gdx.graphics.height - 100f, 120f, 60f)
            }
            "quests_button" -> {
                // Quests button
                Rectangle(20f, Gdx.graphics.height - 150f, 100f, 50f)
            }
            "map_button" -> {
                // Map button
                Rectangle(20f, Gdx.graphics.height - 220f, 100f, 50f)
            }
            "prestige_button" -> {
                // Prestige button
                Rectangle(Gdx.graphics.width / 2f - 75f, 50f, 150f, 60f)
            }
            "none" -> null
            else -> null
        }
    }
    
    // Handles touch input for tutorial overlay
    fun handleTouch(touchX: Float, touchY: Float): Boolean {
        if (!tutorialSystem.isActive) return false
        
        // Check skip button
        if (skipButtonBounds.contains(touchX, touchY)) {
            tutorialSystem.skipTutorial()
            return true
        }
        
        val stepData = tutorialSystem.getCurrentStepData() ?: return false
        
        // For non-interactive steps, any tap advances
        if (!stepData.isInteractive) {
            tutorialSystem.advanceToNext()
            return true
        }
        
        // For interactive steps, check if tap is on highlighted element
        highlightBounds?.let { bounds ->
            val expandedBounds = Rectangle(
                bounds.x - HIGHLIGHT_PADDING,
                bounds.y - HIGHLIGHT_PADDING,
                bounds.width + HIGHLIGHT_PADDING * 2,
                bounds.height + HIGHLIGHT_PADDING * 2
            )
            
            if (expandedBounds.contains(touchX, touchY)) {
                // Let the tap through to the actual UI element
                return false
            }
        }
        
        // Block taps outside highlighted area during interactive steps
        return true
    }
    
    // Disposes resources
    fun dispose() {
        shapeRenderer.dispose()
        batch.dispose()
        font.dispose()
    }
}
