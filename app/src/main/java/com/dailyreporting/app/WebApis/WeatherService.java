package com.dailyreporting.app.WebApis;

import com.dailyreporting.app.models.SaveVisitLogModel;
import com.dailyreporting.app.models.WeatherPojo;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface WeatherService {

    @GET("2.5/weather")
    Call<WeatherPojo> getWeather(@Query("lat") double lat,
                                 @Query("lon") double lon,
                                 @Query("units") String units,
                                 @Query("appid") String appid);

}
