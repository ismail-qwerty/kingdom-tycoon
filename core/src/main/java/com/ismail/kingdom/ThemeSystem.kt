// PATH: core/src/main/java/com/ismail/kingdom/ThemeSystem.kt
package com.ismail.kingdom

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils

// Era theme data
data class EraTheme(
    val primaryColor: Color,
    val secondaryColor: Color,
    val accentColor: Color,
    val backgroundTint: Color,
    val fontColor: Color
)

// Button style types
enum class ButtonType {
    GOLD,       // Primary action buttons
    GREY,       // Secondary/disabled buttons
    TAB,        // Tab navigation buttons
    PRESTIGE,   // Special prestige button
    DANGER      // Destructive actions (reset, etc.)
}

// Label style types
enum class LabelType {
    GOLD_LARGE,     // Large gold numbers
    GOLD_SMALL,     // Small gold numbers
    BODY_WHITE,     // Normal white text
    BODY_GREY,      // Secondary grey text
    TITLE,          // Screen titles
    COMBO           // Combo indicators
}

// Theme system managing era-specific colors and styles
object ThemeSystem {
    
    // Era themes
    private val eraThemes = mapOf(
        1 to EraTheme(
            primaryColor = parseColor("8B6914"),      // Warm dirt brown
            secondaryColor = parseColor("5C4209"),    // Dark brown
            accentColor = parseColor("FFD700"),       // Gold
            backgroundTint = parseColor("2D1B00"),    // Very dark brown
            fontColor = Color.WHITE
        ),
        2 to EraTheme(
            primaryColor = parseColor("808080"),      // Stone grey
            secondaryColor = parseColor("505050"),    // Dark grey
            accentColor = parseColor("C0C0C0"),       // Silver
            backgroundTint = parseColor("1A1A1A"),    // Very dark grey
            fontColor = Color.WHITE
        ),
        3 to EraTheme(
            primaryColor = parseColor("704214"),      // Iron rust
            secondaryColor = parseColor("3D2310"),    // Dark rust
            accentColor = parseColor("FF6B00"),       // Orange
            backgroundTint = parseColor("1A0D00"),    // Very dark rust
            fontColor = Color.WHITE
        ),
        4 to EraTheme(
            primaryColor = parseColor("6B2D8B"),      // Mystic purple
            secondaryColor = parseColor("3D1752"),    // Dark purple
            accentColor = parseColor("BF00FF"),       // Bright purple
            backgroundTint = parseColor("0D0017"),    // Very dark purple
            fontColor = Color.WHITE
        ),
        5 to EraTheme(
            primaryColor = parseColor("B8860B"),      // Legendary gold
            secondaryColor = parseColor("8B6914"),    // Dark gold
            accentColor = parseColor("FFD700"),       // Bright gold
            backgroundTint = parseColor("1A1200"),    // Very dark gold
            fontColor = Color.WHITE
        )
    )

    // Parses hex color string to Color
    private fun parseColor(hex: String): Color {
        val hexValue = if (hex.length == 6) hex + "FF" else hex
        val rgba = hexValue.toLong(16)
        return Color(
            ((rgba shr 24) and 0xFF) / 255f,
            ((rgba shr 16) and 0xFF) / 255f,
            ((rgba shr 8) and 0xFF) / 255f,
            (rgba and 0xFF) / 255f
        )
    }
    
    // Current theme
    var currentTheme: EraTheme = eraThemes[1]!!
        private set
    
    private var currentEraId: Int = 1
    
    // Applies theme for specified era
    fun applyTheme(eraId: Int) {
        val theme = eraThemes[eraId]
        if (theme != null) {
            currentTheme = theme
            currentEraId = eraId
            com.badlogic.gdx.Gdx.app.log("ThemeSystem", "Applied theme for era $eraId")
        } else {
            com.badlogic.gdx.Gdx.app.error("ThemeSystem", "Theme for era $eraId not found")
        }
    }
    
    // Lerps to target theme (for smooth prestige transitions)
    fun lerpToTheme(targetEraId: Int, progress: Float): EraTheme {
        val targetTheme = eraThemes[targetEraId] ?: return currentTheme
        val clampedProgress = progress.coerceIn(0f, 1f)
        
        return EraTheme(
            primaryColor = lerpColor(currentTheme.primaryColor, targetTheme.primaryColor, clampedProgress),
            secondaryColor = lerpColor(currentTheme.secondaryColor, targetTheme.secondaryColor, clampedProgress),
            accentColor = lerpColor(currentTheme.accentColor, targetTheme.accentColor, clampedProgress),
            backgroundTint = lerpColor(currentTheme.backgroundTint, targetTheme.backgroundTint, clampedProgress),
            fontColor = lerpColor(currentTheme.fontColor, targetTheme.fontColor, clampedProgress)
        )
    }
    
    // Lerps between two colors
    private fun lerpColor(start: Color, end: Color, progress: Float): Color {
        return Color(
            MathUtils.lerp(start.r, end.r, progress),
            MathUtils.lerp(start.g, end.g, progress),
            MathUtils.lerp(start.b, end.b, progress),
            MathUtils.lerp(start.a, end.a, progress)
        )
    }
    
    // Gets button style name for Skin
    fun getButtonStyle(type: ButtonType): String {
        return when (type) {
            ButtonType.GOLD -> "gold-button"
            ButtonType.GREY -> "grey-button"
            ButtonType.TAB -> "tab-button"
            ButtonType.PRESTIGE -> "prestige-button"
            ButtonType.DANGER -> "danger-button"
        }
    }
    
    // Gets label style name for Skin
    fun getLabelStyle(type: LabelType): String {
        return when (type) {
            LabelType.GOLD_LARGE -> "gold-large"
            LabelType.GOLD_SMALL -> "gold-small"
            LabelType.BODY_WHITE -> "body-white"
            LabelType.BODY_GREY -> "body-grey"
            LabelType.TITLE -> "title"
            LabelType.COMBO -> "combo"
        }
    }
    
    // Gets current era ID
    fun getCurrentEraId(): Int = currentEraId
    
    // Gets theme for specific era
    fun getTheme(eraId: Int): EraTheme? = eraThemes[eraId]
    
    // Gets primary color for current era
    fun getPrimaryColor(): Color = currentTheme.primaryColor
    
    // Gets secondary color for current era
    fun getSecondaryColor(): Color = currentTheme.secondaryColor
    
    // Gets accent color for current era
    fun getAccentColor(): Color = currentTheme.accentColor
    
    // Gets background tint for current era
    fun getBackgroundTint(): Color = currentTheme.backgroundTint
    
    // Gets font color for current era
    fun getFontColor(): Color = currentTheme.fontColor
    
    // Creates a tinted version of a color
    fun tintColor(color: Color, tintAmount: Float): Color {
        val tint = currentTheme.primaryColor
        return Color(
            MathUtils.lerp(color.r, tint.r, tintAmount),
            MathUtils.lerp(color.g, tint.g, tintAmount),
            MathUtils.lerp(color.b, tint.b, tintAmount),
            color.a
        )
    }
    
    // Gets a darkened version of primary color (for backgrounds)
    fun getDarkenedPrimary(amount: Float = 0.3f): Color {
        return Color(
            currentTheme.primaryColor.r * amount,
            currentTheme.primaryColor.g * amount,
            currentTheme.primaryColor.b * amount,
            1f
        )
    }
    
    // Gets a lightened version of primary color (for highlights)
    fun getLightenedPrimary(amount: Float = 1.5f): Color {
        return Color(
            (currentTheme.primaryColor.r * amount).coerceAtMost(1f),
            (currentTheme.primaryColor.g * amount).coerceAtMost(1f),
            (currentTheme.primaryColor.b * amount).coerceAtMost(1f),
            1f
        )
    }
}

// Extension function for easy theme color access
fun eraTheme(): EraTheme = ThemeSystem.currentTheme
