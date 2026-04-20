// PATH: core/src/main/java/com/ismail/kingdom/ui/EventBanner.kt
package com.ismail.kingdom.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.ismail.kingdom.models.KingdomEvent
import com.ismail.kingdom.models.KingdomEventType

// Notification banner that slides down when event starts
class EventBanner(
    private val event: KingdomEvent,
    private val skin: Skin,
    private val stage: Stage,
    private val onTapped: () -> Unit
) : Table() {

    private val BANNER_HEIGHT = 80f
    private val DISPLAY_DURATION = 5.0f

    init {
        // Set background with event-themed color
        background = createColorDrawable(getEventColor(event.type))
        pad(15f)

        // Position at top, off-screen initially
        setSize(stage.width, BANNER_HEIGHT)
        setPosition(0f, stage.height)

        // Event icon
        val iconLabel = Label(getEventIcon(event.type), skin, "gold-large")
        iconLabel.setFontScale(1.5f)
        add(iconLabel).padRight(15f)

        // Event name
        val nameLabel = Label(event.name, skin, "gold-large")
        nameLabel.setAlignment(Align.left)
        nameLabel.color = Color.WHITE
        add(nameLabel).expandX().left()

        // "NEW!" badge
        val newBadge = Label("NEW!", skin, "body")
        newBadge.color = Color.YELLOW
        newBadge.setFontScale(0.9f)
        add(newBadge).padLeft(10f)

        // Make tappable
        addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                onTapped()
                dismiss()
            }
        })

        // Slide down animation
        slideDown()
    }

    // Gets event icon emoji
    private fun getEventIcon(type: KingdomEventType): String {
        return when (type) {
            KingdomEventType.GOBLIN_RAID -> "⚔️"
            KingdomEventType.ROYAL_FESTIVAL -> "🎉"
            KingdomEventType.MERCHANT_CARAVAN -> "🏪"
            KingdomEventType.DRAGON_SIGHTING -> "🐉"
            KingdomEventType.PLAGUE_OF_FROGS -> "🐸"
            KingdomEventType.HARVEST_MOON -> "🌕"
            KingdomEventType.GOLD_RUSH -> "💰"
            KingdomEventType.DOUBLE_INCOME -> "📈"
            KingdomEventType.TAP_BONUS -> "👆"
            KingdomEventType.BUILDING_DISCOUNT -> "📉"
            KingdomEventType.CROWN_SHARD_BONUS -> "💎"
            KingdomEventType.SPECIAL_QUEST -> "📜"
        }
    }

    // Gets event color
    private fun getEventColor(type: KingdomEventType): Color {
        return when (type) {
            KingdomEventType.GOBLIN_RAID -> Color(0.6f, 0.2f, 0.2f, 0.95f) // Dark red
            KingdomEventType.ROYAL_FESTIVAL -> Color(0.6f, 0.3f, 0.8f, 0.95f) // Purple
            KingdomEventType.MERCHANT_CARAVAN -> Color(0.2f, 0.5f, 0.3f, 0.95f) // Green
            KingdomEventType.DRAGON_SIGHTING -> Color(0.8f, 0.4f, 0.1f, 0.95f) // Orange
            KingdomEventType.PLAGUE_OF_FROGS -> Color(0.3f, 0.6f, 0.3f, 0.95f) // Light green
            KingdomEventType.HARVEST_MOON -> Color(0.8f, 0.7f, 0.3f, 0.95f) // Gold
            KingdomEventType.GOLD_RUSH -> Color.GOLD
            KingdomEventType.DOUBLE_INCOME -> Color.SKY
            KingdomEventType.TAP_BONUS -> Color.WHITE
            KingdomEventType.BUILDING_DISCOUNT -> Color.PURPLE
            KingdomEventType.CROWN_SHARD_BONUS -> Color.CYAN
            KingdomEventType.SPECIAL_QUEST -> Color.YELLOW
        }
    }

    // Slides banner down from top
    private fun slideDown() {
        clearActions()
        addAction(
            Actions.sequence(
                Actions.moveTo(0f, stage.height - BANNER_HEIGHT, 0.5f, Interpolation.pow2Out),
                Actions.delay(DISPLAY_DURATION),
                Actions.run { slideUp() }
            )
        )
    }

    // Slides banner up and removes
    private fun slideUp() {
        clearActions()
        addAction(
            Actions.sequence(
                Actions.moveTo(0f, stage.height, 0.4f, Interpolation.pow2In),
                Actions.removeActor()
            )
        )
    }

    // Dismisses banner immediately
    fun dismiss() {
        clearActions()
        addAction(
            Actions.sequence(
                Actions.moveTo(0f, stage.height, 0.3f, Interpolation.pow2In),
                Actions.removeActor()
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
