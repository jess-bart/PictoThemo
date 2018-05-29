package com.jessy_barthelemy.pictothemo.asyncInteractions;

import android.content.Context;

import com.jessy_barthelemy.pictothemo.R;
import com.jessy_barthelemy.pictothemo.apiObjects.TokenInformation;
import com.jessy_barthelemy.pictothemo.exceptions.LoginException;
import com.jessy_barthelemy.pictothemo.helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.interfaces.IAsyncApiObjectResponse;

import java.lang.ref.WeakReference;
import java.net.UnknownHostException;

public class DeletePictureTask extends BaseAsyncTask<Void, Object, Boolean> {

    private int picture;

    public DeletePictureTask(int picture, Context context, IAsyncApiObjectResponse delegate){
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
                return helper.deletePicture(this.picture);
            } catch (LoginException e) {
                LogInTask.login(tokenInfo, context);
                LogInTask.postExcecute(false, null, context, null, null);
                return helper.deletePicture(this.picture);
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
        Context context = this.weakContext.get();

        if(result)
            this.getDelegate().asyncTaskSuccess(context.getResources().getString(R.string.remove_picture_success));
        else
            this.getDelegate().asyncTaskFail(context.getResources().getString(R.string.remove_picture_error));
    }

    public IAsyncApiObjectResponse getDelegate(){
        return (IAsyncApiObjectResponse) this.delegate;
    }
}