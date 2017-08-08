package com.jessy_barthelemy.pictothemo.AsyncInteractions;

import android.content.Context;
import android.os.AsyncTask;

import com.jessy_barthelemy.pictothemo.ApiObjects.Theme;
import com.jessy_barthelemy.pictothemo.Helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.Helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncApiObjectResponse;
import com.jessy_barthelemy.pictothemo.R;

public class VoteThemeTask extends AsyncTask<String, Void, Boolean> {

    private Context context;
    /*reference to the class that want a success callback*/
    private IAsyncApiObjectResponse delegate;
    private int themeID;

    public VoteThemeTask(int themeID, Context context, IAsyncApiObjectResponse delegate){
        this.themeID = themeID;
        this.context = context;
        if(delegate != null)
            this.delegate = delegate;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        ApiHelper helper = new ApiHelper(ApplicationHelper.getTokenInformations(this.context));
        try {
            return helper.voteForTheme(this.themeID);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if(success)
            this.delegate.asyncTaskSuccess(new Theme(this.themeID, null));
        else
            this.delegate.asyncTaskFail(this.context.getResources().getString(R.string.theme_vote_error));
    }
}