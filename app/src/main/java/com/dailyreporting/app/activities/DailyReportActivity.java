package com.dailyreporting.app.activities;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.dailyreporting.app.R;
import com.dailyreporting.app.Services.MyNotificationPublisher;
import com.dailyreporting.app.Services.NotifyService;
import com.dailyreporting.app.WebApis.ApiClientClass;
import com.dailyreporting.app.WebApis.CorrectionCodes;
import com.dailyreporting.app.WebApis.ProjectActivityTypes;
import com.dailyreporting.app.WebApis.ProjectLocations;
import com.dailyreporting.app.WebApis.ProjectServices;
import com.dailyreporting.app.database.CorrectionsCodeRepo;
import com.dailyreporting.app.database.ProjectActivityRepo;
import com.dailyreporting.app.database.ProjectLocationRepo;
import com.dailyreporting.app.models.CorrectionCode;
import com.dailyreporting.app.models.CorrectionCodeResponse;
import com.dailyreporting.app.models.ProjectActivityType;
import com.dailyreporting.app.models.ProjectActivityTypeModel;
import com.dailyreporting.app.models.ProjectLocation;
import com.dailyreporting.app.models.ProjectLocationResponse;
import com.dailyreporting.app.models.ProjectModel;
import com.dailyreporting.app.models.Projects;
import com.dailyreporting.app.utils.CommonMethods;
import com.dailyreporting.app.utils.Constants;
import com.orhanobut.logger.Logger;

import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dailyreporting.app.utils.Constants.MyPREFERENCES;

public class DailyReportActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView imgMenuIcon, imgActivities;
    TextView txtDay;
    Context context;
    Button btnCompleteDailyReport;
    RelativeLayout rlActivities, dailyReportRL, rlSettings, rlSubContractor, rlVisits, rlCorrections;
    String TAG = getClass().getSimpleName();
    TextView txtDate, projectTV;
    int projectID = 0;
    boolean isOpened = false;
    private ProgressDialog dialogProject;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String authorizationToken = "";
    private Projects projects;
    private String projectSelected = "";
    private ProgressDialog dialogCorrection;
    private int pageSize = 400;
    private String langValue = Constants.ENGLISH_LANG;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";
    private boolean setReminder = false;
    private String workLogDate = "";
    private boolean dateSelected = false;
    private String lastConfigCheck;
    private boolean fromHourCheck = false;
    private String worklogDay="";
    private boolean settingFont = false;
    private float fontSize=13f;
    TextView activityTV,subActivity,dailyFolderTv,visitTV,correctionTV,settingTV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = DailyReportActivity.this;
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        checkLang();

        setContentView(R.layout.activity_dailyreport);
        txtDay = findViewById(R.id.txtDay);
        rlActivities = findViewById(R.id.rlActivities);
        imgActivities = findViewById(R.id.imgActivities);
        subActivity = findViewById(R.id.subActivity);
        activityTV = findViewById(R.id.activityTV);
        dailyFolderTv = findViewById(R.id.dailyFolderTv);
        correctionTV = findViewById(R.id.correctionTV);
        settingTV = findViewById(R.id.settingTV);
        visitTV = findViewById(R.id.visitTV);
        rlSubContractor = findViewById(R.id.rlSubContractor);
        dailyReportRL = findViewById(R.id.dailyReportRL);
        rlVisits = findViewById(R.id.rlVisits);
        rlCorrections = findViewById(R.id.rlCorrections);
        rlSettings = findViewById(R.id.rlSettings);
        imgMenuIcon = findViewById(R.id.imgMenuIcon);
        txtDate = findViewById(R.id.txtDate);
        projectTV = findViewById(R.id.projectTV);
        btnCompleteDailyReport = findViewById(R.id.btnCompleteDailyReport);
        btnCompleteDailyReport.setOnClickListener(this);
        rlActivities.setOnClickListener(this);
        txtDate.setOnClickListener(this);
        rlSettings.setOnClickListener(this);
        rlSubContractor.setOnClickListener(this);
        rlVisits.setOnClickListener(this);
        rlCorrections.setOnClickListener(this);
        dailyReportRL.setOnClickListener(this);
        imgMenuIcon.setColorFilter(ContextCompat.getColor(this, R.color.purple));
        imgActivities.setColorFilter(ContextCompat.getColor(this, R.color.blue));
        projectSelected = sharedPreferences.getString(Constants.PROJECT_SELECTED, "");
        projectID = sharedPreferences.getInt(Constants.PROJECT_ID, 0);
        checkHourValue();
        Spannable word = new SpannableString("Wednesday");

        word.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue)), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        txtDay.setText(word);
        Spannable wordTwo = new SpannableString("\n24 Mar, 2021");

        wordTwo.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtDay.append(wordTwo);


        authorizationToken = sharedPreferences.getString(Constants.TOKEN, "");
        CommonMethods.showLog("Authorization", authorizationToken);
        if (CommonMethods.compareDates()) {
            if (!setReminder) {
                if (CommonMethods.getLocalData()) {
                    updateLabel();
                }
            }

        }

        if (projectSelected.isEmpty()) {
            getAllProjects();
        }

    }

    private void checkLang() {
        langValue = sharedPreferences.getString(Constants.LANG_SELECTED, Constants.ENGLISH_LANG);
        if (langValue.equalsIgnoreCase(Constants.FRENCH_LANG)) {
            setLocale(Constants.FRENCH_LANG);
        } else {
            setLocale(Constants.ENGLISH_LANG);
        }
    }


    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


    }

    @Override
    protected void onResume() {
        super.onResume();
        String langCheck = sharedPreferences.getString(Constants.LANG_SELECTED, Constants.ENGLISH_LANG);
        if (!langCheck.equalsIgnoreCase(langValue)) {
            checkLang();
            recreate();
        }

        projectSelected = sharedPreferences.getString(Constants.PROJECT_SELECTED, "");
        projectID = sharedPreferences.getInt(Constants.PROJECT_ID, 0);
        setTextData();
        if (!sharedPreferences.getString(Constants.WoRKLOG_DATE, "").isEmpty()) {
            workLogDate = sharedPreferences.getString(Constants.WoRKLOG_DATE, "");

            if (!workLogDate.isEmpty()) {
                txtDate.setText(workLogDate);
                try {
                    worklogDay = CommonMethods.getDayFromDate(workLogDate);
                    txtDay.setText(worklogDay);
                }catch (Exception e)
                {
                    Logger.e(e.getMessage());
                }
            }
        }

        isOpened = sharedPreferences.getBoolean(Constants.PROJECT_CLICKED, false);

        if (CommonMethods.isNetworkAvailable(DailyReportActivity.this)) {
            if (isOpened) {
                getProjectActivityType();
            }

        }

      settingFont =   sharedPreferences.getBoolean(Constants.SETTING_FONT,false);
        if(settingFont)
        {
            fontSize = sharedPreferences.getFloat(Constants.FONT_SIZE,13f);
            setFontText();
        }
    }

    private void setFontText() {
        settingTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        correctionTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        visitTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        subActivity.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        activityTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        dailyFolderTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        btnCompleteDailyReport.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtDay.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        projectTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
    }

    private void setTextData() {
        String currentDate = CommonMethods.getCurrentDateByFormat("dd MMM, yyyy");
        String currentDay = CommonMethods.getCurrentDay();
        txtDate.setText(currentDate);
        txtDay.setText(currentDay);
        if (!projectSelected.trim().isEmpty()) {
            projectTV.setText(projectSelected);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.rlActivities:

                Intent intent = new Intent(DailyReportActivity.this, ListActivity.class);
                intent.putExtra("WorkLogDate", workLogDate);
                startActivity(intent);
                break;
            case R.id.txtDate:

                try {
                    setDatePicker(DailyReportActivity.this);

                } catch (Exception e) {
                    Logger.e(e.getMessage());
                }


                break;

            case R.id.rlSettings:
                startActivity(new Intent(DailyReportActivity.this, SettingsActivity.class));
                break;

            case R.id.dailyReportRL:
                Intent intent5= new Intent(DailyReportActivity.this, DailyFolderActivity.class);
                intent5.putExtra("WorkLogDate",workLogDate);
                startActivity(intent5);

                break;

            case R.id.rlSubContractor:
                Intent intent1 = new Intent(DailyReportActivity.this, SubContractorActivity.class);
                intent1.putExtra("WorkLogDate", workLogDate);
                startActivity(intent1);
                break;

            case R.id.rlVisits:
                Intent intent2 = new Intent(DailyReportActivity.this, VisitsActivity.class);
                intent2.putExtra("WorkLogDate", workLogDate);
                startActivity(intent2);
                break;

            case R.id.rlCorrections:
                Intent intent3 = new Intent(DailyReportActivity.this, CorrectionsActivity.class);
                intent3.putExtra("WorkLogDate", workLogDate);
                startActivity(intent3);
                break;

            case R.id.btnCompleteDailyReport:

                Intent intent4 =new Intent(DailyReportActivity.this, CompleteDailyReportActivity.class);
                intent4.putExtra("WorkLogDate", workLogDate);
                startActivity(intent4);
                break;
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
                        worklogDay = CommonMethods.getDayFromDate(workLogDate);
                        txtDay.setText(worklogDay);
                        txtDate.setText(workLogDate);
                        editor.putString(Constants.WoRKLOG_DATE, workLogDate);
                        editor.putString(Constants.LAST_CHECK, "");
                        editor.apply();
                        dateSelected = true;
                        setHourValue();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }


    public void getAllProjects() {
        try {
            com.dailyreporting.app.WebApis.Projects web_api_2 = ApiClientClass.getApiClientDev(DailyReportActivity.this).create(com.dailyreporting.app.WebApis.Projects.class);
            Call<ProjectModel> call = web_api_2.getAllProjects(1);

            if (call != null) {
                call.enqueue(new Callback<ProjectModel>() {
                    @Override
                    public void onResponse(Call<ProjectModel> call, Response<ProjectModel> response) {
                        if (response.body() != null && response.body().getData() != null && response.body().getData().getItems() != null) {
                            try {

                                ProjectServices.deleteProjects();
                                ProjectServices.saveProjects(response.body().getData().getItems());

                                editor.putString(Constants.PROJECT_SELECTED, response.body().getData().getItems().get(0).getName());
                                editor.putInt(Constants.PROJECT_ID, Integer.parseInt(response.body().getData().getItems().get(0).getId()));
                                editor.apply();
                                projectTV.setText(response.body().getData().getItems().get(0).getName());
                                projectID = sharedPreferences.getInt(Constants.PROJECT_ID, 0);
                                getProjectActivityType();
                            } catch (Exception e) {
                                Logger.e(e.getMessage());
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<ProjectModel> call, Throwable t) {

                        Toast.makeText(DailyReportActivity.this, getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                        CommonMethods.showLog(TAG, "Failure " + t.getMessage());
                    }
                });
            }
        } catch (Exception e) {

            Log.e(TAG, e.getMessage());
        }

    }

    public void getProjectActivityType() {
        try {
            dialogProject = new ProgressDialog(DailyReportActivity.this);
            try {
                dialogProject.show();
            } catch (WindowManager.BadTokenException e) {
                Logger.e(e.getMessage());
            }
            dialogProject.setCancelable(false);
            dialogProject.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialogProject.setContentView(R.layout.custom_loading_layout);
            ProjectActivityTypes web_api_2 = ApiClientClass.getApiClientDev(DailyReportActivity.this).create(ProjectActivityTypes.class);
            Call<ProjectActivityTypeModel> call = web_api_2.getProjectActivity(projectID, pageSize,-1);

            if (call != null) {
                call.enqueue(new Callback<ProjectActivityTypeModel>() {
                    @Override
                    public void onResponse(Call<ProjectActivityTypeModel> call, Response<ProjectActivityTypeModel> response) {
                        dialogProject.dismiss();

                        getCorrectionCode();
                        if (response.body() != null && response.body().getData() != null && response.body().getData().getItems() != null) {
                            ProjectActivityType projectType;
                            ProjectActivityRepo.DeleteAll();
                            for (int i = 0; i < response.body().getData().getItems().size(); i++) {
                                if (!response.body().getData().getItems().get(i).getName().trim().isEmpty() && !response.body().getData().getItems().get(i).getActivityCode().trim().isEmpty()) {
                                    ProjectActivityTypeModel.Items responseData = response.body().getData().getItems().get(i);
                                    projectType = new ProjectActivityType();
                                    projectType.name = responseData.getName();
                                    projectType.projectId = responseData.getProjectId();
                                    projectType.projectName = responseData.getProjectName();
                                    projectType.addedOn = responseData.getAddedOn();
                                    projectType.addedBy = responseData.getAddedBy();
                                    projectType.lastModBy = responseData.getLastModBy();
                                    projectType.lastModOn = responseData.getLastModOn();
                                    projectType.activityId = Integer.parseInt(responseData.getId());
                                    projectType.activityCode = responseData.getActivityCode();
                                    projectType.parentID = responseData.getParentId();
                                    projectType.showChild = 0;
                                    ProjectActivityRepo.Save(projectType);
                                }

                            }

                        } else {
                            dialogProject.hide();
                        }
                    }

                    @Override
                    public void onFailure(Call<ProjectActivityTypeModel> call, Throwable t) {
                        dialogProject.hide();
                        Toast.makeText(DailyReportActivity.this, getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                        CommonMethods.showLog(TAG, "Failure " + t.getMessage());
                    }
                });
            }
        } catch (Exception e) {

            if (dialogProject != null) {
                dialogProject.dismiss();
            }
            Log.e(TAG, e.getMessage());
        }

    }


    public void getCorrectionCode() {
        try {
            dialogCorrection = new ProgressDialog(DailyReportActivity.this);
            try {
                dialogCorrection.show();
            } catch (WindowManager.BadTokenException e) {

            }
            dialogCorrection.setCancelable(false);
            dialogCorrection.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialogCorrection.setContentView(R.layout.custom_loading_layout);
            CorrectionCodes web_api_2 = ApiClientClass.getApiClientDev(DailyReportActivity.this).create(CorrectionCodes.class);
            Call<CorrectionCodeResponse> call = web_api_2.getCorrectionCode(projectID);

            if (call != null) {
                call.enqueue(new Callback<CorrectionCodeResponse>() {
                    @Override
                    public void onResponse(Call<CorrectionCodeResponse> call, Response<CorrectionCodeResponse> response) {
                        dialogCorrection.dismiss();
                        getLocationMethod();
                        if (response.body() != null && response.body().getData() != null && response.body().getData().getItems() != null) {

                            CorrectionCode projectType;
                            try {
                                CorrectionsCodeRepo.DeleteCodeByProject(projectID);
                            } catch (Exception e) {
                                CorrectionsCodeRepo.DeleteAll();
                            }

                            for (int i = 0; i < response.body().getData().getItems().size(); i++) {
                                CorrectionCodeResponse.Items responseData = response.body().getData().getItems().get(i);
                                projectType = new CorrectionCode();

                                projectType.projectId = responseData.getProjectId();
                                projectType.addedOn = responseData.getAddedOn();
                                projectType.addedBy = responseData.getAddedBy();
                                projectType.lastModBy = responseData.getLastModBy();
                                projectType.lastModOn = responseData.getLastModOn();
                                projectType.code = responseData.getCode();
                                projectType.correctionId = responseData.getId();
                                CorrectionsCodeRepo.Save(projectType);
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<CorrectionCodeResponse> call, Throwable t) {
                        dialogCorrection.hide();
                        CommonMethods.showLog(TAG, "Failure " + t.getMessage());
                    }
                });
            }
        } catch (Exception e) {

            if (dialogCorrection != null) {
                dialogCorrection.hide();
            }
            Log.e(TAG, e.getMessage());
        }

    }


    public void getLocationMethod() {
        try {
            dialogCorrection = new ProgressDialog(DailyReportActivity.this);
            try {
                dialogCorrection.show();
            } catch (WindowManager.BadTokenException e) {

            }
            dialogCorrection.setCancelable(false);
            dialogCorrection.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialogCorrection.setContentView(R.layout.custom_loading_layout);
            ProjectLocations web_api_2 = ApiClientClass.getApiClientDev(DailyReportActivity.this).create(ProjectLocations.class);
            Call<ProjectLocationResponse> call = web_api_2.getProjectLocation(1, projectID);

            if (call != null) {
                call.enqueue(new Callback<ProjectLocationResponse>() {
                    @Override
                    public void onResponse(Call<ProjectLocationResponse> call, Response<ProjectLocationResponse> response) {
                        dialogCorrection.hide();
                        if (response.body() != null && response.body().getData() != null && response.body().getData().getItems() != null) {

                            ProjectLocation projectType;
                            ProjectLocationRepo.DeleteAll();
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
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ProjectLocationResponse> call, Throwable t) {
                        dialogCorrection.hide();
                        CommonMethods.showLog(TAG, "Failure " + t.getMessage());
                    }
                });
            }
        } catch (Exception e) {

            if (dialogCorrection != null) {
                dialogCorrection.hide();
            }
            Log.e(TAG, e.getMessage());
        }

    }

    public void createNotification(View view) {
        Intent myIntent = new Intent(getApplicationContext(), NotifyService.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, myIntent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.AM_PM, Calendar.AM);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingIntent);
    }

    private void scheduleNotification(Notification notification, long delay) {
        setReminder = true;
        Intent notificationIntent = new Intent(this, MyNotificationPublisher.class);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, delay, pendingIntent);
    }

    private Notification getNotification(String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, default_notification_channel_id);
        builder.setContentTitle(getString(R.string.upload_report));
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setAutoCancel(true);
        builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        return builder.build();
    }

    private void updateLabel() {

        String currentDate = CommonMethods.getCurrentDateByFormat("dd/MM/yy");

        scheduleNotification(getNotification(currentDate), 60);
    }

    public void setHourValue() {
        lastConfigCheck = sharedPreferences.getString(Constants.LAST_CHECK, "");

        if (lastConfigCheck.isEmpty()) {

            lastConfigCheck = String.valueOf(System.currentTimeMillis());

            editor.putString(Constants.LAST_CHECK, lastConfigCheck);
            editor.apply();

        }
    }

    public void checkHourValue() {
        lastConfigCheck = sharedPreferences.getString(Constants.LAST_CHECK, "");

        if (lastConfigCheck.isEmpty()) {
            fromHourCheck = false;
            setWorkDate();
        } else {

            long todaysTime = System.currentTimeMillis();

            Long timeElapsed = todaysTime - Long.valueOf(lastConfigCheck);

            if (timeElapsed > 120000) {
                fromHourCheck = true;
                setWorkDate();
            } else {
                workLogDate = sharedPreferences.getString(Constants.WoRKLOG_DATE, "");

                if (!workLogDate.isEmpty()) {
                    txtDate.setText(workLogDate);
                }
            }

        }
    }

    private void setWorkDate() {
        editor.putString(Constants.WoRKLOG_DATE, "");
        editor.putString(Constants.LAST_CHECK, "");
        editor.apply();
        workLogDate = CommonMethods.getCurrentDateByFormat("dd MMM, yyyy");
        if (fromHourCheck) {
            CommonMethods.showLongToast(DailyReportActivity.this, getString(R.string.date_revert));
        }
    }
}
