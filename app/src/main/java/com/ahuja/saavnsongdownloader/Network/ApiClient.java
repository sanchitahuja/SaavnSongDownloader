package com.ahuja.saavnsongdownloader.Network;


import com.ahuja.saavnsongdownloader.Network.models.Show;
import com.ahuja.saavnsongdownloader.Network.models.Song;
import com.ahuja.saavnsongdownloader.Network.models.SongList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Sanchit Ahuja on 8/5/2018.
 */
public class ApiClient {
public static final String BASE_URL="http://www.saavn.com";
    static Retrofit retrofit;
    static Map<String,String> parametersMap;
    static ApiInterface apiInterface;

    public ApiClient(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        retrofit=new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();
        initApiHashMap();
        apiInterface= retrofit.create(ApiInterface.class);

    }
    public static ApiInterface getClient(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        retrofit=new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();
        initApiHashMap();
        apiInterface= retrofit.create(ApiInterface.class);
        return apiInterface;
    }
    private static void initApiHashMap()
    {
        parametersMap = new HashMap<>();

        parametersMap.put("ctx", "android");
        parametersMap.put("_format", "json");
        parametersMap.put("_marker", "0");

        parametersMap.put("network_type", "WIFI");
        parametersMap.put("network_subtype", "");
        parametersMap.put("network_operator", "Reliance");
        parametersMap.put("api_version", "4");
        parametersMap.put("cc", "in");
        parametersMap.put("v", "61");
        parametersMap.put("readable_version", "5.4");
        parametersMap.put("app_version", "5.4");
        parametersMap.put("manufacturer", "Google");
        parametersMap.put("model", "Pixel");
        parametersMap.put("build", "2");
        parametersMap.put("state", "logout");
        parametersMap.put("session_device_id", RandomStringUtils.randomAlphanumeric(8)+"."
                + RandomUtils.nextInt(1471527612,1481527612));
    }

    public void setSongToken(String type, String songToken){
        initApiHashMap();
        parametersMap.put("__call", "content.decodeTokenAndFetchResults");
        parametersMap.put("type", type);
        parametersMap.put("token",songToken);
    }

    public void setShowToken(String type, String showToken, String seasonNum){
        initApiHashMap();
        parametersMap.put("__call", "content.decodeTokenAndFetchResults");
        parametersMap.put("type", type);
        parametersMap.put("token",showToken);
        parametersMap.put("season_number", seasonNum);
    }
    public void setPlaylistToken(String username, String listname, String token){
        initApiHashMap();
        parametersMap.put("__call", "playlist.search");
        parametersMap.put("username", username);
        parametersMap.put("listname", listname);
        if (token != null) {
            parametersMap.put("token", token);
        }
    }
    public Call<List<Song>>makeSongRequest(){
        return apiInterface.createSongRequest(parametersMap);
    }

    public Call<Show>makeShowRequest(){
        return apiInterface.createShowRequest(parametersMap);
    }
    public Call<Song>makeEpisodeRequest(){
        return apiInterface.createEpisodeRequest(parametersMap);
    }
    public Call<SongList>makePlaylistRequest(){
        return apiInterface.createPlaylistRequest(parametersMap);
    }


}
