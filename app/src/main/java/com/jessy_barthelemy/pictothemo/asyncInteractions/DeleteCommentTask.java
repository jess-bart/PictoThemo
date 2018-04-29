package com.jessy_barthelemy.pictothemo.asyncInteractions;

import android.content.Context;
import android.os.AsyncTask;

import com.jessy_barthelemy.pictothemo.apiObjects.TokenInformation;
import com.jessy_barthelemy.pictothemo.exceptions.LoginException;
import com.jessy_barthelemy.pictothemo.helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.interfaces.IAsyncResponse;
import com.jessy_barthelemy.pictothemo.R;

public class DeleteCommentTask extends AsyncTask<Void, Object, Boolean> {

    private Context context;
    /*reference to the class that want a success callback*/
    private IAsyncResponse delegate;
    private int picture;

    public DeleteCommentTask(int picture, Context context, IAsyncResponse delegate){
        this.picture = picture;
        this.context = context;

        if(delegate != null)
            this.delegate = delegate;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            TokenInformation tokenInfo = ApplicationHelper.getTokenInformations(this.context);
            ApiHelper helper = ApiHelper.getInstance();

            try {
                return helper.deleteComment(this.picture);
            } catch (LoginException e) {
                LogInTask.login(tokenInfo, this.context);
                LogInTask.postExcecute(false, null, this.context, null, null);
                return helper.deleteComment(this.picture);
            }
        }catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if(result)
            this.delegate.asyncTaskSuccess();
        else
            this.delegate.asyncTaskFail(this.context.getResources().getString(R.string.comment_remove_error));
    }
}