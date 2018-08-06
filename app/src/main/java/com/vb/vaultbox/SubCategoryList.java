package com.vb.vaultbox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
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
import com.pnikosis.materialishprogress.ProgressWheel;
import com.vb.vaultbox.Utills.Global;
import com.vb.vaultbox.models.CategoryModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Manish Yadav on 6/27/2018.
 */

public class SubCategoryList extends AppCompatActivity {


    ListView category_list;
    ArrayList<CategoryModel> categorylist;
    ItemsAdapter categoryAdapter;

    String cat_id = "", cat_name = "";
    ProgressWheel progress_wheel;
    ArrayList<String> image_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_category_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.text_toolbar);
        mTitle.setText(getResources().getString(R.string.chose_subcategory));
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

        category_list = (ListView) findViewById(R.id.category_list);
        progress_wheel = (ProgressWheel) findViewById(R.id.progress_wheel);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            cat_id = bundle.getString("cat_id");
            cat_name = bundle.getString("cat_name");
            image_list = bundle.getStringArrayList("images");
            mTitle.setText(cat_name);
        }

        category_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SubCategoryList.this, SellForm.class);
                intent.putExtra("cat_id", cat_id);
                intent.putExtra("subcat_id", categorylist.get(i).getCatID());
                intent.putStringArrayListExtra("image_list", image_list);
                intent.putExtra("superSubCatID", categorylist.get(i).getSuperSubCat());
                startActivity(intent);
            }
        });


        getSubCategory();
    }



    public void getSubCategory() {
        RequestQueue queue = Volley.newRequestQueue(SubCategoryList.this);
        final JSONObject json = new JSONObject();
        try {
//           param.put("catID", cat_id);
            json.put("catID", cat_id);
            Log.e("", "getSubCategory data :  " + json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progress_wheel.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, Global.URL + "subCats.php", json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v(" reponse", "" + response);
                        progress_wheel.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject = response, jsonObject1;
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
//                                    model.setCatIcon(object.getString("catIcon"));
                                    model.setCatID(object.getString("subCatID"));
                                    model.setCatName(object.getString("Name"));
                                    model.setSuperSubCat(object.getString("superSubCat"));
                                    categorylist.add(model);
                                }


                                categoryAdapter = new ItemsAdapter(SubCategoryList.this, categorylist);
                                category_list.setAdapter(categoryAdapter);

                            } else if (jsonObject.getString("status").equals("Fail")) {
                                Global.msgDialog(SubCategoryList.this, jsonObject.getString("message"));
                            } else {
                                Global.msgDialog(SubCategoryList.this, jsonObject.getString("message"));
//                                Global.msgDialog(Login.this, "Error in parsing data");
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
                Global.msgDialog(SubCategoryList.this, "Unable To Get Response ");
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        queue.add(jsonObjectRequest);
    }


    public class ItemsAdapter extends BaseAdapter {

        LayoutInflater inflater = null;
        ArrayList<CategoryModel> arrayList;

        Context context;
        Activity activity;
        SharedPreferences prefs;

        public ItemsAdapter(Activity a, ArrayList<CategoryModel> modelArrayList) {
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


}
