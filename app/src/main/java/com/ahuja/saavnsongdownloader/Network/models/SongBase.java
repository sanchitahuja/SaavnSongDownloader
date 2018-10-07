
package com.ahuja.saavnsongdownloader.Network.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SongBase
{

    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("language")
    @Expose
    public String language;
    @SerializedName("year")
    @Expose
    public String year;

}
