package com.dailyreporting.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dailyreporting.app.R;
import com.dailyreporting.app.database.ProjectActivityRepo;
import com.dailyreporting.app.models.ProjectActivityType;
import com.dailyreporting.app.models.Projects;

import java.util.ArrayList;
import java.util.List;

public class ShowProjectAdapter extends RecyclerView.Adapter<ShowProjectAdapter.ViewHolder> {
    private final Context context;
    private final InterfaceList interfaceList;
    private List<Projects> listProject = new ArrayList<>();
    private boolean fromParent;

    public ShowProjectAdapter(Context context, List<Projects> listProject, boolean fromParent) {
        this.context = context;
        this.listProject = listProject;
        this.fromParent = fromParent;
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

        if (!listProject.get(i).name.isEmpty()) {
            String cardNo = listProject.get(i).name;
            holder.cardNameTV.setText(cardNo);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    interfaceList.checkProjectClicked(listProject.get(i),i);

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
    void checkProjectClicked(Projects list, int position);

}
}
