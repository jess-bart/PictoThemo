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

public class LogInTask extends BaseAsyncTask<Void, Void, String> {

    private ProgressDialog waitDialog;
    private boolean showLoading;
    /*reference to the class that want a success callback*/
    private IAsyncSimpleResponse delegate;
    private static boolean isNetworkAvailable;
    private static TokenInformation tokensInfos;

    /*Constructor without ui*/
    public LogInTask(Context context, TokenInformation tokenInfos, boolean showLoading){
        this.weakContext = new WeakReference<>(context);
        this.showLoading = showLoading;
        tokensInfos = tokenInfos;

        if(showLoading){
            this.initDialog();
        }
    }

    public void setDelegate(IAsyncSimpleResponse context){
        this.delegate = context;
    }

    private void initDialog(){
        Context context = this.weakContext.get();
        this.waitDialog = new ProgressDialog(context);
        this.waitDialog.setMessage(context.getResources().getString(R.string.login_verification));
        this.waitDialog.setIndeterminate(false);
        this.waitDialog.setCancelable(false);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(this.showLoading)
            this.waitDialog.show();
    }

    /*
    * Param 1 : Pseudo(String)
    * Param 2 : Password(String)
    * Return the error message or null
    * */
    @Override
    protected String doInBackground(Void... params) {
        Context context = this.weakContext.get();

        return login(tokensInfos, context);
    }

    @Override
    protected void onPostExecute(String errorMessage) {
        super.onPostExecute(errorMessage);
        Context context = this.weakContext.get();
        postExcecute(showLoading, waitDialog, context, delegate, errorMessage);
    }

    public static String login(TokenInformation tokens, Context context){
        String errorMessage = null;
        isNetworkAvailable = true;
        try {
            String flags = (tokens.isPasswordSalted())?ApiHelper.FLAG_SALT:null;
            ApiHelper helper = ApiHelper.getInstance();
            tokensInfos = helper.getAccessToken(tokens.getUser().getPseudo(), tokens.getPassword(), flags);

            if(tokensInfos == null)
                errorMessage = context.getResources().getString(R.string.login_fail);

        }catch (PictothemoException pe){
            errorMessage = pe.getMessage();
        }catch (Exception e){
            isNetworkAvailable = false;
            errorMessage = context.getResources().getString(R.string.network_unavalaible);
        }
        return errorMessage;
    }

    public static void postExcecute(boolean showLoading,
                                    ProgressDialog waitDialog,
                                    Context context,
                                    IAsyncSimpleResponse delegate,
                                    String errorMessage){
        if(showLoading)
            waitDialog.dismiss();
        //Error handling
        if(errorMessage != null && !errorMessage.isEmpty()){
            if(delegate != null)
                delegate.asyncTaskFail(errorMessage);
            if(isNetworkAvailable){
                ApplicationHelper.resetPreferences(context);
                if(tokensInfos != null && tokensInfos.isPasswordSalted())
                    ApplicationHelper.restartApp(context);
            }

        }else{
            ApplicationHelper.savePreferences(context, tokensInfos);
            if(delegate != null)
                delegate.asyncTaskSuccess();
        }
    }
}