package com.vb.vaultbox;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
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
import com.github.chrisbanes.photoview.PhotoView;
import com.vb.vaultbox.Utills.Global;
import com.vb.vaultbox.Utills.MyRatingBar;
import com.vb.vaultbox.Utills.ResizableCustomView;
import com.vb.vaultbox.adapters.ReviewAdapter;
import com.vb.vaultbox.models.ReviewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.vb.vaultbox.Utills.DateTimeUtils.returnTime;

/**
 * Created by Pradeep on 7/18/2018.
 */

public class ListingDetails extends AppCompatActivity {

    Context context;
    ImageView img_next, img_prev;
    ViewPager viewpager;
    PageListener pageListener;
    static int currentPage;
    String listingID;
    ArrayList<String> Images;
    ReviewAdapter reviewAdapter;
    ArrayList<ReviewModel> review_ArrayList;
    RecyclerView review_list;
    TextView addReviews, txt, title, time, price, notfound;
    TextView details;
    //    RatingBar rating;
    MyRatingBar rating;

    RequestQueue queue;
    SharedPreferences prefs;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.text_toolbar);
        mTitle.setText(getResources().getString(R.string.details));
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

        prefs = PreferenceManager.getDefaultSharedPreferences(ListingDetails.this);
        queue = Volley.newRequestQueue(ListingDetails.this);
        dialog = new ProgressDialog(ListingDetails.this);
        dialog.setMessage("Loading...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            listingID = bundle.getString("listingID");
        }
        getIds();


//        img_next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                viewpager.setCurrentItem(getItem(+1), true);
//            }
//        });
//
//        img_prev.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                viewpager.setCurrentItem(getItem(-1), true);
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDetails();
        getRatings();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.select_menu, menu);
        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_report:

                break;
            case R.id.action_share:
                String shareBody ="Found this cool deal! Check it out.\n" +
                        title.getText().toString() + "\n" +
                        "MRP : S$"+price.getText().toString()+"\n" +
                        "Description : "+details.getText().toString();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "VaultBox");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
                break;
            default:
                super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }


    private void getIds() {

//        img_next = (ImageView) findViewById(R.id.next_img);
//        img_prev = (ImageView) findViewById(R.id.back_img);
//        notfound = (TextView) findViewById(R.id.notfound);
//        txt = (TextView) findViewById(R.id.txt);
//        txt_imagecounter = (TextView) findViewById(R.id.txt_imagecounter);
        context = ListingDetails.this;
        viewpager = (ViewPager) findViewById(R.id.pager);
        viewpager = (ViewPager) findViewById(R.id.pager);
        review_list = (RecyclerView) findViewById(R.id.review_list);
        addReviews = (TextView) findViewById(R.id.add_reviews);
        txt = (TextView) findViewById(R.id.txt);
        title = (TextView) findViewById(R.id.title);
        time = (TextView) findViewById(R.id.time);
        price = (TextView) findViewById(R.id.price);
        details = (TextView) findViewById(R.id.details);
        rating = (MyRatingBar) findViewById(R.id.rating);
        notfound = (TextView) findViewById(R.id.notfound);

//        rating.setEnabled(false);
//        rating.setProgress(null);
//        rating.setProgressed(null);

        pageListener = new PageListener();
        viewpager.setOnPageChangeListener(pageListener);


        addReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListingDetails.this, AddReviews.class);
                intent.putExtra("cat_id", listingID);
                startActivity(intent);
            }
        });

    }

    public void getDetails() {
        queue = Volley.newRequestQueue(ListingDetails.this);
        final JSONObject json = new JSONObject();
        try {
            json.put("listingID", listingID);
            Log.e("", " Input Data :   " + json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, Global.URL + "listingDetail.php", json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        try {
                            Log.e("resp listingDetail:", "" + response);
//{"status":200,"message":"Success","data":{"ListingID":"42","Title":"zggs","Description":"Shhs",
// "Image":[{"ListImage0":"http:\/\/media.sparsematrixtechnology.com\/images\/listing\/755961287fileName"}],
// "Category_ID":"1","Category_Name":"Hotel & Travel","Price":"57545","Auction":"1","Seller_ID":"41","Seller_Name":"Himanshu Tuteja",
// "City":"Noida","Add_Date":"2018-06-27 09:36:44","Total_Likes":0,"Rating":0}}
                            JSONObject jsonObject = response, jsonObject1;
                            JSONArray jsonArray;
                            JSONObject object;
                            if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                                jsonObject1 = jsonObject.getJSONObject("data");

                                title.setText(jsonObject1.getString("Title"));
                                time.setText(returnTime(ListingDetails.this, jsonObject1.getString("Add_Date")));
                                price.setText(jsonObject1.getString("Price"));
                                details.setText(jsonObject1.getString("Description"));
                                if (jsonObject1.getString("Description").length() > 50)
                                    ResizableCustomView.doResizeTextView(details, 2, "View More", true);
                                try {
                                    Float r = Float.valueOf(jsonObject1.getString("Rating"));
                                    rating.setRating(r);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    rating.setRating(0);
                                }

                                jsonArray = jsonObject1.getJSONArray("Image");
                                txt.setText((jsonArray.length() + " Photos"));
                                Images = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    object = jsonArray.getJSONObject(i);
                                    Images.add(object.getString("ListImage" + i));
                                }
                                ImagePagerAdapter pageradapter = new ImagePagerAdapter(context, 0, Images);
                                viewpager.setAdapter(pageradapter);
                                // Show images following the position
                                viewpager.setCurrentItem(0);

                            } else if (jsonObject.getString("status").equals("Fail")) {
                                Global.msgDialog(ListingDetails.this, jsonObject.getString("message"));
                            } else {
                                Global.msgDialog(ListingDetails.this, jsonObject.getString("message"));
//                                Global.msgDialog(Login. ListingDetails.this, "Error in parsing data");
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
                Global.msgDialog(ListingDetails.this, "Unable To Get Response ");
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        queue.add(jsonObjectRequest);
    }

    public void getRatings() {
//        RequestQueue queue = Volley.newRequestQueue(ListingDetails.this);
        queue = Volley.newRequestQueue(ListingDetails.this);
        final JSONObject json = new JSONObject();
        try {
            json.put("propId", listingID);
            Log.e("", " Input Data :   " + json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, Global.URL + "getReview.php", json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        try {
                            Log.e("response getReview :", "" + response);
//{"status":200,"message":"Success","data":[{"userName":"pathak","userImage":"http:\/\/media.sparsematrixtechnology.com\/images\/user\/IMG_20180716_100150604.jpg",
// "Rating":"4","Review":"hello"}]}
                            JSONObject jsonObject = response, jsonObject1;
                            JSONArray jsonArray;
                            JSONObject object;
                            review_ArrayList = new ArrayList<>();
                            review_ArrayList.clear();
                            if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                                jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    object = jsonArray.getJSONObject(i);
                                    ReviewModel model = new ReviewModel();
                                    model.setUserName(object.getString("userName"));
                                    model.setUserImage(object.getString("userImage"));
                                    model.setRating(object.getString("Rating"));
                                    model.setReview(object.getString("Review"));
                                    review_ArrayList.add(model);
                                }

                                reviewAdapter = new ReviewAdapter(review_ArrayList, ListingDetails.this);
                                review_list.setAdapter(reviewAdapter);

                                if (review_ArrayList.size() == 0)
                                    notfound.setVisibility(View.VISIBLE);
                                else
                                    notfound.setVisibility(View.GONE);

                            } else if (jsonObject.getString("status").equals("100")) {
//                                Global.msgDialog(ListingDetails.this, jsonObject.getString("message"));
                                Log.e("onResponse: ", jsonObject.getString("message"));
                                if (review_ArrayList.size() == 0)
                                    notfound.setVisibility(View.VISIBLE);
                                else
                                    notfound.setVisibility(View.GONE);
                            } else {
                                Global.msgDialog(ListingDetails.this, jsonObject.getString("message"));
//                                Global.msgDialog(Login. ListingDetails.this, "Error in parsing data");
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
                Global.msgDialog(ListingDetails.this, "Unable To Get Response ");
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        queue.add(jsonObjectRequest);
    }

    private int getItem(int i) {
        return viewpager.getCurrentItem() + i;
    }

    private class PageListener extends ViewPager.SimpleOnPageChangeListener {
        public void onPageSelected(int position) {
            currentPage = position + 1;
//            txt_imagecounter.setText("" + currentPage + "/" + totalCount);
        }
    }

    public class ImagePagerAdapter extends PagerAdapter {
        int image_position;
        ImageView img_pager;
        TextView names;
        Context mContext;
        ArrayList<String> pager_image;
        LayoutInflater inflater;

        public ImagePagerAdapter(Context context, int image_position1, ArrayList<String> mThumbIds) {
            mContext = context;
            image_position = image_position1;
            pager_image = mThumbIds;
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = inflater.inflate(R.layout.full_image, container, false);

            img_pager = (ImageView) itemView.findViewById(R.id.full_image_view);
//            names = (TextView) itemView.findViewById(R.id.names);
//            names.setText(pager_image.get(position).getImagename());
            try {
                Glide.with(mContext).load(pager_image.get(position))
                        .apply(RequestOptions.placeholderOf(R.drawable.loading).dontAnimate()
                                .error(R.drawable.banner).dontAnimate())
                        .into(img_pager);
            } catch (Exception e) {
                e.printStackTrace();
            }

            img_pager.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(mContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.profiledialog);
                    dialog.setCancelable(true);
                    PhotoView img = (PhotoView) dialog.findViewById(R.id.image_view);
                    ImageView del = (ImageView) dialog.findViewById(R.id.del);
                    img.setScale(3);
                    Glide.with(mContext).load(pager_image.get(position))
                            .apply(RequestOptions.placeholderOf(R.drawable.loading).dontAnimate()
                                    .error(R.drawable.banner).dontAnimate())
                            .into(img);
                    del.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });

            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(img_pager);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return pager_image.size();
        }

    }

}
