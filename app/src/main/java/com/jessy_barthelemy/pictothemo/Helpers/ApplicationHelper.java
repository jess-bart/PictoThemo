package com.jessy_barthelemy.pictothemo.Helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.jessy_barthelemy.pictothemo.Activities.LoginActivity;
import com.jessy_barthelemy.pictothemo.ApiObjects.TokenInformations;
import com.jessy_barthelemy.pictothemo.R;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ApplicationHelper {
    private static final String USER_PSEUDO_PREF = "USER_PSEUDO";
    private static final String USER_PASSWORD_PREF = "USER_PASSWORD";
    private static final String USER_TOKEN_PREF = "USER_TOKEN";
    private static final String USER_EXPIRES_TOKEN_PREF = "USER_EXPIRES_TOKEN";
    static final int PSEUDO_MAX_LENGTH = 4;
    static final int PASSWORD_MAX_LENGTH = 6;
    public static final String PICTOTHEMO_PREFS = "PICTOTHEMO_PREFS";
    public static final String THEME_PREFS_PREFIX = "theme";
    public static final String EXTRA_PICTURES_LIST = "pictures";

    public static final String DEFAULT_PICTURE_FORMAT = ".png";

    private static final String MYSQL_DATE_FORMAT = "yyyy-MM-dd";
    private static final String MYSQL_DATE_LONG_FORMAT = "yyyy-MM-dd HH:mm:ss";

    //Static helper methods
    public static String hashPassword(String password) throws ParseException {
        if(password == null || password.isEmpty()) return "";
        String hashText = null;
        MessageDigest m;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

        if(m != null){
            m.reset();
            m.update(password.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1,digest);
            hashText = bigInt.toString(16);
            while(hashText.length() < 32 ){
                hashText = "0"+hashText;
            }
        }

        return hashText;
    }

    public static void savePreferences(Context context, TokenInformations tokenInfos){
        SharedPreferences settings = context.getSharedPreferences(ApplicationHelper.PICTOTHEMO_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(ApplicationHelper.USER_PSEUDO_PREF, tokenInfos.getPseudo());
        editor.putString(ApplicationHelper.USER_PASSWORD_PREF, tokenInfos.getPassword());
        editor.putString(ApplicationHelper.USER_TOKEN_PREF, tokenInfos.getAccessToken());
        if(tokenInfos.getExpiresToken() != null){
            SimpleDateFormat df = new SimpleDateFormat(ApplicationHelper.MYSQL_DATE_FORMAT, Locale.getDefault());
            editor.putString(ApplicationHelper.USER_EXPIRES_TOKEN_PREF, df.format(tokenInfos.getExpiresToken().getTime()));
        }
        editor.apply();
    }

    public static TokenInformations getTokenInformations(Context context){
        SharedPreferences settings = context.getSharedPreferences(ApplicationHelper.PICTOTHEMO_PREFS, Context.MODE_PRIVATE);
        String accessToken = settings.getString(ApplicationHelper.USER_TOKEN_PREF, "");
        String expiresToken = settings.getString(ApplicationHelper.USER_EXPIRES_TOKEN_PREF, "");
        String pseudo = settings.getString(ApplicationHelper.USER_PSEUDO_PREF, "");
        String password = settings.getString(ApplicationHelper.USER_PASSWORD_PREF, "");
        Calendar expiresDate;
        try {
            expiresDate = ApplicationHelper.convertStringToDate(expiresToken, false);
        } catch (ParseException e) {
            return null;
        }

        return new TokenInformations(accessToken, expiresDate, pseudo, password, !accessToken.isEmpty());
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

    public static Calendar convertStringToDate(String date, boolean longFormat) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat((longFormat)?MYSQL_DATE_LONG_FORMAT:MYSQL_DATE_FORMAT, Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(formatter.parse(date));
        return calendar;
    }

    public static String convertDateToString(Calendar date, boolean longFormat) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat((longFormat)?MYSQL_DATE_LONG_FORMAT:MYSQL_DATE_FORMAT, Locale.getDefault());
        return formatter.format(date.getTime());
    }

    public static String handleUnknowPseudo(Context ctx, String pseudo){
        if(pseudo == null || pseudo.isEmpty())
            return ctx.getResources().getString(R.string.user_unknow);
        else
            return pseudo;
    }
}
