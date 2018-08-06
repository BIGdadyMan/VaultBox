package com.vb.vaultbox;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.vb.vaultbox.Utills.DateTimeUtils;
import com.vb.vaultbox.Utills.Global;
import com.vb.vaultbox.adapters.CategoryAdapter;
import com.vb.vaultbox.adapters.FeaturedListingAdapter;
import com.vb.vaultbox.models.CategoryModel;
import com.vb.vaultbox.models.ListingModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import in.srain.cube.views.GridViewWithHeaderAndFooter;

/**
 * Created by Pradeep on 7/18/2018.
 */

public class CategoryDetails extends AppCompatActivity {

    Context context;
    String CategoryID, CategoryName = "";
    RecyclerView cat_list, Featuredlist;
    GalleryAdpater adpater;
    CategoryAdapter categoryAdapter;
    FeaturedListingAdapter Featuredadpater;
    ArrayList<CategoryModel> categorylist;
    ArrayList<ListingModel> FeaturedarrayList, FreshList;
    GridViewWithHeaderAndFooter main_items;
    ProgressWheel progress_wheel;
    int page = 0;
    private int preLast;

    RequestQueue queue;
    SharedPreferences prefs;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_detalis);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.text_toolbar);

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

        prefs = PreferenceManager.getDefaultSharedPreferences(CategoryDetails.this);
        queue = Volley.newRequestQueue(CategoryDetails.this);
        dialog = new ProgressDialog(CategoryDetails.this);
        dialog.setMessage("Loading...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            CategoryID = bundle.getString("CategoryID");
            CategoryName = bundle.getString("CategoryName");
            mTitle.setText(CategoryName);
        }

        getIds();

        main_items.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final int lastItem = firstVisibleItem + visibleItemCount;
                if (lastItem == totalItemCount) {
                    if (preLast != lastItem) {
                        //to avoid multiple calls for last item
                        Log.d("Last", "Last");
                        preLast = lastItem;
                        page++;
                        getCategoryListing(page);
                    }
                }
            }
        });


        main_items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(CategoryDetails.this, ListingDetails.class);
                intent.putExtra("listingID", FreshList.get(position).getListingID());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        getFeaturedListing();
        page = 0;
//        getCategoryListing(page);


    }

    private void getIds() {

        main_items = (GridViewWithHeaderAndFooter) findViewById(R.id.main_items);
        View headerView = (View) getLayoutInflater().inflate(R.layout.category_details_header, null);
        Featuredlist = (RecyclerView) headerView.findViewById(R.id.featured_listing);
        progress_wheel = (ProgressWheel) findViewById(R.id.progress_wheel);

        TextView titel = (TextView) headerView.findViewById(R.id.ff);
        titel.setText(CategoryName);
//        rating.setEnabled(false);
//        rating.setProgress(null);
//        rating.setProgressed(null);
        main_items.addHeaderView(headerView);
        FreshList = new ArrayList<>();
        adpater = new GalleryAdpater(CategoryDetails.this, FreshList);
        main_items.setAdapter(adpater);


    }

    public void getFeaturedListing() {
//        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue = Volley.newRequestQueue(CategoryDetails.this);
        final JSONObject json = new JSONObject();
        try {
            json.put("cat_ID", CategoryID);
            Log.e("", " Input Data :   " + json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, Global.URL + "featuredList.php", json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        try {
                            Log.e("response featuredList :", "" + response);
//{"status":200,"message":"Success","data":[{"ListingID":"97","Title":"test","Description":"sdadadadadadada","ListImage":"http:\/\/media.sparsematrixtechnology.com\/images\/listing\/468946680app_icon1.png",
// "Price":"10","Auction":"0","SellerUsername":"pradeepbamola007","SellerImage":"","City":"Noida, Uttar Pradesh, India",
// "Add_Date":"2018-07-15 03:03:38","Total_Likes":null}]}
                            JSONObject jsonObject = response, jsonObject1;
                            JSONArray jsonArray;
                            JSONObject object;
                            if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                                FeaturedarrayList = new ArrayList<>();
                                FeaturedarrayList.clear();
                                jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    object = jsonArray.getJSONObject(i);
                                    ListingModel model = new ListingModel();
                                    model.setListingID(object.getString("ListingID"));
                                    model.setTitle(object.getString("Title"));
                                    model.setDescription(object.getString("Description"));
                                    model.setListImage(object.getString("ListImage"));
                                    model.setPrice(object.getString("Price"));
//                                    model.setAuction(object.getString("Auction"));
                                    model.setSellerUsername(object.getString("SellerUsername"));
                                    model.setSellerImage(object.getString("SellerImage"));
                                    model.setCity(object.getString("City"));
                                    model.setAdd_Date(object.getString("Add_Date"));
                                    model.setTotal_Likes(object.getString("Total_Likes"));
                                    FeaturedarrayList.add(model);
                                }
                                Featuredadpater = new FeaturedListingAdapter(FeaturedarrayList, CategoryDetails.this);
//                                LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(  getActivity(), LinearLayoutManager.HORIZONTAL, false);
//                                cat_list.setLayoutManager(horizontalLayoutManager);
                                Featuredlist.setAdapter(Featuredadpater);

                            } else if (jsonObject.getString("status").equals("Fail")) {
                                Global.msgDialog(CategoryDetails.this, jsonObject.getString("message"));
                            } else {
                                Global.msgDialog(CategoryDetails.this, jsonObject.getString("message"));
//                                Global.msgDialog(Login. getActivity(), "Error in parsing data");
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
                Global.msgDialog(CategoryDetails.this, "Unable To Get Response ");
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        queue.add(jsonObjectRequest);
    }

    public void getCategoryListing(final int page) {
//        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue = Volley.newRequestQueue(CategoryDetails.this);
        final JSONObject json = new JSONObject();
        try {
            json.put("page_no", page);
            json.put("cat_ID", CategoryID);
            Log.e("", " Input Data :   " + json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, Global.URL + "listing.php", json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        try {
                            Log.e("categoryFindings : ", page + ": " + response);
//{"status":200,"message":"Success","data":[{"ListingID":"97","Title":"test","Description":"sdadadadadadada","ListImage":"http:\/\/media.sparsematrixtechnology.com\/images\/listing\/468946680app_icon1.png",
// "Price":"10","Auction":"0","SellerUsername":"pradeepbamola007","SellerImage":"","City":"Noida, Uttar Pradesh, India",
// "Add_Date":"2018-07-15 03:03:38","Total_Likes":null}]}
                            JSONObject jsonObject = response, jsonObject1;
                            JSONArray jsonArray;
                            JSONObject object;
                            if (jsonObject.getString("status").equalsIgnoreCase("200")) {
//                                FreshList = new ArrayList<>();
//                                FreshList.clear();
                                jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    object = jsonArray.getJSONObject(i);
                                    ListingModel model = new ListingModel();
                                    model.setListingID(object.getString("ListingID"));
                                    model.setTitle(object.getString("Title"));
                                    model.setDescription(object.getString("Description"));
                                    model.setListImage(object.getString("ListImage"));
                                    model.setPrice(object.getString("Price"));
//                                    model.setAuction(object.getString("Auction"));
                                    model.setSellerUsername(object.getString("SellerUsername"));
                                    model.setSellerImage(object.getString("SellerImage"));
                                    model.setCity(object.getString("City"));
                                    model.setAdd_Date(object.getString("Add_Date"));
                                    model.setTotal_Likes(object.getString("Total_Likes"));
                                    FreshList.add(model);
                                }
//                                Featuredadpater = new FeaturedListingAdapter(FreshList, getActivity());
//                                LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(  getActivity(), LinearLayoutManager.HORIZONTAL, false);
//                                cat_list.setLayoutManager(horizontalLayoutManager);
//                                Featuredlist.setAdapter(Featuredadpater);
                                adpater.notifyDataSetChanged();

                            } else if (jsonObject.getString("status").equals("100")) {
                                Log.e("onResponse: ", jsonObject.getString("message"));
                            } else {
                                Global.msgDialog(CategoryDetails.this, jsonObject.getString("message"));
//                                Global.msgDialog(Login. getActivity(), "Error in parsing data");
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
                Global.msgDialog(CategoryDetails.this, "Unable To Get Response ");
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        queue.add(jsonObjectRequest);
    }

    public class GalleryAdpater extends BaseAdapter {

        Context context;
        ArrayList<ListingModel> arrlist;
        ViewHolder holder;
        LayoutInflater inflater;

        public GalleryAdpater(Context ctx, ArrayList<ListingModel> arrlist1) {
            this.context = ctx;
            this.arrlist = arrlist1;
            inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getCount() {
            return arrlist.size();
//            return 10;
        }

        private class ViewHolder {
            CircleImageView u_image;
            TextView status, name, time, like_count, item_price, title, item_description;
            ImageView image, details, like;
            RelativeLayout top;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.galleryadapter, null);
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.image = (ImageView) convertView.findViewById(R.id.image);
                holder.u_image = (CircleImageView) convertView.findViewById(R.id.u_image);
                holder.status = (TextView) convertView.findViewById(R.id.status);
                holder.like_count = (TextView) convertView.findViewById(R.id.like_count);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.item_price = (TextView) convertView.findViewById(R.id.item_price);
                holder.item_description = (TextView) convertView.findViewById(R.id.item_description);
                holder.details = (ImageView) convertView.findViewById(R.id.details);
                holder.like = (ImageView) convertView.findViewById(R.id.like);
                holder.top = (RelativeLayout) convertView.findViewById(R.id.top);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Glide.with(context).load(arrlist.get(position).getListImage())
//                    .apply(RequestOptions.placeholderOf(R.drawable.placeholder_user).dontAnimate())
                    .into(holder.image);


            Glide.with(context).load(arrlist.get(position).getSellerImage())
                    .apply(RequestOptions.placeholderOf(R.drawable.placeholder_user).dontAnimate())
                    .into(holder.u_image);
            holder.name.setText(arrlist.get(position).getSellerUsername());
            holder.time.setText(DateTimeUtils.returnTime(CategoryDetails.this, arrlist.get(position).getAdd_Date()));
            holder.item_description.setText(arrlist.get(position).getDescription());
            holder.title.setText(arrlist.get(position).getTitle());
            holder.item_price.setText(("S$ " + arrlist.get(position).getPrice()));

            try {
                holder.like_count.setText(Integer.valueOf(arrlist.get(position).getTotal_Likes()));
            } catch (Exception e) {
//                e.printStackTrace();
                holder.like_count.setText("0");
            }

            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            holder.like_count.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            holder.top.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            holder.details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(CategoryDetails.this, ListingDetails.class);
                    intent.putExtra("listingID", arrlist.get(position).getListingID());
                    startActivity(intent);
                }
            });

            return convertView;
        }

    }

}
