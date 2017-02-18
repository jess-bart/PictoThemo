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

public class LogInTask extends AsyncTask<Void, Void, String> {

    private Context context;
    private ProgressDialog waitDialog;
    private boolean showLoading;
    private TokenInformations tokensInfos;
    /*reference to the class that want a success callback*/
    private IAsyncResponse delegate;

    /*Constructor without ui*/
    public LogInTask(Context ctx, TokenInformations tokenInfos, boolean showLoading){
        this.context = ctx;
        this.showLoading = true;
        this.tokensInfos = tokenInfos;

        if(showLoading){
            this.initDialog();
        }
    }

    public void setDelegate(IAsyncResponse context){
        this.delegate = context;
    }

    private void initDialog(){
        this.waitDialog = new ProgressDialog(this.context);
        this.waitDialog.setMessage(context.getResources().getString(R.string.login_verification));
        this.waitDialog.setIndeterminate(false);
        this.waitDialog.setCancelable(false);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(showLoading)
            waitDialog.show();
    }

    /*
    * Param 1 : Email(String)
    * Param 2 : Password(String)
    * Return the error message or null
    * */
    @Override
    protected String doInBackground(Void... params) {
        String errorMessage = null;
        try {
            String flags = (tokensInfos.isPasswordSalted())?ApiHelper.FLAG_SALT:null;
            ApiHelper helper = new ApiHelper();
            JSONObject result = helper.getAccessToken(tokensInfos.getEmail(), tokensInfos.getPassword(), flags);

            if(result.getString(ApiHelper.ACCESS_TOKEN) != null && !result.getString(ApiHelper.ACCESS_TOKEN).isEmpty()){
                this.tokensInfos.setAccessToken(result.getString(ApiHelper.ACCESS_TOKEN));
                this.tokensInfos.setExpiresToken(result.getString(ApiHelper.EXPIRES_TOKEN));

                if(!tokensInfos.isPasswordSalted()){
                    this.tokensInfos.setPassword(ApplicationHelper.hashPassword(this.tokensInfos.getPassword()+result.getString(ApiHelper.SALT)));
                    this.tokensInfos.setPasswordSalted(true);
                }

            }else{
                errorMessage = context.getResources().getString(R.string.login_fail);
            }
        }catch (InvalidParameterException ipe){
            errorMessage = context.getResources().getString(R.string.login_fail);
        }catch (Exception e){
            errorMessage = context.getResources().getString(R.string.network_unavalaible);
        }

        return errorMessage;
    }

    protected void onPostExecute(String errorMessage) {
        if(showLoading)
            waitDialog.dismiss();
        //Error handling
        if(errorMessage != null && !errorMessage.isEmpty()){
            if(delegate != null)
                delegate.asyncTaskFail(errorMessage);

            ApplicationHelper.resetPreferences(this.context);
            if(this.tokensInfos.isPasswordSalted()){
                ApplicationHelper.restartApp(this.context);
            }
        }else{
            ApplicationHelper.savePreferences(this.context, this.tokensInfos);
            if(delegate != null)
                delegate.asyncTaskSuccess();
        }
    }
}