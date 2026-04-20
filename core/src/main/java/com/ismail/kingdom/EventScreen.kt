// PATH: core/src/main/java/com/ismail/kingdom/EventScreen.kt
package com.ismail.kingdom
 
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.ismail.kingdom.ads.RewardedAdType
import com.ismail.kingdom.models.KingdomEvent
import com.ismail.kingdom.models.KingdomEventType
import com.ismail.kingdom.utils.Formatters
import ktx.app.KtxScreen
 
// Event screen showing active and past events
class EventScreen(private val game: KingdomTycoonGame) : KtxScreen {
 
    private val stage = Stage(ScreenViewport())
    private val skin = Skin()
 
    private lateinit var activeEventCard: Table
    private lateinit var timeRemainingLabel: Label
    private lateinit var bonusDescriptionLabel: Label
    private lateinit var doubleAdButton: TextButton
    private lateinit var countdownLabel: Label
    private lateinit var historyContainer: VerticalGroup
 
    private var updateTimer = 0f
    private val UPDATE_INTERVAL = 1.0f
 
    override fun show() {
        Gdx.input.inputProcessor = stage
 
        // Create skin
        createSkin()
 
        // Build UI
        buildUI()
    }
 
    // Creates UI skin
    private fun createSkin() {
        val font = com.badlogic.gdx.graphics.g2d.BitmapFont()
 
        val labelStyle = Label.LabelStyle(font, Color.WHITE)
        skin.add("default", labelStyle)
        skin.add("body", labelStyle)
 
        val goldLabelStyle = Label.LabelStyle(font, Color.GOLD)
        skin.add("gold-small", goldLabelStyle)
        skin.add("gold-large", goldLabelStyle)
 
        val buttonStyle = TextButton.TextButtonStyle()
        buttonStyle.font = font
        buttonStyle.fontColor = Color.WHITE
        skin.add("default", buttonStyle)
    }
 
    // Builds UI layout
    private fun buildUI() {
        val root = Table()
        root.setFillParent(true)
        root.background = createColorDrawable(Color(0.08f, 0.06f, 0.04f, 1f))
        stage.addActor(root)
 
        // Title bar
        val titleBar = createTitleBar()
        root.add(titleBar).fillX().height(80f).row()
 
        // Scroll container
        val scrollPane = ScrollPane(createContent(), skin)
        scrollPane.setScrollingDisabled(true, false)
        scrollPane.setFadeScrollBars(false)
        root.add(scrollPane).expand().fill()
    }
 
    // Creates title bar
    private fun createTitleBar(): Table {
        val table = Table()
        table.background = createColorDrawable(Color(0.15f, 0.12f, 0.08f, 1f))
        table.pad(15f)
 
        // Back button
        val backButton = TextButton("← BACK", skin)
        backButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                onBackClicked()
            }
        })
        table.add(backButton).size(120f, 50f).left().expandX()
 
        // Title
        val titleLabel = Label("KINGDOM EVENTS", skin, "gold-large")
        titleLabel.setAlignment(Align.center)
        titleLabel.color = Color.GOLD
        table.add(titleLabel).center().expandX()
 
        // Spacer
        table.add().size(120f, 50f).right().expandX()
 
        return table
    }
 
    // Creates content
    private fun createContent(): Table {
        val content = Table()
        content.pad(20f)
 
        val currentEvent = game.gameEngine.gameState.currentEvent
 
        if (currentEvent != null) {
            // Active event card
            activeEventCard = createActiveEventCard(currentEvent)
            content.add(activeEventCard).fillX().padBottom(30f).row()
        } else {
            // No active event - show countdown
            val noEventCard = createNoEventCard()
            content.add(noEventCard).fillX().padBottom(30f).row()
        }
 
        // Divider
        val divider = createDivider()
        content.add(divider).fillX().height(2f).padBottom(20f).row()
 
        // Event history
        val historyTitle = Label("EVENT HISTORY", skin, "body")
        historyTitle.setAlignment(Align.center)
        historyTitle.color = Color.GRAY
        historyTitle.setFontScale(1.1f)
        content.add(historyTitle).fillX().padBottom(15f).row()
 
        historyContainer = VerticalGroup()
        historyContainer.space(10f)
        historyContainer.align(Align.top)
 
        // Add last 3 events (placeholder)
        val pastEvents = getPastEvents()
        if (pastEvents.isEmpty()) {
            val emptyLabel = Label("No past events", skin, "body")
            emptyLabel.color = Color.DARK_GRAY
            emptyLabel.setFontScale(0.9f)
            historyContainer.addActor(emptyLabel)
        } else {
            for (event in pastEvents) {
                val historyCard = createHistoryCard(event)
                historyContainer.addActor(historyCard)
            }
        }
 
        content.add(historyContainer).fillX()
 
        return content
    }
 
    // Creates active event card
    private fun createActiveEventCard(event: KingdomEvent): Table {
        val card = Table()
        card.background = createColorDrawable(getEventColor(event.type))
        card.pad(20f)
 
        // Event icon (large)
        val iconLabel = Label(getEventIcon(event.type), skin, "gold-large")
        iconLabel.setFontScale(3.0f)
        card.add(iconLabel).padBottom(15f).row()
 
        // Event name
        val nameLabel = Label(event.name, skin, "gold-large")
        nameLabel.setAlignment(Align.center)
        nameLabel.color = Color.WHITE
        nameLabel.setFontScale(1.5f)
        card.add(nameLabel).fillX().padBottom(10f).row()
 
        // Time remaining
        timeRemainingLabel = Label("", skin, "body")
        timeRemainingLabel.setAlignment(Align.center)
        timeRemainingLabel.color = Color.YELLOW
        timeRemainingLabel.setFontScale(1.1f)
        updateTimeRemaining(event)
        card.add(timeRemainingLabel).fillX().padBottom(20f).row()
 
        // Bonus description
        bonusDescriptionLabel = Label(getEventBonusDescription(event), skin, "body")
        bonusDescriptionLabel.setAlignment(Align.center)
        bonusDescriptionLabel.setWrap(true)
        bonusDescriptionLabel.color = Color.LIGHT_GRAY
        card.add(bonusDescriptionLabel).width(500f).padBottom(20f).row()
 
        // Double duration ad button
        doubleAdButton = TextButton("📺 Watch Ad to Double Duration", skin)
        doubleAdButton.label.setFontScale(1.1f)
        doubleAdButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                onDoubleAdClicked()
            }
        })
        card.add(doubleAdButton).size(350f, 60f)
 
        // Pulsing animation
        card.addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.scaleTo(1.02f, 1.02f, 1.0f, Interpolation.sine),
                    Actions.scaleTo(1.0f, 1.0f, 1.0f, Interpolation.sine)
                )
            )
        )
 
        return card
    }
 
    // Creates no event card
    private fun createNoEventCard(): Table {
        val card = Table()
        card.background = createColorDrawable(Color(0.15f, 0.12f, 0.09f, 0.8f))
        card.pad(30f)
 
        // Icon
        val iconLabel = Label("⏳", skin, "gold-large")
        iconLabel.setFontScale(3.0f)
        card.add(iconLabel).padBottom(15f).row()
 
        // Message
        val messageLabel = Label("No Active Event", skin, "body")
        messageLabel.setAlignment(Align.center)
        messageLabel.color = Color.GRAY
        messageLabel.setFontScale(1.3f)
        card.add(messageLabel).fillX().padBottom(20f).row()
 
        // Countdown
        countdownLabel = Label("", skin, "body")
        countdownLabel.setAlignment(Align.center)
        countdownLabel.color = Color.YELLOW
        countdownLabel.setFontScale(1.1f)
        updateCountdown()
        card.add(countdownLabel).fillX()
 
        return card
    }
 
    // Creates history card
    private fun createHistoryCard(event: KingdomEvent): Table {
        val card = Table()
        card.background = createColorDrawable(Color(0.12f, 0.1f, 0.08f, 0.7f))
        card.pad(12f)
 
        // Icon
        val iconLabel = Label(getEventIcon(event.type), skin, "body")
        iconLabel.setFontScale(1.2f)
        iconLabel.color = Color.DARK_GRAY
        card.add(iconLabel).padRight(15f)
 
        // Name
        val nameLabel = Label(event.name, skin, "body")
        nameLabel.color = Color.GRAY
        nameLabel.setFontScale(0.9f)
        card.add(nameLabel).left().expandX()
 
        // Checkmark
        val checkmark = Label("✓", skin, "body")
        checkmark.color = Color.DARK_GRAY
        card.add(checkmark).right()
 
        return card
    }
 
    // Tracks textures created by this screen for disposal
    private val screenTextures = mutableListOf<Texture>()
 
    // Creates divider
    private fun createDivider(): Image {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.GRAY)
        pixmap.fill()
        val texture = Texture(pixmap)
        pixmap.dispose()
        screenTextures.add(texture)
        return Image(texture)
    }
 
    // Gets event icon
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
            KingdomEventType.BUILDING_DISCOUNT -> "🏗️"
            KingdomEventType.CROWN_SHARD_BONUS -> "💎"
            KingdomEventType.SPECIAL_QUEST -> "📜"
        }
    }
 
    // Gets event color
    private fun getEventColor(type: KingdomEventType): Color {
        return when (type) {
            KingdomEventType.GOBLIN_RAID -> Color(0.6f, 0.2f, 0.2f, 0.95f)
            KingdomEventType.ROYAL_FESTIVAL -> Color(0.6f, 0.3f, 0.8f, 0.95f)
            KingdomEventType.MERCHANT_CARAVAN -> Color(0.2f, 0.5f, 0.3f, 0.95f)
            KingdomEventType.DRAGON_SIGHTING -> Color(0.8f, 0.4f, 0.1f, 0.95f)
            KingdomEventType.PLAGUE_OF_FROGS -> Color(0.3f, 0.6f, 0.3f, 0.95f)
            KingdomEventType.HARVEST_MOON -> Color(0.8f, 0.7f, 0.3f, 0.95f)
            KingdomEventType.GOLD_RUSH -> Color.GOLD
            KingdomEventType.DOUBLE_INCOME -> Color.GREEN
            KingdomEventType.TAP_BONUS -> Color.ORANGE
            KingdomEventType.BUILDING_DISCOUNT -> Color.CYAN
            KingdomEventType.CROWN_SHARD_BONUS -> Color.PURPLE
            KingdomEventType.SPECIAL_QUEST -> Color.BROWN
        }
    }
 
    // Gets event bonus description
    private fun getEventBonusDescription(event: KingdomEvent): String {
        val incomeBonus = game.gameEngine.eventSystem.getEventIncomeMultiplier()
        val tapBonus = game.gameEngine.eventSystem.getEventTapMultiplier()
        val costBonus = game.gameEngine.eventSystem.getEventCostMultiplier()
 
        val bonuses = mutableListOf<String>()
        if (incomeBonus > 1.0) bonuses.add("${(incomeBonus * 100).toInt()}% Income")
        if (tapBonus > 1.0) bonuses.add("${(tapBonus * 100).toInt()}% Tap Gold")
        if (costBonus < 1.0) bonuses.add("${((1.0 - costBonus) * 100).toInt()}% Cost Reduction")
 
        return "Active Bonuses: ${bonuses.joinToString(", ")}"
    }
 
    // Gets past events (placeholder)
    private fun getPastEvents(): List<KingdomEvent> {
        // Placeholder - in production, get from GameState.pastEvents
        return emptyList()
    }
 
    // Updates time remaining
    private fun updateTimeRemaining(event: KingdomEvent) {
        val timeRemaining = event.remainingSeconds
        timeRemainingLabel.setText("⏱ ${Formatters.formatTime(timeRemaining)} remaining")
 
        // Red when < 10% time left
        val percentRemaining = timeRemaining.toFloat() / event.duration
        timeRemainingLabel.color = if (percentRemaining < 0.1f) Color.RED else Color.YELLOW
    }
 
    // Updates countdown to next event
    private fun updateCountdown() {
        val secondsUntilNext = game.gameEngine.eventSystem.getSecondsUntilNextEvent()
        countdownLabel.setText("Next event in: ${Formatters.formatTime(secondsUntilNext)}")
    }
 
    // Handles double ad button
    private fun onDoubleAdClicked() {
        game.adsManager?.showRewardedAd(
            type = RewardedAdType.SPEED_BOOST_4X,
            onRewarded = {
                Gdx.app.log("EventScreen", "Event duration doubled!")
                // TODO: Implement event duration doubling in EventSystem
                doubleAdButton.isDisabled = true
                doubleAdButton.setText("Duration Doubled!")
            },
            onFailed = {
                Gdx.app.log("EventScreen", "Ad failed to show")
            }
        )
    }
 
    // Handles back button
    private fun onBackClicked() {
        ScreenNavigator.goBack()
    }
 
    override fun render(delta: Float) {
        // Clear screen
        Gdx.gl.glClearColor(0.08f, 0.06f, 0.04f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
 
        // Update timer
        updateTimer += delta
        if (updateTimer >= UPDATE_INTERVAL) {
            updateTimer = 0f
            updateUI()
        }
 
        // Update and draw stage
        stage.act(delta)
        stage.draw()
    }
 
    // Updates UI elements
    private fun updateUI() {
        val currentEvent = game.gameEngine.gameState.currentEvent
 
        if (currentEvent != null) {
            updateTimeRemaining(currentEvent)
        } else {
            updateCountdown()
        }
    }
 
    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }
 
    override fun dispose() {
        stage.dispose()
        skin.dispose()
        screenTextures.forEach { it.dispose() }
        screenTextures.clear()
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
