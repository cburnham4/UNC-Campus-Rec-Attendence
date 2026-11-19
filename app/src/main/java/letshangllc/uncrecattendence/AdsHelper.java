package letshangllc.uncrecattendence;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowMetrics;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * AdMob banner helper with UMP consent.
 * Amazon Mobile Ads was removed (dead / unencrypted traffic).
 */
public class AdsHelper {
    private static final String TAG = "AdsHelper";
    private static final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);

    private final String admobUnitId;
    private final Activity activity;
    private final View rootView;

    private AdView admobAdView;
    private ConsentInformation consentInformation;

    public AdsHelper(View rootView, String admobUnitId, Activity activity) {
        this.rootView = rootView;
        this.admobUnitId = admobUnitId;
        this.activity = activity;
    }

    public void setUpAds() {
        ConsentRequestParameters.Builder paramsBuilder = new ConsentRequestParameters.Builder()
                .setTagForUnderAgeOfConsent(false);

        if (BuildConfig.DEBUG) {
            ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(activity)
                    .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                    .addTestDeviceHashedId(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
            paramsBuilder.setConsentDebugSettings(debugSettings);
        }

        ConsentRequestParameters params = paramsBuilder.build();
        consentInformation = UserMessagingPlatform.getConsentInformation(activity);
        consentInformation.requestConsentInfoUpdate(
                activity,
                params,
                () -> UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                        activity,
                        loadAndShowError -> {
                            if (loadAndShowError != null) {
                                Log.w(TAG, "Consent form error: " + loadAndShowError.getMessage());
                            }
                            if (consentInformation.canRequestAds()) {
                                initializeMobileAds();
                            }
                        }
                ),
                requestConsentError -> {
                    Log.w(TAG, "Consent request error: " + requestConsentError.getMessage());
                    if (consentInformation.canRequestAds()) {
                        initializeMobileAds();
                    }
                }
        );

        if (consentInformation.canRequestAds()) {
            initializeMobileAds();
        }
    }

    private void initializeMobileAds() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            activity.runOnUiThread(this::loadBannerAd);
            return;
        }

        new Thread(() -> MobileAds.initialize(activity, initializationStatus ->
                activity.runOnUiThread(this::loadBannerAd)
        )).start();
    }

    private void loadBannerAd() {
        ViewGroup adViewContainer = rootView.findViewById(R.id.ad_layout);
        if (adViewContainer == null) {
            Log.e(TAG, "Ad container view not found");
            return;
        }

        if (admobAdView != null) {
            adViewContainer.removeAllViews();
            admobAdView.destroy();
        }

        admobAdView = new AdView(activity);
        admobAdView.setAdUnitId(admobUnitId);
        admobAdView.setAdSize(getAdaptiveAdSize());

        adViewContainer.removeAllViews();
        adViewContainer.addView(admobAdView);
        admobAdView.loadAd(new AdRequest.Builder().build());
    }

    private AdSize getAdaptiveAdSize() {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        int adWidthPixels = displayMetrics.widthPixels;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            adWidthPixels = windowMetrics.getBounds().width();
        }

        ViewGroup adViewContainer = rootView.findViewById(R.id.ad_layout);
        if (adViewContainer != null && adViewContainer.getWidth() > 0) {
            adWidthPixels = adViewContainer.getWidth();
        }

        int adWidth = Math.max(1, (int) (adWidthPixels / displayMetrics.density));
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }

    public void onPause() {
        if (admobAdView != null) {
            admobAdView.pause();
        }
    }

    public void onResume() {
        if (admobAdView != null) {
            admobAdView.resume();
        }
    }

    public void onDestroy() {
        if (admobAdView != null) {
            admobAdView.destroy();
            admobAdView = null;
        }
    }
}
