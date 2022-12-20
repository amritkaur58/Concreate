package com.dailyreporting.app.Repositories;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.dailyreporting.app.R;
import com.dailyreporting.app.WebApis.ActivityService;
import com.dailyreporting.app.WebApis.ApiClientClass;
import com.dailyreporting.app.models.SaveVisitLogModel;
import com.dailyreporting.app.utils.CommonMethods;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitiesRepository {
    private final static String TAG = "Activity Repo";

    public MutableLiveData<SaveVisitLogModel> saveActivityRepo(Context context, SharedPreferences sharedPreferences, RequestBody id, RequestBody workLogId, RequestBody activityId, RequestBody note, RequestBody fileId, MultipartBody.Part[] surveyImagesParts, RequestBody locationId, RequestBody lastModBy, RequestBody addedBy, RequestBody isCompliant, RequestBody isCompleted, RequestBody latitude, RequestBody longitude, RequestBody addedOn, RequestBody lastModOn, ProgressDialog dialogActivity) {
        final MutableLiveData<SaveVisitLogModel> data = new MutableLiveData<>();

        ActivityService userService = ApiClientClass.getApiClientDev(context).create(ActivityService.class);
        Call<SaveVisitLogModel> call = userService.saveActivityListMulti(id, workLogId, activityId, note, fileId, surveyImagesParts, locationId, lastModBy, addedBy, isCompliant, isCompleted, addedOn, lastModOn);
        if (call != null) {
            call.enqueue(new Callback<SaveVisitLogModel>() {
                @Override
                public void onResponse(Call<SaveVisitLogModel> call, Response<SaveVisitLogModel> response) {
                    if (dialogActivity != null) {
                        dialogActivity.dismiss();
                    }
                    if (response.body() != null & response.code() == 200) {
                        SaveVisitLogModel res = new SaveVisitLogModel();
                        res = response.body();
                        data.postValue(res);
                    } else {
                        Toast.makeText(context, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<SaveVisitLogModel> call, Throwable t) {
                    dialogActivity.hide();
                    CommonMethods.showToast(context, context.getResources().getString(R.string.something_wrong));
                    CommonMethods.showLog(TAG, "Failure " + t.getMessage());
                }
            });

        }
        return data;
    }

}
