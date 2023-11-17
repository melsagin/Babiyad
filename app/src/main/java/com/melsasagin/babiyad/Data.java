package com.melsasagin.babiyad;

public class Data
{
    public String fullName, email, userType;

    public Data() {}

    public Data(String fullName, String email, String userType) {
        this.fullName = fullName;
        this.email = email;
        this.userType = userType;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getUserType() {
        return userType;
    }
}
