package com.jessy_barthelemy.pictothemo.asyncInteractions;

import android.content.Context;
import android.os.AsyncTask;

import com.jessy_barthelemy.pictothemo.apiObjects.CommentResult;
import com.jessy_barthelemy.pictothemo.apiObjects.TokenInformation;
import com.jessy_barthelemy.pictothemo.enumerations.CommentStatus;
import com.jessy_barthelemy.pictothemo.exceptions.LoginException;
import com.jessy_barthelemy.pictothemo.helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.interfaces.IAsyncApiObjectResponse;
import com.jessy_barthelemy.pictothemo.R;

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
            ApiHelper helper = ApiHelper.getInstance();

            try {
                return helper.addComment(this.picture, this.text);
            } catch (LoginException e) {
                LogInTask.login(tokenInfo, this.context);
                LogInTask.postExcecute(false, null, this.context, null, null);
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