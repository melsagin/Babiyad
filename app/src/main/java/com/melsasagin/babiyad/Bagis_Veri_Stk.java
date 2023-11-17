package com.melsasagin.babiyad;

public class Bagis_Veri_Stk {
    String name, bagis, email, bagis_tarihi, key, alinan_tarih;
    boolean complete;

    public Bagis_Veri_Stk(){}

    public Bagis_Veri_Stk(String key, String email, String name, String bagis, String date_donated, String date_received, boolean complete) {
        this.key = key;
        this.email = email;
        this.name = name;
        this.bagis = bagis;

        this.bagis_tarihi = date_donated;
        this.alinan_tarih = date_received;
        this.complete = complete;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate_received() {
        return alinan_tarih;
    }

    public void setDate_received(String date_received) {
        this.alinan_tarih = date_received;
    }

    public String getBagis() {return bagis;}

    public void setBagis(String food) {
        this.bagis = food;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDate_donated() {
        return bagis_tarihi;
    }

    public void setDate_donated(String date_donated) {
        this.bagis_tarihi = date_donated;
    }

    public boolean getComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
