package com.example.pethoalpar.zxingexample;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Handler;
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

    public SyncService() {
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String workingIntent = intent != null ? intent.getStringExtra(Config.WIFI_STATUS) : null;
        ArrayList<JSONObject> courseData = new ArrayList<>();
        mydb = new OfflineDatabase(getApplicationContext());
//        if(workingIntent.equals("Wifi enabled") || workingIntent.equals("Mobile data enabled")) {
            Cursor cursor = mydb.getUnsyscData();

            if (cursor.moveToFirst()) {
//                Log.d("Result cursor--", DatabaseUtils.dumpCursorToString(cursor));
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
    }

    private void syncDatabase(final String data){

        String syncUrl = Config.BASE_URL + Config.SYNC;
        Log.d("data", data);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, syncUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String result) {
                        Log.d("Result", result);
                        try {
                            JSONArray jsonArray = new JSONArray(result);
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if(jsonObject.getString("status").equals("success"))
                                    mydb.markedSyncRecord(jsonObject);
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
}
