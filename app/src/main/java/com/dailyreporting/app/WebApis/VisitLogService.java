package com.dailyreporting.app.WebApis;

import com.dailyreporting.app.models.SaveVisitLogModel;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface VisitLogService {

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("VisitLogs/Save")
    Call<SaveVisitLogModel> saveVisitLog(@Body JsonObject body);


}
