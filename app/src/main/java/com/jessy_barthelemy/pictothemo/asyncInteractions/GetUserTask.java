package com.jessy_barthelemy.pictothemo.asyncInteractions;

import android.content.Context;

import com.jessy_barthelemy.pictothemo.R;
import com.jessy_barthelemy.pictothemo.apiObjects.User;
import com.jessy_barthelemy.pictothemo.helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.interfaces.IAsyncApiObjectResponse;

import java.lang.ref.WeakReference;
import java.net.UnknownHostException;

public class GetUserTask extends BaseAsyncTask<String, Void, User> {

    private long id;

    public GetUserTask(long id, Context context, IAsyncApiObjectResponse delegate){
        this.id = id;
        this.weakContext = new WeakReference<>(context);

        if(delegate != null)
            this.delegate = delegate;
    }

    @Override
    protected User doInBackground(String... params) {
        ApiHelper helper = ApiHelper.getInstance();
        try {
            return helper.getUser(this.id);
        } catch (UnknownHostException e) {
            this.isOffline = true;
            return null;
        }
    }

    @Override
    protected void onPostExecute(User user) {
        super.onPostExecute(user);
        Context context = this.weakContext.get();

        if(user != null)
            this.getDelegate().asyncTaskSuccess(user);
        else if(context != null)
            this.getDelegate().asyncTaskFail(context.getString(R.string.profil_load_error));
    }

    public IAsyncApiObjectResponse getDelegate(){
        return (IAsyncApiObjectResponse) this.delegate;
    }
}