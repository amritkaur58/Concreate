package com.dailyreporting.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dailyreporting.app.R;
import com.dailyreporting.app.models.ProjectLocation;

import java.util.List;

public class LocationArrayAdapter extends ArrayAdapter<ProjectLocation> {
    private Context context;
    private List<ProjectLocation> listAddActivity;

    public LocationArrayAdapter(@NonNull Context context, int resource,List<ProjectLocation> listAddActivity) {
        super(context, resource,listAddActivity);
        this.listAddActivity = listAddActivity;
        this.context = context;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            v = inflater.inflate(R.layout.saved_location_item, null);
        }
        TextView lbl = (TextView) v.findViewById(R.id.cardNameTV);
        lbl.setText(listAddActivity.get(position).name);

        return v;
    }

    @Override
    public ProjectLocation getItem(int position) {
        return listAddActivity.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            v = inflater.inflate(R.layout.saved_location_item, null);
        }
        TextView lbl =  v.findViewById(R.id.cardNameTV);
        lbl.setText(listAddActivity.get(position).name);

        return v;
    }

    @Override
    public int getCount() {
        return listAddActivity.size();
    }

    public List<ProjectLocation> getItems() {
        return listAddActivity;
    }
}
