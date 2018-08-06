package com.vb.vaultbox.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.vb.vaultbox.ListingDetails;
import com.vb.vaultbox.R;
import com.vb.vaultbox.Utills.DateTimeUtils;
import com.vb.vaultbox.models.CategoryModel;
import com.vb.vaultbox.models.ListingModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Pradeep on 6/22/2018.
 */

public class FeaturedListingAdapter extends RecyclerView.Adapter<FeaturedListingAdapter.ViewHolder> {

    private ArrayList<ListingModel> arrayList;
    Context context;

    public FeaturedListingAdapter(ArrayList<ListingModel> horizontalList, Context context) {
        this.arrayList = horizontalList;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView u_image;
        TextView status, name, time, like_count, item_price, title, item_description;
        ImageView image, details, like;
        RelativeLayout all,top;

        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            time = (TextView) view.findViewById(R.id.time);
            image = (ImageView) view.findViewById(R.id.image);
            u_image = (CircleImageView) view.findViewById(R.id.u_image);
            status = (TextView) view.findViewById(R.id.status);
            like_count = (TextView) view.findViewById(R.id.like_count);
            title = (TextView) view.findViewById(R.id.title);
            item_price = (TextView) view.findViewById(R.id.item_price);
            item_description = (TextView) view.findViewById(R.id.item_description);
            details = (ImageView) view.findViewById(R.id.details);
            like = (ImageView) view.findViewById(R.id.like);
            all = (RelativeLayout) view.findViewById(R.id.all);
            top = (RelativeLayout) view.findViewById(R.id.top);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.featured_listing_items, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ListingModel model = arrayList.get(position);

        Glide.with(context).load(model.getListImage())
//                    .apply(RequestOptions.placeholderOf(R.drawable.placeholder_user).dontAnimate())
                .into(holder.image);
        Glide.with(context).load(model.getSellerImage())
                .apply(RequestOptions.placeholderOf(R.drawable.placeholder_user).dontAnimate())
                .into(holder.u_image);

        holder.name.setText(model.getSellerUsername());
        holder.time.setText(DateTimeUtils.returnTime(context, model.getAdd_Date()));
        holder.item_description.setText(model.getDescription());
        holder.title.setText(model.getTitle());
        holder.item_price.setText(("S$ " + model.getPrice()));

        try {
            holder.like_count.setText(Integer.valueOf(model.getTotal_Likes()));
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
                Intent intent = new Intent(context, ListingDetails.class);
                intent.putExtra("listingID",model.getListingID());
                context.startActivity(intent);
            }
        });

        holder.all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ListingDetails.class);
                intent.putExtra("listingID",model.getListingID());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


}
