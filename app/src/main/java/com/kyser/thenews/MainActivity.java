package com.kyser.thenews;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.kyser.thenews.component.NewsItemAdaptor;
import com.kyser.thenews.component.SpaceItemDecoration;
import com.kyser.thenews.component.WebContentFragment;
import com.kyser.thenews.newsstream.model.Article;

public class MainActivity extends AppCompatActivity implements NewsItemAdaptor.ItemSelection {

    private NewsCastLiveData mNewsCastData;
    private RecyclerView mNewsList;
    private NewsItemAdaptor mNewscastAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.d("==================","===status "+initializationStatus.getAdapterStatusMap().entrySet().toString());
            }
        });
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        setRecyclerView();
        initViewModel();
        findViewById(R.id.content_web_fragment).setVisibility(View.GONE);
    }

    private void setRecyclerView() {
        mNewsList = (RecyclerView)findViewById(R.id.news_cast);
        mNewsList.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        mNewsList.addItemDecoration(new SpaceItemDecoration(40,0));
        mNewscastAdaptor = new NewsItemAdaptor(this,this);
        mNewsList.setAdapter(mNewscastAdaptor);
    }

    private void initViewModel(){
        mNewsCastData = ViewModelProviders.of(this).get(NewsCastLiveData.class);
        observeViewModel(mNewsCastData);
    }

    private void observeViewModel(NewsCastLiveData newsCastData) {
        newsCastData.getNewsCastModel().observe(this, projects -> {
            Log.v("==============="," "+projects.getTotalResults());
            mNewscastAdaptor.setNewsResponse(projects);
        });
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

    @Override
    public void onItemSelection(Article article, int position) {
        WebContentFragment fragment = (WebContentFragment) getSupportFragmentManager().findFragmentById(R.id.content_web_fragment);
        fragment.setWebContentView(article);
    }
}