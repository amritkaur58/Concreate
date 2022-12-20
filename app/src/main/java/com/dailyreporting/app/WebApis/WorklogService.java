package com.dailyreporting.app.WebApis;

import com.dailyreporting.app.models.WorkLogResponse;
import com.dailyreporting.app.models.WorklogDetail;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface WorklogService {

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("WorkLogs/Save")
    Call<WorkLogResponse> saveWorkLog(@Header("Authorization") String token,
                                      @Body JsonObject body);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("WorkLogs/Get")
    Call<WorklogDetail> getWorklogDetail(@Query("id") int id);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("WorkLogs/GetAll")
    Call<WorklogDetail> getWorklogs(@Query("projectId") int id);


}
