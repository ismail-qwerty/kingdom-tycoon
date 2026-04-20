// PATH: android/src/main/java/com/ismail/kingdom/android/AndroidAdsController.kt
package com.ismail.kingdom.android

import android.app.Activity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.ismail.kingdom.ads.AdsInterface
import com.ismail.kingdom.ads.RewardedAdType

// Android AdMob implementation of AdsInterface
class AndroidAdsController(private val activity: Activity) : AdsInterface {

    private var rewardedAd: RewardedAd? = null

    // AdMob test unit ID — replace with real ID before release
    private val AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"

    init {
        // Initialises the Mobile Ads SDK and loads the first ad
        MobileAds.initialize(activity)
        loadRewardedAd()
    }

    // Shows a rewarded ad of the specified type
    override fun showRewardedAd(type: RewardedAdType, onRewarded: (RewardedAdType) -> Unit, onFailed: () -> Unit) {
        val ad = rewardedAd ?: run { onFailed(); return }
        activity.runOnUiThread {
            ad.show(activity) { _ ->
                onRewarded(type)
                rewardedAd = null
                loadRewardedAd()
            }
        }
    }

    // Shows an interstitial ad (full-screen)
    override fun showInterstitialAd(onComplete: () -> Unit) {
        onComplete()
    }

    // Shows or hides banner ad
    override fun showBannerAd(show: Boolean) {}

    // Checks if a rewarded ad is ready to show
    override fun isAdReady(type: RewardedAdType): Boolean = rewardedAd != null

    // Checks if interstitial ad is ready
    override fun isInterstitialReady(): Boolean = false

    // Loads the next rewarded ad from AdMob
    private fun loadRewardedAd() {
        val request = AdRequest.Builder().build()
        RewardedAd.load(activity, AD_UNIT_ID, request, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) { rewardedAd = ad }
            override fun onAdFailedToLoad(error: LoadAdError) { rewardedAd = null }
        })
    }
}
