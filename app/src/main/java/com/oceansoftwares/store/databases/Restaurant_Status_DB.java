package com.oceansoftwares.store.databases;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.oceansoftwares.store.models.ResDetails;


public class Restaurant_Status_DB {

    SQLiteDatabase db;

    // Table Name
    public static final String TABLE_RES_STS = "Restaurant_status";
    // Table Columns
    public static final String RES_ID                      = "res_id";
    public static final String RES_STATUS                  = "res_status";
    public static final String RES_START_TIME              = "res_st_time";
    public static final String RES_END_TIME                = "res_ed_time";
    public static final String RES_BREAKFAST_START_TIME    = "res_bf_st_time";
    public static final String RES_BREAKFAST_END_TIME      = "res_bf_ed_time";
    public static final String RES_LUNCH_START_TIME        = "res_ln_st_time";
    public static final String RES_LUNCH_END_TIME          = "res_ln_ed_time";
    public static final String RES_DINNER_START_TIME       = "res_dn_st_time";
    public static final String RES_DINNER_END_TIME         = "res_dn_ed_time";
    public static final String RES_SNACKS_START_TIME       = "res_sn_st_time";
    public static final String RES_SNACKS_END_TIME         = "res_sn_ed_time";
    public static final String RES_REMARKS                 = "res_remarks";

    //*********** Returns the Query to Create TABLE_USER_INFO ********//

    public static String createTable() {

        return "CREATE TABLE "+ TABLE_RES_STS +
                "(" +
                RES_ID               +" TEXT," +
                RES_STATUS        +" TEXT," +
                RES_START_TIME         +" TEXT," +
                RES_END_TIME    +" TEXT," +
                RES_BREAKFAST_START_TIME         +" TEXT," +
                RES_BREAKFAST_END_TIME              +" TEXT," +
                RES_LUNCH_START_TIME        +" TEXT," +
                RES_LUNCH_END_TIME              +" TEXT," +
                RES_DINNER_START_TIME           +" TEXT," +
                RES_DINNER_END_TIME       +" TEXT," +
                RES_SNACKS_START_TIME          +" TEXT," +
                RES_SNACKS_END_TIME       +" TEXT," +
                RES_REMARKS          +" TEXT" +
                ")";
    }


    //*********** Insert New User Data ********//

    public void insertRestaurantData(ResDetails res){
        // get and open SQLiteDatabase Instance from static method of DB_Manager class
        db = DB_Manager.getInstance().openDatabase();

        ContentValues values = new ContentValues();

        values.put(RES_ID,              res.getId());
        values.put(RES_STATUS,       res.getStatus());
        values.put(RES_START_TIME,        res.getOpen_time());
        values.put(RES_END_TIME,   res.getClose_time());
        values.put(RES_BREAKFAST_START_TIME,        res.getBreakfast_s_time());
        values.put(RES_BREAKFAST_END_TIME,             res.getBreakfast_e_time());
        values.put(RES_LUNCH_START_TIME,       res.getLunch_s_time());
        values.put(RES_LUNCH_END_TIME,             res.getLunch_e_time());
        values.put(RES_DINNER_START_TIME,          res.getDinner_s_time());
        values.put(RES_DINNER_END_TIME,      res.getDinner_e_time());
        values.put(RES_SNACKS_START_TIME,         res.getSnacks_s_time());
        values.put(RES_SNACKS_END_TIME,      res.getSnacks_e_time());
        values.put(RES_REMARKS,         res.getRemarks());

        db.insert(TABLE_RES_STS, null, values);

        Log.e("INSERTION IN RES TABLE:","SUCCESS");

        // close the Database
        DB_Manager.getInstance().closeDatabase();
    }


    //*********** Get the Details of single User ********//

    public ResDetails getUserData(String userID){
        // get and open SQLiteDatabase Instance from static method of DB_Manager class
        db = DB_Manager.getInstance().openDatabase();

        Cursor cursor =  db.rawQuery( "SELECT * FROM "+ TABLE_RES_STS +" WHERE "+ RES_ID +" =?", new String[] {userID});

        ResDetails userInfo = new ResDetails();


        if (cursor.moveToFirst()) {
            do {
                userInfo.setId(Integer.parseInt(cursor.getString(0)));
                userInfo.setStatus(cursor.getString(1));
                userInfo.setOpen_time(cursor.getString(2));
                userInfo.setClose_time(cursor.getString(3));
                userInfo.setBreakfast_s_time(cursor.getString(4));
                userInfo.setBreakfast_e_time(cursor.getString(5));
                userInfo.setLunch_s_time(cursor.getString(6));
                userInfo.setLunch_e_time(cursor.getString(7));
                userInfo.setDinner_s_time(cursor.getString(8));
                userInfo.setDinner_e_time(cursor.getString(9));
                userInfo.setSnacks_s_time(cursor.getString(10));
                userInfo.setSnacks_e_time(cursor.getString(11));
                userInfo.setRemarks(cursor.getString(12));

            } while (cursor.moveToNext());


            // close the Database
            DB_Manager.getInstance().closeDatabase();

            return userInfo;
        }

        return null;
    }

    //*********** Delete the Data of the User ********//

    public void deleteResStatusData(String userID){
        // get and open SQLiteDatabase Instance from static method of DB_Manager class
        db = DB_Manager.getInstance().openDatabase();

        db.delete(TABLE_RES_STS, RES_ID +" = ?", new String[] {userID});

        Log.e("DELETION IN RES TABLE:","SUCCESS");
        // close the Database
        DB_Manager.getInstance().closeDatabase();
    }


}
