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
import com.dailyreporting.app.activities.AddSubContractorActivity;
import com.dailyreporting.app.models.ProjectActivityType;

import java.io.File;
import java.util.List;

public class SubContractorImageAdapter extends RecyclerView.Adapter<SubContractorImageAdapter.ViewHolder> {
    private final Context context;
    private final List<String> listSubContractor;
    private final InterfaceList interfaceList;

    public SubContractorImageAdapter(Context context, List<String> listAddActivity) {
        this.context = context;
        this.listSubContractor = listAddActivity;
        interfaceList = (InterfaceList) this.context;
    }

    @NonNull
    @Override
    public SubContractorImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_video, parent, false);
        return new SubContractorImageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubContractorImageAdapter.ViewHolder holder, int position) {

        if (listSubContractor.get(position).equalsIgnoreCase("add")) {
            holder.rlAdd.setVisibility(View.VISIBLE);
            holder.rlCross.setVisibility(View.GONE);

        } else {
            holder.rlAdd.setVisibility(View.GONE);
            holder.rlCross.setVisibility(View.VISIBLE);
            holder.imgCross.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(Uri.fromFile(new File(listSubContractor.get(position))))
                    .apply(new RequestOptions().override(100, 100))
                    .into(holder.imgCross);
        }

        holder.rlAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AddSubContractorActivity) context).getImage();
            }
        });
        holder.deleteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listSubContractor.remove(position);
                //listSubContractor.add("add");
                notifyDataSetChanged();
                interfaceList.deleteItem(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listSubContractor.size();
    }

    public interface InterfaceList {
        void deleteItem( int position);
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
