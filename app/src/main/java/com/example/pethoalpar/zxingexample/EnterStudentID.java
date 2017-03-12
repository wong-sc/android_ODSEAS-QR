package com.example.pethoalpar.zxingexample;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ContentFrameLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EnterStudentID extends Fragment implements View.OnClickListener{

    public static final String ARG_PAGE = "ARF_PAGE";
    public String dataStringStudentID;
    public String dataStringSubjectCode;

    RequestQueue requestQueue;

    private int mPage;

    private Button buttonEnter, buttonComfirm;
    private EditText studentid;
    private TextView studentname;
    private boolean foundStudent = false;

    public static EnterStudentID newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        EnterStudentID fragment = new EnterStudentID();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_enter_student_id, container, false);
        studentname = (TextView) v.findViewById(R.id.studentname);
        buttonEnter = (Button) v.findViewById(R.id.buttonEnter);
        buttonEnter.setOnClickListener(this);
        buttonComfirm = (Button)v.findViewById(R.id.buttonConfirm);
        buttonComfirm.setOnClickListener(this);
        Intent i = getActivity().getIntent();
        dataStringSubjectCode = i.getStringExtra("passDataValue");
        Log.d("hye datastring subject", dataStringSubjectCode);
        requestQueue = Volley.newRequestQueue(getActivity());
        return v;
    }

    public void buttonEnter(View v) {

        studentid = (EditText) getActivity().findViewById(R.id.studentid);
        dataStringStudentID = studentid.getText().toString();
        Log.d("hye STUDENT ID subject", dataStringStudentID);
        Log.d("hye DATASTRING subject", dataStringSubjectCode);
        checkStudent();

    }

    public void buttonConfirm(View v) {
        checkAlreadyScan();
    }

    public void checkAlreadyScan() {
        requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Config.BASE_URL + "seas/student/checkAlreadyScan.php?stud_id=" + dataStringStudentID + "&subject_code=" + dataStringSubjectCode,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        try {
                            JSONArray jsonArray = jsonObject.getJSONArray("result");

                            Log.d("testing", "" + jsonArray.length());

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject result = jsonArray.getJSONObject(i);
                                String Scanned = "1";
                                String isScanned = result.getString("isScanned");
                                Log.d("hye subject codess", isScanned);
                                if (Scanned.equals(isScanned)) {

                                    showMessage("Alert", dataStringStudentID + " has already scanned!");
                                    studentid.setText("");
                                    studentname.setText("Student Name: ");

                                } else {
                                    getData();
                                    Toast.makeText(getContext(), "Successfully added " + dataStringStudentID, Toast.LENGTH_LONG).show();
                                    studentid.setText("");
                                    studentname.setText("Student Name: ");
                                }

                            }

                            if (jsonArray.length() == 0) {
                                showMessage("Alert", dataStringStudentID + " has already scanned!");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("Volley", volleyError.toString());
                    }
                }


        );
        requestQueue.add(jsonObjectRequest);
    }

    public void checkStudent() {
        requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Config.BASE_URL + "seas/student/getStudentSubject.php?stud_id=" + dataStringStudentID,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        foundStudent = false;

                        try {
                            JSONArray jsonArray = jsonObject.getJSONArray("result");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject result = jsonArray.getJSONObject(i);

                                String subject_code = result.getString("subject_code");
                                Log.d("hye subject codess", subject_code);
                                if (dataStringSubjectCode.equals(subject_code)) {
                                    //Toast.makeText(EnterStudentID.this,"student exists",Toast.LENGTH_LONG).show();
                                    foundStudent = true;
                                    getStudentName();
                                }

                            }
                            if (!foundStudent) {
                                //Toast.makeText(EnterStudentID.this,"student does not exists",Toast.LENGTH_LONG).show();
                                showMessage("Alert", "Student does not register for this subject.");
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("Volley", volleyError.toString());

                    }
                }


        );
        requestQueue.add(jsonObjectRequest);
    }

    public void getStudentName() {

        requestQueue = Volley.newRequestQueue(getActivity());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Config.BASE_URL + "seas/student/getStudentData.php?stud_id=" + dataStringStudentID,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        try {
                            JSONArray jsonArray = jsonObject.getJSONArray("result");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject result = jsonArray.getJSONObject(i);

                                String stud_name = result.getString("stud_name");
                                studentname.append(stud_name);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("Volley", "Error");
                    }
                }


        );
        requestQueue.add(jsonObjectRequest);
    }

    public void getData() {
        String getUrl = Config.BASE_URL + "seas/gcm_test/v1/updateAttendanceRecord";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.equals("success checkin")) {

                    showMessage("Alert", "Student has checked in for this course");

                } else if (response.equals("success checkout")){
                    showMessage("Alert", "Student has checked out for this course");
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.toString(),Toast.LENGTH_LONG).show();
//                        Log.d("Error", error.getMessage());
//                        Toast.makeText(getContext(), "Student does not register for this subject.", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("student_id", dataStringStudentID);
                params.put("course_id", dataStringSubjectCode);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }

    public void showMessage(String title, String message) {

        AlertDialog.Builder Adialog = new AlertDialog.Builder(getContext());
        Adialog.setMessage(message).setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = Adialog.create();
        alert.setTitle(title);
        Adialog.show();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.buttonEnter:
                studentname.setText("Student Name: ");
                buttonEnter(v);
                InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(studentid.getWindowToken(),0);
                break;
            case R.id.buttonConfirm:
                buttonConfirm(v);
//                studentid.clearComposingText();
//                studentname.setText("Student Name: ");
                break;
            default:
                break;
        }
    }
}
//public class EnterStudentID extends AppCompatActivity{
//
//    public String dataStringStudentID;
//    public String dataStringSubjectCode;
//
//    RequestQueue requestQueue;
//
//    private Button buttonEnter;
//    private EditText studentid;
//    private TextView studentname;
//    private boolean foundStudent = false;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_enter_student_id);
//
//        Intent i = getIntent();
//        dataStringSubjectCode = i.getStringExtra("passDataSubjectCode");
//        Log.d("hye datastring subject", dataStringSubjectCode);
//
//        requestQueue = Volley.newRequestQueue(getApplicationContext());
//
//    }
//
//    public void buttonEnter (View v) {
//        editEnterStudID = (EditText) findViewById(R.id.EnterStudID);
//
//        dataStringStudentID = editEnterStudID.getText().toString();
//        Log.d("hye datastring subject", dataStringStudentID);
//        checkStudent();
//        //getStudentName();
//        //textStudName.setText(editEnterStudID.getText());
//
//    }
//
//    public void buttonConfirm (View v) {
//
//        //Toast.makeText(this,"Successfully added "+dataStringStudentID,Toast.LENGTH_LONG).show();
//
//        checkAlreadyScan();
//        getData();
//    }
//
//    public void checkAlreadyScan(){
//        requestQueue = Volley.newRequestQueue(this);
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Config.BASE_URL+"seas/student/checkAlreadyScan.php?stud_id="+dataStringStudentID+"&subject_code="+dataStringSubjectCode,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject jsonObject) {
//
//                        try {
//                            JSONArray jsonArray = jsonObject.getJSONArray("result");
//
//                            Log.d("testing", ""+jsonArray.length());
//
//                            for(int i = 0 ; i < jsonArray.length() ; i++){
//                                JSONObject result = jsonArray.getJSONObject(i);
//                                String Scanned = "1";
//                                String isScanned = result.getString("isScanned");
//                                Log.d ("hye subject codess",isScanned);
//                                if(Scanned.equals(isScanned)) {
//
//                                    //Toast.makeText(EnterStudentID.this,dataStringStudentID+" already scanned!",Toast.LENGTH_LONG).show();
//                                    AlertDialog.Builder Adialog = new AlertDialog.Builder(EnterStudentID.this);
//                                    Adialog.setMessage(dataStringStudentID+" has already scanned!").setCancelable(false)
//                                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    dialog.cancel();
//                                                }
//                                            });
//
//                                    AlertDialog alert = Adialog.create();
//                                    alert.setTitle("Alert");
//                                    alert.show();
//
//                                }
//                                else{
//                                    Toast.makeText(EnterStudentID.this,"Successfully added "+dataStringStudentID,Toast.LENGTH_LONG).show();
//                                }
//
//                            }
//
//                            if (jsonArray.length()==0){
//                                AlertDialog.Builder Adialog = new AlertDialog.Builder(EnterStudentID.this);
//                                Adialog.setMessage(dataStringStudentID+" has already scanned!").setCancelable(false)
//                                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                dialog.cancel();
//                                            }
//                                        });
//
//                                AlertDialog alert = Adialog.create();
//                                alert.setTitle("Alert");
//                                alert.show();
//
//                            }
//
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        Log.e("Volley",volleyError.toString());
//
//                    }
//                }
//
//
//        );
//        requestQueue.add(jsonObjectRequest);
//    }
//
//    public void checkStudent(){
//        requestQueue = Volley.newRequestQueue(this);
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Config.BASE_URL+"seas/student/getStudentSubject.php?stud_id="+dataStringStudentID,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject jsonObject) {
//
//                        try {
//                            JSONArray jsonArray = jsonObject.getJSONArray("result");
//
//                            for(int i = 0 ; i < jsonArray.length() ; i++){
//                                JSONObject result = jsonArray.getJSONObject(i);
//
//                                String subject_code = result.getString("subject_code");
//                                Log.d ("hye subject codess",subject_code);
//                                if(dataStringSubjectCode.equals(subject_code)) {
//                                    //Toast.makeText(EnterStudentID.this,"student exists",Toast.LENGTH_LONG).show();
//                                    foundStudent = true;
//                                    getStudentName();
//                                }
//
//                            }
//                            if (!foundStudent){
//                                //Toast.makeText(EnterStudentID.this,"student does not exists",Toast.LENGTH_LONG).show();
//                                AlertDialog.Builder Adialog = new AlertDialog.Builder(EnterStudentID.this);
//                                Adialog.setMessage("Student does not register for this subject.").setCancelable(false)
//                                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                dialog.cancel();
//                                            }
//                                        });
//
//                                AlertDialog alert = Adialog.create();
//                                alert.setTitle("Alert");
//                                alert.show();
//                            }
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        Log.e("Volley",volleyError.toString());
//
//                    }
//                }
//
//
//        );
//        requestQueue.add(jsonObjectRequest);
//    }
//
//    public void getStudentName(){
//        textStudName = (TextView) findViewById(R.id.textViewName);
//        requestQueue = Volley.newRequestQueue(this);
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Config.BASE_URL+"seas/student/getStudentData.php?stud_id="+dataStringStudentID,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject jsonObject) {
//
//                        try {
//                            JSONArray jsonArray = jsonObject.getJSONArray("result");
//
//                            for(int i = 0 ; i < jsonArray.length() ; i++){
//                                JSONObject result = jsonArray.getJSONObject(i);
//
//                                String stud_name = result.getString("stud_name");
//                                textStudName.append(stud_name);
//                            }
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        Log.e("Volley","Error");
//
//                    }
//                }
//
//
//        );
//        requestQueue.add(jsonObjectRequest);
//    }
//
//    public void getData(){
//        String getUrl = Config.BASE_URL+"seas/gcm_test/v1/updateIsScanned";
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, getUrl, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//                if (response.equals("Update success")){
//
//                    AlertDialog.Builder Adialog = new AlertDialog.Builder(EnterStudentID.this);
//                    Adialog.setMessage("Attendance has been taken").setCancelable(false)
//                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.cancel();
//                                }
//                            });
//
//                    AlertDialog alert = Adialog.create();
//                    alert.setTitle("Alert");
//                    alert.show();
//                }
//
//                if (response.equals("Update success")){
//
//                    AlertDialog.Builder Adialog = new AlertDialog.Builder(EnterStudentID.this);
//                    Adialog.setMessage("Attendance has been taken").setCancelable(false)
//                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.cancel();
//                                }
//                            });
//
//                    AlertDialog alert = Adialog.create();
//                    alert.setTitle("Alert");
//                    alert.show();
//                }
//
//            }
//        },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        //Toast.makeText(EnterStudentID.this,error.getMessage().toString(),Toast.LENGTH_LONG).show();
//                        Toast.makeText(EnterStudentID.this,"Student does not register for this subject.",Toast.LENGTH_LONG).show();
//                    }
//                }){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String,String> params = new HashMap<String, String>();
//                params.put("stud_id",dataStringStudentID);
//                params.put("subject_code",dataStringSubjectCode);
//
//                return params;
//            }
//        };
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(stringRequest);
//
//    }
//

