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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dailyreporting.app.R;
import com.dailyreporting.app.database.SubContractorRepo;
import com.dailyreporting.app.models.SubContractActivity;

import java.util.ArrayList;
import java.util.List;

public class SubContractDailyCondtionActivity extends AppCompatActivity implements View.OnClickListener {
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
    private SubContractActivity subContractActivity= new SubContractActivity();
    private TextView txtSubContractor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dailyactivities);
        txtSubContractor = findViewById(R.id.txtDailyActivities);
        btnEdit = findViewById(R.id.btnEdit);
        btnSubmit = findViewById(R.id.btnSubmit);
        imgBack = findViewById(R.id.imgBack);
        seekBar = findViewById(R.id.seekBar);
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
        spinnerComplaint.setSelection(0);
        spinnerTaskCompleted.setSelection(0);
        txtSubContractor.setText(getResources().getString(R.string.subcontractordailycondition));
        if (SubContractorRepo.GetAll() != null) {
            if (SubContractorRepo.GetAll().size() > 0) {
                subContractActivity = SubContractorRepo.GetAll().get(0);


                etActivityName.setText(subContractActivity.activityname);
                etCode.setText(subContractActivity.code);
                etLocation.setText(subContractActivity.location);
                etNote.setText(subContractActivity.Note);
                strActivityName = subContractActivity.activityname;
                strCode = subContractActivity.code;
                strLocation = subContractActivity.location;
                strNote = subContractActivity.Note;

                etNote.addTextChangedListener(new SubContractDailyCondtionActivity.GenericTextWatcher(etNote));
                etLocation.addTextChangedListener(new SubContractDailyCondtionActivity.GenericTextWatcher(etLocation));
                etCode.addTextChangedListener(new SubContractDailyCondtionActivity.GenericTextWatcher(etCode));
                etActivityName.addTextChangedListener(new SubContractDailyCondtionActivity.GenericTextWatcher(etActivityName));
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
                    startActivity(new Intent(SubContractDailyCondtionActivity.this, DailyVisitActivity.class));
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
