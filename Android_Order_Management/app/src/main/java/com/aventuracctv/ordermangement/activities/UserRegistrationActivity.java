package com.aventuracctv.ordermangement.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aventuracctv.ordermangement.R;
import com.aventuracctv.ordermangement.base.App;
import com.aventuracctv.ordermangement.data.PostResponse;
import com.aventuracctv.ordermangement.data.User;
import com.aventuracctv.ordermangement.net.ApiRequests;
import com.aventuracctv.ordermangement.net.GsonPostRequest;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by jburmeister on 5/3/2016.
 */
public class UserRegistrationActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "RegistrationActivity";

    private EditText editTextUsername, editTextPassword, editTextEmail;
    private Button btnRegister;
    private ProgressBar mProgressBar;

    String username, password, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_registration);

        editTextEmail = (EditText) findViewById(R.id.input_email);
        editTextUsername = (EditText) findViewById(R.id.input_username);
        editTextPassword = (EditText) findViewById(R.id.input_password);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);

        btnRegister = (Button) findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == btnRegister){
            mProgressBar.setVisibility(View.VISIBLE);
            btnRegister.setEnabled(false);
            registerUser();
        }
    }

    private void registerUser() {
        username = editTextUsername.getText().toString().trim().toLowerCase();
        password = editTextPassword.getText().toString().trim().toLowerCase();
        email = editTextEmail.getText().toString().trim().toLowerCase();

        register();
    }

    private void register() {
        class RegisterUser extends AsyncTask<String, Void, String>{
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(UserRegistrationActivity.this, "Please Wait",null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }
            //
            @Override
            protected String doInBackground(String... params) {
                postGsonData();
                return  null;
            }
        }

        RegisterUser ru = new RegisterUser();
        ru.execute(username, password, email);
    }

    public void postGsonData() {
       /*
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.51.28/order_management/v1/register",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server
                        if(response.equalsIgnoreCase("success")){
                            //Creating a shared preference
                            SharedPreferences sharedPreferences = UserRegistrationActivity.this.getSharedPreferences("myloginapp", Context.MODE_PRIVATE);

                            //Creating editor to store values to shared preferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            //Adding values to editor
                            editor.putBoolean("loggedin", true);
                            editor.putString("email", email);

                            //Saving values to editor
                            editor.commit();
                            Toast.makeText(UserRegistrationActivity.this, "String Response succeding", Toast.LENGTH_LONG).show();
                            //Starting profile activity
                            Intent intent = new Intent(UserRegistrationActivity.this, MainActivity.class);
                            startActivity(intent);
                        }else{
                            //If the server response is not success
                            //Displaying an error message on toast
                            Toast.makeText(UserRegistrationActivity.this, "Invalid username or password", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                //Adding parameters to request
                params.put("email", email);
                params.put("password", password);

                //returning parameter
                return params;
            }
        };

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        Toast.makeText(UserRegistrationActivity.this, "Added String request to queue", Toast.LENGTH_LONG).show();
        */
        final GsonPostRequest gsonPostRequest =
                ApiRequests.registerAccount
                        (
                                new Response.Listener<User>() {
                                    @Override
                                    public void onResponse(User user) {
                                        mProgressBar.setVisibility(View.GONE);
                                        onSignupSuccess(user);
                                    }
                                }
                                ,
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        String msgError = error.getMessage();
                                        mProgressBar.setVisibility(View.GONE);
                                        onSignupFailed(msgError);
                                    }
                                },
                                username, password, email
                        );

        App.addRequest(gsonPostRequest, TAG);
        //Toast.makeText(UserRegistrationActivity.this, "Added gson request to queue", Toast.LENGTH_LONG).show();
    }

    public void onSignupSuccess(User user) {
        btnRegister.setEnabled(true);
        setResult(RESULT_OK, null);
        Toast.makeText(getBaseContext(), user.getmessage(), Toast.LENGTH_LONG).show();
        finish();
    }

    public void onSignupFailed(String msgError) {
        Toast.makeText(getBaseContext(), msgError, Toast.LENGTH_LONG).show();
        btnRegister.setEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Remove all network call from stack
        App.cancelAllRequests(TAG);
    }
}