
package com.ahuja.saavnsongdownloader.Network.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MoreInfo {

    @SerializedName("album")
    @Expose
    public String album;
    @SerializedName("320kbps")
    @Expose
    public String _320kbps;
    @SerializedName("encrypted_media_url")
    @Expose
    public String encryptedMediaUrl;
    @SerializedName("artistMap")
    @Expose
    public ArtistMap artistMap;
    @SerializedName("show_title")
    @Expose
    public String showTitle;
    @SerializedName("season_title")
    @Expose
    public String seasonTitle;
}
