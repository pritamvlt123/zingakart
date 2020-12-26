package com.example.zingakart.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Html;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.zingakart.APIClient.APIClient;
import com.example.zingakart.APIClient.Api_Client;
import com.example.zingakart.APIClient.User_Service;
import com.example.zingakart.Adapters.ExpandableListAdapter;
import com.example.zingakart.Adapters.ItemViewAdapter;
import com.example.zingakart.Model.ItemViewModel;
import com.example.zingakart.Model.MenuModel;
import com.example.zingakart.Model.PinCodeModel;
import com.example.zingakart.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ItemDescriptionActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LocationListener {

    private static final String TAG = "this";
    private AppBarConfiguration mAppBarConfiguration;
    ExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    List<MenuModel> headerList = new ArrayList<>();
    HashMap<MenuModel, List<MenuModel>> childList = new HashMap<>();
    private ItemViewAdapter adapter;
    private List<ItemViewModel> itemViewModelList;
    ImageView cart, profile_img, iv_checklocation;
    TextInputEditText edit_city;
    ProgressBar progressBar;
    Button btn_check_location;
    SearchView searchView;
    Spinner spinner_size;
    String first_name = "";
    String city_name = "";
    String postal_code = "";
    TextView tv_username, tv_itemname, tv_price, tv_discount_price, tv_item_category, tv_long_description,
            tv_short_description, tv_state, tv_pincode, tv_available, tv_cod, text_size;
    private NavigationView navigationView;
    SharedPreferences sharedPreferences;
    private DrawerLayout drawer;
    FloatingActionButton fab;
    public static final String MyPREFERENCES = "MyPrefs";
    ImageView add_favorites, item_image, item_add_cart, done;
    Boolean OnClick = false;
    Button btn_addcart, btn_buynow;
    String item_name = "";
    String item_price = "";
    String price = "";
    String discount_price = "";
    String item_category = "";
    String image = "";
    String long_description = "";
    String short_description = "";
    String total = "";
    String num = "";
    String sum = "";
    String pincode = "";
    String type = "";
    String name = "";
    String visibility = "";
    ElegantNumberButton button;
    List<String> item = new ArrayList<String>();
    List<String> qty = new ArrayList<String>();
    List<String> pri = new ArrayList<String>();
    List<String> im = new ArrayList<String>();
    String choice = "";
    ArrayList<String> choiceList;
    ArrayList<String> attributes;
    RecyclerView recyclerView;
    User_Service userService;
    boolean homeShouldOpenDrawer; // flag for onOptionsItemSelected
    int temp_price = 0;
    int final_price = 0;
    Vibrator v;
    ConstraintLayout constraintLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_description);

        add_favorites = findViewById(R.id.im_favorite);
        iv_checklocation = findViewById(R.id.iv_checklocation);
        tv_itemname = findViewById(R.id.tv_item_name);
        spinner_size = findViewById(R.id.spinner_size);
        text_size = findViewById(R.id.text_size);
        tv_long_description = findViewById(R.id.tv_long_description);
        tv_short_description = findViewById(R.id.tv_short_description);
        done = findViewById(R.id.done);
        tv_cod = findViewById(R.id.tv_cod);
        tv_state = findViewById(R.id.tv_state);
        tv_available = findViewById(R.id.tv_available);
        tv_pincode = findViewById(R.id.tv_pincode);
        btn_addcart = findViewById(R.id.btn_addcart);
        btn_buynow = findViewById(R.id.btn_buynow);
        cart = findViewById(R.id.cart);
        item_add_cart = findViewById(R.id.im_add_item);
        tv_item_category = findViewById(R.id.tv_item_category);
        tv_price = findViewById(R.id.tv_actual_price);
        button = findViewById(R.id.number_button);
        tv_discount_price = findViewById(R.id.tv_discounted_price);
        constraintLayout = findViewById(R.id.constraint);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        prepareMenuData();

        tv_username = findViewById(R.id.tv_username);
        getVariablePrice();

        if (getIntent().getExtras() != null) {
        item_name = getIntent().getStringExtra("item_name");
        item_price = getIntent().getStringExtra("item_price");
        price = getIntent().getStringExtra("price");
        discount_price = getIntent().getStringExtra("discount_price");
        item_category = getIntent().getStringExtra("item_category");
        long_description = getIntent().getStringExtra("long_description");
        short_description = getIntent().getStringExtra("short_description");
        city_name = getIntent().getStringExtra("city_name");
        postal_code = getIntent().getStringExtra("postal_code");
        type = getIntent().getStringExtra("type");
        name = getIntent().getStringExtra("name");
        attributes = getIntent().getStringArrayListExtra("attributes");

        if (name.equalsIgnoreCase("Size")) {
            text_size.setVisibility(View.VISIBLE);
            spinner_size.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, attributes);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_size.setAdapter(adapter);

            spinner_size.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    // your code here
                    if (spinner_size.getSelectedItemPosition() >= 0) {
                        choice = spinner_size.getItemAtPosition(position).toString();
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

                 });
            }
        }
        else if (name.equalsIgnoreCase("Seller"))
        {
            text_size.setVisibility(View.GONE);
            spinner_size.setVisibility(View.GONE);
        }
        else
        {
            text_size.setVisibility(View.GONE);
            spinner_size.setVisibility(View.GONE);
        }

        if (item_price.equals("") && discount_price.equals("")) {
            tv_discount_price.setText("\u20B9" + price);
            tv_price.setVisibility(View.GONE);


            btn_addcart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    item_add_cart.setVisibility(View.VISIBLE);
                    item.add(item_name);
                    pri.add(String.valueOf(price));
                    im.add(image);
                    qty.add(button.getNumber());
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            v.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE));
                        }


                        new AlertDialog.Builder(ItemDescriptionActivity.this)
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
                        new AlertDialog.Builder(ItemDescriptionActivity.this)
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

            btn_buynow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    item_add_cart.setVisibility(View.VISIBLE);
                    item.add(item_name);
                    pri.add(String.valueOf(price));
                    im.add(image);
                    qty.add(button.getNumber());
                    Intent intent = new Intent(ItemDescriptionActivity.this, CartActivity.class);
                    intent.putStringArrayListExtra("item_name", (ArrayList<String>) item);
                    intent.putStringArrayListExtra("item_qty", (ArrayList<String>) qty);
                    intent.putStringArrayListExtra("item_price", (ArrayList<String>) pri);
                    intent.putStringArrayListExtra("image", (ArrayList<String>) im);
                    intent.putExtra("choice", choice);
                    intent.putExtra("total_price", sum);
                    startActivity(intent);
                }
            });


            cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Slide slide = new Slide();
                    slide.setSlideEdge(Gravity.START);
                    TransitionManager.beginDelayedTransition(constraintLayout, slide);
                    Intent intent = new Intent(ItemDescriptionActivity.this, CartActivity.class);
                    intent.putStringArrayListExtra("item_name", (ArrayList<String>) item);
                    intent.putStringArrayListExtra("item_qty", (ArrayList<String>) qty);
                    intent.putStringArrayListExtra("item_price", (ArrayList<String>) pri);
                    intent.putStringArrayListExtra("image", (ArrayList<String>) im);
                    intent.putExtra("choice", choice);
                    intent.putExtra("total_price", sum);
                    startActivity(intent);
                }
            });


        } else {
            temp_price = (int) Float.parseFloat(item_price.toString());
            tv_price.setText("\u20B9" + item_price);
            tv_discount_price.setText("\u20B9" + discount_price);
            tv_discount_price.setPaintFlags(tv_discount_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            btn_addcart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    item_add_cart.setVisibility(View.VISIBLE);
                    item.add(item_name);
                    pri.add(String.valueOf(temp_price));
                    im.add(image);
                    qty.add(button.getNumber());
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            v.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE));
                        }


                        new AlertDialog.Builder(ItemDescriptionActivity.this)
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
                        new AlertDialog.Builder(ItemDescriptionActivity.this)
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

            btn_buynow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    item_add_cart.setVisibility(View.VISIBLE);
                    item.add(item_name);
                    pri.add(String.valueOf(temp_price));
                    im.add(image);
                    qty.add(button.getNumber());
                    Intent intent = new Intent(ItemDescriptionActivity.this, CartActivity.class);
                    intent.putStringArrayListExtra("item_name", (ArrayList<String>) item);
                    intent.putStringArrayListExtra("item_qty", (ArrayList<String>) qty);
                    intent.putStringArrayListExtra("item_price", (ArrayList<String>) pri);
                    intent.putStringArrayListExtra("image", (ArrayList<String>) im);
                    intent.putExtra("total_price", sum);
                    startActivity(intent);
                }
            });


            cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ItemDescriptionActivity.this, CartActivity.class);
                    intent.putStringArrayListExtra("item_name", (ArrayList<String>) item);
                    intent.putStringArrayListExtra("item_qty", (ArrayList<String>) qty);
                    intent.putStringArrayListExtra("item_price", (ArrayList<String>) pri);
                    intent.putStringArrayListExtra("image", (ArrayList<String>) im);
                    intent.putExtra("choice", choice);
                    intent.putExtra("total_price", sum);
                    startActivity(intent);
                }
            });


        }

        String l_description = String.valueOf(Html.fromHtml(long_description));
        String s_description = String.valueOf(Html.fromHtml(short_description));


        tv_long_description.setText(l_description);
        tv_short_description.setText(s_description);

        tv_state.setText(city_name);


        iv_checklocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert();
            }
        });


        image = getIntent().getStringExtra("image");
        item_image = findViewById(R.id.item_image);
        Glide.with(this).load(image).diskCacheStrategy(DiskCacheStrategy.ALL).into(item_image);

        tv_itemname.setText(item_name);

        tv_item_category.setText(item_category);

        add_favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isFavourite = true;
                if (isFavourite) {
                    add_favorites.setBackgroundResource(R.drawable.favorite);
                    Toast.makeText(ItemDescriptionActivity.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                    isFavourite = false;
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            v.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE));
                        }
                    }
                    if (type.equals("simple")) {
                        item.add(item_name);
                        pri.add(String.valueOf(temp_price));
                        im.add(image);
                        qty.add(button.getNumber());
                        Intent intent = new Intent(ItemDescriptionActivity.this, WishListActivity.class);
                        intent.putStringArrayListExtra("item_name", (ArrayList<String>) item);
                        intent.putStringArrayListExtra("item_price", (ArrayList<String>) pri);
                        intent.putStringArrayListExtra("item_qty", (ArrayList<String>) qty);
                        intent.putExtra("choice", choice);
                        intent.putStringArrayListExtra("image", (ArrayList<String>) im);
                        intent.putExtra("visible", "1");
                        intent.putExtra("total_price", sum);
                        startActivity(intent);
                    } else if (type.equals("variable")) {
                        item.add(item_name);
                        pri.add(String.valueOf(temp_price));
                        im.add(image);
                        qty.add(button.getNumber());
                        Intent intent = new Intent(ItemDescriptionActivity.this, WishListActivity.class);
                        intent.putStringArrayListExtra("item_name", (ArrayList<String>) item);
                        intent.putStringArrayListExtra("item_price", (ArrayList<String>) pri);
                        intent.putStringArrayListExtra("item_qty", (ArrayList<String>) qty);
                        intent.putExtra("choice", choice);
                        intent.putStringArrayListExtra("image", (ArrayList<String>) im);
                        intent.putExtra("visible", "1");
                        intent.putExtra("total_price", sum);
                        startActivity(intent);
                    }

                } else {
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(80);
                    add_favorites.setBackgroundResource(R.drawable.remove_favorite);
                    Toast.makeText(ItemDescriptionActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    isFavourite = true;
                }

            }
        });

        button.setOnClickListener(new ElegantNumberButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num = button.getNumber();
                sum = String.valueOf(Integer.valueOf(num) * Integer.valueOf(temp_price));
                tv_price.setText("\u20B9" + sum);
            }
        });


        expandableListView = findViewById(R.id.expandableListView);
        populateExpandableList();

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_item_fragment)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_item_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        enableViews(true);

        tv_pincode.setText(postal_code);

    }

    private void getVariablePrice() {

    }

    private void enableViews(boolean enable) {
        if (enable) {
            // Enables back button icon
            // passing null or 0 brings back the <- icon
            getSupportActionBar().setHomeAsUpIndicator(null);
            homeShouldOpenDrawer = false;
        } else {
            // Enables burger icon
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
            homeShouldOpenDrawer = true;
        }

    }


    private void showAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ItemDescriptionActivity.this, R.style.CustomDialogTheme);
        View view1 = LayoutInflater.from(this).inflate(R.layout.check_location, null);
        edit_city = (view1.findViewById(R.id.edit_city));
        progressBar = (view1.findViewById(R.id.pbHeaderProgress));
        btn_check_location = (view1.findViewById(R.id.btn_check_location));

        btn_check_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(ItemDescriptionActivity.this, "Check location API", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.VISIBLE);
                JsonObject js = new JsonObject();
                js.addProperty("consumer_key", "ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc");
                js.addProperty("consumer_secret", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8");
                js.addProperty("pincode", edit_city.getText().toString());


                userService = APIClient.getClient().create(User_Service.class);
                Call<PinCodeModel> call = userService.checkPincode("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", edit_city.getText().toString());
                call.enqueue(new Callback<PinCodeModel>() {
                    @Override
                    public void onResponse(Call<PinCodeModel> call, Response<PinCodeModel> response) {
                        if (response.body().getStatus().equals("success")) {
                            progressBar.setVisibility(View.GONE);
                            response.body().toString();
                            Toast.makeText(ItemDescriptionActivity.this, "Available at this location", Toast.LENGTH_SHORT).show();
                            pincode = edit_city.getText().toString();
                            tv_pincode.setText(pincode);
                            done.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_done_24));
                            tv_cod.setText("Cash on delivery available");
                            btn_addcart.getBackground().setColorFilter(ContextCompat.getColor(ItemDescriptionActivity.this, R.color.btn_cart), PorterDuff.Mode.MULTIPLY);
                            btn_buynow.getBackground().setColorFilter(ContextCompat.getColor(ItemDescriptionActivity.this, R.color.btn_buy), PorterDuff.Mode.MULTIPLY);
                            btn_addcart.setEnabled(true);
                            btn_buynow.setEnabled(true);

                        } else if (response.body().getStatus().equals("error")) {
                            {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(ItemDescriptionActivity.this, "Not available at this location", Toast.LENGTH_SHORT).show();
                                String na = response.body().getMessage();
                                tv_available.setText(na);
                                pincode = edit_city.getText().toString();
                                tv_pincode.setText(pincode);
                                done.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_clear_24));
                                tv_cod.setText("Not available for delivery");
                                btn_addcart.getBackground().setColorFilter(ContextCompat.getColor(ItemDescriptionActivity.this, R.color.quantum_grey_600), PorterDuff.Mode.MULTIPLY);
                                btn_buynow.getBackground().setColorFilter(ContextCompat.getColor(ItemDescriptionActivity.this, R.color.quantum_grey_600), PorterDuff.Mode.MULTIPLY);
                                btn_addcart.setEnabled(false);
                                btn_buynow.setEnabled(false);

                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<PinCodeModel> call, Throwable t) {
                        Toast.makeText(ItemDescriptionActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });

            }

        });

        builder.setView(view1);
        builder.setCancelable(true);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_item_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (homeShouldOpenDrawer) {
                    drawer.openDrawer(GravityCompat.START);
                } else {
                    onBackPressed();
                }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    private void prepareMenuData() {

        MenuModel menuModel = new MenuModel("Home", true, false); //Menu of Android Tutorial. No sub menus
        headerList.add(menuModel);

        if (!menuModel.hasChildren) {
            childList.put(menuModel, null);

        }

        MenuModel menuModel1 = new MenuModel("Clothing", true, true); //Menu of Java Tutorials
        headerList.add(menuModel1);
        List<MenuModel> childModelsList = new ArrayList<>();
        MenuModel childModel = new MenuModel("Men's Clothing", false, false);
        childModelsList.add(childModel);

        childModel = new MenuModel("Men's Footwear", false, false);
        childModelsList.add(childModel);

        childModel = new MenuModel("Men's Accessories", false, false);
        childModelsList.add(childModel);

        childModel = new MenuModel("Women's Clothing", false, false);
        childModelsList.add(childModel);

        childModel = new MenuModel("Women's Ethnic Wear", false, false);
        childModelsList.add(childModel);

        childModel = new MenuModel("Women's Western Wear", false, false);
        childModelsList.add(childModel);

        childModel = new MenuModel("Women's Lingerie", false, false);
        childModelsList.add(childModel);

        childModel = new MenuModel("Women's Footwear", false, false);
        childModelsList.add(childModel);

        childModel = new MenuModel("Women's Jewellery", false, false);
        childModelsList.add(childModel);

        childModel = new MenuModel("Women's Accessories", false, false);
        childModelsList.add(childModel);


        if (menuModel1.hasChildren) {
            Log.d("API123", "here");
            childList.put(menuModel1, childModelsList);
        }

        MenuModel menuModel2 = new MenuModel("Baby & Kids", true, true); //Menu of Java Tutorials
        headerList.add(menuModel2);
        List<MenuModel> childModelsList1 = new ArrayList<>();
        MenuModel childModel1 = new MenuModel("Kid's Clothing", false, false);
        childModelsList1.add(childModel1);

        childModel1 = new MenuModel("Boy's Clothing", false, false);
        childModelsList1.add(childModel1);

        childModel1 = new MenuModel("Girl's Clothing", false, false);
        childModelsList1.add(childModel1);

        childModel1 = new MenuModel("Kid's Accessories", false, false);
        childModelsList1.add(childModel1);

        childModel1 = new MenuModel("Toys", false, false);
        childModelsList1.add(childModel1);


        if (menuModel2.hasChildren) {
            Log.d("API123", "here");
            childList.put(menuModel2, childModelsList1);
        }


        MenuModel menuModel3 = new MenuModel("Home & Kitchen", true, true); //Menu of Java Tutorials
        headerList.add(menuModel3);
        List<MenuModel> childModelsList2 = new ArrayList<>();
        MenuModel childModel2 = new MenuModel("Kitchen & Dining", false, false);
        childModelsList2.add(childModel2);

        childModel2 = new MenuModel("Home Appliances", false, false);
        childModelsList2.add(childModel2);

        childModel2 = new MenuModel("Home Furnishing", false, false);
        childModelsList2.add(childModel2);

        childModel2 = new MenuModel("Tools & Hardware", false, false);
        childModelsList2.add(childModel2);

        childModel2 = new MenuModel("Lightning Solutions", false, false);
        childModelsList2.add(childModel2);

        childModel2 = new MenuModel("Decor", false, false);
        childModelsList2.add(childModel2);

        if (menuModel3.hasChildren) {
            Log.d("API123", "here");
            childList.put(menuModel3, childModelsList2);
        }

        MenuModel menuModel4 = new MenuModel("Electronics", true, true); //Menu of Java Tutorials
        headerList.add(menuModel4);
        List<MenuModel> childModelsList3 = new ArrayList<>();
        MenuModel childModel3 = new MenuModel("Mobiles", false, false);
        childModelsList3.add(childModel3);

        childModel3 = new MenuModel("Mobiles & Tablet Accessories", false, false);
        childModelsList3.add(childModel3);

        childModel3 = new MenuModel("Computer Accessories", false, false);
        childModelsList3.add(childModel3);


        if (menuModel4.hasChildren) {
            Log.d("API123", "here");
            childList.put(menuModel4, childModelsList3);
        }


        MenuModel menuModel5 = new MenuModel("Sports & Fitness", true, true); //Menu of Java Tutorials
        headerList.add(menuModel5);
        List<MenuModel> childModelsList4 = new ArrayList<>();
        MenuModel childModel4 = new MenuModel("Healthcare", false, false);
        childModelsList4.add(childModel4);


        if (menuModel5.hasChildren) {
            Log.d("API123", "here");
            childList.put(menuModel5, childModelsList4);
        }

        MenuModel menuModel6 = new MenuModel("Tea & Fitness", true, true); //Menu of Java Tutorials
        headerList.add(menuModel6);
        List<MenuModel> childModelsList5 = new ArrayList<>();
        MenuModel childModel5 = new MenuModel("By Region", false, false);
        childModelsList5.add(childModel5);
        childModel5 = new MenuModel("By Flush", false, false);
        childModelsList5.add(childModel5);

        if (menuModel6.hasChildren) {
            Log.d("API123", "here");
            childList.put(menuModel6, childModelsList5);
        }


        MenuModel menuModel7 = new MenuModel("Daily Essentials", true, true); //Menu of Java Tutorials
        headerList.add(menuModel7);
        List<MenuModel> childModelsList6 = new ArrayList<>();
        MenuModel childModel6 = new MenuModel("Daily Needs", false, false);
        childModelsList6.add(childModel6);


        if (menuModel6.hasChildren) {
            Log.d("API123", "here");
            childList.put(menuModel7, childModelsList6);
        }

    }

    private void populateExpandableList() {

        expandableListAdapter = new ExpandableListAdapter(this, headerList, childList);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if (headerList.get(groupPosition).isGroup) {
                    if (!headerList.get(groupPosition).hasChildren) {
                        //onBackPressed();
                        Toast.makeText(ItemDescriptionActivity.this, "Position" + groupPosition, Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(ItemDescriptionActivity.this, "Position" + groupPosition, Toast.LENGTH_SHORT).show();

                }

                return false;
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                if (childList.get(headerList.get(groupPosition)) != null) {
                    MenuModel model = childList.get(headerList.get(groupPosition)).get(childPosition);
                    //onBackPressed();
                    Toast.makeText(ItemDescriptionActivity.this, "Position" + childPosition, Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}