package com.dailyreporting.app.WebApis;

import android.content.Context;

import com.bugsee.library.Bugsee;
import com.dailyreporting.app.utils.CommonMethods;
import com.dailyreporting.app.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClientClass {
    public static Retrofit getApiClientDev(Context context) {
        TokenInterceptor interceptor = new TokenInterceptor(context);
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        String baseUrl = Constants.BASE_URL;

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(interceptor);
        OkHttpClient client = Bugsee.addNetworkLoggingToOkHttpBuilder(builder)
                .build();
        if (CommonMethods.Mode.isLive(context)) {
            baseUrl = Constants.BASE_URL;
        } else {
            baseUrl = Constants.BASE_URL_STAGING;
        }

        final Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit;
    }

}
