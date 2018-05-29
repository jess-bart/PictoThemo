package com.jessy_barthelemy.pictothemo.asyncInteractions;

import android.content.Context;
import android.graphics.Bitmap;

import com.jessy_barthelemy.pictothemo.R;
import com.jessy_barthelemy.pictothemo.interfaces.IAsyncApiObjectResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

public class SaveImageToDiskTask extends BaseAsyncTask<Void, Void, Boolean> {
    private Bitmap image;
    private OutputStream stream;

    SaveImageToDiskTask(Bitmap image, OutputStream stream){
        this.image = image;
        this.stream = stream;
    }

    public SaveImageToDiskTask(Bitmap image, OutputStream stream, Context context, IAsyncApiObjectResponse delegate){
        this.image = image;
        this.stream = stream;
        this.delegate = delegate;
        this.weakContext = new WeakReference<>(context);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if(this.image != null){
            this.image.compress(Bitmap.CompressFormat.PNG, 100, this.stream);
            return true;
        }

        try {
            this.stream.close();
        } catch (IOException ignored) {}

        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if(this.delegate == null)
            return;

        Context context = this.weakContext.get();

        if(result)
            this.getDelegate().asyncTaskSuccess(context.getResources().getString(R.string.save_picture_success));
        else
            this.getDelegate().asyncTaskFail(context.getResources().getString(R.string.save_picture_error));
    }

    public IAsyncApiObjectResponse getDelegate(){
        return (IAsyncApiObjectResponse) this.delegate;
    }
}
