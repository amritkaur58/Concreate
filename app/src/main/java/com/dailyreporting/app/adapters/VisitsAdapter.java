package com.dailyreporting.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dailyreporting.app.R;
import com.dailyreporting.app.activities.SaveVisitLog;
import com.dailyreporting.app.database.VisitLogRepo;
import com.dailyreporting.app.models.VisitLog;
import com.dailyreporting.app.utils.CommonMethods;
import com.dailyreporting.app.utils.Constants;

import java.util.List;

import static com.dailyreporting.app.utils.Constants.MyPREFERENCES;

public class VisitsAdapter extends RecyclerView.Adapter<VisitsAdapter.ViewHolder> {
    private final Context context;
    private final List<VisitLog> listVisitLog;

    public VisitsAdapter(Context context, List<VisitLog> listVisitLog) {
        this.context = context;
        this.listVisitLog = listVisitLog;
    }

    @NonNull
    @Override
    public VisitsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_subcontractor, parent, false);
        return new VisitsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VisitsAdapter.ViewHolder holder, int position) {
        holder.txtActivityName.setText(listVisitLog.get(position).Title);
        holder.txtDescription.setText(listVisitLog.get(position).VisitReason);
        try
        {
            holder.txtTime.setText(CommonMethods.getAddOnFromUTC(listVisitLog.get(position).AddedOn));
        }catch (Exception e)
        {
            e.printStackTrace();
        }
     /*   Glide.with(context)
                .load(Uri.fromFile(new File(listVisitLog.get(position).imagepath)))
                .apply(new RequestOptions().override(100, 100))
                .into(holder.imgActivity);*/
        holder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SaveVisitLog.class);
                intent.putExtra(Constants.TABLE_ID,listVisitLog.get(position).getId().intValue() );
                context.startActivity(intent);

            }
        });

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int position = holder.getAdapterPosition();
                VisitLogRepo.Delete(listVisitLog.get(position).getId().intValue());
                listVisitLog.remove(position);
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
        return listVisitLog.size();
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
