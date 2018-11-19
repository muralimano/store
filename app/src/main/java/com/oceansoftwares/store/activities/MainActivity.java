package com.oceansoftwares.store.activities;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentTransaction;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.DrawableWrapper;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.MobileAds;
import com.oceansoftwares.store.app.App;
import com.oceansoftwares.store.customs.CircularImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oceansoftwares.store.R;
import com.oceansoftwares.store.customs.LocaleHelper;
import com.oceansoftwares.store.databases.Restaurant_Status_DB;
import com.oceansoftwares.store.databases.User_Info_DB;
import com.oceansoftwares.store.fragments.Categories_2;
import com.oceansoftwares.store.fragments.Categories_3;
import com.oceansoftwares.store.fragments.Categories_4;
import com.oceansoftwares.store.fragments.Categories_5;
import com.oceansoftwares.store.fragments.Categories_6;
import com.oceansoftwares.store.fragments.Close_Fragment;
import com.oceansoftwares.store.fragments.Comment_Fragment;
import com.oceansoftwares.store.fragments.ContactUs;
import com.oceansoftwares.store.fragments.HomePage_2;
import com.oceansoftwares.store.fragments.HomePage_3;
import com.oceansoftwares.store.fragments.HomePage_1;
import com.oceansoftwares.store.fragments.HomePage_5;
import com.oceansoftwares.store.fragments.Languages;
import com.oceansoftwares.store.fragments.News;
import com.oceansoftwares.store.fragments.Products;
import com.oceansoftwares.store.fragments.SettingsFragment;
import com.oceansoftwares.store.fragments.Update_Account;
import com.oceansoftwares.store.models.ResDetails;
import com.oceansoftwares.store.models.ResStatus;
import com.oceansoftwares.store.models.device_model.AppSettingsDetails;
import com.oceansoftwares.store.models.user_model.UserDetails;
import com.oceansoftwares.store.customs.NotificationBadger;
import com.oceansoftwares.store.network.APIClient;
import com.oceansoftwares.store.receivers.AlarmReceiver;
import com.oceansoftwares.store.utils.NotificationScheduler;
import com.oceansoftwares.store.utils.Utilities;
import com.oceansoftwares.store.app.MyAppPrefsManager;
import com.oceansoftwares.store.activities.adapters.DrawerExpandableListAdapter;
import com.oceansoftwares.store.constant.ConstantValues;
import com.oceansoftwares.store.fragments.About;
import com.oceansoftwares.store.fragments.Categories_1;
import com.oceansoftwares.store.fragments.My_Addresses;
import com.oceansoftwares.store.fragments.My_Cart;
import com.oceansoftwares.store.fragments.HomePage_4;
import com.oceansoftwares.store.fragments.My_Orders;
import com.oceansoftwares.store.fragments.SearchFragment;
import com.oceansoftwares.store.fragments.WishList;
import com.oceansoftwares.store.models.drawer_model.Drawer_Items;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * MainActivity of the App
 **/

public class MainActivity extends AppCompatActivity {

    int homeStyle, categoryStyle;
    
   public Toolbar toolbar;
    ActionBar actionBar;
    
    ImageView drawer_header;
    DrawerLayout drawerLayout;
    NavigationView navigationDrawer;

    SharedPreferences sharedPreferences;
    MyAppPrefsManager myAppPrefsManager;

    ExpandableListView main_drawer_list;
    DrawerExpandableListAdapter drawerExpandableAdapter;

    boolean doublePressedBackToExit = false;

    private static String mSelectedItem;
    private static final String SELECTED_ITEM_ID = "selected";
    public static ActionBarDrawerToggle actionBarDrawerToggle;

    List<Drawer_Items> listDataHeader = new ArrayList<>();
    Map<Drawer_Items, List<Drawer_Items>> listDataChild = new HashMap<>();


    List<ResDetails> countryList;
    List<String> countryNames;
    
    
    //*********** Called when the Activity is becoming Visible to the User ********//

    
    @Override
    protected void onStart() {
        super.onStart();

        countryNames=new ArrayList<>();
        
        if (myAppPrefsManager.isFirstTimeLaunch()) {
            NotificationScheduler.setReminder(MainActivity.this, AlarmReceiver.class);
        }
    
        myAppPrefsManager.setFirstTimeLaunch(false);

        RequestResStatus();
    }
    
    
    
    //*********** Called when the Activity is first Created ********//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    
        MobileAds.initialize(this, ConstantValues.ADMOBE_ID);
        
        
        // Get MyAppPrefsManager
        myAppPrefsManager = new MyAppPrefsManager(MainActivity.this);
        sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);


        // Binding Layout Views
        toolbar = (Toolbar) findViewById(R.id.myToolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationDrawer = (NavigationView) findViewById(R.id.main_drawer);


        // Get ActionBar and Set the Title and HomeButton of Toolbar
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(ConstantValues.APP_HEADER);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        // Setup Expandable DrawerList
        setupExpandableDrawerList();

        // Setup Expandable Drawer Header
        setupExpandableDrawerHeader();


        // Initialize ActionBarDrawerToggle
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawerOpen, R.string.drawerClose) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // Hide OptionsMenu
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Recreate the OptionsMenu
                invalidateOptionsMenu();
            }
        };


        // Add ActionBarDrawerToggle to DrawerLayout
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        // Synchronize the indicator with the state of the linked DrawerLayout
        actionBarDrawerToggle.syncState();



        // Get the default ToolbarNavigationClickListener
        final View.OnClickListener toggleNavigationClickListener = actionBarDrawerToggle.getToolbarNavigationClickListener();

        // Handle ToolbarNavigationClickListener with OnBackStackChangedListener
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {

                // Check BackStackEntryCount of FragmentManager
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

                    // Set new ToolbarNavigationClickListener
                    actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Close the Drawer if Opened
                            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                                drawerLayout.closeDrawers();
                            } else {
                                // Pop previous Fragment from BackStack
                                getSupportFragmentManager().popBackStack();
                            }
                        }
                    });


                } else {
                    // Set DrawerToggle Indicator and default ToolbarNavigationClickListener
                    actionBar.setTitle(ConstantValues.APP_HEADER);
                    actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
                    actionBarDrawerToggle.setToolbarNavigationClickListener(toggleNavigationClickListener);
                }
            }
        });



        // Select SELECTED_ITEM from SavedInstanceState
        mSelectedItem = savedInstanceState == null ?  ConstantValues.DEFAULT_HOME_STYLE :  savedInstanceState.getString(SELECTED_ITEM_ID);

        // Navigate to SelectedItem
        drawerSelectedItemNavigation(mSelectedItem);

    }
    
    

    //*********** Setup Header of Navigation Drawer ********//

    public void setupExpandableDrawerHeader() {

        // Binding Layout Views of DrawerHeader
        drawer_header = (ImageView) findViewById(R.id.drawer_header);
        CircularImageView drawer_profile_image = (CircularImageView) findViewById(R.id.drawer_profile_image);
        TextView drawer_profile_name = (TextView) findViewById(R.id.drawer_profile_name);
        TextView drawer_profile_email = (TextView) findViewById(R.id.drawer_profile_email);
        TextView drawer_profile_welcome = (TextView) findViewById(R.id.drawer_profile_welcome);

        // Check if the User is Authenticated
        if (ConstantValues.IS_USER_LOGGED_IN) {
            // Check User's Info from SharedPreferences
            if (sharedPreferences.getString("userEmail", null) != null) {

                // Get User's Info from Local Database User_Info_DB
                User_Info_DB userInfoDB = new User_Info_DB();
                UserDetails userInfo = userInfoDB.getUserData(sharedPreferences.getString("userID", null));

                // Set User's Name, Email and Photo
                drawer_profile_email.setText(userInfo.getCustomersEmailAddress());
                drawer_profile_name.setText(userInfo.getCustomersFirstname()+" "+userInfo.getCustomersLastname());
                Glide.with(this)
                        .load(ConstantValues.ECOMMERCE_URL+userInfo.getCustomersPicture()).asBitmap()
                        .placeholder(R.drawable.profile)
                        .error(R.drawable.profile)
                        .into(drawer_profile_image);

            }
            else {
                // Set Default Name, Email and Photo
                drawer_profile_image.setImageResource(R.drawable.profile);
                drawer_profile_name.setText(getString(R.string.login_or_signup));
                drawer_profile_email.setText(getString(R.string.login_or_create_account));
            }

        }
        else {
            // Set Default Name, Email and Photo
            drawer_profile_welcome.setVisibility(View.GONE);
            drawer_profile_image.setImageResource(R.drawable.profile);
            drawer_profile_name.setText(getString(R.string.login_or_signup));
            drawer_profile_email.setText(getString(R.string.login_or_create_account));
        }


        // Handle DrawerHeader Click Listener
        drawer_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check if the User is Authenticated
                if (ConstantValues.IS_USER_LOGGED_IN) {

                    // Navigate to Update_Account Fragment
                    Fragment fragment = new SettingsFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_fragment, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .addToBackStack(getString(R.string.actionHome)).commit();

                    // Close NavigationDrawer
                    drawerLayout.closeDrawers();

                }
                else {
                    // Navigate to Login Activity
                    startActivity(new Intent(MainActivity.this, Login.class));
                    finish();
                    overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
                }
            }
        });
    }



    //*********** Setup Expandable List of Navigation Drawer ********//

    public void setupExpandableDrawerList() {
    
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        AppSettingsDetails appSettings = ((App) getApplicationContext()).getAppSettingsDetails();
    
        if (appSettings != null) {
            
            homeStyle = appSettings.getHomeStyle();
            categoryStyle = appSettings.getCategoryStyle();
            
            listDataHeader.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.actionHome)));
            listDataHeader.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.actionCategories)));
            listDataHeader.add(new Drawer_Items(R.drawable.ic_cart, getString(R.string.actionShop)));
            
            if (appSettings.getEditProfilePage() == 1)
                listDataHeader.add(new Drawer_Items(R.drawable.ic_account, getString(R.string.actionAccount)));
            if (appSettings.getMyOrdersPage() == 1)
                listDataHeader.add(new Drawer_Items(R.drawable.ic_order, getString(R.string.actionOrders)));
            if (appSettings.getShippingAddressPage() == 1)
                listDataHeader.add(new Drawer_Items(R.drawable.ic_location, getString(R.string.actionAddresses)));
            if (appSettings.getWishListPage() == 1)
                listDataHeader.add(new Drawer_Items(R.drawable.ic_favorite, getString(R.string.actionFavourites)));
            if (appSettings.getIntroPage() == 1)
                listDataHeader.add(new Drawer_Items(R.drawable.ic_intro, getString(R.string.actionIntro)));
            if (appSettings.getNewsPage() == 1)
                listDataHeader.add(new Drawer_Items(R.drawable.ic_newspaper, getString(R.string.actionNews)));
            if (appSettings.getContactUsPage() == 1)
                listDataHeader.add(new Drawer_Items(R.drawable.ic_chat_bubble, getString(R.string.actionContactUs)));
            if (appSettings.getAboutUsPage() == 1)
                listDataHeader.add(new Drawer_Items(R.drawable.ic_info, getString(R.string.actionAbout)));
            if (appSettings.getShareApp() == 1)
                listDataHeader.add(new Drawer_Items(R.drawable.ic_share, getString(R.string.actionShareApp)));
            if (appSettings.getRateApp() == 1)
                listDataHeader.add(new Drawer_Items(R.drawable.ic_star_circle, getString(R.string.actionRateApp)));
            if (appSettings.getSettingPage() == 1)
                listDataHeader.add(new Drawer_Items(R.drawable.ic_settings, getString(R.string.actionSettings)));
            
            // Add last Header Item in Drawer Header List
            if (ConstantValues.IS_USER_LOGGED_IN) {
                listDataHeader.add(new Drawer_Items(R.drawable.ic_logout, getString(R.string.actionLogout)));
            } else {
                listDataHeader.add(new Drawer_Items(R.drawable.ic_logout, getString(R.string.actionLogin)));
            }

            listDataHeader.add(new Drawer_Items(R.drawable.ic_sort, getString(R.string.actionComment)));
    
    
            if (!ConstantValues.IS_CLIENT_ACTIVE) {
                List<Drawer_Items> home_styles = new ArrayList<>();
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle1)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle2)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle3)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle4)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle5)));
    
                List<Drawer_Items> category_styles = new ArrayList<>();
                category_styles.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.categoryStyle1)));
                category_styles.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.categoryStyle2)));
                category_styles.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.categoryStyle3)));
                category_styles.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.categoryStyle4)));
                category_styles.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.categoryStyle5)));
                category_styles.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.categoryStyle6)));
    
                List<Drawer_Items> shop_childs = new ArrayList<>();
                shop_childs.add(new Drawer_Items(R.drawable.ic_arrow_up, getString(R.string.Newest)));
                shop_childs.add(new Drawer_Items(R.drawable.ic_sale, getString(R.string.topSeller)));
                shop_childs.add(new Drawer_Items(R.drawable.ic_star_circle, getString(R.string.superDeals)));
                shop_childs.add(new Drawer_Items(R.drawable.ic_most_liked, getString(R.string.mostLiked)));
    
    
                // Add Child to selective Headers
                listDataChild.put(listDataHeader.get(0), home_styles);
                listDataChild.put(listDataHeader.get(1), category_styles);
                listDataChild.put(listDataHeader.get(2), shop_childs);
            }
            
        }
        else {
            homeStyle = 1;
            categoryStyle = 1;
            
            listDataHeader.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.actionHome)));
            listDataHeader.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.actionCategories)));
            listDataHeader.add(new Drawer_Items(R.drawable.ic_cart, getString(R.string.actionShop)));
            listDataHeader.add(new Drawer_Items(R.drawable.ic_account, getString(R.string.actionAccount)));
            listDataHeader.add(new Drawer_Items(R.drawable.ic_order, getString(R.string.actionOrders)));
            listDataHeader.add(new Drawer_Items(R.drawable.ic_location, getString(R.string.actionAddresses)));
            listDataHeader.add(new Drawer_Items(R.drawable.ic_favorite, getString(R.string.actionFavourites)));
            listDataHeader.add(new Drawer_Items(R.drawable.ic_intro, getString(R.string.actionIntro)));
            listDataHeader.add(new Drawer_Items(R.drawable.ic_newspaper, getString(R.string.actionNews)));
            listDataHeader.add(new Drawer_Items(R.drawable.ic_info, getString(R.string.actionAbout)));
            listDataHeader.add(new Drawer_Items(R.drawable.ic_chat_bubble, getString(R.string.actionContactUs)));
            listDataHeader.add(new Drawer_Items(R.drawable.ic_share, getString(R.string.actionShareApp)));
            listDataHeader.add(new Drawer_Items(R.drawable.ic_star_circle, getString(R.string.actionRateApp)));
            listDataHeader.add(new Drawer_Items(R.drawable.ic_settings, getString(R.string.actionSettings)));
            // Add last Header Item in Drawer Header List
            if (ConstantValues.IS_USER_LOGGED_IN) {
                listDataHeader.add(new Drawer_Items(R.drawable.ic_logout, getString(R.string.actionLogout)));
            } else {
                listDataHeader.add(new Drawer_Items(R.drawable.ic_logout, getString(R.string.actionLogin)));
            }
    
    
            if (!ConstantValues.IS_CLIENT_ACTIVE) {
                List<Drawer_Items> home_styles = new ArrayList<>();
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle1)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle2)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle3)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle4)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle5)));
    
                List<Drawer_Items> category_styles = new ArrayList<>();
                category_styles.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.categoryStyle1)));
                category_styles.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.categoryStyle2)));
                category_styles.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.categoryStyle3)));
                category_styles.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.categoryStyle4)));
                category_styles.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.categoryStyle5)));
                category_styles.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.categoryStyle6)));
    
                List<Drawer_Items> shop_childs = new ArrayList<>();
                shop_childs.add(new Drawer_Items(R.drawable.ic_arrow_up, getString(R.string.Newest)));
                shop_childs.add(new Drawer_Items(R.drawable.ic_sale, getString(R.string.topSeller)));
                shop_childs.add(new Drawer_Items(R.drawable.ic_star_circle, getString(R.string.superDeals)));
                shop_childs.add(new Drawer_Items(R.drawable.ic_most_liked, getString(R.string.mostLiked)));
    
    
                // Add Child to selective Headers
                listDataChild.put(listDataHeader.get(0), home_styles);
                listDataChild.put(listDataHeader.get(1), category_styles);
                listDataChild.put(listDataHeader.get(2), shop_childs);
            }
            
        }
        
        

        // Initialize DrawerExpandableListAdapter
        drawerExpandableAdapter = new DrawerExpandableListAdapter(this, listDataHeader, listDataChild);

        // Bind ExpandableListView and set DrawerExpandableListAdapter to the ExpandableListView
        main_drawer_list = (ExpandableListView) findViewById(R.id.main_drawer_list);
        main_drawer_list.setAdapter(drawerExpandableAdapter);
        
        drawerExpandableAdapter.notifyDataSetChanged();

        

        // Handle Group Item Click Listener
        main_drawer_list.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if (drawerExpandableAdapter.getChildrenCount(groupPosition) < 1) {
                    // Navigate to Selected Main Item
                    if (groupPosition == 0) {
                        drawerSelectedItemNavigation(ConstantValues.DEFAULT_HOME_STYLE);
                    }
                    else if (groupPosition == 1) {
                        drawerSelectedItemNavigation(ConstantValues.DEFAULT_CATEGORY_STYLE);
                    }
                    else {
                        drawerSelectedItemNavigation(listDataHeader.get(groupPosition).getTitle());
                    }
                }
                return false;
            }
        });


        // Handle Child Item Click Listener
        main_drawer_list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                // Navigate to Selected Child Item
                drawerSelectedItemNavigation(listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getTitle());
                return false;
            }
        });


        // Handle Group Expand Listener
        main_drawer_list.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {}
        });
        // Handle Group Collapse Listener
        main_drawer_list.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {}
        });

    }



    //*********** Navigate to given Selected Item of NavigationDrawer ********//

    private void drawerSelectedItemNavigation(String selectedItem) {

        Fragment fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
    
        if(selectedItem.equalsIgnoreCase(getString(R.string.actionHome))) {
            mSelectedItem = selectedItem;
        
            // Navigate to any selected HomePage Fragment
            fragment = new HomePage_1();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        
            drawerLayout.closeDrawers();
        
        }
        else if(selectedItem.equalsIgnoreCase(getString(R.string.homeStyle1))) {
            mSelectedItem = selectedItem;

            // Navigate to HomePage1 Fragment
            fragment = new HomePage_1();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();

            drawerLayout.closeDrawers();

        }
        else if(selectedItem.equalsIgnoreCase(getString(R.string.homeStyle2))) {
            mSelectedItem = selectedItem;

            // Navigate to HomePage2 Fragment
            fragment = new HomePage_2();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();

            drawerLayout.closeDrawers();

        }
        else if(selectedItem.equalsIgnoreCase(getString(R.string.homeStyle3))) {
            mSelectedItem = selectedItem;

            // Navigate to HomePage3 Fragment
            fragment = new HomePage_3();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();

            drawerLayout.closeDrawers();

        }
        else if(selectedItem.equalsIgnoreCase(getString(R.string.homeStyle4))) {
            mSelectedItem = selectedItem;

            // Navigate to HomePage4 Fragment
            fragment = new HomePage_4();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();

            drawerLayout.closeDrawers();

        }
        else if(selectedItem.equalsIgnoreCase(getString(R.string.homeStyle5))) {
            mSelectedItem = selectedItem;

            // Navigate to HomePage5 Fragment
            fragment = new HomePage_5();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();

            drawerLayout.closeDrawers();

        }
        else if(selectedItem.equalsIgnoreCase(getString(R.string.actionCategories))) {
            mSelectedItem = selectedItem;
    
            Bundle bundle = new Bundle();
            bundle.putBoolean("isHeaderVisible", false);
    
            // Navigate to any selected CategoryPage Fragment
            fragment = new Categories_1();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();
    
            drawerLayout.closeDrawers();
            
        }
        else if(selectedItem.equalsIgnoreCase(getString(R.string.categoryStyle1))) {
            mSelectedItem = selectedItem;

            Bundle bundle = new Bundle();
            bundle.putBoolean("isHeaderVisible", false);

            // Navigate to Categories_1 Fragment
            fragment = new Categories_1();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        }
        else if(selectedItem.equalsIgnoreCase(getString(R.string.categoryStyle2))) {
            mSelectedItem = selectedItem;

            Bundle bundle = new Bundle();
            bundle.putBoolean("isHeaderVisible", false);

            // Navigate to Categories_2 Fragment
            fragment = new Categories_2();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        }
        else if(selectedItem.equalsIgnoreCase(getString(R.string.categoryStyle3))) {
            mSelectedItem = selectedItem;

            Bundle bundle = new Bundle();
            bundle.putBoolean("isHeaderVisible", false);

            // Navigate to Categories_3 Fragment
            fragment = new Categories_3();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        }
        else if(selectedItem.equalsIgnoreCase(getString(R.string.categoryStyle4))) {
            mSelectedItem = selectedItem;

            Bundle bundle = new Bundle();
            bundle.putBoolean("isHeaderVisible", false);

            // Navigate to Categories_4 Fragment
            fragment = new Categories_4();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        }
        else if(selectedItem.equalsIgnoreCase(getString(R.string.categoryStyle5))) {
            mSelectedItem = selectedItem;

            Bundle bundle = new Bundle();
            bundle.putBoolean("isHeaderVisible", false);

            // Navigate to Categories_5 Fragment
            fragment = new Categories_5();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        }
        else if(selectedItem.equalsIgnoreCase(getString(R.string.categoryStyle6))) {
            mSelectedItem = selectedItem;

            Bundle bundle = new Bundle();
            bundle.putBoolean("isHeaderVisible", false);

            // Navigate to Categories_6 Fragment
            fragment = new Categories_6();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        }
        else if(selectedItem.equalsIgnoreCase(getString(R.string.actionShop))) {
            mSelectedItem = selectedItem;
    
            Bundle bundle = new Bundle();
            bundle.putString("sortBy", "Newest");
            bundle.putBoolean("isMenuItem", true);
    
            // Navigate to Products Fragment
            fragment = new Products();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();
    
            drawerLayout.closeDrawers();
    
        }
        else if (selectedItem.equalsIgnoreCase(getString(R.string.Newest))) {
            mSelectedItem = selectedItem;

            Bundle bundle = new Bundle();
            bundle.putString("sortBy", "Newest");
            bundle.putBoolean("isMenuItem", true);

            // Navigate to Products Fragment
            fragment = new Products();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        }
        else if (selectedItem.equalsIgnoreCase(getString(R.string.superDeals))) {
            mSelectedItem = selectedItem;

            Bundle bundle = new Bundle();
            bundle.putString("sortBy", "special");
            bundle.putBoolean("isMenuItem", true);

            // Navigate to Products Fragment
            fragment = new Products();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        }
        else if (selectedItem.equalsIgnoreCase(getString(R.string.topSeller))) {
            mSelectedItem = selectedItem;

            Bundle bundle = new Bundle();
            bundle.putString("sortBy", "top seller");
            bundle.putBoolean("isMenuItem", true);

            // Navigate to Products Fragment
            fragment = new Products();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        }
        else if (selectedItem.equalsIgnoreCase(getString(R.string.mostLiked))) {
            mSelectedItem = selectedItem;

            Bundle bundle = new Bundle();
            bundle.putString("sortBy", "most liked");
            bundle.putBoolean("isMenuItem", true);

            // Navigate to Products Fragment
            fragment = new Products();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        }
        else if (selectedItem.equalsIgnoreCase(getString(R.string.actionAccount))) {
            if (ConstantValues.IS_USER_LOGGED_IN) {
                mSelectedItem = selectedItem;

                // Navigate to Update_Account Fragment
                fragment = new Update_Account();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(getString(R.string.actionHome)).commit();

                drawerLayout.closeDrawers();

            }
            else {
                // Navigate to Login Activity
                startActivity(new Intent(MainActivity.this, Login.class));
                finish();
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
            }

        }
        else if (selectedItem.equalsIgnoreCase(getString(R.string.actionOrders))) {
            if (ConstantValues.IS_USER_LOGGED_IN) {
                mSelectedItem = selectedItem;

                // Navigate to My_Orders Fragment
                fragment = new My_Orders();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(getString(R.string.actionHome)).commit();

                drawerLayout.closeDrawers();

            }
            else {
                // Navigate to Login Activity
                startActivity(new Intent(MainActivity.this, Login.class));
                finish();
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
            }

        }
        else if (selectedItem.equalsIgnoreCase(getString(R.string.actionAddresses))) {
            if (ConstantValues.IS_USER_LOGGED_IN) {
                mSelectedItem = selectedItem;

                // Navigate to My_Addresses Fragment
                fragment = new My_Addresses();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(getString(R.string.actionHome)).commit();

                drawerLayout.closeDrawers();

            }
            else {
                // Navigate to Login Activity
                startActivity(new Intent(MainActivity.this, Login.class));
                finish();
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
            }

        }
        else if (selectedItem.equalsIgnoreCase(getString(R.string.actionFavourites))) {
            if (ConstantValues.IS_USER_LOGGED_IN) {
                mSelectedItem = selectedItem;

                // Navigate to WishList Fragment
                fragment = new WishList();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(getString(R.string.actionHome)).commit();

                drawerLayout.closeDrawers();

            }
            else {
                // Navigate to Login Activity
                startActivity(new Intent(MainActivity.this, Login.class));
                finish();
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
            }

        }
        else if (selectedItem.equalsIgnoreCase(getString(R.string.actionNews))) {
            mSelectedItem = selectedItem;

            // Navigate to News Fragment
            fragment = new News();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        }
        else if (selectedItem.equalsIgnoreCase(getString(R.string.actionIntro))) {
            mSelectedItem = selectedItem;

            // Navigate to IntroScreen
            startActivity(new Intent(getBaseContext(), IntroScreen.class));

        }
        else if (selectedItem.equalsIgnoreCase(getString(R.string.actionAbout))) {
            mSelectedItem = selectedItem;

            // Navigate to About Fragment
            fragment = new About();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        }
        else if (selectedItem.equalsIgnoreCase(getString(R.string.actionShareApp))) {
            mSelectedItem = selectedItem;
    
            // Share App with the help of static method of Utilities class
            Utilities.shareMyApp(MainActivity.this);
    
            drawerLayout.closeDrawers();
    
        }
        else if (selectedItem.equalsIgnoreCase(getString(R.string.actionRateApp))) {
            mSelectedItem = selectedItem;
    
            // Rate App with the help of static method of Utilities class
            Utilities.rateMyApp(MainActivity.this);
    
            drawerLayout.closeDrawers();
    
        }
        else if (selectedItem.equalsIgnoreCase(getString(R.string.actionContactUs))) {
            mSelectedItem = selectedItem;

            // Navigate to ContactUs Fragment
            fragment = new ContactUs();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        }
        else if (selectedItem.equalsIgnoreCase(getString(R.string.actionSettings))) {
            mSelectedItem = selectedItem;

            // Navigate to SettingsFragment Fragment
            fragment = new SettingsFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        }
        else if (selectedItem.equalsIgnoreCase(getString(R.string.actionLogin))) {
            mSelectedItem = selectedItem;

            // Navigate to Login Activity
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);

        }
        else if (selectedItem.equalsIgnoreCase(getString(R.string.actionLogout))) {
            mSelectedItem = selectedItem;

            // Edit UserID in SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userID", "");
            editor.apply();

            // Set UserLoggedIn in MyAppPrefsManager
            MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(this);
            myAppPrefsManager.setUserLoggedIn(false);

            // Set isLogged_in of ConstantValues
            ConstantValues.IS_USER_LOGGED_IN = myAppPrefsManager.isUserLoggedIn();


            // Navigate to Login Activity
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
        }else if(selectedItem.equalsIgnoreCase(getString(R.string.actionComment))){
            // Navigate to SettingsFragment Fragment
            fragment = new Comment_Fragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();
        }
    }



    //*********** Called by the System when the Device's Configuration changes while Activity is Running ********//

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Configure ActionBarDrawerToggle with new Configuration
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    
    
    //*********** Invoked to Save the Instance's State when the Activity may be Temporarily Destroyed ********//
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the Selected NavigationDrawer Item
        outState.putString(SELECTED_ITEM_ID, mSelectedItem);
    }
    
    
    
    //*********** Set the Base Context for the ContextWrapper ********//
    
    @Override
    protected void attachBaseContext(Context newBase) {

        String languageCode = ConstantValues.LANGUAGE_CODE;
        if ("".equalsIgnoreCase(languageCode))
            languageCode = ConstantValues.LANGUAGE_CODE = "en";
        
        super.attachBaseContext(LocaleHelper.wrapLocale(newBase, languageCode));
    }
    
    
    
    //*********** Receives the result from a previous call of startActivityForResult(Intent, int) ********//
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    

    //*********** Creates the Activity's OptionsMenu ********//

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate toolbar_menu Menu
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        // Bind Menu Items
        MenuItem languageItem = menu.findItem(R.id.toolbar_ic_language);
        MenuItem searchItem = menu.findItem(R.id.toolbar_ic_search);
        MenuItem cartItem = menu.findItem(R.id.toolbar_ic_cart);

        languageItem.setVisible(false);


        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView image = (ImageView) inflater.inflate(R.layout.layout_animated_ic_cart, null);

        Drawable itemIcon = cartItem.getIcon().getCurrent();
        image.setImageDrawable(itemIcon);

        cartItem.setActionView(image);


        cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to My_Cart Fragment
                Fragment fragment = new My_Cart();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(getString(R.string.actionHome)).commit();
            }
        });


        // Tint Menu Icons with the help of static method of Utilities class
        Utilities.tintMenuIcon(MainActivity.this, languageItem, R.color.white);
        Utilities.tintMenuIcon(MainActivity.this, searchItem, R.color.white);
        Utilities.tintMenuIcon(MainActivity.this, cartItem, R.color.white);

        return true;
    }



    //*********** Prepares the OptionsMenu of Toolbar ********//

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // Clear OptionsMenu if NavigationDrawer is Opened
        if (drawerLayout.isDrawerOpen(navigationDrawer)) {

            MenuItem languageItem = menu.findItem(R.id.toolbar_ic_language);
            MenuItem searchItem = menu.findItem(R.id.toolbar_ic_search);
            MenuItem cartItem = menu.findItem(R.id.toolbar_ic_cart);

            languageItem.setVisible(true);
            searchItem.setVisible(false);
            cartItem.setVisible(false);

        }
        else {
            MenuItem cartItem = menu.findItem(R.id.toolbar_ic_cart);
    
            // Get No. of Cart Items with the static method of My_Cart Fragment
            int cartSize = My_Cart.getCartSize();

            
            // if Cart has some Items
            if (cartSize > 0) {

                // Animation for cart_menuItem
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_icon);
                animation.setRepeatMode(Animation.REVERSE);
                animation.setRepeatCount(1);

                cartItem.getActionView().startAnimation(animation);
                cartItem.getActionView().setAnimation(null);


                LayerDrawable icon = null;
                Drawable drawable = cartItem.getIcon();

                if (drawable instanceof DrawableWrapper) {
                    drawable = ((DrawableWrapper)drawable).getWrappedDrawable();
                }
                icon = (LayerDrawable) drawable;
                

                // Set BadgeCount on Cart_Icon with the static method of NotificationBadger class
                NotificationBadger.setBadgeCount(this, icon, String.valueOf(cartSize));

            } else {
                // Set the Icon for Empty Cart
                cartItem.setIcon(R.drawable.ic_cart_empty);
            }

        }

        return super.onPrepareOptionsMenu(menu);
    }



    //*********** Called whenever an Item in OptionsMenu is Selected ********//

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    
        Fragment fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();


        switch (item.getItemId()) {

            case R.id.toolbar_ic_language:

                drawerLayout.closeDrawers();

                // Navigate to Languages Fragment
                fragment = new Languages();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(getString(R.string.actionHome)).commit();
                break;

            case R.id.toolbar_ic_search:

                // Navigate to SearchFragment Fragment
                fragment = new SearchFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(getString(R.string.actionHome)).commit();
                break;

            case R.id.toolbar_ic_cart:

                // Navigate to My_Cart Fragment
                fragment = new My_Cart();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(getString(R.string.actionHome)).commit();
                break;

            default:
                break;
        }

        return true;
    }



    //*********** Called when the Activity has detected the User pressed the Back key ********//

    @Override
    public void onBackPressed() {

        // Get FragmentManager
        FragmentManager fm = getSupportFragmentManager();


        // Check if NavigationDrawer is Opened
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();

        }
        // Check if BackStack has some Fragments
        else  if (fm.getBackStackEntryCount() > 0) {

            // Pop previous Fragment
            fm.popBackStack();
            
        }
        // Check if doubleBackToExitPressed is true
        else if (doublePressedBackToExit) {
            super.onBackPressed();

        }
        else {
            this.doublePressedBackToExit = true;
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

            // Delay of 2 seconds
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    // Set doublePressedBackToExit false after 2 seconds
                    doublePressedBackToExit = false;
                }
            }, 2000);
        }
    }

    private void RequestResStatus() {
        //Restaurant_Status_DB restaurant_status_db=new Restaurant_Status_DB();
        //restaurant_status_db.deleteResStatusData("1");

        Call<ResStatus> call = APIClient.getInstance()
                .getResStatus();

        call.enqueue(new Callback<ResStatus>() {
            @Override
            public void onResponse(Call<ResStatus> call, Response<ResStatus> response) {

               // countryList.clear();
                String data = "";
                String Open_time = "",Close_time="";
                String Br_stime,Br_etime="";
                String Ln_stime,Ln_etime="";
                String Dr_stime,Dr_etime="";
                String Sn_stime,Sn_etime="";
                String open_time = "";
                String close_time="";

                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        countryList= response.body().getData();

                        // Add the Country Names to the countryNames List
                        for (int i=0;  i<countryList.size();  i++) {
                            countryNames.add(countryList.get(i).getStatus());
                            open_time=countryList.get(i).getOpen_time();
                            close_time=countryList.get(i).getClose_time();
                            data=countryList.get(i).getStatus();

                            String bf_s=countryList.get(i).getBreakfast_s_time();
                            String bf_e=countryList.get(i).getBreakfast_e_time();
                            String ln_s=countryList.get(i).getLunch_s_time();
                            String ln_e=countryList.get(i).getLunch_e_time();
                            String dn_s=countryList.get(i).getDinner_s_time();
                            String dn_e=countryList.get(i).getDinner_e_time();
                            String sn_s=countryList.get(i).getSnacks_s_time();
                            String sn_e=countryList.get(i).getSnacks_e_time();
                            String remarks=countryList.get(i).getRemarks();

                            myAppPrefsManager.setResRemarks(remarks);

//                            String endtime="2018-10-22 06:00:00";
//                            StringTokenizer tk = new StringTokenizer(open_time);
//                            String date = tk.nextToken();
//                            String time = tk.nextToken();
//
//                            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
//                            SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm a");
//                            Date dt;
//
//                            StringTokenizer tk1 = new StringTokenizer(close_time);
//                            String date1 = tk1.nextToken();
//                            String time1 = tk1.nextToken();
//
//                            SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm:ss");
//                            SimpleDateFormat sdfs1 = new SimpleDateFormat("hh:mm a");
//                            Date dt1;
//
//                            //String endtime="2018-10-22 06:00:00";
//                            StringTokenizer tk2 = new StringTokenizer(bf_s);
//                            String date2 = tk2.nextToken();
//                            String time2 = tk2.nextToken();
//
//                            SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm:ss");
//                            SimpleDateFormat sdfs2 = new SimpleDateFormat("hh:mm a");
//                            Date dt2;
//
//
//                            StringTokenizer tk3 = new StringTokenizer(bf_e);
//                            String date3 = tk3.nextToken();
//                            String time3 = tk3.nextToken();
//
//                            SimpleDateFormat sdf3 = new SimpleDateFormat("hh:mm:ss");
//                            SimpleDateFormat sdfs3 = new SimpleDateFormat("hh:mm a");
//                            Date dt3;
//
//                            StringTokenizer tk4 = new StringTokenizer(ln_s);
//                            String date4 = tk4.nextToken();
//                            String time4 = tk4.nextToken();
//
//                            SimpleDateFormat sdf4 = new SimpleDateFormat("hh:mm:ss");
//                            SimpleDateFormat sdfs4 = new SimpleDateFormat("hh:mm a");
//                            Date dt4;
//
//
//                            StringTokenizer tk5 = new StringTokenizer(ln_e);
//                            String date5 = tk5.nextToken();
//                            String time5 = tk5.nextToken();
//
//                            SimpleDateFormat sdf5 = new SimpleDateFormat("hh:mm:ss");
//                            SimpleDateFormat sdfs5 = new SimpleDateFormat("hh:mm a");
//                            Date dt5;
//
//                            StringTokenizer tk6 = new StringTokenizer(dn_s);
//                            String date6 = tk6.nextToken();
//                            String time6 = tk6.nextToken();
//
//                            SimpleDateFormat sdf6 = new SimpleDateFormat("hh:mm:ss");
//                            SimpleDateFormat sdfs6 = new SimpleDateFormat("hh:mm a");
//                            Date dt6;
//
//
//                            StringTokenizer tk7 = new StringTokenizer(dn_e);
//                            String date7 = tk7.nextToken();
//                            String time7 = tk7.nextToken();
//
//                            SimpleDateFormat sdf7 = new SimpleDateFormat("hh:mm:ss");
//                            SimpleDateFormat sdfs7 = new SimpleDateFormat("hh:mm a");
//                            Date dt7;
//
//                            StringTokenizer tk8 = new StringTokenizer(sn_s);
//                            String date8 = tk8.nextToken();
//                            String time8 = tk8.nextToken();
//
//                            SimpleDateFormat sdf8 = new SimpleDateFormat("hh:mm:ss");
//                            SimpleDateFormat sdfs8 = new SimpleDateFormat("hh:mm a");
//                            Date dt8;
//
//
//                            StringTokenizer tk9 = new StringTokenizer(sn_e);
//                            String date9 = tk9.nextToken();
//                            String time9 = tk9.nextToken();
//
//                            SimpleDateFormat sdf9 = new SimpleDateFormat("hh:mm:ss");
//                            SimpleDateFormat sdfs9 = new SimpleDateFormat("hh:mm a");
//                            Date dt9;
//
//                            try {
//                                dt = sdf.parse(time);
//                                dt1 = sdf1.parse(time1);
//                                dt2 = sdf2.parse(time2);
//                                dt3 = sdf3.parse(time3);
//                                dt4 = sdf4.parse(time4);
//                                dt5 = sdf5.parse(time5);
//                                dt6 = sdf6.parse(time6);
//                                dt7 = sdf7.parse(time7);
//                                dt8 = sdf8.parse(time8);
//                                dt9 = sdf9.parse(time9);
//                                Open_time=sdfs.format(dt);
//                                Close_time=sdfs1.format(dt1);
//                                Br_stime=sdfs2.format(dt2);
//                                Br_etime=sdfs3.format(dt3);
//                                Ln_stime=sdfs4.format(dt4);
//                                Ln_etime=sdfs5.format(dt5);
//                                Dr_stime=sdfs6.format(dt6);
//                                Dr_etime=sdfs7.format(dt7);
//                                Sn_stime=sdfs8.format(dt8);
//                                Sn_etime=sdfs9.format(dt9);
//
//                                System.out.println("Open Time: " +Open_time ); // <-- I got result here
//                                System.out.println("Close Time: " +Close_time); // <-- I got result here
//                                System.out.println("BR STime: " +Br_stime); // <-- I got result here
//                                System.out.println("BR ETime: " +Br_etime); // <-- I got result here
//                                System.out.println("LN STime: " +Ln_stime); // <-- I got result here
//                                System.out.println("LN ETime: " +Ln_etime); // <-- I got result here
//                                System.out.println("DN STime: " +Dr_stime); // <-- I got result here
//                                System.out.println("DN ETime: " +Dr_etime); // <-- I got result here
//                                System.out.println("SN STime: " +Sn_stime); // <-- I got result here
//                                System.out.println("SN ETime: " +Sn_etime); // <-- I got result here
//
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//
//                            Log.e("Remarks:",remarks);

                            Restaurant_Status_DB restaurant_status_db=new Restaurant_Status_DB();
                                ResDetails resDetails=new ResDetails();
                                resDetails.setId(1);
                                resDetails.setStatus(data);
                                resDetails.setOpen_time(open_time);
                                resDetails.setClose_time(close_time);
                                resDetails.setBreakfast_s_time(bf_s);
                                resDetails.setBreakfast_e_time(bf_e);
                                resDetails.setLunch_s_time(ln_s);
                                resDetails.setLunch_e_time(ln_e);
                                resDetails.setDinner_s_time(dn_s);
                                resDetails.setDinner_e_time(dn_e);
                                resDetails.setSnacks_s_time(sn_s);
                                resDetails.setSnacks_e_time(sn_e);
                                resDetails.setRemarks(remarks);
                                restaurant_status_db.insertRestaurantData(resDetails);
                            System.out.println("Open Time: " +open_time ); // <-- I got result here
                                System.out.println("Close Time: " +close_time); // <-- I got result here
                                System.out.println("BR STime: " +bf_s); // <-- I got result here
                                System.out.println("BR ETime: " +bf_e); // <-- I got result here
                                System.out.println("LN STime: " +ln_s); // <-- I got result here
                                System.out.println("LN ETime: " +ln_e); // <-- I got result here
                                System.out.println("DN STime: " +dn_s); // <-- I got result here
                                System.out.println("DN ETime: " +dn_e); // <-- I got result here
                                System.out.println("SN STime: " +sn_s); // <-- I got result here
                                System.out.println("SN ETime: " +sn_e); // <-- I got result here
                        }
                        Log.e("Restaurant Status:",data);

                        if (data.equals("open")){
                            Log.e("Restaurant Status:",data);
                            Date mToday = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                            String curTime = sdf.format(mToday);

                            try {
                                Calendar calendar = Calendar.getInstance();

                                SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
                                String  currentTime = df.format(calendar.getTime());
                                Log.e("Current Time1:",currentTime);

                                Calendar calendarcurrent = Calendar.getInstance();
                                Date dcur = new SimpleDateFormat("HH:mm:ss").parse(currentTime);
                                calendarcurrent.setTime(dcur);
                                calendarcurrent.add(Calendar.DATE, 1);

                                Date d = new SimpleDateFormat("HH:mm:ss").parse(open_time);
                                Calendar calendar0 = Calendar.getInstance();
                                calendar0.setTime(d);
                                calendar0.add(Calendar.DATE, 1);

                                Date d1 = new SimpleDateFormat("HH:mm:ss").parse(close_time);
                                Calendar calendar1 = Calendar.getInstance();
                                calendar1.setTime(d1);
                                calendar1.add(Calendar.DATE, 1);
//                                Date userDate = sdf.parse(curTime);
//                                Date start = sdf.parse(Open_time);
//                                Date end = sdf.parse(Close_time);

                                Date x = calendarcurrent.getTime();

                                if (x.after(calendar0.getTime()) && x.before(calendar1.getTime())) {
                                     Log.d("result", "Restaurant is Open");

                                }else{
                                    Log.e("Restaurant Status:", data);
                                    Toast.makeText(getApplicationContext(),"Restaurant is Closed",Toast.LENGTH_LONG).show();
                                    Fragment fragment = new Close_Fragment();
                                    FragmentManager fragmentManager = getSupportFragmentManager();
                                    fragmentManager.beginTransaction()
                                            .replace(R.id.main_fragment, fragment)
                                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                            .addToBackStack(getString(R.string.actionHome)).commit();
                                    Log.d("result", "Restaurant is Closed");
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }else{
                            Log.e("Restaurant Status:", data);
                            Toast.makeText(getApplicationContext(),"Restaurant is Closed",Toast.LENGTH_LONG).show();
                            Fragment fragment = new Close_Fragment();
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.main_fragment, fragment)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                    .addToBackStack(getString(R.string.actionHome)).commit();
                        }
                    }
                    else if (response.body().getSuccess().equalsIgnoreCase("0")) {


                    }
                    else {
                        // Unable to get Success status

                    }
                }
                else {
                    Toast.makeText(getBaseContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResStatus> call, Throwable t) {
                Toast.makeText(getBaseContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
    }


}

