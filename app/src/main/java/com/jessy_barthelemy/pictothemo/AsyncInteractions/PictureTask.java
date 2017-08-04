package com.jessy_barthelemy.pictothemo.AsyncInteractions;

import android.os.AsyncTask;

import com.jessy_barthelemy.pictothemo.ApiObjects.Picture;
import com.jessy_barthelemy.pictothemo.ApiObjects.User;
import com.jessy_barthelemy.pictothemo.Helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncApiObjectResponse;

import org.json.JSONObject;

import java.util.Calendar;

public class PictureTask extends AsyncTask<String, Void, Picture> {

    private Calendar date;
    private String flags;
    /*reference to the class that want a success callback*/
    private IAsyncApiObjectResponse delegate;

    public PictureTask(Calendar date, String flags, IAsyncApiObjectResponse delegate){
        this.date = date;
        this.flags = flags;

        if(delegate != null)
            this.delegate = delegate;
    }

    @Override
    protected Picture doInBackground(String... params) {
        Picture picture;
        ApiHelper helper = new ApiHelper();
        try {
            JSONObject result;
            result = helper.getPictures(this.date, this.flags);

            if(result.length() == 0)
                return null;

            JSONObject potd = result.getJSONArray(ApiHelper.ENTITY_PICTURES).getJSONObject(0);
            int id = potd.has(ApiHelper.ID)?potd.getInt(ApiHelper.ID):0;
            String theme = potd.has(ApiHelper.THEME_NAME)?potd.getString(ApiHelper.THEME_NAME):"";
            String pseudo = potd.has(ApiHelper.PSEUDO)?potd.getString(ApiHelper.PSEUDO):"";
            int positiveVote = potd.has(ApiHelper.POSITIVE_VOTE)?potd.getInt(ApiHelper.POSITIVE_VOTE):0;
            int negativeVote = potd.has(ApiHelper.NEGATIVE_VOTE)?potd.getInt(ApiHelper.NEGATIVE_VOTE):0;

            picture = new Picture(id, theme, new User(pseudo), this.date, positiveVote, negativeVote);
        } catch (Exception e) {
            picture = null;
        }

        return picture;
    }

    @Override
    protected void onPostExecute(Picture picture) {
        delegate.asyncTaskSuccess(picture);
    }
}