package com.jessy_barthelemy.pictothemo.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.jessy_barthelemy.pictothemo.AsyncInteractions.GetImageTask;
import com.jessy_barthelemy.pictothemo.Helpers.ApplicationHelper;

import java.util.Calendar;

public class NetworkReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {

        boolean hasToChangeBackground = ApplicationHelper.hasToChangeBackgroundToday(context, false);
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)
                && isOnline(context) && hasToChangeBackground){
            GetImageTask imageTask = new GetImageTask(context, null, null, Calendar.getInstance(), true);
            imageTask.execute();
        }
    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
}