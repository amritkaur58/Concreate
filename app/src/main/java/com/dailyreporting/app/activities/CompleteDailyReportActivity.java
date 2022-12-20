package com.dailyreporting.app.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dailyreporting.app.Extras.Logger;
import com.dailyreporting.app.R;
import com.dailyreporting.app.WebApis.ApiClientClass;
import com.dailyreporting.app.WebApis.UserService;
import com.dailyreporting.app.adapters.ShowProjectAdapter;
import com.dailyreporting.app.database.ProjectsRepo;
import com.dailyreporting.app.models.Projects;
import com.dailyreporting.app.models.VisitResponse;
import com.dailyreporting.app.utils.CommonMethods;
import com.dailyreporting.app.utils.Constants;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dailyreporting.app.utils.Constants.MyPREFERENCES;

public class CompleteDailyReportActivity extends AppCompatActivity implements View.OnClickListener, ShowProjectAdapter.InterfaceList {
    TextView txtProjectNumber, projectNoTV;
    EditText userNameET;
    TextView projectNumber;
    String TAG = getClass().getSimpleName();
    TextView titleTop, txtGeneralInfo;
    TextView txtDay, txtProjectName, txtFieldUser;
    private Button btnSave;
    private ImageView imgBack;
    private SeekBar seekBar;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int projectId = 0;
    private String projectSelected = "";
    private TextView dateET;
    private ProgressDialog userDialog;
    private String authorizationToken = "";
    private PopupWindow popUpNumber;
    private boolean fromNumber = false;
    private String currentDate = "";
    private ProgressDialog chekcDialog;
    private boolean settingFont = false;
    private float fontSize = 13f;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completedailyreport);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        projectSelected = sharedPreferences.getString(Constants.PROJECT_SELECTED, "");
        authorizationToken = sharedPreferences.getString(Constants.TOKEN, "");
        projectId = sharedPreferences.getInt(Constants.PROJECT_ID, 0);
        btnSave = findViewById(R.id.btnSave);
        projectNumber = findViewById(R.id.projectNumberET);
        dateET = findViewById(R.id.dateET);
        titleTop = findViewById(R.id.titleTop);
        txtGeneralInfo = findViewById(R.id.txtGeneralInfo);
        projectNoTV = findViewById(R.id.projectNoTV);
        userNameET = findViewById(R.id.userNameET);
        txtProjectNumber = findViewById(R.id.txtProjectNumber);
        txtDay = findViewById(R.id.txtDay);
        txtProjectName = findViewById(R.id.txtProjectName);
        txtFieldUser = findViewById(R.id.txtFieldUser);

        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnClickListener(this);
        projectNumber.setOnClickListener(this);
        seekBar.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        dateET.setOnClickListener(this);

        if (CommonMethods.isNetworkAvailable(CompleteDailyReportActivity.this)) {
            getUserInfo();

        } else {
            CommonMethods.showToast(CompleteDailyReportActivity.this, getString(R.string.internet_connection));
        }
        setTextValue();

        getPermission();
        checkDarkMode();
    }

    private void setTextValue() {
        projectNumber.setText(String.valueOf(projectId));
        projectNoTV.setText(projectSelected);
        currentDate = CommonMethods.getCurrentDateByFormat("dd MMM, yyyy");
        dateET.setText(currentDate);
        if (getIntent().hasExtra("WorkLogDate")) {
            String WorkLogDate = getIntent().getStringExtra("WorkLogDate");
            currentDate = WorkLogDate;
            dateET.setText(WorkLogDate);
            try {
                String workLogDay = CommonMethods.getDayFromDate(WorkLogDate);
                txtDay.setText(workLogDay);
            } catch (Exception e) {
                Logger.error(e);
            }
        }

    }


    private void showProjectNumber(List<Projects> projectData) {
        LayoutInflater inflater = (LayoutInflater)
                getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_card_layout, null);

        RecyclerView cardRV = view.findViewById(R.id.cardRV);
        cardRV.setLayoutManager(new LinearLayoutManager(CompleteDailyReportActivity.this));

        if (projectData.size() > 0) {
            fromNumber = true;
            cardRV.setAdapter(new ShowProjectAdapter(CompleteDailyReportActivity.this, projectData, fromNumber));
        }

        popUpNumber = new PopupWindow(view, 600, RelativeLayout.LayoutParams.WRAP_CONTENT, true);

        popUpNumber.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

    }


    public void setDatePicker() {

        int mYear, mMonth, mDay;

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(CompleteDailyReportActivity.this,
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
                        currentDate = CommonMethods.getWorkLogDate(dateVal);
                        dateET.setText(currentDate);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSave:
                chekcDialog = new ProgressDialog(CompleteDailyReportActivity.this);
                try {
                    chekcDialog.show();
                } catch (WindowManager.BadTokenException e) {
                    e.printStackTrace();
                }
                chekcDialog.setCancelable(false);
                chekcDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                chekcDialog.setContentView(R.layout.custom_loading_layout);
                btnSave.setClickable(false);
                checkLocalData();
                break;

            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.dateET:
                try {
                    setDatePicker();
                } catch (Exception e) {
                    Logger.error(e);
                }
                break;
            case R.id.projectNoTV:
                List<Projects> projectData = ProjectsRepo.GetAll();
                showProjectNumber(projectData);

                try {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        popUpNumber.showAsDropDown(view, 12, 0, Gravity.BOTTOM);
                    }
                    popUpNumber.showAtLocation(view, Gravity.TOP, 2, 0);

                } catch (Exception e) {
                    Logger.error(e);
                }
                break;


        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        settingFont = sharedPreferences.getBoolean(Constants.SETTING_FONT, false);
        if (settingFont) {
            fontSize = sharedPreferences.getFloat(Constants.FONT_SIZE, 13f);
            setFontText();
        }
    }

    private void setFontText() {

        titleTop.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtGeneralInfo.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtProjectNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        projectNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtDay.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtProjectName.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtFieldUser.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        projectNoTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

        userNameET.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

        dateET.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        btnSave.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

    }


    private void checkLocalData() {
        if (chekcDialog != null) {
            chekcDialog.dismiss();
        }
        if (CommonMethods.checkLocalData(projectId)) {

            Intent intent = new Intent(CompleteDailyReportActivity.this, DailyConditionsActivity.class);
            intent.putExtra("ProjectId", projectId);
            intent.putExtra("ProjectSelected", projectSelected);
            intent.putExtra("WorkLogDate", currentDate);
            startActivity(intent);
            finish();
        } else {
            CommonMethods.showToast(CompleteDailyReportActivity.this, getString(R.string.add_daily_report));
        }
    }

    public void getUserInfo() {
        try {
            userDialog = new ProgressDialog(CompleteDailyReportActivity.this);
            try {
                userDialog.show();
            } catch (WindowManager.BadTokenException e) {
                Logger.error(e);
            }
            userDialog.setCancelable(false);
            userDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            userDialog.setContentView(R.layout.custom_loading_layout);
            UserService web_api_2 = ApiClientClass.getApiClientDev(CompleteDailyReportActivity.this).create(UserService.class);
            Call<VisitResponse> call = web_api_2.getUserInfo();

            if (call != null) {
                call.enqueue(new Callback<VisitResponse>() {
                    @Override
                    public void onResponse(Call<VisitResponse> call, Response<VisitResponse> response) {
                        userDialog.hide();

                        if (response.body() != null && response.body().getData() != null) {
                            userNameET.setText(response.body().getData().getFullName());

                        }
                    }

                    @Override
                    public void onFailure(Call<VisitResponse> call, Throwable t) {
                        userDialog.hide();
                        Toast.makeText(CompleteDailyReportActivity.this, getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                        CommonMethods.showLog(TAG, "Failure " + t.getMessage());
                    }
                });
            }
        } catch (Exception e) {

            if (userDialog != null) {
                userDialog.hide();
            }
            Log.e(TAG, e.getMessage());
        }

    }

    private void getPermission() {

        if (ContextCompat.checkSelfPermission(CompleteDailyReportActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(CompleteDailyReportActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(CompleteDailyReportActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(CompleteDailyReportActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    }
                }
                return;
            }
        }
    }

    @Override
    public void checkProjectClicked(Projects list, int position) {

        popUpNumber.dismiss();
        projectNoTV.setText(list.name);
        projectNumber.setText(list.idValue);

        try {
            projectSelected = list.name;
            projectId = Integer.parseInt(list.idValue);

        } catch (Exception e) {
            Logger.error(e);
        }

    }

    private void checkDarkMode() {
        if (CommonMethods.checkDarkMode(CompleteDailyReportActivity.this)) {
            titleTop.setTextColor(ContextCompat.getColor(CompleteDailyReportActivity.this, R.color.white));
            txtGeneralInfo.setTextColor(ContextCompat.getColor(CompleteDailyReportActivity.this, R.color.white));
        }
    }

}
