// PATH: core/src/main/java/com/ismail/kingdom/ui/AdvisorsPanel.kt
package com.ismail.kingdom.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.ismail.kingdom.GameEngine
import com.ismail.kingdom.models.Advisor

// Panel displaying all advisors for current era in 2-column grid
class AdvisorsPanel(
    private val stage: Stage,
    private val skin: Skin,
    private val gameEngine: GameEngine
) : ScrollPane(null, skin) {

    private val advisorRows = mutableMapOf<String, AdvisorRow>()
    private val contentTable = Table()
    private val celebrationBanner: Label

    private var currentEra = 1
    private var updateTimer = 0f
    private val UPDATE_INTERVAL = 0.5f

    private var lastAllAutomatedCheck = false

    init {
        // Configure scroll pane
        setScrollingDisabled(true, false)
        // setClip(true)
        setFadeScrollBars(false)
        setOverscroll(false, false)

        // Create main container
        val mainContainer = Table()
        mainContainer.pad(20f)

        // Celebration banner (initially hidden)
        celebrationBanner = Label("🎉 ALL AUTOMATED 🎉", skin, "gold-large")
        celebrationBanner.setAlignment(Align.center)
        celebrationBanner.color = Color.GOLD
        celebrationBanner.setFontScale(1.5f)
        celebrationBanner.isVisible = false
        mainContainer.add(celebrationBanner).fillX().padBottom(20f).row()

        // Content table (2-column grid)
        contentTable.pad(0f)
        mainContainer.add(contentTable).expand().fill()

        actor = mainContainer

        // Build initial UI
        rebuildForEra(currentEra)
    }

    // Rebuilds panel for specified era
    fun rebuildForEra(eraId: Int) {
        currentEra = eraId

        // Clear existing rows
        contentTable.clear()
        advisorRows.clear()

        // Get advisors for current era
        val eraAdvisors = getEraAdvisors(eraId)

        // Sort: hired first, then by ID
        val sortedAdvisors = eraAdvisors.sortedWith(
            compareByDescending<Advisor> { it.isUnlocked }
                .thenBy { it.id }
        )

        // Create rows in 2-column grid
        var column = 0
        for (advisor in sortedAdvisors) {
            val building = gameEngine.gameState.buildings.find { it.id == advisor.buildingId }
            val row = AdvisorRow(advisor, building, skin) { adv ->
                onAdvisorHire(adv)
            }

            advisorRows[advisor.id] = row

            // Add to grid
            contentTable.add(row).width(250f).height(140f).pad(5f)
            column++

            if (column >= 2) {
                contentTable.row()
                column = 0
            }
        }

        // Check if all automated
        checkAllAutomated()

        layout()
    }

    // Gets advisors for specified era from game state
    private fun getEraAdvisors(era: Int): List<Advisor> {
        return gameEngine.gameState.advisors
            .filter { advisor ->
                // Extract era from buildingId (e.g., "era1_wheat_farm" -> 1)
                val buildingEra = advisor.buildingId.substringAfter("era").substringBefore("_").toIntOrNull() ?: 1
                buildingEra == era
            }
            .sortedBy { it.id }
    }

    // Updates panel (called every frame)
    fun update(delta: Float) {
        updateTimer += delta

        // Update affordability every UPDATE_INTERVAL seconds
        if (updateTimer >= UPDATE_INTERVAL) {
            updateTimer = 0f
            updateAffordability()
        }

        // Check all automated status
        checkAllAutomated()
    }

    // Updates affordability for all advisor rows
    private fun updateAffordability() {
        val currentGold = gameEngine.gameState.currentGold

        for ((advisorId, row) in advisorRows) {
            row.updateAffordability(currentGold)
        }
    }

    // Checks if all era advisors are hired
    private fun checkAllAutomated() {
        val eraAdvisors = getEraAdvisors(currentEra)
        val allHired = eraAdvisors.isNotEmpty() && eraAdvisors.all { it.isUnlocked }

        if (allHired && !lastAllAutomatedCheck) {
            // Show celebration banner
            showCelebrationBanner()
            lastAllAutomatedCheck = true
        } else if (!allHired) {
            celebrationBanner.isVisible = false
            lastAllAutomatedCheck = false
        }
    }

    // Shows celebration banner with animation
    private fun showCelebrationBanner() {
        celebrationBanner.isVisible = true
        celebrationBanner.clearActions()
        celebrationBanner.addAction(
            Actions.sequence(
                Actions.alpha(0f),
                Actions.parallel(
                    Actions.fadeIn(0.5f, Interpolation.fade),
                    Actions.scaleTo(1.3f, 1.3f, 0.5f, Interpolation.elasticOut)
                ),
                Actions.forever(
                    Actions.sequence(
                        Actions.color(Color.GOLD, 0.5f, Interpolation.sine),
                        Actions.color(Color.ORANGE, 0.5f, Interpolation.sine)
                    )
                )
            )
        )

        Gdx.app.log("AdvisorsPanel", "All advisors automated for Era $currentEra!")
    }

    // Handles advisor hire
    private fun onAdvisorHire(advisor: Advisor) {
        val hired = gameEngine.unlockAdvisor(advisor.id)

        if (hired) {
            Gdx.app.log("AdvisorsPanel", "Hired advisor: ${advisor.name}")

            // Rebuild to move hired advisor to top
            rebuildForEra(currentEra)

            // Update immediately
            updateAffordability()
        } else {
            Gdx.app.log("AdvisorsPanel", "Hire failed")
        }
    }

    // Gets automation status for current era
    fun getAutomationStatus(): Pair<Int, Int> {
        val eraAdvisors = getEraAdvisors(currentEra)
        val hiredCount = eraAdvisors.count { it.isUnlocked }
        val totalCount = eraAdvisors.size
        return Pair(hiredCount, totalCount)
    }

    // Refreshes panel (forces immediate update)
    fun refresh() {
        updateAffordability()
        checkAllAutomated()
    }

    // Scrolls to top smoothly
    fun scrollToTop() {
        scrollTo(0f, contentTable.height, 0f, 0f, true, true)
    }
}

// Automation status bar widget for HUD
class AutomationStatusBar(
    private val skin: Skin,
    private val onTapped: () -> Unit
) : Table() {

    private val statusLabel: Label
    private val iconContainer: HorizontalGroup

    init {
        background = createColorDrawable(Color(0.15f, 0.12f, 0.08f, 0.9f))
        pad(8f, 15f, 8f, 15f)

        // Status label
        statusLabel = Label("0/0 Automated", skin, "body")
        statusLabel.setFontScale(0.85f)
        statusLabel.color = Color.LIGHT_GRAY
        add(statusLabel).padRight(10f)

        // Icon container (tiny advisor icons)
        iconContainer = HorizontalGroup()
        iconContainer.space(3f)
        add(iconContainer)

        // Make tappable
        addListener(object : com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            override fun clicked(event: com.badlogic.gdx.scenes.scene2d.InputEvent?, x: Float, y: Float) {
                onTapped()
            }
        })
    }

    // Updates status display
    fun updateStatus(hiredCount: Int, totalCount: Int) {
        statusLabel.setText("$hiredCount/$totalCount Automated")

        // Update color based on progress
        val progress = if (totalCount > 0) hiredCount.toFloat() / totalCount else 0f
        statusLabel.color = when {
            progress >= 1.0f -> Color.GREEN
            progress >= 0.5f -> Color.GOLD
            else -> Color.LIGHT_GRAY
        }

        // Update icons
        iconContainer.clear()
        for (i in 0 until totalCount.coerceAtMost(10)) {
            val icon = createAdvisorIcon(i < hiredCount)
            iconContainer.addActor(icon)
        }
    }

    // Creates tiny advisor icon
    private fun createAdvisorIcon(isHired: Boolean): Image {
        val pixmap = Pixmap(12, 12, Pixmap.Format.RGBA8888)
        pixmap.setColor(if (isHired) Color.GREEN else Color.DARK_GRAY)
        pixmap.fillCircle(6, 6, 5)
        val texture = Texture(pixmap)
        pixmap.dispose()
        return Image(texture)
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
