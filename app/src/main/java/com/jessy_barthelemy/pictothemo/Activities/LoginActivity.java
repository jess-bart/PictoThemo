package com.jessy_barthelemy.pictothemo.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.jessy_barthelemy.pictothemo.Api.TokenInformations;
import com.jessy_barthelemy.pictothemo.AsyncInteractions.LogInTask;
import com.jessy_barthelemy.pictothemo.AsyncInteractions.RegistrationTask;
import com.jessy_barthelemy.pictothemo.Helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.Helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.Helpers.FormHelper;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncResponse;
import com.jessy_barthelemy.pictothemo.R;

public class LoginActivity extends AppCompatActivity implements IAsyncResponse{

    private final int PERMISSION_READ_ACCOUNT = 100;

    private TextInputLayout pseudo;
    private TextInputLayout password;
    private Resources resources;
    private FormHelper formHelper;
    private Button loginAction;
    private Button registerAction;
    private TokenInformations tokenInfos;
    /*Keep the last action to execute on password IME action (login or register)*/
    private boolean attemptRegitration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pseudo = (TextInputLayout) findViewById(R.id.login_pseudo);
        password = (TextInputLayout) findViewById(R.id.login_password);
        loginAction = (Button) findViewById(R.id.login_action);
        registerAction = (Button) findViewById(R.id.login_register_action);

        tokenInfos = ApplicationHelper.getTokenInformations(this);
        if(!tokenInfos.getAccessToken().isEmpty()){
            ApiHelper helper = new ApiHelper();
            helper.validateToken(this, tokenInfos, this);
        }

        attemptRegitration = true;
        formHelper = new FormHelper();
        resources = getResources();

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

    private void attemptRegistration(){
        attemptRegitration = true;

        ApplicationHelper.resetPreferences(this);
        tokenInfos = new TokenInformations();

        if(this.isFormValid()){
            /*Database verification*/
            this.tokenInfos.setPseudo(pseudo.getEditText().getText().toString());
            this.tokenInfos.setPassword(password.getEditText().getText().toString());
            RegistrationTask registration = new RegistrationTask(this, tokenInfos, this);
            registration.execute();
        }
    }

    private void attemptLogin(){
        attemptRegitration = false;

        ApplicationHelper.resetPreferences(this);
        tokenInfos = new TokenInformations();

        if(this.isFormValid()){
            this.tokenInfos.setPseudo(pseudo.getEditText().getText().toString());
            this.tokenInfos.setPassword(password.getEditText().getText().toString());
            LogInTask login = new LogInTask(this, tokenInfos, true);
            login.setDelegate(this);
            login.execute();
        }
    }

    private boolean isFormValid(){
        //Pseudo verification
        if(!formHelper.validatePseudo(pseudo.getEditText().getText().toString())){
            pseudo.setErrorEnabled(true);
            pseudo.setError(resources.getString(R.string.login_pseudo_verification));
            return false;
        }

        pseudo.setError(null);
        pseudo.setErrorEnabled(false);

        //Password verification
        if(!formHelper.validatePassword(password.getEditText().getText().toString())){
            password.setErrorEnabled(true);
            password.setError(resources.getString(R.string.login_password_verification));
            return false;
        }

        password.setError(null);
        password.setErrorEnabled(false);

        return true;
    }

    @Override
    public void asyncTaskSuccess() {
        password.setErrorEnabled(false);
        password.setError(null);

        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void asyncTaskFail(String errorMessage) {
        password.setErrorEnabled(true);
        password.setError(errorMessage);
    }
}