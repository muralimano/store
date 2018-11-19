package com.oceansoftwares.store.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;


import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.oceansoftwares.store.R;
import com.oceansoftwares.store.activities.MainActivity;
import com.oceansoftwares.store.app.MyAppPrefsManager;
import com.oceansoftwares.store.models.ResDetails;
import com.oceansoftwares.store.models.ResStatus;
import com.oceansoftwares.store.network.APIClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

public class Close_Fragment extends Fragment {
   Button close;
   TextView reason;

    List<ResDetails> countryList;
    List<String> countryNames;

    MyAppPrefsManager myAppPrefsManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.close, container, false);

        // Enable Drawer Indicator with static variable actionBarDrawerToggle of MainActivity
        MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(true);

        // Set the Title of Toolbar
      //  ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.welcomeScreen));
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();



        close=(Button)rootView.findViewById(R.id.close);
        reason=(TextView)rootView.findViewById(R.id.official_web);

        myAppPrefsManager = new MyAppPrefsManager(getContext());

        String remarks=myAppPrefsManager.getResRemarks();
        Log.e("Remarks: ",remarks);

        if(remarks.isEmpty()){
            reason.setText("Restaurant is Closed");
        }else {
            reason.setText("Restaurant Closed Due To "+remarks);
        }



        countryNames = new ArrayList<>();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().finishAffinity();

            }
        });



        return rootView;
    }

    private void RequestResStatus() {

        Call<ResStatus> call = APIClient.getInstance()
                .getResStatus();

        call.enqueue(new Callback<ResStatus>() {
            @Override
            public void onResponse(Call<ResStatus> call, Response<ResStatus> response) {

                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        countryList= response.body().getData();

                        // Add the Country Names to the countryNames List
                        for (int i=0;  i<countryList.size();  i++) {
                            countryNames.add(countryList.get(i).getStatus());
                        }

                    }
                    else if (response.body().getSuccess().equalsIgnoreCase("0")) {


                    }
                    else {
                        // Unable to get Success status

                    }
                }
                else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResStatus> call, Throwable t) {
                Toast.makeText(getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
    }

}
