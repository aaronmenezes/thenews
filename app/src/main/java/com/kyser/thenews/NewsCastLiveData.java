package com.kyser.thenews;

import android.app.Application;

import com.kyser.thenews.newsstream.NewsSource;
import com.kyser.thenews.newsstream.model.NewsResponse;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class NewsCastLiveData  extends AndroidViewModel {

    private MutableLiveData<NewsResponse> mediaListObservable;

    public NewsCastLiveData(Application application) {
        super(application);
        getNewsCast();
    }
    public void getNewsCast(){
        mediaListObservable = NewsSource.getInstance().getLatestNews("in");
    }

    public MutableLiveData<NewsResponse> getNewsCastModel(){
       return mediaListObservable;
    }
}
