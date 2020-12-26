package com.example.zingakart.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import com.example.zingakart.Model.SelectedCategoryModel;
import com.example.zingakart.R;

import java.util.ArrayList;
import java.util.List;

public class ItemSearchAdapter extends BaseAdapter implements Filterable {

    public Context context;
    private List<SelectedCategoryModel> selectedCategoryModels;
    private List<SelectedCategoryModel> orig;

    public ItemSearchAdapter(Context context, List<SelectedCategoryModel> selectedCategoryModels) {
        super();
        this.context = context;
        this.selectedCategoryModels = selectedCategoryModels;
    }


    public class ItemSearchHolder
    {
        TextView name;
    }

    public Filter getFilter() {
        return new Filter() {

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

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                selectedCategoryModels.clear();
                selectedCategoryModels.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return selectedCategoryModels.size();
    }

    @Override
    public Object getItem(int position) {
        return selectedCategoryModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemSearchHolder holder;
        if(convertView==null)
        {
            convertView= LayoutInflater.from(context).inflate(R.layout.row, parent, false);
            holder=new ItemSearchHolder();
            holder.name=(TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        }
        else
        {
            holder=(ItemSearchHolder) convertView.getTag();
        }

        holder.name.setText(selectedCategoryModels.get(position).getName());

        return convertView;

    }

}