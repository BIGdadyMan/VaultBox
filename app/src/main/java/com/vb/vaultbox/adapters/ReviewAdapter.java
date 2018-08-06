package com.vb.vaultbox.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.vb.vaultbox.R;
import com.vb.vaultbox.models.ReviewModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Pradeep on 6/22/2018.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private ArrayList<ReviewModel> arrayList;
    private Context context;

    public ReviewAdapter(ArrayList<ReviewModel> horizontalList, Context context) {
        this.arrayList = horizontalList;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView u_image;
        TextView name, review;
//        RatingBar ratingbar;
        RatingBar ratingbar;

        public ViewHolder(View view) {
            super(view);
            u_image = (CircleImageView) view.findViewById(R.id.u_image);
            name = (TextView) view.findViewById(R.id.name);
            review = (TextView) view.findViewById(R.id.review);
            ratingbar = (RatingBar) view.findViewById(R.id.rating);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_listing_items, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ReviewModel model = arrayList.get(position);

        Glide.with(context).load(model.getUserImage())
                .apply(RequestOptions.placeholderOf(R.drawable.placeholder_user).dontAnimate())
                .into(holder.u_image);

        holder.name.setText(model.getUserName());
//        holder.review.setText(DateTimeUtils.returnTime(context, model.getAdd_Date()));
        holder.review.setText(model.getReview());
        try {
            holder.ratingbar.setRating(Float.valueOf(model.getRating()));
        } catch (Exception e) {
            e.printStackTrace();
            holder.ratingbar.setRating(0);
        }
//        holder.item_price.setText(("$ " + model.getPrice()));
//
//        try {
//            holder.like_count.setText(Integer.valueOf(model.getTotal_Likes()));
//        } catch (Exception e) {
////                e.printStackTrace();
//            holder.like_count.setText("0");
//        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


}
