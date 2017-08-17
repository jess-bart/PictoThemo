package com.jessy_barthelemy.pictothemo.ApiObjects;

import java.util.ArrayList;

public class PictureList {
    private ArrayList<Picture> pictures;

    public PictureList(ArrayList<Picture> pictures) {
        this.pictures = pictures;
    }

    public ArrayList<Picture> getPictures() {
        return pictures;
    }
}
