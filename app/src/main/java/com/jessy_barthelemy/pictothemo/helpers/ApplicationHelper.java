package com.jessy_barthelemy.pictothemo.helpers;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.jessy_barthelemy.pictothemo.activities.LoginActivity;
import com.jessy_barthelemy.pictothemo.apiObjects.TokenInformation;
import com.jessy_barthelemy.pictothemo.apiObjects.User;
import com.jessy_barthelemy.pictothemo.asyncInteractions.GetImageTask;
import com.jessy_barthelemy.pictothemo.R;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ApplicationHelper {
    private static final String USER_ID_PREF = "USER_ID";
    private static final String USER_PSEUDO_PREF = "USER_PSEUDO";
    private static final String USER_PASSWORD_PREF = "USER_PASSWORD";
    private static final String USER_TOKEN_PREF = "USER_TOKEN";
    private static final String USER_EXPIRES_TOKEN_PREF = "USER_EXPIRES_TOKEN";
    public static final String TUTORIAL_PREF = "TUTORIAL_PREF";
    public static final String REVIEW_PREF = "REVIEW_PREF";
    public static final String PICTOTHEMO_PREFS = "PICTOTHEMO_PREFS";
    static final int PSEUDO_MAX_LENGTH = 4;
    static final int PASSWORD_MAX_LENGTH = 6;
    public static final int UPDATE_PICTURE = 1;
    public static final String THEME_PREFS_PREFIX = "theme";
    public static final String EXTRA_PICTURES_LIST = "pictures";
    public static final String PREF_WALLPAPER_DATE = "wallpaperLastChange";

    public static final String DEFAULT_PICTURE_FORMAT = ".png";
    public static final int UPLOAD_IMAGE_COMPRESSION = 50;

    public static final String MYSQL_DATE_FORMAT = "yyyy-MM-dd";
    private static final String MYSQL_DATE_LONG_FORMAT = "yyyy-MM-dd HH:mm:ss";

    //Static helper methods
    static String hashPassword(String password) throws ParseException {
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
            StringBuilder builder = new StringBuilder();
            while(hashText.length() < 32 ){
                builder.append("0");
            }

            hashText = builder.append(hashText).toString();
        }

        return hashText.toUpperCase();
    }

    public static void savePreferences(Context context, TokenInformation tokenInfos){
        SharedPreferences settings = context.getSharedPreferences(ApplicationHelper.PICTOTHEMO_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putLong(ApplicationHelper.USER_ID_PREF, tokenInfos.getUser().getId());
        editor.putString(ApplicationHelper.USER_PSEUDO_PREF, tokenInfos.getUser().getPseudo());
        editor.putString(ApplicationHelper.USER_PASSWORD_PREF, tokenInfos.getPassword());
        editor.putString(ApplicationHelper.USER_TOKEN_PREF, tokenInfos.getAccessToken());
        if(tokenInfos.getExpiresToken() != null){
            SimpleDateFormat df = new SimpleDateFormat(ApplicationHelper.MYSQL_DATE_FORMAT, Locale.getDefault());
            editor.putString(ApplicationHelper.USER_EXPIRES_TOKEN_PREF, df.format(tokenInfos.getExpiresToken().getTime()));
        }
        editor.apply();
    }

    public static TokenInformation getTokenInformations(Context context){
        if(context == null)
            return null;

        SharedPreferences settings = context.getSharedPreferences(ApplicationHelper.PICTOTHEMO_PREFS, Context.MODE_PRIVATE);
        String accessToken = settings.getString(ApplicationHelper.USER_TOKEN_PREF, "");
        String expiresToken = settings.getString(ApplicationHelper.USER_EXPIRES_TOKEN_PREF, "");
        long id = settings.getLong(ApplicationHelper.USER_ID_PREF, -1);
        String pseudo = settings.getString(ApplicationHelper.USER_PSEUDO_PREF, "");
        String password = settings.getString(ApplicationHelper.USER_PASSWORD_PREF, "");
        Calendar expiresDate;
        expiresDate = ApplicationHelper.convertStringToDate(expiresToken, false);

        return new TokenInformation(accessToken, expiresDate, new User(id, pseudo), password, !accessToken.isEmpty());
    }

    public static User getCurrentUser(Context context){
        SharedPreferences settings = context.getSharedPreferences(ApplicationHelper.PICTOTHEMO_PREFS, Context.MODE_PRIVATE);
        long id = settings.getLong(ApplicationHelper.USER_ID_PREF, -1);
        String pseudo = settings.getString(ApplicationHelper.USER_PSEUDO_PREF, "");

        return new User(id, pseudo);
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

    static Calendar convertStringToDate(String date, boolean longFormat){
        SimpleDateFormat formatter = new SimpleDateFormat((longFormat)?MYSQL_DATE_LONG_FORMAT:MYSQL_DATE_FORMAT, Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(formatter.parse(date));
            return calendar;
        } catch (ParseException e) {
            return null;
        }
    }

    public static String convertDateToString(Calendar date, boolean longFormat, boolean presentationFormat){
        if(date == null)
            return null;

        if(presentationFormat){
            DateFormat df = DateFormat.getDateInstance((longFormat)? DateFormat.SHORT:DateFormat.LONG, Locale.getDefault());
            return df.format(date.getTime());
        }else{
            String format = longFormat?MYSQL_DATE_LONG_FORMAT:MYSQL_DATE_FORMAT;
            SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
            return formatter.format(date.getTime());
        }
    }

    public static String handleUnknowPseudo(Context ctx, String pseudo){
        if(pseudo == null || pseudo.isEmpty())
            return ctx.getResources().getString(R.string.user_unknow);
        else
            return pseudo;
    }

    public static boolean hasToChangeBackgroundToday(Context context, boolean wallpaperPreference){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String lastWallpaperDate = prefs.getString(PREF_WALLPAPER_DATE, null);

        SimpleDateFormat dateFormat = new SimpleDateFormat(MYSQL_DATE_FORMAT, Locale.getDefault());
        String today = dateFormat.format(new Date());

        return (prefs.getBoolean(context.getString(R.string.settings_wallpaper_key), false) || wallpaperPreference) && !today.equals(lastWallpaperDate);
    }

    public static void setWallpaper(final Context context, final Bitmap image){
        Thread thread = new Thread() {
            @Override
            public void run() {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(context.getApplicationContext());
            try {
                if(image != null)
                    wallpaperManager.setBitmap(image);
            } catch (IOException ignored) {}
            }
        };

        thread.start();
    }

    public static void setTutorialPref(Context context, boolean showTutorial){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(ApplicationHelper.TUTORIAL_PREF, showTutorial);
        edit.apply();
    }

    public static boolean hasToShowReview(Context context){
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean alreadyReviewed = prefs.getBoolean(REVIEW_PREF, false);
            //date + 7 days
            return !alreadyReviewed && System.currentTimeMillis() > context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .firstInstallTime + 604800000;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void setShowReview(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(REVIEW_PREF, true);
        edit.apply();
    }

    public static void changeBackgroundIfNeeded(Context context){
        boolean hasToChangeBackground = ApplicationHelper.hasToChangeBackgroundToday(context, false);
        if (isOnline(context) && hasToChangeBackground){
            GetImageTask imageTask = new GetImageTask(context, null, null, Calendar.getInstance(), true);
            imageTask.execute();
        }
    }

    public static boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void setTitleBarColor(AppCompatActivity activity, int colorStatusBar, int colorTitleBar){
        activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(activity.getResources().getColor(colorTitleBar)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(activity, colorStatusBar));
        }
    }
}
