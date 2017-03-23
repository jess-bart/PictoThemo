package com.jessy_barthelemy.pictothemo.ApiObjects;

import java.util.ArrayList;

public class ThemeList {
    private ArrayList<Theme> themes;

    public ThemeList(ArrayList<Theme> themes) {
        this.themes = themes;
    }

    public ArrayList<Theme> getThemes() {
        return themes;
    }
}
