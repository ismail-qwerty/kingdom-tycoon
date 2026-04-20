// PATH: core/src/main/java/com/ismail/kingdom/ParticleManager.kt
package com.ismail.kingdom

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.ismail.kingdom.models.KingdomEventType
import kotlin.math.cos
import kotlin.math.sin

// Particle effect types
enum class ParticleType {
    COIN_BURST,
    MILESTONE_BURST,
    PRESTIGE_FLASH,
    OFFLINE_RAIN,
    EVENT_START
}

// Manages all particle effects using Scene2D Actions
object ParticleManager {

    private var defaultFont: BitmapFont? = null
    private var defaultSkin: Skin? = null

    // Initializes with font and skin for label creation
    fun initialize(font: BitmapFont? = null, skin: Skin? = null) {
        defaultFont = font
        defaultSkin = skin
    }

    // Spawns particle effect at position
    fun spawn(
        type: ParticleType,
        position: Vector2,
        stage: Stage? = null,
        eventType: KingdomEventType? = null
    ) {
        stage ?: return

        when (type) {
            ParticleType.COIN_BURST -> spawnCoinBurst(position, stage)
            ParticleType.MILESTONE_BURST -> spawnMilestoneBurst(position, stage)
            ParticleType.PRESTIGE_FLASH -> spawnPrestigeFlash(position, stage)
            ParticleType.OFFLINE_RAIN -> spawnOfflineRain(position, stage)
            ParticleType.EVENT_START -> spawnEventStart(position, stage, eventType)
        }
    }

    // Spawns 8 coin emoji particles in a burst pattern
    private fun spawnCoinBurst(position: Vector2, stage: Stage) {
        val coinCount = 8
        val angleStep = 360f / coinCount

        repeat(coinCount) { i ->
            val angle = angleStep * i
            val radians = Math.toRadians(angle.toDouble()).toFloat()
            val distance = MathUtils.random(80f, 150f)
            val targetX = position.x + cos(radians) * distance
            val targetY = position.y + sin(radians) * distance
            val duration = MathUtils.random(0.5f, 0.8f)

            val label = createLabel("🪙", Color.GOLD, stage)
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
    }

    // Spawns gold sparkle particles for milestones
    private fun spawnMilestoneBurst(position: Vector2, stage: Stage) {
        val sparkleCount = 12
        val angleStep = 360f / sparkleCount

        repeat(sparkleCount) { i ->
            val angle = angleStep * i
            val radians = Math.toRadians(angle.toDouble()).toFloat()
            val distance = MathUtils.random(100f, 200f)
            val targetX = position.x + cos(radians) * distance
            val targetY = position.y + sin(radians) * distance
            val duration = MathUtils.random(0.6f, 1.0f)

            val label = createLabel("✦", Color.GOLD, stage)
            label.setPosition(position.x, position.y)
            label.addAction(
                Actions.sequence(
                    Actions.parallel(
                        Actions.moveTo(targetX, targetY, duration, Interpolation.pow3Out),
                        Actions.fadeOut(duration, Interpolation.linear)
                    ),
                    Actions.removeActor()
                )
            )
        }
    }

    // Spawns prestige flash effect
    private fun spawnPrestigeFlash(position: Vector2, stage: Stage) {
        repeat(20) {
            val angle = MathUtils.random(0f, 360f)
            val radians = Math.toRadians(angle.toDouble()).toFloat()
            val distance = MathUtils.random(50f, 300f)
            val targetX = position.x + cos(radians) * distance
            val targetY = position.y + sin(radians) * distance
            val duration = MathUtils.random(0.4f, 0.8f)

            val label = createLabel("★", Color.CYAN, stage)
            label.setPosition(position.x, position.y)
            label.addAction(
                Actions.sequence(
                    Actions.parallel(
                        Actions.moveTo(targetX, targetY, duration, Interpolation.pow2Out),
                        Actions.fadeOut(duration)
                    ),
                    Actions.removeActor()
                )
            )
        }
    }

    // Spawns offline rain effect
    private fun spawnOfflineRain(position: Vector2, stage: Stage) {
        repeat(15) { i ->
            val offsetX = MathUtils.random(-200f, 200f)
            val startY = position.y + 400f
            val endY = position.y - 100f
            val delay = i * 0.1f
            val duration = MathUtils.random(1.0f, 1.5f)

            val label = createLabel("💰", Color.GOLD, stage)
            label.setPosition(position.x + offsetX, startY)
            label.addAction(
                Actions.sequence(
                    Actions.delay(delay),
                    Actions.parallel(
                        Actions.moveTo(position.x + offsetX, endY, duration, Interpolation.linear),
                        Actions.fadeOut(duration)
                    ),
                    Actions.removeActor()
                )
            )
        }
    }

    // Spawns event start effect
    private fun spawnEventStart(position: Vector2, stage: Stage, eventType: KingdomEventType?) {
        val symbol = when (eventType) {
            KingdomEventType.GOLD_RUSH -> "💰"
            KingdomEventType.DOUBLE_INCOME -> "⚡"
            KingdomEventType.BUILDING_DISCOUNT -> "🏰"
            KingdomEventType.ROYAL_FESTIVAL -> "📜"
            else -> "✨"
        }

        repeat(10) { i ->
            val angle = (360f / 10f) * i
            val radians = Math.toRadians(angle.toDouble()).toFloat()
            val distance = MathUtils.random(100f, 180f)
            val targetX = position.x + cos(radians) * distance
            val targetY = position.y + sin(radians) * distance
            val duration = 0.8f

            val label = createLabel(symbol, Color.WHITE, stage)
            label.setPosition(position.x, position.y)
            label.addAction(
                Actions.sequence(
                    Actions.parallel(
                        Actions.moveTo(targetX, targetY, duration, Interpolation.pow2Out),
                        Actions.fadeOut(duration)
                    ),
                    Actions.removeActor()
                )
            )
        }
    }

    // Creates a label actor for particle display
    private fun createLabel(text: String, color: Color, stage: Stage): Label {
        val style = Label.LabelStyle().apply {
            font = defaultFont ?: BitmapFont()
            fontColor = color.cpy()
        }
        val label = Label(text, style)
        label.color.a = 1f
        stage.addActor(label)
        return label
    }

    // Updates all active effects (no-op, Actions handle updates)
    fun update(delta: Float) {}

    // Stops specific effect type (no-op, Actions auto-remove)
    fun stopEffect(type: ParticleType) {}

    // Clears all effects (no-op, Actions auto-remove)
    fun clear() {}

    // Disposes resources
    fun dispose() {
        defaultFont = null
        defaultSkin = null
    }

    // Gets active effect count (always 0 since Actions auto-remove)
    fun getActiveEffectCount(): Int = 0
}
