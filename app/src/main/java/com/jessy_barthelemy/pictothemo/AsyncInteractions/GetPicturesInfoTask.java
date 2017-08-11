package com.jessy_barthelemy.pictothemo.AsyncInteractions;

import android.os.AsyncTask;

import com.jessy_barthelemy.pictothemo.ApiObjects.Picture;
import com.jessy_barthelemy.pictothemo.Helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.Helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncApiObjectResponse;

import java.text.ParseException;
import java.util.Calendar;

public class GetPicturesInfoTask extends AsyncTask<String, Void, Picture> {

    private Calendar date;
    private String flags;
    private int id;
    /*reference to the class that want a success callback*/
    private IAsyncApiObjectResponse delegate;

    public GetPicturesInfoTask(String flags, IAsyncApiObjectResponse delegate){
        this.flags = flags;

        if(delegate != null)
            this.delegate = delegate;
    }

    public GetPicturesInfoTask(Calendar date, String flags, IAsyncApiObjectResponse delegate){
        this(flags, delegate);
        this.date = date;
    }

    public GetPicturesInfoTask(int id, String flags, IAsyncApiObjectResponse delegate){
        this(flags, delegate);
        this.id = id;
    }

    @Override
    protected Picture doInBackground(String... params) {
        ApiHelper helper = new ApiHelper();
        String selector = null;
        try {
            selector = (this.date == null)?String.valueOf(this.id): ApplicationHelper.convertDateToString(this.date, false);
        } catch (ParseException e) {
            return null;
        }
        Picture picture = helper.getPictureInfo(selector, this.flags);

        return picture;
    }

    @Override
    protected void onPostExecute(Picture picture) {
        this.delegate.asyncTaskSuccess(picture);
    }
}