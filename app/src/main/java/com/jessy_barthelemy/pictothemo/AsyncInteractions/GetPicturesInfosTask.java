package com.jessy_barthelemy.pictothemo.AsyncInteractions;

import android.os.AsyncTask;

import com.jessy_barthelemy.pictothemo.ApiObjects.Comment;
import com.jessy_barthelemy.pictothemo.ApiObjects.Picture;
import com.jessy_barthelemy.pictothemo.ApiObjects.User;
import com.jessy_barthelemy.pictothemo.Helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.Helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncApiObjectResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

public class GetPicturesInfosTask extends AsyncTask<String, Void, Picture> {

    private Calendar date;
    private String flags;
    private int id;
    /*reference to the class that want a success callback*/
    private IAsyncApiObjectResponse delegate;

    public GetPicturesInfosTask(String flags, IAsyncApiObjectResponse delegate){
        this.flags = flags;

        if(delegate != null)
            this.delegate = delegate;
    }

    public GetPicturesInfosTask(Calendar date, String flags, IAsyncApiObjectResponse delegate){
        this(flags, delegate);
        this.date = date;
    }

    public GetPicturesInfosTask(int id, String flags, IAsyncApiObjectResponse delegate){
        this(flags, delegate);
        this.id = id;
    }

    @Override
    protected Picture doInBackground(String... params) {
        Picture picture;
        ApiHelper helper = new ApiHelper();
        try {
            JSONObject result;

            String selector = (this.date == null)?String.valueOf(this.id):ApplicationHelper.convertDateToString(this.date, false);
            result = helper.getPictures(selector, this.flags);


            if(result.length() == 0)
                return null;

            JSONObject potd = result.getJSONArray(ApiHelper.ENTITY_PICTURES).getJSONObject(0);
            int id = potd.has(ApiHelper.ID)?potd.getInt(ApiHelper.ID):0;
            String theme = potd.has(ApiHelper.THEME_NAME)?potd.getString(ApiHelper.THEME_NAME):"";

            int userID = potd.has(ApiHelper.USER_ID)?potd.getInt(ApiHelper.USER_ID):0;
            String pseudo = potd.has(ApiHelper.PSEUDO)?potd.getString(ApiHelper.PSEUDO):"";

            int positiveVote = potd.has(ApiHelper.POSITIVE_VOTE)?potd.getInt(ApiHelper.POSITIVE_VOTE):0;
            int negativeVote = potd.has(ApiHelper.NEGATIVE_VOTE)?potd.getInt(ApiHelper.NEGATIVE_VOTE):0;

            picture = new Picture(id, theme, new User(userID, pseudo), this.date, positiveVote, negativeVote);
            //comments
            String textComment;
            User user;
            if(potd.has(ApiHelper.COMMENTS)){
                JSONArray comments = potd.getJSONArray(ApiHelper.COMMENTS);
                for(int i = 0, length = comments.length(); i < length; ++i){
                    JSONObject commentObj = comments.getJSONObject(i);

                    textComment=  commentObj.getString("text");
                    user = new User(commentObj.getInt(ApiHelper.ID), commentObj.getString(ApiHelper.PSEUDO));
                    Comment comment = new Comment(user, textComment, ApplicationHelper.convertStringToDate(commentObj.getString("publish_date"), true));
                    picture.getComments().add(comment);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            picture = null;
        }

        return picture;
    }

    @Override
    protected void onPostExecute(Picture picture) {
        this.delegate.asyncTaskSuccess(picture);
    }
}