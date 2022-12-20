package com.dailyreporting.app.WebApis;

import com.dailyreporting.app.models.ProjectModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface Projects {
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("Projects/GetAll")
    Call<ProjectModel> getAllProjects(@Query("pageNumber") int pageNumber);
}
