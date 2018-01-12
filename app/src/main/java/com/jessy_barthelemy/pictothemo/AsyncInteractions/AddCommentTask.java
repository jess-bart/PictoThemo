package com.jessy_barthelemy.pictothemo.AsyncInteractions;

import android.content.Context;
import android.os.AsyncTask;

import com.jessy_barthelemy.pictothemo.ApiObjects.Comment;
import com.jessy_barthelemy.pictothemo.ApiObjects.CommentResult;
import com.jessy_barthelemy.pictothemo.ApiObjects.TokenInformation;
import com.jessy_barthelemy.pictothemo.Enum.CommentStatus;
import com.jessy_barthelemy.pictothemo.Exception.TokenExpiredException;
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
        try {
            TokenInformation tokenInfo = ApplicationHelper.getTokenInformations(this.context);
            ApiHelper helper = new ApiHelper(tokenInfo);
            try {
                return helper.addComment(this.picture, this.text);
            } catch (TokenExpiredException e) {
                LogInTask.login(tokenInfo, this.context);
                LogInTask.postExcecute(false, null, this.context, null, null);
                helper.setTokensInfo(ApplicationHelper.getTokenInformations(this.context));
                return helper.addComment(this.picture, this.text);
            }
        }catch (Exception e) {
            CommentResult result = new CommentResult();
            result.setResult(CommentStatus.ERROR);
            return result;
        }

    }

    @Override
    protected void onPostExecute(CommentResult result) {
        CommentStatus status = result.getResult();
        switch (status){
            case ALREADY_COMMENTED:
                this.delegate.asyncTaskFail(this.context.getResources().getString(R.string.already_commented));
                break;
            case ERROR:
                this.delegate.asyncTaskFail(this.context.getResources().getString(R.string.comment_add_error));
                break;
            case SUCCESS:
                this.delegate.asyncTaskSuccess(result.getComment());
                break;
        }
    }
}