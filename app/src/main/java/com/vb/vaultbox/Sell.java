package com.vb.vaultbox;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;

import com.asksira.bsimagepicker.BSImagePicker;

import java.util.ArrayList;
import java.util.List;

public class Sell extends AppCompatActivity implements BSImagePicker.OnSingleImageSelectedListener,
        BSImagePicker.OnMultiImageSelectedListener {
    BSImagePicker pickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sell);

//        Toolbar toolbar=(Toolbar) findViewById(R.id.appBar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final DisplayMetrics metrics = getResources().getDisplayMetrics();
//        findViewById(R.id.tv_single_selection).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                BSImagePicker pickerDialog = new BSImagePicker.Builder("com.asksira.imagepickersheetdemo.fileprovider")
//                        .build();
//                pickerDialog.show(getSupportFragmentManager(), "picker");
//            }
//        });
        findViewById(R.id.tv_multi_selection).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickerDialog = new BSImagePicker.Builder("com.asksira.imagepickersheetdemo.fileprovider")
                        .setMaximumDisplayingImages(Integer.MAX_VALUE)
                        .isMultiSelect()
                        .setMinimumMultiSelectCount(1)
                        .setMaximumMultiSelectCount(10)
                        .setPeekHeight(metrics.heightPixels)
                        .hideCameraTile()
                        .build();
                pickerDialog.setCancelable(false);
                pickerDialog.show(getSupportFragmentManager(), "picker");
            }
        });
        pickerDialog = new BSImagePicker.Builder("com.asksira.imagepickersheetdemo.fileprovider")
                .setMaximumDisplayingImages(Integer.MAX_VALUE)
                .isMultiSelect()
                .setMinimumMultiSelectCount(1)
                .setMaximumMultiSelectCount(10)
                .setPeekHeight(metrics.heightPixels)
                .hideCameraTile()
                .build();
        pickerDialog.setCancelable(false);
        pickerDialog.show(getSupportFragmentManager(), "picker");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (pickerDialog.isVisible())
            pickerDialog.dismiss();
        this.finish();
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
        Intent intent = new Intent(Sell.this, Category_List.class);
        intent.putStringArrayListExtra("images", images);
        startActivity(intent);
    }
}
