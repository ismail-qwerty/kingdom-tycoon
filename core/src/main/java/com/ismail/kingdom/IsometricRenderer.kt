// PATH: core/src/main/java/com/ismail/kingdom/IsometricRenderer.kt
package com.ismail.kingdom

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion

// Renders the isometric tile grid and any placed buildings
class IsometricRenderer(private val batch: SpriteBatch) {

    private val TILE_W = 128f
    private val TILE_H = 64f
    val MAP_SIZE = 8

    // Lazily loaded tile texture
    private val tileTexture: Texture by lazy { Texture(Gdx.files.internal("tiles/grass.png")) }
    private val tileRegion: TextureRegion by lazy { TextureRegion(tileTexture) }

    // Building textures cached by asset path
    private val buildingTextures = mutableMapOf<String, Texture>()

    // Draws the full isometric map grid
    fun renderMap(offsetX: Float, offsetY: Float) {
        for (row in 0 until MAP_SIZE) {
            for (col in 0 until MAP_SIZE) {
                val (sx, sy) = isoToScreen(col, row, offsetX, offsetY)
                batch.draw(tileRegion, sx - TILE_W / 2f, sy, TILE_W, TILE_H)
            }
        }
    }

    // Draws a building sprite at the given tile coordinate
    fun renderBuilding(col: Int, row: Int, assetPath: String, offsetX: Float, offsetY: Float) {
        val tex = buildingTextures.getOrPut(assetPath) { Texture(Gdx.files.internal(assetPath)) }
        val (sx, sy) = isoToScreen(col, row, offsetX, offsetY)
        batch.draw(tex, sx - TILE_W / 2f, sy, TILE_W, TILE_H * 2f)
    }

    // Converts tile grid coordinates to isometric screen coordinates
    fun isoToScreen(col: Int, row: Int, offsetX: Float, offsetY: Float): Pair<Float, Float> {
        val sx = (col - row) * (TILE_W / 2f) + offsetX
        val sy = (col + row) * (TILE_H / 2f) + offsetY
        return sx to sy
    }

    // Releases all loaded textures
    fun dispose() {
        tileTexture.dispose()
        buildingTextures.values.forEach { it.dispose() }
    }
}
