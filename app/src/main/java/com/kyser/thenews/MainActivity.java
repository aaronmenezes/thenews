package com.kyser.thenews;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.kyser.thenews.component.NewsItemAdaptor;
import com.kyser.thenews.component.SpaceItemDecoration;
import com.kyser.thenews.component.WebContentFragment;
import com.kyser.thenews.newsstream.model.Article;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NewsItemAdaptor.ItemSelection, TextWatcher, AdapterView.OnItemClickListener {

    private NewsCastLiveData mNewsCastData;
    private NewsItemAdaptor mNewscastAdaptor;
    private WebContentFragment mFragment;
    private InterstitialAd mInterstitialAd;
    private int mInterstitialCounter = 0;
    private final String[] mCategories = {"India","Business", "Entertainment", "General", "Health", "Science ", "Sports", "Technology"};
    private TextToSpeech mTTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragment = (WebContentFragment) getSupportFragmentManager().findFragmentById(R.id.content_web_fragment);
        initTTS();
        setAdView();
        setFilterCategory();

        findViewById(R.id.tool_search).setOnClickListener(view -> {
            toggleSearch();
        });
        findViewById(R.id.cat_menu).setOnClickListener(view -> toggleMenu());
        findViewById(R.id.close_menu).setOnClickListener(view -> toggleMenu());
        findViewById(R.id.menu_overlay).setOnClickListener(view -> toggleMenu());
        findViewById(R.id.tts_btn).setOnClickListener(view -> { toggleReadOut();});

        setRecyclerView();
        initViewModel();
        findViewById(R.id.content_web_fragment).setVisibility(View.GONE);
    }

    private void initTTS() {
        mTTS=new TextToSpeech(getApplicationContext(), status -> mTTS.setLanguage(Locale.UK));
    }

    private void toggleReadOut() {
        ImageButton ttsButton =   findViewById(R.id.tts_btn);
        if(ttsButton.isSelected()) {
            ttsButton.setSelected(false);
            mTTS.stop();
        }else {
            ttsButton.setSelected(true);
            List<Article> articleList =  mNewscastAdaptor.getNewsResponse().getArticles();
             for(int i=0;i<articleList.size();i++) {
                if(i==0)
                  mTTS.speak( articleList.get(i).getTitle(), TextToSpeech.QUEUE_FLUSH, null);
                else
                  mTTS.speak(articleList.get(i).getTitle(), TextToSpeech.QUEUE_ADD, null);
                mTTS.speak("     ", TextToSpeech.QUEUE_ADD, null);
                mTTS.speak("     ", TextToSpeech.QUEUE_ADD, null);
             }
        }
    }

    private void toggleMenu() {
        View menu  = findViewById(R.id.cat_filter);
        if(menu.getVisibility() == View.VISIBLE){
            menu.setVisibility(View.GONE);
            findViewById(R.id.close_menu).setVisibility(View.GONE);
            findViewById(R.id.menu_overlay).setVisibility(View.GONE);
        }else{
            menu.setVisibility(View.VISIBLE);
            findViewById(R.id.close_menu).setVisibility(View.VISIBLE);
            findViewById(R.id.menu_overlay).setVisibility(View.VISIBLE);
        }
    }

    private void setFilterCategory() {
        ArrayList<String> cat = new ArrayList<>();
        Collections.addAll(cat,mCategories );
        ListView lt = findViewById(R.id.filter_category);
        lt.setAdapter(new ArrayAdapter<String>(this,R.layout.category_item,R.id.filter_category_text,cat));
        lt.setOnItemClickListener(this);
    }

    private void setAdView() {
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
    }

    private void toggleSearch() {
        EditText mEditText = findViewById(R.id.tool_search_input);
        ImageButton mSearchButton = findViewById(R.id.tool_search);
        TextView mCatLabel = findViewById(R.id.cat_label);

        if (mEditText.getVisibility() == View.VISIBLE){
            mEditText.setVisibility(View.GONE);
            mSearchButton.setSelected(false);
            resetSearch();
            mEditText.removeTextChangedListener(this);
            mEditText.setText("");
            mCatLabel.setText(getResources().getString(R.string.india_news));
            hideKeyBoard();
        }else {
            mSearchButton.setSelected(true);
            mEditText.setVisibility(View.VISIBLE);
            mEditText.addTextChangedListener(this);
            mEditText.requestFocus();
            mCatLabel.setText(getResources().getString(R.string.search_pre_label));
        }
    }

    private void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void setRecyclerView() {
        RecyclerView mNewsList = findViewById(R.id.news_cast);
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
        ((TextView) findViewById(R.id.cat_label)).setText(getResources().getString(R.string.india_news));
        mNewscastAdaptor.setNewsResponse(mNewsCastData.getNewsCastModel().getValue());
    }

    private void searchArticles(String query) {
        mNewsCastData.getSearchResult(query);
        mNewsCastData.getSearchModel().observe(this, projects -> {
            mNewscastAdaptor.setNewsResponse(projects);
        });
    }

    private void changeCategory(String category) {
        mNewsCastData.getCategoryResult(category);
        mNewsCastData.getSearchModel().observe(this, projects -> {
            mNewscastAdaptor.setNewsResponse(projects);
        });
        ((TextView) findViewById(R.id.cat_label)).setText(category);
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

    public void showInterstitial(){
        if(mInterstitialAd.isLoaded() && mInterstitialCounter%5==0)
            mInterstitialAd.show();
        mInterstitialCounter++;
    }
    @Override
    public void onItemSelection(Article article, int position) {
        mFragment.setWebContentView(article);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

    @Override
    public void afterTextChanged(Editable editable) {
        if(editable.length()==0){
            resetSearch();
            ((TextView) findViewById(R.id.cat_label)).setText(getResources().getString(R.string.india_news));
        }else {
            searchArticles(editable.toString());
            ((TextView) findViewById(R.id.cat_label)).setText(new StringBuilder().append(getResources().getString(R.string.search_pre_label)).append(editable.toString()).toString());
        }
    }

    @Override
    public void onBackPressed() {
        if(mFragment.isVisible())
            mFragment.hide();
        else{
           finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(i==0)
            resetSearch();
        else
            changeCategory(mCategories[i]);
        toggleMenu();
    }
}