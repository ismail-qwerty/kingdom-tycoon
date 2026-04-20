// PATH: core/src/main/java/com/ismail/kingdom/ads/AdManager.kt
package com.ismail.kingdom.ads

import com.ismail.kingdom.models.GameState

// Manages ad rewards and active boost timers
class AdManager(private val adsInterface: AdsInterface?) {
    
    var speedBoostActive: Boolean = false
        private set
    
    var speedBoostRemainingSeconds: Float = 0f
        private set
    
    private val SPEED_BOOST_DURATION = 120f // 2 minutes
    private val SPEED_BOOST_MULTIPLIER = 4.0
    
    // Updates speed boost timer
    fun update(delta: Float) {
        if (speedBoostActive) {
            speedBoostRemainingSeconds -= delta
            
            if (speedBoostRemainingSeconds <= 0f) {
                speedBoostActive = false
                speedBoostRemainingSeconds = 0f
            }
        }
    }
    
    // Requests speed boost ad
    fun requestSpeedBoost(state: GameState, onSuccess: () -> Unit, onFailed: () -> Unit) {
        if (adsInterface == null) {
            onFailed()
            return
        }
        
        if (speedBoostActive) {
            onFailed()
            return
        }
        
        if (!adsInterface.isAdReady(RewardedAdType.SPEED_BOOST_4X)) {
            onFailed()
            return
        }
        
        adsInterface.showRewardedAd(
            type = RewardedAdType.SPEED_BOOST_4X,
            onRewarded = { type ->
                activateSpeedBoost()
                onSuccess()
            },
            onFailed = {
                onFailed()
            }
        )
    }
    
    // Activates speed boost
    private fun activateSpeedBoost() {
        speedBoostActive = true
        speedBoostRemainingSeconds = SPEED_BOOST_DURATION
    }
    
    // Gets speed boost multiplier (1.0 if not active, 4.0 if active)
    fun getSpeedBoostMultiplier(): Double {
        return if (speedBoostActive) SPEED_BOOST_MULTIPLIER else 1.0
    }
    
    // Requests double offline earnings ad
    fun requestDoubleOffline(offlineGold: Double, onResult: (Double) -> Unit) {
        if (adsInterface == null) {
            onResult(offlineGold)
            return
        }
        
        if (!adsInterface.isAdReady(RewardedAdType.DOUBLE_OFFLINE_EARNINGS)) {
            onResult(offlineGold)
            return
        }
        
        adsInterface.showRewardedAd(
            type = RewardedAdType.DOUBLE_OFFLINE_EARNINGS,
            onRewarded = { type ->
                onResult(offlineGold * 2.0)
            },
            onFailed = {
                onResult(offlineGold)
            }
        )
    }
    
    // Triggers interstitial ad on era transition
    fun onEraTransition(onComplete: () -> Unit) {
        if (adsInterface == null) {
            onComplete()
            return
        }
        
        if (!adsInterface.isInterstitialReady()) {
            onComplete()
            return
        }
        
        adsInterface.showInterstitialAd(onComplete)
    }
    
    // Checks if speed boost ad is available
    fun isSpeedBoostAvailable(): Boolean {
        return adsInterface?.isAdReady(RewardedAdType.SPEED_BOOST_4X) == true && !speedBoostActive
    }
    
    // Checks if double offline ad is available
    fun isDoubleOfflineAvailable(): Boolean {
        return adsInterface?.isAdReady(RewardedAdType.DOUBLE_OFFLINE_EARNINGS) == true
    }
    
    // Gets formatted time remaining for speed boost
    fun getSpeedBoostTimeRemaining(): String {
        if (!speedBoostActive) return "0:00"
        
        val minutes = (speedBoostRemainingSeconds / 60).toInt()
        val seconds = (speedBoostRemainingSeconds % 60).toInt()
        
        return "$minutes:${seconds.toString().padStart(2, '0')}"
    }
    
    // Shows or hides banner ad
    fun showBanner(show: Boolean) {
        adsInterface?.showBannerAd(show)
    }
}
