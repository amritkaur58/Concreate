package com.dailyreporting.app.models;

import com.orm.SugarRecord;

import java.util.Date;

public class WorkLog extends SugarRecord {
    public String Title;
    public double Temperature;
    public String Weather;
    public int AddedBy;
    public Date LastModOn;
    public Date AddedOn;


}
