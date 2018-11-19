package com.oceansoftwares.store.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResDetails {

    @SerializedName("id")
    @Expose
    private int Id;
    @SerializedName("status")
    @Expose
    private String Status;

    @SerializedName("open_time")
    @Expose
    private String Open_time;

    @SerializedName("close_time")
    @Expose
    private String close_time;

    @SerializedName("bf_s_time")
    @Expose
    private String breakfast_s_time;

    @SerializedName("bf_e_time")
    @Expose
    private String breakfast_e_time;

    @SerializedName("ln_s_time")
    @Expose
    private String lunch_s_time;

    @SerializedName("ln_e_time")
    @Expose
    private String lunch_e_time;

    @SerializedName("dn_s_time")
    @Expose
    private String dinner_s_time;

    @SerializedName("dn_e_time")
    @Expose
    private String dinner_e_time;

    @SerializedName("sn_s_time")
    @Expose
    private String snacks_s_time;

    @SerializedName("sn_e_time")
    @Expose
    private String snacks_e_time;

    @SerializedName("remarks")
    @Expose
    private String remarks;

    public String getBreakfast_s_time() {
        return breakfast_s_time;
    }

    public void setBreakfast_s_time(String breakfast_s_time) {
        this.breakfast_s_time = breakfast_s_time;
    }

    public String getBreakfast_e_time() {
        return breakfast_e_time;
    }

    public void setBreakfast_e_time(String breakfast_e_time) {
        this.breakfast_e_time = breakfast_e_time;
    }

    public String getLunch_s_time() {
        return lunch_s_time;
    }

    public void setLunch_s_time(String lunch_s_time) {
        this.lunch_s_time = lunch_s_time;
    }

    public String getLunch_e_time() {
        return lunch_e_time;
    }

    public void setLunch_e_time(String lunch_e_time) {
        this.lunch_e_time = lunch_e_time;
    }

    public String getDinner_s_time() {
        return dinner_s_time;
    }

    public void setDinner_s_time(String dinner_s_time) {
        this.dinner_s_time = dinner_s_time;
    }

    public String getDinner_e_time() {
        return dinner_e_time;
    }

    public void setDinner_e_time(String dinner_e_time) {
        this.dinner_e_time = dinner_e_time;
    }

    public String getSnacks_s_time() {
        return snacks_s_time;
    }

    public void setSnacks_s_time(String snacks_s_time) {
        this.snacks_s_time = snacks_s_time;
    }

    public String getSnacks_e_time() {
        return snacks_e_time;
    }

    public void setSnacks_e_time(String snacks_e_time) {
        this.snacks_e_time = snacks_e_time;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getClose_time() {
        return close_time;
    }

    public void setClose_time(String close_time) {
        this.close_time = close_time;
    }

    public String getOpen_time() {
        return Open_time;
    }

    public void setOpen_time(String open_time) {
        Open_time = open_time;
    }

    public int getId() {
        return Id;
    }

    /**
     *
     * @param id
     *     The countries_id
     */
    public void setId(int id) {
        this.Id = id;
    }

    /**
     *
     * @return
     *     The Status
     */
    public String getStatus() {
        return Status;
    }

    /**
     *
     * @param status
     *     The countries_name
     */
    public void setStatus(String status) {
        this.Status = status;
    }


}
