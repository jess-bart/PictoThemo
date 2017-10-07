package com.jessy_barthelemy.pictothemo.ApiObjects;

import java.io.Serializable;
import java.util.Calendar;

public class Theme implements Serializable{
    private int id;
    private String name;
    private Calendar candidateDate;

    public Theme(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Theme(int id, String name, Calendar candidateDate) {
        this.id = id;
        this.name = name;
        this.candidateDate = candidateDate;
    }

    public Theme(String name, Calendar candidateDate) {
        this.candidateDate = candidateDate;
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
}
