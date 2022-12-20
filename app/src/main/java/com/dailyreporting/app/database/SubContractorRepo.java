package com.dailyreporting.app.database;

import com.dailyreporting.app.models.ActivityModel;
import com.dailyreporting.app.models.Correction;
import com.dailyreporting.app.models.CorrectionCode;
import com.dailyreporting.app.models.SubContractActivity;
import com.dailyreporting.app.utils.Constants;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

public class SubContractorRepo {
    public static List<SubContractActivity> GetAll() {
        return SubContractActivity.listAll(SubContractActivity.class);
    }

    public static SubContractActivity Get(int activityId) {
        return SubContractActivity.findById(SubContractActivity.class, activityId);
    }

    public static String Save(SubContractActivity model) {
        if(model.ActivityId==0)
            return "Please Select Activity Name";
        else if(model.contractorName.isEmpty())
            return "Please enter Subcontractor Name";
        model.save();
        return "";
    }

    public static boolean Delete(int id) {
        SubContractActivity subContractModel = SubContractActivity.findById(SubContractActivity.class, id);
        return subContractModel.delete();
    }

    public static void DeleteAll() {
        SugarRecord.deleteAll(SubContractActivity.class);
    }

    public static List<SubContractActivity> GetSubContractByProject(int id)
    {
        List<SubContractActivity> list = new ArrayList<>();
        list = SubContractActivity.findWithQuery(SubContractActivity.class,"SELECT * FROM "+ Constants.SUB_CONTRACT_ACTIVITY + "  WHERE project_Id = ?",String.valueOf(id));
        return list;
    }

    public static List<SubContractActivity> GetContractByProjectDate(int id, String date)
    {
        List<SubContractActivity> list = new ArrayList<>();
        list = SubContractActivity.findWithQuery(SubContractActivity.class,"SELECT * FROM "+ Constants.SUB_CONTRACT_ACTIVITY + "  WHERE project_Id = ? and  "+Constants.WoRKLOG_DATE+" = ?",String.valueOf(id),date);
        return list;
    }

}
