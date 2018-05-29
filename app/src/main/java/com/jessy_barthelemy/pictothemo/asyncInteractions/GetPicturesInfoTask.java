package com.jessy_barthelemy.pictothemo.asyncInteractions;

import android.content.Context;

import com.jessy_barthelemy.pictothemo.R;
import com.jessy_barthelemy.pictothemo.apiObjects.Picture;
import com.jessy_barthelemy.pictothemo.apiObjects.PictureList;
import com.jessy_barthelemy.pictothemo.helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.interfaces.IAsyncApiObjectResponse;

import java.lang.ref.WeakReference;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;

public class GetPicturesInfoTask extends BaseAsyncTask<String, Void, ArrayList<Picture>> {

    private Calendar startingDate;
    private Calendar endingDate;
    private Boolean potd;
    private String theme;
    private String user;
    private Integer voteCount;

    public GetPicturesInfoTask(Calendar startingDate, Calendar endingDate, String theme, String user, Integer voteCount, Boolean potd, Context context, IAsyncApiObjectResponse delegate){
        if(delegate != null)
            this.delegate = delegate;

        this.potd = potd;
        this.startingDate = startingDate;
        this.endingDate = endingDate;
        this.theme = theme;
        this.user = user;
        this.voteCount = voteCount;
        this.weakContext = new WeakReference<>(context);
    }

    @Override
    protected ArrayList<Picture> doInBackground(String... params) {
        ApiHelper helper = ApiHelper.getInstance();
        try {
            return helper.getPicturesInfo(this.startingDate, this.endingDate, this.theme, this.user, this.voteCount, this.potd);
        } catch (UnknownHostException e) {
            this.isOffline = true;
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<Picture> pictures) {
        super.onPostExecute(pictures);

        Context context = this.weakContext.get();
        if(pictures != null){
            PictureList pictureList = new PictureList(pictures);
            this.getDelegate().asyncTaskSuccess(pictureList);
        }else if(context != null)
            this.getDelegate().asyncTaskFail(context.getString(R.string.save_picture_empty));
    }

    public IAsyncApiObjectResponse getDelegate(){
        return (IAsyncApiObjectResponse) this.delegate;
    }
}