// PATH: core/src/main/java/com/ismail/kingdom/ui/SpellPanel.kt
package com.ismail.kingdom.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.ismail.kingdom.GameEngine
import com.ismail.kingdom.ParticleManager
import com.ismail.kingdom.ParticleType
import com.ismail.kingdom.systems.Spell
import com.ismail.kingdom.systems.SpellSystem
import com.ismail.kingdom.utils.Formatters

// Panel displaying spell cards in 2-column grid (Era 4+)
class SpellPanel(
    private val stage: Stage,
    private val skin: Skin,
    private val gameEngine: GameEngine
) : Actor() {

    private val spellSystem = SpellSystem()
    private var scrollPane: ScrollPane? = null
    private val spellCards = mutableMapOf<String, SpellCard>()

    private var manaLabel: Label? = null
    private var manaGenLabel: Label? = null

    private val disposableTextures = mutableListOf<Texture>()

    private var updateTimer = 0f
    private val UPDATE_INTERVAL = 0.5f

    init {
        rebuildPanel()
    }

    // Rebuilds the spell panel
    fun rebuildPanel() {
        // Clear existing
        scrollPane?.remove()
        spellCards.clear()

        // Create content table
        val content = Table()
        content.pad(20f)

        // Mana display at top
        val manaBar = createManaBar()
        content.add(manaBar).colspan(2).fillX().height(80f).padBottom(20f).row()

        // Get all spells
        val spells = spellSystem.getSpellsAvailable(gameEngine.gameState)

        // Create spell cards in 2-column grid
        var column = 0
        for (spell in spells) {
            val card = SpellCard(spell)
            spellCards[spell.id] = card

            content.add(card).width(480f).height(220f).pad(10f)

            column++
            if (column >= 2) {
                content.row()
                column = 0
            }
        }

        // Create scroll pane
        scrollPane = ScrollPane(content, skin)
        scrollPane?.setScrollingDisabled(true, false)
        // scrollPane?.setClip(true)
        scrollPane?.setFadeScrollBars(false)

        scrollPane?.setSize(width, height)
        stage.addActor(scrollPane)
    }

    // Creates mana display bar
    private fun createManaBar(): Table {
        val table = Table()
        table.background = createColorDrawable(Color(0.2f, 0.1f, 0.3f, 1f))
        table.pad(15f)

        // Mana icon
        val manaIcon = Label("✨", skin, "gold")
        manaIcon.setFontScale(1.5f)

        // Mana amount
        val currentMana = gameEngine.gameState.resources["mana"]?.amount ?: 0.0
        manaLabel = Label("Mana: ${Formatters.formatGold(currentMana)}", skin, "gold")

        // Mana generation rate
        val manaGen = spellSystem.manaGenRate(gameEngine.gameState)
        manaGenLabel = Label("(+${Formatters.formatGold(manaGen)}/s)", skin)
        manaGenLabel?.color = Color.CYAN

        table.add(manaIcon).padRight(15f)
        table.add(manaLabel).expandX().left()
        table.add(manaGenLabel).right()

        return table
    }

    // Updates panel
    fun update(delta: Float) {
        updateTimer += delta

        if (updateTimer >= UPDATE_INTERVAL) {
            updateTimer = 0f

            // Update mana display
            val currentMana = gameEngine.gameState.resources["mana"]?.amount ?: 0.0
            manaLabel?.setText("Mana: ${Formatters.formatGold(currentMana)}")

            val manaGen = spellSystem.manaGenRate(gameEngine.gameState)
            manaGenLabel?.setText("(+${Formatters.formatGold(manaGen)}/s)")

            // Update spell cards
            for (card in spellCards.values) {
                card.updateState()
            }
        }

        // Update spell system
        spellSystem.update(delta, gameEngine.gameState)
    }

    // Handles spell cast
    private fun onSpellCast(spell: Spell) {
        val result = spellSystem.castSpell(spell.id, gameEngine.gameState)

        if (result.success) {
            // Play cast animation
            playCastAnimation(spell, result.animationType)

            // Update card
            spellCards[spell.id]?.updateState()

            // Update mana display
            val currentMana = gameEngine.gameState.resources["mana"]?.amount ?: 0.0
            manaLabel?.setText("Mana: ${Formatters.formatGold(currentMana)}")

            // Show success message
            Gdx.app.log("SpellPanel", result.message)

            // TODO: Play spell sound effect

        } else {
            // Show error message
            Gdx.app.log("SpellPanel", "Cast failed: ${result.message}")

            // Shake card
            spellCards[spell.id]?.shake()
        }
    }

    // Plays spell cast animation
    private fun playCastAnimation(spell: Spell, animationType: String) {
        // Screen tint overlay
        val overlay = Image(createColorDrawable(getSpellColor(animationType)))
        overlay.setFillParent(true)
        overlay.color.a = 0f
        stage.addActor(overlay)

        overlay.addAction(
            Actions.sequence(
                Actions.fadeIn(0.2f),
                Actions.delay(0.3f),
                Actions.fadeOut(0.5f),
                Actions.removeActor()
            )
        )

        // Spawn particles at center
        val centerX = stage.width / 2f
        val centerY = stage.height / 2f

        when (animationType) {
            "gold_burst" -> ParticleManager.spawn(ParticleType.MILESTONE_BURST, com.badlogic.gdx.math.Vector2(centerX, centerY))
            "mana_surge" -> ParticleManager.spawn(ParticleType.PRESTIGE_FLASH, com.badlogic.gdx.math.Vector2(centerX, centerY))
            "arcane_storm" -> {
                // Multiple bursts
                for (i in 0..4) {
                    ParticleManager.spawn(ParticleType.COIN_BURST, com.badlogic.gdx.math.Vector2(centerX + (i - 2) * 100f, centerY))
                }
            }
            else -> ParticleManager.spawn(ParticleType.COIN_BURST, com.badlogic.gdx.math.Vector2(centerX, centerY))
        }

        // TODO: Play dramatic sound effect
    }

    // Gets spell color for screen tint
    private fun getSpellColor(animationType: String): Color {
        return when (animationType) {
            "gold_burst" -> Color(1f, 0.84f, 0f, 0.3f) // Gold
            "time_warp" -> Color(0.5f, 0.5f, 1f, 0.3f) // Blue
            "mana_surge" -> Color(0.5f, 1f, 1f, 0.3f) // Cyan
            "enchant" -> Color(1f, 0.5f, 1f, 0.3f) // Magenta
            "crystal_clear" -> Color(1f, 1f, 1f, 0.3f) // White
            "arcane_storm" -> Color(0.7f, 0.3f, 1f, 0.3f) // Purple
            "void_drain" -> Color(0.3f, 0f, 0.5f, 0.3f) // Dark purple
            "legendary_seal" -> Color(1f, 0.9f, 0.5f, 0.3f) // Bright gold
            else -> Color(0.5f, 0.5f, 0.5f, 0.3f)
        }
    }

    // Refreshes panel
    fun refresh() {
        for (card in spellCards.values) {
            card.updateState()
        }
    }

    override fun setSize(width: Float, height: Float) {
        super.setSize(width, height)
        scrollPane?.setSize(width, height)
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

    // Spell card widget
    inner class SpellCard(private val spell: Spell) : Table() {

        private val nameLabel: Label
        private val descLabel: Label
        private val costLabel: Label
        private val cooldownLabel: Label
        private val castButton: TextButton

        init {
            background = createColorDrawable(Color(0.25f, 0.15f, 0.3f, 1f))
            pad(15f)

            // Spell icon (emoji placeholder)
            val icon = Label(getSpellIcon(spell.id), skin, "gold")
            icon.setFontScale(2.5f)

            // Spell info
            val infoTable = Table()
            nameLabel = Label(spell.name, skin, "gold")
            nameLabel.setFontScale(1.2f)

            descLabel = Label(spell.description, skin)
            descLabel.color = Color.LIGHT_GRAY
            descLabel.setWrap(true)

            costLabel = Label("Cost: ${Formatters.formatGold(spell.manaCost)} ✨", skin)
            costLabel.color = Color.CYAN

            cooldownLabel = Label("", skin)
            cooldownLabel.color = Color.ORANGE

            infoTable.add(nameLabel).left().row()
            infoTable.add(descLabel).left().width(300f).padTop(5f).row()
            infoTable.add(costLabel).left().padTop(10f).row()
            infoTable.add(cooldownLabel).left().padTop(5f).row()

            // Cast button
            castButton = TextButton("CAST", skin)
            castButton.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    onSpellCast(spell)
                }
            })

            add(icon).size(80f).padRight(20f).top()
            add(infoTable).expandX().left().top()
            row()
            add(castButton).colspan(2).size(180f, 50f).padTop(10f)

            updateState()
        }

        // Gets spell icon emoji
        private fun getSpellIcon(spellId: String): String {
            return when (spellId) {
                "gold_rush" -> "💰"
                "time_warp" -> "⏰"
                "mana_surge" -> "✨"
                "enchant_all" -> "🔮"
                "crystal_clear" -> "💎"
                "arcane_storm" -> "⚡"
                "void_drain" -> "🌀"
                "legendary_seal" -> "👑"
                else -> "✨"
            }
        }

        // Updates card state
        fun updateState() {
            val canCast = spellSystem.canCast(spell.id, gameEngine.gameState)

            // Update cooldown display
            if (spell.remainingCooldown > 0) {
                val minutes = spell.remainingCooldown / 60
                val seconds = spell.remainingCooldown % 60
                cooldownLabel.setText("Cooldown: ${minutes}m ${seconds}s")
                cooldownLabel.isVisible = true
            } else {
                cooldownLabel.isVisible = false
            }

            // Update button state
            castButton.isDisabled = !canCast

            if (spell.id == "legendary_seal") {
                val remaining = spellSystem.getLegendarySealUsesRemaining()
                castButton.setText("CAST ($remaining left)")
            } else {
                castButton.setText(if (canCast) "CAST" else "ON COOLDOWN")
            }

            // Highlight if active
            if (spell.isActive) {
                background = createColorDrawable(Color(0.4f, 0.25f, 0.5f, 1f))
            } else {
                background = createColorDrawable(Color(0.25f, 0.15f, 0.3f, 1f))
            }
        }

        // Shakes card on failed cast
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
