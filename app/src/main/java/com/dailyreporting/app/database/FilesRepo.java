package com.dailyreporting.app.database;


import com.dailyreporting.app.models.ActivityModel;
import com.dailyreporting.app.models.CorrectionCode;
import com.dailyreporting.app.models.FilesModel;
import com.dailyreporting.app.utils.Constants;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

public class FilesRepo {
    public static FilesModel filesModel;

    public static List<FilesModel> GetAll() {
        return FilesModel.listAll(FilesModel.class);
    }

    public static List<FilesModel> Get(String fileId) {
        List<FilesModel> value = FilesModel.find(FilesModel.class, Constants.ItemGuid + "= ?", fileId);
        return value;
    }

    public static String Save(FilesModel model) {
        if (model.FullPath.isEmpty())
            return "Please Select Image";
        model.save();
        return "";
    }

    public static boolean Delete(int id) {
        filesModel = ActivityModel.findById(FilesModel.class, id);
        return filesModel.delete();
    }

    public static void DeleteAll() {
        SugarRecord.deleteAll(FilesModel.class);
    }

    public static void DeleteFileByProject(int id) {
        CorrectionCode.executeQuery("DELETE FROM " + Constants.FILE_MODEL + " WHERE FILE_ID  = '" + id + "'");

    }

    public static void DeleteFileByUid(String id) {
        CorrectionCode.executeQuery("DELETE FROM " + Constants.FILE_MODEL + " WHERE ITEM_GU_ID  = '" + id + "'");

    }

    public static List<ActivityModel> GetActivityByProjectDate(int id, String date) {
        List<ActivityModel> list = new ArrayList<>();
        list = ActivityModel.findWithQuery(ActivityModel.class, "SELECT * FROM " + Constants.ACTIVITY_MODEL + "  WHERE project_Id = ? and  " + Constants.WoRKLOG_DATE + " = ?", String.valueOf(id), date);
        return list;
    }


    public static List<ActivityModel> GetActivityByProject(int id) {
        List<ActivityModel> list = new ArrayList<>();
        list = ActivityModel.findWithQuery(ActivityModel.class, "SELECT * FROM " + Constants.ACTIVITY_MODEL + "  WHERE project_Id = ?", String.valueOf(id));
        return list;
    }

}


