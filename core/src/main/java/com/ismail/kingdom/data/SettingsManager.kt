// PATH: core/src/main/java/com/ismail/kingdom/data/SettingsManager.kt
package com.ismail.kingdom.data

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences

// Number format options
enum class NumberFormat {
    ABBREVIATED,  // 1.23K
    SCIENTIFIC,   // 1.23e3
    FULL          // 1,234
}

// Manages game settings
object SettingsManager {
    
    private const val PREFS_NAME = "kingdom_settings"
    private const val KEY_SOUND_ENABLED = "sound_enabled"
    private const val KEY_MUSIC_ENABLED = "music_enabled"
    private const val KEY_MUSIC_VOLUME = "music_volume"
    private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    private const val KEY_NUMBER_FORMAT = "number_format"
    
    private var prefs: Preferences? = null
    
    // Settings properties
    var soundEnabled: Boolean = true
        private set
    
    var musicEnabled: Boolean = true
        private set
    
    var musicVolume: Float = 0.7f // 0.0 to 1.0
        private set
    
    var notificationsEnabled: Boolean = true
        private set
    
    var numberFormat: NumberFormat = NumberFormat.ABBREVIATED
        private set
    
    // Initializes settings manager
    fun initialize() {
        prefs = Gdx.app.getPreferences(PREFS_NAME)
        
        // Load settings
        loadSettings()
        
        // Apply number format to Formatters
        applyNumberFormat()
    }
    
    // Loads settings from preferences
    private fun loadSettings() {
        soundEnabled = prefs?.getBoolean(KEY_SOUND_ENABLED, true) ?: true
        musicEnabled = prefs?.getBoolean(KEY_MUSIC_ENABLED, true) ?: true
        musicVolume = prefs?.getFloat(KEY_MUSIC_VOLUME, 0.7f) ?: 0.7f
        notificationsEnabled = prefs?.getBoolean(KEY_NOTIFICATIONS_ENABLED, true) ?: true
        
        val formatString = prefs?.getString(KEY_NUMBER_FORMAT, NumberFormat.ABBREVIATED.name) 
            ?: NumberFormat.ABBREVIATED.name
        numberFormat = try {
            NumberFormat.valueOf(formatString)
        } catch (e: Exception) {
            NumberFormat.ABBREVIATED
        }
        
        Gdx.app.log("SettingsManager", "Settings loaded: sound=$soundEnabled, music=$musicEnabled, format=$numberFormat")
    }
    
    // Saves settings to preferences
    private fun saveSettings() {
        prefs?.putBoolean(KEY_SOUND_ENABLED, soundEnabled)
        prefs?.putBoolean(KEY_MUSIC_ENABLED, musicEnabled)
        prefs?.putFloat(KEY_MUSIC_VOLUME, musicVolume)
        prefs?.putBoolean(KEY_NOTIFICATIONS_ENABLED, notificationsEnabled)
        prefs?.putString(KEY_NUMBER_FORMAT, numberFormat.name)
        prefs?.flush()
        
        Gdx.app.log("SettingsManager", "Settings saved")
    }
    
    // Sets sound enabled
    fun setSoundEnabled(enabled: Boolean) {
        soundEnabled = enabled
        saveSettings()
    }
    
    // Sets music enabled
    fun setMusicEnabled(enabled: Boolean) {
        musicEnabled = enabled
        saveSettings()
        
        // TODO: Apply to music player when implemented
    }
    
    // Sets music volume (0.0 to 1.0)
    fun setMusicVolume(volume: Float) {
        musicVolume = volume.coerceIn(0f, 1f)
        saveSettings()
        
        // TODO: Apply to music player when implemented
    }
    
    // Sets notifications enabled
    fun setNotificationsEnabled(enabled: Boolean) {
        notificationsEnabled = enabled
        saveSettings()
        
        // TODO: Request/revoke Android notification permission
    }
    
    // Sets number format
    fun setNumberFormat(format: NumberFormat) {
        numberFormat = format
        saveSettings()
        applyNumberFormat()
    }
    
    // Applies number format to Formatters (stored for reference only)
    private fun applyNumberFormat() {
        Gdx.app.log("SettingsManager", "Number format applied: $numberFormat")
    }
    
    // Resets all settings to defaults
    fun resetToDefaults() {
        soundEnabled = true
        musicEnabled = true
        musicVolume = 0.7f
        notificationsEnabled = true
        numberFormat = NumberFormat.ABBREVIATED
        
        saveSettings()
        applyNumberFormat()
        
        Gdx.app.log("SettingsManager", "Settings reset to defaults")
    }
    
    // Gets music volume as percentage (0-100)
    fun getMusicVolumePercent(): Int {
        return (musicVolume * 100).toInt()
    }
    
    // Sets music volume from percentage (0-100)
    fun setMusicVolumePercent(percent: Int) {
        setMusicVolume(percent / 100f)
    }
}
