// PATH: core/src/main/java/com/ismail/kingdom/systems/AntiCheatSystem.kt
package com.ismail.kingdom.systems

import com.ismail.kingdom.models.GameState
import com.ismail.kingdom.utils.SafeMath

// Result of game state validation
data class ValidationResult(
    val isValid: Boolean,
    val violations: List<String> = emptyList(),
    val correctedFields: List<String> = emptyList()
)

// Anti-cheat system to validate and sanitize game state
object AntiCheatSystem {
    
    // Reasonable maximum values
    private const val MAX_INCOME_MULTIPLIER = 10000.0
    private const val MAX_CROWN_SHARDS = 100000
    private const val MAX_BUILDING_COUNT = 10000
    private const val MAX_HERO_LEVEL = 1000
    private const val MAX_ADVISOR_LEVEL = 100
    private const val MAX_ERA = 5
    private const val MAX_PRESTIGE_LAYER = 3
    
    // Time validation constants
    private const val MIN_SAVE_INTERVAL_MS = 1000L // 1 second
    private const val MAX_OFFLINE_DAYS = 30
    private const val MAX_OFFLINE_HOURS_CAPPED = 24
    
    // Validates entire game state and returns result
    fun validateGameState(state: GameState): ValidationResult {
        val violations = mutableListOf<String>()
        val correctedFields = mutableListOf<String>()
        
        // Validate income multiplier
        if (state.incomeMultiplier > MAX_INCOME_MULTIPLIER) {
            violations.add("Income multiplier exceeds maximum: ${state.incomeMultiplier} > $MAX_INCOME_MULTIPLIER")
            state.incomeMultiplier = MAX_INCOME_MULTIPLIER
            correctedFields.add("incomeMultiplier")
        }
        
        if (state.incomeMultiplier < 0) {
            violations.add("Income multiplier is negative: ${state.incomeMultiplier}")
            state.incomeMultiplier = 1.0
            correctedFields.add("incomeMultiplier")
        }
        
        // Validate crown shards
        if (state.crownShards > MAX_CROWN_SHARDS) {
            violations.add("Crown shards exceed maximum: ${state.crownShards} > $MAX_CROWN_SHARDS")
            state.crownShards = MAX_CROWN_SHARDS
            correctedFields.add("crownShards")
        }
        
        if (state.crownShards < 0) {
            violations.add("Crown shards is negative: ${state.crownShards}")
            state.crownShards = 0
            correctedFields.add("crownShards")
        }
        
        // Validate gold
        if (!SafeMath.isValidGameNumber(state.currentGold)) {
            violations.add("Current gold is invalid: ${state.currentGold}")
            state.currentGold = 0.0
            correctedFields.add("currentGold")
        } else {
            state.currentGold = SafeMath.clampGold(state.currentGold)
        }
        
        if (!SafeMath.isValidGameNumber(state.totalLifetimeGold)) {
            violations.add("Lifetime gold is invalid: ${state.totalLifetimeGold}")
            state.totalLifetimeGold = state.currentGold
            correctedFields.add("totalLifetimeGold")
        } else {
            state.totalLifetimeGold = SafeMath.clampGold(state.totalLifetimeGold)
        }
        
        // Validate era and prestige
        if (state.currentEra < 1 || state.currentEra > MAX_ERA) {
            violations.add("Current era out of range: ${state.currentEra}")
            state.currentEra = state.currentEra.coerceIn(1, MAX_ERA)
            correctedFields.add("currentEra")
        }
        
        if (state.prestigeLayer < 0 || state.prestigeLayer > MAX_PRESTIGE_LAYER) {
            violations.add("Prestige layer out of range: ${state.prestigeLayer}")
            state.prestigeLayer = state.prestigeLayer.coerceIn(0, MAX_PRESTIGE_LAYER)
            correctedFields.add("prestigeLayer")
        }
        
        // Validate buildings
        state.buildings.forEachIndexed { index, building ->
            if (building.count > MAX_BUILDING_COUNT) {
                violations.add("Building ${building.id} count exceeds maximum: ${building.count}")
                correctedFields.add("buildings[$index].count")
            }
            
            if (building.count < 0) {
                violations.add("Building ${building.id} count is negative: ${building.count}")
                correctedFields.add("buildings[$index].count")
            }
            
            if (!SafeMath.isValidGameNumber(building.baseCost)) {
                violations.add("Building ${building.id} has invalid base cost")
                correctedFields.add("buildings[$index].baseCost")
            }
            
            if (!SafeMath.isValidGameNumber(building.baseIncome)) {
                violations.add("Building ${building.id} has invalid base income")
                correctedFields.add("buildings[$index].baseIncome")
            }
        }
        
        // Validate heroes
        state.heroes.forEachIndexed { index, hero ->
            if (hero.level > MAX_HERO_LEVEL) {
                violations.add("Hero ${hero.name} level exceeds maximum: ${hero.level}")
                correctedFields.add("heroes[$index].level")
            }
            
            if (hero.level < 0) {
                violations.add("Hero ${hero.name} level is negative: ${hero.level}")
                correctedFields.add("heroes[$index].level")
            }
            
            if (!SafeMath.isValidGameNumber(hero.passiveBonus)) {
                violations.add("Hero ${hero.name} has invalid passive bonus")
                correctedFields.add("heroes[$index].passiveBonus")
            }
        }
        
        // Validate advisors
        state.advisors.forEachIndexed { index, advisor ->
            if (advisor.level > MAX_ADVISOR_LEVEL) {
                violations.add("Advisor ${advisor.name} level exceeds maximum: ${advisor.level}")
                correctedFields.add("advisors[$index].level")
            }
            
            if (advisor.level < 0) {
                violations.add("Advisor ${advisor.name} level is negative: ${advisor.level}")
                correctedFields.add("advisors[$index].level")
            }
        }
        
        // Validate resources
        state.resources.forEach { (resourceId, resource) ->
            if (!SafeMath.isValidGameNumber(resource.amount)) {
                violations.add("Resource $resourceId has invalid amount: ${resource.amount}")
                resource.amount = 0.0
                correctedFields.add("resources[$resourceId].amount")
            } else if (resource.amount > SafeMath.MAX_GAME_VALUE) {
                violations.add("Resource $resourceId exceeds maximum: ${resource.amount}")
                resource.amount = SafeMath.MAX_GAME_VALUE
                correctedFields.add("resources[$resourceId].amount")
            }
        }
        
        // Validate save time
        val currentTime = System.currentTimeMillis()
        if (state.lastSaveTime > currentTime) {
            violations.add("Last save time is in the future: ${state.lastSaveTime} > $currentTime")
            state.lastSaveTime = currentTime
            correctedFields.add("lastSaveTime")
        }
        
        // Validate tap count
        if (state.tapCount < 0) {
            violations.add("Tap count is negative: ${state.tapCount}")
            state.tapCount = 0
            correctedFields.add("tapCount")
        }
        
        return ValidationResult(
            isValid = violations.isEmpty(),
            violations = violations,
            correctedFields = correctedFields
        )
    }
    
    // Sanitizes offline time to prevent exploits
    fun sanitizeOfflineTime(secondsOffline: Long): Long {
        // Reject negative values
        if (secondsOffline < 0) {
            println("AntiCheat: Negative offline time detected: $secondsOffline")
            return 0L
        }
        
        // Reject impossibly long offline times (> 30 days)
        val maxOfflineSeconds = MAX_OFFLINE_DAYS * 24 * 60 * 60L
        if (secondsOffline > maxOfflineSeconds) {
            println("AntiCheat: Offline time exceeds 30 days: $secondsOffline seconds")
            return MAX_OFFLINE_HOURS_CAPPED * 60 * 60L // Cap at 24 hours
        }
        
        // Always cap at 24 hours
        val maxCappedSeconds = MAX_OFFLINE_HOURS_CAPPED * 60 * 60L
        if (secondsOffline > maxCappedSeconds) {
            println("AntiCheat: Offline time capped from $secondsOffline to $maxCappedSeconds seconds")
            return maxCappedSeconds
        }
        
        return secondsOffline
    }
    
    // Validates time since last save
    fun validateSaveInterval(lastSaveTime: Long): Boolean {
        if (lastSaveTime == 0L) {
            return true // First save
        }
        
        val currentTime = System.currentTimeMillis()
        val timeSinceLastSave = currentTime - lastSaveTime
        
        if (timeSinceLastSave < 0) {
            println("AntiCheat: Time since last save is negative (clock manipulation?)")
            return false
        }
        
        if (timeSinceLastSave < MIN_SAVE_INTERVAL_MS) {
            println("AntiCheat: Save interval too short: $timeSinceLastSave ms")
            return false
        }
        
        return true
    }
    
    // Handles validation failures
    fun onValidationFailed(result: ValidationResult) {
        if (result.isValid) return
        
        println("========== ANTI-CHEAT VALIDATION FAILED ==========")
        println("Violations detected: ${result.violations.size}")
        
        result.violations.forEach { violation ->
            println("  - $violation")
        }
        
        if (result.correctedFields.isNotEmpty()) {
            println("\nCorrected fields: ${result.correctedFields.size}")
            result.correctedFields.forEach { field ->
                println("  - $field")
            }
        }
        
        println("==================================================")
    }
    
    // Validates a single numeric value
    fun validateNumericValue(
        value: Double,
        fieldName: String,
        min: Double = 0.0,
        max: Double = SafeMath.MAX_GAME_VALUE
    ): Double {
        if (!SafeMath.isValidGameNumber(value)) {
            println("AntiCheat: Invalid $fieldName: $value")
            return min
        }
        
        if (value < min) {
            println("AntiCheat: $fieldName below minimum: $value < $min")
            return min
        }
        
        if (value > max) {
            println("AntiCheat: $fieldName above maximum: $value > $max")
            return max
        }
        
        return value
    }
    
    // Validates an integer value
    fun validateIntValue(
        value: Int,
        fieldName: String,
        min: Int = 0,
        max: Int = Int.MAX_VALUE
    ): Int {
        if (value < min) {
            println("AntiCheat: $fieldName below minimum: $value < $min")
            return min
        }
        
        if (value > max) {
            println("AntiCheat: $fieldName above maximum: $value > $max")
            return max
        }
        
        return value
    }
    
    // Performs a full security check on game state
    fun performSecurityCheck(state: GameState): Boolean {
        val validationResult = validateGameState(state)
        
        if (!validationResult.isValid) {
            onValidationFailed(validationResult)
            return false
        }
        
        // Additional checks
        if (!validateSaveInterval(state.lastSaveTime)) {
            println("AntiCheat: Save interval validation failed")
            return false
        }
        
        return true
    }
}
