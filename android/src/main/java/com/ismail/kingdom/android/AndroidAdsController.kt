// PATH: android/src/main/java/com/ismail/kingdom/android/AndroidAdsController.kt
package com.ismail.kingdom.android

import android.app.Activity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.ismail.kingdom.AdsController

// Android AdMob implementation of AdsController
class AndroidAdsController(private val activity: Activity) : AdsController {

    private var rewardedAd: RewardedAd? = null
    private var pendingReward: (() -> Unit)? = null

    // AdMob test unit ID — replace with real ID before release
    private val AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"

    init {
        // Initialises the Mobile Ads SDK and loads the first ad
        MobileAds.initialize(activity)
        loadRewardedAd()
    }

    // Returns true when a rewarded ad is loaded and ready
    override fun isRewardedAdReady(): Boolean = rewardedAd != null

    // Shows the rewarded ad if ready; stores callback for when reward is granted
    override fun showRewardedAd(onRewarded: () -> Unit) {
        val ad = rewardedAd ?: return
        pendingReward = onRewarded
        activity.runOnUiThread {
            ad.show(activity) { _ ->
                pendingReward?.invoke()
                pendingReward = null
                rewardedAd = null
                loadRewardedAd()
            }
        }
    }

    // Loads the next rewarded ad from AdMob
    override fun loadRewardedAd() {
        val request = AdRequest.Builder().build()
        RewardedAd.load(activity, AD_UNIT_ID, request, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) { rewardedAd = ad }
            override fun onAdFailedToLoad(error: LoadAdError) { rewardedAd = null }
        })
    }
}
