package com.example.zingakart.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
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
import com.example.zingakart.Activities.WishListActivity;
import com.example.zingakart.Model.SubCategoryModel;
import com.example.zingakart.R;

import java.util.ArrayList;
import java.util.List;

public class SubCategoryModelAdapter extends RecyclerView.Adapter<SubCategoryModelAdapter.ViewHolder> implements Filterable {
    List<SubCategoryModel> subCategoryModelList;
    Context context;
    String long_description = "";
    String short_description = "";
    String city_name = "";
    String postal_code = "";
    String sum = "";
    String type_name = "";
    List<String> attribute;
    List<String> item = new ArrayList<String>();
    List<String> qty = new ArrayList<String>();
    List<String> pri = new ArrayList<String>();
    List<String> im = new ArrayList<String>();
    Vibrator v;


    public SubCategoryModelAdapter(Context context, List<SubCategoryModel> subCategoryModelList, String long_description, String short_description, String city_name, String postal_code, String type_name, List<String> attribute) {

        this.context = context;
        this.subCategoryModelList = subCategoryModelList;
        this.long_description = long_description;
        this.short_description = short_description;
        this.city_name = city_name;
        this.postal_code = postal_code;
        this.type_name = type_name;
        this.attribute= attribute;
    }


    @NonNull
    @Override
    public SubCategoryModelAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subcategory_itemrow, parent, false);

        return new SubCategoryModelAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SubCategoryModelAdapter.ViewHolder holder, final int position) {

        holder.tv_name.setText(subCategoryModelList.get(position).getName().replace("&amp;", "&"));
        holder.tv_item_category.setText(subCategoryModelList.get(position).getSlug());

        if(subCategoryModelList.get(position).getType().equals("simple"))
        {
            holder.tv_price.setText("\u20B9" + subCategoryModelList.get(position).getSalePrice());
            holder.tv_discount.setText("\u20B9" + subCategoryModelList.get(position).getRegularPrice());

        }
        else if(subCategoryModelList.get(position).getType().equals("variable"))
        {
            holder.tv_price.setText("\u20B9" + subCategoryModelList.get(position).getPrice());
            holder.tv_discount.setVisibility(View.GONE);
        }

        holder.tv_discount.setPaintFlags(holder.tv_discount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        Glide.with(context).load(subCategoryModelList.get(position).getImages().get(0).getSrc()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.tv_image);



        holder.add_favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isFavourite = readStae();
                if (isFavourite) {
                    holder.add_favorites.setBackgroundResource(R.drawable.favorite);
                    Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
                    isFavourite = false;
                    saveStae(isFavourite);
                    Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            v.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE));
                        }
                    }

                    if(subCategoryModelList.get(position).getType().equals("simple"))
                    {
                        item.add(holder.tv_name.getText().toString());
                        pri.add(String.valueOf(subCategoryModelList.get(position).getSalePrice()));
                        im.add(subCategoryModelList.get(position).getImages().get(position).getSrc());
                        Intent intent = new Intent(context, WishListActivity.class);
                        intent.putStringArrayListExtra("item_name", (ArrayList<String>) item);
                        intent.putStringArrayListExtra("item_price", (ArrayList<String>) pri);
                        intent.putExtra("visible", "");
                        intent.putStringArrayListExtra("image", (ArrayList<String>) im);
                        intent.putExtra("total_price", sum);
                        context.startActivity(intent);
                    }
                    else if(subCategoryModelList.get(position).getType().equals("variable"))
                    {
                        item.add(holder.tv_name.getText().toString());
                        pri.add(String.valueOf(subCategoryModelList.get(position).getPrice()));
                        im.add(subCategoryModelList.get(position).getImages().get(position).getSrc());
                        Intent intent = new Intent(context, WishListActivity.class);
                        intent.putStringArrayListExtra("item_name", (ArrayList<String>) item);
                        intent.putStringArrayListExtra("item_price", (ArrayList<String>) pri);
                        intent.putExtra("visible", "");
                        intent.putStringArrayListExtra("image", (ArrayList<String>) im);
                        intent.putExtra("total_price", sum);
                        context.startActivity(intent);
                    }

                } else {
                    Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(80);
                    holder.add_favorites.setBackgroundResource(R.drawable.remove_favorite);
                    Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    isFavourite = true;
                    saveStae(isFavourite);

                }

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ItemDescriptionActivity.class);
                intent.putExtra("name", type_name);
                intent.putExtra("item_price", subCategoryModelList.get(position).getSalePrice());
                intent.putExtra("discount_price", subCategoryModelList.get(position).getRegularPrice());
                intent.putExtra("price", subCategoryModelList.get(position).getPrice());
                intent.putExtra("item_category", holder.tv_item_category.getText().toString());
                intent.putExtra("image", subCategoryModelList.get(position).getImages().get(0).getSrc());
                intent.putExtra("long_description", long_description);
                intent.putExtra("short_description", short_description);
                intent.putExtra("city_name", city_name);
                intent.putExtra("postal_code", postal_code);
                intent.putStringArrayListExtra("attributes", (ArrayList<String>) attribute);
                context.startActivity(intent);
            }
        });

    }

    private void saveStae(boolean isFavourite) {
        SharedPreferences aSharedPreferenes = context.getSharedPreferences(
                "Favourite", Context.MODE_PRIVATE);
        SharedPreferences.Editor aSharedPreferenesEdit = aSharedPreferenes
                .edit();
        aSharedPreferenesEdit.putBoolean("State", isFavourite);
        aSharedPreferenesEdit.commit();
    }

    private boolean readStae() {
        SharedPreferences aSharedPreferenes = context.getSharedPreferences(
                "Favourite", Context.MODE_PRIVATE);
        return aSharedPreferenes.getBoolean("State", true);
    }

    @Override
    public int getItemCount() {
        return subCategoryModelList.size();
    }

    @Override
    public Filter getFilter() {
        return itemFilter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

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

