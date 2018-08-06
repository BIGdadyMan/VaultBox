package com.vb.vaultbox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
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
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.vb.vaultbox.Utills.Global;
import com.vb.vaultbox.models.LocationModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Manish Yadav on 6/27/2018.
 */

public class Location extends AppCompatActivity {

    ListView category_list;
    ArrayList<LocationModel> arrayList, arrayList_new;
    ItemsAdapter adapter;
    EditText searchedtxt;
    ProgressWheel progress_wheel;
    RequestQueue queue;
    private static final String TAG = "Location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.text_toolbar);
        mTitle.setText(Html.fromHtml("Location"));
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

        queue = Volley.newRequestQueue(Location.this);

        category_list = (ListView) findViewById(R.id.category_list);
        progress_wheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        searchedtxt = (EditText) findViewById(R.id.etxt_password);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .setCountry("IN")
                .build();

        autocompleteFragment.setFilter(autocompleteFilter);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getAddress());
                Intent intent = new Intent();
                intent.putExtra("location", place.getAddress());
                intent.putExtra("location_id", place.getId());
                setResult(RESULT_OK, intent);
                finish();

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        searchedtxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("    value   ", "" + s);
                if (s.length() > 0) {
                    arrayList_new = new ArrayList<LocationModel>();
                    for (int i = 0; i < arrayList.size(); i++) {
                        if (arrayList.get(i).getCityName().toLowerCase().contains(s.toString().toLowerCase())) {
                            arrayList_new.add(arrayList.get(i));
                        }
                    }
                    adapter = new ItemsAdapter(Location.this, arrayList_new);
                    category_list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    adapter = new ItemsAdapter(Location.this, arrayList);
                    category_list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (arrayList_new.isEmpty()) {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        category_list.setVisibility(View.GONE);
        category_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int Pos, long l) {
                if (searchedtxt.getText().length() > 0) {
                    Intent intent = new Intent();
                    intent.putExtra("location", arrayList_new.get(Pos).getCityName());
                    intent.putExtra("location_id", arrayList_new.get(Pos).getCityID());
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("location", arrayList.get(Pos).getCityName());
                    intent.putExtra("location_id", arrayList.get(Pos).getCityID());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

//        locationTask();
    }

    public void locationTask() {
        final JSONObject json = new JSONObject();
//        try {
//            json.put("action", "login");
//            json.put("emailId", email_String);
//            json.put("password", password_String);
//            json.put("device", "android");
//            json.put("deviceToken", FirebaseInstanceId.getInstance().getToken());
//
//            Log.e("", "login data   " + json);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        progress_wheel.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, Global.URL + "city.php", json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("login reponse", "" + response);
                        progress_wheel.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject2, jsonObject = new JSONObject(response.toString());
                            JSONArray jsonArray;
                            if (jsonObject.getString("status").equals("200")) {

                                arrayList = new ArrayList<LocationModel>();
                                arrayList.clear();
                                jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    jsonObject2 = jsonArray.getJSONObject(i);
                                    LocationModel model = new LocationModel();
                                    model.setCityID(jsonObject2.getString("CityID"));
                                    model.setCityName(jsonObject2.getString("CityName"));
                                    arrayList.add(model);
                                }
                                adapter = new ItemsAdapter(Location.this, arrayList);
                                category_list.setAdapter(adapter);
                            } else if (jsonObject.getString("status").equals("0")) {
                                Global.msgDialog(Location.this, jsonObject.getString("msg"));
                            } else {
                                Global.msgDialog(Location.this, "Error in server");
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
                Global.msgDialog(Location.this, "Internet connection is slow");
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        queue.add(jsonObjectRequest);
    }

    public class ItemsAdapter extends BaseAdapter {

        LayoutInflater inflater = null;
        ArrayList<LocationModel> arrayList;

        Context context;
        Activity activity;
        SharedPreferences prefs;

        public ItemsAdapter(Activity a, ArrayList<LocationModel> modelArrayList) {
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

        private class ViewHolder {
            TextView textView_list_item;
        }

        ViewHolder holder;

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.location_items, null);
                holder = new ViewHolder();
                holder.textView_list_item = (TextView) convertView.findViewById(R.id.location);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.textView_list_item.setText(arrayList.get(position).getCityName());

            return convertView;
        }


    }


}
