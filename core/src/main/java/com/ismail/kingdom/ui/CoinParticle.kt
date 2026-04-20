// PATH: core/src/main/java/com/ismail/kingdom/ui/CoinParticle.kt
package com.ismail.kingdom.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import kotlin.math.cos
import kotlin.math.sin

// Manages coin particle effects for tap feedback using Scene2D Actions
class CoinParticle(private val position: Vector2, private val stage: Stage, private val font: BitmapFont? = null) {
    
    private var isComplete = false
    
    init {
        spawnCoinBurst()
    }
    
    // Spawns 8 coin particles in a burst pattern
    private fun spawnCoinBurst() {
        val coinCount = 8
        val angleStep = 360f / coinCount

        repeat(coinCount) { i ->
            val angle = angleStep * i
            val radians = Math.toRadians(angle.toDouble()).toFloat()
            val distance = MathUtils.random(60f, 120f)
            val targetX = position.x + cos(radians) * distance
            val targetY = position.y + sin(radians) * distance
            val duration = MathUtils.random(0.4f, 0.7f)

            val label = createCoinLabel()
            label.setPosition(position.x, position.y)
            label.addAction(
                Actions.sequence(
                    Actions.parallel(
                        Actions.moveTo(targetX, targetY, duration, Interpolation.pow2Out),
                        Actions.fadeOut(duration, Interpolation.pow2In)
                    ),
                    Actions.removeActor()
                )
            )
        }
        
        isComplete = true
    }
    
    // Creates a coin label actor
    private fun createCoinLabel(): Label {
        val style = Label.LabelStyle().apply {
            this.font = this@CoinParticle.font ?: BitmapFont()
            fontColor = Color.GOLD.cpy()
        }
        val label = Label("🪙", style)
        label.color.a = 1f
        stage.addActor(label)
        return label
    }
    
    // Updates particle effect (no-op, Actions handle updates)
    fun update(delta: Float) {}
    
    // Checks if particle effect is complete
    fun isComplete(): Boolean = isComplete
    
    // Disposes particle effect (no-op, Actions auto-remove)
    fun dispose() {}
}

// Pool manager for coin particles
object CoinParticlePool {
    
    private var currentStage: Stage? = null
    private var currentFont: BitmapFont? = null
    
    // Initializes pool with stage and font
    fun initialize(stage: Stage, font: BitmapFont? = null) {
        currentStage = stage
        currentFont = font
    }
    
    // Spawns coin particles at position
    fun spawn(position: Vector2) {
        val stage = currentStage ?: return
        CoinParticle(position, stage, currentFont)
    }
    
    // Updates all active particles (no-op, Actions handle updates)
    fun update(delta: Float) {}
    
    // Draws all active particles (no-op, Stage handles drawing)
    fun draw(batch: com.badlogic.gdx.graphics.g2d.SpriteBatch) {}
    
    // Clears all particles (no-op, Actions auto-remove)
    fun clear() {}
}
