// PATH: core/src/main/java/com/ismail/kingdom/Era.kt
package com.ismail.kingdom

// Represents the 5 progression eras of the kingdom
sealed class Era(val index: Int, val displayName: String, val goldMultiplier: Double) {
    object Stone      : Era(0, "Stone Age",       1.0)
    object Medieval   : Era(1, "Medieval",         5.0)
    object Renaissance: Era(2, "Renaissance",     25.0)
    object Industrial : Era(3, "Industrial",     125.0)
    object Modern     : Era(4, "Modern",          625.0)

    companion object {
        // Returns the era matching the given index, defaulting to Stone
        fun fromIndex(i: Int): Era = all.getOrElse(i) { Stone }

        val all: List<Era> = listOf(Stone, Medieval, Renaissance, Industrial, Modern)
    }
}
