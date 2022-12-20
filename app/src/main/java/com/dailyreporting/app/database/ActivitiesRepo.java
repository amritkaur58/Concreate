package com.dailyreporting.app.database;


import android.util.Log;

import com.dailyreporting.app.models.ActivityModel;
import com.dailyreporting.app.models.CorrectionCode;
import com.dailyreporting.app.utils.Constants;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

public class ActivitiesRepo {
   public static ActivityModel addActivityModel;
    public static List<ActivityModel> GetAll() {
        return ActivityModel.listAll(ActivityModel.class);
    }

    public static ActivityModel Get(int activityId) {
        return ActivityModel.findById(ActivityModel.class, activityId);
    }


    public static String Save(ActivityModel model) {
        if(model.activityname.isEmpty())
        return "Please Select Activity Name";
        model.save();
        return "";
    }

    public static boolean Delete(int id) {
         addActivityModel = ActivityModel.findById(ActivityModel.class, id);
        return addActivityModel.delete();
    }

    public static void DeleteAll() {
        SugarRecord.deleteAll(ActivityModel.class);

    }


    public static void DeleteActivityByProject(int id)
    {
        CorrectionCode.executeQuery("DELETE FROM "+ Constants.ACTIVITY_MODEL + " WHERE PROJECT_ID = " + id + "");

    }

    public static List<ActivityModel> GetActivityByProjectDate(int id,String date)
    {
        List<ActivityModel> list = new ArrayList<>();
        list = ActivityModel.findWithQuery(ActivityModel.class,"SELECT * FROM "+ Constants.ACTIVITY_MODEL + "  WHERE project_Id = ? and  "+Constants.WoRKLOG_DATE+" = ?",String.valueOf(id),date);
        return list;
    }

    public static List<ActivityModel> GetActivityByProject(int id)
    {
        List<ActivityModel> list = new ArrayList<>();
        list = ActivityModel.findWithQuery(ActivityModel.class,"SELECT * FROM "+ Constants.ACTIVITY_MODEL + "  WHERE project_Id = ?",String.valueOf(id));
        return list;
    }

}


