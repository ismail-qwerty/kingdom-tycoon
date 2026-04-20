// PATH: core/src/main/java/com/ismail/kingdom/ui/VirtualScrollList.kt
package com.ismail.kingdom.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Pool

// Virtual scrolling list that only renders visible items
class VirtualScrollList(
    private val itemCount: Int,
    private val itemHeight: Float,
    private val renderItem: (index: Int, actor: Actor) -> Unit,
    skin: Skin
) : ScrollPane(null, skin) {

    private val BUFFER_ITEMS = 2 // Render 2 extra items above/below viewport

    private val container = Table()
    private val actorPool = object : Pool<Actor>() {
        override fun newObject(): Actor = Actor()
    }

    private var lastVisibleStart = -1
    private var lastVisibleEnd = -1

    init {
        actor = container
        setScrollingDisabled(true, false)
        // setClip(true)
        setFadeScrollBars(false)

        // Set container height to accommodate all items
        container.height = itemCount * itemHeight

        // Initial render
        updateVisibleItems()
    }

    // Updates which items are visible and renders only those
    fun updateVisibleItems() {
        val scrollY = scrollY
        val viewportHeight = height

        // Calculate visible range
        val visibleStart = ((scrollY / itemHeight).toInt() - BUFFER_ITEMS).coerceAtLeast(0)
        val visibleEnd = (((scrollY + viewportHeight) / itemHeight).toInt() + BUFFER_ITEMS).coerceAtMost(itemCount - 1)

        // Only update if range changed
        if (visibleStart == lastVisibleStart && visibleEnd == lastVisibleEnd) {
            return
        }

        lastVisibleStart = visibleStart
        lastVisibleEnd = visibleEnd

        // Clear container
        container.clear()

        // Add spacer for items above viewport
        if (visibleStart > 0) {
            container.add().height(visibleStart * itemHeight).row()
        }

        // Render visible items
        for (i in visibleStart..visibleEnd) {
            val actor = actorPool.obtain()
            renderItem(i, actor)
            container.add(actor).height(itemHeight).fillX().row()
        }

        // Add spacer for items below viewport
        val remainingItems = itemCount - visibleEnd - 1
        if (remainingItems > 0) {
            container.add().height(remainingItems * itemHeight).row()
        }

        container.invalidate()
    }

    override fun act(delta: Float) {
        super.act(delta)
        updateVisibleItems()
    }

    // Refreshes all visible items
    fun refresh() {
        lastVisibleStart = -1
        lastVisibleEnd = -1
        updateVisibleItems()
    }
}
