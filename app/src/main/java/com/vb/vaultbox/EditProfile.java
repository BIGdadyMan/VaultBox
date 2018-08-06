package com.vb.vaultbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.vb.vaultbox.Utills.Global;
import com.vb.vaultbox.Utills.ImageFilePath;
import com.vb.vaultbox.Utills.SessionManager;
import com.vb.vaultbox.Utills.Utility;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;


public class EditProfile extends AppCompatActivity {
    Button update;
    TextView counted;
    TextView mTitle;
    String gender_str, user_name_str, first_name_str, last_name_str, bio_str, email_str, phone_str, dob_str;
    EditText user_name;
    EditText first_name;
    EditText last_name;
    EditText bio;
    EditText email;
    EditText mobile;
    static EditText dob;
    CircleImageView u_image;
    Spinner genderspinner;

    RequestQueue queue;
    SharedPreferences prefs;
    ProgressWheel progress_wheel;

    ProgressDialog dialog;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;
    String selectedpath = "", image_str = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.text_toolbar);
        mTitle.setText(getResources().getString(R.string.edit_profile).toUpperCase());
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

        dialog = new ProgressDialog(EditProfile.this);
        dialog.setMessage("Loading...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);


        prefs = PreferenceManager.getDefaultSharedPreferences(EditProfile.this);
        queue = Volley.newRequestQueue(EditProfile.this);
        progress_wheel = (ProgressWheel) findViewById(R.id.progress_wheel);


//        user_name, first_name, last_name, bio, email, mobile, dob
        user_name = (EditText) findViewById(R.id.user_name);
        first_name = (EditText) findViewById(R.id.first_name);
        last_name = (EditText) findViewById(R.id.last_name);
        bio = (EditText) findViewById(R.id.bio);
        email = (EditText) findViewById(R.id.email);
        mobile = (EditText) findViewById(R.id.mobile);
        dob = (EditText) findViewById(R.id.dob);
        genderspinner = (Spinner) findViewById(R.id.gender);
        u_image = (CircleImageView) findViewById(R.id.u_image);
        update = (Button) findViewById(R.id.update);
        counted = (TextView) findViewById(R.id.count);

        getprofile();

        genderspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    gender_str = genderspinner.getSelectedItem().toString();
                } else {
                    gender_str = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        final TextWatcher mTextEditorWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //This sets a textview to the current length
//                mTextView.setText(String.valueOf(s.length()));
                counted.setText(s.length() + "/250");
            }

            public void afterTextChanged(Editable s) {
            }
        };
        bio.addTextChangedListener(mTextEditorWatcher);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMethod();
            }
        });
        u_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

    }

    public void getprofile() {
        RequestQueue queue = Volley.newRequestQueue(EditProfile.this);
        final JSONObject json = new JSONObject();
        try {
            json.put("userID", SessionManager.get_user_id(prefs));
            Log.e(" Input Data :   ", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, Global.URL + "profile.php", json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        try {
                            Log.e("response   ", "" + response);
                            JSONObject jsonObject = response, jsonObject1;
                            JSONArray jsonArray;
                            JSONObject object;
//"status":200,"message":"Success","data":{"userId":"22","Email":"pathakkrishna7161@gmail.com","UserName":"pathakkrishna7161","FirstName":"Krishan","LastName":"Pathak",
// "Mobile":"","Bio":null,"ProfileImage":"","Birthday":null,"Gender":"Male","followers":0,"following":0}}                          if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                                object = jsonObject.getJSONObject("data");

                                SessionManager.save_emailid(prefs, object.getString("Email"));
                                SessionManager.save_user_id(prefs, object.getString("userId"));
                                SessionManager.save_name(prefs, object.getString("UserName"));
                                SessionManager.save_fisrtName(prefs, object.getString("FirstName"));
                                SessionManager.save_lastName(prefs, object.getString("LastName"));
                                SessionManager.save_mobile(prefs, object.getString("Mobile"));
                                SessionManager.save_image(prefs, object.getString("ProfileImage"));
                                SessionManager.save_dob(prefs, object.getString("Birthday"));
                                SessionManager.save_gender(prefs, object.getString("Gender"));
                                SessionManager.save_followers(prefs, object.getString("followers"));
                                SessionManager.save_following(prefs, object.getString("following"));
                                SessionManager.save_Bio(prefs, object.getString("Bio"));
                                setUpData();

                            } else if (jsonObject.getString("status").equals("Fail")) {
                                Global.msgDialog(EditProfile.this, jsonObject.getString("message"));
                            } else {
                                Global.msgDialog(EditProfile.this, jsonObject.getString("message"));
//                                Global.msgDialog(Login. EditProfile.this, "Error in parsing data");
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
                Global.msgDialog(EditProfile.this, "Unable To Get Response ");
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        queue.add(jsonObjectRequest);
    }


    public void setUpData() {
        try {

            user_name.setText(SessionManager.get_name(prefs));
            first_name.setText(SessionManager.get_fisrtName(prefs));
            last_name.setText(SessionManager.get_lastName(prefs));
            bio.setText(SessionManager.get_Bio(prefs));
            email.setText(SessionManager.get_emailid(prefs));
            mobile.setText(SessionManager.get_mobile(prefs));
            dob.setText(SessionManager.get_dob(prefs));
            genderspinner.setSelection(getIndex(genderspinner, SessionManager.get_gender(prefs)));

            Glide.with(EditProfile.this).load(SessionManager.get_image(prefs))
                    .apply(RequestOptions.placeholderOf(R.drawable.placeholder_user).dontAnimate()
                            .error(R.drawable.placeholder_user).dontAnimate()).into(u_image);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }
        return 0;
    }

    public void changeMethod() {
        first_name_str = first_name.getText().toString().trim();
        last_name_str = last_name.getText().toString().trim();
        phone_str = mobile.getText().toString().trim();
        email_str = email.getText().toString().trim();

        if (!Global.validateName(user_name.getText().toString().trim())) {
            user_name.setError(Html.fromHtml("<font color='red'>Please enter User name</font>"));
            return;
        } else if (!Global.validateName(first_name_str)) {
            first_name.setError(Html.fromHtml("<font color='red'>Please enter first name</font>"));
            return;
        } else if (!Global.validateName(last_name_str)) {
            last_name.setError(Html.fromHtml("<font color='red'>Please enter last name</font>"));
            return;
        } else if (!Global.validateEmail(email_str)) {
            email.setError(Html.fromHtml("<font color='red'>Please enter Valid Email</font>"));
            return;
        } else if (!Global.validateName(phone_str)) {
            mobile.setError(Html.fromHtml("<font color='red'>Enter correct phone no</font>"));
            return;
        } else if (!Global.validateName(dob.getText().toString())) {
            dob.setError(Html.fromHtml("<font color='red'>Select Date of Birth</font>"));
            return;
        } else {
            if (Global.isOnline(EditProfile.this)) {
//                progress_wheel.setVisibility(View.VISIBLE);
//                update_Profile();
                new updateProfileTask().execute();
            } else {
                Global.showDialog(EditProfile.this);
            }
        }
    }

    public void update_Profile() {
        final JSONObject json = new JSONObject();
        try {
//  "userID = 22,email=pathakkrishna7161@gmail.com,mobile=123245455,FirstName=Krishan,LastName=Pathak,Profileimg=abcd.jpg
//            UserName=pathakkrishna7161,gender=Male,birthday=27/07/1992,bio=No Refund"
            json.put("userID", SessionManager.get_user_id(prefs));
            json.put("email", email.getText().toString());
            json.put("mobile", mobile.getText().toString());
            json.put("FirstName", first_name.getText().toString());
            json.put("LastName", last_name.getText().toString());
            json.put("UserName", user_name.getText().toString());
            json.put("gender", genderspinner.getSelectedItem().toString());
            json.put("birthday", dob.getText().toString());
            json.put("bio", bio.getText().toString());

            Log.e("", "signup data   " + json);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        progress_wheel.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, Global.URL + "UpdateUserDetail", json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("signup reponse", "" + response);
                        progress_wheel.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject, jsonObject1 = new JSONObject(response.toString());
                            jsonObject = jsonObject1.getJSONObject("meta");
                            if (jsonObject.getString("status").equals("Success")) {

                                SessionManager.save_fisrtName(prefs, first_name_str);
                                SessionManager.save_lastName(prefs, last_name_str);
                                SessionManager.save_mobile(prefs, phone_str);
                                Global.msgDialog(EditProfile.this, jsonObject.getString("message"));

                            } else if (jsonObject.getString("status").equals("Failed")) {
                                Global.msgDialog(EditProfile.this, jsonObject.getString("message"));
                            } else {
                                Global.msgDialog(EditProfile.this, "Error in server");
                                Global.msgDialog(EditProfile.this, jsonObject.getString("message"));
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
                Global.msgDialog(EditProfile.this, "Server Not Responding");
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        queue.add(jsonObjectRequest);
    }

    private class updateProfileTask extends AsyncTask<Void, String, String> {
        private final ProgressDialog dialog = new ProgressDialog(EditProfile.this);
        String response = "";
        StringBuilder s = new StringBuilder();

        @Override
        protected void onPreExecute() {
            dialog.setIndeterminate(false);
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {

                URL url = new URL(Global.URL + "edit.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setDoInput(true);
                MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

//                    json.put("userID", SessionManager.get_user_id(prefs));
//                json.put("email", email.getText().toString());
//                json.put("mobile", mobile.getText().toString());
//                json.put("FirstName", first_name.getText().toString());
//                json.put("LastName", last_name.getText().toString());
//                json.put("UserName", user_name.getText().toString());
//                json.put("gender", genderspinner.getSelectedItem().toString());
//                json.put("birthday", dob.getText().toString());
//                json.put("bio", bio.getText().toString());

                entity.addPart("userID", new StringBody(SessionManager.get_user_id(prefs)));
                entity.addPart("email", new StringBody(email.getText().toString()));
                entity.addPart("mobile", new StringBody(mobile.getText().toString()));
                entity.addPart("FirstName", new StringBody(first_name.getText().toString()));
                entity.addPart("LastName", new StringBody(last_name.getText().toString()));
                entity.addPart("UserName", new StringBody(user_name.getText().toString()));
                entity.addPart("gender", new StringBody(genderspinner.getSelectedItem().toString()));
                entity.addPart("birthday", new StringBody(dob.getText().toString()));
                if (bio.getText().toString().equalsIgnoreCase("null")) {
                    entity.addPart("bio", new StringBody(""));
                } else {
                    entity.addPart("bio", new StringBody(bio.getText().toString()));
                }
//                for (int i = 0; i < image_list.size(); i++) {
                if (selectedpath.length() > 1) {
                    FileBody imagefile = new FileBody(new File(selectedpath));
                    entity.addPart("Profileimg", imagefile);
                }
//                }

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

                                AlertDialog.Builder alertbox = new AlertDialog.Builder(EditProfile.this);
                                alertbox.setTitle(EditProfile.this.getResources().getString(R.string.app_name));
                                alertbox.setMessage(jsonObject1.getString("message"));
                                alertbox.setCancelable(false);
                                alertbox.setPositiveButton(EditProfile.this.getResources().getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                finish();
                                            }
                                        });
                                alertbox.show();

                            } else if (jsonObject1.getString("status").equals("Failed")) {
                                Global.msgDialog(EditProfile.this, jsonObject1.getString("message"));
                            } else {
                                Global.msgDialog(EditProfile.this, jsonObject1.getString("message"));
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
                Global.GlobalDialog(EditProfile.this, "Server is not responding");
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

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(EditProfile.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {

        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
        selectedpath = String.valueOf(destination);
        Glide.with(EditProfile.this).load(selectedpath)
                .into(u_image);
//                .apply(RequestOptions.placeholderOf(R.drawable.placeholder_user).dontAnimate())

//        updateimage();
//        new updateimageTask().execute();
//        new uploadMedia().execute();

        // thumbnail=rotateImage(thumbnail, selectedImagePath);
        try {
            thumbnail = Global.rotateImageIfRequired(EditProfile.this, thumbnail, Uri.parse(selectedpath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        u_image.setImageBitmap(thumbnail);

    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            // Get selected gallery image
            Uri selectedPicture = data.getData();
            // Get and resize getUserProfile image
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = EditProfile.this.getContentResolver().query(selectedPicture, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap loadedBitmap = BitmapFactory.decodeFile(picturePath);

            ExifInterface exif = null;
            try {
                File pictureFile = new File(picturePath);
                exif = new ExifInterface(pictureFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }

            int orientation = ExifInterface.ORIENTATION_NORMAL;

            if (exif != null)
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    loadedBitmap = rotateBitmap(loadedBitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    loadedBitmap = rotateBitmap(loadedBitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    loadedBitmap = rotateBitmap(loadedBitmap, 270);
                    break;
            }
            selectedpath = ImageFilePath.getPath(getBaseContext(), selectedPicture);
            Glide.with(EditProfile.this).load(selectedpath)
                    .into(u_image);
//            updateimage();
//            new updateimageTask().execute();
//            new uploadMedia().execute();
//            u_image.setImageBitmap(loadedBitmap);

        }
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            dialog.getDatePicker().setMaxDate(c.getTimeInMillis());
            return dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            month = month + 1;
            dob.setText((day + "/" + (month) + "/" + year));
        }
    }

}
