// PATH: core/src/main/java/com/ismail/kingdom/ui/BuildingRow.kt
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
import com.ismail.kingdom.models.Building
import com.ismail.kingdom.utils.Formatters

// Custom actor for displaying a single building row
class BuildingRow(
    private val building: Building,
    private val skin: Skin,
    private val onBuyClicked: (Building, Int) -> Unit
) : Table() {

    private val nameLabel: Label
    private val incomeLabel: Label
    private val buyButton: TextButton
    private val lockOverlay: Image
    private val unlockRequirementLabel: Label
    private val progressBar: ProgressBar
    private val progressBarCell: Cell<ProgressBar>

    private var isAffordable = false
    private var currentQuantity = 1

    init {
        background = createColorDrawable(Color(0.25f, 0.2f, 0.15f, 0.9f))
        pad(10f)

        val buildingSprite = createBuildingSprite()
        add(buildingSprite).size(64f).padRight(15f)

        val infoTable = Table()

        val displayName = if (building.count > 0) {
            "${building.name} x${building.count}"
        } else {
            building.name
        }
        nameLabel = Label(displayName, skin, "body")
        nameLabel.setAlignment(Align.left)
        infoTable.add(nameLabel).left().row()

        val income = building.totalIncome()
        val incomeText = if (income > 0) {
            Formatters.formatIPS(income)
        } else {
            "Not producing"
        }
        incomeLabel = Label(incomeText, skin, "gold-small")
        incomeLabel.color = if (income > 0) Color.GREEN else Color.GRAY
        infoTable.add(incomeLabel).left()

        add(infoTable).expandX().left().padRight(10f)

        val cost = building.currentCost()
        buyButton = TextButton(Formatters.formatGold(cost), skin)
        buyButton.label.setFontScale(0.9f)

        buyButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (!building.isUnlocked) return
                onBuyClicked(building, currentQuantity)
            }

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                if (!building.isUnlocked) return false
                return super.touchDown(event, x, y, pointer, button)
            }
        })

        add(buyButton).size(140f, 70f).right()

        row()

        progressBar = ProgressBar(0f, 10f, 0.1f, false, skin)
        progressBar.value = (building.count % 10).toFloat()
        progressBarCell = add(progressBar).colspan(3).fillX().height(6f).padTop(5f)

        if (!building.isUnlocked) {
            row()
            lockOverlay = Image()
            lockOverlay.color = Color(0f, 0f, 0f, 0.7f)
            add(lockOverlay).size(32f).padTop(5f)

            val unlockText = getUnlockRequirementText()
            unlockRequirementLabel = Label(unlockText, skin, "body")
            unlockRequirementLabel.setFontScale(0.8f)
            unlockRequirementLabel.color = Color.YELLOW
            unlockRequirementLabel.setAlignment(Align.center)
            add(unlockRequirementLabel).colspan(2).expandX().center().padTop(5f)
        } else {
            lockOverlay = Image()
            unlockRequirementLabel = Label("", skin, "body")
            lockOverlay.isVisible = false
            unlockRequirementLabel.isVisible = false
        }

        updateAffordability(0.0)
    }

    private fun createBuildingSprite(): Image {
        return try {
            if (GameAssets.isLoaded()) {
                val texture = GameAssets.getBuildingTexture(building.id)
                Image(texture)
            } else {
                createPlaceholderSprite()
            }
        } catch (e: Exception) {
            createPlaceholderSprite()
        }
    }

    private fun createPlaceholderSprite(): Image {
        val pixmap = Pixmap(64, 64, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.BROWN)
        pixmap.fill()
        val texture = Texture(pixmap)
        pixmap.dispose()
        return Image(texture)
    }

    private fun getUnlockRequirementText(): String {
        return "Unlock by purchasing previous building"
    }

    fun updateAffordability(currentGold: Double) {
        val cost = building.currentCost()
        isAffordable = currentGold >= cost && building.isUnlocked

        buyButton.color = if (isAffordable) {
            Color.GOLD
        } else if (!building.isUnlocked) {
            Color.DARK_GRAY
        } else {
            Color.GRAY
        }

        buyButton.setText(Formatters.formatGold(cost))

        val displayName = if (building.count > 0) {
            "${building.name} x${building.count}"
        } else {
            building.name
        }
        nameLabel.setText(displayName)

        val income = building.totalIncome()
        val incomeText = if (income > 0) {
            Formatters.formatIPS(income)
        } else {
            "Not producing"
        }
        incomeLabel.setText(incomeText)
        incomeLabel.color = if (income > 0) Color.GREEN else Color.GRAY

        progressBar.value = (building.count % 10).toFloat()

        lockOverlay.isVisible = !building.isUnlocked
        unlockRequirementLabel.isVisible = !building.isUnlocked
    }

    fun flashMilestone() {
        clearActions()
        addAction(
            Actions.sequence(
                Actions.repeat(3,
                    Actions.sequence(
                        Actions.color(Color.GOLD, 0.2f),
                        Actions.color(Color(0.25f, 0.2f, 0.15f, 0.9f), 0.2f)
                    )
                )
            )
        )
    }

    fun setQuantity(quantity: Int) {
        currentQuantity = quantity
    }

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

// Quantity selector popup for bulk purchases
class QuantitySelectorPopup(
    private val skin: Skin,
    private val maxAffordable: Int,
    private val onQuantitySelected: (Int) -> Unit
) : Table() {

    init {
        background = createColorDrawable(Color(0.1f, 0.08f, 0.05f, 0.95f))
        pad(10f)

        val titleLabel = Label("Select Quantity", skin, "body")
        titleLabel.setAlignment(Align.center)
        add(titleLabel).colspan(4).center().padBottom(10f).row()

        val x1Button = TextButton("x1", skin)
        val x10Button = TextButton("x10", skin)
        val x100Button = TextButton("x100", skin)
        val maxButton = TextButton("MAX\n($maxAffordable)", skin)

        // FIX: capture references to outer members before anonymous object scope
        val callback = onQuantitySelected

        x1Button.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                callback(1)
                dismiss()
            }
        })

        x10Button.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                callback(10)
                dismiss()
            }
        })

        x100Button.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                callback(100)
                dismiss()
            }
        })

        maxButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                callback(maxAffordable)
                dismiss()
            }
        })

        x10Button.isDisabled = maxAffordable < 10
        x100Button.isDisabled = maxAffordable < 100

        add(x1Button).size(80f, 60f).pad(5f)
        add(x10Button).size(80f, 60f).pad(5f)
        add(x100Button).size(80f, 60f).pad(5f)
        add(maxButton).size(80f, 60f).pad(5f)

        color = Color(1f, 1f, 1f, 0f)
        addAction(Actions.fadeIn(0.2f, Interpolation.fade))
    }

    private fun dismiss() {
        addAction(
            Actions.sequence(
                Actions.fadeOut(0.15f),
                Actions.removeActor()
            )
        )
    }

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