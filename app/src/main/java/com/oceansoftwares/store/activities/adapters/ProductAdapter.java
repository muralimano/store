package com.oceansoftwares.store.activities.adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import com.oceansoftwares.store.activities.MainActivity;

import com.oceansoftwares.store.databases.Restaurant_Status_DB;
import com.oceansoftwares.store.databases.User_Cart_DB;
import com.oceansoftwares.store.models.ResDetails;
import com.oceansoftwares.store.models.product_model.Option;
import com.oceansoftwares.store.models.product_model.Value;
import com.oceansoftwares.store.utils.Utilities;
import com.oceansoftwares.store.activities.Login;
import com.oceansoftwares.store.databases.User_Recents_DB;
import com.oceansoftwares.store.fragments.My_Cart;
import com.oceansoftwares.store.fragments.Product_Description;
import com.oceansoftwares.store.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.oceansoftwares.store.constant.ConstantValues;
import com.oceansoftwares.store.models.cart_model.CartProduct;
import com.oceansoftwares.store.models.cart_model.CartProductAttributes;
import com.oceansoftwares.store.models.product_model.ProductDetails;


/**
 * ProductAdapter is the adapter class of RecyclerView holding List of Products in All_Products and other Product relevant Classes
 **/

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    private Context context;
    private String customerID;
    private Boolean isGridView;
    private Boolean isHorizontal;

    private User_Recents_DB recents_db;
    private User_Cart_DB cart_db;
    private List<ProductDetails> productList;

    String show="";
    String show1,show2,show3="";
    String EndTime="";
    String curTime="";

    String Br_time,Ln_time,Sn_time,Dn_time="";

    String currentTime="";

    String SBr_time,SLn_time,SSn_time,SDn_time="";
    String EBr_time,ELn_time,ESn_time,EDn_time="";


    public ProductAdapter(Context context, List<ProductDetails> productList, Boolean isHorizontal) {
        this.context = context;
        this.productList = productList;
        this.isHorizontal = isHorizontal;

        recents_db = new User_Recents_DB();
        cart_db=new User_Cart_DB();
        customerID = this.context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE).getString("userID", "");
    }



    //********** Called to Inflate a Layout from XML and then return the Holder *********//

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View itemView = null;

        // Check which Layout will be Inflated
        if (isHorizontal) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_grid_sm, parent, false);
        }
        else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(isGridView ? R.layout.layout_product_grid_lg : R.layout.layout_product_list_lg, parent, false);
        }


        // Return a new holder instance
        return new MyViewHolder(itemView);
    }



    //********** Called by RecyclerView to display the Data at the specified Position *********//

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        Restaurant_Status_DB restaurant_status_db=new Restaurant_Status_DB();
        ResDetails resDetails=new ResDetails();
        resDetails=restaurant_status_db.getUserData("1");
        //String status=resDetails.getComment();
        String Breakfast_Stime=resDetails.getBreakfast_s_time();
        String Breakfast_Etime=resDetails.getBreakfast_e_time();
        String Lunch_Stime=resDetails.getLunch_s_time();
        String Lunch_Etime=resDetails.getLunch_e_time();
        String Dinner_Stime=resDetails.getDinner_s_time();
        String Dinner_Etime=resDetails.getDinner_e_time();
        String Snacks_Stime=resDetails.getSnacks_s_time();
        String Snacks_Etime=resDetails.getSnacks_e_time();

        try {
            Calendar calendar = Calendar.getInstance();

            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            String  currentTime = df.format(calendar.getTime());
            Log.e("Current Time1:",currentTime);

            Calendar calendarcurrent = Calendar.getInstance();
            Date dcur = new SimpleDateFormat("HH:mm:ss").parse(currentTime);
            calendarcurrent.setTime(dcur);
            calendarcurrent.add(Calendar.DATE, 1);

            Date d = new SimpleDateFormat("HH:mm:ss").parse(Snacks_Stime);
            Calendar calendar0 = Calendar.getInstance();
            calendar0.setTime(d);
            calendar0.add(Calendar.DATE, 1);

            Date d1 = new SimpleDateFormat("HH:mm:ss").parse(Snacks_Etime);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(d1);
            calendar1.add(Calendar.DATE, 1);

            Date d2 = new SimpleDateFormat("HH:mm:ss").parse(Dinner_Stime);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(d2);
            calendar2.add(Calendar.DATE, 1);

            Date d3 = new SimpleDateFormat("HH:mm:ss").parse(Dinner_Etime);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(d3);
            calendar3.add(Calendar.DATE, 1);

            Date d4 = new SimpleDateFormat("HH:mm:ss").parse(Breakfast_Stime);
            Calendar calendar4 = Calendar.getInstance();
            calendar4.setTime(d4);
            calendar4.add(Calendar.DATE, 1);

            Date d5 = new SimpleDateFormat("HH:mm:ss").parse(Breakfast_Etime);
            Calendar calendar5 = Calendar.getInstance();
            calendar5.setTime(d5);
            calendar5.add(Calendar.DATE, 1);

            Date d6 = new SimpleDateFormat("HH:mm:ss").parse(Lunch_Stime);
            Calendar calendar6 = Calendar.getInstance();
            calendar6.setTime(d6);
            calendar6.add(Calendar.DATE, 1);

            Date d7 = new SimpleDateFormat("HH:mm:ss").parse(Lunch_Etime);
            Calendar calendar7 = Calendar.getInstance();
            calendar7.setTime(d7);
            calendar7.add(Calendar.DATE, 1);

            Date x = calendarcurrent.getTime();
            if (x.after(calendar0.getTime()) && x.before(calendar1.getTime())) {
                //checkes whether the current time is between 14:49:00 and 20:11:13.

                System.out.println("TIME CORRECT STATUS:"+"Snacks");
                Log.e("Snacks Timing:","START AT"+calendar0.getTime()+"End At"+calendar1.getTime());
                show="Snacks";
                show1="Dinner";
                show2="Breakfast";
                show3="Lunch";
            }else if(x.after(calendar2.getTime())&& x.before(calendar3.getTime())){
                System.out.println("TIME CORRECT STATUS:"+"Dinner");
                Log.e("Dinner Timing:","START AT"+calendar2.getTime()+"End At"+calendar3.getTime());
                show="Dinner";
                show1="Breakfast";
                show2="Lunch";
                show3="Snacks";
            }else if(x.after(calendar4.getTime())&& x.before(calendar5.getTime())){
                show="Breakfast";
                show1="Lunch";
                show2="Snacks";
                show3="Dinner";
                System.out.println("TIME CORRECT STATUS:"+"Breakfast");
                Log.e("Breakfast Timing:","START AT"+calendar4.getTime()+"End At"+calendar5.getTime());
            }else if(x.after(calendar6.getTime())&& x.before(calendar7.getTime())){
                show="Lunch";
                show1="Breakfast";
                show2="Snacks";
                show3="Dinner";
                System.out.println("TIME CORRECT STATUS:"+"Lunch");
                Log.e("Breakfast Timing:","START AT"+calendar6.getTime()+"End At"+calendar7.getTime());
            }else{
                System.out.println("TIME CORRECT STATUS:"+"False");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



        Log.e("Lunchtime:",Lunch_Stime);
        Log.d("Start", String.valueOf(Breakfast_Etime));
        Calendar calendar = Calendar.getInstance();
       // Log.e("STORED STATUS:",status);

        try {
            Calendar calendar0 = Calendar.getInstance();

            Calendar calendarbr = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");

            Date start = sdf.parse(Breakfast_Stime);
            Date end = sdf.parse(Breakfast_Etime);
            Date start1 = sdf.parse(Lunch_Stime);
            Date end1 = sdf.parse(Lunch_Etime);
            Date start2 = sdf.parse(Snacks_Stime);
            Date end2 = sdf.parse(Snacks_Etime);
            Date start3 = sdf.parse(Dinner_Stime);
            Date end3 = sdf.parse(Dinner_Etime);
            Date userDate = sdf.parse(curTime);





            if (userDate.after(start) && userDate.before(end)) {
               // Log.d("result", "falls between start and end , Lunch ");
                show="Breakfast";
                show1="Lunch";
                show2="Snacks";
                show3="Dinner";
                //EndTime= Breakfast_Etime;

            }else if(userDate.after(start1) && userDate.before(end1)){
                show="Lunch";
                show1="Breakfast";
                show2="Snacks";
                show3="Dinner";
                //EndTime= Lunch_Etime;
               // Log.e("END Time:",Lunch_Etime);
                Log.d("result", "falls between start1 and end1 , Lunch ");
            }else if(userDate.after(start2) && userDate.before(end2)){
                show="Snacks";
                show1="Dinner";
                show2="Breakfast";
                show3="Lunch";

                //EndTime= Snacks_Etime;
                //Log.e("END Time:",Snacks_Etime);
               // Log.d("result", "falls between start2 and end2 , Snacks ");
            }else if(userDate.after(start3) && userDate.before(end3)){
                show="Dinner";
                show1="Breakfast";
                show2="Lunch";
                show3="Snacks";
                //EndTime= Dinner_Etime;
               // Log.e("END Time:",Dinner_Etime);
                //Log.d("result", "falls between start3 and end3 , Dinner ");
            }
            else{
                Log.d("result", "does not fall between start and end , go to screen 3 ");
            }
        } catch (Exception e) {
            // Invalid date was entered
        }

        if (position != productList.size()) {

            // Get the data model based on Position
            final ProductDetails product = productList.get(position);
            //holder.cart_item_quantity.setText("1");
            // Check if the Product is already in the Cart
            if (My_Cart.checkCartHasProduct(product.getProductsId())) {
                CartProduct cartProduct = new CartProduct();
                My_Cart my_cart = new My_Cart();
                cartProduct = my_cart.GetCartProduct(product.getProductsId());
                String QTY = String.valueOf(cartProduct.getCustomersBasketProduct().getCustomersBasketQuantity());
                //Log.e("CartQTY:", String.valueOf(cartProduct.getCustomersBasketProduct().getCustomersBasketQuantity()));
                holder.cart_item_quantity.setText(QTY);
                //  holder.product_checked.setVisibility(View.VISIBLE);
                holder.product_checked.setVisibility(View.GONE);
                holder.number.setVisibility(View.VISIBLE);
                holder.product_add_cart_btn.setVisibility(View.GONE);

            } else {
                holder.product_checked.setVisibility(View.GONE);
                //holder.product_add_cart_btn.setVisibility(View.GONE);
                 holder.product_add_cart_btn.setVisibility(View.VISIBLE);
                holder.number.setVisibility(View.GONE);

//                if (product.getProductsQuantity() < 1) {
//                    holder.number.setVisibility(View.GONE);
//                    holder.product_add_cart_btn.setText(context.getString(R.string.outOfStock));
//                    holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_button_red));
//                } else {
//                    holder.product_add_cart_btn.setText(context.getString(R.string.addToCart));
//                    holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_button_green));
//                }

                if((product.getProductsHours().contains(show))&&(product.getProductsQuantity()>1)){
                    holder.number.setVisibility(View.GONE);
                    holder.product_add_cart_btn.setText(context.getString(R.string.addToCart));
                    Log.e("PRODUCT HOURS:",show);

                    holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_button_green));
                   // holder.product_add_cart_btn.setClickable(false);
                }else {

                    String time="22:00";
                    try {
                        final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        final Date dateObj = sdf.parse(Breakfast_Stime);
                        final Date dateObj1 = sdf.parse(Lunch_Stime);
                        final Date dateObj2 = sdf.parse(Snacks_Stime);
                        final Date dateObj3 = sdf.parse(Dinner_Stime);
                        Br_time=new SimpleDateFormat("hh:mm aa").format(dateObj);
                        Ln_time=new SimpleDateFormat("hh:mm aa").format(dateObj1);
                        Sn_time=new SimpleDateFormat("hh:mm aa").format(dateObj2);
                        Dn_time=new SimpleDateFormat("hh:mm aa").format(dateObj3);
                        //time = new SimpleDateFormat("hh:mm aa").format(dateObj);
                        Log.e("Time:", String.valueOf(dateObj2));
                    } catch (final ParseException e) {
                        e.printStackTrace();
                    }

                   // holder.product_add_cart_btn.setText(context.getString(R.string.notAvailable));

                    //holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_button_red));
                    if(!product.getProductsHours().contains(show)){
                        if(product.getProductsHours().contains(show1)){

                            Log.e("Show1: ",show1);
                            if(show1.equals("Breakfast")){
                                holder.product_add_cart_btn.setText("NXT Avail at "+Br_time);
                                holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_button_red));
                            }else if(show1.equals("Lunch")){
                                holder.product_add_cart_btn.setText("NXT Avail at "+Ln_time);
                                holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_button_red));
                            }else if(show1.equals("Snacks")){
                                holder.product_add_cart_btn.setText("NXT Avail at "+Sn_time);
                                holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_button_red));
                            }else if(show1.equals("Dinner")){
                                holder.product_add_cart_btn.setText("NXT Avail at "+Dn_time);
                                holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_button_red));
                            }
                           // holder.product_add_cart_btn.setText("Avail at "+show1);
                        }else if(product.getProductsHours().contains(show2)){
                            if(show2.equals("Breakfast")){
                                holder.product_add_cart_btn.setText("NXT Avail at "+Br_time);
                                holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_button_red));
                            }else if(show2.equals("Lunch")){
                                holder.product_add_cart_btn.setText("NXT Avail at "+Ln_time);
                                holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_button_red));
                            }else if(show2.equals("Snacks")){
                                holder.product_add_cart_btn.setText("NXT Avail at "+Sn_time);
                                Log.e("SnacksTime:",Snacks_Stime);
                                holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_button_red));
                            }else if(show2.equals("Dinner")){
                                holder.product_add_cart_btn.setText("NXT Avail at "+Dn_time);
                                holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_button_red));
                            }
                           // holder.product_add_cart_btn.setText("Avail at "+show2);
                           // holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_button_red));
                        }else if(product.getProductsHours().contains(show3)){
                            if(show3.equals("Breakfast")){
                                holder.product_add_cart_btn.setText("NXT Avail at "+Br_time);
                                holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_button_red));
                            }else if(show3.equals("Lunch")){
                                holder.product_add_cart_btn.setText("NXT Avail at "+Ln_time);
                                holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_button_red));
                            }else if(show3.equals("Snacks")){
                                holder.product_add_cart_btn.setText("NXT Avail at "+Sn_time);
                                holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_button_red));
                            }else if(show3.equals("Dinner")){
                                holder.product_add_cart_btn.setText("NXT Avail at "+Dn_time);
                                holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_button_red));
                            }
                            //holder.product_add_cart_btn.setText("Avail at "+show3);
                            //holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_button_red));
                        }

                        //holder.product_add_cart_btn.setText(context.getString(R.string.notAvailable));
                        //holder.product_add_cart_btn.setText("Avail After "+EndTime);

                       // Log.e("Product HOurs:",product.getProductsHours());
                    }
               }
                }

                // Set Product Image on ImageView with Glide Library
                Glide.with(context)
                        .load(ConstantValues.ECOMMERCE_URL + product.getProductsImage())
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                holder.cover_loader.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                holder.cover_loader.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(holder.product_thumbnail);


                holder.product_title.setText(product.getProductsName());
                holder.product_price_old.setPaintFlags(holder.product_price_old.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


                // Calculate the Discount on Product with static method of Helper class
                final String discount = Utilities.checkDiscount(product.getProductsPrice(), product.getDiscountPrice());

                if (discount != null) {
                    // Set Product's Price
                    holder.product_price_old.setVisibility(View.VISIBLE);
                    holder.product_price_old.setText(ConstantValues.CURRENCY_SYMBOL + product.getProductsPrice());
                    holder.product_price_new.setText(ConstantValues.CURRENCY_SYMBOL + product.getDiscountPrice());

                    holder.product_tag_new.setVisibility(View.GONE);
                    holder.product_tag_new_text.setVisibility(View.GONE);

                    // Set Discount Tag and its Text
                    holder.product_tag_discount.setVisibility(View.VISIBLE);
                    holder.product_tag_discount_text.setVisibility(View.VISIBLE);
                    holder.product_tag_discount_text.setText(discount);

                } else {

                    // Check if the Product is Newly Added with the help of static method of Helper class
                    if (Utilities.checkNewProduct(product.getProductsDateAdded())) {
                        // Set New Tag and its Text
                        holder.product_tag_new.setVisibility(View.VISIBLE);
                        holder.product_tag_new_text.setVisibility(View.VISIBLE);
                    } else {
                        holder.product_tag_new.setVisibility(View.GONE);
                        holder.product_tag_new_text.setVisibility(View.GONE);
                    }

                    // Hide Discount Text and Set Product's Price
                    holder.product_tag_discount.setVisibility(View.GONE);
                    holder.product_tag_discount_text.setVisibility(View.GONE);
                    holder.product_price_old.setVisibility(View.GONE);
                    holder.product_price_new.setText(ConstantValues.CURRENCY_SYMBOL+ product.getProductsPrice());
                }


                holder.product_like_btn.setOnCheckedChangeListener(null);

                // Check if Product is Liked
                if (product.getIsLiked().equalsIgnoreCase("1")) {
                    holder.product_like_btn.setChecked(true);
                } else {
                    holder.product_like_btn.setChecked(false);
                }


                // Handle the Click event of product_like_btn ToggleButton
                holder.product_like_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // Check if the User is Authenticated
                        if (ConstantValues.IS_USER_LOGGED_IN) {


                            if (holder.product_like_btn.isChecked()) {
                                product.setIsLiked("1");
                                holder.product_like_btn.setChecked(true);

                                // Like the Product for the User with the static method of Product_Description
                                Product_Description.LikeProduct(product.getProductsId(), customerID, context, view);
                            } else {
                                product.setIsLiked("0");
                                holder.product_like_btn.setChecked(false);

                                // Unlike the Product for the User with the static method of Product_Description
                                Product_Description.UnlikeProduct(product.getProductsId(), customerID, context, view);
                            }

                        } else {
                            // Keep the Like Button Unchecked
                            holder.product_like_btn.setChecked(false);

                            // Navigate to Login Activity
                            Intent i = new Intent(context, Login.class);
                            context.startActivity(i);
                            ((MainActivity) context).finish();
                            ((MainActivity) context).overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
                        }
                    }
                });


                // Handle the Click event of product_thumbnail ImageView
                holder.product_thumbnail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        // Get Product Info
                        Bundle itemInfo = new Bundle();
                        itemInfo.putParcelable("productDetails", product);

                        // Navigate to Product_Description of selected Product
//                    Fragment fragment = new Product_Description();
//                    fragment.setArguments(itemInfo);
//                    MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
//                    FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
//                    fragmentManager.beginTransaction()
//                            .replace(R.id.main_fragment, fragment)
//                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                            .addToBackStack(null).commit();


                        // Add the Product to User's Recently Viewed Products
//                        if (!recents_db.getUserRecents().contains(product.getProductsId())) {
//                            recents_db.insertRecentItem(product.getProductsId());
//                        }
                    }
                });


                // Handle the Click event of product_checked ImageView
                holder.product_checked.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // Get Product Info
//                    Bundle itemInfo = new Bundle();
//                    itemInfo.putParcelable("productDetails", product);
//
//                    // Navigate to Product_Description of selected Product
//                    Fragment fragment = new Product_Description();
//                    fragment.setArguments(itemInfo);
//                    MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
//                    FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
//                    fragmentManager.beginTransaction()
//                            .replace(R.id.main_fragment, fragment)
//                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                            .addToBackStack(null).commit();


                        // Add the Product to User's Recently Viewed Products
//                        if (!recents_db.getUserRecents().contains(product.getProductsId())) {
//                            recents_db.insertRecentItem(product.getProductsId());
//                        }
                    }
                });


                // Check the Button's Visibility
                if (ConstantValues.IS_ADD_TO_CART_BUTTON_ENABLED) {


                    holder.product_add_cart_btn.setOnClickListener(null);

//                    if (product.getProductsQuantity() < 1) {
//                        holder.product_add_cart_btn.setText(context.getString(R.string.outOfStock));
//                        holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_button_red));
//                    } else {
//                        holder.product_add_cart_btn.setText(context.getString(R.string.addToCart));
//                        holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_button_green));
//                    }

                    holder.product_add_cart_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String proname="none";
                            if(holder.product_add_cart_btn.getText().equals(context.getString(R.string.addToCart))){
                                holder.product_add_cart_btn.setVisibility(View.INVISIBLE);
                                holder.number.setVisibility(View.VISIBLE);
                                if (product.getProductsQuantity() > 0) {
                                    Utilities.animateCartMenuIcon(context, (MainActivity) context);

//                                    proname=cart_db.getUserData(product.getProductsName());
//                                    Log.e("Productname",proname);
//                                    if(!(proname.isEmpty())){
//                                        Log.e("Productname",proname);
//                                    }else{
//                                        Log.e("Productname","Not Available");
//
//                                    }


                                    // Add Product to User's Cart
                                    addProductToCart(product);

                                    // holder.product_checked.setVisibility(View.VISIBLE);
                                    holder.number.setVisibility(View.VISIBLE);
                                    holder.product_add_cart_btn.setVisibility(View.INVISIBLE);

                                    Snackbar.make(view, context.getString(R.string.item_added_to_cart), Snackbar.LENGTH_SHORT).show();
                                }

                            }else{
                                Log.e("Not","Available");
                                Snackbar.make(view, context.getString(R.string.notAvailable), Snackbar.LENGTH_SHORT).show();
                            }


                        }
                    });
                    final int[] number = {1};
                    number[0] = Integer.parseInt(holder.cart_item_quantity.getText().toString());
                    Log.e("QTY:", String.valueOf(number[0]));


                    holder.cart_item_quantity_plusBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CartProduct cartProduct = new CartProduct();
                       if(number[0]<10){
                             number[0] = number[0] + 1;
                             holder.cart_item_quantity.setText("" + number[0]);

                             Double price1 = Double.valueOf(product.getProductsPrice());
                             Double Qty = Double.valueOf(number[0]);
                             Double pricenew = price1 * Qty;
                             My_Cart my_cart = new My_Cart();
                             cartProduct = my_cart.GetCartProduct(product.getProductsId());

                             String proid = String.valueOf(product.getProductsId());

    // Update CartItem in Local Database using static method of My_Cart
                             My_Cart.UpdateQTY
                             (
                               cartProduct.getCustomersBasketProduct().getProductsName(), String.valueOf(number[0]), String.valueOf(pricenew)
                             );

                              Log.e("NEW PRICE:", String.valueOf(pricenew));

                        }
                      }
                    });

                    holder.cart_item_quantity_minusBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if ((number[0] > 1)) {
                                CartProduct cartProduct = new CartProduct();
                                number[0] = number[0] - 1;
                                holder.cart_item_quantity.setText("" + number[0]);
                                Log.e("PRODUCT NAME:", product.getProductsName());
                                Log.e("PRICE:", product.getProductsPrice());
                                Double price1 = Double.valueOf(product.getProductsPrice());
                                Double Qty = Double.valueOf(number[0]);
                                Double pricenew = price1 * Qty;
                                My_Cart my_cart = new My_Cart();
                                cartProduct = my_cart.GetCartProduct(product.getProductsId());
                                Log.e("NEW PRICE:", String.valueOf(pricenew));


                                String proid = String.valueOf(product.getProductsId());
                                Log.e("PRODUCT ID:", proid);


                                // Update CartItem in Local Database using static method of My_Cart
                                My_Cart.UpdateQTY
                                        (
                                                cartProduct.getCustomersBasketProduct().getProductsName(), String.valueOf(number[0]), String.valueOf(pricenew)
                                        );

                               // Toast.makeText(context, "QTY-" + number[0], Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                } else {
                    // Make the Button Invisible
                    holder.product_add_cart_btn.setVisibility(View.GONE);
                }

            }
        }





    //********** Returns the total number of items in the data set *********//

    @Override
    public int getItemCount() {
        return productList.size();
    }



    //********** Toggles the RecyclerView LayoutManager *********//

    public void toggleLayout(Boolean isGridView) {
        this.isGridView = isGridView;
    }



    /********** Custom ViewHolder provides a direct reference to each of the Views within a Data_Item *********/

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ProgressBar cover_loader;
        ImageView product_checked;
        Button product_add_cart_btn;
        ToggleButton product_like_btn;
        ImageView product_thumbnail, product_tag_new, product_tag_discount;
        TextView product_title, product_price_old, product_price_new, product_tag_new_text, product_tag_discount_text;
        private ImageButton cart_item_quantity_minusBtn, cart_item_quantity_plusBtn, cart_item_removeBtn;
        LinearLayout number;

        private TextView cart_item_quantity;


        public MyViewHolder(final View itemView) {
            super(itemView);

            number=(LinearLayout)itemView.findViewById(R.id.number);

            product_checked = (ImageView) itemView.findViewById(R.id.product_checked);
            cover_loader = (ProgressBar) itemView.findViewById(R.id.product_cover_loader);

            product_add_cart_btn = (Button) itemView.findViewById(R.id.product_card_Btn);
            product_like_btn = (ToggleButton) itemView.findViewById(R.id.product_like_btn);
            product_title = (TextView) itemView.findViewById(R.id.product_title);
            product_price_old = (TextView) itemView.findViewById(R.id.product_price_old);
            product_price_new = (TextView) itemView.findViewById(R.id.product_price_new);
            product_thumbnail = (ImageView) itemView.findViewById(R.id.product_cover);
            product_tag_new = (ImageView) itemView.findViewById(R.id.product_tag_new);
            product_tag_new_text = (TextView) itemView.findViewById(R.id.product_tag_new_text);
            product_tag_discount = (ImageView) itemView.findViewById(R.id.product_tag_discount);
            product_tag_discount_text = (TextView) itemView.findViewById(R.id.product_tag_discount_text);
            cart_item_quantity = (TextView) itemView.findViewById(R.id.cart_item_quantity);

            cart_item_quantity_plusBtn = (ImageButton) itemView.findViewById(R.id.cart_item_quantity_plusBtn);
            cart_item_quantity_minusBtn = (ImageButton) itemView.findViewById(R.id.cart_item_quantity_minusBtn);
        }

    }


    //********** Adds the Product to User's Cart *********//

    private void addProductToCart(ProductDetails product) {

        CartProduct cartProduct = new CartProduct();

        double productBasePrice, productFinalPrice, attributesPrice = 0;
        List<CartProductAttributes> selectedAttributesList = new ArrayList<>();


        // Check Discount on Product with the help of static method of Helper class
        final String discount = Utilities.checkDiscount(product.getProductsPrice(), product.getDiscountPrice());

        // Get Product's Price based on Discount
        if (discount != null) {
            product.setIsSaleProduct("1");
            productBasePrice = Double.parseDouble(product.getDiscountPrice());
        } else {
            product.setIsSaleProduct("0");
            productBasePrice = Double.parseDouble(product.getProductsPrice());
        }


        // Get Default Attributes from AttributesList
        for (int i=0;  i<product.getAttributes().size();  i++) {

            CartProductAttributes productAttribute = new CartProductAttributes();

            // Get Name and First Value of current Attribute
            Option option = product.getAttributes().get(i).getOption();
            Value value = product.getAttributes().get(i).getValues().get(0);


            // Add the Attribute's Value Price to the attributePrices
            String attrPrice = value.getPricePrefix() + value.getPrice();
            attributesPrice += Double.parseDouble(attrPrice);


            // Add Value to new List
            List<Value> valuesList = new ArrayList<>();
            valuesList.add(value);


            // Set the Name and Value of Attribute
            productAttribute.setOption(option);
            productAttribute.setValues(valuesList);


            // Add current Attribute to selectedAttributesList
            selectedAttributesList.add(i, productAttribute);
        }


        // Add Attributes Price to Product's Final Price
        productFinalPrice = productBasePrice + attributesPrice;


        // Set Product's Price and Quantity
        product.setCustomersBasketQuantity(1);
        product.setProductsPrice(String.valueOf(productBasePrice));
        product.setAttributesPrice(String.valueOf(attributesPrice));
        product.setProductsFinalPrice(String.valueOf(productFinalPrice));
        product.setTotalPrice(String.valueOf(productFinalPrice));

        // Set Customer's Basket Product and selected Attributes Info
        cartProduct.setCustomersBasketProduct(product);
        cartProduct.setCustomersBasketProductAttributes(selectedAttributesList);



        // Add the Product to User's Cart with the help of static method of My_Cart class
        My_Cart.AddCartItem
                (
                        cartProduct
                );


        // Recreate the OptionsMenu
        ((MainActivity) context).invalidateOptionsMenu();

    }



}

