package com.dailyreporting.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dailyreporting.app.R;
import com.dailyreporting.app.models.CorrectionCode;

import java.util.List;

public class CorrectionCodeAdapter extends ArrayAdapter<CorrectionCode> {
    private Context context;
    private List<CorrectionCode> listAddActivity;

    public CorrectionCodeAdapter(@NonNull Context context, int resource, List<CorrectionCode> listAddActivity) {
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
        lbl.setText(listAddActivity.get(position).code);

        return v;
    }

    @Override
    public CorrectionCode getItem(int position) {
        return listAddActivity.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            v = inflater.inflate(R.layout.saved_location_item, null);
        }
        TextView lbl = (TextView) v.findViewById(R.id.cardNameTV);
        lbl.setText(listAddActivity.get(position).code);

        return v;
    }

    @Override
    public int getCount() {
        return listAddActivity.size();
    }

    public List<CorrectionCode> getItems() {
        return listAddActivity;
    }
}
