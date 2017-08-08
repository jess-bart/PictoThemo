package com.jessy_barthelemy.pictothemo.ApiObjects;

import java.io.Serializable;
import java.util.Calendar;

public class Comment implements Serializable {
    private User user;
    private String text;
    private Calendar date;

    public Comment(User user, String text, Calendar date) {
        this.user = user;
        this.text = text;
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Calendar getDate() {
        return date;
    }
}