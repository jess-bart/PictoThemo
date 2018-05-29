package com.jessy_barthelemy.pictothemo.asyncInteractions;

import android.app.ProgressDialog;
import android.content.Context;

import com.jessy_barthelemy.pictothemo.R;
import com.jessy_barthelemy.pictothemo.apiObjects.TokenInformation;
import com.jessy_barthelemy.pictothemo.exceptions.PictothemoException;
import com.jessy_barthelemy.pictothemo.helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.interfaces.IAsyncSimpleResponse;

import java.lang.ref.WeakReference;

public class RegistrationTask extends BaseAsyncTask<String, Void, String> {

    private ProgressDialog waitDialog;
    private TokenInformation tokensInfos;
    /*reference to the class that want a success callback*/
    private IAsyncSimpleResponse delegate;

    public RegistrationTask(Context context, TokenInformation tokenInfos, IAsyncSimpleResponse delegate){
        this.weakContext = new WeakReference<>(context);
        this.tokensInfos = tokenInfos;

        this.waitDialog = new ProgressDialog(context);
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
        Context context = this.weakContext.get();

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
        super.onPostExecute(errorMessage);
        Context context = this.weakContext.get();

        this.waitDialog.dismiss();

        if(errorMessage != null && !errorMessage.isEmpty()){
            ApplicationHelper.resetPreferences(context);
            this.delegate.asyncTaskFail(errorMessage);
            return;
        }

        ApplicationHelper.savePreferences(context, this.tokensInfos);
        this.delegate.asyncTaskSuccess();
    }
}