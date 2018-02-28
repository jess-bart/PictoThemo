package com.jessy_barthelemy.pictothemo.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.jessy_barthelemy.pictothemo.helpers.ApplicationHelper;

public class NetworkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() !=null && intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
            ApplicationHelper.changeBackgroundIfNeeded(context);
        }
    }
}