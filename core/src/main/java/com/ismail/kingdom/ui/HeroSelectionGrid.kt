// PATH: core/src/main/java/com/ismail/kingdom/ui/HeroSelectionGrid.kt
package com.ismail.kingdom.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.ismail.kingdom.assets.AssetDescriptors
import com.ismail.kingdom.assets.GameAssets
import com.ismail.kingdom.models.Hero

// Hero selection grid for Legend prestige
class HeroSelectionGrid(
    private val heroes: List<Hero>,
    private val ownedHeroIds: Set<String>,
    private val skin: Skin,
    private val onHeroSelected: (Hero) -> Unit
) : Table() {
    
    private val heroCards = mutableListOf<HeroCard>()
    private var selectedHero: Hero? = null
    
    init {
        background = createColorDrawable(Color(0.1f, 0.08f, 0.05f, 0.95f))
        pad(20f)
        
        // Title
        val titleLabel = Label("CHOOSE YOUR LEGEND", skin, "gold-large")
        titleLabel.setAlignment(Align.center)
        titleLabel.color = Color.GOLD
        titleLabel.setFontScale(1.5f)
        add(titleLabel).colspan(3).fillX().padBottom(20f).row()
        
        // Subtitle
        val subtitleLabel = Label("Select a hero to unlock their permanent passive", skin, "body")
        subtitleLabel.setAlignment(Align.center)
        subtitleLabel.color = Color.LIGHT_GRAY
        subtitleLabel.setFontScale(0.9f)
        add(subtitleLabel).colspan(3).fillX().padBottom(25f).row()
        
        // Create 3x4 grid (12 heroes)
        var column = 0
        for (hero in heroes) {
            val isOwned = ownedHeroIds.contains(hero.id)
            val card = HeroCard(hero, isOwned, skin) { selectedHero ->
                onCardSelected(selectedHero)
            }
            
            heroCards.add(card)
            add(card).size(180f, 220f).pad(8f)
            
            column++
            if (column >= 3) {
                row()
                column = 0
            }
        }
    }
    
    // Handles card selection
    private fun onCardSelected(hero: Hero) {
        // Deselect all cards
        for (card in heroCards) {
            card.setSelected(false)
        }
        
        // Select clicked card
        val clickedCard = heroCards.find { it.hero.id == hero.id }
        clickedCard?.setSelected(true)
        
        selectedHero = hero
        onHeroSelected(hero)
    }
    
    // Gets selected hero
    fun getSelectedHero(): Hero? = selectedHero
    
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

// Individual hero card
class HeroCard(
    val hero: Hero,
    private val isOwned: Boolean,
    private val skin: Skin,
    private val onSelected: (Hero) -> Unit
) : Table() {
    
    private var isSelected = false
    
    init {
        background = createColorDrawable(
            if (isOwned) Color(0.15f, 0.12f, 0.09f, 0.7f) else Color(0.22f, 0.18f, 0.14f, 0.95f)
        )
        pad(10f)
        
        // Portrait
        val portrait = createPortrait()
        add(portrait).size(80f).padBottom(10f).row()
        
        // Name
        val nameLabel = Label(hero.name, skin, "body")
        nameLabel.setAlignment(Align.center)
        nameLabel.setFontScale(0.95f)
        nameLabel.color = if (isOwned) Color.GRAY else Color.WHITE
        nameLabel.setWrap(true)
        add(nameLabel).width(160f).padBottom(8f).row()
        
        // Passive description
        val passiveLabel = Label(hero.passiveDescription, skin, "body")
        passiveLabel.setAlignment(Align.center)
        passiveLabel.setFontScale(0.75f)
        passiveLabel.color = if (isOwned) Color.DARK_GRAY else Color.LIGHT_GRAY
        passiveLabel.setWrap(true)
        add(passiveLabel).width(160f).padBottom(5f).row()
        
        // Owned checkmark
        if (isOwned) {
            val checkmarkLabel = Label("✓ OWNED", skin, "body")
            checkmarkLabel.setAlignment(Align.center)
            checkmarkLabel.color = Color.DARK_GRAY
            checkmarkLabel.setFontScale(0.8f)
            add(checkmarkLabel).fillX()
            
            // Dim the entire card
            color = Color(0.6f, 0.6f, 0.6f, 1f)
        }
        
        // Click listener (only if not owned)
        if (!isOwned) {
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    onSelected(hero)
                }
            })
        }
    }
    
    // Creates hero portrait
    private fun createPortrait(): Image {
        return try {
            if (GameAssets.isLoaded()) {
                val texture = GameAssets.getHeroPortrait(hero.id)
                Image(texture)
            } else {
                createPlaceholderPortrait()
            }
        } catch (e: Exception) {
            createPlaceholderPortrait()
        }
    }
    
    // Creates placeholder portrait
    private fun createPlaceholderPortrait(): Image {
        val pixmap = Pixmap(80, 80, Pixmap.Format.RGBA8888)
        pixmap.setColor(if (isOwned) Color.DARK_GRAY else Color.BROWN)
        pixmap.fillCircle(40, 40, 40)
        val texture = Texture(pixmap)
        pixmap.dispose()
        return Image(texture)
    }
    
    // Sets selection state
    fun setSelected(selected: Boolean) {
        isSelected = selected
        
        if (selected && !isOwned) {
            // Gold border and scale animation
            background = createColorDrawable(Color(0.8f, 0.6f, 0.2f, 0.95f))
            clearActions()
            addAction(
                Actions.sequence(
                    Actions.scaleTo(1.1f, 1.1f, 0.2f, Interpolation.elasticOut),
                    Actions.scaleTo(1.0f, 1.0f, 0.2f, Interpolation.elasticIn)
                )
            )
        } else if (!isOwned) {
            background = createColorDrawable(Color(0.22f, 0.18f, 0.14f, 0.95f))
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
            TextureRegionDrawable(texture)
        } catch (e: Exception) {
            null
        }
    }
}
