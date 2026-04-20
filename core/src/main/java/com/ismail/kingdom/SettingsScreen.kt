// PATH: core/src/main/java/com/ismail/kingdom/SettingsScreen.kt
package com.ismail.kingdom

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.ismail.kingdom.data.NumberFormat
import com.ismail.kingdom.data.SettingsManager
import ktx.app.KtxScreen

// Settings screen
class SettingsScreen(private val game: KingdomTycoonGame) : KtxScreen {
    
    private val stage = Stage(ScreenViewport())
    private val skin = Skin()
    
    private lateinit var soundCheckbox: CheckBox
    private lateinit var musicCheckbox: CheckBox
    private lateinit var musicVolumeSlider: Slider
    private lateinit var volumeLabel: Label
    private lateinit var notificationsCheckbox: CheckBox
    private lateinit var numberFormatSelector: SelectBox<String>
    
    private var resetConfirmationDialog: ResetConfirmationDialog? = null
    
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
        
        val checkBoxStyle = CheckBox.CheckBoxStyle()
        checkBoxStyle.font = font
        checkBoxStyle.fontColor = Color.WHITE
        checkBoxStyle.checkboxOn = createCheckboxDrawable(true)
        checkBoxStyle.checkboxOff = createCheckboxDrawable(false)
        skin.add("default", checkBoxStyle)
        
        val sliderStyle = Slider.SliderStyle()
        sliderStyle.background = createColorDrawable(Color.DARK_GRAY)
        sliderStyle.knob = createKnobDrawable()
        sliderStyle.knobBefore = createColorDrawable(Color.GOLD)
        skin.add("default-horizontal", sliderStyle)
        
        val selectBoxStyle = SelectBox.SelectBoxStyle()
        selectBoxStyle.font = font
        selectBoxStyle.fontColor = Color.WHITE
        selectBoxStyle.background = createColorDrawable(Color(0.2f, 0.16f, 0.12f, 1f))
        skin.add("default", selectBoxStyle)
        
        val listStyle = com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle()
        listStyle.font = font
        listStyle.fontColorSelected = Color.GOLD
        listStyle.fontColorUnselected = Color.WHITE
        listStyle.selection = createColorDrawable(Color(0.3f, 0.24f, 0.18f, 1f))
        skin.add("default", listStyle)
        
        val scrollPaneStyle = ScrollPane.ScrollPaneStyle()
        skin.add("default", scrollPaneStyle)
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
        
        // Scroll pane with settings
        val scrollPane = ScrollPane(createSettingsContent(), skin)
        scrollPane.setScrollingDisabled(true, false)
        scrollPane.setFadeScrollBars(false)
        root.add(scrollPane).expand().fill().row()
        
        // Version number
        val versionLabel = Label("Version 1.0.0", skin, "body")
        versionLabel.setAlignment(Align.center)
        versionLabel.color = Color.GRAY
        versionLabel.setFontScale(0.8f)
        root.add(versionLabel).fillX().padBottom(10f)
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
        val titleLabel = Label("SETTINGS", skin, "gold-large")
        titleLabel.setAlignment(Align.center)
        titleLabel.color = Color.GOLD
        table.add(titleLabel).center().expandX()
        
        // Spacer
        table.add().size(120f, 50f).right().expandX()
        
        return table
    }
    
    // Creates settings content
    private fun createSettingsContent(): Table {
        val content = Table()
        content.pad(30f)
        
        // Sound section
        val soundSection = createSoundSection()
        content.add(soundSection).fillX().padBottom(20f).row()
        
        // Music section
        val musicSection = createMusicSection()
        content.add(musicSection).fillX().padBottom(20f).row()
        
        // Notifications section
        val notificationsSection = createNotificationsSection()
        content.add(notificationsSection).fillX().padBottom(20f).row()
        
        // Number format section
        val numberFormatSection = createNumberFormatSection()
        content.add(numberFormatSection).fillX().padBottom(30f).row()
        
        // Divider
        val divider = createDivider()
        content.add(divider).fillX().height(2f).padBottom(30f).row()
        
        // Reset progress button
        val resetButton = createResetButton()
        content.add(resetButton).size(300f, 60f).padBottom(20f).row()
        
        // Privacy policy link
        val privacyButton = createLinkButton("Privacy Policy") {
            onPrivacyPolicyClicked()
        }
        content.add(privacyButton).size(250f, 50f).padBottom(10f).row()
        
        // Rate game link
        val rateButton = createLinkButton("⭐ Rate the Game") {
            onRateGameClicked()
        }
        content.add(rateButton).size(250f, 50f)
        
        return content
    }
    
    // Creates sound section
    private fun createSoundSection(): Table {
        val table = Table()
        table.background = createColorDrawable(Color(0.15f, 0.12f, 0.09f, 0.8f))
        table.pad(15f)
        
        val label = Label("Sound Effects", skin, "body")
        label.setFontScale(1.1f)
        table.add(label).left().expandX()
        
        soundCheckbox = CheckBox("", skin)
        soundCheckbox.isChecked = SettingsManager.soundEnabled
        soundCheckbox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                SettingsManager.setSoundEnabled(soundCheckbox.isChecked)
            }
        })
        table.add(soundCheckbox).right()
        
        return table
    }
    
    // Creates music section
    private fun createMusicSection(): Table {
        val table = Table()
        table.background = createColorDrawable(Color(0.15f, 0.12f, 0.09f, 0.8f))
        table.pad(15f)
        
        // Music toggle
        val toggleTable = Table()
        val label = Label("Music", skin, "body")
        label.setFontScale(1.1f)
        toggleTable.add(label).left().expandX()
        
        musicCheckbox = CheckBox("", skin)
        musicCheckbox.isChecked = SettingsManager.musicEnabled
        musicCheckbox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                SettingsManager.setMusicEnabled(musicCheckbox.isChecked)
                musicVolumeSlider.isDisabled = !musicCheckbox.isChecked
            }
        })
        toggleTable.add(musicCheckbox).right()
        
        table.add(toggleTable).fillX().row()
        
        // Volume slider
        val volumeTable = Table()
        val volumeTitleLabel = Label("Volume:", skin, "body")
        volumeTitleLabel.setFontScale(0.9f)
        volumeTitleLabel.color = Color.LIGHT_GRAY
        volumeTable.add(volumeTitleLabel).left().padRight(10f)
        
        musicVolumeSlider = Slider(0f, 100f, 1f, false, skin)
        musicVolumeSlider.value = SettingsManager.getMusicVolumePercent().toFloat()
        musicVolumeSlider.isDisabled = !SettingsManager.musicEnabled
        musicVolumeSlider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val percent = musicVolumeSlider.value.toInt()
                SettingsManager.setMusicVolumePercent(percent)
                volumeLabel.setText("$percent%")
            }
        })
        volumeTable.add(musicVolumeSlider).width(200f).padRight(10f)
        
        volumeLabel = Label("${SettingsManager.getMusicVolumePercent()}%", skin, "body")
        volumeLabel.setFontScale(0.9f)
        volumeLabel.color = Color.GOLD
        volumeTable.add(volumeLabel).width(50f)
        
        table.add(volumeTable).fillX().padTop(10f)
        
        return table
    }
    
    // Creates notifications section
    private fun createNotificationsSection(): Table {
        val table = Table()
        table.background = createColorDrawable(Color(0.15f, 0.12f, 0.09f, 0.8f))
        table.pad(15f)
        
        val label = Label("Notifications", skin, "body")
        label.setFontScale(1.1f)
        table.add(label).left().expandX()
        
        notificationsCheckbox = CheckBox("", skin)
        notificationsCheckbox.isChecked = SettingsManager.notificationsEnabled
        notificationsCheckbox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                SettingsManager.setNotificationsEnabled(notificationsCheckbox.isChecked)
                // TODO: Request Android notification permission
            }
        })
        table.add(notificationsCheckbox).right()
        
        return table
    }
    
    // Creates number format section
    private fun createNumberFormatSection(): Table {
        val table = Table()
        table.background = createColorDrawable(Color(0.15f, 0.12f, 0.09f, 0.8f))
        table.pad(15f)
        
        val label = Label("Number Format", skin, "body")
        label.setFontScale(1.1f)
        table.add(label).left().padRight(20f)
        
        numberFormatSelector = SelectBox(skin)
        numberFormatSelector.setItems(
            "Abbreviated (1.23K)",
            "Scientific (1.23e3)",
            "Full (1,234)"
        )
        
        // Set current selection
        val currentFormat = SettingsManager.numberFormat
        numberFormatSelector.selectedIndex = when (currentFormat) {
            NumberFormat.ABBREVIATED -> 0
            NumberFormat.SCIENTIFIC -> 1
            NumberFormat.FULL -> 2
        }
        
        numberFormatSelector.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val format = when (numberFormatSelector.selectedIndex) {
                    0 -> NumberFormat.ABBREVIATED
                    1 -> NumberFormat.SCIENTIFIC
                    2 -> NumberFormat.FULL
                    else -> NumberFormat.ABBREVIATED
                }
                SettingsManager.setNumberFormat(format)
            }
        })
        
        table.add(numberFormatSelector).width(250f).right()
        
        return table
    }
    
    // Creates reset button
    private fun createResetButton(): TextButton {
        val button = TextButton("Reset Progress", skin)
        button.label.setFontScale(1.1f)
        button.color = Color.RED
        button.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                showResetConfirmation()
            }
        })
        return button
    }
    
    // Creates link button
    private fun createLinkButton(text: String, onClick: () -> Unit): TextButton {
        val button = TextButton(text, skin)
        button.label.setFontScale(1.0f)
        button.color = Color.CYAN
        button.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                onClick()
            }
        })
        return button
    }
    
    // Creates divider
    private fun createDivider(): Image {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.GRAY)
        pixmap.fill()
        val texture = Texture(pixmap)
        pixmap.dispose()
        return Image(texture)
    }
    
    // Shows reset confirmation dialog
    private fun showResetConfirmation() {
        resetConfirmationDialog = ResetConfirmationDialog(stage, skin) {
            onResetConfirmed()
        }
        stage.addActor(resetConfirmationDialog)
    }
    
    // Handles reset confirmation
    private fun onResetConfirmed() {
        game.gameEngine.gameState.reset()
        val prefs = com.badlogic.gdx.Gdx.app.getPreferences("kingdom_save")
        game.saveManager.save(game.gameEngine.gameState)

        ScreenNavigator.clearStack()
        ScreenNavigator.navigate(ScreenType.MAIN_MENU, TransitionType.FADE, addToStack = true)
    }
    
    // Handles privacy policy click
    private fun onPrivacyPolicyClicked() {
        Gdx.net.openURI("https://example.com/privacy-policy")
        Gdx.app.log("SettingsScreen", "Opening privacy policy")
    }
    
    // Handles rate game click
    private fun onRateGameClicked() {
        // Open Play Store
        val packageName = "com.ismail.kingdom"
        Gdx.net.openURI("https://play.google.com/store/apps/details?id=$packageName")
        Gdx.app.log("SettingsScreen", "Opening Play Store")
    }
    
    // Handles back button
    private fun onBackClicked() {
        ScreenNavigator.goBack()
    }
    
    override fun render(delta: Float) {
        // Clear screen
        Gdx.gl.glClearColor(0.08f, 0.06f, 0.04f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        
        // Update reset dialog if active
        resetConfirmationDialog?.update(delta)
        
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
    
    // Creates checkbox drawable
    private fun createCheckboxDrawable(checked: Boolean): TextureRegionDrawable {
        val pixmap = Pixmap(32, 32, Pixmap.Format.RGBA8888)
        
        // Border
        pixmap.setColor(Color.WHITE)
        pixmap.drawRectangle(0, 0, 32, 32)
        
        // Fill if checked
        if (checked) {
            pixmap.setColor(Color.GREEN)
            pixmap.fillRectangle(4, 4, 24, 24)
        }
        
        val texture = Texture(pixmap)
        pixmap.dispose()
        return TextureRegionDrawable(texture)
    }
    
    // Creates knob drawable
    private fun createKnobDrawable(): TextureRegionDrawable {
        val pixmap = Pixmap(20, 20, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.GOLD)
        pixmap.fillCircle(10, 10, 10)
        val texture = Texture(pixmap)
        pixmap.dispose()
        return TextureRegionDrawable(texture)
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

// Reset confirmation dialog
class ResetConfirmationDialog(
    private val stage: Stage,
    private val skin: Skin,
    private val onConfirm: () -> Unit
) : Table() {
    
    private lateinit var confirmButton: TextButton
    private lateinit var progressBar: ProgressBar
    
    private var holdTimer = 0f
    private val HOLD_DURATION = 3.0f
    private var isHolding = false
    
    init {
        setFillParent(true)
        background = createColorDrawable(Color(0f, 0f, 0f, 0.85f))
        
        // Dialog panel
        val panel = createPanel()
        add(panel).center()
    }
    
    // Creates dialog panel
    private fun createPanel(): Table {
        val panel = Table()
        panel.background = createColorDrawable(Color(0.15f, 0.12f, 0.08f, 0.98f))
        panel.pad(30f)
        
        // Title
        val titleLabel = Label("ARE YOU SURE?", skin, "gold-large")
        titleLabel.setAlignment(Align.center)
        titleLabel.color = Color.RED
        titleLabel.setFontScale(1.8f)
        panel.add(titleLabel).fillX().padBottom(20f).row()
        
        // Warning text
        val warningLabel = Label("This will permanently delete ALL progress:", skin, "body")
        warningLabel.setAlignment(Align.center)
        warningLabel.color = Color.YELLOW
        warningLabel.setFontScale(1.1f)
        panel.add(warningLabel).fillX().padBottom(15f).row()
        
        // Lost items list
        val lostItems = listOf(
            "All Buildings",
            "All Gold",
            "Crown Shards",
            "Heroes",
            "Quests",
            "Map Progress",
            "Everything!"
        )
        
        for (item in lostItems) {
            val itemLabel = Label("• $item", skin, "body")
            itemLabel.color = Color.LIGHT_GRAY
            itemLabel.setAlignment(Align.center)
            panel.add(itemLabel).fillX().padBottom(5f).row()
        }
        
        panel.add().height(20f).row()
        
        // Buttons
        val buttonsTable = Table()
        
        // Cancel button
        val cancelButton = TextButton("CANCEL", skin)
        cancelButton.label.setFontScale(1.2f)
        cancelButton.color = Color.GREEN
        cancelButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                dismiss()
            }
        })
        buttonsTable.add(cancelButton).size(150f, 60f).padRight(20f)
        
        // Confirm button container
        val confirmContainer = Table()
        
        confirmButton = TextButton("HOLD TO CONFIRM", skin)
        confirmButton.label.setFontScale(1.0f)
        confirmButton.color = Color.RED
        
        confirmButton.addListener(object : ClickListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                isHolding = true
                holdTimer = 0f
                return true
            }
            
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                isHolding = false
                holdTimer = 0f
                progressBar.value = 0f
            }
        })
        
        confirmContainer.add(confirmButton).size(200f, 60f).row()
        
        // Progress bar
        progressBar = ProgressBar(0f, HOLD_DURATION, 0.01f, false, skin)
        progressBar.value = 0f
        confirmContainer.add(progressBar).fillX().height(8f).padTop(5f)
        
        buttonsTable.add(confirmContainer)
        
        panel.add(buttonsTable).fillX()
        
        return panel
    }
    
    // Updates hold timer
    fun update(delta: Float) {
        if (isHolding) {
            holdTimer += delta
            progressBar.value = holdTimer
            
            if (holdTimer >= HOLD_DURATION) {
                isHolding = false
                onConfirm()
                dismiss()
            }
        }
    }
    
    // Dismisses dialog
    private fun dismiss() {
        addAction(
            Actions.sequence(
                Actions.fadeOut(0.2f),
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
