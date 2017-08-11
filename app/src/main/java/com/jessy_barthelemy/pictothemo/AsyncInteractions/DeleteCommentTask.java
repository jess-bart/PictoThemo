package com.jessy_barthelemy.pictothemo.AsyncInteractions;

import android.content.Context;
import android.os.AsyncTask;

import com.jessy_barthelemy.pictothemo.Helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.Helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncResponse;
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
        ApiHelper helper = new ApiHelper(ApplicationHelper.getTokenInformations(this.context));
        return helper.deleteComment(this.picture);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if(result)
            this.delegate.asyncTaskSuccess();
        else
            this.delegate.asyncTaskFail(this.context.getResources().getString(R.string.comment_remove_error));
    }
}