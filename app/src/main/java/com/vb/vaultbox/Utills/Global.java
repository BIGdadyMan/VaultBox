package com.vb.vaultbox.Utills;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.vb.vaultbox.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


/**
 * Created by user1 on 3/17/2016.
 */
public class Global {

//    public static String URL = "http://sparsematrixtechnology.com/vaultbox/mobileWebservice/";
    public static String URL = "http://sparsematrixtechnology.com/vaultbox/mobileWebservice/";

    public static String name = "";
    public static String emailId = "";
    public static String profile_image = "";
    public static String location = "";
    public static String term_condtion_accept = "";
    public static String phone = "";
    public static String UserType = "";
    public static String aboutus = "";
    public static String TotalAmount = "";
    public static String termCondtion = "";


    public static void showDialog(final Activity ac) {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(ac);
        alertbox.setTitle(ac.getResources().getString(R.string.app_name));
        alertbox.setMessage("No Internet Connection!");
        alertbox.setPositiveButton(ac.getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        ac.finish();
                    }
                });
        alertbox.show();
    }

    public static void msgDialog(final Activity ac, String msg) {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(ac);
        alertbox.setTitle(ac.getResources().getString(R.string.app_name));
        alertbox.setMessage(msg);
        alertbox.setPositiveButton(ac.getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
        alertbox.show();
    }

    public static void msgDialogFinish(final Activity ac, String msg) {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(ac);
        alertbox.setTitle(ac.getResources().getString(R.string.app_name));
        alertbox.setMessage(msg);
        alertbox.setPositiveButton(ac.getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        ac.finish();
                    }
                });
        alertbox.show();
    }

    public static void GlobalDialog(final Activity ac, String msg) {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(ac);
        alertbox.setTitle(ac.getResources().getString(R.string.app_name));
        alertbox.setMessage(msg);
        alertbox.setPositiveButton(ac.getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
        alertbox.show();

    }

    public static void centerToolbarTitle(final Toolbar toolbar) {
        final CharSequence title = toolbar.getTitle();
        final ArrayList<View> outViews = new ArrayList<>(1);
        toolbar.findViewsWithText(outViews, title, View.FIND_VIEWS_WITH_TEXT);
        if (!outViews.isEmpty()) {
            final TextView titleView = (TextView) outViews.get(0);
            titleView.setGravity(Gravity.CENTER);
            final Toolbar.LayoutParams layoutParams = (Toolbar.LayoutParams) titleView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            toolbar.requestLayout();
            //also you can use titleView for changing font: titleView.setTypeface(Typeface);
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting())
            return true;
        else
            return false;
    }

    public static boolean validateName(String firstrname) {
        if (firstrname.length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public final static boolean validateEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static void hideKeyboard(Activity act) {
        InputMethodManager inputMethodManager = (InputMethodManager) act.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(act.getCurrentFocus().getWindowToken(), 0);
    }

    public static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }
    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

}
