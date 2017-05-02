package app.app.app.odseasqr;

import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
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
import app.app.app.odseasqr.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements View.OnClickListener {

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
                        try {
                            JSONObject result = new JSONObject(response);
                            if(result.getString("status").equals("Successfully Login")){
                                openProfile(result.getString("staff_id"), result.getString("staff_name"));
                            }else{
                                AlertDialog.Builder alert = new AlertDialog.Builder(Login.this);
                                alert.setTitle("Result");
                                alert.setMessage(result.getString("status"));
                                alert.setCancelable(true);
                                alert.show();
                                Toast.makeText(Login.this,response,Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Login.this,error.toString(),Toast.LENGTH_LONG).show();
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

    private void openProfile(String staff_id, String staff_name){

        SharedPreferences sharedPreferences = Login.this.getSharedPreferences("myloginapp", Context.MODE_PRIVATE);

        //Creating editor to store values to shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //Adding values to editor
        editor.putBoolean("loggedin", true);

        /*CHECK WHETHER IS FIRST TIME LOGIN, DECIDE WANT TO DOWNLOAD DATA FROM SERVER OR NOT*/
        editor.putBoolean("firstTimeLogin", true);

        /*STORE STAFF ID AS PREFERENCES FOR FUTURE USAGE*/
        editor.putString("staff_id", staff_id);
        editor.putString("staff_name", staff_name);

        //Saving values to editor
        editor.apply();
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
