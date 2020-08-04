package com.aventuracctv.ordermangement.data;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by jburmeister on 5/3/2016.
 */
@Parcel
public class PostResponse {

    String status;

    @SerializedName("status")
    public String getStatus() {
        return status;
    }
}