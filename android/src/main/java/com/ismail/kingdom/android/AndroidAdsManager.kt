// PATH: android/src/main/java/com/ismail/kingdom/android/AndroidAdsManager.kt
package com.ismail.kingdom.android

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.ismail.kingdom.ads.AdsInterface
import com.ismail.kingdom.ads.RewardedAdType

// Android implementation of AdsInterface using Google Mobile Ads SDK
class AndroidAdsManager(private val activity: Activity) : AdsInterface {
    
    companion object {
        private const val TAG = "AndroidAdsManager"
        
        // Test ad unit IDs
        private const val REWARDED_AD_DOUBLE_OFFLINE = "ca-app-pub-3940256099942544/5224354917"
        private const val REWARDED_AD_SPEED_BOOST = "ca-app-pub-3940256099942544/5224354917"
        private const val INTERSTITIAL_AD_ID = "ca-app-pub-3940256099942544/1033173712"
        private const val BANNER_AD_ID = "ca-app-pub-3940256099942544/6300978111"
    }
    
    private var rewardedAdDoubleOffline: RewardedAd? = null
    private var rewardedAdSpeedBoost: RewardedAd? = null
    private var interstitialAd: InterstitialAd? = null
    private var bannerAdView: AdView? = null
    
    private var isLoadingDoubleOffline = false
    private var isLoadingSpeedBoost = false
    private var isLoadingInterstitial = false
    
    // Tracking for ad display rules
    private var offlineAdShownThisSession = false
    private var lastInterstitialTime = 0L
    private var doubleOfflineLoadAttempts = 0
    private val MAX_LOAD_ATTEMPTS = 2
    private val INTERSTITIAL_COOLDOWN_MS = 5 * 60 * 1000L // 5 minutes
    
    // Initializes Mobile Ads SDK
    fun initialize() {
        MobileAds.initialize(activity) { initializationStatus ->
            Log.d(TAG, "Mobile Ads SDK initialized: ${initializationStatus.adapterStatusMap}")
            
            // Load all ads after initialization
            loadRewardedAdDoubleOffline()
            loadRewardedAdSpeedBoost()
            loadInterstitialAd()
        }
    }
    
    // Loads rewarded ad for double offline earnings
    private fun loadRewardedAdDoubleOffline() {
        if (isLoadingDoubleOffline) return
        if (doubleOfflineLoadAttempts >= MAX_LOAD_ATTEMPTS) {
            Log.w(TAG, "Max load attempts reached for Double Offline ad")
            return
        }
        
        isLoadingDoubleOffline = true
        doubleOfflineLoadAttempts++
        val adRequest = AdRequest.Builder().build()
        
        RewardedAd.load(
            activity,
            REWARDED_AD_DOUBLE_OFFLINE,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d(TAG, "Double Offline rewarded ad loaded")
                    rewardedAdDoubleOffline = ad
                    isLoadingDoubleOffline = false
                    doubleOfflineLoadAttempts = 0 // Reset on success
                }
                
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e(TAG, "Double Offline rewarded ad failed to load (attempt $doubleOfflineLoadAttempts): ${loadAdError.message}")
                    rewardedAdDoubleOffline = null
                    isLoadingDoubleOffline = false
                    
                    // Try once more if first attempt failed
                    if (doubleOfflineLoadAttempts < MAX_LOAD_ATTEMPTS) {
                        activity.runOnUiThread {
                            android.os.Handler().postDelayed({
                                loadRewardedAdDoubleOffline()
                            }, 2000) // Wait 2s before retry
                        }
                    }
                }
            }
        )
    }
    
    // Loads rewarded ad for speed boost
    private fun loadRewardedAdSpeedBoost() {
        if (isLoadingSpeedBoost) return
        
        isLoadingSpeedBoost = true
        val adRequest = AdRequest.Builder().build()
        
        RewardedAd.load(
            activity,
            REWARDED_AD_SPEED_BOOST,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d(TAG, "Speed Boost rewarded ad loaded")
                    rewardedAdSpeedBoost = ad
                    isLoadingSpeedBoost = false
                }
                
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e(TAG, "Speed Boost rewarded ad failed to load: ${loadAdError.message}")
                    rewardedAdSpeedBoost = null
                    isLoadingSpeedBoost = false
                }
            }
        )
    }
    
    // Loads interstitial ad
    private fun loadInterstitialAd() {
        if (isLoadingInterstitial) return
        
        isLoadingInterstitial = true
        val adRequest = AdRequest.Builder().build()
        
        InterstitialAd.load(
            activity,
            INTERSTITIAL_AD_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "Interstitial ad loaded")
                    interstitialAd = ad
                    isLoadingInterstitial = false
                    
                    // Set callback for when ad is dismissed
                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            Log.d(TAG, "Interstitial ad dismissed")
                            interstitialAd = null
                            loadInterstitialAd() // Reload for next time
                        }
                        
                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            Log.e(TAG, "Interstitial ad failed to show: ${adError.message}")
                            interstitialAd = null
                        }
                    }
                }
                
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e(TAG, "Interstitial ad failed to load: ${loadAdError.message}")
                    interstitialAd = null
                    isLoadingInterstitial = false
                }
            }
        )
    }
    
    // Creates and loads banner ad
    fun createBannerAd(container: FrameLayout) {
        bannerAdView = AdView(activity).apply {
            adUnitId = BANNER_AD_ID
            setAdSize(AdSize.BANNER)
            
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    Log.d(TAG, "Banner ad loaded")
                }
                
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e(TAG, "Banner ad failed to load: ${loadAdError.message}")
                }
            }
        }
        
        container.addView(bannerAdView)
        
        val adRequest = AdRequest.Builder().build()
        bannerAdView?.loadAd(adRequest)
    }
    
    override fun showRewardedAd(
        type: RewardedAdType,
        onRewarded: (RewardedAdType) -> Unit,
        onFailed: () -> Unit
    ) {
        // Check session limit for offline earnings ad
        if (type == RewardedAdType.DOUBLE_OFFLINE_EARNINGS && offlineAdShownThisSession) {
            Log.w(TAG, "Offline earnings ad already shown this session")
            onFailed()
            return
        }
        
        val ad = when (type) {
            RewardedAdType.DOUBLE_OFFLINE_EARNINGS -> rewardedAdDoubleOffline
            RewardedAdType.SPEED_BOOST_4X -> rewardedAdSpeedBoost
        }
        
        if (ad == null) {
            Log.w(TAG, "Rewarded ad not ready for type: $type")
            onFailed()
            return
        }
        
        var rewardGranted = false
        
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Rewarded ad dismissed, reward granted: $rewardGranted")
                
                // Only call onFailed if reward was not granted (user exited early)
                if (!rewardGranted) {
                    onFailed()
                }
                
                // Mark offline ad as shown for this session
                if (type == RewardedAdType.DOUBLE_OFFLINE_EARNINGS) {
                    offlineAdShownThisSession = true
                }
                
                // Clear the ad reference and reload
                when (type) {
                    RewardedAdType.DOUBLE_OFFLINE_EARNINGS -> {
                        rewardedAdDoubleOffline = null
                        loadRewardedAdDoubleOffline()
                    }
                    RewardedAdType.SPEED_BOOST_4X -> {
                        rewardedAdSpeedBoost = null
                        loadRewardedAdSpeedBoost()
                    }
                }
            }
            
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e(TAG, "Rewarded ad failed to show: ${adError.message}")
                onFailed()
                
                // Clear the ad reference
                when (type) {
                    RewardedAdType.DOUBLE_OFFLINE_EARNINGS -> rewardedAdDoubleOffline = null
                    RewardedAdType.SPEED_BOOST_4X -> rewardedAdSpeedBoost = null
                }
            }
        }
        
        ad.show(activity) { rewardItem ->
            Log.d(TAG, "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
            rewardGranted = true
            onRewarded(type)
        }
    }
    
    override fun showInterstitialAd(onComplete: () -> Unit) {
        // Check cooldown (max once per 5 minutes)
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastInterstitialTime < INTERSTITIAL_COOLDOWN_MS) {
            Log.w(TAG, "Interstitial ad on cooldown")
            onComplete()
            return
        }
        
        val ad = interstitialAd
        
        if (ad == null) {
            Log.w(TAG, "Interstitial ad not ready, continuing without ad")
            onComplete()
            return
        }
        
        lastInterstitialTime = currentTime
        
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Interstitial ad dismissed")
                interstitialAd = null
                loadInterstitialAd()
                onComplete()
            }
            
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e(TAG, "Interstitial ad failed to show: ${adError.message}")
                interstitialAd = null
                onComplete()
            }
        }
        
        ad.show(activity)
    }
    
    override fun showBannerAd(show: Boolean) {
        activity.runOnUiThread {
            if (show && bannerAdView == null) {
                Log.w(TAG, "Banner ad not initialized, cannot show")
                return@runOnUiThread
            }
            bannerAdView?.visibility = if (show) View.VISIBLE else View.GONE
            Log.d(TAG, "Banner ad visibility: ${if (show) "VISIBLE" else "GONE"}")
        }
    }
    
    override fun isAdReady(type: RewardedAdType): Boolean {
        return when (type) {
            RewardedAdType.DOUBLE_OFFLINE_EARNINGS -> rewardedAdDoubleOffline != null
            RewardedAdType.SPEED_BOOST_4X -> rewardedAdSpeedBoost != null
        }
    }
    
    override fun isInterstitialReady(): Boolean {
        return interstitialAd != null
    }
    
    // Resets session tracking (call on app restart)
    fun resetSession() {
        offlineAdShownThisSession = false
        doubleOfflineLoadAttempts = 0
    }
    
    // Returns whether offline ad was shown this session
    fun wasOfflineAdShownThisSession(): Boolean = offlineAdShownThisSession
    
    // Cleans up ads
    fun destroy() {
        bannerAdView?.destroy()
        bannerAdView = null
        rewardedAdDoubleOffline = null
        rewardedAdSpeedBoost = null
        interstitialAd = null
    }
}
