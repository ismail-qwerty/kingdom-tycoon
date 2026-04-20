// PATH: core/src/main/java/com/ismail/kingdom/AnimationSystem.kt
package com.ismail.kingdom

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.ismail.kingdom.utils.Formatters

// System for managing all game animations using LibGDX Actions
object AnimationSystem {

    // Kingdom Hall tap animation
    fun animateKingdomHallTap(actor: Actor, isRapidTap: Boolean = false) {
        // If rapid tapping, don't reset - just re-pulse from current scale
        if (isRapidTap && actor.scaleX > 1.0f) {
            actor.clearActions()
            actor.addAction(
                Actions.sequence(
                    Actions.scaleTo(1.12f, 1.12f, 0.08f, Interpolation.pow2Out),
                    Actions.scaleTo(1.0f, 1.0f, 0.15f, Interpolation.pow2In)
                )
            )
        } else {
            actor.clearActions()
            actor.addAction(
                Actions.sequence(
                    Actions.scaleTo(1.12f, 1.12f, 0.08f, Interpolation.pow2Out),
                    Actions.scaleTo(1.0f, 1.0f, 0.15f, Interpolation.pow2In)
                )
            )
        }
    }

    // Income number counter animation (lerps to target value)
    fun animateIncomeCounter(
        label: Label,
        currentValue: Double,
        targetValue: Double,
        duration: Float = 0.5f
    ) {
        // Store start value
        val startValue = currentValue

        // Create lerp action
        label.clearActions()
        label.addAction(
            Actions.sequence(
                Actions.run {
                    var elapsed = 0f
                    val action = object : com.badlogic.gdx.scenes.scene2d.Action() {
                        override fun act(delta: Float): Boolean {
                            elapsed += delta
                            val progress = (elapsed / duration).coerceIn(0f, 1f)

                            // Lerp value
                            val lerpedValue = startValue + (targetValue - startValue) * progress.toDouble()
                            label.setText(Formatters.formatGold(lerpedValue))

                            return progress >= 1f
                        }
                    }
                    label.addAction(action)
                }
            )
        )
    }

    // IPS display animation (smoother lerp over 2 seconds)
    fun animateIPSCounter(
        label: Label,
        currentValue: Double,
        targetValue: Double
    ) {
        animateIncomeCounter(label, currentValue, targetValue, duration = 2.0f)
    }

    // Combo indicator animation (slides in, pulses, slides out)
    fun animateComboIndicator(
        label: Label,
        comboMultiplier: Double,
        stage: Stage
    ) {
        // Set text
        label.setText("${comboMultiplier.toInt()}x COMBO!")
        label.color = Color.ORANGE
        label.setAlignment(Align.center)

        // Position at top center, off-screen initially
        val startY = stage.height + 50f
        val targetY = stage.height - 150f
        label.setPosition((stage.width - label.width) / 2f, startY)

        label.clearActions()
        label.addAction(
            Actions.sequence(
                // Slide in from top
                Actions.moveTo(label.x, targetY, 0.3f, Interpolation.pow2Out),

                // Pulse while active
                Actions.forever(
                    Actions.sequence(
                        Actions.scaleTo(1.2f, 1.2f, 0.25f, Interpolation.sine),
                        Actions.scaleTo(1.0f, 1.0f, 0.25f, Interpolation.sine)
                    )
                )
            )
        )
    }

    // Stops combo indicator animation and slides out
    fun stopComboIndicator(label: Label, stage: Stage) {
        label.clearActions()
        label.addAction(
            Actions.sequence(
                Actions.moveTo(label.x, stage.height + 50f, 0.3f, Interpolation.pow2In),
                Actions.run { label.isVisible = false }
            )
        )
    }

    // Tap popup animation (floating "+1.23K" text)
    fun animateTapPopup(
        stage: Stage,
        skin: Skin,
        position: Vector2,
        goldAmount: Double,
        isCritical: Boolean = false
    ) {
        val text = if (isCritical) {
            "CRITICAL!\n+${Formatters.formatGold(goldAmount)}"
        } else {
            "+${Formatters.formatGold(goldAmount)}"
        }

        val label = Label(text, skin, if (isCritical) "gold-large" else "gold-small")
        label.setAlignment(Align.center)
        label.color = if (isCritical) Color.RED else Color.GOLD
        label.setFontScale(if (isCritical) 1.5f else 1.0f)
        label.setPosition(position.x - 50f, position.y)

        stage.addActor(label)

        label.addAction(
            Actions.sequence(
                // Rise and fade
                Actions.parallel(
                    Actions.moveBy(0f, 80f, 0.8f, Interpolation.pow2Out),
                    Actions.sequence(
                        Actions.delay(0.5f),
                        Actions.fadeOut(0.3f)
                    )
                ),
                Actions.removeActor()
            )
        )
    }

    // Building row highlight animation (on purchase)
    fun animateBuildingPurchase(
        row: Table,
        countLabel: Label,
        newCount: Int
    ) {
        // Flash background gold
        val originalColor = row.color.cpy()
        row.clearActions()
        row.addAction(
            Actions.sequence(
                Actions.color(Color.GOLD, 0.15f),
                Actions.color(originalColor, 0.15f)
            )
        )

        // Bounce count label
        countLabel.clearActions()
        countLabel.addAction(
            Actions.sequence(
                Actions.scaleTo(1.3f, 1.3f, 0.1f, Interpolation.elasticOut),
                Actions.scaleTo(1.0f, 1.0f, 0.2f, Interpolation.elasticIn),
                Actions.run {
                    // Update count after animation
                    countLabel.setText("x$newCount")
                }
            )
        )
    }

    // Building row milestone flash (on x10, x20, etc.)
    fun animateBuildingMilestone(row: Table) {
        val originalColor = row.color.cpy()
        row.clearActions()
        row.addAction(
            Actions.sequence(
                Actions.repeat(3,
                    Actions.sequence(
                        Actions.color(Color.GOLD, 0.2f, Interpolation.sine),
                        Actions.color(originalColor, 0.2f, Interpolation.sine)
                    )
                )
            )
        )
    }

    // Era transition animation (fade out old, fade in new)
    fun animateEraTransition(
        oldBackground: Actor?,
        newBackground: Actor,
        uiElements: List<Actor>,
        onComplete: () -> Unit
    ) {
        // Fade out old background
        oldBackground?.addAction(
            Actions.fadeOut(1.5f, Interpolation.fade)
        )

        // Fade in new background
        newBackground.color = Color(1f, 1f, 1f, 0f)
        newBackground.addAction(
            Actions.fadeIn(1.5f, Interpolation.fade)
        )

        // Scale UI elements with stagger
        var delay = 0f
        for (element in uiElements) {
            element.setScale(0.8f)
            element.color = Color(1f, 1f, 1f, 0f)

            element.addAction(
                Actions.sequence(
                    Actions.delay(delay),
                    Actions.parallel(
                        Actions.scaleTo(1.0f, 1.0f, 0.5f, Interpolation.elasticOut),
                        Actions.fadeIn(0.5f, Interpolation.fade)
                    )
                )
            )

            delay += 0.1f // Stagger by 0.1s
        }

        // Call completion callback after all animations
        val totalDuration = 1.5f + (uiElements.size * 0.1f) + 0.5f
        newBackground.addAction(
            Actions.sequence(
                Actions.delay(totalDuration),
                Actions.run { onComplete() }
            )
        )
    }

    // Gold display pulse animation (on large income)
    fun animateGoldPulse(label: Label) {
        label.clearActions()
        label.addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.scaleTo(1.15f, 1.15f, 0.1f, Interpolation.pow2Out),
                    Actions.color(Color.ORANGE, 0.1f)
                ),
                Actions.parallel(
                    Actions.scaleTo(1.0f, 1.0f, 0.15f, Interpolation.pow2In),
                    Actions.color(Color.GOLD, 0.15f)
                )
            )
        )
    }

    // Button press animation (generic)
    fun animateButtonPress(button: Actor) {
        button.clearActions()
        button.addAction(
            Actions.sequence(
                Actions.scaleTo(0.95f, 0.95f, 0.05f, Interpolation.pow2Out),
                Actions.scaleTo(1.0f, 1.0f, 0.1f, Interpolation.elasticOut)
            )
        )
    }

    // Shake animation (for errors or critical events)
    fun animateShake(actor: Actor, intensity: Float = 10f) {
        val originalX = actor.x
        val originalY = actor.y

        actor.clearActions()
        actor.addAction(
            Actions.sequence(
                Actions.repeat(5,
                    Actions.sequence(
                        Actions.moveBy(intensity, 0f, 0.05f),
                        Actions.moveBy(-intensity * 2f, 0f, 0.05f),
                        Actions.moveBy(intensity, 0f, 0.05f)
                    )
                ),
                Actions.moveTo(originalX, originalY)
            )
        )
    }

    // Fade in animation (for popups)
    fun animateFadeIn(actor: Actor, duration: Float = 0.3f, onComplete: (() -> Unit)? = null) {
        actor.color = Color(1f, 1f, 1f, 0f)
        actor.clearActions()

        val sequence = if (onComplete != null) {
            Actions.sequence(
                Actions.fadeIn(duration, Interpolation.fade),
                Actions.run { onComplete() }
            )
        } else {
            Actions.fadeIn(duration, Interpolation.fade)
        }

        actor.addAction(sequence)
    }

    // Fade out animation (for popups)
    fun animateFadeOut(actor: Actor, duration: Float = 0.3f, onComplete: (() -> Unit)? = null) {
        actor.clearActions()

        val sequence = if (onComplete != null) {
            Actions.sequence(
                Actions.fadeOut(duration, Interpolation.fade),
                Actions.run { onComplete() }
            )
        } else {
            Actions.fadeOut(duration, Interpolation.fade)
        }

        actor.addAction(sequence)
    }

    // Slide in from bottom animation
    fun animateSlideInFromBottom(
        actor: Actor,
        stage: Stage,
        duration: Float = 0.4f,
        onComplete: (() -> Unit)? = null
    ) {
        val targetY = actor.y
        actor.y = -actor.height
        actor.clearActions()

        val sequence = if (onComplete != null) {
            Actions.sequence(
                Actions.moveTo(actor.x, targetY, duration, Interpolation.pow2Out),
                Actions.run { onComplete() }
            )
        } else {
            Actions.moveTo(actor.x, targetY, duration, Interpolation.pow2Out)
        }

        actor.addAction(sequence)
    }

    // Slide out to bottom animation
    fun animateSlideOutToBottom(
        actor: Actor,
        duration: Float = 0.3f,
        onComplete: (() -> Unit)? = null
    ) {
        actor.clearActions()

        val sequence = if (onComplete != null) {
            Actions.sequence(
                Actions.moveTo(actor.x, -actor.height, duration, Interpolation.pow2In),
                Actions.run { onComplete() }
            )
        } else {
            Actions.moveTo(actor.x, -actor.height, duration, Interpolation.pow2In)
        }

        actor.addAction(sequence)
    }

    // Bounce animation (for attention)
    fun animateBounce(actor: Actor, height: Float = 20f) {
        val originalY = actor.y

        actor.clearActions()
        actor.addAction(
            Actions.sequence(
                Actions.moveBy(0f, height, 0.3f, Interpolation.pow2Out),
                Actions.moveBy(0f, -height, 0.3f, Interpolation.bounceOut)
            )
        )
    }

    // Rotate animation (for loading indicators)
    fun animateRotate(actor: Actor, continuous: Boolean = true) {
        actor.setOrigin(Align.center)
        actor.clearActions()

        if (continuous) {
            actor.addAction(
                Actions.forever(
                    Actions.rotateBy(360f, 1.0f, Interpolation.linear)
                )
            )
        } else {
            actor.addAction(
                Actions.rotateBy(360f, 0.5f, Interpolation.pow2Out)
            )
        }
    }

    // Glow pulse animation (for important elements)
    fun animateGlowPulse(actor: Actor, glowColor: Color = Color.GOLD) {
        val originalColor = actor.color.cpy()

        actor.clearActions()
        actor.addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.color(glowColor, 0.5f, Interpolation.sine),
                    Actions.color(originalColor, 0.5f, Interpolation.sine)
                )
            )
        )
    }

    // Stop all animations on actor
    fun stopAnimations(actor: Actor) {
        actor.clearActions()
    }

    // Delayed action helper
    fun delayedAction(delay: Float, action: () -> Unit): com.badlogic.gdx.scenes.scene2d.Action {
        return Actions.sequence(
            Actions.delay(delay),
            Actions.run { action() }
        )
    }
}

// Extension functions for easier animation access
fun Actor.tapAnimation(isRapidTap: Boolean = false) {
    AnimationSystem.animateKingdomHallTap(this, isRapidTap)
}

fun Actor.buttonPressAnimation() {
    AnimationSystem.animateButtonPress(this)
}

fun Actor.shakeAnimation(intensity: Float = 10f) {
    AnimationSystem.animateShake(this, intensity)
}

fun Actor.bounceAnimation(height: Float = 20f) {
    AnimationSystem.animateBounce(this, height)
}

fun Actor.glowPulseAnimation(glowColor: Color = Color.GOLD) {
    AnimationSystem.animateGlowPulse(this, glowColor)
}

fun Actor.stopAllAnimations() {
    AnimationSystem.stopAnimations(this)
}
