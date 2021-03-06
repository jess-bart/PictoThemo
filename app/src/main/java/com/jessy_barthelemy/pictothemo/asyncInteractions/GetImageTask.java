package com.jessy_barthelemy.pictothemo.asyncInteractions;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.jessy_barthelemy.pictothemo.R;
import com.jessy_barthelemy.pictothemo.helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.helpers.ApplicationHelper;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class GetImageTask extends BaseAsyncTask<Void, Integer, Bitmap>{
    private WeakReference<ImageView> weakImageView;
    private WeakReference<View> weakProgressBar;
    private String url;
    private String name;
    private int screenWidth;
    private File cache;
    private boolean setAsWallpaper;

    private GetImageTask(Context context, ImageView imageView, View progressBar, boolean setAsWallpaper){
        this.weakImageView = new WeakReference<>(imageView);
        this.weakProgressBar = new WeakReference<>(progressBar);
        this.weakContext = new WeakReference<>(context);
        this.screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        this.setAsWallpaper = setAsWallpaper;
    }

    public GetImageTask(Context context, ImageView imageView, View progressBar, Calendar date, boolean setAsWallpaper){
        this(context, imageView, progressBar, setAsWallpaper);
        this.url = ApiHelper.BASIC_PROTOCOL+ApiHelper.URL_PICTURE_DATE+"/"+ApplicationHelper.convertDateToString(date, false, false);
        this.name = ApplicationHelper.convertDateToString(date, false, false)+ApplicationHelper.DEFAULT_PICTURE_FORMAT;
        this.cache = this.getCacheDir();
    }

    public GetImageTask(Context context, ImageView imageView, View progressBar, int id, boolean setAsWallpaper){
        this(context, imageView, progressBar, setAsWallpaper);
        this.url = ApiHelper.BASIC_PROTOCOL+ApiHelper.URL_PICTURE+"/"+id;
        this.name = id+ApplicationHelper.DEFAULT_PICTURE_FORMAT;
        this.cache = this.getCacheDir();
    }

    private File getCacheDir(){
        Context context = this.weakContext.get();

        File[] dirs = context.getExternalCacheDirs();
        if(dirs[dirs.length -1] != null){
            return new File(dirs[dirs.length -1].getPath(), this.name);
        }else
            return new File(context.getExternalCacheDir(), this.name);
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        Bitmap result;

        try {
            //attempting to get cached file
            if(this.cache == null || !cache.isFile())
                throw new FileNotFoundException();

            result = this.decodeBitmap(new FileInputStream(cache), cache.length());
            if(result == null)
                throw new IOException();
        } catch (Exception e) {
            result = this.getImageFromInternet();
        }

        return result;
    }

    private Bitmap getImageFromInternet(){
        HttpURLConnection connection = null;
        if(this.cache != null)
            this.cache.delete();

        try {
            connection = (HttpURLConnection)new URL(this.url).openConnection();
            connection.connect();
            InputStream input = connection.getInputStream();
            return this.decodeBitmap(input, connection.getContentLength());
        }catch(UnknownHostException e){
            this.isOffline = true;
            return null;
        } catch (Exception e) {
            return null;
        } finally {
            if(connection != null)
                connection.disconnect();
        }
    }

    private Bitmap decodeBitmap(InputStream input, long length){
        Bitmap result;
        try{
            byte data[] = new byte[8192];
            InputStream bufferedInput = new BufferedInputStream(input, 1024);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            int count;
            long read = 0;
            while ((count = bufferedInput.read(data)) != -1) {
                read += count;
                outStream.write(data, 0, count);
                publishProgress((int) ((read * 100) / length));
            }

            result = BitmapFactory.decodeByteArray(outStream.toByteArray(), 0, outStream.size());
        }catch (Exception e){
            e.printStackTrace();
            result = null;
        }

        return result;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        final View progressBar = this.weakProgressBar.get();

        if(progressBar == null)
            return;
        progressBar.getLayoutParams().width = (values[0]*this.screenWidth)/100;
        progressBar.requestLayout();
    }

    @Override
    protected void onPostExecute(final Bitmap image) {
        super.onPostExecute(image);

        final Context context = this.weakContext.get();
        final View progressBar = this.weakProgressBar.get();
        final ImageView imageView = this.weakImageView.get();


        if(this.setAsWallpaper){
            ApplicationHelper.setWallpaper(context, image);
            SimpleDateFormat dateFormat = new SimpleDateFormat(ApplicationHelper.MYSQL_DATE_FORMAT, Locale.getDefault());
            String today = dateFormat.format(new Date());

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(ApplicationHelper.PREF_WALLPAPER_DATE, today);
            editor.apply();
        }

        if(progressBar != null){
            progressBar.setVisibility(View.GONE);
            Animation fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);
            imageView.startAnimation(fadeOut);

            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    if(image != null)
                        imageView.setImageBitmap(image);
                    else
                        imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.error));

                    Animation anim = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                    imageView.startAnimation(anim);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        }

        if(this.cache != null && !this.cache.isFile()){
            try {
                SaveImageToDiskTask saveTask = new SaveImageToDiskTask(image, new FileOutputStream(this.cache.getAbsolutePath()));
                saveTask.execute();
            } catch (FileNotFoundException ignored) {}
        }
    }
}