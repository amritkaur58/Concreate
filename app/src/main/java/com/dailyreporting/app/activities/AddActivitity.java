package com.dailyreporting.app.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dailyreporting.app.R;
import com.dailyreporting.app.Services.ServicesMethod;
import com.dailyreporting.app.WebApis.ApiClientClass;
import com.dailyreporting.app.WebApis.ProjectActivityTypes;
import com.dailyreporting.app.adapters.ActivityListAdapter;
import com.dailyreporting.app.adapters.AddAdapter;
import com.dailyreporting.app.adapters.LocationArrayAdapter;
import com.dailyreporting.app.adapters.SelectionAdapter;
import com.dailyreporting.app.adapters.ShowActivityChildAdapter;
import com.dailyreporting.app.adapters.ShowActivityCodeAdapter;
import com.dailyreporting.app.adapters.ShowCommonActivityAdapter;
import com.dailyreporting.app.database.ActivitiesRepo;
import com.dailyreporting.app.database.DailyFolderRepo;
import com.dailyreporting.app.database.FilesRepo;
import com.dailyreporting.app.database.ProjectActivityRepo;
import com.dailyreporting.app.database.ProjectLocationRepo;
import com.dailyreporting.app.models.ActivityModel;
import com.dailyreporting.app.models.DailyFolder;
import com.dailyreporting.app.models.FilesModel;
import com.dailyreporting.app.models.ProjectActivityType;
import com.dailyreporting.app.models.ProjectActivityTypeModel;
import com.dailyreporting.app.models.ProjectLocation;
import com.dailyreporting.app.models.SubContractActivity;
import com.dailyreporting.app.utils.CommonMethods;
import com.dailyreporting.app.utils.Constants;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dailyreporting.app.utils.Constants.MyPREFERENCES;

public class AddActivitity extends AppCompatActivity implements View.OnClickListener, ShowCommonActivityAdapter.InterfaceList, SelectionAdapter.InterfaceList, ShowActivityCodeAdapter.InterfaceList, ShowActivityChildAdapter.InterfaceList, AddAdapter.InterfaceList {

    private final int CAMERA_REQUEST_CODE = 10;
    private final int GALLERY_REQUEST_CODE = 20;
    private final int REQUEST_CODE = 40;
    private final int RESULT_CROP = 30;
    private final int PERMISSION_REQUEST_CODE = 99;
    private final List<ActivityModel> activityData = new ArrayList<>();
    private final int recordAudioRequestCode = 145;
    private final List<String> listTaskCompleted = new ArrayList<>();
    private final List<String> pathList = new ArrayList<>();
    private final int pageSize = 300;
    TextView voiceTV;
    RelativeLayout voiceRL;
    String TAG = getClass().getSimpleName();
    TextView etActivityName;
    ImageView refreshIV;
    ImageView addIV, refreshType;
    ImageView spinIV;
    TextView completedTV;
    private RecyclerView recyclerView;
    private AddAdapter addAdapter;
    private ImageView imgBack;
    private Spinner spinnerTaskCompleted, spinnerComplaint;
    private ArrayAdapter arrayTaskCompleted;
    private ArrayAdapter arrayComplaint;
    private ArrayAdapter arrayName;
    ////////////////////////
    private String selectedImagePath, strComplaint = "Yes", strTaskCompleted = "Yes";
    private boolean imageStatus = false;
    private File output;
    private File folder;
    private int count = 1;
    private EditText etCode, etLocation, etNote;
    private Button btnSave;
    private String strActivityName = "", strCode = "", strNote = "", strLocation = "", strImagePath = "";
    private List<String> listEdit = new ArrayList<>();
    private ActivityModel activityModel = new ActivityModel();
    private FilesModel filesModel = new FilesModel();
    private TextView txtEdit, txtDay, dateTV;
    private Spinner nameSP;
    private Spinner locationSP;
    private List<ProjectActivityType> activityList = new ArrayList<>();
    private String activityName = "";
    private String activityId = "0";
    private LocationArrayAdapter arrayLocation;
    private List<ProjectLocation> locationList = new ArrayList<>();
    private String locationName = "";
    private List<SubContractActivity> selectionList = new ArrayList<>();
    private String locationId = "0";
    private boolean editView = false;
    private int tableId = 0;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String projectSelected = "";
    private TextView txtCity, addPicTV, complaintTV;
    private SpeechRecognizer speechRecognizer;
    private String activityCode = "";
    private RelativeLayout stagingRL;
    private ImageView saveIV;
    private String parentId = "0";
    private ProgressDialog dialogLocation;
    private int projectId = 0;
    private PopupWindow popupActivity, popupChild;
    private List<ProjectActivityType> activityCodeList = new ArrayList<>();
    private ProgressDialog dialogProject;
    private boolean fromParent = false;
    private List<String> taskList = new ArrayList<>();
    private boolean isCompleted = true;
    private boolean isComplaint = true;
    private String workLogDate = "";
    private String workLogTime = "";
    private String workLogDay = "";
    private boolean fromComplaint = false;
    private PopupWindow popUpCompleted;
    private PopupWindow popupComplaint;
    private boolean settingFont = false;
    private float fontSize = 13f;
    private TextView txtActivityName;
    private TextView txtLocation, txtTaskCompleted, txtComplaint;
    private AppCompatButton btnCancel;
    private String uid = "";
    private String fileId = "";
    private List<FilesModel> editFileModel = new ArrayList<>();

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);

        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

    }

    private void checkLang() {
        String langValue = sharedPreferences.getString(Constants.LANG_SELECTED, Constants.ENGLISH_LANG);
        if (langValue.equalsIgnoreCase(Constants.FRENCH_LANG)) {
            setLocaleValue(Constants.FRENCH_LANG);
        } else {
            setLocaleValue(Constants.ENGLISH_LANG);
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


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        checkLang();
        setContentView(R.layout.activity_addactivity);
        listEdit = new ArrayList<>();
        Intent intent = getIntent();
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        projectSelected = sharedPreferences.getString(Constants.PROJECT_SELECTED, "");
        projectId = sharedPreferences.getInt(Constants.PROJECT_ID, 0);
        Toolbar toolbar = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            toolbar = findViewById(R.id.toolbar);
        }
        if (toolbar != null) {
            txtEdit = toolbar.findViewById(R.id.txtEdit);
            imgBack = toolbar.findViewById(R.id.imgBack);
            stagingRL = toolbar.findViewById(R.id.stagingRL);
            saveIV = toolbar.findViewById(R.id.saveIV);
            saveIV.setImageDrawable(getDrawable(R.drawable.ic_folder));
            saveIV.setVisibility(View.VISIBLE);
            saveIV.setOnClickListener(this);
        }

        // define ids
        txtCity = findViewById(R.id.txtCity);
        addPicTV = findViewById(R.id.addPicTV);
        completedTV = findViewById(R.id.completedTV);
        recyclerView = findViewById(R.id.recyclerView);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        refreshType = findViewById(R.id.refreshType);
        txtLocation = findViewById(R.id.txtLocation);
        txtTaskCompleted = findViewById(R.id.txtTaskCompleted);
        txtComplaint = findViewById(R.id.txtComplaint);

        voiceRL = findViewById(R.id.voiceRL);
        etNote = findViewById(R.id.etNote);
        complaintTV = findViewById(R.id.complaintTV);
        txtActivityName = findViewById(R.id.txtActivityName);
        addIV = findViewById(R.id.addIV);
        etLocation = findViewById(R.id.etLocation);
        etCode = findViewById(R.id.etCode);
        refreshIV = findViewById(R.id.refreshIV);
        voiceTV = findViewById(R.id.voiceTV);
        txtDay = findViewById(R.id.txtDay);
        dateTV = findViewById(R.id.dateTV);
        nameSP = findViewById(R.id.nameSP);
        locationSP = findViewById(R.id.locationSP);
        etActivityName = findViewById(R.id.etActivityName);
        spinIV = findViewById(R.id.spinIV);

        spinnerTaskCompleted = findViewById(R.id.spinnerTaskCompleted);
        spinnerComplaint = findViewById(R.id.spinnerComplaint);
        btnSave.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        refreshIV.setOnClickListener(this);
        addIV.setOnClickListener(this);
        completedTV.setOnClickListener(this);
        refreshType.setOnClickListener(this);
        etActivityName.setOnClickListener(this);
        complaintTV.setOnClickListener(this);
        spinIV.setOnClickListener(this);
        selectionList = CommonMethods.getSelectionList();
        workLogDate = CommonMethods.getCurrentDateByFormat("dd MMM, yyyy");
        workLogTime = CommonMethods.getCurrentDateByFormat("HH:mm");
        String currentDate = CommonMethods.getCurrentDateByFormat("dd MMM, yyyy");
        String currentDay = CommonMethods.getCurrentDay();
        //  dateTV.setText(currentDate);
        txtDay.setText(currentDay);
        if (getIntent().hasExtra("WorkLogDate")) {
            workLogDate = getIntent().getStringExtra("WorkLogDate");
            dateTV.setText(workLogDate);

            try {
                workLogDay = CommonMethods.getDayFromDate(workLogDate);
                txtDay.setText(workLogDay);
            } catch (Exception e) {
                Logger.e(Constants.ERROR_DETAIL, e.getMessage());
            }
        }

        //Code changes
        if (!CommonMethods.Mode.isLive(AddActivitity.this)) {
            stagingRL.setVisibility(View.VISIBLE);
        }

        getActivityList();

        txtCity.setText(projectSelected);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkRecordPermission();
        }
        //set Location data

        locationList = ProjectLocationRepo.GetLocalByProject(projectId);
        getInspection(locationList);


        // initialize recyclerview
        recyclerView.setLayoutManager(new LinearLayoutManager(AddActivitity.this, RecyclerView.HORIZONTAL, false));
        recyclerView.setNestedScrollingEnabled(false);
        if (getIntent().hasExtra(Constants.TABLE_ID)) {
            setEditData();

        } else {
            if (ActivitiesRepo.GetAll() != null) {
                if (!ActivitiesRepo.GetAll().isEmpty()) {
                    count = ActivitiesRepo.GetAll().size() + 1;
                }
            }
            listEdit.add("add");
            addAdapter = new AddAdapter(AddActivitity.this, listEdit);
            recyclerView.setAdapter(addAdapter);
        }
        spinnerTaskCompleted.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                strTaskCompleted = spinnerTaskCompleted.getSelectedItem().toString();
                isCompleted = Boolean.parseBoolean(strTaskCompleted);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerComplaint.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                strComplaint = spinnerComplaint.getSelectedItem().toString();
                isComplaint = Boolean.parseBoolean(strComplaint);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        nameSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        locationSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (locationList.size() > 0) {
                    locationName = locationList.get(i).name;
                    locationId = String.valueOf(locationList.get(i).locationId);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toString());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {
                etNote.setText("");
                voiceTV.setText(getString(R.string.listen_string));
            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {
                voiceTV.setText(getString(R.string.voicetext));
            }

            @Override
            public void onResults(Bundle bundle) {
                voiceTV.setText(getString(R.string.voicetext));
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                etNote.setText(data.get(0));
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        voiceRL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    voiceTV.setText(getString(R.string.voicetext));
                    speechRecognizer.stopListening();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    voiceTV.setText("Listening");
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
                return false;
            }
        });

        taskList = CommonMethods.getArrayList();

        getTaskCompleted(taskList);
        getComplaint(taskList);
        showComplaintDialog(selectionList);
        showCompletedDialog(selectionList);
        checkDarkMode();

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
        txtEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        addPicTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtDay.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtCity.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        dateTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtActivityName.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        etActivityName.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtTaskCompleted.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtComplaint.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtLocation.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        etNote.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        btnCancel.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        btnSave.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        voiceTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
    }

    private void showCompletedDialog(List<SubContractActivity> selectionList) {

        LayoutInflater inflater = (LayoutInflater)
                getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_card_layout, null);

        RecyclerView cardRV = view.findViewById(R.id.cardRV);
        cardRV.setLayoutManager(new LinearLayoutManager(AddActivitity.this));

        if (selectionList.size() > 0) {
            completedTV.setText(selectionList.get(1).parentName);
            isCompleted = false;
            fromComplaint = false;
            cardRV.setAdapter(new SelectionAdapter(AddActivitity.this, selectionList, fromComplaint));
        }

        popUpCompleted = new PopupWindow(view, 600, RelativeLayout.LayoutParams.WRAP_CONTENT, true);

        popUpCompleted.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

    }

    private void showComplaintDialog(List<SubContractActivity> selectionList) {

        LayoutInflater inflater = (LayoutInflater)
                getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_card_layout, null);

        RecyclerView cardRV = view.findViewById(R.id.cardRV);
        cardRV.setLayoutManager(new LinearLayoutManager(AddActivitity.this));

        if (selectionList.size() > 0) {
            fromComplaint = true;
            complaintTV.setText(selectionList.get(0).parentName);
            isComplaint = true;
            cardRV.setAdapter(new SelectionAdapter(AddActivitity.this, selectionList, fromComplaint));
        }

        popupComplaint = new PopupWindow(view, 600, RelativeLayout.LayoutParams.WRAP_CONTENT, true);

        popupComplaint.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

    }


    private void setEditData() {
        tableId = getIntent().getIntExtra(Constants.TABLE_ID, 0);
        editView = true;
        txtEdit.setText("Edit Activity");

        activityModel = ActivitiesRepo.Get(tableId);
        if (activityModel.imagepath.isEmpty()) {
            listEdit.add("add");
        } else {
            fileId = activityModel.itemGuId;
            editFileModel = FilesRepo.Get(fileId);
            listEdit.clear();
            for (int i = 0; i < editFileModel.size(); i++) {
                listEdit.add(editFileModel.get(i).getFullPath());
            }
            pathList.clear();
            pathList.addAll(listEdit);
            listEdit.add("add");
            strImagePath = activityModel.imagepath;
        }

        addAdapter = new AddAdapter(AddActivitity.this, listEdit);
        recyclerView.setAdapter(addAdapter);

        etActivityName.setText(activityModel.activityCode + "-" + activityModel.activityname);

        etLocation.setText(activityModel.location);
        etNote.setText(activityModel.note);
        strActivityName = activityModel.activityname;
        strCode = activityModel.code;
        strLocation = activityModel.location;
        locationName = activityModel.location;
        locationId = String.valueOf(activityModel.LocationId);
        for (int i = 0; i < locationList.size(); i++) {
            if (locationId.equalsIgnoreCase(String.valueOf(locationList.get(i).locationId))) {
                locationSP.setSelection(i);
            }
        }
        strNote = activityModel.note;
        count = activityModel.userid;
        isComplaint = Boolean.parseBoolean(activityModel.IsCompliant);
        isCompleted = Boolean.parseBoolean(activityModel.IsCompleted);
        if (isComplaint) {
            complaintTV.setText(getString(R.string.yes));
        } else {
            complaintTV.setText(getString(R.string.no));
        }
        if (isCompleted) {
            completedTV.setText(getString(R.string.yes));
        } else {
            completedTV.setText(getString(R.string.no));
        }
        activityName = activityModel.activityname;
        activityCode = activityModel.activityCode;
        activityId = String.valueOf(activityModel.ActivityId);

       /* if (activityModel.imagepath.isEmpty()) {

            setSavedImageData(activityModel.imagepath);
        }*/
    }

    private void getInspection(List<ProjectLocation> listInspection) {
        try {

            arrayLocation = new LocationArrayAdapter(AddActivitity.this, android.R.layout.simple_spinner_item, listInspection) {
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
            arrayLocation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            locationSP.setAdapter(arrayLocation);
            locationName = listInspection.get(0).name;
            locationId = String.valueOf(listInspection.get(0).locationId);

        } catch (Exception e) {
            CommonMethods.showLogger(AddActivitity.this, e);
        }

    }

    private void getActivityList() {
        activityList = ProjectActivityRepo.getCodeByParent(parentId, String.valueOf(projectId));
        try {

            arrayName = new ActivityListAdapter(AddActivitity.this, android.R.layout.simple_spinner_item, activityList) {
                @Override
                public boolean isEnabled(int position) {

                    if (activityList.size() > 0) {
                        activityName = activityList.get(position).name;
                        activityCode = activityList.get(position).activityCode;
                        activityId = String.valueOf(activityList.get(position).activityId);
                        activityCodeList = ProjectActivityRepo.getCodeByParent(activityId, String.valueOf(projectId));
                        if (activityCodeList.size() > 0)
                            showActivityCode(activityCodeList);
                        try {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                popupActivity.showAsDropDown(nameSP, 12, 0, Gravity.BOTTOM);
                            }
                            popupActivity.showAtLocation(nameSP, Gravity.TOP, 2, 0);

                        } catch (Exception e) {
                            Logger.e(Constants.ERROR_DETAIL, e.getMessage());
                        }
                    }
                    return true;
                }

                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);

                    return view;
                }
            };
            arrayName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            nameSP.setAdapter(arrayName);

        } catch (Exception e) {
            CommonMethods.showLogger(AddActivitity.this, e);
        }

    }

    private void checkRecordPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, recordAudioRequestCode);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.refreshIV:

                getLocation();

                break;
            case R.id.complaintTV:

                try {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        popupComplaint.showAsDropDown(complaintTV, 12, 0, Gravity.BOTTOM);
                    }
                    popupComplaint.showAtLocation(complaintTV, Gravity.TOP, 2, 0);

                } catch (Exception e) {
                    Logger.e(Constants.ERROR_DETAIL, e.getMessage());
                }


                break;

            case R.id.completedTV:

                try {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        popUpCompleted.showAsDropDown(completedTV, 12, 0, Gravity.BOTTOM);
                    }
                    popUpCompleted.showAtLocation(completedTV, Gravity.TOP, 2, 0);

                } catch (Exception e) {
                    Logger.e(Constants.ERROR_DETAIL, e.getMessage());
                }

                break;

            case R.id.dateTV:
                try {
                    setDatePicker();
                } catch (Exception e) {
                    Logger.e(Constants.ERROR_DETAIL, e.getMessage());
                }


                break;
            case R.id.refreshType:

                getProjectActivityType();

                break;
            case R.id.spinIV:
            case R.id.etActivityName:

                parentId = String.valueOf(0);
                activityList = ProjectActivityRepo.getCodeByParent(parentId, String.valueOf(projectId));
                showParentActivity(activityList);

                try {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        popupActivity.showAsDropDown(etActivityName, 12, 0, Gravity.BOTTOM);
                    }
                    popupActivity.showAtLocation(etActivityName, Gravity.TOP, 2, 0);

                } catch (Exception e) {
                    Logger.e(Constants.ERROR_DETAIL, e.getMessage());
                }
                break;
            case R.id.addIV:

                Intent intent1 = new Intent(AddActivitity.this, SaveActivityType.class);
                startActivity(intent1);

                break;
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.saveIV:
                Intent intent = new Intent(AddActivitity.this, DailyFolderActivity.class);
                intent.putExtra("selectFolder", "true");
                startActivityForResult(intent, Constants.SECOND_ACTIVITY_REQUEST_CODE);
                break;

            case R.id.btnSave:

                uid = CommonMethods.getUuid();
                activityModel = new ActivityModel();
                filesModel = new FilesModel();
                if (editView) {
                    try {
                        activityModel.setId(Long.valueOf(tableId));
                    } catch (Exception e) {
                        CommonMethods.showLog("Log Data", e.getMessage());
                    }
                }
                activityModel.note = (etNote.getText().toString().trim());
                if (etNote.getText().toString().trim().isEmpty()) {
                    activityModel.note = Constants.DEFAULT_VAL;
                }
                activityModel.activityname = activityName;
                activityModel.activityCode = activityCode;
                activityModel.ActivityId = Integer.parseInt(activityId);

                activityModel.LocationId = Integer.parseInt(locationId);
                activityModel.IsCompliant = String.valueOf(isComplaint);
                activityModel.IsCompleted = String.valueOf(isCompleted);

                activityModel.taskcompleted = (strTaskCompleted);
                activityModel.complaint = (strComplaint);

                activityModel.WORK_LOG_DATE = workLogDate;
                activityModel.WorkLogId = 11;
                activityModel.imagepath = (strImagePath);
                String timeValue = workLogDate + " " + workLogTime;
                String time = CommonMethods.convertLocalToUtc(timeValue);
                activityModel.AddedOn = time;
                activityModel.LastModOn = time;
                activityModel.projectID = String.valueOf(projectId);
                if (fileId.isEmpty()) {
                    activityModel.itemGuId = uid;
                } else {
                    uid = fileId;
                    activityModel.itemGuId = uid;
                    try {
                        FilesRepo.DeleteFileByUid(uid);
                    } catch (Exception e) {
                        Logger.e(Constants.ERROR_DETAIL, e.getMessage());
                    }
                }
                String data = ActivitiesRepo.Save(activityModel);

                if (pathList.size() > 0) {
                    saveImages(time);
                }

                if (data.isEmpty()) {
                    finish();
                } else {
                    CommonMethods.showToast(AddActivitity.this, data);
                }

                break;
        }
    }

    private void saveImages(String time) {
        if (!uid.isEmpty()) {
            for (int i = 0; i < pathList.size(); i++) {
                int randomNumber = CommonMethods.getRandomNumber();
                filesModel = new FilesModel();
                filesModel.AddedOn = time;
                filesModel.LastModOn = time;
                filesModel.ItemGuId = uid;
                filesModel.FullPath = pathList.get(i);
                filesModel.fileId = randomNumber;
                String repoResponse = FilesRepo.Save(filesModel);

            }

        }
    }

    public void setDatePicker() {

        int mYear, mMonth, mDay;

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(AddActivitity.this,
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
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }

    private void getLocation() {

        dialogLocation = new ProgressDialog(AddActivitity.this);
        try {
            dialogLocation.show();
        } catch (WindowManager.BadTokenException e) {

        }
        dialogLocation.setCancelable(false);
        dialogLocation.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogLocation.setContentView(R.layout.custom_loading_layout);

        boolean isRefresh = ServicesMethod.getLocationMethod(dialogLocation, AddActivitity.this, projectId);

        if (isRefresh) {
            locationList = ProjectLocationRepo.GetLocalByProject(projectId);
            getInspection(locationList);
        }
    }

    private void getTaskCompleted(List<String> listTaskCompleted) {
        try {

            arrayTaskCompleted = new ArrayAdapter(AddActivitity.this, android.R.layout.simple_spinner_item, listTaskCompleted) {
                @Override
                public boolean isEnabled(int position) {
                    return true;
                }

                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView textview = (TextView) view;
                    textview.setTextColor(ContextCompat.getColor(AddActivitity.this, R.color.black));
                    return view;
                }
            };
            arrayTaskCompleted.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTaskCompleted.setAdapter(arrayTaskCompleted);

            int makePosition = arrayTaskCompleted.getPosition(activityModel.taskcompleted);
            spinnerTaskCompleted.setSelection(makePosition);


        } catch (Exception e) {
            Logger.e(Constants.ERROR_DETAIL, e.getMessage());
        }

    }

    private void getComplaint(List<String> listTaskCompleted) {
        try {
            //    spinnerComplaint.setOnItemSelectedListener(AddActivitity.this);
            arrayComplaint = new ArrayAdapter(AddActivitity.this, android.R.layout.simple_spinner_item, listTaskCompleted) {
                @Override
                public boolean isEnabled(int position) {

                    return true;
                }

                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView textview = (TextView) view;

                    textview.setTextColor(ContextCompat.getColor(AddActivitity.this, R.color.black));

                    return view;
                }
            };
            arrayComplaint.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerComplaint.setAdapter(arrayTaskCompleted);

            int makePosition = arrayComplaint.getPosition(activityModel.IsCompliant);
            spinnerComplaint.setSelection(makePosition);


        } catch (Exception e) {

        }

    }

    public Bitmap checkRotate(Bitmap bitmap) {
        Bitmap rotatedBitmap = null;
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(selectedImagePath);

            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);


            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = RotateBitmap(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = RotateBitmap(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = RotateBitmap(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
        } catch (IOException e) {

        }
        return rotatedBitmap;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

                Uri uri = Uri.fromFile(output);
                selectedImagePath = CommonMethods.getPath(AddActivitity.this, uri);

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(AddActivitity.this.getContentResolver(), uri);

                bitmap = checkRotate(bitmap);
                uri = CommonMethods.bitmapToUriConverter(bitmap, AddActivitity.this);
                selectedImagePath = CommonMethods.getPath(AddActivitity.this, uri);
                setImageData(bitmap, selectedImagePath, uri);
            }

            if (resultCode == RESULT_OK && requestCode == GALLERY_REQUEST_CODE) {


                if (data.getData() != null) {
                    Uri selectedImageUri = data.getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(AddActivitity.this.getContentResolver(), selectedImageUri);
                    selectedImageUri = CommonMethods.bitmapToUriConverter(bitmap, AddActivitity.this);
                    selectedImagePath = CommonMethods.getPath(AddActivitity.this, selectedImageUri);
                    setImageData(bitmap, selectedImagePath, selectedImageUri);
                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            if (i < 4) {
                                ClipData.Item item = mClipData.getItemAt(i);
                                //Uri uri = item.getUri();
                                Uri selectedImageUri = item.getUri();
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(AddActivitity.this.getContentResolver(), selectedImageUri);
                                selectedImageUri = CommonMethods.bitmapToUriConverter(bitmap, AddActivitity.this);
                                selectedImagePath = CommonMethods.getPath(AddActivitity.this, selectedImageUri);
                                setImageData(bitmap, selectedImagePath, selectedImageUri);
                            }

                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                    }
                }


            }

            if (requestCode == RESULT_CROP && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap selectedBitmap = extras.getParcelable("data");


                Uri selectedImageUri = CommonMethods.bitmapToUriConverter(selectedBitmap, AddActivitity.this);
                selectedImagePath = CommonMethods.getPath(AddActivitity.this, selectedImageUri);
                setImageData(selectedBitmap, selectedImagePath, selectedImageUri);

                Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(selectedImagePath), 220, 220);

            }
            if (requestCode == Constants.SECOND_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

                long noteID = data.getLongExtra("folderID", 0l);
                setFolderData(noteID);

            }

        } catch (Exception e) {
            Logger.e(e.getMessage());

        }
    }

    private void setFolderData(long noteID) {
        DailyFolder folderData = DailyFolderRepo.Get((int) noteID);
        etNote.setText(folderData.note);
        setSavedImageData(folderData.image);
    }

    private void performCrop(String picUri) {
        try {
            //Start Crop Activity

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            File f = new File(picUri);
            Uri contentUri = Uri.fromFile(f);
            Uri photoURI = FileProvider.getUriForFile(AddActivitity.this, AddActivitity.this.getPackageName() + ".provider", f);

            //Uri photoURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".my.package.name.provider", createImageFile());

            cropIntent.setDataAndType(photoURI, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 350);
            cropIntent.putExtra("outputY", 350);

            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, RESULT_CROP);

        }
        // respond to users whose devices do not support the crop action
        catch (Exception anfe) {
            // display an error message
            String errorMessage = "Your device does not support editing";
            Toast toast = Toast.makeText(AddActivitity.this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == recordAudioRequestCode && grantResults.length > 0) {


        }
    }

    public void showDiloag() {
        try {
            Dialog dialog = new Dialog(AddActivitity.this);
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AddActivitity.this);
            builder.setTitle(AddActivitity.this.getResources().getString(R.string.select_image));
            builder.setItems(new CharSequence[]{"Camera", "Gallery"}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            openCamera();
                            break;
                        case 1:
                            pickGalleryImage();
                            break;
                        default:
                            break;
                    }
                }
            });
            builder.show();
            dialog.dismiss();
        } catch (Exception e) {
            //GlobalMethods.SendErrorReport(e,context);
        }
    }


    public void pickGalleryImage() {
        try {
            imageStatus = true;
            if (Build.VERSION.SDK_INT < 19) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, AddActivitity.this.getResources().getString(R.string.select_image)), GALLERY_REQUEST_CODE);
            } else {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUEST_CODE);
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());

        }
    }

    public void openCamera() {
        try {
            folder = AddActivitity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            //folder = new File(Environment.getExternalStorageDirectory(),"Nagad");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            imageStatus = true;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                try {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    output = new File(folder, System.currentTimeMillis() + ".jpg");

                    Uri photoURI = FileProvider.getUriForFile(AddActivitity.this, AddActivitity.this.getPackageName() + ".provider", output);
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoURI);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                } catch (Exception e) {
                    Logger.e(e.getMessage());
                }
            } else {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                output = new File(folder, System.currentTimeMillis() + ".jpg");
                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }

        } catch (Exception e) {
            //GlobalMethods.SendErrorReport(e,context);
        }
    }


    private boolean isPermissionAllowed() {
        try {
            int camera = ContextCompat.checkSelfPermission(AddActivitity.this, Manifest.permission.CAMERA);
            int writestorage = ContextCompat.checkSelfPermission(AddActivitity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            List<String> listPermissionNeeded = new ArrayList<>();

            if (camera != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(Manifest.permission.CAMERA);
            }

            if (writestorage != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (!listPermissionNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(AddActivitity.this, listPermissionNeeded.toArray
                        (new String[listPermissionNeeded.size()]), PERMISSION_REQUEST_CODE);
                return false;
            }

        } catch (Exception e) {
            //GlobalMethods.SendErrorReport(e,context);
        }
        return true;
    }

    public void requestPermission() {
        try {
            if (ActivityCompat.shouldShowRequestPermissionRationale(AddActivitity.this, Manifest.permission.CAMERA)) {

            }
            if (ActivityCompat.shouldShowRequestPermissionRationale(AddActivitity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            }

            ActivityCompat.requestPermissions(AddActivitity.this, new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            //GlobalMethods.SendErrorReport(e,context);
        }
    }

    public void setImageData(Bitmap bitmap, String path, Uri uri) {
        // listEdit = new ArrayList<>();
        for (int i = 0; i < listEdit.size(); i++) {
            if (listEdit.get(i).equalsIgnoreCase("add")) {
                listEdit.remove(i);
            }
        }
        listEdit.add(path);
        strImagePath = path;
        pathList.clear();
        pathList.addAll(listEdit);
        listEdit.add("add");
        addAdapter = new AddAdapter(AddActivitity.this, listEdit);
        recyclerView.setAdapter(addAdapter);
    }

    public void setSavedImageData(String path) {
        listEdit = new ArrayList<>();
        listEdit.add("add");
        listEdit.add(path);
        strImagePath = path;
        addAdapter = new AddAdapter(AddActivitity.this, listEdit);
        recyclerView.setAdapter(addAdapter);

    }

    public void getImage() {
        if (!CommonMethods.CheckPermission.isPermissionAllowed(AddActivitity.this, PERMISSION_REQUEST_CODE)) {
            CommonMethods.CheckPermission.requestPermission(AddActivitity.this, PERMISSION_REQUEST_CODE);
            return;
        }
        showDiloag();
    }

    @Override
    public void checkChildClick(ProjectActivityType list, int position) {
        try {

            activityName = list.name;
            activityCode = list.activityCode;
            activityId = String.valueOf(list.activityId);
            etActivityName.setText(activityCode + "-" + activityName);
            popupActivity.dismiss();

        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    @Override
    public void checkInnerClick(ProjectActivityType list, int position) {
        try {

            activityName = list.name;
            activityCode = list.activityCode;
            activityId = String.valueOf(list.activityId);
            etActivityName.setText(activityCode + "-" + activityName);
            popupActivity.dismiss();

        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    @Override
    public void itemClickListener(SubContractActivity list, int position, boolean checkStatus) {
        if (checkStatus) {
            if (popupComplaint != null) {
                popupComplaint.dismiss();
            }

            strComplaint = list.IsCompliant;
            isComplaint = Boolean.parseBoolean(strComplaint);
            complaintTV.setText(list.parentName);
        } else {
            if (popUpCompleted != null) {
                popUpCompleted.dismiss();
            }

            strTaskCompleted = list.IsCompliant;
            isCompleted = Boolean.parseBoolean(strTaskCompleted);
            completedTV.setText(list.parentName);
        }

    }

    @Override
    public void checkItemCodeClick(ProjectActivityType list, int position) {
        try {
            if (activityCodeList.size() > 0) {
                activityName = activityCodeList.get(position).name;
                activityCode = activityCodeList.get(position).activityCode;
                activityId = String.valueOf(activityCodeList.get(position).activityId);
                etActivityName.setText(activityName);
                popupChild.dismiss();
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }


    }

    @Override
    public void checkParentClick(ProjectActivityType list, int position) {
        try {
            if (activityList.size() > 0) {
                activityName = activityList.get(position).name;
                activityCode = activityList.get(position).activityCode;
                activityId = String.valueOf(activityList.get(position).activityId);
                popupActivity.dismiss();

            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }

    }

    private void showActivityCode(List<ProjectActivityType> projectData) {
        LayoutInflater inflater = (LayoutInflater)
                getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_card_layout, null);

        RecyclerView cardRV = view.findViewById(R.id.cardRV);
        cardRV.setLayoutManager(new LinearLayoutManager(AddActivitity.this));

        if (projectData.size() > 0) {
            fromParent = false;

            cardRV.setAdapter(new ShowActivityCodeAdapter(AddActivitity.this, projectData, fromParent));
        }

        popupChild = new PopupWindow(view, 600, RelativeLayout.LayoutParams.WRAP_CONTENT, true);

        popupChild.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

    }

    private void showParentActivity(List<ProjectActivityType> projectData) {
        LayoutInflater inflater = (LayoutInflater)
                getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_card_layout, null);

        RecyclerView cardRV = view.findViewById(R.id.cardRV);
        cardRV.setLayoutManager(new LinearLayoutManager(AddActivitity.this));

        if (projectData.size() > 0) {
            fromParent = true;

            cardRV.setAdapter(new ShowActivityCodeAdapter(AddActivitity.this, projectData, fromParent));
        }

        popupActivity = new PopupWindow(view, 600, RelativeLayout.LayoutParams.WRAP_CONTENT, true);

        popupActivity.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

    }

    public void getProjectActivityType() {
        try {
            dialogProject = new ProgressDialog(AddActivitity.this);
            try {
                dialogProject.show();
            } catch (WindowManager.BadTokenException e) {
                Logger.e(Constants.ERROR_DETAIL, e.getMessage());
            }
            dialogProject.setCancelable(false);
            dialogProject.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialogProject.setContentView(R.layout.custom_loading_layout);
            ProjectActivityTypes web_api_2 = ApiClientClass.getApiClientDev(AddActivitity.this).create(ProjectActivityTypes.class);
            Call<ProjectActivityTypeModel> call = web_api_2.getProjectActivity(projectId, pageSize, -1);

            if (call != null) {
                call.enqueue(new Callback<ProjectActivityTypeModel>() {
                    @Override
                    public void onResponse(Call<ProjectActivityTypeModel> call, Response<ProjectActivityTypeModel> response) {
                        dialogProject.hide();

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
                        Toast.makeText(AddActivitity.this, getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                        CommonMethods.showLog(TAG, "Failure " + t.getMessage());
                    }
                });
            }
        } catch (Exception e) {

            if (dialogProject != null) {
                dialogProject.hide();
            }
            Log.e(TAG, e.getMessage());
        }

    }

    private void checkDarkMode() {
        if (CommonMethods.checkDarkMode(AddActivitity.this)) {
            txtEdit.setTextColor(ContextCompat.getColor(AddActivitity.this, R.color.white));
            addPicTV.setTextColor(ContextCompat.getColor(AddActivitity.this, R.color.white));
        }
    }

    @Override
    public void deleteItem(int position) {
        try {
            FilesRepo.DeleteFileByProject(editFileModel.get(position).fileId);
            pathList.remove(position);
        } catch (Exception e) {
            Logger.e(Constants.ERROR_DETAIL, e.getMessage());
        }
    }

    private class GenericTextWatcher implements TextWatcher {

        private final View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            switch (view.getId()) {
                case R.id.etActivityName:

                    strActivityName = editable.toString();


                    break;
                case R.id.etCode:

                    strCode = editable.toString();

                    break;
                case R.id.etLocation:

                    strLocation = editable.toString();

                    break;
                case R.id.etNote:

                    strNote = editable.toString();

                    break;

            }
        }
    }
}
