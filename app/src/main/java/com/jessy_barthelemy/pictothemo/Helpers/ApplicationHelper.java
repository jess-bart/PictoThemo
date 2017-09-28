package com.jessy_barthelemy.pictothemo.Helpers;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;

import com.jessy_barthelemy.pictothemo.Activities.LoginActivity;
import com.jessy_barthelemy.pictothemo.ApiObjects.TokenInformations;
import com.jessy_barthelemy.pictothemo.ApiObjects.User;
import com.jessy_barthelemy.pictothemo.R;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    static final int PSEUDO_MAX_LENGTH = 4;
    static final int PASSWORD_MAX_LENGTH = 6;
    public static final int UPDATE_PICTURE = 1;
    public static final String PICTOTHEMO_PREFS = "PICTOTHEMO_PREFS";
    public static final String THEME_PREFS_PREFIX = "theme";
    public static final String EXTRA_PICTURES_LIST = "pictures";
    public static final String PREF_WALLPAPER_DATE = "wallpaperLastChange";

    public static final String DEFAULT_PICTURE_FORMAT = ".png";

    public static final String MYSQL_DATE_FORMAT = "yyyy-MM-dd";
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

        editor.putInt(ApplicationHelper.USER_ID_PREF, tokenInfos.getUser().getId());
        editor.putString(ApplicationHelper.USER_PSEUDO_PREF, tokenInfos.getUser().getPseudo());
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
        int id = settings.getInt(ApplicationHelper.USER_ID_PREF, -1);
        String pseudo = settings.getString(ApplicationHelper.USER_PSEUDO_PREF, "");
        String password = settings.getString(ApplicationHelper.USER_PASSWORD_PREF, "");
        Calendar expiresDate;
        expiresDate = ApplicationHelper.convertStringToDate(expiresToken, false);

        return new TokenInformations(accessToken, expiresDate, new User(id, pseudo), password, !accessToken.isEmpty());
    }

    public static User getCurrentUser(Context context){
        SharedPreferences settings = context.getSharedPreferences(ApplicationHelper.PICTOTHEMO_PREFS, Context.MODE_PRIVATE);
        int id = settings.getInt(ApplicationHelper.USER_ID_PREF, -1);
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

    public static Calendar convertStringToDate(String date, boolean longFormat){
        SimpleDateFormat formatter = new SimpleDateFormat((longFormat)?MYSQL_DATE_LONG_FORMAT:MYSQL_DATE_FORMAT, Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(formatter.parse(date));
            return calendar;
        } catch (ParseException e) {
            return null;
        }
    }

    public static String convertDateToString(Calendar date, boolean longFormat){
        SimpleDateFormat formatter = new SimpleDateFormat((longFormat)?MYSQL_DATE_LONG_FORMAT:MYSQL_DATE_FORMAT, Locale.getDefault());
        return formatter.format(date.getTime());
    }

    public static String handleUnknowPseudo(Context ctx, String pseudo){
        if(pseudo == null || pseudo.isEmpty())
            return ctx.getResources().getString(R.string.user_unknow);
        else
            return pseudo;
    }

    /*wallpaper*/
    private static void refreshWallpaper(Context context) {
        try {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
            WallpaperInfo wallpaperInfo = wallpaperManager.getWallpaperInfo();

            if (wallpaperInfo == null) {
                Drawable wallpaper = wallpaperManager.getDrawable();
                Bitmap wallpaperBitmap = drawableToBitmap(wallpaper);
                wallpaperManager.setBitmap(wallpaperBitmap);
            }
        } catch (Exception e) {}
    }

    private static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable)
            return ((BitmapDrawable) drawable).getBitmap();

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
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
                wallpaperManager.setBitmap(image);
                refreshWallpaper(context);
            } catch (IOException e) {}
            }
        };

        thread.start();
    }
}
