// PATH: android/src/main/java/com/ismail/kingdom/android/AppRatingPrompt.kt
package com.ismail.kingdom.android

import android.app.Activity
import android.content.Context
import com.google.android.play.core.review.ReviewManagerFactory
import com.ismail.kingdom.data.SessionManager
import com.ismail.kingdom.models.GameState

// Manages app rating prompt using Google Play In-App Review API
class AppRatingPrompt(private val activity: Activity) {
    
    private val prefs = activity.getSharedPreferences("kingdom_rating", Context.MODE_PRIVATE)
    private val RATING_SHOWN_KEY = "rating_shown"
    
    private val MIN_PRESTIGE_COUNT = 3
    private val MIN_SESSION_COUNT = 10
    
    // Checks if rating prompt should be shown and displays it
    fun checkAndShowRating(gameState: GameState) {
        // Only show once
        if (hasShownRating()) {
            return
        }
        
        // Check requirements
        val prestigeCount = gameState.prestigeLayer
        val sessionCount = SessionManager.getSessionCount()
        
        if (prestigeCount >= MIN_PRESTIGE_COUNT && sessionCount >= MIN_SESSION_COUNT) {
            showRatingPrompt()
        }
    }
    
    // Shows Google Play In-App Review prompt
    private fun showRatingPrompt() {
        val reviewManager = ReviewManagerFactory.create(activity)
        
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                val flow = reviewManager.launchReviewFlow(activity, reviewInfo)
                
                flow.addOnCompleteListener {
                    // Mark as shown regardless of whether user rated
                    markRatingShown()
                }
            }
        }
    }
    
    // Checks if rating has been shown before
    private fun hasShownRating(): Boolean {
        return prefs.getBoolean(RATING_SHOWN_KEY, false)
    }
    
    // Marks rating as shown
    private fun markRatingShown() {
        prefs.edit().putBoolean(RATING_SHOWN_KEY, true).apply()
    }
    
    // Resets rating flag (for testing)
    fun resetRatingFlag() {
        prefs.edit().putBoolean(RATING_SHOWN_KEY, false).apply()
    }
}
