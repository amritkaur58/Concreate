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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dailyreporting.app.R;
import com.dailyreporting.app.WebApis.ApiClientClass;
import com.dailyreporting.app.WebApis.VisitLogService;
import com.dailyreporting.app.adapters.VisitsAdapter;
import com.dailyreporting.app.database.VisitLogRepo;
import com.dailyreporting.app.models.SaveVisitLogModel;
import com.dailyreporting.app.models.VisitLog;
import com.dailyreporting.app.utils.CommonMethods;
import com.dailyreporting.app.utils.Constants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dailyreporting.app.utils.Constants.MyPREFERENCES;

public class VisitsActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView recyclerView;
    VisitsAdapter visitsAdapter;
    TextView txtHeader;
    ImageView imgBack;
    RelativeLayout rlAdd;
    ImageView saveIV;
    String TAG = getClass().getSimpleName();
    private TextView txtDay, dateTV;
    private TextView txtNoData;
    private List<VisitLog> listVisitLog = new ArrayList<>();
    private List<VisitLog> visitList = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ProgressDialog dialog;
    private RelativeLayout nextRL;
    private TextView txtCity;
    private String projectSelected = "";
    private boolean fromReport = false;
    private String workLogId = "11";
    private int projectId = 0;
    private String WorkLogDate = "";
    private String worklogDay = "";
    private boolean settingFont = false;
    private float fontSize = 13f;
    private TextView nextTV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visits_log);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        projectSelected = sharedPreferences.getString(Constants.PROJECT_SELECTED, "");
        recyclerView = findViewById(R.id.recyclerView);
        imgBack = findViewById(R.id.imgBack);
        txtCity = findViewById(R.id.txtCity);
        txtDay = findViewById(R.id.txtDay);
        txtNoData = findViewById(R.id.txtNoData);
        dateTV = findViewById(R.id.dateTV);
        rlAdd = findViewById(R.id.rlAdd);
        nextRL = findViewById(R.id.nextRL);
        nextTV = findViewById(R.id.nextTV);
        txtHeader = findViewById(R.id.txtHeader);
        saveIV = findViewById(R.id.saveIV);
        txtHeader.setText(getString(R.string.visits_string));
        txtCity.setText(projectSelected);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setNestedScrollingEnabled(false);
        WorkLogDate = CommonMethods.getCurrentDateByFormat("dd MMM, yyyy");
        String currentDate = CommonMethods.getCurrentDateByFormat("dd MMM, yyyy");
        String currentDay = CommonMethods.getCurrentDay();
        dateTV.setText(currentDate);
        txtDay.setText(currentDay);
        if (getIntent().hasExtra("WorkLogDate")) {
            WorkLogDate = getIntent().getStringExtra("WorkLogDate");
            dateTV.setText(WorkLogDate);

            try {
                worklogDay = CommonMethods.getDayFromDate(WorkLogDate);
                txtDay.setText(worklogDay);
            } catch (Exception e) {
                Logger.e(e.getMessage());
            }
        }

        if (getIntent().hasExtra(Constants.FROM_REPORT)) {
            workLogId = getIntent().getStringExtra(Constants.WORKLOG_ID);
            nextRL.setVisibility(View.VISIBLE);
            rlAdd.setVisibility(View.GONE);
            fromReport = true;
        }
        imgBack.setOnClickListener(this);
        nextRL.setOnClickListener(this);
        rlAdd.setOnClickListener(this);
        saveIV.setOnClickListener(this);


        checkDarkMode();

    }


    private void setFontText() {
        txtDay.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtCity.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        dateTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtNoData.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        nextTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.rlAdd:
                Intent intent = new Intent(VisitsActivity.this, SaveVisitLog.class);
                intent.putExtra("WorkLogDate", WorkLogDate);
                startActivity(intent);
                break;
            case R.id.nextRL:
                if (fromReport) {
                    Intent intent1 = new Intent(VisitsActivity.this, CorrectionsActivity.class);
                    intent1.putExtra(Constants.FROM_REPORT, "true");
                    intent1.putExtra("WorkLogDate", WorkLogDate);
                    intent1.putExtra(Constants.WORKLOG_ID, workLogId);
                    startActivity(intent1);
                    finish();
                }
                break;
            case R.id.saveIV:
                getSavedVisitLog();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        projectId = sharedPreferences.getInt(Constants.PROJECT_ID, 0);
        List<VisitLog> data = new ArrayList<>();

        try {

            data = VisitLogRepo.GetVisitByProjectDate(projectId, WorkLogDate);
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }

        if (data != null && data.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            txtNoData.setVisibility(View.GONE);
            listVisitLog = new ArrayList<>();
            listVisitLog.addAll(VisitLogRepo.GetVisitByProjectDate(projectId, WorkLogDate));
            visitsAdapter = new VisitsAdapter(VisitsActivity.this, listVisitLog);
            recyclerView.setAdapter(visitsAdapter);
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

    private void getSavedVisitLog() {
        visitList = VisitLogRepo.GetAll();
        if (visitList.size() > 0) {
            for (int i = 0; i < visitList.size(); i++) {
                saveVisit(visitList.get(i), i);
            }

        }


    }

    private void saveVisit(VisitLog visitModel, int i) {

        try {

            dialog = new ProgressDialog(this);
            try {
                if (i == visitList.size() - 1) {
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
                e.printStackTrace();
            }

            VisitLogService userService = ApiClientClass.getApiClientDev(VisitsActivity.this).create(VisitLogService.class);
            Call<SaveVisitLogModel> call = userService.saveVisitLog(gsonObject);

            CommonMethods.showLog(TAG, "Visit log gsonObject  " + gsonObject);
            if (call != null) {
                call.enqueue(new Callback<SaveVisitLogModel>() {
                    @Override
                    public void onResponse(Call<SaveVisitLogModel> call, Response<SaveVisitLogModel> response) {
                        dialog.hide();

                        try {
                            if (response.body() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                                Long idValue = visitList.get(visitList.size() - 1).getId();

                                VisitLogRepo.Delete(visitModel.getId().intValue());
                                listVisitLog.remove(i);
                                visitsAdapter.notifyDataSetChanged();
                                if (listVisitLog.size() <= 0) {
                                    txtNoData.setVisibility(View.VISIBLE);
                                }


                            } else {
                                CommonMethods.showToast(VisitsActivity.this, response.body().getMessage());
                            }

                        } catch (Exception e) {
                            //CommonMethods.showToast(VisitsActivity.this,getString(R.string.invalid_login));
                            e.printStackTrace();
                        }
                        if (fromReport) {
                            Intent intent1 = new Intent(VisitsActivity.this, CorrectionsActivity.class);
                            intent1.putExtra(Constants.FROM_REPORT, "true");
                            intent1.putExtra(Constants.WORKLOG_ID, workLogId);
                            startActivity(intent1);
                            finish();
                        }


                    }

                    @Override
                    public void onFailure(Call<SaveVisitLogModel> call, Throwable t) {
                        dialog.hide();
                        CommonMethods.showToast(VisitsActivity.this, getResources().getString(R.string.something_wrong));
                        CommonMethods.showLog(TAG, "Failure " + t.getMessage());
                    }
                });
            }

        } catch (Exception e) {
            dialog.dismiss();
            Log.e(TAG, e.getMessage());
        }


    }

    private void checkDarkMode() {
        if (CommonMethods.checkDarkMode(VisitsActivity.this)) {
            txtHeader.setTextColor(ContextCompat.getColor(VisitsActivity.this, R.color.white));
            txtNoData.setTextColor(ContextCompat.getColor(VisitsActivity.this, R.color.white));
        }
    }


}
