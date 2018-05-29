package com.jessy_barthelemy.pictothemo.asyncInteractions;

import android.content.Context;

import com.jessy_barthelemy.pictothemo.R;
import com.jessy_barthelemy.pictothemo.apiObjects.CommentResult;
import com.jessy_barthelemy.pictothemo.apiObjects.TokenInformation;
import com.jessy_barthelemy.pictothemo.enumerations.CommentStatus;
import com.jessy_barthelemy.pictothemo.exceptions.LoginException;
import com.jessy_barthelemy.pictothemo.helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.interfaces.IAsyncApiObjectResponse;

import java.lang.ref.WeakReference;
import java.net.UnknownHostException;

public class AddCommentTask extends BaseAsyncTask<Void, Object, CommentResult> {

    private int picture;
    private String text;

    public AddCommentTask(int picture, String text, Context context, IAsyncApiObjectResponse delegate){
        this.picture = picture;
        this.text = text;
        this.weakContext = new WeakReference<>(context);

        if(delegate != null)
            this.delegate = delegate;
    }

    @Override
    protected CommentResult doInBackground(Void... params) {
        Context context = this.weakContext.get();

        try {
            TokenInformation tokenInfo = ApplicationHelper.getTokenInformations(context);
            ApiHelper helper = ApiHelper.getInstance();

            try {
                return helper.addComment(this.picture, this.text);
            } catch (LoginException e) {
                LogInTask.login(tokenInfo, context);
                LogInTask.postExcecute(false, null, context, null, null);
                return helper.addComment(this.picture, this.text);
            }
        }catch (UnknownHostException e){
            this.isOffline = true;
            CommentResult result = new CommentResult();
            result.setResult(CommentStatus.ERROR);
            return result;
        }catch (Exception e) {
            CommentResult result = new CommentResult();
            result.setResult(CommentStatus.ERROR);
            return result;
        }
    }

    @Override
    protected void onPostExecute(CommentResult result) {
        super.onPostExecute(result);

        Context context = this.weakContext.get();
        CommentStatus status = result.getResult();
        switch (status){
            case ALREADY_COMMENTED:
                this.getDelegate().asyncTaskFail(context.getResources().getString(R.string.already_commented));
                break;
            case ERROR:
                this.getDelegate().asyncTaskFail(context.getResources().getString(R.string.comment_add_error));
                break;
            case SUCCESS:
                this.getDelegate().asyncTaskSuccess(result.getComment());
                break;
        }
    }

    public IAsyncApiObjectResponse getDelegate(){
        return (IAsyncApiObjectResponse) this.delegate;
    }
}