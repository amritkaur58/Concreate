package com.dailyreporting.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dailyreporting.app.R;
import com.dailyreporting.app.database.ProjectActivityRepo;
import com.dailyreporting.app.models.ProjectActivityType;

import java.util.ArrayList;
import java.util.List;

public class ShowCommonActivityAdapter extends RecyclerView.Adapter<ShowCommonActivityAdapter.ViewHolder> {
    private final Context context;
    private final InterfaceList interfaceList;
    private List<ProjectActivityType> listProject = new ArrayList<>();

    public ShowCommonActivityAdapter(Context context, List<ProjectActivityType> listProject) {
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
        holder.childRV.setVisibility(View.GONE);
        final boolean[] isOpen = {false};
        if (!listProject.get(i).name.isEmpty()) {
            String cardNo = listProject.get(i).name;
            String code = listProject.get(i).activityCode;
            holder.cardNameTV.setText(code +"-"+cardNo);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String activityId = String.valueOf(listProject.get(i).activityId);
                List<ProjectActivityType> activityCodeList = ProjectActivityRepo.getCodeByParent(activityId,listProject.get(i).projectId);
                if (activityCodeList.size() > 0) {
                    // ProjectActivityRepo.updateViewActivity(String.valueOf(listProject.get(i).getId()),1);

                    holder.childRV.setLayoutManager(new LinearLayoutManager(context));
                    holder.childRV.setAdapter(new ShowCommonActivityAdapter(context, activityCodeList));

                        if(isOpen[0])
                        {
                            isOpen[0] = false;
                            holder.childRV.setVisibility(View.GONE);
                        }
                        else
                        {
                            isOpen[0] = true;
                            holder.childRV.setVisibility(View.VISIBLE);
                        }



              /*      Context cxt= context;

                    RecyclerView recyclerView = new RecyclerView(cxt);
                    LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    int padding= (int) cxt.getResources().getDimension(R.dimen.elevation_header);
                    layoutParams.setMargins(padding,padding,padding,padding);
                    recyclerView.setLayoutParams(layoutParams);

                    recyclerView.setPadding(padding,padding,padding,padding);

                    RelativeLayout linearLayout=(RelativeLayout)holder.itemView;
                    linearLayout.addView(recyclerView);

*/
                }else
                {
                    interfaceList.checkInnerClick(listProject.get(i), i);
                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return listProject.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView cardNameTV;
        RecyclerView childRV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardNameTV = itemView.findViewById(R.id.cardNameTV);
            childRV = itemView.findViewById(R.id.childRV);

        }
    }

    public interface InterfaceList {
        void checkInnerClick(ProjectActivityType list, int position);
    }
}
