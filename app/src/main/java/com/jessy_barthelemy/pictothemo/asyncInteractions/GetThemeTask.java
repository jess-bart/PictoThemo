package com.jessy_barthelemy.pictothemo.asyncInteractions;

import android.os.AsyncTask;

import com.jessy_barthelemy.pictothemo.apiObjects.ThemeList;
import com.jessy_barthelemy.pictothemo.helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.interfaces.IAsyncApiObjectResponse;

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
        ApiHelper helper = ApiHelper.getInstance();
        return helper.getThemes(this.date);
    }

    @Override
    protected void onPostExecute(ThemeList themes) {
        this.delegate.asyncTaskSuccess(themes);
    }
}