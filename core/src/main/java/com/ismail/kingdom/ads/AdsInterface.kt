// PATH: core/src/main/java/com/ismail/kingdom/ads/AdsInterface.kt
package com.ismail.kingdom.ads

// Types of rewarded ads available
enum class RewardedAdType {
    DOUBLE_OFFLINE_EARNINGS,  // 2x offline gold
    SPEED_BOOST_4X            // 4x income for 120 seconds
}

// Platform-independent ads interface
interface AdsInterface {
    
    // Shows a rewarded ad of the specified type
    fun showRewardedAd(
        type: RewardedAdType,
        onRewarded: (RewardedAdType) -> Unit,
        onFailed: () -> Unit
    )
    
    // Shows an interstitial ad (full-screen)
    fun showInterstitialAd(onComplete: () -> Unit)
    
    // Shows or hides banner ad
    fun showBannerAd(show: Boolean)
    
    // Checks if a rewarded ad is ready to show
    fun isAdReady(type: RewardedAdType): Boolean
    
    // Checks if interstitial ad is ready
    fun isInterstitialReady(): Boolean
}
