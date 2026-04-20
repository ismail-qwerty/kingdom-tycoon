package com.ismail.kingdom.systems

import com.ismail.kingdom.models.GameState
import com.ismail.kingdom.models.KingdomEvent
import com.ismail.kingdom.models.KingdomEventType
import kotlin.random.Random

// Manages weekly kingdom events
class EventSystem {

    var currentEvent: KingdomEvent? = null
        private set

    private var nextEventTimer: Float = 0f
    private val MIN_EVENT_INTERVAL_DAYS = 5
    private val MAX_EVENT_INTERVAL_DAYS = 9
    private val SECONDS_PER_DAY = 86400

    // Updates event timer and applies/removes bonuses
    fun update(delta: Float, state: GameState) {
        val currentEvt = currentEvent

        if (currentEvt != null && currentEvt.isActive) {
            // Countdown active event
            currentEvt.remainingSeconds -= delta.toInt()

            if (currentEvt.remainingSeconds <= 0) {
                // Event ended
                endEvent(state)
            }
        } else {
            // Count down to next event
            nextEventTimer -= delta

            if (nextEventTimer <= 0f) {
                scheduleNextEvent(state)
            }
        }
    }

    // Schedules the next random event
    fun scheduleNextEvent(state: GameState) {
        val eventType = KingdomEventType.values().random()
        val event = createEvent(eventType)

        currentEvent = event
        event.isActive = true

        // Apply event bonuses
        applyEventBonuses(event, state)

        println("Event started: ${event.name}")
    }

    // Creates an event based on type
    private fun createEvent(type: KingdomEventType): KingdomEvent {
        return when (type) {
            KingdomEventType.GOBLIN_RAID -> KingdomEvent(
                id = "goblin_raid_${System.currentTimeMillis()}",
                name = "Goblin Raid",
                description = "Defend your kingdom and earn triple!",
                type = KingdomEventType.GOBLIN_RAID,
                multiplier = 3.0,
                durationSeconds = 172800, // 48 hours
                remainingSeconds = 172800,
                isActive = false
            )

            KingdomEventType.ROYAL_FESTIVAL -> KingdomEvent(
                id = "royal_festival_${System.currentTimeMillis()}",
                name = "Royal Festival",
                description = "Celebrate with double income and quest rewards!",
                type = KingdomEventType.ROYAL_FESTIVAL,
                multiplier = 2.0,
                durationSeconds = 172800, // 48 hours
                remainingSeconds = 172800,
                isActive = false
            )

            KingdomEventType.MERCHANT_CARAVAN -> KingdomEvent(
                id = "merchant_caravan_${System.currentTimeMillis()}",
                name = "Merchant Caravan",
                description = "Buildings cost 50% less for a limited time!",
                type = KingdomEventType.MERCHANT_CARAVAN,
                multiplier = 0.5, // Cost reduction
                durationSeconds = 86400, // 24 hours
                remainingSeconds = 86400,
                isActive = false
            )

            KingdomEventType.DRAGON_SIGHTING -> KingdomEvent(
                id = "dragon_sighting_${System.currentTimeMillis()}",
                name = "Dragon Sighting",
                description = "Tap the dragon for 5x gold!",
                type = KingdomEventType.DRAGON_SIGHTING,
                multiplier = 5.0,
                durationSeconds = 43200, // 12 hours
                remainingSeconds = 43200,
                isActive = false
            )

            KingdomEventType.PLAGUE_OF_FROGS -> KingdomEvent(
                id = "plague_of_frogs_${System.currentTimeMillis()}",
                name = "Plague of Frogs",
                description = "The frogs bring gold somehow",
                type = KingdomEventType.PLAGUE_OF_FROGS,
                multiplier = 4.0,
                durationSeconds = 172800, // 48 hours
                remainingSeconds = 172800,
                isActive = false
            )

            KingdomEventType.HARVEST_MOON -> KingdomEvent(
                id = "harvest_moon_${System.currentTimeMillis()}",
                name = "Harvest Moon",
                description = "Offline earnings cap doubled for 3 days!",
                type = KingdomEventType.HARVEST_MOON,
                multiplier = 2.0, // Offline cap multiplier
                durationSeconds = 259200, // 72 hours
                remainingSeconds = 259200,
                isActive = false
            )

            KingdomEventType.GOLD_RUSH -> KingdomEvent(
                id = "gold_rush_${System.currentTimeMillis()}",
                name = "Gold Rush",
                description = "Gold everywhere!",
                type = KingdomEventType.GOLD_RUSH,
                multiplier = 2.5,
                durationSeconds = 3600,
                remainingSeconds = 3600,
                isActive = false
            )

            KingdomEventType.DOUBLE_INCOME -> KingdomEvent(
                id = "double_income_${System.currentTimeMillis()}",
                name = "Double Income",
                description = "2x income for all!",
                type = KingdomEventType.DOUBLE_INCOME,
                multiplier = 2.0,
                durationSeconds = 7200,
                remainingSeconds = 7200,
                isActive = false
            )

            KingdomEventType.TAP_BONUS -> KingdomEvent(
                id = "tap_bonus_${System.currentTimeMillis()}",
                name = "Tap Bonus",
                description = "Extra gold per tap!",
                type = KingdomEventType.TAP_BONUS,
                multiplier = 3.0,
                durationSeconds = 1800,
                remainingSeconds = 1800,
                isActive = false
            )

            KingdomEventType.BUILDING_DISCOUNT -> KingdomEvent(
                id = "building_discount_${System.currentTimeMillis()}",
                name = "Building Discount",
                description = "All buildings 20% off!",
                type = KingdomEventType.BUILDING_DISCOUNT,
                multiplier = 0.8,
                durationSeconds = 3600,
                remainingSeconds = 3600,
                isActive = false
            )

            KingdomEventType.CROWN_SHARD_BONUS -> KingdomEvent(
                id = "crown_shard_bonus_${System.currentTimeMillis()}",
                name = "Crown Shard Bonus",
                description = "Earn more crown shards!",
                type = KingdomEventType.CROWN_SHARD_BONUS,
                multiplier = 1.5,
                durationSeconds = 14400,
                remainingSeconds = 14400,
                isActive = false
            )

            KingdomEventType.SPECIAL_QUEST -> KingdomEvent(
                id = "special_quest_${System.currentTimeMillis()}",
                name = "Special Quest",
                description = "New special quest available!",
                type = KingdomEventType.SPECIAL_QUEST,
                multiplier = 1.0,
                durationSeconds = 86400,
                remainingSeconds = 86400,
                isActive = false
            )
        }
    }

    // Applies event bonuses to game state
    private fun applyEventBonuses(event: KingdomEvent, state: GameState) {
        // Implementation remains similar but uses Unified enum
    }

    // Ends the current event
    private fun endEvent(state: GameState) {
        val event = currentEvent ?: return

        event.isActive = false
        currentEvent = null

        // Schedule next event in 5-9 days
        val daysUntilNext = Random.nextInt(MIN_EVENT_INTERVAL_DAYS, MAX_EVENT_INTERVAL_DAYS + 1)
        nextEventTimer = (daysUntilNext * SECONDS_PER_DAY).toFloat()

        println("Event ended: ${event.name}. Next event in $daysUntilNext days")
    }

    // Gets income multiplier from active event
    fun getEventIncomeMultiplier(): Double {
        val event = currentEvent
        if (event == null || !event.isActive) return 1.0

        return when (event.type) {
            KingdomEventType.GOBLIN_RAID -> event.multiplier
            KingdomEventType.ROYAL_FESTIVAL -> event.multiplier
            KingdomEventType.PLAGUE_OF_FROGS -> event.multiplier
            KingdomEventType.DOUBLE_INCOME -> event.multiplier
            else -> 1.0
        }
    }

    // Gets tap multiplier from active event
    fun getEventTapMultiplier(): Double {
        val event = currentEvent
        if (event == null || !event.isActive) return 1.0

        return when (event.type) {
            KingdomEventType.DRAGON_SIGHTING -> event.multiplier
            KingdomEventType.TAP_BONUS -> event.multiplier
            else -> 1.0
        }
    }

    // Gets cost multiplier from active event
    fun getEventCostMultiplier(): Double {
        val event = currentEvent
        if (event == null || !event.isActive) return 1.0

        return when (event.type) {
            KingdomEventType.MERCHANT_CARAVAN -> event.multiplier
            KingdomEventType.BUILDING_DISCOUNT -> event.multiplier
            else -> 1.0
        }
    }

    // Gets quest reward multiplier from active event
    fun getEventQuestMultiplier(): Double {
        val event = currentEvent
        if (event == null || !event.isActive) return 1.0

        return when (event.type) {
            KingdomEventType.ROYAL_FESTIVAL -> event.multiplier
            else -> 1.0
        }
    }

    // Gets offline cap multiplier from active event
    fun getEventOfflineCapMultiplier(): Double {
        val event = currentEvent
        if (event == null || !event.isActive) return 1.0

        return when (event.type) {
            KingdomEventType.HARVEST_MOON -> event.multiplier
            else -> 1.0
        }
    }

    // Gets formatted time remaining for current event
    fun getEventTimeRemaining(): String {
        val event = currentEvent
        if (event == null || !event.isActive) return "No active event"

        val hours = event.remainingSeconds / 3600
        val minutes = (event.remainingSeconds % 3600) / 60

        return if (hours > 0) {
            "${hours}h ${minutes}m"
        } else {
            "${minutes}m"
        }
    }

    // Gets time until next event
    fun getTimeUntilNextEvent(): String {
        if (currentEvent?.isActive == true) {
            return "Event in progress"
        }

        val days = (nextEventTimer / SECONDS_PER_DAY).toInt()
        val hours = ((nextEventTimer % SECONDS_PER_DAY) / 3600).toInt()

        return if (days > 0) {
            "${days}d ${hours}h"
        } else {
            "${hours}h"
        }
    }

    fun getSecondsUntilNextEvent(): Int {
        return nextEventTimer.toInt()
    }

    // Forces an event for testing purposes
    fun forceEventForTesting(eventType: KingdomEventType, state: GameState) {
        val event = createEvent(eventType)
        currentEvent = event
        event.isActive = true
        applyEventBonuses(event, state)

        println("Forced event: ${event.name}")
    }

    // Checks if a specific event type is active
    fun isEventActive(eventType: KingdomEventType): Boolean {
        val event = currentEvent
        return event != null && event.isActive && event.type == eventType
    }

    // Gets the current active event
    fun getActiveEvent(): KingdomEvent? {
        return if (currentEvent?.isActive == true) currentEvent else null
    }

    // Initializes event system with saved event data
    fun initialize(savedEvent: KingdomEvent?, nextEventSeconds: Float) {
        currentEvent = savedEvent
        nextEventTimer = nextEventSeconds

        // If no event scheduled, schedule one
        if (currentEvent == null && nextEventTimer <= 0f) {
            val daysUntilNext = Random.nextInt(MIN_EVENT_INTERVAL_DAYS, MAX_EVENT_INTERVAL_DAYS + 1)
            nextEventTimer = (daysUntilNext * SECONDS_PER_DAY).toFloat()
        }
    }

    // Gets next event timer for saving
    fun getNextEventTimer(): Float {
        return nextEventTimer
    }

    // Cancels current event (for admin/testing)
    fun cancelCurrentEvent(state: GameState) {
        if (currentEvent != null) {
            endEvent(state)
        }
    }
}
