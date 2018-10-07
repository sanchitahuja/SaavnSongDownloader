package com.ahuja.saavnsongdownloader.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.ahuja.saavnsongdownloader.Database.DBHelper.Consts.COMMA;

/**
 * Created by Sanchit Ahuja on 9/9/2018.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME="songsdb";
    public static final String TABLE_NAME="songsinfotable";
    public static final int DB_VER=1;
    public interface Consts{
        String LBR = " ( ";
        String RBR = " ) ";
        String COMMA = " , ";
        String SEMICOL = " ; ";

        String TYPE_INT = " INTEGER ";
        String TYPE_UNIQUE = " UNIQUE ";
        String TYPE_TEXT = " TEXT ";
        String TYPE_PK = " PRIMARY KEY ";
        String TYPE_AI = " AUTOINCREMENT ";
        String TYPE_REAL = " REAL ";
    }
    public interface TableColumns{
        String DOWNLOAD_URL="download_url",ALBUM_NAME="album_name",SONG_NAME="song_name",
                DOWNLOAD_FOLDER="download_folder";

         String ARTIST_NAME="artist_name",YEAR="year",LANGUAGE="language";
         String EXTENSION="extension";
         String DOWNLOAD_ID="downloadid";
    }
    public static final String CMD_CREATE_TABLE=
            "CREATE TABLE IF NOT EXISTS "+
                    TABLE_NAME+ Consts.LBR+
                    TableColumns.DOWNLOAD_URL+ Consts.TYPE_TEXT+ Consts.TYPE_UNIQUE+ COMMA+
                    TableColumns.ALBUM_NAME+ Consts.TYPE_TEXT+ COMMA+
                    TableColumns.SONG_NAME+ Consts.TYPE_TEXT+ COMMA+
                    TableColumns.DOWNLOAD_FOLDER+ Consts.TYPE_TEXT+ COMMA+
                    TableColumns.ARTIST_NAME+ Consts.TYPE_TEXT+ COMMA+
                    TableColumns.YEAR+ Consts.TYPE_TEXT+ COMMA+
                    TableColumns.LANGUAGE+ Consts.TYPE_TEXT+ COMMA+
                    TableColumns.EXTENSION+ Consts.TYPE_TEXT+ COMMA+
                    TableColumns.DOWNLOAD_ID+ Consts.TYPE_TEXT+
                    Consts.RBR+ Consts.SEMICOL;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CMD_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
