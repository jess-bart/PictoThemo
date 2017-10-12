package com.jessy_barthelemy.pictothemo.AsyncInteractions;

import android.content.Context;
import android.os.AsyncTask;

import com.jessy_barthelemy.pictothemo.ApiObjects.User;
import com.jessy_barthelemy.pictothemo.Helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.Helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncApiObjectResponse;

public class GetUserTask extends AsyncTask<String, Void, User> {

    private int id;
    private Context context;
    /*reference to the class that want a success callback*/
    private IAsyncApiObjectResponse delegate;

    public GetUserTask(int id, Context context, IAsyncApiObjectResponse delegate){
        this.id = id;
        this.context = context;

        if(delegate != null)
            this.delegate = delegate;
    }

    @Override
    protected User doInBackground(String... params) {
        ApiHelper helper = new ApiHelper(ApplicationHelper.getTokenInformations(this.context));
        return helper.getUser(this.id);
    }

    @Override
    protected void onPostExecute(User user) {
        if(user != null)
            this.delegate.asyncTaskSuccess(user);
        else
            this.delegate.asyncTaskFail("erreur user");
    }
}