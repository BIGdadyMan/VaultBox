package com.vb.vaultbox;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
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
import com.vb.vaultbox.Utills.MyRatingBar;
import com.vb.vaultbox.Utills.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Manish Yadav on 6/27/2018.
 */

public class AddReviews extends AppCompatActivity {

    String cat_id;
    ProgressWheel progress_wheel;
    String string_review, string_rating = "0";
    TextView submit;
    EditText review;
//    RatingBar rating;
MyRatingBar rating;

    RequestQueue queue;
    SharedPreferences prefs;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_reviews);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.text_toolbar);
        mTitle.setText("Add Review");
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

        prefs = PreferenceManager.getDefaultSharedPreferences(AddReviews.this);
        queue = Volley.newRequestQueue(AddReviews.this);
        dialog = new ProgressDialog(AddReviews.this);
        dialog.setMessage("Loading...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            cat_id = bundle.getString("cat_id");
        }

        progress_wheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        submit = (TextView) findViewById(R.id.submit);
        review = (EditText) findViewById(R.id.review);
        rating = (MyRatingBar) findViewById(R.id.rating);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                string_name, string_price, string_description, string_location;
                string_review = review.getText().toString().trim();
                string_rating = String.valueOf(Math.round(rating.getRating()));
                if (!Global.validateName(string_review)) {
                    review.setError(Html.fromHtml("<font color='red'>Enter Review</font>"));
                    return;
                } else {
                    if (Global.isOnline(AddReviews.this)) {
                        submitReview();
                    } else {
                        Toast.makeText(AddReviews.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public void submitReview() {
        queue = Volley.newRequestQueue(AddReviews.this);
        final JSONObject json = new JSONObject();
        try {
//            userID,message,propId,rating
            json.put("userID", SessionManager.get_user_id(prefs));
            json.put("message", string_review);
            json.put("propId", cat_id);
            json.put("rating", string_rating);
            Log.e(" Input Data : ", " " + json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, Global.URL + "addReview.php", json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        try {
                            Log.e("resp addReview:", "" + response);
                            //{"status":200,"message":"Successfully Added"}
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                                Global.msgDialogFinish(AddReviews.this, jsonObject.getString("message"));
                            } else if (jsonObject.getString("status").equals("Fail")) {
                                Global.msgDialog(AddReviews.this, jsonObject.getString("message"));
                            } else {
                                Global.msgDialog(AddReviews.this, jsonObject.getString("message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dialog.dismiss();
                Global.msgDialog(AddReviews.this, "Unable To Get Response ");
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        queue.add(jsonObjectRequest);
    }


}
