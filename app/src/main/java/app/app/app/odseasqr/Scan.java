package app.app.app.odseasqr;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import app.app.app.odseasqr.R;
import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class Scan extends Fragment implements ZXingScannerView.ResultHandler{
    public static final String ARG_PAGE = "ARF_PAGE";
    public String[] splited;
    public String subjectCode, matchedSubject, subjectName, staffID, position;
    RequestQueue requestQueue;
    SharedPreferences preferences;
    OfflineDatabase mydb;


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
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_scan,container,false);
        final TextView subjectname = (TextView) v.findViewById(R.id.textViewSubjectName);

        Intent i = getActivity().getIntent();
        subjectCode = i.getStringExtra("passDataValue");
        subjectName = i.getStringExtra("passSubjectInfo");
        subjectname.setText(subjectName);


        preferences = getActivity().getSharedPreferences("myloginapp", Context.MODE_PRIVATE);
        staffID = preferences.getString("staff_id", "null");
        position = preferences.getString(Dashboard.POSITION, "null");
        mydb = new OfflineDatabase(getContext());

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

                /*SEARCH WHETHER THE STUDENT QR CODE CONTAIN THE SELECTED SUBJECT CODE*/
                for(int i = 1 ; i < splited.length ; i++){
                    if(subjectCode.equals(splited[i])) {
                        matchedSubject = splited[i];
                        matched = true;
                        break;
                    }
                }
                if(!matched)
                    showMessage("Alert", splited[0] + " does not belong to this examination");
                else{
                    if(position.equals(Config.CHIEF))
                        checkAlreadyScan();
                    else{
                        String studentSubject = mydb.checkAlreadyScan(matchedSubject, splited[0]);
                        processStudentIschecked(studentSubject);
                    }
                }
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

    public void processStudentIschecked(String result){

        try {
            JSONArray jsonArray = new JSONArray(result);

            Log.d("testing", "" + jsonArray.toString());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String Scanned = "1";
                String isScanned = jsonObject.getString("ischecked");
                Log.d("hye subject codess", isScanned);
                if (Scanned.equals(isScanned)) {

                    showMessage("Alert", splited[0] + " has already scanned!");

                } else {
                    if(position.equals(Config.CHIEF))
                        getData();
                    else {
                        String status = mydb.updateAttendanceRecord(splited[0], subjectCode, staffID, "1", 0);
                        processGetData(status);
                    }
                }
            }
            if (jsonArray.length() == 0) {
                showMessage("Alert", splited[0] + " has already scanned!");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void processGetData(String response){
        if (response.equals("success checkin")) {
            showMessage("Alert", splited[0] + " has checked in for this course");
        } else if (response.equals("success checkout")){
            showMessage("Alert", splited[0] + " has checked out for this course");
        }
    }

    public void getData(){
        String getUrl = Config.BASE_URL+Config.UPDATE_ATTENDANCE_DATA;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                processGetData(response);
                mydb.updateAttendanceRecord(splited[0], subjectCode, staffID, "1", 1);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String status = mydb.updateAttendanceRecord(splited[0], subjectCode, staffID, "1", 0);
                        /*CHECK WHETHER THE STUDENT HAS CHECKIN / CHECKOUT */
                        processGetData(status);
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("student_id",splited[0]);
                params.put("course_id",matchedSubject);
                params.put("staff_id", staffID);
                params.put("style_id", "1");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }
    public void checkAlreadyScan(){
        requestQueue = Volley.newRequestQueue(getContext());
        String checkAlreadyScan = Config.BASE_URL + Config.CHECK_ALREADY_SCAN;
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, checkAlreadyScan,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonObject) {

                        try {
                            JSONArray jsonArray = new JSONArray(jsonObject);
                            Log.d ("hye subject codess",jsonArray.toString());

                            for(int i = 0 ; i < jsonArray.length() ; i++){
                                JSONObject result = jsonArray.getJSONObject(i);
                                String Scanned = "1";
                                String isScanned = result.getString("ischecked");
                                Log.d ("hye subject codess",isScanned);
                                if(Scanned.equals(isScanned)) {
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
//                                    Toast.makeText(getContext(),"Successfully added "+splited[0],Toast.LENGTH_LONG).show();
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
                        Log.e("Volley","Error" + volleyError.toString());
                        String studentSubject = mydb.checkAlreadyScan(matchedSubject, splited[0]);
                        Log.d("Result scan -", studentSubject);
                        processStudentIschecked(studentSubject);
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