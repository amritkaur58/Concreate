package com.dailyreporting.app.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dailyreporting.app.R;
import com.dailyreporting.app.WebApis.ApiClientClass;
import com.dailyreporting.app.WebApis.ProjectServices;
import com.dailyreporting.app.WebApis.UserService;
import com.dailyreporting.app.adapters.SavedProjectAdapter;
import com.dailyreporting.app.database.ActivitiesRepo;
import com.dailyreporting.app.database.CorrectionsCodeRepo;
import com.dailyreporting.app.database.CorrectionsRepo;
import com.dailyreporting.app.database.DailyFolderRepo;
import com.dailyreporting.app.database.ProjectActivityRepo;
import com.dailyreporting.app.database.ProjectLocationRepo;
import com.dailyreporting.app.database.SubContractorRepo;
import com.dailyreporting.app.database.VisitLogRepo;
import com.dailyreporting.app.models.Projects;
import com.dailyreporting.app.models.VisitResponse;
import com.dailyreporting.app.utils.CommonMethods;
import com.dailyreporting.app.utils.Constants;
import com.dailyreporting.app.utils.LocaleHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dailyreporting.app.utils.Constants.MyPREFERENCES;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, SavedProjectAdapter.InterfaceList {
    private final int PERMISSION_REQUEST_CODE = 99;
    RelativeLayout rlLogin;
    EditText etEmail, etPassword;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9]+\\.+[a-z]+";
    String TAG = getClass().getSimpleName();
    TextView spLanguage;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean loginUser = false;
    private TextView versionTV;
    RelativeLayout langRL;
    private int count = 0;
    boolean firstOpen = true;
    private PopupWindow popLanguage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        etEmail = findViewById(R.id.etEmail);
        langRL = findViewById(R.id.langRL);
        etPassword = findViewById(R.id.etPassword);
        rlLogin = findViewById(R.id.rlLogins);
        versionTV = findViewById(R.id.versionTV);
        spLanguage = findViewById(R.id.spLanguage);
        if (rlLogin != null)
            rlLogin.setOnClickListener(this);
        versionTV.setOnClickListener(this);
        spLanguage.setOnClickListener(this);
        langRL.setOnClickListener(this);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        loginUser = sharedPreferences.getBoolean(Constants.LOGIN_USER, false);
        if (loginUser) {
            openMainActivity();
        }
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            versionTV.setText(getString(R.string.version_string) + " " + version);
        } catch (PackageManager.NameNotFoundException e) {
            CommonMethods.showLog("Log value", e.getMessage());
        }
        getImage();
        setLangPopup();
    }

    private void setLanguageSpinner() {
        /*ArrayAdapter mAdapter = new ArrayAdapter<String>(LoginActivity.this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.language_option));
        spLanguage.setAdapter(mAdapter);
        spLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Context context;

                switch (i) {
                    case 0:
                        if(!firstOpen)
                        {
                            firstOpen = false;
                            setLocale(Constants.ENGLISH_LANG);
                        }

                        break;
                    case 1:
                        if(!firstOpen)
                        {
                            firstOpen = false;
                            setLocale(Constants.FRENCH_LANG);
                        }

                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
*/

    }

    private void setLocale(String lang) {
        recreate();
        Context context = LocaleHelper.setLocale(LoginActivity.this, lang);
        Locale locale = new Locale(lang);
        Configuration newConfig = new Configuration();
        newConfig.locale = locale;
        onConfigurationChanged(newConfig);
        recreate();  //this is used for recreate activity

        Locale.setDefault(locale);

        getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

       /* getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_login);
        setTitle(R.string.app_name);*/

    }

    void setLanguageText() {
        Locale current = getResources().getConfiguration().locale;

        if (current.getLanguage().equalsIgnoreCase(Constants.FRENCH_LANG)) {
            spLanguage.setText(getString(R.string.french_string));
        } else {
            spLanguage.setText(getString(R.string.english_string));
        }
    }

    private void setLangPopup() {
        List<Projects> list = new ArrayList<>();
        Projects projectData = new Projects();
        projectData.name = "English";
        projectData.idValue = "1";
        list.add(projectData);
        projectData = new Projects();
        projectData.name = "French";
        projectData.idValue = "2";
        list.add(projectData);
        setLanguageText();
        LayoutInflater inflater = (LayoutInflater)
                getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_card_layout, null);

        RecyclerView cardRV = view.findViewById(R.id.cardRV);
        cardRV.setLayoutManager(new LinearLayoutManager(LoginActivity.this));

        if (list.size() > 0) {

            cardRV.setAdapter(new SavedProjectAdapter(LoginActivity.this, list, false));
        }

        popLanguage = new PopupWindow(view, 600, RelativeLayout.LayoutParams.WRAP_CONTENT, true);

        popLanguage.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlLogins:
                if (!etEmail.getText().toString().trim().isEmpty())
                    loginApi();

                break;
            case R.id.spLanguage:
            case R.id.langRL:
                try {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        popLanguage.showAsDropDown(view, 12, 0, Gravity.BOTTOM);
                    }
                    popLanguage.showAtLocation(view, Gravity.TOP, 2, 0);

                } catch (Exception e) {
                    Logger.e(e.getMessage());
                }
                break;
            case R.id.versionTV:
                count = count + 1;
                if (count >= 3) {

                    CommonMethods.Mode.saveApiMode(LoginActivity.this);

                }
                break;
        }
    }

    public void getImage() {
        if (!isPermissionAllowed()) {
            requestPermission();
            return;
        }
    }

    public void requestPermission() {
        try {

            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, PERMISSION_REQUEST_CODE);
        } catch (Exception e) {

        }
    }

    private boolean isPermissionAllowed() {
        try {
            int camera = ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CAMERA);
            int writestorage = ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            List<String> listPermissionNeeded = new ArrayList<>();

            if (camera != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(Manifest.permission.CAMERA);
            }

            if (writestorage != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (!listPermissionNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(LoginActivity.this, listPermissionNeeded.toArray
                        (new String[listPermissionNeeded.size()]), PERMISSION_REQUEST_CODE);
                return false;
            }

        } catch (Exception e) {
            //GlobalMethods.SendErrorReport(e,context);
        }
        return true;
    }


    private void loginApi() {

        if (!(etEmail.getText().toString().trim().matches(emailPattern))) {
            Toast.makeText(LoginActivity.this, getString(R.string.invalid_email_address), Toast.LENGTH_SHORT).show();

        } else if (etPassword.getText().toString().trim().equals("")) {
            Toast.makeText(getApplicationContext(), getString(R.string.invalid_pass), Toast.LENGTH_SHORT).show();
        } else {
            try {
                rlLogin.setEnabled(false);
                ProgressDialog dialog = new ProgressDialog(this);
                try {
                    dialog.show();
                } catch (WindowManager.BadTokenException e) {

                    e.printStackTrace();
                }
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.custom_loading_layout);

                JSONObject user = new JSONObject();

                JsonObject gsonObject = null;
                try {
                    user.put(Constants.LOGIN_EMAIL, etEmail.getText().toString().toLowerCase());
                    user.put(Constants.LOGIN_PASSWORD, etPassword.getText().toString());

                    JsonParser jsonParser = new JsonParser();
                    gsonObject = (JsonObject) jsonParser.parse(user.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                UserService userService = ApiClientClass.getApiClientDev(LoginActivity.this).create(UserService.class);
                Call<VisitResponse> call = userService.login(gsonObject);

                CommonMethods.showLog(TAG, "login gsonObject  " + gsonObject);
                if (call != null) {
                    call.enqueue(new Callback<VisitResponse>() {
                        @Override
                        public void onResponse(Call<VisitResponse> call, Response<VisitResponse> response) {
                            dialog.hide();

                            try {
                                if (response.body() != null && response.body().getData() != null) {
                                    resetDaytabase();
                                    editor.putBoolean(Constants.LOGIN_USER, true);
                                    editor.putString(Constants.TOKEN, response.body().getData().getToken());
                                    editor.apply();
                                    openMainActivity();

                                } else {
                                    if (response.code() == 404) {

                                        CommonMethods.showToast(LoginActivity.this, getString(R.string.network_error));
                                    } else {
                                        CommonMethods.showToast(LoginActivity.this, getString(R.string.invalid_login));
                                    }
                                }

                            } catch (Exception e) {
                                CommonMethods.showToast(LoginActivity.this, getString(R.string.invalid_login));
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onFailure(Call<VisitResponse> call, Throwable t) {
                            dialog.hide();
                            CommonMethods.showToast(LoginActivity.this, getResources().getString(R.string.something_wrong));
                            CommonMethods.showLog(TAG, "Failure " + t.getMessage());
                        }
                    });
                }
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        rlLogin.setEnabled(true);
                    }
                }, 8000);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }


    }

    private void resetDaytabase() {
        try {
            CorrectionsRepo.DeleteAll();
            ActivitiesRepo.DeleteAll();
            SubContractorRepo.DeleteAll();
            VisitLogRepo.DeleteAll();
            DailyFolderRepo.DeleteAll();
        } catch (Exception e) {
            CommonMethods.showLog("ERROr", e.getMessage());
        }
    }

    public void openMainActivity() {
        if (!loginUser) {
            try {
                ProjectServices.deleteProjects();
                ProjectActivityRepo.DeleteAll();
                CorrectionsCodeRepo.DeleteAll();
                ProjectLocationRepo.DeleteAll();
            } catch (Exception e) {
                Logger.e(e.getMessage());
            }
        }
        startActivity(new Intent(LoginActivity.this, DailyReportActivity.class));
        finish();


    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    public void checkItemTagClick(Projects list, int position) {
        popLanguage.dismiss();

        if (list.idValue.equalsIgnoreCase("2")) {
            editor.putString(Constants.LANG_SELECTED, Constants.FRENCH_LANG);
            editor.apply();
            setLocale(Constants.FRENCH_LANG);
            spLanguage.setText(getString(R.string.french_string));
        } else {
            editor.putString(Constants.LANG_SELECTED, Constants.ENGLISH_LANG);
            editor.apply();
            setLocale(Constants.ENGLISH_LANG);
            spLanguage.setText(getString(R.string.english_string));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            setLanguageText();
        } catch (Exception e) {

        }

    }
}
