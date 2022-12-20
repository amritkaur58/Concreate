package com.dailyreporting.app.Repositories;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.dailyreporting.app.R;
import com.dailyreporting.app.WebApis.ApiClientClass;
import com.dailyreporting.app.WebApis.WorklogService;
import com.dailyreporting.app.models.SaveVisitLogModel;
import com.dailyreporting.app.models.WorkLogResponse;
import com.dailyreporting.app.utils.CommonMethods;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorklogRepo {
    private final static String TAG = "Worklog Repo";

    public MutableLiveData<WorkLogResponse> SaveWorkLog(Context context, String authorizationToken, SharedPreferences sharedPreferences, ProgressDialog dialog, JsonObject gsonObject) {
        final MutableLiveData<WorkLogResponse> data = new MutableLiveData<>();

        WorklogService userService = ApiClientClass.getApiClientDev(context).create(WorklogService.class);
        Call<WorkLogResponse> call = userService.saveWorkLog(authorizationToken, gsonObject);

        if (call != null) {
            call.enqueue(new Callback<WorkLogResponse>() {
                @Override
                public void onResponse(Call<WorkLogResponse> call, Response<WorkLogResponse> response) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    if (response.body() != null ) {
                        WorkLogResponse res = new WorkLogResponse();
                        res = response.body();
                        data.postValue(res);
                    } else {
                        CommonMethods.showToast(context, response.body().getMessage());
                    }
                }

                @Override
                public void onFailure(Call<WorkLogResponse> call, Throwable t) {
                    dialog.hide();
                    CommonMethods.showToast(context, context.getResources().getString(R.string.something_wrong));
                  }
            });

        }
        return data;
    }

}
