package com.aventuracctv.ordermangement.data;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.aventuracctv.ordermangement.base.App;
import com.aventuracctv.ordermangement.interfaces.LoginCallback;
import com.aventuracctv.ordermangement.net.ApiRequests;
import com.aventuracctv.ordermangement.net.GsonPostRequest;


/**
 * Created by jburmeister on 5/3/2016.
 */

public class LoginClass {

    private static final String TAG = "LoginClass";
    private static Context mCtx;
    private String username, password;

    private LoginCallback loginCallback;

    public void LoginClass(Context context, String username, String password) {
        mCtx = context;
        this.username = username;
        this.password = password;
        new login().execute(username, password);
    }

    private class login extends AsyncTask<String, Void, String>{
        ProgressDialog loading;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(mCtx, "Please Wait",null, true, true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();
            Toast.makeText(mCtx.getApplicationContext(),s,Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... params) {
            postGsonData();
            return  null;
        }
    }

    private void postGsonData() {
        final GsonPostRequest<User> gsonPostRequest =
                ApiRequests.login
                        (
                                new Response.Listener<User>() {
                                    @Override
                                    public void onResponse(User user) {
                                        // Deal with the DummyObject here

                                        onLoginSuccess(user);
                                    }
                                }
                                ,
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // Deal with the error here
                                        String msgError = error.getMessage();

                                        onLoginFailed(msgError);
                                    }
                                },
                                username, password
                        );

        App.addRequest(gsonPostRequest, TAG);
    }

    private void onLoginSuccess(User user) {
        Toast.makeText(mCtx, user.getmessage(), Toast.LENGTH_LONG).show();
    }

    //Ending up here when logging in
    private void onLoginFailed(String msgError) {
        Toast.makeText(mCtx, msgError, Toast.LENGTH_LONG).show();
    }
}