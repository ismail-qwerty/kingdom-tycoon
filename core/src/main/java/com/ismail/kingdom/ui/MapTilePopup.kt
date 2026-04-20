// PATH: core/src/main/java/com/ismail/kingdom/ui/MapTilePopup.kt
package com.ismail.kingdom.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.ismail.kingdom.models.MapTile
import com.ismail.kingdom.models.TileType
import com.ismail.kingdom.utils.Formatters

// Bottom sheet popup for map tile interaction
class MapTilePopup(
    private val tile: MapTile,
    private val skin: Skin,
    private val stage: Stage,
    private val currentGold: Double,
    private val onExploreClicked: (MapTile) -> Unit,
    private val onDismiss: () -> Unit
) : Table() {
    
    private val titleLabel: Label
    private val loreLabel: Label
    private val costLabel: Label
    private val exploreButton: TextButton
    private val rewardLabel: Label
    
    private var typewriterIndex = 0
    private var typewriterTimer = 0f
    private val TYPEWRITER_SPEED = 0.03f // seconds per character
    
    init {
        // Set background
        background = createColorDrawable(Color(0.1f, 0.08f, 0.05f, 0.95f))
        pad(20f)
        
        // Position at bottom of screen (clamped to stay on screen)
        val popupHeight = 300f
        val popupWidth = stage.width.coerceAtMost(600f)
        setSize(popupWidth, popupHeight)
        
        // Center horizontally, position at bottom
        val xPos = (stage.width - popupWidth) / 2f
        setPosition(xPos, -popupHeight)
        
        // Tile type icon
        val iconLabel = Label(getTileTypeEmoji(tile.type), skin, "gold-large")
        iconLabel.setFontScale(2.0f)
        add(iconLabel).padBottom(10f).row()
        
        // Title (tile type name)
        titleLabel = Label(getTileTypeName(tile.type), skin, "gold-large")
        titleLabel.setAlignment(Align.center)
        titleLabel.color = Color.GOLD
        add(titleLabel).fillX().padBottom(15f).row()
        
        if (tile.isRevealed) {
            // Show lore text (already revealed)
            loreLabel = Label(tile.loreText, skin, "body")
            loreLabel.setAlignment(Align.center)
            loreLabel.setWrap(true)
            loreLabel.setFontScale(0.9f)
            loreLabel.color = Color.LIGHT_GRAY
            add(loreLabel).width(stage.width - 60f).padBottom(15f).row()
            
            // Show reward received
            rewardLabel = Label("Reward: +${Formatters.formatGold(tile.goldReward ?: 0.0)}", skin, "gold-small")
            rewardLabel.color = Color.GREEN
            rewardLabel.setAlignment(Align.center)
            add(rewardLabel).fillX().padBottom(10f).row()
            
            costLabel = Label("", skin, "body")
            costLabel.isVisible = false
            exploreButton = TextButton("", skin)
            exploreButton.isVisible = false
            
        } else {
            // Show explore option
            loreLabel = Label("", skin, "body")
            loreLabel.setAlignment(Align.center)
            loreLabel.setWrap(true)
            loreLabel.setFontScale(0.9f)
            loreLabel.color = Color.LIGHT_GRAY
            add(loreLabel).width(stage.width - 60f).height(60f).padBottom(15f).row()
            
            // Cost label
            costLabel = Label("${Formatters.formatGold(tile.exploreCost)} Gold to Explore", skin, "body")
            costLabel.setAlignment(Align.center)
            costLabel.color = Color.YELLOW
            add(costLabel).fillX().padBottom(10f).row()
            
            // Explore button
            val canAfford = currentGold >= tile.exploreCost
            val canExplore = tile.isAdjacentToRevealed && canAfford
            
            exploreButton = TextButton("EXPLORE", skin)
            exploreButton.label.setFontScale(1.2f)
            exploreButton.isDisabled = !canExplore
            exploreButton.color = if (canExplore) Color.GOLD else Color.DARK_GRAY
            
            if (!tile.isAdjacentToRevealed) {
                exploreButton.setText("TOO FAR")
            } else if (!canAfford) {
                exploreButton.setText("CAN'T AFFORD")
            }
            
            exploreButton.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    if (canExplore) {
                        onExplore()
                    }
                }
            })
            
            add(exploreButton).size(200f, 60f).padBottom(10f).row()
            
            rewardLabel = Label("", skin, "body")
            rewardLabel.isVisible = false
        }
        
        // Close button
        val closeButton = TextButton("CLOSE", skin)
        closeButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                dismiss()
            }
        })
        add(closeButton).size(150f, 50f)
        
        // Slide up animation
        addAction(
            Actions.moveTo(0f, 0f, 0.3f, Interpolation.pow2Out)
        )
        
        // Start typewriter effect if revealed
        if (tile.isRevealed && tile.loreText.isNotEmpty()) {
            startTypewriterEffect()
        }
    }
    
    // Gets tile type emoji
    private fun getTileTypeEmoji(type: TileType): String {
        return when (type) {
            TileType.EMPTY -> "🌾"
            TileType.RESOURCE_DEPOSIT -> "💎"
            TileType.ANCIENT_RUINS -> "🏛"
            TileType.ENEMY_CAMP -> "⚔"
            TileType.MERCHANT -> "🏪"
            TileType.QUEST_SITE -> "📜"
            TileType.LEGENDARY_SPOT -> "⭐"
        }
    }
    
    // Gets tile type name
    private fun getTileTypeName(type: TileType): String {
        return when (type) {
            TileType.EMPTY -> "Empty Land"
            TileType.RESOURCE_DEPOSIT -> "Resource Deposit"
            TileType.ANCIENT_RUINS -> "Ancient Ruins"
            TileType.ENEMY_CAMP -> "Enemy Camp"
            TileType.MERCHANT -> "Merchant"
            TileType.QUEST_SITE -> "Quest Site"
            TileType.LEGENDARY_SPOT -> "Legendary Spot"
        }
    }
    
    // Starts typewriter effect for lore text
    private fun startTypewriterEffect() {
        typewriterIndex = 0
        loreLabel.setText("")
        
        addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.delay(TYPEWRITER_SPEED),
                    Actions.run {
                        if (typewriterIndex < tile.loreText.length) {
                            typewriterIndex++
                            loreLabel.setText(tile.loreText.substring(0, typewriterIndex))
                        }
                    }
                )
            )
        )
    }
    
    // Handles explore button click
    private fun onExplore() {
        // Disable button
        exploreButton.isDisabled = true
        exploreButton.color = Color.GRAY
        
        // Callback
        onExploreClicked(tile)
        
        // Show lore text with typewriter
        tile.isRevealed = true
        loreLabel.isVisible = true
        startTypewriterEffect()
        
        // Hide cost and explore button
        costLabel.isVisible = false
        exploreButton.isVisible = false
        
        // Show reward
        rewardLabel.setText("Reward: +${Formatters.formatGold(tile.goldReward ?: 0.0)}")
        rewardLabel.color = Color.GREEN
        rewardLabel.isVisible = true
        
        // Spawn reward popup
        spawnRewardPopup()
        
        // Auto-dismiss after 3 seconds
        addAction(
            Actions.sequence(
                Actions.delay(3.0f),
                Actions.run { dismiss() }
            )
        )
    }
    
    // Spawns reward popup
    private fun spawnRewardPopup() {
        val stageCoords = localToStageCoordinates(Vector2(width / 2f, height + 50f))
        
        val rewardPopup = Label("+${Formatters.formatGold(tile.goldReward ?: 0.0)}", skin, "gold-large")
        rewardPopup.color = Color.GOLD
        rewardPopup.setPosition(stageCoords.x - 50f, stageCoords.y)
        
        rewardPopup.addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.moveBy(0f, 150f, 1.5f, Interpolation.pow2Out),
                    Actions.fadeOut(1.5f)
                ),
                Actions.removeActor()
            )
        )
        
        stage.addActor(rewardPopup)
        
        // Spawn coin particles
        CoinParticlePool.spawn(stageCoords)
    }
    
    // Dismisses popup
    fun dismiss() {
        clearActions()
        addAction(
            Actions.sequence(
                Actions.moveTo(0f, -height, 0.25f, Interpolation.pow2In),
                Actions.run {
                    onDismiss()
                    remove()
                }
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
