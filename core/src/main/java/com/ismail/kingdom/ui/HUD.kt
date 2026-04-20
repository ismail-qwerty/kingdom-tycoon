// PATH: core/src/main/java/com/ismail/kingdom/ui/HUD.kt
package com.ismail.kingdom.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align
import com.ismail.kingdom.utils.Formatters

// Manages HUD updates and floating text effects
class HUD(private val stage: Stage, private val skin: Skin) {
    
    private var goldLabel: Label? = null
    private var ipsLabel: Label? = null
    private var speedBoostLabel: Label? = null
    private var prestigeButton: com.badlogic.gdx.scenes.scene2d.ui.TextButton? = null
    
    private var lastGold = 0.0
    
    // Sets gold label reference
    fun setGoldLabel(label: Label) {
        goldLabel = label
    }
    
    // Sets IPS label reference
    fun setIPSLabel(label: Label) {
        ipsLabel = label
    }
    
    // Sets speed boost label reference
    fun setSpeedBoostLabel(label: Label) {
        speedBoostLabel = label
    }
    
    // Sets prestige button reference
    fun setPrestigeButton(button: com.badlogic.gdx.scenes.scene2d.ui.TextButton) {
        prestigeButton = button
    }
    
    // Updates gold display with animation on increase
    fun updateGoldDisplay(gold: Double) {
        goldLabel?.let { label ->
            label.setText(Formatters.formatGold(gold))
            
            if (gold > lastGold) {
                label.clearActions()
                label.addAction(
                    Actions.sequence(
                        Actions.scaleTo(1.2f, 1.2f, 0.1f, Interpolation.pow2Out),
                        Actions.scaleTo(1.0f, 1.0f, 0.1f, Interpolation.pow2In)
                    )
                )
            }
            
            lastGold = gold
        }
    }
    
    // Updates IPS display
    fun updateIPSDisplay(ips: Double) {
        ipsLabel?.setText(Formatters.formatIPS(ips))
    }
    
    // Shows floating tap popup at position
    fun showTapPopup(amount: Double, position: Vector2) {
        val formattedAmount = Formatters.formatGold(amount)
        val popup = Label("+$formattedAmount", skin, "gold-small")
        popup.setPosition(position.x - 50f, position.y)
        popup.color = Color.GOLD
        
        popup.addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.moveBy(0f, 150f, 1.2f, Interpolation.pow2Out),
                    Actions.fadeOut(1.2f)
                ),
                Actions.removeActor()
            )
        )
        
        stage.addActor(popup)
    }
    
    // Shows combo indicator
    fun showComboIndicator(multiplier: Double) {
        val comboLabel = Label("${multiplier.toInt()}x COMBO!", skin, "gold-large")
        comboLabel.setAlignment(Align.center)
        comboLabel.setPosition((stage.width - 400f) / 2f, stage.height * 0.6f)
        comboLabel.setSize(400f, 80f)
        comboLabel.color = Color.ORANGE
        
        comboLabel.addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.scaleTo(1.3f, 1.3f, 0.2f, Interpolation.elasticOut),
                    Actions.alpha(1.0f)
                ),
                Actions.delay(1.5f),
                Actions.parallel(
                    Actions.scaleTo(0.8f, 0.8f, 0.3f),
                    Actions.fadeOut(0.3f)
                ),
                Actions.removeActor()
            )
        )
        
        stage.addActor(comboLabel)
    }
    
    // Shows milestone burst effect
    fun showMilestoneBurst(buildingName: String, count: Int) {
        // Full-screen flash
        val flash = Label("", skin, "body")
        flash.setFillParent(true)
        flash.color = Color(1f, 1f, 1f, 0.5f)
        
        flash.addAction(
            Actions.sequence(
                Actions.fadeOut(0.5f),
                Actions.removeActor()
            )
        )
        
        stage.addActor(flash)
        
        // Milestone text
        val milestoneLabel = Label("$count $buildingName!", skin, "gold-large")
        milestoneLabel.setAlignment(Align.center)
        milestoneLabel.setPosition((stage.width - 600f) / 2f, stage.height * 0.5f)
        milestoneLabel.setSize(600f, 100f)
        milestoneLabel.color = Color.GOLD
        milestoneLabel.setFontScale(2.0f)
        
        milestoneLabel.addAction(
            Actions.sequence(
                Actions.alpha(0f),
                Actions.parallel(
                    Actions.scaleTo(1.5f, 1.5f, 0.3f, Interpolation.bounceOut),
                    Actions.fadeIn(0.3f)
                ),
                Actions.delay(2.0f),
                Actions.parallel(
                    Actions.scaleTo(0.5f, 0.5f, 0.4f),
                    Actions.fadeOut(0.4f)
                ),
                Actions.removeActor()
            )
        )
        
        stage.addActor(milestoneLabel)
    }
    
    // Shows speed boost timer
    fun showSpeedBoostTimer(seconds: Float) {
        speedBoostLabel?.let { label ->
            if (seconds > 0) {
                val timeText = Formatters.formatTime(seconds.toInt())
                label.setText("⚡ $timeText")
                label.isVisible = true
                
                // Pulsing effect
                val pulse = (Math.sin(System.currentTimeMillis() / 200.0) * 0.3 + 0.7).toFloat()
                label.color = Color(1f, 0.5f, 0f, pulse)
            } else {
                label.isVisible = false
            }
        }
    }
    
    // Shows prestige ready pulse
    fun showPrestigeReady() {
        prestigeButton?.let { button ->
            button.clearActions()
            button.addAction(
                Actions.forever(
                    Actions.sequence(
                        Actions.color(Color.GOLD, 0.5f, Interpolation.sine),
                        Actions.color(Color.WHITE, 0.5f, Interpolation.sine)
                    )
                )
            )
        }
    }
    
    // Stops prestige ready pulse
    fun hidePrestigeReady() {
        prestigeButton?.let { button ->
            button.clearActions()
            button.color = Color.WHITE
        }
    }
}
