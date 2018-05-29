package com.jessy_barthelemy.pictothemo.asyncInteractions;

import android.content.Context;
import android.util.Log;

import com.jessy_barthelemy.pictothemo.R;
import com.jessy_barthelemy.pictothemo.apiObjects.Theme;
import com.jessy_barthelemy.pictothemo.apiObjects.TokenInformation;
import com.jessy_barthelemy.pictothemo.exceptions.LoginException;
import com.jessy_barthelemy.pictothemo.helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.interfaces.IAsyncApiObjectResponse;

import java.lang.ref.WeakReference;

public class VoteThemeTask extends BaseAsyncTask<String, Void, Boolean>{

    private Theme theme;
    ApiHelper helper;

    public VoteThemeTask(Theme theme, Context context, IAsyncApiObjectResponse delegate){
        this.theme = theme;
        this.weakContext = new WeakReference<>(context);
        if(delegate != null)
            this.delegate = delegate;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        Log.d("JBA DEBUG","DOINBACK");


        Context context = this.weakContext.get();
        TokenInformation tokenInfo = ApplicationHelper.getTokenInformations(context);
        this.helper = ApiHelper.getInstance();

        try {
            try {
                return this.helper.voteForTheme(this.theme.getId());
            } catch (LoginException e) {
                LogInTask.login(tokenInfo, context);
                LogInTask.postExcecute(false, null, context, null, null);
                return this.helper.voteForTheme(this.theme.getId());
            }
        }catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        if(success)
            this.getDelegate().asyncTaskSuccess(new Theme(this.theme.getId(), null));
        else{
            Context context = this.weakContext.get();
            this.getDelegate().asyncTaskFail(context.getResources().getString(R.string.theme_vote_error, this.theme.getName()));
        }
    }

    public IAsyncApiObjectResponse getDelegate(){
        return (IAsyncApiObjectResponse) this.delegate;
    }
}