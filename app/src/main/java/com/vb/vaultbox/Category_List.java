package com.vb.vaultbox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.vb.vaultbox.Utills.Global;
import com.vb.vaultbox.models.CategoryModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Category_List extends AppCompatActivity implements BSImagePicker.OnSingleImageSelectedListener {

    RecyclerView images_list;
    CategoryAdapter categoryAdapter;
    ImagesAdapter imagesAdapter;
    ArrayList<CategoryModel> categorylist;
    ProgressWheel progress_wheel;
    ListView category_list;

    String cat_id = "";
    ArrayList<String> images;
    int posi;
    ArrayList<String> images_nw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.text_toolbar);
        mTitle.setText(getResources().getString(R.string.chose_category));
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
        Global.centerToolbarTitle(toolbar);

        images_list = (RecyclerView) findViewById(R.id.images);
//        main_items = (NonScrollableGridView) findViewById(R.id.main_items);
        progress_wheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        category_list = (ListView) findViewById(R.id.category_list);


        images = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            images = bundle.getStringArrayList("images");
        }

        getCategory();
        images_nw = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if (images.size() > i)
                images_nw.add(images.get(i));
            else
                images_nw.add("");
        }


        imagesAdapter = new ImagesAdapter(images_nw, Category_List.this);
        images_list.setAdapter(imagesAdapter);

        category_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                cat_id = categorylist.get(i).getCatID();
                Intent intent = new Intent(Category_List.this, SubCategoryList.class);
                intent.putExtra("cat_id", cat_id);
                intent.putExtra("cat_name", categorylist.get(i).getCatName());
                images_nw.removeAll(Arrays.asList("", null));
                intent.putStringArrayListExtra("images", images_nw);
                startActivity(intent);
            }
        });

//        images_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
//                cat_id = categorylist.get(i).getCatID();
//                Intent intent = new Intent(Category_List.this, SubCategoryList.class);
//                intent.putExtra("cat_id", cat_id);
//                startActivity(intent);
//            }
//        });

    }

    public void getCategory() {
        RequestQueue queue = Volley.newRequestQueue(Category_List.this);
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


                                categoryAdapter = new CategoryAdapter(Category_List.this, categorylist);
                                category_list.setAdapter(categoryAdapter);

                            } else if (jsonObject.getString("status").equals("Fail")) {
                                Global.msgDialog(Category_List.this, jsonObject.getString("message"));
                            } else {
                                Global.msgDialog(Category_List.this, jsonObject.getString("message"));
//                                Global.msgDialog(Login.this, "Error in parsing data");
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
                        Global.msgDialog(Category_List.this, "Error from server");
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


    public class CategoryAdapter extends BaseAdapter {

        LayoutInflater inflater = null;
        ArrayList<CategoryModel> arrayList;

        Context context;
        Activity activity;
        SharedPreferences prefs;

        public CategoryAdapter(Activity a, ArrayList<CategoryModel> modelArrayList) {
            context = a;
            activity = a;
            inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            arrayList = modelArrayList;
            prefs = PreferenceManager.getDefaultSharedPreferences(context);

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return arrayList.size();
//            return 5;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return arrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View vi = convertView;
            vi = inflater.inflate(R.layout.category_list_item, null);

            TextView textView_list_item = (TextView) vi.findViewById(R.id.type);

            textView_list_item.setText(arrayList.get(position).getCatName());

            return vi;
        }


    }

    public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

        private ArrayList<String> arrayList;
        Context context;

        public ImagesAdapter(ArrayList<String> horizontalList, Context context) {
            this.arrayList = horizontalList;
            this.context = context;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            //        TextView cat_name;
            ImageView cat_icon;

            public ViewHolder(View view) {
                super(view);
//            cat_name = (TextView) view.findViewById(R.id.cat_name);
                cat_icon = (ImageView) view.findViewById(R.id.cat_icon);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.image_recycler_items, parent, false);

            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
//        CategoryModel model = arrayList.get(position);
//        holder.cat_name.setText(model.getCatName());
            Glide.with(context).load(arrayList.get(position))
                    .apply(new RequestOptions().placeholder(R.drawable.screen_shot).error(R.drawable.screen_shot))
//                .apply(RequestOptions.placeholderOf(R.drawable.screen_shot))
                    .into(holder.cat_icon);
            holder.cat_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    posi = position;
                    final DisplayMetrics metrics = getResources().getDisplayMetrics();
                    BSImagePicker pickerDialog = new BSImagePicker.Builder("com.asksira.imagepickersheetdemo.fileprovider")
                            .setPeekHeight(metrics.heightPixels)
                            .build();
                    pickerDialog.show(getSupportFragmentManager(), "picker");
                }
            });

        }

        @Override
        public int getItemCount() {
//        return arrayList.size();
            return 10;
        }

    }

    @Override
    public void onSingleImageSelected(Uri uri) {
        if (posi < images_nw.size()) {
            images_nw.set(posi, uri.toString());
        } else {
            images_nw.add(uri.toString());
        }
        imagesAdapter.notifyDataSetChanged();
    }

}
