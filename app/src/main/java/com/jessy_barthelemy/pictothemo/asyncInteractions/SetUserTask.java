package com.jessy_barthelemy.pictothemo.asyncInteractions;

import android.content.Context;

import com.jessy_barthelemy.pictothemo.R;
import com.jessy_barthelemy.pictothemo.apiObjects.TokenInformation;
import com.jessy_barthelemy.pictothemo.apiObjects.User;
import com.jessy_barthelemy.pictothemo.exceptions.LoginException;
import com.jessy_barthelemy.pictothemo.helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.interfaces.IAsyncApiObjectResponse;

import java.lang.ref.WeakReference;
import java.net.UnknownHostException;

public class SetUserTask extends BaseAsyncTask<String, Void, Boolean> {

    private User user;

    public SetUserTask(User user, Context context, IAsyncApiObjectResponse delegate){
        this.user = user;
        this.weakContext = new WeakReference<>(context);

        if(delegate != null)
            this.delegate = delegate;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            Context context = this.weakContext.get();
            TokenInformation tokenInfo = ApplicationHelper.getTokenInformations(context);
            ApiHelper helper = ApiHelper.getInstance();

            try {
                return helper.setUser(this.user);
            } catch (LoginException e) {
                LogInTask.login(tokenInfo, context);
                LogInTask.postExcecute(false, null, context, null, null);
                return helper.setUser(this.user);
            }
        }catch (UnknownHostException e) {
            this.isOffline = true;
            return false;
        }catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        if(success)
            this.getDelegate().asyncTaskSuccess(true);
        else{
            Context context = this.weakContext.get();
            this.getDelegate().asyncTaskFail(context.getString(R.string.profil_profil_error));
        }
    }

    public IAsyncApiObjectResponse getDelegate(){
        return (IAsyncApiObjectResponse) this.delegate;
    }
}