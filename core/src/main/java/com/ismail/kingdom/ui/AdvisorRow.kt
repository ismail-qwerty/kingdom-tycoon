// PATH: core/src/main/java/com/ismail/kingdom/ui/AdvisorRow.kt
package com.ismail.kingdom.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.ismail.kingdom.assets.AssetDescriptors
import com.ismail.kingdom.assets.GameAssets
import com.ismail.kingdom.models.Advisor
import com.ismail.kingdom.models.Building
import com.ismail.kingdom.utils.Formatters

// Custom actor for displaying a single advisor row
class AdvisorRow(
    private val advisor: Advisor,
    private val building: Building?,
    private val skin: Skin,
    private val onHireClicked: (Advisor) -> Unit
) : Table() {
    
    private val nameLabel: Label
    private val taglineLabel: Label
    private val automatesLabel: Label
    private val hireButton: TextButton
    private val hiredLabel: Label
    private val checkmarkLabel: Label
    private val gearIcon: Image
    private val requirementLabel: Label
    
    private var gearRotation = 0f
    
    init {
        // Set background
        background = createColorDrawable(Color(0.2f, 0.16f, 0.12f, 0.95f))
        pad(12f)
        
        // Portrait (64x64 circular)
        val portraitStack = Stack()
        val portrait = createAdvisorPortrait()
        portraitStack.add(portrait)
        add(portraitStack).size(64f).padRight(12f)
        
        // Info column
        val infoTable = Table()
        
        // Name
        nameLabel = Label(advisor.name, skin, "body")
        nameLabel.setAlignment(Align.left)
        nameLabel.setFontScale(1.1f)
        infoTable.add(nameLabel).left().row()
        
        // Tagline
        taglineLabel = Label(advisor.description, skin, "body")
        taglineLabel.setFontScale(0.75f)
        taglineLabel.color = Color.LIGHT_GRAY
        taglineLabel.setWrap(true)
        infoTable.add(taglineLabel).left().width(180f).row()
        
        // Automates text
        val buildingName = building?.name ?: "Unknown"
        automatesLabel = Label("Automates: $buildingName", skin, "gold-small")
        automatesLabel.setFontScale(0.8f)
        automatesLabel.color = Color.GOLD
        infoTable.add(automatesLabel).left().padTop(5f)
        
        add(infoTable).expandX().left().padRight(10f).row()
        
        // Bottom row: hire button OR hired status
        if (advisor.isUnlocked) {
            // Hired status
            val hiredTable = Table()
            
            checkmarkLabel = Label("✓", skin, "gold-large")
            checkmarkLabel.color = Color.GREEN
            hiredTable.add(checkmarkLabel).padRight(5f)
            
            hiredLabel = Label("HIRED", skin, "body")
            hiredLabel.color = Color.GREEN
            hiredLabel.setFontScale(0.9f)
            hiredTable.add(hiredLabel).padRight(10f)
            
            // Animated gear icon
            gearIcon = createGearIcon()
            gearIcon.setOrigin(Align.center)
            gearIcon.addAction(
                Actions.forever(
                    Actions.rotateBy(-360f, 4f, Interpolation.linear)
                )
            )
            hiredTable.add(gearIcon).size(24f)
            
            add(hiredTable).colspan(2).center().padTop(8f)
            
            hireButton = TextButton("", skin)
            hireButton.isVisible = false
            requirementLabel = Label("", skin, "body")
            requirementLabel.isVisible = false
            
        } else {
            // Check if building requirement met
            val canHire = building != null && building.count > 0
            
            if (canHire) {
                // Show hire button
                hireButton = TextButton("HIRE\n${Formatters.formatGold(advisor.cost)}", skin)
                hireButton.label.setAlignment(Align.center)
                hireButton.label.setFontScale(0.85f)
                
                hireButton.addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        onHireClicked(advisor)
                    }
                })
                
                add(hireButton).colspan(2).size(140f, 60f).center().padTop(8f)
                
                requirementLabel = Label("", skin, "body")
                requirementLabel.isVisible = false
                
            } else {
                // Show requirement
                requirementLabel = Label("Requires: $buildingName", skin, "body")
                requirementLabel.setFontScale(0.8f)
                requirementLabel.color = Color.GRAY
                requirementLabel.setAlignment(Align.center)
                add(requirementLabel).colspan(2).center().padTop(8f)
                
                hireButton = TextButton("", skin)
                hireButton.isVisible = false
                
                // Grey out the row
                color = Color(0.6f, 0.6f, 0.6f, 1f)
            }
            
            checkmarkLabel = Label("", skin, "body")
            hiredLabel = Label("", skin, "body")
            gearIcon = Image()
            checkmarkLabel.isVisible = false
            hiredLabel.isVisible = false
            gearIcon.isVisible = false
        }
    }
    
    // Creates advisor portrait (circular crop)
    private fun createAdvisorPortrait(): Image {
        return try {
            if (GameAssets.isLoaded()) {
                val texture = GameAssets.getAdvisorPortrait(advisor.id)
                
                // Create circular mask
                val circularTexture = createCircularTexture(texture, 64)
                Image(circularTexture)
            } else {
                createPlaceholderPortrait()
            }
        } catch (e: Exception) {
            createPlaceholderPortrait()
        }
    }
    
    // Creates circular texture from square texture
    private fun createCircularTexture(sourceTexture: Texture, size: Int): Texture {
        // For simplicity, return source texture
        // In production, use FrameBuffer to render circular mask
        return sourceTexture
    }
    
    // Creates placeholder portrait
    private fun createPlaceholderPortrait(): Image {
        val pixmap = Pixmap(64, 64, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.DARK_GRAY)
        pixmap.fillCircle(32, 32, 32)
        val texture = Texture(pixmap)
        pixmap.dispose()
        return Image(texture)
    }
    
    // Creates gear icon
    private fun createGearIcon(): Image {
        return try {
            if (GameAssets.isLoaded()) {
                val texture = GameAssets.getTexture(AssetDescriptors.SETTINGS_ICON)
                Image(texture)
            } else {
                createPlaceholderGear()
            }
        } catch (e: Exception) {
            createPlaceholderGear()
        }
    }
    
    // Creates placeholder gear
    private fun createPlaceholderGear(): Image {
        val pixmap = Pixmap(24, 24, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.ORANGE)
        pixmap.fillCircle(12, 12, 10)
        val texture = Texture(pixmap)
        pixmap.dispose()
        return Image(texture)
    }
    
    // Updates affordability
    fun updateAffordability(currentGold: Double) {
        if (!advisor.isUnlocked && hireButton.isVisible) {
            val canAfford = currentGold >= advisor.cost
            hireButton.color = if (canAfford) Color.GOLD else Color.GRAY
        }
    }
    
    // Checks if advisor is hired
    fun isHired(): Boolean = advisor.isUnlocked
    
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
