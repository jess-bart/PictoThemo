package com.jessy_barthelemy.pictothemo.Helpers;

import android.content.Context;
import android.net.Uri;

import com.jessy_barthelemy.pictothemo.ApiObjects.Comment;
import com.jessy_barthelemy.pictothemo.ApiObjects.HttpVerb;
import com.jessy_barthelemy.pictothemo.ApiObjects.Picture;
import com.jessy_barthelemy.pictothemo.ApiObjects.Theme;
import com.jessy_barthelemy.pictothemo.ApiObjects.ThemeList;
import com.jessy_barthelemy.pictothemo.ApiObjects.TokenInformations;
import com.jessy_barthelemy.pictothemo.ApiObjects.User;
import com.jessy_barthelemy.pictothemo.AsyncInteractions.LogInTask;
import com.jessy_barthelemy.pictothemo.Enum.CommentResult;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncResponse;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class ApiHelper {
    private static final String URL_API = "http://ptapi.esy.es/api/test";

    //Entity
    public static final String ENTITY_PICTURES = "pictures";
    public static final String ENTITY_THEMES = "themes";

    //Authentication fields
    public static final String THEME_NAME = "name";
    public static final String ID = "id";
    public static final String PSEUDO = "pseudo";
    public static final String FLAGS = "flags";
    private static final String PASSWORD = "password";
    private static final String USER_ID = "userID";
    private static final String THEME = "theme";
    private static final String POTD = "potd";
    private static final String VOTE_COUNT = "voteCount";

    private static final String ACCESS_TOKEN = "access_token";
    private static final String EXPIRES_TOKEN = "expires_token";

    private static final String COMMENTS = "comments";
    private static final String COMMENT_TEXT = "text";
    private static final String STARTING_DATE = "starting_date";
    private static final String ENDING_DATE = "ending_date";
    private static final String PUBLISH_DATE = "publish_date";
    private static final String CANDIDATE_DATE = "candidate_date";

    private static final String SUCCESS = "success";
    private static final String ALREADY_COMMENTED = "already_commented";

    public static final String POSITIVE_VOTE = "positive";
    public static final String NEGATIVE_VOTE = "negative";
    private static final String SALT = "salt";
    public static final String FLAG_SALT = "SALTED";
    public static final String FLAG_POTD = "POTD";
    public static final String FLAG_COMMENTS = "COMMENTS";

    //endpoint
    public static final String URL_POTD = URL_API+"/POTD/";
    public static final String URL_PICTURE = URL_API+"/Picture/";
    private static final String URL_PICTURE_INFO = URL_API+"/PictureInfo";
    private static final String URL_PICTURE_VOTE = URL_API+"/PictureVote";
    private static final String URL_THEMES = URL_API+"/Theme";
    private final String URL_AUTHENTICATION = URL_API+"/Authentication";
    private final String URL_COMMENT = URL_API+"/Comment";
    private final String URL_USERS = URL_API+"/User";

    private TokenInformations tokensInfos;

    public ApiHelper(){
        this.tokensInfos = null;
    }

    public ApiHelper(TokenInformations tokensInfos){
        this.tokensInfos = tokensInfos;
    }

    public TokenInformations getAccessToken(String pseudo, String password, String flags) throws IOException, JSONException, InvalidParameterException, ParseException {
        Uri.Builder parameters = new Uri.Builder()
                .appendQueryParameter(PSEUDO, pseudo)
                .appendQueryParameter(PASSWORD, password);

        if(flags != null)
            parameters.appendQueryParameter(FLAGS, flags);
        boolean isPasswordSalted = flags != null && flags.equals(FLAG_SALT);
        HttpURLConnection http = this.createHttpConnection(URL_AUTHENTICATION, HttpVerb.POST.toString(), parameters, false);

        if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
            JSONObject result = this.getJSONResponse(http);
            if(result.has(ApiHelper.ACCESS_TOKEN) && !result.getString(ApiHelper.ACCESS_TOKEN).isEmpty()){

                if(!isPasswordSalted){
                    if(result.has(ApiHelper.SALT))
                        password = ApplicationHelper.hashPassword(password + result.getString(ApiHelper.SALT));
                    else
                        return null;
                }

                int id = result.has(ApiHelper.ID)?result.getInt(ApiHelper.ID):-1;
                this.tokensInfos = new TokenInformations(result.getString(ApiHelper.ACCESS_TOKEN), ApplicationHelper.convertStringToDate(result.getString(ApiHelper.EXPIRES_TOKEN), false),
                                                         new User(id, pseudo), password, isPasswordSalted);
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
                .appendQueryParameter(PSEUDO, pseudo)
                .appendQueryParameter(PASSWORD, password);
        HttpURLConnection http = this.createHttpConnection(URL_USERS, HttpVerb.PUT.toString(), parameter, false);
        if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
            JSONObject result = this.getJSONResponse(http);
            if(result.has(ApiHelper.ACCESS_TOKEN) && result.has(ApiHelper.EXPIRES_TOKEN) && result.has(ApiHelper.SALT) && result.has(ApiHelper.ID)){
                User user = new User(result.getInt(ApiHelper.ID), pseudo);
                this.tokensInfos = new TokenInformations(result.getString(ApiHelper.ACCESS_TOKEN), ApplicationHelper.convertStringToDate(result.getString(ApiHelper.EXPIRES_TOKEN), false),
                    user, ApplicationHelper.hashPassword(password+result.getString(ApiHelper.SALT)), true);
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
        http.setRequestProperty("Connection", "close");
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
        String temp = response.toString();
        return new JSONObject(response.toString());
    }

    public ArrayList<Picture> getPicturesInfo(Calendar startingDate, Calendar endingDate, String theme, String user, Integer voteCount, String flags) {
        Uri.Builder parameter = new Uri.Builder().appendQueryParameter(FLAGS, flags);

        ArrayList<Picture> pictures = null;
        if(startingDate != null)
            parameter.appendQueryParameter(STARTING_DATE, ApplicationHelper.convertDateToString(startingDate, false));

        if(endingDate != null)
            parameter.appendQueryParameter(ENDING_DATE, ApplicationHelper.convertDateToString(endingDate, false));

        if(theme != null)
            parameter.appendQueryParameter(THEME, theme);

        if(user != null)
            parameter.appendQueryParameter(PSEUDO, user);

        if(voteCount != null)
            parameter.appendQueryParameter(VOTE_COUNT, String.valueOf(voteCount));

        HttpURLConnection http = null;
        try{
            http = this.createHttpConnection(URL_PICTURE_INFO, HttpVerb.GET.toString(), parameter, false);

            if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
                JSONObject result = this.getJSONResponse(http);

                if(result.length() == 0)
                    return null;

                pictures = new ArrayList<>();
                JSONArray picturesObj = result.getJSONArray(ApiHelper.ENTITY_PICTURES);

                JSONObject commentObj, pictureObj;
                JSONArray comments;
                boolean potd;
                int id, userID, positiveVote, negativeVote;
                Picture picture;
                String textComment, pictureTheme;
                User pictureUser;
                Calendar date;
                for(int i = 0, length = picturesObj.length(); i < length; ++i){
                    pictureObj = picturesObj.getJSONObject(i);

                    id = pictureObj.has(ApiHelper.ID)?pictureObj.getInt(ApiHelper.ID):0;
                    pictureTheme = pictureObj.has(ApiHelper.THEME_NAME)?pictureObj.getString(ApiHelper.THEME_NAME):"";

                    userID = pictureObj.has(ApiHelper.USER_ID)?pictureObj.getInt(ApiHelper.USER_ID):0;
                    String pseudo = pictureObj.has(ApiHelper.PSEUDO)?pictureObj.getString(ApiHelper.PSEUDO):"";

                    date = null;
                    if(pictureObj.has(ApiHelper.CANDIDATE_DATE))
                        date = ApplicationHelper.convertStringToDate(pictureObj.getString(ApiHelper.CANDIDATE_DATE), false);

                    positiveVote = pictureObj.has(ApiHelper.POSITIVE_VOTE)?pictureObj.getInt(ApiHelper.POSITIVE_VOTE):0;
                    negativeVote = pictureObj.has(ApiHelper.NEGATIVE_VOTE)?pictureObj.getInt(ApiHelper.NEGATIVE_VOTE):0;
                    potd = pictureObj.has(ApiHelper.POTD)?pictureObj.getInt(ApiHelper.POTD) > 0:false;

                    picture = new Picture(id, new Theme(pictureTheme, date), new User(userID, pseudo), positiveVote, negativeVote, potd);

                    //comments
                    if(pictureObj.has(ApiHelper.COMMENTS)){
                        comments = pictureObj.getJSONArray(ApiHelper.COMMENTS);
                        for(int j = 0, commentLength = comments.length(); j < commentLength; ++j){
                            commentObj = comments.getJSONObject(j);

                            textComment=  commentObj.getString(COMMENT_TEXT);
                            pictureUser = new User(commentObj.getInt(ApiHelper.ID), commentObj.getString(ApiHelper.PSEUDO));
                            Comment comment = new Comment(pictureUser, textComment, ApplicationHelper.convertStringToDate(commentObj.getString(PUBLISH_DATE), true));
                            picture.getComments().add(comment);
                        }

                        Collections.sort(picture.getComments());
                    }

                    pictures.add(picture);
                }
            }else if(http.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST){
                throw new InvalidParameterException();
            }
        }catch (IOException | JSONException e) {
            //bugfix, sometimes catch fired with null exception...
            if(e != null)
                return null;
        } finally {
            if(http != null)
                http.disconnect();
        }

        return pictures;
    }

    public ThemeList getThemes(Calendar candidateDate) {

        HttpURLConnection http = null;
        try {
            String date = ApplicationHelper.convertDateToString(candidateDate, false);
            http = this.createHttpConnection(URL_THEMES+"/"+date, HttpVerb.GET.toString(), null, false);

            if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
                ArrayList<Theme> themeList = new ArrayList<>();
                JSONObject response = this.getJSONResponse(http);

                if(response.length() == 0)
                    return null;

                JSONArray themes = response.getJSONArray(ApiHelper.ENTITY_THEMES);
                for (int i = 0, len = themes.length();i < len; i++) {
                    JSONObject theme = themes.getJSONObject(i);
                    int id = theme.has(ApiHelper.ID)?theme.getInt(ApiHelper.ID):0;
                    String name = theme.has(ApiHelper.THEME_NAME)?theme.getString(ApiHelper.THEME_NAME):"";
                    themeList.add(new Theme(id, name));
                }

                return new ThemeList(themeList);

            }else if(http.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST){
                throw new InvalidParameterException();
            }

        } catch (IOException | JSONException e) {
            return null;
        } finally {
            if(http != null)
                http.disconnect();
        }

        return null;
    }

    public boolean voteForTheme(int theme) throws IOException, JSONException, ParseException {
        HttpURLConnection http = this.createHttpConnection(URL_THEMES+"/"+theme, HttpVerb.PUT.toString(), null, true);
        int resultCode = http.getResponseCode();
        http.disconnect();
        return resultCode == HttpURLConnection.HTTP_OK;
    }

    public Picture voteForPicture(Picture picture, boolean positive) {
        Uri.Builder parameter = new Uri.Builder().appendQueryParameter(POSITIVE_VOTE, positive ? "1" : "0");
        HttpURLConnection http = null;
        try {
            http = this.createHttpConnection(URL_PICTURE_VOTE + "/" + picture.getId(), HttpVerb.POST.toString(), parameter, true);
            int resultCode = http.getResponseCode();

            if (resultCode != HttpURLConnection.HTTP_OK)
                return null;

            JSONObject resultArray = this.getJSONResponse(http);

            if (resultArray.length() == 0)
                return null;

            JSONObject result = resultArray.getJSONArray(ApiHelper.ENTITY_PICTURES).getJSONObject(0);

            if (result.has(ApiHelper.POSITIVE_VOTE))
                picture.setPositiveVote(result.getInt(ApiHelper.POSITIVE_VOTE));

            if (result.has(ApiHelper.NEGATIVE_VOTE))
                picture.setNegativeVote(result.getInt(ApiHelper.NEGATIVE_VOTE));

            return picture;
        } catch (IOException | JSONException e) {
            return null;
        }finally {
            if(http != null)
                http.disconnect();
        }
    }

    public CommentResult addComment(int picture, String text) {
        Uri.Builder parameter = new Uri.Builder().appendQueryParameter(COMMENT_TEXT, text);
        HttpURLConnection http = null;
        try {
            http = this.createHttpConnection(URL_COMMENT + "/" + picture, HttpVerb.POST.toString(), parameter, true);
            int resultCode = http.getResponseCode();

            if (resultCode != HttpURLConnection.HTTP_OK)
                return CommentResult.ERROR;

            JSONObject resultArray = this.getJSONResponse(http);

            if (resultArray.has(ALREADY_COMMENTED))
                return CommentResult.ALREADY_COMMENTED;

            if (resultArray.has(SUCCESS))
                return CommentResult.SUCCESS;

            return CommentResult.ERROR;
        } catch (IOException | JSONException e) {
            return CommentResult.ERROR;
        }finally {
            if(http != null)
                http.disconnect();
        }
    }

    public boolean deleteComment(int picture) {
        HttpURLConnection http = null;
        boolean result = false;
        try {
            http = this.createHttpConnection(URL_COMMENT + "/" + picture, HttpVerb.DELETE.toString(), null, true);
            int resultCode = http.getResponseCode();

            if (resultCode != HttpURLConnection.HTTP_OK)
                return false;

            JSONObject resultArray = this.getJSONResponse(http);


            if (resultArray.has(SUCCESS))
                result =  resultArray.getBoolean(SUCCESS);
        } catch (IOException | JSONException e) {
            result = false;
        }finally {
            if(http != null)
                http.disconnect();
        }

        return result;
    }
}