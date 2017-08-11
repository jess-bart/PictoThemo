package com.jessy_barthelemy.pictothemo.AsyncInteractions;

import android.content.Context;
import android.os.AsyncTask;

import com.jessy_barthelemy.pictothemo.ApiObjects.Picture;
import com.jessy_barthelemy.pictothemo.Helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.Helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncApiObjectResponse;
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
        ApiHelper helper = new ApiHelper(ApplicationHelper.getTokenInformations(this.context));
        this.picture = helper.voteForPicture(this.picture, this.positive);
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        if(this.picture != null)
            this.delegate.asyncTaskSuccess(this.picture);
        else
            this.delegate.asyncTaskFail(this.context.getResources().getString(R.string.picture_vote_error));
    }
}