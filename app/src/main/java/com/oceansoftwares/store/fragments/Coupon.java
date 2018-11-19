package com.oceansoftwares.store.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oceansoftwares.store.R;
import com.oceansoftwares.store.activities.MainActivity;

public class Coupon extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.close, container, false);
        // Enable Drawer Indicator with static variable actionBarDrawerToggle of MainActivity
        MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(true);

        // Set the Title of Toolbar
        //  ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.welcomeScreen));
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        return rootView;
    }
}
