// PATH: core/src/main/java/com/ismail/kingdom/ui/QuestCard.kt
package com.ismail.kingdom.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.ismail.kingdom.models.Quest
import com.ismail.kingdom.utils.Formatters

// Custom actor for displaying a single quest card
class QuestCard(
    private val quest: Quest,
    private val skin: Skin,
    private val stage: Stage,
    private val onClaimClicked: (Quest) -> Unit
) : Table() {
    
    private val titleLabel: Label
    private val progressBar: ProgressBar
    private val progressLabel: Label
    private val timeLabel: Label
    private val rewardTable: Table
    private val claimButton: TextButton
    private val checkmarkLabel: Label
    
    private var isCompleted = false
    
    init {
        // Set background
        background = createColorDrawable(Color(0.22f, 0.18f, 0.14f, 0.95f))
        pad(15f)
        
        // Title
        titleLabel = Label(quest.title, skin, "body")
        titleLabel.setFontScale(1.2f)
        titleLabel.setAlignment(Align.left)
        titleLabel.setWrap(true)
        add(titleLabel).fillX().colspan(2).padBottom(10f).row()
        
        // Progress bar
        progressBar = ProgressBar(0f, quest.targetValue.toFloat(), 1f, false, skin)
        progressBar.value = quest.currentValue.toFloat()
        add(progressBar).fillX().colspan(2).height(20f).padBottom(5f).row()
        
        // Progress text
        val progressText = "${Formatters.formatGold(quest.currentValue)} / ${Formatters.formatGold(quest.targetValue)}"
        progressLabel = Label(progressText, skin, "gold-small")
        progressLabel.setFontScale(0.85f)
        progressLabel.color = Color.LIGHT_GRAY
        add(progressLabel).left().padBottom(8f)
        
        // Time remaining (if timed)
        timeLabel = Label("", skin, "body")
        timeLabel.setFontScale(0.8f)
        timeLabel.setAlignment(Align.right)
        if ((quest.timeLimit ?: 0) > 0) {
            updateTimeRemaining(quest.timeLimit ?: 0)
        } else {
            timeLabel.isVisible = false
        }
        add(timeLabel).right().padBottom(8f).row()
        
        // Reward preview
        rewardTable = createRewardPreview()
        add(rewardTable).left().expandX().padBottom(10f)
        
        // Claim button
        claimButton = TextButton("CLAIM", skin)
        claimButton.label.setFontScale(1.1f)
        claimButton.isDisabled = !quest.isComplete
        claimButton.color = if (quest.isComplete) Color.GOLD else Color.DARK_GRAY
        
        claimButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (quest.isComplete && !isCompleted) {
                    onClaim()
                }
            }
        })
        
        add(claimButton).size(120f, 50f).right().row()
        
        // Checkmark (initially hidden)
        checkmarkLabel = Label("✓", skin, "gold-large")
        checkmarkLabel.setFontScale(3.0f)
        checkmarkLabel.color = Color.GREEN
        checkmarkLabel.setAlignment(Align.center)
        checkmarkLabel.isVisible = false
    }
    
    // Creates reward preview table
    private fun createRewardPreview(): Table {
        val table = Table()
        
        // Gold reward
        val goldIcon = Label("💰", skin, "gold-small")
        val goldAmount = Label(Formatters.formatGold(quest.goldReward), skin, "gold-small")
        goldAmount.color = Color.GOLD
        table.add(goldIcon).padRight(5f)
        table.add(goldAmount).padRight(15f)
        
        // Crown shard reward (if applicable)
        if (quest.crownShardReward > 0) {
            val shardIcon = Label("👑", skin, "gold-small")
            val shardAmount = Label("+${quest.crownShardReward}", skin, "gold-small")
            shardAmount.color = Color.CYAN
            table.add(shardIcon).padRight(5f)
            table.add(shardAmount)
        }
        
        return table
    }
    
    // Updates time remaining display
    fun updateTimeRemaining(secondsRemaining: Int) {
        if ((quest.timeLimit ?: 0) <= 0) return
        
        val timeText = Formatters.formatTime(secondsRemaining)
        timeLabel.setText("⏱ $timeText")
        
        // Red when < 10% time left
        val percentRemaining = secondsRemaining.toFloat() / (quest.timeLimit ?: 1)
        timeLabel.color = if (percentRemaining < 0.1f) Color.RED else Color.YELLOW
        timeLabel.isVisible = true
    }
    
    // Updates progress
    fun updateProgress(currentValue: Double) {
        progressBar.value = currentValue.toFloat()
        
        val progressText = "${Formatters.formatGold(currentValue)} / ${Formatters.formatGold(quest.targetValue)}"
        progressLabel.setText(progressText)
        
        // Update claim button
        val isComplete = currentValue >= quest.targetValue
        claimButton.isDisabled = !isComplete
        claimButton.color = if (isComplete) Color.GOLD else Color.DARK_GRAY
        
        // Trigger completion animation if just completed
        if (isComplete && !isCompleted && !quest.isComplete) {
            playCompletionAnimation()
        }
    }
    
    // Handles claim button click
    private fun onClaim() {
        isCompleted = true
        
        // Button expand animation
        claimButton.clearActions()
        claimButton.addAction(
            Actions.sequence(
                Actions.scaleTo(1.2f, 1.2f, 0.2f, Interpolation.elasticOut),
                Actions.scaleTo(1.0f, 1.0f, 0.2f, Interpolation.elasticIn)
            )
        )
        
        // Spawn gold particles
        spawnRewardParticles()
        
        // Callback
        onClaimClicked(quest)
        
        // Disable button
        claimButton.isDisabled = true
        claimButton.setText("CLAIMED")
        claimButton.color = Color.GRAY
    }
    
    // Plays completion animation
    fun playCompletionAnimation() {
        // Card glows gold
        clearActions()
        addAction(
            Actions.sequence(
                Actions.repeat(3,
                    Actions.sequence(
                        Actions.color(Color.GOLD, 0.3f),
                        Actions.color(Color(0.22f, 0.18f, 0.14f, 0.95f), 0.3f)
                    )
                )
            )
        )
        
        // Show checkmark
        if (!checkmarkLabel.hasParent()) {
            addActor(checkmarkLabel)
            checkmarkLabel.setPosition(
                (width - 100f) / 2f,
                (height - 100f) / 2f
            )
            checkmarkLabel.setSize(100f, 100f)
        }
        
        checkmarkLabel.isVisible = true
        checkmarkLabel.clearActions()
        checkmarkLabel.addAction(
            Actions.sequence(
                Actions.alpha(0f),
                Actions.parallel(
                    Actions.fadeIn(0.3f),
                    Actions.scaleTo(1.5f, 1.5f, 0.3f, Interpolation.elasticOut)
                ),
                Actions.delay(1.0f),
                Actions.fadeOut(0.3f),
                Actions.run { checkmarkLabel.isVisible = false }
            )
        )
        
        // Spawn reward numbers
        spawnRewardNumbers()
    }
    
    // Spawns reward numbers that float up
    private fun spawnRewardNumbers() {
        val stageCoords = localToStageCoordinates(Vector2(width / 2f, height / 2f))
        
        // Gold reward
        val goldLabel = Label("+${Formatters.formatGold(quest.goldReward)}", skin, "gold-large")
        goldLabel.color = Color.GOLD
        goldLabel.setPosition(stageCoords.x - 50f, stageCoords.y)
        
        goldLabel.addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.moveBy(0f, 150f, 1.5f, Interpolation.pow2Out),
                    Actions.fadeOut(1.5f)
                ),
                Actions.removeActor()
            )
        )
        
        stage.addActor(goldLabel)
        
        // Crown shard reward
        if (quest.crownShardReward > 0) {
            val shardLabel = Label("+${quest.crownShardReward} 👑", skin, "gold-large")
            shardLabel.color = Color.CYAN
            shardLabel.setPosition(stageCoords.x - 50f, stageCoords.y + 40f)
            
            shardLabel.addAction(
                Actions.sequence(
                    Actions.parallel(
                        Actions.moveBy(0f, 150f, 1.5f, Interpolation.pow2Out),
                        Actions.fadeOut(1.5f)
                    ),
                    Actions.removeActor()
                )
            )
            
            stage.addActor(shardLabel)
        }
    }
    
    // Spawns gold particles burst
    private fun spawnRewardParticles() {
        val stageCoords = localToStageCoordinates(Vector2(width / 2f, height / 2f))
        
        // Spawn 12 coin particles in burst pattern
        for (i in 0..11) {
            CoinParticlePool.spawn(stageCoords)
        }
    }
    
    // Slides out card to left
    fun slideOut(onComplete: () -> Unit) {
        clearActions()
        addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.moveBy(-width - 50f, 0f, 0.4f, Interpolation.pow2In),
                    Actions.fadeOut(0.4f)
                ),
                Actions.run { onComplete() }
            )
        )
    }
    
    // Slides in card from right
    fun slideIn() {
        x = width + 50f
        color = Color(1f, 1f, 1f, 0f)
        
        clearActions()
        addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.moveTo(0f, y, 0.5f, Interpolation.pow2Out),
                    Actions.fadeIn(0.5f)
                )
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

// Completed quest card (dimmed version)
class CompletedQuestCard(
    private val quest: Quest,
    private val skin: Skin
) : Table() {
    
    init {
        // Set dimmed background
        background = createColorDrawable(Color(0.15f, 0.12f, 0.09f, 0.7f))
        pad(10f)
        
        // Title (smaller, dimmed)
        val titleLabel = Label(quest.title, skin, "body")
        titleLabel.setFontScale(0.9f)
        titleLabel.color = Color.GRAY
        titleLabel.setAlignment(Align.left)
        add(titleLabel).left().expandX()
        
        // Checkmark
        val checkmark = Label("✓", skin, "body")
        checkmark.color = Color.DARK_GRAY
        add(checkmark).right().padLeft(10f)
        
        // Reward
        val rewardLabel = Label("+${Formatters.formatGold(quest.goldReward)}", skin, "gold-small")
        rewardLabel.setFontScale(0.8f)
        rewardLabel.color = Color.DARK_GRAY
        add(rewardLabel).right().padLeft(5f)
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
