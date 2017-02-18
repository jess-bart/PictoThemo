package com.jessy_barthelemy.pictothemo;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jessy_barthelemy.pictothemo.Api.TokenInformations;
import com.jessy_barthelemy.pictothemo.AsyncInteractions.LogInTask;
import com.jessy_barthelemy.pictothemo.AsyncInteractions.RegistrationTask;
import com.jessy_barthelemy.pictothemo.Helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.Helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.Helpers.FormHelper;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncResponse;

public class LoginActivity extends AppCompatActivity implements IAsyncResponse{

    private final int PERMISSION_READ_ACCOUNT = 100;

    private TextInputLayout email;
    private TextInputLayout password;
    private Resources resources;
    private FormHelper formHelper;
    private TokenInformations tokenInfos;
    /*Keep the last action to execute on password IME action (login or register)*/
    private boolean attemptRegitration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tokenInfos = ApplicationHelper.getTokenInformations(this);
        if(!tokenInfos.getAccessToken().isEmpty()){
            ApiHelper helper = new ApiHelper();
            helper.validateToken(this, tokenInfos, this);
        }

        email = (TextInputLayout) findViewById(R.id.login_email);
        password = (TextInputLayout) findViewById(R.id.login_password);
        Button loginAction = (Button) findViewById(R.id.login_action);
        Button registerAction = (Button) findViewById(R.id.login_register_action);

        attemptRegitration = true;
        formHelper = new FormHelper();
        resources = getResources();

        //Prefill the email field
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.GET_ACCOUNTS}, PERMISSION_READ_ACCOUNT);
        }else{
            this.preFillEmail();
        }


        password.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_GO
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    //Hide keyboard
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(LoginActivity.this.getCurrentFocus().getWindowToken(), 0);

                    if(attemptRegitration)
                        attemptRegistration();
                    else
                        attemptLogin();
                    return true;
                }
                return false;
            }
        });

        registerAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegistration();
            }
        });
        loginAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_READ_ACCOUNT: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.preFillEmail();
                }
            }
        }
    }

    public void preFillEmail(){
        try{
            Account[] accounts = AccountManager.get(this).getAccountsByType("com.google");
            if(accounts.length > 0 && formHelper.validateEmail(accounts[0].name))
                email.getEditText().setText(accounts[0].name);
        }catch(SecurityException e){
            e.printStackTrace();
        }
    }

    private void attemptRegistration(){
        attemptRegitration = true;

        //Email verification
        if(!formHelper.validateEmail(email.getEditText().getText().toString())){
            email.setErrorEnabled(true);
            email.setError(resources.getString(R.string.login_email_verification));
            return;
        }

        email.setError(null);
        email.setErrorEnabled(false);

        //Password verification
        if(!formHelper.validatePassword(password.getEditText().getText().toString())){
            password.setErrorEnabled(true);
            password.setError(resources.getString(R.string.login_password_verification));
            return;
        }

        password.setError(null);
        password.setErrorEnabled(false);

        /*Database verification*/
        RegistrationTask registration = new RegistrationTask(this, email);
        registration.execute(email.getEditText().getText().toString(), password.getEditText().getText().toString());
    }

    private void attemptLogin(){
        attemptRegitration = false;
        ApplicationHelper.resetPreferences(this);
        tokenInfos = new TokenInformations();

        //Email verification
        if(!formHelper.validateEmail(email.getEditText().getText().toString())){
            email.setErrorEnabled(true);
            email.setError(resources.getString(R.string.login_email_verification));
            return;
        }

        email.setError(null);
        email.setErrorEnabled(false);

        //Password verification
        if(!formHelper.validatePassword(password.getEditText().getText().toString())){
            password.setErrorEnabled(true);
            password.setError(resources.getString(R.string.login_password_verification));
            return;
        }

        password.setError(null);
        password.setErrorEnabled(false);

        this.tokenInfos.setEmail(email.getEditText().getText().toString());
        this.tokenInfos.setPassword(password.getEditText().getText().toString());
        LogInTask login = new LogInTask(this, tokenInfos, true);
        login.setDelegate(this);
        login.execute();
    }

    @Override
    public void asyncTaskSuccess() {
        password.setErrorEnabled(false);
        password.setError(null);
        Toast.makeText(this, "CONNECTEd", Toast.LENGTH_LONG).show();
    }

    @Override
    public void asyncTaskFail(String errorMessage) {
        password.setErrorEnabled(true);
        password.setError(errorMessage);
    }
}