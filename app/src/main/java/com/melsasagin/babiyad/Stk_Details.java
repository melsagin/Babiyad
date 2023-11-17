package com.melsasagin.babiyad;

import java.util.Map;

public class Stk_Details {
    private String head;
    private String address;
    private String desc, phone,stk, city;
    int uye, sma, kanser;
    private int details_verify, docs_verify, verify, docs_status, image_status, details_status;
    private Map<String, UploadInfo> images;
    public Stk_Details() {}

    public Stk_Details(String head, String address, String city, int uye, int details_verify, int docs_verify, int sma, int kanser, String phone, String desc, String stk, Map<String, UploadInfo> images, int docs_status, int image_status, int details_status) {
        this.head = head;
        this.address = address;
        this.city = city;
        this.uye = uye;
        this.sma = sma;
        this.kanser = kanser;
        this.phone = phone;
        this.desc = desc;
        this.details_verify = details_verify;
        this.docs_verify = docs_verify;
        this.verify = 0;
        this.stk = stk;
        this.images = images;
        this.docs_status  = docs_status;
        this.image_status = image_status;
        this.details_status = details_status;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStk() {
        return stk;
    }

    public void setStk(String stk) {
        this.stk = stk;
    }

    public int getDetails_status() {
        return details_status;
    }

    public void setDetails_status(int details_status) {
        this.details_status = details_status;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setUye(int uye) {
        this.uye = uye;
    }

    public void setSma(int sma) {
        this.sma =sma;
    }

    public void setKanser(int kanser) {
        this.kanser = kanser;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getDetails_verify() {
        return details_verify;
    }

    public void setDetails_verify(int details_verify) {
        this.details_verify = details_verify;
    }

    public int getDocs_verify() {
        return docs_verify;
    }

    public void setDocs_verify(int docs_verify) {
        this.docs_verify = docs_verify;
    }

    public int getVerify() {
        return verify;
    }

    public void setVerify(int verify) {
        this.verify = verify;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDesc() {
        return desc;
    }

    public String getHead() {
        return head;
    }

    public String getAddress() {
        return address;
    }

    public int getUye() {
        return uye;
    }

    public int getSma() {
        return sma;
    }

    public int getKanser() {
        return kanser;
    }

    public int getDocs_status() {
        return docs_status;
    }

    public void setDocs_status(int docs_status) {
        this.docs_status = docs_status;
    }

    public int getImage_status() {
        return image_status;
    }

    public void setImage_status(int image_status) {
        this.image_status = image_status;
    }

    public Map<String, UploadInfo> getImages() {
        return images;
    }

    public void setImages(Map<String, UploadInfo> images) {
        this.images = images;
    }

}


