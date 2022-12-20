package com.dailyreporting.app.activities;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
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
import com.dailyreporting.app.WebApis.ActivityService;
import com.dailyreporting.app.WebApis.ApiClientClass;
import com.dailyreporting.app.adapters.ActivitiesAdapter;
import com.dailyreporting.app.database.ActivitiesRepo;
import com.dailyreporting.app.database.FilesRepo;
import com.dailyreporting.app.models.ActivityModel;
import com.dailyreporting.app.models.FilesModel;
import com.dailyreporting.app.models.SaveVisitLogModel;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dailyreporting.app.utils.Constants.MyPREFERENCES;

public class ListActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView saveIV;
    String TAG = getClass().getSimpleName();
    RelativeLayout nextRL;
    private RecyclerView recyclerView;
    private ActivitiesAdapter activitiesAdapter;
    private RelativeLayout rlAdd;
    private ImageView imgBack;
    private List<ActivityModel> listAddActivity = new ArrayList<>();
    private TextView txtNoData;
    private TextView dateTV;
    private TextView txtDay;
    private MultipartBody.Part attachmentBody;
    private List<ActivityModel> activityList = new ArrayList<>();
    private ProgressDialog dialog;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Date date;
    private TextView txtCity;
    private String projectSelected = "";
    private boolean fromReport = false;
    private String workLogId = "11";
    private TextView txtEdit;
    private RelativeLayout stagingRL;
    private int projectId = 0;
    private String workLogDate = "";
    private String workLogDay = "";
    private boolean settingFont = false;
    private float fontSize = 13f;
    private TextView nextTV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);
        Toolbar toolbar = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            toolbar = findViewById(R.id.toolbar);
        }
        if (toolbar != null) {
            txtEdit = toolbar.findViewById(R.id.txtEdit);
            imgBack = toolbar.findViewById(R.id.imgBack);
            stagingRL = toolbar.findViewById(R.id.stagingRL);
            saveIV = toolbar.findViewById(R.id.saveIV);
            txtEdit.setText(getString(R.string.activities));
            saveIV.setImageDrawable(getDrawable(R.drawable.ic_folder));
            saveIV.setVisibility(View.INVISIBLE);
        }

        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        workLogDate = CommonMethods.getCurrentDateByFormat("dd MMM, yyyy");
        projectSelected = sharedPreferences.getString(Constants.PROJECT_SELECTED, "");
        recyclerView = findViewById(R.id.recyclerView);
        txtNoData = findViewById(R.id.txtNoData);
        rlAdd = findViewById(R.id.rlAdd);
        nextRL = findViewById(R.id.nextRL);
        nextTV = findViewById(R.id.nextTV);
        dateTV = findViewById(R.id.dateTV);
        txtDay = findViewById(R.id.txtDay);

        nextRL = findViewById(R.id.nextRL);
        txtCity = findViewById(R.id.txtCity);
        txtCity.setText(projectSelected);
        imgBack.setOnClickListener(this);
        saveIV.setOnClickListener(this);
        nextRL.setOnClickListener(this);

        rlAdd.setOnClickListener(this);
        String currentDate = CommonMethods.getCurrentDateByFormat("dd MMM, yyyy");
        dateTV.setText(currentDate);
        String currentDay = CommonMethods.getCurrentDay();
        txtDay.setText(currentDay);
        if (getIntent().hasExtra("WorkLogDate")) {
            workLogDate = getIntent().getStringExtra("WorkLogDate");
            dateTV.setText(workLogDate);
            try {
                workLogDay = CommonMethods.getDayFromDate(workLogDate);
                txtDay.setText(workLogDay);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (getIntent().hasExtra(Constants.FROM_REPORT)) {
            try {
                dateTV.setClickable(false);
                dateTV.setFocusable(false);
            } catch (Exception e) {
                e.printStackTrace();
            }

            workLogId = getIntent().getStringExtra(Constants.WORKLOG_ID);
            fromReport = true;
            nextRL.setVisibility(View.VISIBLE);
            rlAdd.setVisibility(View.GONE);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setNestedScrollingEnabled(false);

        if (!CommonMethods.Mode.isLive(ListActivity.this)) {
            stagingRL.setVisibility(View.VISIBLE);
        }

        checkDarkMode();

    }

    private void checkDarkMode() {
        if (CommonMethods.checkDarkMode(ListActivity.this)) {
            txtEdit.setTextColor(ContextCompat.getColor(ListActivity.this, R.color.white));
            txtNoData.setTextColor(ContextCompat.getColor(ListActivity.this, R.color.white));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlAdd:
                Intent intent1 = new Intent(ListActivity.this, AddActivitity.class);
                intent1.putExtra("WorkLogDate", workLogDate);
                startActivity(intent1);
                break;
            case R.id.dateTV:
                try {
                    setDatePicker(ListActivity.this);

                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;
            case R.id.nextRL:
                if (fromReport) {
                    Intent intent = new Intent(ListActivity.this, SubContractorActivity.class);
                    intent.putExtra(Constants.FROM_REPORT, "true");
                    intent.putExtra("WorkLogDate", workLogDate);
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
        activityList = ActivitiesRepo.GetAll();
        if (activityList.size() > 0) {
            for (int i = 0; i < activityList.size(); i++) {
                editor.putString(Constants.LOCATION_ID, String.valueOf(activityList.get(i).LocationId));
                editor.apply();

                saveActivity(activityList.get(i), i);
            }

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        projectId = sharedPreferences.getInt(Constants.PROJECT_ID, 0);
        setActivityList();
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

    void setActivityList() {
        List<ActivityModel> data = new ArrayList<>();

        try {

            data = ActivitiesRepo.GetActivityByProjectDate(projectId, workLogDate);
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }

        if (data != null && data.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            txtNoData.setVisibility(View.GONE);
            listAddActivity = new ArrayList<>();
            listAddActivity.addAll(data);
            activitiesAdapter = new ActivitiesAdapter(ListActivity.this, listAddActivity);
            recyclerView.setAdapter(activitiesAdapter);
        } else {
            txtNoData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    public void checkList(List<ActivityModel> listAddActivity) {
        if (listAddActivity.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            txtNoData.setVisibility(View.GONE);
        } else {
            txtNoData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

    }

    private void saveActivity(ActivityModel visitModel, int i) {

        try {

            dialog = new ProgressDialog(this);
            try {
                if (i == activityList.size() - 1) {
                    dialog.show();
                }

            } catch (WindowManager.BadTokenException e) {

            }
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.custom_loading_layout);

            JSONObject user = new JSONObject();

            JsonObject gsonObject = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

            String val = sdf.format(new Date(System.currentTimeMillis()));

            try {
                user.put("Id", 0);
                user.put("WorkLogId", workLogId);
                user.put("LocationId", visitModel.LocationId);
                user.put("ActivityId", visitModel.ActivityId);
                user.put("FileId", 1);
                user.put("File", "");
                user.put("Latitude", "89.50");
                user.put("Longitude", "152.10");
                user.put("IsCompleted", true);
                user.put("IsCompliant", false);
                user.put("AddedOn", val);
                user.put("LastModOn", visitModel.LastModOn);
                user.put("Note", visitModel.note);

                JsonParser jsonParser = new JsonParser();
                gsonObject = (JsonObject) jsonParser.parse(user.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody reqv = RequestBody.create(MediaType.parse("text/plain"), "");
            File file = new File(visitModel.imagepath);
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);

// MultipartBody.Part is used to send also the actual file name

            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("File", file.getName(), requestFile);
            RequestBody Id = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(0));
            RequestBody WorkLogId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(workLogId));
            RequestBody LocationId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(visitModel.LocationId));
            RequestBody ActivityId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(visitModel.ActivityId));
            RequestBody FileId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(0));
            RequestBody File = RequestBody.create(MediaType.parse("text/plain"), "");
            RequestBody Latitude = RequestBody.create(MediaType.parse("text/plain"), "89.3");
            RequestBody Longitude = RequestBody.create(MediaType.parse("text/plain"), "153.5");
            RequestBody IsCompleted = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(visitModel.IsCompleted));
            RequestBody IsCompliant = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(visitModel.IsCompliant));
            RequestBody Note = RequestBody.create(MediaType.parse("text/plain"), visitModel.note);
            RequestBody AddedOn = RequestBody.create(MediaType.parse("text/plain"), visitModel.AddedOn);
            RequestBody LastModOn = RequestBody.create(MediaType.parse("text/plain"), "");
            RequestBody LastModBy = RequestBody.create(MediaType.parse("text/plain"), "");
            RequestBody AddedBy = RequestBody.create(MediaType.parse("text/plain"), "");
           /* int Id = 0;
            int WorkLogId = visitModel.WorkLogId;
            int LocationId = visitModel.LocationId;
            int ActivityId = visitModel.ActivityId;
            int FileId = 0;
            String File = "";
            double Latitude = 89.3;
            double Longitude = 153.5;
            boolean IsCompleted = true;
            boolean IsCompliant  = false;
            String Note = visitModel.note;*/
/*
            int LastModBy = 0;
            int AddedBy = 0;
            String AddedOn = val;
            String  LastModOn = "";*/

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


            RequestBody fileValue = RequestBody.create(MediaType.parse("text/plain"), "0");
            ActivityService userService = ApiClientClass.getApiClientDev(ListActivity.this).create(ActivityService.class);
            Call<SaveVisitLogModel> call = userService.saveActivityListMulti(Id, WorkLogId, ActivityId, Note, FileId, surveyImagesParts, LocationId, LastModBy, AddedBy, IsCompliant, IsCompleted,  AddedOn, LastModOn);

            CommonMethods.showLog(TAG, "Visit log gsonObject  " + gsonObject);
            if (call != null) {
                call.enqueue(new Callback<SaveVisitLogModel>() {
                    @Override
                    public void onResponse(Call<SaveVisitLogModel> call, Response<SaveVisitLogModel> response) {
                        dialog.hide();

                        try {
                            if (response.body() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                                Long idValue = activityList.get(activityList.size() - 1).getId();

                                ActivitiesRepo.Delete(visitModel.getId().intValue());
                                listAddActivity.remove(i);
                                activitiesAdapter.notifyDataSetChanged();
                                if (listAddActivity.size() <= 0) {
                                    txtNoData.setVisibility(View.VISIBLE);
                                }


                            } else {
                                CommonMethods.showToast(ListActivity.this, response.message());
                            }

                        } catch (Exception e) {
                            //  CommonMethods.showToast(ListActivity.this,getString(R.string.something_wrong));
                            e.printStackTrace();
                        }
                        if (fromReport) {
                            Intent intent = new Intent(ListActivity.this, SubContractorActivity.class);
                            intent.putExtra(Constants.FROM_REPORT, "true");
                            intent.putExtra(Constants.WORKLOG_ID, workLogId);
                            startActivity(intent);
                            finish();
                        }

                    }

                    @Override
                    public void onFailure(Call<SaveVisitLogModel> call, Throwable t) {
                        dialog.hide();
                        CommonMethods.showToast(ListActivity.this, getResources().getString(R.string.something_wrong));
                        CommonMethods.showLog(TAG, "Failure " + t.getMessage());
                    }
                });
            }

        } catch (Exception e) {
            dialog.dismiss();
            Log.e(TAG, e.getMessage());
        }


    }

    public void setDatePicker(Context context) {

        int mYear, mMonth, mDay;

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {

                    private String monthValue = "";
                    private String day = "";

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        int month = monthOfYear + 1;
                        if (month < 10) {

                            monthValue = "0" + month;
                        } else {
                            monthValue = String.valueOf(month);
                        }
                        if (dayOfMonth < 10) {

                            day = "0" + dayOfMonth;
                        } else {
                            day = String.valueOf(dayOfMonth);
                        }
                        String dateVal = year + "-" + monthValue + "-" + day;
                        workLogDate = CommonMethods.getWorkLogDate(dateVal);
                        dateTV.setText(workLogDate);
                        setActivityList();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }


}
