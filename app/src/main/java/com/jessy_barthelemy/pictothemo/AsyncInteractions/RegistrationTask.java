package com.jessy_barthelemy.pictothemo.AsyncInteractions;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.jessy_barthelemy.pictothemo.Api.TokenInformations;
import com.jessy_barthelemy.pictothemo.Helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.Helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncResponse;
import com.jessy_barthelemy.pictothemo.R;

import org.json.JSONObject;

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
            JSONObject result = helper.createUser(tokensInfos.getPseudo(), tokensInfos.getPassword());

            if(result.getString(ApiHelper.ACCESS_TOKEN) != null && !result.getString(ApiHelper.ACCESS_TOKEN).isEmpty()){
                this.tokensInfos.setAccessToken(result.getString(ApiHelper.ACCESS_TOKEN));
                this.tokensInfos.setExpiresToken(result.getString(ApiHelper.EXPIRES_TOKEN));
                this.tokensInfos.setPassword(ApplicationHelper.hashPassword(this.tokensInfos.getPassword()+result.getString(ApiHelper.SALT)));
                this.tokensInfos.setPasswordSalted(true);

            }else{
                errorMessage = context.getResources().getString(R.string.registration_fail);
            }
        }catch (InvalidParameterException ipe){
            errorMessage = context.getResources().getString(R.string.registration_fail);
        }catch (Exception e){
            errorMessage = e.getMessage();//context.getResources().getString(R.string.network_unavalaible);
            e.printStackTrace();
        }

        return errorMessage;
    }

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