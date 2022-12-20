package com.dailyreporting.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dailyreporting.app.R;
import com.dailyreporting.app.adapters.TakePictureAdapter;

public class TakePictureActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView recyclerView;
    TakePictureAdapter takePictureAdapter;
    TextView txtNext;
    ImageView imgBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takepicture);
        recyclerView = findViewById(R.id.recyclerView);
        txtNext = findViewById(R.id.txtNext);
        imgBack = findViewById(R.id.imgBack);
        txtNext.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerView.setNestedScrollingEnabled(false);
        takePictureAdapter = new TakePictureAdapter(TakePictureActivity.this);
        recyclerView.setAdapter(takePictureAdapter);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtNext:
                startActivity(new Intent(TakePictureActivity.this, SavePictureActivity.class));
                break;

            case R.id.imgBack:
                onBackPressed();
                break;
        }
    }
}
