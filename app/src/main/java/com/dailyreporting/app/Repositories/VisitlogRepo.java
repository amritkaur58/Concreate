package com.dailyreporting.app.Repositories;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.dailyreporting.app.WebApis.ApiClientClass;
import com.dailyreporting.app.WebApis.VisitLogService;
import com.dailyreporting.app.models.SaveVisitLogModel;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisitlogRepo {

    public MutableLiveData<SaveVisitLogModel> saveVisitLogApi(Context context, SharedPreferences sharedPreferences, JsonObject gsonObject, ProgressDialog dialog) {
        final MutableLiveData<SaveVisitLogModel> data = new MutableLiveData<>();
        VisitLogService userService = ApiClientClass.getApiClientDev(context).create(VisitLogService.class);
        Call<SaveVisitLogModel> call = userService.saveVisitLog(gsonObject);

        if (call != null) {
            call.enqueue(new Callback<SaveVisitLogModel>() {
                @Override
                public void onResponse(Call<SaveVisitLogModel> call, Response<SaveVisitLogModel> response) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    if (response.body() != null && response.code()==200) {
                            SaveVisitLogModel res = new SaveVisitLogModel();
                            res = response.body();
                            data.postValue(res);
                    } else {
                        Toast.makeText(context, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<SaveVisitLogModel> call, Throwable t) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        return data;
    }


}
