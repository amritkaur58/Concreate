package com.dailyreporting.app.WebApis;

import com.dailyreporting.app.models.CorrectionCodeResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface CorrectionCodes {

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("CorrectionCodes/GetAll")
    Call<CorrectionCodeResponse> getCorrectionCode(@Query("projectId") int projectId);


}
