package com.jessy_barthelemy.pictothemo.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jessy_barthelemy.pictothemo.helpers.ApplicationHelper;


public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ApplicationHelper.changeBackgroundIfNeeded(context);
    }
}