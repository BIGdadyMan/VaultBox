package com.vb.vaultbox.Utills;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vb.vaultbox.HomeActivity;
import com.vb.vaultbox.R;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by user1 on 3/23/2016.
 */
public class ProfilePic extends Activity {
    ImageView imageView1;
    FileBody imagefile;
    private static final int REQUEST_CAMERA = 1, SELECT_FILE = 2;
    private String selectedImagePath = "";
    TextView tviSkip, tvi_upload_profile;
    Button camera, btngalary;
    String screentype = "";
    MarshMallowPermission marshMallowPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_pic);

        if (!Global.isOnline(ProfilePic.this)) {
            Global.showDialog(ProfilePic.this);
            return;
        }
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorAccent));
        }

        marshMallowPermission = new MarshMallowPermission(this);

        imageView1 = (ImageView) findViewById(R.id.imageView1);
        tviSkip = (TextView) findViewById(R.id.tviSkip);
        camera = (Button) findViewById(R.id.camera);
        btngalary = (Button) findViewById(R.id.btngalary);
        tvi_upload_profile = (TextView) findViewById(R.id.tvi_upload_profile);

        Global.profile_image = SessionManager.get_image(PreferenceManager.getDefaultSharedPreferences(ProfilePic.this));
//        Global.UserType = SessionManager.get_usertype(PreferenceManager.getDefaultSharedPreferences(ProfilePic.this));

        if (Global.profile_image.length() > 0) {
            tvi_upload_profile.setText("Update Profile Picture");
        }
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            screentype = "";
        } else {
            screentype = extras.getString("screentype");
        }

        btngalary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!marshMallowPermission.checkPermissionForExternalStorage()) {
                    marshMallowPermission.requestPermissionForExternalStorage();
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                }
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!marshMallowPermission.checkPermissionForCamera()) {
                    marshMallowPermission.requestPermissionForCamera();
                } else {
                    if (!marshMallowPermission.checkPermissionForExternalStorage()) {
                        marshMallowPermission.requestPermissionForExternalStorage();
                    } else {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                        startActivityForResult(intent, REQUEST_CAMERA);
                    }
                }
            }
        });

        tviSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(ProfilePic.this, HomeActivity.class);
                in.putExtra("Key", screentype);
                startActivity(in);
                finish();
            }
        });

        if (Global.profile_image.length() > 1) {
            Glide.with(ProfilePic.this).load(Global.profile_image).into(imageView1);
//            aQuery.id(R.id.imageView1).image(Global.profile_image, true, true, 200, 0);//.image(Global.profile_image, true, true, 0, 0, null, AQuery.FADE_IN);
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();

        if (PreferenceManager.getDefaultSharedPreferences(getBaseContext()).contains("my_image")) {
            PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().remove("my_image").commit();
        }

        Intent in = new Intent(ProfilePic.this, HomeActivity.class);
        in.putExtra("Key", screentype);
        startActivity(in);
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bm;
                    BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                    btmapOptions.inSampleSize = 8;
                    bm = BitmapFactory.decodeFile(f.getAbsolutePath(), btmapOptions);
                    bm = Bitmap.createScaledBitmap(bm, 300, 200, true);
                    imageView1.setImageBitmap(bm);
                    String path = Environment.getExternalStorageDirectory() + File.separator + "Phoenix" + File.separator + "default";
                    PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("my_image", f.toString()).commit();
                    //f.delete();
                    OutputStream fOut = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        fOut = new FileOutputStream(file);
                        bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                        fOut.flush();
                        fOut.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String tempPath = getPath(selectedImageUri, ProfilePic.this);
                PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("my_image", tempPath).commit();
                Bitmap bm;
                BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                btmapOptions.inSampleSize = 8;
                bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
                bm = Bitmap.createScaledBitmap(bm, 300, 200, true);

                bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
                imageView1.setImageBitmap(bm);
            }
            new MyAsyncTask().execute();
        }
    }

    private String getPath(Uri uri, Activity activity) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    private class MyAsyncTask extends AsyncTask<Void, String, String> {
        private final ProgressDialog dialog = new ProgressDialog(ProfilePic.this);
        String response = "";
        StringBuilder s = new StringBuilder();

        @Override
        protected void onPreExecute() {
            // dialog.setMessage("loading...");
            dialog.setIndeterminate(false);
            dialog.show();
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.my_pb);
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                selectedImagePath = PreferenceManager.getDefaultSharedPreferences(ProfilePic.this).getString("my_image", "");
                imagefile = new FileBody(new File(selectedImagePath));

                URL url = new URL(Global.URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setDoInput(true);
                MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);


                entity.addPart("action", new StringBody("changeProfilePic"));
                entity.addPart("userId", new StringBody(PreferenceManager.getDefaultSharedPreferences(ProfilePic.this).getString("user_id", "0")));
//                entity.addPart("userType", new StringBody(getIntent().getStringExtra("user_type")));

                if (selectedImagePath.length() > 1) {
                    entity.addPart("profile_image", imagefile);
                }
                connection.addRequestProperty("Content-length", entity.getContentLength() + "");
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

            if (result != "" || result != null) {

                try {
                    JSONObject json = new JSONObject(result);
                    Log.e("response    ", "" + json);
                    if (json.has("status")) {
                        if (json.getString("status").equals("Success")) {
                            JSONObject response = json.getJSONObject("response");


                            SessionManager.save_image(PreferenceManager.getDefaultSharedPreferences(ProfilePic.this), response.getString("image"));
//                            Global.UserType = SessionManager.get_usertype(PreferenceManager.getDefaultSharedPreferences(ProfilePic.this));


                            Intent in = new Intent(ProfilePic.this, HomeActivity.class);
                            in.putExtra("Key", screentype);
                            startActivity(in);
                            finish();

                        }
                    } else {
                        Global.GlobalDialog(ProfilePic.this, json.getString("msg"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Global.GlobalDialog(ProfilePic.this, "Server is not responding");
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


}
