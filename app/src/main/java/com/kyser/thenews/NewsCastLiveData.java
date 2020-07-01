package com.kyser.thenews;

import android.app.Application;

import com.kyser.thenews.newsstream.NewsSource;
import com.kyser.thenews.newsstream.model.NewsResponse;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class NewsCastLiveData  extends AndroidViewModel {

    private MutableLiveData<NewsResponse> mediaListObservable;
    private MutableLiveData<NewsResponse> mSearchResultList;

    public NewsCastLiveData(Application application) {
        super(application);
        getNewsCast();
    }
    public void getNewsCast(){
        mediaListObservable = NewsSource.getInstance().getLatestNews("in");
    }

    public void getSearchResult(String query){
        mSearchResultList = NewsSource.getInstance().getSearchResult(query);
    }

    public MutableLiveData<NewsResponse> getNewsCastModel(){
       return mediaListObservable;
    }
    public MutableLiveData<NewsResponse> getSearchModel(){
        return mSearchResultList;
    }
}
