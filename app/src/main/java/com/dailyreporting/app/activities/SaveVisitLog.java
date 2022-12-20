package com.dailyreporting.app.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dailyreporting.app.R;
import com.dailyreporting.app.adapters.AddImageCommonAdapter;
import com.dailyreporting.app.adapters.LocationArrayAdapter;
import com.dailyreporting.app.database.DailyFolderRepo;
import com.dailyreporting.app.database.ProjectLocationRepo;
import com.dailyreporting.app.database.VisitLogRepo;
import com.dailyreporting.app.models.ActivityModel;
import com.dailyreporting.app.models.DailyFolder;
import com.dailyreporting.app.models.ProjectLocation;
import com.dailyreporting.app.models.VisitLog;
import com.dailyreporting.app.utils.CommonMethods;
import com.dailyreporting.app.utils.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static com.dailyreporting.app.utils.Constants.MyPREFERENCES;
import static com.yalantis.ucrop.util.BitmapLoadUtils.calculateInSampleSize;
import static com.yalantis.ucrop.util.FileUtils.getDataColumn;
import static com.yalantis.ucrop.util.FileUtils.isDownloadsDocument;
import static com.yalantis.ucrop.util.FileUtils.isExternalStorageDocument;
import static com.yalantis.ucrop.util.FileUtils.isGooglePhotosUri;
import static com.yalantis.ucrop.util.FileUtils.isMediaDocument;

public class SaveVisitLog extends AppCompatActivity implements View.OnClickListener, AddImageCommonAdapter.InterfaceList {
    private final List<String> listTaskCompleted = new ArrayList<>();
    private final String strComplaint = "";
    private final String strTaskCompleted = "";
    private final int CAMERA_REQUEST_CODE = 10;
    private final int GALLERY_REQUEST_CODE = 20;
    private final int REQUEST_CODE = 40;
    private final int RESULT_CROP = 30;
    private final int count = 1;
    private final int PERMISSION_REQUEST_CODE = 99;
    private final List<ActivityModel> listAddActivity = new ArrayList<>();
    String TAG = getClass().getSimpleName();
    Spinner locationSP;
    List<ProjectLocation> locationList = new ArrayList<>();
    Button btnCancel;
    TextView reasonTV, companyNameTV, txtLocation, personNameTv, visitTitle, arrivalTV, departTV;
    private RecyclerView recyclerView;
    private AddImageCommonAdapter addAdapter;
    private ImageView imgBack;
    private ArrayAdapter arrayTaskCompleted;
    private LocationArrayAdapter arrayComplaint;
    ////////////////////////
    private String selectedImagePath;
    private boolean imageStatus = false;
    private File output;
    private File folder;
    private EditText etActivityName, visitReason, etNote, companyNameET, personNameET;
    private Button btnSave;
    private String strActivityName = "", strCode = "", strNote = "", strLocation = "", strImagePath = "";
    private List<String> listEdit = new ArrayList<>();
    private VisitLog visitLog;
    private TextView txtEdit, dateTV;
    private TextView txtDay;
    private boolean editView = false;
    private String locationName = "";
    private String locationId = "0";
    private SpeechRecognizer speechRecognizer;
    private final List<VisitLog> visitList = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int tableId = 0;
    private VisitLog visitModel;
    private String projectSelected = "";
    private TextView txtCity;
    private TextView arrivalET, departET;
    private String currentDate = "";
    private String arrivalTime = "";
    private String departDate = "";
    private RelativeLayout stagingRL;
    private ImageView saveIV;
    private String token;
    private RelativeLayout voiceRL;
    private TextView voiceTV;
    private int projectId = 0;
    private String WorkLogDate = "";
    private String worklogDay = "";
    private String workLogTime = "";
    private boolean settingFont = false;
    private float fontSize = 13f;

    ///////////////////////// code for Camera gallery /////////////////////////
    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);

        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);


    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_visit);
        listEdit = new ArrayList<>();
        Toolbar toolbar = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            toolbar = findViewById(R.id.toolbar);
        }
        if (toolbar != null) {
            txtEdit = toolbar.findViewById(R.id.txtEdit);
            imgBack = toolbar.findViewById(R.id.imgBack);
            stagingRL = toolbar.findViewById(R.id.stagingRL);
            txtEdit.setText(getString(R.string.save_visit));
            saveIV = toolbar.findViewById(R.id.saveIV);
            saveIV.setImageDrawable(getDrawable(R.drawable.ic_folder));
            saveIV.setVisibility(View.VISIBLE);
            saveIV.setOnClickListener(this);
        }

        // define ids
        recyclerView = findViewById(R.id.recyclerView);
        btnSave = findViewById(R.id.btnSave);
        etNote = findViewById(R.id.etNote);
        dateTV = findViewById(R.id.dateTV);
        voiceTV = findViewById(R.id.voiceTV);
        voiceRL = findViewById(R.id.voiceRL);
        visitReason = findViewById(R.id.visitReason);
        txtCity = findViewById(R.id.txtCity);
        txtDay = findViewById(R.id.txtDay);
        reasonTV = findViewById(R.id.reasonTV);
        txtLocation = findViewById(R.id.txtLocation);
        companyNameTV = findViewById(R.id.companyNameTV);
        personNameTv = findViewById(R.id.personNameTv);
        visitTitle = findViewById(R.id.visitTitle);
        arrivalTV = findViewById(R.id.arrivalTV);
        departTV = findViewById(R.id.departTV);
        btnCancel = findViewById(R.id.btnCancel);


        etActivityName = findViewById(R.id.etActivityName);
        companyNameET = findViewById(R.id.companyNameET);
        personNameET = findViewById(R.id.personNameET);

        arrivalET = findViewById(R.id.arrivalET);
        departET = findViewById(R.id.departET);
        locationSP = findViewById(R.id.spinnerInspection);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        projectSelected = sharedPreferences.getString(Constants.PROJECT_SELECTED, "");
        projectId = sharedPreferences.getInt(Constants.PROJECT_ID, 0);
        btnSave.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        departET.setOnClickListener(this);
        arrivalET.setOnClickListener(this);
        txtCity.setText(projectSelected);
        currentDate = CommonMethods.getCurrentDateByFormat("dd MMM, yyyy");
        String currentDay = CommonMethods.getCurrentDay();
        dateTV.setText(currentDate);
        txtDay.setText(currentDay);
        WorkLogDate = CommonMethods.getCurrentDateByFormat("dd MMM, yyyy");
        workLogTime = CommonMethods.getCurrentDateByFormat("HH:mm");
        if (getIntent().hasExtra("WorkLogDate")) {
            WorkLogDate = getIntent().getStringExtra("WorkLogDate");
            dateTV.setText(currentDate);
            try {
                worklogDay = CommonMethods.getDayFromDate(WorkLogDate);
                txtDay.setText(worklogDay);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setDateTime();
        // initialize recyclerview
        recyclerView.setLayoutManager(new LinearLayoutManager(SaveVisitLog.this, RecyclerView.HORIZONTAL, false));
        recyclerView.setNestedScrollingEnabled(false);
        if (!editView) {
            listEdit.add("a");
            addAdapter = new AddImageCommonAdapter(SaveVisitLog.this, listEdit);
            recyclerView.setAdapter(addAdapter);
        }

        locationList = ProjectLocationRepo.GetLocalByProject(projectId);
        getInspection(locationList);


        locationSP.setSelection(0);

        if (getIntent().hasExtra(Constants.TABLE_ID)) {
            editView = true;
            tableId = getIntent().getIntExtra(Constants.TABLE_ID, 0);
            visitModel = VisitLogRepo.Get(tableId);
            visitReason.setText(visitModel.VisitReason);
            companyNameET.setText(visitModel.CompanyName);
            personNameET.setText(visitModel.PersonName);
            etNote.setText(visitModel.Note);
            etActivityName.setText(visitModel.Title);
            locationId = String.valueOf(visitModel.LocationId);
            String arrivalEditTime = CommonMethods.getTimeFromUTC(visitModel.ArrivalTime);
            arrivalET.setText(arrivalEditTime);
            String departEditTime = CommonMethods.getTimeFromUTC(visitModel.DepartureTime);
            departET.setText(departEditTime);
            for (int i = 0; i < locationList.size(); i++) {
                if (locationList.get(i).locationId == visitModel.LocationId) {
                    locationId = String.valueOf(locationList.get(i).locationId);
                    locationSP.setSelection(i);
                }
            }
        }

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

        if (!CommonMethods.Mode.isLive(SaveVisitLog.this)) {
            stagingRL.setVisibility(View.VISIBLE);
        }
        setVoiceToText();
        checkDarkMode();
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
        reasonTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtLocation.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        companyNameTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        personNameTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        visitTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        arrivalTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        departTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        btnCancel.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

        visitReason.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        companyNameET.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        personNameET.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        etActivityName.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        arrivalET.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        departET.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        etNote.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        voiceTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

        btnSave.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

    }

    private void getInspection(List<ProjectLocation> listInspection) {
        try {

            arrayComplaint = new LocationArrayAdapter(SaveVisitLog.this, android.R.layout.simple_spinner_item, listInspection) {
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
            arrayComplaint.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            locationSP.setAdapter(arrayComplaint);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.arrivalET:
                setTimeDialog();
                break;
            case R.id.departET:
                setDepartTimeDialog();
                break;
            case R.id.imgBack:
                onBackPressed();
                break;


            case R.id.btnSave:

                visitLog = new VisitLog();
                if (editView) {
                    visitLog.setId(visitModel.getId());
                }
                visitLog.Title = etActivityName.getText().toString();
                visitLog.VisitReason = (visitReason.getText().toString().trim());
                visitLog.LocationId = Integer.parseInt(locationId);
                visitLog.CompanyName = companyNameET.getText().toString().trim();
                visitLog.PersonName = (personNameET.getText().toString().trim());
                visitLog.Note = (etNote.getText().toString().trim());
                if (etNote.getText().toString().trim().isEmpty()) {
                    visitLog.Note = Constants.DEFAULT_VAL;
                }
                if (etActivityName.getText().toString().trim().isEmpty()) {
                    visitLog.Title = Constants.DEFAULT_VAL;
                }
                if (personNameET.getText().toString().trim().isEmpty()) {
                    visitLog.PersonName = Constants.DEFAULT_VAL;
                }
                if (companyNameET.getText().toString().trim().isEmpty()) {
                    visitLog.CompanyName = Constants.DEFAULT_VAL;
                }
                visitLog.WorkLogId = 11;
                String val = CommonMethods.getCurrentTimeInUtc();
                String timeValue = WorkLogDate + " " + workLogTime;
                String time = CommonMethods.convertLocalToUtc(timeValue);

                visitLog.AddedOn = time;
                visitLog.ArrivalTime = arrivalTime;
                visitLog.DepartureTime = departDate;
                visitLog.projectId = String.valueOf(projectId);
                visitLog.WORK_LOG_DATE = WorkLogDate;
                String res = VisitLogRepo.Save(visitLog);
                //visitLog.Id>0
                if (res.isEmpty()) {
                    finish();
                } else {
                    CommonMethods.showToast(SaveVisitLog.this, res);
                }

                break;

            case R.id.saveIV:
                Intent intent = new Intent(SaveVisitLog.this, DailyFolderActivity.class);
                intent.putExtra("selectFolder", "true");
                startActivityForResult(intent, Constants.SECOND_ACTIVITY_REQUEST_CODE);
                break;
        }
    }

    private void setDateTime() {
        arrivalTime = CommonMethods.convertLocalToUtc(CommonMethods.getCurrentTimeArrival());
        departDate = CommonMethods.convertLocalToUtc(CommonMethods.getCurrentTimeArrival());

    }

    public void setTimeDialog() {

        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(SaveVisitLog.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
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
        mTimePicker = new TimePickerDialog(SaveVisitLog.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
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
            e.printStackTrace();
        }
        return rotatedBitmap;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

                Uri uri = Uri.fromFile(output);
                selectedImagePath = getPath(SaveVisitLog.this, uri);

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(SaveVisitLog.this.getContentResolver(), uri);

                bitmap = checkRotate(bitmap);
                uri = bitmapToUriConverter(bitmap);
                selectedImagePath = getPath(SaveVisitLog.this, uri);
                setImageData(selectedImagePath);
            }


            if (resultCode == RESULT_OK && requestCode == GALLERY_REQUEST_CODE) {


                if (data.getData() != null) {
                    Uri selectedImageUri = data.getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(SaveVisitLog.this.getContentResolver(), selectedImageUri);
                    selectedImageUri = bitmapToUriConverter(bitmap);
                    selectedImagePath = getPath(SaveVisitLog.this, selectedImageUri);
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
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(SaveVisitLog.this.getContentResolver(), selectedImageUri);
                                selectedImageUri = bitmapToUriConverter(bitmap);
                                selectedImagePath = getPath(SaveVisitLog.this, selectedImageUri);
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


                Uri selectedImageUri = bitmapToUriConverter(selectedBitmap);
                selectedImagePath = getPath(SaveVisitLog.this, selectedImageUri);
                setImageData(selectedImagePath);

                Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(selectedImagePath), 220, 220);

            }
            if (requestCode == Constants.SECOND_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

                long noteID = data.getLongExtra("folderID", 0l);
                setFolderData(noteID);
            }


        } catch (Exception e) {
            e.printStackTrace();
            //GlobalMethods.SendErrorReport(e,context);
        }
    }

    private void setFolderData(long noteID) {
        DailyFolder folderData = DailyFolderRepo.Get((int) noteID);
        etNote.setText(folderData.note);
        setImageData(folderData.image);
    }

    public Uri bitmapToUriConverter(Bitmap mBitmap) {
        Uri uri = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 350, 350);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap newBitmap = Bitmap.createScaledBitmap(mBitmap, 350, 350,
                    true);
            File file = new File(SaveVisitLog.this.getFilesDir(), "Image"
                    + new Random().nextInt() + ".jpg");
            FileOutputStream out = SaveVisitLog.this.openFileOutput(file.getName(),
                    Context.MODE_PRIVATE);
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            //get absolute path
            String realPath = file.getAbsolutePath();
            File f = new File(realPath);
            uri = Uri.fromFile(f);

        } catch (Exception e) {
            Log.e("Message", e.getMessage());
            //GlobalMethods.SendErrorReport(e,context);
        }
        return uri;
    }


    public void showDiloag() {
        try {
            Dialog dialog = new Dialog(SaveVisitLog.this);
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SaveVisitLog.this);
            builder.setTitle(SaveVisitLog.this.getResources().getString(R.string.select_image));
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
                startActivityForResult(Intent.createChooser(intent, SaveVisitLog.this.getResources().getString(R.string.select_image)), GALLERY_REQUEST_CODE);
            } else {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUEST_CODE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //GlobalMethods.SendErrorReport(e,context);
        }
    }

    public void openCamera() {
        try {
            folder = SaveVisitLog.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            //folder = new File(Environment.getExternalStorageDirectory(),"Nagad");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            imageStatus = true;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                try {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    output = new File(folder, System.currentTimeMillis() + ".jpg");

                    Uri photoURI = FileProvider.getUriForFile(SaveVisitLog.this, SaveVisitLog.this.getPackageName() + ".provider", output);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                } catch (Exception e) {
                    e.printStackTrace();
                    //globalMethods.SendErrorReport(e,getApplicationContext());
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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String getPath(final Context context, final Uri uri) {
        try {
            final boolean isKitKat = Build.VERSION.SDK_INT >= 19;
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{
                            split[1]
                    };
                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                // Return the remote address
                if (isGooglePhotosUri(uri))
                    return uri.getLastPathSegment();
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        } catch (Exception e) {
            //GlobalMethods.SendErrorReport(e,context);
        }
        return null;
    }

    private boolean isPermissionAllowed() {
        try {
            int camera = ContextCompat.checkSelfPermission(SaveVisitLog.this, Manifest.permission.CAMERA);
            int writestorage = ContextCompat.checkSelfPermission(SaveVisitLog.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            List<String> listPermissionNeeded = new ArrayList<>();

            if (camera != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(Manifest.permission.CAMERA);
            }

            if (writestorage != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (!listPermissionNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(SaveVisitLog.this, listPermissionNeeded.toArray
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
            if (ActivityCompat.shouldShowRequestPermissionRationale(SaveVisitLog.this, Manifest.permission.CAMERA)) {

            }
            if (ActivityCompat.shouldShowRequestPermissionRationale(SaveVisitLog.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            }

            ActivityCompat.requestPermissions(SaveVisitLog.this, new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            //GlobalMethods.SendErrorReport(e,context);
        }
    }

    public void setImageData(String path) {
        listEdit = new ArrayList<>();

        listEdit.add(path);
        strImagePath = path;

        addAdapter = new AddImageCommonAdapter(SaveVisitLog.this, listEdit);
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

    }

    private void checkDarkMode() {
        if (CommonMethods.checkDarkMode(SaveVisitLog.this)) {
            txtEdit.setTextColor(ContextCompat.getColor(SaveVisitLog.this, R.color.white));

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
            ActivityModel addActivityModel = listAddActivity.get(0);
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
