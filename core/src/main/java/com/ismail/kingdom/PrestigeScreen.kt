// PATH: core/src/main/java/com/ismail/kingdom/PrestigeScreen.kt
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
import com.ismail.kingdom.assets.AssetDescriptors
import com.ismail.kingdom.assets.GameAssets
import com.ismail.kingdom.models.PrestigeLayer
import com.ismail.kingdom.ui.HeroSelectionGrid
import com.ismail.kingdom.utils.Formatters
import ktx.app.KtxScreen
 
// Prestige confirmation screen with hold-to-confirm
class PrestigeScreen(
    private val game: KingdomTycoonGame,
    private val prestigeLayer: PrestigeLayer
) : KtxScreen {
 
    private val stage = Stage(ScreenViewport())
    private val skin = Skin()
 
    private lateinit var confirmButton: TextButton
    private lateinit var confirmProgressBar: ProgressBar
    private lateinit var heroSelectionGrid: HeroSelectionGrid
 
    private var holdTimer = 0f
    private val HOLD_DURATION = 3.0f
    private var isHolding = false
    private var selectedHero: com.ismail.kingdom.models.Hero? = null
 
    private var currentEraTexture: Texture? = null
    private var nextEraTexture: Texture? = null
 
    override fun show() {
        Gdx.input.inputProcessor = stage
 
        // Load era textures
        loadEraTextures()
 
        // Create skin
        createSkin()
 
        // Build UI
        buildUI()
    }
 
    // Loads era textures
    private fun loadEraTextures() {
        try {
            if (GameAssets.isLoaded()) {
                val currentEra = game.gameEngine.gameState.currentEra
                val nextEra = currentEra + 1
 
                currentEraTexture = GameAssets.getTexture(when (currentEra) {
                    1 -> AssetDescriptors.Eras.ERA1_BACKGROUND
                    2 -> AssetDescriptors.Eras.ERA2_BACKGROUND
                    3 -> AssetDescriptors.Eras.ERA3_BACKGROUND
                    4 -> AssetDescriptors.Eras.ERA4_BACKGROUND
                    else -> AssetDescriptors.Eras.ERA5_BACKGROUND
                })
                if (nextEra <= 5) {
                    nextEraTexture = GameAssets.getTexture(when (nextEra) {
                        1 -> AssetDescriptors.Eras.ERA1_BACKGROUND
                        2 -> AssetDescriptors.Eras.ERA2_BACKGROUND
                        3 -> AssetDescriptors.Eras.ERA3_BACKGROUND
                        4 -> AssetDescriptors.Eras.ERA4_BACKGROUND
                        else -> AssetDescriptors.Eras.ERA5_BACKGROUND
                    })
                }
            }
        } catch (e: Exception) {
            Gdx.app.log("PrestigeScreen", "Failed to load era textures: ${e.message}")
        }
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
 
        val progressBarStyle = ProgressBar.ProgressBarStyle()
        progressBarStyle.background = createColorDrawable(Color.DARK_GRAY)
        progressBarStyle.knobBefore = createColorDrawable(Color.GOLD)
        skin.add("default-horizontal", progressBarStyle)
    }
 
    // Builds UI layout
    private fun buildUI() {
        // Dark overlay
        val overlay = Table()
        overlay.setFillParent(true)
        overlay.background = createColorDrawable(Color(0f, 0f, 0f, 0.85f))
        stage.addActor(overlay)
 
        // Center panel (80% width)
        val panel = createPrestigePanel()
        overlay.add(panel).width(stage.width * 0.8f).center()
    }
 
    // Creates prestige panel
    private fun createPrestigePanel(): Table {
        val panel = Table()
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888).also {
            it.setColor(Color(0.12f, 0.1f, 0.08f, 0.98f))
            it.fill()
        }
        val bg = Texture(pixmap)
        pixmap.dispose() // Fix: dispose Pixmap immediately after Texture creation
        panel.background = TextureRegionDrawable(com.badlogic.gdx.graphics.g2d.TextureRegion(bg))
        panel.pad(30f)
 
        // Scroll pane for content
        val scrollPane = ScrollPane(createPanelContent(), skin)
        scrollPane.setScrollingDisabled(true, false)
        scrollPane.setFadeScrollBars(false)
        panel.add(scrollPane).expand().fill()
 
        return panel
    }
 
    // Creates panel content
    private fun createPanelContent(): Table {
        val content = Table()
 
        // Title
        val titleLabel = Label(getPrestigeTitle(), skin, "gold-large")
        titleLabel.setAlignment(Align.center)
        titleLabel.color = Color.GOLD
        titleLabel.setFontScale(2.0f)
        content.add(titleLabel).fillX().padBottom(20f).row()
 
        // Era transformation preview
        val transformationPreview = createTransformationPreview()
        content.add(transformationPreview).fillX().padBottom(25f).row()
 
        // Crown shards section
        val shardsSection = createShardsSection()
        content.add(shardsSection).fillX().padBottom(20f).row()
 
        // Income multiplier preview
        val multiplierSection = createMultiplierSection()
        content.add(multiplierSection).fillX().padBottom(25f).row()
 
        // Keep/Reset lists
        val keepResetSection = createKeepResetSection()
        content.add(keepResetSection).fillX().padBottom(25f).row()
 
        // Hero selection (only for Legend)
        if (prestigeLayer == PrestigeLayer.LEGEND) {
            val heroes = game.gameEngine.gameState.heroes
            val ownedHeroIds = game.gameEngine.gameState.permanentHeroPassives
 
            heroSelectionGrid = HeroSelectionGrid(heroes, ownedHeroIds.toSet(), skin) { hero ->
                selectedHero = hero
                updateConfirmButton()
            }
            content.add(heroSelectionGrid).fillX().padBottom(25f).row()
        }
 
        // Buttons
        val buttonsTable = createButtons()
        content.add(buttonsTable).fillX()
 
        return content
    }
 
    // Creates transformation preview
    private fun createTransformationPreview(): Table {
        val table = Table()
 
        val currentEra = game.gameEngine.gameState.currentEra
        val nextEra = currentEra + 1
 
        // Current era preview
        val currentPreview = createEraPreview(currentEra, currentEraTexture)
        table.add(currentPreview).size(150f, 100f).pad(10f)
 
        // Arrow
        val arrowLabel = Label("→", skin, "gold-large")
        arrowLabel.setFontScale(2.0f)
        arrowLabel.color = Color.GOLD
        table.add(arrowLabel).padLeft(15f).padRight(15f)
 
        // Next era preview
        val nextPreview = createEraPreview(nextEra, nextEraTexture)
        table.add(nextPreview).size(150f, 100f).pad(10f)
 
        return table
    }
 
    // Creates era preview
    private fun createEraPreview(eraId: Int, texture: Texture?): Stack {
        val stack = Stack()
 
        // Background
        if (texture != null) {
            val image = Image(texture)
            stack.add(image)
        } else {
            val placeholder = Image(createPlaceholderTexture())
            stack.add(placeholder)
        }
 
        // Era label
        val label = Label("Era $eraId", skin, "body")
        label.setAlignment(Align.center)
        label.color = Color.WHITE
        stack.add(label)
 
        return stack
    }
 
    // Creates shards section
    private fun createShardsSection(): Table {
        val table = Table()
        table.background = createColorDrawable(Color(0.2f, 0.16f, 0.12f, 0.8f))
        table.pad(15f)
 
        val titleLabel = Label("You will earn:", skin, "body")
        titleLabel.setFontScale(1.1f)
        titleLabel.color = Color.YELLOW
        table.add(titleLabel).left().row()
 
        val shards = calculateCrownShards()
        val shardsLabel = Label("👑 $shards Crown Shards", skin, "gold-large")
        shardsLabel.setFontScale(1.5f)
        shardsLabel.color = Color.CYAN
        table.add(shardsLabel).padTop(10f)
 
        return table
    }
 
    // Creates multiplier section
    private fun createMultiplierSection(): Table {
        val table = Table()
 
        val currentMultiplier = game.gameEngine.gameState.incomeMultiplier
        val newMultiplier = currentMultiplier + (calculateCrownShards() * 0.02)
 
        val label = Label(
            "Income multiplier: ${String.format("%.2f", currentMultiplier)}x → ${String.format("%.2f", newMultiplier)}x",
            skin,
            "body"
        )
        label.setAlignment(Align.center)
        label.color = Color.GREEN
        label.setFontScale(1.1f)
        table.add(label)
 
        return table
    }
 
    // Creates keep/reset section
    private fun createKeepResetSection(): Table {
        val table = Table()
 
        // Keep section
        val keepTable = Table()
        keepTable.background = createColorDrawable(Color(0.2f, 0.3f, 0.2f, 0.6f))
        keepTable.pad(12f)
 
        val keepTitle = Label("✓ You will keep:", skin, "body")
        keepTitle.color = Color.GREEN
        keepTitle.setFontScale(1.0f)
        keepTable.add(keepTitle).left().row()
 
        val keepItems = listOf(
            "Crown Shards",
            "Heroes",
            "Income Multiplier",
            if (prestigeLayer == PrestigeLayer.LEGEND) "Permanent Hero Passives" else null,
            if (prestigeLayer.ordinal >= PrestigeLayer.RIFT.ordinal) "Shadow Kingdom" else null
        ).filterNotNull()
 
        for (item in keepItems) {
            val itemLabel = Label("  • $item", skin, "body")
            itemLabel.color = Color.LIGHT_GRAY
            itemLabel.setFontScale(0.85f)
            keepTable.add(itemLabel).left().padTop(3f).row()
        }
 
        table.add(keepTable).expandX().fillX().pad(5f)
 
        // Reset section
        val resetTable = Table()
        resetTable.background = createColorDrawable(Color(0.3f, 0.2f, 0.2f, 0.6f))
        resetTable.pad(12f)
 
        val resetTitle = Label("✗ You will reset:", skin, "body")
        resetTitle.color = Color.RED
        resetTitle.setFontScale(1.0f)
        resetTable.add(resetTitle).left().row()
 
        val resetItems = listOf(
            "All Buildings",
            "Gold",
            "Active Quests",
            "Map Progress"
        )
 
        for (item in resetItems) {
            val itemLabel = Label("  • $item", skin, "body")
            itemLabel.color = Color.LIGHT_GRAY
            itemLabel.setFontScale(0.85f)
            resetTable.add(itemLabel).left().padTop(3f).row()
        }
 
        table.add(resetTable).expandX().fillX().pad(5f)
 
        return table
    }
 
    // Creates buttons
    private fun createButtons(): Table {
        val table = Table()
 
        // Cancel button
        val cancelButton = TextButton("CANCEL", skin)
        cancelButton.label.setFontScale(1.2f)
        cancelButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                onCancel()
            }
        })
        table.add(cancelButton).size(180f, 60f).padRight(20f)
 
        // Confirm button container
        val confirmContainer = Table()
 
        // Confirm button
        confirmButton = TextButton(getConfirmButtonText(), skin)
        confirmButton.label.setFontScale(1.2f)
        confirmButton.color = Color.GOLD
 
        // Disable if Legend and no hero selected
        if (prestigeLayer == PrestigeLayer.LEGEND) {
            confirmButton.isDisabled = true
            confirmButton.color = Color.DARK_GRAY
        }
 
        confirmButton.addListener(object : ClickListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                if (!confirmButton.isDisabled) {
                    isHolding = true
                    holdTimer = 0f
                }
                return true
            }
 
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                isHolding = false
                holdTimer = 0f
                confirmProgressBar.value = 0f
            }
        })
 
        confirmContainer.add(confirmButton).size(180f, 60f).row()
 
        // Progress bar
        confirmProgressBar = ProgressBar(0f, HOLD_DURATION, 0.01f, false, skin)
        confirmProgressBar.value = 0f
        confirmContainer.add(confirmProgressBar).fillX().height(8f).padTop(5f)
 
        table.add(confirmContainer)
 
        return table
    }
 
    // Gets prestige title
    private fun getPrestigeTitle(): String {
        return when (prestigeLayer) {
            PrestigeLayer.NONE -> ""
            PrestigeLayer.ASCENSION -> "ASCENSION"
            PrestigeLayer.RIFT -> "RIFT"
            PrestigeLayer.LEGEND -> "LEGEND"
        }
    }
 
    // Gets confirm button text
    private fun getConfirmButtonText(): String {
        return when (prestigeLayer) {
            PrestigeLayer.NONE -> ""
            PrestigeLayer.ASCENSION -> "ASCEND"
            PrestigeLayer.RIFT -> "OPEN RIFT"
            PrestigeLayer.LEGEND -> "BECOME LEGEND"
        }
    }
 
    // Calculates crown shards to be earned
    private fun calculateCrownShards(): Int {
        // PrestigeSystem already has access to gameState and handles its own logic
        return game.gameEngine.prestigeSystem.calculateCrownShards()
    }
 
    // Updates confirm button state
    private fun updateConfirmButton() {
        if (prestigeLayer == PrestigeLayer.LEGEND) {
            confirmButton.isDisabled = selectedHero == null
            confirmButton.color = if (selectedHero != null) Color.GOLD else Color.DARK_GRAY
        }
    }
 
    // Handles cancel
    private fun onCancel() {
        ScreenNavigator.goBack()
    }
 
    // Handles prestige confirmation
    private fun onPrestigeConfirmed() {
        // Play prestige animation
        playPrestigeAnimation()
    }
 
    // Plays prestige animation sequence
    private fun playPrestigeAnimation() {
        val overlay = Table()
        overlay.setFillParent(true)
        stage.addActor(overlay)
 
        // White flash
        overlay.background = createColorDrawable(Color.WHITE)
        overlay.color = Color(1f, 1f, 1f, 0f)
        overlay.addAction(
            Actions.sequence(
                Actions.fadeIn(0.2f),
                Actions.delay(0.3f),
                Actions.fadeOut(0.5f),
                Actions.run {
                    // Perform prestige
                    performPrestige()
 
                    // Era transition
                    showEraTransition()
                }
            )
        )
    }
 
    // Performs actual prestige
    private fun performPrestige() {
        when (prestigeLayer) {
            PrestigeLayer.NONE -> { /* do nothing */ }
            PrestigeLayer.ASCENSION -> game.gameEngine.performAscension()
            PrestigeLayer.RIFT -> game.gameEngine.performRift()
            PrestigeLayer.LEGEND -> {
                if (selectedHero != null) {
                    game.gameEngine.performLegend(selectedHero!!.id)
                }
            }
        }
    }
 
    // Shows era transition
    private fun showEraTransition() {
        val newEra = game.gameEngine.gameState.currentEra
 
        // Crown shards count-up
        val shardsEarned = calculateCrownShards()
        val shardsLabel = Label("+$shardsEarned 👑", skin, "gold-large")
        shardsLabel.setFontScale(3.0f)
        shardsLabel.color = Color.CYAN
        shardsLabel.setPosition(
            (stage.width - 300f) / 2f,
            stage.height * 0.6f
        )
        shardsLabel.setSize(300f, 100f)
        shardsLabel.setAlignment(Align.center)
 
        shardsLabel.addAction(
            Actions.sequence(
                Actions.alpha(0f),
                Actions.parallel(
                    Actions.fadeIn(0.5f),
                    Actions.scaleTo(1.5f, 1.5f, 0.5f, Interpolation.elasticOut)
                ),
                Actions.delay(1.5f),
                Actions.fadeOut(0.5f),
                Actions.run {
                    showEraTitleCard(newEra)
                }
            )
        )
 
        stage.addActor(shardsLabel)
    }
 
    // Shows era title card
    private fun showEraTitleCard(eraId: Int) {
        val titleLabel = Label("ERA $eraId UNLOCKED", skin, "gold-large")
        titleLabel.setFontScale(2.5f)
        titleLabel.color = Color.GOLD
        titleLabel.setPosition(
            (stage.width - 500f) / 2f,
            stage.height * 0.5f
        )
        titleLabel.setSize(500f, 100f)
        titleLabel.setAlignment(Align.center)
 
        titleLabel.addAction(
            Actions.sequence(
                Actions.alpha(0f),
                Actions.parallel(
                    Actions.fadeIn(0.5f),
                    Actions.scaleTo(1.3f, 1.3f, 0.5f, Interpolation.bounceOut)
                ),
                Actions.delay(2.0f),
                Actions.fadeOut(0.5f),
                Actions.run {
                    // Return to game screen
                    ScreenNavigator.navigate(ScreenType.GAME, TransitionType.FADE, addToStack = false)
                }
            )
        )
 
        stage.addActor(titleLabel)
    }
 
    override fun render(delta: Float) {
        // Clear screen
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
 
        // Update hold timer
        if (isHolding) {
            holdTimer += delta
            confirmProgressBar.value = holdTimer
 
            if (holdTimer >= HOLD_DURATION) {
                isHolding = false
                onPrestigeConfirmed()
            }
        }
 
        // Update and draw stage
        stage.act(delta)
        stage.draw()
    }
 
    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }
 
    override fun dispose() {
        stage.dispose()
        skin.dispose()
    }
 
    // Creates placeholder texture
    private fun createPlaceholderTexture(): Texture {
        val pixmap = Pixmap(150, 100, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.DARK_GRAY)
        pixmap.fill()
        val texture = Texture(pixmap)
        pixmap.dispose()
        return texture
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
