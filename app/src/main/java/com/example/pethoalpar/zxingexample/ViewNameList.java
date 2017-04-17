package com.example.pethoalpar.zxingexample;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.GenericArrayType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewNameList extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Spinner spinner;
    String course_id;
    Intent intent;
    RequestQueue requestQueue;
    ArrayList<ViewNameListModel> data = new ArrayList<>();
    ViewNameListAdapter nameListAdapter;
    RecyclerView nameList;
    SharedPreferences preferences;
    OfflineDatabase mydb;
    Boolean connection = true;
    TableLayout table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewnamelist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferences = ViewNameList.this.getSharedPreferences("myloginapp", Context.MODE_PRIVATE);
        mydb = new OfflineDatabase(this);
        spinner = (Spinner) findViewById(R.id.sort);
        intent = getIntent();
        course_id = intent.getStringExtra("course_id");
        Toast.makeText(ViewNameList.this, course_id, Toast.LENGTH_SHORT).show();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.sorting,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        table = (TableLayout) findViewById(R.id.namelist);

//        nameList = (RecyclerView) findViewById(R.id.namelist);
//        nameListAdapter = new ViewNameListAdapter(ViewNameList.this, data);
//        nameList.setAdapter(nameListAdapter);
//        nameList.setLayoutManager(new LinearLayoutManager(ViewNameList.this));
    }

    @Override
    public void onItemSelected(AdapterView<?> item, View view, int i, long l) {
        String selectedItem = item.getItemAtPosition(i).toString();
        String getnamelist;

        switch (i){
            case 0:
                Toast.makeText(ViewNameList.this, "Item position: 0 --" + selectedItem, Toast.LENGTH_SHORT).show();
                if(preferences.getString(Config.WIFI_STATUS, "").equals("Not connected to Internet")){
                    getnamelist = mydb.getAllData(course_id);
                    processNameList(getnamelist);
                }
                getNameList(Config.GET_ALL_DATA);
                break;
            case 1:
                Toast.makeText(ViewNameList.this, "Item position: 1 --" + selectedItem, Toast.LENGTH_SHORT).show();
                if(preferences.getString(Config.WIFI_STATUS, "").equals("Not connected to Internet")){
                    getnamelist = mydb.getAttendeesData(course_id);
                    processNameList(getnamelist);
                }
                getNameList(Config.GET_ATTENDEES_DATA);
                break;
            case 2:
                Toast.makeText(ViewNameList.this, "Item position: 2 --" + selectedItem, Toast.LENGTH_SHORT).show();
                if(preferences.getString(Config.WIFI_STATUS, "").equals("Not connected to Internet")) {
                    getnamelist = mydb.getAbsenteesData(course_id);
                    processNameList(getnamelist);
                }
                getNameList(Config.GET_ABSENTEES_DATA);
                break;
            case 3:
                Toast.makeText(ViewNameList.this, "Item position: 3 --" + selectedItem, Toast.LENGTH_SHORT).show();
                if(preferences.getString(Config.WIFI_STATUS, "").equals("Not connected to Internet")){
                    getnamelist = mydb.getSubmittedData(course_id);
                    processNameList(getnamelist);
                }
                getNameList(Config.GET_SUBMITTED_DATA);
                break;
            case 4:
                Toast.makeText(ViewNameList.this, "Item position: 4 --" + selectedItem, Toast.LENGTH_SHORT).show();
                if(preferences.getString(Config.WIFI_STATUS, "").equals("Not connected to Internet")){
                    getnamelist = mydb.getInExaminationData(course_id);
                    processNameList(getnamelist);
                }
                getNameList(Config.GET_INEXAMINATION_DATA);
                break;
            default:
                break;
        }
    }

    public void methodOptions(String url){
        String getnamelist;
        switch (url){
            case Config.GET_ALL_DATA:
                    getnamelist = mydb.getAllData(course_id);
                    processNameList(getnamelist);
                break;
            case Config.GET_ATTENDEES_DATA:
                    getnamelist = mydb.getAttendeesData(course_id);
                    processNameList(getnamelist);
                break;
            case Config.GET_ABSENTEES_DATA:
                    getnamelist = mydb.getAbsenteesData(course_id);
                    processNameList(getnamelist);
                break;
            case Config.GET_SUBMITTED_DATA:
                    getnamelist = mydb.getSubmittedData(course_id);
                    processNameList(getnamelist);
                break;
            case Config.GET_INEXAMINATION_DATA:
                    getnamelist = mydb.getInExaminationData(course_id);
                    processNameList(getnamelist);
                break;
            default:
                break;
        }

    }

    public void getNameList(final String linkUrl){
        requestQueue = Volley.newRequestQueue(this);
        String getAllData = Config.BASE_URL + linkUrl;
        Log.d("URL", getAllData);
        StringRequest jsonObjectRequest1 = new StringRequest(Request.Method.POST, getAllData,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonObject) {
                        processNameList(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("Volley line 105","Error");
                        connection = false;
                        methodOptions(linkUrl);
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

    public void processNameList(String result){
        Log.d("result-----", result);
        data.clear();
        try {
            JSONArray jsonArray = new JSONArray(result); // convert string to JSON Array
            Toast.makeText(ViewNameList.this, "Result length" + jsonArray.length(), Toast.LENGTH_SHORT).show();
            int count = table.getChildCount();
            Log.d("count", count + "");
            if (count > 1) {
                table.removeViews(1, count - 1);
            }

            for(int i = 0 ; i < jsonArray.length() ; i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                TableRow row = new TableRow(this);
//                row.setWeightSum(8);
                row.setPadding(5, 5, 5, 5);

                TableRow.LayoutParams bilParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.1f);
//                bilParams.weight = 1;

                TableRow.LayoutParams matricParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.25f);
//                bilParams.weight = 2;

                TableRow.LayoutParams studentNameParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.35f);
//                studentNameParams.weight = 4;

                TableRow.LayoutParams statusParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.18f);
//                statusParams.weight = 1;

                TextView bil = new TextView(this);
                bil.setText(String.valueOf(i+1));
                bil.setLayoutParams(bilParams);
                row.addView(bil);

                TextView matric = new TextView(this);
                matric.setText(jsonObject.getString("student_id"));
                matric.setLayoutParams(matricParams);
                row.addView(matric);

                TextView student_name = new TextView(this);
                student_name.setText(jsonObject.getString("student_name"));
                student_name.setLayoutParams(studentNameParams);
                row.addView(student_name);

                ImageView status = new ImageView(this);
                if(preferences.getString(Config.WIFI_STATUS, "").equals("Not connected to Internet") || !connection) {
                    if(jsonObject.getString("status").equals("1"))
                        status.setImageResource(R.drawable.ic_check_circle_black_24dp);
                    else
                        status.setImageResource(R.drawable.ic_sync_problem_black_24dp);
                } else status.setImageResource(R.drawable.ic_check_circle_black_24dp);

                status.setLayoutParams(statusParams);
                row.addView(status);
                table.addView(row);

//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                ViewNameListModel model = new ViewNameListModel();
//                Log.d("student_name: ", jsonObject.getString("student_name"));
//                Log.d("student_id: ", jsonObject.getString("student_id"));
//                model.setStudent_name(jsonObject.getString("student_name"));
//                model.setStudent_matric(jsonObject.getString("student_id"));
//                if(preferences.getString(Config.WIFI_STATUS, "").equals("Not connected to Internet") || !connection) {
//                    if(jsonObject.getString("status").equals("1"))
//                        model.setStatus(R.drawable.ic_check_circle_black_24dp);
//                    else
//                        model.setStatus(R.drawable.ic_sync_problem_black_24dp);
//                } else model.setStatus(R.drawable.ic_check_circle_black_24dp);
//                data.add(model);
            }

//            nameListAdapter.notifyDataSetChanged();
            Toast.makeText(ViewNameList.this, "Result length " + data.size(), Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}
