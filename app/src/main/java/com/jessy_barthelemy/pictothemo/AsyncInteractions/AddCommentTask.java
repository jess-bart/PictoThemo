package com.jessy_barthelemy.pictothemo.AsyncInteractions;

import android.content.Context;
import android.os.AsyncTask;

import com.jessy_barthelemy.pictothemo.ApiObjects.Comment;
import com.jessy_barthelemy.pictothemo.Enum.CommentResult;
import com.jessy_barthelemy.pictothemo.Helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.Helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncApiObjectResponse;
import com.jessy_barthelemy.pictothemo.R;

import java.util.Calendar;

public class AddCommentTask extends AsyncTask<Void, Object, CommentResult> {

    private Context context;
    /*reference to the class that want a success callback*/
    private IAsyncApiObjectResponse delegate;
    private int picture;
    private String text;

    public AddCommentTask(int picture, String text, Context context, IAsyncApiObjectResponse delegate){
        this.picture = picture;
        this.text = text;
        this.context = context;

        if(delegate != null)
            this.delegate = delegate;
    }

    @Override
    protected CommentResult doInBackground(Void... params) {
        ApiHelper helper = new ApiHelper(ApplicationHelper.getTokenInformations(this.context));
        return helper.addComment(this.picture, this.text);
    }

    @Override
    protected void onPostExecute(CommentResult result) {
        switch (result){
            case ALREADY_COMMENTED:
                this.delegate.asyncTaskFail(this.context.getResources().getString(R.string.already_commented));
                break;
            case ERROR:
                this.delegate.asyncTaskFail(this.context.getResources().getString(R.string.comment_add_error));
                break;
            case SUCCESS:
                this.delegate.asyncTaskSuccess(new Comment(ApplicationHelper.getCurrentUser(this.context), this.text, Calendar.getInstance()));
                break;
        }
    }
}