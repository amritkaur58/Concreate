package com.dailyreporting.app.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dailyreporting.app.R;
import com.dailyreporting.app.activities.AddActivitity;

import java.io.File;
import java.util.List;

public class AddAdapter extends RecyclerView.Adapter<AddAdapter.ViewHolder> {
    private final Context context;
    private final List<String> listAddActivity;
    private final InterfaceList interfaceList;

    public AddAdapter(Context context, List<String> listAddActivity) {
        this.context = context;
        this.listAddActivity = listAddActivity;
        interfaceList = (InterfaceList) this.context;
    }

    @NonNull
    @Override
    public AddAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_video, parent, false);
        return new AddAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (listAddActivity.get(position).equalsIgnoreCase("add")) {
            holder.rlAdd.setVisibility(View.VISIBLE);
            holder.rlCross.setVisibility(View.GONE);

        } else {
            holder.rlAdd.setVisibility(View.GONE);
            holder.rlCross.setVisibility(View.VISIBLE);
            holder.imgCross.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(Uri.fromFile(new File(listAddActivity.get(position))))
                    .apply(new RequestOptions().override(100, 100))
                    .into(holder.imgCross);
        }

        holder.rlAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AddActivitity) context).getImage();

            }
        });

        holder.deleteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listAddActivity.remove(position);
                //  listAddActivity.add("add");
                notifyDataSetChanged();
                interfaceList.deleteItem(position);
            }
        });
    }

    public interface InterfaceList {
        void deleteItem( int position);
    }

    @Override
    public int getItemCount() {
        return listAddActivity.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rlAdd, rlCross;
        ImageView imgCross, deleteIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rlCross = itemView.findViewById(R.id.rlCross);
            rlAdd = itemView.findViewById(R.id.rlAdd);
            imgCross = itemView.findViewById(R.id.imgCross);
            deleteIV = itemView.findViewById(R.id.deleteIV);
        }
    }

}
