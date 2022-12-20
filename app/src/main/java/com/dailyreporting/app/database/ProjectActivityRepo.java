package com.dailyreporting.app.database;

import com.dailyreporting.app.models.CorrectionCode;
import com.dailyreporting.app.models.ProjectActivityType;
import com.dailyreporting.app.models.Projects;
import com.dailyreporting.app.utils.Constants;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

public class ProjectActivityRepo {
    public static List<ProjectActivityType> GetAll() {
        return ProjectActivityType.listAll(ProjectActivityType.class);
    }

    public static ProjectActivityType Get(int id) {
        return Projects.findById(ProjectActivityType.class, id);
    }

    public static List<ProjectActivityType> getCodeByParent(String id,String projectID)
    {
        List<ProjectActivityType> list = new ArrayList<>();
        list = ProjectActivityType.findWithQuery(ProjectActivityType.class,"SELECT * FROM "+ Constants.PROJECT_ACTIVITY_TYPE + "  WHERE "+Constants.PARENT_ID+" = ? and  "+Constants.PROJECT_ID+" = ?",id,projectID);
        return list;
    }

    public static void updateViewActivity(String id,int showChild)
    {

         ProjectActivityType.findWithQuery(ProjectActivityType.class,"Update "+ Constants.PROJECT_ACTIVITY_TYPE +" Set "+Constants.SHOW_CHILD+" ="+showChild+ "  WHERE id"+" = ?",id);

    }

    public static String Save(ProjectActivityType model) {

        model.save();
        return "";
    }

    public static boolean Delete(int id) {
        ProjectActivityType data = ProjectActivityType.findById(ProjectActivityType.class, id);
        return data.delete();
    }

    public static void DeleteAll() {
        SugarRecord.deleteAll(ProjectActivityType.class);
    }
}
