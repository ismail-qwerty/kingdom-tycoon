// PATH: core/src/main/java/com/ismail/kingdom/effects/PortalTransition.kt
package com.ismail.kingdom.effects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import kotlin.math.sin

// Handles the portal warp transition effect between dimensions
class PortalTransition {
    
    private var isActive = false
    private var timer = 0f
    private val duration = 1.2f
    private var onComplete: (() -> Unit)? = null
    private var isReverse = false
    
    // Starts the portal transition effect
    fun start(reverse: Boolean = false, onComplete: () -> Unit) {
        this.isActive = true
        this.timer = 0f
        this.isReverse = reverse
        this.onComplete = onComplete
    }
    
    // Updates the transition animation
    fun update(delta: Float) {
        if (!isActive) return
        
        timer += delta
        
        if (timer >= duration) {
            isActive = false
            onComplete?.invoke()
            onComplete = null
        }
    }
    
    // Renders the portal warp effect
    fun render(shapeRenderer: ShapeRenderer) {
        if (!isActive) return
        
        val progress = (timer / duration).coerceIn(0f, 1f)
        val effectProgress = if (isReverse) 1f - progress else progress
        
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        
        val centerX = Gdx.graphics.width / 2f
        val centerY = Gdx.graphics.height / 2f
        val maxRadius = Gdx.graphics.width.coerceAtLeast(Gdx.graphics.height) * 1.5f
        
        // Draw expanding/contracting circles with dark purple/blue colors
        for (i in 0 until 8) {
            val offset = i * 0.1f
            val waveProgress = ((effectProgress + offset) % 1f)
            val radius = maxRadius * waveProgress
            
            val alpha = (1f - waveProgress) * 0.6f
            val hue = if (isReverse) 0.7f else 0.8f // Purple to blue
            
            val color = Color(
                0.2f + sin(waveProgress * 3.14f) * 0.3f,
                0.1f,
                0.4f + sin(waveProgress * 3.14f) * 0.4f,
                alpha
            )
            
            shapeRenderer.color = color
            shapeRenderer.circle(centerX, centerY, radius, 64)
        }
        
        // Draw vortex overlay
        if (effectProgress > 0.3f && effectProgress < 0.7f) {
            val vortexAlpha = if (effectProgress < 0.5f) {
                (effectProgress - 0.3f) / 0.2f
            } else {
                (0.7f - effectProgress) / 0.2f
            }
            
            shapeRenderer.color = Color(0.05f, 0.0f, 0.15f, vortexAlpha * 0.9f)
            shapeRenderer.rect(0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        }
        
        shapeRenderer.end()
        
        Gdx.gl.glDisable(GL20.GL_BLEND)
    }
    
    // Checks if transition is currently active
    fun isActive(): Boolean = isActive
}
