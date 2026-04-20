// PATH: core/src/main/java/com/ismail/kingdom/ui/PrestigeUI.kt
package com.ismail.kingdom.ui

import com.ismail.kingdom.models.GameState
import com.ismail.kingdom.systems.PrestigeSystem

// Progress data for prestige UI
data class PrestigeProgress(
    val currentGold: Double,
    val requiredGold: Double,
    val percentComplete: Float,
    val crownShardsPreview: Int,
    val canPrestige: Boolean,
    val nextLayer: Int,
    val nextLayerName: String,
    val currentLayerName: String,
    val additionalRequirements: String
)

// Helper class for prestige UI display
class PrestigeUI(private val prestigeSystem: PrestigeSystem) {
    
    // Gets comprehensive prestige progress for UI
    fun getPrestigeProgress(state: GameState): PrestigeProgress {
        val nextLayer = state.prestigeLayer + 1
        
        if (nextLayer > 3) {
            // Max prestige reached
            return PrestigeProgress(
                currentGold = state.totalLifetimeGold,
                requiredGold = 0.0,
                percentComplete = 1.0f,
                crownShardsPreview = 0,
                canPrestige = false,
                nextLayer = 3,
                nextLayerName = "Max",
                currentLayerName = prestigeSystem.getPrestigeLayerName(state.prestigeLayer),
                additionalRequirements = "Maximum prestige achieved!"
            )
        }
        
        val requiredGold = prestigeSystem.getPrestigeRequirement(nextLayer)
        val percentComplete = (state.totalLifetimeGold / requiredGold).toFloat().coerceIn(0f, 1f)
        val crownShardsPreview = prestigeSystem.calculateCrownShardsPreview(state, nextLayer)
        
        val canPrestige = when (nextLayer) {
            1 -> prestigeSystem.canAscend(state)
            2 -> prestigeSystem.canRift(state)
            3 -> prestigeSystem.canLegend(state)
            else -> false
        }
        
        val additionalReqs = getAdditionalRequirements(state, nextLayer)
        
        return PrestigeProgress(
            currentGold = state.totalLifetimeGold,
            requiredGold = requiredGold,
            percentComplete = percentComplete,
            crownShardsPreview = crownShardsPreview,
            canPrestige = canPrestige,
            nextLayer = nextLayer,
            nextLayerName = prestigeSystem.getPrestigeLayerName(nextLayer),
            currentLayerName = prestigeSystem.getPrestigeLayerName(state.prestigeLayer),
            additionalRequirements = additionalReqs
        )
    }
    
    // Gets additional requirements text for prestige layer
    private fun getAdditionalRequirements(state: GameState, layer: Int): String {
        return when (layer) {
            1 -> "" // No additional requirements
            2 -> {
                if (state.prestigeLayer < 1) {
                    "Requires: Ascension completed"
                } else if (state.currentEra < 3) {
                    "Requires: Era 3+"
                } else {
                    ""
                }
            }
            3 -> {
                if (state.prestigeLayer < 2) {
                    "Requires: Rift completed"
                } else if (state.currentEra < 5) {
                    "Requires: Era 5"
                } else {
                    ""
                }
            }
            else -> ""
        }
    }
    
    // Gets prestige requirement text for display
    fun getPrestigeRequirementText(layer: Int): String {
        val requirement = prestigeSystem.getPrestigeRequirement(layer)
        val formatted = formatLargeNumber(requirement)
        
        return when (layer) {
            1 -> "Ascension: Earn $formatted total lifetime gold"
            2 -> "Rift: Earn $formatted total lifetime gold (Era 3+, after Ascension)"
            3 -> "Legend: Earn $formatted total lifetime gold (Era 5, after Rift)"
            else -> "Unknown prestige layer"
        }
    }
    
    // Gets detailed prestige description
    fun getPrestigeDescription(layer: Int): String {
        return when (layer) {
            1 -> """
                ASCENSION
                
                Reset your kingdom and advance to the next era!
                
                Rewards:
                • Crown Shards (permanent income boost)
                • Advance to next era
                • Unlock 10 new buildings
                • Keep all heroes
                
                Reset:
                • Buildings, resources, quests, map
                • Gold and tap count
            """.trimIndent()
            
            2 -> """
                RIFT
                
                Open a portal to the Shadow Kingdom!
                
                Rewards:
                • 2× Crown Shards
                • Shadow Kingdom unlocked
                • Parallel income stream (50% of main)
                • Advance to next era
                
                Requirements:
                • Completed Ascension
                • Reached Era 3+
                
                Reset:
                • Same as Ascension
            """.trimIndent()
            
            3 -> """
                LEGEND
                
                Transcend mortality and claim a hero's power forever!
                
                Rewards:
                • 3× Crown Shards
                • Choose 1 hero's passive (permanent)
                • Multiple Legend prestiges = multiple heroes
                • All permanent heroes stack
                
                Requirements:
                • Completed Rift
                • Reached Era 5
                
                Reset:
                • Return to Era 1
                • Same resets as Ascension
            """.trimIndent()
            
            else -> "Unknown prestige layer"
        }
    }
    
    // Gets crown shard bonus text
    fun getCrownShardBonusText(crownShards: Int): String {
        val bonusPercent = (crownShards * 2.0).toInt()
        return "+$bonusPercent% permanent income (${crownShards} Crown Shards)"
    }
    
    // Gets formatted gold remaining text
    fun getGoldRemainingText(current: Double, required: Double): String {
        val remaining = (required - current).coerceAtLeast(0.0)
        return if (remaining > 0) {
            "Need ${formatLargeNumber(remaining)} more gold"
        } else {
            "Ready to prestige!"
        }
    }
    
    // Gets prestige button text
    fun getPrestigeButtonText(layer: Int, canPrestige: Boolean): String {
        val layerName = prestigeSystem.getPrestigeLayerName(layer)
        return if (canPrestige) {
            "Perform $layerName"
        } else {
            "$layerName Locked"
        }
    }
    
    // Gets prestige confirmation text
    fun getPrestigeConfirmationText(layer: Int, crownShards: Int): String {
        val layerName = prestigeSystem.getPrestigeLayerName(layer)
        return """
            Perform $layerName?
            
            You will earn $crownShards Crown Shards.
            
            Your kingdom will be reset, but you'll keep:
            • Crown Shards
            • Heroes
            • Permanent income multiplier
            
            This cannot be undone!
        """.trimIndent()
    }
    
    // Gets Shadow Kingdom status text
    fun getShadowKingdomStatusText(prestigeSystem: PrestigeSystem): String {
        return if (prestigeSystem.isShadowKingdomUnlocked()) {
            val buildingCount = prestigeSystem.getShadowKingdomBuildings().size
            "Shadow Kingdom Active ($buildingCount buildings)"
        } else {
            "Shadow Kingdom Locked (Unlock at Rift)"
        }
    }
    
    // Gets permanent hero passives text
    fun getPermanentHeroPassivesText(prestigeSystem: PrestigeSystem, state: GameState): String {
        val permanentIds = prestigeSystem.getPermanentHeroPassives()
        
        if (permanentIds.isEmpty()) {
            return "No permanent hero passives yet"
        }
        
        val heroNames = permanentIds.mapNotNull { id ->
            state.heroes.find { it.id == id }?.name
        }
        
        return "Permanent Heroes: ${heroNames.joinToString(", ")}"
    }
    
    // Gets prestige statistics text
    fun getPrestigeStatisticsText(state: GameState): String {
        return """
            Current Era: ${state.currentEra}
            Prestige Layer: ${prestigeSystem.getPrestigeLayerName(state.prestigeLayer)}
            Crown Shards: ${state.crownShards}
            Income Multiplier: ${formatMultiplier(state.incomeMultiplier)}
            Total Lifetime Gold: ${formatLargeNumber(state.totalLifetimeGold)}
        """.trimIndent()
    }
    
    // Formats large numbers with suffixes
    fun formatLargeNumber(value: Double): String {
        return when {
            value < 1000 -> "%.0f".format(value)
            value < 1_000_000 -> "%.1fK".format(value / 1000)
            value < 1_000_000_000 -> "%.1fM".format(value / 1_000_000)
            value < 1_000_000_000_000 -> "%.1fB".format(value / 1_000_000_000)
            value < 1_000_000_000_000_000 -> "%.1fT".format(value / 1_000_000_000_000)
            else -> "%.1fQa".format(value / 1_000_000_000_000_000)
        }
    }
    
    // Formats multiplier for display
    private fun formatMultiplier(multiplier: Double): String {
        return "×%.2f".format(multiplier)
    }
    
    // Gets progress bar color based on completion
    fun getProgressBarColor(percentComplete: Float): String {
        return when {
            percentComplete < 0.33f -> "#FF4444" // Red
            percentComplete < 0.66f -> "#FFAA00" // Orange
            percentComplete < 1.0f -> "#FFFF00" // Yellow
            else -> "#44FF44" // Green
        }
    }
    
    // Gets prestige tier color
    fun getPrestigeTierColor(layer: Int): String {
        return when (layer) {
            0 -> "#FFFFFF" // White
            1 -> "#4444FF" // Blue (Ascension)
            2 -> "#AA00FF" // Purple (Rift)
            3 -> "#FFD700" // Gold (Legend)
            else -> "#FFFFFF"
        }
    }
    
    // Checks if player should be prompted to prestige
    fun shouldPromptPrestige(state: GameState): Boolean {
        val progress = getPrestigeProgress(state)
        return progress.canPrestige && progress.percentComplete >= 1.0f
    }
    
    // Gets prestige milestone text (for notifications)
    fun getPrestigeMilestoneText(percentComplete: Float): String? {
        return when {
            percentComplete >= 0.25f && percentComplete < 0.26f -> "25% to next prestige!"
            percentComplete >= 0.50f && percentComplete < 0.51f -> "50% to next prestige!"
            percentComplete >= 0.75f && percentComplete < 0.76f -> "75% to next prestige!"
            percentComplete >= 0.90f && percentComplete < 0.91f -> "90% to next prestige!"
            percentComplete >= 1.0f -> "Ready to prestige!"
            else -> null
        }
    }
}
