package com.project.tranvanmanh.e_chat;

public class Notify {

    private String from;
    private String type;

    public Notify(){}

    public Notify(String from, String type) {
        this.from = from;
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
