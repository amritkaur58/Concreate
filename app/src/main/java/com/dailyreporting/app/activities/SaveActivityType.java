package com.dailyreporting.app.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.dailyreporting.app.R;
import com.dailyreporting.app.WebApis.ApiClientClass;
import com.dailyreporting.app.WebApis.ProjectActivityTypes;
import com.dailyreporting.app.WebApis.VisitLogService;
import com.dailyreporting.app.WebApis.WorklogService;
import com.dailyreporting.app.adapters.ActivityListAdapter;
import com.dailyreporting.app.adapters.AddImageCommonAdapter;
import com.dailyreporting.app.database.DailyFolderRepo;
import com.dailyreporting.app.database.ProjectActivityRepo;
import com.dailyreporting.app.database.VisitLogRepo;
import com.dailyreporting.app.models.DailyFolder;
import com.dailyreporting.app.models.ProjectActivityType;
import com.dailyreporting.app.models.SaveVisitLogModel;
import com.dailyreporting.app.models.VisitLog;
import com.dailyreporting.app.models.WorkLogResponse;
import com.dailyreporting.app.utils.CommonMethods;
import com.dailyreporting.app.utils.Constants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.googlecode.mp4parsercopy.authoring.Edit;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dailyreporting.app.utils.Constants.MyPREFERENCES;

public class SaveActivityType extends AppCompatActivity implements View.OnClickListener {

    String TAG = getClass().getSimpleName();

    private RecyclerView recyclerView;
    private AddImageCommonAdapter addAdapter;
    private ImageView imgBack;
    private String selectedImagePath = "";
    private File output;
    private File folder;
    private List<String> listEdit = new ArrayList<>();
    private DailyFolder dailyFolder;
    private TextView txtEdit, dateTV;
    private TextView txtDay;
    private boolean editView = false;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String authorizationToken = "";
    private int tableId = 0;

    RelativeLayout voiceRL;
    TextView voiceTV;
    private RelativeLayout stagingRL;
    private ImageView saveIV;
    private String parentId = "0";
    private List<ProjectActivityType> activityList = new ArrayList<>();
    private ActivityListAdapter arrayName;

    Spinner activitySP;
    private String activityName = "";
    private String activityCode ="";
    private String activityId="";
    private ProgressDialog dialog;
    private int projectId =0;
    private EditText codeET;
    private EditText nameET;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_type);
        listEdit = new ArrayList<>();
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        authorizationToken = sharedPreferences.getString(Constants.TOKEN, "");
        projectId = sharedPreferences.getInt(Constants.PROJECT_ID, 0);

        Toolbar toolbar = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            toolbar = findViewById(R.id.toolbar);
        }
        if (toolbar != null) {
            txtEdit = toolbar.findViewById(R.id.txtEdit);
            imgBack = toolbar.findViewById(R.id.imgBack);
            stagingRL = toolbar.findViewById(R.id.stagingRL);
            saveIV = toolbar.findViewById(R.id.saveIV);
            saveIV.setVisibility(View.VISIBLE);
            txtEdit.setText(getString(R.string.add_activity_type));
        }
        activitySP = findViewById(R.id.activitySP);
        recyclerView = findViewById(R.id.recyclerView);
        codeET = findViewById(R.id.codeET);
        nameET = findViewById(R.id.nameET);

        imgBack.setOnClickListener(this);
        saveIV.setOnClickListener(this);
        getActivityList();
        activitySP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (activityList.size() > 0) {
                    activityName = activityList.get(i).name;
                    activityCode = activityList.get(i).activityCode;
                    activityId = String.valueOf(activityList.get(i).activityId);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        }


    private void saveVisit() {

        try {
            dialog = new ProgressDialog(this);
            try {

                dialog.show();

            } catch (WindowManager.BadTokenException e) {

            }

            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.custom_loading_layout);

            JSONObject user = new JSONObject();

            JsonObject gsonObject = null;
            try {
                user.put("Id", 0);

                user.put("Name", nameET.getText().toString().trim());
                user.put("ParentId", activityId);
                user.put("ProjectId", projectId);
                user.put("ActivityCode", codeET.getText().toString().trim());

                JsonParser jsonParser = new JsonParser();
                gsonObject = (JsonObject) jsonParser.parse(user.toString());

            } catch (JSONException e) {
                Logger.e(Constants.ERROR_DETAIL,e.getMessage());
            }


            ProjectActivityTypes userService = ApiClientClass.getApiClientDev(SaveActivityType.this).create(ProjectActivityTypes.class);
            Call<WorkLogResponse> call = userService.saveActivityType(gsonObject);

            CommonMethods.showLog(TAG, "Activity log gsonObject  " + gsonObject);
            if (call != null) {
                call.enqueue(new Callback<WorkLogResponse>() {
                    @Override
                    public void onResponse(Call<WorkLogResponse> call, Response<WorkLogResponse> response) {
                        dialog.dismiss();

                        try {
                            if (response.body() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                                finish();

                            } else {
                                CommonMethods.showToast(SaveActivityType.this, response.body().getMessage());
                            }

                        } catch (Exception e) {
                            Logger.e(e.getMessage());
                        }


                    }

                    @Override
                    public void onFailure(Call<WorkLogResponse> call, Throwable t) {
                        dialog.dismiss();
                        CommonMethods.showToast(SaveActivityType.this, getResources().getString(R.string.something_wrong));
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
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.imgBack:
                onBackPressed();
                break;

            case R.id.saveIV:
                if(codeET.getText().toString().trim().isEmpty())
                {

                    CommonMethods.showToast(SaveActivityType.this,getString(R.string.enter_activity_code));
                }else  if(nameET.getText().toString().trim().isEmpty())
                {
                    CommonMethods.showToast(SaveActivityType.this,getString(R.string.enter_activity_name));
                }else {
                    saveVisit();
                }

                break;
        }
    }
    private void getActivityList() {
        activityList = ProjectActivityRepo.getCodeByParent(parentId, String.valueOf(projectId));
        try {

            arrayName = new ActivityListAdapter(SaveActivityType.this, android.R.layout.simple_spinner_item, activityList) {
                @Override
                public boolean isEnabled(int position) {

                    return true;
                }

                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);

                    return view;
                }
            };
            arrayName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            activitySP.setAdapter(arrayName);


        } catch (Exception e) {
            CommonMethods.showLogger(SaveActivityType.this, e);
        }

    }

}
