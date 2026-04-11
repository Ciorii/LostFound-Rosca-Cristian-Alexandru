package com.example.lostfound.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ImgBBClient {
    private static final String BASE_URL = "https://api.imgbb.com/";
    private static Retrofit retrofit;

    public static ImgBBApiService getApi() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ImgBBApiService.class);
    }
}