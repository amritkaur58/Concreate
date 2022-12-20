package com.dailyreporting.app.Repositories;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.dailyreporting.app.R;
import com.dailyreporting.app.WebApis.ApiClientClass;
import com.dailyreporting.app.WebApis.SubContractService;
import com.dailyreporting.app.models.SaveVisitLogModel;
import com.dailyreporting.app.utils.CommonMethods;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubContractRepository {
    private final static String TAG = "Sub Contract Repo";

    public MutableLiveData<SaveVisitLogModel> saveContractRepo(Context context, SharedPreferences sharedPreferences, ProgressDialog dialog, RequestBody id, RequestBody workLogId, RequestBody activityId, RequestBody locationId, RequestBody lastModBy, RequestBody note, RequestBody fileId, RequestBody noOfWorker, MultipartBody.Part[] surveyImagesParts, RequestBody addedBy, RequestBody isCompliant, RequestBody isCompleted, RequestBody arrivalTime, RequestBody departTime, RequestBody addedOn, RequestBody lastModOn, RequestBody modOn, RequestBody contractorName, RequestBody on) {
        final MutableLiveData<SaveVisitLogModel> data = new MutableLiveData<>();

        SubContractService userService = ApiClientClass.getApiClientDev(context).create(SubContractService.class);
        Call<SaveVisitLogModel> call = userService.saveSubContractMulti(id, workLogId, activityId, locationId, lastModBy, note, fileId, noOfWorker, surveyImagesParts, addedBy, isCompliant, isCompleted, arrivalTime, departTime, addedOn, addedOn, lastModOn, contractorName, lastModOn);
        if (call != null) {
            call.enqueue(new Callback<SaveVisitLogModel>() {
                @Override
                public void onResponse(Call<SaveVisitLogModel> call, Response<SaveVisitLogModel> response) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    if (response.body() != null && response.code() == 200) {
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
