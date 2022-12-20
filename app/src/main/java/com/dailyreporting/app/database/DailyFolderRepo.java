package com.dailyreporting.app.database;

import android.util.Log;

import com.dailyreporting.app.models.Correction;
import com.dailyreporting.app.models.CorrectionCode;
import com.dailyreporting.app.models.DailyFolder;
import com.dailyreporting.app.models.VisitLog;
import com.dailyreporting.app.utils.Constants;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

public class DailyFolderRepo {
    public static List<DailyFolder> GetAll() {
        return DailyFolder.listAll(DailyFolder.class);
    }

    public static DailyFolder Get(int id) {
        return DailyFolder.findById(DailyFolder.class, id);
    }

    public static String Save(DailyFolder model) {
        model.save();
        return "";
    }

    public static boolean Delete(int id) {
        DailyFolder data = DailyFolder.findById(DailyFolder.class, id);
        return data.delete();
    }

    public static List<DailyFolder> GetDailyFolder(int id)
    {
        List<DailyFolder> list = new ArrayList<>();
        list = DailyFolder.findWithQuery(DailyFolder.class,"SELECT * FROM "+ Constants.DAILY_FOLDER + "  WHERE project_Id = ?",String.valueOf(id));
        return list;
    }


    public static List<DailyFolder> GetDailyByProjectDate(int id, String date)
    {
        List<DailyFolder> list = new ArrayList<>();
        list = DailyFolder.findWithQuery(DailyFolder.class,"SELECT * FROM "+ Constants.DAILY_FOLDER + "  WHERE project_Id = ? and  "+Constants.WoRKLOG_DATE+" = ?",String.valueOf(id),date);
        return list;
    }

    public static void DeleteAll() {
        SugarRecord.deleteAll(DailyFolder.class);
    }

    public static void DeleteFolderByProject(String id)
    {
        CorrectionCode.executeQuery("DELETE FROM "+ Constants.DAILY_FOLDER + "  WHERE "+Constants.WoRKLOG_DATE +"= "+ id + "");
    }

}
