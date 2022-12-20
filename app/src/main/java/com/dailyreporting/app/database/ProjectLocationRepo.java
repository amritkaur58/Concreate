package com.dailyreporting.app.database;

import com.dailyreporting.app.models.ActivityModel;
import com.dailyreporting.app.models.ProjectLocation;
import com.dailyreporting.app.utils.Constants;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

public class ProjectLocationRepo {
    public static List<ProjectLocation> GetAll() {
        return ProjectLocation.listAll(ProjectLocation.class);
    }

    public static ProjectLocation Get(int id) {
        return ProjectLocation.findById(ProjectLocation.class, id);
    }

    public static String Save(ProjectLocation model) {

        model.save();
        return "";
    }

    public static boolean Delete(int id) {
        ProjectLocation data = ProjectLocation.findById(ProjectLocation.class, id);
        return data.delete();
    }

    public static void DeleteAll() {
        SugarRecord.deleteAll(ProjectLocation.class);
    }

    public static List<ProjectLocation> GetLocalByProject(int id)
    {
        List<ProjectLocation> list = new ArrayList<>();
        list = ProjectLocation.findWithQuery(ProjectLocation.class,"SELECT * FROM "+ Constants.PROJECT_LOCATION + "  WHERE project_Id = ?",String.valueOf(id));
        return list;
    }
}
