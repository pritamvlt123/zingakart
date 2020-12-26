package com.example.zingakart.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.zingakart.APIClient.User_Service;
import com.example.zingakart.Adapters.ExpandableListAdapter;
import com.example.zingakart.Adapters.FeaturedItemsAdapter;
import com.example.zingakart.Model.MenuModel;
import com.example.zingakart.Model.SubCategoryModel;
import com.example.zingakart.R;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FeaturedItemsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    ExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    List<MenuModel> headerList = new ArrayList<>();
    HashMap<MenuModel, List<MenuModel>> childList = new HashMap<>();
    ImageView cart, profile_img;
    private NavigationView navigationView;
    User_Service userService;
    SharedPreferences sharedPreferences;
    private DrawerLayout drawer;
    List<SubCategoryModel> subCategoryModelList;
    public static final String MyPREFERENCES = "MyPrefs" ;
    Boolean OnClick = false;
    ElegantNumberButton button;
    List<String> item = new ArrayList<String>();
    List<String> qty = new ArrayList<String>();
    List<String> pri = new ArrayList<String>();
    List<String> im = new ArrayList<String>();
    FeaturedItemsAdapter featuredItemsAdapter;
    RecyclerView recyclerView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_featured_items);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerview);
        progressBar = findViewById(R.id.pbHeaderProgress);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        prepareMenuData();

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());


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
        NavController navController = Navigation.findNavController(this, R.id.nav_featured_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
            Log.d("API123","here");
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
            Log.d("API123","here");
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
            Log.d("API123","here");
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
            Log.d("API123","here");
            childList.put(menuModel4, childModelsList3);
        }


        MenuModel menuModel5 = new MenuModel("Sports & Fitness", true, true); //Menu of Java Tutorials
        headerList.add(menuModel5);
        List<MenuModel> childModelsList4 = new ArrayList<>();
        MenuModel childModel4 = new MenuModel("Healthcare", false, false);
        childModelsList4.add(childModel4);



        if (menuModel5.hasChildren) {
            Log.d("API123","here");
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
            Log.d("API123","here");
            childList.put(menuModel6, childModelsList5);
        }


        MenuModel menuModel7 = new MenuModel("Daily Essentials", true, true); //Menu of Java Tutorials
        headerList.add(menuModel7);
        List<MenuModel> childModelsList6 = new ArrayList<>();
        MenuModel childModel6 = new MenuModel("Daily Needs", false, false);
        childModelsList6.add(childModel6);


        if (menuModel6.hasChildren) {
            Log.d("API123","here");
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
                        Toast.makeText(FeaturedItemsActivity.this, "Position" + groupPosition, Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(FeaturedItemsActivity.this, "Position" + groupPosition, Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(FeaturedItemsActivity.this, "Position" + childPosition, Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });
    }

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