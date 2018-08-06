package com.vb.vaultbox.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vb.vaultbox.CategoryDetails;
import com.vb.vaultbox.R;
import com.vb.vaultbox.models.CategoryModel;

import java.util.ArrayList;

/**
 * Created by Pradeep on 6/22/2018.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private ArrayList<CategoryModel> arrayList;
    Context context;

    public CategoryAdapter(ArrayList<CategoryModel> horizontalList, Context context) {
        this.arrayList = horizontalList;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView cat_name;
        ImageView cat_icon;
        LinearLayout ull;

        public ViewHolder(View view) {
            super(view);
            cat_name = (TextView) view.findViewById(R.id.cat_name);
            cat_icon = (ImageView) view.findViewById(R.id.cat_icon);
            ull = (LinearLayout) view.findViewById(R.id.ull);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_recycler_items, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final CategoryModel model = arrayList.get(position);
        holder.cat_name.setText(model.getCatName());
        Glide.with(context).load(model.getCatIcon()).into(holder.cat_icon);
        holder.ull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CategoryDetails.class);
                intent.putExtra("CategoryID", model.getCatID());
                intent.putExtra("CategoryName", model.getCatName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


}
