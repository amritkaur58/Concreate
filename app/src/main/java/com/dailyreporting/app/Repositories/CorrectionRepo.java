package com.dailyreporting.app.Repositories;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.dailyreporting.app.R;
import com.dailyreporting.app.WebApis.ApiClientClass;
import com.dailyreporting.app.WebApis.CorrectionService;
import com.dailyreporting.app.models.SaveVisitLogModel;
import com.dailyreporting.app.utils.CommonMethods;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CorrectionRepo {
    private final static String TAG = "OrderRepo";

    public MutableLiveData<SaveVisitLogModel> saveCorrectionApi(Context context, SharedPreferences sharedPreferences, RequestBody id, RequestBody workLogId, RequestBody visitReason, RequestBody codeId, RequestBody fileId, MultipartBody.Part[] surveyImagesParts, RequestBody isInspection, RequestBody description, RequestBody isComplaint, RequestBody personName, RequestBody eventTimeValue, RequestBody eventTime, RequestBody locationId, ProgressDialog dialog) {
        final MutableLiveData<SaveVisitLogModel> data = new MutableLiveData<>();

        CorrectionService apiClient = ApiClientClass.getApiClientDev(context).create(CorrectionService.class);

        Call<SaveVisitLogModel> call = apiClient.saveCorrectionMulti(id, workLogId, visitReason, codeId, fileId, surveyImagesParts, isInspection, description, isComplaint, personName, eventTimeValue, eventTime, locationId);
        if (call != null) {
            call.enqueue(new Callback<SaveVisitLogModel>() {
                @Override
                public void onResponse(Call<SaveVisitLogModel> call, Response<SaveVisitLogModel> response) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    if (response.body() != null && response.code() ==200) {
                         SaveVisitLogModel res = new SaveVisitLogModel();
                            res = response.body();
                            data.postValue(res);

                    } else {
                        Toast.makeText(context, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<SaveVisitLogModel> call, Throwable t) {
                    dialog.hide();
                    CommonMethods.showToast(context, context.getResources().getString(R.string.something_wrong));
                    CommonMethods.showLog(TAG, "Failure " + t.getMessage());
                }
            });


        }
        return data;
    }

}
