package com.vb.vaultbox.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.vb.vaultbox.Category_List;
import com.vb.vaultbox.ListingDetails;
import com.vb.vaultbox.R;
import com.vb.vaultbox.Utills.DateTimeUtils;
import com.vb.vaultbox.Utills.Global;
import com.vb.vaultbox.adapters.CategoryAdapter;
import com.vb.vaultbox.adapters.FeaturedListingAdapter;
import com.vb.vaultbox.models.CategoryModel;
import com.vb.vaultbox.models.ListingModel;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import in.srain.cube.views.GridViewWithHeaderAndFooter;


public class Search extends Fragment {

    RecyclerView cat_list, Featuredlist;
    GalleryAdpater adpater;
    CategoryAdapter categoryAdapter;
    FeaturedListingAdapter Featuredadpater;
    ArrayList<CategoryModel> categorylist;
    ArrayList<ListingModel> FeaturedarrayList, FreshList;
    GridViewWithHeaderAndFooter main_items;
    ProgressWheel progress_wheel;
    ProgressDialog dialog;
    SharedPreferences prefs;
    RequestQueue queue;
    TextView notfoundheader,notfound;

    int page = 0;
    private int preLast;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search, container, false);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        queue = Volley.newRequestQueue(getActivity());

        cat_list = (RecyclerView) view.findViewById(R.id.category);
        main_items = (GridViewWithHeaderAndFooter) view.findViewById(R.id.main_items);
        notfound = (TextView) view.findViewById(R.id.notfound);
        View headerView = inflater.inflate(R.layout.search_header, null);
        Featuredlist = (RecyclerView) headerView.findViewById(R.id.featured_listing);
        mPager = (ViewPager) headerView.findViewById(R.id.pager);
        notfoundheader = (TextView) headerView.findViewById(R.id.notfoundheader);
        indicator = (CirclePageIndicator) headerView.findViewById(R.id.indicator);
        progress_wheel = (ProgressWheel) view.findViewById(R.id.progress_wheel);

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);

        main_items.addHeaderView(headerView);
        FreshList = new ArrayList<>();
        adpater = new GalleryAdpater(getActivity(), FreshList);
        main_items.setAdapter(adpater);


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
                        getFreshFindings(page);
                    }
                }
            }
        });
        getCategory();
        getFeaturedListing();
        getBanner();
//        getCategoryListing(page);

        main_items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getActivity(), ListingDetails.class);
                intent.putExtra("listingID", FreshList.get(position).getListingID());
                startActivity(intent);
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        page = 0;

    }

    public void onMultiImageSelected(List<Uri> uriList) {
        Log.d("Matisse", "Uris: " + uriList);
        Intent intent = new Intent(getActivity(), Category_List.class);
        startActivity(intent);
    }

    public void getCategory() {
//        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue = Volley.newRequestQueue(getActivity());
        progress_wheel.setVisibility(View.VISIBLE);
        StringRequest postRequest = new StringRequest(Request.Method.GET, Global.URL + "category.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress_wheel.setVisibility(View.GONE);
                        try {
                            Log.e("response   ", "" + response);
                            JSONObject jsonObject = new JSONObject(response), jsonObject1;
                            JSONArray jsonArray;
                            JSONObject object;
//{"status":200,"message":"Success","data":[{"catID":"1","catName":"Hotel & Travel","catIcon":"http:\/\/sparsematrixtechnology.com\/vaultbox\/admin\/img\/categotryIcon\/icon1.png","catImg":"1.jpg"},{"catID":"2","catName":"Healthy & fitness","catIcon":"http:\/\/sparsematrixtechnology.com\/vaultbox\/admin\/img\/categotryIcon\/icon2.png","catImg":"2.jpg"},{"catID":"3","catName":"Real Estate","catIcon":"http:\/\/sparsematrixtechnology.com\/vaultbox\/admin\/img\/categotryIcon\/icon3.png","catImg":"3.jpg"},{"catID":"4","catName":"Restaurant","catIcon":"http:\/\/sparsematrixtechnology.com\/vaultbox\/admin\/img\/categotryIcon\/icon4.png","catImg":"1.jpg"}]}
                            if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                                categorylist = new ArrayList<CategoryModel>();
                                categorylist.clear();
                                jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    object = jsonArray.getJSONObject(i);
                                    CategoryModel model = new CategoryModel();
                                    model.setCatIcon(object.getString("catIcon"));
                                    model.setCatID(object.getString("catID"));
                                    model.setCatImg(object.getString("catImg"));
                                    model.setCatName(object.getString("catName"));
                                    categorylist.add(model);
                                }
                                categoryAdapter = new CategoryAdapter(categorylist, getActivity());
//                                LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(  getActivity(), LinearLayoutManager.HORIZONTAL, false);
//                                cat_list.setLayoutManager(horizontalLayoutManager);
                                cat_list.setAdapter(categoryAdapter);

                            } else if (jsonObject.getString("status").equals("Fail")) {
                                Global.msgDialog(getActivity(), jsonObject.getString("message"));
                            } else {
                                Global.msgDialog(getActivity(), jsonObject.getString("message"));
//                                Global.msgDialog(Login. getActivity(), "Error in parsing data");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progress_wheel.setVisibility(View.GONE);
                        Global.msgDialog(getActivity(), "Error from server");
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> param = new HashMap<String, String>();
                Log.e("param   ", "" + param);

                return param;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }

    public void getBanner() {
//        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue = Volley.newRequestQueue(getActivity());
        progress_wheel.setVisibility(View.VISIBLE);
//        http://sparsematrixtechnology.com/vaultbox/mobileWebservice/banner.php
        StringRequest postRequest = new StringRequest(Request.Method.GET, Global.URL + "banner.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress_wheel.setVisibility(View.GONE);
                        try {
                            Log.e("response   ", "" + response);
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray;
                            JSONObject object;
                            if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                                ImagesArray = new ArrayList<>();
                                ImagesArray.clear();
                                jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    object = jsonArray.getJSONObject(i);
                                    ImagesArray.add(object.getString("image"));
                                }
                                init();
                            } else if (jsonObject.getString("status").equals("Fail")) {
                                Global.msgDialog(getActivity(), jsonObject.getString("message"));
                            } else {
                                Global.msgDialog(getActivity(), jsonObject.getString("message"));
//                                Global.msgDialog(Login. getActivity(), "Error in parsing data");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progress_wheel.setVisibility(View.GONE);
                        Global.msgDialog(getActivity(), "Error from server");
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> param = new HashMap<String, String>();
                Log.e("param   ", "" + param);

                return param;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }

    public void getFeaturedListing() {
//        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue = Volley.newRequestQueue(getActivity());
        final JSONObject json = new JSONObject();
//        try {
//            json.put("userID", SessionManager.get_user_id(prefs));
//            Log.e("", " Input Data :   " + json);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
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
                                Featuredadpater = new FeaturedListingAdapter(FeaturedarrayList, getActivity());
//                                LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(  getActivity(), LinearLayoutManager.HORIZONTAL, false);
//                                cat_list.setLayoutManager(horizontalLayoutManager);
                                Featuredlist.setAdapter(Featuredadpater);
                                if(FeaturedarrayList.size()==0){
                                    notfoundheader.setVisibility(View.VISIBLE);
                                }else{
                                    notfoundheader.setVisibility(View.GONE);
                                }

                            } else if (jsonObject.getString("status").equals("100")) {
//                                Global.msgDialog(getActivity(), jsonObject.getString("message"));
                                if(FeaturedarrayList.size()==0){
                                    notfoundheader.setVisibility(View.VISIBLE);
                                }else{
                                    notfoundheader.setVisibility(View.GONE);
                                }
                            } else {
                                Global.msgDialog(getActivity(), jsonObject.getString("message"));
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
                Global.msgDialog(getActivity(), "Unable To Get Response ");
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        queue.add(jsonObjectRequest);
    }

    public void getFreshFindings(final int page) {
//        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue = Volley.newRequestQueue(getActivity());
        final JSONObject json = new JSONObject();
        try {
            json.put("page_no", page);
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
                            Log.e("FreshFindings : ", page + ": " + response);
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
                                if(FreshList.size()==0){
                                    notfound.setVisibility(View.VISIBLE);
                                }else{
                                    notfound.setVisibility(View.GONE);
                                }
                            } else if (jsonObject.getString("status").equals("100")) {
//                                Global.msgDialog(getActivity(), jsonObject.getString("message"));
                                if(FreshList.size()==0){
                                    notfound.setVisibility(View.VISIBLE);
                                }else{
                                    notfound.setVisibility(View.GONE);
                                }
                            } else {
                                Global.msgDialog(getActivity(), jsonObject.getString("message"));
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
//                Log.e("FreshFindings Error : ", page + ": " + response);
                Global.msgDialog(getActivity(), error.getMessage());
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
        GalleryAdpater.ViewHolder holder;
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
                convertView = inflater.inflate(R.layout.galleryadaptersearch, null);
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
            holder.time.setText(DateTimeUtils.returnTime(getActivity(), arrlist.get(position).getAdd_Date()));
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
                    Intent intent = new Intent(getActivity(), ListingDetails.class);
                    intent.putExtra("listingID", arrlist.get(position).getListingID());
                    startActivity(intent);
                }
            });

            return convertView;
        }

    }

    public class SlidingImage_Adapter extends PagerAdapter {
        private ArrayList<String> IMAGES;
        private LayoutInflater inflater;
        private Context context;


        public SlidingImage_Adapter(Context context, ArrayList<String> IMAGES) {
            this.context = context;
            this.IMAGES = IMAGES;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return IMAGES.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);

            assert imageLayout != null;
            final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);

            Glide.with(getActivity()).load(IMAGES.get(position)).into(imageView);
//            imageView.setImageResource(IMAGES.get(position));

            view.addView(imageLayout, 0);

            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }


    }

    ArrayList<String> ImagesArray = new ArrayList<>();
    ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    CirclePageIndicator indicator;

    private void init() {
        mPager.setAdapter(new SlidingImage_Adapter(getActivity(), ImagesArray));
        indicator.setViewPager(mPager);

        final float density = getResources().getDisplayMetrics().density;

//Set circle indicator radius
        indicator.setRadius(5 * density);

        NUM_PAGES = ImagesArray.size();

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

    }
}
