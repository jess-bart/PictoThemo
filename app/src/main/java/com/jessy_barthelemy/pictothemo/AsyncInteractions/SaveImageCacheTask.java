package com.jessy_barthelemy.pictothemo.AsyncInteractions;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class SaveImageCacheTask extends AsyncTask<Void, Void, Void> {
    private Bitmap image;
    private String path;

    public SaveImageCacheTask(Bitmap image, String path){
        this.image = image;
        this.path = path;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            FileOutputStream stream = new FileOutputStream(path);
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        } catch (FileNotFoundException e) {
        }
        return null;
    }
}
