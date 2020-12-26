package com.example.zingakart.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.zingakart.APIClient.User_Service;
import com.example.zingakart.Helper.RecylerViewClickInterface;
import com.example.zingakart.Model.SelectedCategoryModel;
import com.example.zingakart.Model.SubCategoryModel;
import com.example.zingakart.R;

import java.util.ArrayList;
import java.util.List;

public class ItemViewAdapter extends RecyclerView.Adapter<ItemViewAdapter.ViewHolder> implements Filterable {
    private List<SelectedCategoryModel> selectedCategoryModels;
    private List<SubCategoryModel> subCategoryModelList;
    Boolean OnClick = false;
    User_Service userService;
    Context context;
    private RecylerViewClickInterface recylerViewClickInterface;
    OnclickItem onclickItem;

    public ItemViewAdapter(Context context,List<SelectedCategoryModel> selectedCategoryModels, RecylerViewClickInterface recylerViewClickInterface, OnclickItem onclickItem) {

        this.context = context;
        this.selectedCategoryModels = selectedCategoryModels;
        this.recylerViewClickInterface = recylerViewClickInterface;
        this.onclickItem = onclickItem;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);

        return new ViewHolder(view,onclickItem);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewAdapter.ViewHolder holder, final int position) {

        holder.tv_name.setText(selectedCategoryModels.get(position).getName().replace("&amp;","and"));
        if(selectedCategoryModels.get(position).getImage() == null)
        {
            Glide.with(context).load(context.getResources().getIdentifier("no_image", "drawable", context.getPackageName())).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.tv_image);
        }
        else
        {
            Glide.with(context).load(selectedCategoryModels.get(position).getImage().getSrc()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.tv_image);
        }


    }


    @Override
    public int getItemCount() {
        return selectedCategoryModels.size();
    }

    @Override
    public Filter getFilter() {
        return itemFilter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        OnclickItem onclickItem;
        private TextView tv_name,tv_price,tv_discount, tv_item_category ;
        private ImageView tv_image, add_favorites;
        public ViewHolder(@NonNull View itemView, OnclickItem onclickItem) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_item_name);
            tv_image = (ImageView) itemView.findViewById(R.id.item_image);
            tv_item_category = (TextView) itemView.findViewById(R.id.tv_item_category);
            this.onclickItem = onclickItem;

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            onclickItem.onItemClick(getAdapterPosition());
            int id = selectedCategoryModels.get(getAdapterPosition()).getId();
            recylerViewClickInterface.onItemClick(getAdapterPosition(),id);
        }
    }


    private Filter itemFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<SelectedCategoryModel> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(selectedCategoryModels);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (SelectedCategoryModel item : selectedCategoryModels) {
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
            selectedCategoryModels.clear();
            selectedCategoryModels.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public interface OnclickItem{
        void onItemClick(int position);
    }

}
