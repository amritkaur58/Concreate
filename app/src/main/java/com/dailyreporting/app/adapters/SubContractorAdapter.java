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
import com.dailyreporting.app.activities.AddSubContractorActivity;
import com.dailyreporting.app.database.FilesRepo;
import com.dailyreporting.app.database.SubContractorRepo;
import com.dailyreporting.app.models.FilesModel;
import com.dailyreporting.app.models.SubContractActivity;
import com.dailyreporting.app.utils.CommonMethods;
import com.dailyreporting.app.utils.Constants;

import java.io.File;
import java.util.List;

import static com.dailyreporting.app.utils.Constants.MyPREFERENCES;

public class SubContractorAdapter extends RecyclerView.Adapter<SubContractorAdapter.ViewHolder> {
    private final Context context;
    private final List<SubContractActivity> listSubContractor;

    public SubContractorAdapter(Context context, List<SubContractActivity> listSubContractor) {
        this.context = context;
        this.listSubContractor = listSubContractor;
    }

    @NonNull
    @Override
    public SubContractorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_subcontractor, parent, false);
        return new SubContractorAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubContractorAdapter.ViewHolder holder, int position) {
        holder.txtActivityName.setText(listSubContractor.get(position).activityname);
        holder.txtDescription.setText(listSubContractor.get(position).Note);
        Glide.with(context)
                .load(Uri.fromFile(new File(listSubContractor.get(position).imagepath)))
                .apply(new RequestOptions().override(100, 100))
                .into(holder.imgActivity);
        try {
            List<FilesModel> fileList = FilesRepo.Get(listSubContractor.get(position).itemGuId);
            if (fileList.size() > 0) {
                holder.txtFile.setText(fileList.size() + " file(s)");
            } else {
                holder.txtFile.setText("");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.txtTime.setText(CommonMethods.getAddOnFromUTC(listSubContractor.get(position).AddedOn));
        holder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddSubContractorActivity.class);
                intent.putExtra(Constants.TABLE_ID, listSubContractor.get(position).getId().intValue());
                context.startActivity(intent);

            }
        });

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int position = holder.getAdapterPosition();
                SubContractorRepo.Delete(listSubContractor.get(position).getId().intValue());
                listSubContractor.remove(position);
                notifyItemRemoved(position);

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
        return listSubContractor.size();
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
