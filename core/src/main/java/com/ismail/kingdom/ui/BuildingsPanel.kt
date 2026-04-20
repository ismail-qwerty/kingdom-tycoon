// PATH: core/src/main/java/com/ismail/kingdom/ui/BuildingsPanel.kt
package com.ismail.kingdom.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.ismail.kingdom.GameEngine
import com.ismail.kingdom.models.Building

// Optimized panel displaying buildings with virtual scrolling
class BuildingsPanel(
    private val stage: Stage,
    private val skin: Skin,
    private val gameEngine: GameEngine
) : Actor() {
    
    private var virtualList: VirtualScrollList? = null
    private var currentEra = 1
    private var eraBuildings = listOf<Building>()
    
    private var updateTimer = 0f
    private val UPDATE_INTERVAL = 0.5f
    
    private var lastMilestoneCheck = mutableMapOf<String, Int>()
    
    // Cached strings to avoid allocation every frame
    private val cachedCosts = mutableMapOf<String, String>()
    private val cachedIncomes = mutableMapOf<String, String>()
    
    init {
        rebuildForEra(currentEra)
        
        addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                dismissPopups()
            }
        })
    }
    
    // Rebuilds panel for specified era
    fun rebuildForEra(eraId: Int) {
        currentEra = eraId
        
        // Get buildings for era
        eraBuildings = gameEngine.gameState.buildings
            .filter { it.era == eraId }
            .sortedBy { it.id }
        
        // Clear caches
        cachedCosts.clear()
        cachedIncomes.clear()
        lastMilestoneCheck.clear()
        
        // Initialize milestone tracking
        for (building in eraBuildings) {
            lastMilestoneCheck[building.id] = building.count
        }
        
        // Create virtual scroll list
        virtualList?.remove()
        virtualList = VirtualScrollList(
            itemCount = eraBuildings.size,
            itemHeight = 110f,
            renderItem = { index, actor -> renderBuildingRow(index, actor) },
            skin = skin
        )
        
        virtualList?.setSize(width, height)
        stage.addActor(virtualList)
    }
    
    // Renders a single building row (called only for visible items)
    private fun renderBuildingRow(index: Int, actor: Actor) {
        if (index >= eraBuildings.size) return
        
        val building = eraBuildings[index]
        val currentGold = gameEngine.gameState.currentGold
        
        // Reuse or create BuildingRow
        val row = if (actor is BuildingRow) {
            actor
        } else {
            BuildingRow(building, skin) { bldg, quantity ->
                onBuildingPurchase(bldg, quantity)
            }
        }
        
        // Update affordability (uses cached strings)
        row.updateAffordability(currentGold)
    }
    
    // Updates panel (called every frame)
    fun update(delta: Float) {
        updateTimer += delta
        
        if (updateTimer >= UPDATE_INTERVAL) {
            updateTimer = 0f
            
            // Refresh visible items only
            virtualList?.refresh()
            
            // Check milestones
            checkMilestones()
        }
    }
    
    // Checks for milestone achievements
    private fun checkMilestones() {
        for ((buildingId, lastCount) in lastMilestoneCheck) {
            val building = gameEngine.gameState.buildings.find { it.id == buildingId } ?: continue
            val currentCount = building.count
            
            if (currentCount >= 10 && currentCount % 10 == 0 && currentCount > lastCount) {
                onMilestoneReached(building)
                lastMilestoneCheck[buildingId] = currentCount
            } else if (currentCount != lastCount) {
                lastMilestoneCheck[buildingId] = currentCount
            }
        }
    }
    
    // Handles milestone reached
    private fun onMilestoneReached(building: Building) {
        Gdx.app.log("BuildingsPanel", "Milestone: ${building.name} x${building.count}")
        // TODO: Play sound, show animation
    }
    
    // Handles building purchase
    private fun onBuildingPurchase(building: Building, quantity: Int) {
        if (quantity == 1) {
            val result = gameEngine.buyBuilding(building.id)
            if (result.success) {
                invalidateCaches(building.id)
                virtualList?.refresh()
            }
        } else {
            showQuantitySelector(building)
        }
    }
    
    // Shows quantity selector popup
    private fun showQuantitySelector(building: Building) {
        val maxAffordable = gameEngine.getAffordableQuantity(building.id)
        
        if (maxAffordable <= 0) return
        
        val popup = QuantitySelectorPopup(skin, maxAffordable) { quantity ->
            executeBulkPurchase(building, quantity)
        }
        
        popup.setPosition(stage.width / 2f - 150f, stage.height / 2f)
        stage.addActor(popup)
    }
    
    // Executes bulk purchase
    private fun executeBulkPurchase(building: Building, quantity: Int) {
        val result = gameEngine.bulkBuyBuilding(building.id, quantity)
        if (result.success) {
            invalidateCaches(building.id)
            virtualList?.refresh()
        }
    }
    
    // Invalidates cached strings for a building
    private fun invalidateCaches(buildingId: String) {
        cachedCosts.remove(buildingId)
        cachedIncomes.remove(buildingId)
    }
    
    // Dismisses all open popups
    private fun dismissPopups() {
        val actors = stage.actors
        val popupsToRemove = mutableListOf<QuantitySelectorPopup>()
        
        for (actor in actors) {
            if (actor is QuantitySelectorPopup) {
                popupsToRemove.add(actor)
            }
        }
        
        for (popup in popupsToRemove) {
            popup.remove()
        }
    }
    
    // Refreshes panel
    fun refresh() {
        virtualList?.refresh()
    }
    
    // Scrolls to top
    fun scrollToTop() {
        virtualList?.scrollY = 0f
    }
    
    override fun setSize(width: Float, height: Float) {
        super.setSize(width, height)
        virtualList?.setSize(width, height)
    }
}
