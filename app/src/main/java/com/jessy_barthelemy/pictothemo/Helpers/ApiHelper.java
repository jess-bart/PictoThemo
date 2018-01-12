package com.jessy_barthelemy.pictothemo.Helpers;

import android.content.Context;
import android.net.Uri;

import com.jessy_barthelemy.pictothemo.ApiObjects.Comment;
import com.jessy_barthelemy.pictothemo.ApiObjects.CommentResult;
import com.jessy_barthelemy.pictothemo.ApiObjects.Picture;
import com.jessy_barthelemy.pictothemo.ApiObjects.Theme;
import com.jessy_barthelemy.pictothemo.ApiObjects.ThemeList;
import com.jessy_barthelemy.pictothemo.ApiObjects.TokenInformation;
import com.jessy_barthelemy.pictothemo.ApiObjects.Trophy;
import com.jessy_barthelemy.pictothemo.ApiObjects.User;
import com.jessy_barthelemy.pictothemo.AsyncInteractions.LogInTask;
import com.jessy_barthelemy.pictothemo.Enum.CommentStatus;
import com.jessy_barthelemy.pictothemo.Enum.HttpVerb;
import com.jessy_barthelemy.pictothemo.Enum.UploadResult;
import com.jessy_barthelemy.pictothemo.Exception.PictothemoException;
import com.jessy_barthelemy.pictothemo.Exception.TokenExpiredException;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ApiHelper {
    private static final String URL_API = "http://192.168.0.10:8080/web";

    //private static final String URL_API = "http://83.154.88.156/Pictothemo";
    //Entity
    private static final String ENTITY_TROPHIES = "trophies";
    private static final String ENTITY_USER= "user";

    //Authentication fields
    private static final String THEME_NAME = "name";
    private static final String TROPHY_TITLE = "title";
    private static final String TROPHY_DESCRIPTION = "description";
    private static final String ID = "id";
    private static final String PROFIL_ID = "profilId";
    private static final String PSEUDO = "pseudo";
    private static final String VALIDATED = "validated";
    private static final String REGISTRATION_DATE = "registrationDate";
    private static final String FLAGS = "flags";
    private static final String PASSWORD = "password";
    private static final String THEME = "theme";
    private static final String POTD = "potd";
    private static final String VOTE_COUNT = "voteCount";

    private static final String ACCESS_TOKEN = "accessToken";
    private static final String EXPIRES_TOKEN = "expiresToken";

    private static final String COMMENTS = "comments";
    private static final String COMMENT_TEXT = "text";
    private static final String STARTING_DATE = "startingDate";
    private static final String ENDING_DATE = "endingDate";
    private static final String PUBLISH_DATE = "publishDate";
    private static final String CANDIDATE_DATE = "candidateDate";

    private static final String SUCCESS = "success";
    private static final String FORMAT_ERROR = "format";
    private static final String SIZE_ERROR = "size";
    private static final String ENTITY_ALREADY_EXISTS = "ENTITY_ALREADY_EXISTS";

    private static final String POSITIVE_VOTE = "positives";
    private static final String NEGATIVE_VOTE = "negatives";
    private static final String SALT = "salt";
    public static final String FLAG_SALT = "SALTED";

    //error
    private static final String CODE = "code";

    //endpoint
    private static final String URL_PICTURE_INFO = URL_API+"/picture";
    public static final String URL_PICTURE = URL_PICTURE_INFO+"/content";
    public static final String URL_PICTURE_DATE = URL_PICTURE+"/date";

    private static final String URL_PICTURE_VOTE = URL_PICTURE_INFO+"/vote";
    private static final String URL_THEMES = URL_API+"/theme";
    private static final String URL_USERS = URL_API+"/user";
    private static final String URL_USERS_BY_ID = URL_USERS+"/id/%s";
    private static final String URL_AUTHENTICATION = URL_USERS+"/authentication";
    private static final String URL_COMMENT = URL_PICTURE_INFO+"/comment";

    private TokenInformation tokensInfos;

    public ApiHelper(){
        this.tokensInfos = null;
    }

    public ApiHelper(TokenInformation tokensInfos){
        this.tokensInfos = tokensInfos;
    }

    public TokenInformation getAccessToken(String pseudo, String password, String flags) throws IOException, JSONException, PictothemoException, ParseException {
        Uri.Builder parameters = null;

        if(flags != null){
            parameters = new Uri.Builder();
            parameters.appendQueryParameter(FLAGS, flags);
        }

        boolean isPasswordSalted = flags != null && flags.equals(FLAG_SALT);
        HashMap<String, String> headers = new HashMap<>();
        headers.put(PSEUDO, pseudo);
        headers.put(PASSWORD, password);

        HttpURLConnection http = this.createHttpConnection(URL_AUTHENTICATION, HttpVerb.POST.toString(), parameters, headers, false);

        if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
            JSONObject result = this.getJSONObjectResponse(http, false);
            http.disconnect();
            if(result.has(ApiHelper.ACCESS_TOKEN) && !result.getString(ApiHelper.ACCESS_TOKEN).isEmpty()){

                if(!isPasswordSalted){
                    if(result.has(ApiHelper.SALT))
                        password = ApplicationHelper.hashPassword(password + result.getString(ApiHelper.SALT));
                    else
                        return null;
                }

                int id = result.has(ApiHelper.ID)?result.getInt(ApiHelper.ID):-1;
                this.tokensInfos = new TokenInformation(result.getString(ApiHelper.ACCESS_TOKEN), ApplicationHelper.convertStringToDate(result.getString(ApiHelper.EXPIRES_TOKEN), false),
                                                         new User(id, pseudo), password, isPasswordSalted);
                return this.tokensInfos;
            }else{
                return null;
            }
        }else{
            JSONObject response = this.getJSONObjectResponse(http, true);
            http.disconnect();
            throw new PictothemoException(response);
        }
    }

    public void setTokensInfo(TokenInformation tokensInfo) {
        this.tokensInfos = tokensInfo;
    }


    public void validateToken(Context context, TokenInformation tokenInfos, IAsyncResponse delegate){
        if(Calendar.getInstance().after(tokenInfos.getExpiresToken())){
            this.refreshToken(context, tokenInfos, delegate);
        }else{
            delegate.asyncTaskSuccess();
        }
    }

    private void refreshToken(Context context, TokenInformation tokenInfos, IAsyncResponse delegate){
        LogInTask login = new LogInTask(context, tokenInfos, false);
        login.setDelegate(delegate);
        login.execute();
    }

    private HttpURLConnection createHttpConnection(String requestUrl, String method, Uri.Builder parameters, HashMap<String, String> headers, final boolean authorization) throws IOException{
        return this.createHttpConnection(requestUrl, method, parameters, null, headers, authorization);
    }

    private HttpURLConnection createHttpConnection(String requestUrl, String method, Uri.Builder parameters, String jsonParam,  HashMap<String, String> headers, final boolean authorization) throws IOException{
        URL url;
        if(method.equals(HttpVerb.GET.toString()) && parameters != null)
            url = new URL(requestUrl+parameters.toString());
        else
            url = new URL(requestUrl);

        HttpURLConnection  http = (HttpURLConnection)url.openConnection();
        http.setRequestMethod(method);
        http.setRequestProperty("Connection", "close");

        http.addRequestProperty("Accept-language", Locale.getDefault().getCountry());

        if(headers != null)
            for (Map.Entry<String, String > entry : headers.entrySet())
                http.addRequestProperty(entry.getKey(), entry.getValue());

        //headers
        if(authorization && this.tokensInfos != null)
            http.addRequestProperty("Authorization", this.tokensInfos.getAccessToken());

        if(method.equals(HttpVerb.POST.toString()) || method.equals(HttpVerb.PUT.toString())){
            http.setDoOutput(true);
            if(parameters != null){
                http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                OutputStream os = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(parameters.build().getEncodedQuery());

                writer.flush();
                writer.close();
                os.close();
            }
        }

        if(jsonParam != null){
            OutputStreamWriter stream= new OutputStreamWriter(http.getOutputStream());
            stream.write(jsonParam);
            stream.flush();
            stream.close();
        }

        http.connect();
        return http;
    }

    private String getJSONResponse(HttpURLConnection http, boolean error) throws IOException, JSONException {
        BufferedReader in = new BufferedReader(new InputStreamReader((error) ? http.getErrorStream() : http.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    private JSONObject getJSONObjectResponse(HttpURLConnection http, boolean error) throws IOException, JSONException {
        return new JSONObject(this.getJSONResponse(http, error));
    }

    private JSONArray getJSONArrayResponse(HttpURLConnection http) throws IOException, JSONException {
        return new JSONArray(this.getJSONResponse(http, false));
    }

    public User getUser(long id) {

        HttpURLConnection http = null;
        try {
            http = this.createHttpConnection(String.format(URL_USERS_BY_ID, id), HttpVerb.GET.toString(), null, null, true);

            if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
                JSONObject userObj = this.getJSONObjectResponse(http, false);

                if(userObj.length() == 0)
                    return null;

                String name = userObj.has(ApiHelper.PSEUDO)?userObj.getString(ApiHelper.PSEUDO):"";
                int profile = userObj.has(ApiHelper.PROFIL_ID)?userObj.getInt(ApiHelper.PROFIL_ID):0;
                String date = userObj.has(ApiHelper.REGISTRATION_DATE)?userObj.getString(ApiHelper.REGISTRATION_DATE):"";
                User user = new User(id, name, ApplicationHelper.convertStringToDate(date, false), profile);

                int trophyId;
                boolean validated;
                String title;
                String description;
                JSONObject trophy;
                JSONArray trophies = userObj.getJSONArray(ENTITY_TROPHIES);
                for (int i = 0, len = trophies.length();i < len; i++) {
                    trophy = trophies.getJSONObject(i);
                    trophyId = trophy.has(ID)?trophy.getInt(ID):0;
                    title = trophy.has(TROPHY_TITLE)?trophy.getString(TROPHY_TITLE):"";
                    description = trophy.has(TROPHY_DESCRIPTION)?trophy.getString(TROPHY_DESCRIPTION):"";
                    validated = trophy.has(VALIDATED) && trophy.getBoolean(VALIDATED);
                    user.addTrophy(new Trophy(trophyId, title, description, validated));
                }

                return user;

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

    public boolean setUser(User user) throws TokenExpiredException{
        HttpURLConnection http = null;
        try {
            JSONObject userObj = new JSONObject();
            userObj.put(ID, user.getId());
            userObj.put(PROFIL_ID, user.getProfil());
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Accept", "application/json");
            http = this.createHttpConnection(URL_USERS, HttpVerb.PUT.toString(), null, userObj.toString(), headers, true);

            int responseCode = http.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_FORBIDDEN)
                throw new TokenExpiredException();

            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (IOException | JSONException e) {
            return false;
        } finally {
            if(http != null)
                http.disconnect();
        }
    }

    public TokenInformation createUser(String pseudo, String password) throws IOException, JSONException, ParseException, PictothemoException {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(PSEUDO, pseudo);
        headers.put(PASSWORD, password);
        HttpURLConnection http = this.createHttpConnection(URL_USERS, HttpVerb.POST.toString(), null, headers, false);
        if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
            JSONObject result = this.getJSONObjectResponse(http, false);
            http.disconnect();
            if(result.has(ApiHelper.ACCESS_TOKEN) && result.has(ApiHelper.EXPIRES_TOKEN) && result.has(ApiHelper.SALT) && result.has(ApiHelper.ID)){
                User user = new User(result.getInt(ApiHelper.ID), pseudo);
                this.tokensInfos = new TokenInformation(result.getString(ApiHelper.ACCESS_TOKEN), ApplicationHelper.convertStringToDate(result.getString(ApiHelper.EXPIRES_TOKEN), false),
                    user, ApplicationHelper.hashPassword(password+result.getString(ApiHelper.SALT)), true);
                return this.tokensInfos;
            }
        }else{
            JSONObject response = this.getJSONObjectResponse(http, true);
            http.disconnect();
            throw new PictothemoException(response);
        }

        return null;
    }

    public ArrayList<Picture> getPicturesInfo(Calendar startingDate, Calendar endingDate, String theme, String user, Integer voteCount, Boolean potd) {
        Uri.Builder parameter = new Uri.Builder();

        ArrayList<Picture> pictures = null;
        if(startingDate != null)
            parameter.appendQueryParameter(STARTING_DATE, ApplicationHelper.convertDateToString(startingDate, false, false));

        if(endingDate != null)
            parameter.appendQueryParameter(ENDING_DATE, ApplicationHelper.convertDateToString(endingDate, false, false));

        if(theme != null && !theme.isEmpty())
            parameter.appendQueryParameter(THEME, theme);

        if(user != null && !user.isEmpty())
            parameter.appendQueryParameter(ENTITY_USER, user);

        if(voteCount != null)
            parameter.appendQueryParameter(VOTE_COUNT, String.valueOf(voteCount));

        if(potd != null)
            parameter.appendQueryParameter(POTD, potd.toString());

        HttpURLConnection http = null;
        try{
            http = this.createHttpConnection(URL_PICTURE_INFO, HttpVerb.GET.toString(), parameter, null, false);

            if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
                JSONArray picturesObj = this.getJSONArrayResponse(http);

                if(picturesObj.length() == 0)
                    return null;

                pictures = new ArrayList<>();

                JSONObject commentObj, pictureObj;
                JSONArray comments;
                int userID = -1;
                int id, positiveVote, negativeVote;
                Picture picture;
                String textComment;
                String pictureTheme = null;
                String pseudo = null;
                int profil = 0;
                User pictureUser;
                Calendar date;
                JSONObject themeObj;
                JSONObject userObj;
                for(int i = 0, length = picturesObj.length(); i < length; ++i){
                    pictureObj = picturesObj.getJSONObject(i);
                    date = null;
                    id = pictureObj.has(ApiHelper.ID)?pictureObj.getInt(ApiHelper.ID):0;

                    themeObj = pictureObj.has(ApiHelper.THEME)?pictureObj.getJSONObject(ApiHelper.THEME):null;
                    if(themeObj != null){
                        pictureTheme = themeObj.has(ApiHelper.THEME_NAME)?themeObj.getString(ApiHelper.THEME_NAME):"";
                        if(themeObj.has(ApiHelper.CANDIDATE_DATE))
                            date = ApplicationHelper.convertStringToDate(themeObj.getString(ApiHelper.CANDIDATE_DATE), false);
                    }

                    userObj = pictureObj.has(ApiHelper.ENTITY_USER)?pictureObj.getJSONObject(ApiHelper.ENTITY_USER):null;
                    if(userObj != null){
                        userID = userObj.has(ApiHelper.ID)?userObj.getInt(ApiHelper.ID):0;
                        pseudo = userObj.has(ApiHelper.PSEUDO)?userObj.getString(ApiHelper.PSEUDO):"";
                        profil = userObj.has(ApiHelper.PROFIL_ID)?userObj.getInt(ApiHelper.PROFIL_ID):0;
                    }

                    positiveVote = pictureObj.has(ApiHelper.POSITIVE_VOTE)?pictureObj.getInt(ApiHelper.POSITIVE_VOTE):0;
                    negativeVote = pictureObj.has(ApiHelper.NEGATIVE_VOTE)?pictureObj.getInt(ApiHelper.NEGATIVE_VOTE):0;
                    potd = pictureObj.has(ApiHelper.POTD) && pictureObj.getBoolean(ApiHelper.POTD);

                    picture = new Picture(id, new Theme(pictureTheme, date), new User(userID, pseudo, profil), positiveVote, negativeVote, potd);

                    //comments
                    if(pictureObj.has(ApiHelper.COMMENTS)){
                        comments = pictureObj.getJSONArray(ApiHelper.COMMENTS);
                        for(int j = 0, commentLength = comments.length(); j < commentLength; ++j){
                            commentObj = comments.getJSONObject(j);

                            textComment=  commentObj.getString(COMMENT_TEXT);

                            userObj = commentObj.has(ApiHelper.ENTITY_USER)?commentObj.getJSONObject(ApiHelper.ENTITY_USER):null;
                            if(userObj != null){
                                userID = userObj.has(ApiHelper.ID)?userObj.getInt(ApiHelper.ID):0;
                                pseudo = userObj.has(ApiHelper.PSEUDO)?userObj.getString(ApiHelper.PSEUDO):"";
                                profil = userObj.has(ApiHelper.PROFIL_ID)?userObj.getInt(ApiHelper.PROFIL_ID):0;
                            }

                            pictureUser = new User(userID, pseudo, profil);
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
            String date = ApplicationHelper.convertDateToString(candidateDate, false, false);
            http = this.createHttpConnection(URL_THEMES+"/"+date, HttpVerb.GET.toString(), null, null, false);

            if(http.getResponseCode() == HttpURLConnection.HTTP_OK){
                ArrayList<Theme> themeList = new ArrayList<>();
                JSONArray themes = this.getJSONArrayResponse(http);

                if(themes.length() == 0)
                    return null;

                String name;
                int id;
                JSONObject theme;
                for (int i = 0, len = themes.length();i < len; i++) {
                    theme = themes.getJSONObject(i);
                    id = theme.has(ApiHelper.ID)?theme.getInt(ApiHelper.ID):0;
                    name = theme.has(ApiHelper.THEME_NAME)?theme.getString(ApiHelper.THEME_NAME):"";
                    date = theme.has(ApiHelper.CANDIDATE_DATE)?theme.getString(ApiHelper.CANDIDATE_DATE):"";
                    themeList.add(new Theme(id, name, ApplicationHelper.convertStringToDate(date, false)));
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

    public boolean voteForTheme(int theme) throws IOException, JSONException, ParseException, TokenExpiredException {
        HttpURLConnection http = this.createHttpConnection(URL_THEMES+"/"+theme, HttpVerb.POST.toString(), null, null, true);
        int resultCode = http.getResponseCode();
        http.disconnect();
        if(resultCode == HttpURLConnection.HTTP_FORBIDDEN)
            throw new TokenExpiredException();

        return resultCode == HttpURLConnection.HTTP_OK;
    }

    public Picture voteForPicture(Picture picture, boolean positive) throws TokenExpiredException{
        HttpURLConnection http = null;
        try {
            http = this.createHttpConnection(String.format("%s/%s/%s", URL_PICTURE_VOTE, picture.getId(), positive), HttpVerb.POST.toString(), null, null, true);
            int resultCode = http.getResponseCode();

            if(resultCode == HttpURLConnection.HTTP_FORBIDDEN)
                throw new TokenExpiredException();

            if (resultCode != HttpURLConnection.HTTP_OK)
                return null;

            JSONObject pictureObj = this.getJSONObjectResponse(http, false);

            if (pictureObj.length() == 0)
                return null;

            if (pictureObj.has(ApiHelper.POSITIVE_VOTE))
                picture.setPositiveVote(pictureObj.getInt(ApiHelper.POSITIVE_VOTE));

            if (pictureObj.has(ApiHelper.NEGATIVE_VOTE))
                picture.setNegativeVote(pictureObj.getInt(ApiHelper.NEGATIVE_VOTE));

            return picture;
        } catch (IOException | JSONException e) {
            return null;
        }finally {
            if(http != null)
                http.disconnect();
        }
    }

    public CommentResult addComment(int picture, String text) throws TokenExpiredException{
        CommentResult result = new CommentResult();
        result.setResult(CommentStatus.SUCCESS);

        Uri.Builder parameter = new Uri.Builder().appendQueryParameter(COMMENT_TEXT, text);
        HttpURLConnection http = null;
        try {
            http = this.createHttpConnection(URL_COMMENT + "/" + picture, HttpVerb.POST.toString(), parameter, null, true);
            int resultCode = http.getResponseCode();

            JSONObject resultArray;
            if(resultCode == HttpURLConnection.HTTP_FORBIDDEN)
                throw new TokenExpiredException();

            if (resultCode != HttpURLConnection.HTTP_OK){
                resultArray = this.getJSONObjectResponse(http, true);

                if (resultArray.has(CODE) && resultArray.getString(CODE).equalsIgnoreCase(ENTITY_ALREADY_EXISTS))
                    result.setResult(CommentStatus.ALREADY_COMMENTED);

                return result;
            }

            resultArray = this.getJSONObjectResponse(http, false);
            JSONObject userObj = resultArray.getJSONObject(ENTITY_USER);
            if(userObj != null){
                long id = (userObj.has(ID) ? userObj.getLong(ID) : -1);
                String pseudo = (userObj.has(PSEUDO) ? userObj.getString(PSEUDO) : null);
                int profil = (userObj.has(PROFIL_ID) ? userObj.getInt(PROFIL_ID) : -1);
                User user = new User(id, pseudo, profil);
                Comment comment = new Comment(user, text, Calendar.getInstance());
                comment.setText(text);

                result.setComment(comment);
            }
        } catch (IOException | JSONException e) {
            result.setResult(CommentStatus.ERROR);
        }
        finally {
            if(http != null)
                http.disconnect();
        }

        return result;
    }

    public boolean deleteComment(int picture) throws TokenExpiredException{
        HttpURLConnection http = null;
        boolean result = false;
        try {
            http = this.createHttpConnection(URL_COMMENT + "/" + picture, HttpVerb.DELETE.toString(), null, null, true);
            int resultCode = http.getResponseCode();

            if(resultCode == HttpURLConnection.HTTP_FORBIDDEN)
                throw new TokenExpiredException();

            if (resultCode != HttpURLConnection.HTTP_OK)
                return false;

            JSONObject resultArray = this.getJSONObjectResponse(http, false);

            if (resultArray.has(SUCCESS) && resultArray.getBoolean(SUCCESS))
                result =  resultArray.getBoolean(SUCCESS);
        } catch (IOException | JSONException e) {
            result = false;
        }finally {
            if(http != null)
                http.disconnect();
        }

        return result;
    }

    public UploadResult uploadFile(InputStream fileInputStream, String filename) throws IOException, JSONException, ParseException{

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";

        URL url = new URL(URL_PICTURE);
        HttpURLConnection http = (HttpURLConnection)url.openConnection();
        http.setRequestMethod(HttpVerb.POST.toString());
        http.setReadTimeout(15000);
        http.setConnectTimeout(15000);

        int read, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1048576;

        http = (HttpURLConnection) url.openConnection();

        http.setDoInput(true);
        http.setDoOutput(true);
        http.setUseCaches(false);
        http.setRequestProperty("Connection", "Keep-Alive");
        http.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
        http.setRequestProperty("Authorization", this.tokensInfos.getAccessToken());

        DataOutputStream dos = new DataOutputStream(http.getOutputStream());

        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Disposition: form-data; name=\"picture\";filename=\"" + filename +"\"" + lineEnd);
        dos.writeBytes(lineEnd);

        bytesAvailable = fileInputStream.available();
        bufferSize = Math.min(bytesAvailable, maxBufferSize);
        buffer = new byte[bufferSize];

        while ((read = fileInputStream.read(buffer)) != -1) {
            dos.write(buffer, 0, read);
        }

        dos.writeBytes(lineEnd);
        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
        int resultCode = http.getResponseCode();

        if(resultCode == HttpURLConnection.HTTP_OK) {
            JSONObject result = this.getJSONObjectResponse(http, false);

            fileInputStream.close();
            dos.flush();
            dos.close();

            if(result.length() == 0 )
                return UploadResult.ERROR;
            else if (result.has(FORMAT_ERROR))
                return UploadResult.FORMAT_ERROR;
            else if (result.has(SIZE_ERROR))
                return UploadResult.SIZE_ERROR;
            else if (result.has(SUCCESS) && result.getBoolean(SUCCESS))
                return UploadResult.SUCCESS;
        }

        return UploadResult.ERROR;
    }
}