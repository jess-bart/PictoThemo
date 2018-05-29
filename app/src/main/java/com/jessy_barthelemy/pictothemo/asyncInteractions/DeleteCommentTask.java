package com.jessy_barthelemy.pictothemo.asyncInteractions;

import android.content.Context;

import com.jessy_barthelemy.pictothemo.R;
import com.jessy_barthelemy.pictothemo.apiObjects.TokenInformation;
import com.jessy_barthelemy.pictothemo.exceptions.LoginException;
import com.jessy_barthelemy.pictothemo.helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.interfaces.IAsyncSimpleResponse;

import java.lang.ref.WeakReference;
import java.net.UnknownHostException;

public class DeleteCommentTask extends BaseAsyncTask<Void, Object, Boolean> {

    private int picture;

    public DeleteCommentTask(int picture, Context context, IAsyncSimpleResponse delegate){
        this.picture = picture;
        this.weakContext = new WeakReference<>(context);

        if(delegate != null)
            this.delegate = delegate;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            Context context = this.weakContext.get();
            TokenInformation tokenInfo = ApplicationHelper.getTokenInformations(context);
            ApiHelper helper = ApiHelper.getInstance();

            try {
                return helper.deleteComment(this.picture);
            } catch (LoginException e) {
                LogInTask.login(tokenInfo, context);
                LogInTask.postExcecute(false, null, context, null, null);
                return helper.deleteComment(this.picture);
            }
        }catch (UnknownHostException e){
            this.isOffline = true;
            return false;
        }catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        if(result)
            this.getDelegate().asyncTaskSuccess();
        else{
            Context context = this.weakContext.get();
            this.getDelegate().asyncTaskFail(context.getResources().getString(R.string.comment_remove_error));
        }
    }

    public IAsyncSimpleResponse getDelegate(){
        return (IAsyncSimpleResponse) this.delegate;
    }
}