// PATH: core/src/main/java/com/ismail/kingdom/ui/QuestsPanel.kt
package com.ismail.kingdom.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.ismail.kingdom.GameEngine
import com.ismail.kingdom.models.Quest
import com.ismail.kingdom.utils.Formatters

// Panel displaying active and completed quests
class QuestsPanel(
    private val stage: Stage,
    private val skin: Skin,
    private val gameEngine: GameEngine
) : ScrollPane(null, skin) {

    private val questCards = mutableListOf<QuestCard>()
    private val completedCards = mutableListOf<CompletedQuestCard>()

    private val activeQuestsContainer: Table
    private val completedQuestsContainer: VerticalGroup
    private val refreshTimerLabel: Label

    private var updateTimer = 0f
    private val UPDATE_INTERVAL = 1.0f // Update every second for timers

    private val MAX_ACTIVE_QUESTS = 3
    private val MAX_COMPLETED_DISPLAY = 5

    init {
        // Configure scroll pane
        setScrollingDisabled(true, false)
        // setClip(true)
        setFadeScrollBars(false)
        setOverscroll(false, false)

        // Main container
        val mainContainer = Table()
        mainContainer.pad(20f)

        // Title
        val titleLabel = Label("ACTIVE QUESTS", skin, "gold-large")
        titleLabel.setAlignment(Align.center)
        titleLabel.color = Color.GOLD
        mainContainer.add(titleLabel).fillX().padBottom(15f).row()

        // Active quests container (3 cards)
        activeQuestsContainer = Table()
        activeQuestsContainer.pad(0f)
        mainContainer.add(activeQuestsContainer).fillX().padBottom(30f).row()

        // Divider
        val divider = createDivider()
        mainContainer.add(divider).fillX().height(2f).padBottom(20f).row()

        // Completed section title
        val completedTitleLabel = Label("COMPLETED", skin, "body")
        completedTitleLabel.setAlignment(Align.center)
        completedTitleLabel.color = Color.GRAY
        completedTitleLabel.setFontScale(1.1f)
        mainContainer.add(completedTitleLabel).fillX().padBottom(10f).row()

        // Completed quests container
        completedQuestsContainer = VerticalGroup()
        completedQuestsContainer.space(8f)
        completedQuestsContainer.align(Align.top)
        mainContainer.add(completedQuestsContainer).fillX().padBottom(30f).row()

        // Refresh timer
        refreshTimerLabel = Label("Next refresh in: --:--", skin, "body")
        refreshTimerLabel.setAlignment(Align.center)
        refreshTimerLabel.color = Color.YELLOW
        refreshTimerLabel.setFontScale(0.9f)
        mainContainer.add(refreshTimerLabel).fillX()

        actor = mainContainer

        // Build initial UI
        rebuildQuests()
    }

    // Rebuilds quest cards
    fun rebuildQuests() {
        // Clear existing cards
        activeQuestsContainer.clear()
        questCards.clear()

        // Get active quests
        val activeQuests = gameEngine.gameState.activeQuests.take(MAX_ACTIVE_QUESTS)

        // Create cards for active quests
        for (quest in activeQuests) {
            val card = QuestCard(quest, skin, stage) { q ->
                onQuestClaim(q)
            }

            questCards.add(card)
            activeQuestsContainer.add(card).fillX().height(200f).padBottom(15f).row()
        }

        // Fill empty slots with placeholder
        for (i in activeQuests.size until MAX_ACTIVE_QUESTS) {
            val placeholder = createPlaceholderCard()
            activeQuestsContainer.add(placeholder).fillX().height(200f).padBottom(15f).row()
        }

        // Rebuild completed quests
        rebuildCompletedQuests()

        layout()
    }

    // Rebuilds completed quests section
    private fun rebuildCompletedQuests() {
        completedQuestsContainer.clear()
        completedCards.clear()

        // Get last 5 completed quests (placeholder - need completed quests list in GameState)
        val completedQuests = getRecentCompletedQuests()

        if (completedQuests.isEmpty()) {
            val emptyLabel = Label("No completed quests yet", skin, "body")
            emptyLabel.color = Color.DARK_GRAY
            emptyLabel.setFontScale(0.85f)
            completedQuestsContainer.addActor(emptyLabel)
        } else {
            for (quest in completedQuests) {
                val card = CompletedQuestCard(quest, skin)
                completedCards.add(card)
                completedQuestsContainer.addActor(card)
            }
        }
    }

    // Gets recent completed quests
    private fun getRecentCompletedQuests(): List<Quest> {
        // Placeholder - in production, get from GameState.completedQuests
        return emptyList()
    }

    // Creates placeholder card for empty quest slot
    private fun createPlaceholderCard(): Table {
        val table = Table()
        table.background = createColorDrawable(Color(0.15f, 0.12f, 0.09f, 0.5f))
        table.pad(15f)

        val label = Label("Quest Slot Available", skin, "body")
        label.color = Color.DARK_GRAY
        label.setAlignment(Align.center)
        table.add(label).expand().center()

        return table
    }

    // Creates divider line
    private fun createDivider(): Image {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.GRAY)
        pixmap.fill()
        val texture = Texture(pixmap)
        pixmap.dispose()
        return Image(texture)
    }

    // Updates panel (called every frame)
    fun update(delta: Float) {
        updateTimer += delta

        // Update every second
        if (updateTimer >= UPDATE_INTERVAL) {
            updateTimer = 0f
            updateQuests()
            updateRefreshTimer()
        }
    }

    // Updates quest progress and timers
    private fun updateQuests() {
        val activeQuests = gameEngine.gameState.activeQuests.take(MAX_ACTIVE_QUESTS)

        for (i in activeQuests.indices) {
            if (i < questCards.size) {
                val quest = activeQuests[i]
                val card = questCards[i]

                // Update progress
                card.updateProgress(quest.currentValue)

                // Update time remaining
                if ((quest.timeLimit ?: 0) > 0) {
                    val timeRemaining = calculateTimeRemaining(quest)
                    card.updateTimeRemaining(timeRemaining)
                }
            }
        }
    }

    // Calculates time remaining for quest
    private fun calculateTimeRemaining(quest: Quest): Int {
        // Placeholder - in production, calculate from quest.startTime + timeLimit - currentTime
        return quest.timeLimit ?: 0
    }

    // Updates refresh timer
    private fun updateRefreshTimer() {
        // Placeholder - in production, get from QuestSystem
        val hoursRemaining = 4
        val minutesRemaining = 23

        refreshTimerLabel.setText("Next refresh in: ${hoursRemaining}h ${minutesRemaining}m")
    }

    // Handles quest claim
    private fun onQuestClaim(quest: Quest) {
        val result = gameEngine.completeQuest(quest.id)

        val completed = gameEngine.completeQuest(quest.id)
        if (completed) {
            Gdx.app.log("QuestsPanel", "Quest claimed: ${quest.title}")

            // Find the card and trigger slide-out
            val cardIndex = questCards.indexOfFirst { it == questCards.firstOrNull() }
            if (cardIndex >= 0 && cardIndex < questCards.size) {
                val card = questCards[cardIndex]

                card.slideOut {
                    // After slide-out, rebuild quests
                    rebuildQuests()

                    // Slide in new quest if available
                    if (cardIndex < questCards.size) {
                        questCards[cardIndex].slideIn()
                    }
                }
            } else {
                // Fallback: just rebuild
                rebuildQuests()
            }
        } else {
            Gdx.app.log("QuestsPanel", "Quest claim failed")
        }
    }

    // Triggers completion animation for quest
    fun triggerQuestCompletion(questId: String) {
        val quest = gameEngine.gameState.activeQuests.find { it.id == questId }
        if (quest != null) {
            val cardIndex = gameEngine.gameState.activeQuests.indexOf(quest)
            if (cardIndex >= 0 && cardIndex < questCards.size) {
                questCards[cardIndex].playCompletionAnimation()
            }
        }
    }

    // Refreshes panel (forces immediate update)
    fun refresh() {
        rebuildQuests()
    }

    // Scrolls to top smoothly
    fun scrollToTop() {
        scrollTo(0f, activeQuestsContainer.height, 0f, 0f, true, true)
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
