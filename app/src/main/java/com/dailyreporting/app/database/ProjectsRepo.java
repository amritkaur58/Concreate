package com.dailyreporting.app.database;

import com.dailyreporting.app.models.Projects;
import com.orm.SugarRecord;

import java.util.List;

public class ProjectsRepo {
    public static List<Projects> GetAll() {
        return Projects.listAll(Projects.class);
    }

    public static Projects Get(int id) {
        return Projects.findById(Projects.class, id);
    }

    public static String Save(Projects model) {

        model.save();
        return "";
    }

    public static boolean Delete(int id) {
        Projects data = Projects.findById(Projects.class, id);
        return data.delete();
    }

    public static void DeleteAll() {
        SugarRecord.deleteAll(Projects.class);
    }
}
