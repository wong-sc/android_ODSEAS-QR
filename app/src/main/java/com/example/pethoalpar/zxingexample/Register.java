package com.example.pethoalpar.zxingexample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity  implements View.OnClickListener {

    //private static final String REGISTER_URL = "http://10.64.117.203/seas/staff/register.php";
    private static final String REGISTER_URL = Config.BASE_URL+"seas/staff/register.php";

    public static final String KEY_STAFF_ID = "staff_id";
    public static final String KEY_PASSWORD = "staff_password";
    public static final String KEY_EMAIL = "staff_email";


    private EditText editTextStaffID;
    private EditText editTextEmail;
    private EditText editTextPassword;

    private Button buttonRegister;
    private TextView loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextStaffID = (EditText) findViewById(R.id.editTextStaffID);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextEmail= (EditText) findViewById(R.id.editTextEmail);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        loginLink = (TextView)findViewById(R.id.tvLoginLink);

        buttonRegister.setOnClickListener(this);
        loginLink.setOnClickListener(this);
    }

    private void registerUser(){
        final String staff_id = editTextStaffID.getText().toString().trim();
        final String staff_password = editTextPassword.getText().toString().trim();
        final String staff_email = editTextEmail.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(Register.this,response,Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Register.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(KEY_STAFF_ID,staff_id);
                params.put(KEY_PASSWORD,staff_password);
                params.put(KEY_EMAIL, staff_email);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View v) {
        if(v == buttonRegister){
            registerUser();
        }
        if(v == loginLink){
            startActivity(new Intent(this, Login.class));
        }
    }
}
