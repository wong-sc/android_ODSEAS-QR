package com.example.pethoalpar.zxingexample;

import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.os.Bundle;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements View.OnClickListener {

    //public static final String LOGIN_URL = "http://simplifiedcoding.16mb.com/UserRegistration/volleyLogin.php";
    private static final String LOGIN_URL = Config.BASE_URL+Config.LOGIN;

    public static final String KEY_STAFF_ID="staff_id";
    public static final String KEY_PASSWORD="staff_password";

    private EditText editTextStaffID;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView registerLink;

    private String staff_id;
    private String staff_password;

    public boolean loggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextStaffID = (EditText) findViewById(R.id.editTextStaffID);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        registerLink = (TextView)findViewById(R.id.tvRegisterLink);
        registerLink.setPaintFlags( registerLink.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));

        buttonLogin.setOnClickListener(this);
        registerLink.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //In onresume fetching value from sharedpreference
        SharedPreferences sharedPreferences = getSharedPreferences("myloginapp",Context.MODE_PRIVATE);

        //Fetching the boolean value form sharedpreferences
        loggedIn = sharedPreferences.getBoolean("loggedin", false);

        //If we will get true
        if(loggedIn){
            //We will start the Profile Activity
            Intent intent = new Intent(Login.this, Dashboard.class);
            startActivity(intent);
            finish();
        }
    }

    private void userLogin() {
        staff_id = editTextStaffID.getText().toString().trim();
        staff_password = editTextPassword.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.trim().equals("Successfully Login")){
                            openProfile(staff_id);
                        }else{
                            AlertDialog.Builder alert = new AlertDialog.Builder(Login.this);
                            alert.setTitle("Result");
                            alert.setMessage(response.toString());
                            alert.setCancelable(true);
                            alert.show();
                            Toast.makeText(Login.this,response,Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Login.this,error.toString(),Toast.LENGTH_LONG ).show();
                        AlertDialog.Builder alert = new AlertDialog.Builder(Login.this);
                        alert.setTitle("Result");
                        alert.setMessage(error.toString());
                        alert.setCancelable(true);
                        alert.show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put(KEY_STAFF_ID, staff_id);
                map.put(KEY_PASSWORD, staff_password);
                return map;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void openProfile(String staff_id){

        SharedPreferences sharedPreferences = Login.this.getSharedPreferences("myloginapp", Context.MODE_PRIVATE);

        //Creating editor to store values to shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //Adding values to editor
        editor.putBoolean("loggedin", true);
        editor.putString("staff_id", staff_id);
        //editor.putString(Config.EMAIL_SHARED_PREF, email);

        //Saving values to editor
        editor.commit();

        Intent intent = new Intent(this, Dashboard.class);
        intent.putExtra(KEY_STAFF_ID, staff_id);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if(v == buttonLogin){
            userLogin();
            //startActivity(new Intent(this, Login.class));
        }
        if(v == registerLink){
            startActivity(new Intent(this, Register.class));
        }
    }
}
