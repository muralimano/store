package com.oceansoftwares.store.fragments;


import android.os.Build;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.oceansoftwares.store.activities.MainActivity;
import com.oceansoftwares.store.R;

import java.util.ArrayList;
import java.util.List;

import com.oceansoftwares.store.activities.adapters.OrdersListAdapter;
import com.oceansoftwares.store.constant.ConstantValues;
import com.oceansoftwares.store.customs.DialogLoader;
import com.oceansoftwares.store.models.order_model.OrderData;
import com.oceansoftwares.store.models.order_model.OrderDetails;
import com.oceansoftwares.store.network.APIClient;

import retrofit2.Call;
import retrofit2.Callback;


public class My_Orders extends Fragment {

    View rootView;
    String customerID;
    
    AdView mAdView;
    TextView emptyRecord;
    FrameLayout banner_adView;
    RecyclerView orders_recycler;
    SwipeRefreshLayout swipeRefreshLayout;

    DialogLoader dialogLoader;
    OrdersListAdapter ordersListAdapter;

    List<OrderDetails> ordersList = new ArrayList<>();


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.my_orders, container, false);

        MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.actionOrders));

        // Get the CustomerID from SharedPreferences
        customerID = this.getContext().getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).getString("userID", "");
        
        
        // Binding Layout Views
        emptyRecord = (TextView) rootView.findViewById(R.id.empty_record);
        banner_adView = (FrameLayout) rootView.findViewById(R.id.banner_adView);
        orders_recycler = (RecyclerView) rootView.findViewById(R.id.orders_recycler);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.simpleSwipeRefreshLayout);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // cancel the Visual indication of a refresh
                swipeRefreshLayout.setRefreshing(false);
                ordersList.clear();
                RequestMyOrders();
            }
        });
    
    
        if (ConstantValues.IS_ADMOBE_ENABLED) {
            // Initialize Admobe
            mAdView = new AdView(getContext());
            mAdView.setAdSize(AdSize.BANNER);
            mAdView.setAdUnitId(ConstantValues.AD_UNIT_ID_BANNER);
            AdRequest adRequest = new AdRequest.Builder().build();
            banner_adView.addView(mAdView);
            mAdView.loadAd(adRequest);
        }
        
        

        // Hide some of the Views
        emptyRecord.setVisibility(View.GONE);


        dialogLoader = new DialogLoader(getContext());


        // Request for User's Orders
        RequestMyOrders();


        return rootView;
    }



    //*********** Adds Orders returned from the Server to the OrdersList ********//

    private void addOrders(OrderData orderData) {

        // Add Orders to ordersList from the List of OrderData
        ordersList = orderData.getData();
        String data=ordersList.get(0).getLastModified().toString();
        Log.e("LastModified Date:",data);


        // Initialize the OrdersListAdapter for RecyclerView
        ordersListAdapter = new OrdersListAdapter(getContext(), customerID, ordersList);

        // Set the Adapter and LayoutManager to the RecyclerView
        orders_recycler.setAdapter(ordersListAdapter);
        orders_recycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        
        ordersListAdapter.notifyDataSetChanged();
    }



    //*********** Request User's Orders from the Server ********//

    public void RequestMyOrders() {

        dialogLoader.showProgressDialog();

        Call<OrderData> call = APIClient.getInstance()
                .getOrders
                        (
                                customerID,
                                ConstantValues.LANGUAGE_ID
                        );

        call.enqueue(new Callback<OrderData>() {
            @Override
            public void onResponse(Call<OrderData> call, retrofit2.Response<OrderData> response) {

                dialogLoader.hideProgressDialog();

                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {
                        
                        // Orders have been returned. Add Orders to the ordersList
                        addOrders(response.body());
                        
                    }
                    else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        emptyRecord.setVisibility(View.VISIBLE);
                        Snackbar.make(rootView, response.body().getMessage(), Snackbar.LENGTH_LONG).show();
    
                    }
                    else {
                        // Unable to get Success status
                        emptyRecord.setVisibility(View.VISIBLE);
                        Snackbar.make(rootView, getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderData> call, Throwable t) {
                Toast.makeText(getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
    }

}

