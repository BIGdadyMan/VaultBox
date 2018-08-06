package com.vb.vaultbox;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vb.vaultbox.Utills.SessionManager;


public class Settings extends AppCompatActivity {


    SharedPreferences prefs;
    Toast toast;
    RelativeLayout edit_profile, change_password,notifications, feed, rate, recomend, about,logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_user);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView  mTitle = (TextView) toolbar.findViewById(R.id.text_toolbar);
        mTitle.setText(getResources().getString(R.string.setting));
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


        prefs = PreferenceManager.getDefaultSharedPreferences(Settings.this);

        edit_profile = (RelativeLayout)  findViewById(R.id.edit_profile);
        change_password = (RelativeLayout)  findViewById(R.id.change_password);
        notifications = (RelativeLayout)  findViewById(R.id.notifications);
        logout = (RelativeLayout)  findViewById(R.id.logout);
//        feed = (RelativeLayout)  findViewById(R.id.feed);
//        rate = (RelativeLayout)  findViewById(R.id.rate);
//        recomend = (RelativeLayout)  findViewById(R.id.recomend);
//        about = (RelativeLayout)  findViewById(R.id.about);

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, ChangePassword.class);
                startActivity(intent);
            }
        });
        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(Settings.this, BlockedUser.class);
//                startActivity(intent);
            }
        });
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, EditProfile.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Settings.this);
                alertDialog.setTitle("");
                alertDialog.setMessage("Do you really want to logout?");
                alertDialog.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
//                                logout(Global.BASEURLLOGIN + "SignOut/" + SessionManager.get_user_id(prefs));
                                SessionManager.dataclear(prefs);
                                SessionManager.save_check_login(prefs, false);
                                Intent intent = new Intent(Settings.this, Login.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });
                alertDialog.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                alertDialog.show();
            }
        });

//        feed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.setType("text/plain");
//                intent.putExtra(Intent.EXTRA_EMAIL, "");
//                intent.putExtra(Intent.EXTRA_SUBJECT, "FeedBack");
//                intent.putExtra(Intent.EXTRA_TEXT, "Hi, ");
//                startActivity(Intent.createChooser(intent, "Send Email"));
//            }
//        });
//
//        rate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Uri uri = Uri.parse("market://details?id=" + Settings.this.getPackageName());
//                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
//                // To count with Play market backstack, After pressing back button,
//                // to taken back to our application, we need to add following flags to intent.
//                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
//                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
//                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//                try {
//                    startActivity(goToMarket);
//                } catch (ActivityNotFoundException e) {
//                    startActivity(new Intent(Intent.ACTION_VIEW,
//                            Uri.parse("http://play.google.com/store/apps/details?id=" + Settings.this.getPackageName())));
//                }
//            }
//        });
//        recomend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    Intent i = new Intent(Intent.ACTION_SEND);
//                    i.setType("text/plain");
//                    i.putExtra(Intent.EXTRA_SUBJECT, Settings.this.getResources().getString(R.string.app_name));
//                    String sAux = "\nLet me recommend you this application\n\n";
//                    sAux = sAux + "https://play.google.com/store/apps/details?id=" + Settings.this.getPackageName();
//                    i.putExtra(Intent.EXTRA_TEXT, sAux);
//                    startActivity(Intent.createChooser(i, "Select one"));
//                } catch (Exception e) {
//                    //e.toString();
//                }
//
//            }
//        });
//        about.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                getFragmentManager().beginTransaction().replace(R.id.frame_container, new About()).addToBackStack(null).commit();
//            }
//        });

//        return view;
    }



}
