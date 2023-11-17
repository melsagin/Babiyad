package com.melsasagin.babiyad;

public class User {

    public String fullName,email,userType;
    int enable;//database de kullanıcıları disable etme bölümü olduğu için eklendi
    //örneğin kullanıcı sahte bir hesapsa disable etmek için yazdım

    public User() {}

    public User(String fullName, String email, String userType, int enable) {
        this.fullName = fullName;
        this.email = email;
        this.userType = userType;
        this.enable = enable;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

}
