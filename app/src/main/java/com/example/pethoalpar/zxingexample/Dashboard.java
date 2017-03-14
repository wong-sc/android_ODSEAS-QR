package com.example.pethoalpar.zxingexample;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, Spinner.OnItemSelectedListener {

    private ArrayList<String> subjectData;
    private ArrayList<String> subjectDetails;

    private JSONArray result;
    private JSONArray result2;

    private TextView textViewSubjectCode;
    private TextView textViewSubjectName;
    private TextView detail;
    private TextView tvCourse;
    private TextView tvInvigilatorName;
    private TextView tvNoOfStudent;
    private TextView tvVenue;
    private TextView tvDate;
    private TextView tvTime;
    private CardView card;

    public String passData;
    public String subjectInfo;
    private String staff_id;
    private String student_number;

    private Spinner spinner;

    SharedPreferences preferences;

    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

         preferences = getSharedPreferences("myloginapp", Context.MODE_PRIVATE);

        staff_id = preferences.getString("staff_id", "");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        subjectData = new ArrayList<String>();
        subjectDetails = new ArrayList<String>();
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        textViewSubjectCode = (TextView) findViewById(R.id.textViewSubjectCode);
        textViewSubjectName = (TextView) findViewById(R.id.textViewSubjectName);
        tvNoOfStudent = (TextView) findViewById(R.id.tvNoOfStudent);
        tvVenue = (TextView) findViewById(R.id.tvVenue);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTime = (TextView) findViewById(R.id.tvTime);
        detail = (TextView) findViewById(R.id.name);
        card = (CardView) findViewById(R.id.card_view);
        card.setVisibility(View.GONE);

        tvCourse = (TextView) findViewById(R.id.tvCourse);
        tvInvigilatorName = (TextView) findViewById(R.id.tvInvigilatorName);

        if(!preferences.getBoolean("firstTimeLogin", true))
            promtDownloadData(staff_id);
        else
            Toast.makeText(Dashboard.this, "You already have downloaded content", Toast.LENGTH_SHORT).show();

        if(isNetworkStatusAvialable (this)) {
            Toast.makeText(getApplicationContext(), "internet avialable", Toast.LENGTH_SHORT).show();
            getData();
            btnNext = (Button)findViewById(R.id.buttonNext);
            btnNext.setOnClickListener(this);

        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void promtDownloadData(String staff_id){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("To runable during offline mode, it is encourage that to download the content first into this application.");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(Dashboard.this, "You have selected YES", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(Dashboard.this, "You have selected NO", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialogBuilder.show();

    }

    public void getOfflineData(String linkUrl){
//        requestQueue = Volley.newRequestQueue(this);
//        String getAllData = Config.BASE_URL + linkUrl;
//        Log.d("URL", getAllData);
//        StringRequest jsonObjectRequest1 = new StringRequest(Request.Method.POST, getAllData,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String jsonObject) {
//
//                        Log.d("result-----", jsonObject);
//                        data.clear();
//                        try {
//                            JSONObject jsonObject1 = new JSONObject(jsonObject);
//                            JSONArray jsonArray = jsonObject1.getJSONArray("result");
//                            Toast.makeText(ViewNameList.this, "Result length" + jsonArray.length(), Toast.LENGTH_SHORT).show();
//
//                            for(int i = 0 ; i < jsonArray.length() ; i++){
//                                JSONObject result = jsonArray.getJSONObject(i);
//                                ViewNameListModel model = new ViewNameListModel();
//                                Log.d("studentName: ", result.getString("studentname"));
//                                Log.d("matricNo: ", result.getString("matricno"));
//                                model.setStudent_name(result.getString("studentname"));
//                                model.setStudent_matric(result.getString("matricno"));
//                                data.add(model);
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        Log.e("Volley line 105","Error");
//                    }
//                }
//        ){
//            @Override
//            protected Map<String,String> getParams(){
//                Map<String,String> params = new HashMap<String, String>();
//                params.put("subject_code",subjectCode);
//                return params;
//            }
//        };
//        requestQueue.add(jsonObjectRequest1);
    }

    private void getData(){
        //Creating a string request
        String getData = Config.BASE_URL + Config.GET_SUBJECT_DATA;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getData,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("testing data", response);
                        JSONObject j = null;
                        try {
                            //Parsing the fetched Json String to JSON Object
                            j = new JSONObject(response);

                            //Storing the Array of JSON String to our JSON Array
                            result = j.getJSONArray("result");

                            //Calling method getStudents to get the students from the JSON Array
                            getSubjectData(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Dashboard.this, "Error: "+ error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError{
                Map<String,String> params = new HashMap<String, String>();
                params.put("staff_id",staff_id);
                return params;
            }
        };

        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void getDetails(final String subjectID, final int position){

        String getDetails = Config.BASE_URL + Config.GET_DETAILS_DATA;
        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getDetails,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("testing data", response);
                        JSONObject j = null;
                        try {
                            //Parsing the fetched Json String to JSON Object
                            j = new JSONObject(response);

                            //Storing the Array of JSON String to our JSON Array
                            result2 = j.getJSONArray("result");
//                            detail.setText(result2.toString());
                            processDetails(result2, position);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("subject_code",subjectID);
                return params;
            }
        };

        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void getSubjectData(JSONArray j){
        //Traversing through all the items in the json array
        for(int i=0;i<j.length();i++){
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);

                //Adding the name of the student to array list
                subjectData.add(json.getString("subject_code") + " " + json.getString("subject_name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Setting adapter to show the items in the spinner
        spinner.setAdapter(new ArrayAdapter<String>(Dashboard.this, android.R.layout.simple_spinner_dropdown_item, subjectData));
        card.setVisibility(View.VISIBLE);
    }

    private void processDetails(JSONArray j,int position){
        String invigilator = "";
        //Traversing through all the items in the json array
        for(int i=0;i<j.length();i++){
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);
                invigilator = invigilator + json.getString("staff_name") +"  ("+json.getString("invigilator_position") + ") " + "\n";

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        tvInvigilatorName.setText(invigilator);
        tvCourse.setText("Course Name: "+ getSubjectCode(position) + " " + getSubjectName(position));
        tvNoOfStudent.setText("Number of Students: "+ getStudentNumber(position));
        tvVenue.setText("Venue: "+ getVenue(position));
        tvDate.setText("Date: "+ getExamDate(position));
        tvTime.setText("Time: "+ getExamTime(position));
    }


    private String getSubjectCode (int position){
        String subjectCode = "";
        try {
            JSONObject json = result.getJSONObject(position);
            subjectCode = json.getString("subject_code");
        }catch (JSONException e){
            e.printStackTrace();
        }
        return subjectCode;
    }

    private String getSubjectName (int position){
        String subjectName = "";
        try {
            JSONObject json = result.getJSONObject(position);
            subjectName = json.getString("subject_name");
        }catch (JSONException e){
            e.printStackTrace();
        }
        return subjectName;
    }

    private String getStudentNumber (int position){
        try {
            JSONObject json = result.getJSONObject(position);
            student_number=json.getString("no_students");
        }catch (JSONException e){
            e.printStackTrace();
        }
        return student_number;
    }

    private String getVenue (int position){
        String venue = "";
        try {
            JSONObject json = result.getJSONObject(position);
            venue = json.getString("venue_name");
        }catch (JSONException e){
            e.printStackTrace();
        }
        return venue;
    }

    private String getExamDate (int position){
        String date = "";
        try {
            JSONObject json = result.getJSONObject(position);
            date = json.getString("exam_date");
        }catch (JSONException e){
            e.printStackTrace();
        }
        return date;
    }

    private String getExamTime (int position){
        String starttime = "";
        String endtime = "";
        try {
            JSONObject json = result.getJSONObject(position);
            starttime = json.getString("start_time");
            endtime = json.getString("end_time");
        }catch (JSONException e){
            e.printStackTrace();
        }
        return starttime + " - " + endtime;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        textViewSubjectCode.setText("Subject selected:");
        textViewSubjectName.setText(getSubjectCode(position)+" "+getSubjectName(position));
        getDetails(getSubjectCode(position), position);
        passData = getSubjectCode(position);
        subjectInfo = getSubjectCode(position)+" "+getSubjectName(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        textViewSubjectCode.setText("");
        textViewSubjectName.setText("");
    }

    @Override
    public void onClick(View v)
    {
        if (v == findViewById(R.id.buttonNext)){
            Intent intent = new Intent(this, TakeAttendance.class);
            intent.putExtra("passDataValue",passData);
            intent.putExtra("passSubjectInfo",subjectInfo);
            intent.putExtra("studentnumber",student_number);
            startActivity(intent);
        }
    }

    //Logout function
    private void logout(){
        //Creating an alert dialog to confirm logout
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to logout?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        //Getting out sharedpreferences
//                        SharedPreferences preferences = getSharedPreferences("myloginapp", Context.MODE_PRIVATE);
//                        //Getting editor
                        SharedPreferences.Editor editor = preferences.edit();

                        //Puting the value false for loggedin
                        editor.putBoolean("loggedin", false);

                        //Putting blank value to email
                        editor.putString("email", "");

                        //Saving the sharedpreferences
                        editor.commit();

                        //Starting login activity
                        Intent intent = new Intent(Dashboard.this, Login.class);
                        startActivity(intent);
                        finish();

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menuLogout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the home action
        } else if (id == R.id.nav_download) {

        } else if (id == R.id.nav_viewattendance) {

        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_logout) {

            logout();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static boolean isNetworkStatusAvialable (Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null)
        {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if(netInfos != null)
                if(netInfos.isConnected())
                    return true;
        }
        return false;
    }
}
