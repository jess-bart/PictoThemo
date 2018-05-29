package com.jessy_barthelemy.pictothemo.asyncInteractions;

import android.os.AsyncTask;

import com.jessy_barthelemy.pictothemo.apiObjects.ThemeList;
import com.jessy_barthelemy.pictothemo.helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.interfaces.IAsyncApiObjectResponse;

import java.net.UnknownHostException;
import java.util.Calendar;

public class GetThemeTask extends BaseAsyncTask<String, Void, ThemeList> {

    private Calendar date;

    public GetThemeTask(Calendar date, IAsyncApiObjectResponse delegate){
        this.date = date;

        if(delegate != null)
            this.delegate = delegate;
    }

    @Override
    protected ThemeList doInBackground(String... params) {
        ApiHelper helper = ApiHelper.getInstance();
        try {
            return helper.getThemes(this.date);
        } catch (UnknownHostException e) {
            this.isOffline = true;
            return null;
        }
    }

    @Override
    protected void onPostExecute(ThemeList themes) {
        super.onPostExecute(themes);
        ((IAsyncApiObjectResponse)this.delegate).asyncTaskSuccess(themes);
    }
}