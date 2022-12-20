package com.dailyreporting.app.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;

import com.dailyreporting.app.Extras.Logger;
import com.dailyreporting.app.R;
import com.dailyreporting.app.Repositories.WorklogRepo;
import com.dailyreporting.app.WebApis.ApiClientClass;
import com.dailyreporting.app.WebApis.WeatherService;
import com.dailyreporting.app.WebApis.WorklogService;
import com.dailyreporting.app.models.WeatherPojo;
import com.dailyreporting.app.models.WorkLogResponse;
import com.dailyreporting.app.models.WorklogDetail;
import com.dailyreporting.app.utils.CommonMethods;
import com.dailyreporting.app.utils.Constants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import zh.wang.android.yweathergetter4a.YahooWeather;

import static com.dailyreporting.app.utils.Constants.MyPREFERENCES;

public class DailyConditionsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_LOCATION = 167;
    private static final Random RANDOM = new SecureRandom();
    private static final int ITERATIONS = 1000;
    private static final int KEY_LENGTH = 256; // bits
    public static javax.crypto.SecretKey SecretKey = null;
    public static byte[] Ciphertext = null;
    public static String saltGlobal = null;
    public static IvParameterSpec Iv = null;
    public static byte[] iv = new byte[16];
    private final YahooWeather mYahooWeather = YahooWeather.getInstance(5000, true);
    Spinner weatherSP;
    List<String> weatherList = new ArrayList<>();
    String TAG = getClass().getSimpleName();
    TextView tempET;
    TextView titleTop, txtDailyConditions;
    TextView txtWeather, txtTemperature;
    private Button btnSave;
    private ImageView imgBack;
    private SeekBar seekBar;
    private String authorizationToken = "";
    private ArrayAdapter<String> arrayName;
    private String weatherName = "";
    private ProgressDialog dialog;
    private int projectId = 0;
    private String projectSelected = "";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String locationId = "";
    private LocationManager nManager;
    private String latitude = "0.00";
    private String longitude = "0.00";
    private LocationManager locationManager;
    private double longitudeLong = 0.00;
    private double latitudeLong = 0.00;
    private String addr;
    private URL url;
    private URLConnection conec;
    private InputStream stream;
    private XmlPullParser xpp;
    private ProgressDialog mProgressDialog;
    private IvParameterSpec mIvParameterSpec;
    private boolean isOpened = false;
    private double latDouble = 0.00, longiDouble = 0.00;
    private String WorkLogDate = "";
    private String addedate = "";
    private TextView weatherTV, cityName;
    private ProgressDialog dialogWorklog;
    private boolean settingFont = false;
    private float fontSize = 13f;

    public static byte[] getNextSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        saltGlobal = Arrays.toString(salt);
        return salt;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dailyconditions);
        btnSave = findViewById(R.id.btnSave);
        imgBack = findViewById(R.id.imgBack);
        seekBar = findViewById(R.id.seekBar);
        weatherSP = findViewById(R.id.weatherSP);
        tempET = findViewById(R.id.tempET);
        weatherTV = findViewById(R.id.weatherTV);
        cityName = findViewById(R.id.cityName);
        txtWeather = findViewById(R.id.txtWeather);
        txtTemperature = findViewById(R.id.txtTemperature);
        txtDailyConditions = findViewById(R.id.txtDailyConditions);
        titleTop = findViewById(R.id.titleTop);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        locationId = sharedPreferences.getString(Constants.LOCATION_ID, "");
        authorizationToken = sharedPreferences.getString(Constants.TOKEN, "");
        seekBar.setOnClickListener(this);
        seekBar.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
        btnSave.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        WorkLogDate = CommonMethods.getCurrentDateByFormat("dd MMM, yyyy");

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        setKeyforWeather();
        getLocation();

        if (getIntent().hasExtra("WorkLogDate")) {
            WorkLogDate = getIntent().getStringExtra("WorkLogDate");
        }

        if (getIntent().hasExtra("ProjectId")) {
            projectId = getIntent().getIntExtra("ProjectId", 0);
            projectSelected = getIntent().getStringExtra("ProjectSelected");

        }
        getWorkLogDetail();
        addedate = CommonMethods.convertWorkLogDate(WorkLogDate);
        setWeatherSpinner();
        weatherSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (weatherList.size() > 0) {
                    weatherName = weatherSP.getSelectedItem().toString();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
        titleTop.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtDailyConditions.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

        tempET.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtWeather.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtTemperature.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        weatherTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        cityName.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

        btnSave.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

    }

    private void setWeatherMethod() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (!isOpened) {
                        isOpened = true;
                        loadWeatherFeed();
                    }

                    //  String temp_c = SendToUrl(addr);
                } catch (Exception e) {
                    Logger.error(e);
                }

            }
        });

        thread.start();

    }

    private void setKeyforWeather() {
        getNextSalt();
        mIvParameterSpec = new IvParameterSpec(iv);

        try {
            hashPassword((getString(R.string.parse_application_id)), saltGlobal);
            Encrypt();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException
                | NoSuchPaddingException | InvalidParameterSpecException | UnsupportedEncodingException | IllegalBlockSizeException e) {
            Logger.error(e);
        }
        mIvParameterSpec = new IvParameterSpec(iv);

        try {
            hashPassword((getString(R.string.parse_application_id)), saltGlobal);
            Encrypt();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException
                | NoSuchPaddingException | InvalidParameterSpecException | UnsupportedEncodingException | IllegalBlockSizeException e) {
            Logger.error(e);
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
        mProgressDialog = new ProgressDialog(DailyConditionsActivity.this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    private void getLocationMethod() {
        if (ActivityCompat.checkSelfPermission(
                DailyConditionsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                DailyConditionsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location locationGPS = nManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                latDouble = locationGPS.getLatitude();
                longiDouble = locationGPS.getLongitude();
                latitude = String.valueOf(latDouble);
                longitude = String.valueOf(longiDouble);
                editor.putString(Constants.CURRENT_LATITUDE, latitude);
                editor.putString(Constants.CURRENT_LONGITUDE, longitude);
                editor.apply();
                setWeatherMethod();
            } else {
                try {
                    find_Location(DailyConditionsActivity.this);
                } catch (Exception e) {
                    Logger.error(e);
                }

            }
        }
    }

    private void getLocation() {

        try {
            String[] locArray = {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION;
            nManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!nManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                OnGPS();
            } else {
                getLocationMethod();
            }

        } catch (Exception e) {
            Logger.error(e);
        }
    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void setWeatherSpinner() {

        weatherList.add("sunny");
        weatherList.add("cloudy");
        weatherList.add("windy");
        weatherList.add("rainy");
        weatherList.add("stormy");

        setSpinnerAdapter(weatherList);
    }

    private void setSpinnerAdapter(List<String> activityList) {
        try {

            arrayName = new ArrayAdapter<String>(DailyConditionsActivity.this, android.R.layout.simple_spinner_item, activityList) {
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
            weatherSP.setAdapter(arrayName);


        } catch (Exception e) {
            Logger.error(e);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSave:
                workLogService();

                break;
            case R.id.imgBack:
                onBackPressed();
                break;
        }
    }

    private void workLogService() {

        try {

            dialog = new ProgressDialog(this);
            try {
                dialog.show();
            } catch (WindowManager.BadTokenException e) {

            }
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.custom_loading_layout);

            JSONObject user = new JSONObject();

            JsonObject gsonObject = null;
            if (!weatherTV.getText().toString().isEmpty()) {
                weatherName = weatherTV.getText().toString().trim();
            } else {
                weatherName = getString(R.string.unpredictable);
            }
            try {
                user.put("Id", 0);
                user.put("ProjectId", projectId);
                user.put("Title", projectSelected);
                user.put("Temperature", tempET.getText().toString().trim());
                user.put("Weather", weatherName);
                user.put("WorkLogDate", addedate);

                JsonParser jsonParser = new JsonParser();
                gsonObject = (JsonObject) jsonParser.parse(user.toString());

            } catch (JSONException e) {
                Logger.error(e);
            }

            LiveData<WorkLogResponse> req = new WorklogRepo().SaveWorkLog(DailyConditionsActivity.this, authorizationToken, sharedPreferences, dialog, gsonObject);
            req.observe(this, response -> {
                try {
                    if (response.getStatus().equalsIgnoreCase("200")) {
                        Intent intent = new Intent(DailyConditionsActivity.this, ListActivity.class);
                        intent.putExtra(Constants.FROM_REPORT, "true");
                        intent.putExtra("WorkLogDate", WorkLogDate);
                        intent.putExtra(Constants.WORKLOG_ID, response.getData().getId());
                        startActivity(intent);
                        finish();
                    } else if (response.getStatus().equalsIgnoreCase("208")) {
                        Intent intent = new Intent(DailyConditionsActivity.this, ListActivity.class);
                        intent.putExtra(Constants.FROM_REPORT, "true");
                        intent.putExtra("WorkLogDate", WorkLogDate);
                        intent.putExtra(Constants.WORKLOG_ID, response.getData().getId());
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            CommonMethods.showToast(DailyConditionsActivity.this, getString(R.string.something_wrong));

                        } catch (Exception e) {
                            Logger.error(e);
                        }


                    }

                } catch (Exception e) {
                    CommonMethods.showToast(DailyConditionsActivity.this, getString(R.string.something_wrong));
                    Logger.error(e);
                }


            });


        } catch (Exception e) {
            if (dialog != null)
                dialog.dismiss();
            Log.e(TAG, e.getMessage());
        }


    }

    public void getWorkLogDetail() {
        try {
            dialogWorklog = new ProgressDialog(DailyConditionsActivity.this);
            try {
                dialogWorklog.show();
            } catch (WindowManager.BadTokenException e) {

            }
            dialogWorklog.setCancelable(false);
            dialogWorklog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialogWorklog.setContentView(R.layout.custom_loading_layout);
            WorklogService web_api_2 = ApiClientClass.getApiClientDev(DailyConditionsActivity.this).create(WorklogService.class);
            Call<WorklogDetail> call = web_api_2.getWorklogs(projectId);

            if (call != null) {
                call.enqueue(new Callback<WorklogDetail>() {
                    @Override
                    public void onResponse(Call<WorklogDetail> call, Response<WorklogDetail> response) {
                        dialogWorklog.dismiss();
                        try {
                            cityName.setText(response.body().getData().getItems().get(0).getCityName());
                        } catch (Exception e) {
                            Logger.error(e);
                        }

                    }

                    @Override
                    public void onFailure(Call<WorklogDetail> call, Throwable t) {
                        dialogWorklog.dismiss();
                        CommonMethods.showLog(TAG, "Failure " + t.getMessage());
                    }
                });
            }
        } catch (Exception e) {

            if (dialogWorklog != null) {
                dialogWorklog.dismiss();
            }
            Log.e(TAG, e.getMessage());
        }

    }

    public void find_Location(Context con) {
        Log.d("Find Location", "in find_location");

        String location_context = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) con.getSystemService(location_context);

        List<String> providers = locationManager.getProviders(true);
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(provider, 1000, 0, new LocationListener() {
                public void onLocationChanged(Location location) {
                }

                public void onProviderDisabled(String provider) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }
            });
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                latitudeLong = location.getLatitude();
                longitudeLong = location.getLongitude();
                latitude = String.valueOf(latitudeLong);
                longitude = String.valueOf(longitudeLong);
                editor.putString(Constants.CURRENT_LATITUDE, latitude);
                editor.putString(Constants.CURRENT_LONGITUDE, longitude);
                editor.apply();
                setWeatherMethod();
                // addr = ConvertPointToLocation(latitudeLong, longitudeLong);


            }
        }
    }

    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    private void loadWeatherFeed() {

        if (shouldAskPermissions()) {

            Intent intent = new Intent(this, PermissionAboveMarshmellow.class);
            this.startActivity(intent);

            WeatherFeed();

        } else {

            WeatherFeed();
        }

    }

    private void hashPassword(String password, String salt) throws InvalidKeySpecException, NoSuchAlgorithmException {
        char[] passwordChars = password.toCharArray();
        byte[] saltBytes = salt.getBytes();

        PBEKeySpec spec = new PBEKeySpec(
                passwordChars,
                saltBytes,
                ITERATIONS,
                KEY_LENGTH
        );

        /* Derive the key, given password and salt. */
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        javax.crypto.SecretKey tmp = factory.generateSecret(spec);
        SecretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

    }

    private void WeatherFeed() {

        if (CommonMethods.isNetworkAvailable(DailyConditionsActivity.this)) {
            try {
                Decrypt();
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException |
                    UnsupportedEncodingException | BadPaddingException | InvalidAlgorithmParameterException
                    | InvalidKeyException e) {
                Logger.error(e);
            }
        }
    }

    public String ConvertPointToLocation(double pointlat, double pointlog) {

        String address = "";
        Geocoder geoCoder = new Geocoder(DailyConditionsActivity.this,
                Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocation(pointlat, pointlog, 1);
            if (addresses.size() > 0) {
                //   for (int index = 0; index < addresses.get(0).getMaxAddressLineIndex(); index++)
                address = addresses.get(0).getCountryName() + " ";
            }
        } catch (IOException e) {
            Logger.error(e);
        }
        return address;
    }

    private String SendToUrl(String string) {
        // TODO Auto-generated method stub

        try {
            string = string.replace(" ", "%20");

            String queryString = "http://api.wunderground.com/auto/wui/geo/WXCurrentObXML/index.xml?query=" + string;
            url = new URL(queryString);

            conec = url.openConnection();
            stream = conec.getInputStream();
            xpp = XmlPullParserFactory.newInstance().newPullParser();
            xpp.setInput(stream, null);
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String elementName = xpp.getName();
                    if ("temp_c".equals(elementName)) {
                        int acount = xpp.getAttributeCount();
                        for (int x = 0; x < acount; x++) {
                            xpp.getAttributeValue(x);
                            String str = xpp.getAttributeValue(x);
                        }

                    }
                }
                eventType = xpp.next();
            }
        } catch (MalformedURLException e) {
            Logger.error(e);
        } catch (IOException e) {
            Logger.error(e);
        } catch (XmlPullParserException e) {
            Logger.error(e);
        }

        return "";
    }

    private void UserLiveData(String passwordString) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.HTTP.CLIENTS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //GPSTracker gpsTracker = new GPSTracker(this);
        WeatherService service = retrofit.create(WeatherService.class);

        Call<WeatherPojo> listCall = service.getWeather(latDouble, longiDouble,
                "metric", passwordString);
        listCall.enqueue(new Callback<WeatherPojo>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<WeatherPojo> call, Response<WeatherPojo> response) {
                if (response.code() == 200) {
                    WeatherPojo weatherList = response.body();
                    try {
                        tempET.setText(weatherList.getMain().getTemp());
                    } catch (Exception e) {
                        Logger.error(e);
                    }
                    for (int z = 0; z < weatherList.getWeather().size(); z++) {
                        if (!weatherList.getWeather().get(z).getMain().isEmpty()) {
                            weatherTV.setText(weatherList.getWeather().get(z).getMain());
                        }


                        //  temp.setText(String.valueOf(weatherList.getMain().getTemp())+ GetUnit(String.valueOf(getApplication().getResources().getConfiguration().locale.getCountry())));


                    }
                } else {
                    int sc = response.code();
                    switch (sc) {
                        case 400:
                            Log.e("Error 400", "Bad Request");

                            break;
                        case 404:
                            Log.e("Error 404", "Not Found");

                            break;
                        default:
                            Log.e("Error", "Generic Error");

                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherPojo> call, Throwable t) {
                Logger.info(t.getMessage());
            }


        });

    }

    private void Decrypt() throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException,
            IllegalBlockSizeException, UnsupportedEncodingException, InvalidAlgorithmParameterException, InvalidKeyException {
        /* Decrypt the message, given derived key and initialization vector. */
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, SecretKey, Iv);
        String plaintext = new String(cipher.doFinal(Ciphertext), StandardCharsets.UTF_8);
        UserLiveData(plaintext);
    }

    private void Encrypt() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidParameterSpecException,
            UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {

        /* Encrypt the message. */
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, SecretKey);
        RANDOM.nextBytes(iv);
        Iv = new IvParameterSpec(iv);
        Ciphertext = cipher.doFinal((getString(R.string.parse_application_id)).getBytes(StandardCharsets.UTF_8));
    }

    private void checkDarkMode() {
        if (CommonMethods.checkDarkMode(DailyConditionsActivity.this)) {
            titleTop.setTextColor(ContextCompat.getColor(DailyConditionsActivity.this, R.color.white));
            txtDailyConditions.setTextColor(ContextCompat.getColor(DailyConditionsActivity.this, R.color.white));
        }
    }

}

