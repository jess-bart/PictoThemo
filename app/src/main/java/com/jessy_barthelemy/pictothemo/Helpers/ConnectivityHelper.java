package com.jessy_barthelemy.pictothemo.Helpers;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by Jess on 05/02/2017.
 */

public class ConnectivityHelper {

    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
