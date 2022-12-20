package com.dailyreporting.app.models;


import com.orm.SugarRecord;

public class ActivityModel extends SugarRecord {
    public String activityname;
    public String activityCode;
    public int WorkLogId;
    public int ActivityId;
    public int LocationId;
    public double Latitude;
    public double Longitude;
    public String IsCompleted = "true";
    public String IsCompliant = "true";
    public String AddedBy;
    public String LastModBy;
    public String AddedOn;
    public String WORK_LOG_DATE;
    public String LastModOn;

    public String code;
    public String location;
    public int userid;
    public String imagepath;
    public String note;
    public String taskcompleted;
    public String complaint;

    public String projectID;

    public String itemGuId;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getActivityname() {
        return activityname;
    }

    public void setActivityname(String activityname) {
        this.activityname = activityname;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTaskcompleted() {
        return taskcompleted;
    }

    public void setTaskcompleted(String taskcompleted) {
        this.taskcompleted = taskcompleted;
    }

    public String getComplaint() {
        return complaint;
    }

    public void setComplaint(String complaint) {
        this.complaint = complaint;
    }

}
