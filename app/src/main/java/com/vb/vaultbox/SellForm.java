package com.vb.vaultbox;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.suke.widget.SwitchButton;
import com.vb.vaultbox.Utills.Global;
import com.vb.vaultbox.Utills.SessionManager;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Manish Yadav on 6/27/2018.
 */

public class SellForm extends AppCompatActivity {


    String cat_id, subcat_id, superSubCatID;
    ArrayList<String> image_list;

    TextView location, submit, media;
    SharedPreferences prefs;
    ProgressWheel progress_wheel;
    String string_name, string_price, string_description, string_location, string_auction, loc_id;
    SwitchButton switch_button;
    EditText item_description, item_price, item_name;

    private static final int PICK_VIDEO = 441;
    Uri URI = null;
    Uri selectedImage, mCapturedImageURI, selectedVideo;
    Cursor cursor;
    int columnindex, i;
    String file_path, image_path = "", video_path = "";
    BitmapFactory.Options options;
    String selectedpath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sell_form);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.text_toolbar);
        mTitle.setText(Html.fromHtml("Add Basic Details <br> <small><small><font color=\"#7E3FAD\"> Highlighted</font> fields are required</small></small>"));
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

        prefs = PreferenceManager.getDefaultSharedPreferences(SellForm.this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            cat_id = bundle.getString("cat_id");
            subcat_id = bundle.getString("subcat_id");
            superSubCatID = bundle.getString("superSubCatID");
            image_list = bundle.getStringArrayList("image_list");
        }

        progress_wheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        location = (TextView) findViewById(R.id.lcn);
        media = (TextView) findViewById(R.id.mdia);
        submit = (TextView) findViewById(R.id.submit);
        item_description = (EditText) findViewById(R.id.item_description);
        item_price = (EditText) findViewById(R.id.item_price);
        item_name = (EditText) findViewById(R.id.item_name);
        switch_button = (SwitchButton) findViewById(R.id.switch_button);


        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SellForm.this, Location.class);
                startActivityForResult(i, 1);
            }
        });

        media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openvideo();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                string_name, string_price, string_description, string_location;

                string_location = location.getText().toString().trim();
                string_description = item_description.getText().toString().trim();
                string_name = item_name.getText().toString().trim();
                string_price = item_price.getText().toString().trim();
                if (switch_button.isChecked()) {
                    string_auction = "1";
                } else {
                    string_auction = "0";
                }

                if (!Global.validateName(string_name)) {
                    item_name.setError(Html.fromHtml("<font color='red'>Enter Name</font>"));
                    return;
                } else if (!Global.validateName(string_price)) {
                    item_price.setError(Html.fromHtml("<font color='red'>Enter Price</font>"));
                    return;
                } else if (!Global.validateName(string_location)) {
                    location.setError(Html.fromHtml("<font color='red'>Enter Location</font>"));
                    return;
                } else if (!Global.validateName(string_description)) {
                    item_description.setError(Html.fromHtml("<font color='red'>Enter Item Description</font>"));
                    return;
                } else {
                    if (Global.isOnline(SellForm.this)) {

                        if (video_path.equals("")) {
                            new updateimageTask().execute();
                        } else {
                            new updateVideoTask().execute();
                        }
                    } else {
                        Toast.makeText(SellForm.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
//        openvideo();
    }

    private void openvideo() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Video"),
                PICK_VIDEO);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String loc = data.getStringExtra("location");
                loc_id = data.getStringExtra("location_id");
                location.setText(loc);
            }
        } else if (requestCode == PICK_VIDEO) {

            try {
                selectedVideo = data.getData();
                String[] filePathColumn = {MediaStore.Video.Media.DATA};

                cursor = getContentResolver().query(selectedVideo,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                columnindex = cursor.getColumnIndex(filePathColumn[0]);
                file_path = cursor.getString(columnindex);
                // Log.e("Attachment Path:", attachmentFile);
                URI = Uri.parse("file://" + file_path);
                video_path = file_path;
                selectedpath = video_path;

                media.setText(file_path);
                cursor.close();

//                if (resultCode == 0) {
//                    dialog_attachment.dismiss();
//                } else {
//                    dialog_attachment.dismiss();
//                    System.out.println("cccccc    " + video_path);
//                    video_thumbnail = ThumbnailUtils.createVideoThumbnail(video_path, MediaStore.Video.Thumbnails.MINI_KIND);
//                    new updateimageTask().execute();
////                    new ImageUploadTask().execute(Global.BASEURLUSER + "UploadMediaFile", selectedpath, "", "", Post_type);
//                    new updateThumbTask().execute();
//                    image.setVisibility(View.VISIBLE);
//                    Glide.with(getActivity()).load(bitmapToByte(video_thumbnail)).asBitmap().into(image);
//
//
//                }

            } catch (Exception e) {
            }
        }
    }


    private class updateimageTask extends AsyncTask<Void, String, String> {
        private final ProgressDialog dialog = new ProgressDialog(SellForm.this);
        String response = "";
        StringBuilder s = new StringBuilder();

        @Override
        protected void onPreExecute() {
            // dialog.setMessage("loading...");
            dialog.setIndeterminate(false);
            dialog.show();
            dialog.setCancelable(false);
//            dialog.setContentView(R.layout.my_pb);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {

                URL url = new URL(Global.URL + "addListing.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setDoInput(true);
                MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

//                userID,title,description,catID,subCatID,superSubCatID,city,price,listImage0, listImage1,auction, countImage
//                string_name, string_price, string_description, string_location;

                entity.addPart("userID", new StringBody(SessionManager.get_user_id(prefs)));
                entity.addPart("title", new StringBody(string_name));
                entity.addPart("price", new StringBody(string_price));
                entity.addPart("city", new StringBody(string_location));
                entity.addPart("description", new StringBody(string_description));
                entity.addPart("catID", new StringBody(cat_id));
                entity.addPart("subCatID", new StringBody(subcat_id));
                if (superSubCatID == null || superSubCatID.equalsIgnoreCase("null")) {
                    entity.addPart("superSubCatID", new StringBody(""));
                } else {
                    entity.addPart("superSubCatID", new StringBody(superSubCatID));
                }
                entity.addPart("countImage", new StringBody(String.valueOf(image_list.size())));
                entity.addPart("auction", new StringBody(string_auction));
                for (int i = 0; i < image_list.size(); i++) {
                    if (image_list.get(i).trim().length() > 1) {
                        FileBody imagefile = new FileBody(new File(new URI(image_list.get(i))));
                        entity.addPart("listImage" + i, imagefile);
                    }
                }

//                connection.addRequestProperty("Content-length", entity.getContentLength() + "");
                connection.addRequestProperty(entity.getContentType().getName(), entity.getContentType().getValue());
                OutputStream os = connection.getOutputStream();
                entity.writeTo(connection.getOutputStream());
                os.close();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    s = new StringBuilder(readStream(connection.getInputStream()));
                    return s.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
                s = new StringBuilder(e.toString());
            }
            return s.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            this.dialog.dismiss();
            if (result != "" && result != null) {
                try {
                    Log.e("response    ", "" + result);
                    JSONObject json = new JSONObject(result);
                    Log.e("response    ", "" + json);
                    try {
//                        mProgressDialog.dismiss();
//                        {"status":200,"message":"Listing Added","data":{"ListingID":18}}
                        try {
                            JSONObject jsonObject2, jsonObject, jsonObject1 = new JSONObject(json.toString());
                            if (jsonObject1.getString("status").equals("200")) {

                                AlertDialog.Builder alertbox = new AlertDialog.Builder(SellForm.this);
                                alertbox.setTitle(SellForm.this.getResources().getString(R.string.app_name));
                                alertbox.setMessage(jsonObject1.getString("message"));
                                alertbox.setCancelable(false);
                                alertbox.setPositiveButton(SellForm.this.getResources().getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                Intent intent = new Intent(SellForm.this, HomeActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            }
                                        });
                                alertbox.show();

                            } else if (jsonObject1.getString("status").equals("Failed")) {
                                Global.msgDialog(SellForm.this, jsonObject1.getString("message"));
                            } else {
                                Global.msgDialog(SellForm.this, jsonObject1.getString("message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Global.GlobalDialog(SellForm.this, "Server is not responding");
            }
        }
    }


    private class updateVideoTask extends AsyncTask<Void, String, String> {
        private final ProgressDialog dialog = new ProgressDialog(SellForm.this);
        String response = "";
        StringBuilder s = new StringBuilder();

        @Override
        protected void onPreExecute() {
            // dialog.setMessage("loading...");
            dialog.setIndeterminate(false);
            dialog.show();
            dialog.setCancelable(false);
//            dialog.setContentView(R.layout.my_pb);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {

                URL url = new URL(Global.URL + "listingTest.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setDoInput(true);
                MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

//                userID,title,description,catID,subCatID,superSubCatID,city,price,listImage0, listImage1,auction, countImage
//                string_name, string_price, string_description, string_location;

                entity.addPart("userID", new StringBody(SessionManager.get_user_id(prefs)));
                entity.addPart("title", new StringBody(string_name));
                entity.addPart("price", new StringBody(string_price));
                entity.addPart("city", new StringBody(string_location));
                entity.addPart("description", new StringBody(string_description));
                entity.addPart("catID", new StringBody(cat_id));
                entity.addPart("subCatID", new StringBody(subcat_id));
                if (superSubCatID == null || superSubCatID.equalsIgnoreCase("null")) {
                    entity.addPart("superSubCatID", new StringBody(""));
                } else {
                    entity.addPart("superSubCatID", new StringBody(superSubCatID));
                }
                entity.addPart("countImage", new StringBody(String.valueOf(image_list.size())));
                entity.addPart("auction", new StringBody(string_auction));
                for (int i = 0; i < image_list.size(); i++) {
                    if (image_list.get(i).trim().length() > 1) {
                        FileBody imagefile = new FileBody(new File(new URI(image_list.get(i))));
                        entity.addPart("listImage" + i, imagefile);
                    }
                }
                if (video_path.length() > 1) {
                    FileBody videofile = new FileBody(new File(video_path));
                    entity.addPart("uploadVideo", videofile);
                }

//                connection.addRequestProperty("Content-length", entity.getContentLength() + "");
                connection.addRequestProperty(entity.getContentType().getName(), entity.getContentType().getValue());
                OutputStream os = connection.getOutputStream();
                entity.writeTo(connection.getOutputStream());
                os.close();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    s = new StringBuilder(readStream(connection.getInputStream()));
                    return s.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
                s = new StringBuilder(e.toString());
            }
            return s.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            this.dialog.dismiss();
            if (result != "" && result != null) {
                try {
                    Log.e("response    ", "" + result);
                    JSONObject json = new JSONObject(result);
                    Log.e("response    ", "" + json);
                    try {
//                        mProgressDialog.dismiss();
//                        {"status":200,"message":"Listing Added","data":{"ListingID":18}}
                        try {
                            JSONObject jsonObject2, jsonObject, jsonObject1 = new JSONObject(json.toString());
                            if (jsonObject1.getString("status").equals("200")) {

                                AlertDialog.Builder alertbox = new AlertDialog.Builder(SellForm.this);
                                alertbox.setTitle(SellForm.this.getResources().getString(R.string.app_name));
                                alertbox.setMessage(jsonObject1.getString("message"));
                                alertbox.setCancelable(false);
                                alertbox.setPositiveButton(SellForm.this.getResources().getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                Intent intent = new Intent(SellForm.this, HomeActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            }
                                        });
                                alertbox.show();

                            } else if (jsonObject1.getString("status").equals("Failed")) {
                                Global.msgDialog(SellForm.this, jsonObject1.getString("message"));
                            } else {
                                Global.msgDialog(SellForm.this, jsonObject1.getString("message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Global.GlobalDialog(SellForm.this, "Server is not responding");
            }
        }
    }


    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }

//    public void submitFormTask() {
//        RequestQueue queue = Volley.newRequestQueue(SellForm.this);
//        final JSONObject json = new JSONObject();
//        try {
////  userID,title,description,catID,subCatID,superSubCatID,city,price,listImage0, listImage1,auction, countImage
//            json.put("email", email_String);
//            json.put("password", password_String);
//            json.put("device_type", "android");
//            json.put("lat", "0");
//            json.put("lon", "0");
//            json.put("userID", SessionManager.get_firebaseId(prefs));
//            json.put("device_token", FirebaseInstanceId.getInstance().getToken());
//
//            Log.e("", "login data   " + json);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        progress_wheel.setVisibility(View.VISIBLE);
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
//                Request.Method.POST, Global.URL + "addListing.php", json,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.v("signup reponse", "" + response);
//                        progress_wheel.setVisibility(View.GONE);
//                        try {
//                            JSONObject jsonObject2, jsonObject = new JSONObject(response.toString()), jsonObject1;
//                            jsonObject1 = jsonObject.getJSONObject("meta");
//                            if (jsonObject1.getString("status").equals("Success")) {
//
//
//                                jsonObject2 = jsonObject.getJSONObject("data");
//                                //User getUserProfile data stored in local database
//                                SessionManager.save_emailid(prefs, jsonObject2.getString("email"));
//                                SessionManager.save_user_id(prefs, jsonObject2.getString("user_id"));
////                                SessionManager.save_phone(prefs, jsonObject2.getString("phone"));
//                                SessionManager.save_name(prefs, jsonObject2.getString("first_name"));
//                                SessionManager.save_image(prefs, jsonObject2.getString("image"));
////                                    SessionManager.save_userType(prefs, jsonObject2.getString("userType"));
////                                    SessionManager.save_price(prefs, jsonObject.getString("price"));
//                                SessionManager.save_check_login(prefs, true);
//                                SessionManager.save_password(prefs, password_String);
//
//                                SessionManager.save_user_school_id(prefs, jsonObject2.getString("school_id"));
//
//
//                                //On Successfull Login User will navigate to there dashboard screen,
//                                // from where he/she can request for ride
//                                Intent intent = new Intent(LogIn.this, MainActivity.class);
//                                startActivity(intent);
//                                finish();
////                                }
//                            } else if (jsonObject1.getString("status").equalsIgnoreCase("Failed")) {
//                                Global.msgDialog(LogIn.this, jsonObject1.getString("message"));
//                            } else {
//                                Global.msgDialog(LogIn.this, "Error in server");
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//                progress_wheel.setVisibility(View.GONE);
//                Global.msgDialog(LogIn.this, "Unable To Get Response ");
//            }
//        });
//        int socketTimeout = 30000;
//        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        jsonObjectRequest.setRetryPolicy(policy);
//        queue.add(jsonObjectRequest);
//    }


}
