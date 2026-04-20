// PATH: core/src/main/java/com/ismail/kingdom/GameEngine.kt
package com.ismail.kingdom

import com.badlogic.gdx.Preferences
import com.badlogic.gdx.math.Vector2
import com.ismail.kingdom.data.SaveManager
import com.ismail.kingdom.factories.AdvisorFactory
import com.ismail.kingdom.factories.EraFactory
import com.ismail.kingdom.factories.HeroFactory
import com.ismail.kingdom.models.Advisor
import com.ismail.kingdom.models.Building
import com.ismail.kingdom.models.BuyResult
import com.ismail.kingdom.models.GameState
import com.ismail.kingdom.models.Hero
import com.ismail.kingdom.models.KingdomEvent
import com.ismail.kingdom.models.KingdomEventType
import com.ismail.kingdom.models.MapTile
import com.ismail.kingdom.models.PrestigeResult
import com.ismail.kingdom.models.Quest
import com.ismail.kingdom.models.QuestType
import com.ismail.kingdom.models.Resource
import com.ismail.kingdom.models.TileReward
import com.ismail.kingdom.models.TileType
import com.ismail.kingdom.systems.AdvisorSystem
import com.ismail.kingdom.systems.BuildingSystem
import com.ismail.kingdom.systems.EventSystem
import com.ismail.kingdom.systems.IncomeSystem
import com.ismail.kingdom.systems.MapSystem
import com.ismail.kingdom.systems.OfflineEarningsCalculator
import com.ismail.kingdom.systems.OfflineEarningsResult
import com.ismail.kingdom.systems.PrestigeSystem
import com.ismail.kingdom.systems.QuestManager
import com.ismail.kingdom.systems.TapEvent
import com.ismail.kingdom.systems.TapSystem
import com.ismail.kingdom.models.TileRevealResult

// Central game engine coordinating all systems
class GameEngine(
    private val prefs: Preferences,
    adsInterface: com.ismail.kingdom.ads.AdsInterface? = null
) {
    var gameState: GameState = GameState()
        // private set

    val questManager = QuestManager()
    var incomeSystem = IncomeSystem(gameState)
    var tapSystem = TapSystem(incomeSystem)
    var buildingSystem = BuildingSystem(gameState)
    val advisorSystem = AdvisorSystem()
    val eventSystem = EventSystem()
    val mapSystem = MapSystem()
    var prestigeSystem = PrestigeSystem(gameState)
    val adManager = com.ismail.kingdom.ads.AdManager(adsInterface)
    private var offlineEarningsCalculator = OfflineEarningsCalculator(incomeSystem, eventSystem)

    private var autosaveAccumulator = 0f
    private val AUTOSAVE_INTERVAL = 30f

    init {
        tapSystem.setEventSystem(eventSystem)
        incomeSystem.setEventSystem(eventSystem)
    }

    // Rebinds systems to the current GameState
    internal fun rebindStatefulSystems() {
        incomeSystem = IncomeSystem(gameState)
        incomeSystem.setEventSystem(eventSystem)
        tapSystem = TapSystem(incomeSystem)
        tapSystem.setEventSystem(eventSystem)
        buildingSystem = BuildingSystem(gameState)
        prestigeSystem = PrestigeSystem(gameState)
        offlineEarningsCalculator = OfflineEarningsCalculator(incomeSystem, eventSystem)
    }

    fun initialize() {
        val loadedState = SaveManager.loadGame(prefs)

        if (loadedState != null) {
            gameState = loadedState
            rebindStatefulSystems()

            eventSystem.initialize(
                gameState.currentEvent,
                gameState.nextEventTimer
            )

            if (gameState.mapTiles.isNotEmpty()) {
                mapSystem.loadMap(gameState.mapTiles)
            } else {
                mapSystem.generateMapForEra(gameState.currentEra, gameState)
            }

            val offlineResult = calculateOfflineEarningsOnLoad()
            if (offlineResult.goldEarned > 0) {
                println("Offline earnings: ${offlineResult.formattedGold} (${offlineResult.formattedTime})")
                if (offlineResult.wasCapped) {
                    println("Offline earnings capped at ${offlineEarningsCalculator.getEffectiveOfflineCap()} hours")
                }
            }
        } else {
            initializeNewGame()
        }

        questManager.refreshQuests(gameState)
        gameState.activeQuests.clear()
        gameState.activeQuests.addAll(questManager.activeQuests)
        syncPersistentState()
    }

    private fun initializeNewGame() {
        gameState = GameState()
        rebindStatefulSystems()

        gameState.buildings.clear()
        gameState.buildings.addAll(EraFactory.buildAllBuildings())

        gameState.advisors.clear()
        gameState.advisors.addAll(AdvisorFactory.buildAllAdvisors())

        gameState.heroes.clear()
        gameState.heroes.addAll(HeroFactory.buildAllHeroes())

        gameState.resources["gold"] = Resource("gold", "Gold", gameState.currentGold, 1)

        val firstBuilding = gameState.buildings.firstOrNull { it.era == 1 }
        firstBuilding?.isUnlocked = true

        eventSystem.initialize(null, 0f)
        mapSystem.generateMapForEra(1, gameState)

        questManager.refreshQuests(gameState)
        gameState.activeQuests.clear()
        gameState.activeQuests.addAll(questManager.activeQuests)

        syncPersistentState()
        SaveManager.saveGame(gameState, prefs)
    }

    fun update(delta: Float) {
        adManager.update(delta)
        eventSystem.update(delta, gameState)
        tapSystem.update(delta)

        val previousGold = gameState.currentGold
        incomeSystem.update(delta)
        val goldEarned = (gameState.currentGold - previousGold).coerceAtLeast(0.0)

        if (goldEarned > 0.0) {
            questManager.updateQuest(QuestType.EARN_GOLD, goldEarned)
        }

        gameState.resources.getOrPut("gold") {
            Resource("gold", "Gold", gameState.currentGold, 1)
        }.amount = gameState.currentGold

        val currentIncome = incomeSystem.calculateTotalIncome()
        for (quest in gameState.activeQuests) {
            if (quest.type == QuestType.REACH_INCOME && !quest.isCompleted && quest.isActive) {
                quest.currentValue = currentIncome
                if (quest.currentValue >= quest.targetValue) {
                    quest.isCompleted = true
                }
            }
        }
        // Note: autosave is handled by SaveManager.updateAutoSave() in KingdomTycoonGame.render()
    }

    fun tap(x: Float, y: Float): TapEvent {
        val tapEvent = tapSystem.tap(Vector2(x, y), gameState)

        gameState.addGold(tapEvent.goldEarned)
        gameState.tapCount++

        questManager.updateQuest(QuestType.TAP_COUNT, 1.0)
        questManager.updateQuest(QuestType.EARN_GOLD, tapEvent.goldEarned)

        return tapEvent
    }

    fun buyBuilding(buildingId: String): BuyResult {
        val goldBefore = gameState.currentGold
        val success = buildingSystem.purchaseBuilding(buildingId)
        val goldSpent = (goldBefore - gameState.currentGold).coerceAtLeast(0.0)

        if (success) {
            questManager.updateQuest(QuestType.BUY_BUILDINGS, 1.0)
        }

        return BuyResult(
            success = success,
            message = if (success) "Building purchased" else "Not enough gold",
            goldSpent = goldSpent
        )
    }

    fun bulkBuyBuilding(buildingId: String, quantity: Int): BuyResult {
        var purchased = 0
        var totalSpent = 0.0

        repeat(quantity.coerceAtLeast(0)) {
            val before = gameState.currentGold
            if (buildingSystem.purchaseBuilding(buildingId)) {
                purchased++
                totalSpent += (before - gameState.currentGold).coerceAtLeast(0.0)
            }
        }

        if (purchased > 0) {
            questManager.updateQuest(QuestType.BUY_BUILDINGS, purchased.toDouble())
        }

        return BuyResult(
            success = purchased > 0,
            message = when {
                purchased == quantity -> "Purchased $purchased buildings"
                purchased > 0 -> "Purchased $purchased of $quantity buildings"
                else -> "Not enough gold"
            },
            goldSpent = totalSpent
        )
    }

    fun unlockAdvisor(advisorId: String): Boolean {
        return advisorSystem.unlockAdvisor(advisorId, gameState)
    }

    fun unlockHero(heroId: String, cost: Int): Boolean {
        val hero = gameState.heroes.find { it.id == heroId } ?: return false

        if (hero.isUnlocked) return false
        if (gameState.crownShards < cost) return false

        gameState.crownShards -= cost
        hero.isUnlocked = true
        return true
    }

    private fun calculateOfflineEarningsOnLoad(): OfflineEarningsResult {
        val now = System.currentTimeMillis()
        val result = offlineEarningsCalculator.calculate(gameState, now)

        if (result.goldEarned > 0) {
            gameState.addGold(result.goldEarned)
        }

        gameState.lastSaveTime = now
        return result
    }

    fun getOfflineEarningsResult(): OfflineEarningsResult? {
        val now = System.currentTimeMillis()
        return offlineEarningsCalculator.calculate(gameState, now).takeIf { it.goldEarned > 0.0 }
    }

    fun collectOfflineEarnings(): Double {
        val now = System.currentTimeMillis()
        val result = offlineEarningsCalculator.calculate(gameState, now)

        if (result.goldEarned > 0) {
            gameState.addGold(result.goldEarned)
        }

        gameState.lastSaveTime = now
        return result.goldEarned
    }

    fun performPrestige(): Boolean {
        val success = prestigeSystem.performPrestige()
        if (success) {
            reinitializeAfterPrestige()
        }
        return success
    }

    fun performAscension(): PrestigeResult {
        return performPrestigeLayer("Ascension")
    }

    fun performRift(): PrestigeResult {
        return performPrestigeLayer("Rift")
    }

    fun performLegend(selectedHeroId: String): PrestigeResult {
        return performPrestigeLayer("Legend")
    }

    private fun performPrestigeLayer(label: String): PrestigeResult {
        val previousShards = gameState.crownShards
        val success = prestigeSystem.performPrestige()
        val gained = (gameState.crownShards - previousShards).coerceAtLeast(0)

        if (success) {
            adManager.onEraTransition {
                reinitializeAfterPrestige()
            }
        }

        return PrestigeResult(
            success = success,
            crownShardsGained = gained,
            message = if (success) "$label successful" else "Not enough progress to prestige"
        )
    }

    // Completes quest by id and applies rewards
    fun completeQuest(questId: String): Boolean = questManager.completeQuest(questId, gameState)

    private fun reinitializeAfterPrestige() {
        if (gameState.buildings.isEmpty()) {
            gameState.buildings.addAll(EraFactory.buildAllBuildings())
        }
        if (gameState.advisors.isEmpty()) {
            gameState.advisors.addAll(AdvisorFactory.buildAllAdvisors())
        }
        if (gameState.heroes.isEmpty()) {
            gameState.heroes.addAll(HeroFactory.buildAllHeroes())
        }

        val firstBuilding = gameState.buildings.firstOrNull { it.era == gameState.currentEra }
        firstBuilding?.isUnlocked = true

        mapSystem.generateMapForEra(gameState.currentEra, gameState)

        questManager.refreshQuests(gameState)
        gameState.activeQuests.clear()
        gameState.activeQuests.addAll(questManager.activeQuests)

        syncPersistentState()
        SaveManager.saveGame(gameState, prefs)
    }

    fun getCompletedQuests(): List<Quest> {
        return gameState.activeQuests.filter { it.isCompleted && it.isActive }
    }

    fun claimQuestReward(quest: Quest) {
        val eventMultiplier = eventSystem.getEventQuestMultiplier()
        val goldReward = quest.goldReward * eventMultiplier
        val shardReward = (quest.crownShardReward * eventMultiplier).toInt()

        gameState.addGold(goldReward)
        gameState.crownShards += shardReward
        quest.isActive = false
    }

    fun refreshQuests() {
        questManager.refreshQuests(gameState)
        gameState.activeQuests.clear()
        gameState.activeQuests.addAll(questManager.activeQuests)
    }

    fun getGoldPerSecond(): Double {
        return incomeSystem.calculateTotalIncome()
    }

    fun getBuildingCost(buildingId: String): String {
        return formatGold(buildingSystem.currentCost(buildingId))
    }

    fun getBulkBuyCost(buildingId: String, quantity: Int): String {
        return formatGold(buildingSystem.bulkPurchaseCost(buildingId, quantity))
    }

    fun getAffordableQuantity(buildingId: String): Int {
        var quantity = 0
        while (quantity < 1000 && buildingSystem.bulkPurchaseCost(buildingId, quantity + 1) <= gameState.currentGold) {
            quantity++
        }
        return quantity
    }

    fun isBuildingAutomated(buildingId: String): Boolean {
        return advisorSystem.isAutomated(buildingId, gameState)
    }

    fun getAutomationStatus(): Map<String, Boolean> {
        return advisorSystem.getAutomationStatus(gameState)
    }

    fun getAdvisorForBuilding(buildingId: String): Advisor? {
        return advisorSystem.getAdvisorForBuilding(buildingId, gameState)
    }

    fun getActiveEvent(): KingdomEvent? {
        return eventSystem.getActiveEvent()
    }

    fun getEventTimeRemaining(): String {
        return eventSystem.getEventTimeRemaining()
    }

    fun getTimeUntilNextEvent(): String {
        return eventSystem.getTimeUntilNextEvent()
    }

    fun forceEventForTesting(eventType: KingdomEventType) {
        eventSystem.forceEventForTesting(eventType, gameState)
    }

    fun revealMapTile(tileId: String): TileRevealResult {
        return mapSystem.revealTile(tileId, gameState)
    }

    fun getRevealableTiles(): List<MapTile> {
        return mapSystem.getRevealableTiles()
    }

    fun getRevealedTiles(): List<MapTile> {
        return mapSystem.getRevealedTiles()
    }

    fun getMapTileAt(x: Int, y: Int): MapTile? {
        return mapSystem.getTileAt(x, y)
    }

    fun getExplorationProgress(): Float {
        return mapSystem.getExplorationProgress()
    }

    fun isMapFullyExplored(): Boolean {
        return mapSystem.isMapFullyExplored()
    }

    fun getCheapestRevealableTile(): MapTile? {
        return mapSystem.getCheapestRevealableTile()
    }

    fun generateNewMapForEra(eraId: Int) {
        mapSystem.generateMapForEra(eraId, gameState)
        syncPersistentState()
    }

    fun canAscend(): Boolean {
        return prestigeSystem.canPrestige()
    }

    fun canRift(): Boolean {
        return gameState.prestigeLayer >= 1 && prestigeSystem.canPrestige()
    }

    fun canLegend(): Boolean {
        return gameState.prestigeLayer >= 2 && prestigeSystem.canPrestige()
    }

    fun getPrestigeProgress(): Double {
        val required = prestigeSystem.getPrestigeRequirement()
        if (required <= 0.0) return 0.0
        return (gameState.totalLifetimeGold / required).coerceIn(0.0, 1.0)
    }

    fun getAvailableHeroesForLegend(): List<Hero> {
        return gameState.heroes.filter { it.isUnlocked }
    }

    fun getPermanentHeroPassives(): List<String> {
        return gameState.permanentHeroPassives.toList()
    }

    fun isShadowKingdomUnlocked(): Boolean {
        return gameState.shadowKingdomUnlocked
    }

    fun getShadowKingdomBuildings(): List<Building> {
        return gameState.shadowBuildings.toList()
    }

    fun save() {
        syncPersistentState()
        SaveManager.saveGame(gameState, prefs)
    }

    fun dispose() {
        save()
    }

    private fun syncPersistentState() {
        gameState.currentEvent = eventSystem.getActiveEvent()
        gameState.nextEventTimer = eventSystem.getNextEventTimer()

        gameState.mapTiles.clear()
        gameState.mapTiles.addAll(mapSystem.getAllTiles())

        gameState.activeQuests.clear()
        gameState.activeQuests.addAll(questManager.activeQuests)

        gameState.resources.getOrPut("gold") {
            Resource("gold", "Gold", gameState.currentGold, 1)
        }.amount = gameState.currentGold
    }

    private fun formatGold(value: Double): String {
        return com.ismail.kingdom.utils.Formatters.formatGold(value)
    }
}
