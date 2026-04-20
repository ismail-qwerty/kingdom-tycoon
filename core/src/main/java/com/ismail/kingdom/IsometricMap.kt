// PATH: core/src/main/java/com/ismail/kingdom/IsometricMap.kt
package com.ismail.kingdom

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

// Renders a 10x10 isometric diamond grid with alternating grass/dirt tiles
class IsometricMap {
    
    private val TILE_WIDTH = 128f
    private val TILE_HEIGHT = 64f
    private val MAP_SIZE = 10
    
    private val grassTexture: Texture by lazy { Texture(Gdx.files.internal("tiles/grass.png")) }
    private val dirtTexture: Texture by lazy { Texture(Gdx.files.internal("tiles/dirt.png")) }
    
    // Renders the full 10x10 grid centered at the given camera position
    fun render(batch: SpriteBatch, cameraX: Float, cameraY: Float) {
        // Render back-to-front for proper isometric depth: iterate diagonals from top to bottom
        for (sum in 0 until MAP_SIZE * 2 - 1) {
            for (col in 0 until MAP_SIZE) {
                val row = sum - col
                if (row in 0 until MAP_SIZE) {
                    val (screenX, screenY) = isoToScreen(col, row)
                    val texture = if ((col + row) % 2 == 0) grassTexture else dirtTexture
                    batch.draw(texture, screenX + cameraX, screenY + cameraY, TILE_WIDTH, TILE_HEIGHT)
                }
            }
        }
    }
    
    // Converts grid coordinates to isometric screen coordinates
    private fun isoToScreen(col: Int, row: Int): Pair<Float, Float> {
        val x = (col - row) * (TILE_WIDTH / 2f)
        val y = (col + row) * (TILE_HEIGHT / 2f)
        return x to y
    }
    
    // Returns the world-space center point of the map
    fun getCenterOffset(): Pair<Float, Float> {
        val centerCol = MAP_SIZE / 2
        val centerRow = MAP_SIZE / 2
        val (cx, cy) = isoToScreen(centerCol, centerRow)
        return -cx to -cy
    }
    
    // Releases texture resources
    fun dispose() {
        grassTexture.dispose()
        dirtTexture.dispose()
    }
}
