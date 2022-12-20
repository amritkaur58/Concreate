package com.dailyreporting.app.database;

import com.dailyreporting.app.models.WorkLog;
import com.orm.SugarRecord;

import java.util.List;

public class WorkLogsRepo {
    public static List<WorkLog> GetAll() {
        return WorkLog.listAll(WorkLog.class);
    }

    public static WorkLog Get(int id) {
        return WorkLog.findById(WorkLog.class, id);
    }

    public static void Save(WorkLog model) {
        model.save();
    }

    public static boolean Delete(int id) {
        WorkLog data = WorkLog.findById(WorkLog.class, id);
        return data.delete();
    }

    public static void DeleteAll() {
        SugarRecord.deleteAll(WorkLog.class);
    }
}
