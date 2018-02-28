package com.jessy_barthelemy.pictothemo.broadcastReceivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jessy_barthelemy.pictothemo.helpers.ApplicationHelper;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {

    private static int ALARM_BROADCAST = 133;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            Intent alarmIntent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), ALARM_BROADCAST, alarmIntent, 0);

            Calendar date = Calendar.getInstance();
            date.set(Calendar.HOUR_OF_DAY, 0);
            date.set(Calendar.MINUTE, 0);
            date.set(Calendar.SECOND, 0);
            date.set(Calendar.MILLISECOND, 0);

            date.add(Calendar.DAY_OF_MONTH, 1);

            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            //1 day interval
            if(manager != null)
                manager.setRepeating(AlarmManager.RTC_WAKEUP, date.getTimeInMillis(), 86400000, pendingIntent);

            if(ApplicationHelper.isOnline(context))
                ApplicationHelper.changeBackgroundIfNeeded(context);
        }
    }
}