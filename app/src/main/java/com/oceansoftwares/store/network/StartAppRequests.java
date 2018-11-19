package com.oceansoftwares.store.network;

import android.content.Context;
import android.support.design.widget.Snackbar;

import com.oceansoftwares.store.R;

import java.io.IOException;

import com.oceansoftwares.store.app.App;
import com.oceansoftwares.store.constant.ConstantValues;

import com.oceansoftwares.store.models.banner_model.BannerData;
import com.oceansoftwares.store.models.category_model.CategoryData;
import com.oceansoftwares.store.models.device_model.AppSettingsData;
import com.oceansoftwares.store.models.pages_model.PagesData;
import com.oceansoftwares.store.models.pages_model.PagesDetails;

import retrofit2.Call;


/**
 * StartAppRequests contains some Methods and API Requests, that are Executed on Application Startup
 **/

public class StartAppRequests {

    private App app = new App();


    public StartAppRequests(Context context) {
        app = ((App) context.getApplicationContext());
    }



    //*********** Contains all methods to Execute on Startup ********//

    public void StartRequests(){

        RequestBanners();
        RequestAllCategories();
        RequestAppSetting();
        RequestStaticPagesData();
    }



    //*********** API Request Method to Fetch App Banners ********//

    private void RequestBanners() {

        Call<BannerData> call = APIClient.getInstance()
                .getBanners();

        BannerData bannerData = new BannerData();

        try {
            bannerData = call.execute().body();
            
            if (!bannerData.getSuccess().isEmpty())
                app.setBannersList(bannerData.getData());
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    //*********** API Request Method to Fetch All Categories ********//
    
    private void RequestAllCategories() {

        Call<CategoryData> call = APIClient.getInstance()
                .getAllCategories
                        (
                                ConstantValues.LANGUAGE_ID
                        );

        CategoryData categoryData = new CategoryData();

        try {
            categoryData = call.execute().body();
            
            if (!categoryData.getSuccess().isEmpty())
                app.setCategoriesList(categoryData.getData());
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    //*********** Request App Settings from the Server ********//
    
    private void RequestAppSetting() {

        Call<AppSettingsData> call = APIClient.getInstance()
                .getAppSetting();

        AppSettingsData appSettingsData = null;

        try {
            appSettingsData = call.execute().body();
    
            if (!appSettingsData.getSuccess().isEmpty())
                app.setAppSettingsDetails(appSettingsData.getProductData().get(0));
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    
    //*********** Request Static Pages Data from the Server ********//
    
    private void RequestStaticPagesData() {
    
        ConstantValues.ABOUT_US = app.getString(R.string.lorem_ipsum);
        ConstantValues.TERMS_SERVICES = app.getString(R.string.lorem_ipsum);
        ConstantValues.PRIVACY_POLICY = app.getString(R.string.lorem_ipsum);
        ConstantValues.REFUND_POLICY = app.getString(R.string.lorem_ipsum);
        
        
        Call<PagesData> call = APIClient.getInstance()
                .getStaticPages
                        (
                                ConstantValues.LANGUAGE_ID
                        );
    
        PagesData pages = new PagesData();
        
        try {
            pages = call.execute().body();
    
            if (pages.getSuccess().equalsIgnoreCase("1")) {
    
                app.setStaticPagesDetails(pages.getPagesData());
    
                for (int i=0;  i<pages.getPagesData().size();  i++) {
                    PagesDetails page = pages.getPagesData().get(i);
        
                    if (page.getSlug().equalsIgnoreCase("about-us")) {
                        ConstantValues.ABOUT_US = page.getDescription();
                    }
                    else if (page.getSlug().equalsIgnoreCase("term-services")) {
                        ConstantValues.TERMS_SERVICES = page.getDescription();
                    }
                    else if (page.getSlug().equalsIgnoreCase("privacy-policy")) {
                        ConstantValues.PRIVACY_POLICY = page.getDescription();
                    }
                    else if (page.getSlug().equalsIgnoreCase("refund-policy")) {
                        ConstantValues.REFUND_POLICY = page.getDescription();
                    }
                }
            }
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
