package com.dailyreporting.app.WebApis;

import com.dailyreporting.app.models.SaveVisitLogModel;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ActivityService {

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("Activity/Save")
    Call<SaveVisitLogModel> saveActivityList(@Header("Authorization") String token,
                                             @Body JsonObject body);


    @Multipart
    @POST("Activity/Save")
    Call<SaveVisitLogModel> saveActivityListMulti(
            @Part("Id") RequestBody id,
            @Part("WorkLogId") RequestBody WorkLogId,
            @Part("ActivityTypeId") RequestBody ActivityId,
            @Part("Note") RequestBody Note,
            @Part("FileId") RequestBody FileId,
            @Part MultipartBody.Part[] File,
            @Part("LocationId") RequestBody LocationId,
            @Part("LastModBy") RequestBody LastModBy,
            @Part("AddedBy") RequestBody AddedBy,
            @Part("IsCompliant") RequestBody IsCompliant,
            @Part("IsCompleted") RequestBody IsCompleted,
            @Part("EventTimeValue") RequestBody AddedOn,
            @Part("EventTime") RequestBody LastModOn);

}
