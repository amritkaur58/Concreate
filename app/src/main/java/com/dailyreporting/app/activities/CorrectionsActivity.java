package com.dailyreporting.app.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dailyreporting.app.Extras.Logger;
import com.dailyreporting.app.R;
import com.dailyreporting.app.Repositories.ActivitiesRepository;
import com.dailyreporting.app.Repositories.CorrectionRepo;
import com.dailyreporting.app.Repositories.SubContractRepository;
import com.dailyreporting.app.Repositories.VisitlogRepo;
import com.dailyreporting.app.WebApis.ApiClientClass;
import com.dailyreporting.app.WebApis.CorrectionService;
import com.dailyreporting.app.WebApis.SubContractService;
import com.dailyreporting.app.adapters.CorrectionAdapter;
import com.dailyreporting.app.database.ActivitiesRepo;
import com.dailyreporting.app.database.CorrectionsRepo;
import com.dailyreporting.app.database.DailyFolderRepo;
import com.dailyreporting.app.database.FilesRepo;
import com.dailyreporting.app.database.SubContractorRepo;
import com.dailyreporting.app.database.VisitLogRepo;
import com.dailyreporting.app.models.ActivityModel;
import com.dailyreporting.app.models.Correction;
import com.dailyreporting.app.models.FilesModel;
import com.dailyreporting.app.models.SaveVisitLogModel;
import com.dailyreporting.app.models.SubContractActivity;
import com.dailyreporting.app.models.VisitLog;
import com.dailyreporting.app.utils.CommonMethods;
import com.dailyreporting.app.utils.Constants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dailyreporting.app.utils.Constants.MyPREFERENCES;

public class CorrectionsActivity extends AppCompatActivity implements View.OnClickListener {
    private final List<Correction> worklogList = new ArrayList<>();
    RecyclerView recyclerView;
    CorrectionAdapter correctionAdapter;
    TextView txtHeader;
    ImageView imgBack;
    RelativeLayout rlAdd;
    ImageView saveIV;
    String TAG = getClass().getSimpleName();
    private TextView txtDay, dateTV;
    private TextView txtNoData, nextTV;
    private TextView txtCity;
    private List<Correction> listCorrection = new ArrayList<>();
    private List<Correction> correctionList = new ArrayList<>();
    private ProgressDialog dialog;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private RelativeLayout nextRL;
    private String projectSelected = "";
    private boolean fromReport = false;
    private String workLogId = "11";
    private List<ActivityModel> activityList = new ArrayList<>();
    private ProgressDialog dialogActivity;
    private List<SubContractActivity> contractList = new ArrayList<>();
    private ProgressDialog dialogContract;
    private List<VisitLog> visitList = new ArrayList<>();
    private ProgressDialog dialogVisit;
    private int projectID = 0;
    private String WorkLogDate = "";
    private String worklogDay = "";
    private boolean settingFont = false;
    private float fontSize = 13f;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correction);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        projectSelected = sharedPreferences.getString(Constants.PROJECT_SELECTED, "");
        projectID = sharedPreferences.getInt(Constants.PROJECT_ID, 0);
        recyclerView = findViewById(R.id.recyclerView);
        imgBack = findViewById(R.id.imgBack);
        txtDay = findViewById(R.id.txtDay);
        txtNoData = findViewById(R.id.txtNoData);
        txtCity = findViewById(R.id.txtCity);
        nextTV = findViewById(R.id.nextTV);
        dateTV = findViewById(R.id.dateTV);
        nextRL = findViewById(R.id.nextRL);
        txtHeader = findViewById(R.id.txtHeader);
        rlAdd = findViewById(R.id.rlAdd);
        saveIV = findViewById(R.id.saveIV);
        txtHeader.setText(getString(R.string.corrections));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setNestedScrollingEnabled(false);
        String currentDate = CommonMethods.getCurrentDateByFormat("dd MMM, yyyy");
        String currentDay = CommonMethods.getCurrentDay();
        dateTV.setText(currentDate);
        txtDay.setText(currentDay);
        if (getIntent().hasExtra(Constants.FROM_REPORT)) {
            workLogId = getIntent().getStringExtra(Constants.WORKLOG_ID);
            nextRL.setVisibility(View.VISIBLE);
            rlAdd.setVisibility(View.GONE);
            fromReport = true;
        }
        imgBack.setOnClickListener(this);
        rlAdd.setOnClickListener(this);
        saveIV.setOnClickListener(this);
        nextRL.setOnClickListener(this);
        WorkLogDate = CommonMethods.getCurrentDateByFormat("dd MMM, yyyy");
        if (getIntent().hasExtra("WorkLogDate")) {
            WorkLogDate = getIntent().getStringExtra("WorkLogDate");
            dateTV.setText(WorkLogDate);
            try {
                worklogDay = CommonMethods.getDayFromDate(WorkLogDate);
                txtDay.setText(worklogDay);
            } catch (Exception e) {
                Logger.error(e);
            }
        }
        txtCity.setText(projectSelected);

        checkDarkMode();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.rlAdd:
                Intent intent = new Intent(CorrectionsActivity.this, SaveCorrections.class);
                intent.putExtra("WorkLogDate", WorkLogDate);
                startActivity(intent);
                break;
            case R.id.nextRL:
                if (fromReport)
                    getSavedActivities();

                break;
            case R.id.saveIV:

                getCorrectionList();
                break;
        }
    }


    private void getCorrectionList() {
        correctionList = new ArrayList<>();
        try {

            correctionList = CorrectionsRepo.GetCorrectionByProjectDate(projectID, WorkLogDate);
        } catch (Exception e) {
            Logger.error(e);
        }

        if (correctionList.size() > 0) {
            for (int i = 0; i < correctionList.size(); i++) {
                saveLiveCorrection(correctionList.get(i), i);
            }

        } else {
            CommonMethods.showToast(CorrectionsActivity.this, "Data Uploaded Successfully");
            finish();
        }

    }

    private void saveCorrection(Correction correction, int i) {
        try {

            dialog = new ProgressDialog(this);

            try {
                if (i == correctionList.size() - 1) {
                    dialog.show();
                }

            } catch (WindowManager.BadTokenException e) {

            }
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.custom_loading_layout);

            JSONObject user = new JSONObject();

            JsonObject gsonObject = null;
            try {
                user.put("Id", 0);
                user.put("WorkLogId", workLogId);
                user.put("LocationId", correction.LocationId);
                user.put("CodeId", correction.CodeId);
                user.put("VisualInspectionDone", false);

                user.put("Description", correction.Description);
                user.put("IsCompliantAfterCorrection", true);
                user.put("PersonDoingQualityControl", correction.PersonDoingQualityControl);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

                String val = sdf.format(new Date(System.currentTimeMillis()));
                user.put("ArrivalTime", val);
                user.put("DepartureTime", val);

                JsonParser jsonParser = new JsonParser();
                gsonObject = (JsonObject) jsonParser.parse(user.toString());

            } catch (JSONException e) {
                Logger.error(e);
            }

            MultipartBody.Part body = null;
            if (correction.imagepath.isEmpty()) {
                RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");

                body = MultipartBody.Part.createFormData("File", "", attachmentEmpty);

            } else {
                File file = new File(correction.imagepath);
                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("multipart/form-data"), file);

                body = MultipartBody.Part.createFormData("File", file.getName(), requestFile);

            }
            List<FilesModel> filesModels = FilesRepo.Get(correction.itemGuId);
            MultipartBody.Part[] surveyImagesParts = new MultipartBody.Part[correction.imagepath.length()];
            if (filesModels.size() > 0) {
                surveyImagesParts = new MultipartBody.Part[filesModels.size()];
            }
            for (int index = 0; index < filesModels.size(); index++) {
                Log.d(TAG, "requestUploadSurvey: survey image " +
                        index +
                        "  " +
                        filesModels.get(index)
                                .getFullPath());
                File fileNew = new File(filesModels.get(index).getFullPath());
                RequestBody surveyBody = RequestBody.create(MediaType.parse("image/*"),
                        fileNew);
                surveyImagesParts[index] = MultipartBody.Part.createFormData("File",
                        fileNew.getName(),
                        surveyBody);
            }
            RequestBody Id = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(0));
            RequestBody WorkLogId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(workLogId));
            RequestBody LocationId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(correction.LocationId));
            RequestBody FileId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(0));
            RequestBody File = RequestBody.create(MediaType.parse("text/plain"), "");
            RequestBody IsInspection = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(correction.VisualInspectionDone));
            RequestBody description = RequestBody.create(MediaType.parse("text/plain"), correction.Description);
            RequestBody isComplaint = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(correction.IsCompliantAfterCorrection));
            RequestBody personName = RequestBody.create(MediaType.parse("text/plain"), correction.PersonDoingQualityControl);
            RequestBody eventTimeValue = RequestBody.create(MediaType.parse("text/plain"), correction.AddedOn);
            RequestBody eventTime = RequestBody.create(MediaType.parse("text/plain"), "");
            RequestBody codeId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(correction.CodeId));
            RequestBody visitReason = RequestBody.create(MediaType.parse("text/plain"), correction.VisitReason);

            CorrectionService userService = ApiClientClass.getApiClientDev(CorrectionsActivity.this).create(CorrectionService.class);
            Call<SaveVisitLogModel> call = userService.saveCorrectionMulti(Id, WorkLogId, visitReason, codeId, FileId, surveyImagesParts, IsInspection, description, isComplaint, personName, eventTimeValue, eventTime, LocationId);
            CommonMethods.showLog(TAG, "correction log gsonObject  " + gsonObject);
            if (call != null) {
                call.enqueue(new Callback<SaveVisitLogModel>() {
                    @Override
                    public void onResponse(Call<SaveVisitLogModel> call, Response<SaveVisitLogModel> response) {
                        dialog.hide();

                        try {
                            if (response.body() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                                CorrectionsRepo.Delete(correction.getId().intValue());
                                listCorrection.remove(i);
                                correctionAdapter.notifyDataSetChanged();
                                if (listCorrection.size() <= 0) {
                                    txtNoData.setVisibility(View.VISIBLE);
                                }


                            } else {
                                CommonMethods.showToast(CorrectionsActivity.this, response.message());
                            }

                        } catch (Exception e) {
                            Logger.error(e);
                        }
                        if (response.body() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                            if (i == correctionList.size() - 1) {
                                try {
                                    DailyFolderRepo.DeleteFolderByProject(WorkLogDate);
                                } catch (Exception e) {
                                    CommonMethods.showLog("ERROr", e.getMessage());
                                }

                                CommonMethods.showToast(CorrectionsActivity.this, "Data Uploaded Successfully");
                                finish();
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<SaveVisitLogModel> call, Throwable t) {
                        dialog.hide();
                        CommonMethods.showToast(CorrectionsActivity.this, getResources().getString(R.string.something_wrong));
                        CommonMethods.showLog(TAG, "Failure " + t.getMessage());
                    }
                });
            }

        } catch (Exception e) {
            dialog.dismiss();
            Log.e(TAG, e.getMessage());
        }

    }

    private void saveLiveCorrection(Correction correction, int i) {
        try {

            dialog = new ProgressDialog(this);

            try {
                if (i == correctionList.size() - 1) {
                    dialog.show();
                }

            } catch (WindowManager.BadTokenException e) {

            }
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.custom_loading_layout);

            JSONObject user = new JSONObject();

            JsonObject gsonObject = null;
            try {
                user.put("Id", 0);
                user.put("WorkLogId", workLogId);
                user.put("LocationId", correction.LocationId);
                user.put("CodeId", correction.CodeId);
                user.put("VisualInspectionDone", false);

                user.put("Description", correction.Description);
                user.put("IsCompliantAfterCorrection", true);
                user.put("PersonDoingQualityControl", correction.PersonDoingQualityControl);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

                String val = sdf.format(new Date(System.currentTimeMillis()));
                user.put("ArrivalTime", val);
                user.put("DepartureTime", val);

                JsonParser jsonParser = new JsonParser();
                gsonObject = (JsonObject) jsonParser.parse(user.toString());

            } catch (JSONException e) {
                Logger.error(e);
            }

            MultipartBody.Part body = null;

            List<FilesModel> filesModels = FilesRepo.Get(correction.itemGuId);
            MultipartBody.Part[] surveyImagesParts = new MultipartBody.Part[correction.imagepath.length()];
            if (filesModels.size() > 0) {
                surveyImagesParts = new MultipartBody.Part[filesModels.size()];
            }
            for (int index = 0; index < filesModels.size(); index++) {
                Log.d(TAG, "requestUploadSurvey: survey image " +
                        index +
                        "  " +
                        filesModels.get(index)
                                .getFullPath());
                File fileNew = new File(filesModels.get(index).getFullPath());
                RequestBody surveyBody = RequestBody.create(MediaType.parse("image/*"),
                        fileNew);
                surveyImagesParts[index] = MultipartBody.Part.createFormData("File",
                        fileNew.getName(),
                        surveyBody);
            }
            RequestBody Id = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(0));
            RequestBody WorkLogId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(workLogId));
            RequestBody LocationId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(correction.LocationId));
            RequestBody FileId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(0));
            RequestBody File = RequestBody.create(MediaType.parse("text/plain"), "");
            RequestBody IsInspection = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(correction.VisualInspectionDone));
            RequestBody description = RequestBody.create(MediaType.parse("text/plain"), correction.Description);
            RequestBody isComplaint = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(correction.IsCompliantAfterCorrection));
            RequestBody personName = RequestBody.create(MediaType.parse("text/plain"), correction.PersonDoingQualityControl);
            RequestBody eventTimeValue = RequestBody.create(MediaType.parse("text/plain"), correction.AddedOn);
            RequestBody eventTime = RequestBody.create(MediaType.parse("text/plain"), "");
            RequestBody codeId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(correction.CodeId));
            RequestBody visitReason = RequestBody.create(MediaType.parse("text/plain"), correction.VisitReason);

            LiveData<SaveVisitLogModel> req = new CorrectionRepo().saveCorrectionApi(CorrectionsActivity.this, sharedPreferences, Id, WorkLogId, visitReason, codeId, FileId, surveyImagesParts, IsInspection, description, isComplaint, personName, eventTimeValue, eventTime, LocationId, dialog);
            req.observe(this, response -> {
                if (response != null) {
                    if (response.getStatus().equals("200")) {

                        dialog.hide();

                        try {
                            CorrectionsRepo.Delete(correction.getId().intValue());
                            listCorrection.remove(i);
                            correctionAdapter.notifyDataSetChanged();
                            if (listCorrection.size() <= 0) {
                                txtNoData.setVisibility(View.VISIBLE);
                            } else {
                                CommonMethods.showToast(CorrectionsActivity.this, response.getMessage());
                            }

                        } catch (Exception e) {
                            Logger.error(e);
                        }
                        if (response != null && response.getStatus().equalsIgnoreCase("200")) {
                            if (i == correctionList.size() - 1) {
                                try {
                                    DailyFolderRepo.DeleteFolderByProject(WorkLogDate);
                                } catch (Exception e) {
                                    CommonMethods.showLog("ERROr", e.getMessage());
                                }

                                CommonMethods.showToast(CorrectionsActivity.this, "Data Uploaded Successfully");
                                finish();
                            }

                        }
                    } else {

                        Toast.makeText(CorrectionsActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                } else {
                    dialog.hide();
                    Toast.makeText(CorrectionsActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });


        } catch (Exception e) {
            dialog.dismiss();
            Log.e(TAG, e.getMessage());
        }

    }

    private void setFontText() {
        txtDay.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtCity.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        dateTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtNoData.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        nextTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
    }

    @Override
    protected void onResume() {
        super.onResume();
        projectID = sharedPreferences.getInt(Constants.PROJECT_ID, 0);
        List<Correction> data = new ArrayList<>();

        try {
            data = CorrectionsRepo.GetCorrectionByProjectDate(projectID, WorkLogDate);

        } catch (Exception e) {
            Logger.error(e);
        }

        if (data != null && data.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            txtNoData.setVisibility(View.GONE);
            listCorrection = new ArrayList<>();
            listCorrection.addAll(data);

            correctionAdapter = new CorrectionAdapter(CorrectionsActivity.this, listCorrection);
            recyclerView.setAdapter(correctionAdapter);
        } else {
            txtNoData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        settingFont = sharedPreferences.getBoolean(Constants.SETTING_FONT, false);
        if (settingFont) {
            fontSize = sharedPreferences.getFloat(Constants.FONT_SIZE, 13f);
            setFontText();
        }
    }


    private void getSavedActivities() {
        activityList = new ArrayList<>();
        try {
            activityList = ActivitiesRepo.GetActivityByProjectDate(projectID, WorkLogDate);

        } catch (Exception e) {
            Logger.error(e);
        }
        if (activityList.size() > 0) {
            for (int i = 0; i < activityList.size(); i++) {
                editor.putString(Constants.LOCATION_ID, String.valueOf(activityList.get(i).LocationId));
                editor.apply();

                saveLiveActivity(activityList.get(i), i);
            }
//2022-03-11T11:58:00Z
        } else {
            getSavedContract();
        }

    }
    //new live data method


     /*       LiveData<OrderRepo.BidStatusResponse> req = new OrderRepo().bidStatusAPI(context, sharedPreferences, gsonObject, confirmDialog);
            req.observe(fragment.getViewLifecycleOwner(), response -> {
                confirmDialog.hide();
                if (response.getConfirm.getStatusCode().equals("200")) {
                    if (response.getConfirm.getData() == null) {
                        Toast.makeText(activity, "" + response.getConfirm.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Data data = response.getConfirm.getData();
                        getData(data);
                    }

                    //   Log.e(TAG, "==Bids STATUS ==" + response.body().getMessage());

                }else{
                    Toast.makeText(context, "" + response.getConfirm.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Logger.error(e);
        }
    }
*/


    private void saveLiveActivity(ActivityModel visitModel, int i) {

        try {
            dialogActivity = new ProgressDialog(this);
            try {
                if (i == activityList.size() - 1) {
                    dialogActivity.show();
                }
            } catch (WindowManager.BadTokenException e) {

            }
            dialogActivity.setCancelable(false);
            dialogActivity.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialogActivity.setContentView(R.layout.custom_loading_layout);

            JSONObject user = new JSONObject();

            JsonObject gsonObject = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

            String val = sdf.format(new Date(System.currentTimeMillis()));

            RequestBody reqv = RequestBody.create(MediaType.parse("text/plain"), "");
            RequestBody Id = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(0));
            RequestBody WorkLogId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(workLogId));
            RequestBody LocationId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(visitModel.LocationId));
            RequestBody ActivityId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(visitModel.ActivityId));
            RequestBody FileId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(0));
            RequestBody File = RequestBody.create(MediaType.parse("text/plain"), "");
           // RequestBody Latitude = RequestBody.create(MediaType.parse("text/plain"), "89.3");
            //RequestBody Longitude = RequestBody.create(MediaType.parse("text/plain"), "153.5");
            RequestBody Latitude = RequestBody.create(MediaType.parse("text/plain"), "89.3");
            RequestBody Longitude = RequestBody.create(MediaType.parse("text/plain"), "153.5");
            RequestBody IsCompleted = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(visitModel.IsCompleted));
            RequestBody IsCompliant = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(visitModel.IsCompliant));
            RequestBody Note = RequestBody.create(MediaType.parse("text/plain"), visitModel.note);
            RequestBody AddedOn = RequestBody.create(MediaType.parse("text/plain"), visitModel.AddedOn);
            RequestBody LastModOn = RequestBody.create(MediaType.parse("text/plain"), "");
            RequestBody LastModBy = RequestBody.create(MediaType.parse("text/plain"), "");
            RequestBody AddedBy = RequestBody.create(MediaType.parse("text/plain"), "");
            MultipartBody.Part body = null;
            if (visitModel.imagepath.isEmpty()) {
                RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");

                body = MultipartBody.Part.createFormData("File", "", attachmentEmpty);

            } else {
                File file = new File(visitModel.imagepath);
                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("multipart/form-data"), file);

                body = MultipartBody.Part.createFormData("File", file.getName(), requestFile);

            }
            List<FilesModel> filesModels = FilesRepo.Get(visitModel.itemGuId);
            MultipartBody.Part[] surveyImagesParts = new MultipartBody.Part[visitModel.imagepath.length()];
            if (filesModels.size() > 0) {
                surveyImagesParts = new MultipartBody.Part[filesModels.size()];
            }
            for (int index = 0; index < filesModels.size(); index++) {
                Log.d(TAG, "requestUploadSurvey: survey image " +
                        index +
                        "  " +
                        filesModels.get(index)
                                .getFullPath());
                File fileNew = new File(filesModels.get(index).getFullPath());
                RequestBody surveyBody = RequestBody.create(MediaType.parse("image/*"),
                        fileNew);
                surveyImagesParts[index] = MultipartBody.Part.createFormData("File",
                        fileNew.getName(),
                        surveyBody);
            }

            LiveData<SaveVisitLogModel> req = new ActivitiesRepository().saveActivityRepo(CorrectionsActivity.this, sharedPreferences, Id, WorkLogId, ActivityId, Note, FileId, surveyImagesParts, LocationId, LastModBy, AddedBy, IsCompliant, IsCompleted, Latitude, Longitude, AddedOn, LastModOn, dialogActivity);
            req.observe(this, response -> {
                if (response != null) {
                    dialogActivity.hide();

                    try {
                        Long idValue = activityList.get(activityList.size() - 1).getId();

                        ActivitiesRepo.Delete(visitModel.getId().intValue());

                    } catch (Exception e) {
                        Logger.error(e);
                    }
                    if (fromReport) {
                        if (i == activityList.size() - 1) {
                            getSavedContract();
                        }

                    }

                } else {

                    Toast.makeText(CorrectionsActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();

                }

            });

        } catch (Exception e) {
            dialogActivity.dismiss();
            Logger.error(e);
        }

    }

    private void getSavedContract() {
        contractList = new ArrayList<>();

        try {
            contractList = SubContractorRepo.GetContractByProjectDate(projectID, WorkLogDate);
        } catch (Exception e) {
            Logger.error(e);
        }

        if (contractList.size() > 0) {
            for (int i = 0; i < contractList.size(); i++) {
                saveLiveContract(contractList.get(i), i);
            }

        } else {
            getSavedVisit();
        }

    }

    private void saveContract(SubContractActivity visitModel, int i) {

        try {
            dialogContract = new ProgressDialog(CorrectionsActivity.this);
            try {
                if (i == contractList.size() - 1) {
                    dialogContract.show();
                }

            } catch (WindowManager.BadTokenException e) {

            }
            dialogContract.setCancelable(false);
            dialogContract.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialogContract.setContentView(R.layout.custom_loading_layout);

            JSONObject user = new JSONObject();

            JsonObject gsonObject = null;
            try {
                user.put("Id", 0);
                user.put("WorkLogId", workLogId);
                user.put("LocationId", visitModel.LocationId);
                user.put("ActivityId", visitModel.ActivityId);
                user.put("FileId", 1);
                user.put("File", "");
                user.put("Latitude", "89.50");
                user.put("Longitude", "155.05");
                user.put("IsCompleted", true);
                user.put("IsCompliant", false);
                user.put("EventTimeValue", visitModel.AddedOn);
                user.put("ArrivalTimeValue", visitModel.ArrivalTime);
                user.put("DepartureTimeValue", visitModel.DepartureTime);
                user.put("LastModOn", visitModel.LastModOn);
                user.put("Note", visitModel.Note);

                JsonParser jsonParser = new JsonParser();
                gsonObject = (JsonObject) jsonParser.parse(user.toString());

            } catch (JSONException e) {
                Logger.error(e);
            }

            MultipartBody.Part body = null;
            if (visitModel.imagepath.isEmpty()) {
                RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");

                body = MultipartBody.Part.createFormData("File", "", attachmentEmpty);

            } else {
                File file = new File(visitModel.imagepath);
                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("multipart/form-data"), file);

                body = MultipartBody.Part.createFormData("File", file.getName(), requestFile);

            }
            List<FilesModel> filesModels = FilesRepo.Get(visitModel.itemGuId);
            MultipartBody.Part[] surveyImagesParts = new MultipartBody.Part[visitModel.imagepath.length()];
            if (filesModels.size() > 0) {
                surveyImagesParts = new MultipartBody.Part[filesModels.size()];
            }
            for (int index = 0; index < filesModels.size(); index++) {
                Log.d(TAG, "requestUploadSurvey: survey image " +
                        index +
                        "  " +
                        filesModels.get(index)
                                .getFullPath());
                File fileNew = new File(filesModels.get(index).getFullPath());
                RequestBody surveyBody = RequestBody.create(MediaType.parse("image/*"),
                        fileNew);
                surveyImagesParts[index] = MultipartBody.Part.createFormData("File",
                        fileNew.getName(),
                        surveyBody);
            }
            RequestBody Id = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(0));
            RequestBody WorkLogId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(workLogId));
            RequestBody LocationId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(visitModel.LocationId));
            RequestBody ActivityId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(visitModel.ActivityId));
            RequestBody FileId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(0));
            RequestBody File = RequestBody.create(MediaType.parse("text/plain"), "");
            RequestBody Latitude = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(89.3));
            RequestBody Longitude = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(153.5));
            RequestBody IsCompleted = RequestBody.create(MediaType.parse("text/plain"), visitModel.IsCompleted);
            RequestBody IsCompliant = RequestBody.create(MediaType.parse("text/plain"), visitModel.IsCompliant);
            RequestBody Note = RequestBody.create(MediaType.parse("text/plain"), visitModel.Note);
            RequestBody noOfWorker = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(visitModel.NoOfWorkers));
            RequestBody AddedOn = RequestBody.create(MediaType.parse("text/plain"), visitModel.AddedOn);
            RequestBody LastModOn = RequestBody.create(MediaType.parse("text/plain"), "");
            RequestBody LastModBy = RequestBody.create(MediaType.parse("text/plain"), "");
            RequestBody AddedBy = RequestBody.create(MediaType.parse("text/plain"), "");
            RequestBody departTime = RequestBody.create(MediaType.parse("text/plain"), visitModel.DepartureTime);
            RequestBody arrivalTime = RequestBody.create(MediaType.parse("text/plain"), visitModel.ArrivalTime);
            RequestBody contractorName = RequestBody.create(MediaType.parse("text/plain"), visitModel.contractorName);

            SubContractService userService = ApiClientClass.getApiClientDev(CorrectionsActivity.this).create(SubContractService.class);
            Call<SaveVisitLogModel> call = userService.saveSubContractMulti(Id, WorkLogId, ActivityId, LocationId, LastModBy, Note, FileId, noOfWorker, surveyImagesParts, AddedBy, IsCompliant, IsCompleted, arrivalTime, departTime, AddedOn, LastModOn, LastModOn, contractorName, LastModOn);

            CommonMethods.showLog(TAG, "Contractor log gsonObject  " + gsonObject);
            if (call != null) {
                call.enqueue(new Callback<SaveVisitLogModel>() {
                    @Override
                    public void onResponse(Call<SaveVisitLogModel> call, Response<SaveVisitLogModel> response) {
                        dialogContract.hide();

                        try {
                            if (response.body() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                                Long idValue = contractList.get(contractList.size() - 1).getId();

                                SubContractorRepo.Delete(visitModel.getId().intValue());


                            } else {
                                CommonMethods.showToast(CorrectionsActivity.this, response.body().getMessage());
                            }

                        } catch (Exception e) {
                            // CommonMethods.showToast(SubContractorActivity.this,getString(R.string.something_wrong));
                            Logger.error(e);
                        }
                        if (fromReport) {
                            if (i == contractList.size() - 1) {
                                getSavedVisit();
                            }

                        }


                    }

                    @Override
                    public void onFailure(Call<SaveVisitLogModel> call, Throwable t) {
                        dialogContract.hide();
                        CommonMethods.showToast(CorrectionsActivity.this, getResources().getString(R.string.something_wrong));
                        CommonMethods.showLog(TAG, "Failure " + t.getMessage());
                    }
                });
            }

        } catch (Exception e) {
            dialogContract.dismiss();
            Log.e(TAG, e.getMessage());
        }


    }


    private void saveLiveContract(SubContractActivity visitModel, int i) {

        try {
            dialogContract = new ProgressDialog(CorrectionsActivity.this);
            try {
                if (i == contractList.size() - 1) {
                    dialogContract.show();
                }

            } catch (WindowManager.BadTokenException e) {

            }
            dialogContract.setCancelable(false);
            dialogContract.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialogContract.setContentView(R.layout.custom_loading_layout);

            JSONObject user = new JSONObject();

            JsonObject gsonObject = null;
            try {
                user.put("Id", 0);
                user.put("WorkLogId", workLogId);
                user.put("LocationId", visitModel.LocationId);
                user.put("ActivityId", visitModel.ActivityId);
                user.put("FileId", 1);
                user.put("File", "");
                user.put("Latitude", "89.50");
                user.put("Longitude", "155.05");
                user.put("IsCompleted", true);
                user.put("IsCompliant", false);
                user.put("EventTimeValue", visitModel.AddedOn);
                user.put("ArrivalTimeValue", visitModel.ArrivalTime);
                user.put("DepartureTimeValue", visitModel.DepartureTime);
                user.put("LastModOn", visitModel.LastModOn);
                user.put("Note", visitModel.Note);

                JsonParser jsonParser = new JsonParser();
                gsonObject = (JsonObject) jsonParser.parse(user.toString());

            } catch (JSONException e) {
                Logger.error(e);
            }

            MultipartBody.Part body = null;
            if (visitModel.imagepath.isEmpty()) {
                RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");

                body = MultipartBody.Part.createFormData("File", "", attachmentEmpty);

            } else {
                File file = new File(visitModel.imagepath);
                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("multipart/form-data"), file);

                body = MultipartBody.Part.createFormData("File", file.getName(), requestFile);

            }
            List<FilesModel> filesModels = FilesRepo.Get(visitModel.itemGuId);
            MultipartBody.Part[] surveyImagesParts = new MultipartBody.Part[visitModel.imagepath.length()];
            if (filesModels.size() > 0) {
                surveyImagesParts = new MultipartBody.Part[filesModels.size()];
            }
            for (int index = 0; index < filesModels.size(); index++) {
                Log.d(TAG, "requestUploadSurvey: survey image " +
                        index +
                        "  " +
                        filesModels.get(index)
                                .getFullPath());
                File fileNew = new File(filesModels.get(index).getFullPath());
                RequestBody surveyBody = RequestBody.create(MediaType.parse("image/*"),
                        fileNew);
                surveyImagesParts[index] = MultipartBody.Part.createFormData("File",
                        fileNew.getName(),
                        surveyBody);
            }
            RequestBody Id = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(0));
            RequestBody WorkLogId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(workLogId));
            RequestBody LocationId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(visitModel.LocationId));
            RequestBody ActivityId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(visitModel.ActivityId));
            RequestBody FileId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(0));
            RequestBody File = RequestBody.create(MediaType.parse("text/plain"), "");
            RequestBody Latitude = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(89.3));
            RequestBody Longitude = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(153.5));
            RequestBody IsCompleted = RequestBody.create(MediaType.parse("text/plain"), visitModel.IsCompleted);
            RequestBody IsCompliant = RequestBody.create(MediaType.parse("text/plain"), visitModel.IsCompliant);
            RequestBody Note = RequestBody.create(MediaType.parse("text/plain"), visitModel.Note);
            RequestBody noOfWorker = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(visitModel.NoOfWorkers));
            RequestBody AddedOn = RequestBody.create(MediaType.parse("text/plain"), visitModel.AddedOn);
            RequestBody LastModOn = RequestBody.create(MediaType.parse("text/plain"), "");
            RequestBody LastModBy = RequestBody.create(MediaType.parse("text/plain"), "");
            RequestBody AddedBy = RequestBody.create(MediaType.parse("text/plain"), "");
            RequestBody departTime = RequestBody.create(MediaType.parse("text/plain"), visitModel.DepartureTime);
            RequestBody arrivalTime = RequestBody.create(MediaType.parse("text/plain"), visitModel.ArrivalTime);
            RequestBody contractorName = RequestBody.create(MediaType.parse("text/plain"), visitModel.contractorName);

            LiveData<SaveVisitLogModel> req = new SubContractRepository().saveContractRepo(CorrectionsActivity.this, sharedPreferences, dialogActivity, Id, WorkLogId, ActivityId, LocationId, LastModBy, Note, FileId, noOfWorker, surveyImagesParts, AddedBy, IsCompliant, IsCompleted, arrivalTime, departTime, AddedOn, LastModOn, LastModOn, contractorName, LastModOn);
            req.observe(this, response -> {
                if (response != null) {
                    dialogContract.hide();

                    try {
                        Long idValue = contractList.get(contractList.size() - 1).getId();

                        SubContractorRepo.Delete(visitModel.getId().intValue());

                    } catch (Exception e) {
                        Logger.error(e);
                    }
                    if (fromReport) {
                        if (i == contractList.size() - 1) {
                            getSavedVisit();
                        }

                    }
                } else {
                    CommonMethods.showToast(CorrectionsActivity.this, response.getMessage());

                }
            });


            CommonMethods.showLog(TAG, "Contractor log gsonObject  " + gsonObject);

        } catch (Exception e) {
            dialogContract.dismiss();
            Log.e(TAG, e.getMessage());
        }


    }

    private void getSavedVisit() {
        visitList = new ArrayList<>();

        try {
            visitList = VisitLogRepo.GetVisitByProjectDate(projectID, WorkLogDate);

        } catch (Exception e) {
            Logger.error(e);
        }

        if (visitList.size() > 0) {
            for (int i = 0; i < visitList.size(); i++) {
                saveLiveVisit(visitList.get(i), i);
            }

        } else {
            getCorrectionList();
        }


    }

    private void saveLiveVisit(VisitLog visitModel, int i) {

        try {

            dialogVisit = new ProgressDialog(this);
            try {
                if (i == visitList.size() - 1) {
                    dialogVisit.show();
                }

            } catch (WindowManager.BadTokenException e) {

            }
            dialogVisit.setCancelable(false);
            dialogVisit.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialogVisit.setContentView(R.layout.custom_loading_layout);

            JSONObject user = new JSONObject();

            JsonObject gsonObject = null;
            try {
                user.put("Id", 0);
                user.put("WorkLogId", workLogId);
                user.put("LocationId", visitModel.LocationId);
                user.put("VisitReason", visitModel.VisitReason);
                user.put("PersonName", visitModel.PersonName);
                user.put("CompanyName", visitModel.CompanyName);
                user.put("Title", visitModel.Title);
                user.put("Note", visitModel.Note);

                user.put("ArrivalTimeValue", visitModel.ArrivalTime);
                user.put("DepartureTimeValue", visitModel.DepartureTime);
                user.put("EventTimeValue", visitModel.AddedOn);

                JsonParser jsonParser = new JsonParser();
                gsonObject = (JsonObject) jsonParser.parse(user.toString());

            } catch (JSONException e) {
                Logger.error(e);
            }

            LiveData<SaveVisitLogModel> req = new VisitlogRepo().saveVisitLogApi(CorrectionsActivity.this, sharedPreferences, gsonObject, dialogVisit);
            req.observe(this, response -> {
                if (response != null) {

                    dialogVisit.hide();
                    try {
                        Long idValue = visitList.get(visitList.size() - 1).getId();
                        VisitLogRepo.Delete(visitModel.getId().intValue());

                    } catch (Exception e) {
                        //CommonMethods.showToast(VisitsActivity.this,getString(R.string.invalid_login));
                        Logger.error(e);
                    }
                    if (fromReport) {
                        if (i == visitList.size() - 1) {
                            getCorrectionList();
                        }

                    }

                } else {
                    CommonMethods.showToast(CorrectionsActivity.this, response.getMessage());

                }
            });


        } catch (Exception e) {
            dialogVisit.dismiss();
            Log.e(TAG, e.getMessage());
        }


    }

    private void checkDarkMode() {
        if (CommonMethods.checkDarkMode(CorrectionsActivity.this)) {
            txtHeader.setTextColor(ContextCompat.getColor(CorrectionsActivity.this, R.color.white));
            txtNoData.setTextColor(ContextCompat.getColor(CorrectionsActivity.this, R.color.white));
        }
    }


}
