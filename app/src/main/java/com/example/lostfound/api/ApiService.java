package com.example.lostfound.api;

import com.example.lostfound.models.LostObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @GET("objects")
    Call<java.util.List<LostObject>> getObjects();

    @POST("objects")
    Call<Void> addObject(@Body LostObject object);
}