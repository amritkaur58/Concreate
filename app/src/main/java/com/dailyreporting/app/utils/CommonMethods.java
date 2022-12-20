package com.dailyreporting.app.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dailyreporting.app.R;
import com.dailyreporting.app.database.ActivitiesRepo;
import com.dailyreporting.app.database.CorrectionsRepo;
import com.dailyreporting.app.database.SubContractorRepo;
import com.dailyreporting.app.database.VisitLogRepo;
import com.dailyreporting.app.models.ActivityModel;
import com.dailyreporting.app.models.Correction;
import com.dailyreporting.app.models.SubContractActivity;
import com.dailyreporting.app.models.VisitLog;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;

import static com.dailyreporting.app.utils.Constants.MyPREFERENCES;
import static com.yalantis.ucrop.util.BitmapLoadUtils.calculateInSampleSize;
import static com.yalantis.ucrop.util.FileUtils.getDataColumn;
import static com.yalantis.ucrop.util.FileUtils.isDownloadsDocument;
import static com.yalantis.ucrop.util.FileUtils.isExternalStorageDocument;
import static com.yalantis.ucrop.util.FileUtils.isGooglePhotosUri;
import static com.yalantis.ucrop.util.FileUtils.isMediaDocument;


public class CommonMethods {

    public static final String inputFormat = "hh:mm";
    public static CommonMethods mInstance = null;
    static Context mContext;
    static SimpleDateFormat inputParser;

    public static CommonMethods getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new CommonMethods();
        }
        mContext = context;
        return mInstance;
    }

    public static boolean checkLocalData(int projectId) {
        boolean dataAdded = false;
        List<ActivityModel> activityList = new ArrayList<>();
        activityList = ActivitiesRepo.GetActivityByProject(projectId);
        if (activityList.size() > 0) {
            dataAdded = true;
        }
        List<SubContractActivity> contractList = new ArrayList<>();
        contractList = SubContractorRepo.GetSubContractByProject(projectId);
        if (contractList.size() > 0) {
            dataAdded = true;
        }
        List<VisitLog> visitList = new ArrayList<>();
        visitList = VisitLogRepo.GetVisitLog(projectId);
        if (visitList.size() > 0) {
            dataAdded = true;
        }
        List<Correction> correctionList = new ArrayList<>();
        correctionList = CorrectionsRepo.GetCorrectionByParent(projectId);
        if (correctionList.size() > 0) {
            dataAdded = true;
        }
        return dataAdded;
    }

    public static boolean checkDarkMode(Context context) {
        boolean nightMode = false;
        int currentNightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                nightMode = false;
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                nightMode = true;
                break;

        }
        return nightMode;
    }

    public static void showLog(String tag, String message) {
        Log.e(tag, message + "");
    }

    public static boolean isOnline(Activity activity) {
        if (activity != null) {


            ConnectivityManager conMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

            //  Toast.makeText(activity, "No Internet connection", Toast.LENGTH_LONG).show();
            return netInfo != null && netInfo.isConnected() && netInfo.isAvailable();
        } else {
            return false;
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static List<String> getArrayList() {
        ArrayList<String> listTask = new ArrayList<>();
        listTask.add("true");
        listTask.add("false");
        return listTask;
    }

    public static String getUuid() {
        String uniqueID = "";
        try {
            uniqueID = UUID.randomUUID().toString();

        } catch (Exception e) {
            Logger.e(e.getMessage());
        }

        return uniqueID;
    }

    public static List<SubContractActivity> getSelectionList() {
        ArrayList<SubContractActivity> listTask = new ArrayList<>();
        SubContractActivity model = new SubContractActivity();
        model.parentName = "Yes";
        model.IsCompliant = "true";
        listTask.add(model);
        model = new SubContractActivity();
        model.parentName = "No";
        model.IsCompliant = "false";
        listTask.add(model);
        return listTask;
    }

    public static String getTimeValue(String timeVal) {

        SimpleDateFormat utcFormatter = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        Date gpsUTCDate = null;//from  ww  w.j  a va 2 s  . c  o  m
        try {
            gpsUTCDate = utcFormatter.parse(timeVal);
        } catch (ParseException e) {
            Logger.e(e.getMessage());
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(" hh:mm a");

            return sdf.format(gpsUTCDate);
        } catch (Exception ex) {
            return "xx";
        }

    }

    public static void showLogger(Context context, Exception e) {
     /*   Logger logger = Logger.getAnonymousLogger();
        logger.log(Level.SEVERE, "an exception was thrown", e);
*/
    }

    public static String changeTimeFormat(String time) {
        String a = "";
        try {
            String _24HourTime1 = time;
            SimpleDateFormat _24HourSDF1 = new SimpleDateFormat("HH:mm");
            SimpleDateFormat _12HourSDF1 = new SimpleDateFormat("hh:mm a");
            Date _24HourDt1 = _24HourSDF1.parse(_24HourTime1);
            a = _12HourSDF1.format(_24HourDt1);
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
        return a;
    }

    public static String getDayFromDate(String time) {
        String a = "";
        try {
            SimpleDateFormat inFormat = new SimpleDateFormat("dd MMM, yyyy");
            Date date = inFormat.parse(time);
            SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
            a = outFormat.format(date);
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
        return a;
    }

    public static String getMonthShortName(int monthNumber) {
        String monthName = "";

        if (monthNumber >= 0 && monthNumber < 12)
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.MONTH, monthNumber);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM");
                simpleDateFormat.setCalendar(calendar);
                monthName = simpleDateFormat.format(calendar.getTime());
            } catch (Exception e) {
                if (e != null)
                    Logger.e(e.getMessage());
            }
        return monthName;
    }

    public static String getCurrentDateByFormat(String format) {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.US);
        String createdOn = df.format(date);

        return createdOn;
    }

    public static String getCurrentTimeByFormat(String format) {
        Date date = Calendar.getInstance().getTime();

        DateFormat dateFormat = new SimpleDateFormat(format);

        String localTime = dateFormat.format(date);

        return localTime;
    }

    public static String getCurrentTimeInUtc() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String val = sdf.format(new Date(System.currentTimeMillis()));

        return val;
    }

    public static String getWorkLogTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String val = sdf.format(new Date(System.currentTimeMillis()));

        return val;
    }

    public static String getCurrentTimeArrival() {

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy HH:mm", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getDefault());
        String val = sdf.format(new Date(System.currentTimeMillis()));

        return val;
    }

    public static String getTimeFromUTC(String utcTime) {

        String time = "";
        try {

            if (utcTime != null) {
                SimpleDateFormat utcFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
                utcFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date gpsUTCDate = null;
                try {
                    gpsUTCDate = utcFormatter.parse(utcTime);
                } catch (ParseException e) {
                    Logger.e(e.getMessage());
                }
                SimpleDateFormat localFormatter = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                localFormatter.setTimeZone(TimeZone.getDefault());

                if (gpsUTCDate != null) {
                    time = localFormatter.format(gpsUTCDate.getTime());
                }

            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
        return time;
    }

    public static String setDatePicker(Context context) {
        final String[] workLogDate = {""};
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
                        workLogDate[0] = year + "-" + monthValue + "-" + day;


                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
        return workLogDate[0];
    }

    public static String getWorkLogDate(String date) {

        String time = "";
        try {

            if (date != null) {
                Date currentDate = Calendar.getInstance().getTime();

                DateFormat dateFormat = new SimpleDateFormat("hh:mm a");

                String localTime = dateFormat.format(currentDate);

                SimpleDateFormat utcFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                utcFormatter.setTimeZone(TimeZone.getDefault());
                Date gpsUTCDate = null;
                try {
                    gpsUTCDate = utcFormatter.parse(date);
                } catch (ParseException e) {
                    Logger.e(e.getMessage());
                }
                SimpleDateFormat localFormatter = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
                localFormatter.setTimeZone(TimeZone.getDefault());

                if (gpsUTCDate != null) {
                    time = localFormatter.format(gpsUTCDate.getTime());
                }

            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
        return time;
    }

    public static String convertDateToFormat(String date) {

        String time = "";
        try {

            if (date != null) {
                SimpleDateFormat utcFormatter = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
                utcFormatter.setTimeZone(TimeZone.getDefault());
                Date gpsUTCDate = null;
                try {
                    gpsUTCDate = utcFormatter.parse(date);
                } catch (ParseException e) {
                    Logger.e(e.getMessage());
                }
                SimpleDateFormat localFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                localFormatter.setTimeZone(TimeZone.getDefault());

                if (gpsUTCDate != null) {
                    time = localFormatter.format(gpsUTCDate.getTime());
                }

            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
        return time;
    }

    public static String convertWorkLogDate(String date) {

        String time = "";
        try {

            if (date != null) {
                Date currentDate = Calendar.getInstance().getTime();
                DateFormat dateFormat = new SimpleDateFormat("HH:mm a");
                DateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy");

                String localTime = dateFormat.format(currentDate);
                String localDate = currentDateFormat.format(currentDate);
                String newDate = date + " " + localTime;

                SimpleDateFormat utcFormatter = new SimpleDateFormat("dd MMM, yyyy HH:mm a", Locale.ENGLISH);
                utcFormatter.setTimeZone(TimeZone.getDefault());
                Date gpsUTCDate = null;
                try {
                    gpsUTCDate = utcFormatter.parse(newDate);
                } catch (ParseException e) {
                    Logger.e(e.getMessage());
                }
                SimpleDateFormat localFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
               // localFormatter.setTimeZone(TimeZone.getDefault());
                localFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

                if (gpsUTCDate != null) {
                    time = localFormatter.format(gpsUTCDate.getTime());
                }

            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
        return time;
    }

    public static String getAddOnFromUTC(String utcTime) {

        String time = "";
        try {

            if (utcTime != null) {
                SimpleDateFormat utcFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
                utcFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date gpsUTCDate = null;
                try {
                    gpsUTCDate = utcFormatter.parse(utcTime);
                } catch (ParseException e) {
                    Logger.e(e.getMessage());
                }
                SimpleDateFormat localFormatter = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                localFormatter.setTimeZone(TimeZone.getDefault());

                if (gpsUTCDate != null) {
                    time = localFormatter.format(gpsUTCDate.getTime());
                }

            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
        return time;
    }

    public static String getCurrentDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        return dayOfTheWeek;
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static String convertLocalToUtc(String utcTime) {
        String time = "";
        try {

            if (utcTime != null) {
                SimpleDateFormat utcFormatter = new SimpleDateFormat("dd MMM, yyyy HH:mm", Locale.ENGLISH);
                utcFormatter.setTimeZone(TimeZone.getDefault());
                Date gpsUTCDate = null;
                try {
                    gpsUTCDate = utcFormatter.parse(utcTime);
                } catch (ParseException e) {
                    Logger.e(e.getMessage());
                }
                SimpleDateFormat localFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
                localFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

                if (gpsUTCDate != null) {
                    time = localFormatter.format(gpsUTCDate.getTime());
                }

            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }

        return time;
    }

    public static String getToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String token = sharedPreferences.getString(Constants.TOKEN, "");
        return token;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {
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


    public static Uri bitmapToUriConverter(Bitmap mBitmap, Context context) {
        Uri uri = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            int nh = (int) (mBitmap.getHeight() * (512.0 / mBitmap.getWidth()));

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 512, nh);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap newBitmap = Bitmap.createScaledBitmap(mBitmap, 512, nh,
                    true);
            File file = new File(context.getFilesDir(), "Image"
                    + new Random().nextInt() + ".jpg");
            FileOutputStream out = context.openFileOutput(file.getName(),
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

    public static boolean compareDates() {
        Calendar now = Calendar.getInstance();

        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);

        Date date = parseDate(hour + ":" + minute);
        Date dateCompareOne = parseDate("18:00");
        Date dateCompareTwo = parseDate("22:15");

        //yada yada
        return dateCompareOne.before(date) && dateCompareTwo.after(date);
    }

    private static Date parseDate(String date) {
        inputParser = new SimpleDateFormat(inputFormat, Locale.US);
        try {
            return inputParser.parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }

    public static boolean getLocalData() {

        try {
            if (ActivitiesRepo.GetAll().size() > 0) {
                return true;
            } else {
                if (SubContractorRepo.GetAll().size() > 0) {
                    return true;
                } else {
                    if (VisitLogRepo.GetAll().size() > 0) {
                        return true;
                    } else {
                        return CorrectionsRepo.GetAll().size() > 0;
                    }
                }
            }

        } catch (Exception e) {
            return false;
        }

    }

    public static int getRandomNumber() {
        Random random = new Random(System.nanoTime() % 100000);
        int randomInt = random.nextInt(1000000000);
        return randomInt;
    }

    public boolean checkPermissions(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static class Mode {

        public static boolean isLive(Context context) {
            boolean isLive = false;
            SharedPreferences sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            String apiMode = sharedPreferences.getString(Constants.API_MODE, Constants.LIVE_MODE);
            isLive = apiMode.equalsIgnoreCase(Constants.LIVE_MODE);
            return isLive;
        }

        public static void saveApiMode(Context context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String apiMode = sharedPreferences.getString(Constants.API_MODE, Constants.LIVE_MODE);
            if (apiMode.equalsIgnoreCase(Constants.LIVE_MODE)) {
                CommonMethods.showToast(context, context.getString(R.string.staging_mode));
                editor.putString(Constants.API_MODE, Constants.STAGING_MODE);
                editor.apply();
            } else {
                CommonMethods.showToast(context, context.getString(R.string.live_mode));
                editor.putString(Constants.API_MODE, Constants.LIVE_MODE);
                editor.apply();
            }
        }
    }

    public static class CheckPermission {

        public static void requestPermission(Context context, int requestCode) {
            try {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CAMERA)) {

                }
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                }

                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, requestCode);
            } catch (Exception e) {
                //GlobalMethods.SendErrorReport(e,context);
            }
        }


        public static boolean isPermissionAllowed(Context context, int requestCode) {
            try {
                int camera = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
                int writestorage = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                List<String> listPermissionNeeded = new ArrayList<>();

                if (camera != PackageManager.PERMISSION_GRANTED) {
                    listPermissionNeeded.add(Manifest.permission.CAMERA);
                }

                if (writestorage != PackageManager.PERMISSION_GRANTED) {
                    listPermissionNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }

                if (!listPermissionNeeded.isEmpty()) {
                    ActivityCompat.requestPermissions((Activity) context, listPermissionNeeded.toArray
                            (new String[listPermissionNeeded.size()]), requestCode);
                    return false;
                }

            } catch (Exception e) {
                //GlobalMethods.SendErrorReport(e,context);
            }
            return true;
        }
    }
}