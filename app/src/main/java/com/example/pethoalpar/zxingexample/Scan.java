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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.example.pethoalpar.zxingexample.R.id.imageButtonScan;

public class Scan extends Fragment implements ZXingScannerView.ResultHandler{
    public static final String ARG_PAGE = "ARF_PAGE";
    private ZXingScannerView mScannerView;
    public String[] splited;
    public String subjectCode, matchedSubject, subjectName;
    RequestQueue requestQueue;

    private int mPage;


    public static Scan newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        Scan fragment = new Scan();
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
        View v = inflater.inflate(R.layout.activity_scan,container,false);
        TextView subjectname = (TextView) v.findViewById(R.id.textViewSubjectName);

        Intent i = getActivity().getIntent();
        subjectCode = i.getStringExtra("passDataValue");
        subjectName = i.getStringExtra("passSubjectInfo");
        subjectname.setText(subjectName);

        ImageButton scan = (ImageButton) v.findViewById(R.id.imageButtonScan);
        scan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                IntentIntegrator.forSupportFragment(Scan.this).initiateScan();
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        Log.d("RESULT", String.valueOf(result));
        Boolean matched = false;

        if(result != null) {
            if(result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");

                final String scanContent = result.getContents();

                requestQueue = Volley.newRequestQueue(getActivity());

                String contentResult = scanContent;
                Log.d("Content result---", contentResult);
                splited = contentResult.split("\\s+");
                Log.d("Result Array----", splited[1]);
                Log.d("DataString", subjectCode);
                for(int i = 1 ; i < splited.length ; i++){
                    if(subjectCode.equals(splited[i])) {
                        matchedSubject = splited[i];
                        Toast.makeText(getActivity(),"Successfully scanned "+splited[0],Toast.LENGTH_LONG).show();
                        matched = true;
                        break;
                    }
                }
                if(!matched)
                    Toast.makeText(getActivity(),splited[0] + " does not belong to this examination",Toast.LENGTH_LONG).show();
                else
                    checkAlreadyScan();

            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
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

    public void getData(){
        String getUrl = Config.BASE_URL+Config.UPDATE_ATTENDANCE_DATA;
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
                        showMessage("Alert", "Student does not register for this subject.");
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("student_id",splited[0]);
                params.put("course_id",matchedSubject);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }
    public void checkAlreadyScan(){
        requestQueue = Volley.newRequestQueue(getContext());
        String checkAlreadyScan = Config.BASE_URL + Config.CHECK_ALREADY_SCAN;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, checkAlreadyScan,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        try {
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            Log.d ("hye subject codess",jsonObject.toString());

                            for(int i = 0 ; i < jsonArray.length() ; i++){
                                JSONObject result = jsonArray.getJSONObject(i);
                                String Scanned = "1";
                                String isScanned = result.getString("isScanned");
                                Log.d ("hye subject codess",isScanned);
                                if(Scanned.equals(isScanned)) {

                                    //Toast.makeText(EnterStudentID.this,dataStringStudentID+" already scanned!",Toast.LENGTH_LONG).show();
                                    AlertDialog.Builder Adialog = new AlertDialog.Builder(getContext());
                                    Adialog.setMessage(splited[0]+" has already scanned!").setCancelable(false)
                                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });

                                    AlertDialog alert = Adialog.create();
                                    alert.setTitle("Alert");
                                    alert.show();

                                }
                                else{
                                    getData();
                                    Toast.makeText(getContext(),"Successfully added "+splited[0],Toast.LENGTH_LONG).show();
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("Volley","Error");
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("student_id",splited[0]);
                params.put("course_id",matchedSubject);

                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void handleResult(Result result) {

        Log.d("Result",result.toString());

    }
}