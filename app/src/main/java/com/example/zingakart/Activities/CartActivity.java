package com.example.zingakart.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.zingakart.Adapters.CartViewAdapter;
import com.example.zingakart.Model.CartViewModel;
import com.example.zingakart.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    RecyclerView rv_Cart;
    ImageView no_items, ic_back;
    TextView tv_no_items, tv_total, tv_grand_total, tv_size;
    CartViewAdapter cartViewAdapter;
    //String item_name="";
    String item_price="";
    String image="";
    String total="";
    ArrayList<String> item_name;
    ArrayList<String> qty;
    ArrayList<String> price;
    ArrayList<String> actual_price;
    ArrayList<String> im;
    String choice = "";
    GridLayoutManager mLayoutManager;
    Button btn_payment;
    Double tot = 0.0;
    String formatted_total = "";
    LinearLayout expense_amt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        item_name = new ArrayList<>();
        Toolbar toolbar = findViewById(R.id.toolbar);
        btn_payment = findViewById(R.id.btn_payment);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        no_items = findViewById(R.id.no_items);
        tv_size = findViewById(R.id.tv_size);
        ic_back = findViewById(R.id.ic_back);
        tv_total = findViewById(R.id.tv_total);
        tv_grand_total = findViewById(R.id.tv_grand_total);
        tv_no_items = findViewById(R.id.tv_no_items);
        rv_Cart = findViewById(R.id.rv_cart);
        expense_amt = findViewById(R.id.expenseAmt);


        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mLayoutManager = new GridLayoutManager(CartActivity.this, 1);
        rv_Cart.setLayoutManager(mLayoutManager);



        Intent intent = getIntent();
        item_name = intent.getStringArrayListExtra("item_name");
        qty = intent.getStringArrayListExtra("item_qty");
        price = intent.getStringArrayListExtra("item_price");
        im = intent.getStringArrayListExtra("image");
        total = getIntent().getStringExtra("total_price");
        actual_price = intent.getStringArrayListExtra("price");
        choice = intent.getStringExtra("choice");



        btn_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this, LoginActivity.class);
                intent.putExtra("total_price",tot.toString());
                startActivity(intent);
            }
        });

        //Glide.with(this).load(image).diskCacheStrategy(DiskCacheStrategy.ALL).into(image);
        cartViewAdapter = new CartViewAdapter(CartActivity.this, item_name, qty, price, im, total, choice);
        rv_Cart.setAdapter(cartViewAdapter);

        for (int i = 0; i < item_name.size(); i++) {
            tot = tot + (Integer.parseInt(qty.get(i)) * (Integer.parseInt(price.get(i))));
        }

        formatted_total = String.format("%.2f", tot);
        tv_total.setText(formatted_total);
        tv_grand_total.setText(formatted_total);



    }

    @Override
    public void onBackPressed() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("item_name",item_name);
        bundle.putSerializable("item_qty",qty);
        bundle.putSerializable("item_price",price);
        bundle.putSerializable("image",im);
        super.onBackPressed();
    }

}