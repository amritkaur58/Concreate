package com.dailyreporting.app.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dailyreporting.app.R;
import com.dailyreporting.app.WebApis.ApiClientClass;
import com.dailyreporting.app.WebApis.ProjectServices;
import com.dailyreporting.app.adapters.CommonAdapter;
import com.dailyreporting.app.adapters.SavedProjectAdapter;
import com.dailyreporting.app.adapters.SavedProjectAdapter.InterfaceList;
import com.dailyreporting.app.database.ProjectsRepo;
import com.dailyreporting.app.models.CommonModel;
import com.dailyreporting.app.models.ProjectModel;
import com.dailyreporting.app.models.Projects;
import com.dailyreporting.app.utils.CommonMethods;
import com.dailyreporting.app.utils.Constants;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dailyreporting.app.utils.Constants.MyPREFERENCES;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, InterfaceList, CommonAdapter.InterfaceList {
    RelativeLayout rlEnglish, rlFrench, rlLanguage, logoutRL, projectRL, fontRL;
    ImageView imgFrontIcon;
    View viewOne;
    TextView versionTV,fontValue;
    ImageView imgBack;
    int count = 0;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private PopupWindow mypopupProject;

    TextView settingTV, languageTV, switchTV, logoutTV, fontTV;


    ImageView imgFrontIconSwitch;
    RadioButton radioButton, radioButton1;
    private boolean fromSetting = false;
    private int projectId = 0;
    ImageView refreshType;
    private ProgressDialog dialogProject;
    private PopupWindow myPopupFont;
    private boolean settingFont = false;
    private float fontSize = 13f;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        projectId = sharedPreferences.getInt(Constants.PROJECT_ID, 0);

        checkLang();
        setContentView(R.layout.activity_settings);
        viewOne = findViewById(R.id.viewOne);
        fontValue = findViewById(R.id.fontValue);
        settingTV = findViewById(R.id.settingTV);
        languageTV = findViewById(R.id.languageTV);
        switchTV = findViewById(R.id.switchTV);
        logoutTV = findViewById(R.id.logoutTV);
        fontTV = findViewById(R.id.fontTV);
        versionTV = findViewById(R.id.versionTV);
        imgFrontIconSwitch = findViewById(R.id.imgFrontIconSwitch);
        refreshType = findViewById(R.id.refreshType);
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);
        imgFrontIconSwitch.setOnClickListener(this);
        rlEnglish = findViewById(R.id.rlEnglish);
        rlFrench = findViewById(R.id.rlFrench);
        fontRL = findViewById(R.id.fontRL);
        rlLanguage = findViewById(R.id.rlLanguage);
        radioButton = findViewById(R.id.radioButton);
        radioButton1 = findViewById(R.id.radioButton1);
        imgFrontIcon = findViewById(R.id.imgFrontIcon);
        logoutRL = findViewById(R.id.logoutRL);
        projectRL = findViewById(R.id.projectRL);
        rlLanguage.setOnClickListener(this);
        logoutRL.setOnClickListener(this);
        projectRL.setOnClickListener(this);
        versionTV.setOnClickListener(this);
        radioButton.setOnClickListener(this);
        radioButton1.setOnClickListener(this);
        refreshType.setOnClickListener(this);
        fontRL.setOnClickListener(this);
        setRadioButton();

        setFontSize();
        /*if (CommonMethods.isNetworkAvailable(SettingsActivity.this)) {
            getAllProjects();
        }*/
        setProjectData();
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            versionTV.setText(getString(R.string.version_string) + " " + version);
        } catch (PackageManager.NameNotFoundException e) {
            CommonMethods.showLog("Log value", e.getMessage());
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        settingFont = sharedPreferences.getBoolean(Constants.SETTING_FONT, false);
        if (settingFont) {
            fontSize = sharedPreferences.getFloat(Constants.FONT_SIZE, 13f);
            try {
                int value = ((int) fontSize);
                fontValue.setText(String.valueOf(value)+"sp");

            }catch (Exception e)
            {
                Logger.e(e.getMessage());
            }
           setFontText();
        }
    }

    private void setFontText() {

        settingTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        languageTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        switchTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        fontTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        logoutTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
    }

    private void setProjectData() {
        projectId = sharedPreferences.getInt(Constants.PROJECT_ID, 0);
        List<Projects> projectData = ProjectsRepo.GetAll();
        for (int i = 0; i < projectData.size(); i++) {
            if (String.valueOf(projectId).equalsIgnoreCase(projectData.get(i).idValue)) {
                Projects projects = new Projects();
                projects = projectData.get(i);
                projects.projectSelected = 1;
                projectData.set(i, projects);
            }
        }
        if (projectData.size() > 0) {
            setTagPopup(projectData);

        }

    }

    private void checkLang() {
        String langValue = sharedPreferences.getString(Constants.LANG_SELECTED, Constants.ENGLISH_LANG);
        if (langValue.equalsIgnoreCase(Constants.FRENCH_LANG)) {
            setLocaleValue(Constants.FRENCH_LANG);

        } else {
            setLocaleValue(Constants.ENGLISH_LANG);
        }
    }

    private void setRadioButton() {
        String langValue = sharedPreferences.getString(Constants.LANG_SELECTED, Constants.ENGLISH_LANG);
        if (langValue.equalsIgnoreCase(Constants.FRENCH_LANG)) {

            radioButton.setChecked(false);
            radioButton1.setChecked(true);
        } else {
            radioButton.setChecked(true);
            radioButton1.setChecked(false);
        }
    }


    private void setLocaleValue(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlLanguage:
                if (imgFrontIcon.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.fronticon).getConstantState()) {
                    rlEnglish.setVisibility(View.VISIBLE);
                    rlFrench.setVisibility(View.VISIBLE);
                    viewOne.setVisibility(View.VISIBLE);
                    imgFrontIcon.setImageResource(R.drawable.downarrow);
                } else {
                    rlEnglish.setVisibility(View.GONE);
                    rlFrench.setVisibility(View.GONE);
                    viewOne.setVisibility(View.GONE);
                    imgFrontIcon.setImageResource(R.drawable.fronticon);
                }
                break;


            case R.id.versionTV:
                count = count + 1;
                if (count >= 3) {

                    CommonMethods.Mode.saveApiMode(SettingsActivity.this);
                    logoutMethod();
                }
                break;


            case R.id.refreshType:
                getAllProjects();
                break;


            case R.id.radioButton:
                try {
                    radioButton1.setChecked(false);
                    setLangPref(Constants.ENGLISH_LANG);
                } catch (Exception e) {
                    Logger.e(e.getMessage());
                }

                break;
            case R.id.radioButton1:
                try {
                    radioButton.setChecked(false);
                    setLangPref(Constants.FRENCH_LANG);
                } catch (Exception e) {
                    Logger.e(e.getMessage());
                }

                break;

            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.logoutRL:

                logoutDialog();
                break;
            case R.id.projectRL:
            case R.id.imgFrontIconSwitch:
                setProjectData();
                try {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        mypopupProject.showAsDropDown(view, 12, 0, Gravity.BOTTOM);
                    }
                    mypopupProject.showAtLocation(view, Gravity.TOP, 2, 0);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.fontRL:

                try {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        myPopupFont.showAsDropDown(view, 12, 0, Gravity.BOTTOM);
                    }
                    myPopupFont.showAtLocation(view, Gravity.TOP, 2, 0);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void setLangPref(String lang) {
        editor.putString(Constants.LANG_SELECTED, lang);
        editor.apply();
        setLocale(lang);

    }

    private void setLocale(String lang) {

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        recreate();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


    }

    void logoutDialog() {
        final Dialog dialog = new Dialog(SettingsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_logout_dialog);
        Window window = dialog.getWindow();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);

        try {
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        TextView yes = dialog.findViewById(R.id.yes);
        TextView no = dialog.findViewById(R.id.no);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                logoutMethod();

            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void logoutMethod() {
        editor.putBoolean(Constants.LOGIN_USER, false);
        editor.putString(Constants.TOKEN, "");
        editor.putBoolean(Constants.PROJECT_CLICKED, false);
        editor.putString(Constants.PROJECT_SELECTED, "");
        editor.putInt(Constants.PROJECT_ID, 0);
        editor.apply();
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }


    private void setTagPopup(List<Projects> projectData) {
        LayoutInflater inflater = (LayoutInflater)
                getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_card_layout, null);

        RecyclerView cardRV = view.findViewById(R.id.cardRV);
        cardRV.setLayoutManager(new LinearLayoutManager(SettingsActivity.this));

        if (projectData.size() > 0) {
            fromSetting = true;
            cardRV.setAdapter(new SavedProjectAdapter(SettingsActivity.this, projectData, fromSetting));
        }

        mypopupProject = new PopupWindow(view, 600, RelativeLayout.LayoutParams.WRAP_CONTENT, true);

        mypopupProject.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

    }


    private void setFontSize() {
        List<CommonModel> projectData = new ArrayList<>();
        CommonModel model = new CommonModel();
        model.setFontSize("11sp");
        model.setFontValue(11f);
        model.setId(1);
        projectData.add(model);
        model = new CommonModel();
        model.setFontSize("13sp");
        model.setFontValue(13f);
        model.setId(2);
        projectData.add(model);
        model = new CommonModel();
        model.setFontSize("15sp");
        model.setFontValue(15f);
        model.setId(3);
        projectData.add(model);

        LayoutInflater inflater = (LayoutInflater)
                getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_card_layout, null);

        RecyclerView cardRV = view.findViewById(R.id.cardRV);
        cardRV.setLayoutManager(new LinearLayoutManager(SettingsActivity.this));

        if (projectData.size() > 0) {
            cardRV.setAdapter(new CommonAdapter(SettingsActivity.this, projectData));
        }

        myPopupFont = new PopupWindow(view, 600, RelativeLayout.LayoutParams.WRAP_CONTENT, true);

        myPopupFont.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

    }


    @Override
    public void checkItemTagClick(Projects list, int position) {
        editor.putString(Constants.PROJECT_SELECTED, list.name);
        editor.putInt(Constants.PROJECT_ID, Integer.parseInt(list.idValue));
        editor.apply();
        mypopupProject.dismiss();
        editor.putBoolean(Constants.PROJECT_CLICKED, true);
        editor.apply();
    }


    public void getAllProjects() {
        try {


            dialogProject = new ProgressDialog(this);
            try {

                dialogProject.show();

            } catch (WindowManager.BadTokenException e) {

            }
            dialogProject.setCancelable(false);
            dialogProject.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialogProject.setContentView(R.layout.custom_loading_layout);

            com.dailyreporting.app.WebApis.Projects web_api_2 = ApiClientClass.getApiClientDev(SettingsActivity.this).create(com.dailyreporting.app.WebApis.Projects.class);
            Call<ProjectModel> call = web_api_2.getAllProjects(1);

            if (call != null) {
                call.enqueue(new Callback<ProjectModel>() {
                    @Override
                    public void onResponse(Call<ProjectModel> call, Response<ProjectModel> response) {
                        dialogProject.dismiss();
                        if (response.body() != null || response.body().getData() != null || response.body().getData().getItems() != null) {
                            try {

                                ProjectServices.deleteProjects();
                                ProjectServices.saveProjects(response.body().getData().getItems());
                                editor.putString(Constants.PROJECT_SELECTED, response.body().getData().getItems().get(0).getName());
                                editor.putInt(Constants.PROJECT_ID, Integer.parseInt(response.body().getData().getItems().get(0).getId()));
                                editor.putBoolean(Constants.PROJECT_CLICKED, true);
                                editor.apply();
                                setProjectData();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    }

                    @Override
                    public void onFailure(Call<ProjectModel> call, Throwable t) {
                        dialogProject.dismiss();
                        Toast.makeText(SettingsActivity.this, getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            dialogProject.dismiss();
        }

    }

    @Override
    public void checkItemTagClick(CommonModel list, int position) {
        if (myPopupFont != null) {
            myPopupFont.dismiss();
        }
        editor.putBoolean(Constants.SETTING_FONT, true);
        editor.putFloat(Constants.FONT_SIZE, list.getFontValue());
        fontSize = list.getFontValue();

        try {
            int value = ((int) fontSize);
            fontValue.setText(String.valueOf(value)+"sp");

        }catch (Exception e)
        {
            Logger.e(e.getMessage());
        }
        setFontText();
        editor.apply();
    }
}
