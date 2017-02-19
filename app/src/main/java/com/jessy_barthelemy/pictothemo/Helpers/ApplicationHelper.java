package com.jessy_barthelemy.pictothemo.Helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.jessy_barthelemy.pictothemo.Api.TokenInformations;
import com.jessy_barthelemy.pictothemo.LoginActivity;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ApplicationHelper {
    private static final String PICTOTHEMO_PREFS = "PICTOTHEMO_PREFS";
    private static final String USER_EMAIL_PREF = "USER_EMAIL";
    private static final String USER_PASSWORD_PREF = "USER_PASSWORD";
    private static final String USER_TOKEN_PREF = "USER_TOKEN";
    private static final String USER_EXPIRES_TOKEN_PREF = "USER_EXPIRES_TOKEN";
    static final int PASSWORD_MAX_LENGTH = 6;
    private static final String MYSQL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    //Static helper methods
    public static String hashPassword(String password) throws ParseException {
        if(password == null || password.isEmpty()) return "";

        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.reset();
        m.update(password.getBytes());
        byte[] digest = m.digest();
        BigInteger bigInt = new BigInteger(1,digest);
        String hashText = bigInt.toString(16);
        while(hashText.length() < 32 ){
            hashText = "0"+hashText;
        }

        return hashText;
    }

    public static void savePreferences(Context context, TokenInformations tokenInfos){
        SharedPreferences settings = context.getSharedPreferences(ApplicationHelper.PICTOTHEMO_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(ApplicationHelper.USER_EMAIL_PREF, tokenInfos.getEmail());
        editor.putString(ApplicationHelper.USER_PASSWORD_PREF, tokenInfos.getPassword());
        editor.putString(ApplicationHelper.USER_TOKEN_PREF, tokenInfos.getAccessToken());
        if(tokenInfos.getExpiresToken() != null){
            SimpleDateFormat df = new SimpleDateFormat(ApplicationHelper.MYSQL_DATE_FORMAT);
            editor.putString(ApplicationHelper.USER_EXPIRES_TOKEN_PREF, df.format(tokenInfos.getExpiresToken()));
        }
        editor.apply();
    }

    public static TokenInformations getTokenInformations(Context context){
        SharedPreferences settings = context.getSharedPreferences(ApplicationHelper.PICTOTHEMO_PREFS, Context.MODE_PRIVATE);
        String accessToken = settings.getString(ApplicationHelper.USER_TOKEN_PREF, "");
        String expiresToken = settings.getString(ApplicationHelper.USER_EXPIRES_TOKEN_PREF, "");
        String email = settings.getString(ApplicationHelper.USER_EMAIL_PREF, "");
        String password = settings.getString(ApplicationHelper.USER_PASSWORD_PREF, "");

        return new TokenInformations(accessToken, expiresToken, email, password, !accessToken.isEmpty());
    }

    public static void resetPreferences(Context context){
        SharedPreferences settings = context.getSharedPreferences(ApplicationHelper.PICTOTHEMO_PREFS, Context.MODE_PRIVATE);
        settings.edit().clear().apply();
    }

    public static void restartApp(Context context){
        Intent i = new Intent(context, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public static Date convertStringToDate(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(ApplicationHelper.MYSQL_DATE_FORMAT);
        return formatter.parse(date);
    }

}
