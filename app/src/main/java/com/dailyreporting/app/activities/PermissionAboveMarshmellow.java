package com.dailyreporting.app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class PermissionAboveMarshmellow extends AppCompatActivity {

    private static int LOCATION_PERMISSIONS_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        askPermissions();
        finish();
    }

    protected void askPermissions() {

        LOCATION_PERMISSIONS_REQUEST = 1;
        if(!checkWriteExternalPermission()){

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSIONS_REQUEST);

            Log.i("askPermissions:", "askPermissions");

        }

    }

    private boolean checkWriteExternalPermission()
    {

        String permission = "android.permission.WRITE_EXTERNAL_STORAGE";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        Log.i("RequestPermissions:", "RequestPermissions");
        if (requestCode == LOCATION_PERMISSIONS_REQUEST) {

            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Log.i("RequestPermissions:", "RequestPermissionsInside");

            } else {
                Toast.makeText(this, "Access Location permission denied, PLEASE ACCEPT IT.", Toast.LENGTH_SHORT).show();
                askPermissions();
            }
        } else {

            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }

}