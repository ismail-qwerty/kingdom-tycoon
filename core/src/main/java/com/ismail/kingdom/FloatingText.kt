// PATH: core/src/main/java/com/ismail/kingdom/FloatingText.kt
package com.ismail.kingdom

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch

// Manages floating "+1" text animations
class FloatingText {
    
    private val LIFETIME = 0.8f
    private val RISE_SPEED = 75f // 60px over 0.8s
    
    private data class Text(
        val text: String,
        var x: Float,
        var y: Float,
        var alpha: Float,
        var lifetime: Float
    )
    
    private val activeTexts = mutableListOf<Text>()
    
    // Adds a new floating text at the given position
    fun add(text: String, x: Float, y: Float) {
        activeTexts.add(Text(text, x, y, 1f, LIFETIME))
    }
    
    // Updates all active floating texts
    fun update(delta: Float) {
        val iterator = activeTexts.iterator()
        while (iterator.hasNext()) {
            val text = iterator.next()
            text.lifetime -= delta
            text.y += RISE_SPEED * delta
            text.alpha = (text.lifetime / LIFETIME).coerceIn(0f, 1f)
            
            if (text.lifetime <= 0f) {
                iterator.remove()
            }
        }
    }
    
    // Renders all active floating texts
    fun render(batch: SpriteBatch, font: BitmapFont) {
        for (text in activeTexts) {
            val oldAlpha = font.color.a
            font.color.a = text.alpha
            font.draw(batch, text.text, text.x, text.y)
            font.color.a = oldAlpha
        }
    }
}
