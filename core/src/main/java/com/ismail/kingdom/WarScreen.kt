// PATH: core/src/main/java/com/ismail/kingdom/WarScreen.kt
package com.ismail.kingdom

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.ismail.kingdom.systems.EnemyCamp
import com.ismail.kingdom.systems.WarSystem
import com.ismail.kingdom.ui.CoinParticlePool
import com.ismail.kingdom.utils.Formatters
import ktx.app.KtxScreen

// War screen showing enemy camps and raid mechanics (Era 3+)
class WarScreen(private val game: KingdomTycoonGame) : KtxScreen {

    private val SCREEN_WIDTH = 1080f
    private val SCREEN_HEIGHT = 1920f

    private val viewport = FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT)
    private val stage = Stage(viewport)
    private val batch = SpriteBatch()

    private lateinit var skin: Skin
    private lateinit var font: BitmapFont
    private lateinit var largeFontGold: BitmapFont

    private lateinit var militaryPowerLabel: Label
    private val campCards = mutableMapOf<String, CampCard>()

    private val warSystem = WarSystem()
    private val disposableTextures = mutableListOf<Texture>()

    override fun show() {
        Gdx.input.inputProcessor = stage

        // Load fonts
        font = BitmapFont()
        font.data.setScale(1.2f)
        font.color = Color.WHITE

        largeFontGold = BitmapFont()
        largeFontGold.data.setScale(1.8f)
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
        buttonStyle.up = createColorDrawable(Color(0.3f, 0.2f, 0.1f, 1f))
        buttonStyle.down = createColorDrawable(Color(0.4f, 0.3f, 0.2f, 1f))

        skin.add("default", buttonStyle)

        val scrollStyle = ScrollPane.ScrollPaneStyle()
        skin.add("default", scrollStyle)
    }

    // Builds complete UI
    private fun buildUI() {
        val root = Table()
        root.setFillParent(true)
        stage.addActor(root)

        // Title bar
        val titleBar = createTitleBar()
        root.add(titleBar).height(120f).fillX().row()

        // Military power display
        val powerBar = createPowerBar()
        root.add(powerBar).height(80f).fillX().padBottom(20f).row()

        // Camp map (scrollable)
        val campMap = createCampMap()
        root.add(campMap).expand().fill().row()
    }

    // Creates title bar with back button
    private fun createTitleBar(): Table {
        val table = Table()
        table.background = createColorDrawable(Color(0.15f, 0.1f, 0.08f, 1f))
        table.pad(20f)

        val backButton = TextButton("← BACK", skin)
        backButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                ScreenNavigator.goBack()
            }
        })

        val title = Label("WAR CAMPS", skin, "gold")

        table.add(backButton).left().padRight(20f)
        table.add(title).expandX().center()
        table.add().width(backButton.width) // Spacer for centering

        return table
    }

    // Creates military power display bar
    private fun createPowerBar(): Table {
        val table = Table()
        table.background = createColorDrawable(Color(0.2f, 0.15f, 0.1f, 1f))
        table.pad(15f)

        val powerIcon = Label("⚔", skin, "gold")

        val militaryPower = warSystem.calculateMilitaryPower(game.gameEngine.gameState)
        militaryPowerLabel = Label("Your Power: ${Formatters.formatGold(militaryPower)}", skin, "gold")

        table.add(powerIcon).padRight(15f)
        table.add(militaryPowerLabel).expandX().left()

        return table
    }

    // Creates scrollable camp map
    private fun createCampMap(): ScrollPane {
        val content = Table()
        content.pad(20f)

        val currentEra = game.gameEngine.gameState.currentEra
        val camps = warSystem.getCampsForEra(currentEra)

        // Create camp cards
        for (camp in camps) {
            val card = CampCard(camp)
            campCards[camp.id] = card
            content.add(card).width(SCREEN_WIDTH - 80f).height(200f).padBottom(20f).row()
        }

        val scrollPane = ScrollPane(content, skin)
        scrollPane.setScrollingDisabled(true, false)
        // scrollPane.setClip(true)
        scrollPane.setFadeScrollBars(false)

        return scrollPane
    }

    // Updates military power display
    private fun updatePowerDisplay() {
        val militaryPower = warSystem.calculateMilitaryPower(game.gameEngine.gameState)
        militaryPowerLabel.setText("Your Power: ${Formatters.formatGold(militaryPower)}")
    }

    // Handles raid button click
    private fun onRaidClicked(camp: EnemyCamp) {
        val result = warSystem.raid(camp, game.gameEngine.gameState)

        if (result.success) {
            // Play victory animation
            playVictoryAnimation(camp)

            // Update card
            campCards[camp.id]?.updateState()

            // Spawn coin particles
            val cardPos = campCards[camp.id]?.localToStageCoordinates(
                com.badlogic.gdx.math.Vector2(SCREEN_WIDTH / 2f, 100f)
            )
            if (cardPos != null) {
                CoinParticlePool.spawn(cardPos)
            }

            // Update power display
            updatePowerDisplay()

            // TODO: Update quest progress (RAID_CAMPS, EARN_WAR_GOLD)

        } else {
            // Play defeat animation
            playDefeatAnimation(camp)

            // Show message
            Gdx.app.log("WarScreen", result.message)
        }
    }

    // Plays victory animation (swords clashing, gold rain)
    private fun playVictoryAnimation(camp: EnemyCamp) {
        val card = campCards[camp.id] ?: return

        // Flash gold
        card.addAction(
            Actions.sequence(
                Actions.color(Color.GOLD, 0.1f),
                Actions.color(Color.WHITE, 0.1f),
                Actions.color(Color.GOLD, 0.1f),
                Actions.color(Color.WHITE, 0.1f)
            )
        )

        // TODO: Add sword clash sprite animation
        // TODO: Spawn gold rain particles
    }

    // Plays defeat animation
    private fun playDefeatAnimation(camp: EnemyCamp) {
        val card = campCards[camp.id] ?: return

        // Shake animation
        card.addAction(
            Actions.sequence(
                Actions.moveBy(-10f, 0f, 0.05f),
                Actions.moveBy(20f, 0f, 0.05f),
                Actions.moveBy(-20f, 0f, 0.05f),
                Actions.moveBy(10f, 0f, 0.05f)
            )
        )
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.1f, 0.08f, 0.05f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Update war system
        val currentEra = game.gameEngine.gameState.currentEra
        warSystem.update(delta, currentEra)

        // Update camp cards
        for (card in campCards.values) {
            card.update(delta)
        }

        // Update particles
        CoinParticlePool.update(delta)

        // Draw stage
        stage.act(delta)
        stage.draw()

        // Draw particles
        batch.begin()
        CoinParticlePool.draw(batch)
        batch.end()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun hide() {
        stage.dispose()
        CoinParticlePool.clear()
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

        CoinParticlePool.clear()
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

    // Camp card widget
    inner class CampCard(private val camp: EnemyCamp) : Table() {

        private val nameLabel: Label
        private val levelLabel: Label
        private val rewardLabel: Label
        private val powerLabel: Label
        private val raidButton: TextButton
        private val respawnLabel: Label

        init {
            background = createColorDrawable(Color(0.25f, 0.2f, 0.15f, 1f))
            pad(15f)

            // Camp icon (placeholder emoji)
            val icon = Label("⚔", skin, "gold")
            icon.setFontScale(2f)

            // Camp info
            val infoTable = Table()
            nameLabel = Label(camp.name, skin, "gold")
            levelLabel = Label("Level ${camp.level}", skin)
            levelLabel.color = Color.LIGHT_GRAY

            rewardLabel = Label(
                "💰 ${Formatters.formatGold(camp.goldReward)} + 🔩 ${Formatters.formatGold(camp.ironReward)}",
                skin
            )
            rewardLabel.color = Color.YELLOW

            powerLabel = Label("Required: ${Formatters.formatGold(camp.requiredPower)} ⚔", skin)
            powerLabel.color = Color.ORANGE

            infoTable.add(nameLabel).left().row()
            infoTable.add(levelLabel).left().padTop(5f).row()
            infoTable.add(rewardLabel).left().padTop(10f).row()
            infoTable.add(powerLabel).left().padTop(5f).row()

            // Raid button
            raidButton = TextButton("RAID", skin)
            raidButton.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    onRaidClicked(camp)
                }
            })

            // Respawn label (initially hidden)
            respawnLabel = Label("", skin)
            respawnLabel.color = Color.RED
            respawnLabel.isVisible = false

            add(icon).size(80f).padRight(20f)
            add(infoTable).expandX().left()
            add(raidButton).size(120f, 60f).padLeft(20f)
            row()
            add(respawnLabel).colspan(3).padTop(10f)

            updateState()
        }

        // Updates card state based on camp status
        fun updateState() {
            if (camp.isDefeated) {
                raidButton.isDisabled = true
                raidButton.setText("DEFEATED")
                respawnLabel.isVisible = true
                respawnLabel.setText("Respawns in ${camp.respawnSeconds}s")
            } else {
                val canRaid = warSystem.canRaid(camp, game.gameEngine.gameState)
                raidButton.isDisabled = !canRaid
                raidButton.setText(if (canRaid) "RAID" else "TOO WEAK")
                respawnLabel.isVisible = false
            }
        }

        // Updates respawn timer
        fun update(delta: Float) {
            if (camp.isDefeated && camp.respawnSeconds > 0) {
                respawnLabel.setText("Respawns in ${camp.respawnSeconds}s")
            } else if (camp.respawnSeconds == 0 && camp.isDefeated) {
                updateState()
            }
        }
    }
}
