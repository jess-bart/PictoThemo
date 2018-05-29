package com.jessy_barthelemy.pictothemo.asyncInteractions;

import android.content.Context;
import android.os.AsyncTask;

import com.jessy_barthelemy.pictothemo.interfaces.IAsyncResponse;

import java.lang.ref.WeakReference;

public abstract class BaseAsyncTask<T, U, V> extends AsyncTask<T, U, V> {
    protected boolean isOffline;
    protected WeakReference<Context> weakContext;
    protected IAsyncResponse delegate;

    @Override
    protected void onPostExecute(V v) {
        if(delegate != null && this.isOffline)
            this.delegate.handleNoConnection();
    }
}
