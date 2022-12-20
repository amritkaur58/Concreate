package com.dailyreporting.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dailyreporting.app.R;
import com.dailyreporting.app.activities.SaveDailyFolderActivity;
import com.dailyreporting.app.database.DailyFolderRepo;
import com.dailyreporting.app.database.FilesRepo;
import com.dailyreporting.app.models.DailyFolder;
import com.dailyreporting.app.models.FilesModel;
import com.dailyreporting.app.utils.CommonMethods;
import com.dailyreporting.app.utils.Constants;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.List;

public class DailyFolderAdapter extends RecyclerView.Adapter<DailyFolderAdapter.ViewHolder> {
    private final Context context;
    private final List<DailyFolder> listVisitLog;
    private final boolean fromSelection;
    private final InterfaceList interfaceList;

    public DailyFolderAdapter(Context context, List<DailyFolder> listVisitLog, boolean fromSelection) {
        this.context = context;
        this.listVisitLog = listVisitLog;
        this.fromSelection = fromSelection;
        interfaceList = (InterfaceList) this.context;
    }

    @NonNull
    @Override
    public DailyFolderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_subcontractor, parent, false);
        return new DailyFolderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyFolderAdapter.ViewHolder holder, int position) {
        holder.txtDescription.setText(listVisitLog.get(position).note);
        holder.txtActivityName.setText(context.getString(R.string.dailyfolder));
        try {
            holder.txtTime.setText(CommonMethods.getAddOnFromUTC(listVisitLog.get(position).AddedOn));
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
        Glide.with(context)
                .load(Uri.fromFile(new File(listVisitLog.get(position).image)))
                .apply(new RequestOptions().override(100, 100))
                .into(holder.imgActivity);
        try {
            List<FilesModel> fileList = FilesRepo.Get(listVisitLog.get(position).itemGuId);
            if (fileList.size() > 0) {
                holder.txtFile.setText(fileList.size() + " file(s)");
            } else {
                holder.txtFile.setText("");
            }

        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
        holder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, SaveDailyFolderActivity.class);
                intent.putExtra(Constants.TABLE_ID, listVisitLog.get(position).getId().intValue());
                context.startActivity(intent);

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fromSelection)
                    interfaceList.checkItemClickListener(listVisitLog.get(position), position);
            }
        });

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int position = holder.getAdapterPosition();
                DailyFolderRepo.Delete(listVisitLog.get(position).getId().intValue());
                listVisitLog.remove(position);
                notifyItemRemoved(position);

            }
        });
    }

    @Override
    public int getItemCount() {
        return listVisitLog.size();
    }

    public interface InterfaceList {
        void checkItemClickListener(DailyFolder listVisitLog, int position);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtActivityName;
        private final TextView txtDescription;
        private final TextView txtFile;
        private final ImageView imgDelete;
        private final ImageView imgEdit;
        private final ImageView imgActivity;
        private final TextView txtTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgActivity = itemView.findViewById(R.id.imgActivity);
            imgEdit = itemView.findViewById(R.id.imgedit);
            imgDelete = itemView.findViewById(R.id.imgDelete);
            txtActivityName = itemView.findViewById(R.id.txtActivityName);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtFile = itemView.findViewById(R.id.txtFile);
            txtTime = itemView.findViewById(R.id.txtTime);
        }
    }
}
