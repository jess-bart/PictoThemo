package com.jessy_barthelemy.pictothemo.asyncInteractions;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.jessy_barthelemy.pictothemo.R;
import com.jessy_barthelemy.pictothemo.apiObjects.TokenInformation;
import com.jessy_barthelemy.pictothemo.enumerations.UploadResult;
import com.jessy_barthelemy.pictothemo.exceptions.LoginException;
import com.jessy_barthelemy.pictothemo.helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.interfaces.IAsyncApiObjectResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.UnknownHostException;

public class UploadPictureTask extends BaseAsyncTask<Void, Integer, UploadResult> {

    private InputStream inputStream;
    private byte[] picture;
    private String filename;
    private WeakReference<View> weakProgressBar;
    private WeakReference<FloatingActionButton> weakFab;

    public UploadPictureTask(byte[] picture, String filename, View progressBar, FloatingActionButton fab, Context context, IAsyncApiObjectResponse delegate){
        this.picture = picture;
        this.filename = filename;
        this.weakContext = new WeakReference<>(context);
        this.weakProgressBar = new WeakReference<>(progressBar);
        this.weakFab = new WeakReference<>(fab);

        if(delegate != null)
            this.delegate = delegate;
    }

    @Override
    protected UploadResult doInBackground(Void... params) {
        this.inputStream = new ByteArrayInputStream(picture);
        Context context = this.weakContext.get();
        TokenInformation tokenInfo = ApplicationHelper.getTokenInformations(context);
        ApiHelper helper = ApiHelper.getInstance();
        try {
            try {
                UploadResult result = helper.uploadFile(this.inputStream, this.filename);

                return result;
            } catch (LoginException e) {
                LogInTask.login(tokenInfo, context);
                LogInTask.postExcecute(false, null, context, null, null);
                return helper.uploadFile(this.inputStream, this.filename);
            }
        }catch (UnknownHostException e) {
            this.isOffline = true;
            return UploadResult.ERROR;
        }catch (Exception e) {
            return UploadResult.ERROR;
        }
    }

    @Override
    protected void onPostExecute(UploadResult result) {
        super.onPostExecute(result);
        Context context = this.weakContext.get();
        View progressBar = this.weakProgressBar.get();
        FloatingActionButton fab = this.weakFab.get();

        if(fab != null && progressBar != null){
            progressBar.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
        }

        if(result == UploadResult.SUCCESS)
            this.getDelegate().asyncTaskSuccess(context.getResources().getString(R.string.upload_success));
        else if(result == UploadResult.FORMAT_ERROR)
            this.getDelegate().asyncTaskFail(context.getResources().getString(R.string.upload_format_error));
        else if(result == UploadResult.SIZE_ERROR)
            this.getDelegate().asyncTaskFail(context.getResources().getString(R.string.upload_size_error));
        else if(result == UploadResult.ERROR)
            this.getDelegate().asyncTaskFail(context.getResources().getString(R.string.upload_error));
    }

    public IAsyncApiObjectResponse getDelegate(){
        return (IAsyncApiObjectResponse) this.delegate;
    }
}