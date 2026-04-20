// PATH: core/src/main/java/com/ismail/kingdom/LoadingScreen.kt
package com.ismail.kingdom

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.ismail.kingdom.assets.GameAssets
import ktx.app.KtxScreen

// Loading screen shown while assets load
class LoadingScreen(private val game: KingdomTycoonGame) : KtxScreen {
    
    private val shapeRenderer = ShapeRenderer()
    
    // Era 1 background color (warm dirt brown)
    private val backgroundColor = Color(0x8B / 255f, 0x69 / 255f, 0x14 / 255f, 1f)
    
    private val progressBarWidth = 400f
    private val progressBarHeight = 30f
    
    override fun show() {
        // Start loading assets
        GameAssets.load()
    }
    
    override fun render(delta: Float) {
        // Clear screen with era 1 background color
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        
        // Update asset loading
        GameAssets.update()
        
        // Get loading progress
        val progress = GameAssets.getProgress()
        
        // Draw progress bar
        drawProgressBar(progress)
        
        // Check if loading is complete
        if (GameAssets.isLoaded()) {
            ScreenNavigator.navigate(ScreenType.MAIN_MENU, TransitionType.FADE)
        }
    }
    
    // Draws the loading progress bar
    private fun drawProgressBar(progress: Float) {
        val screenWidth = Gdx.graphics.width.toFloat()
        val screenHeight = Gdx.graphics.height.toFloat()
        
        val barX = (screenWidth - progressBarWidth) / 2f
        val barY = (screenHeight - progressBarHeight) / 2f
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        
        // Draw background (dark brown)
        shapeRenderer.color = Color(0.2f, 0.15f, 0.1f, 1f)
        shapeRenderer.rect(barX, barY, progressBarWidth, progressBarHeight)
        
        // Draw progress fill (gold)
        shapeRenderer.color = Color(0.85f, 0.65f, 0.13f, 1f)
        shapeRenderer.rect(barX, barY, progressBarWidth * progress, progressBarHeight)
        
        // Draw border (lighter brown)
        shapeRenderer.end()
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color(0.6f, 0.45f, 0.2f, 1f)
        shapeRenderer.rect(barX, barY, progressBarWidth, progressBarHeight)
        
        shapeRenderer.end()
        
        // Note: Text rendering would require BitmapFont which isn't loaded yet
        // For now, progress bar is visual indicator
        // In production, use a pre-loaded font or draw text after assets load
    }
    
    override fun dispose() {
        shapeRenderer.dispose()
    }
}
