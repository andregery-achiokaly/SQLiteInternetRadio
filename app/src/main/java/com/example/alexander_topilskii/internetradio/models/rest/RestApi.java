package com.example.alexander_topilskii.internetradio.models.rest;


import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RestApi {
    @FormUrlEncoded
    @POST("/api/login")
    Call<PostModel> login(@Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("/api/signup")
    Call<PostModel> registration(@Field("username") String username, @Field("password") String password);
}
