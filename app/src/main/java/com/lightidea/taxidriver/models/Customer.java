package com.lightidea.taxidriver.models;

public class Customer {
    String Name, Phone, LatLog, PhotoURL;



    public Customer() {
    }

    public Customer(String name, String phone, String latLog, String photoURL) {
        Name = name;
        Phone = phone;
        LatLog = latLog;
        PhotoURL = photoURL;


    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getLatLog() {
        return LatLog;
    }

    public void setLatLog(String latLog) {
        LatLog = latLog;
    }

    public String getPhotoURL() {
        return PhotoURL;
    }

    public void setPhotoURL(String photoURL) {
        PhotoURL = photoURL;
    }
}
