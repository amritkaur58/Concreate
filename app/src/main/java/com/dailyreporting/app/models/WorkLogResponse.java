package com.dailyreporting.app.models;


import java.util.List;

public class WorkLogResponse  {
    private Data data;

    private String message;


    private String status;

    public Data getData ()
    {
        return data;
    }

    public void setData (Data data)
    {
        this.data = data;
    }

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }


    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public class Data
    {
        private String lastModBy;

        private String name;

        private String parentId;

        private String activityCode;

        private String projectId;

        private String locationId;

        private String addedBy;

        private String temperature;

        private String weather;

        private String id;

        private String lastModOn;

        private String title;

        private String addedOn;

        public String getLastModBy ()
        {
            return lastModBy;
        }

        public void setLastModBy (String lastModBy)
        {
            this.lastModBy = lastModBy;
        }

        public String getLocationId ()
        {
            return locationId;
        }

        public void setLocationId (String locationId)
        {
            this.locationId = locationId;
        }

        public String getAddedBy ()
        {
            return addedBy;
        }

        public void setAddedBy (String addedBy)
        {
            this.addedBy = addedBy;
        }

        public String getTemperature ()
        {
            return temperature;
        }

        public void setTemperature (String temperature)
        {
            this.temperature = temperature;
        }

        public String getWeather ()
        {
            return weather;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getActivityCode() {
            return activityCode;
        }

        public void setActivityCode(String activityCode) {
            this.activityCode = activityCode;
        }

        public String getProjectId() {
            return projectId;
        }

        public void setProjectId(String projectId) {
            this.projectId = projectId;
        }

        public void setWeather (String weather)
        {
            this.weather = weather;
        }

        public String getId ()
        {
            return id;
        }

        public void setId (String id)
        {
            this.id = id;
        }

        public String getLastModOn ()
        {
            return lastModOn;
        }

        public void setLastModOn (String lastModOn)
        {
            this.lastModOn = lastModOn;
        }

        public String getTitle ()
        {
            return title;
        }

        public void setTitle (String title)
        {
            this.title = title;
        }

        public String getAddedOn ()
        {
            return addedOn;
        }

        public void setAddedOn (String addedOn)
        {
            this.addedOn = addedOn;
        }


    }
}
