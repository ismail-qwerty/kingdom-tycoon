// PATH: core/src/main/java/com/ismail/kingdom/AdsController.kt
package com.ismail.kingdom

// Platform-agnostic interface for rewarded ad integration
interface AdsController {
    // Returns true if a rewarded ad is loaded and ready to show
    fun isRewardedAdReady(): Boolean

    // Shows the rewarded ad; calls onRewarded when the user earns the reward
    fun showRewardedAd(onRewarded: () -> Unit)

    // Loads the next rewarded ad in advance
    fun loadRewardedAd()
}
