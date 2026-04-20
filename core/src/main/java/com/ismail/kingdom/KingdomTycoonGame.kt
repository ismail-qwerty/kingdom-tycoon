// PATH: core/src/main/java/com/ismail/kingdom/KingdomTycoonGame.kt
package com.ismail.kingdom

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.ismail.kingdom.ads.AdsInterface
import com.ismail.kingdom.data.SaveManager
import com.ismail.kingdom.data.SessionManager
import com.ismail.kingdom.data.SettingsManager
import ktx.app.KtxGame
import ktx.app.KtxScreen

class KingdomTycoonGame(val adsManager: AdsInterface? = null) : KtxGame<KtxScreen>() {

    lateinit var batch: SpriteBatch
    lateinit var assets: AssetManager
    lateinit var font: BitmapFont

    lateinit var gameEngine: GameEngine
        private set

    lateinit var saveManager: SaveManager
        private set

    override fun create() {
        batch = SpriteBatch()
        assets = AssetManager()
        font = BitmapFont()

        SessionManager.initialize()
        SettingsManager.initialize()

        val prefs = Gdx.app.getPreferences("kingdom_save")
        saveManager = SaveManager(prefs)
        gameEngine = GameEngine(prefs, adsManager)

        // FIX: Initialize navigator FIRST, then navigate via it only.
        // Never call addScreen() + setScreen() manually alongside ScreenNavigator —
        // doing so registers a screen instance that the navigator then tries to
        // replace, causing GdxRuntimeException: "screen already exists".
        ScreenNavigator.initialize(this)
        ScreenNavigator.navigate(ScreenType.LOADING, TransitionType.NONE)
    }

    override fun render() {
        super.render()
        saveManager.updateAutoSave(Gdx.graphics.deltaTime, gameEngine.gameState)
        ScreenNavigator.update(Gdx.graphics.deltaTime)
        ScreenNavigator.render()
    }

    override fun dispose() {
        batch.dispose()
        assets.dispose()
        font.dispose()
        ScreenNavigator.dispose()
        super.dispose()
    }
}