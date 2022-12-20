package com.dailyreporting.app.WebApis;

import android.content.Context;

import com.dailyreporting.app.utils.CommonMethods;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {

    private final Context context;

    public TokenInterceptor(Context context) {

        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        String token = CommonMethods.getToken(context);
        Request newRequest = chain.request().newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();

        return chain.proceed(newRequest);
    }
}
