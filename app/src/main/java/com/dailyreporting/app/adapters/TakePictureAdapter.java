package com.dailyreporting.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dailyreporting.app.R;

public class TakePictureAdapter extends RecyclerView.Adapter<TakePictureAdapter.ViewHolder> {
    Context context;

    public TakePictureAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public TakePictureAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_takepicture, parent, false);
        return new TakePictureAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TakePictureAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
