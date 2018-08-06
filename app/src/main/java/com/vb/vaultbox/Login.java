package com.vb.vaultbox;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.vb.vaultbox.Utills.Global;
import com.vb.vaultbox.Utills.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DatabaseReference;

public class Login extends AppCompatActivity {
    //    implements GoogleApiClient.OnConnectionFailedListener,
//        GoogleApiClient.ConnectionCallbacks, ResultCallback<People.LoadPeopleResult> {
    EditText editText_Email, editText_Password;
    TextView mTitle, forgot_password, signup, login;
    String email_String, password_String = "";
    ProgressWheel progress_wheel;
    SharedPreferences prefs;
    RequestQueue queue;
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
    ProgressDialog progress_dialog;
    String personid, personName, personemail, type = "", personPhotoUrl;
    ImageView linear_fb, linear_gplus;

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FirebaseApp.initializeApp(this);
//        buidNewGoogleApiClient();
        setContentView(R.layout.activity_sigin_in);

        prefs = PreferenceManager.getDefaultSharedPreferences(Login.this);
        queue = Volley.newRequestQueue(Login.this);
//        mAuth = FirebaseAuth.getInstance();
//        mDatabase = FirebaseDatabase.getInstance().getReference();

//        callbackManager = CallbackManager.Factory.create();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(Login.this, gso);

        FacebookSdk.sdkInitialize(getApplicationContext());

        login = (TextView) findViewById(R.id.text_signin);
        signup = (TextView) findViewById(R.id.txt_signup);
        editText_Email = (EditText) findViewById(R.id.login_email);
        editText_Password = (EditText) findViewById(R.id.login_password);
        linear_fb = (ImageView) findViewById(R.id.linear_fb);
        linear_gplus = (ImageView) findViewById(R.id.linear_gplus);
        progress_wheel = (ProgressWheel) findViewById(R.id.progress_wheel);

//        printHashKey(Login.this);

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

//        FirebaseApp.initializeApp(this);
//        Log.d("tag ", "InstanceID token: " + FirebaseInstanceId.getInstance().getToken());

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
//                displayMessage(newProfile);
            }
        };
        accessTokenTracker.startTracking();
        profileTracker.startTracking();


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Register.class);
                startActivity(i);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email_String = editText_Email.getText().toString().trim();
                password_String = editText_Password.getText().toString().trim();

                if (!Global.validateEmail(email_String)) {
                    editText_Email.setError(Html.fromHtml("Please enter valid email id"));
                    return;
                } else if (!Global.validateName(password_String)) {
                    editText_Password.setError(Html.fromHtml("Please enter password"));
                    return;
                } else {
                    login_task(email_String, password_String);
                }
//                Intent intent = new Intent(Login.this, HomeActivity.class);
//                startActivity(intent);
//                finish();
//                SessionManager.save_check_login(prefs, true);
            }
        });
        linear_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If Facebook login is selected then this function will run,
                // facebook login is used for User
                onFblogin();
            }
        });
        linear_gplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If Google login is selected then this function will run,
                // google login is used for User
//                gPlusSignIn();
                signIn();
            }
        });

//        forgot_password.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(), Forgot_Password.class);
//                startActivity(i);
//            }
//        });

    }

//    private void loginWithFirebase(String email, String password) {
//        progress_wheel.setVisibility(View.VISIBLE);
//        mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
////                            FirebaseUser user = mAuth.getCurrentUser();
////                            SessionManager.save_firebaseId(prefs, user.getUid().toString());
//                            SessionManager.save_firebaseId(prefs, task.getResult().getUser().getUid());
//                            login_task(email_String, password_String);
//                        } else {
//                            progress_wheel.setVisibility(View.GONE);
//                            Toast.makeText(Login.this, "FireBase Sign In Failed : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
//
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
//                            HitWebservice(personid, personName, personemail, type, personPhotoUrl);
//                        } else {
//                            progress_wheel.setVisibility(View.GONE);
//                            Toast.makeText(Login.this, "Sign In Failed : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

    public void login_task(final String email_String, final String password_String) {
        final JSONObject json = new JSONObject();
        try {
//              param.put("email", emailid); param.put("password", password);
            json.put("email", email_String);
            json.put("password", password_String);
//            json.put("device_type", "android");
//            json.put("lat", "0");
//            json.put("lon", "0");
//            json.put("firebase_id", SessionManager.get_firebaseId(prefs));
//            json.put("device_token", FirebaseInstanceId.getInstance().getToken());

            Log.e("", "login data   " + json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progress_wheel.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, Global.URL + "login.php", json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progress_wheel.setVisibility(View.GONE);
                        try {
                            Log.e("response   ", "" + response);
                            JSONObject jsonObject = response, jsonObject1;
//{"status":200,"message":"Logged in succefully","data":{"userId":"2","firstName":"kk","lastName":"","email":"pathakkrishna7161@gmail.com",
// "profileImage":""}}
                            if (jsonObject.getString("status").equalsIgnoreCase("200")) {

                                jsonObject1 = jsonObject.getJSONObject("data");

                                SessionManager.save_user_id(prefs, jsonObject1.getString("userId"));
                                SessionManager.save_fisrtName(prefs, jsonObject1.getString("firstName"));
                                SessionManager.save_lastName(prefs, jsonObject1.getString("lastName"));
                                SessionManager.save_name(prefs, jsonObject1.getString("firstName") + " " + jsonObject1.getString("lastName"));

                                SessionManager.save_emailid(prefs, jsonObject1.getString("email"));
                                SessionManager.save_image(prefs, jsonObject1.getString("profileImage"));
//                                SessionManager.save_address(prefs, jsonObject1.getString("address"));
//                                SessionManager.save_mobile(prefs, jsonObject1.getString("mobile"));
//                                SessionManager.save_zipcode(prefs, jsonObject1.getString("zipcode"));
//                                SessionManager.save_address(prefs, jsonObject1.getString("address"));
//                                SessionManager.save_firebaseid(prefs, jsonObject.getJSONObject("response").getString("firebaseId"));
                                SessionManager.save_password(prefs, password_String);
                                SessionManager.save_check_login(prefs, true);

//                                Global.msgDialog(Login.this, jsonObject.getString("message"));
                                Intent intent = new Intent(Login.this, HomeActivity.class);
                                startActivity(intent);
                                finish();

                            } else if (jsonObject.getString("status").equals("Fail")) {
                                Global.msgDialog(Login.this, jsonObject.getString("message"));
                            } else {
                                Global.msgDialog(Login.this, jsonObject.getString("message"));
//                                Global.msgDialog(Login.this, "Error in parsing data");
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
                Global.msgDialog(Login.this, "Unable To Get Response ");
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        queue.add(jsonObjectRequest);
    }

    public void gps_on() {
        //Alert dialog box is opened to ak user
        // to open the GPS or Location for better user
        //If it is not done or cancleld than application will be closed.

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Login.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Turn On Location Services ");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                        startActivityForResult(intent, 14);
                    }
                });
        alertDialog.setNegativeButton("Not Now",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                });
        alertDialog.show();
    }

    private void onFblogin() {
        callbackmanager = CallbackManager.Factory.create();
        // Set permissions
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
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
                                                personName = json.getString("first_name") + " " + json.getString("last_name");
                                                System.out.println("Login details = " + personid + " " + personemail + " " + personName);
                                                personPhotoUrl = "https://graph.facebook.com/" + personid + "/picture?type=large";
                                                type = "facebook";

                                                //After Successfull login from facebook this API will be called
                                                HitWebservice(personid, personName, personemail, type, personPhotoUrl);
//                                                createAccount(personemail, personemail);

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

//    private void createAccount(final String email, final String password) {
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            onAuthSuccess(task.getResult().getUser());
//                        } else {
//                            progress_wheel.setVisibility(View.GONE);
//                            if (task.getException().getMessage().equalsIgnoreCase("The email address is already in use by another account.")) {
////                                loginWithFBFirebase(email, password);
//                                HitWebservice(personid, personName, personemail, type, personPhotoUrl);
//                            } else {
//                                Log.d("task data", task.getException().toString());
//                                Log.d("task message", task.getException().getMessage());
//                                Toast.makeText(Login.this, "Firebase Sign Up Failed : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//
//                        }
//                    }
//                });
//    }

//    private void onAuthSuccess(FirebaseUser user) {
//        SessionManager.save_firebaseId(prefs, user.getUid());
//        writeNewUser(user.getUid(), user.getDisplayName(), user.getEmail());
//    }
//
//    private void writeNewUser(String userId, String sname, String semail) {
//        User user = new User(sname, semail, userId);
//        mDatabase.child("users").child(userId).setValue(user);
//
////        login_fb(personid, personPhotoUrl, personName, lastName, personemail);
//        HitWebservice(personid, personName, personemail, type, personPhotoUrl);
////        signup_task(Global.URL, name.getText().toString().trim(), email.getText().toString().trim(),
////                password.getText().toString().trim());
//    }

    private void signIn() {
//        Auth.GoogleSignInApi.signOut(mGoogleSignInClient);
        mGoogleSignInClient.signOut();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //After successfull login from facebook and google.
        // User's data is fetched here and
        //task related to that result is performed.
        //old
//        if (requestCode == SIGN_IN_CODE) {
//            request_code = requestCode;
//            if (resultCode != RESULT_OK) {
//                is_signInBtn_clicked = false;
//                progress_dialog.dismiss();
//            }
//            is_intent_inprogress = false;
//            if (!google_api_client.isConnecting()) {
//                google_api_client.connect();
//            }
//        }

        //NEW
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            handleSignInResult(task);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                // Signed in successfully, show authenticated UI.
                updateUI(account);
            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
                updateUI(null);
            }
        }
        try {
            callbackmanager.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {
            String name = account.getDisplayName();
            String imageurl = "";
            try {
                Uri uri = account.getPhotoUrl();
                if (uri != null)
                    imageurl = new URL(account.getPhotoUrl().toString()).toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            String email = account.getEmail();
            String id = account.getId();

            type = "google";
            personid = id;
            personemail = email;
            personName = name;
            personPhotoUrl = imageurl;

            System.out.println(" details = " + personid + " " + personemail + " " + personName);

            HitWebservice(id, name, email, type, imageurl);
//        mStatusTextView.setText(getString(R.string.signed_in_fmt, account.getDisplayName()));
//        findViewById(R.id.sign_in_button).setVisibility(View.GONE);
//        findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
//        mStatusTextView.setText(R.string.signed_out);
//        findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
//        findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
//            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "SignIn failed", Toast.LENGTH_SHORT).show();
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


            Log.e("Social Login", json.toString());

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
                                SessionManager.save_usertype(prefs, type);


                                Intent intent = new Intent(Login.this, HomeActivity.class);
                                startActivity(intent);
                                finish();

                            } else if (jsonObject.getString("status").equalsIgnoreCase("Failed")) {
                                Global.msgDialog(Login.this, jsonObject.getString("message"));
                            } else {
                                Global.msgDialog(Login.this, jsonObject.getString("message"));
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
                Global.msgDialog(Login.this, "Unable To Get Response ");
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

//    protected void onStart() {
//        //Once the application is started .Then
//        super.onStart();
//        google_api_client.connect();
//    }
//
//    protected void onStop() {
//        super.onStop();
//        if (google_api_client.isConnected()) {
//            google_api_client.disconnect();
//        }
//        accessTokenTracker.stopTracking();
//        profileTracker.stopTracking();
//    }
//
//    protected void onResume() {
//        super.onResume();
//        if (google_api_client.isConnected()) {
//            google_api_client.connect();
//        }
//    }

//    private void buidNewGoogleApiClient() {
//        google_api_client = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(Plus.API, Plus.PlusOptions.builder().build())
//                .addScope(Plus.SCOPE_PLUS_LOGIN)
//                .addScope(Plus.SCOPE_PLUS_PROFILE)
//                .build();
//    }

//    @Override
//    public void onConnectionFailed(ConnectionResult result) {
//        connection_result = result;
//        if (!result.hasResolution()) {
//            google_api_availability.getErrorDialog(this, result.getErrorCode(), request_code).show();
//            return;
//        }
//        if (!is_intent_inprogress) {
//            connection_result = result;
//            if (is_signInBtn_clicked) {
//                buidNewGoogleApiClient();
//                resolveSignInError();
//            }
//        }
//    }
//
//    @Override
//    public void onConnected(Bundle arg0) {
//        is_signInBtn_clicked = false;
//        // Get user's information and set it into the layout
//        getProfileInfo();
//        // Update the UI after signin
//    }
//
//    @Override
//    public void onConnectionSuspended(int arg0) {
//        google_api_client.connect();
//    }

//    private void getProfileInfo() {
//        try {
//            if (Plus.PeopleApi.getCurrentPerson(google_api_client) != null) {
//                Person currentPerson = Plus.PeopleApi.getCurrentPerson(google_api_client);
//                setPersonalInfo(currentPerson);
//
//            } else {
//                progress_dialog.dismiss();
////                Toast.makeText(getApplicationContext(), "No Personal info mention", Toast.LENGTH_LONG).show();
//            }
//        } catch (Exception e) {
//            progress_dialog.dismiss();
//            e.printStackTrace();
//        }
//
//    }

//    private void resolveSignInError() {
//        if (connection_result.hasResolution()) {
//            try {
//                is_intent_inprogress = true;
//                connection_result.startResolutionForResult(this, SIGN_IN_CODE);
//                Log.d("resolve error", "sign in error resolved");
//            } catch (IntentSender.SendIntentException e) {
//                is_intent_inprogress = false;
//                google_api_client.connect();
//            }
//        }
//    }

//    private void setPersonalInfo(Person currentPerson) {
//
//        // Google Plus Data will be there
//        personName = currentPerson.getDisplayName();
//        personPhotoUrl = currentPerson.getImage().getUrl();
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
////               public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        personemail = Plus.AccountApi.getAccountName(google_api_client);
//        personid = currentPerson.getId();
//        progress_dialog.dismiss();
//
//        type = "google";
//
//        System.out.println(" details = " + personid + " " + personemail + " " + personName);
//
//        HitWebservice(personid, personName, personemail, type, personPhotoUrl);
////        createAccount(personemail, personemail);
//
//        gPlusSignOut();
//    }

//    private void gPlusSignIn() {
//        try {
//            if (!google_api_client.isConnecting()) {
//                Log.d("user connected", "connected");
//                is_signInBtn_clicked = true;
//                progress_dialog.show();
//                resolveSignInError();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    private void gPlusSignOut() {
//        if (google_api_client.isConnected()) {
//            Plus.AccountApi.clearDefaultAccount(google_api_client);
//            google_api_client.disconnect();
//            google_api_client.connect();
//        }
//    }

//    @Override
//    public void onResult(People.LoadPeopleResult peopleData) {
//        if (peopleData.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
//            PersonBuffer personBuffer = peopleData.getPersonBuffer();
//            ArrayList<String> list = new ArrayList<String>();
//            ArrayList<String> img_list = new ArrayList<String>();
//            try {
//                int count = personBuffer.getCount();
//                for (int i = 0; i < count; i++) {
//                    list.add(personBuffer.get(i).getDisplayName());
//                    img_list.add(personBuffer.get(i).getImage().getUrl());
//                }
//            } finally {
//                personBuffer.release();
//            }
//        } else {
//            Log.e("circle error", "Error requesting visible circles: " + peopleData.getStatus());
//        }
//    }
}
