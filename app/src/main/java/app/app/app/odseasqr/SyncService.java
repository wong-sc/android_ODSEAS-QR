package app.app.app.odseasqr;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SyncService extends IntentService {

    public static final String LOG_TAG = "RSSPullService";
    OfflineDatabase mydb;
    RequestQueue requestQueue;
    SharedPreferences preferences;
    SyncBroadCast syncBroadCast;
    IntentFilter intentFilter;

    public SyncService() {
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String workingIntent = intent != null ? intent.getStringExtra(Config.WIFI_STATUS) : null;
        syncBroadCast = new SyncBroadCast();
        intentFilter = new IntentFilter("com.odseasqr.android.SYNC");
        registerReceiver(syncBroadCast, intentFilter);
        ArrayList<JSONObject> courseData = new ArrayList<>();
        mydb = new OfflineDatabase(getApplicationContext());
        preferences = getSharedPreferences("myloginapp", Context.MODE_PRIVATE);
//        if(workingIntent.equals("Wifi enabled") || workingIntent.equals("Mobile data enabled")) {
            Cursor cursor = mydb.getUnsyscData(preferences.getString(Config.COURSE_ID, "null"));
        Log.d("course_id", preferences.getString(Config.COURSE_ID, "null"));

            if (cursor.moveToFirst()) {
                Log.d("Result50 sync--", DatabaseUtils.dumpCursorToString(cursor));
                do {
                    JSONObject jsonObject = new JSONObject();
                    try {

                        jsonObject.put("enroll_handler_id", cursor.getString(0));
                        jsonObject.put("student_id", cursor.getString(1));
                        jsonObject.put("course_id", cursor.getString(2));
                        jsonObject.put("ischecked", cursor.getString(3));
                        jsonObject.put("checkin_time", cursor.getString(4));
                        jsonObject.put("checkout_time", cursor.getString(5));
                        jsonObject.put("checkin_staffID", cursor.getString(6));
                        jsonObject.put("checkout_staffID", cursor.getString(7));
                        jsonObject.put("checkin_style_id", cursor.getString(8));
                        jsonObject.put("checkout_style_id", cursor.getString(9));
                        jsonObject.put("status", cursor.getString(10));
                        jsonObject.put("created_date", cursor.getString(11));
                        jsonObject.put("updated_date", cursor.getString(12));

                        courseData.add(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } while (cursor.moveToNext());
                syncDatabase(courseData.toString());
            } else {
                Log.d("Result == ", "NO");
                Toast.makeText(getApplicationContext(), "All data synced with server", Toast.LENGTH_SHORT).show();
            }

            cursor.close();
            mydb.close();
    }

    private void syncDatabase(final String data){

        String syncUrl = Config.BASE_URL + Config.SYNC;
        Log.d("data", data);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, syncUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String result) {
                        Log.d("Result222", result);
                        try {
                            JSONArray jsonArray = new JSONArray(result);
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if(jsonObject.getString("status").equals("success")) {
                                    mydb.markedSyncRecord(jsonObject);
                                    if(preferences.getString(Dashboard.POSITION, "null").equals(Config.CHIEF)) {
                                        Intent intent = new Intent("com.odseasqr.android.SYNC");
                                        intent.putExtra("status", "Start");
                                        sendBroadcast(intent);
                                    } else {
                                        Intent intent = new Intent("com.odseasqr.android.SYNC");
                                        intent.putExtra("status", "Finished");
                                        sendBroadcast(intent);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError volleyError) {
                        Log.e("Volley-studentError", volleyError.getLocalizedMessage() + " / "
                                                + volleyError.getMessage() + " / " + volleyError.toString());
                        if(preferences.getString(Dashboard.POSITION, "null").equals(Config.CHIEF)) {
                            Intent intent = new Intent("com.odseasqr.android.SYNC");
                            intent.putExtra("status", "Start");
                            sendBroadcast(intent);
                        } else {
                            Intent intent = new Intent("com.odseasqr.android.SYNC");
                            intent.putExtra("status", "Finished");
                            sendBroadcast(intent);
                        }
                    }
                }
            ){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("student_data", data);
                    return params;
                }
        };
                requestQueue.add(jsonObjectRequest);
            }

    @Override
    public void onDestroy() {
        unregisterReceiver(syncBroadCast);
        super.onDestroy();
    }
}
