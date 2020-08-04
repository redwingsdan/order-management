package com.aventuracctv.ordermangement.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.aventuracctv.ordermangement.R;
import com.aventuracctv.ordermangement.data.LoginClass;

/**
 * Created by jburmeister on 5/3/2016.
 */

public class UserLoginActivity extends Dialog implements View.OnClickListener {

    private static final String TAG = "RegistrationActivity";
    public Activity c;

    private EditText editTextUsername, editTextPassword, editTextEmail;
    private Button btnLogin;
    private ProgressBar mProgressBar;

    SharedPreferences sharedPreferences;

    LoginClass login;
    String username, password;

    public UserLoginActivity(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.user_login);

        editTextUsername = (EditText) findViewById(R.id.input_username);
        editTextPassword = (EditText) findViewById(R.id.input_password);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        username = sharedPreferences.getString("username", "NONE");
        password = sharedPreferences.getString("password", "NONE");
        editTextUsername.setText(username);
        editTextPassword.setText(password);

    }

    @Override
    public void onClick(View v) {
        if(v == btnLogin){
            mProgressBar.setVisibility(View.VISIBLE);
            username = editTextUsername.getText().toString();
            password = editTextPassword.getText().toString();
            if (!username.equals("NONE")) {
                newPreferences();
                login = new LoginClass();
                login.LoginClass(this.getContext(), username, password);
                dismiss();
            }
        }
    }

    private void newPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();
    }

}