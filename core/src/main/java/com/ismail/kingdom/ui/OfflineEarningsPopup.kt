// PATH: core/src/main/java/com/ismail/kingdom/ui/OfflineEarningsPopup.kt
package com.ismail.kingdom.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.ismail.kingdom.ads.AdManager
import com.ismail.kingdom.ads.RewardedAdType
import com.ismail.kingdom.data.SessionManager
import com.ismail.kingdom.systems.OfflineEarningsResult
import com.ismail.kingdom.utils.Formatters

// Popup showing offline earnings on game launch
class OfflineEarningsPopup(
    private val stage: Stage,
    private val skin: Skin,
    private val offlineResult: OfflineEarningsResult,
    private val adManager: AdManager?,
    private val onCollect: (Double) -> Unit
) : Table() {
    
    private lateinit var goldLabel: Label
    private lateinit var collectButton: TextButton
    private lateinit var watchAdButton: TextButton
    
    private var countUpTimer = 0f
    private val COUNT_UP_DURATION = 2.0f
    private var currentDisplayGold = 0.0
    private val targetGold = offlineResult.goldEarned
    
    private var isCollected = false
    
    init {
        // Set background
        background = createColorDrawable(Color(0.12f, 0.1f, 0.08f, 0.98f))
        pad(30f)
        
        // Position off-screen at bottom
        val popupWidth = stage.width * 0.85f
        val popupHeight = 500f
        setSize(popupWidth, popupHeight)
        setPosition((stage.width - popupWidth) / 2f, -popupHeight)
        
        // Build content
        buildContent()
        
        // Spawn coin rain particles
        spawnCoinRain()
        
        // Slide up animation
        addAction(
            Actions.moveTo(
                (stage.width - popupWidth) / 2f,
                (stage.height - popupHeight) / 2f,
                0.6f,
                Interpolation.swingOut
            )
        )
    }
    
    // Builds popup content
    private fun buildContent() {
        // Title
        val titleLabel = Label("WELCOME BACK!", skin, "gold-large")
        titleLabel.setAlignment(Align.center)
        titleLabel.color = Color.GOLD
        titleLabel.setFontScale(2.0f)
        add(titleLabel).fillX().padBottom(20f).row()
        
        // Time away
        val timeAwayLabel = Label("You were away for ${offlineResult.formattedTime}", skin, "body")
        timeAwayLabel.setAlignment(Align.center)
        timeAwayLabel.color = Color.LIGHT_GRAY
        timeAwayLabel.setFontScale(1.1f)
        add(timeAwayLabel).fillX().padBottom(25f).row()
        
        // Gold earned (animated count-up)
        goldLabel = Label("0", skin, "gold-large")
        goldLabel.setAlignment(Align.center)
        goldLabel.color = Color.GOLD
        goldLabel.setFontScale(2.5f)
        add(goldLabel).fillX().padBottom(15f).row()
        
        // Pulsing glow animation
        goldLabel.addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.color(Color.GOLD, 0.5f, Interpolation.sine),
                    Actions.color(Color.ORANGE, 0.5f, Interpolation.sine)
                )
            )
        )
        
        // IPS reminder
        val ips = if (offlineResult.secondsCalculated > 0) {
            offlineResult.goldEarned / offlineResult.secondsCalculated
        } else {
            0.0
        }
        val ipsLabel = Label("Your kingdom earned ${Formatters.formatIPS(ips)} while you slept", skin, "body")
        ipsLabel.setAlignment(Align.center)
        ipsLabel.color = Color.GREEN
        ipsLabel.setFontScale(0.95f)
        add(ipsLabel).fillX().padBottom(20f).row()
        
        // Cap warning (if capped)
        if (offlineResult.wasCapped) {
            val capLabel = Label("⚠ Earnings capped at 8 hours", skin, "body")
            capLabel.setAlignment(Align.center)
            capLabel.color = Color.YELLOW
            capLabel.setFontScale(0.9f)
            add(capLabel).fillX().padBottom(10f).row()
            
            val adHintLabel = Label("Watch an ad to collect 2x!", skin, "body")
            adHintLabel.setAlignment(Align.center)
            adHintLabel.color = Color.CYAN
            adHintLabel.setFontScale(0.85f)
            add(adHintLabel).fillX().padBottom(20f).row()
        }
        
        // Buttons
        val buttonsTable = createButtons()
        add(buttonsTable).fillX()
    }
    
    // Creates buttons
    private fun createButtons(): Table {
        val table = Table()
        
        // Collect button
        collectButton = TextButton("COLLECT", skin)
        collectButton.label.setFontScale(1.3f)
        collectButton.color = Color.GREEN
        collectButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (!isCollected) {
                    onCollectClicked(1.0)
                }
            }
        })
        table.add(collectButton).size(200f, 70f).pad(10f)
        
        // Watch ad button (only if capped and ad available)
        if (offlineResult.wasCapped && adManager != null) {
            watchAdButton = TextButton("📺 WATCH AD\nCOLLECT 2X", skin)
            watchAdButton.label.setFontScale(1.1f)
            watchAdButton.label.setAlignment(Align.center)
            watchAdButton.color = Color.GOLD
            watchAdButton.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    if (!isCollected) {
                        onWatchAdClicked()
                    }
                }
            })
            table.add(watchAdButton).size(200f, 70f).pad(10f)
        }
        
        return table
    }
    
    // Updates count-up animation
    fun update(delta: Float) {
        if (countUpTimer < COUNT_UP_DURATION) {
            countUpTimer += delta
            val progress = (countUpTimer / COUNT_UP_DURATION).coerceIn(0f, 1f)
            
            // Ease out interpolation for smooth count-up
            val easedProgress = Interpolation.pow2Out.apply(progress)
            currentDisplayGold = targetGold * easedProgress
            
            goldLabel.setText(Formatters.formatGold(currentDisplayGold))
        } else if (countUpTimer >= COUNT_UP_DURATION && currentDisplayGold < targetGold) {
            // Ensure final value is exact
            currentDisplayGold = targetGold
            goldLabel.setText(Formatters.formatGold(currentDisplayGold))
        }
    }
    
    // Handles collect button
    private fun onCollectClicked(multiplier: Double) {
        isCollected = true
        
        val finalAmount = offlineResult.goldEarned * multiplier
        
        // Button animation
        collectButton.clearActions()
        collectButton.addAction(
            Actions.sequence(
                Actions.scaleTo(1.2f, 1.2f, 0.15f, Interpolation.elasticOut),
                Actions.scaleTo(1.0f, 1.0f, 0.15f, Interpolation.elasticIn)
            )
        )
        
        // Spawn coin burst
        spawnCoinBurst()
        
        // Mark as shown
        SessionManager.markOfflineEarningsShown()
        
        // Callback
        onCollect(finalAmount)
        
        // Dismiss popup
        dismiss()
    }
    
    // Handles watch ad button
    private fun onWatchAdClicked() {
        watchAdButton.isDisabled = true
        watchAdButton.setText("Loading...")
        
        adManager?.requestDoubleOffline(offlineResult.goldEarned) { finalGold: Double ->
            Gdx.app.log("OfflineEarningsPopup", "Ad watched successfully, collecting 2x")
            onCollectClicked(finalGold / offlineResult.goldEarned)
        }
    }
    
    // Spawns coin rain particles
    private fun spawnCoinRain() {
        // Spawn 20 coins falling from top
        for (i in 0..19) {
            val randomX = (Math.random() * stage.width).toFloat()
            val randomDelay = (Math.random() * 2.0).toFloat()
            
            val coinPosition = Vector2(randomX, stage.height + 50f)
            
            // Delay spawn for staggered effect
            stage.addAction(
                Actions.sequence(
                    Actions.delay(randomDelay),
                    Actions.run {
                        CoinParticlePool.spawn(coinPosition)
                    }
                )
            )
        }
    }
    
    // Spawns coin burst from center
    private fun spawnCoinBurst() {
        val centerX = x + width / 2f
        val centerY = y + height / 2f
        
        for (i in 0..15) {
            CoinParticlePool.spawn(Vector2(centerX, centerY))
        }
    }
    
    // Dismisses popup
    private fun dismiss() {
        clearActions()
        addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.moveBy(0f, -height - 100f, 0.4f, Interpolation.pow2In),
                    Actions.fadeOut(0.4f)
                ),
                Actions.removeActor()
            )
        )
    }
    
    // Creates colored drawable
    private fun createColorDrawable(color: Color): TextureRegionDrawable? {
        return try {
            val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
            pixmap.setColor(color)
            pixmap.fill()
            val texture = Texture(pixmap)
            pixmap.dispose()
            TextureRegionDrawable(texture)
        } catch (e: Exception) {
            null
        }
    }
}

// Dark overlay for popup backdrop
class OfflineEarningsOverlay(
    private val stage: Stage,
    private val popup: OfflineEarningsPopup
) : Table() {
    
    init {
        setFillParent(true)
        background = createColorDrawable(Color(0f, 0f, 0f, 0.8f))
        
        // Fade in
        color = Color(1f, 1f, 1f, 0f)
        addAction(Actions.fadeIn(0.3f))
        
        // Add popup
        add(popup).center()
    }
    
    // Updates popup
    fun update(delta: Float) {
        popup.update(delta)
    }
    
    // Creates colored drawable
    private fun createColorDrawable(color: Color): TextureRegionDrawable? {
        return try {
            val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
            pixmap.setColor(color)
            pixmap.fill()
            val texture = Texture(pixmap)
            pixmap.dispose()
            TextureRegionDrawable(texture)
        } catch (e: Exception) {
            null
        }
    }
}
