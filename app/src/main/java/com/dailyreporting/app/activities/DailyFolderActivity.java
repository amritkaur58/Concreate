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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dailyreporting.app.R;
import com.dailyreporting.app.WebApis.ApiClientClass;
import com.dailyreporting.app.WebApis.VisitLogService;
import com.dailyreporting.app.adapters.DailyFolderAdapter;
import com.dailyreporting.app.database.DailyFolderRepo;
import com.dailyreporting.app.database.VisitLogRepo;
import com.dailyreporting.app.models.DailyFolder;
import com.dailyreporting.app.models.SaveVisitLogModel;
import com.dailyreporting.app.models.VisitLog;
import com.dailyreporting.app.utils.CommonMethods;
import com.dailyreporting.app.utils.Constants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dailyreporting.app.utils.Constants.MyPREFERENCES;

public class DailyFolderActivity extends AppCompatActivity implements View.OnClickListener,DailyFolderAdapter.InterfaceList {
    RecyclerView recyclerView;
    DailyFolderAdapter adapter;
    ImageView imgBack;
    RelativeLayout rlAdd;
    ImageView saveIV;
    String TAG = getClass().getSimpleName();
    private TextView txtDay, dateTV;
    private TextView txtNoData;
    private List<VisitLog> listVisitLog = new ArrayList<>();
    private List<VisitLog> visitList=new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ProgressDialog dialog;
    TextView txtCity;
    private TextView txtEdit;
    private RelativeLayout stagingRL;
    private String projectSelected ="";
    private boolean fromSelection = false;
    private int projectId =0;
    private String workLogDate="";
    private String worklogDay="";
    private boolean settingFont=false;
    private float fontSize=13f;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_folder);

        Toolbar toolbar = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            toolbar = findViewById(R.id.toolbar);
        }
        if(toolbar!=null)
        {
            txtEdit = toolbar.findViewById(R.id.txtEdit);
            imgBack = toolbar.findViewById(R.id.imgBack);
            stagingRL = toolbar.findViewById(R.id.stagingRL);
            saveIV = toolbar.findViewById(R.id.saveIV);
            txtEdit.setText(getString(R.string.dailyfolder));
        }
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        projectId = sharedPreferences.getInt(Constants.PROJECT_ID, 0);
        projectSelected = sharedPreferences.getString(Constants.PROJECT_SELECTED, "");
        recyclerView = findViewById(R.id.recyclerView);
        txtDay = findViewById(R.id.txtDay);
        txtNoData = findViewById(R.id.txtNoData);
        dateTV = findViewById(R.id.dateTV);
        rlAdd = findViewById(R.id.rlAdd);
        txtCity = findViewById(R.id.txtCity);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setNestedScrollingEnabled(false);
        txtCity.setText(projectSelected);
        workLogDate = CommonMethods.getCurrentDateByFormat("dd MMM, yyyy");
        if(getIntent().hasExtra("selectFolder"))
        {
            fromSelection = true;
            rlAdd.setVisibility(View.GONE);
        }
        String currentDate = CommonMethods.getCurrentDateByFormat("dd MMM,yyyy");
        String currentDay = CommonMethods.getCurrentDay();
        dateTV.setText(currentDate);
        txtDay.setText(currentDay);


        if (getIntent().hasExtra("WorkLogDate")) {
            workLogDate = getIntent().getStringExtra("WorkLogDate");
            dateTV.setText(workLogDate);
            try {
                worklogDay = CommonMethods.getDayFromDate(workLogDate);
                txtDay.setText(worklogDay);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        
        imgBack.setOnClickListener(this);

        rlAdd.setOnClickListener(this);
        saveIV.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.rlAdd:
                Intent intent = new Intent(DailyFolderActivity.this, SaveDailyFolderActivity.class);
                intent.putExtra("WorkLogDate",workLogDate);
                startActivity(intent);
                break;
            case R.id.nextRL:

                break;
            case R.id.saveIV:
                getSavedVisitLog();
                break;
        }
    }

    private void setFontText() {
        txtEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtDay.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtCity.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        dateTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtNoData.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
    }
    @Override
    protected void onResume() {
        super.onResume();
        projectId = sharedPreferences.getInt(Constants.PROJECT_ID, 0);
        List<DailyFolder> data = new ArrayList<>();
        try {
            data  = DailyFolderRepo.GetDailyByProjectDate(projectId,workLogDate);

        }catch (Exception e)
        {
            Logger.e(e.getMessage());
        }
        if (data != null && data.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new DailyFolderAdapter(DailyFolderActivity.this,data,fromSelection);
            recyclerView.setAdapter(adapter);
            txtNoData.setVisibility(View.GONE);

        } else {
            txtNoData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        settingFont =   sharedPreferences.getBoolean(Constants.SETTING_FONT,false);
        if(settingFont)
        {
            fontSize = sharedPreferences.getFloat(Constants.FONT_SIZE,13f);
            setFontText();
        }
    }

    private void getSavedVisitLog() {
        visitList=  VisitLogRepo.GetAll();
        if(visitList.size()>0)
        {
            for(int i =0;i<visitList.size();i++)
            {
                saveVisit(visitList.get(i),i);
            }

        }


    }
    private void saveVisit(VisitLog visitModel, int i) {

        try {

             dialog = new ProgressDialog(this);
            try {
                if(i==visitList.size()-1)
                {
                    dialog.show();
                }

            } catch (WindowManager.BadTokenException e) {

            }
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.custom_loading_layout);

            JSONObject user = new JSONObject();

            JsonObject gsonObject=null;
            try {
                user.put("Id", 0);
                user.put("WorkLogId", visitModel.WorkLogId);
                user.put("LocationId", visitModel.LocationId);
                user.put("VisitReason", visitModel.VisitReason);
                user.put("PersonName", visitModel.PersonName);
                user.put("CompanyName", visitModel.CompanyName);
                user.put("Title", visitModel.Title);
                user.put("Note", visitModel.Note);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

                String val = sdf.format(new Date(System.currentTimeMillis()));
                user.put("ArrivalTime", val);
                user.put("DepartureTime", val);

                JsonParser jsonParser = new JsonParser();
                gsonObject = (JsonObject) jsonParser.parse(user.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }


            VisitLogService userService = ApiClientClass.getApiClientDev(DailyFolderActivity.this).create(VisitLogService.class);
            Call<SaveVisitLogModel> call = userService.saveVisitLog(gsonObject);

            CommonMethods.showLog(TAG, "Visit log gsonObject  " +gsonObject);
            if (call != null) {
                call.enqueue(new Callback<SaveVisitLogModel>() {
                    @Override
                    public void onResponse(Call<SaveVisitLogModel> call, Response<SaveVisitLogModel> response) {
                        dialog.hide();

                        try {
                            if(response.body() != null && response.body().getStatus().equalsIgnoreCase("200") )
                            {
                                Long idValue = visitList.get(visitList.size() - 1).getId();

                                    VisitLogRepo.Delete(visitModel.getId().intValue());
                                    listVisitLog.remove(i);
                                adapter.notifyDataSetChanged();
                                    if(listVisitLog.size()<=0)
                                    {
                                        txtNoData.setVisibility(View.VISIBLE);
                                    }


                            }else
                            {
                                CommonMethods.showToast(DailyFolderActivity.this,response.message());
                            }

                        }catch (Exception e)
                        {
                            CommonMethods.showToast(DailyFolderActivity.this,getString(R.string.something_wrong));

                        }


                    }

                    @Override
                    public void onFailure(Call<SaveVisitLogModel> call, Throwable t) {
                        dialog.hide();
                        CommonMethods.showToast(DailyFolderActivity.this,getResources().getString(R.string.something_wrong));
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
    public void checkItemClickListener(DailyFolder listVisitLog, int position) {
        Intent intent = new Intent();
        intent.putExtra("folderID", listVisitLog.getId());
        setResult(RESULT_OK, intent);
        finish();
    }
}
