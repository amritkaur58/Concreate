package com.dailyreporting.app.WebApis;

import com.dailyreporting.app.models.ProjectActivityTypeModel;
import com.dailyreporting.app.models.WorkLogResponse;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ProjectActivityTypes {
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("ProjectActivityTypes/GetAll")
    Call<ProjectActivityTypeModel> getProjectActivity(@Query("projectId") int projectId,
                                                      @Query("pageSize") int pageSize,
                                                      @Query("parentId") int parentId);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("ProjectActivityTypes/Save")
    Call<WorkLogResponse> saveActivityType(@Body JsonObject body);
}
