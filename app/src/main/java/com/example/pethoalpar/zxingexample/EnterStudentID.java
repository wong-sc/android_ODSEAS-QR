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
        String checkAlreadyScan = Config.BASE_URL + Config.CHECK_ALREADY_SCAN;
        requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, checkAlreadyScan,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonObject) {

                        try {
                            JSONObject jsonObject1 = new JSONObject(jsonObject);
                            JSONArray jsonArray = jsonObject1.getJSONArray("result");

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


        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("stud_id", dataStringStudentID);
                params.put("subject_code", dataStringSubjectCode);
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public void checkStudent() {
        String checkStudent = Config.BASE_URL + Config.GET_STUDENT_SUBJECT;
        requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, checkStudent,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {

                        foundStudent = false;

                        Log.d("Result", result);

                        try {
//
                            JSONObject jsonObject = new JSONObject(result);
                            JSONArray jsonArray = jsonObject.getJSONArray("result");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                String subject_code = object.getString("subject_code");
                                Log.d("hye subject codess", subject_code);
                                if (dataStringSubjectCode.equals(subject_code)) {
                                    Toast.makeText(getActivity(),"Subject found",Toast.LENGTH_LONG).show();
                                    foundStudent = true;
                                    getStudentName();
                                }
                            }
                            if (!foundStudent) {
                                Toast.makeText(getActivity(),"student does not exists",Toast.LENGTH_LONG).show();
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
                        Log.e("Volley-studentError", volleyError.getLocalizedMessage() + "");
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("stud_id", dataStringStudentID);
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public void getStudentName() {

        String getStudentData = Config.BASE_URL + Config.GET_STUDENT_DATA;
        requestQueue = Volley.newRequestQueue(getActivity());

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, getStudentData,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonObject) {

                        Log.d("Result --- ", jsonObject);
                        Toast.makeText(getActivity(),"Return  result"+ jsonObject,Toast.LENGTH_LONG).show();

                        try {
                            JSONObject jsonObject1 = new JSONObject(jsonObject);
                            JSONArray jsonArray = jsonObject1.getJSONArray("result");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject result = jsonArray.getJSONObject(i);

                                String stud_name = result.getString("stud_name");
                                studentname.append(stud_name);
                            }


                        } catch (JSONException e) {
                            Toast.makeText(getActivity(),"JSON  Error",Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("Volley", "Error");
                        Toast.makeText(getActivity(),"Volley Error",Toast.LENGTH_LONG).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("stud_id", dataStringStudentID);
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public void getData() {
//        String getUrl = Config.BASE_URL + "ODSEAS-QR/gcm_test/v1/updateAttendanceRecord";
        String getUrl = Config.BASE_URL + Config.UPDATE_ATTENDANCE_DATA;
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
            protected Map<String, String> getParams() {
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
                break;
            default:
                break;
        }
    }
}
