package com.dailyreporting.app.models;

import java.util.List;

public class ProjectActivityTypeModel {
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

    }

    public class Items
    {
        private String lastModBy;

        private String addedBy;

        private String name;

        private String id;

        private String lastModOn;

        private String projectName;

        private String projectId;

        private String parentId;

        private String addedOn;


        private String activityCode;

        public String getActivityCode() {
            return activityCode;
        }

        public void setActivityCode(String activityCode) {
            this.activityCode = activityCode;
        }

        public String getLastModBy ()
        {
            return lastModBy;
        }

        public void setLastModBy (String lastModBy)
        {
            this.lastModBy = lastModBy;
        }

        public String getAddedBy ()
        {
            return addedBy;
        }

        public void setAddedBy (String addedBy)
        {
            this.addedBy = addedBy;
        }

        public String getName ()
        {
            return name;
        }

        public void setName (String name)
        {
            this.name = name;
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

        public String getProjectName ()
        {
            return projectName;
        }

        public void setProjectName (String projectName)
        {
            this.projectName = projectName;
        }

        public String getProjectId ()
        {
            return projectId;
        }

        public void setProjectId (String projectId)
        {
            this.projectId = projectId;
        }

        public String getAddedOn ()
        {
            return addedOn;
        }

        public void setAddedOn (String addedOn)
        {
            this.addedOn = addedOn;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }
    }



}
