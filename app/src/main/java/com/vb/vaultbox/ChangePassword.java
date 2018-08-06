package com.vb.vaultbox;


import android.content.Intent;
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
import com.vb.vaultbox.Utills.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;


public class ChangePassword extends AppCompatActivity {

    private static final String TAG = "ChangePassword";
    Button change_pass;
    EditText old_pass, new_pass, conf_pass;
    TextView mTitle, forgot_password;
    SharedPreferences prefs;
    RequestQueue queue;
    ProgressWheel progress_wheel;
    String txtold_pass = "", txtnew_pass = "", txtconf_pass = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

//        Toolbar toolbar = (Toolbar) EditProfile.this.findViewById(R.id.toolbar);
//        toolbar.setTitle("Edit Profile");
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            Drawable background = getResources().getDrawable(R.color.colorPrimary);
            getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));
            getWindow().setBackgroundDrawable(background);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.text_toolbar);
        mTitle.setText(getResources().getString(R.string.change_password));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        prefs = PreferenceManager.getDefaultSharedPreferences(ChangePassword.this);
        progress_wheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        old_pass = (EditText) findViewById(R.id.old_pass);
        new_pass = (EditText) findViewById(R.id.new_pass);
        conf_pass = (EditText) findViewById(R.id.conf_pass);
        forgot_password = (TextView) findViewById(R.id.forgot_password);
        change_pass = (Button) findViewById(R.id.change_pass);

        change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMethod();
            }
        });

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChangePassword.this, ForgotPassword.class);
                startActivity(intent);
            }
        });
//        return view;
    }

    public void changeMethod() {

        try {
            txtold_pass = old_pass.getText().toString().trim();
            txtnew_pass = new_pass.getText().toString().trim();
            txtconf_pass = conf_pass.getText().toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (txtold_pass.length() < 1) {
            Toast.makeText(ChangePassword.this, "Please enter old Password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (txtnew_pass.length() < 1) {
            Toast.makeText(ChangePassword.this, "Please enter new Password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!txtconf_pass.equalsIgnoreCase(txtnew_pass)) {
            Toast.makeText(ChangePassword.this, "Passwords must be Same", Toast.LENGTH_SHORT).show();
            return;
        }
        if (txtold_pass.equals(txtnew_pass)) {
            Toast.makeText(ChangePassword.this, "Passwords must not be Same", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Global.isOnline(ChangePassword.this)) {
//            if (!(SessionManager.get_usertype(prefs).equalsIgnoreCase("google") || SessionManager.get_usertype(prefs).equalsIgnoreCase("facebook"))) {
            progress_wheel.setVisibility(View.VISIBLE);
//            firebaseChangePassword();
//                changePassword(Global.URL+"ChangePassword");
//            } else {
            changePassword_task(Global.URL + "changePassword.php");
//            }
        } else {
            Global.showDialog(ChangePassword.this);
        }

    }

//    public void firebaseChangePassword() {
//        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        try {
//            AuthCredential credential = EmailAuthProvider
//                    .getCredential(SessionManager.get_emailid(prefs), txtold_pass);
//            user.reauthenticate(credential)
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
//                                user.updatePassword(txtnew_pass).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if (task.isSuccessful()) {
//                                            SessionManager.save_password(prefs, txtnew_pass);
//
//                                            progress_wheel.setVisibility(View.GONE);
//                                            progress_wheel.setVisibility(View.VISIBLE);
//                                            changePassword_task(Global.URL + "ChangePassword");
//
//                                            Log.d(TAG, "Password updated");
//                                        } else {
//                                            progress_wheel.setVisibility(View.GONE);
//                                            Toast.makeText(ChangePassword.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//
//                                            Log.d(TAG, "Error password not updated");
//                                        }
//                                    }
//                                });
//                            } else {
//                                progress_wheel.setVisibility(View.GONE);
//                                Log.d(TAG, "Error auth failed");
//                            }
//                        }
//                    });
//        } catch (Exception e) {
//            progress_wheel.setVisibility(View.GONE);
//            e.printStackTrace();
//        }
//    }

    public void changePassword_task(String url) {
        queue = Volley.newRequestQueue(ChangePassword.this);
        final JSONObject json = new JSONObject();
        try {
//   userID,current_password,new_password
            json.put("new_password", txtnew_pass);
            json.put("current_password", txtold_pass);
            json.put("userID", SessionManager.get_user_id(prefs));
            Log.e("", "change pass data   " + json);
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
                            JSONObject jsonObject = new JSONObject(response.toString()), jsonObject1;
                            if (jsonObject.getString("status").equals("200")) {
                                SessionManager.save_password(prefs, txtnew_pass);
//                                Global.msgDialog(ChangePassword.this, "Password Changed Sucessfully");
                                Global.msgDialog(ChangePassword.this, jsonObject.getString("message"));
                                old_pass.setText("");
                                new_pass.setText("");
                                conf_pass.setText("");

                            } else if (jsonObject.getString("status").equalsIgnoreCase("Failed")) {
                                Global.msgDialog(ChangePassword.this, jsonObject.getString("message"));
                            } else {
                                Global.msgDialog(ChangePassword.this, jsonObject.getString("message"));
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
                Global.msgDialog(ChangePassword.this, "Unable To Get Response ");
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        queue.add(jsonObjectRequest);
    }

}
