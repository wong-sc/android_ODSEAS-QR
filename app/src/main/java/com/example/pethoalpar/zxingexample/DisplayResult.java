package com.example.pethoalpar.zxingexample;

//import android.app.Fragment;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
        import android.content.Intent;
        import android.os.Bundle;
import android.support.annotation.Nullable;
        import android.util.Log;
import android.view.LayoutInflater;
        import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
        import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

        import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DisplayResult extends Fragment implements View.OnClickListener{

    public static final String ARG_PAGE = "ARF_PAGE";

    private TextView textViewAttended,textViewBooklet;
    private Button sync;
    RequestQueue requestQueue;
    String course_id;
    SharedPreferences preferences;
    OfflineDatabase mydb;

    public static DisplayResult newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        DisplayResult fragment = new DisplayResult();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_display_result,container,false);
        TextView coursename = (TextView) v.findViewById(R.id.coursename);
        TextView studentnumber = (TextView) v.findViewById(R.id.total);
        textViewAttended = (TextView) v.findViewById(R.id.attendee);
        textViewBooklet = (TextView) v.findViewById(R.id.bookletnum);
        sync = (Button) v.findViewById(R.id.sync);

        sync.setOnClickListener(this);

        preferences = getActivity().getSharedPreferences("myloginapp", Context.MODE_PRIVATE);
        mydb = new OfflineDatabase(getContext());

        Intent i = getActivity().getIntent();
        final String dataStringSubjectCode = i.getStringExtra("passSubjectInfo");
        String dataStringStudentNumber = i.getStringExtra("studentnumber");

        course_id = i.getStringExtra("passDataValue");
        coursename.setText(dataStringSubjectCode);
        studentnumber.append(dataStringStudentNumber);
        Button viewlist = (Button) v.findViewById(R.id.viewlist);
        viewlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ViewNameList.class);
                intent.putExtra("course_id", course_id);
                intent.putExtra("course_full_name", dataStringSubjectCode);
                startActivity(intent);
            }
        });

//        Button stopscan = (Button) v.findViewById(R.id.stopscan);
        //check internet - if yes then online function else offline function
        if(preferences.getString(Config.WIFI_STATUS, "").equals("Not connected to Internet")){
            String attendeddata = mydb.getAttendedData(course_id);
            String bookletData = mydb.getAnswerBooklet(course_id);
            processAttendedData(attendeddata);
            processBookletData(bookletData);
        } else {
            getAttendedData();
            getAnswerBooklet();
        }
        return v;
    }

    //Logout function
    private void logout(){
        //Creating an alert dialog to confirm logout
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Are you sure you want to logout?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        //Getting out sharedpreferences
//                        SharedPreferences preferences = getSharedPreferences("myloginapp", Context.MODE_PRIVATE);
//                        //Getting editor
                        SharedPreferences.Editor editor = preferences.edit();

                        //Puting the value false for subjectCode
                        editor.putBoolean(course_id, false);

                        //Putting blank value to email
                        editor.putString("email", "");

                        //Saving the sharedpreferences
                        editor.commit();
                    }
                });

        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        //Showing the alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void getAttendedData() {
        requestQueue = Volley.newRequestQueue(getActivity());
        String getAttendedData = Config.BASE_URL + Config.GET_ATTENDED_DATA;
        StringRequest jsonObjectRequest1 = new StringRequest(Request.Method.POST, getAttendedData,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonObject) {
                        processAttendedData(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("Volley","Error");
                        String attendeddata = mydb.getAttendedData(course_id);
                        mydb.close();
                        processAttendedData(attendeddata);
                    }
                }
            ){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("course_id",course_id);
                    return params;
                }
            };
            requestQueue.add(jsonObjectRequest1);
    }

    public void getAnswerBooklet() {
        String getAnswerBooklet = Config.BASE_URL + Config.GET_ANS_BOOKLETS;
        requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest jsonObjectRequest1 = new StringRequest(Request.Method.POST, getAnswerBooklet,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonObject) {
                        processBookletData(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("Volley","Error");
                        String bookletData = mydb.getAnswerBooklet(course_id);
                        mydb.close();
                        processBookletData(bookletData);
                    }
                }
        ){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("course_id",course_id);
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest1);
    }

    public void processAttendedData(String result){
        textViewAttended.setText(result);
    }

    public void processBookletData(String result){
        textViewBooklet.setText(result);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sync:
                startActivity(new Intent(getActivity(), SyncActivity.class));
        }
    }
}
