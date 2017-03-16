package com.example.pethoalpar.zxingexample;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Shi Chee on 15-Mar-17.
 */

public class NetworkUtils {

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    static SharedPreferences prefs;

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectivityStatusString(Context context) {
        int conn = NetworkUtils.getConnectivityStatus(context);
        prefs = context.getSharedPreferences("myloginapp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String status = null;
        if (conn == NetworkUtils.TYPE_WIFI) {
            status = "Wifi enabled";
            editor.putString(Config.WIFI_STATUS, status);
        } else if (conn == NetworkUtils.TYPE_MOBILE) {
            status = "Mobile data enabled";
            editor.putString(Config.WIFI_STATUS, status);
        } else if (conn == NetworkUtils.TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
            editor.putString(Config.WIFI_STATUS, status);
        }

        editor.apply();
        editor.commit();

        return status;
    }
}
