package com.jessy_barthelemy.pictothemo.AsyncInteractions;

import android.content.Context;
import android.os.AsyncTask;

import com.jessy_barthelemy.pictothemo.ApiObjects.Theme;
import com.jessy_barthelemy.pictothemo.ApiObjects.TokenInformation;
import com.jessy_barthelemy.pictothemo.Exception.TokenExpiredException;
import com.jessy_barthelemy.pictothemo.Helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.Helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncApiObjectResponse;
import com.jessy_barthelemy.pictothemo.R;

public class VoteThemeTask extends AsyncTask<String, Void, Boolean>{

    Context context;
    /*reference to the class that want a success callback*/
    private IAsyncApiObjectResponse delegate;
    private Theme theme;
    ApiHelper helper;

    public VoteThemeTask(Theme theme, Context context, IAsyncApiObjectResponse delegate){
        this.theme = theme;
        this.context = context;
        if(delegate != null)
            this.delegate = delegate;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        TokenInformation tokenInfo = ApplicationHelper.getTokenInformations(this.context);
        this.helper = new ApiHelper(tokenInfo);
        try {
            try {
                return this.helper.voteForTheme(this.theme.getId());
            } catch (TokenExpiredException e) {
                LogInTask.login(tokenInfo, this.context);
                LogInTask.postExcecute(false, null, this.context, null, null);
                this.helper.setTokensInfo(ApplicationHelper.getTokenInformations(this.context));
                return this.helper.voteForTheme(this.theme.getId());
            }
        }catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if(success)
            this.delegate.asyncTaskSuccess(new Theme(this.theme.getId(), null));
        else
            this.delegate.asyncTaskFail(this.context.getResources().getString(R.string.theme_vote_error, this.theme.getName()));
    }
}