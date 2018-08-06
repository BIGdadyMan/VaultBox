package com.vb.vaultbox;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;

import com.asksira.bsimagepicker.BSImagePicker;
import com.vb.vaultbox.fragments.Profile;
import com.vb.vaultbox.fragments.Search;

import java.util.ArrayList;
import java.util.List;

//import com.zhihu.matisse.Matisse;
//import com.zhihu.matisse.MimeType;
//import com.zhihu.matisse.engine.impl.GlideEngine;

public class HomeActivity extends AppCompatActivity implements BSImagePicker.OnSingleImageSelectedListener,
        BSImagePicker.OnMultiImageSelectedListener {
    BSImagePicker pickerDialog;


    LinearLayout sell, profile, activity, groups, search;
    FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        manager = getSupportFragmentManager();


        sell = (LinearLayout) findViewById(R.id.sell);
        profile = (LinearLayout) findViewById(R.id.profile);
        activity = (LinearLayout) findViewById(R.id.activity);
        groups = (LinearLayout) findViewById(R.id.groups);
        search = (LinearLayout) findViewById(R.id.search);

        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(HomeActivity.this, Sell.class);
//                startActivity(intent);
                final DisplayMetrics metrics = getResources().getDisplayMetrics();
                pickerDialog = new BSImagePicker.Builder("com.asksira.imagepickersheetdemo.fileprovider")
                        .setMaximumDisplayingImages(Integer.MAX_VALUE)
                        .isMultiSelect()
                        .setMinimumMultiSelectCount(1)
                        .setMaximumMultiSelectCount(10)
                        .setPeekHeight(metrics.heightPixels)
                        .hideCameraTile()
                        .build();
//                pickerDialog.setCancelable(false);
                pickerDialog.show(getSupportFragmentManager(), "picker");
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.beginTransaction().replace(R.id.frame_container, new Profile()).addToBackStack(null).commit();
            }
        });

        activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                manager.beginTransaction().replace(R.id.frame_container, new Profile()).addToBackStack(null).commit();
            }
        });

        groups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                manager.beginTransaction().replace(R.id.frame_container, new Profile()).addToBackStack(null).commit();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.beginTransaction().replace(R.id.frame_container, new Search()).addToBackStack(null).commit();
            }
        });

        manager.beginTransaction().replace(R.id.frame_container, new Search()).commit();
    }

    @Override
    public void onSingleImageSelected(Uri uri) {
//        Glide.with(Sell.this).load(uri).into(ivImage2);
    }

    @Override
    public void onMultiImageSelected(List<Uri> uriList) {
        ArrayList<String> images = new ArrayList<>();
        for (int i = 0; i < uriList.size(); i++) {
            images.add(uriList.get(i).toString());
        }
        Intent intent = new Intent(HomeActivity.this, Category_List.class);
        intent.putStringArrayListExtra("images", images);
        startActivity(intent);
    }

}
