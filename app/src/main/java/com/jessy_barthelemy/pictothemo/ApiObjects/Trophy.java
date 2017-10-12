package com.jessy_barthelemy.pictothemo.ApiObjects;

public class Trophy {
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
}
