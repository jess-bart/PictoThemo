package com.jessy_barthelemy.pictothemo.AsyncInteractions;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.jessy_barthelemy.pictothemo.ApiObjects.TokenInformations;
import com.jessy_barthelemy.pictothemo.Helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.Helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncResponse;
import com.jessy_barthelemy.pictothemo.R;

import java.security.InvalidParameterException;

public class RegistrationTask extends AsyncTask<String, Void, String> {

    private Context context;
    private ProgressDialog waitDialog;
    private TokenInformations tokensInfos;
    /*reference to the class that want a success callback*/
    private IAsyncResponse delegate;

    public RegistrationTask(Context ctx, TokenInformations tokenInfos, IAsyncResponse delegate){
        this.context = ctx;
        this.tokensInfos = tokenInfos;

        waitDialog = new ProgressDialog(ctx);
        waitDialog.setMessage(context.getResources().getString(R.string.login_verification));
        waitDialog.setIndeterminate(false);
        waitDialog.setCancelable(false);

        if(delegate != null)
            this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        waitDialog.show();
    }

    /*
    * Param 1 : Pseudo(String)
    * Param 2 : Password(String)
    * Return the error message or null
    * */
    @Override
    protected String doInBackground(String... params) {
        String errorMessage = null;
        try {
            ApiHelper helper = new ApiHelper();
            this.tokensInfos = helper.createUser(tokensInfos.getPseudo(), tokensInfos.getPassword());

            if(this.tokensInfos == null){
                errorMessage = context.getResources().getString(R.string.network_unavalaible);
            }
        }catch (InvalidParameterException ipe){
            errorMessage = context.getResources().getString(R.string.registration_fail);
        }catch (Exception e){
            errorMessage = context.getResources().getString(R.string.network_unavalaible);
            e.printStackTrace();
        }

        return errorMessage;
    }

    @Override
    protected void onPostExecute(String errorMessage) {
        waitDialog.dismiss();

        if(errorMessage != null && !errorMessage.isEmpty()){
            ApplicationHelper.resetPreferences(this.context);
            delegate.asyncTaskFail(errorMessage);
            return;
        }

        ApplicationHelper.savePreferences(this.context, this.tokensInfos);
        delegate.asyncTaskSuccess();
    }
}