package com.oceansoftwares.store.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.oceansoftwares.store.activities.adapters.CountryAdapter;
import com.oceansoftwares.store.customs.CircularImageView;

import com.oceansoftwares.store.R;

import com.oceansoftwares.store.constant.ConstantValues;
import com.oceansoftwares.store.customs.DialogLoader;
import com.oceansoftwares.store.customs.LocaleHelper;
import com.oceansoftwares.store.models.address_model.AddressData;
import com.oceansoftwares.store.models.address_model.Countries;
import com.oceansoftwares.store.models.address_model.CountryDetails;
import com.oceansoftwares.store.models.address_model.ZoneDetails;
import com.oceansoftwares.store.models.address_model.Zones;
import com.oceansoftwares.store.models.user_model.UserData;
import com.oceansoftwares.store.models.user_model.UserDetails;
import com.oceansoftwares.store.network.APIClient;
import com.oceansoftwares.store.utils.CheckPermissions;
import com.oceansoftwares.store.utils.Utilities;
import com.oceansoftwares.store.utils.ImagePicker;
import com.oceansoftwares.store.utils.ValidateInputs;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.oceansoftwares.store.app.App.getContext;


/**
 * SignUp activity handles User's Registration
 **/

public class Signup extends AppCompatActivity {

    View parentView;
    String profileImage;
    String customerID, addressID;
    String selectedZoneID, selectedCountryID;
    private static final int PICK_IMAGE_ID = 360;           // the number doesn't matter

    Toolbar toolbar;
    ActionBar actionBar;

    DialogLoader dialogLoader;
    
    AdView mAdView;
    Button signupBtn;
    FrameLayout banner_adView;
    TextView signup_loginText;
    TextView service_terms, privacy_policy, refund_policy, and_text;
    CircularImageView user_photo;
    FloatingActionButton user_photo_edit_fab;
    EditText user_firstname, user_lastname, user_email, user_password, user_mobile;
    EditText user_address, user_country, user_area, user_city, user_postcode;

    ArrayAdapter<String> zoneAdapter;
   // ArrayAdapter<String> countryAdapter;
    CountryAdapter countryAdapter;

    ArrayList<String> zoneNames;
    ArrayList<String> countryNames;
    List<ZoneDetails> zoneList = new ArrayList<>();
    List<CountryDetails> countryList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
    
        MobileAds.initialize(this, ConstantValues.ADMOBE_ID);
        

        // setting Toolbar
        toolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.signup));
        actionBar.setDisplayHomeAsUpEnabled(false);


        // Binding Layout Views
        user_photo = (CircularImageView) findViewById(R.id.user_photo);
        user_firstname = (EditText) findViewById(R.id.user_firstname);
        user_lastname = (EditText) findViewById(R.id.user_lastname);
        user_email = (EditText) findViewById(R.id.user_email);
        user_password = (EditText) findViewById(R.id.user_password);
        user_mobile = (EditText) findViewById(R.id.user_mobile);
        user_address = (EditText) findViewById(R.id.address);
        user_country = (EditText) findViewById(R.id.country);
        user_area = (EditText) findViewById(R.id.zone);
        user_city = (EditText) findViewById(R.id.city);
        user_postcode = (EditText) findViewById(R.id.postcode);
        signupBtn = (Button) findViewById(R.id.signupBtn);
        and_text = (TextView) findViewById(R.id.and);
        service_terms = (TextView) findViewById(R.id.service_terms);
        privacy_policy = (TextView) findViewById(R.id.privacy_policy);
        refund_policy = (TextView) findViewById(R.id.refund_policy);
        signup_loginText = (TextView) findViewById(R.id.signup_loginText);
        banner_adView = (FrameLayout) findViewById(R.id.banner_adView);
        user_photo_edit_fab = (FloatingActionButton) findViewById(R.id.user_photo_edit_fab);

        user_city.setText("Pondicherry");
    
        if (ConstantValues.IS_ADMOBE_ENABLED) {
            // Initialize Admobe
            mAdView = new AdView(Signup.this);
            mAdView.setAdSize(AdSize.BANNER);
            mAdView.setAdUnitId(ConstantValues.AD_UNIT_ID_BANNER);
            AdRequest adRequest = new AdRequest.Builder().build();
            banner_adView.addView(mAdView);
            mAdView.loadAd(adRequest);
            mAdView.setAdListener(new AdListener(){
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    banner_adView.setVisibility(View.VISIBLE);
                }
                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    banner_adView.setVisibility(View.GONE);
                }
            });
        }

        zoneNames = new ArrayList<>();
        countryNames = new ArrayList<>();

        // Request for Countries List
        RequestCountries();
        dialogLoader = new DialogLoader(Signup.this);
    
        and_text.setText(" "+getString(R.string.and)+" ");

        // Handle Touch event of input_country EditText
        user_country.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    countryAdapter=new CountryAdapter(getApplicationContext(),countryNames);
                   // countryAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
                   // countryAdapter.addAll(countryNames);

                    //AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(Signup.this, R.style.myDialog));
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                    dialog.setView(dialogView);
                    dialog.setCancelable(false);

                    Button dialog_button = (Button) dialogView.findViewById(R.id.dialog_button);
                    EditText dialog_input = (EditText) dialogView.findViewById(R.id.dialog_input);
                    TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                    ListView dialog_list = (ListView) dialogView.findViewById(R.id.dialog_list);

                    dialog_title.setText(getString(R.string.country));
                    dialog_list.setVerticalScrollBarEnabled(true);
                    dialog_list.setAdapter(countryAdapter);

                    dialog_input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                        @Override
                        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                            // Filter CountryAdapter
                            countryAdapter.getFilter().filter(charSequence);
                        }
                        @Override
                        public void afterTextChanged(Editable s) {}
                    });


                    final AlertDialog alertDialog = dialog.create();

                    dialog_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.show();



                    dialog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            alertDialog.dismiss();
                            final String selectedItem = String.valueOf(countryAdapter.getItem(position));

                            int countryID = 0;
                            user_country.setText(selectedItem);

                            if (!selectedItem.equalsIgnoreCase("Other")) {

                                for (int i=0;  i<countryList.size();  i++) {
                                    if (countryList.get(i).getCountriesName().equalsIgnoreCase(selectedItem)) {
                                        // Get the ID of selected Country
                                        countryID = countryList.get(i).getCountriesId();
                                    }
                                }

                            }

                            selectedCountryID = String.valueOf(countryID);

                            zoneNames.clear();
                            user_area.setText("");

                            // Request for all Zones in the selected Country
                            RequestZones(String.valueOf(selectedCountryID));
                        }
                    });
                }

                return false;
            }
        });

        // Handle Touch event of input_zone EditText
        user_area.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    countryAdapter=new CountryAdapter(getApplicationContext(),zoneNames);
                   // zoneAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
                    //zoneAdapter.addAll(zoneNames);

                   // AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(Signup.this, R.style.myDialog));
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                    dialog.setView(dialogView);
                    dialog.setCancelable(false);

                    Button dialog_button = (Button) dialogView.findViewById(R.id.dialog_button);
                    EditText dialog_input = (EditText) dialogView.findViewById(R.id.dialog_input);
                    TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                    ListView dialog_list = (ListView) dialogView.findViewById(R.id.dialog_list);

                    dialog_title.setText(getString(R.string.zone));
                    dialog_list.setVerticalScrollBarEnabled(true);
                    dialog_list.setAdapter(countryAdapter);

                    dialog_input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                        @Override
                        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                            // Filter ZoneAdapter
                            countryAdapter.getFilter().filter(charSequence);
                        }
                        @Override
                        public void afterTextChanged(Editable s) {}
                    });


                    final AlertDialog alertDialog = dialog.create();

                    dialog_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.show();



                    dialog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            alertDialog.dismiss();
                            final String selectedItem = String.valueOf(countryAdapter.getItem(position));

                            int zoneID = 0;
                            String pin="";
                            user_area.setText(selectedItem);

                            if (!countryAdapter.getItem(position).equals("Other")) {

                                for (int i=0;  i<zoneList.size();  i++) {
                                    if (zoneList.get(i).getZoneName().equalsIgnoreCase(selectedItem)) {
                                        // Get the ID of selected Country
                                        zoneID = zoneList.get(i).getZoneId();
                                        pin=zoneList.get(i).getZonePin();
                                    }
                                }
                            }

                            selectedZoneID = String.valueOf(zoneID);
                            Log.e("PINCODE:",pin);
                            user_postcode.setText(pin);
                        }
                    });
                }

                return false;
            }
        });




        privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(Signup.this, android.R.style.Theme_NoTitleBar);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_webview_fullscreen, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);
    
                final ImageButton dialog_button = (ImageButton) dialogView.findViewById(R.id.dialog_button);
                final TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                final WebView dialog_webView = (WebView) dialogView.findViewById(R.id.dialog_webView);
            
                dialog_title.setText(getString(R.string.privacy_policy));
            
            
                String description = ConstantValues.PRIVACY_POLICY;
                String styleSheet = "<style> " +
                                        "body{background:#ffffff; margin:0; padding:0} " +
                                        "p{color:#757575;} " +
                                        "img{display:inline; height:auto; max-width:100%;}" +
                                    "</style>";
            
                dialog_webView.setHorizontalScrollBarEnabled(false);
                dialog_webView.loadDataWithBaseURL(null, styleSheet+description, "text/html", "utf-8", null);
            
            
                final AlertDialog alertDialog = dialog.create();
    
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    alertDialog.getWindow().setStatusBarColor(ContextCompat.getColor(Signup.this, R.color.colorPrimaryDark));
                }
            
                dialog_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
            
                alertDialog.show();
            
            }
        });
    
        refund_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(Signup.this, android.R.style.Theme_NoTitleBar);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_webview_fullscreen, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);
    
                final ImageButton dialog_button = (ImageButton) dialogView.findViewById(R.id.dialog_button);
                final TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                final WebView dialog_webView = (WebView) dialogView.findViewById(R.id.dialog_webView);
            
                dialog_title.setText(getString(R.string.refund_policy));
            
            
                String description = ConstantValues.REFUND_POLICY;
                String styleSheet = "<style> " +
                                        "body{background:#ffffff; margin:0; padding:0} " +
                                        "p{color:#757575;} " +
                                        "img{display:inline; height:auto; max-width:100%;}" +
                                    "</style>";
            
                dialog_webView.setHorizontalScrollBarEnabled(false);
                dialog_webView.loadDataWithBaseURL(null, styleSheet+description, "text/html", "utf-8", null);
            
            
                final AlertDialog alertDialog = dialog.create();
    
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    alertDialog.getWindow().setStatusBarColor(ContextCompat.getColor(Signup.this, R.color.colorPrimaryDark));
                }
            
                dialog_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
            
                alertDialog.show();
            }
        });
    
        service_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(Signup.this, android.R.style.Theme_NoTitleBar);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_webview_fullscreen, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);
    
                final ImageButton dialog_button = (ImageButton) dialogView.findViewById(R.id.dialog_button);
                final TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                final WebView dialog_webView = (WebView) dialogView.findViewById(R.id.dialog_webView);
            
                dialog_title.setText(getString(R.string.service_terms));
            
            
                String description = ConstantValues.TERMS_SERVICES;
                String styleSheet = "<style> " +
                                        "body{background:#ffffff; margin:0; padding:0} " +
                                        "p{color:#757575;} " +
                                        "img{display:inline; height:auto; max-width:100%;}" +
                                    "</style>";
            
                dialog_webView.setHorizontalScrollBarEnabled(false);
                dialog_webView.loadDataWithBaseURL(null, styleSheet+description, "text/html", "utf-8", null);
            
            
                final AlertDialog alertDialog = dialog.create();
    
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    alertDialog.getWindow().setStatusBarColor(ContextCompat.getColor(Signup.this, R.color.colorPrimaryDark));
                }
            
                dialog_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
            
                alertDialog.show();
            }
        });


        // Handle Click event of user_photo_edit_fab FAB
        user_photo_edit_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckPermissions.is_CAMERA_PermissionGranted()  &&  CheckPermissions.is_STORAGE_PermissionGranted()) {
                    pickImage();
                }
                else {
                    ActivityCompat.requestPermissions
                            (
                                    Signup.this,
                                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    CheckPermissions.PERMISSIONS_REQUEST_CAMERA
                            );
                }
            }
        });


        // Handle Click event of signup_loginText TextView
        signup_loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish SignUpActivity to goto the LoginActivity
                finish();
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
            }
        });


        // Handle Click event of signupBtn Button
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate Login Form Inputs
                boolean isValidData = validateForm();

                if (isValidData) {
                    parentView = v;
        
                    // Proceed User Registration
                    processRegistration();
                }
            }
        });
    }



    //*********** Picks User Profile Image from Gallery or Camera ********//

    private void pickImage() {
        // Get Intent with Options of Image Picker Apps from the static method of ImagePicker class
        Intent chooseImageIntent = ImagePicker.getImagePickerIntent(Signup.this);

        // Start Activity with Image Picker Intent
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }
    
    
    
    //*********** Receives the result from a previous call of startActivityForResult(Intent, int) ********//
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // Handle Activity Result
            if (requestCode == PICK_IMAGE_ID) {

                // Get the User Selected Image as Bitmap from the static method of ImagePicker class
                Bitmap bitmap = ImagePicker.getImageFromResult(Signup.this, resultCode, data);
    
                // Upload the Bitmap to ImageView
                user_photo.setImageBitmap(bitmap);
                
                // Get the converted Bitmap as Base64ImageString from the static method of Helper class
                profileImage = Utilities.getBase64ImageStringFromBitmap(bitmap);
                
            }
        }
    }
    
    
    
    //*********** This method is invoked for every call on requestPermissions(Activity, String[], int) ********//
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    
        if (requestCode == CheckPermissions.PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // The Camera and Storage Permission is granted
                pickImage();
            }
            else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(Signup.this, Manifest.permission.CAMERA)) {
                    // Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this);
                    builder.setTitle(getString(R.string.permission_camera_storage));
                    builder.setMessage(getString(R.string.permission_camera_storage_needed));
                    builder.setPositiveButton(getString(R.string.grant), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            ActivityCompat.requestPermissions
                                    (
                                            Signup.this,
                                            new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            CheckPermissions.PERMISSIONS_REQUEST_CAMERA
                                    );
                        }
                    });
                    builder.setNegativeButton(getString(R.string.not_now), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
                else {
                    Toast.makeText(Signup.this,getString(R.string.permission_rejected), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    
    
    
    //*********** Proceed User Registration Request ********//

    private void processRegistration() {

        dialogLoader.showProgressDialog();
    
        
        Call<UserData> call = APIClient.getInstance()
                .processRegistration
                        (
                            user_firstname.getText().toString().trim(),
                            user_lastname.getText().toString().trim(),
                            user_email.getText().toString().trim(),
                            user_password.getText().toString().trim(),
                            user_mobile.getText().toString().trim(),
                            profileImage
                        );

        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, retrofit2.Response<UserData> response) {

                dialogLoader.hideProgressDialog();

                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {
                        getCustomer();
                        // Finish SignUpActivity to goto the LoginActivity
                       // finish();
                        // Navigate to Billing_Address Fragment
//                        android.app.Fragment fragment = new Billing_Address();
//                        android.app.FragmentManager fragmentManager = getFragmentManager();
//                        fragmentManager.beginTransaction().replace(R.id.main_fragment, fragment)
//                                .addToBackStack(null).commit();


                    }
                    else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        // Get the Error Message from Response
                        String message = response.body().getMessage();
                        Snackbar.make(parentView, message, Snackbar.LENGTH_SHORT).show();
                        
                    }
                    else {
                        // Unable to get Success status
                        Toast.makeText(Signup.this, getString(R.string.unexpected_response), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    // Show the Error Message
                    Toast.makeText(Signup.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(Signup.this, "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
    }

    //*********** Get Countries List from the Server ********//

    private void RequestCountries() {

        Call<Countries> call = APIClient.getInstance()
                .getCountries();

        call.enqueue(new Callback<Countries>() {
            @Override
            public void onResponse(Call<Countries> call, Response<Countries> response) {

                if (response.isSuccessful()) {

                    // Check the Success status
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        countryList = response.body().getData();

                        // Add the Country Names to the countryNames List
                        for (int i=0;  i<countryList.size();  i++) {
                            countryNames.add(countryList.get(i).getCountriesName());
                        }

                        // countryNames.add("Other");
                        Log.e("Country Name:", String.valueOf(countryNames));

                    }
                    else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        //Snackbar.make(rootView, response.body().getMessage(), Snackbar.LENGTH_LONG).show();
                        Toast.makeText(getContext(), ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    }
                    else {
                        // Unable to get Success status
                        Toast.makeText(getContext(), ""+getString(R.string.unexpected_response), Toast.LENGTH_SHORT).show();
                       // Snackbar.make(rootView, getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Countries> call, Throwable t) {
                Toast.makeText(getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
    }



    //*********** Get Zones List of the Country from the Server ********//

    private void RequestZones(String countryID) {

        Call<Zones> call = APIClient.getInstance()
                .getZones
                        (
                                countryID
                        );

        call.enqueue(new Callback<Zones>() {
            @Override
            public void onResponse(Call<Zones> call, Response<Zones> response) {

                if (response.isSuccessful()) {

                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        zoneList = response.body().getData();

                        // Add the Zone Names to the zoneNames List
                        for (int i=0;  i<zoneList.size();  i++){
                            zoneNames.add(zoneList.get(i).getZoneName());
                        }
                        Log.e("Zone Name:", String.valueOf(zoneNames));
                        // zoneNames.add("Other");

                    }
                    else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                       // Snackbar.make(rootView, response.body().getMessage(), Snackbar.LENGTH_LONG).show();
                        Toast.makeText(getContext(), ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    }
                    else {
                        // Unable to get Success status
                        Toast.makeText(getContext(), ""+getString(R.string.unexpected_response), Toast.LENGTH_SHORT).show();
                       // Snackbar.make(rootView, getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Zones> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getCustomer() {
        Call<UserData> call = APIClient.getInstance()
                .getcustomerId(user_email.getText().toString());

        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {

                if (response.isSuccessful()) {

                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        UserDetails userDetails = response.body().getData().get(0);
                        String id=userDetails.getCustomersId();
                        addUserAddress(id);

                        Log.e("Customers ID : ", String.valueOf(id));

                    }
                    else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        Toast.makeText(getApplicationContext(),""+response.body(),Toast.LENGTH_LONG).show();


                    }
                    else {
                        // Unable to get Success status
                        Toast.makeText(getApplicationContext(),""+getString(R.string.unexpected_response),Toast.LENGTH_LONG).show();

                    }
                }
                else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                Toast.makeText(getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
    }



    //*********** Proceed the Request of New Address ********//

    public void addUserAddress(String customerID) {

        final String customers_default_address_id = getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).getString("userDefaultAddressID", "");


        Call<AddressData> call = APIClient.getInstance()
                .addUserAddress
                        (
                                customerID,
                                user_firstname.getText().toString().trim(),
                                user_lastname.getText().toString().trim(),
                                user_address.getText().toString().trim(),
                                user_postcode.getText().toString().trim(),
                                user_city.getText().toString().trim(),
                                selectedCountryID,
                                selectedZoneID,
                                customers_default_address_id
                        );

        call.enqueue(new Callback<AddressData>() {
            @Override
            public void onResponse(Call<AddressData> call, Response<AddressData> response) {

                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {
                        // Address has been added to User's Addresses
                        // Navigate to Addresses fragment
                        finish();
                        //overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
                       // ((MainActivity) getApplicationContext()).getSupportFragmentManager().popBackStack();


                    }
                    else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                       // Snackbar.make(rootView, response.body().getMessage(), Snackbar.LENGTH_LONG).show();

                    }
                    else {
                        // Unable to get Success status
                        Toast.makeText(getContext(),getString(R.string.unexpected_response) , Toast.LENGTH_SHORT).show();
                        //Snackbar.make(rootView, getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AddressData> call, Throwable t) {
                Toast.makeText(getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
    }






    //*********** Validate SignUp Form Inputs ********//

    private boolean validateForm() {
        if (!ValidateInputs.isValidName(user_firstname.getText().toString().trim())) {
            user_firstname.setError(getString(R.string.invalid_first_name));
            return false;
        } else if (!ValidateInputs.isValidName(user_lastname.getText().toString().trim())) {
            user_lastname.setError(getString(R.string.invalid_last_name));
            return false;
        } else if (!ValidateInputs.isValidEmail(user_email.getText().toString().trim())) {
            user_email.setError(getString(R.string.invalid_email));
            return false;
        } else if (!ValidateInputs.isValidPassword(user_password.getText().toString().trim())) {
            user_password.setError(getString(R.string.invalid_password));
            return false;
        } else if (!ValidateInputs.isValidNumber(user_mobile.getText().toString().trim())) {
            user_mobile.setError(getString(R.string.invalid_contact));
            return false;
        } else {
            return true;
        }
    }
    
    
    
    //*********** Set the Base Context for the ContextWrapper ********//
    
    @Override
    protected void attachBaseContext(Context newBase) {
    
        String languageCode = ConstantValues.LANGUAGE_CODE;
        if ("".equalsIgnoreCase(languageCode))
            languageCode = ConstantValues.LANGUAGE_CODE = "en";
    
        super.attachBaseContext(LocaleHelper.wrapLocale(newBase, languageCode));
    }
    
    
    
    //*********** Called when the Activity has detected the User pressed the Back key ********//
    
    @Override
    public void onBackPressed() {
        // Finish SignUpActivity to goto the LoginActivity
        finish();
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
    }
    
}

