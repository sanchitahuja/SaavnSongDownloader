package com.ahuja.saavnsongdownloader.Database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ahuja.saavnsongdownloader.Network.models.SongInfo;

import java.util.ArrayList;
import java.util.Currency;

import static com.ahuja.saavnsongdownloader.Database.DBHelper.TABLE_NAME;

/**
 * Created by Sanchit Ahuja on 9/9/2018.
 */
public class DBData {
    /*
    DBHelper.TableColumns.ALBUM_NAME,
    DBHelper.TableColumns.ARTIST_NAME,
    DBHelper.TableColumns.DOWNLOAD_FOLDER,
    DBHelper.TableColumns.DOWNLOAD_URL,
    DBHelper.TableColumns.EXTENSION,
    DBHelper.TableColumns.LANGUAGE,
    DBHelper.TableColumns.SONG_NAME,
    DBHelper.TableColumns.YEAR
*/
    private static final String TAG = "DBData";

    public static boolean addSong(SQLiteDatabase db, SongInfo songInfo){

        if(db==null||db.isReadOnly())
            return false;
        ContentValues contentValues=new ContentValues();
        contentValues.put(DBHelper.TableColumns.ALBUM_NAME,songInfo.getAlbum_name());
        contentValues.put(DBHelper.TableColumns.ARTIST_NAME,songInfo.getArtist_name());
        contentValues.put(DBHelper.TableColumns.DOWNLOAD_FOLDER,songInfo.getDownload_folder());
        contentValues.put(DBHelper.TableColumns.DOWNLOAD_URL,songInfo.getDownload_url());
        contentValues.put(DBHelper.TableColumns.EXTENSION,songInfo.getExtension());
        contentValues.put(DBHelper.TableColumns.LANGUAGE,songInfo.getLanguage());
        contentValues.put(DBHelper.TableColumns.SONG_NAME,songInfo.getSong_name());
        contentValues.put(DBHelper.TableColumns.YEAR,songInfo.getYear());
        try {
            db.insert(TABLE_NAME,null,contentValues);
            return true;
        }
        catch (Exception e){
            Log.d(TAG, "addSong: "+e);
            return false;
        }
    }
    public static ArrayList<SongInfo>getSongs(SQLiteDatabase db){
        ArrayList<SongInfo>songInfoArrayList=new ArrayList<>();
        Cursor c=db.query(TABLE_NAME,new String[]{

                DBHelper.TableColumns.ALBUM_NAME,
                DBHelper.TableColumns.ARTIST_NAME,
                DBHelper.TableColumns.DOWNLOAD_FOLDER,
                DBHelper.TableColumns.DOWNLOAD_URL,
                DBHelper.TableColumns.EXTENSION,
                DBHelper.TableColumns.LANGUAGE,
                DBHelper.TableColumns.SONG_NAME,
                DBHelper.TableColumns.YEAR}
        ,DBHelper.TableColumns.DOWNLOAD_ID+" IS NULL OR "+DBHelper.TableColumns.DOWNLOAD_ID+" = ?",new String[]{""},null,null,null
        );
        Log.d(TAG, "getSongs: Cursor Size "+c.getCount());
        c.moveToFirst();
        while (c.getCount()>0&&!c.isAfterLast()){
            SongInfo songInfo=new SongInfo();
            Log.d(TAG, "getSongs: SONGNAME "+c.getString(c.getColumnIndex(DBHelper.TableColumns.SONG_NAME)));
            Log.d(TAG, "getSongs: ALBUM_NAME "+c.getString(c.getColumnIndex(DBHelper.TableColumns.ALBUM_NAME)));
            Log.d(TAG, "getSongs: ARTIST "+c.getString(c.getColumnIndex(DBHelper.TableColumns.ARTIST_NAME)));
            Log.d(TAG, "getSongs: DOWNLOADFOLDER "+c.getString(c.getColumnIndex(DBHelper.TableColumns.DOWNLOAD_FOLDER)));
            Log.d(TAG, "getSongs: URL "+c.getString(c.getColumnIndex(DBHelper.TableColumns.DOWNLOAD_URL)));
            Log.d(TAG, "getSongs: Language "+c.getString(c.getColumnIndex(DBHelper.TableColumns.LANGUAGE)));
            Log.d(TAG, "getSongs: Extension "+c.getString(c.getColumnIndex(DBHelper.TableColumns.EXTENSION)));
            Log.d(TAG, "================================================================");
            songInfo.setAlbum_name(c.getString(c.getColumnIndex(DBHelper.TableColumns.ALBUM_NAME)));
            songInfo.setArtist_name(c.getString(c.getColumnIndex(DBHelper.TableColumns.ARTIST_NAME)));
            songInfo.setDownload_folder(c.getString(c.getColumnIndex(DBHelper.TableColumns.DOWNLOAD_FOLDER)));
            songInfo.setSimpleDownload_url(c.getString(c.getColumnIndex(DBHelper.TableColumns.DOWNLOAD_URL)));
            songInfo.setLanguage(c.getString(c.getColumnIndex(DBHelper.TableColumns.LANGUAGE)));
            songInfo.setSong_name(c.getString(c.getColumnIndex(DBHelper.TableColumns.SONG_NAME)));
            songInfo.setYear(c.getString(c.getColumnIndex(DBHelper.TableColumns.YEAR)));
            songInfo.setExtension(c.getString(c.getColumnIndex(DBHelper.TableColumns.EXTENSION)));
            songInfoArrayList.add(songInfo);

            c.moveToNext();
        }

        c.close();
        return songInfoArrayList;
    }

    public static boolean updateDownloadID(SQLiteDatabase db,String songName,String id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.TableColumns.DOWNLOAD_ID, id);
        try {
            db.update(TABLE_NAME, contentValues, DBHelper.TableColumns.SONG_NAME + "=?", new String[]{songName});
        } catch (Exception e) {
            Log.d(TAG, "updateDownloadID: Exception" + e);
            return false;
        }
        return true;
    }

    public static boolean deleteSong(SQLiteDatabase db,String songName) {

        try {
            db.delete(TABLE_NAME,  DBHelper.TableColumns.SONG_NAME + "=?", new String[]{songName});
        } catch (Exception e) {
            Log.d(TAG, "deleteSong: Exception" + e);
            return false;
        }
        return true;
    }
    public static boolean deleteSongByDownloadID(SQLiteDatabase db, String downloadId) {

        try {
            db.delete(TABLE_NAME,  DBHelper.TableColumns.DOWNLOAD_ID + "=?", new String[]{downloadId});
        } catch (Exception e) {
            Log.d(TAG, "deleteSongByDownloadID: Exception" + e);
            return false;
        }
        return true;
    }

    public static ArrayList<SongInfo> getSongsByDownloadID(SQLiteDatabase db, String downloadId) {
        ArrayList<SongInfo>songInfoArrayList=new ArrayList<>();
        Cursor c=db.query(TABLE_NAME,new String[]{

                        DBHelper.TableColumns.ALBUM_NAME,
                        DBHelper.TableColumns.ARTIST_NAME,
                        DBHelper.TableColumns.DOWNLOAD_FOLDER,
                        DBHelper.TableColumns.DOWNLOAD_URL,
                        DBHelper.TableColumns.EXTENSION,
                        DBHelper.TableColumns.LANGUAGE,
                        DBHelper.TableColumns.SONG_NAME,
                        DBHelper.TableColumns.YEAR}
                ,DBHelper.TableColumns.DOWNLOAD_ID+"=?",new String[]{downloadId},null,null,null
        );
        c.moveToFirst();
        while (c.getCount()>0&&!c.isAfterLast()){
            SongInfo songInfo=new SongInfo();
            songInfo.setAlbum_name(c.getString(c.getColumnIndex(DBHelper.TableColumns.ALBUM_NAME)));
            songInfo.setArtist_name(c.getString(c.getColumnIndex(DBHelper.TableColumns.ARTIST_NAME)));
            songInfo.setDownload_folder(c.getString(c.getColumnIndex(DBHelper.TableColumns.DOWNLOAD_FOLDER)));
            songInfo.setSimpleDownload_url(c.getString(c.getColumnIndex(DBHelper.TableColumns.DOWNLOAD_URL)));
            songInfo.setLanguage(c.getString(c.getColumnIndex(DBHelper.TableColumns.LANGUAGE)));
            songInfo.setSong_name(c.getString(c.getColumnIndex(DBHelper.TableColumns.SONG_NAME)));
            songInfo.setYear(c.getString(c.getColumnIndex(DBHelper.TableColumns.YEAR)));
            songInfo.setExtension(c.getString(c.getColumnIndex(DBHelper.TableColumns.EXTENSION)));
            songInfoArrayList.add(songInfo);
            c.moveToNext();
        }

        c.close();
        return songInfoArrayList;



    }
}
