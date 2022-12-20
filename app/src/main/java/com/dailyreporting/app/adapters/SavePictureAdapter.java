package com.dailyreporting.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dailyreporting.app.R;

public class SavePictureAdapter extends RecyclerView.Adapter<SavePictureAdapter.ViewHolder> {
    Context context;

    public SavePictureAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public SavePictureAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_savepictures, parent, false);
        return new SavePictureAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavePictureAdapter.ViewHolder holder, int position) {
        holder.imgVideo.setImageResource(R.drawable.demo);
    }

    @Override
    public int getItemCount() {
        return 8;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgVideo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgVideo = itemView.findViewById(R.id.imgVideo);
        }
    }
}
