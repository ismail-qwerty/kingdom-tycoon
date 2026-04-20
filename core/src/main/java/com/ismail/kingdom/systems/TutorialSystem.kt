// PATH: core/src/main/java/com/ismail/kingdom/systems/TutorialSystem.kt
package com.ismail.kingdom.systems

import com.ismail.kingdom.models.GameState

// Defines each step in the tutorial sequence
enum class TutorialStep {
    TAP_KINGDOM_HALL,
    BUY_FIRST_BUILDING,
    BUY_SECOND_BUILDING,
    HIRE_FIRST_ADVISOR,
    CHECK_QUESTS,
    OPEN_MAP,
    FIRST_MILESTONE,
    PRESTIGE_INTRO,
    TUTORIAL_COMPLETE
}

// Tutorial step data with instructions
data class TutorialStepData(
    val step: String,
    val title: String,
    val instruction: String,
    val highlightTarget: String,
    val isInteractive: Boolean = true
)

// Manages the first-time player tutorial experience
class TutorialSystem(private val gameState: GameState) {

    var currentStep: TutorialStep = TutorialStep.TAP_KINGDOM_HALL
        private set

    var isActive: Boolean = true
        private set

    private val completedSteps = mutableSetOf<TutorialStep>()

    private val tutorialSteps = mapOf(
        TutorialStep.TAP_KINGDOM_HALL to TutorialStepData("TAP_KINGDOM_HALL", "Welcome to Your Kingdom!", "Tap your Kingdom Hall to earn gold!", "kingdom_hall"),
        TutorialStep.BUY_FIRST_BUILDING to TutorialStepData("BUY_FIRST_BUILDING", "You Earned Gold!", "Buy your first building!", "building_0"),
        TutorialStep.BUY_SECOND_BUILDING to TutorialStepData("BUY_SECOND_BUILDING", "Excellent!", "Buy one more building!", "building_1"),
        TutorialStep.HIRE_FIRST_ADVISOR to TutorialStepData("HIRE_FIRST_ADVISOR", "Hire an Advisor", "Advisors automate buildings!", "advisors_tab"),
        TutorialStep.CHECK_QUESTS to TutorialStepData("CHECK_QUESTS", "Complete Quests", "Check your Quests for goals!", "quests_button"),
        TutorialStep.OPEN_MAP to TutorialStepData("OPEN_MAP", "Explore Your Kingdom", "Open the map to explore!", "map_button"),
        TutorialStep.FIRST_MILESTONE to TutorialStepData("FIRST_MILESTONE", "10 Buildings! MILESTONE!", "Amazing progress!", "none", false),
        TutorialStep.PRESTIGE_INTRO to TutorialStepData("PRESTIGE_INTRO", "Prestige Awaits", "Prestige for permanent power!", "prestige_button", false)
    )

    // Initializes tutorial from saved state
    fun initialize() {
        if (gameState.tutorialCompleted) {
            isActive = false
            currentStep = TutorialStep.TUTORIAL_COMPLETE
        } else {
            currentStep = try {
                TutorialStep.valueOf(gameState.currentTutorialStep)
            } catch (e: Exception) {
                TutorialStep.TAP_KINGDOM_HALL
            }
            gameState.completedTutorialSteps.forEach { stepName ->
                try { completedSteps.add(TutorialStep.valueOf(stepName)) } catch (e: Exception) {}
            }
        }
    }

    // Advances to the next tutorial step
    fun advance(step: TutorialStep) {
        if (!isActive) return
        completedSteps.add(currentStep)
        gameState.completedTutorialSteps.add(currentStep.name)
        currentStep = step
        gameState.currentTutorialStep = step.name
        if (step == TutorialStep.TUTORIAL_COMPLETE) completeTutorial()
    }

    // Advances to the next step in sequence
    fun advanceToNext() {
        val nextStep = when (currentStep) {
            TutorialStep.TAP_KINGDOM_HALL -> TutorialStep.BUY_FIRST_BUILDING
            TutorialStep.BUY_FIRST_BUILDING -> TutorialStep.BUY_SECOND_BUILDING
            TutorialStep.BUY_SECOND_BUILDING -> TutorialStep.HIRE_FIRST_ADVISOR
            TutorialStep.HIRE_FIRST_ADVISOR -> TutorialStep.CHECK_QUESTS
            TutorialStep.CHECK_QUESTS -> TutorialStep.OPEN_MAP
            TutorialStep.OPEN_MAP -> TutorialStep.TUTORIAL_COMPLETE
            TutorialStep.FIRST_MILESTONE -> currentStep
            TutorialStep.PRESTIGE_INTRO -> TutorialStep.TUTORIAL_COMPLETE
            TutorialStep.TUTORIAL_COMPLETE -> TutorialStep.TUTORIAL_COMPLETE
        }
        advance(nextStep)
    }

    // Checks if a specific step is complete
    fun isStepComplete(step: TutorialStep): Boolean = completedSteps.contains(step)

    // Gets the current step data
    fun getCurrentStepData(): TutorialStepData? = tutorialSteps[currentStep]

    // Checks if current step should auto-advance based on game state
    fun checkAutoAdvance() {
        if (!isActive) return
        when (currentStep) {
            TutorialStep.TAP_KINGDOM_HALL -> if (gameState.tapCount > 0) advanceToNext()
            TutorialStep.BUY_FIRST_BUILDING -> if (gameState.buildings.any { it.count > 0 }) advanceToNext()
            TutorialStep.BUY_SECOND_BUILDING -> if (gameState.buildings.sumOf { it.count } >= 2) advanceToNext()
            TutorialStep.HIRE_FIRST_ADVISOR -> if (gameState.advisors.any { it.isHired }) advanceToNext()
            else -> {}
        }
    }

    // Triggers milestone step when player reaches 10 buildings
    fun checkMilestone() {
        if (!isActive || isStepComplete(TutorialStep.FIRST_MILESTONE)) return
        if (gameState.buildings.sumOf { it.count } >= 10) advance(TutorialStep.FIRST_MILESTONE)
    }

    // Triggers prestige intro when player reaches 10% of prestige requirement
    fun checkPrestigeIntro() {
        if (!isActive || isStepComplete(TutorialStep.PRESTIGE_INTRO)) return
        if (currentStep == TutorialStep.FIRST_MILESTONE) return
        if (gameState.totalLifetimeGold / 1e9 >= 0.1) advance(TutorialStep.PRESTIGE_INTRO)
    }

    // Completes the tutorial
    fun completeTutorial() {
        isActive = false
        currentStep = TutorialStep.TUTORIAL_COMPLETE
        gameState.tutorialCompleted = true
        gameState.currentTutorialStep = TutorialStep.TUTORIAL_COMPLETE.name
    }

    // Skips the tutorial entirely
    fun skipTutorial() {
        TutorialStep.values().forEach { step ->
            completedSteps.add(step)
            gameState.completedTutorialSteps.add(step.name)
        }
        completeTutorial()
    }

    // Checks if tutorial should be shown for new players
    fun shouldShowTutorial(): Boolean = !gameState.tutorialCompleted && gameState.totalLifetimeGold < 1000.0

    // Gets progress percentage through tutorial
    fun getProgress(): Float {
        val totalSteps = TutorialStep.values().size - 1
        return (completedSteps.size.toFloat() / totalSteps).coerceIn(0f, 1f)
    }
}
