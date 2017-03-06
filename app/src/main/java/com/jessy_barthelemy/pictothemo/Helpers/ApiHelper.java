package com.jessy_barthelemy.pictothemo.Helpers;

import android.content.Context;
import android.net.Uri;

import com.jessy_barthelemy.pictothemo.Api.HttpVerb;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ApiHelper {
    public static final String URL_API = "http://ptapi.esy.es/api/test";
    public static final SimpleDateFormat RES_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    //Authentication fields
    public static final String ID = "id";
    public static final String PSEUDO = "pseudo";
    public static final String PASSWORD = "password";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String EXPIRES_TOKEN = "expires_token";
    public static final String THEME_NAME = "name";
    public static final String PICTURES = "pictures";
    public static final String POSITIVE_VOTE = "positive";
    public static final String NEGATIVE_VOTE = "negative";
    public static final String SALT = "salt";
    public static final String FLAG_SALT = "SALTED";
    public static final String FLAG_POTD = "POTD";
    public static final String FLAG_COMMENTS = "COMMENTS";

    /*endpoint*/
    public static final String URL_POTD = URL_API+"/POTD/";
    public static final String URL_PICTURES = URL_API+"/Pictures";
    private final String URL_AUTHENTICATION = URL_API+"/Authentication";
    private final String URL_USERS = URL_API+"/Users";

    public JSONObject getAccessToken(String pseudo, String password, String flags) throws IOException, JSONException, InvalidParameterException {
        Uri.Builder parameters = new Uri.Builder()
                .appendQueryParameter("pseudo", pseudo)
                .appendQueryParameter("password", password);

        if(flags != null)
            parameters.appendQueryParameter("flags", flags);
        HttpURLConnection http = this.createHttpConnection(URL_AUTHENTICATION, HttpVerb.POST.toString(), parameters);

        if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
            return this.getJSONResponse(http);
        }else if(http.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN){
            throw new InvalidMarkException();
        }

        http.disconnect();
        return null;
    }

    public void validateToken(Context context, TokenInformations tokenInfos, IAsyncResponse delegate){
        Date t = new Date();
        if(new Date().after(tokenInfos.getExpiresToken())){
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

    public JSONObject createUser(String pseudo, String password) throws IOException, JSONException{
        Uri.Builder parameter = new Uri.Builder()
                .appendQueryParameter("pseudo", pseudo)
                .appendQueryParameter("password", password);
        HttpURLConnection http = this.createHttpConnection(URL_USERS, HttpVerb.PUT.toString(), parameter);

        if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
            return this.getJSONResponse(http);
        }else if(http.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN){
            throw new InvalidParameterException();
        }

        http.disconnect();
        return null;
    }

    public boolean deleteUser(String pseudo, String password) throws IOException{
        boolean result;
        String parameters = String.format("?pseudo=%s&password=%s", pseudo, password);
        HttpURLConnection http = this.createHttpConnection(URL_USERS+parameters, HttpVerb.DELETE.toString(), null);

        result = http.getResponseCode() == HttpURLConnection.HTTP_OK;
        http.disconnect();
        return result;
    }

    private HttpURLConnection createHttpConnection(String Url, String method, Uri.Builder parameters) throws IOException{
        URL url = null;
        if(method.equals(HttpVerb.GET.toString()) && parameters != null)
            url = new URL(Url+parameters.toString());
        else
            url = new URL(Url);

        HttpURLConnection http = (HttpURLConnection)url.openConnection();
        http.setRequestMethod(method);

        if(method.equals(HttpVerb.POST.toString()) || method.equals(HttpVerb.PUT.toString())){
            http.setDoOutput(true);
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            OutputStream os = http.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            if(parameters != null)
                writer.write(parameters.build().getEncodedQuery());

            writer.flush();
            writer.close();
            os.close();
        }
        http.connect();
        return http;
    }

    private JSONObject getJSONResponse(HttpURLConnection http) throws IOException, JSONException {
        BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return new JSONObject(response.toString());
    }

    public JSONObject getPictures(Date candidateDate, String flags) throws IOException, JSONException, ParseException {
        Uri.Builder parameter = null;
        if(flags != null)
            parameter = new Uri.Builder().appendQueryParameter("flags", flags);

        String date = ApplicationHelper.convertDateToString(candidateDate);
        HttpURLConnection http = this.createHttpConnection(URL_PICTURES+"/"+date, HttpVerb.GET.toString(), parameter);

        if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
            return this.getJSONResponse(http);
        }else if(http.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST){
            throw new InvalidParameterException("Invalid parameters");
        }

        http.disconnect();
        return null;
    }
}


