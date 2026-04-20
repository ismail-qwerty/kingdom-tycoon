// PATH: core/src/main/java/com/ismail/kingdom/MainMenuScreen.kt
package com.ismail.kingdom

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import ktx.app.KtxScreen

// Main menu screen showing title and tap-to-start prompt
class MainMenuScreen(private val game: KingdomTycoonGame) : KtxScreen {

    private val titleFont = BitmapFont().apply {
        data.setScale(4f)
        color = Color.GOLD
    }

    private val promptFont = BitmapFont().apply {
        data.setScale(2f)
        color = Color.WHITE
    }

    private val titleLayout = GlyphLayout()
    private val promptLayout = GlyphLayout()

    private val titleText = "Kingdom Tycoon"
    private val promptText = "Tap to Start"

    // Renders the menu screen with centered text
    override fun render(delta: Float) {
        handleInput()

        Gdx.gl.glClearColor(0.1f, 0.12f, 0.15f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.begin()

        titleLayout.setText(titleFont, titleText)
        val titleX = (Gdx.graphics.width - titleLayout.width) / 2f
        val titleY = Gdx.graphics.height * 0.66f
        titleFont.draw(game.batch, titleText, titleX, titleY)

        promptLayout.setText(promptFont, promptText)
        val promptX = (Gdx.graphics.width - promptLayout.width) / 2f
        val promptY = Gdx.graphics.height * 0.4f
        promptFont.draw(game.batch, promptText, promptX, promptY)

        game.batch.end()
    }

    // Handles tap input to transition to GameScreen
    private fun handleInput() {
        if (Gdx.input.justTouched()) {
            // GameEngine.initialize() already handles load-or-new internally
            game.gameEngine.initialize()
            Gdx.app.log("MainMenuScreen", "Game initialized")
            ScreenNavigator.navigate(ScreenType.GAME, TransitionType.FADE)
        }
    }

    override fun dispose() {
        titleFont.dispose()
        promptFont.dispose()
    }
}
