package com.dailyreporting.app.activities;


import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dailyreporting.app.R;
import com.dailyreporting.app.WebApis.ApiClientClass;
import com.dailyreporting.app.WebApis.ProjectActivityTypes;
import com.dailyreporting.app.adapters.ActivityListAdapter;
import com.dailyreporting.app.adapters.LocationArrayAdapter;
import com.dailyreporting.app.adapters.SelectionAdapter;
import com.dailyreporting.app.adapters.ShowActivityChildAdapter;
import com.dailyreporting.app.adapters.ShowActivityCodeAdapter;
import com.dailyreporting.app.adapters.ShowChildCodeAdapter;
import com.dailyreporting.app.adapters.ShowCommonActivityAdapter;
import com.dailyreporting.app.adapters.SubContractorImageAdapter;
import com.dailyreporting.app.database.DailyFolderRepo;
import com.dailyreporting.app.database.FilesRepo;
import com.dailyreporting.app.database.ProjectActivityRepo;
import com.dailyreporting.app.database.ProjectLocationRepo;
import com.dailyreporting.app.database.SubContractorRepo;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dailyreporting.app.utils.Constants.MyPREFERENCES;

public class AddSubContractorActivity extends AppCompatActivity implements ShowActivityChildAdapter.InterfaceList, SelectionAdapter.InterfaceList, View.OnClickListener, ShowCommonActivityAdapter.InterfaceList, ShowChildCodeAdapter.InterfaceList, ShowActivityCodeAdapter.InterfaceList, SubContractorImageAdapter.InterfaceList {
    private final int CAMERA_REQUEST_CODE = 10;
    private final int GALLERY_REQUEST_CODE = 20;
    private final int REQUEST_CODE = 40;
    private final int RESULT_CROP = 30;
    private final int PERMISSION_REQUEST_CODE = 99;
    private final String strUserId = "";
    private final int pageSize = 300;
    private final List<String> pathList = new ArrayList<>();
    TextView voiceTV;
    TextView arrivalET, departET, txtCity, addPicTV;
    RelativeLayout countRL;
    TextView etLocation;
    TextView etActivityName, childTV;
    TextView complaintTV, completedTV;
    private RecyclerView recyclerView;
    private SubContractorImageAdapter subContractorImageAdapter;
    private ImageView imgBack;
    private Spinner spinnerTaskCompleted, spinnerComplaint;
    private ArrayAdapter arrayTaskCompleted;
    private ArrayAdapter arrayComplaint;
    private List<String> listTaskCompleted = new ArrayList<>();
    ////////////////////////
    private String selectedImagePath;
    private boolean imageStatus = false;
    private File output;
    private File folder;
    private String strComplaint;
    private String strTaskCompleted = Constants.CONSTANT_VAL;
    private EditText etCode, etNote, personET;
    private Button btnSave, btnCancel;
    private int count = 1;
    private String strActivityName = "";
    private String strCode = "";
    private String strNote = "";
    private String strLocation = "";
    private String strImagePath = "";
    private List<String> listEdit = new ArrayList<>();
    private SubContractActivity subContractActivity = new SubContractActivity();
    private TextView txtEdit, txtDay, dateTV;
    private Spinner locationSP;
    private List<ProjectLocation> locationList = new ArrayList<>();
    private LocationArrayAdapter arrayLocation;
    private String locationName = "";
    private int locationId = 0;
    private int tableId = 0;
    private boolean editView = false;
    private Spinner nameSP;
    private List<ProjectActivityType> activityList = new ArrayList<>();
    private ActivityListAdapter arrayName;
    private String activityName = "";
    private int activityId = 0;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String projectSelected = "";
    private EditText workerET;
    private String currentDate = "";
    private String departDate = "";
    private String arrivalTime = "";
    private RelativeLayout stagingRL;
    private SpeechRecognizer speechRecognizer;
    private ImageView saveIV, dropIV, cDropIV, addIV, refreshType;
    private RelativeLayout voiceRL;
    private String parentId = "0";
    private PopupWindow popupActivity;
    private boolean fromParent = false;
    private String activityCode = "0";
    private PopupWindow popupChild, popupComplaint, popUpCompleted;
    private List<ProjectActivityType> activityCodeList = new ArrayList<>();
    private String activityChildName = "";
    private String activityChildCode = "";
    private int activityChildId = 0;
    private ProgressDialog dialogProject;
    private int projectId = 0;
    private int noOfWorker = 1;
    private List<String> taskList = new ArrayList<>();
    private boolean isComplaint = true;
    private boolean isCompleted = true;
    private String workLogDate = "";
    private String workLogDay = "";
    private String workLogTime = "";
    private List<SubContractActivity> selectionList = new ArrayList<>();
    private boolean fromComplaint = false;
    private boolean settingFont = false;
    private float fontSize = 13f;
    private TextView personTV, txtActivityName, txtLocation, arrivalTV, departTV, workerTV, txtTaskCompleted, txtComplaint;
    private String fileId = "";
    private List<FilesModel> editFileModel = new ArrayList<>();
    private String uid = "";
    private FilesModel filesModel = new FilesModel();


    ///////////////////////// code for Camera gallery /////////////////////////
    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);

        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        checkLang();
        setContentView(R.layout.activity_add_contract);
        listEdit = new ArrayList<>();
        Intent intent = getIntent();
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        projectSelected = sharedPreferences.getString(Constants.PROJECT_SELECTED, "");
        workLogDate = CommonMethods.getCurrentDateByFormat("dd MMM, yyyy");
        workLogTime = CommonMethods.getCurrentDateByFormat("HH:mm");
        Toolbar toolbar = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            toolbar = findViewById(R.id.toolbar);
        }
        if (toolbar != null) {
            txtEdit = toolbar.findViewById(R.id.txtEdit);
            imgBack = toolbar.findViewById(R.id.imgBack);
            stagingRL = toolbar.findViewById(R.id.stagingRL);
            txtEdit.setText(getText(R.string.save_sub_contract));
            saveIV = toolbar.findViewById(R.id.saveIV);
            saveIV.setImageDrawable(getDrawable(R.drawable.ic_folder));
            saveIV.setVisibility(View.VISIBLE);
            saveIV.setOnClickListener(this);
        }

        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        projectId = sharedPreferences.getInt(Constants.PROJECT_ID, 0);
        // define ids
        recyclerView = findViewById(R.id.recyclerView);
        addIV = findViewById(R.id.addIV);
        addPicTV = findViewById(R.id.addPicTV);
        txtCity = findViewById(R.id.txtCity);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        txtLocation = findViewById(R.id.txtLocation);
        arrivalTV = findViewById(R.id.arrivalTV);
        departTV = findViewById(R.id.departTV);
        workerTV = findViewById(R.id.workerTV);
        txtTaskCompleted = findViewById(R.id.txtTaskCompleted);
        txtComplaint = findViewById(R.id.txtComplaint);

        etNote = findViewById(R.id.etNote);
        personTV = findViewById(R.id.personTV);
        txtActivityName = findViewById(R.id.txtActivityName);
        countRL = findViewById(R.id.countRL);
        voiceRL = findViewById(R.id.voiceRL);
        arrivalET = findViewById(R.id.arrivalET);
        complaintTV = findViewById(R.id.complaintTV);
        departET = findViewById(R.id.departET);
        personET = findViewById(R.id.personET);
        completedTV = findViewById(R.id.completedTV);
        workerET = findViewById(R.id.workerET);
        etLocation = findViewById(R.id.etLocation);
        etCode = findViewById(R.id.etCode);
        locationSP = findViewById(R.id.locationSP);
        etActivityName = findViewById(R.id.etActivityName);
        childTV = findViewById(R.id.childTV);
        dropIV = findViewById(R.id.dropIV);
        refreshType = findViewById(R.id.refreshType);
        cDropIV = findViewById(R.id.cDropIV);
        txtDay = findViewById(R.id.txtDay);
        voiceTV = findViewById(R.id.voiceTV);
        dateTV = findViewById(R.id.dateTV);
        nameSP = findViewById(R.id.nameSP);
        spinnerTaskCompleted = findViewById(R.id.spinnerTaskCompleted);
        spinnerComplaint = findViewById(R.id.spinnerComplaint);
        btnSave.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        departET.setOnClickListener(this);
        arrivalET.setOnClickListener(this);
        childTV.setOnClickListener(this);
        dropIV.setOnClickListener(this);
        completedTV.setOnClickListener(this);
        cDropIV.setOnClickListener(this);
        etActivityName.setOnClickListener(this);
        refreshType.setOnClickListener(this);
        addIV.setOnClickListener(this);
        complaintTV.setOnClickListener(this);
        txtCity.setText(projectSelected);
        // initialize recycler view

        // Add Data in ArrayList
        listTaskCompleted = new ArrayList<>();
        listTaskCompleted.add("Yes");
        listTaskCompleted.add("No");
        dateTV.setText(currentDate);
        currentDate = CommonMethods.getCurrentDateByFormat("dd MMM, yyyy");
        String currentDay = CommonMethods.getCurrentDay();

        txtDay.setText(currentDay);
        if (getIntent().hasExtra("WorkLogDate")) {
            workLogDate = getIntent().getStringExtra("WorkLogDate");
            dateTV.setText(workLogDate);
            try {
                workLogDay = CommonMethods.getDayFromDate(workLogDate);
                txtDay.setText(workLogDay);
            } catch (Exception e) {
                Logger.e(e.getMessage());
            }

        }
        recyclerView.setLayoutManager(new LinearLayoutManager(AddSubContractorActivity.this, RecyclerView.HORIZONTAL, false));
        recyclerView.setNestedScrollingEnabled(false);
        getAllData();
        setDateTime();
        locationSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (locationList.size() > 0) {
                    etLocation.setVisibility(View.GONE);
                    locationName = locationList.get(i).name;
                    locationId = locationList.get(i).locationId;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (getIntent().hasExtra(Constants.TABLE_ID)) {
            setEditData();

        } else {
            if (SubContractorRepo.GetAll() != null) {
                if (SubContractorRepo.GetAll().size() > 0) {
                    count = SubContractorRepo.GetAll().size() + 1;
                }
            }
            listEdit.add("add");
            subContractorImageAdapter = new SubContractorImageAdapter(AddSubContractorActivity.this, listEdit);
            recyclerView.setAdapter(subContractorImageAdapter);
        }

        taskList = CommonMethods.getArrayList();

        selectionList = CommonMethods.getSelectionList();
        getTaskCompleted(taskList);
        getComplaint(taskList);
        showComplaintDialog(selectionList);
        showCompletedDialog(selectionList);
        setVoiceToText();
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


        stagingRL.setVisibility(View.GONE);
        setPopUpData();
        checkDarkMode();
    }

    private void setPopUpData() {
        parentId = String.valueOf(0);
        activityList = ProjectActivityRepo.getCodeByParent(parentId, String.valueOf(projectId));
        showParentActivity(activityList);
        String activityValue = String.valueOf(activityId);
        activityCodeList = ProjectActivityRepo.getCodeByParent(activityValue, String.valueOf(projectId));
        if (activityCodeList.size() > 0) {
            showActivityCode(activityCodeList);
        }
    }

    private void setDateTime() {
        arrivalTime = CommonMethods.convertLocalToUtc(CommonMethods.getCurrentTimeArrival());
        departDate = CommonMethods.convertLocalToUtc(CommonMethods.getCurrentTimeArrival());

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


    private void setEditData() {
        tableId = getIntent().getIntExtra(Constants.TABLE_ID, 0);
        editView = true;
        subContractActivity = SubContractorRepo.Get(tableId);

        if (subContractActivity.imagepath.isEmpty()) {
            listEdit.add("add");

        } else {
            fileId = subContractActivity.itemGuId;
            editFileModel = FilesRepo.Get(fileId);
            listEdit.clear();
            for (int i = 0; i < editFileModel.size(); i++) {
                listEdit.add(editFileModel.get(i).getFullPath());
            }
            pathList.clear();
            pathList.addAll(listEdit);
            listEdit.add("add");
            strImagePath = subContractActivity.imagepath;
        }
        subContractorImageAdapter = new SubContractorImageAdapter(AddSubContractorActivity.this, listEdit);
        recyclerView.setAdapter(subContractorImageAdapter);

        activityId = subContractActivity.parentId;
        activityCode = subContractActivity.parentCode;
        activityName = subContractActivity.parentName;
        activityChildId = subContractActivity.ActivityId;
        activityChildName = subContractActivity.activityname;
        activityChildCode = subContractActivity.activityCode;
        arrivalTime = subContractActivity.ArrivalTime;
        departDate = subContractActivity.DepartureTime;
        isComplaint = Boolean.parseBoolean(subContractActivity.IsCompliant);
        isCompleted = Boolean.parseBoolean(subContractActivity.IsCompleted);
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
        // etActivityName.setText(subContractActivity.parentCode + "-" + subContractActivity.parentName);
        etActivityName.setText(subContractActivity.activityCode + "-" + subContractActivity.activityname);
        etCode.setText(subContractActivity.code);
        etLocation.setVisibility(View.VISIBLE);
        etLocation.setText(subContractActivity.location);
        locationId = subContractActivity.LocationId;
        locationName = subContractActivity.location;
        etNote.setText(subContractActivity.Note);

        for (int i = 0; i < locationList.size(); i++) {
            if (locationId == locationList.get(i).locationId) {
                locationSP.setSelection(i);
            }
        }
        strActivityName = subContractActivity.activityname;
        strCode = subContractActivity.code;
        strLocation = subContractActivity.location;
        strNote = subContractActivity.Note;
        personET.setText(subContractActivity.contractorName);
        String arrivalEditTime = CommonMethods.getTimeFromUTC(subContractActivity.ArrivalTime);
        arrivalET.setText(arrivalEditTime);
        String departEditTime = CommonMethods.getTimeFromUTC(subContractActivity.DepartureTime);
        departET.setText(departEditTime);
        workerET.setText(String.valueOf(subContractActivity.NoOfWorkers));
        /*if (!subContractActivity.imagepath.isEmpty()) {
            setImageData(subContractActivity.imagepath);
        }*/
    }

    private void getAllData() {
        locationList = ProjectLocationRepo.GetLocalByProject(projectId);
        getInspection(locationList);

        activityList = ProjectActivityRepo.GetAll();
        getActivityList(activityList);
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
        personTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        personET.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtActivityName.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        etActivityName.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtLocation.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        arrivalTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        departTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        workerTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtTaskCompleted.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtComplaint.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        btnCancel.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        arrivalET.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        departET.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        completedTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

        complaintTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        etNote.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        voiceTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        addPicTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        btnSave.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        workerET.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

    }

    private void setVoiceToText() {
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

    }

    private void getActivityList(List<ProjectActivityType> activityList) {
        try {

            arrayName = new ActivityListAdapter(AddSubContractorActivity.this, android.R.layout.simple_spinner_item, activityList) {
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
            nameSP.setAdapter(arrayName);


        } catch (Exception e) {
            Logger.e(e.getMessage());
        }

    }

    private void getInspection(List<ProjectLocation> listInspection) {
        try {

            arrayLocation = new LocationArrayAdapter(AddSubContractorActivity.this, android.R.layout.simple_spinner_item, listInspection) {
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
            locationId = listInspection.get(0).locationId;

        } catch (Exception e) {
            Logger.e(e.getMessage());
        }

    }


    private void showParentActivity(List<ProjectActivityType> projectData) {
        LayoutInflater inflater = (LayoutInflater)
                getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_card_layout, null);

        RecyclerView cardRV = view.findViewById(R.id.cardRV);
        cardRV.setLayoutManager(new LinearLayoutManager(AddSubContractorActivity.this));

        if (projectData.size() > 0) {
            fromParent = true;
           /* activityId = projectData.get(0).activityId;
            activityName = projectData.get(0).name;
            activityCode = projectData.get(0).activityCode;*/
            cardRV.setAdapter(new ShowActivityCodeAdapter(AddSubContractorActivity.this, projectData, fromParent));
        }

        popupActivity = new PopupWindow(view, 600, RelativeLayout.LayoutParams.WRAP_CONTENT, true);

        popupActivity.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

    }

    private void showActivityCode(List<ProjectActivityType> projectData) {
        LayoutInflater inflater = (LayoutInflater)
                getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_card_layout, null);

        RecyclerView cardRV = view.findViewById(R.id.cardRV);
        cardRV.setLayoutManager(new LinearLayoutManager(AddSubContractorActivity.this));

        if (projectData.size() > 0) {
            fromParent = false;
            activityChildName = projectData.get(0).name;
            activityChildCode = projectData.get(0).activityCode;
            activityChildId = projectData.get(0).activityId;
            cardRV.setAdapter(new ShowChildCodeAdapter(AddSubContractorActivity.this, projectData, fromParent));
        }

        popupChild = new PopupWindow(view, 600, RelativeLayout.LayoutParams.WRAP_CONTENT, true);

        popupChild.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

    }

    private void showComplaintDialog(List<SubContractActivity> selectionList) {

        LayoutInflater inflater = (LayoutInflater)
                getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_card_layout, null);

        RecyclerView cardRV = view.findViewById(R.id.cardRV);
        cardRV.setLayoutManager(new LinearLayoutManager(AddSubContractorActivity.this));

        if (selectionList.size() > 0) {
            fromComplaint = true;
            complaintTV.setText(selectionList.get(0).parentName);
            isComplaint = true;
            cardRV.setAdapter(new SelectionAdapter(AddSubContractorActivity.this, selectionList, fromComplaint));
        }

        popupComplaint = new PopupWindow(view, 600, RelativeLayout.LayoutParams.WRAP_CONTENT, true);

        popupComplaint.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

    }

    private void showCompletedDialog(List<SubContractActivity> selectionList) {

        LayoutInflater inflater = (LayoutInflater)
                getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_card_layout, null);

        RecyclerView cardRV = view.findViewById(R.id.cardRV);
        cardRV.setLayoutManager(new LinearLayoutManager(AddSubContractorActivity.this));

        if (selectionList.size() > 0) {
            completedTV.setText(selectionList.get(1).parentName);
            isCompleted = false;
            fromComplaint = false;
            cardRV.setAdapter(new SelectionAdapter(AddSubContractorActivity.this, selectionList, fromComplaint));
        }

        popUpCompleted = new PopupWindow(view, 600, RelativeLayout.LayoutParams.WRAP_CONTENT, true);

        popUpCompleted.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

    }


    public void getProjectActivityType() {
        try {
            dialogProject = new ProgressDialog(AddSubContractorActivity.this);
            try {
                dialogProject.show();
            } catch (WindowManager.BadTokenException e) {
                Logger.e(e.getMessage());
            }
            dialogProject.setCancelable(false);
            dialogProject.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialogProject.setContentView(R.layout.custom_loading_layout);
            ProjectActivityTypes web_api_2 = ApiClientClass.getApiClientDev(AddSubContractorActivity.this).create(ProjectActivityTypes.class);
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
                        Toast.makeText(AddSubContractorActivity.this, getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        } catch (Exception e) {

            if (dialogProject != null) {
                dialogProject.hide();
            }

        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addIV:

                Intent intent1 = new Intent(AddSubContractorActivity.this, SaveActivityType.class);
                startActivity(intent1);

            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.refreshType:

                getProjectActivityType();

                break;
            case R.id.etActivityName:
            case R.id.dropIV:
                parentId = String.valueOf(0);
                activityList = ProjectActivityRepo.getCodeByParent(parentId, String.valueOf(projectId));
                showParentActivity(activityList);

                try {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        popupActivity.showAsDropDown(etActivityName, 12, 0, Gravity.BOTTOM);
                    }
                    popupActivity.showAtLocation(etActivityName, Gravity.TOP, 2, 0);

                } catch (Exception e) {
                    Logger.e(e.getMessage());
                }
                break;
            case R.id.childTV:
            case R.id.cDropIV:

                String activityValue = String.valueOf(activityId);
                activityCodeList = ProjectActivityRepo.getCodeByParent(activityValue, String.valueOf(projectId));
                if (activityCodeList.size() > 0) {

                    showActivityCode(activityCodeList);

                    try {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            popupChild.showAsDropDown(childTV, 12, 0, Gravity.BOTTOM);
                        }
                        popupChild.showAtLocation(childTV, Gravity.TOP, 2, 0);

                    } catch (Exception e) {
                        Logger.e(e.getMessage());
                    }
                }


                break;

            case R.id.complaintTV:

                try {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        popupComplaint.showAsDropDown(complaintTV, 12, 0, Gravity.BOTTOM);
                    }
                    popupComplaint.showAtLocation(complaintTV, Gravity.TOP, 2, 0);

                } catch (Exception e) {
                    Logger.e(e.getMessage());
                }


                break;

            case R.id.completedTV:

                try {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        popUpCompleted.showAsDropDown(completedTV, 12, 0, Gravity.BOTTOM);
                    }
                    popUpCompleted.showAtLocation(completedTV, Gravity.TOP, 2, 0);


                } catch (Exception e) {
                    Logger.e(e.getMessage());
                }


                break;

            case R.id.arrivalET:
                setTimeDialog();
                break;
            case R.id.departET:
                setDepartTimeDialog();
                break;

            case R.id.btnSave:
                uid = CommonMethods.getUuid();
                subContractActivity.activityname = activityChildName;
                subContractActivity.ActivityId = activityChildId;
                subContractActivity.activityCode = activityChildCode;
                subContractActivity.parentId = activityId;
                subContractActivity.parentCode = activityCode;
                subContractActivity.parentName = activityName;
                subContractActivity.location = locationName;
                subContractActivity.LocationId = locationId;
                subContractActivity.IsCompliant = String.valueOf(isComplaint);
                subContractActivity.IsCompleted = String.valueOf(isCompleted);
                subContractActivity.imagepath = (strImagePath);
                subContractActivity.ArrivalTime = arrivalTime;
                subContractActivity.DepartureTime = departDate;
                subContractActivity.WorkLogId = 11;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                String val = sdf.format(new Date(System.currentTimeMillis()));
                String timeValue = workLogDate + " " + workLogTime;
                String time = CommonMethods.convertLocalToUtc(timeValue);

                subContractActivity.AddedOn = time;

                subContractActivity.location = locationName;
                subContractActivity.projectID = String.valueOf(projectId);
                subContractActivity.LocationId = locationId;
                subContractActivity.Note = etNote.getText().toString().trim();
                if (etNote.getText().toString().trim().isEmpty()) {
                    subContractActivity.Note = Constants.DEFAULT_VAL;
                }

                subContractActivity.contractorName = personET.getText().toString().trim();
                try {
                    noOfWorker = Integer.parseInt(workerET.getText().toString());
                } catch (Exception e) {
                    Logger.e(e.getMessage());
                }
                subContractActivity.NoOfWorkers = noOfWorker;
                subContractActivity.imagepath = (strImagePath);
                subContractActivity.WORK_LOG_DATE = workLogDate;
                if (fileId.isEmpty()) {
                    subContractActivity.itemGuId = uid;
                } else {
                    uid = fileId;
                    subContractActivity.itemGuId = uid;
                    try {
                        FilesRepo.DeleteFileByUid(uid);
                    } catch (Exception e) {
                        Logger.e(e.getMessage());
                    }
                }
                String response = SubContractorRepo.Save(subContractActivity);
                if (pathList.size() > 0) {
                    saveImages(time);
                }

                if (response.isEmpty()) {
                    finish();
                } else {
                    CommonMethods.showToast(AddSubContractorActivity.this, response);
                }


                break;

            case R.id.saveIV:
                Intent intent = new Intent(AddSubContractorActivity.this, DailyFolderActivity.class);
                intent.putExtra("selectFolder", "true");
                startActivityForResult(intent, Constants.SECOND_ACTIVITY_REQUEST_CODE);
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

    private void getTaskCompleted(List<String> listTaskCompleted) {
        try {
            //  spinnerTaskCompleted.setOnItemSelectedListener(AddSubContractorActivity.this);
            arrayTaskCompleted = new ArrayAdapter(AddSubContractorActivity.this, android.R.layout.simple_spinner_item, listTaskCompleted) {
                @Override
                public boolean isEnabled(int position) {

                    return true;
                }

                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView textview = (TextView) view;
                    if (position == 0) {
                        textview.setTextColor(Color.BLACK);
                    } else {
                        textview.setTextColor(getResources().getColor(R.color.black));
                    }
                    return view;
                }
            };
            arrayTaskCompleted.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTaskCompleted.setAdapter(arrayTaskCompleted);

            int makePosition = arrayTaskCompleted.getPosition(subContractActivity.IsCompleted);
            spinnerTaskCompleted.setSelection(makePosition);


        } catch (Exception e) {

        }

    }

    private void getComplaint(List<String> listTaskCompleted) {
        try {
            //    spinnerComplaint.setOnItemSelectedListener(AddSubContractorActivity.this);
            arrayComplaint = new ArrayAdapter(AddSubContractorActivity.this, android.R.layout.simple_spinner_item, listTaskCompleted) {
                @Override
                public boolean isEnabled(int position) {

                    return true;
                }

                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView textview = (TextView) view;
                    if (position == 0) {
                        textview.setTextColor(Color.BLACK);
                    } else {
                        textview.setTextColor(getResources().getColor(R.color.black));
                    }
                    return view;
                }
            };
            arrayComplaint.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerComplaint.setAdapter(arrayTaskCompleted);

            /*int makePosition = arrayComplaint.getPosition(subContractActivity.IsCompliant);
            spinnerComplaint.setSelection(makePosition);*/


        } catch (Exception e) {

            Logger.e(e.getMessage());
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
            Logger.e(e.getMessage());
        }
        return rotatedBitmap;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

                Uri uri = Uri.fromFile(output);
                selectedImagePath = CommonMethods.getPath(AddSubContractorActivity.this, uri);

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(AddSubContractorActivity.this.getContentResolver(), uri);

                bitmap = checkRotate(bitmap);

                uri = CommonMethods.bitmapToUriConverter(bitmap, AddSubContractorActivity.this);
                selectedImagePath = CommonMethods.getPath(AddSubContractorActivity.this, uri);
                setImageData(selectedImagePath);
            }


            if (resultCode == RESULT_OK && requestCode == GALLERY_REQUEST_CODE) {


                if (data.getData() != null) {
                    Uri selectedImageUri = data.getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(AddSubContractorActivity.this.getContentResolver(), selectedImageUri);
                    selectedImageUri = CommonMethods.bitmapToUriConverter(bitmap, AddSubContractorActivity.this);
                    selectedImagePath = CommonMethods.getPath(AddSubContractorActivity.this, selectedImageUri);
                    setImageData(selectedImagePath);
                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            if (i < 4) {
                                ClipData.Item item = mClipData.getItemAt(i);
                                //Uri uri = item.getUri();
                                Uri selectedImageUri = item.getUri();
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(AddSubContractorActivity.this.getContentResolver(), selectedImageUri);
                                selectedImageUri = CommonMethods.bitmapToUriConverter(bitmap, AddSubContractorActivity.this);
                                selectedImagePath = CommonMethods.getPath(AddSubContractorActivity.this, selectedImageUri);
                                setImageData(selectedImagePath);
                            }

                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                    }
                }


            }

            if (requestCode == RESULT_CROP && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap selectedBitmap = extras.getParcelable("data");


                Uri selectedImageUri = CommonMethods.bitmapToUriConverter(selectedBitmap, AddSubContractorActivity.this);
                selectedImagePath = CommonMethods.getPath(AddSubContractorActivity.this, selectedImageUri);
                setImageData(selectedImagePath);

                Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(selectedImagePath), 220, 220);

            }
            if (requestCode == Constants.SECOND_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

                long noteID = data.getLongExtra("folderID", 0l);
                setFolderData(noteID);
            }


        } catch (Exception e) {
            Logger.e(e.getMessage());
            //GlobalMethods.SendErrorReport(e,context);
        }
    }


    private void setFolderData(long noteID) {
        DailyFolder folderData = DailyFolderRepo.Get((int) noteID);
        etNote.setText(folderData.note);
        setImageData(folderData.image);
    }


    private void performCrop(String picUri) {
        try {
            //Start Crop Activity

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            File f = new File(picUri);
            Uri contentUri = Uri.fromFile(f);
            Uri photoURI = FileProvider.getUriForFile(AddSubContractorActivity.this, AddSubContractorActivity.this.getPackageName() + ".provider", f);

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
            Toast toast = Toast.makeText(AddSubContractorActivity.this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    public void showDiloag() {
        try {
            Dialog dialog = new Dialog(AddSubContractorActivity.this);
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AddSubContractorActivity.this);
            builder.setTitle(AddSubContractorActivity.this.getResources().getString(R.string.select_image));
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
                startActivityForResult(Intent.createChooser(intent, AddSubContractorActivity.this.getResources().getString(R.string.select_image)), GALLERY_REQUEST_CODE);
            } else {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUEST_CODE);
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
            //GlobalMethods.SendErrorReport(e,context);
        }
    }

    public void openCamera() {
        try {
            folder = AddSubContractorActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            //folder = new File(Environment.getExternalStorageDirectory(),"Nagad");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            imageStatus = true;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                try {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    output = new File(folder, System.currentTimeMillis() + ".jpg");

                    Uri photoURI = FileProvider.getUriForFile(AddSubContractorActivity.this, AddSubContractorActivity.this.getPackageName() + ".provider", output);
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoURI);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                } catch (Exception e) {
                    Logger.e(e.getMessage());
                    //globalMethods.SendErrorReport(e,getApplicationContext());
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
            int camera = ContextCompat.checkSelfPermission(AddSubContractorActivity.this, Manifest.permission.CAMERA);
            int writestorage = ContextCompat.checkSelfPermission(AddSubContractorActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            List<String> listPermissionNeeded = new ArrayList<>();

            if (camera != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(Manifest.permission.CAMERA);
            }

            if (writestorage != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (!listPermissionNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(AddSubContractorActivity.this, listPermissionNeeded.toArray
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
            if (ActivityCompat.shouldShowRequestPermissionRationale(AddSubContractorActivity.this, Manifest.permission.CAMERA)) {

            }
            if (ActivityCompat.shouldShowRequestPermissionRationale(AddSubContractorActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            }

            ActivityCompat.requestPermissions(AddSubContractorActivity.this, new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            //GlobalMethods.SendErrorReport(e,context);
        }
    }

    public void setImageData(String path) {
        for (int i = 0; i < listEdit.size(); i++) {
            if (listEdit.get(i).equalsIgnoreCase("add")) {
                listEdit.remove(i);
            }
        }
        countRL.setVisibility(View.GONE);
        listEdit.add(path);
        strImagePath = path;
        pathList.clear();
        pathList.addAll(listEdit);
        listEdit.add("add");
        subContractorImageAdapter = new SubContractorImageAdapter(AddSubContractorActivity.this, listEdit);
        recyclerView.setAdapter(subContractorImageAdapter);
    }

    public void getImage() {
        if (!isPermissionAllowed()) {
            requestPermission();
            return;
        }
        showDiloag();
    }

    public void setTimeDialog() {

        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(AddSubContractorActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                arrivalET.setText(selectedHour + ":" + selectedMinute);
                String rawTime = selectedHour + ":" + selectedMinute;
                String timeValue = CommonMethods.getTimeValue(selectedHour + ":" + selectedMinute);
                arrivalET.setText(timeValue);
                if (!currentDate.isEmpty()) {
                    arrivalTime = CommonMethods.convertLocalToUtc(currentDate + " " + rawTime);
                    CommonMethods.showLog("Date", arrivalTime);
                }
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public void setDepartTimeDialog() {

        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(AddSubContractorActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                departET.setText(selectedHour + ":" + selectedMinute);
                String rawTime = selectedHour + ":" + selectedMinute;
                String timeValue = CommonMethods.getTimeValue(selectedHour + ":" + selectedMinute);
                departET.setText(timeValue);
                if (!currentDate.isEmpty()) {
                    departDate = CommonMethods.convertLocalToUtc(currentDate + " " + rawTime);
                    CommonMethods.showLog("Date", departDate);
                }
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    @Override
    public void checkItemCodeClick(ProjectActivityType list, int position) {
        try {
            if (activityList.size() > 0) {
                activityName = activityList.get(position).name;
                activityCode = activityList.get(position).activityCode;
                activityId = activityList.get(position).activityId;
                etActivityName.setText(activityCode + "-" + activityName);
                childTV.setText(getString(R.string.select_activity_type));
                popupActivity.dismiss();
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    @Override
    public void checkParentClick(ProjectActivityType list, int position) {

    }

    @Override
    public void checkChildClick(ProjectActivityType list, int position) {
      /*  try {
            if (activityCodeList.size() > 0) {
                activityChildName = list.name;
                activityChildCode = list.activityCode;
                activityChildId = list.activityId;
                childTV.setText(activityChildCode + "-" + activityChildName);
                popupChild.dismiss();
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
*/
        try {

            activityChildName = list.name;
            activityChildCode = list.activityCode;
            activityChildId = list.activityId;
            etActivityName.setText(activityChildCode + "-" + activityChildName);
            popupActivity.dismiss();

        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    @Override
    public void checkInnerClick(ProjectActivityType list, int position) {
       /* try {
            if (activityCodeList.size() > 0) {
                activityChildName = list.name;
                activityChildCode = list.activityCode;
                activityChildId = list.activityId;
                childTV.setText(activityChildCode + "-" + activityChildName);
                popupChild.dismiss();
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
*/
        try {

            activityChildName = list.name;
            activityChildCode = list.activityCode;
            activityChildId = list.activityId;
            etActivityName.setText(activityChildCode + "-" + activityChildName);
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

    private void checkDarkMode() {
        if (CommonMethods.checkDarkMode(AddSubContractorActivity.this)) {
            txtEdit.setTextColor(ContextCompat.getColor(AddSubContractorActivity.this, R.color.white));
            addPicTV.setTextColor(ContextCompat.getColor(AddSubContractorActivity.this, R.color.white));
        }
    }

    @Override
    public void deleteItem(int position) {
        try {
            FilesRepo.DeleteFileByProject(editFileModel.get(position).fileId);
            pathList.remove(position);
        } catch (Exception e) {
            Logger.e(e.getMessage());
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
