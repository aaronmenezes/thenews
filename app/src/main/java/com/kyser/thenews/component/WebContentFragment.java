package com.kyser.thenews.component;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kyser.thenews.MainActivity;
import com.kyser.thenews.R;
import com.kyser.thenews.newsstream.model.Article;
import java.util.Objects;

public class WebContentFragment extends Fragment {

    private WebView mContentView;
    private Article mArticle;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,  @Nullable ViewGroup container,  @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fullscreen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContentView = view.findViewById(R.id.fullscreen_content);
        mContentView.setWebViewClient(new WebViewClient());
        view.findViewById(R.id.content_back).setOnClickListener(view1 -> {
            hide();
        });
        view.findViewById(R.id.content_share).setOnClickListener(view1 -> {
            share();
        });
    }

    private void share() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Read About : "+mArticle.getUrl();
        sharingIntent.putExtra(android.content.Intent.EXTRA_TITLE, "Read : "+ mArticle.getTitle());
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Read : "+ mArticle.getDescription());
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public void hide() {
        ((MainActivity)getActivity()).showInterstitial();
        mContentView.loadUrl(" ");
        getView().setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContentView = null;
    }

    public void setWebContentView(Article article){
        mArticle = article;
        mContentView.loadUrl(article.getUrl());
        Objects.requireNonNull(getView()).setVisibility(View.VISIBLE);
    }
}