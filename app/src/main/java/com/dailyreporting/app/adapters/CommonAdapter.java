package com.dailyreporting.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dailyreporting.app.R;
import com.dailyreporting.app.models.CommonModel;
import com.dailyreporting.app.models.Projects;

import java.util.ArrayList;
import java.util.List;

public class CommonAdapter extends RecyclerView.Adapter<CommonAdapter.ViewHolder> {
    private final Context context;
    private final InterfaceList interfaceList;
    private List<CommonModel> listProject=new ArrayList<>();
    public CommonAdapter(Context context, List<CommonModel> listProject) {
        this.context = context;
        this.listProject = listProject;

        interfaceList = (InterfaceList) this.context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.saved_project_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {


        if(!listProject.get(i).getFontSize().isEmpty())
        {
            String cardNo = listProject.get(i).getFontSize();
            holder.cardNameTV.setText(cardNo);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interfaceList.checkItemTagClick(listProject.get(i),i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listProject.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView cardNameTV;


        CheckBox projectCB;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardNameTV = itemView.findViewById(R.id.cardNameTV);
            projectCB = itemView.findViewById(R.id.projectCB);

        }
    }
    public interface InterfaceList {
        void checkItemTagClick(CommonModel list, int position);

    }
}
