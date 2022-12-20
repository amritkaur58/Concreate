package com.dailyreporting.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dailyreporting.app.R;
import com.dailyreporting.app.models.SubContractActivity;

import java.util.ArrayList;
import java.util.List;

public class SelectionAdapter extends RecyclerView.Adapter<SelectionAdapter.ViewHolder> {
    private final InterfaceList interfaceList;
    private Context context;
    private List<SubContractActivity> list = new ArrayList<>();
    private boolean fromComplaint;

    public SelectionAdapter(Context context, List<SubContractActivity> list, boolean fromComplaint) {
        this.context = context;
        this.list = list;
        this.fromComplaint = fromComplaint;
        interfaceList = (InterfaceList) this.context;
    }

    @NonNull
    @Override
    public SelectionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.saved_location_item, parent, false);
        return new SelectionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectionAdapter.ViewHolder holder, int position) {

        holder.cardNameTV.setText(list.get(position).parentName);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interfaceList.itemClickListener(list.get(position),position,fromComplaint);
            }
        });

    }

    public interface InterfaceList {
        void itemClickListener(SubContractActivity list, int position, boolean fromComplaint);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView cardNameTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardNameTV = itemView.findViewById(R.id.cardNameTV);

        }
    }
}
