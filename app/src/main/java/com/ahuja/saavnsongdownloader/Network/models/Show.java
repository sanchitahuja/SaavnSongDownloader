package com.ahuja.saavnsongdownloader.Network.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Show
{
    @SerializedName("episodes")
    @Expose
    public List<Song> episodes;
}
