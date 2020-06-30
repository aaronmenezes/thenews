package com.kyser.thenews.newsstream;

import com.kyser.thenews.newsstream.model.NewsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NewsService {

    @GET("top-headlines")
    Call<NewsResponse> getLatest(@Query("apiKey") String apiKey, @Query("country") String country, @Query("pageSize") String pageSize);
}
