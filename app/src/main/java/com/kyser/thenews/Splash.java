package com.kyser.thenews;

import androidx.appcompat.app.AppCompatActivity;

import android.app.LauncherActivity;
import android.content.Intent;
import android.graphics.drawable.ClipDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class Splash extends AppCompatActivity {

    private final  int mStep = 300;
    private Runnable mClipRunnable;
    private ClipDrawable mClipDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideNavBar();
        initAdSdk();
        setContentView(R.layout.activity_splash);
        startAnimation();
    }

    private void initAdSdk() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.d("======","===status "+initializationStatus.getAdapterStatusMap().entrySet().toString());
            }
        });
    }

    private void setLevel() {
        mClipDrawable.setLevel( mClipDrawable.getLevel()+mStep);
        if( mClipDrawable.getLevel() < 10000 )
            new Handler().postDelayed(mClipRunnable,100);
        else {
            Intent launchIntent = new Intent(this, MainActivity.class);
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(launchIntent);
        }
    }

    private void startAnimation() {
        mClipDrawable = (ClipDrawable) ((ImageView) findViewById(R.id.splash_logo_cip)).getDrawable();
        mClipRunnable = () -> setLevel();
        new Handler().postDelayed(mClipRunnable,100);
    }

    @Override
    protected void onStart() {
        super.onStart();
        hideNavBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideNavBar();
    }

    public void hideNavBar() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}