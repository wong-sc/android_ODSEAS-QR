package app.app.app.odseasqr;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, Spinner.OnItemSelectedListener {

    private ArrayList<String> subjectData;

    private JSONArray result;
    private JSONArray result2;

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
    List<Integer> courseid = new ArrayList<>();
    HashMap<Integer, String> params = new HashMap<Integer, String>();
    HashMap<Integer, String> params_name = new HashMap<Integer, String>();
    public static final String POSITION = "position";
    int[] itemIDs;
    LinearLayout content_layout;

    private Spinner spinner;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    RequestQueue requestQueue;
    OfflineDatabase mydb;
    ProgressDialog loading;
    NavigationView navigationView;
    private SwipeRefreshLayout swipeContainer;
    Menu menu;
    ProgressBar load;

    Button btnNext, btnStop;
//    TextView username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Set the activity_dashboard.xml file to the view*/
        setContentView(R.layout.activity_dashboard);

        /*Set the toolbar/ actionbar in the page*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*SharedPreferences are simply sets of data values that stored persistently.
        Persistently which mean data you stored in the SharedPreferences are still
        exist even if you stop the application or turn off the device*/
        preferences = getSharedPreferences("myloginapp", Context.MODE_PRIVATE);
        editor = preferences.edit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView txtProfileName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.username);
        txtProfileName.setText(preferences.getString("staff_name","Unknown"));

        /*Declare variable*/
        staff_id = preferences.getString("staff_id", "");
        mydb = new OfflineDatabase(this);
        loading = new ProgressDialog(Dashboard.this);
        subjectData = new ArrayList<String>();
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        tvNoOfStudent = (TextView) findViewById(R.id.tvNoOfStudent);
        tvVenue = (TextView) findViewById(R.id.tvVenue);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTime = (TextView) findViewById(R.id.tvTime);
        card = (CardView) findViewById(R.id.card_view);
//        card.setVisibility(View.GONE);
        btnNext = (Button)findViewById(R.id.buttonNext);
        tvCourse = (TextView) findViewById(R.id.tvCourse);
        tvInvigilatorName = (TextView) findViewById(R.id.tvInvigilatorName);
        btnStop = (Button) findViewById(R.id.btnStop);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        content_layout = (LinearLayout) findViewById(R.id.content);
        content_layout.setVisibility(View.GONE);
        load = (ProgressBar) findViewById(R.id.loading);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(itemIDs.length != 0)
                    for (int itemID : itemIDs) {
                        menu.removeItem(itemID);
                    }
                getOfflineData(Config.GET_OFFLINE_DATA);
                init();
            }
        });

        /*If user first time login (included user who log out the device previously),
        * then it will prompt user to download*/
        if(preferences.getBoolean("firstTimeLogin", false))
            promtDownloadData(staff_id);
        else{
            Toast.makeText(Dashboard.this, "Download content successfully", Toast.LENGTH_SHORT).show();
        }

        btnNext.setOnClickListener(this);
        btnStop.setOnClickListener(this);
    }

    private void init(){
        if(isNetworkStatusAvailable(this)) {
            Toast.makeText(getApplicationContext(), "Internet Available", Toast.LENGTH_SHORT).show();
            getData();
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            // ask sqlite to generate spinner data by passing the staff id
            getSpinnerData();
        }
        swipeContainer.setRefreshing(false);
    }

    public void getSpinnerData(){
        Log.d("tagdata", staff_id);
        String spinnerData = mydb.getSpinnerData(staff_id);
        try {
            JSONArray jsonArray = new JSONArray(spinnerData);
            result = jsonArray;
            getSubjectData(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("Spinner result --", spinnerData);
    }

    public void promtDownloadData(String staff_id){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("To runable during offline mode, it is encourage to download the content first into this application. Please enable your internet connection");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getOfflineData(Config.GET_OFFLINE_DATA);
                loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                loading.setTitle("Downloading Content..........");
                loading.setMessage("Getting data from server, may take some time......");
                loading.setCancelable(false);
                loading.show();
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int pid = android.os.Process.myPid();
                android.os.Process.killProcess(pid);
            }
        });
        alertDialogBuilder.show();
    }

    public void getOfflineData(String linkUrl){
        requestQueue = Volley.newRequestQueue(this);
        final SharedPreferences.Editor editor = preferences.edit();
        String getAllData = Config.BASE_URL + linkUrl;
        Log.d("URL", getAllData);
        StringRequest jsonObjectRequest1 = new StringRequest(Request.Method.POST, getAllData,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonObject) {

                        Log.d("result-----", jsonObject);
                        try {
                            JSONObject jsonObject1 = new JSONObject(jsonObject);
                            String course = jsonObject1.getString("course");
                            String course_handler = jsonObject1.getString("course_handler");
                            String enroll_handler = jsonObject1.getString("enroll_handler");
                            String student = jsonObject1.getString("student");
                            String venue = jsonObject1.getString("venue");
                            String venue_handler = jsonObject1.getString("venue_handler");
                            String staff = jsonObject1.getString("staff");
                            String style = jsonObject1.getString("attendance_style");

                            loading.setMessage("Saving data.......");

                            mydb.insertCourseData(course);
                            mydb.insertCourseHandler(course_handler);
                            if(preferences.getBoolean("firstTimeLogin", false))
                                mydb.insertEnrollHandler(enroll_handler);
                            else
                                mydb.SyncEnrollHandler(enroll_handler);
                            mydb.insertStudent(student);
                            mydb.insertVenue(venue);
                            mydb.insertVenueHandler(venue_handler);
                            mydb.insertStaff(staff);
                            mydb.insertAttendanceStyle(style);

                            loading.dismiss();
                            editor.putBoolean("firstTimeLogin", false);
                            editor.commit();
                            init();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("Volley line 105","Error");
                    }
                }
        );
        requestQueue.add(jsonObjectRequest1);
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
//                        Toast.makeText(Dashboard.this, "Error: "+ error.toString(), Toast.LENGTH_SHORT).show();
                        getSpinnerData();
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
                            result2 = new JSONArray(response);
                            processDetails(result2, position);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        String subjectDetails = mydb.getSubjectDetails(getSubjectCode(position));
                        try {
                            JSONArray jsonArray2 = new JSONArray(subjectDetails);
                            result2 = jsonArray2;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        processOfflineDetails(position);
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("course_id",subjectID);
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
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        menu = navView.getMenu();
//        SubMenu subMenu = menu.addSubMenu(R.id.group_course, 100, 500, "View Attendance");
        courseid.clear();
        subjectData.clear();
        params_name.clear();
        params.clear();
        if(itemIDs != null)
            if(itemIDs.length != 0)
                for (int itemID : itemIDs) {
                    menu.removeItem(itemID);
                }
        itemIDs = new int[j.length()];
        for(int i=0;i<j.length();i++){
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);
                //Adding the name of the student to array list
                subjectData.add(json.getString("course_id") + " " + json.getString("course_name"));
                int itemID = Integer.valueOf(json.getString("course_id").substring(3));
                itemIDs[i] = itemID;
                menu.add(R.id.group_2, itemID, 500,json.getString("course_id") + " " + json.getString("course_name"));
                params.put(itemID, json.getString("course_id"));
                params_name.put(itemID, json.getString("course_id") + " " + json.getString("course_name"));
                courseid.add(itemID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Setting adapter to show the items in the spinner
        spinner.setAdapter(new ArrayAdapter<String>(Dashboard.this, android.R.layout.simple_spinner_dropdown_item, subjectData));
//        card.setVisibility(View.VISIBLE);
    }

    private void processDetails(JSONArray j,int position){
        String invigilator = "";
        //Traversing through all the items in the json array
        for(int i=0;i<j.length();i++){
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);
                invigilator = invigilator + json.getString("staff_name") +"  ("+json.getString("invigilator_position") + ") " + "\n";
                Log.d("person: ", invigilator + "Session:" + preferences.getString("staff_name", "Unknown") + " " + json.getString("invigilator_position"));

                if(preferences.getString("staff_name", "Unknown").equals(json.getString("staff_name"))
                        && json.getString("invigilator_position").equals(Config.CHIEF)){
                    btnStop.setVisibility(View.VISIBLE);
                    editor.putString(POSITION, json.getString("invigilator_position"));
                    editor.commit();
                    editor.apply();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        tvInvigilatorName.setText(invigilator);
        tvCourse.setText(getSubjectCode(position) + " " + getSubjectName(position));
        tvNoOfStudent.setText(getStudentNumber(position));
        tvVenue.setText(getVenue(position));
        tvDate.setText(getExamDate(position));
        tvTime.setText(getExamTime(position));
        editor.putString(Config.COURSE_ID, getSubjectCode(position));
        editor.commit();
        editor.apply();
        content_layout.setVisibility(View.VISIBLE);
        load.setVisibility(View.GONE);
    }

    private void processOfflineDetails(int position){
        String invigilator = "";
        //Traversing through all the items in the json array
        for(int i=0;i<result2.length();i++){
            try {
                //Getting json object
                JSONObject json = result2.getJSONObject(i);
                invigilator = invigilator + json.getString("staff_name") +"  ("+json.getString("invigilator_position") + ") " + "\n";
                Log.d("person: ", invigilator + "Session:" + preferences.getString("staff_name", "Unknown") + " " + json.getString("invigilator_position"));
                if(preferences.getString("staff_name", "Unknown").equals(json.getString("staff_name"))
                        && json.getString("invigilator_position").equals(Config.CHIEF)){
                    btnStop.setVisibility(View.VISIBLE);
                    editor.putString(POSITION, json.getString("invigilator_position"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        tvInvigilatorName.setText(invigilator);
        tvCourse.setText(getSubjectCode(position) + " " + getSubjectName(position));
        tvNoOfStudent.setText(getStudentNumber(position));
        tvVenue.setText(getVenue(position));
        tvDate.setText(getExamDate(position));
        tvTime.setText(getExamTime(position));
        editor.putString(Config.COURSE_ID, getSubjectCode(position));
        editor.commit();
        editor.apply();
        content_layout.setVisibility(View.VISIBLE);
        load.setVisibility(View.GONE);
    }

    private String getSubjectCode (int position){
        String subjectCode = "";
        try {
            JSONObject json = result.getJSONObject(position);
            subjectCode = json.getString("course_id");
            Log.d("Result subject code-", subjectCode);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return subjectCode;
    }

    private String getSubjectName (int position){
        String subjectName = "";
        try {
            JSONObject json = result.getJSONObject(position);
            subjectName = json.getString("course_name");
        }catch (JSONException e){
            e.printStackTrace();
        }
        return subjectName;
    }

    private String getStudentNumber (int position){
        try {
            JSONObject json = result.getJSONObject(position);
            student_number=json.getString("student_number");
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
        btnStop.setVisibility(View.GONE);
        content_layout.setVisibility(View.GONE);
        load.setVisibility(View.VISIBLE);
        editor.putString(POSITION, "null");
        editor.commit();
        editor.apply();
        if(preferences.getString(Config.WIFI_STATUS, "").equals(Config.NOT_CONNECTED)){
            String subjectDetails = mydb.getSubjectDetails(getSubjectCode(position));
            try {
                JSONArray jsonArray2 = new JSONArray(subjectDetails);
                result2 = jsonArray2;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            processOfflineDetails(position);
        } else {
            getDetails(getSubjectCode(position), position);
        }
        passData = getSubjectCode(position);
        subjectInfo = getSubjectCode(position)+" "+getSubjectName(position);
        if(passData != null)
        is_time_available();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onClick(View v)
    {
        if(v == findViewById(R.id.btnStop)){
            if(mydb.check_course_status(passData))
                ComfirmMessage("Are you sure you want to finalise the list?", "Once it is finalised, there will be no amendment can be done");
            else
                showMessage("Subject Finalised", "Subject finalised by CHIEF Invigilator, no amendment can be done in this moment");
            return;
        }

        if(mydb.check_course_status(passData)) {
            if (v == findViewById(R.id.buttonNext) && is_time_available()) {
                Intent intent = new Intent(this, TakeAttendance.class);
                intent.putExtra("passDataValue", passData);
                intent.putExtra("passSubjectInfo", subjectInfo);
                intent.putExtra("studentnumber", student_number);
                startActivity(intent);
            } else {
                showMessage("Session Close", "This session will only open during 30 minutes before and close after 30 minutes of the examination");
            }
        } else {
            showMessage("Session Close", "This session has been closed by CHIEF Invigilator");
        }
    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder Adialog = new AlertDialog.Builder(this);
        Adialog.setTitle(title);
        Adialog.setMessage(message).setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = Adialog.create();
        Adialog.show();
    }

    public void ComfirmMessage(String title, String message) {
        AlertDialog.Builder Adialog = new AlertDialog.Builder(this);
        Adialog.setTitle(title);
        Adialog.setMessage(message).setCancelable(false)
                .setPositiveButton("Finalise", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stopSubject();
                        mydb.StopCourse(passData);
                    }
                })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
        Adialog.show();
    }

    public void stopSubject(){

        String stop_course = Config.BASE_URL + Config.STOP_COURSE;
        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, stop_course,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("testing", response + " haha");

                        if(response.equals("success"))
                            mydb.StopCourse(passData);
                        else
                            showMessage("Fail to stop course", "Error has encountered from server");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showMessage("Fail to stop course", error.toString());
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("course_id",passData);
                return params;
            }
        };
        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Adding request to the queue
        requestQueue.add(stringRequest);

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
                        editor.putBoolean("firstTimeLogin", false);

                        //Putting blank value to email
                        editor.putString("email", "");

                        //Saving the sharedpreferences
                        editor.commit();
                        getApplicationContext().deleteDatabase(OfflineDatabase.DATABASE_NAME);

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
    public boolean onPrepareOptionsMenu(Menu menu) {
        Menu course = navigationView.getMenu();
        MenuItem item = course.findItem(R.id.nav_viewattendance);
        item.setEnabled(false);
        return true;
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
        Log.d("id", id+"");

        if (params.containsKey(id)){
            String variable = params.get(id);
            Intent intent = new Intent(Dashboard.this, ViewNameList.class);
            intent.putExtra("course_id", variable);
            String full_course = params_name.get(id);
            intent.putExtra("course_full_name", full_course);
            startActivity(intent);
        } else if (id == R.id.nav_viewattendance) {

        } else if (id == R.id.nav_logout) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean is_time_available(){
        String available = mydb.CheckCourseTime(passData);
        if(available.equals(Config.UNAVAILABLE)){
            return false;
        } else {
            return true;
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        if(itemIDs != null)
        if(itemIDs.length != 0)
            for (int itemID : itemIDs) {
                menu.removeItem(itemID);
            }
        init();
    }

    public static boolean isNetworkStatusAvailable(Context context) {
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
