package com.jessy_barthelemy.pictothemo.asyncInteractions;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.jessy_barthelemy.pictothemo.apiObjects.TokenInformation;
import com.jessy_barthelemy.pictothemo.exceptions.PictothemoException;
import com.jessy_barthelemy.pictothemo.helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.interfaces.IAsyncResponse;
import com.jessy_barthelemy.pictothemo.R;

public class RegistrationTask extends AsyncTask<String, Void, String> {

    private Context context;
    private ProgressDialog waitDialog;
    private TokenInformation tokensInfos;
    /*reference to the class that want a success callback*/
    private IAsyncResponse delegate;

    public RegistrationTask(Context ctx, TokenInformation tokenInfos, IAsyncResponse delegate){
        this.context = ctx;
        this.tokensInfos = tokenInfos;

        this.waitDialog = new ProgressDialog(ctx);
        this.waitDialog.setMessage(context.getResources().getString(R.string.login_verification));
        this.waitDialog.setIndeterminate(false);
        this.waitDialog.setCancelable(false);

        if(delegate != null)
            this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.waitDialog.show();
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
            ApiHelper helper = ApiHelper.getInstance();

            this.tokensInfos = helper.createUser(tokensInfos.getUser().getPseudo(), tokensInfos.getPassword());

            if(this.tokensInfos == null){
                errorMessage = context.getResources().getString(R.string.network_unavalaible);
            }
        }catch (PictothemoException pe){
            errorMessage = pe.getMessage();
        }catch (Exception e){
            errorMessage = context.getResources().getString(R.string.network_unavalaible);
        }

        return errorMessage;
    }

    @Override
    protected void onPostExecute(String errorMessage) {
        this.waitDialog.dismiss();

        if(errorMessage != null && !errorMessage.isEmpty()){
            ApplicationHelper.resetPreferences(this.context);
            this.delegate.asyncTaskFail(errorMessage);
            return;
        }

        ApplicationHelper.savePreferences(this.context, this.tokensInfos);
        this.delegate.asyncTaskSuccess();
    }
}