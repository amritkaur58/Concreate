package com.dailyreporting.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dailyreporting.app.R;
import com.dailyreporting.app.models.Projects;

import java.util.ArrayList;
import java.util.List;

public class SavedProjectAdapter extends RecyclerView.Adapter<SavedProjectAdapter.ViewHolder> {
    private final Context context;
    private final InterfaceList interfaceList;
    private List<Projects> listProject=new ArrayList<>();
    private boolean fromSetting;

    public SavedProjectAdapter(Context context, List<Projects> listProject, boolean fromSetting) {
        this.context = context;
        this.listProject = listProject;
        this.fromSetting = fromSetting;
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

        if(fromSetting)
        {
            if(listProject.get(i).projectSelected==1)
            {
                holder.projectCB.setChecked(true);
                holder.projectCB.setVisibility(View.VISIBLE);
            }

        }
        if(!listProject.get(i).name.isEmpty())
        {
            String cardNo = listProject.get(i).name;
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
        void checkItemTagClick(Projects list, int position);

    }
}
