package com.ahuja.saavnsongdownloader.Network.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class SongList extends SongBase
{
    @SerializedName("list")
    @Expose
    public List<Song> list;
}
