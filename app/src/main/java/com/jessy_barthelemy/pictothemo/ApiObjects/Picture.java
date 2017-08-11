package com.jessy_barthelemy.pictothemo.ApiObjects;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class Picture implements Serializable, Comparable<Picture>{
    private int id ;
    private String theme;
    private User user;
    private int positiveVote;
    private int negativeVote;
    private Calendar date;
    private ArrayList<Comment> comments;

    public Picture(int id, String theme, User user, Calendar date, int positiveVote, int negativeVote) {
        this.id = id;
        this.theme = theme;
        this.user = user;
        this.positiveVote = positiveVote;
        this.negativeVote = negativeVote;
        this.date = date;
        this.comments = new ArrayList<>();
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public User getUser() {
        return user;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public int getPositiveVote() {
        return positiveVote;
    }

    public void setPositiveVote(int positiveVote) {
        this.positiveVote = positiveVote;
    }

    public int getNegativeVote() {
        return negativeVote;
    }

    public void setNegativeVote(int negativeVote) {
        this.negativeVote = negativeVote;
    }

    @Override
    public int compareTo(@NonNull Picture picture) {
        return picture.getDate().compareTo(this.date);
    }
}
