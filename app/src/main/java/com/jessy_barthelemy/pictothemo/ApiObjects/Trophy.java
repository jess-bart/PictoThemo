package com.jessy_barthelemy.pictothemo.ApiObjects;

import java.io.Serializable;

public class Trophy implements Serializable{
    private int id;
    private String title;
    private String description;
    private boolean validated;

    public Trophy(int id, String title, String description, boolean validated) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.validated = validated;
    }

    public int getId() {
        return id;
    }

    public boolean isValidated() {
        return validated;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
