package com.jessy_barthelemy.pictothemo.asyncInteractions;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.jessy_barthelemy.pictothemo.apiObjects.Picture;
import com.jessy_barthelemy.pictothemo.apiObjects.TokenInformation;
import com.jessy_barthelemy.pictothemo.exceptions.LoginException;
import com.jessy_barthelemy.pictothemo.helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.interfaces.IAsyncApiObjectResponse;
import com.jessy_barthelemy.pictothemo.R;

public class VotePictureTask extends AsyncTask<Void, Object, Void> {

    private Context context;
    /*reference to the class that want a success callback*/
    private IAsyncApiObjectResponse delegate;
    private Picture picture;
    private boolean positive;

    public VotePictureTask(Picture picture, boolean positive, Context context, IAsyncApiObjectResponse delegate){
        this.picture = picture;
        this.positive = positive;
        this.context = context;

        if(delegate != null)
            this.delegate = delegate;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            TokenInformation tokenInfo = ApplicationHelper.getTokenInformations(this.context);
            ApiHelper helper = ApiHelper.getInstance();

            try {
                this.picture = helper.voteForPicture(this.picture, this.positive);
            } catch (LoginException e) {
                LogInTask.login(tokenInfo, this.context);
                LogInTask.postExcecute(false, null, this.context, null, null);
                this.picture = helper.voteForPicture(this.picture, this.positive);
            }
        }catch (Exception e) {}

        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        if(this.picture != null){
            this.delegate.asyncTaskSuccess(this.picture);
            Toast.makeText(this.context, R.string.vote_success, Toast.LENGTH_LONG).show();
        }
         else
            this.delegate.asyncTaskFail(this.context.getResources().getString(R.string.picture_vote_error));
    }
}