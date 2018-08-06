package com.vb.vaultbox;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.vb.vaultbox.Utills.Global;
import com.vb.vaultbox.Utills.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;


public class Register extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, ResultCallback<People.LoadPeopleResult> {
    TextView mTitle, sign_in;
    EditText edttxt_fname, edttxt_lname, edttxt_email, edttxt_password, edttxt_repassword, edttxt_phone;
    ProgressWheel progress_wheel;
    RequestQueue queue;
    Button btn_signup;
    SharedPreferences prefs;
    TextView sign_up;
    /*google plus*/
    GoogleApiClient google_api_client;
    GoogleApiAvailability google_api_availability;
    private CallbackManager callbackmanager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private ConnectionResult connection_result;
    private int request_code;
    private boolean is_intent_inprogress;
    private boolean is_signInBtn_clicked;
    TextView toolbartext;
    String userType = "";
//    FirebaseAuth mAuth;
//    DatabaseReference mDatabase;
    private static final int SIGN_IN_CODE = 0;
    String personid, personName, personemail, type = "", personPhotoUrl;
    ImageView linear_fb, linear_gplus;
    ProgressDialog progress_dialog;
    CallbackManager callbackManager;

    String first_name_String = "", last_name_String = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        if (Build.VERSION.SDK_INT >= 21) {
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
//        }
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_help);
//        mTitle = (TextView) toolbar.findViewById(R.id.text_toolbar);
//        mTitle.setText("REGISTER");
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayShowTitleEnabled(false);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//        }
//
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
        sign_up = (TextView) findViewById(R.id.txt_signup);
        progress_wheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        queue = Volley.newRequestQueue(Register.this);
        prefs = PreferenceManager.getDefaultSharedPreferences(Register.this);

//        mAuth = FirebaseAuth.getInstance();
//        mDatabase = FirebaseDatabase.getInstance().getReference();

        sign_in = (TextView) findViewById(R.id.text_signin);
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Register.this.finish();
            }
        });


        edttxt_fname = (EditText) findViewById(R.id.txt_first_name);
        edttxt_lname = (EditText) findViewById(R.id.txt_last_name);
        edttxt_email = (EditText) findViewById(R.id.txt_register_email);
        edttxt_password = (EditText) findViewById(R.id.txt_register_password);
        edttxt_repassword = (EditText) findViewById(R.id.txt_register_confirmpassword);
        edttxt_phone = (EditText) findViewById(R.id.txt_register_phone);
        linear_fb = (ImageView) findViewById(R.id.linear_fb);
        linear_gplus = (ImageView) findViewById(R.id.linear_gplus);


        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!Global.validateName(edttxt_fname.getText().toString().trim())) {
                    edttxt_fname.setError(Html.fromHtml("Please enter first name"));
                    return;
                } else if (!Global.validateName(edttxt_lname.getText().toString().trim())) {
                    edttxt_lname.setError(Html.fromHtml("Please enter last name"));
                    return;
                } else if (!Global.validateEmail(edttxt_email.getText().toString().trim())) {
                    edttxt_email.setError(Html.fromHtml("Enter valid email id"));
                    return;
                } else if (!Global.validateName(edttxt_password.getText().toString().trim())) {
                    edttxt_password.setError(Html.fromHtml("Please enter password"));
                    return;
                } else if (edttxt_password.getText().toString().trim().length() < 6) {
                    edttxt_password.setError(Html.fromHtml("Password should be at least 6 characters"));
                    return;
                } else if (!Global.validateName(edttxt_repassword.getText().toString())) {
                    edttxt_repassword.setError(Html.fromHtml("Please enter Re-password"));
                    return;
                } else if (edttxt_repassword.getText().toString().length() < 6) {
                    edttxt_repassword.setError(Html.fromHtml("Re-Password should be at least 6 characters"));
                    return;
                } else if (!edttxt_repassword.getText().toString().equalsIgnoreCase(edttxt_password.getText().toString().trim())) {
                    edttxt_repassword.setError(null);
                    Toast.makeText(Register.this, "Password Do not matched", Toast.LENGTH_LONG).show();
                } else if (!Global.validateName(edttxt_phone.getText().toString().trim())) {
                    edttxt_phone.setError(Html.fromHtml("Please enter phone"));
                    return;
                } else {
                    progress_wheel.setVisibility(View.VISIBLE);
                    signup_task(Global.URL + "signup.php");
                }
            }
        });


        buidNewGoogleApiClient();
        callbackManager = CallbackManager.Factory.create();
        FacebookSdk.sdkInitialize(getApplicationContext());
        progress_dialog = new ProgressDialog(this);
        progress_dialog.setMessage("Signing in....");
        progress_dialog.setCancelable(false);

        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
            }
        };

        profileTracker = new ProfileTracker() {
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
//                displayMessage(newProfile);
            }
        };
        accessTokenTracker.startTracking();
        profileTracker.startTracking();


        linear_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFblogin();
            }
        });
        linear_gplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gPlusSignIn();
            }
        });


    }

    public void signup_task(String url) {
        final JSONObject json = new JSONObject();
        try {
//              param.put("email", emailid); param.put("password", password);
            json.put("email", edttxt_email.getText().toString().trim());
            json.put("password", edttxt_password.getText().toString().trim());
            json.put("FirstName", edttxt_fname.getText().toString().trim());
            json.put("LastName", edttxt_lname.getText().toString().trim());
            json.put("mobile", edttxt_phone.getText().toString().trim());
//            json.put("device_type", "android");
//            json.put("lat", "0");
//            json.put("lon", "0");
//            json.put("firebase_id", SessionManager.get_firebaseId(prefs));
//            json.put("device_token", FirebaseInstanceId.getInstance().getToken());
            Log.e("signup_task:  ", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        progress_wheel.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progress_wheel.setVisibility(View.GONE);
                        try {
                            Log.e("response   ", "" + response);
                            JSONObject jsonObject = response, jsonObject2;
//{"status":200,"message":"Successfully Registered","data":{"userId":5}}
                            if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                                jsonObject2 = jsonObject.getJSONObject("data");
                                SessionManager.save_user_id(prefs, jsonObject2.getString("userId"));
                                Global.msgDialog(Register.this, jsonObject.getString("message"));
//                                SessionManager.save_image(prefs, jsonObject2.getString("image"));
//                                SessionManager.save_emailid(prefs, jsonObject2.getString("emailId"));
//                                SessionManager.save_fisrtName(prefs, jsonObject2.getString("firstName"));
//                                SessionManager.save_mobile(prefs, jsonObject2.getString("mobile"));
//                                SessionManager.save_password(prefs, jsonObject2.getString("password2"));
//                                SessionManager.save_address(prefs, jsonObject2.getString("address"));
//                                SessionManager.save_(prefs, jsonObject2.getString("deviceToken"));
                                SessionManager.save_check_login(prefs, true);
//                                Intent in = new Intent(Register.this, ProfilePic.class);
//                                in.putExtra("screentype", "regi");
//                                startActivity(in);
//                                finish();
                                Intent in = new Intent(Register.this, HomeActivity.class);
                                startActivity(in);
                                finish();

                            } else if (jsonObject.getString("status").equals("Fails")) {
                                Global.msgDialog(Register.this, jsonObject.getString("message"));
                            } else {
                                Global.msgDialog(Register.this, jsonObject.getString("message"));
//                                Toast.makeText(Register.this, "Error From Server ", Toast.LENGTH_LONG).show();
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
                Global.msgDialog(Register.this, "Unable To Get Response ");
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        queue.add(jsonObjectRequest);
    }

//    public void signup_task(String url) {
//        progress_wheel.setVisibility(View.VISIBLE);
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            progress_wheel.setVisibility(View.GONE);
//                            System.out.println("response  " + response);
//                            JSONObject jsonObject2, jsonObject = new JSONObject(response), jsonObject3;
//
////
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        error.printStackTrace();
//                        progress_wheel.setVisibility(View.GONE);
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> param = new HashMap<String, String>();
//
////                email,password,FirstName,LastName,mobile
//                param.put("FirstName", edttxt_fname.getText().toString().trim());
//                param.put("LastName", edttxt_lname.getText().toString().trim());
//                param.put("email", edttxt_email.getText().toString().trim());
//                param.put("password", edttxt_password.getText().toString().trim());
//                param.put("mobile", edttxt_phone.getText().toString().trim());
////                param.put("firebaseId", SessionManager.get_firebaseid(prefs));
////                param.put("device", "android");
////                param.put("deviceToken", FirebaseInstanceId.getInstance().getToken());
//                System.out.println("data   " + param);
//                return param;
//            }
//        };
//        int socketTimeout = 30000;
//        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        postRequest.setRetryPolicy(policy);
//        queue.add(postRequest);
//    }

//    private void createAccount(final String email, final String password) {
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            onAuthSuccess(task.getResult().getUser());
//                        } else {
//                            progress_wheel.setVisibility(View.GONE);
//                            Log.d("task data", task.getException().toString());
//                            Toast.makeText(Register.this, "Firebase Sign Up Failed : " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
//    }
//
//    private void createAccountFb(final String email, final String password) {
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            onAuthSuccess(task.getResult().getUser());
//                        } else {
//                            progress_wheel.setVisibility(View.GONE);
//                            if (task.getException().getMessage().equalsIgnoreCase("The email address is already in use by another account.")) {
//                                loginWithFBFirebase(email, password);
//                            } else {
//                                Log.d("task data", task.getException().toString());
//                                Toast.makeText(Register.this, "Firebase Sign Up Failed : " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                            }
//
//                        }
//                    }
//                });
//    }
//
//    private void onAuthSuccess(FirebaseUser user) {
//        SessionManager.save_firebaseId(prefs, user.getUid());
//        if (type.equalsIgnoreCase("facebook") || type.equalsIgnoreCase("google")) {
//            HitWebservice(personid, personName, personemail, type, personPhotoUrl);
//            writeNewUser(user.getUid(), personName, user.getEmail());
//        } else {
//            writeNewUser(user.getUid(), first_name_String + " " + last_name_String, user.getEmail());
//        }
//
//    }

//    private void writeNewUser(String userId, String sname, String semail) {
//        User user = new User(sname, semail, userId);
//        mDatabase.child("users").child(userId).setValue(user);
//        if (type.equalsIgnoreCase("facebook") || type.equalsIgnoreCase("google")) {
//            HitWebservice(personid, personName, personemail, type, personPhotoUrl);
//        } else {
//            Signup_task(first_name_String, last_name_String, email_String, password_String, phone_String, tele_country_code_string);
//        }
//    }

//    private void loginWithFBFirebase(String email, String password) {
//        progress_wheel.setVisibility(View.VISIBLE);
//        mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            SessionManager.save_firebaseId(prefs, task.getResult().getUser().getUid());
////                            login_task(Global.URL, etxt_email.getText().toString().trim(), etxt_password.getText().toString().trim());
////                            webservice_login(email_String, password_String);
//                            HitWebservice(personid, personPhotoUrl, personName, lastName, personemail);
//                        } else {
//                            progress_wheel.setVisibility(View.GONE);
//                            Toast.makeText(Register.this, "Sign In Failed : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }


    private void onFblogin() {
        callbackmanager = CallbackManager.Factory.create();
        // Set permissions
        LoginManager.getInstance().logInWithReadPermissions(this,
                Arrays.asList("email", "public_profile"));

        LoginManager.getInstance().registerCallback(callbackmanager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {

                                    @Override
                                    public void onCompleted(JSONObject json,
                                                            GraphResponse response) {
                                        // TODO Auto-generated method stub
                                        if (response.getError() != null) {
                                            // handle error
                                            System.out.println("ERROR");
                                        } else {
                                            System.out.println("Success");
                                            try {
                                                String jsonresult = String.valueOf(json);
                                                System.out.println("JSON Result" + jsonresult);
                                                personid = json.getString("id");
                                                personemail = json.getString("email");
                                                personName = json.getString("first_name");
                                                System.out.println("Login details = " + personid + " " + personemail + " " + personName);
                                                personPhotoUrl = "https://graph.facebook.com/" + personid + "/picture?type=large";
                                                type = "facebook";
//                                                createAccountFb(personemail, personemail);
                                                HitWebservice(personid, personName, personemail, type, personPhotoUrl);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,first_name,last_name,link,email,picture");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        System.out.println("Login details = ");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        error.printStackTrace();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_CODE) {
            request_code = requestCode;
            if (resultCode != RESULT_OK) {
                is_signInBtn_clicked = false;
                progress_dialog.dismiss();
            }
            is_intent_inprogress = false;
            if (!google_api_client.isConnecting()) {
                google_api_client.connect();
            }
        }
        try {
            callbackmanager.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    public void HitWebservice(final String personid, final String name, final String email_String, final String type, final String image) {
        final JSONObject json = new JSONObject();
        try {
//            email,RegType,FirstName,LastName,mobile
//            json.put("device_token", FirebaseInstanceId.getInstance().getToken());
//            json.put("device_type", "android");
            json.put("email", email_String);
            String[] split = name.split(" ");
            try {
                json.put("FirstName", split[0]);
            } catch (Exception e) {
                json.put("FirstName", name);
            }
            try {
                json.put("LastName", split[1]);
            } catch (Exception e) {
                json.put("LastName", "");
            }
//            json.put("lat", "0");
//            json.put("lon", "0");
            json.put("mobile", "");
            json.put("RegType", type);
//            json.put("social_id", personid);
//            json.put("user_image", image);
//            json.put("firebase_id", SessionManager.get_firebaseId(prefs));


            Log.e("login data  ", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        progress_wheel.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, Global.URL + "socialLogin.php", json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("signup reponse", "" + response);
                        progress_wheel.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject2, jsonObject = new JSONObject(response.toString()), jsonObject1;
//                            jsonObject1 = jsonObject.getJSONObject("meta");
                            if (jsonObject.getString("status").equals("200")) {

                                jsonObject2 = jsonObject.getJSONObject("data");

//                                SessionManager.save_emailid(prefs, jsonObject2.getString("email"));
                                SessionManager.save_user_id(prefs, jsonObject2.getString("userId"));
//                                SessionManager.save_phone(prefs, jsonObject2.getString("phone"));
//                                SessionManager.save_name(prefs, jsonObject2.getString("first_name") + " " + jsonObject2.getString("last_name"));

                                SessionManager.save_check_login(prefs, true);
                                Intent intent = new Intent(Register.this, HomeActivity.class);
                                startActivity(intent);
                                finish();

                            } else if (jsonObject.getString("status").equalsIgnoreCase("Failed")) {
                                Global.msgDialog(Register.this, jsonObject.getString("message"));
                            } else {
                                Global.msgDialog(Register.this, jsonObject.getString("message"));
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
                Global.msgDialog(Register.this, "Unable To Get Response ");
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        queue.add(jsonObjectRequest);
    }

    public void printHashKey(Context pContext) {
        try {
            String packageName = pContext.getApplicationContext().getPackageName();
            PackageInfo info = getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("keyy  ", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("keyy  ", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("keyy  ", "printHashKey()", e);
        }
    }

    protected void onStart() {
        super.onStart();
        google_api_client.connect();
    }

    protected void onStop() {
        super.onStop();
        if (google_api_client.isConnected()) {
            google_api_client.disconnect();
        }
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    protected void onResume() {
        super.onResume();
        if (google_api_client.isConnected()) {
            google_api_client.connect();
        }
    }

    private void buidNewGoogleApiClient() {
        google_api_client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            google_api_availability.getErrorDialog(this, result.getErrorCode(), request_code).show();
            return;
        }
        if (!is_intent_inprogress) {
            connection_result = result;
            if (is_signInBtn_clicked) {
                resolveSignInError();
            }
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        is_signInBtn_clicked = false;
        // Get user's information and set it into the layout
        getProfileInfo();
        // Update the UI after signin
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        google_api_client.connect();
    }

    private void getProfileInfo() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(google_api_client) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(google_api_client);
                setPersonalInfo(currentPerson);
            } else {
                progress_dialog.dismiss();
                Toast.makeText(getApplicationContext(), "No Personal info mention", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            progress_dialog.dismiss();
            e.printStackTrace();
        }
    }

    private void resolveSignInError() {
        if (connection_result.hasResolution()) {
            try {
                is_intent_inprogress = true;
                connection_result.startResolutionForResult(this, SIGN_IN_CODE);
                Log.d("resolve error", "sign in error resolved");
            } catch (IntentSender.SendIntentException e) {
                is_intent_inprogress = false;
                google_api_client.connect();
            }
        }
    }

    private void setPersonalInfo(Person currentPerson) {

        // Google Plus Data will be there
        personName = currentPerson.getDisplayName();
        personPhotoUrl = currentPerson.getImage().getUrl();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        personemail = Plus.AccountApi.getAccountName(google_api_client);
        personid = currentPerson.getId();
        progress_dialog.dismiss();

        type = "google";
//        createAccountFb(personemail, personemail);
        HitWebservice(personid, personName, personemail, type, personPhotoUrl);
//        HitWebservice(id, name, email, type, imageurl);

        gPlusSignOut();
    }

    private void gPlusSignIn() {
        try {
            if (!google_api_client.isConnecting()) {
                Log.d("user connected", "connected");
                is_signInBtn_clicked = true;
                progress_dialog.show();
                resolveSignInError();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void gPlusSignOut() {
        if (google_api_client.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(google_api_client);
            google_api_client.disconnect();
            google_api_client.connect();
        }
    }

    @Override
    public void onResult(People.LoadPeopleResult peopleData) {
        if (peopleData.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
            PersonBuffer personBuffer = peopleData.getPersonBuffer();
            ArrayList<String> list = new ArrayList<String>();
            ArrayList<String> img_list = new ArrayList<String>();
            try {
                int count = personBuffer.getCount();
                for (int i = 0; i < count; i++) {
                    list.add(personBuffer.get(i).getDisplayName());
                    img_list.add(personBuffer.get(i).getImage().getUrl());
                }
            } finally {
                personBuffer.release();
            }
        } else {
            Log.e("circle error", "Error requesting visible circles: " + peopleData.getStatus());
        }
    }


}

