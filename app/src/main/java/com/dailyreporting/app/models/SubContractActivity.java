package com.dailyreporting.app.models;

import com.orm.SugarRecord;

public class SubContractActivity extends SugarRecord {
    public String activityname;
    public String code;
    public String location;
    public int ActivityId;
    public int WorkLogId;
    public int FileId;
    public int NoOfWorkers;
    public String imagepath;
    public String Note;
    public String IsCompleted;
    public String IsCompliant;
    public String ArrivalTime;
    public String DepartureTime;
    public int LocationId = 0;
    public String WORK_LOG_DATE;
    public String AddedBy;
    public String LastModBy;
    public String AddedOn;
    public String LastModOn;
    public String contractorName;
    public String parentName;
    public String parentCode;
    public int parentId;
    public String projectID;
    public String activityCode;
    public String itemGuId;

}
