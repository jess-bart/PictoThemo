package com.jessy_barthelemy.pictothemo.Helpers;

import android.content.Context;
import android.net.Uri;

import com.jessy_barthelemy.pictothemo.ApiObjects.HttpVerb;
import com.jessy_barthelemy.pictothemo.ApiObjects.TokenInformations;
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
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ApiHelper {
    private static final String URL_API = "http://ptapi.esy.es/api/test";
    public static final SimpleDateFormat RES_FORMAT = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

    //Entity
    public static final String ENTITY_PICTURES = "pictures";
    public static final String ENTITY_THEMES = "themes";

    //Authentication fields
    public static final String ID = "id";
    public static final String USER_ID = "userID";
    public static final String PSEUDO = "pseudo";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String EXPIRES_TOKEN = "expires_token";
    public static final String THEME_NAME = "name";

    public static final String COMMENTS = "comments";
    public static final String POSITIVE_VOTE = "positive";
    public static final String NEGATIVE_VOTE = "negative";
    private static final String SALT = "salt";
    public static final String FLAG_SALT = "SALTED";
    public static final String FLAG_POTD = "POTD";
    public static final String FLAG_COMMENTS = "COMMENTS";

    //endpoint
    public static final String URL_POTD = URL_API+"/POTD/";
    public static final String URL_PICTURE = URL_API+"/Picture/";
    private static final String URL_PICTURE_INFO = URL_API+"/PictureInfos";
    private static final String URL_THEMES = URL_API+"/Themes";
    private final String URL_AUTHENTICATION = URL_API+"/Authentication";
    private final String URL_USERS = URL_API+"/Users";

    private TokenInformations tokensInfos;

    public ApiHelper(){
        this.tokensInfos = null;
    }

    public ApiHelper(TokenInformations tokensInfos){
        this.tokensInfos = tokensInfos;
    }

    public TokenInformations getAccessToken(String pseudo, String password, String flags) throws IOException, JSONException, InvalidParameterException, ParseException {
        Uri.Builder parameters = new Uri.Builder()
                .appendQueryParameter("pseudo", pseudo)
                .appendQueryParameter("password", password);

        if(flags != null)
            parameters.appendQueryParameter("flags", flags);
        boolean isPasswordSalted = flags == FLAG_SALT;
        HttpURLConnection http = this.createHttpConnection(URL_AUTHENTICATION, HttpVerb.POST.toString(), parameters, false);

        if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
            JSONObject result = this.getJSONResponse(http);
            password = (isPasswordSalted)?password:ApplicationHelper.hashPassword(password + result.getString(ApiHelper.SALT));

            if(result.getString(ApiHelper.ACCESS_TOKEN) != null && !result.getString(ApiHelper.ACCESS_TOKEN).isEmpty()){

                this.tokensInfos = new TokenInformations(result.getString(ApiHelper.ACCESS_TOKEN), ApplicationHelper.convertStringToDate(result.getString(ApiHelper.EXPIRES_TOKEN), false),
                                                         pseudo, password, isPasswordSalted);
                return this.tokensInfos;
            }else{
                return null;
            }
        }else if(http.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN){
            throw new InvalidParameterException();
        }

        http.disconnect();
        return null;
    }

    public void validateToken(Context context, TokenInformations tokenInfos, IAsyncResponse delegate){
        if(Calendar.getInstance().after(tokenInfos.getExpiresToken())){
            this.refreshToken(context, tokenInfos, delegate);
        }else{
            delegate.asyncTaskSuccess();
        }
    }

    private void refreshToken(Context context, TokenInformations tokenInfos, IAsyncResponse delegate){
        LogInTask login = new LogInTask(context, tokenInfos, false);
        login.setDelegate(delegate);
        login.execute();
    }

    public TokenInformations createUser(String pseudo, String password) throws IOException, JSONException, ParseException {
        Uri.Builder parameter = new Uri.Builder()
                .appendQueryParameter("pseudo", pseudo)
                .appendQueryParameter("password", password);
        HttpURLConnection http = this.createHttpConnection(URL_USERS, HttpVerb.PUT.toString(), parameter, false);
        if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
            JSONObject result = this.getJSONResponse(http);
            if(result.has(ApiHelper.ACCESS_TOKEN) && result.has(ApiHelper.EXPIRES_TOKEN) && result.has(ApiHelper.SALT)){
                this.tokensInfos = new TokenInformations(result.getString(ApiHelper.ACCESS_TOKEN), ApplicationHelper.convertStringToDate(result.getString(ApiHelper.EXPIRES_TOKEN), false),
                    pseudo, ApplicationHelper.hashPassword(password+result.getString(ApiHelper.SALT)), true);
                return this.tokensInfos;
            }
        }else if(http.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN){
            throw new InvalidParameterException();
        }

        http.disconnect();
        return null;
    }

    public boolean deleteUser(String pseudo, String password) throws IOException{
        boolean result;
        String parameters = String.format("?pseudo=%s&password=%s", pseudo, password);
        HttpURLConnection http = this.createHttpConnection(URL_USERS+parameters, HttpVerb.DELETE.toString(), null, true);

        result = http.getResponseCode() == HttpURLConnection.HTTP_OK;
        http.disconnect();
        return result;
    }

    private HttpURLConnection createHttpConnection(String Url, String method, Uri.Builder parameters, boolean authorization) throws IOException{
        URL url;
        if(method.equals(HttpVerb.GET.toString()) && parameters != null)
            url = new URL(Url+parameters.toString());
        else
            url = new URL(Url);

        HttpURLConnection http = (HttpURLConnection)url.openConnection();
        http.setRequestMethod(method);

        if(authorization && this.tokensInfos != null)
            http.addRequestProperty("Authorization", this.tokensInfos.getAccessToken());

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

    public JSONObject getPictures(String selector, String flags) throws IOException, JSONException, ParseException {
        Uri.Builder parameter = null;

        String url = URL_PICTURE_INFO+"/"+selector;

        if(flags != null)
            parameter = new Uri.Builder().appendQueryParameter("flags", flags);

        HttpURLConnection http = this.createHttpConnection(url, HttpVerb.GET.toString(), parameter, false);

        if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
            return this.getJSONResponse(http);
        }else if(http.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST){
            throw new InvalidParameterException();
        }

        http.disconnect();
        return null;
    }

    public JSONObject getThemes(Calendar candidateDate) throws IOException, JSONException, ParseException {
        String date = ApplicationHelper.convertDateToString(candidateDate, false);
        HttpURLConnection http = this.createHttpConnection(URL_THEMES+"/"+date, HttpVerb.GET.toString(), null, false);

        if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
            return this.getJSONResponse(http);
        }else if(http.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST){
            throw new InvalidParameterException();
        }

        http.disconnect();
        return null;
    }

    public boolean voteForTheme(int theme) throws IOException, JSONException, ParseException {
        HttpURLConnection http = this.createHttpConnection(URL_THEMES+"/"+theme, HttpVerb.PUT.toString(), null, true);
        int resultCode = http.getResponseCode();
        http.disconnect();
        return resultCode == HttpURLConnection.HTTP_OK;
    }
}