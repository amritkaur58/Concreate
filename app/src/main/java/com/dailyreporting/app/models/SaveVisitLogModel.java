package com.dailyreporting.app.models;

public class SaveVisitLogModel {
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
        private String departureTime;

        private String note;

        private String lastModBy;

        private String addedBy;

        private String companyName;

        private String lastModOn;

        private String title;

        private String addedOn;

        private String visitReason;

        private String personName;

        private String locationId;

        private String arrivalTime;

        private String id;

        private String workLogId;

        public String getDepartureTime ()
        {
            return departureTime;
        }

        public void setDepartureTime (String departureTime)
        {
            this.departureTime = departureTime;
        }

        public String getNote ()
        {
            return note;
        }

        public void setNote (String note)
        {
            this.note = note;
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

        public String getCompanyName ()
        {
            return companyName;
        }

        public void setCompanyName (String companyName)
        {
            this.companyName = companyName;
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

        public String getVisitReason ()
        {
            return visitReason;
        }

        public void setVisitReason (String visitReason)
        {
            this.visitReason = visitReason;
        }

        public String getPersonName ()
        {
            return personName;
        }

        public void setPersonName (String personName)
        {
            this.personName = personName;
        }

        public String getLocationId ()
        {
            return locationId;
        }

        public void setLocationId (String locationId)
        {
            this.locationId = locationId;
        }

        public String getArrivalTime ()
        {
            return arrivalTime;
        }

        public void setArrivalTime (String arrivalTime)
        {
            this.arrivalTime = arrivalTime;
        }

        public String getId ()
        {
            return id;
        }

        public void setId (String id)
        {
            this.id = id;
        }

        public String getWorkLogId ()
        {
            return workLogId;
        }

        public void setWorkLogId (String workLogId)
        {
            this.workLogId = workLogId;
        }


    }


}
