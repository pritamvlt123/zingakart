package com.example.zingakart.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.zingakart.Activities.CartActivity;
import com.example.zingakart.Model.CartViewModel;
import com.example.zingakart.R;

import java.util.ArrayList;
import java.util.List;

public class CartViewAdapter extends RecyclerView.Adapter<CartViewAdapter.CartViewHolder> {

    Context context;
    ArrayList<String> item_name, qty, price, im;
    String choice = "";
    String total= "";

    public CartViewAdapter(Context context, ArrayList<String> item_name, ArrayList<String> qty, ArrayList<String> price, ArrayList<String> im, String total, String choice) {
        this.context = context;
        this.item_name = item_name;
        this.qty = qty;
        this.price = price;
        this.im = im;
        this.total = total;
        this.choice = choice;
    }


    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview= LayoutInflater.from(context).inflate(R.layout.adapter_cart_item,parent,false);
        return new CartViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        //CartViewModel cartViewModel = cartList.get(position);

        if(item_name.size()!=0)
        {
            holder.tvCatName.setText(item_name.get(position));
            holder.tvqtynumber.setText(qty.get(position));
            holder.tvCatPrice.setText("\u20B9"+price.get(position));
            holder.tvSize.setText(choice.toString());

            Glide.with(context).load(im.get(position)).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.ivCart);

        }

    }

    @Override
    public int getItemCount() {
        return item_name.size();
    }



    class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCart ;
        TextView tvCatName,tvqtynumber,tvCatPrice,tvSize;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCart=(ImageView)itemView.findViewById(R.id.ivCart);
            tvCatName=(TextView)itemView.findViewById(R.id.tvCatName);
            tvqtynumber=(TextView)itemView.findViewById(R.id.tvqtynumber);
            tvCatPrice=(TextView)itemView.findViewById(R.id.tvCatPrice);
            tvSize=(TextView)itemView.findViewById(R.id.tv_size);

        }
    }
}

