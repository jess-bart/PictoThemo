package com.jessy_barthelemy.pictothemo.AsyncInteractions;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.jessy_barthelemy.pictothemo.Enum.UploadResult;
import com.jessy_barthelemy.pictothemo.Helpers.ApiHelper;
import com.jessy_barthelemy.pictothemo.Helpers.ApplicationHelper;
import com.jessy_barthelemy.pictothemo.Interfaces.IAsyncApiObjectResponse;
import com.jessy_barthelemy.pictothemo.R;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

public class UploadPictureTask extends AsyncTask<Void, Integer, UploadResult> {

    private Context context;
    /*reference to the class that want a success callback*/
    private IAsyncApiObjectResponse delegate;
    private InputStream inputStream;
    private String filename;
    private View progressBar;
    private FloatingActionButton fab;

    public UploadPictureTask(InputStream inputStream, String filename, View progressBar, FloatingActionButton fab, Context context, IAsyncApiObjectResponse delegate){
        this.inputStream = inputStream;
        this.filename = filename;
        this.context = context;
        this.progressBar = progressBar;
        this.fab = fab;

        if(delegate != null)
            this.delegate = delegate;
    }

    @Override
    protected UploadResult doInBackground(Void... params) {
        ApiHelper helper = new ApiHelper(ApplicationHelper.getTokenInformations(this.context));
        try {
            return helper.uploadFile(this.inputStream, this.filename);
        } catch (IOException | JSONException | ParseException e) {
        }

        return UploadResult.ERROR;
    }

    @Override
    protected void onPostExecute(UploadResult result) {
        this.progressBar.setVisibility(View.GONE);
        this.fab.setVisibility(View.VISIBLE);

        if(result == UploadResult.SUCCESS)
            this.delegate.asyncTaskSuccess(this.context.getResources().getString(R.string.upload_success));
        else if(result == UploadResult.FORMAT_ERROR)
            this.delegate.asyncTaskFail(this.context.getResources().getString(R.string.upload_format_error));
        else if(result == UploadResult.SIZE_ERROR)
            this.delegate.asyncTaskFail(this.context.getResources().getString(R.string.upload_size_error));
        else if(result == UploadResult.ERROR)
            this.delegate.asyncTaskFail(this.context.getResources().getString(R.string.upload_error));
    }
}