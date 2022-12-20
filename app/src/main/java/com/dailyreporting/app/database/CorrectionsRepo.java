package com.dailyreporting.app.database;

import com.dailyreporting.app.models.Correction;
import com.dailyreporting.app.utils.Constants;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

public class CorrectionsRepo {
    public static List<Correction> GetAll() {
        return Correction.listAll(Correction.class);
    }

    public static Correction Get(int id) {
        return Correction.findById(Correction.class, id);
    }

    public static String Save(Correction model) {
        if (model.CodeId == 0)
            return "Please Select Code";
        else if (model.LocationId == 0)
            return "Please Select Location";

        model.save();
        return "";
    }

    public static boolean Delete(int id) {
        Correction data = Correction.findById(Correction.class, id);
        return data.delete();
    }

    public static void DeleteAll() {
        SugarRecord.deleteAll(Correction.class);
    }


    public static List<Correction> GetCorrectionByParent(int id) {
        List<Correction> list = new ArrayList<>();
        list = Correction.findWithQuery(Correction.class, "SELECT * FROM " + Constants.CORRECTION + "  WHERE project_Id = ?", String.valueOf(id));
        return list;
    }

    public static List<Correction> GetCorrectionByProjectDate(int id, String date) {
        List<Correction> list = new ArrayList<>();
        list = Correction.findWithQuery(Correction.class, "SELECT * FROM " + Constants.CORRECTION + "  WHERE project_Id = ? and  " + Constants.WoRKLOG_DATE + " = ?", String.valueOf(id), date);
        return list;
    }

}
