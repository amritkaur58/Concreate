package com.dailyreporting.app.models;

public class VisitResponse {
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
        private String lastName;

        private String fullName;

        private String lastModBy;

        private String roleId;

        private String addedBy;

        private String isBlocked;

        private String language;

        private String isAdmin;

        private String lastModOn;

        private String addedOn;

        private String token;

        private String firstName;

        private String password;

        private String phoneNumber;

        private String roleName;

        private String id;

        private String email;

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getIsAdmin() {
            return isAdmin;
        }

        public void setIsAdmin(String isAdmin) {
            this.isAdmin = isAdmin;
        }

        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }

        public String getLastName ()
        {
            return lastName;
        }

        public void setLastName (String lastName)
        {
            this.lastName = lastName;
        }

        public String getLastModBy ()
        {
            return lastModBy;
        }

        public void setLastModBy (String lastModBy)
        {
            this.lastModBy = lastModBy;
        }

        public String getRoleId ()
        {
            return roleId;
        }

        public void setRoleId (String roleId)
        {
            this.roleId = roleId;
        }

        public String getAddedBy ()
        {
            return addedBy;
        }

        public void setAddedBy (String addedBy)
        {
            this.addedBy = addedBy;
        }

        public String getIsBlocked ()
        {
            return isBlocked;
        }

        public void setIsBlocked (String isBlocked)
        {
            this.isBlocked = isBlocked;
        }

        public String getLanguage ()
        {
            return language;
        }

        public void setLanguage (String language)
        {
            this.language = language;
        }

        public String getLastModOn ()
        {
            return lastModOn;
        }

        public void setLastModOn (String lastModOn)
        {
            this.lastModOn = lastModOn;
        }

        public String getAddedOn ()
        {
            return addedOn;
        }

        public void setAddedOn (String addedOn)
        {
            this.addedOn = addedOn;
        }

        public String getToken ()
        {
            return token;
        }

        public void setToken (String token)
        {
            this.token = token;
        }

        public String getFirstName ()
        {
            return firstName;
        }

        public void setFirstName (String firstName)
        {
            this.firstName = firstName;
        }

        public String getPassword ()
        {
            return password;
        }

        public void setPassword (String password)
        {
            this.password = password;
        }

        public String getPhoneNumber ()
        {
            return phoneNumber;
        }

        public void setPhoneNumber (String phoneNumber)
        {
            this.phoneNumber = phoneNumber;
        }

        public String getId ()
        {
            return id;
        }

        public void setId (String id)
        {
            this.id = id;
        }

        public String getEmail ()
        {
            return email;
        }

        public void setEmail (String email)
        {
            this.email = email;
        }


    }


}
