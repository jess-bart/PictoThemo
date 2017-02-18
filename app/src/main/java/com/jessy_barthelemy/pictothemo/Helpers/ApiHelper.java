package com.jessy_barthelemy.pictothemo.Helpers;

import android.content.Context;
import android.net.Uri;

import com.jessy_barthelemy.pictothemo.Api.TokenInformations;
import com.jessy_barthelemy.pictothemo.AsyncInteractions.LogInTask;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.InvalidMarkException;
import java.security.InvalidParameterException;
import java.util.Date;

public class ApiHelper {

    private final String API_URL = "http://ptapi.esy.es/api/";
    private final String AUTHENTICATION = "Authentication";

    //Authentication fields
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String EXPIRES_TOKEN = "expires_token";
    public static final String SALT = "salt";
    public static final String FLAG_SALT = "SALTED";

    public JSONObject getAccessToken(String email, String password, String flags) throws IOException, JSONException, InvalidParameterException {
        URL url = new URL(API_URL+AUTHENTICATION);

        Uri.Builder query = new Uri.Builder()
                .appendQueryParameter("email", email)
                .appendQueryParameter("password", password);

        if(flags != null)
            query.appendQueryParameter("flags", flags);
        HttpURLConnection http = (HttpURLConnection)url.openConnection();
        http.setRequestMethod("POST");
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        http.setDoOutput(true);

        OutputStream os = http.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(query.build().getEncodedQuery());
        writer.flush();
        writer.close();
        os.close();

        if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
            BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return new JSONObject(response.toString());
        }else if(http.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN){
            throw new InvalidMarkException();
        }

        http.disconnect();
        return null;
    }

    public void validateToken(Context context, TokenInformations tokenInfos, IAsyncResponse delegate){
        if(!new Date().after(tokenInfos.getExpiresToken())){
            this.refreshToken(context, tokenInfos, delegate);
        }else{
            delegate.asyncTaskSuccess();
        }
    }

    private void refreshToken(Context context, TokenInformations tokenInfos, IAsyncResponse delegate){
        LogInTask login = new LogInTask(context, tokenInfos, true);
        login.setDelegate(delegate);
        login.execute();
    }
}
