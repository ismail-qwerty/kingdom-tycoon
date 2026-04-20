// PATH: core/src/main/java/com/ismail/kingdom/utils/PerformanceMonitor.kt
package com.ismail.kingdom.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.ismail.kingdom.ParticleManager

// Monitors game performance (debug only)
object PerformanceMonitor {
    
    var debugEnabled = false
    
    private var fps = 0
    private var particleCount = 0
    private var actorCount = 0
    private var memoryMB = 0L
    
    private var updateTimer = 0f
    private val UPDATE_INTERVAL = 0.5f
    
    private val font = BitmapFont()
    
    init {
        font.color = Color.YELLOW
        font.data.setScale(0.8f)
    }
    
    // Updates performance metrics
    fun update(delta: Float, stage: Stage?) {
        if (!debugEnabled) return
        
        updateTimer += delta
        
        if (updateTimer >= UPDATE_INTERVAL) {
            updateTimer = 0f
            
            // Update FPS
            fps = Gdx.graphics.framesPerSecond
            
            // Update particle count
            particleCount = ParticleManager.getActiveEffectCount()
            
            // Update actor count
            actorCount = stage?.actors?.size ?: 0
            
            // Update memory usage
            memoryMB = Gdx.app.javaHeap / 1024 / 1024
            
            // Log warning if FPS drops below 50
            if (fps < 50) {
                Gdx.app.log("PerformanceMonitor", "WARNING: FPS dropped to $fps")
            }
        }
    }
    
    // Renders performance overlay
    fun render(batch: SpriteBatch) {
        if (!debugEnabled) return
        
        batch.begin()
        
        val x = 10f
        var y = Gdx.graphics.height - 10f
        
        font.draw(batch, "FPS: $fps", x, y)
        y -= 20f
        
        font.draw(batch, "Particles: $particleCount", x, y)
        y -= 20f
        
        font.draw(batch, "Actors: $actorCount", x, y)
        y -= 20f
        
        font.draw(batch, "Memory: ${memoryMB}MB", x, y)
        
        batch.end()
    }
    
    // Disposes resources
    fun dispose() {
        font.dispose()
    }
}
