package com.project.tranvanmanh.e_chat;

public class Request {

    private String req_type;

    public Request(){};

    public Request(String req_type) {
        this.req_type = req_type;
    }

    public String getReq_type() {
        return req_type;
    }

    public void setReq_type(String req_type) {
        this.req_type = req_type;
    }
}
