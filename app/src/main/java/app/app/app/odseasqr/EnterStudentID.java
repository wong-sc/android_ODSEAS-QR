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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import app.app.app.odseasqr.R;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EnterStudentID extends Fragment implements View.OnClickListener{

    public static final String ARG_PAGE = "ARF_PAGE";
    public String dataStringStudentID;
    public String dataStringSubjectCode, subjectName;
    public String staffID, position;
    SharedPreferences preferences;
    OfflineDatabase mydb;
    RequestQueue requestQueue;

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
        buttonComfirm.setClickable(false);

        TextView subjectname = (TextView) v.findViewById(R.id.textViewSubjectName);

        Intent i = getActivity().getIntent();
        dataStringSubjectCode = i.getStringExtra("passDataValue");
        subjectName = i.getStringExtra("passSubjectInfo");
        subjectname.setText(subjectName);

        mydb = new OfflineDatabase(getContext());
        requestQueue = Volley.newRequestQueue(getActivity());
        preferences = getActivity().getSharedPreferences("myloginapp", Context.MODE_PRIVATE);
        staffID = preferences.getString("staff_id", "null");
        position = preferences.getString(Dashboard.POSITION, "null");
        return v;
    }

    public void buttonEnter(View v) {

        studentid = (EditText) getActivity().findViewById(R.id.studentid);
        dataStringStudentID = studentid.getText().toString();

        /* get the invigilator position, if == CHIEF then perform both offline/online function
        * else only go for offline function*/
        if(is_time_available()){
            if(position.equals(Config.CHIEF)){
                if(preferences.getString(Config.WIFI_STATUS, "").equals(Config.NOT_CONNECTED)) {
                    String studentSubject = mydb.getStudentSubject(dataStringStudentID);
                    Log.d("Result scan -", studentSubject);
                    processStudentSubject(studentSubject);
                }
                else
                    checkStudent();
            } else {
                String studentSubject = mydb.getStudentSubject(dataStringStudentID);
                Log.d("Result scan -", studentSubject);
                processStudentSubject(studentSubject);
            }
        } else {
            showMessage("Course Expired", "You have exceed 30 minutes after exam.");
        }
    }

    private boolean is_time_available(){
        String available = mydb.CheckCourseTime(dataStringSubjectCode);
        if(available.equals(Config.AVAILABLE))
            return true;
        else return false;
    }

    public void buttonConfirm(View v) {

            if(position.equals(Config.CHIEF)){

                if(preferences.getString(Config.WIFI_STATUS, "").equals(Config.NOT_CONNECTED)){
                    String checkScan = mydb.checkAlreadyScan(dataStringSubjectCode, dataStringStudentID);
                    processStudentIschecked(checkScan);
                } else
                    checkAlreadyScan();

            } else {

                String checkScan = mydb.checkAlreadyScan(dataStringSubjectCode, dataStringStudentID);
                processStudentIschecked(checkScan);

            }
    }

    public void checkAlreadyScan() {
        String checkAlreadyScan = Config.BASE_URL + Config.CHECK_ALREADY_SCAN;
        requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, checkAlreadyScan,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonObject) {
                        processStudentIschecked(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("Volley", volleyError.toString());
                        String checkScan = mydb.checkAlreadyScan(dataStringSubjectCode, dataStringStudentID);
                        processStudentIschecked(checkScan);
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("student_id", dataStringStudentID);
                params.put("course_id", dataStringSubjectCode);
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
                        processStudentSubject(result);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("Volley-studentError", volleyError.getLocalizedMessage() + "");
                        /*Loading local data once server encounter error*/
                        String studentSubject = mydb.getStudentSubject(dataStringStudentID);
                        Log.d("Result scan -", studentSubject);
                        processStudentSubject(studentSubject);
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("student_id", dataStringStudentID);
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public void processStudentSubject(String result){
        foundStudent = false;
        Log.d("Result", result);
        try {
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String subject_code = object.getString("course_id");
                Log.d("hye course code", subject_code);
                if (dataStringSubjectCode.equals(subject_code)) {
                    Toast.makeText(getActivity(),"Subject found",Toast.LENGTH_LONG).show();
                    foundStudent = true;
                    buttonComfirm.setClickable(true);
                    if(preferences.getString(Config.WIFI_STATUS, "").equals("Not connected to Internet")){
                        String studentName = mydb.getStudentData(dataStringStudentID);
                        processStudentName(studentName);
                    } else {
                        getStudentName();
                    }
                }
            }
            if (!foundStudent) {
                Toast.makeText(getActivity(),"student does not exists",Toast.LENGTH_LONG).show();
                showMessage("Alert", "Student does not register for this course.");
                buttonComfirm.setClickable(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

                /* if isChecked == 1, this student had taken the attendance */
                if (Scanned.equals(isScanned)) {
                    showMessage("Alert", dataStringStudentID + " has already scanned!");
                    studentid.setText("");
                    studentname.setText("Student Name: ");
                    buttonComfirm.setClickable(false);
                } else {

//                    if(preferences.getString(Config.WIFI_STATUS, "").equals("Not connected to Internet")){
//                        String status = mydb.updateAttendanceRecord(dataStringStudentID, dataStringSubjectCode, staffID, "2", 3);
//                        processGetData(status);
//                    } else
                    if(position.equals(Config.CHIEF))
                        getData();
                    else {
                        String status = mydb.updateAttendanceRecord(dataStringStudentID, dataStringSubjectCode, staffID, "2", 0);
                        processGetData(status);
                    }

                    Toast.makeText(getContext(), "Successfully added " + dataStringStudentID, Toast.LENGTH_LONG).show();
                    studentid.setText("");
                    studentname.setText("Student Name: ");
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void processStudentName(String result){

        try {
            JSONArray jsonArray = new JSONArray(result);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String stud_name = jsonObject.getString("student_name");
                studentname.append(stud_name);
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(),"JSON  Error",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
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
                        processStudentName(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("Volley", "Error");
                        Toast.makeText(getActivity(),"Volley Error",Toast.LENGTH_LONG).show();
                        String studentName = mydb.getStudentData(dataStringStudentID);
                        processStudentName(studentName);
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("student_id", dataStringStudentID);
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public void processGetData(String response){
        if (response.equals("success checkin")) {

            showMessage("Alert", "Student has checked in for this course");
            buttonComfirm.setClickable(false);

        } else if (response.equals("success checkout")){

            showMessage("Alert", "Student has checked out for this course");
            buttonComfirm.setClickable(false);

        }
    }

    public void getData() {
        String getUrl = Config.BASE_URL + Config.UPDATE_ATTENDANCE_DATA;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                processGetData(response);
                String status = mydb.updateAttendanceRecord(dataStringStudentID, dataStringSubjectCode, staffID, "2", 1);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.toString(),Toast.LENGTH_LONG).show();

                        String status = mydb.updateAttendanceRecord(dataStringStudentID, dataStringSubjectCode, staffID, "2", 0);
                        processGetData(status);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("student_id", dataStringStudentID);
                params.put("course_id", dataStringSubjectCode);
                params.put("staff_id", staffID);
                params.put("style_id", "2");
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
