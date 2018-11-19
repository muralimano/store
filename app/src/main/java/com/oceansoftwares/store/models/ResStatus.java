package com.oceansoftwares.store.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ResStatus {

    @SerializedName("success")
    @Expose
    private String success;
    @SerializedName("data")
    @Expose
    private List<ResDetails> data = new ArrayList<ResDetails>();
    @SerializedName("message")
    @Expose
    private String message;

    /**
     *
     * @return
     *     The success
     */
    public String getSuccess() {
        return success;
    }

    /**
     *
     * @param success
     *     The success
     */
    public void setSuccess(String success) {
        this.success = success;
    }

    /**
     *
     * @return
     *     The data
     */
    public List<ResDetails> getData() {
        return data;
    }

    /**
     *
     * @param data
     *     The data
     */
    public void setData(List<ResDetails> data) {
        this.data = data;
    }

    /**
     *
     * @return
     *     The message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message
     *     The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

}
