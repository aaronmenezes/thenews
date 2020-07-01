package com.kyser.thenews;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.kyser.thenews.component.NewsItemAdaptor;
import com.kyser.thenews.component.SpaceItemDecoration;
import com.kyser.thenews.component.WebContentFragment;
import com.kyser.thenews.newsstream.model.Article;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NewsItemAdaptor.ItemSelection, TextWatcher {

    private NewsCastLiveData mNewsCastData;
    private RecyclerView mNewsList;
    private NewsItemAdaptor mNewscastAdaptor;
    private WebContentFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragment = (WebContentFragment) getSupportFragmentManager().findFragmentById(R.id.content_web_fragment);
        setAdView();

        findViewById(R.id.tool_search).setOnClickListener(view -> {
            toggleSearch();
        });

        setRecyclerView();
        initViewModel();
        findViewById(R.id.content_web_fragment).setVisibility(View.GONE);
    }

    private void setAdView() {
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void toggleSearch() {
        EditText mEditText = findViewById(R.id.tool_search_input);
        ImageButton mSearchButton = findViewById(R.id.tool_search);
        if (mEditText.getVisibility() == View.VISIBLE){
            mEditText.setVisibility(View.GONE);
            mSearchButton.setSelected(false);
            resetSearch();
            mEditText.removeTextChangedListener(this);
            mEditText.setText("");
        }else {
            mSearchButton.setSelected(true);
            mEditText.setVisibility(View.VISIBLE);
            mEditText.addTextChangedListener(this);
            mEditText.requestFocus();
        }
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
            Log.v("====="," "+projects.getTotalResults());
            mNewscastAdaptor.setNewsResponse(projects);
        });
    }

    private void resetSearch(){
        mNewscastAdaptor.setNewsResponse(mNewsCastData.getNewsCastModel().getValue());
    }

    private void searchArticles(String query) {
        mNewsCastData.getSearchResult(query);
        mNewsCastData.getSearchModel().observe(this, projects -> {
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
        mFragment.setWebContentView(article);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

    @Override
    public void afterTextChanged(Editable editable) {
        if(editable.length()==0){
            resetSearch();
        }else
            searchArticles(editable.toString());
    }

    @Override
    public void onBackPressed() {
        if(mFragment.isVisible())
            mFragment.hide();
        else{
           finish();
        }
    }
}