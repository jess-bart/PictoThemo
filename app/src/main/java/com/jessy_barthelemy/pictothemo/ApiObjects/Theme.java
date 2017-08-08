package com.jessy_barthelemy.pictothemo.ApiObjects;

import java.util.Calendar;

public class Theme {
    private int id;
    private String name;
    private Calendar candidateDate;
    private boolean won;

    public Theme(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Calendar getCandidateDate() {
        return candidateDate;
    }

    public void setCandidateDate(Calendar candidateDate) {
        this.candidateDate = candidateDate;
    }

    public boolean isWon() {
        return won;
    }

    public void setWon(boolean won) {
        this.won = won;
    }
}
