package com.dailyreporting.app.database;

import android.util.Log;

import com.dailyreporting.app.models.ActivityModel;
import com.dailyreporting.app.models.CorrectionCode;
import com.dailyreporting.app.models.Projects;
import com.dailyreporting.app.utils.Constants;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

public class CorrectionsCodeRepo {
    public static List<CorrectionCode> GetAll() {
        return CorrectionCode.listAll(CorrectionCode.class);
    }

    public static CorrectionCode Get(int id) {
        return CorrectionCode.findById(CorrectionCode.class, id);
    }

    public static void Save(CorrectionCode model) {
        model.save();
    }

    public static boolean Delete(int id) {
        CorrectionCode data = CorrectionCode.findById(CorrectionCode.class, id);
        return data.delete();
    }

    public static void DeleteAll() {
        SugarRecord.deleteAll(CorrectionCode.class);
    }

    public static void DeleteCodeByProject(int id)
    {
        CorrectionCode.executeQuery("DELETE FROM "+ Constants.CORRECTION_CODE + " WHERE PROJECT_ID = " + id + "");
        Log.e("deleted","dahs");
    }


    public static List<CorrectionCode> GetCodeById(int id)
    {
        List<CorrectionCode> list = new ArrayList<>();
        list = CorrectionCode.findWithQuery(CorrectionCode.class,"SELECT * FROM "+ Constants.CORRECTION_CODE + "  WHERE project_Id = ?",String.valueOf(id));
        return list;
    }





}
