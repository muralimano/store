package com.oceansoftwares.store.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class commentdetails {

    @SerializedName("id")
    @Expose
    private int Id;
    @SerializedName("comment")
    @Expose
    private String Comment;

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
     *     The Comment
     */
    public String getComment() {
        return Comment;
    }

    /**
     *
     * @param comment
     *     The countries_name
     */
    public void setComment(String comment) {
        this.Comment = comment;
    }


}
