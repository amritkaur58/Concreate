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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dailyreporting.app.R;
import com.dailyreporting.app.WebApis.ApiClientClass;
import com.dailyreporting.app.WebApis.SubContractService;
import com.dailyreporting.app.adapters.SubContractorAdapter;
import com.dailyreporting.app.database.FilesRepo;
import com.dailyreporting.app.database.SubContractorRepo;
import com.dailyreporting.app.models.FilesModel;
import com.dailyreporting.app.models.SaveVisitLogModel;
import com.dailyreporting.app.models.SubContractActivity;
import com.dailyreporting.app.utils.CommonMethods;
import com.dailyreporting.app.utils.Constants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.orhanobut.logger.Logger;

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

public class SubContractorActivity extends AppCompatActivity implements View.OnClickListener {
    String TAG = getClass().getSimpleName();
    private RecyclerView recyclerView;
    private SubContractorAdapter subContractorAdapter;
    private RelativeLayout rlAdd;
    private ImageView imgBack;
    private List<SubContractActivity> listSubContractor = new ArrayList<>();
    private TextView txtNoData;
    private TextView txtCity;
    private TextView dateTV, txtDay;
    private ImageView saveIV;
    private List<SubContractActivity> contractList = new ArrayList<>();
    private ProgressDialog dialog;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private RelativeLayout nextRL;
    private String projectSelected = "";
    private boolean fromReport = false;
    private String workLogId = "11";
    private TextView txtEdit;
    private RelativeLayout stagingRL;
    private int projectId = 0;
    private String WorkLogDate = "";
    private String workLogDay = "";
    private boolean settingFont = false;
    private float fontSize = 13f;
    private TextView nextTV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcontractor);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        projectSelected = sharedPreferences.getString(Constants.PROJECT_SELECTED, "");

        Toolbar toolbar = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            toolbar = findViewById(R.id.toolbar);
        }
        if (toolbar != null) {
            txtEdit = toolbar.findViewById(R.id.txtEdit);
            imgBack = toolbar.findViewById(R.id.imgBack);
            stagingRL = toolbar.findViewById(R.id.stagingRL);
            saveIV = toolbar.findViewById(R.id.saveIV);
            txtEdit.setText(getString(R.string.subcontractor));
        }

        recyclerView = findViewById(R.id.recyclerView);
        txtNoData = findViewById(R.id.txtNoData);
        nextTV = findViewById(R.id.nextTV);
        rlAdd = findViewById(R.id.rlAdd);
        txtCity = findViewById(R.id.txtCity);
        dateTV = findViewById(R.id.dateTV);
        nextRL = findViewById(R.id.nextRL);
        txtDay = findViewById(R.id.txtDay);
        imgBack = findViewById(R.id.imgBack);

        String currentDate = CommonMethods.getCurrentDateByFormat("dd MMM, yyyy");
        String currentDay = CommonMethods.getCurrentDay();
        dateTV.setText(currentDate);
        txtDay.setText(currentDay);
        rlAdd.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        nextRL.setOnClickListener(this);
        saveIV.setOnClickListener(this);
        txtCity.setText(projectSelected);
        WorkLogDate = CommonMethods.getCurrentDateByFormat("dd MMM, yyyy");
        if (getIntent().hasExtra("WorkLogDate")) {
            WorkLogDate = getIntent().getStringExtra("WorkLogDate");
            dateTV.setText(WorkLogDate);
            try {
                workLogDay = CommonMethods.getDayFromDate(WorkLogDate);
                txtDay.setText(workLogDay);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if (getIntent().hasExtra(Constants.FROM_REPORT)) {
            workLogId = getIntent().getStringExtra(Constants.WORKLOG_ID);
            fromReport = true;
            nextRL.setVisibility(View.VISIBLE);
            rlAdd.setVisibility(View.GONE);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setNestedScrollingEnabled(false);

        if (!CommonMethods.Mode.isLive(SubContractorActivity.this)) {
            stagingRL.setVisibility(View.VISIBLE);
        }
        checkDarkMode();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlAdd:
                Intent intent1 = new Intent(SubContractorActivity.this, AddSubContractorActivity.class);
                intent1.putExtra("WorkLogDate", WorkLogDate);
                startActivity(intent1);
                break;

            case R.id.nextRL:
                if (fromReport) {
                    Intent intent = new Intent(SubContractorActivity.this, VisitsActivity.class);
                    intent.putExtra(Constants.FROM_REPORT, "true");
                    intent.putExtra("WorkLogDate", WorkLogDate);
                    intent.putExtra(Constants.WORKLOG_ID, workLogId);
                    startActivity(intent);
                    finish();
                }
                break;

            case R.id.imgBack:
                onBackPressed();
                break;

            case R.id.saveIV:
                getSavedActivities();
                break;
        }

    }


    private void getSavedActivities() {
        contractList = SubContractorRepo.GetAll();
        if (contractList.size() > 0) {
            for (int i = 0; i < contractList.size(); i++) {
                saveActivity(contractList.get(i), i);
            }

        }

    }

    private void saveActivity(SubContractActivity visitModel, int i) {

        try {

            dialog = new ProgressDialog(SubContractorActivity.this);
            try {
                if (i == contractList.size() - 1) {
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

               /* user.put("ArrivalTime", val);
                user.put("DepartureTime", val);
*/
                JsonParser jsonParser = new JsonParser();
                gsonObject = (JsonObject) jsonParser.parse(user.toString());

            } catch (JSONException e) {
                e.printStackTrace();

            }


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

            String val = sdf.format(new Date(System.currentTimeMillis()));
            File file = new File(visitModel.imagepath);
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);


            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("File", file.getName(), requestFile);
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

            SubContractService userService = ApiClientClass.getApiClientDev(SubContractorActivity.this).create(SubContractService.class);
            Call<SaveVisitLogModel> call = userService.saveSubContractMulti(Id, WorkLogId, ActivityId, LocationId, LastModBy, Note, FileId, noOfWorker, surveyImagesParts, AddedBy, IsCompliant, IsCompleted, arrivalTime, departTime, AddedOn, LastModOn, LastModOn, contractorName, LastModOn);

            CommonMethods.showLog(TAG, "Contractor log gsonObject  " + gsonObject);
            if (call != null) {
                call.enqueue(new Callback<SaveVisitLogModel>() {
                    @Override
                    public void onResponse(Call<SaveVisitLogModel> call, Response<SaveVisitLogModel> response) {
                        dialog.hide();

                        try {
                            if (response.body() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                                Long idValue = contractList.get(contractList.size() - 1).getId();

                                SubContractorRepo.Delete(visitModel.getId().intValue());
                                listSubContractor.remove(i);
                                subContractorAdapter.notifyDataSetChanged();
                                if (listSubContractor.size() <= 0) {
                                    txtNoData.setVisibility(View.VISIBLE);
                                }


                            } else {
                                CommonMethods.showToast(SubContractorActivity.this, response.body().getMessage());
                            }

                        } catch (Exception e) {
                            // CommonMethods.showToast(SubContractorActivity.this,getString(R.string.something_wrong));
                            e.printStackTrace();
                        }
                        if (fromReport) {
                            Intent intent = new Intent(SubContractorActivity.this, VisitsActivity.class);
                            intent.putExtra(Constants.FROM_REPORT, "true");
                            intent.putExtra(Constants.WORKLOG_ID, workLogId);
                            startActivity(intent);
                            finish();
                        }


                    }

                    @Override
                    public void onFailure(Call<SaveVisitLogModel> call, Throwable t) {
                        dialog.hide();
                        CommonMethods.showToast(SubContractorActivity.this, getResources().getString(R.string.something_wrong));
                        CommonMethods.showLog(TAG, "Failure " + t.getMessage());
                    }
                });
            }

        } catch (Exception e) {
            dialog.dismiss();
            Log.e(TAG, e.getMessage());
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        projectId = sharedPreferences.getInt(Constants.PROJECT_ID, 0);
        List<SubContractActivity> data = new ArrayList<>();
        try {

            data = SubContractorRepo.GetContractByProjectDate(projectId, WorkLogDate);
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }

        if (data != null && data.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            txtNoData.setVisibility(View.GONE);
            listSubContractor = new ArrayList<>();
            listSubContractor.addAll(SubContractorRepo.GetContractByProjectDate(projectId, WorkLogDate));
            subContractorAdapter = new SubContractorAdapter(SubContractorActivity.this, listSubContractor);
            recyclerView.setAdapter(subContractorAdapter);
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

    private void setFontText() {
        txtEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        nextTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtDay.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtCity.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        dateTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtNoData.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
    }

    private void checkDarkMode() {
        if (CommonMethods.checkDarkMode(SubContractorActivity.this)) {
            txtEdit.setTextColor(ContextCompat.getColor(SubContractorActivity.this, R.color.white));
            txtNoData.setTextColor(ContextCompat.getColor(SubContractorActivity.this, R.color.white));
        }
    }

}


























