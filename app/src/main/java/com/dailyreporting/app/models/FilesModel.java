package com.dailyreporting.app.models;


import com.orm.SugarRecord;

public class FilesModel extends SugarRecord {
    public int fileId;
    public String ItemGuId;
    public String FullPath;
    public String AddedBy;
    public String AddedOn;
    public int LastModBy;
    public String LastModOn;

    public String getItemGuId() {
        return ItemGuId;
    }

    public void setItemGuId(String itemGuId) {
        ItemGuId = itemGuId;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public String getFullPath() {
        return FullPath;
    }

    public void setFullPath(String fullPath) {
        FullPath = fullPath;
    }

    public String getAddedBy() {
        return AddedBy;
    }

    public void setAddedBy(String addedBy) {
        AddedBy = addedBy;
    }

    public String getAddedOn() {
        return AddedOn;
    }

    public void setAddedOn(String addedOn) {
        AddedOn = addedOn;
    }

    public int getLastModBy() {
        return LastModBy;
    }

    public void setLastModBy(int lastModBy) {
        LastModBy = lastModBy;
    }

    public String getLastModOn() {
        return LastModOn;
    }

    public void setLastModOn(String lastModOn) {
        LastModOn = lastModOn;
    }
}
