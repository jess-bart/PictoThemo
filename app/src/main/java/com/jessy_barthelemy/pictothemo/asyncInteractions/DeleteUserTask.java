package com.jessy_barthelemy.pictothemo.asyncInteractions;

import android.content.Context;
import android.widget.Toast;

import com.jessy_barthelemy.pictothemo.R;
import com.jessy_barthelemy.pictothemo.apiObjects.TokenInformation;
import com.jessy_barthelemy.pictothemo.exceptions.LoginException;
import com.jessy_barthelemy.pictothemo.helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.helpers.ApplicationHelper;

import java.lang.ref.WeakReference;
import java.net.UnknownHostException;

public class DeleteUserTask extends BaseAsyncTask<Void, Object, Boolean> {

    private String password;

    public DeleteUserTask(String password, Context context){
        this.password = password;
        this.weakContext = new WeakReference<>(context);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            Context context = this.weakContext.get();
            TokenInformation tokenInfo = ApplicationHelper.getTokenInformations(context);
            ApiHelper helper = ApiHelper.getInstance();

            try {
                return helper.deleteUser(this.password);
            } catch (LoginException e) {
                LogInTask.login(tokenInfo, context);
                LogInTask.postExcecute(false, null, context, null, null);
                return helper.deleteUser(this.password);
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

        if(result) {
            Toast.makeText(context, context.getResources().getString(R.string.remove_user_success), Toast.LENGTH_LONG).show();
            ApplicationHelper.resetPreferences(context);
            ApplicationHelper.restartApp(context);
        }else
            Toast.makeText(context, context.getResources().getString(R.string.remove_user_error), Toast.LENGTH_LONG).show();
    }
}