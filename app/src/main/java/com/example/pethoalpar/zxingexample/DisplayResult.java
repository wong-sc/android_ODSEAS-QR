package com.example.pethoalpar.zxingexample;

//import android.app.Fragment;
import android.support.v4.app.Fragment;
        import android.content.Intent;
        import android.os.Bundle;
import android.support.annotation.Nullable;
        import android.util.Log;
import android.view.LayoutInflater;
        import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
        import android.widget.TextView;

        import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

        import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DisplayResult extends Fragment {

    public static final String ARG_PAGE = "ARF_PAGE";

    private int mPage;
    private TextView textViewAttended,textViewBooklet;
    RequestQueue requestQueue;
    String subjectCode;

    public static DisplayResult newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        DisplayResult fragment = new DisplayResult();
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
        View v = inflater.inflate(R.layout.activity_display_result,container,false);
        TextView coursename = (TextView) v.findViewById(R.id.coursename);
        TextView studentnumber = (TextView) v.findViewById(R.id.total);
        textViewAttended = (TextView) v.findViewById(R.id.attendee);
        textViewBooklet = (TextView) v.findViewById(R.id.bookletnum);
        Intent i = getActivity().getIntent();
        String dataStringSubjectCode = i.getStringExtra("passSubjectInfo");
        String dataStringStudentNumber = i.getStringExtra("studentnumber");
        subjectCode = i.getStringExtra("passDataValue");
        coursename.setText(dataStringSubjectCode);
        studentnumber.append(dataStringStudentNumber);
        Button viewlist = (Button) v.findViewById(R.id.viewlist);
        viewlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ViewNameList.class);
                startActivity(intent);
            }
        });
        Button stopscan = (Button) v.findViewById(R.id.stopscan);
        getAttendedData();
        getAnswerBooklet();
        return v;
    }

    public void getAttendedData() {
        requestQueue = Volley.newRequestQueue(getActivity());
        String getAttendedData = Config.BASE_URL + Config.GET_ATTENDED_DATA;
        StringRequest jsonObjectRequest1 = new StringRequest(Request.Method.POST, getAttendedData,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonObject) {

                        try {
                            JSONObject jsonObject1 = new JSONObject(jsonObject);
                            JSONArray jsonArray = jsonObject1.getJSONArray("result");

                            for(int i = 0 ; i < jsonArray.length() ; i++){
                                JSONObject result = jsonArray.getJSONObject(i);

                                String attended = result.getString("attended");
                                textViewAttended.setText(attended);
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
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("subject_code",subjectCode);
                    return params;
                }
            };
            requestQueue.add(jsonObjectRequest1);

    }

    public void getAnswerBooklet() {
        String getAnswerBooklet = Config.BASE_URL + Config.GET_ANS_BOOKLETS;
        requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest jsonObjectRequest1 = new StringRequest(Request.Method.POST, getAnswerBooklet,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonObject) {

                        try {
                            JSONObject jsonObject1 = new JSONObject(jsonObject);
                            JSONArray jsonArray = jsonObject1.getJSONArray("result");

                            for(int i = 0 ; i < jsonArray.length() ; i++){
                                JSONObject result = jsonArray.getJSONObject(i);

                                String booklet = result.getString("booklet");
                                textViewBooklet.setText(booklet);
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
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("subject_code",subjectCode);
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest1);

    }
}
