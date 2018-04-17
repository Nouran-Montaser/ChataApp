package com.example.lenovo.chat;

/**
 * Created by Lenovo on 2/16/2018.
 */

public class Conversation {

    public String message, user , from;
    public long time;
    public boolean seen;

    public Conversation() {
    }

    public Conversation(String user, String message, long time, boolean seen ,String from) {
        this.message = message;
        this.user = user;
        this.time = time;
        this.seen = seen;
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
