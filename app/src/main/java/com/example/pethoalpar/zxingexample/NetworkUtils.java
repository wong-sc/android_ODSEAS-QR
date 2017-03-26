package com.example.pethoalpar.zxingexample;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {

    private static int TYPE_WIFI = 1;
    private static int TYPE_MOBILE = 2;
    private static int TYPE_NOT_CONNECTED = 0;
    static SharedPreferences prefs;

    public static int getConnectivityStatus(Context context) {
        /*check the type of connectivity and return the status to the getConnectivityStatusString*/
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
        /*this function is to get the connectivity status whether it is on Wifi or mobile data or not connected to any network*/
        int conn = NetworkUtils.getConnectivityStatus(context);
        prefs = context.getSharedPreferences("myloginapp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String status = null;
        if (conn == NetworkUtils.TYPE_WIFI) {
            status = "Wifi enabled";
            /*returned status will be store in a shared preferences (local storage)*/
            editor.putString(Config.WIFI_STATUS, status);
        } else if (conn == NetworkUtils.TYPE_MOBILE) {
            status = "Mobile data enabled";
            editor.putString(Config.WIFI_STATUS, status);
        } else if (conn == NetworkUtils.TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
            editor.putString(Config.WIFI_STATUS, status);
        }

        /*stored value will only be saved once the editor.apply and editor.commit been executed*/
        editor.apply();
        editor.commit();

        return status;
    }
}
