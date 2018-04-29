package com.jessy_barthelemy.pictothemo.asyncInteractions;

import android.content.Context;
import android.os.AsyncTask;

import com.jessy_barthelemy.pictothemo.apiObjects.User;
import com.jessy_barthelemy.pictothemo.helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.interfaces.IAsyncApiObjectResponse;
import com.jessy_barthelemy.pictothemo.R;

public class GetUserTask extends AsyncTask<String, Void, User> {

    private long id;
    private Context context;
    /*reference to the class that want a success callback*/
    private IAsyncApiObjectResponse delegate;

    public GetUserTask(long id, Context context, IAsyncApiObjectResponse delegate){
        this.id = id;
        this.context = context;

        if(delegate != null)
            this.delegate = delegate;
    }

    @Override
    protected User doInBackground(String... params) {
        ApiHelper helper = ApiHelper.getInstance();
        return helper.getUser(this.id);
    }

    @Override
    protected void onPostExecute(User user) {
        if(user != null)
            this.delegate.asyncTaskSuccess(user);
        else if(this.context != null)
            this.delegate.asyncTaskFail(this.context.getString(R.string.profil_load_error));
    }
}