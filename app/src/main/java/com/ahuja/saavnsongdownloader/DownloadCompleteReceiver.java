package com.ahuja.saavnsongdownloader;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import com.ahuja.saavnsongdownloader.Database.DBData;
import com.ahuja.saavnsongdownloader.Database.DBHelper;
import com.ahuja.saavnsongdownloader.Network.models.SongInfo;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;
import java.util.ArrayList;



public class DownloadCompleteReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(intent.getAction().equals("android.intent.action.DOWNLOAD_COMPLETE"))
        {
            DBHelper helper = new DBHelper(context);
            Bundle extras = intent.getExtras();
            Long downloaded_id = extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID);
            SQLiteDatabase db = helper.getReadableDatabase();
            ArrayList<SongInfo>arrayList= DBData.getSongsByDownloadID(db,String.valueOf(downloaded_id));
            if (arrayList.size() > 0)
            {
                SongInfo songInfo=arrayList.get(0);
                DownloadManager downloadManager = (DownloadManager) context
                        .getSystemService(Context.DOWNLOAD_SERVICE);
                File filename = new File(downloadManager.getUriForDownloadedFile(downloaded_id).getPath());
                try
                {
                    AudioFile f = AudioFileIO.read(filename);
                    Tag tag = f.getTagOrCreateAndSetDefault();

                    tag.setField(FieldKey.ALBUM,songInfo.getAlbum_name());
                    tag.setField(FieldKey.ARTIST,songInfo.getArtist_name() );
                    tag.setField(FieldKey.YEAR,songInfo.getYear());
                    tag.setField(FieldKey.LANGUAGE, songInfo.getLanguage());
                    tag.setField(FieldKey.GENRE,songInfo.getLanguage());
                    AudioFileIO.write(f);
                    Toast.makeText(context,songInfo.getSong_name()+" downloaded Successfully",Toast.LENGTH_SHORT).show();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                DBData.deleteSongByDownloadID(db,String.valueOf(downloaded_id));
            }

            db.close();
            helper.close();
        }
    }
}
