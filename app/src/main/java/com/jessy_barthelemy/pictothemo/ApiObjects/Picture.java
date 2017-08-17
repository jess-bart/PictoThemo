package com.jessy_barthelemy.pictothemo.ApiObjects;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

public class Picture implements Serializable, Comparable<Picture>{
    private int id ;
    private Theme theme;
    private User user;
    private int positiveVote;
    private int negativeVote;
    private ArrayList<Comment> comments;
    private boolean potd;

    public Picture(int id, Theme theme, User user, int positiveVote, int negativeVote, boolean potd) {
        this.id = id;
        this.theme = theme;
        this.user = user;
        this.positiveVote = positiveVote;
        this.negativeVote = negativeVote;
        this.comments = new ArrayList<>();
        this.potd = potd;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public User getUser() {
        return user;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public boolean isPotd() {
        return potd;
    }

    @Override
    public int compareTo(@NonNull Picture picture) {
        return picture.getTheme().getCandidateDate().compareTo(this.getTheme().getCandidateDate());
    }
}
