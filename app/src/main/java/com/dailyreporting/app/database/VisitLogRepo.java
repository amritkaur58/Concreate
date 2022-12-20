package com.dailyreporting.app.database;

import com.dailyreporting.app.models.Correction;
import com.dailyreporting.app.models.SubContractActivity;
import com.dailyreporting.app.models.VisitLog;
import com.dailyreporting.app.utils.Constants;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

public class VisitLogRepo {
    public static List<VisitLog> GetAll() {
        return VisitLog.listAll(VisitLog.class);
    }

    public static VisitLog Get(int id) {
        return VisitLog.findById(VisitLog.class, id);
    }

    public static String Save(VisitLog model) {
         if(model.VisitReason.isEmpty())
            return "Please enter Visit reason";
        model.save();

   /*     else if(model.Note.isEmpty())
            return "Please enter Note";*/
        return "";
    }

    public static boolean Delete(int id) {
        VisitLog data = VisitLog.findById(VisitLog.class, id);
        return data.delete();
    }

    public static void DeleteAll() {
        SugarRecord.deleteAll(VisitLog.class);
    }

    public static List<VisitLog> GetVisitLog(int id)
    {
        List<VisitLog> list = new ArrayList<>();
        list = VisitLog.findWithQuery(VisitLog.class,"SELECT * FROM "+ Constants.VISIT_LOG + "  WHERE project_Id = ?",String.valueOf(id));
        return list;
    }


    public static List<VisitLog> GetVisitByProjectDate(int id, String date)
    {
        List<VisitLog> list = new ArrayList<>();
        list = VisitLog.findWithQuery(VisitLog.class,"SELECT * FROM "+ Constants.VISIT_LOG + "  WHERE project_Id = ? and  "+Constants.WoRKLOG_DATE+" = ?",String.valueOf(id),date);
        return list;
    }

}
