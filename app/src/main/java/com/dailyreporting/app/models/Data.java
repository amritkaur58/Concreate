package com.dailyreporting.app.models;


import java.util.List;

public class Data  {
    private String totalItems;

    private String itemsPerPage;

    private String totalPages;

    private String currentPage;

    private List<Items> items;

    public String getTotalItems ()
    {
        return totalItems;
    }

    public void setTotalItems (String totalItems)
    {
        this.totalItems = totalItems;
    }

    public String getItemsPerPage ()
    {
        return itemsPerPage;
    }

    public void setItemsPerPage (String itemsPerPage)
    {
        this.itemsPerPage = itemsPerPage;
    }

    public String getTotalPages ()
    {
        return totalPages;
    }

    public void setTotalPages (String totalPages)
    {
        this.totalPages = totalPages;
    }

    public String getCurrentPage ()
    {
        return currentPage;
    }

    public void setCurrentPage (String currentPage)
    {
        this.currentPage = currentPage;
    }

    public List<Items> getItems ()
    {
        return items;
    }

    public void setItems (List<Items> items)
    {
        this.items = items;
    }


    public class Items
    {
        private String submittedBy;

        private String correctionsCount;

        private String lastName;

        private String visitLogsCount;

        private String addedBy;

        private String addedOnValue;

        private String project;

        private String fullFileUrl;

        private String title;

        private String addedOn;

        private String activityCount;

        private String firstName;

        private String subContractorActivityCount;

        private String cityName;

        private String projectCode;

        private String temperature;

        private String weather;

        private String location;

        private String projectAddedOn;

        private String id;

        private String projectId;

        public String getSubmittedBy ()
        {
            return submittedBy;
        }

        public void setSubmittedBy (String submittedBy)
        {
            this.submittedBy = submittedBy;
        }

        public String getCorrectionsCount ()
        {
            return correctionsCount;
        }

        public void setCorrectionsCount (String correctionsCount)
        {
            this.correctionsCount = correctionsCount;
        }

        public String getLastName ()
        {
            return lastName;
        }

        public void setLastName (String lastName)
        {
            this.lastName = lastName;
        }

        public String getVisitLogsCount ()
        {
            return visitLogsCount;
        }

        public void setVisitLogsCount (String visitLogsCount)
        {
            this.visitLogsCount = visitLogsCount;
        }

        public String getAddedBy ()
        {
            return addedBy;
        }

        public void setAddedBy (String addedBy)
        {
            this.addedBy = addedBy;
        }

        public String getAddedOnValue ()
        {
            return addedOnValue;
        }

        public void setAddedOnValue (String addedOnValue)
        {
            this.addedOnValue = addedOnValue;
        }

        public String getProject ()
        {
            return project;
        }

        public void setProject (String project)
        {
            this.project = project;
        }

        public String getFullFileUrl ()
        {
            return fullFileUrl;
        }

        public void setFullFileUrl (String fullFileUrl)
        {
            this.fullFileUrl = fullFileUrl;
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

        public String getActivityCount ()
        {
            return activityCount;
        }

        public void setActivityCount (String activityCount)
        {
            this.activityCount = activityCount;
        }

        public String getFirstName ()
        {
            return firstName;
        }

        public void setFirstName (String firstName)
        {
            this.firstName = firstName;
        }

        public String getSubContractorActivityCount ()
        {
            return subContractorActivityCount;
        }

        public void setSubContractorActivityCount (String subContractorActivityCount)
        {
            this.subContractorActivityCount = subContractorActivityCount;
        }

        public String getCityName ()
        {
            return cityName;
        }

        public void setCityName (String cityName)
        {
            this.cityName = cityName;
        }

        public String getProjectCode ()
        {
            return projectCode;
        }

        public void setProjectCode (String projectCode)
        {
            this.projectCode = projectCode;
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

        public void setWeather (String weather)
        {
            this.weather = weather;
        }

        public String getLocation ()
        {
            return location;
        }

        public void setLocation (String location)
        {
            this.location = location;
        }

        public String getProjectAddedOn ()
        {
            return projectAddedOn;
        }

        public void setProjectAddedOn (String projectAddedOn)
        {
            this.projectAddedOn = projectAddedOn;
        }



        public String getId ()
        {
            return id;
        }

        public void setId (String id)
        {
            this.id = id;
        }

        public String getProjectId ()
        {
            return projectId;
        }

        public void setProjectId (String projectId)
        {
            this.projectId = projectId;
        }


    }
}
