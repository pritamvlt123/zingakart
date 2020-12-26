package com.example.zingakart.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.zingakart.Activities.ItemDescriptionActivity;
import com.example.zingakart.Model.SelectedCategoryModel;
import com.example.zingakart.Model.SubCategoryModel;
import com.example.zingakart.R;

import java.util.ArrayList;
import java.util.List;

public class FeaturedItemsAdapter extends RecyclerView.Adapter<FeaturedItemsAdapter.ViewHolder> implements Filterable {
    List<SubCategoryModel> subCategoryModelList;
    Context context;
    Boolean OnClick = false;

    public FeaturedItemsAdapter(Context context, List<SubCategoryModel> subCategoryModelList) {

        this.context = context;
        this.subCategoryModelList = subCategoryModelList;


    }


    @NonNull
    @Override
    public FeaturedItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_featured_items, parent, false);

        return new FeaturedItemsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FeaturedItemsAdapter.ViewHolder holder, final int position) {
        SubCategoryModel subCategoryModel = subCategoryModelList.get(position);
        //description = subCategoryModelList.get(position).getDescription();
        holder.tv_name.setText(subCategoryModelList.get(position).getName().replace("&amp;","&"));
        holder.tv_item_category.setText(subCategoryModelList.get(position).getSlug());
        holder.tv_price.setText("\u20B9"+subCategoryModelList.get(position).getSalePrice());
        holder.tv_discount.setText("\u20B9"+subCategoryModelList.get(position).getPrice());
        holder.tv_price.setPaintFlags(holder.tv_discount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        // loading album cover using Glide library

//        if (subCategoryModelList.get(position).getImages().get(0).getSrc() != null) {
//            if (subCategoryModelList.get(8).getName().equals("Chocolate") && subCategoryModelList.get(8).getImages().get(0).getSrc().isEmpty()) {
//                Glide.with(context).load("").diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.tv_image);
//
//            }
           Glide.with(context).load(subCategoryModelList.get(position).getImages().get(0).getSrc()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.tv_image);
//        }


        holder.add_favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (OnClick) {
                    holder.add_favorites.setBackgroundResource(R.drawable.favorite);
                    Toast.makeText(context, "Item added to your wishlist", Toast.LENGTH_SHORT).show();
                } else {
                    holder.add_favorites.setBackgroundResource(R.drawable.remove_favorite);
                    Toast.makeText(context, "Item removed from your wishlist", Toast.LENGTH_SHORT).show();
                }
                OnClick = !OnClick;


            }
        });

        holder.tv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ItemDescriptionActivity.class);
                intent.putExtra("item_name", holder.tv_name.getText().toString());
                intent.putExtra("item_price", holder.tv_price.getText().toString());
                intent.putExtra("discount_price", holder.tv_discount.getText().toString());
                intent.putExtra("item_category", holder.tv_item_category.getText().toString());
                intent.putExtra("image", subCategoryModelList.get(position).getImages().get(0).getSrc());
                intent.putExtra("short_description", subCategoryModelList.get(position).getShortDescription());
                intent.putExtra("long_description", subCategoryModelList.get(position).getDescription());
                context.startActivity(intent);
            }
        });


    }


    @Override
    public int getItemCount() {
        return subCategoryModelList.size();
    }

    @Override
    public Filter getFilter() {
        return itemFilter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name, tv_price, tv_discount, tv_item_category;
        private ImageView tv_image, add_favorites;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_item_name);
            tv_image = (ImageView) itemView.findViewById(R.id.item_image);
            tv_item_category = (TextView) itemView.findViewById(R.id.tv_item_category);
            add_favorites = (ImageView) itemView.findViewById(R.id.add_favorite);
            tv_price = (TextView) itemView.findViewById(R.id.tv_actual_price);
            tv_discount = (TextView) itemView.findViewById(R.id.tv_discounted_price);

        }

    }


    private Filter itemFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<SubCategoryModel> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(subCategoryModelList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (SubCategoryModel item : subCategoryModelList) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            subCategoryModelList.clear();
            subCategoryModelList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };
}
