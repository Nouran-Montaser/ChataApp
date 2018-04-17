package com.example.lenovo.chat;


public class Friends {

    String id;
    String date;

    public Friends() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Friends(String id, String date) {
        this.id = id;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

