package app.app.app.odseasqr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class ZxingQRActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView mScannerView;
    public String[] splited;
    public String subjectCode, matchedSubject, subjectName, staffID, position;
    RequestQueue requestQueue;
    SharedPreferences preferences;
    OfflineDatabase mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("onCreate", "onCreate");

        mScannerView = new ZXingScannerView(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setContentView(mScannerView);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                Toast.makeText(getApplicationContext(), "Permission already granted", Toast.LENGTH_LONG).show();

            } else {
                requestPermission();
            }
        }

        preferences = getSharedPreferences("myloginapp", Context.MODE_PRIVATE);
        staffID = preferences.getString("staff_id", "null");
        position = preferences.getString(Dashboard.POSITION, "null");
        mydb = new OfflineDatabase(ZxingQRActivity.this);

        Intent i = getIntent();
        subjectCode = i.getStringExtra("subject_code");
        subjectName = i.getStringExtra("subject_name");
    }

    private boolean checkPermission() {
        return ( ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA ) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(ZxingQRActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if(mScannerView == null) {
                    mScannerView = new ZXingScannerView(this);
                    setContentView(mScannerView);
                }
                mScannerView.setResultHandler(this);
                mScannerView.startCamera();
            } else {
                requestPermission();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {

        final String result = rawResult.getText();
        Log.d("QRCodeScanner", rawResult.getText());
        Log.d("QRCodeScanner", rawResult.getBarcodeFormat().toString());
        boolean matched = false;

        splited = result.split("\\s+");
        Log.d("QRCodeScanner", "data = " + splited.length);

        for(int i = 1 ; i < splited.length ; i++){
            if(subjectCode.equals(splited[i])) {
                matchedSubject = splited[i];
                matched = true;
                break;
            }
        }
        if(!matched)
            showMessage(splited[0] + " does not belong to this examination");
        else{
            if(position.equals(Config.CHIEF))
                checkAlreadyScan();
            else{
                String studentSubject = mydb.checkAlreadyScan(matchedSubject, splited[0]);
                processStudentIschecked(studentSubject);
            }
        }


    }

    public void checkAlreadyScan(){
        requestQueue = Volley.newRequestQueue(ZxingQRActivity.this);
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
                                    showMessage(splited[0]+" has already scanned!");
//                                    android.app.AlertDialog.Builder Adialog = new android.app.AlertDialog.Builder(ZxingQRActivity.this);
//                                    Adialog.setMessage(splited[0]+" has already scanned!").setCancelable(false)
//                                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    dialog.cancel();
//                                                }
//                                            });
//
//                                    android.app.AlertDialog alert = Adialog.create();
//                                    alert.show();

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
        RequestQueue requestQueue = Volley.newRequestQueue(ZxingQRActivity.this);
        requestQueue.add(stringRequest);
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

                    showMessage(splited[0] + " has already scanned!");

                } else {
                    if(position.equals(Config.CHIEF))
                        getData();
                    else {
                        String status = mydb.updateAttendanceRecord(splited[0], subjectCode, staffID, "1", 0);
                        processGetData(status);
                    }
//                    Toast.makeText(getContext(), "Successfully added " + splited[0], Toast.LENGTH_LONG).show();
                }
            }
            if (jsonArray.length() == 0) {
                showMessage(splited[0] + " has already scanned!");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void processGetData(String response){
        if (response.equals("success checkin")) {
            showMessage(splited[0] + " has checked in for this course");
        } else if (response.equals("success checkout")){
            showMessage(splited[0] + " has checked out for this course");
        }
    }

    private void showMessage(String message){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mScannerView.resumeCameraPreview(ZxingQRActivity.this);
            }
        });

        builder.setMessage(message);
        AlertDialog alert1 = builder.create();
        alert1.show();

    }

}
