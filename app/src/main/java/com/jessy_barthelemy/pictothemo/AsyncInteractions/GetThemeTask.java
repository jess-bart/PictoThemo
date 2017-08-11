package com.jessy_barthelemy.pictothemo.AsyncInteractions;

import android.os.AsyncTask;

import com.jessy_barthelemy.pictothemo.ApiObjects.ThemeList;
import com.jessy_barthelemy.pictothemo.Helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncApiObjectResponse;

import java.util.Calendar;

public class GetThemeTask extends AsyncTask<String, Void, ThemeList> {

    private Calendar date;
    /*reference to the class that want a success callback*/
    private IAsyncApiObjectResponse delegate;

    public GetThemeTask(Calendar date, IAsyncApiObjectResponse delegate){
        this.date = date;

        if(delegate != null)
            this.delegate = delegate;
    }

    @Override
    protected ThemeList doInBackground(String... params) {
        ApiHelper helper = new ApiHelper();
        ThemeList response = helper.getThemes(this.date);

        return response;
    }

    @Override
    protected void onPostExecute(ThemeList themes) {
        this.delegate.asyncTaskSuccess(themes);
    }
}