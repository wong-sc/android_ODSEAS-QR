package com.example.pethoalpar.zxingexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NetworkStatus extends BroadcastReceiver {

    Context context;
    OfflineDatabase mydb;

    @Override
    public void onReceive(Context context, Intent intent) {
        String status = NetworkUtils.getConnectivityStatusString(context);
        Toast.makeText(context, status, Toast.LENGTH_LONG).show();

        this.context = context;
        mydb = new OfflineDatabase(context);

        if(status.equals("Wifi enabled") || status.equals("Mobile data enabled")){
            Intent startsync = new Intent(context, SyncService.class);
            startsync.putExtra(Config.WIFI_STATUS, status);
            context.startService(startsync);
        }
    }
}
