package com.example.lostfound.api;

import com.example.lostfound.models.LostObject;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("objects")
    Call<List<LostObject>> getObjects();

    @POST("objects")
    Call<Void> addObject(@Body LostObject object);

    @DELETE("objects/{id}")
    Call<Void> deleteObject(@Path("id") String id, @Query("ownerUid") String ownerUid);
}