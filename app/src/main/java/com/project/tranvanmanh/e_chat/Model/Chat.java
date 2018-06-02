package com.project.tranvanmanh.e_chat.Model;

public class Chat {

    private Boolean seen;
    private Long time;

    public Chat(){}

    public Chat(Boolean seen, Long time) {
        this.seen = seen;
        this.time = time;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
