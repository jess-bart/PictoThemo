package com.jessy_barthelemy.pictothemo.asyncInteractions;

import android.content.Context;
import android.widget.Toast;

import com.jessy_barthelemy.pictothemo.R;
import com.jessy_barthelemy.pictothemo.apiObjects.Picture;
import com.jessy_barthelemy.pictothemo.apiObjects.TokenInformation;
import com.jessy_barthelemy.pictothemo.exceptions.LoginException;
import com.jessy_barthelemy.pictothemo.helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.interfaces.IAsyncApiObjectResponse;

import java.lang.ref.WeakReference;
import java.net.UnknownHostException;

public class VotePictureTask extends BaseAsyncTask<Void, Object, Void> {

    private Picture picture;
    private boolean positive;
    private boolean unauthorized;

    public VotePictureTask(Picture picture, boolean positive, Context context, IAsyncApiObjectResponse delegate){
        this.picture = picture;
        this.positive = positive;
        this.weakContext = new WeakReference<>(context);

        if(delegate != null)
            this.delegate = delegate;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Context context = this.weakContext.get();
            TokenInformation tokenInfo = ApplicationHelper.getTokenInformations(context);
            ApiHelper helper = ApiHelper.getInstance();

            try {
                this.picture = helper.voteForPicture(this.picture, this.positive);
            } catch (LoginException e) {
                LogInTask.login(tokenInfo, context);
                LogInTask.postExcecute(false, null, context, null, null);
                this.picture = helper.voteForPicture(this.picture, this.positive);
            }catch (UnsupportedOperationException e){
                this.unauthorized = true;
            }
        }catch (UnknownHostException e) {
            this.isOffline = true;
        }catch (Exception ignore) {}

        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        super.onPostExecute(v);
        Context context = this.weakContext.get();
        if(this.unauthorized){
            this.getDelegate().asyncTaskFail(context.getResources().getString(R.string.vote_not_allowed));
        }
        else if(this.picture != null) {
            this.getDelegate().asyncTaskSuccess(this.picture);
            Toast.makeText(context, R.string.vote_success, Toast.LENGTH_LONG).show();
        }else
            this.getDelegate().asyncTaskFail(context.getResources().getString(R.string.picture_vote_error));
    }

    public IAsyncApiObjectResponse getDelegate(){
        return (IAsyncApiObjectResponse) this.delegate;
    }
}