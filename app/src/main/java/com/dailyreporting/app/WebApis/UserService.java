package com.dailyreporting.app.WebApis;

import com.dailyreporting.app.models.VisitResponse;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface UserService {
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("User/Login")
    Call<VisitResponse> login(@Body JsonObject body);


    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("User/Me")
    Call<VisitResponse> getUserInfo();


}
