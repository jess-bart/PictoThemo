package com.jessy_barthelemy.pictothemo.AsyncInteractions;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.design.widget.TextInputLayout;

import com.jessy_barthelemy.pictothemo.R;

public class RegistrationTask extends AsyncTask<String, Void, String> {

    private TextInputLayout email;
    private Resources resources;
    private ProgressDialog waitDialog;

    public RegistrationTask(Context ctx, TextInputLayout email){
        this.email = email;
        this.resources = ctx.getResources();

        waitDialog = new ProgressDialog(ctx);
        waitDialog.setMessage(resources.getString(R.string.login_verification));
        waitDialog.setIndeterminate(false);
        waitDialog.setCancelable(false);
    }

    /*
    * Param 1 : Email(String)
    * Param 2 : Password(String)
    * Return the error message or null
    * */
    @Override
    protected String doInBackground(String... params) {
        SystemClock.sleep(7000);
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        waitDialog.show();
    }

    protected void onPostExecute(String result) {
        waitDialog.dismiss();

        if(result != null && !result.isEmpty()){
            email.setErrorEnabled(true);
            email.setError(result);
            return;
        }

        email.setErrorEnabled(false);
        email.setError(null);
    }
}