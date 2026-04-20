// PATH: core/src/main/java/com/ismail/kingdom/systems/QuestManager.kt
package com.ismail.kingdom.systems

import com.ismail.kingdom.models.GameState
import com.ismail.kingdom.models.Quest
import com.ismail.kingdom.models.QuestType

// Manages active quests and quest progression
class QuestManager {

    private val _activeQuests = mutableListOf<Quest>()
    val activeQuests: List<Quest> get() = _activeQuests

    private var questPool = mutableListOf<Quest>()

    // Refreshes the active quest list with 3 new quests from the pool
    fun refreshQuests(gameState: GameState) {
        // Generate quest pool based on current era
        questPool = generateQuestPool(gameState.currentEra).toMutableList()

        // Clear current active quests
        _activeQuests.clear()

        // Pick 3 random quests from the pool
        val shuffled = questPool.shuffled()
        _activeQuests.addAll(shuffled.take(3))
    }

    // Updates quest progress for a specific quest type
    fun updateQuest(type: QuestType, value: Double) {
        for (quest in _activeQuests) {
            if (quest.type == type && !quest.isCompleted && quest.isActive) {
                quest.currentValue += value

                // Check if quest is completed
                if (quest.currentValue >= quest.targetValue) {
                    quest.isCompleted = true
                }
            }
        }
    }

    // Checks for newly completed quests and returns them
    fun checkCompletions(): List<Quest> {
        return _activeQuests.filter { it.isCompleted && it.isActive }
    }

    // Claims rewards from a completed quest
    fun claimReward(quest: Quest, gameState: GameState) {
        if (quest.isCompleted && quest.isActive) {
            gameState.addGold(quest.goldReward)
            gameState.crownShards += quest.crownShardReward
            quest.isActive = false
        }
    }

    // Finds quest by id, marks complete, applies rewards; returns true if found
    fun completeQuest(questId: String, gameState: GameState): Boolean {
        val quest = _activeQuests.find { it.id == questId } ?: return false
        if (quest.isComplete || !quest.isActive) return false
        quest.isComplete = true
        gameState.addGold(quest.goldReward)
        gameState.crownShards += quest.crownShardReward
        quest.isActive = false
        return true
    }

    // Generates a pool of 15 quests for the given era
    fun generateQuestPool(era: Int): List<Quest> {
        val baseMultiplier = when (era) {
            1 -> 1.0
            2 -> 100.0
            3 -> 10_000.0
            4 -> 1_000_000.0
            5 -> 100_000_000.0
            else -> 1.0
        }

        return when (era) {
            1 -> generateEra1Quests(baseMultiplier)
            2 -> generateEra2Quests(baseMultiplier)
            3 -> generateEra3Quests(baseMultiplier)
            4 -> generateEra4Quests(baseMultiplier)
            5 -> generateEra5Quests(baseMultiplier)
            else -> generateEra1Quests(1.0)
        }
    }

    // Era 1 Quest Pool
    private fun generateEra1Quests(mult: Double): List<Quest> {
        return listOf(
            Quest("q1_earn_100", "Humble Beginnings", "Earn 100 gold", QuestType.EARN_GOLD, 100.0 * mult, 0.0, 50.0 * mult, 1),
            Quest("q1_earn_1k", "Growing Wealth", "Earn 1,000 gold", QuestType.EARN_GOLD, 1_000.0 * mult, 0.0, 500.0 * mult, 2),
            Quest("q1_earn_10k", "Small Fortune", "Earn 10,000 gold", QuestType.EARN_GOLD, 10_000.0 * mult, 0.0, 5_000.0 * mult, 3),
            Quest("q1_buy_5", "Expand the Village", "Purchase 5 buildings", QuestType.BUY_BUILDINGS, 5.0, 0.0, 200.0 * mult, 1),
            Quest("q1_buy_10", "Growing Settlement", "Purchase 10 buildings", QuestType.BUY_BUILDINGS, 10.0, 0.0, 1_000.0 * mult, 2),
            Quest("q1_buy_25", "Thriving Village", "Purchase 25 buildings", QuestType.BUY_BUILDINGS, 25.0, 0.0, 5_000.0 * mult, 3),
            Quest("q1_income_10", "Steady Income", "Reach 10 gold/s income", QuestType.REACH_INCOME, 10.0 * mult, 0.0, 100.0 * mult, 1),
            Quest("q1_income_100", "Profitable Village", "Reach 100 gold/s income", QuestType.REACH_INCOME, 100.0 * mult, 0.0, 1_000.0 * mult, 2),
            Quest("q1_income_1k", "Economic Boom", "Reach 1,000 gold/s income", QuestType.REACH_INCOME, 1_000.0 * mult, 0.0, 10_000.0 * mult, 3),
            Quest("q1_tap_50", "Eager Worker", "Tap 50 times", QuestType.TAP_COUNT, 50.0, 0.0, 100.0 * mult, 1),
            Quest("q1_tap_200", "Dedicated Worker", "Tap 200 times", QuestType.TAP_COUNT, 200.0, 0.0, 500.0 * mult, 2),
            Quest("q1_tap_500", "Tireless Worker", "Tap 500 times", QuestType.TAP_COUNT, 500.0, 0.0, 2_000.0 * mult, 3),
            Quest("q1_earn_fast", "Speed Run", "Earn 500 gold in 60 seconds", QuestType.EARN_GOLD, 500.0 * mult, 0.0, 1_000.0 * mult, 2, timeLimit = 60),
            Quest("q1_tap_fast", "Rapid Tapper", "Tap 100 times in 30 seconds", QuestType.TAP_COUNT, 100.0, 0.0, 500.0 * mult, 2, timeLimit = 30),
            Quest("q1_milestone", "First Milestone", "Reach 1 million total gold earned", QuestType.EARN_GOLD, 1_000_000.0 * mult, 0.0, 50_000.0 * mult, 5)
        )
    }

    // Era 2 Quest Pool
    private fun generateEra2Quests(mult: Double): List<Quest> {
        return listOf(
            Quest("q2_earn_10k", "Stone Wealth", "Earn 10,000 gold", QuestType.EARN_GOLD, 10_000.0 * mult, 0.0, 5_000.0 * mult, 2),
            Quest("q2_earn_100k", "Town Treasury", "Earn 100,000 gold", QuestType.EARN_GOLD, 100_000.0 * mult, 0.0, 50_000.0 * mult, 3),
            Quest("q2_earn_1m", "Merchant Prince", "Earn 1,000,000 gold", QuestType.EARN_GOLD, 1_000_000.0 * mult, 0.0, 500_000.0 * mult, 5),
            Quest("q2_buy_5", "Stone Foundations", "Purchase 5 stone buildings", QuestType.BUY_BUILDINGS, 5.0, 0.0, 20_000.0 * mult, 2),
            Quest("q2_buy_15", "Expanding Town", "Purchase 15 stone buildings", QuestType.BUY_BUILDINGS, 15.0, 0.0, 100_000.0 * mult, 3),
            Quest("q2_buy_30", "Stone Metropolis", "Purchase 30 stone buildings", QuestType.BUY_BUILDINGS, 30.0, 0.0, 500_000.0 * mult, 5),
            Quest("q2_income_1k", "Town Economy", "Reach 1,000 gold/s income", QuestType.REACH_INCOME, 1_000.0 * mult, 0.0, 10_000.0 * mult, 2),
            Quest("q2_income_10k", "Trade Hub", "Reach 10,000 gold/s income", QuestType.REACH_INCOME, 10_000.0 * mult, 0.0, 100_000.0 * mult, 3),
            Quest("q2_income_100k", "Economic Power", "Reach 100,000 gold/s income", QuestType.REACH_INCOME, 100_000.0 * mult, 0.0, 1_000_000.0 * mult, 5),
            Quest("q2_tap_100", "Stone Mason", "Tap 100 times", QuestType.TAP_COUNT, 100.0, 0.0, 10_000.0 * mult, 2),
            Quest("q2_tap_300", "Master Builder", "Tap 300 times", QuestType.TAP_COUNT, 300.0, 0.0, 50_000.0 * mult, 3),
            Quest("q2_tap_600", "Legendary Craftsman", "Tap 600 times", QuestType.TAP_COUNT, 600.0, 0.0, 200_000.0 * mult, 5),
            Quest("q2_earn_fast", "Stone Rush", "Earn 50,000 gold in 60 seconds", QuestType.EARN_GOLD, 50_000.0 * mult, 0.0, 100_000.0 * mult, 3, timeLimit = 60),
            Quest("q2_tap_fast", "Swift Builder", "Tap 150 times in 30 seconds", QuestType.TAP_COUNT, 150.0, 0.0, 50_000.0 * mult, 3, timeLimit = 30),
            Quest("q2_milestone", "Town Milestone", "Reach 100 million total gold earned", QuestType.EARN_GOLD, 100_000_000.0 * mult, 0.0, 5_000_000.0 * mult, 10)
        )
    }

    // Era 3 Quest Pool
    private fun generateEra3Quests(mult: Double): List<Quest> {
        return listOf(
            Quest("q3_earn_100k", "Iron Fortune", "Earn 100,000 gold", QuestType.EARN_GOLD, 100_000.0 * mult, 0.0, 50_000.0 * mult, 3),
            Quest("q3_earn_1m", "Kingdom Treasury", "Earn 1,000,000 gold", QuestType.EARN_GOLD, 1_000_000.0 * mult, 0.0, 500_000.0 * mult, 5),
            Quest("q3_earn_10m", "Royal Wealth", "Earn 10,000,000 gold", QuestType.EARN_GOLD, 10_000_000.0 * mult, 0.0, 5_000_000.0 * mult, 10),
            Quest("q3_buy_5", "Military Might", "Purchase 5 military buildings", QuestType.BUY_BUILDINGS, 5.0, 0.0, 2_000_000.0 * mult, 3),
            Quest("q3_buy_15", "War Machine", "Purchase 15 military buildings", QuestType.BUY_BUILDINGS, 15.0, 0.0, 10_000_000.0 * mult, 5),
            Quest("q3_buy_30", "Iron Empire", "Purchase 30 military buildings", QuestType.BUY_BUILDINGS, 30.0, 0.0, 50_000_000.0 * mult, 10),
            Quest("q3_income_10k", "War Economy", "Reach 10,000 gold/s income", QuestType.REACH_INCOME, 10_000.0 * mult, 0.0, 1_000_000.0 * mult, 3),
            Quest("q3_income_100k", "Military Power", "Reach 100,000 gold/s income", QuestType.REACH_INCOME, 100_000.0 * mult, 0.0, 10_000_000.0 * mult, 5),
            Quest("q3_income_1m", "Kingdom Dominance", "Reach 1,000,000 gold/s income", QuestType.REACH_INCOME, 1_000_000.0 * mult, 0.0, 100_000_000.0 * mult, 10),
            Quest("q3_tap_200", "Iron Worker", "Tap 200 times", QuestType.TAP_COUNT, 200.0, 0.0, 1_000_000.0 * mult, 3),
            Quest("q3_tap_500", "War Forger", "Tap 500 times", QuestType.TAP_COUNT, 500.0, 0.0, 5_000_000.0 * mult, 5),
            Quest("q3_tap_1000", "Legendary Smith", "Tap 1,000 times", QuestType.TAP_COUNT, 1_000.0, 0.0, 20_000_000.0 * mult, 10),
            Quest("q3_earn_fast", "Iron Rush", "Earn 5,000,000 gold in 60 seconds", QuestType.EARN_GOLD, 5_000_000.0 * mult, 0.0, 10_000_000.0 * mult, 5, timeLimit = 60),
            Quest("q3_tap_fast", "Battle Tempo", "Tap 200 times in 30 seconds", QuestType.TAP_COUNT, 200.0, 0.0, 5_000_000.0 * mult, 5, timeLimit = 30),
            Quest("q3_milestone", "Kingdom Milestone", "Reach 10 billion total gold earned", QuestType.EARN_GOLD, 10_000_000_000.0 * mult, 0.0, 500_000_000.0 * mult, 20)
        )
    }

    // Era 4 Quest Pool
    private fun generateEra4Quests(mult: Double): List<Quest> {
        return listOf(
            Quest("q4_earn_1m", "Arcane Wealth", "Earn 1,000,000 gold", QuestType.EARN_GOLD, 1_000_000.0 * mult, 0.0, 500_000.0 * mult, 5),
            Quest("q4_earn_10m", "Mage Treasury", "Earn 10,000,000 gold", QuestType.EARN_GOLD, 10_000_000.0 * mult, 0.0, 5_000_000.0 * mult, 10),
            Quest("q4_earn_100m", "Arcane Fortune", "Earn 100,000,000 gold", QuestType.EARN_GOLD, 100_000_000.0 * mult, 0.0, 50_000_000.0 * mult, 15),
            Quest("q4_buy_5", "Magical Foundations", "Purchase 5 magical buildings", QuestType.BUY_BUILDINGS, 5.0, 0.0, 20_000_000.0 * mult, 5),
            Quest("q4_buy_15", "Arcane Network", "Purchase 15 magical buildings", QuestType.BUY_BUILDINGS, 15.0, 0.0, 100_000_000.0 * mult, 10),
            Quest("q4_buy_30", "Mage Empire", "Purchase 30 magical buildings", QuestType.BUY_BUILDINGS, 30.0, 0.0, 500_000_000.0 * mult, 15),
            Quest("q4_income_100k", "Mana Flow", "Reach 100,000 gold/s income", QuestType.REACH_INCOME, 100_000.0 * mult, 0.0, 10_000_000.0 * mult, 5),
            Quest("q4_income_1m", "Arcane Power", "Reach 1,000,000 gold/s income", QuestType.REACH_INCOME, 1_000_000.0 * mult, 0.0, 100_000_000.0 * mult, 10),
            Quest("q4_income_10m", "Magical Supremacy", "Reach 10,000,000 gold/s income", QuestType.REACH_INCOME, 10_000_000.0 * mult, 0.0, 1_000_000_000.0 * mult, 15),
            Quest("q4_tap_300", "Spell Caster", "Tap 300 times", QuestType.TAP_COUNT, 300.0, 0.0, 10_000_000.0 * mult, 5),
            Quest("q4_tap_700", "Archmage", "Tap 700 times", QuestType.TAP_COUNT, 700.0, 0.0, 50_000_000.0 * mult, 10),
            Quest("q4_tap_1500", "Grand Sorcerer", "Tap 1,500 times", QuestType.TAP_COUNT, 1_500.0, 0.0, 200_000_000.0 * mult, 15),
            Quest("q4_earn_fast", "Mana Surge", "Earn 50,000,000 gold in 60 seconds", QuestType.EARN_GOLD, 50_000_000.0 * mult, 0.0, 100_000_000.0 * mult, 10, timeLimit = 60),
            Quest("q4_tap_fast", "Spell Barrage", "Tap 250 times in 30 seconds", QuestType.TAP_COUNT, 250.0, 0.0, 50_000_000.0 * mult, 10, timeLimit = 30),
            Quest("q4_milestone", "Arcane Milestone", "Reach 1 trillion total gold earned", QuestType.EARN_GOLD, 1_000_000_000_000.0 * mult, 0.0, 50_000_000_000.0 * mult, 30)
        )
    }

    // Era 5 Quest Pool
    private fun generateEra5Quests(mult: Double): List<Quest> {
        return listOf(
            Quest("q5_earn_10m", "Legendary Wealth", "Earn 10,000,000 gold", QuestType.EARN_GOLD, 10_000_000.0 * mult, 0.0, 5_000_000.0 * mult, 10),
            Quest("q5_earn_100m", "Mythic Treasury", "Earn 100,000,000 gold", QuestType.EARN_GOLD, 100_000_000.0 * mult, 0.0, 50_000_000.0 * mult, 15),
            Quest("q5_earn_1b", "Divine Fortune", "Earn 1,000,000,000 gold", QuestType.EARN_GOLD, 1_000_000_000.0 * mult, 0.0, 500_000_000.0 * mult, 25),
            Quest("q5_buy_5", "Legendary Structures", "Purchase 5 legendary buildings", QuestType.BUY_BUILDINGS, 5.0, 0.0, 200_000_000.0 * mult, 10),
            Quest("q5_buy_15", "Mythic Realm", "Purchase 15 legendary buildings", QuestType.BUY_BUILDINGS, 15.0, 0.0, 1_000_000_000.0 * mult, 15),
            Quest("q5_buy_30", "Divine Empire", "Purchase 30 legendary buildings", QuestType.BUY_BUILDINGS, 30.0, 0.0, 5_000_000_000.0 * mult, 25),
            Quest("q5_income_1m", "Legendary Flow", "Reach 1,000,000 gold/s income", QuestType.REACH_INCOME, 1_000_000.0 * mult, 0.0, 100_000_000.0 * mult, 10),
            Quest("q5_income_10m", "Mythic Power", "Reach 10,000,000 gold/s income", QuestType.REACH_INCOME, 10_000_000.0 * mult, 0.0, 1_000_000_000.0 * mult, 15),
            Quest("q5_income_100m", "Divine Dominance", "Reach 100,000,000 gold/s income", QuestType.REACH_INCOME, 100_000_000.0 * mult, 0.0, 10_000_000_000.0 * mult, 25),
            Quest("q5_tap_500", "Hero's Touch", "Tap 500 times", QuestType.TAP_COUNT, 500.0, 0.0, 100_000_000.0 * mult, 10),
            Quest("q5_tap_1000", "Titan's Might", "Tap 1,000 times", QuestType.TAP_COUNT, 1_000.0, 0.0, 500_000_000.0 * mult, 15),
            Quest("q5_tap_2000", "God's Will", "Tap 2,000 times", QuestType.TAP_COUNT, 2_000.0, 0.0, 2_000_000_000.0 * mult, 25),
            Quest("q5_earn_fast", "Divine Rush", "Earn 500,000,000 gold in 60 seconds", QuestType.EARN_GOLD, 500_000_000.0 * mult, 0.0, 1_000_000_000.0 * mult, 15, timeLimit = 60),
            Quest("q5_tap_fast", "Godspeed", "Tap 300 times in 30 seconds", QuestType.TAP_COUNT, 300.0, 0.0, 500_000_000.0 * mult, 15, timeLimit = 30),
            Quest("q5_milestone", "Ultimate Milestone", "Reach 100 trillion total gold earned", QuestType.EARN_GOLD, 100_000_000_000_000.0 * mult, 0.0, 5_000_000_000_000.0 * mult, 50)
        )
    }
}
