package com.example.zingakart.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Html;
import android.util.Log;
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
import com.example.zingakart.Adapters.CartViewAdapter;
import com.example.zingakart.Adapters.ExpandableListAdapter;
import com.example.zingakart.Adapters.ItemViewAdapter;
import com.example.zingakart.Adapters.SubCategoryModelAdapter;
import com.example.zingakart.Adapters.Wishlistadapter;
import com.example.zingakart.Helper.RecylerViewClickInterface;
import com.example.zingakart.Model.ItemViewModel;
import com.example.zingakart.Model.MenuModel;
import com.example.zingakart.Model.PinCodeModel;
import com.example.zingakart.Model.SelectedCategoryModel;
import com.example.zingakart.Model.SubCategoryModel;
import com.example.zingakart.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WishListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "this";
    private AppBarConfiguration mAppBarConfiguration;
    ExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    List<MenuModel> headerList = new ArrayList<>();
    HashMap<MenuModel, List<MenuModel>> childList = new HashMap<>();
    ItemViewAdapter itemViewAdapter;
    private List<ItemViewModel> itemViewModelList;
    private List<SelectedCategoryModel> selectedCategoryModelList;
    private List<SubCategoryModel> subCategoryModelList;
    ImageView cart, profile_img, iv_checklocation;
    TextInputEditText edit_city;
    ProgressBar progressBar;
    String city_name = "";
    String postal_code = "";
    TextView tv_username, tv_no_items;
    private NavigationView navigationView;
    SharedPreferences sharedPreferences;
    private DrawerLayout drawer;
    FloatingActionButton fab;
    public static final String MyPREFERENCES = "MyPrefs";
    ImageView item_add_cart, no_items;
    Boolean OnClick = false;
    Button btn_addcart, btn_buynow;
    String image = "";
    String total = "";
    String visibility = "";
    String num = "";
    String sum = "";
    String pincode = "";
    SubCategoryModelAdapter subCategoryModelAdapter;
    ElegantNumberButton button;
    String choice = "";
    ArrayList<String> choiceList;
    ArrayList<String> attributes;
    ArrayList<String> price;
    ArrayList<String> item_name;
    ArrayList<String> qty;
    ArrayList<String> pri;
    ArrayList<String> im;
    User_Service userService;
    boolean homeShouldOpenDrawer; // flag for onOptionsItemSelected
    Wishlistadapter wishlistadapter;
    RecyclerView rv_Cart;
    RecyclerView recyclerView;
    GridLayoutManager mLayoutManager;
    private int wishListsize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);
        item_name = new ArrayList<>();
        tv_username = findViewById(R.id.tv_username);
        cart = findViewById(R.id.cart);
        item_add_cart = findViewById(R.id.im_add_item);
        rv_Cart = findViewById(R.id.recyclerview);
        progressBar = findViewById(R.id.pbHeaderProgress);
        no_items = findViewById(R.id.no_items);
        tv_no_items = findViewById(R.id.tv_no_items);

        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       // prepareMenuData();


        mLayoutManager = new GridLayoutManager(WishListActivity.this, 1);
        rv_Cart.setLayoutManager(mLayoutManager);




        Intent intent = getIntent();
        item_name = intent.getStringArrayListExtra("item_name");
        price = intent.getStringArrayListExtra("item_price");
        im = intent.getStringArrayListExtra("image");
        qty = intent.getStringArrayListExtra("item_qty");
        total = getIntent().getStringExtra("total_price");
        choice = intent.getStringExtra("choice");
        visibility = getIntent().getStringExtra("visible");


        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(WishListActivity.this, CartActivity.class);
//                intent.putStringArrayListExtra("item_qty", (ArrayList<String>) qty);
//                intent.putStringArrayListExtra("image", (ArrayList<String>) im);
//                intent.putStringArrayListExtra("item_price", (ArrayList<String>) pri);
//                intent.putExtra("total_price", sum);
//                startActivity(intent);
                no_items.setVisibility(View.VISIBLE);
                tv_no_items.setVisibility(View.VISIBLE);
                tv_no_items.setText("Oops!! \nNo Items in Cart");
            }
        });


        expandableListView = findViewById(R.id.expandableListView);
        //populateExpandableList();

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_wish_list)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_wish_list);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        enableViews(true);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        Intent intent = new Intent(WishListActivity.this, WishListActivity.class);
                        startActivity(intent);
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        wishlistadapter = new Wishlistadapter(WishListActivity.this, item_name, qty, price, im, total, visibility, choice);
        rv_Cart.setAdapter(wishlistadapter);
        progressBar.setVisibility(View.GONE);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_wish_list);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

   /* private void prepareMenuData() {
        MenuModel menuModel1 = new MenuModel("Men", true, true); //Menu of Java Tutorials
        headerList.add(menuModel1);
        List<MenuModel> childModelsList = new ArrayList<>();
        MenuModel childModel = new MenuModel("Men's Clothing", false, false);
        childModelsList.add(childModel);

        childModel = new MenuModel("Men's Footwear", false, false);
        childModelsList.add(childModel);

        childModel = new MenuModel("Men's Accessories", false, false);
        childModelsList.add(childModel);


        if (menuModel1.hasChildren) {
            Log.d("API123", "here");
            childList.put(menuModel1, childModelsList);
        }

        MenuModel menuModel2 = new MenuModel("Women", true, true); //Menu of Java Tutorials
        headerList.add(menuModel2);
        List<MenuModel> childModelsList1 = new ArrayList<>();
        MenuModel childModel1 = new MenuModel("Women's Ethnic Wear", false, false);
        childModelsList1.add(childModel1);

        childModel1 = new MenuModel("Women's Western Wear", false, false);
        childModelsList1.add(childModel1);

        childModel1 = new MenuModel("Women's Lingerie", false, false);
        childModelsList1.add(childModel1);

        childModel1 = new MenuModel("Women's Footwear", false, false);
        childModelsList1.add(childModel1);

        childModel1 = new MenuModel("Women's Jewellery", false, false);
        childModelsList1.add(childModel1);

        childModel1 = new MenuModel("Women's Accessories", false, false);
        childModelsList1.add(childModel1);

        childModel1 = new MenuModel("Women's Winter Collection", false, false);
        childModelsList1.add(childModel1);

        if (menuModel2.hasChildren) {
            Log.d("API123", "here");
            childList.put(menuModel2, childModelsList1);
        }

        MenuModel menuModel3 = new MenuModel("Baby & Kids", true, true); //Menu of Java Tutorials
        headerList.add(menuModel3);
        List<MenuModel> childModelsList2 = new ArrayList<>();
        MenuModel childModel2 = new MenuModel("Kid's Clothing", false, false);
        childModelsList2.add(childModel2);

        childModel2 = new MenuModel("Boy's Clothing", false, false);
        childModelsList2.add(childModel2);

        childModel2 = new MenuModel("Girl's Clothing", false, false);
        childModelsList2.add(childModel2);

        childModel2 = new MenuModel("Kid's Accessories", false, false);
        childModelsList2.add(childModel2);

        childModel2 = new MenuModel("Toys", false, false);
        childModelsList2.add(childModel2);


        if (menuModel3.hasChildren) {
            Log.d("API123", "here");
            childList.put(menuModel3, childModelsList2);
        }


        MenuModel menuModel4 = new MenuModel("Home & Kitchen", true, true); //Menu of Java Tutorials
        headerList.add(menuModel4);
        List<MenuModel> childModelsList3 = new ArrayList<>();
        MenuModel childModel3 = new MenuModel("Kitchen & Dining", false, false);
        childModelsList3.add(childModel3);

        childModel3 = new MenuModel("Home Appliances", false, false);
        childModelsList3.add(childModel3);

        childModel3 = new MenuModel("Home Furnishing", false, false);
        childModelsList3.add(childModel3);

        childModel3 = new MenuModel("Tools & Hardware", false, false);
        childModelsList3.add(childModel3);

        childModel3 = new MenuModel("Lightning Solutions", false, false);
        childModelsList3.add(childModel3);

        childModel3 = new MenuModel("Decor", false, false);
        childModelsList3.add(childModel3);

        if (menuModel4.hasChildren) {
            Log.d("API123", "here");
            childList.put(menuModel4, childModelsList3);
        }

        MenuModel menuModel5 = new MenuModel("Electronics", true, true); //Menu of Java Tutorials
        headerList.add(menuModel5);
        List<MenuModel> childModelsList4 = new ArrayList<>();
        MenuModel childModel4 = new MenuModel("Mobiles", false, false);
        childModelsList4.add(childModel4);

        childModel4 = new MenuModel("Mobiles & Tablet Accessories", false, false);
        childModelsList4.add(childModel4);

        childModel4 = new MenuModel("Computer Accessories", false, false);
        childModelsList4.add(childModel4);


        if (menuModel5.hasChildren) {
            Log.d("API123", "here");
            childList.put(menuModel5, childModelsList4);
        }


        MenuModel menuModel6 = new MenuModel("Sports & Fitness", true, true); //Menu of Java Tutorials
        headerList.add(menuModel6);
        List<MenuModel> childModelsList5 = new ArrayList<>();
        MenuModel childModel5 = new MenuModel("Healthcare", false, false);
        childModelsList5.add(childModel5);


        if (menuModel6.hasChildren) {
            Log.d("API123", "here");
            childList.put(menuModel6, childModelsList5);
        }

        MenuModel menuModel7 = new MenuModel("Tea & Coffee", true, true); //Menu of Java Tutorials
        headerList.add(menuModel7);
        List<MenuModel> childModelsList6 = new ArrayList<>();
        MenuModel childModel6 = new MenuModel("By Region", false, false);
        childModelsList6.add(childModel6);
        childModel6 = new MenuModel("By Flush", false, false);
        childModelsList6.add(childModel6);

        if (menuModel7.hasChildren) {
            Log.d("API123", "here");
            childList.put(menuModel7, childModelsList6);
        }


        MenuModel menuModel9 = new MenuModel("Daily Essentials", true, true); //Menu of Java Tutorials
        headerList.add(menuModel9);
        List<MenuModel> childModelsList8 = new ArrayList<>();
        MenuModel childModel8 = new MenuModel("Chocolate", false, false);
        childModelsList8.add(childModel8);
        childModel8 = new MenuModel("Snacks", false, false);
        childModelsList8.add(childModel8);

        if (menuModel9.hasChildren) {
            Log.d("API123", "here");
            childList.put(menuModel9, childModelsList8);
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
                    }
                    //Toast.makeText(WishListActivity.this, "Position" + groupPosition, Toast.LENGTH_SHORT).show();
                    if (groupPosition == 2) {
                        //onBackPressed();
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        //Toast.makeText(WishListActivity.this, "Position" + groupPosition, Toast.LENGTH_SHORT).show();
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SelectedCategoryModel>> call = userService.getSecondLevelCat("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "17");
                        call.enqueue(new Callback<List<SelectedCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SelectedCategoryModel>> call, Response<List<SelectedCategoryModel>> response) {
                                selectedCategoryModelList = response.body();
                                itemViewAdapter = new ItemViewAdapter(getApplicationContext(), selectedCategoryModelList, WishListActivity.this, WishListActivity.this);

                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(itemViewAdapter);
                                itemViewAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SelectedCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 7) {
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        //Toast.makeText(WishListActivity.this, "Position" + groupPosition, Toast.LENGTH_SHORT).show();
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SelectedCategoryModel>> call = userService.getSecondLevelCat("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "626");
                        call.enqueue(new Callback<List<SelectedCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SelectedCategoryModel>> call, Response<List<SelectedCategoryModel>> response) {
                                selectedCategoryModelList = response.body();
                                itemViewAdapter = new ItemViewAdapter(getApplicationContext(), selectedCategoryModelList, WishListActivity.this, WishListActivity.this);

                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(itemViewAdapter);
                                itemViewAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SelectedCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else if (groupPosition == 4) {
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        //Toast.makeText(WishListActivity.this, "Position" + groupPosition, Toast.LENGTH_SHORT).show();
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SelectedCategoryModel>> call = userService.getSecondLevelCat("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "519");
                        call.enqueue(new Callback<List<SelectedCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SelectedCategoryModel>> call, Response<List<SelectedCategoryModel>> response) {
                                selectedCategoryModelList = response.body();
                                itemViewAdapter = new ItemViewAdapter(getApplicationContext(), selectedCategoryModelList, WishListActivity.this, WishListActivity.this);

                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(itemViewAdapter);
                                itemViewAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SelectedCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else if (groupPosition == 5) {
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        //Toast.makeText(WishListActivity.this, "Position" + groupPosition, Toast.LENGTH_SHORT).show();
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SelectedCategoryModel>> call = userService.getSecondLevelCat("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "548");
                        call.enqueue(new Callback<List<SelectedCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SelectedCategoryModel>> call, Response<List<SelectedCategoryModel>> response) {
                                selectedCategoryModelList = response.body();
                                itemViewAdapter = new ItemViewAdapter(getApplicationContext(), selectedCategoryModelList, WishListActivity.this, WishListActivity.this);

                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(itemViewAdapter);
                                itemViewAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SelectedCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 3) {

                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        //Toast.makeText(WishListActivity.this, "Position" + groupPosition, Toast.LENGTH_SHORT).show();
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SelectedCategoryModel>> call = userService.getSecondLevelCat("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "380");
                        call.enqueue(new Callback<List<SelectedCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SelectedCategoryModel>> call, Response<List<SelectedCategoryModel>> response) {
                                selectedCategoryModelList = response.body();
                                itemViewAdapter = new ItemViewAdapter(getApplicationContext(), selectedCategoryModelList, WishListActivity.this, WishListActivity.this);

                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(itemViewAdapter);
                                itemViewAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SelectedCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 0) {

                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        //Toast.makeText(WishListActivity.this, "Position" + groupPosition, Toast.LENGTH_SHORT).show();
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SelectedCategoryModel>> call = userService.getSecondLevelCat("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "270");
                        call.enqueue(new Callback<List<SelectedCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SelectedCategoryModel>> call, Response<List<SelectedCategoryModel>> response) {
                                selectedCategoryModelList = response.body();
                                itemViewAdapter = new ItemViewAdapter(getApplicationContext(), selectedCategoryModelList, WishListActivity.this, WishListActivity.this);

                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(itemViewAdapter);
                                itemViewAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SelectedCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 6) {

                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        //Toast.makeText(WishListActivity.this, "Position" + groupPosition, Toast.LENGTH_SHORT).show();
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SelectedCategoryModel>> call = userService.getSecondLevelCat("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "593");
                        call.enqueue(new Callback<List<SelectedCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SelectedCategoryModel>> call, Response<List<SelectedCategoryModel>> response) {
                                selectedCategoryModelList = response.body();
                                itemViewAdapter = new ItemViewAdapter(getApplicationContext(), selectedCategoryModelList, WishListActivity.this, WishListActivity.this);

                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(itemViewAdapter);
                                itemViewAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SelectedCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 1) {

                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        //Toast.makeText(WishListActivity.this, "Position" + groupPosition, Toast.LENGTH_SHORT).show();
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SelectedCategoryModel>> call = userService.getSecondLevelCat("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "319");
                        call.enqueue(new Callback<List<SelectedCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SelectedCategoryModel>> call, Response<List<SelectedCategoryModel>> response) {
                                selectedCategoryModelList = response.body();
                                itemViewAdapter = new ItemViewAdapter(getApplicationContext(), selectedCategoryModelList, WishListActivity.this, WishListActivity.this);

                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(itemViewAdapter);
                                itemViewAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SelectedCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
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
                    //Toast.makeText(WishListActivity.this, "Child Position" + childPosition, Toast.LENGTH_SHORT).show();
                    if (groupPosition == 0 && childPosition == 0) {
                        drawer.close();
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "271");
                        call.enqueue(new Callback<List<SubCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                                subCategoryModelList = response.body();
                                String long_description = response.body().get(childPosition).getDescription();
                                String short_description = response.body().get(childPosition).getShortDescription();
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 0 && childPosition == 1) {
                        drawer.close();
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "284");
                        call.enqueue(new Callback<List<SubCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                                subCategoryModelList = response.body();
                                String long_description = response.body().get(childPosition).getDescription();
                                String short_description = response.body().get(childPosition).getShortDescription();
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 0 && childPosition == 2) {

                        drawer.close();
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "312");
                        call.enqueue(new Callback<List<SubCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                                subCategoryModelList = response.body();
                                String long_description = response.body().get(childPosition).getDescription();
                                String short_description = response.body().get(childPosition).getShortDescription();
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 1 && childPosition == 0) {
                        drawer.close();
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "320");
                        call.enqueue(new Callback<List<SubCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                                subCategoryModelList = response.body();
                                String long_description = response.body().get(childPosition).getDescription();
                                String short_description = response.body().get(childPosition).getShortDescription();
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 1 && childPosition == 1) {
                        drawer.close();
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "326");
                        call.enqueue(new Callback<List<SubCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                                subCategoryModelList = response.body();
                                String long_description = response.body().get(childPosition).getDescription();
                                String short_description = response.body().get(childPosition).getShortDescription();
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 1 && childPosition == 2) {
                        drawer.close();
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "332");
                        call.enqueue(new Callback<List<SubCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                                subCategoryModelList = response.body();
                                String long_description = response.body().get(childPosition).getDescription();
                                String short_description = response.body().get(childPosition).getShortDescription();
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 1 && childPosition == 3) {
                        drawer.close();
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "338");
                        call.enqueue(new Callback<List<SubCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                                subCategoryModelList = response.body();
                                String long_description = response.body().get(childPosition).getDescription();
                                String short_description = response.body().get(childPosition).getShortDescription();
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 1 && childPosition == 4) {
                        drawer.close();
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "346");
                        call.enqueue(new Callback<List<SubCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                                subCategoryModelList = response.body();
                                String long_description = response.body().get(childPosition).getDescription();
                                String short_description = response.body().get(childPosition).getShortDescription();
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 1 && childPosition == 6) {
                        drawer.close();
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "3031");
                        call.enqueue(new Callback<List<SubCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                                subCategoryModelList = response.body();
                                String long_description = response.body().get(childPosition).getDescription();
                                String short_description = response.body().get(childPosition).getShortDescription();
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 1 && childPosition == 5) {
                        drawer.close();
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "373");
                        call.enqueue(new Callback<List<SubCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                                subCategoryModelList = response.body();
                                String long_description = response.body().get(childPosition).getDescription();
                                String short_description = response.body().get(childPosition).getShortDescription();
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 2 && childPosition == 0) {
                        drawer.close();
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "3075");
                        call.enqueue(new Callback<List<SubCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                                subCategoryModelList = response.body();
                                String long_description = response.body().get(childPosition).getDescription();
                                String short_description = response.body().get(childPosition).getShortDescription();
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 2 && childPosition == 1) {
                        drawer.close();
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "1409");
                        call.enqueue(new Callback<List<SubCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                                subCategoryModelList = response.body();
                                String long_description = response.body().get(childPosition).getDescription();
                                String short_description = response.body().get(childPosition).getShortDescription();
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 2 && childPosition == 2) {
                        drawer.close();
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "1407");
                        call.enqueue(new Callback<List<SubCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                                subCategoryModelList = response.body();
                                String long_description = response.body().get(childPosition).getDescription();
                                String short_description = response.body().get(childPosition).getShortDescription();
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 2 && childPosition == 3) {
                        drawer.close();
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "1357");
                        call.enqueue(new Callback<List<SubCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                                subCategoryModelList = response.body();
                                String long_description = response.body().get(childPosition).getDescription();
                                String short_description = response.body().get(childPosition).getShortDescription();
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 2 && childPosition == 4) {
                        drawer.close();
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "3068");
                        call.enqueue(new Callback<List<SubCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                                subCategoryModelList = response.body();
                                String long_description = response.body().get(childPosition).getDescription();
                                String short_description = response.body().get(childPosition).getShortDescription();
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 3 && childPosition == 3) {
                        drawer.close();
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "424");
                        call.enqueue(new Callback<List<SubCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                                subCategoryModelList = response.body();
                                String long_description = response.body().get(childPosition).getDescription();
                                String short_description = response.body().get(childPosition).getShortDescription();
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 3 && childPosition == 1) {
                        drawer.close();
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "666");
                        call.enqueue(new Callback<List<SubCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                                subCategoryModelList = response.body();
                                String long_description = response.body().get(childPosition).getDescription();
                                String short_description = response.body().get(childPosition).getShortDescription();
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 3 && childPosition == 2) {
                        drawer.close();
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "395");
                        call.enqueue(new Callback<List<SubCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                                subCategoryModelList = response.body();
                                String long_description = response.body().get(childPosition).getDescription();
                                String short_description = response.body().get(childPosition).getShortDescription();
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 3 && childPosition == 0) {
                        drawer.close();
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "381");
                        call.enqueue(new Callback<List<SubCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                                subCategoryModelList = response.body();
                                String long_description = response.body().get(childPosition).getDescription();
                                String short_description = response.body().get(childPosition).getShortDescription();
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 3 && childPosition == 4) {
                        drawer.close();
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "409");
                        call.enqueue(new Callback<List<SubCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                                subCategoryModelList = response.body();
                                String long_description = response.body().get(childPosition).getDescription();
                                String short_description = response.body().get(childPosition).getShortDescription();
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 3 && childPosition == 5) {
                        drawer.close();
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "416");
                        call.enqueue(new Callback<List<SubCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                                subCategoryModelList = response.body();
                                String long_description = response.body().get(childPosition).getDescription();
                                String short_description = response.body().get(childPosition).getShortDescription();
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 4 && childPosition == 2) {
                        drawer.close();
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "523");
                        call.enqueue(new Callback<List<SubCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                                subCategoryModelList = response.body();
                                String long_description = response.body().get(childPosition).getDescription();
                                String short_description = response.body().get(childPosition).getShortDescription();
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 4 && childPosition == 1) {
                        drawer.close();
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "532");
                        call.enqueue(new Callback<List<SubCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                                subCategoryModelList = response.body();
                                String long_description = response.body().get(childPosition).getDescription();
                                String short_description = response.body().get(childPosition).getShortDescription();
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 4 && childPosition == 0) {
                        drawer.close();
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "520");
                        call.enqueue(new Callback<List<SubCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                                subCategoryModelList = response.body();
                                String long_description = response.body().get(childPosition).getDescription();
                                String short_description = response.body().get(childPosition).getShortDescription();
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 5 && childPosition == 0) {
                        drawer.close();
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "556");
                        call.enqueue(new Callback<List<SubCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                                subCategoryModelList = response.body();
                                String long_description = response.body().get(childPosition).getDescription();
                                String short_description = response.body().get(childPosition).getShortDescription();
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 6 && childPosition == 0) {
                        drawer.close();
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "618");
                        call.enqueue(new Callback<List<SubCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                                subCategoryModelList = response.body();
                                String long_description = response.body().get(childPosition).getDescription();
                                String short_description = response.body().get(childPosition).getShortDescription();
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 6 && childPosition == 1) {
                        drawer.close();
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "611");
                        call.enqueue(new Callback<List<SubCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                                subCategoryModelList = response.body();
                                String long_description = response.body().get(childPosition).getDescription();
                                String short_description = response.body().get(childPosition).getShortDescription();
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 7 && childPosition == 0) {
                        drawer.close();
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "3043");
                        call.enqueue(new Callback<List<SubCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                                subCategoryModelList = response.body();
                                String long_description = response.body().get(childPosition).getDescription();
                                String short_description = response.body().get(childPosition).getShortDescription();
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 7 && childPosition == 1) {
                        drawer.close();
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "3044");
                        call.enqueue(new Callback<List<SubCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                                subCategoryModelList = response.body();
                                String long_description = response.body().get(childPosition).getDescription();
                                String short_description = response.body().get(childPosition).getShortDescription();
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }


                }

                return false;
            }
        });
    }


    @Override
    public void onItemClick(int position, int id) {
        if (position == 0) {
            progressBar.setVisibility(View.VISIBLE);
            userService = Api_Client.getClient().create(User_Service.class);
            Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", String.valueOf(id));
            call.enqueue(new Callback<List<SubCategoryModel>>() {
                @Override
                public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                    subCategoryModelList = response.body();
                    String long_description = response.body().get(position).getDescription();
                    String short_description = response.body().get(position).getShortDescription();
                    subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                    progressBar.setVisibility(View.GONE);
                    subCategoryModelAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(subCategoryModelAdapter);
                }

                @Override
                public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                    Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });

        } else if (position == 1) {
            progressBar.setVisibility(View.VISIBLE);
            userService = Api_Client.getClient().create(User_Service.class);
            Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", String.valueOf(id));
            call.enqueue(new Callback<List<SubCategoryModel>>() {
                @Override
                public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                    subCategoryModelList = response.body();
                    String long_description = response.body().get(position).getDescription();
                    String short_description = response.body().get(position).getShortDescription();
                    subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                    progressBar.setVisibility(View.GONE);
                    subCategoryModelAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(subCategoryModelAdapter);

                }

                @Override
                public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                    Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });

        } else if (position == 2) {
            progressBar.setVisibility(View.VISIBLE);
            userService = Api_Client.getClient().create(User_Service.class);
            Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", String.valueOf(id));
            call.enqueue(new Callback<List<SubCategoryModel>>() {
                @Override
                public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                    subCategoryModelList = response.body();
                    String long_description = response.body().get(position).getDescription();
                    String short_description = response.body().get(position).getShortDescription();
                    subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                    progressBar.setVisibility(View.GONE);
                    subCategoryModelAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(subCategoryModelAdapter);

                }

                @Override
                public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                    Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });

        } else if (position == 3) {
            progressBar.setVisibility(View.VISIBLE);
            userService = Api_Client.getClient().create(User_Service.class);
            Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", String.valueOf(id));
            call.enqueue(new Callback<List<SubCategoryModel>>() {
                @Override
                public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                    subCategoryModelList = response.body();
                    String long_description = response.body().get(position).getDescription();
                    String short_description = response.body().get(position).getShortDescription();
                    subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                    progressBar.setVisibility(View.GONE);
                    subCategoryModelAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(subCategoryModelAdapter);

                }

                @Override
                public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                    Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });

        } else if (position == 4) {
            progressBar.setVisibility(View.VISIBLE);
            userService = Api_Client.getClient().create(User_Service.class);
            Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", String.valueOf(id));
            call.enqueue(new Callback<List<SubCategoryModel>>() {
                @Override
                public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                    subCategoryModelList = response.body();
                    String long_description = response.body().get(position).getDescription();
                    String short_description = response.body().get(position).getShortDescription();
                    subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                    progressBar.setVisibility(View.GONE);
                    subCategoryModelAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(subCategoryModelAdapter);

                }

                @Override
                public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                    Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });

        } else if (position == 5) {
            progressBar.setVisibility(View.VISIBLE);
            userService = Api_Client.getClient().create(User_Service.class);
            Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", String.valueOf(id));
            call.enqueue(new Callback<List<SubCategoryModel>>() {
                @Override
                public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                    subCategoryModelList = response.body();
                    String long_description = response.body().get(position).getDescription();
                    String short_description = response.body().get(position).getShortDescription();
                    subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                    progressBar.setVisibility(View.GONE);
                    subCategoryModelAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(subCategoryModelAdapter);

                }

                @Override
                public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                    Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });

        } else if (position == 6) {
            progressBar.setVisibility(View.VISIBLE);
            userService = Api_Client.getClient().create(User_Service.class);
            Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", String.valueOf(id));
            call.enqueue(new Callback<List<SubCategoryModel>>() {
                @Override
                public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                    subCategoryModelList = response.body();
                    String long_description = response.body().get(position).getDescription();
                    String short_description = response.body().get(position).getShortDescription();
                    subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                    progressBar.setVisibility(View.GONE);
                    subCategoryModelAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(subCategoryModelAdapter);

                }

                @Override
                public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                    Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });

        } else if (position == 7) {
            progressBar.setVisibility(View.VISIBLE);
            userService = Api_Client.getClient().create(User_Service.class);
            Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", String.valueOf(id));
            call.enqueue(new Callback<List<SubCategoryModel>>() {
                @Override
                public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                    subCategoryModelList = response.body();
                    String long_description = response.body().get(position).getDescription();
                    String short_description = response.body().get(position).getShortDescription();
                    subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                    progressBar.setVisibility(View.GONE);
                    subCategoryModelAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(subCategoryModelAdapter);

                }

                @Override
                public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                    Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });

        } else if (position == 8) {
            progressBar.setVisibility(View.VISIBLE);
            userService = Api_Client.getClient().create(User_Service.class);
            Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", String.valueOf(id));
            call.enqueue(new Callback<List<SubCategoryModel>>() {
                @Override
                public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                    subCategoryModelList = response.body();
                    String long_description = response.body().get(position).getDescription();
                    String short_description = response.body().get(position).getShortDescription();
                    subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                    progressBar.setVisibility(View.GONE);
                    subCategoryModelAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(subCategoryModelAdapter);

                }

                @Override
                public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                    Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });

        } else if (position == 9) {
            progressBar.setVisibility(View.VISIBLE);
            userService = Api_Client.getClient().create(User_Service.class);
            Call<List<SubCategoryModel>> call = userService.getSubCategory("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", String.valueOf(id));
            call.enqueue(new Callback<List<SubCategoryModel>>() {
                @Override
                public void onResponse(Call<List<SubCategoryModel>> call, Response<List<SubCategoryModel>> response) {
                    subCategoryModelList = response.body();
                    for (int i = 0; i < 7; i++) {
                        String long_description = response.body().get(i).getDescription();
                        String short_description = response.body().get(i).getShortDescription();
                        subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList, long_description, short_description, city_name, postal_code, WishListActivity.this);
                        progressBar.setVisibility(View.GONE);
                        subCategoryModelAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(subCategoryModelAdapter);
                    }

                }

                @Override
                public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                    Toast.makeText(WishListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }*/

//    @Override
//    public void onLongItemClick(int position) {
//
//    }
//
//
//    @Override
//    public void onLocationChanged(@NonNull Location location) {
//
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//
//    }
//
//    @Override
//    public void onProviderEnabled(@NonNull String provider) {
//
//    }
//
//    @Override
//    public void onProviderDisabled(@NonNull String provider) {
//
//    }
//
//    @Override
//    public void onItemClick(int position) {

    }
