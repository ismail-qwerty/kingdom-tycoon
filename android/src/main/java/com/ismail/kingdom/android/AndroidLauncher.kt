// PATH: android/src/main/java/com/ismail/kingdom/android/AndroidLauncher.kt
package com.ismail.kingdom.android

import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.ismail.kingdom.KingdomTycoonGame

// Android launcher with crash protection
class AndroidLauncher : AndroidApplication() {
    
    private lateinit var game: KingdomTycoonGame
    private var adsManager: AndroidAdsManager? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            // Keep screen on during gameplay
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            
            // Lock to portrait orientation
            requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            
            // Configure LibGDX
            val config = AndroidApplicationConfiguration().apply {
                useImmersiveMode = true
                useAccelerometer = false
                useCompass = false
                useGyroscope = false
            }
            
            // Initialize ads manager
            adsManager = AndroidAdsManager(this)
            adsManager?.initialize()
            
            // Initialize game with ads
            game = KingdomTycoonGame(adsManager)
            initialize(game, config)
            
        } catch (e: Exception) {
            // Show error and close
            android.util.Log.e("KingdomTycoon", "Startup error: ${e.message}", e)
            Toast.makeText(this, "Error starting game: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
    
    override fun onPause() {
        super.onPause()
        try {
            if (::game.isInitialized) {
                game.gameEngine.save()
            }
        } catch (e: Exception) {
            android.util.Log.e("KingdomTycoon", "Pause error: ${e.message}")
        }
    }
    
    override fun onResume() {
        super.onResume()
    }
    
    override fun onDestroy() {
        try {
            if (::game.isInitialized) {
                game.gameEngine.save()
            }
            adsManager?.destroy()
        } catch (e: Exception) {
            android.util.Log.e("KingdomTycoon", "Destroy error: ${e.message}")
        }
        super.onDestroy()
    }
}
