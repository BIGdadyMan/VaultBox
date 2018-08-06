package com.vb.vaultbox;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.vb.vaultbox.Utills.Global;
import com.vb.vaultbox.Utills.SessionManager;
import com.vb.vaultbox.models.FollowModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class Followers extends AppCompatActivity {
    SharedPreferences prefs;
    RequestQueue queue;
    ListView list;
    ListAdapter adpater;
    ArrayList<FollowModel> arrayList;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.following_user);

//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.profile, container, false);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.text_toolbar);
        mTitle.setText(getResources().getString(R.string.followers));
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

        prefs = PreferenceManager.getDefaultSharedPreferences(Followers.this);
        queue = Volley.newRequestQueue(Followers.this);

        dialog = new ProgressDialog(Followers.this);
        dialog.setMessage("Loading...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);


//        name = (TextView) findViewById(R.id.name);
//        username = (TextView) findViewById(R.id.username);
//        offers = (TextView) findViewById(R.id.offers);
//        followers = (TextView) findViewById(R.id.followers);
//        following = (TextView) findViewById(R.id.following);
//        u_image = (CircleImageView) findViewById(R.id.u_image);
//        setting = (ImageView) findViewById(R.id.setting);
        list = (ListView) findViewById(R.id.list);


        arrayList = new ArrayList<>();
        adpater = new ListAdapter(Followers.this, arrayList,"");
        list.setAdapter(adpater);



        getFollowing();

//        return view;
    }


    public void getFollowers() {
        RequestQueue queue = Volley.newRequestQueue(Followers.this);
        final JSONObject json = new JSONObject();
        try {
            json.put("userID", SessionManager.get_user_id(prefs));
            Log.e("", " Input Data :   " + json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, Global.URL + "followerList.php", json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        try {
                            Log.e("response   ", "" + response);
                            JSONObject jsonObject = response, jsonObject1;
                            JSONArray jsonArray;
                            JSONObject object;
//{"status":200,"message":"Success","data":[{"catID":"1","catName":"Hotel & Travel","catIcon":"http:\/\/sparsematrixtechnology.com\/vaultbox\/admin\/img\/categotryIcon\/icon1.png","catImg":"1.jpg"},{"catID":"2","catName":"Healthy & fitness","catIcon":"http:\/\/sparsematrixtechnology.com\/vaultbox\/admin\/img\/categotryIcon\/icon2.png","catImg":"2.jpg"},{"catID":"3","catName":"Real Estate","catIcon":"http:\/\/sparsematrixtechnology.com\/vaultbox\/admin\/img\/categotryIcon\/icon3.png","catImg":"3.jpg"},{"catID":"4","catName":"Restaurant","catIcon":"http:\/\/sparsematrixtechnology.com\/vaultbox\/admin\/img\/categotryIcon\/icon4.png","catImg":"1.jpg"}]}
                            if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                                arrayList = new ArrayList<>();
                                arrayList.clear();
                                jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    object = jsonArray.getJSONObject(i);
                                    FollowModel model = new FollowModel();
                                    model.setId(object.getString("FollowerID"));
                                    model.setFirstNAme(object.getString("FirstNAme"));
                                    model.setLastName(object.getString("LastName"));
                                    model.setEmail(object.getString("Email"));
                                    model.setImage(object.getString("Image"));
                                    arrayList.add(model);
                                }
                                adpater = new ListAdapter(Followers.this, arrayList,"followers");
//                                LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(  Following.this, LinearLayoutManager.HORIZONTAL, false);
//                                cat_list.setLayoutManager(horizontalLayoutManager);
                                list.setAdapter(adpater);

                            } else if (jsonObject.getString("status").equals("Fail")) {
                                Global.msgDialog(Followers.this, jsonObject.getString("message"));
                            } else {
                                Global.msgDialog(Followers.this, jsonObject.getString("message"));
//                                Global.msgDialog(Login. Following.this, "Error in parsing data");
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
                Global.msgDialog(Followers.this, "Unable To Get Response ");
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        queue.add(jsonObjectRequest);
    }

    public void getFollowing() {
        RequestQueue queue = Volley.newRequestQueue(Followers.this);
        final JSONObject json = new JSONObject();
        try {
            json.put("userID", SessionManager.get_user_id(prefs));
            Log.e("", " Input Data :   " + json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, Global.URL + "followingList.php", json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        try {
                            Log.e("response   ", "" + response);
                            JSONObject jsonObject = response, jsonObject1;
                            JSONArray jsonArray;
                            JSONObject object;
//{"status":200,"message":"Success","data":[{"catID":"1","catName":"Hotel & Travel","catIcon":"http:\/\/sparsematrixtechnology.com\/vaultbox\/admin\/img\/categotryIcon\/icon1.png","catImg":"1.jpg"},{"catID":"2","catName":"Healthy & fitness","catIcon":"http:\/\/sparsematrixtechnology.com\/vaultbox\/admin\/img\/categotryIcon\/icon2.png","catImg":"2.jpg"},{"catID":"3","catName":"Real Estate","catIcon":"http:\/\/sparsematrixtechnology.com\/vaultbox\/admin\/img\/categotryIcon\/icon3.png","catImg":"3.jpg"},{"catID":"4","catName":"Restaurant","catIcon":"http:\/\/sparsematrixtechnology.com\/vaultbox\/admin\/img\/categotryIcon\/icon4.png","catImg":"1.jpg"}]}
                            if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                                arrayList = new ArrayList<>();
                                arrayList.clear();
                                jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    object = jsonArray.getJSONObject(i);
                                    FollowModel model = new FollowModel();
                                    model.setId(object.getString("FollowingID"));
                                    model.setFirstNAme(object.getString("FirstNAme"));
                                    model.setLastName(object.getString("LastName"));
                                    model.setEmail(object.getString("Email"));
                                    model.setImage(object.getString("Image"));
                                    arrayList.add(model);
                                }
                                adpater = new ListAdapter(Followers.this, arrayList,"following");
//                                LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(  Following.this, LinearLayoutManager.HORIZONTAL, false);
//                                cat_list.setLayoutManager(horizontalLayoutManager);
                                list.setAdapter(adpater);

                            } else if (jsonObject.getString("status").equals("Fail")) {
                                Global.msgDialog(Followers.this, jsonObject.getString("message"));
                            } else {
                                Global.msgDialog(Followers.this, jsonObject.getString("message"));
//                                Global.msgDialog(Login. Following.this, "Error in parsing data");
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
                Global.msgDialog(Followers.this, "Unable To Get Response ");
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        queue.add(jsonObjectRequest);
    }

    public class ListAdapter extends BaseAdapter {

        Context context;
        ArrayList<FollowModel> arrlist;
        ViewHolder holder;
        LayoutInflater inflater;
        String status;

        public ListAdapter(Context ctx, ArrayList<FollowModel> arrlist1, String mstatus) {
            this.context = ctx;
            this.arrlist = arrlist1;
            this.status = mstatus;
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
            TextView status, name, time;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.follow_adapter_item, null);
                holder = new ViewHolder();
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.u_image = (CircleImageView) convertView.findViewById(R.id.u_image);
            holder.status = (TextView) convertView.findViewById(R.id.status);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.time = (TextView) convertView.findViewById(R.id.time);

            Glide.with(context).load(arrlist.get(position).getImage())
                    .apply(RequestOptions.placeholderOf(R.drawable.placeholder_user).dontAnimate())
                    .into(holder.u_image);

            holder.name.setText(arrlist.get(position).getFirstNAme()+" "+arrlist.get(position).getLastName());
            holder.time.setText(arrlist.get(position).getEmail());

            if (status.equalsIgnoreCase("following")) {
                holder.status.setVisibility(View.VISIBLE);
                holder.status.setText("unfollow");
                holder.status.setBackground(getResources().getDrawable(R.drawable.btn_bg_red));
            } else if (status.equalsIgnoreCase("followers")) {
                holder.status.setVisibility(View.VISIBLE);
                holder.status.setText("following");
                holder.status.setBackground(getResources().getDrawable(R.drawable.btn_bg_grey));
            } else {
                holder.status.setVisibility(View.GONE);
            }

            return convertView;
        }

    }
}
