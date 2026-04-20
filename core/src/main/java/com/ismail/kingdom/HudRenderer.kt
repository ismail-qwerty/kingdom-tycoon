// PATH: core/src/main/java/com/ismail/kingdom/HudRenderer.kt
package com.ismail.kingdom

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch

// Renders the HUD overlay: gold count, era name, and GPS
class HudRenderer(private val batch: SpriteBatch) {

    private val font = BitmapFont().apply { color = Color.YELLOW }

    // Draws gold, era, and income-per-second text onto the screen
    fun render(state: GameState, gps: Double) {
        val h = Gdx.graphics.height.toFloat()
        font.draw(batch, "Era: ${state.era.displayName}", 16f, h - 16f)
        font.draw(batch, "Gold: ${formatGold(state.gold)}", 16f, h - 40f)
        font.draw(batch, "GPS: ${formatGold(gps)}/s", 16f, h - 64f)
        if (state.canPrestige()) {
            font.draw(batch, "PRESTIGE READY!", 16f, h - 96f)
        }
    }

    // Formats large gold values with K/M/B suffixes
    private fun formatGold(value: Double): String = when {
        value >= 1_000_000_000 -> "%.2fB".format(value / 1_000_000_000)
        value >= 1_000_000     -> "%.2fM".format(value / 1_000_000)
        value >= 1_000         -> "%.2fK".format(value / 1_000)
        else                   -> "%.0f".format(value)
    }

    // Releases font resources
    fun dispose() = font.dispose()
}
