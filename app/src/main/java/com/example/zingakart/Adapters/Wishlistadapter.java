package com.example.zingakart.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.zingakart.Activities.CartActivity;
import com.example.zingakart.Activities.WishListActivity;
import com.example.zingakart.R;

import java.util.ArrayList;

public class Wishlistadapter extends RecyclerView.Adapter<Wishlistadapter.WishListViewholder> {

    Context context;
    ArrayList<String> item_name, price, im, qty;
    String total= "";
    String choice= "";
    String visibility= "";


    public Wishlistadapter(Context context, ArrayList<String> item_name, ArrayList<String> qty,ArrayList<String> price, ArrayList<String> im, String total, String visibility, String choice) {
        this.context = context;
        this.item_name = item_name;
        this.price = price;
        this.im = im;
        this.total = total;
        this.visibility = visibility;
        this.choice = choice;
        this.qty = qty;
    }


    @NonNull
    @Override
    public Wishlistadapter.WishListViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview= LayoutInflater.from(context).inflate(R.layout.adapter_wishlist,parent,false);
        return new Wishlistadapter.WishListViewholder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull Wishlistadapter.WishListViewholder holder, int position) {
        //CartViewModel cartViewModel = cartList.get(position);

        if(item_name.size()!=0)
        {

            if(visibility.equals(""))
            {
                holder.size.setVisibility(View.GONE);
                holder.qty.setVisibility(View.GONE);
                holder.tvCatName.setText(item_name.get(position));
                holder.tvCatPrice.setText("\u20B9"+price.get(position));
                Glide.with(context).load(im.get(position)).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.ivCart);

            }
            else if(visibility.equals("1"))
            {

                holder.size.setVisibility(View.VISIBLE);
                holder.qty.setVisibility(View.VISIBLE);
                holder.tvCatName.setText(item_name.get(position));
                holder.tvCatPrice.setText("\u20B9"+price.get(position));
                holder.tvqtynumber.setText(qty.get(position));
                holder.tvSize.setText(choice.toString());
                Glide.with(context).load(im.get(position)).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.ivCart);
            }

        }

        holder.btn_addcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE));
                    }


                    new AlertDialog.Builder(context)
                            .setTitle("Note")
                            .setMessage("Item Added to Cart")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                } else {
                    //deprecated in API 26
                    v.vibrate(80);
                    new AlertDialog.Builder(context)
                            .setTitle("Note")
                            .setMessage("Item Added to Cart")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .show();



                }
            }
        });

        holder.btn_buy_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CartActivity.class);
                context.startActivity(intent);
            }
        });

    }



    @Override
    public int getItemCount() {
        return item_name.size();
    }

    static class WishListViewholder extends RecyclerView.ViewHolder {
        ImageView ivCart ;
        TextView tvCatName,tvCatPrice, tvqtynumber, tvSize, size, qty ;
        Button btn_buy_now, btn_addcart;

        public WishListViewholder(@NonNull View itemView) {
            super(itemView);
            ivCart=(ImageView)itemView.findViewById(R.id.ivCart);
            tvCatName=(TextView)itemView.findViewById(R.id.tvCatName);
            tvqtynumber=(TextView)itemView.findViewById(R.id.tvqtynumber);
            tvCatPrice=(TextView)itemView.findViewById(R.id.tvCatPrice);
            btn_buy_now = (Button)itemView.findViewById(R.id.btn_buynow);
            btn_addcart = (Button)itemView.findViewById(R.id.btn_addcart);
            tvSize=(TextView)itemView.findViewById(R.id.tv_size);
            size=(TextView)itemView.findViewById(R.id.size);
            qty=(TextView)itemView.findViewById(R.id.qty);
        }

    }
    public interface OnAdapterCountListener {
        void onAdapterCountListener(int count);
    }
}
