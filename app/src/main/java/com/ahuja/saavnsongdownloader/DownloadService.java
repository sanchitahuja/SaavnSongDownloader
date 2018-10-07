package com.ahuja.saavnsongdownloader;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;

import com.ahuja.saavnsongdownloader.Database.DBData;
import com.ahuja.saavnsongdownloader.Database.DBHelper;
import com.ahuja.saavnsongdownloader.Network.models.SongInfo;

import java.io.File;

/**
 * Created by Sanchit Ahuja on 9/20/2018.
 */
public class DownloadService extends IntentService {
    public static final String SONG_DOWNLOAD="downloadsong";
    private static DownloadManager downloadManager;
    private static DBHelper dbHelper;

    public DownloadService() {
        super("SaavnSongDownloaderService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver(broadcastReceiverOnDownloadComplete,new IntentFilter( DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        SongInfo songInfo=intent.getParcelableExtra(SONG_DOWNLOAD);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath()+
                songInfo.getDownload_folder()+"/"+songInfo.getSong_name()+songInfo.getExtension());
        if(file.exists()){

        }
        else{
            if(downloadManager==null){
                downloadManager= (DownloadManager) getApplicationContext().getSystemService(DOWNLOAD_SERVICE);
            }
            if(dbHelper==null)
                dbHelper=new DBHelper(getApplicationContext());
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(songInfo.getDownload_url()));

            request.setDescription(songInfo.getAlbum_name())
                    .setTitle(songInfo.getSong_name())
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationUri(Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath()+
                            songInfo.getDownload_folder()+"/"
                                    + songInfo.getSong_name() + songInfo.getExtension()))
                    );
            long downloadId=downloadManager.enqueue(request);
            DBData.updateDownloadID(dbHelper.getWritableDatabase(),songInfo.getSong_name(),String.valueOf(downloadId));
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(broadcastReceiverOnDownloadComplete);
        super.onDestroy();
    }

    BroadcastReceiver broadcastReceiverOnDownloadComplete=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(dbHelper==null)
                dbHelper=new DBHelper(getApplicationContext());
            Bundle extras = intent.getExtras();
            Long downloadID = extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID);
            DBData.deleteSongByDownloadID(dbHelper.getWritableDatabase(),String.valueOf(downloadID));
        }
    };
}
