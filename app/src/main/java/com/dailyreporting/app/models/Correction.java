package com.dailyreporting.app.models;

import com.orm.SugarRecord;

public class Correction extends SugarRecord {
    public int WorkLogId;
    public String VisitReason;
    public int LocationId;
    public int CodeId;
    public String VisualInspectionDone;
    public String Description;
    public String IsCompliantAfterCorrection;
    public String PersonDoingQualityControl;
    public String AddedBy;
    public String LastModBy;
    public String LastModOn;
    public String AddedOn;
    public String imagepath;
    public String projectId;
    public String WORK_LOG_DATE;
    public String itemGuId;
}
