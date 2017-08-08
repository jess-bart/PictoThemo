package com.jessy_barthelemy.pictothemo.AsyncInteractions;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.jessy_barthelemy.pictothemo.Helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.Helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.R;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.Calendar;


public class GetImageTask extends AsyncTask<Void, Integer, Bitmap>{
    private ImageView imageView;
    private View progressBar;
    private String url;
    private Context context;
    private String name;
    private int screenWidth;
    private File cache;

    public GetImageTask(Context context, ImageView imageView, View progressBar){
        this.imageView = imageView;
        this.progressBar = progressBar;
        this.context = context;
        this.screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public GetImageTask(Context context, ImageView imageView, View progressBar, Calendar date){
        this(context, imageView, progressBar);
        this.url = ApiHelper.URL_POTD+ApiHelper.RES_FORMAT.format(date.getTime());

        try {
            this.name = ApplicationHelper.convertDateToString(date, false)+ApplicationHelper.DEFAULT_PICTURE_FORMAT;
            this.cache = new File(context.getExternalCacheDir(), this.name);
        } catch (ParseException e) {
            this.name = "";
        }
    }

    public GetImageTask(Context context, ImageView imageView, View progressBar, int id){
        this(context, imageView, progressBar);
        this.url = ApiHelper.URL_PICTURE+ id;

        this.name = id+ApplicationHelper.DEFAULT_PICTURE_FORMAT;
        this.cache = new File(context.getExternalCacheDir(), this.name);
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
        this.cache.delete();

        try {
            connection = (HttpURLConnection)new URL(this.url).openConnection();
            connection.connect();
            InputStream input = connection.getInputStream();
            return this.decodeBitmap(input, connection.getContentLength());
        } catch (Exception e) {
            return null;
        } finally {
            if(connection != null)
                connection.disconnect();
        }
    }

    private Bitmap decodeBitmap(InputStream input, long length) throws IOException {
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
        this.progressBar.getLayoutParams().width = (values[0]*this.screenWidth)/100;
        this.progressBar.requestLayout();
    }

    @Override
    protected void onPostExecute(final Bitmap image) {
        this.progressBar.setVisibility(View.GONE);
        Animation fadeOut = AnimationUtils.loadAnimation(this.context, R.anim.fade_out);
        this.imageView.startAnimation(fadeOut);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                if(image != null){
                    GetImageTask.this.imageView.setImageBitmap(image);
                }else{
                    GetImageTask.this.imageView.setImageDrawable(ContextCompat.getDrawable(GetImageTask.this.context, R.drawable.error));
                }

                Animation anim = AnimationUtils.loadAnimation(GetImageTask.this.context, R.anim.fade_in);
                GetImageTask.this.imageView.startAnimation(anim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        if(this.cache != null && !this.cache.isFile()){
            SaveImageCacheTask saveTask = new SaveImageCacheTask(image, this.cache.getAbsolutePath());
            saveTask.execute();
        }
    }
}