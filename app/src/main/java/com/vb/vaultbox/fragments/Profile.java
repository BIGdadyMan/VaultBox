package com.vb.vaultbox.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.vb.vaultbox.Followers;
import com.vb.vaultbox.Following;
import com.vb.vaultbox.R;
import com.vb.vaultbox.Settings;
import com.vb.vaultbox.Utills.DateTimeUtils;
import com.vb.vaultbox.Utills.Global;
import com.vb.vaultbox.Utills.SessionManager;
import com.vb.vaultbox.Utills.WordUtils;
import com.vb.vaultbox.models.ListingModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class Profile extends Fragment {
    TextView name;
    //    ProgressWheel progress_wheel;
    SharedPreferences prefs;
    RequestQueue queue;
    GridView list;
    TextView following, followers, offers, username, count, share;

    CircleImageView u_image;
    ImageView setting;
    ListAdapter adpater;
    ArrayList<ListingModel> arrayList;
    ProgressDialog dialog;
    TextView notfound;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile, container, false);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        queue = Volley.newRequestQueue(getActivity());

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);


        name = (TextView) view.findViewById(R.id.name);
        username = (TextView) view.findViewById(R.id.username);
        offers = (TextView) view.findViewById(R.id.offers);
        followers = (TextView) view.findViewById(R.id.followers);
        following = (TextView) view.findViewById(R.id.following);
        count = (TextView) view.findViewById(R.id.count);
        share = (TextView) view.findViewById(R.id.share);
        u_image = (CircleImageView) view.findViewById(R.id.u_image);
        setting = (ImageView) view.findViewById(R.id.setting);
        list = (GridView) view.findViewById(R.id.list);
        notfound = (TextView) view.findViewById(R.id.notfound);


        arrayList = new ArrayList<>();
        adpater = new ListAdapter(getActivity(), arrayList, "");
        list.setAdapter(adpater);

//        offers.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                arrayList = new ArrayList<>();
//                adpater.notifyDataSetChanged();
//            }
//        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                arrayList = new ArrayList<>();
//                adpater.notifyDataSetChanged();
//                getFollowers();
                Intent intent = new Intent(getActivity(), Followers.class);
                startActivity(intent);
            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                arrayList = new ArrayList<>();
//                adpater.notifyDataSetChanged();
//                getFollowing();
                Intent intent = new Intent(getActivity(), Following.class);
                startActivity(intent);
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Settings.class);
                startActivity(intent);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shareBody = "VaultBox Profile Details.\n" +
                        "UserName : " + SessionManager.get_name(prefs) + "\n" +
                        "Email : " + SessionManager.get_emailid(prefs) + "\n";
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "VaultBox");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
            }
        });

        getprofile();
        getListing();

        return view;
    }

    public void getprofile() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        final JSONObject json = new JSONObject();
        try {
            json.put("userID", SessionManager.get_user_id(prefs));
            Log.e(" Input Data :   ", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, Global.URL + "profile.php", json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        try {
                            Log.e("response   ", "" + response);
                            JSONObject jsonObject = response, jsonObject1;
                            JSONArray jsonArray;
                            JSONObject object;
//"status":200,"message":"Success","data":{"userId":"22","Email":"pathakkrishna7161@gmail.com","UserName":"pathakkrishna7161","FirstName":"Krishan","LastName":"Pathak",
// "Mobile":"","Bio":null,"ProfileImage":"","Birthday":null,"Gender":"Male","followers":0,"following":0}}                          if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                                object = jsonObject.getJSONObject("data");

                                SessionManager.save_emailid(prefs, object.getString("Email"));
                                SessionManager.save_user_id(prefs, object.getString("userId"));
                                SessionManager.save_name(prefs, object.getString("UserName"));
                                SessionManager.save_fisrtName(prefs, object.getString("FirstName"));
                                SessionManager.save_lastName(prefs, object.getString("LastName"));
                                SessionManager.save_mobile(prefs, object.getString("Mobile"));
                                SessionManager.save_image(prefs, object.getString("ProfileImage"));
                                SessionManager.save_dob(prefs, object.getString("Birthday"));
                                SessionManager.save_gender(prefs, object.getString("Gender"));
                                SessionManager.save_followers(prefs, object.getString("followers"));
                                SessionManager.save_following(prefs, object.getString("following"));
                                SessionManager.save_Bio(prefs, object.getString("Bio"));

                                Glide.with(getActivity()).load(SessionManager.get_image(prefs))
                                        .apply(RequestOptions.placeholderOf(R.drawable.placeholder_user).dontAnimate()
                                                .error(R.drawable.placeholder_user).dontAnimate()).into(u_image);

                                username.setText(WordUtils.capitalize(SessionManager.get_fisrtName(prefs) + " " + SessionManager.get_lastName(prefs)));
                                name.setText(SessionManager.get_name(prefs));
                                following.setText((SessionManager.get_following(prefs) + " following"));
                                followers.setText((SessionManager.get_followers(prefs) + " followers"));


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

    public void getListing() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        final JSONObject json = new JSONObject();
        try {
            json.put("userID", SessionManager.get_user_id(prefs));
            Log.e("", " Input Data :   " + json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, Global.URL + "UserListing.php", json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        try {
                            Log.e("response   ", "" + response);
//{"status":200,"message":"Success","data":[{"ListingID":"97","Title":"test","Description":"sdadadadadadada","ListImage":"http:\/\/media.sparsematrixtechnology.com\/images\/listing\/468946680app_icon1.png",
// "Price":"10","Auction":"0","SellerUsername":"pradeepbamola007","SellerImage":"","City":"Noida, Uttar Pradesh, India",
// "Add_Date":"2018-07-15 03:03:38","Total_Likes":null}]}
                            JSONObject jsonObject = response, jsonObject1;
                            JSONArray jsonArray;
                            JSONObject object;
                            if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                                arrayList = new ArrayList<>();
                                arrayList.clear();
                                count.setText((jsonObject.getString("Total_List") + " Listings"));
                                try {
                                    jsonArray = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        object = jsonArray.getJSONObject(i);
                                        ListingModel model = new ListingModel();
                                        model.setListingID(object.getString("ListingID"));
                                        model.setTitle(object.getString("Title"));
                                        model.setDescription(object.getString("Description"));
                                        model.setListImage(object.getString("ListImage"));
                                        model.setPrice(object.getString("Price"));
                                        model.setAuction(object.getString("Auction"));
                                        model.setSellerUsername(object.getString("SellerUsername"));
                                        model.setSellerImage(object.getString("SellerImage"));
                                        model.setCity(object.getString("City"));
                                        model.setAdd_Date(object.getString("Add_Date"));
                                        model.setTotal_Likes(object.getString("Total_Likes"));
                                        arrayList.add(model);
                                    }
                                    adpater = new ListAdapter(getActivity(), arrayList, "followers");

                                    list.setAdapter(adpater);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (arrayList.size() == 0) {
                                    notfound.setVisibility(View.VISIBLE);
                                }

                            } else if (jsonObject.getString("status").equals("100")) {
                                Global.msgDialog(getActivity(), jsonObject.getString("message"));
                            } else {
//                                Global.msgDialog(getActivity(), jsonObject.getString("message"));
                                Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                notfound.setVisibility(View.VISIBLE);
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

    public class ListAdapter extends BaseAdapter {

        Context context;
        ArrayList<ListingModel> arrlist;
        ViewHolder holder;
        LayoutInflater inflater;
        String status;

        public ListAdapter(Context ctx, ArrayList<ListingModel> arrlist1, String mstatus) {
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
            TextView status, name, time, like_count, item_price, title, item_description;
            ImageView image, details, like;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.galleryadapter, null);
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
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
//{"status":200,"message":"Success","data":[{"ListingID":"97","Title":"test","Description":"sdadadadadadada","ListImage":"http:\/\/media.sparsematrixtechnology.com\/images\/listing\/468946680app_icon1.png",
// "Price":"10","Auction":"0","SellerUsername":"pradeepbamola007","SellerImage":"","City":"Noida, Uttar Pradesh, India",
// "Add_Date":"2018-07-15 03:03:38","Total_Likes":null}]}

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
            holder.item_price.setText(("$ " + arrlist.get(position).getPrice()));

            try {
                holder.like_count.setText(Integer.valueOf(arrlist.get(position).getTotal_Likes()));
            } catch (Exception e) {
//                e.printStackTrace();
                holder.like_count.setText("0");
            }


            return convertView;
        }

    }
}
