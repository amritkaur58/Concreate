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

public interface SubContractService {

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("SubContractorActivity/Save")
    Call<SaveVisitLogModel> saveSubContract(@Header("Authorization") String token,
                                            @Body JsonObject body);

    @Multipart
    @POST("SubContractorActivity/Save")
    Call<SaveVisitLogModel> saveSubContractMulti(
            @Part("Id") RequestBody id,
            @Part("WorkLogId") RequestBody WorkLogId,
            @Part("ActivityTypeId") RequestBody ActivityId,
            @Part("LocationId") RequestBody LocationId,
            @Part("LastModBy") RequestBody LastModBy,
            @Part("Note") RequestBody Note,
            @Part("FileId") RequestBody FileId,
            @Part("NoOfWorkers") RequestBody NoOfWorkers,
            @Part MultipartBody.Part[] File,
            @Part("AddedBy") RequestBody AddedBy,
            @Part("IsCompliant") RequestBody IsCompliant,
            @Part("IsCompleted") RequestBody IsCompleted,
            @Part("ArrivalTimeValue") RequestBody ArrivalTimeValue,
            @Part("DepartureTimeValue") RequestBody DepartureTimeValue,
            @Part("EventTimeValue") RequestBody EventTimeValue,
            @Part("EventTime") RequestBody EventTime,
            @Part("ArrivalTime") RequestBody ArrivalTime,
            @Part("ContractorName") RequestBody ContractorName,
            @Part("DepartureTime") RequestBody DepartureTime);

}
