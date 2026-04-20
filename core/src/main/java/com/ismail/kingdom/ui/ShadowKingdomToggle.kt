// PATH: core/src/main/java/com/ismail/kingdom/ui/ShadowKingdomToggle.kt
package com.ismail.kingdom.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.ismail.kingdom.systems.ShadowKingdomSystem
import kotlin.math.sin

// Portal icon toggle button for accessing shadow kingdom
class ShadowKingdomToggle(
    private val shadowSystem: ShadowKingdomSystem,
    private val onPortalClick: () -> Unit
) {
    
    private val x = Gdx.graphics.width - 100f
    private val y = Gdx.graphics.height / 2f
    private val size = 80f
    
    private var pulseTimer = 0f
    
    // Updates the portal animation
    fun update(delta: Float) {
        if (!shadowSystem.isUnlocked()) return
        pulseTimer += delta
    }
    
    // Renders the portal icon with pulsing animation
    fun render(batch: SpriteBatch, shapeRenderer: ShapeRenderer, font: BitmapFont) {
        if (!shadowSystem.isUnlocked()) return
        
        val pulse = sin(pulseTimer * 2f) * 0.2f + 0.8f
        
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        
        // Outer glow
        for (i in 0 until 3) {
            val glowSize = size + i * 10f
            val alpha = (0.3f - i * 0.1f) * pulse
            shapeRenderer.color = Color(0.5f, 0.2f, 0.8f, alpha)
            shapeRenderer.circle(x, y, glowSize / 2, 32)
        }
        
        // Portal circle
        shapeRenderer.color = Color(0.3f, 0.1f, 0.5f, 0.9f)
        shapeRenderer.circle(x, y, size / 2, 32)
        
        // Inner swirl effect
        val swirl1 = sin(pulseTimer * 3f) * 15f
        val swirl2 = sin(pulseTimer * 3f + 2f) * 15f
        
        shapeRenderer.color = Color(0.6f, 0.3f, 0.9f, 0.7f)
        shapeRenderer.circle(x + swirl1, y + swirl2, size / 4, 16)
        
        shapeRenderer.color = Color(0.4f, 0.2f, 0.7f, 0.5f)
        shapeRenderer.circle(x - swirl2, y - swirl1, size / 5, 16)
        
        shapeRenderer.end()
        
        Gdx.gl.glDisable(GL20.GL_BLEND)
        
        // Portal label
        batch.begin()
        font.color = Color(0.7f, 0.5f, 0.9f, 1f)
        font.draw(batch, "Shadow", x - 25f, y - size / 2 - 10f)
        font.draw(batch, "Realm", x - 20f, y - size / 2 - 25f)
        batch.end()
    }
    
    // Checks if the portal was clicked
    fun handleTouch(touchX: Float, touchY: Float): Boolean {
        if (!shadowSystem.isUnlocked()) return false
        
        val dx = touchX - x
        val dy = touchY - y
        val distance = kotlin.math.sqrt(dx * dx + dy * dy)
        
        if (distance <= size / 2) {
            onPortalClick()
            return true
        }
        
        return false
    }
    
    // Returns the portal bounds for collision detection
    fun getBounds(): PortalBounds {
        return PortalBounds(x, y, size / 2)
    }
}

// Represents portal button bounds
data class PortalBounds(
    val centerX: Float,
    val centerY: Float,
    val radius: Float
) {
    fun contains(px: Float, py: Float): Boolean {
        val dx = px - centerX
        val dy = py - centerY
        return kotlin.math.sqrt(dx * dx + dy * dy) <= radius
    }
}
