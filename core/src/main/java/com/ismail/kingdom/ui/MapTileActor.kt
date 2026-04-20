// PATH: core/src/main/java/com/ismail/kingdom/ui/MapTileActor.kt
package com.ismail.kingdom.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.ismail.kingdom.assets.AssetDescriptors
import com.ismail.kingdom.assets.GameAssets
import com.ismail.kingdom.models.MapTile
import com.ismail.kingdom.models.TileType

// Custom actor for rendering a single map tile
class MapTileActor(
    private val tile: MapTile,
    private val eraId: Int,
    private val onTileClicked: (MapTile) -> Unit
) : Actor() {

    private var tileTexture: Texture? = null
    private var fogTexture: Texture? = null
    private var iconTexture: Texture? = null

    private var isSelected = false
    private var fogAlpha = 1.0f
    private var noiseOffset = 0f
    private var borderPulse = 0f

    private val shapeRenderer = ShapeRenderer()

    init {
        setSize(100f, 100f)

        // Load textures
        loadTextures()

        // Add click listener
        addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                onTileClicked(tile)
            }
        })

        // Start fog animation if unrevealed
        if (!tile.isRevealed) {
            startFogAnimation()
        }
    }

    // Loads textures for tile
    private fun loadTextures() {
        try {
            if (GameAssets.isLoaded()) {
                // Load era background as tile base
                tileTexture = GameAssets.getEraBackground(eraId)

                // Load tile type icon
                if (tile.isRevealed) {
                    iconTexture = getTileTypeIcon(tile.type)
                }
            } else {
                createPlaceholderTextures()
            }
        } catch (e: Exception) {
            createPlaceholderTextures()
        }

        // Create fog texture
        fogTexture = createFogTexture()
    }

    // Gets icon texture for tile type
    private fun getTileTypeIcon(type: TileType): Texture? {
        return try {
            when (type) {
                TileType.EMPTY -> null
                TileType.RESOURCE_DEPOSIT -> GameAssets.getTexture(AssetDescriptors.COIN_ICON)
                TileType.ANCIENT_RUINS -> GameAssets.getTexture(AssetDescriptors.CROWN_ICON)
                TileType.ENEMY_CAMP -> GameAssets.getTexture(AssetDescriptors.SETTINGS_ICON)
                TileType.MERCHANT -> GameAssets.getTexture(AssetDescriptors.COIN_ICON)
                TileType.QUEST_SITE -> GameAssets.getTexture(AssetDescriptors.CROWN_ICON)
                TileType.LEGENDARY_SPOT -> GameAssets.getTexture(AssetDescriptors.CROWN_ICON)
            }
        } catch (e: Exception) {
            null
        }
    }

    // Creates placeholder textures
    private fun createPlaceholderTextures() {
        val pixmap = Pixmap(100, 100, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.BROWN)
        pixmap.fill()
        tileTexture = Texture(pixmap)
        pixmap.dispose()
    }

    // Creates fog texture
    private fun createFogTexture(): Texture {
        val pixmap = Pixmap(100, 100, Pixmap.Format.RGBA8888)

        // Create noise pattern
        for (x in 0 until 100) {
            for (y in 0 until 100) {
                val noise = (Math.sin(x * 0.1 + y * 0.1) * 0.1 + 0.9).toFloat()
                val gray = (0.2f * noise).coerceIn(0.15f, 0.25f)
                pixmap.setColor(gray, gray, gray, 1f)
                pixmap.drawPixel(x, y)
            }
        }

        val texture = Texture(pixmap)
        pixmap.dispose()
        return texture
    }

    // Starts fog animation
    private fun startFogAnimation() {
        addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.run { noiseOffset += 0.01f }
                )
            )
        )
    }

    // Draws the tile
    override fun draw(batch: Batch, parentAlpha: Float) {
        val x = this.x
        val y = this.y
        val width = this.width
        val height = this.height

        // Draw base tile texture
        if (tile.isRevealed && tileTexture != null) {
            batch.setColor(1f, 1f, 1f, parentAlpha)
            batch.draw(tileTexture, x, y, width, height)

            // Draw tile type icon
            if (iconTexture != null) {
                val iconSize = 32f
                val iconX = x + (width - iconSize) / 2f
                val iconY = y + (height - iconSize) / 2f
                batch.draw(iconTexture, iconX, iconY, iconSize, iconSize)
            }
        } else {
            // Draw fog
            val fogColor = if (tile.isAdjacentToRevealed) {
                Color(0.3f, 0.3f, 0.3f, fogAlpha * parentAlpha)
            } else {
                Color(0.2f, 0.2f, 0.2f, fogAlpha * parentAlpha)
            }

            batch.setColor(fogColor)
            if (fogTexture != null) {
                batch.draw(fogTexture, x, y, width, height)
            }
        }

        // Draw selection border
        if (isSelected) {
            batch.end()

            shapeRenderer.projectionMatrix = batch.projectionMatrix
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line)

            // Pulsing gold border
            val pulseAlpha = (Math.sin(borderPulse.toDouble()) * 0.3 + 0.7).toFloat()
            shapeRenderer.color = Color(1f, 0.84f, 0f, pulseAlpha * parentAlpha)
            shapeRenderer.rect(x, y, width, height)
            shapeRenderer.rect(x + 2f, y + 2f, width - 4f, height - 4f)

            shapeRenderer.end()
            batch.begin()

            borderPulse += 0.1f
        }
    }

    // Updates tile state
    override fun act(delta: Float) {
        super.act(delta)

        // Update noise animation
        if (!tile.isRevealed) {
            noiseOffset += delta * 2f
        }
    }

    // Reveals the tile with animation
    fun revealTile() {
        if (tile.isRevealed) return

        tile.isRevealed = true

        // Load icon texture
        iconTexture = getTileTypeIcon(tile.type)

        // Fog dissolve animation
        clearActions()
        addAction(
            Actions.sequence(
                Actions.alpha(1f),
                Actions.fadeOut(0.5f, Interpolation.fade),
                Actions.run { fogAlpha = 0f }
            )
        )
    }

    // Sets selection state
    fun setSelected(selected: Boolean) {
        isSelected = selected
        borderPulse = 0f
    }

    // Gets tile
    fun getTile(): MapTile = tile

    // Disposes resources
    fun dispose() {
        fogTexture?.dispose()
        shapeRenderer.dispose()
    }
}
