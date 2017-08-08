package com.jessy_barthelemy.pictothemo.AsyncInteractions;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

class SaveImageCacheTask extends AsyncTask<Void, Void, Void> {
    private Bitmap image;
    private String path;

    SaveImageCacheTask(Bitmap image, String path){
        this.image = image;
        this.path = path;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            FileOutputStream stream = new FileOutputStream(this.path);
            if(this.image != null)
                this.image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        } catch (FileNotFoundException e) {
        }
        return null;
    }
}
