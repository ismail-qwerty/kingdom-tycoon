// PATH: core/src/main/java/com/ismail/kingdom/data/SessionManager.kt
package com.ismail.kingdom.data

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import java.util.*

// Manages session state and tracking
object SessionManager {
    
    private const val PREFS_NAME = "kingdom_session"
    private const val KEY_FIRST_LAUNCH = "first_launch_timestamp"
    private const val KEY_LAST_LAUNCH = "last_launch_date"
    private const val KEY_SESSION_COUNT = "session_count"
    
    private var prefs: Preferences? = null
    
    // Session memory flags (reset each launch)
    var hasShownOfflineEarnings = false
        private set
    
    // Initializes session manager
    fun initialize() {
        prefs = Gdx.app.getPreferences(PREFS_NAME)
        
        // Track this session
        trackSession()
    }
    
    // Checks if this is the first launch ever
    fun isFirstLaunchEver(): Boolean {
        val firstLaunchTimestamp = prefs?.getLong(KEY_FIRST_LAUNCH, 0L) ?: 0L
        return firstLaunchTimestamp == 0L
    }
    
    // Checks if this is the first launch today
    fun isFirstLaunchToday(): Boolean {
        val lastLaunchDate = prefs?.getString(KEY_LAST_LAUNCH, "") ?: ""
        val todayDate = getTodayDateString()
        
        return lastLaunchDate != todayDate
    }
    
    // Tracks current session
    fun trackSession() {
        val currentTime = System.currentTimeMillis()
        val todayDate = getTodayDateString()
        
        // Set first launch timestamp if not set
        if (isFirstLaunchEver()) {
            prefs?.putLong(KEY_FIRST_LAUNCH, currentTime)
        }
        
        // Update last launch date
        prefs?.putString(KEY_LAST_LAUNCH, todayDate)
        
        // Increment session count
        val sessionCount = prefs?.getInteger(KEY_SESSION_COUNT, 0) ?: 0
        prefs?.putInteger(KEY_SESSION_COUNT, sessionCount + 1)
        
        prefs?.flush()
        
        Gdx.app.log("SessionManager", "Session tracked: count=${sessionCount + 1}, firstToday=${isFirstLaunchToday()}")
    }
    
    // Marks offline earnings as shown for this session
    fun markOfflineEarningsShown() {
        hasShownOfflineEarnings = true
    }
    
    // Gets total session count
    fun getSessionCount(): Int {
        return prefs?.getInteger(KEY_SESSION_COUNT, 0) ?: 0
    }
    
    // Gets days since first launch
    fun getDaysSinceFirstLaunch(): Int {
        val firstLaunchTimestamp = prefs?.getLong(KEY_FIRST_LAUNCH, 0L) ?: 0L
        if (firstLaunchTimestamp == 0L) return 0
        
        val currentTime = System.currentTimeMillis()
        val daysSince = ((currentTime - firstLaunchTimestamp) / (1000 * 60 * 60 * 24)).toInt()
        return daysSince
    }
    
    // Gets today's date string (YYYY-MM-DD)
    private fun getTodayDateString(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // 0-indexed
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        
        return String.format("%04d-%02d-%02d", year, month, day)
    }
    
    // Resets session data (for testing)
    fun resetSessionData() {
        prefs?.clear()
        prefs?.flush()
        hasShownOfflineEarnings = false
        Gdx.app.log("SessionManager", "Session data reset")
    }
}
