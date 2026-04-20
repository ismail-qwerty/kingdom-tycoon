// PATH: core/src/main/java/com/ismail/kingdom/KingdomGame.kt
package com.ismail.kingdom

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.ismail.kingdom.models.GameState
import com.ismail.kingdom.data.SaveManager

// Main game class
@Deprecated("Use KingdomTycoonGame instead")
class KingdomGame(val adsManager: Any?) : Game() {
    lateinit var batch: SpriteBatch
    lateinit var gameState: GameState

    override fun create() {
        batch = SpriteBatch()
        val prefs = Gdx.app.getPreferences("kingdom_save")
        gameState = SaveManager.load(prefs) ?: GameState()
        Gdx.app.log("KingdomGame", "Game created successfully")
    }

    override fun render() {
        super.render()
    }

    override fun dispose() {
        if (::batch.isInitialized) batch.dispose()
        if (::gameState.isInitialized) {
            val prefs = Gdx.app.getPreferences("kingdom_save")
            SaveManager.save(gameState, prefs)
        }
    }
}
