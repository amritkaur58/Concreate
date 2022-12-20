package com.dailyreporting.app.WebApis;

import com.dailyreporting.app.database.ProjectsRepo;
import com.dailyreporting.app.models.Projects;
import com.dailyreporting.app.models.ProjectModel;

import java.util.ArrayList;
import java.util.List;

public class ProjectServices {

    private static Projects projects;
    private static List<Projects> list = new ArrayList<>();

    public static List<Projects> showProjects(List<ProjectModel.Items> items)
    {
        deleteProjects();
        saveProjects(items);
        list = getProjects();
        return list;
    }

    public static void deleteProjects()
    {
        ProjectsRepo.DeleteAll();
    }


    public static List<Projects> getProjects()
    {
        List<Projects> projectData = ProjectsRepo.GetAll();
        return projectData;
    }

   public static void saveProjects(List<ProjectModel.Items> items)
    {
        for(int i=0; i<items.size();i++)
        {

            ProjectModel.Items responseData = items.get(i);
            projects = new Projects();
            projects.name = responseData.getName();
            projects.logoFileId = responseData.getLogoFileId();
            projects.addedBy = responseData.getAddedBy();
            projects.addedOn = responseData.getAddedOn();
            projects.companyName = responseData.getCompanyName();
            projects.idValue = responseData.getId();
            projects.lastModBy = responseData.getLastModBy();
            projects.lastModOn = responseData.getLastModOn();
            projects.projectSelected = 0;
            String res = ProjectsRepo.Save(projects);
        }
    }
}
