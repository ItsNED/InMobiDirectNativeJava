package com.example.inmobidirectnativejava;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiNative;
import com.inmobi.ads.listeners.VideoEventListener;
import com.inmobi.ads.listeners.NativeAdEventListener;
import com.inmobi.sdk.InMobiSdk;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "Inmobi";

    private ViewGroup mContainer;

    private InMobiNative nativeAd;

    private SwipeRefreshLayout refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSDK();

        initView();

        createNativeAds();

        loadAd();

        Toast.makeText(MainActivity.this, "SWIPE DOWN TO REFRESH.", Toast.LENGTH_SHORT).show();

    }

    private void initView(){
        Log.d(TAG, "initView");
        refresh = findViewById(R.id.refresh);
        refresh.setOnRefreshListener(this);
        mContainer = (ViewGroup)findViewById(R.id.container);
    }



    private void initSDK(){
        Log.d(TAG, "initSDK");
        JSONObject consentObject = new JSONObject();
        try {
            // Provide correct consent value to sdk which is obtained by User
            consentObject.put(InMobiSdk.IM_GDPR_CONSENT_AVAILABLE, true);
            // Provide 0 if GDPR is not applicable and 1 if applicable
            consentObject.put("gdpr", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        InMobiSdk.init(this, "4eff932232044a2d8ee97ebd6f669489", consentObject);
        InMobiSdk.setLogLevel(InMobiSdk.LogLevel.DEBUG);

    }

    private View loadAdIntoView(@NonNull final InMobiNative inMobiNative) {
        Log.d(TAG, "loadAdIntoView");
        View adView = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_ad, null);

        ImageView icon = (ImageView) adView.findViewById(R.id.adIcon);
        TextView title = (TextView) adView.findViewById(R.id.adTitle);
        TextView description = (TextView) adView.findViewById(R.id.adDescription);
        Button action = (Button) adView.findViewById(R.id.adAction);
        FrameLayout content = (FrameLayout) adView.findViewById(R.id.adContent);
        RatingBar ratingBar = (RatingBar) adView.findViewById(R.id.adRating);


        Picasso.with(MainActivity.this)
                .load(inMobiNative.getAdIconUrl())
                .into(icon);
        title.setText(inMobiNative.getAdTitle());
        description.setText(inMobiNative.getAdDescription());
        action.setText(inMobiNative.getAdCtaText());


        DisplayMetrics displayMetrics = new DisplayMetrics();
        MainActivity.this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        content.addView(inMobiNative.getPrimaryViewOfWidth(MainActivity.this, content, mContainer, displayMetrics.widthPixels));

        float rating = inMobiNative.getAdRating();
        if (rating != 0) {
            ratingBar.setRating(rating);
        }
        ratingBar.setVisibility(rating != 0 ? View.VISIBLE : View.GONE);

        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nativeAd.reportAdClickAndOpenLandingPage();
            }
        });

        return adView;
    }

    NativeAdEventListener adListener = new NativeAdEventListener() {
        @Override
        public void onAdLoadSucceeded(InMobiNative inMobiNative) {
            super.onAdLoadSucceeded(inMobiNative);
            Log.d(TAG, "onAdLoadSucceeded: ");
            View view = loadAdIntoView(inMobiNative);
            if(view == null){
                Log.d(TAG, "Could not render");
            } else{
              mContainer.addView(view);
            }
        }

        @Override
        public void onAdLoadFailed(InMobiNative inMobiNative, InMobiAdRequestStatus inMobiAdRequestStatus) {
            super.onAdLoadFailed(inMobiNative, inMobiAdRequestStatus);
            Log.d(TAG, "onAdLoadFailed: " + inMobiAdRequestStatus.getMessage());
        }

        @Override
        public void onAdFullScreenDismissed(InMobiNative inMobiNative) {
            super.onAdFullScreenDismissed(inMobiNative);
            Log.d(TAG, "onAdFullScreenDismissed: ");
        }

        @Override
        public void onAdFullScreenWillDisplay(InMobiNative inMobiNative) {
            super.onAdFullScreenWillDisplay(inMobiNative);
            Log.d(TAG, "onAdFullScreenWillDisplay: ");
        }

        @Override
        public void onAdFullScreenDisplayed(InMobiNative inMobiNative) {
            super.onAdFullScreenDisplayed(inMobiNative);
            Log.d(TAG, "onAdFullScreenDisplayed: ");
        }

        @Override
        public void onUserWillLeaveApplication(InMobiNative inMobiNative) {
            super.onUserWillLeaveApplication(inMobiNative);
            Log.d(TAG, "onUserWillLeaveApplication: ");
        }

        @Override
        public void onAdImpressed(InMobiNative inMobiNative) {
            super.onAdImpressed(inMobiNative);
            Log.d(TAG, "onAdImpressed: ");
        }

        @Override
        public void onAdClicked(InMobiNative inMobiNative) {
            super.onAdClicked(inMobiNative);
            Log.d(TAG, "onAdClicked: ");
        }

        @Override
        public void onAdStatusChanged(InMobiNative inMobiNative) {
            super.onAdStatusChanged(inMobiNative);
            Log.d(TAG, "onAdStatusChanged: ");
        }
    };

    private void createNativeAds(){
        Log.d(TAG, "createNativeAds");
        nativeAd = new InMobiNative(MainActivity.this, 1553753205807L, adListener);

        nativeAd.setVideoEventListener(new VideoEventListener() {
            @Override
            public void onVideoCompleted(InMobiNative inMobiNative) {
                super.onVideoCompleted(inMobiNative);
                Log.d(TAG, "onVideoCompleted: ");
                // TODO: Do something after ad completed.
            }

            @Override
            public void onVideoSkipped(InMobiNative inMobiNative) {
                super.onVideoSkipped(inMobiNative);
                Log.d(TAG, "onVideoSkipped: ");
            }

            @Override
            public void onAudioStateChanged(InMobiNative inMobiNative, boolean b) {
                super.onAudioStateChanged(inMobiNative, b);
                Log.d(TAG, "onAudioStateChanged: ");
            }
        });

    }

    private void loadAd(){
        Log.d(TAG, "loadAd");
        nativeAd.load();
    }

    private void clearAd(){
        Log.d(TAG, "clearAd");
        mContainer.removeAllViews();
        nativeAd.destroy();
    }

    private void reloadAd(){
        Log.d(TAG, "reloadAd");
        clearAd();
        createNativeAds();
        loadAd();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        nativeAd.destroy();
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "onRefresh");
        reloadAd();
        refresh.setRefreshing(false);
    }
}

