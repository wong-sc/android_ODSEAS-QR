package com.example.pethoalpar.zxingexample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SelectSubject extends AppCompatActivity implements View.OnClickListener, Spinner.OnItemSelectedListener {
    private Spinner spinner;

    private ArrayList<String> subjectData;

    private JSONArray result;
    private JSONArray result2;

    private TextView textViewSubjectCode;
    private TextView textViewSubjectName;
    private TextView detail;
    private CardView card;

    public String passData;
    public String subjectInfo;

    //ArrayAdapter<CharSequence> adapter;

    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
//        spinner = (Spinner)findViewById(R.id.spinner);
//        adapter = ArrayAdapter.createFromResource(this, R.array.subject_names, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
        // ArrayList<String> subjectData = new ArrayList<String>();

        subjectData = new ArrayList<String>();
        spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        textViewSubjectCode = (TextView)findViewById(R.id.textViewSubjectCode);
        textViewSubjectName = (TextView)findViewById(R.id.textViewSubjectName);
        detail = (TextView)findViewById(R.id.name);
        card = (CardView) findViewById(R.id.card_view);
        card.setVisibility(View.GONE);

        if(isNetworkStatusAvialable (getApplicationContext())) {
            //Toast.makeText(getApplicationContext(), "internet avialable", Toast.LENGTH_SHORT).show();
            getData();
            btnNext = (Button)findViewById(R.id.buttonNext);
            btnNext.setOnClickListener(this);

        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();

        }
    }

    private void getData(){
        //Creating a string request
        StringRequest stringRequest = new StringRequest(Config.BASE_URL+"ODSEAS-QR/student/getSubjectData.php",
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

                    }
                });

        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void getDetails(String subjectID){
        //Creating a string request
        StringRequest stringRequest = new StringRequest(Config.BASE_URL+"ODSEAS-QR/student/getDetailsData.php?subject_code="+subjectID,
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
                            detail.setText(result2.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

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
        spinner.setAdapter(new ArrayAdapter<String>(SelectSubject.this, android.R.layout.simple_spinner_dropdown_item, subjectData));
        card.setVisibility(View.VISIBLE);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        textViewSubjectCode.setText("Subject selected:");
        textViewSubjectName.setText(getSubjectCode(position)+" "+getSubjectName(position));
        detail.setText(getSubjectCode(position));
        getDetails(getSubjectCode(position));

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
            Intent intent = new Intent(this, Scan.class);
            intent.putExtra("passDataValue",passData);
            intent.putExtra("passSubjectInfo",subjectInfo);
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
                        SharedPreferences preferences = getSharedPreferences("myloginapp", Context.MODE_PRIVATE);
                        //Getting editor
                        SharedPreferences.Editor editor = preferences.edit();

                        //Puting the value false for loggedin
                        editor.putBoolean("loggedin", false);

                        //Putting blank value to email
                        editor.putString("email", "");

                        //Saving the sharedpreferences
                        editor.commit();

                        //Starting login activity
                        Intent intent = new Intent(SelectSubject.this, Login.class);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        //Adding our menu to toolbar
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuLogout) {
            //calling logout method when the logout button is clicked
            logout();
        }
        return super.onOptionsItemSelected(item);
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
