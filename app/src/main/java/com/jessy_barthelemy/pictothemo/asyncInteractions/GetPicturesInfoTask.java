package com.jessy_barthelemy.pictothemo.asyncInteractions;

import android.content.Context;
import android.os.AsyncTask;

import com.jessy_barthelemy.pictothemo.apiObjects.Picture;
import com.jessy_barthelemy.pictothemo.apiObjects.PictureList;
import com.jessy_barthelemy.pictothemo.helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.interfaces.IAsyncApiObjectResponse;
import com.jessy_barthelemy.pictothemo.R;

import java.util.ArrayList;
import java.util.Calendar;

public class GetPicturesInfoTask extends AsyncTask<String, Void, ArrayList<Picture>> {

    private Calendar startingDate;
    private Calendar endingDate;
    private Boolean potd;
    private String theme;
    private String user;
    private Integer voteCount;
    private Context context;

    /*reference to the class that want a success callback*/
    private IAsyncApiObjectResponse delegate;

    public GetPicturesInfoTask(Calendar startingDate, Calendar endingDate, String theme, String user, Integer voteCount, Boolean potd, Context context, IAsyncApiObjectResponse delegate){
        if(delegate != null)
            this.delegate = delegate;

        this.potd = potd;
        this.startingDate = startingDate;
        this.endingDate = endingDate;
        this.theme = theme;
        this.user = user;
        this.voteCount = voteCount;
        this.context = context;
    }

    @Override
    protected ArrayList<Picture> doInBackground(String... params) {
        ApiHelper helper = new ApiHelper();
        return helper.getPicturesInfo(this.startingDate, this.endingDate, this.theme, this.user, this.voteCount, this.potd);
    }

    @Override
    protected void onPostExecute(ArrayList<Picture> pictures) {
        if(pictures != null){
            PictureList pictureList = new PictureList(pictures);
            this.delegate.asyncTaskSuccess(pictureList);
        }else if(this.context != null){
            this.delegate.asyncTaskFail(this.context.getString(R.string.save_picture_empty));
        }

    }
}