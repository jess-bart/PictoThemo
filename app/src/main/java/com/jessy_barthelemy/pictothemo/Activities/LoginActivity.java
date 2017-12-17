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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jessy_barthelemy.pictothemo.ApiObjects.TokenInformation;
import com.jessy_barthelemy.pictothemo.ApiObjects.User;
import com.jessy_barthelemy.pictothemo.AsyncInteractions.LogInTask;
import com.jessy_barthelemy.pictothemo.AsyncInteractions.RegistrationTask;
import com.jessy_barthelemy.pictothemo.Helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.Helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.Helpers.FormHelper;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncResponse;
import com.jessy_barthelemy.pictothemo.R;

public class LoginActivity extends AppCompatActivity implements IAsyncResponse{

    private TextInputLayout pseudo;
    private TextInputLayout password;
    private Resources resources;
    private FormHelper formHelper;
    private ImageView loader;
    private LinearLayout loginContainer;
    private TokenInformation tokenInfos;
    /*Keep the last action to execute on password IME action (login or register)*/
    private boolean attemptRegitration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.pseudo = (TextInputLayout) this.findViewById(R.id.login_pseudo);
        this.password = (TextInputLayout) this.findViewById(R.id.login_password);
        this.loader = (ImageView) this.findViewById(R.id.loader);
        this.loginContainer = (LinearLayout) this.findViewById(R.id.login_container);

        Button loginAction = (Button) this.findViewById(R.id.login_action);
        Button registerAction = (Button) findViewById(R.id.login_register_action);

        this.tokenInfos = ApplicationHelper.getTokenInformations(this);
        if(this.tokenInfos != null && !this.tokenInfos.getAccessToken().isEmpty()){
            ApiHelper helper = new ApiHelper();
            helper.validateToken(this, this.tokenInfos, this);

            this.loginContainer.setVisibility(View.GONE);
            this.loader.setVisibility(View.VISIBLE);
        }

        this.attemptRegitration = true;
        this.formHelper = new FormHelper();
        this.resources = getResources();

        if(this.password != null && this.password.getEditText() != null){
            this.password.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if (actionId == EditorInfo.IME_ACTION_GO
                            || actionId == EditorInfo.IME_ACTION_DONE
                            || event.getAction() == KeyEvent.ACTION_DOWN
                            && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                        //Hide keyboard
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        if(LoginActivity.this.getCurrentFocus() != null)
                            imm.hideSoftInputFromWindow(LoginActivity.this.getCurrentFocus().getWindowToken(), 0);

                        if(LoginActivity.this.attemptRegitration)
                            attemptRegistration();
                        else
                            attemptLogin();
                        return true;
                    }
                    return false;
                }
            });
        }

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
        this.attemptRegitration = true;

        ApplicationHelper.resetPreferences(this);
        this.tokenInfos = new TokenInformation();

        if(this.isFormValid()){
            /*Database verification*/
            this.tokenInfos.setUser(new User(pseudo.getEditText().getText().toString()));
            this.tokenInfos.setPassword(password.getEditText().getText().toString());
            RegistrationTask registration = new RegistrationTask(this, this.tokenInfos, this);
            registration.execute();
        }
    }

    private void attemptLogin(){
        this.attemptRegitration = false;

        ApplicationHelper.resetPreferences(this);
        this.tokenInfos = new TokenInformation();

        if(this.isFormValid()){
            this.tokenInfos.setUser(new User(pseudo.getEditText().getText().toString()));
            this.tokenInfos.setPassword(this.password.getEditText().getText().toString());
            LogInTask login = new LogInTask(this, this.tokenInfos, true);
            login.setDelegate(this);
            login.execute();
        }
    }

    private boolean isFormValid(){
        //Pseudo verification
        if(!this.formHelper.validatePseudo(this.pseudo.getEditText().getText().toString())){
            this.pseudo.setErrorEnabled(true);
            this.pseudo.setError(resources.getString(R.string.login_pseudo_verification));
            return false;
        }

        this.pseudo.setError(null);
        this.pseudo.setErrorEnabled(false);

        //Password verification
        if(!this.formHelper.validatePassword(this.password.getEditText().getText().toString())){
            this.password.setErrorEnabled(true);
            this.password.setError(resources.getString(R.string.login_password_verification));
            return false;
        }

        this.password.setError(null);
        this.password.setErrorEnabled(false);

        return true;
    }

    @Override
    public void asyncTaskSuccess() {
        this.password.setErrorEnabled(false);
        this.password.setError(null);

        Intent intent = new Intent(getApplicationContext(), BaseActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void asyncTaskFail(String errorMessage) {
        this.loginContainer.setVisibility(View.VISIBLE);
        this.loader.setVisibility(View.GONE);

        this.password.setErrorEnabled(true);
        this.password.setError(errorMessage);
    }
}