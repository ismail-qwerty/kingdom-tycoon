// PATH: core/src/main/java/com/ismail/kingdom/HallOfLegendsScreen.kt
package com.ismail.kingdom

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.ismail.kingdom.systems.HallOfLegendsSystem
import com.ismail.kingdom.systems.LegendaryBuff
import com.ismail.kingdom.utils.Formatters
import ktx.app.KtxScreen

// Hall of Legends screen with legendary buffs (Era 5)
class HallOfLegendsScreen(private val game: KingdomTycoonGame) : KtxScreen {

    private val SCREEN_WIDTH = 1080f
    private val SCREEN_HEIGHT = 1920f

    private val viewport = FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT)
    private val stage = Stage(viewport)
    private val batch = SpriteBatch()

    private lateinit var skin: Skin
    private lateinit var font: BitmapFont
    private lateinit var largeFontGold: BitmapFont

    private lateinit var gloryLabel: Label
    private val buffCards = mutableMapOf<String, BuffCard>()

    private val hallSystem = HallOfLegendsSystem()
    private val disposableTextures = mutableListOf<Texture>()

    override fun show() {
        Gdx.input.inputProcessor = stage

        // Load fonts
        font = BitmapFont()
        font.data.setScale(1.2f)
        font.color = Color.WHITE

        largeFontGold = BitmapFont()
        largeFontGold.data.setScale(2.0f)
        largeFontGold.color = Color.GOLD

        // Create skin
        createSkin()

        // Build UI
        buildUI()
    }

    // Creates UI skin
    private fun createSkin() {
        skin = Skin()

        val labelStyle = Label.LabelStyle(font, Color.WHITE)
        val goldLabelStyle = Label.LabelStyle(largeFontGold, Color.GOLD)

        skin.add("default", labelStyle)
        skin.add("gold", goldLabelStyle)

        val buttonStyle = TextButton.TextButtonStyle()
        buttonStyle.font = font
        buttonStyle.fontColor = Color.WHITE
        buttonStyle.up = createColorDrawable(Color(0.4f, 0.3f, 0.1f, 1f))
        buttonStyle.down = createColorDrawable(Color(0.5f, 0.4f, 0.2f, 1f))

        skin.add("default", buttonStyle)

        val scrollStyle = ScrollPane.ScrollPaneStyle()
        skin.add("default", scrollStyle)
    }

    // Builds complete UI
    private fun buildUI() {
        val root = Table()
        root.setFillParent(true)

        // Starfield background
        root.background = createStarfieldBackground()

        stage.addActor(root)

        // Title bar
        val titleBar = createTitleBar()
        root.add(titleBar).height(140f).fillX().row()

        // Glory display
        val gloryBar = createGloryBar()
        root.add(gloryBar).height(100f).fillX().padBottom(20f).row()

        // Buff grid (scrollable)
        val buffGrid = createBuffGrid()
        root.add(buffGrid).expand().fill().row()
    }

    // Creates title bar
    private fun createTitleBar(): Table {
        val table = Table()
        table.background = createColorDrawable(Color(0.1f, 0.08f, 0.05f, 0.9f))
        table.pad(20f)

        val backButton = TextButton("← BACK", skin)
        backButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                ScreenNavigator.goBack()
            }
        })

        val title = Label("HALL OF LEGENDS", skin, "gold")

        val subtitle = Label("Eternal Power Awaits", skin)
        subtitle.color = Color.LIGHT_GRAY

        val titleStack = Table()
        titleStack.add(title).row()
        titleStack.add(subtitle).padTop(5f)

        table.add(backButton).left().padRight(20f)
        table.add(titleStack).expandX().center()
        table.add().width(backButton.width) // Spacer

        return table
    }

    // Creates Glory display bar
    private fun createGloryBar(): Table {
        val table = Table()
        table.background = createColorDrawable(Color(0.15f, 0.1f, 0.08f, 0.95f))
        table.pad(20f)

        // Glory icon
        val gloryIcon = Label("👑", skin, "gold")
        gloryIcon.setFontScale(2f)

        // Glory amount
        val currentGlory = game.gameEngine.gameState.resources["glory"]?.amount ?: 0.0
        gloryLabel = Label("Glory: ${Formatters.formatGold(currentGlory)}", skin, "gold")

        // Progress indicator
        val unlockedCount = hallSystem.getUnlockedCount()
        val totalCount = hallSystem.getTotalCount()
        val progressLabel = Label("$unlockedCount / $totalCount Unlocked", skin)
        progressLabel.color = Color.CYAN

        table.add(gloryIcon).padRight(20f)
        table.add(gloryLabel).expandX().left()
        table.add(progressLabel).right()

        return table
    }

    // Creates buff grid with 3 tiers
    private fun createBuffGrid(): ScrollPane {
        val content = Table()
        content.pad(20f)

        // Tier 1
        val tier1Label = Label("═══ TIER I: FOUNDATIONS ═══", skin, "gold")
        tier1Label.setAlignment(Align.center)
        content.add(tier1Label).colspan(2).fillX().padBottom(15f).row()

        createTierCards(1, content)

        // Tier 2
        val tier2Label = Label("═══ TIER II: ASCENSION ═══", skin, "gold")
        tier2Label.setAlignment(Align.center)
        content.add(tier2Label).colspan(2).fillX().padTop(30f).padBottom(15f).row()

        createTierCards(2, content)

        // Tier 3
        val tier3Label = Label("═══ TIER III: ETERNITY ═══", skin, "gold")
        tier3Label.setAlignment(Align.center)
        content.add(tier3Label).colspan(2).fillX().padTop(30f).padBottom(15f).row()

        createTierCards(3, content)

        val scrollPane = ScrollPane(content, skin)
        scrollPane.setScrollingDisabled(true, false)
        // scrollPane.setClip(true)
        scrollPane.setFadeScrollBars(false)

        return scrollPane
    }

    // Creates cards for a specific tier
    private fun createTierCards(tier: Int, content: Table) {
        val buffs = hallSystem.getBuffsByTier(tier)

        var column = 0
        for (buff in buffs) {
            val card = BuffCard(buff)
            buffCards[buff.id] = card

            content.add(card).width(500f).height(180f).pad(10f)

            column++
            if (column >= 2) {
                content.row()
                column = 0
            }
        }

        // Add empty cell if odd number
        if (column == 1) {
            content.add().width(500f).height(180f)
            content.row()
        }
    }

    // Updates Glory display
    private fun updateGloryDisplay() {
        val currentGlory = game.gameEngine.gameState.resources["glory"]?.amount ?: 0.0
        gloryLabel.setText("Glory: ${Formatters.formatGold(currentGlory)}")
    }

    // Handles buff unlock
    private fun onBuffUnlock(buff: LegendaryBuff) {
        val success = hallSystem.unlockBuff(buff.id, game.gameEngine.gameState)

        if (success) {
            // Play unlock animation
            playUnlockAnimation(buff)

            // Update card
            buffCards[buff.id]?.updateState()

            // Update Glory display
            updateGloryDisplay()

            // Show success message
            Gdx.app.log("HallOfLegends", "Unlocked: ${buff.name}")

            // TODO: Play epic sound effect

        } else {
            // Show error
            Gdx.app.log("HallOfLegends", "Cannot unlock: ${buff.name}")

            // Shake card
            buffCards[buff.id]?.shake()
        }
    }

    // Plays unlock animation
    private fun playUnlockAnimation(buff: LegendaryBuff) {
        val card = buffCards[buff.id] ?: return

        // Golden light burst overlay
        val overlay = Image(createColorDrawable(Color(1f, 0.9f, 0.5f, 0f)))
        overlay.setFillParent(true)
        stage.addActor(overlay)

        overlay.addAction(
            Actions.sequence(
                Actions.fadeIn(0.3f),
                Actions.delay(0.5f),
                Actions.fadeOut(0.7f),
                Actions.removeActor()
            )
        )

        // Card glow effect
        card.addAction(
            Actions.sequence(
                Actions.color(Color.GOLD, 0.2f),
                Actions.color(Color.WHITE, 0.2f),
                Actions.color(Color.GOLD, 0.2f),
                Actions.color(Color.WHITE, 0.2f)
            )
        )

        // Spawn particles at card center
        val cardPos = card.localToStageCoordinates(Vector2(250f, 90f))
        ParticleManager.spawn(ParticleType.PRESTIGE_FLASH, cardPos, stage)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.05f, 0.03f, 0.08f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Update particles
        ParticleManager.update(delta)

        // Draw stage
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun hide() {
        stage.dispose()
        ParticleManager.clear()
    }

    override fun dispose() {
        stage.dispose()
        batch.dispose()
        font.dispose()
        largeFontGold.dispose()
        skin.dispose()

        for (texture in disposableTextures) {
            texture.dispose()
        }
        disposableTextures.clear()

        ParticleManager.clear()
    }

    // Creates starfield background
    private fun createStarfieldBackground(): TextureRegionDrawable? {
        return try {
            val pixmap = Pixmap(512, 512, Pixmap.Format.RGBA8888)

            // Dark purple gradient
            for (y in 0 until 512) {
                val brightness = (y / 512f) * 0.1f
                pixmap.setColor(0.05f + brightness, 0.03f + brightness, 0.08f + brightness, 1f)
                pixmap.drawLine(0, y, 511, y)
            }

            // Add stars
            pixmap.setColor(Color.WHITE)
            for (i in 0..100) {
                val x = (Math.random() * 512).toInt()
                val y = (Math.random() * 512).toInt()
                val size = (Math.random() * 2 + 1).toInt()
                pixmap.fillCircle(x, y, size)
            }

            val texture = Texture(pixmap)
            pixmap.dispose()

            disposableTextures.add(texture)
            TextureRegionDrawable(texture)
        } catch (e: Exception) {
            null
        }
    }

    // Creates colored drawable
    private fun createColorDrawable(color: Color): TextureRegionDrawable? {
        return try {
            val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
            pixmap.setColor(color)
            pixmap.fill()
            val texture = Texture(pixmap)
            pixmap.dispose()

            disposableTextures.add(texture)
            TextureRegionDrawable(texture)
        } catch (e: Exception) {
            null
        }
    }

    // Buff card widget
    inner class BuffCard(private val buff: LegendaryBuff) : Table() {

        private val nameLabel: Label
        private val descLabel: Label
        private val costLabel: Label
        private val unlockButton: TextButton
        private val unlockedLabel: Label

        init {
            val bgColor = when (buff.tier) {
                1 -> Color(0.25f, 0.2f, 0.15f, 0.95f)
                2 -> Color(0.3f, 0.2f, 0.25f, 0.95f)
                3 -> Color(0.35f, 0.25f, 0.15f, 0.95f)
                else -> Color(0.25f, 0.2f, 0.15f, 0.95f)
            }
            background = createColorDrawable(bgColor)
            pad(15f)

            // Buff icon
            val icon = Label(getBuffIcon(buff.tier), skin, "gold")
            icon.setFontScale(2f)

            // Buff info
            val infoTable = Table()
            nameLabel = Label(buff.name, skin, "gold")
            nameLabel.setFontScale(1.1f)

            descLabel = Label(buff.description, skin)
            descLabel.color = Color.LIGHT_GRAY
            descLabel.setWrap(true)

            costLabel = Label("Cost: ${Formatters.formatGold(buff.cost)} 👑", skin)
            costLabel.color = Color.YELLOW

            infoTable.add(nameLabel).left().row()
            infoTable.add(descLabel).left().width(300f).padTop(5f).row()
            infoTable.add(costLabel).left().padTop(10f).row()

            // Unlock button
            unlockButton = TextButton("UNLOCK", skin)
            unlockButton.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    onBuffUnlock(buff)
                }
            })

            // Unlocked label
            unlockedLabel = Label("✓ UNLOCKED", skin, "gold")
            unlockedLabel.isVisible = false

            add(icon).size(60f).padRight(15f).top()
            add(infoTable).expandX().left().top()
            row()
            add(unlockButton).colspan(2).size(180f, 50f).padTop(10f)
            add(unlockedLabel).colspan(2).padTop(10f)

            updateState()
        }

        // Gets buff icon based on tier
        private fun getBuffIcon(tier: Int): String {
            return when (tier) {
                1 -> "⭐"
                2 -> "💫"
                3 -> "✨"
                else -> "⭐"
            }
        }

        // Updates card state
        fun updateState() {
            if (buff.isUnlocked) {
                unlockButton.isVisible = false
                unlockedLabel.isVisible = true
                background = createColorDrawable(Color(0.4f, 0.35f, 0.2f, 0.95f))
            } else {
                val canUnlock = hallSystem.canUnlock(buff.id, game.gameEngine.gameState)
                unlockButton.isDisabled = !canUnlock
                unlockButton.setText(if (canUnlock) "UNLOCK" else "LOCKED")
                unlockButton.isVisible = true
                unlockedLabel.isVisible = false
            }
        }

        // Shakes card on failed unlock
        fun shake() {
            addAction(
                Actions.sequence(
                    Actions.moveBy(-10f, 0f, 0.05f),
                    Actions.moveBy(20f, 0f, 0.05f),
                    Actions.moveBy(-20f, 0f, 0.05f),
                    Actions.moveBy(10f, 0f, 0.05f)
                )
            )
        }
    }
}
