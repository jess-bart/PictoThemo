package com.jessy_barthelemy.pictothemo.asyncInteractions;

import android.content.Context;
import android.os.AsyncTask;

import com.jessy_barthelemy.pictothemo.R;
import com.jessy_barthelemy.pictothemo.apiObjects.TokenInformation;
import com.jessy_barthelemy.pictothemo.exceptions.LoginException;
import com.jessy_barthelemy.pictothemo.helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.interfaces.IAsyncApiObjectResponse;
import com.jessy_barthelemy.pictothemo.interfaces.IAsyncResponse;

public class DeletePictureTask extends AsyncTask<Void, Object, Boolean> {

    private Context context;
    /*reference to the class that want a success callback*/
    private IAsyncApiObjectResponse delegate;
    private int picture;

    public DeletePictureTask(int picture, Context context, IAsyncApiObjectResponse delegate){
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
                return helper.deletePicture(this.picture);
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
            this.delegate.asyncTaskSuccess(this.context.getResources().getString(R.string.remove_picture_success));
        else
            this.delegate.asyncTaskFail(this.context.getResources().getString(R.string.remove_picture_error));
    }
}