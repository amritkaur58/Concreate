package com.dailyreporting.app.models;

import com.orm.SugarRecord;

public class VisitLog extends SugarRecord {
    public int WorkLogId;
    public String VisitReason;
    public int LocationId;
    public int VisitID;
    public String PersonName;
    public String CompanyName;
    public String Title;
    public String Note;
    public String ArrivalTime;
    public String DepartureTime;
    public int AddedBy;
    public String LastModOn;
    public String AddedOn;
    public String projectId;
    public String WORK_LOG_DATE;

}
