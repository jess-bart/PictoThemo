package com.jessy_barthelemy.pictothemo.AsyncInteractions;

import android.content.Context;
import android.os.AsyncTask;

import com.jessy_barthelemy.pictothemo.ApiObjects.CommentResult;
import com.jessy_barthelemy.pictothemo.ApiObjects.TokenInformation;
import com.jessy_barthelemy.pictothemo.ApiObjects.User;
import com.jessy_barthelemy.pictothemo.Enum.CommentStatus;
import com.jessy_barthelemy.pictothemo.Exception.TokenExpiredException;
import com.jessy_barthelemy.pictothemo.Helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.Helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncApiObjectResponse;
import com.jessy_barthelemy.pictothemo.R;

public class SetUserTask extends AsyncTask<String, Void, Boolean> {

    private User user;
    private Context context;
    /*reference to the class that want a success callback*/
    private IAsyncApiObjectResponse delegate;

    public SetUserTask(User user, Context context, IAsyncApiObjectResponse delegate){
        this.user = user;
        this.context = context;

        if(delegate != null)
            this.delegate = delegate;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            TokenInformation tokenInfo = ApplicationHelper.getTokenInformations(this.context);
            ApiHelper helper = new ApiHelper(tokenInfo);
            try {
                return helper.setUser(this.user);
            } catch (TokenExpiredException e) {
                LogInTask.login(tokenInfo, this.context);
                LogInTask.postExcecute(false, null, this.context, null, null);
                helper.setTokensInfo(ApplicationHelper.getTokenInformations(this.context));
                return helper.setUser(this.user);
            }
        }catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if(success)
            this.delegate.asyncTaskSuccess(true);
        else
            this.delegate.asyncTaskFail(this.context.getString(R.string.profil_profil_error));
    }
}