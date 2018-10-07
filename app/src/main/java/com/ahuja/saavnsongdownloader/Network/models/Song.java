package com.ahuja.saavnsongdownloader.Network.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Song extends SongBase
{
    @SerializedName("more_info")
    @Expose
    public MoreInfo moreInfo;
}
