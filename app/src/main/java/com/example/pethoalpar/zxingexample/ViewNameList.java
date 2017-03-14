package com.example.pethoalpar.zxingexample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewNameList extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Spinner spinner;
    String subjectCode;
    Intent intent;
    RequestQueue requestQueue;
    ArrayList<ViewNameListModel> data = new ArrayList<>();
    ViewNameListAdapter nameListAdapter;
    RecyclerView nameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewnamelist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        spinner = (Spinner) findViewById(R.id.sort);
        intent = getIntent();
        subjectCode = intent.getStringExtra("subject_code");
        Toast.makeText(ViewNameList.this, subjectCode, Toast.LENGTH_SHORT).show();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.sorting,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> item, View view, int i, long l) {
        String selectedItem = item.getItemAtPosition(i).toString();

        switch (i){
            case 0:
                Toast.makeText(ViewNameList.this, "Item postion: 0 --" + selectedItem, Toast.LENGTH_SHORT).show();
                getNameList(Config.GET_ALL_DATA);
                break;
            case 1:
                Toast.makeText(ViewNameList.this, "Item postion: 1 --" + selectedItem, Toast.LENGTH_SHORT).show();
                getNameList(Config.GET_ATTENDEES_DATA);
                break;
            case 2:
                Toast.makeText(ViewNameList.this, "Item postion: 2 --" + selectedItem, Toast.LENGTH_SHORT).show();
                getNameList(Config.GET_ABSENTEES_DATA);
                break;
            case 3:
                Toast.makeText(ViewNameList.this, "Item postion: 3 --" + selectedItem, Toast.LENGTH_SHORT).show();
                getNameList(Config.GET_SUBMITTED_DATA);
                break;
            case 4:
                Toast.makeText(ViewNameList.this, "Item postion: 4 --" + selectedItem, Toast.LENGTH_SHORT).show();
                getNameList(Config.GET_INEXAMINATION_DATA);
                break;
            default:
                break;
        }
    }

    public void getNameList(String linkUrl){
        requestQueue = Volley.newRequestQueue(this);
        String getAllData = Config.BASE_URL + linkUrl;
        Log.d("URL", getAllData);
        StringRequest jsonObjectRequest1 = new StringRequest(Request.Method.POST, getAllData,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonObject) {

                        Log.d("result-----", jsonObject);
                        data.clear();
                        try {
                            JSONObject jsonObject1 = new JSONObject(jsonObject);
                            JSONArray jsonArray = jsonObject1.getJSONArray("result");
                            Toast.makeText(ViewNameList.this, "Result length" + jsonArray.length(), Toast.LENGTH_SHORT).show();

                            for(int i = 0 ; i < jsonArray.length() ; i++){
                                JSONObject result = jsonArray.getJSONObject(i);
                                ViewNameListModel model = new ViewNameListModel();
                                Log.d("studentName: ", result.getString("studentname"));
                                Log.d("matricNo: ", result.getString("matricno"));
                                model.setStudent_name(result.getString("studentname"));
                                model.setStudent_matric(result.getString("matricno"));
                                data.add(model);
                            }

                            Toast.makeText(ViewNameList.this, "Result length" + data.size(), Toast.LENGTH_SHORT).show();
                            nameList = (RecyclerView) findViewById(R.id.namelist);
                            nameListAdapter = new ViewNameListAdapter(ViewNameList.this, data);
                            nameList.setAdapter(nameListAdapter);
                            nameList.setLayoutManager(new LinearLayoutManager(ViewNameList.this));

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
        ){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("subject_code",subjectCode);
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest1);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
