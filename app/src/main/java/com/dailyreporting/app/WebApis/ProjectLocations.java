package com.dailyreporting.app.WebApis;

import com.dailyreporting.app.models.ProjectLocationResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface ProjectLocations {

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("ProjectsLocations/GetAll")
    Call<ProjectLocationResponse> getProjectLocation(
            @Query("pageNumber") int pageNumber,
            @Query("projectId") int projectId
    );


}
