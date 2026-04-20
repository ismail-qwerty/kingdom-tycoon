// PATH: core/src/main/java/com/ismail/kingdom/AudioSystem.kt
package com.ismail.kingdom

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.utils.Disposable
import com.ismail.kingdom.data.SettingsManager

// Sound effect identifiers
enum class SoundId {
    TAP,                // Coin clink on tap
    TAP_COMBO,          // Higher-pitched combo clink
    PURCHASE,           // Ka-ching register sound
    MILESTONE,          // Triumphant fanfare
    PRESTIGE,           // Epic orchestral hit
    ADVISOR_HIRED,      // Approval chime
    QUEST_COMPLETE,     // Success fanfare
    EVENT_START,        // Dramatic announcement
    UI_CLICK,           // Soft button click
    MAP_REVEAL          // Discovery sound
}

// Audio system managing music and sound effects
object AudioSystem : Disposable {
    
    // Music tracks
    private val musicTracks = mutableMapOf<Int, Music>()
    private var currentMusic: Music? = null
    private var currentEra: Int = 0
    
    // Sound effects
    private val soundEffects = mutableMapOf<SoundId, Sound>()
    
    // Crossfade state
    private var isCrossfading = false
    private var crossfadeTimer = 0f
    private var crossfadeDuration = 2.0f
    private var fadeOutMusic: Music? = null
    private var fadeInMusic: Music? = null
    private var fadeOutStartVolume = 1.0f
    
    // Combo pitch scaling
    private var currentComboPitch = 1.0f
    private val MIN_COMBO_PITCH = 1.0f
    private val MAX_COMBO_PITCH = 1.5f
    
    // Initializes audio system and loads all assets
    fun initialize() {
        loadMusicTracks()
        loadSoundEffects()
        
        Gdx.app.log("AudioSystem", "Audio system initialized")
    }
    
    // Loads all music tracks
    private fun loadMusicTracks() {
        try {
            for (era in 1..5) {
                val filename = "audio/music/era${era}_theme.ogg"
                if (Gdx.files.internal(filename).exists()) {
                    val music = Gdx.audio.newMusic(Gdx.files.internal(filename))
                    music.isLooping = true
                    musicTracks[era] = music
                    Gdx.app.log("AudioSystem", "Loaded music: $filename")
                } else {
                    Gdx.app.log("AudioSystem", "Music file not found: $filename")
                }
            }
        } catch (e: Exception) {
            Gdx.app.error("AudioSystem", "Failed to load music tracks: ${e.message}")
        }
    }
    
    // Loads all sound effects
    private fun loadSoundEffects() {
        val soundFiles = mapOf(
            SoundId.TAP to "audio/sfx/sfx_tap.ogg",
            SoundId.TAP_COMBO to "audio/sfx/sfx_tap_combo.ogg",
            SoundId.PURCHASE to "audio/sfx/sfx_purchase.ogg",
            SoundId.MILESTONE to "audio/sfx/sfx_milestone.ogg",
            SoundId.PRESTIGE to "audio/sfx/sfx_prestige.ogg",
            SoundId.ADVISOR_HIRED to "audio/sfx/sfx_advisor_hired.ogg",
            SoundId.QUEST_COMPLETE to "audio/sfx/sfx_quest_complete.ogg",
            SoundId.EVENT_START to "audio/sfx/sfx_event_start.ogg",
            SoundId.UI_CLICK to "audio/sfx/sfx_ui_click.ogg",
            SoundId.MAP_REVEAL to "audio/sfx/sfx_map_reveal.ogg"
        )
        
        try {
            for ((soundId, filename) in soundFiles) {
                if (Gdx.files.internal(filename).exists()) {
                    val sound = Gdx.audio.newSound(Gdx.files.internal(filename))
                    soundEffects[soundId] = sound
                    Gdx.app.log("AudioSystem", "Loaded sound: $filename")
                } else {
                    Gdx.app.log("AudioSystem", "Sound file not found: $filename")
                }
            }
        } catch (e: Exception) {
            Gdx.app.error("AudioSystem", "Failed to load sound effects: ${e.message}")
        }
    }
    
    // Plays music for specified era with crossfade
    fun playEraMusic(eraId: Int) {
        if (!SettingsManager.musicEnabled) {
            return
        }
        
        if (eraId == currentEra && currentMusic?.isPlaying == true) {
            return // Already playing this era's music
        }
        
        val newMusic = musicTracks[eraId]
        if (newMusic == null) {
            Gdx.app.error("AudioSystem", "Music for era $eraId not found")
            return
        }
        
        if (currentMusic == null) {
            // No music playing, start immediately
            currentMusic = newMusic
            currentEra = eraId
            newMusic.volume = SettingsManager.musicVolume
            newMusic.play()
            Gdx.app.log("AudioSystem", "Started music for era $eraId")
        } else {
            // Crossfade from current to new
            startCrossfade(newMusic, eraId)
        }
    }
    
    // Starts crossfade between music tracks
    private fun startCrossfade(newMusic: Music, newEra: Int) {
        isCrossfading = true
        crossfadeTimer = 0f
        fadeOutMusic = currentMusic
        fadeInMusic = newMusic
        fadeOutStartVolume = currentMusic?.volume ?: SettingsManager.musicVolume
        
        // Start new music at 0 volume
        fadeInMusic?.volume = 0f
        fadeInMusic?.play()
        
        currentMusic = newMusic
        currentEra = newEra
        
        Gdx.app.log("AudioSystem", "Crossfading to era $newEra music")
    }
    
    // Updates crossfade (call every frame)
    fun update(delta: Float) {
        if (isCrossfading) {
            crossfadeTimer += delta
            val progress = (crossfadeTimer / crossfadeDuration).coerceIn(0f, 1f)
            
            // Fade out old music
            fadeOutMusic?.volume = fadeOutStartVolume * (1f - progress)
            
            // Fade in new music
            fadeInMusic?.volume = SettingsManager.musicVolume * progress
            
            // Complete crossfade
            if (progress >= 1f) {
                fadeOutMusic?.stop()
                fadeOutMusic = null
                fadeInMusic = null
                isCrossfading = false
                Gdx.app.log("AudioSystem", "Crossfade complete")
            }
        }
    }
    
    // Sets music volume (0.0 to 1.0)
    fun setMusicVolume(volume: Float) {
        val clampedVolume = volume.coerceIn(0f, 1f)
        currentMusic?.volume = clampedVolume
        
        if (isCrossfading) {
            // Update target volume for fade in
            fadeInMusic?.volume = clampedVolume * (crossfadeTimer / crossfadeDuration)
        }
    }
    
    // Pauses music (on app backgrounding)
    fun pauseMusic() {
        currentMusic?.pause()
        Gdx.app.log("AudioSystem", "Music paused")
    }
    
    // Resumes music
    fun resumeMusic() {
        if (SettingsManager.musicEnabled) {
            currentMusic?.play()
            Gdx.app.log("AudioSystem", "Music resumed")
        }
    }
    
    // Stops music
    fun stopMusic() {
        currentMusic?.stop()
        fadeOutMusic?.stop()
        fadeInMusic?.stop()
        isCrossfading = false
        Gdx.app.log("AudioSystem", "Music stopped")
    }
    
    // Plays sound effect
    fun play(soundId: SoundId) {
        if (!SettingsManager.soundEnabled) {
            return
        }
        
        val sound = soundEffects[soundId]
        if (sound != null) {
            sound.play(1.0f) // Full volume
        } else {
            Gdx.app.error("AudioSystem", "Sound $soundId not found")
        }
    }
    
    // Plays sound effect with custom volume and pitch
    fun play(soundId: SoundId, volume: Float, pitch: Float) {
        if (!SettingsManager.soundEnabled) {
            return
        }
        
        val sound = soundEffects[soundId]
        if (sound != null) {
            sound.play(volume.coerceIn(0f, 1f), pitch.coerceIn(0.5f, 2.0f), 0f)
        } else {
            Gdx.app.error("AudioSystem", "Sound $soundId not found")
        }
    }
    
    // Plays tap sound with combo pitch scaling
    fun playTapSound(comboCount: Int) {
        if (!SettingsManager.soundEnabled) {
            return
        }
        
        // Calculate pitch based on combo (1.0 → 1.5 over 20 taps)
        val comboPitch = if (comboCount > 1) {
            val progress = (comboCount - 1).toFloat() / 19f // 0 to 1 over 20 taps
            MIN_COMBO_PITCH + (MAX_COMBO_PITCH - MIN_COMBO_PITCH) * progress.coerceIn(0f, 1f)
        } else {
            MIN_COMBO_PITCH
        }
        
        currentComboPitch = comboPitch
        
        // Use combo sound if combo active, otherwise normal tap
        val soundId = if (comboCount > 4) SoundId.TAP_COMBO else SoundId.TAP
        play(soundId, 1.0f, comboPitch)
    }
    
    // Plays UI click sound
    fun playUIClick() {
        play(SoundId.UI_CLICK)
    }
    
    // Plays purchase sound
    fun playPurchase() {
        play(SoundId.PURCHASE)
    }
    
    // Plays milestone sound
    fun playMilestone() {
        play(SoundId.MILESTONE)
    }
    
    // Plays prestige sound
    fun playPrestige() {
        play(SoundId.PRESTIGE)
    }
    
    // Plays advisor hired sound
    fun playAdvisorHired() {
        play(SoundId.ADVISOR_HIRED)
    }
    
    // Plays quest complete sound
    fun playQuestComplete() {
        play(SoundId.QUEST_COMPLETE)
    }
    
    // Plays event start sound
    fun playEventStart() {
        play(SoundId.EVENT_START)
    }
    
    // Plays map reveal sound
    fun playMapReveal() {
        play(SoundId.MAP_REVEAL)
    }
    
    // Gets current combo pitch (for debugging)
    fun getCurrentComboPitch(): Float = currentComboPitch
    
    // Disposes all audio resources
    override fun dispose() {
        // Dispose music
        for (music in musicTracks.values) {
            music.dispose()
        }
        musicTracks.clear()
        
        // Dispose sounds
        for (sound in soundEffects.values) {
            sound.dispose()
        }
        soundEffects.clear()
        
        currentMusic = null
        fadeOutMusic = null
        fadeInMusic = null
        
        Gdx.app.log("AudioSystem", "Audio system disposed")
    }
}
