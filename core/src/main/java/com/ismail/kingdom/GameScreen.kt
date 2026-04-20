// PATH: core/src/main/java/com/ismail/kingdom/GameScreen.kt
package com.ismail.kingdom

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.ismail.kingdom.assets.AssetDescriptors
import com.ismail.kingdom.assets.GameAssets
import com.ismail.kingdom.ui.CoinParticlePool
import com.ismail.kingdom.ui.HUD
import com.ismail.kingdom.utils.Formatters
import ktx.app.KtxScreen

// Tab types for bottom panel
enum class TabType {
    BUILDINGS,
    ADVISORS,
    QUESTS
}

// Main gameplay screen with Scene2D UI
class GameScreen(private val game: KingdomTycoonGame) : KtxScreen {

    // Screen dimensions
    private val SCREEN_WIDTH = 1080f
    private val SCREEN_HEIGHT = 1920f

    // Layout dimensions
    private val TOP_BAR_HEIGHT = 180f
    private val KINGDOM_HALL_HEIGHT = 600f
    private val BOTTOM_PANEL_HEIGHT = 700f
    private val BOTTOM_NAV_HEIGHT = 120f

    // Stage and viewport
    private val viewport = FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT)
    private val stage = Stage(viewport)
    private val batch = SpriteBatch()

    // UI components
    private lateinit var topBar: Table
    private lateinit var goldLabel: Label
    private lateinit var ipsLabel: Label
    private lateinit var speedBoostLabel: Label

    private lateinit var kingdomHallStack: Stack
    private lateinit var kingdomHallImage: Image
    private lateinit var comboLabel: Label

    private lateinit var bottomPanel: Table
    private lateinit var buildingsPanel: ScrollPane
    private lateinit var advisorsPanel: ScrollPane
    private lateinit var questsPanel: ScrollPane
    private lateinit var currentPanel: Actor

    private lateinit var bottomNav: Table

    // Fonts
    private lateinit var goldLargeFont: BitmapFont
    private lateinit var goldSmallFont: BitmapFont
    private lateinit var bodyFont: BitmapFont

    // UI Skin
    private lateinit var skin: Skin

    // HUD manager
    private lateinit var hud: HUD

    // Current tab
    private var currentTab = TabType.BUILDINGS

    // Disposable textures for drawables
    private val disposableTextures = mutableListOf<Texture>()

    override fun show() {
        // Set input processor
        Gdx.input.inputProcessor = stage

        // Load fonts
        loadFonts()

        // Create UI skin
        createSkin()

        // Build UI
        buildUI()

        // Initialize HUD
        hud = HUD(stage, skin)
        hud.setGoldLabel(goldLabel)
        hud.setIPSLabel(ipsLabel)
        hud.setSpeedBoostLabel(speedBoostLabel)

        // Initialize coin particle pool
        CoinParticlePool.initialize(stage, bodyFont)

        // Start background music (placeholder)
        // TODO: Add music when audio assets are ready
    }

    // Loads bitmap fonts
    private fun loadFonts() {
        goldLargeFont = BitmapFont() // Placeholder - replace with GameAssets.getBitmapFont()
        goldSmallFont = BitmapFont()
        bodyFont = BitmapFont()

        goldLargeFont.data.setScale(2.0f)
        goldSmallFont.data.setScale(1.5f)
        bodyFont.data.setScale(1.0f)

        goldLargeFont.color = Color.GOLD
        goldSmallFont.color = Color.GOLD
        bodyFont.color = Color.WHITE
    }

    // Creates UI skin with styles
    private fun createSkin() {
        skin = Skin()

        // Label styles
        val goldLargeStyle = Label.LabelStyle(goldLargeFont, Color.GOLD)
        val goldSmallStyle = Label.LabelStyle(goldSmallFont, Color.GOLD)
        val bodyStyle = Label.LabelStyle(bodyFont, Color.WHITE)

        skin.add("gold-large", goldLargeStyle)
        skin.add("gold-small", goldSmallStyle)
        skin.add("body", bodyStyle)

        // Button style (placeholder)
        val buttonStyle = TextButton.TextButtonStyle()
        buttonStyle.font = bodyFont
        buttonStyle.fontColor = Color.WHITE
        skin.add("default", buttonStyle)

        // ScrollPane style
        val scrollStyle = ScrollPane.ScrollPaneStyle()
        skin.add("default", scrollStyle)
    }

    // Builds the complete UI layout
    private fun buildUI() {
        val root = Table()
        root.setFillParent(true)
        stage.addActor(root)

        // Top bar
        topBar = createTopBar()
        root.add(topBar).height(TOP_BAR_HEIGHT).fillX().row()

        // Kingdom Hall (tappable area)
        kingdomHallStack = createKingdomHall()
        root.add(kingdomHallStack).height(KINGDOM_HALL_HEIGHT).fillX().row()

        // Bottom panel (tabs)
        bottomPanel = createBottomPanel()
        root.add(bottomPanel).height(BOTTOM_PANEL_HEIGHT).fillX().row()

        // Bottom navigation
        bottomNav = createBottomNav()
        root.add(bottomNav).height(BOTTOM_NAV_HEIGHT).fillX().row()
    }

    // Creates top bar with gold, IPS, and settings
    private fun createTopBar(): Table {
        val table = Table()
        table.background = createColorDrawable(Color(0.2f, 0.15f, 0.1f, 1f))
        table.pad(20f)

        // Gold display (left)
        val goldTable = Table()
        val coinIcon = Label("💰", skin, "gold-large")
        goldLabel = Label("0", skin, "gold-large")
        goldTable.add(coinIcon).padRight(10f)
        goldTable.add(goldLabel)

        // IPS display (center)
        ipsLabel = Label("0/s", skin, "gold-small")

        // Speed boost timer (top right, initially hidden)
        speedBoostLabel = Label("", skin, "gold-small")
        speedBoostLabel.color = Color.ORANGE
        speedBoostLabel.isVisible = false

        // Settings button (right)
        val settingsButton = TextButton("⚙", skin)
        settingsButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                onSettingsClicked()
            }
        })

        table.add(goldTable).left().expandX()
        table.add(ipsLabel).center().expandX()
        table.add(speedBoostLabel).right().padRight(10f)
        table.add(settingsButton).right().size(60f)

        return table
    }

    // Creates kingdom hall tappable area
    private fun createKingdomHall(): Stack {
        val stack = Stack()

        // Background (era-appropriate)
        val background = Image() // Placeholder - load from GameAssets
        background.setFillParent(true)
        stack.add(background)

        // Kingdom Hall building sprite
        kingdomHallImage = Image() // Placeholder - load from GameAssets
        kingdomHallImage.setSize(400f, 400f)
        kingdomHallImage.setPosition(
            (SCREEN_WIDTH - 400f) / 2f,
            (KINGDOM_HALL_HEIGHT - 400f) / 2f
        )

        // Add tap listener
        kingdomHallImage.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                onKingdomHallTapped(x, y)
            }
        })

        stack.add(kingdomHallImage)

        // Combo label (initially hidden)
        comboLabel = Label("", skin, "gold-large")
        comboLabel.setAlignment(Align.center)
        comboLabel.setPosition(
            (SCREEN_WIDTH - 300f) / 2f,
            KINGDOM_HALL_HEIGHT - 100f
        )
        comboLabel.setSize(300f, 50f)
        comboLabel.isVisible = false
        stack.add(comboLabel)

        return stack
    }

    // Creates bottom panel with tabs
    private fun createBottomPanel(): Table {
        val table = Table()
        table.background = createColorDrawable(Color(0.15f, 0.12f, 0.08f, 1f))

        // Tab bar
        val tabBar = createTabBar()
        table.add(tabBar).height(80f).fillX().row()

        // Content area (scrollable panels)
        buildingsPanel = createBuildingsPanel()
        advisorsPanel = createAdvisorsPanel()
        questsPanel = createQuestsPanel()

        currentPanel = buildingsPanel
        table.add(currentPanel).expand().fill()

        return table
    }

    // Creates tab bar
    private fun createTabBar(): Table {
        val table = Table()

        val buildingsTab = TextButton("BUILDINGS", skin)
        val advisorsTab = TextButton("ADVISORS", skin)
        val questsTab = TextButton("QUESTS", skin)

        buildingsTab.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                switchTab(TabType.BUILDINGS)
            }
        })

        advisorsTab.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                switchTab(TabType.ADVISORS)
            }
        })

        questsTab.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                switchTab(TabType.QUESTS)
            }
        })

        table.add(buildingsTab).expandX().fillX().height(80f)
        table.add(advisorsTab).expandX().fillX().height(80f)
        table.add(questsTab).expandX().fillX().height(80f)

        return table
    }

    // Creates buildings panel
    private fun createBuildingsPanel(): ScrollPane {
        val content = Table()
        content.pad(20f)

        // Add building rows (placeholder)
        for (i in 1..10) {
            val buildingRow = createBuildingRow(i)
            content.add(buildingRow).fillX().height(100f).padBottom(10f).row()
        }

        val scrollPane = ScrollPane(content, skin)
        scrollPane.setScrollingDisabled(true, false)
        // scrollPane.setClip(true) // Enable clipping
        return scrollPane
    }

    // Creates a single building row
    private fun createBuildingRow(index: Int): Table {
        val table = Table()
        table.background = createColorDrawable(Color(0.25f, 0.2f, 0.15f, 1f))
        table.pad(10f)

        // Building icon
        val icon = Label("🏠", skin, "gold-large")

        // Building info
        val infoTable = Table()
        val nameLabel = Label("Building $index", skin, "body")
        val costLabel = Label("Cost: 100", skin, "gold-small")
        infoTable.add(nameLabel).left().row()
        infoTable.add(costLabel).left()

        // Buy button
        val buyButton = TextButton("BUY", skin)
        buyButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                onBuildingBuyClicked(index)
            }
        })

        table.add(icon).size(60f).padRight(20f)
        table.add(infoTable).expandX().left()
        table.add(buyButton).size(120f, 60f)

        return table
    }

    // Creates advisors panel
    private fun createAdvisorsPanel(): ScrollPane {
        val content = Table()
        content.pad(20f)

        val label = Label("Advisors Panel\n(Coming Soon)", skin, "body")
        label.setAlignment(Align.center)
        content.add(label).expand().center()

        val scrollPane = ScrollPane(content, skin)
        // scrollPane.setClip(true) // Enable clipping
        return scrollPane
    }

    // Creates quests panel
    private fun createQuestsPanel(): ScrollPane {
        val content = Table()
        content.pad(20f)

        val label = Label("Quests Panel\n(Coming Soon)", skin, "body")
        label.setAlignment(Align.center)
        content.add(label).expand().center()

        val scrollPane = ScrollPane(content, skin)
        // scrollPane.setClip(true) // Enable clipping
        return scrollPane
    }

    // Creates bottom navigation
    private fun createBottomNav(): Table {
        val table = Table()
        table.background = createColorDrawable(Color(0.1f, 0.08f, 0.05f, 1f))

        val mapButton = TextButton("🗺 MAP", skin)
        val prestigeButton = TextButton("👑 PRESTIGE", skin)
        val eventsButton = TextButton("🎉 EVENTS", skin)

        mapButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                onMapClicked()
            }
        })

        prestigeButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                onPrestigeClicked()
            }
        })

        eventsButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                onEventsClicked()
            }
        })

        table.add(mapButton).expandX().fillX().height(BOTTOM_NAV_HEIGHT).pad(10f)
        table.add(prestigeButton).expandX().fillX().height(BOTTOM_NAV_HEIGHT).pad(10f)
        table.add(eventsButton).expandX().fillX().height(BOTTOM_NAV_HEIGHT).pad(10f)

        return table
    }

    // Switches active tab
    fun switchTab(tab: TabType) {
        currentTab = tab

        // Clear bottom panel cells
        bottomPanel.clearChildren()

        // Re-add tab bar
        val tabBar = createTabBar()
        bottomPanel.add(tabBar).height(80f).fillX().row()

        // Add new panel
        currentPanel = when (tab) {
            TabType.BUILDINGS -> buildingsPanel
            TabType.ADVISORS -> advisorsPanel
            TabType.QUESTS -> questsPanel
        }

        bottomPanel.add(currentPanel).expand().fill()
    }

    // Handles kingdom hall tap
    private fun onKingdomHallTapped(x: Float, y: Float) {
        // Convert local coords to stage coords
        val stageCoords = Vector2(x, y)
        kingdomHallImage.localToStageCoordinates(stageCoords)

        // Scale animation
        kingdomHallImage.clearActions()
        kingdomHallImage.addAction(
            Actions.sequence(
                Actions.scaleTo(1.1f, 1.1f, 0.1f, Interpolation.pow2Out),
                Actions.scaleTo(1.0f, 1.0f, 0.1f, Interpolation.pow2In)
            )
        )

        // Call game engine tap
        val tapEvent = game.gameEngine.tap(stageCoords.x, stageCoords.y)

        // Show tap popup via HUD
        hud.showTapPopup(tapEvent.goldEarned, stageCoords)

        // Spawn coin particles
        CoinParticlePool.spawn(stageCoords)

        // Update combo display
        updateComboDisplay()
    }

    // Updates combo display
    private fun updateComboDisplay() {
        val comboCount = game.gameEngine.gameState.tapCount // Placeholder - use TapSystem.getComboCount()

        if (comboCount > 4) {
            val multiplier = when {
                comboCount >= 20 -> 3.0
                comboCount >= 10 -> 2.0
                comboCount >= 5 -> 1.5
                else -> 1.0
            }

            hud.showComboIndicator(multiplier)
        }
    }

    // Event handlers
    private fun onSettingsClicked() {
        ScreenNavigator.navigate(ScreenType.SETTINGS, TransitionType.FADE)
    }

    private fun onBuildingBuyClicked(index: Int) {
        println("Buy building $index")
    }

    private fun onMapClicked() {
        ScreenNavigator.navigate(ScreenType.MAP, TransitionType.SLIDE_LEFT)
    }

    private fun onPrestigeClicked() {
        ScreenNavigator.navigate(ScreenType.PRESTIGE, TransitionType.FLASH_WHITE)
    }

    private fun onEventsClicked() {
        ScreenNavigator.navigate(ScreenType.EVENT, TransitionType.FADE)
    }

    override fun render(delta: Float) {
        // Clear screen
        Gdx.gl.glClearColor(0.1f, 0.08f, 0.05f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Update game engine
        game.gameEngine.update(delta)

        // Update UI
        updateUI(delta)

        // Update particles
        updateParticles(delta)

        // Update and draw stage
        stage.act(delta)
        stage.draw()

        // Draw particles
        drawParticles()
    }

    // Updates UI elements
    private fun updateUI(delta: Float) {
        val state = game.gameEngine.gameState

        // Update HUD
        hud.updateGoldDisplay(state.currentGold)
        hud.updateIPSDisplay(game.gameEngine.getGoldPerSecond())

        // Update speed boost timer
        if (game.gameEngine.adManager.speedBoostActive) {
            val timeRemaining = game.gameEngine.adManager.speedBoostRemainingSeconds
            hud.showSpeedBoostTimer(timeRemaining)
        } else {
            hud.showSpeedBoostTimer(0f)
        }
    }

    // Updates coin particles
    private fun updateParticles(delta: Float) {
        CoinParticlePool.update(delta)
    }

    // Draws coin particles
    private fun drawParticles() {
        batch.begin()
        CoinParticlePool.draw(batch)
        batch.end()
    }

    // Creates a colored drawable with null-safety
    private fun createColorDrawable(color: Color): com.badlogic.gdx.scenes.scene2d.utils.Drawable? {
        return try {
            val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
            pixmap.setColor(color)
            pixmap.fill()
            val texture = Texture(pixmap)
            pixmap.dispose()

            disposableTextures.add(texture)
            TextureRegionDrawable(texture)
        } catch (e: Exception) {
            null // Return null if texture creation fails
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun hide() {
        // Clear particles when screen is hidden
        CoinParticlePool.clear()
    }

    override fun dispose() {
        stage.dispose()
        batch.dispose()
        goldLargeFont.dispose()
        goldSmallFont.dispose()
        bodyFont.dispose()
        skin.dispose()

        // Dispose all created textures
        for (texture in disposableTextures) {
            texture.dispose()
        }
        disposableTextures.clear()

        CoinParticlePool.clear()
    }
}
