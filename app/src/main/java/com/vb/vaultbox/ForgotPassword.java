package com.vb.vaultbox;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.vb.vaultbox.Utills.Global;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by user1 on 3/17/2016.
 */
public class ForgotPassword extends AppCompatActivity {
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Button submit;
    EditText email;
    ProgressWheel progress_wheel;
    SharedPreferences prefs;
    RequestQueue queue;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        if (!Global.isOnline(ForgotPassword.this)) {
            Global.showDialog(ForgotPassword.this);
            return;
        }

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            Drawable background = getResources().getDrawable(R.color.colorPrimary);
            getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));
            getWindow().setBackgroundDrawable(background);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.text_toolbar);
        mTitle.setText(getResources().getString(R.string.forgot_password));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        queue = Volley.newRequestQueue(ForgotPassword.this);
        submit = (Button) findViewById(R.id.submit);
        email = (EditText) findViewById(R.id.email);
        progress_wheel = (ProgressWheel) findViewById(R.id.progress_wheel);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgetMethod();
            }
        });

    }


    public void forgetMethod() {
        if (email.getText().toString().length() < 1) {
            Toast.makeText(this, "Please enter Email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Global.validateEmail(email.getText().toString())) {
            Toast.makeText(this, "Please enter a valid Email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Global.isOnline(ForgotPassword.this)) {
            progress_wheel.setVisibility(View.VISIBLE);
//            forgotPassword_task(Global.URL+"Forgotpassword", email.getText().toString().trim());
        } else {
            Global.showDialog(ForgotPassword.this);
        }
    }

    public void forgotPassword_task(String url, final String email_String) {
        final JSONObject json = new JSONObject();
        try {
            json.put("email", email_String);
            Log.e("", "forgot data   " + json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progress_wheel.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v(" reponse", "" + response);
                        progress_wheel.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject2, jsonObject = new JSONObject(response.toString()), jsonObject1;
                            jsonObject1 = jsonObject.getJSONObject("meta");
                            if (jsonObject1.getString("status").equals("Success")) {

                                Global.msgDialog(ForgotPassword.this, jsonObject1.getString("message"));

                            } else if (jsonObject1.getString("status").equalsIgnoreCase("Failed")) {
                                Global.msgDialog(ForgotPassword.this, jsonObject1.getString("message"));
                            } else {
                                Global.msgDialog(ForgotPassword.this, "Error in server");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progress_wheel.setVisibility(View.GONE);
                Global.msgDialog(ForgotPassword.this, "Unable To Get Response ");
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        queue.add(jsonObjectRequest);
    }


}
