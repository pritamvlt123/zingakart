package com.example.zingakart.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zingakart.APIClient.Api_Client;
import com.example.zingakart.APIClient.User_Service;
import com.example.zingakart.Adapters.ExpandableListAdapter;
import com.example.zingakart.Adapters.ItemSearchAdapter;
import com.example.zingakart.Adapters.ItemViewAdapter;
import com.example.zingakart.Adapters.SubCategoryModelAdapter;
import com.example.zingakart.Fragments.HomeFragment;
import com.example.zingakart.Fragments.WishListFragment;
import com.example.zingakart.Helper.DatabaseHandler;
import com.example.zingakart.Helper.RecylerViewClickInterface;
import com.example.zingakart.Helper.SessionManager;
import com.example.zingakart.Helper.TrackerGPS_V3;
import com.example.zingakart.Model.ItemViewModel;
import com.example.zingakart.Model.MenuModel;
import com.example.zingakart.Model.SelectedCategoryModel;
import com.example.zingakart.Model.SubCategoryModel;
import com.example.zingakart.R;
import com.example.zingakart.Utils.ConnectivityReceiver;
import com.example.zingakart.app.AppController;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.tbruyelle.rxpermissions3.RxPermissions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.zingakart.Helper.TrackerGPS_V3.REQUEST_CHECK_SETTINGS;

public class MainActivity extends AppCompatActivity implements RecylerViewClickInterface, ItemViewAdapter.OnclickItem, TrackerGPS_V3.LocationCallBack {

    private static final String TAG = "this";
    private final ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver();
    private AppBarConfiguration mAppBarConfiguration;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    ExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    List<MenuModel> headerList = new ArrayList<>();
    HashMap<MenuModel, List<MenuModel>> childList = new HashMap<>();
    private RecyclerView recyclerView;
    List<ItemViewModel> itemViewModelList;
    private List<SelectedCategoryModel> selectedCategoryModelList;
    private List<SubCategoryModel> subCategoryModelList;
    ImageView cart, profile_img;
    SearchView searchView;
    String first_name = "";
    TrackerGPS_V3 gps_v3;
    TextView tv_username;
    NavigationView navigationView;
    ConstraintLayout coordinator;
    SharedPreferences sharedPreferences;
    DrawerLayout drawer;
    FloatingActionButton fab;
    public static final String MyPREFERENCES = "MyPrefs";
    User_Service userService;
    ItemViewAdapter itemViewAdapter;
    ProgressBar progressBar;
    SubCategoryModelAdapter subCategoryModelAdapter;
    int groupPos = 0;
    String provider;
    LocationManager locationManager;
    String city_name =  "";
    String postal_code =  "";
    private String user_id;
    private ListView mListView;
    ItemSearchAdapter itemSearchAdapter;
    private boolean exit=false;//declare in public
    SessionManager sessionManager;
    DatabaseHandler databaseHandler;
    boolean isSkip;
    boolean doubleBackToExitPressedOnce = false;
    private int cartSize = 0, wishlistSize = 0;
    String name = "";
    String price = "";
    String regularPrice = "";
    String discountPrice = "";
    String item_category = "";
    String long_description = "";
    String short_description = "";
    String type = "";
    String type_name = "";
    List<String> attribute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        selectedCategoryModelList = new ArrayList<>();
        progressBar = findViewById(R.id.pbHeaderProgress);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar.setVisibility(View.VISIBLE);
        tv_username = findViewById(R.id.tv_username);
        cart = findViewById(R.id.cart);
        searchView = findViewById(R.id.search);
        coordinator = findViewById(R.id.coordinator);
        mListView = findViewById(R.id.listView);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        expandableListView = findViewById(R.id.expandableListView);
        fab = findViewById(R.id.fab);


        city_name = getIntent().getStringExtra("city_name");
        postal_code = getIntent().getStringExtra("postal_code");
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search Here");


        profile_img = findViewById(R.id.profile_image);
        int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlate = searchView.findViewById(searchPlateId);
        if (searchPlate != null) {
            searchPlate.setBackgroundColor(Color.TRANSPARENT);
            int searchTextId = searchPlate.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);

        }
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ContactUsActivity.class);
                startActivity(intent);
            }
        });


        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager = new SessionManager(getApplicationContext());
                databaseHandler = new DatabaseHandler(getApplicationContext());
                isSkip = sessionManager.isSkipLogin();
                if (isSkip) {
                    updateCart();
                    if (cartSize != 0) {
                        Intent intent = new Intent(MainActivity.this, CartActivity.class);
                        startActivity(intent);
                    } else {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Note")
                                .setMessage("Please add items to your Cart")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                                .show();
                    }
                }
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //itemViewAdapter.getFilter().filter(newText);
                //itemViewAdapter.notifyDataSetChanged();
                return false;
            }
        });



        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        prepareMenuData();
        populateExpandableList();

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_wish_list)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.nav_home:
                        loadFeaturedItems();
                        break;
                    case R.id.nav_wish_list:
                        Intent intent1 = new Intent(MainActivity.this, WishListActivity.class);
                        startActivity(intent1);
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        checkConnection();
        gps_v3 = new TrackerGPS_V3(this);
        checkRuntimePermission();

    }

    private void updateCart() {
        sessionManager = new SessionManager(this);
        isSkip = sessionManager.isSkipLogin();
        if (!isSkip) {
            DatabaseHandler databaseHandler = new DatabaseHandler(MainActivity.this);
            user_id = databaseHandler.getUserDetails().get("id");
//            RetroInterface serviceApi = ApiClient.getClient().create(RetroInterface.class);
//            Map<String, String> params = new HashMap<String, String>();
//            params.put("user_id", user_id);
//            Call<String> call1 = serviceApi.getcartlist(params);
//            call1.enqueue(new Callback<String>() {
//                @Override
//                public void onResponse(Call<String> call1, Response<String> response) {
//                    if (response.body() != null && !response.body().equalsIgnoreCase("")) {
//                        String result = response.body();
//                        JSONObject json = null;
//                        try {
//                            json = new JSONObject(result);
//                            JSONArray list = json.getJSONArray("categories");
//                            cartSize = list.length();
//                            setNotification(3, String.valueOf(cartSize));
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        cartSize = 0;
//                        hideNotification(3);
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<String> call1, Throwable t) {
//                    cartSize = 0;
//                    hideNotification(3);
//                }
//            });
//        }
        }
    }

    public void checkRuntimePermission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(new Observer<Boolean>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            gps_v3.startLocationUpdates();
                        } else {
                            Toast.makeText(getApplicationContext(), "Permission request denied", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }

                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //For Grab location
        if (resultCode == RESULT_OK && requestCode == REQUEST_CHECK_SETTINGS) {
            gps_v3.startLocationUpdates();
        }

    }

    @Override
    public void updateLocation(double lat, double lng) {
        initializeAddress(lat, lng);
    }

    void initializeAddress(Double la, Double lo) {

        Log.d("data", "Inside");
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(la, lo, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            Log.d(TAG, "initializeAddress: " + la + "\t" + lo);
            if (addresses == null || addresses.size() == 0) {
                //getLocationFromWeb(lat, lon);
            } else {
                Address address = addresses.get(0);
                ArrayList<String> addressFragments = new ArrayList<>();

                String gps_Locality = address.getLocality();
                String gps_AdminArea = address.getAdminArea();
                String gps_postalCode = address.getPostalCode();
                String gps_country = address.getCountryName();

                String locationData = gps_Locality + gps_AdminArea + gps_postalCode + gps_country;
                Log.d("data1 >", locationData);
                Log.d("data1 >>", address.getSubAdminArea() + address.getSubLocality());

                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressFragments.add(address.getAddressLine(i));
                    Log.d(TAG, "initializeAddress: " + i + ": " + address.getAddressLine(i));
                }
                String mapAdd = TextUtils.join(" ", addressFragments);
                city_name = gps_Locality.toString();
                postal_code = gps_postalCode.toString();
                //mapAdd = tempAd;//addresses.get(0).getAddressLine(1);
                Log.d("data2 >", mapAdd);

            }
        } catch (Exception e) {
            e.printStackTrace();
            /*if (mapAdd.contentEquals("WebService.NO_ADDRESS_FOUND") || mapAdd.contentEquals(" ") || mapAdd.isEmpty()) {
                getLocationFromWeb(lat, lon);
            }*/
        }
    }


    private void loadFeaturedItems() {
        progressBar.setVisibility(View.VISIBLE);
        userService = Api_Client.getClient().create(User_Service.class);
        Call<List<SelectedCategoryModel>> call = userService.getCategories("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "272,273,274,276,284,1363,321,322,323,338,886", "100");
        call.enqueue(new Callback<List<SelectedCategoryModel>>() {
            @Override
            public void onResponse(Call<List<SelectedCategoryModel>> call, Response<List<SelectedCategoryModel>> response) {
                selectedCategoryModelList = response.body();
                itemViewAdapter = new ItemViewAdapter(getApplicationContext(), selectedCategoryModelList, MainActivity.this, MainActivity.this);
                progressBar.setVisibility(View.GONE);
                recyclerView.setAdapter(itemViewAdapter);

            }

            @Override
            public void onFailure(Call<List<SelectedCategoryModel>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onBackPressed() {
        progressBar.setVisibility(View.VISIBLE);
        loadFeaturedItems();
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to close Zingakart", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 1000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if(mToggle.onOptionsItemSelected(item)){
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
////        Fragment fragment = null;
////        Class fragmentClass;
//        switch (item.getItemId()) {
//            case R.id.nav_home:
////                fragmentClass = HomeFragment.class;
//                Intent i = new Intent(MainActivity.this, HomeFragment.class);
//                startActivity(i);
//                break;
//            case R.id.nav_wish_list:
////                fragmentClass = WishListFragment.class;
//                Intent j = new Intent(MainActivity.this, WishListActivity.class);
//                startActivity(j);
//                break;
//            default:
////                fragmentClass = HomeFragment.class;
//        }
////
////        try {
////            fragment = (Fragment) fragmentClass.newInstance();
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////
////        // Insert the fragment by replacing any existing fragment
////        FragmentManager fragmentManager = getSupportFragmentManager();
////        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
//
//        // Highlight the selected item has been done by NavigationView
//        item.setChecked(true);
//        // Set action bar title
//        setTitle(item.getTitle());
//        // Close the navigation drawer
//        drawer.closeDrawers();
//        return true;
//    }


    private void prepareMenuData() {


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
                    //Toast.makeText(MainActivity.this, "Position" + groupPosition, Toast.LENGTH_SHORT).show();
                    if (groupPosition == 2) {
                        //onBackPressed();
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SelectedCategoryModel>> call = userService.getSecondLevelCat("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "17");
                        call.enqueue(new Callback<List<SelectedCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SelectedCategoryModel>> call, Response<List<SelectedCategoryModel>> response) {
                                selectedCategoryModelList = response.body();
                                itemViewAdapter = new ItemViewAdapter(getApplicationContext(), selectedCategoryModelList, MainActivity.this, MainActivity.this);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(itemViewAdapter);
                                itemViewAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SelectedCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 7) {
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        //Toast.makeText(MainActivity.this, "Position" + groupPosition, Toast.LENGTH_SHORT).show();
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SelectedCategoryModel>> call = userService.getSecondLevelCat("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "626");
                        call.enqueue(new Callback<List<SelectedCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SelectedCategoryModel>> call, Response<List<SelectedCategoryModel>> response) {
                                selectedCategoryModelList = response.body();
                                itemViewAdapter = new ItemViewAdapter(getApplicationContext(), selectedCategoryModelList, MainActivity.this, MainActivity.this);

                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(itemViewAdapter);
                                itemViewAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SelectedCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else if (groupPosition == 4) {
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        //Toast.makeText(MainActivity.this, "Position" + groupPosition, Toast.LENGTH_SHORT).show();
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SelectedCategoryModel>> call = userService.getSecondLevelCat("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "519");
                        call.enqueue(new Callback<List<SelectedCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SelectedCategoryModel>> call, Response<List<SelectedCategoryModel>> response) {
                                selectedCategoryModelList = response.body();
                                itemViewAdapter = new ItemViewAdapter(getApplicationContext(), selectedCategoryModelList, MainActivity.this, MainActivity.this);

                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(itemViewAdapter);
                                itemViewAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SelectedCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else if (groupPosition == 5) {
                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        //Toast.makeText(MainActivity.this, "Position" + groupPosition, Toast.LENGTH_SHORT).show();
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SelectedCategoryModel>> call = userService.getSecondLevelCat("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "548");
                        call.enqueue(new Callback<List<SelectedCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SelectedCategoryModel>> call, Response<List<SelectedCategoryModel>> response) {
                                selectedCategoryModelList = response.body();
                                itemViewAdapter = new ItemViewAdapter(getApplicationContext(), selectedCategoryModelList, MainActivity.this, MainActivity.this);

                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(itemViewAdapter);
                                itemViewAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SelectedCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 3) {

                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        //Toast.makeText(MainActivity.this, "Position" + groupPosition, Toast.LENGTH_SHORT).show();
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SelectedCategoryModel>> call = userService.getSecondLevelCat("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "380");
                        call.enqueue(new Callback<List<SelectedCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SelectedCategoryModel>> call, Response<List<SelectedCategoryModel>> response) {
                                selectedCategoryModelList = response.body();
                                itemViewAdapter = new ItemViewAdapter(getApplicationContext(), selectedCategoryModelList, MainActivity.this, MainActivity.this);

                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(itemViewAdapter);
                                itemViewAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SelectedCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 0) {

                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        //Toast.makeText(MainActivity.this, "Position" + groupPosition, Toast.LENGTH_SHORT).show();
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SelectedCategoryModel>> call = userService.getSecondLevelCat("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "270");
                        call.enqueue(new Callback<List<SelectedCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SelectedCategoryModel>> call, Response<List<SelectedCategoryModel>> response) {
                                selectedCategoryModelList = response.body();
                                itemViewAdapter = new ItemViewAdapter(getApplicationContext(), selectedCategoryModelList, MainActivity.this, MainActivity.this);

                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(itemViewAdapter);
                                itemViewAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SelectedCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 6) {

                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        //Toast.makeText(MainActivity.this, "Position" + groupPosition, Toast.LENGTH_SHORT).show();
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SelectedCategoryModel>> call = userService.getSecondLevelCat("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "593");
                        call.enqueue(new Callback<List<SelectedCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SelectedCategoryModel>> call, Response<List<SelectedCategoryModel>> response) {
                                selectedCategoryModelList = response.body();
                                itemViewAdapter = new ItemViewAdapter(getApplicationContext(), selectedCategoryModelList, MainActivity.this, MainActivity.this);

                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(itemViewAdapter);
                                itemViewAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SelectedCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (groupPosition == 1) {

                        selectedCategoryModelList.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        userService = Api_Client.getClient().create(User_Service.class);
                        Call<List<SelectedCategoryModel>> call = userService.getSecondLevelCat("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", "319");
                        call.enqueue(new Callback<List<SelectedCategoryModel>>() {
                            @Override
                            public void onResponse(Call<List<SelectedCategoryModel>> call, Response<List<SelectedCategoryModel>> response) {
                                selectedCategoryModelList = response.body();
                                itemViewAdapter = new ItemViewAdapter(getApplicationContext(), selectedCategoryModelList, MainActivity.this, MainActivity.this);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(itemViewAdapter);
                                itemViewAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SelectedCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                    //Toast.makeText(MainActivity.this, "Child Position" + childPosition, Toast.LENGTH_SHORT).show();
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
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                                subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setAdapter(subCategoryModelAdapter);
                                subCategoryModelAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                    type = response.body().get(position).getType();
                    long_description = response.body().get(position).getDescription();
                    short_description = response.body().get(position).getShortDescription();
                    if(response.body().get(position).getType().equalsIgnoreCase("simple") && (response.body().get(position).getAttributes().get(position).getName().equalsIgnoreCase("Size")))
                    {
                        type_name = response.body().get(position).getAttributes().get(position).getName();
                        attribute = response.body().get(position).getAttributes().get(position).getOptions();
                    }
                    else if(response.body().get(position).getType().equalsIgnoreCase("variable") && (response.body().get(position).getAttributes().get(position).getName().equalsIgnoreCase("Size")))
                    {
                        type_name = response.body().get(position).getAttributes().get(position).getName();
                        attribute = response.body().get(position).getAttributes().get(position).getOptions();
                    }
                    subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                    progressBar.setVisibility(View.GONE);
                    subCategoryModelAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(subCategoryModelAdapter);
                }

                @Override
                public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                    if(response.body().get(position).getType().equalsIgnoreCase("simple") && (response.body().get(position).getAttributes().get(position).getName().equalsIgnoreCase("Size")))
                    {
                        type_name = response.body().get(position).getAttributes().get(position).getName();
                        attribute = response.body().get(position).getAttributes().get(position).getOptions();
                    }
                    else if(response.body().get(position).getType().equalsIgnoreCase("variable") && (response.body().get(position).getAttributes().get(position).getName().equalsIgnoreCase("Size")))
                    {
                        type_name = response.body().get(position).getAttributes().get(position).getName();
                        attribute = response.body().get(position).getAttributes().get(position).getOptions();
                    }
                    subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                    progressBar.setVisibility(View.GONE);
                    subCategoryModelAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(subCategoryModelAdapter);

                }

                @Override
                public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                    for(int i =0; i < subCategoryModelList.size(); i++)
                    {
                        if(response.body().get(i).getType().equalsIgnoreCase("simple") && (response.body().get(i).getAttributes().get(i).getName().equalsIgnoreCase("Size")))
                        {
                            type_name = response.body().get(i).getAttributes().get(i).getName();
                            attribute = response.body().get(i).getAttributes().get(i).getOptions();
                        }
                        else if(response.body().get(i).getType().equalsIgnoreCase("variable") && (response.body().get(i).getAttributes().get(i).getName().equalsIgnoreCase("Size")))
                        {
                            type_name = response.body().get(i).getAttributes().get(i).getName();
                            attribute = response.body().get(i).getAttributes().get(i).getOptions();
                        }
                    }

                    subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                    progressBar.setVisibility(View.GONE);
                    subCategoryModelAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(subCategoryModelAdapter);

                }

                @Override
                public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                    if(response.body().get(position).getType().equalsIgnoreCase("simple") && (response.body().get(position).getAttributes().get(position).getName().equalsIgnoreCase("Size")))
                    {
                        type_name = response.body().get(position).getAttributes().get(position).getName();
                        attribute = response.body().get(position).getAttributes().get(position).getOptions();
                    }
                    else if(response.body().get(position).getType().equalsIgnoreCase("variable") && (response.body().get(position).getAttributes().get(position).getName().equalsIgnoreCase("Size")))
                    {
                        type_name = response.body().get(position).getAttributes().get(position).getName();
                        attribute = response.body().get(position).getAttributes().get(position).getOptions();
                    }
                    subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                    progressBar.setVisibility(View.GONE);
                    subCategoryModelAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(subCategoryModelAdapter);

                }

                @Override
                public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                    if(response.body().get(position).getType().equalsIgnoreCase("simple") && (response.body().get(position).getAttributes().get(position).getName().equalsIgnoreCase("Size")))
                    {
                        type_name = response.body().get(position).getAttributes().get(position).getName();
                        attribute = response.body().get(position).getAttributes().get(position).getOptions();
                    }
                    else if(response.body().get(position).getType().equalsIgnoreCase("variable") && (response.body().get(position).getAttributes().get(position).getName().equalsIgnoreCase("Size")))
                    {
                        type_name = response.body().get(position).getAttributes().get(position).getName();
                        attribute = response.body().get(position).getAttributes().get(position).getOptions();
                    }
                    subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                    progressBar.setVisibility(View.GONE);
                    subCategoryModelAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(subCategoryModelAdapter);

                }

                @Override
                public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                    if(response.body().get(position).getType().equalsIgnoreCase("simple") && (response.body().get(position).getAttributes().get(position).getName().equalsIgnoreCase("Size")))
                    {
                        type_name = response.body().get(position).getAttributes().get(position).getName();
                        attribute = response.body().get(position).getAttributes().get(position).getOptions();
                    }
                    else if(response.body().get(position).getType().equalsIgnoreCase("variable") && (response.body().get(position).getAttributes().get(position).getName().equalsIgnoreCase("Size")))
                    {
                        type_name = response.body().get(position).getAttributes().get(position).getName();
                        attribute = response.body().get(position).getAttributes().get(position).getOptions();
                    }
                    subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                    progressBar.setVisibility(View.GONE);
                    subCategoryModelAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(subCategoryModelAdapter);

                }

                @Override
                public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                    if(response.body().get(position).getType().equalsIgnoreCase("simple") && (response.body().get(position).getAttributes().get(position).getName().equalsIgnoreCase("Size")))
                    {
                        type_name = response.body().get(position).getAttributes().get(position).getName();
                        attribute = response.body().get(position).getAttributes().get(position).getOptions();
                    }
                    else if(response.body().get(position).getType().equalsIgnoreCase("variable") && (response.body().get(position).getAttributes().get(position).getName().equalsIgnoreCase("Size")))
                    {
                        type_name = response.body().get(position).getAttributes().get(position).getName();
                        attribute = response.body().get(position).getAttributes().get(position).getOptions();
                    }
                    subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                    progressBar.setVisibility(View.GONE);
                    subCategoryModelAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(subCategoryModelAdapter);

                }

                @Override
                public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                    if(response.body().get(position).getType().equalsIgnoreCase("simple") && (response.body().get(position).getAttributes().get(position).getName().equalsIgnoreCase("Size")))
                    {
                        type_name = response.body().get(position).getAttributes().get(position).getName();
                        attribute = response.body().get(position).getAttributes().get(position).getOptions();
                    }
                    else if(response.body().get(position).getType().equalsIgnoreCase("variable") && (response.body().get(position).getAttributes().get(position).getName().equalsIgnoreCase("Size")))
                    {
                        type_name = response.body().get(position).getAttributes().get(position).getName();
                        attribute = response.body().get(position).getAttributes().get(position).getOptions();
                    }
                    subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                    progressBar.setVisibility(View.GONE);
                    subCategoryModelAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(subCategoryModelAdapter);

                }

                @Override
                public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                    if(response.body().get(position).getType().equalsIgnoreCase("simple") && (response.body().get(position).getAttributes().get(position).getName().equalsIgnoreCase("Size")))
                    {
                        type_name = response.body().get(position).getAttributes().get(position).getName();
                        attribute = response.body().get(position).getAttributes().get(position).getOptions();
                    }
                    else if(response.body().get(position).getType().equalsIgnoreCase("variable") && (response.body().get(position).getAttributes().get(position).getName().equalsIgnoreCase("Size")))
                    {
                        type_name = response.body().get(position).getAttributes().get(position).getName();
                        attribute = response.body().get(position).getAttributes().get(position).getOptions();
                    }
                    subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                    progressBar.setVisibility(View.GONE);
                    subCategoryModelAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(subCategoryModelAdapter);

                }

                @Override
                public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                        if(response.body().get(position).getType().equalsIgnoreCase("simple") && (response.body().get(position).getAttributes().get(position).getName().equalsIgnoreCase("Size")))
                        {
                            type_name = response.body().get(position).getAttributes().get(position).getName();
                            attribute = response.body().get(position).getAttributes().get(position).getOptions();
                        }
                        else if(response.body().get(position).getType().equalsIgnoreCase("variable") && (response.body().get(position).getAttributes().get(position).getName().equalsIgnoreCase("Size")))
                        {
                            type_name = response.body().get(position).getAttributes().get(position).getName();
                            attribute = response.body().get(position).getAttributes().get(position).getOptions();
                        }
                        subCategoryModelAdapter = new SubCategoryModelAdapter(MainActivity.this, subCategoryModelList,long_description, short_description, city_name, postal_code, type_name, attribute);
                        progressBar.setVisibility(View.GONE);
                        subCategoryModelAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(subCategoryModelAdapter);
                    }

                }

                @Override
                public void onFailure(Call<List<SubCategoryModel>> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    @Override
    public void onLongItemClick(int position) {

    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "position" + position, Toast.LENGTH_SHORT).show();


    }


    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        if (!isConnected) {
            Snackbar snackbar = Snackbar
                    .make(coordinator, "No internet connection!", Snackbar.LENGTH_LONG)
                    .setActionTextColor(Color.RED)
                    .setDuration(10000)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkConnection();
                        }
                    });

            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
        if (isConnected) {
            sessionManager = new SessionManager(this);
            databaseHandler = new DatabaseHandler(this);
            isSkip = sessionManager.isSkipLogin();
            if (!isSkip) {
                user_id = databaseHandler.getUserDetails().get("id");
            }
            loadFeaturedItems();
        }


    }

//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        switch(item.getItemId())
//        {
//            case R.id.nav_wish_list:
//                Intent intent = new Intent(MainActivity.this, WishListActivity.class);
//                startActivity(intent);
//            break;
//        }
//        drawer.closeDrawer(GravityCompat.START);
//        return false;
//    }


    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }



}
