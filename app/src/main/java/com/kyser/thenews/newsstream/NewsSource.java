package com.kyser.thenews.newsstream;

import android.util.Log;

import com.kyser.thenews.newsstream.model.NewsResponse;

import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsSource {

    private static NewsSource __instance;
    private NewsService mService;

    public  NewsSource() {
        initService();
    }

    private void initService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://newsapi.org/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mService = retrofit.create(NewsService.class);
    }

    public static NewsSource getInstance(){
        if(__instance == null)
            __instance= new NewsSource();
        return __instance;
    }

    public MutableLiveData<NewsResponse> getLatestNews(String country){
        MutableLiveData<NewsResponse>  newsResponseMutableLiveData = new MutableLiveData<>();
        mService.getLatest("6069225c835e4bfe800d3de6eb0b36fd","in","100").enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                Log.v("=======","-----"+response);
                newsResponseMutableLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                Log.e("==============","===="+t.getLocalizedMessage());
            }
        });
        return newsResponseMutableLiveData;
    }
}
