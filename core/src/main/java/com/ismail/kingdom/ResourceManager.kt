// PATH: core/src/main/java/com/ismail/kingdom/ResourceManager.kt
package com.ismail.kingdom

// Holds all game resources
data class ResourceManager(
    var gold: Long = 0,
    var goldPerSecond: Long = 0,
    var stone: Long = 0,
    var iron: Long = 0,
    var mana: Long = 0
)
