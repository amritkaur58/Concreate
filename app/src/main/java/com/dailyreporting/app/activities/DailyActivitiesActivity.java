package com.dailyreporting.app.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dailyreporting.app.R;
import com.dailyreporting.app.database.ActivitiesRepo;
import com.dailyreporting.app.models.ActivityModel;

import java.util.ArrayList;
import java.util.List;

public class DailyActivitiesActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnEdit, btnSubmit;
    private ImageView imgBack;
    private SeekBar seekBar;
    private EditText etActivityName, etCode, etLocation, etNote;
    /////////////////
    private Spinner spinnerTaskCompleted, spinnerComplaint;
    private ArrayAdapter arrayTaskCompleted;
    private ArrayAdapter arrayComplaint;
    private List<String> listTaskCompleted = new ArrayList<>();
    private String strUserId = "", strActivityName = "", strCode = "", strNote = "", strLocation = "", strImagePath = "",strTaskCompleted="",strComplaint="";
    private List<String> listEdit = new ArrayList<>();
    private ActivityModel activityModel= new ActivityModel();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dailyactivities);
        btnEdit = findViewById(R.id.btnEdit);
        btnSubmit = findViewById(R.id.btnSubmit);
        imgBack = findViewById(R.id.imgBack);
        seekBar = findViewById(R.id.seekBar);
        spinnerComplaint = findViewById(R.id.spinnerComplaint);
        seekBar.setOnClickListener(this);
        seekBar.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
        imgBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        // Add Data in ArrayList
        listTaskCompleted = new ArrayList<>();
        listTaskCompleted.add("Yes");
        listTaskCompleted.add("No");
        // call spinner method

        if (ActivitiesRepo.GetAll() != null) {
            if (ActivitiesRepo.GetAll().size() > 0) {
                activityModel = ActivitiesRepo.GetAll().get(0);
                etActivityName.setText(activityModel.activityname);
                etCode.setText(activityModel.code);
                etLocation.setText(activityModel.location);
                etNote.setText(activityModel.note);
                strActivityName = activityModel.activityname;
                strCode = activityModel.code;
                strLocation = activityModel.location;
                strNote = activityModel.note;

                etNote.addTextChangedListener(new DailyActivitiesActivity.GenericTextWatcher(etNote));
                etLocation.addTextChangedListener(new DailyActivitiesActivity.GenericTextWatcher(etLocation));
                etCode.addTextChangedListener(new DailyActivitiesActivity.GenericTextWatcher(etCode));
                etActivityName.addTextChangedListener(new DailyActivitiesActivity.GenericTextWatcher(etActivityName));
            }
        }
        spinnerTaskCompleted.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                strTaskCompleted = spinnerTaskCompleted.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        /////
        spinnerComplaint.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                strComplaint = spinnerComplaint.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSubmit:
                if (etActivityName.getText().toString().equals("")) {
                    Toast.makeText(this, this.getResources().getString(R.string.enteractivityname), Toast.LENGTH_SHORT).show();
                } else if (etCode.getText().toString().equals("")) {
                    Toast.makeText(this, this.getResources().getString(R.string.entercode), Toast.LENGTH_SHORT).show();
                } else if (etLocation.getText().toString().equals("")) {
                    Toast.makeText(this, this.getResources().getString(R.string.enterlocation), Toast.LENGTH_SHORT).show();
                } else if (etNote.getText().toString().equals("")) {
                    Toast.makeText(this, this.getResources().getString(R.string.enterlocation), Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(DailyActivitiesActivity.this, SubContractDailyCondtionActivity.class));
                }

                break;

            case R.id.imgBack:
                onBackPressed();
                break;
        }
    }
    private class GenericTextWatcher implements TextWatcher {

        private View view;

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
