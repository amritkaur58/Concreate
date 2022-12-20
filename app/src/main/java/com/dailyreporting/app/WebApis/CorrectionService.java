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

public interface CorrectionService {

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("Correction/Save")
    Call<SaveVisitLogModel> saveCorrection(@Header("Authorization") String token,
                                           @Body JsonObject body);

    @Multipart
    @POST("Correction/Save")
    Call<SaveVisitLogModel> saveCorrectionMulti(@Part("Id") RequestBody id,
                                                @Part("WorkLogId") RequestBody WorkLogId,
                                                @Part("VisitReason") RequestBody ActivityId,
                                                @Part("CodeId") RequestBody codeId,
                                                @Part("FileId") RequestBody LastModBy,
                                                @Part MultipartBody.Part[] Note,
                                                @Part("VisualInspectionDone") RequestBody FileId,
                                                @Part("Description") RequestBody NoOfWorkers,
                                                @Part("IsCompliantAfterCorrection") RequestBody File,
                                                @Part("PersonDoingQualityControl") RequestBody AddedBy,
                                                @Part("EventTimeValue") RequestBody IsCompliant,
                                                @Part("EventTime") RequestBody IsCompleted,
                                                @Part("LocationId") RequestBody LocationId);

}
