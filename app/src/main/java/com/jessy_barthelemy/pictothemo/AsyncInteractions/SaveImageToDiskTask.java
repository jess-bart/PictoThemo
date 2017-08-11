package com.jessy_barthelemy.pictothemo.AsyncInteractions;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncResponse;
import com.jessy_barthelemy.pictothemo.R;

import java.io.IOException;
import java.io.OutputStream;

public class SaveImageToDiskTask extends AsyncTask<Void, Void, Boolean> {
    private Bitmap image;
    private OutputStream stream;
    private IAsyncResponse delegate;
    private Context context;

    public SaveImageToDiskTask(Bitmap image, OutputStream stream){
        this.image = image;
        this.stream = stream;
    }

    public SaveImageToDiskTask(Bitmap image, OutputStream stream, Context context, IAsyncResponse delegate){
        this.image = image;
        this.stream = stream;
        this.delegate = delegate;
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if(this.image != null){
            this.image.compress(Bitmap.CompressFormat.PNG, 100, this.stream);
            return true;
        }

        try {
            this.stream.close();
        } catch (IOException e) {}

        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if(this.delegate == null)
            return;

        if(result)
            this.delegate.asyncTaskSuccess();
        else
            this.delegate.asyncTaskFail(this.context.getResources().getString(R.string.save_picture_error));
    }
}
