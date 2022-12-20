package com.dailyreporting.app.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.dailyreporting.app.WebApis.CorrectionCodes;
import com.dailyreporting.app.WebApis.ProjectLocations;
import com.dailyreporting.app.adapters.AddImageCommonAdapter;
import com.dailyreporting.app.adapters.CorrectionCodeAdapter;
import com.dailyreporting.app.adapters.LocationArrayAdapter;
import com.dailyreporting.app.database.CorrectionsCodeRepo;
import com.dailyreporting.app.database.CorrectionsRepo;
import com.dailyreporting.app.database.DailyFolderRepo;
import com.dailyreporting.app.database.FilesRepo;
import com.dailyreporting.app.database.ProjectLocationRepo;
import com.dailyreporting.app.models.ActivityModel;
import com.dailyreporting.app.models.Correction;
import com.dailyreporting.app.models.CorrectionCode;
import com.dailyreporting.app.models.CorrectionCodeResponse;
import com.dailyreporting.app.models.DailyFolder;
import com.dailyreporting.app.models.FilesModel;
import com.dailyreporting.app.models.ProjectLocation;
import com.dailyreporting.app.models.ProjectLocationResponse;
import com.dailyreporting.app.utils.CommonMethods;
import com.dailyreporting.app.utils.Constants;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dailyreporting.app.utils.Constants.MyPREFERENCES;

public class SaveCorrections extends AppCompatActivity implements View.OnClickListener, AddImageCommonAdapter.InterfaceList {
    private final String strTaskCompleted = "";
    private final int CAMERA_REQUEST_CODE = 10;
    private final int GALLERY_REQUEST_CODE = 20;
    private final int REQUEST_CODE = 40;
    private final int RESULT_CROP = 30;
    private final int count = 1;
    private final int PERMISSION_REQUEST_CODE = 99;
    private final List<ActivityModel> listAddActivity = new ArrayList<>();
    private final String strActivityName = "";
    private final String strCode = "";
    private final String strNote = "";
    private final String strLocation = "";
    private final List<String> pathList = new ArrayList<>();
    String TAG = getClass().getSimpleName();
    Spinner complaintSP;
    TextView txtCity;
    RelativeLayout countRL;
    ImageView refreshIV;
    Button btnCancel;
    TextView visitTV, personNameTv, companyNameTV, txtLocation, reasonTV, visitTitle;
    private List<String> listTaskCompleted = new ArrayList<>();
    private String strInspection = "yes";
    private RecyclerView recyclerView;
    private AddImageCommonAdapter addAdapter;
    private ImageView imgBack;
    private ArrayAdapter arrayTaskCompleted;
    private ArrayAdapter arrayComplaint;
    private boolean imageStatus = false;
    private File output;
    private File folder;
    private EditText codeET, etNote, personNameET, visitET;
    private Button btnSave;
    private String strImagePath = "";
    private List<String> listEdit = new ArrayList<>();
    private Correction correction;
    private TextView txtEdit, dateTV;
    private TextView txtDay;
    private boolean editView = false;
    private Spinner spinnerInspection;
    private Spinner spinnerCode;
    private List<CorrectionCode> codeList = new ArrayList<>();
    private String codeName = "0";
    private String codeId = "0";
    private ArrayAdapter arrayCode;
    private List<ProjectLocation> locationList = new ArrayList<>();
    private LocationArrayAdapter locationAdapter;
    private Spinner locationSP;
    private String locationName = "";
    private String locationId = "0";
    private int tableId = 0;
    private Correction correctionData;
    private String complaintValue = "";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String projectSelected = "";
    ////////////////////////
    private String selectedImagePath = "";
    private ProgressDialog dialogCorrection;
    private String authorizationToken = "";
    private RelativeLayout stagingRL;
    private ImageView saveIV;
    private TextView voiceTV, addPicTV;
    private SpeechRecognizer speechRecognizer;
    private RelativeLayout voiceRL;
    private int projectID = 0;
    private List<String> taskList = new ArrayList<>();
    private boolean isInspection = true;
    private boolean isComplain = true;
    private String WorkLogDate = "";
    private String worklogDay = "";
    private String workLogTime = "";
    private boolean settingFont = false;
    private float fontSize = 13f;
    private String uid = "";
    private String fileId = "";
    private List<FilesModel> editFileModel = new ArrayList<>();
    private int projectId = 0;
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
        setContentView(R.layout.activity_save_correction);
        listEdit = new ArrayList<>();

        Toolbar toolbar = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            toolbar = findViewById(R.id.toolbar);
        }
        if (toolbar != null) {
            txtEdit = toolbar.findViewById(R.id.txtEdit);
            imgBack = toolbar.findViewById(R.id.imgBack);
            stagingRL = toolbar.findViewById(R.id.stagingRL);
            txtEdit.setText(getString(R.string.save_correction));
            saveIV = toolbar.findViewById(R.id.saveIV);
            saveIV.setImageDrawable(getDrawable(R.drawable.ic_folder));
            saveIV.setVisibility(View.VISIBLE);
            saveIV.setOnClickListener(this);
        }

        // define ids

        btnCancel = findViewById(R.id.btnCancel);
        visitTV = findViewById(R.id.visitTV);
        personNameTv = findViewById(R.id.personNameTv);
        companyNameTV = findViewById(R.id.companyNameTV);
        txtLocation = findViewById(R.id.txtLocation);
        reasonTV = findViewById(R.id.reasonTV);
        visitTitle = findViewById(R.id.visitTitle);
        recyclerView = findViewById(R.id.recyclerView);
        voiceTV = findViewById(R.id.voiceTV);
        voiceRL = findViewById(R.id.voiceRL);
        countRL = findViewById(R.id.countRL);
        addPicTV = findViewById(R.id.addPicTV);
        btnSave = findViewById(R.id.btnSave);
        visitET = findViewById(R.id.visitET);
        refreshIV = findViewById(R.id.refreshIV);
        etNote = findViewById(R.id.etNote);
        dateTV = findViewById(R.id.dateTV);
        complaintSP = findViewById(R.id.complaintSP);
        locationSP = findViewById(R.id.locationSp);
        txtCity = findViewById(R.id.txtCity);
        txtDay = findViewById(R.id.txtDay);
        codeET = findViewById(R.id.codeET);
        personNameET = findViewById(R.id.personNameET);
        spinnerCode = findViewById(R.id.spinnerCode);
        spinnerInspection = findViewById(R.id.spinnerInspection);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        projectSelected = sharedPreferences.getString(Constants.PROJECT_SELECTED, "");
        projectID = sharedPreferences.getInt(Constants.PROJECT_ID, 0);
        authorizationToken = sharedPreferences.getString(Constants.TOKEN, "");
        projectId = sharedPreferences.getInt(Constants.PROJECT_ID, 0);
        btnSave.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        refreshIV.setOnClickListener(this);

        txtCity.setText(projectSelected);
        String currentDate = CommonMethods.getCurrentDateByFormat("dd MMM, yyyy");
        String currentDay = CommonMethods.getCurrentDay();
        workLogTime = CommonMethods.getCurrentDateByFormat("HH:mm");
        dateTV.setText(currentDate);
        txtDay.setText(currentDay);
        WorkLogDate = CommonMethods.getCurrentDateByFormat("dd MMM, yyyy");
        if (getIntent().hasExtra("WorkLogDate")) {
            WorkLogDate = getIntent().getStringExtra("WorkLogDate");
            dateTV.setText(WorkLogDate);
            try {
                worklogDay = CommonMethods.getDayFromDate(WorkLogDate);
                txtDay.setText(worklogDay);
            } catch (Exception e) {
                Logger.e(Constants.ERROR_DETAIL, e.getMessage());
            }
        }
        // Add Data in ArrayList
        listTaskCompleted = new ArrayList<>();
        listTaskCompleted.add("Yes");
        listTaskCompleted.add("No");
        taskList = CommonMethods.getArrayList();

        getInspection(taskList);
        getComplaint(taskList);


        //set Location
        locationList = ProjectLocationRepo.GetLocalByProject(projectId);
        getLocation(locationList);

        locationSP.setSelection(0);
        setCodeByProject();
        recyclerView.setLayoutManager(new LinearLayoutManager(SaveCorrections.this, RecyclerView.HORIZONTAL, false));
        recyclerView.setNestedScrollingEnabled(false);
        if (!editView) {
            listEdit.add("add");
            addAdapter = new AddImageCommonAdapter(SaveCorrections.this, listEdit);
            recyclerView.setAdapter(addAdapter);
        }

        if (getIntent().hasExtra(Constants.TABLE_ID)) {
            setEditData();
        }

        spinnerInspection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                strInspection = spinnerInspection.getSelectedItem().toString();
                isInspection = Boolean.parseBoolean(strInspection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        complaintSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                complaintValue = complaintSP.getSelectedItem().toString();
                isComplain = Boolean.parseBoolean(complaintValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        spinnerCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (codeList.size() > 0) {
                    codeName = codeList.get(i).code;
                    codeId = String.valueOf(codeList.get(i).correctionId);
                }


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

        if (!CommonMethods.Mode.isLive(SaveCorrections.this)) {
            stagingRL.setVisibility(View.VISIBLE);
        }
        setVoiceToText();
        checkDarkMode();
    }

    private void setEditData() {
        editView = true;
        tableId = getIntent().getIntExtra(Constants.TABLE_ID, 0);
        correctionData = CorrectionsRepo.Get(tableId);
        personNameET.setText(correctionData.PersonDoingQualityControl);
        visitET.setText(correctionData.VisitReason);
        etNote.setText(correctionData.Description);
        codeName = String.valueOf(correctionData.CodeId);
        locationId = String.valueOf(correctionData.LocationId);
        /*if (!correctionData.imagepath.isEmpty()) {
            setImageData(correctionData.imagepath);
        }
*/
        selectedImagePath = correctionData.imagepath;
        if (correctionData.imagepath.isEmpty()) {
            listEdit.add("add");
        } else {
            fileId = correctionData.itemGuId;
            editFileModel = FilesRepo.Get(fileId);
            listEdit.clear();
            for (int i = 0; i < editFileModel.size(); i++) {
                listEdit.add(editFileModel.get(i).getFullPath());
            }
            pathList.clear();
            pathList.addAll(listEdit);
            listEdit.add("add");
            strImagePath = correctionData.imagepath;
        }
        addAdapter = new AddImageCommonAdapter(SaveCorrections.this, listEdit);
        recyclerView.setAdapter(addAdapter);

        for (int i = 0; i < codeList.size(); i++) {
            if (codeList.get(i).code.equalsIgnoreCase(String.valueOf(correctionData.CodeId))) {
                codeId = codeList.get(i).code;
                spinnerCode.setSelection(i);
            }
        }
        for (int i = 0; i < locationList.size(); i++) {
            if (locationList.get(i).locationId == correctionData.LocationId) {
                locationId = String.valueOf(locationList.get(i).locationId);
                locationSP.setSelection(i);
            }
        }

    }

    private void setCodeByProject() {

        try {
            codeList = CorrectionsCodeRepo.GetCodeById(projectId);
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }

        getCode(codeList);

    }

    private void getLocation(List<ProjectLocation> locationList) {
        try {

            locationAdapter = new LocationArrayAdapter(SaveCorrections.this, android.R.layout.simple_spinner_item, locationList) {
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
            locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            locationSP.setAdapter(locationAdapter);


        } catch (Exception e) {
            Logger.e(Constants.ERROR_DETAIL, e.getMessage());
        }

    }


    private void getInspection(List<String> listInspection) {
        try {
            //    spinnerComplaint.setOnItemSelectedListener(AddActivitity.this);
            arrayComplaint = new ArrayAdapter(SaveCorrections.this, android.R.layout.simple_spinner_item, listInspection) {
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
            spinnerInspection.setAdapter(arrayComplaint);

            int makePosition = arrayComplaint.getPosition(correction.VisualInspectionDone);
            spinnerInspection.setSelection(makePosition);


        } catch (Exception e) {

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
        txtEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtDay.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtCity.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        dateTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

        personNameET.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        etNote.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        voiceTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        visitTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        reasonTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtLocation.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        companyNameTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        personNameTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        visitTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        btnCancel.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        voiceTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        addPicTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        btnSave.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

        etNote.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        visitET.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        personNameET.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);


        btnSave.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

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

    private void getComplaint(List<String> listInspection) {
        try {
            //    spinnerComplaint.setOnItemSelectedListener(AddActivitity.this);
            arrayComplaint = new ArrayAdapter(SaveCorrections.this, android.R.layout.simple_spinner_item, listInspection) {
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
            complaintSP.setAdapter(arrayComplaint);

            int makePosition = arrayComplaint.getPosition(correction.VisualInspectionDone);
            complaintSP.setSelection(makePosition);


        } catch (Exception e) {

        }

    }


    public void getCorrectionCode() {
        try {
            dialogCorrection = new ProgressDialog(SaveCorrections.this);
            try {
                dialogCorrection.show();
            } catch (WindowManager.BadTokenException e) {

            }
            dialogCorrection.setCancelable(false);
            dialogCorrection.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialogCorrection.setContentView(R.layout.custom_loading_layout);
            CorrectionCodes web_api_2 = ApiClientClass.getApiClientDev(SaveCorrections.this).create(CorrectionCodes.class);
            Call<CorrectionCodeResponse> call = web_api_2.getCorrectionCode(projectId);

            if (call != null) {
                call.enqueue(new Callback<CorrectionCodeResponse>() {
                    @Override
                    public void onResponse(Call<CorrectionCodeResponse> call, Response<CorrectionCodeResponse> response) {
                        dialogCorrection.hide();

                        if (response.body() != null && response.body().getData() != null && response.body().getData().getItems() != null) {

                            CorrectionCode projectType;
                            try {
                                CorrectionsCodeRepo.DeleteAll();
                            } catch (Exception e) {
                                Logger.e(e.getMessage());
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
                            setCodeByProject();
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


    private void getCode(List<CorrectionCode> listInspection) {
        try {

            arrayCode = new CorrectionCodeAdapter(SaveCorrections.this, android.R.layout.simple_spinner_item, listInspection) {
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
            arrayCode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCode.setAdapter(arrayCode);
            spinnerCode.setSelection(0);

        } catch (Exception e) {

            Logger.e(e.getMessage());
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.saveIV:
                Intent intent = new Intent(SaveCorrections.this, DailyFolderActivity.class);
                intent.putExtra("selectFolder", "true");
                startActivityForResult(intent, Constants.SECOND_ACTIVITY_REQUEST_CODE);
                break;
            case R.id.btnSave:
                uid = CommonMethods.getUuid();
                correction = new Correction();
                if (editView) {
                    correction.setId(correctionData.getId());
                }
                correction.WorkLogId = 11;
                correction.CodeId = Integer.parseInt(codeId);
                correction.IsCompliantAfterCorrection = String.valueOf(isComplain);
                correction.VisualInspectionDone = String.valueOf(isInspection);

                correction.LocationId = Integer.parseInt(locationId);
                correction.PersonDoingQualityControl = (personNameET.getText().toString().trim());
                correction.Description = (etNote.getText().toString().trim());
                if (personNameET.getText().toString().trim().isEmpty()) {
                    correction.PersonDoingQualityControl = Constants.DEFAULT_VAL;
                }
                if (etNote.getText().toString().trim().isEmpty()) {
                    correction.Description = Constants.DEFAULT_VAL;
                }
                String currentDate = CommonMethods.getCurrentTimeInUtc();
                String timeValue = WorkLogDate + " " + workLogTime;
                String time = CommonMethods.convertLocalToUtc(timeValue);

                correction.AddedOn = time;
                correction.LastModOn = currentDate;
                correction.imagepath = selectedImagePath;
                correction.projectId = String.valueOf(projectID);
                correction.WORK_LOG_DATE = WorkLogDate;
                correction.VisitReason = visitET.getText().toString().trim();
                if (visitET.getText().toString().trim().isEmpty()) {
                    correction.VisitReason = Constants.DEFAULT_VAL;
                }
                if (fileId.isEmpty()) {
                    correction.itemGuId = uid;
                } else {
                    uid = fileId;
                    correction.itemGuId = uid;
                    try {
                        FilesRepo.DeleteFileByUid(uid);
                    } catch (Exception e) {
                        Logger.e(Constants.ERROR_DETAIL, e.getMessage());
                    }
                }

                String res = CorrectionsRepo.Save(correction);

                if (pathList.size() > 0) {
                    saveImages(time);
                }


                if (res.isEmpty()) {
                    finish();
                } else {
                    CommonMethods.showToast(SaveCorrections.this, res);
                }


                break;

            case R.id.refreshIV:

                getCorrectionCode();

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
                filesModel.fileId = randomNumber;
                filesModel.FullPath = pathList.get(i);
                String repoResponse = FilesRepo.Save(filesModel);

            }

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
            Logger.e(Constants.ERROR_DETAIL, e.getMessage());
        }
        return rotatedBitmap;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

                Uri uri = Uri.fromFile(output);
                selectedImagePath = CommonMethods.getPath(SaveCorrections.this, uri);

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(SaveCorrections.this.getContentResolver(), uri);

                bitmap = checkRotate(bitmap);
                uri = CommonMethods.bitmapToUriConverter(bitmap, SaveCorrections.this);
                selectedImagePath = CommonMethods.getPath(SaveCorrections.this, uri);
                setImageData(selectedImagePath);
            }


            if (resultCode == RESULT_OK && requestCode == GALLERY_REQUEST_CODE) {


                if (data.getData() != null) {
                    Uri selectedImageUri = data.getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(SaveCorrections.this.getContentResolver(), selectedImageUri);
                    selectedImageUri = CommonMethods.bitmapToUriConverter(bitmap, SaveCorrections.this);
                    selectedImagePath = CommonMethods.getPath(SaveCorrections.this, selectedImageUri);
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
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(SaveCorrections.this.getContentResolver(), selectedImageUri);
                                selectedImageUri = CommonMethods.bitmapToUriConverter(bitmap, SaveCorrections.this);
                                selectedImagePath = CommonMethods.getPath(SaveCorrections.this, selectedImageUri);
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


                Uri selectedImageUri = CommonMethods.bitmapToUriConverter(selectedBitmap, SaveCorrections.this);
                selectedImagePath = CommonMethods.getPath(SaveCorrections.this, selectedImageUri);
                setImageData(selectedImagePath);

                Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(selectedImagePath), 220, 220);

            }
            if (requestCode == Constants.SECOND_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

                long noteID = data.getLongExtra("folderID", 0l);
                setFolderData(noteID);

            }

        } catch (Exception e) {
            Logger.e(Constants.ERROR_DETAIL, e.getMessage());
            //GlobalMethods.SendErrorReport(e,context);
        }
    }

    private void setFolderData(long noteID) {
        DailyFolder folderData = DailyFolderRepo.Get((int) noteID);
        etNote.setText(folderData.note);
        setImageData(folderData.image);
    }


    public void showDiloag() {
        try {
            Dialog dialog = new Dialog(SaveCorrections.this);
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SaveCorrections.this);
            builder.setTitle(SaveCorrections.this.getResources().getString(R.string.select_image));
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
                startActivityForResult(Intent.createChooser(intent, SaveCorrections.this.getResources().getString(R.string.select_image)), GALLERY_REQUEST_CODE);
            } else {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUEST_CODE);
            }
        } catch (Exception e) {
            Logger.e(Constants.ERROR_DETAIL, e.getMessage());
        }
    }

    public void openCamera() {
        try {
            folder = SaveCorrections.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            //folder = new File(Environment.getExternalStorageDirectory(),"Nagad");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            imageStatus = true;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                try {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    output = new File(folder, System.currentTimeMillis() + ".jpg");

                    Uri photoURI = FileProvider.getUriForFile(SaveCorrections.this, SaveCorrections.this.getPackageName() + ".provider", output);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                } catch (Exception e) {
                    Logger.e(Constants.ERROR_DETAIL, e.getMessage());
                }
            } else {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                output = new File(folder, System.currentTimeMillis() + ".jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }

        } catch (Exception e) {
            //GlobalMethods.SendErrorReport(e,context);
        }
    }

    private boolean isPermissionAllowed() {
        try {
            int camera = ContextCompat.checkSelfPermission(SaveCorrections.this, Manifest.permission.CAMERA);
            int writestorage = ContextCompat.checkSelfPermission(SaveCorrections.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            List<String> listPermissionNeeded = new ArrayList<>();

            if (camera != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(Manifest.permission.CAMERA);
            }

            if (writestorage != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (!listPermissionNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(SaveCorrections.this, listPermissionNeeded.toArray
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
            if (ActivityCompat.shouldShowRequestPermissionRationale(SaveCorrections.this, Manifest.permission.CAMERA)) {

            }
            if (ActivityCompat.shouldShowRequestPermissionRationale(SaveCorrections.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            }

            ActivityCompat.requestPermissions(SaveCorrections.this, new String[]{Manifest.permission.CAMERA,
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
        selectedImagePath = path;
        pathList.clear();
        pathList.addAll(listEdit);
        listEdit.add("add");
        addAdapter = new AddImageCommonAdapter(SaveCorrections.this, listEdit);
        recyclerView.setAdapter(addAdapter);
    }


    @Override
    public void checkItemClickListener() {

        if (!isPermissionAllowed()) {
            requestPermission();
            return;
        }
        showDiloag();
    }

    @Override
    public void deleteProject(int id) {
        try {
            FilesRepo.DeleteFileByProject(editFileModel.get(id).fileId);
            pathList.remove(id);
        } catch (Exception e) {
            Logger.e(Constants.ERROR_DETAIL, e.getMessage());
        }
    }


    public void getLocationMethod() {
        try {
            dialogCorrection = new ProgressDialog(SaveCorrections.this);
            try {
                dialogCorrection.show();
            } catch (WindowManager.BadTokenException e) {

            }

            dialogCorrection.setCancelable(false);
            dialogCorrection.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialogCorrection.setContentView(R.layout.custom_loading_layout);
            ProjectLocations web_api_2 = ApiClientClass.getApiClientDev(SaveCorrections.this).create(ProjectLocations.class);
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

    private void checkDarkMode() {
        if (CommonMethods.checkDarkMode(SaveCorrections.this)) {
            txtEdit.setTextColor(ContextCompat.getColor(SaveCorrections.this, R.color.white));
            addPicTV.setTextColor(ContextCompat.getColor(SaveCorrections.this, R.color.white));
        }
    }
}
