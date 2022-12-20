package com.dailyreporting.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.TypedValue;
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
import com.dailyreporting.app.activities.AddActivitity;
import com.dailyreporting.app.database.ActivitiesRepo;
import com.dailyreporting.app.database.FilesRepo;
import com.dailyreporting.app.models.ActivityModel;
import com.dailyreporting.app.models.FilesModel;
import com.dailyreporting.app.utils.CommonMethods;
import com.dailyreporting.app.utils.Constants;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.List;

import static com.dailyreporting.app.utils.Constants.MyPREFERENCES;

public class ActivitiesAdapter extends RecyclerView.Adapter<ActivitiesAdapter.ViewHolder> {
    private final Context context;
    private final List<ActivityModel> listAddActivity;

    public ActivitiesAdapter(Context context, List<ActivityModel> listAddActivity) {
        this.context = context;
        this.listAddActivity = listAddActivity;
    }

    @NonNull
    @Override
    public ActivitiesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_subcontractor, parent, false);
        return new ActivitiesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivitiesAdapter.ViewHolder holder, int position) {

        holder.txtActivityName.setText(listAddActivity.get(position).activityname);
        holder.txtDescription.setText(listAddActivity.get(position).note);
        Glide.with(context)
                .load(Uri.fromFile(new File(listAddActivity.get(position).imagepath)))
                .apply(new RequestOptions().override(100, 100))
                .into(holder.imgActivity);

        try {
            List<FilesModel> fileList = FilesRepo.Get(listAddActivity.get(position).itemGuId);
            if (fileList.size() > 0) {
                holder.txtFile.setText(fileList.size() + " file(s)");
            } else {
                holder.txtFile.setText("");
            }

        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
        holder.txtTime.setText(CommonMethods.getAddOnFromUTC(listAddActivity.get(position).AddedOn));
        holder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(context, AddActivitity.class);
                    intent.putExtra(Constants.TABLE_ID, listAddActivity.get(position).getId().intValue());
                    context.startActivity(intent);
                } catch (Exception e) {
                    Logger.e(e.getMessage());
                }


            }
        });

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int position = holder.getAdapterPosition();
                ActivitiesRepo.Delete(listAddActivity.get(position).getId().intValue());
                listAddActivity.remove(position);
                notifyItemRemoved(position);
                //  ((ListActivity)context).checkList(listAddActivity);

            }
        });
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        boolean settingFont = sharedPreferences.getBoolean(Constants.SETTING_FONT, false);
        if (settingFont) {
            float fontSize = sharedPreferences.getFloat(Constants.FONT_SIZE, 13f);
            holder.txtActivityName.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
            holder.txtDescription.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

        }
    }

    @Override
    public int getItemCount() {
        return listAddActivity.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtActivityName;
        private final TextView txtDescription;
        private final TextView txtFile;
        private final TextView txtTime;
        private final ImageView imgDelete;
        private final ImageView imgEdit;
        private final ImageView imgActivity;

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
