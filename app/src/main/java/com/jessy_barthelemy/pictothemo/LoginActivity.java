package com.jessy_barthelemy.pictothemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jessy_barthelemy.pictothemo.DatabaseInteractions.RegistrationTask;

import java.util.Hashtable;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout email;
    private TextInputLayout password;
    private Button loginAction;
    private Button registerAction;
    private Resources resources;
    /*Keep the last action to execute on password IME action (login or register)*/
    private boolean attemptRegitration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        attemptRegitration = true;
        resources = getResources();
        email = (TextInputLayout)findViewById(R.id.login_email);
        password = (TextInputLayout)findViewById(R.id.login_password);
        loginAction = (Button) findViewById(R.id.login_action);
        registerAction = (Button) findViewById(R.id.login_register_action);

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

    private void attemptLogin(){
        attemptRegitration = false;
        //Email verification
        if(!validateEmail(email.getEditText().getText().toString())){
            email.setErrorEnabled(true);
            email.setError(resources.getString(R.string.login_email_verification));
            return;
        }

        email.setError(null);
        email.setErrorEnabled(false);

        //Password verification
        if(!validatePassword(password.getEditText().getText().toString())){
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

    public boolean validateEmail(String email){
        try{
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
            return true;
        }catch(AddressException e){
            return false;
        }
    }

    public boolean validatePassword(String password){
        return password.length() >= 8;
    }

    private void attemptRegistration(){
        Toast.makeText(LoginActivity.this, "Attempting registration", Toast.LENGTH_SHORT).show();
        attemptRegitration = true;
    }

    /*
    *     public boolean isNetworkAvailable() {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
    * */
}