
package com.ahuja.saavnsongdownloader.Network.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ArtistMap {

    @SerializedName("primary_artists")
    @Expose
    public List<PrimaryArtist> primaryArtists = null;

}
