package com.dailyreporting.app.Services;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;

import com.dailyreporting.app.WebApis.ApiClientClass;
import com.dailyreporting.app.WebApis.ProjectLocations;
import com.dailyreporting.app.database.ProjectLocationRepo;
import com.dailyreporting.app.models.ProjectLocation;
import com.dailyreporting.app.models.ProjectLocationResponse;
import com.dailyreporting.app.utils.CommonMethods;
import com.dailyreporting.app.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dailyreporting.app.utils.Constants.MyPREFERENCES;

public class ServicesMethod {

    public static boolean getLocationMethod(ProgressDialog dialog, Context context, int projectID) {

        final boolean[] isRefreshed = {false};
        final ProjectLocations[] web_api_2 = {ApiClientClass.getApiClientDev(context).create(ProjectLocations.class)};
        Call<ProjectLocationResponse> call = web_api_2[0].getProjectLocation(1, projectID);

        if (call != null) {
            call.enqueue(new Callback<ProjectLocationResponse>() {
                @Override
                public void onResponse(Call<ProjectLocationResponse> call, Response<ProjectLocationResponse> response) {
                    dialog.hide();
                    if (response.body() != null && response.body().getData() != null && response.body().getData().getItems() != null) {

                        ProjectLocation projectType;
                        ProjectLocationRepo.DeleteAll();
                        SharedPreferences sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(Constants.PROJECT_CLICKED, false);
                        editor.apply();
                        for (int i = 0; i < response.body().getData().getItems().size(); i++) {
                            ProjectLocationResponse.Items responseData = response.body().getData().getItems().get(i);
                            projectType = new ProjectLocation();

                            projectType.projectId = responseData.getProjectId();
                            projectType.projectName = responseData.getProjectName();
                            projectType.addedOn = responseData.getAddedOn();
                            projectType.addedBy = responseData.getAddedBy();
                            projectType.lastModBy = responseData.getLastModBy();
                            projectType.lastModOn = responseData.getLastModOn();
                            projectType.name = responseData.getName();
                            projectType.locationId = Integer.parseInt(responseData.getId());
                            ProjectLocationRepo.Save(projectType);
                            isRefreshed[0] = true;
                        }


                    }
                }

                @Override
                public void onFailure(Call<ProjectLocationResponse> call, Throwable t) {
                    dialog.hide();
                    CommonMethods.showLog("Fail", "Failure " + t.getMessage());
                }
            });
        }

        return isRefreshed[0];
    }

}
