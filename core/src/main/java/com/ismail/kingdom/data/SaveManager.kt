// PATH: core/src/main/java/com/ismail/kingdom/data/SaveManager.kt
package com.ismail.kingdom.data

import com.badlogic.gdx.Preferences
import com.ismail.kingdom.models.GameState
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

// Handles saving and loading game state using kotlinx.serialization
class SaveManager(private val prefs: Preferences) {

    companion object {
        private const val SAVE_KEY = "save_data"
        private const val BACKUP_KEY = "save_data_backup"
        private const val SAVE_VERSION = 1
        const val AUTO_SAVE_INTERVAL = 30f

        private val json = Json {
            prettyPrint = false
            ignoreUnknownKeys = true
            encodeDefaults = true
        }

        // Static save — canonical public API
        fun save(state: GameState, prefs: Preferences) {
            try {
                state.lastSaveTime = System.currentTimeMillis()
                val versionedSave = VersionedSave(SAVE_VERSION, state)
                val jsonString = json.encodeToString(versionedSave)
                if (prefs.contains(SAVE_KEY)) {
                    val existing = prefs.getString(SAVE_KEY, "")
                    if (existing.isNotEmpty()) prefs.putString(BACKUP_KEY, existing)
                }
                prefs.putString(SAVE_KEY, jsonString)
                prefs.flush()
            } catch (e: Exception) {
                println("SaveManager: save failed - ${e.message}")
            }
        }

        // Backward-compat alias
        fun saveGame(state: GameState, prefs: Preferences) = save(state, prefs)

        // Static load — canonical public API, returns null if no save exists
        fun load(prefs: Preferences): GameState? {
            return try {
                val jsonString = prefs.getString(SAVE_KEY, "")
                if (jsonString.isEmpty()) return null
                val jsonElement = json.parseToJsonElement(jsonString)
                if (jsonElement is JsonObject && jsonElement.containsKey("version")) {
                    val versionedSave = json.decodeFromString<VersionedSave>(jsonString)
                    versionedSave.state
                } else {
                    json.decodeFromString<GameState>(jsonString)
                }
            } catch (e: Exception) {
                println("SaveManager: load failed - ${e.message}")
                null
            }
        }

        // Backward-compat alias
        fun loadGame(prefs: Preferences): GameState? = load(prefs)
    }

    private var autoSaveTimer = 0f

    // Updates auto-save timer and triggers save if needed
    fun updateAutoSave(delta: Float, state: GameState) {
        autoSaveTimer += delta
        if (autoSaveTimer >= AUTO_SAVE_INTERVAL) {
            autoSaveTimer = 0f
            save(state)
        }
    }

    // Saves the game state to preferences
    fun save(state: GameState) {
        saveGame(state, prefs)
    }

    // Loads the game state from preferences
    fun load(): GameState? {
        return loadGame(prefs)
    }

    // Deletes all save data
    fun deleteSave() {
        prefs.remove(SAVE_KEY)
        prefs.remove(BACKUP_KEY)
        prefs.flush()
    }

    // Checks if a save file exists
    fun hasSave(): Boolean {
        return prefs.contains(SAVE_KEY) && prefs.getString(SAVE_KEY, "").isNotEmpty()
    }
}

// Wrapper for versioned save data
@kotlinx.serialization.Serializable
data class VersionedSave(
    val version: Int,
    val state: GameState
)
