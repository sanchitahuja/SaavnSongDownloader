package com.ahuja.saavnsongdownloader.Network;


import com.ahuja.saavnsongdownloader.Network.models.Show;
import com.ahuja.saavnsongdownloader.Network.models.Song;
import com.ahuja.saavnsongdownloader.Network.models.SongList;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by Sanchit Ahuja on 8/5/2018.
 */
public interface ApiInterface {

    @GET("/api.php")
    Call<List<Song>> createSongRequest(
            @QueryMap(encoded = true) Map<String, String> options
    );
    @GET("/api.php")
    Call<SongList> createPlaylistRequest(
            @QueryMap(encoded = true) Map<String, String> options
    );

    @GET("/api.php")
    Call<Show> createShowRequest(
            @QueryMap(encoded = true) Map<String, String> options
    );

    @GET("/api.php")
    Call<Song> createEpisodeRequest(
            @QueryMap(encoded = true) Map<String, String> options
    );

}
