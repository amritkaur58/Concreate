package com.dailyreporting.app.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
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
import com.dailyreporting.app.adapters.AddImageCommonAdapter;
import com.dailyreporting.app.adapters.LocationArrayAdapter;
import com.dailyreporting.app.database.DailyFolderRepo;
import com.dailyreporting.app.database.FilesRepo;
import com.dailyreporting.app.models.ActivityModel;
import com.dailyreporting.app.models.DailyFolder;
import com.dailyreporting.app.models.FilesModel;
import com.dailyreporting.app.models.ProjectLocation;
import com.dailyreporting.app.models.VisitLog;
import com.dailyreporting.app.utils.CommonMethods;
import com.dailyreporting.app.utils.Constants;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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

public class SaveDailyFolderActivity extends AppCompatActivity implements View.OnClickListener, AddImageCommonAdapter.InterfaceList {
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
    private final String strActivityName = "";
    private final String strCode = "";
    private final String strNote = "";
    private final String strLocation = "";
    private final String locationName = "";
    private final String locationId = "0";
    private final List<VisitLog> visitList = new ArrayList<>();
    private final String currentDate = "";
    private final String arrivalTime = "";
    private final String departDate = "";
    private final List<String> pathList = new ArrayList<>();
    String TAG = getClass().getSimpleName();
    Spinner locationSP;
    List<ProjectLocation> locationList = new ArrayList<>();
    RelativeLayout voiceRL;
    TextView voiceTV;
    private RecyclerView recyclerView;
    private AddImageCommonAdapter addAdapter;
    private ImageView imgBack;
    private ArrayAdapter arrayTaskCompleted;
    private LocationArrayAdapter arrayComplaint;
    ////////////////////////
    private String selectedImagePath = "";
    private boolean imageStatus = false;
    private File output;
    private File folder;
    private EditText etActivityName, visitReason, etNote, companyNameET, personNameET;
    private Button btnSave;
    private String strImagePath = "";
    private List<String> listEdit = new ArrayList<>();
    private DailyFolder dailyFolder;
    private TextView txtEdit, dateTV;
    private TextView txtDay;
    private boolean editView = false;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String authorizationToken = "";
    private int tableId = 0;
    private String projectSelected = "";
    private TextView txtCity;
    private TextView arrivalET, departET;
    private RelativeLayout stagingRL;
    private SpeechRecognizer speechRecognizer;
    private int projectId = 0;
    private String workLogDate = "";
    private String worklogDay = "";
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
        setContentView(R.layout.layout_add_folder);
        listEdit = new ArrayList<>();
        Toolbar toolbar = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            toolbar = findViewById(R.id.toolbar);
        }
        if (toolbar != null) {
            txtEdit = toolbar.findViewById(R.id.txtEdit);
            imgBack = toolbar.findViewById(R.id.imgBack);
            stagingRL = toolbar.findViewById(R.id.stagingRL);
            txtEdit.setText(getString(R.string.add_to_folder));
        }

        // define ids
        recyclerView = findViewById(R.id.recyclerView);
        dateTV = findViewById(R.id.dateTV);
        voiceRL = findViewById(R.id.voiceRL);
        txtDay = findViewById(R.id.txtDay);
        btnSave = findViewById(R.id.btnSave);
        etNote = findViewById(R.id.etNote);
        txtCity = findViewById(R.id.txtCity);
        voiceTV = findViewById(R.id.voiceTV);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        projectId = sharedPreferences.getInt(Constants.PROJECT_ID, 0);
        authorizationToken = sharedPreferences.getString(Constants.TOKEN, "");
        workLogDate = CommonMethods.getCurrentDateByFormat("dd MMM, yyyy");
        projectSelected = sharedPreferences.getString(Constants.PROJECT_SELECTED, "");
        if (getIntent().hasExtra(Constants.TABLE_ID)) {
            editView = true;
            tableId = getIntent().getIntExtra(Constants.TABLE_ID, 0);
            setEditData(tableId);
        }
        String currentDate = CommonMethods.getCurrentDateByFormat("dd MMM,yyyy");
        String currentDay = CommonMethods.getCurrentDay();
        dateTV.setText(currentDate);
        txtDay.setText(currentDay);
        recyclerView.setLayoutManager(new LinearLayoutManager(SaveDailyFolderActivity.this, RecyclerView.HORIZONTAL, false));
        recyclerView.setNestedScrollingEnabled(false);
        if (!editView) {
            listEdit.add("add");
            addAdapter = new AddImageCommonAdapter(SaveDailyFolderActivity.this, listEdit);
            recyclerView.setAdapter(addAdapter);
        }

        btnSave.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        if (getIntent().hasExtra("WorkLogDate")) {
            workLogDate = getIntent().getStringExtra("WorkLogDate");
            dateTV.setText(workLogDate);
            try {
                worklogDay = CommonMethods.getDayFromDate(workLogDate);
                txtDay.setText(worklogDay);
            } catch (Exception e) {
                Logger.e(e.getMessage());
            }
        }
        txtCity.setText(projectSelected);

        setVoiceToText();

    }

    private void setEditData(int tableId) {
        dailyFolder = DailyFolderRepo.Get(tableId);
        etNote.setText(dailyFolder.note);
      /*  if (!dailyFolder.image.isEmpty()) {
            listEdit.add(dailyFolder.image);
            addAdapter = new AddImageCommonAdapter(SaveDailyFolderActivity.this, listEdit);
            recyclerView.setAdapter(addAdapter);
            selectedImagePath = dailyFolder.image;

        }*/
        if (dailyFolder.image.isEmpty()) {
            listEdit.add("add");
        } else {
            selectedImagePath = dailyFolder.image;
            fileId = dailyFolder.itemGuId;
            editFileModel = FilesRepo.Get(fileId);
            listEdit.clear();
            for (int i = 0; i < editFileModel.size(); i++) {
                listEdit.add(editFileModel.get(i).getFullPath());
            }
            pathList.clear();
            pathList.addAll(listEdit);
            listEdit.add("add");
            strImagePath = dailyFolder.image;
        }
        addAdapter = new AddImageCommonAdapter(SaveDailyFolderActivity.this, listEdit);
        recyclerView.setAdapter(addAdapter);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.imgBack:
                onBackPressed();
                break;

            case R.id.btnSave:
                dailyFolder = new DailyFolder();
                uid = CommonMethods.getUuid();
                if (editView) {
                    dailyFolder.setId(Long.valueOf(tableId));
                }

                dailyFolder.note = (etNote.getText().toString().trim());
                dailyFolder.image = selectedImagePath;
                dailyFolder.projectID = String.valueOf(projectId);
                String val = CommonMethods.getCurrentTimeInUtc();
                dailyFolder.AddedOn = val;
                dailyFolder.WORK_LOG_DATE = workLogDate;

                if (fileId.isEmpty()) {
                    dailyFolder.itemGuId = uid;
                } else {
                    uid = fileId;
                    dailyFolder.itemGuId = uid;
                    try {
                        FilesRepo.DeleteFileByUid(uid);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                String res = DailyFolderRepo.Save(dailyFolder);
                if (pathList.size() > 0) {
                    saveImages(val);
                }

                if (res.isEmpty()) {
                    finish();
                } else {
                    CommonMethods.showToast(SaveDailyFolderActivity.this, res);
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
                filesModel.fileId = randomNumber;
                filesModel.FullPath = pathList.get(i);
                String repoResponse = FilesRepo.Save(filesModel);

            }

        }
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
                selectedImagePath = CommonMethods.getPath(SaveDailyFolderActivity.this, uri);

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(SaveDailyFolderActivity.this.getContentResolver(), uri);

                bitmap = checkRotate(bitmap);
                uri = CommonMethods.bitmapToUriConverter(bitmap, SaveDailyFolderActivity.this);
                selectedImagePath = CommonMethods.getPath(SaveDailyFolderActivity.this, uri);
                setImageData(bitmap, selectedImagePath, uri);
            }


            if (resultCode == RESULT_OK && requestCode == GALLERY_REQUEST_CODE) {


                if (data.getData() != null) {
                    Uri selectedImageUri = data.getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(SaveDailyFolderActivity.this.getContentResolver(), selectedImageUri);
                    selectedImageUri = bitmapToUriConverter(bitmap);
                    selectedImagePath = getPath(SaveDailyFolderActivity.this, selectedImageUri);
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
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(SaveDailyFolderActivity.this.getContentResolver(), selectedImageUri);
                                selectedImageUri = bitmapToUriConverter(bitmap);
                                selectedImagePath = getPath(SaveDailyFolderActivity.this, selectedImageUri);
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


                Uri selectedImageUri = bitmapToUriConverter(selectedBitmap);
                selectedImagePath = getPath(SaveDailyFolderActivity.this, selectedImageUri);
                setImageData(selectedBitmap, selectedImagePath, selectedImageUri);

                Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(selectedImagePath), 220, 220);

            }


        } catch (Exception e) {
            Logger.e(e.getMessage());
            //GlobalMethods.SendErrorReport(e,context);
        }
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
            File file = new File(SaveDailyFolderActivity.this.getFilesDir(), "Image"
                    + new Random().nextInt() + ".jpg");
            FileOutputStream out = SaveDailyFolderActivity.this.openFileOutput(file.getName(),
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
            Dialog dialog = new Dialog(SaveDailyFolderActivity.this);
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SaveDailyFolderActivity.this);
            builder.setTitle(SaveDailyFolderActivity.this.getResources().getString(R.string.select_image));
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
                startActivityForResult(Intent.createChooser(intent, SaveDailyFolderActivity.this.getResources().getString(R.string.select_image)), GALLERY_REQUEST_CODE);
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
            folder = SaveDailyFolderActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            //folder = new File(Environment.getExternalStorageDirectory(),"Nagad");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            imageStatus = true;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                try {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    output = new File(folder, System.currentTimeMillis() + ".jpg");

                    Uri photoURI = FileProvider.getUriForFile(SaveDailyFolderActivity.this, SaveDailyFolderActivity.this.getPackageName() + ".provider", output);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                } catch (Exception e) {
                    Logger.e(e.getMessage());
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
            int camera = ContextCompat.checkSelfPermission(SaveDailyFolderActivity.this, Manifest.permission.CAMERA);
            int writestorage = ContextCompat.checkSelfPermission(SaveDailyFolderActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            List<String> listPermissionNeeded = new ArrayList<>();

            if (camera != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(Manifest.permission.CAMERA);
            }

            if (writestorage != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (!listPermissionNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(SaveDailyFolderActivity.this, listPermissionNeeded.toArray
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
            if (ActivityCompat.shouldShowRequestPermissionRationale(SaveDailyFolderActivity.this, Manifest.permission.CAMERA)) {

            }
            if (ActivityCompat.shouldShowRequestPermissionRationale(SaveDailyFolderActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            }

            ActivityCompat.requestPermissions(SaveDailyFolderActivity.this, new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            //GlobalMethods.SendErrorReport(e,context);
        }
    }

    public void setImageData(Bitmap bitmap, String path, Uri uri) {

        for (int i = 0; i < listEdit.size(); i++) {
            if (listEdit.get(i).equalsIgnoreCase("add")) {
                listEdit.remove(i);
            }
        }
        listEdit.add(path);
        strImagePath = path;
        selectedImagePath = path;
        pathList.clear();
        pathList.addAll(listEdit);
        listEdit.add("add");
        addAdapter = new AddImageCommonAdapter(SaveDailyFolderActivity.this, listEdit);
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
            Logger.e(e.getMessage());
        }

    }


}
