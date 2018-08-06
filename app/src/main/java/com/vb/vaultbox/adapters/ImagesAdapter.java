package com.vb.vaultbox.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.vb.vaultbox.R;
import com.vb.vaultbox.models.CategoryModel;

import java.util.ArrayList;

/**
 * Created by Pradeep on 6/22/2018.
 */

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

    private ArrayList<String> arrayList;
    Context context;

    public ImagesAdapter(ArrayList<String> horizontalList, Context context) {
        this.arrayList = horizontalList;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
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
    public void onBindViewHolder(ViewHolder holder, int position) {
//        CategoryModel model = arrayList.get(position);
//        holder.cat_name.setText(model.getCatName());
        Glide.with(context).load(arrayList.get(position))
                .apply(new RequestOptions().placeholder(R.drawable.screen_shot).error(R.drawable.screen_shot))
//                .apply(RequestOptions.placeholderOf(R.drawable.screen_shot))
                .into(holder.cat_icon);
    }

    @Override
    public int getItemCount() {
//        return arrayList.size();
        return 10;
    }


}
