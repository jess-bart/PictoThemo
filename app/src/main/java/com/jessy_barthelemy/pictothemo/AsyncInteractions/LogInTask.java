package com.jessy_barthelemy.pictothemo.AsyncInteractions;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.jessy_barthelemy.pictothemo.ApiObjects.TokenInformation;
import com.jessy_barthelemy.pictothemo.Exception.PictothemoException;
import com.jessy_barthelemy.pictothemo.Helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.Helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncResponse;
import com.jessy_barthelemy.pictothemo.R;

public class LogInTask extends AsyncTask<Void, Void, String> {

    private Context context;
    private ProgressDialog waitDialog;
    private boolean showLoading;
    /*reference to the class that want a success callback*/
    private IAsyncResponse delegate;
    private static boolean isNetworkAvailable;
    private static TokenInformation tokensInfos;

    /*Constructor without ui*/
    public LogInTask(Context ctx, TokenInformation tokenInfos, boolean showLoading){
        this.context = ctx;
        this.showLoading = showLoading;
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
        return login(tokensInfos, this.context);
    }

    @Override
    protected void onPostExecute(String errorMessage) {
        postExcecute(showLoading, waitDialog, context, delegate, errorMessage);
    }

    public static String login(TokenInformation tokens, Context context){
        String errorMessage = null;
        isNetworkAvailable = true;
        try {
            String flags = (tokens.isPasswordSalted())?ApiHelper.FLAG_SALT:null;
            ApiHelper helper = new ApiHelper();
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
                                    IAsyncResponse delegate,
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