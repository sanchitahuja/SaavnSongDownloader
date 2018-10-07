package com.ahuja.saavnsongdownloader;


import android.content.DialogInterface;
import android.content.Intent;

import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.ahuja.saavnsongdownloader.Database.DBData;
import com.ahuja.saavnsongdownloader.Database.DBHelper;
import com.ahuja.saavnsongdownloader.Network.ApiClient;
import com.ahuja.saavnsongdownloader.Network.models.Show;
import com.ahuja.saavnsongdownloader.Network.models.Song;
import com.ahuja.saavnsongdownloader.Network.models.SongInfo;
import com.ahuja.saavnsongdownloader.Network.models.SongList;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    public static final String host_address="https://www.saavn.com";
    private static final int PERMISSIONS_REQUEST_STORAGE =101 ;
    DBHelper dbHelper;
    SongsListAdapter songsListAdapter;
    AlertDialog dialog;
    ArrayList<SongInfo> songs_list = new ArrayList<>();
RecyclerView recyclerView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       if(item.getItemId()==R.id.menu_download_all){
           ArrayList<SongInfo>arrayList=DBData.getSongs(dbHelper.getReadableDatabase());
           for (SongInfo songInfo:arrayList){
               Intent intent=new Intent(MainActivity.this,DownloadService.class);
               intent.putExtra(DownloadService.SONG_DOWNLOAD,songInfo);
               MainActivity.this.startService(intent);
           }
           songsListAdapter.updateAdapterList();
       }
       else if(item.getItemId()==R.id.menu_clear_all){
           ArrayList<SongInfo>arrayList=DBData.getSongs(dbHelper.getReadableDatabase());
           for (SongInfo songInfo:arrayList){
              DBData.deleteSong(dbHelper.getWritableDatabase(),songInfo.getSong_name());
           }
           songsListAdapter.updateAdapterList();

       }
       else if(item.getItemId()==R.id.menu_link){
           final AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
           builder.setTitle("Enter Link");
           LayoutInflater li=MainActivity.this.getLayoutInflater();
           builder.setView(li.inflate(R.layout.menu_link,null));
           builder.setPositiveButton("Process", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog1, int which) {
                   AppCompatEditText et= dialog.findViewById(R.id.link_et);
                   String text=et.getText().toString();
                   if (text != null && text.contains(host_address))
                   {
                       new ProcessLink().execute(text.substring(text.indexOf(host_address)).trim());
                   }
                   else{
                       Toast.makeText(MainActivity.this,"Link cannot be processed",Toast.LENGTH_SHORT).show();
                   }
               }
           });
           builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog1, int which) {
                    dialog1.dismiss();
               }
           });
           dialog=builder.create();
           dialog.show();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView=findViewById(R.id.recycler_view);

        dbHelper=new DBHelper(getApplicationContext());
        songsListAdapter=new SongsListAdapter(getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(songsListAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null && type.equals("text/plain"))
        {
            handleSendText(intent);
        }
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);


    }

    public void handleSendText(Intent intent){
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null && sharedText.contains(host_address))
        {

            new ProcessLink().execute(sharedText.substring(sharedText.indexOf(host_address)).trim());
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        if (viewHolder instanceof SongsListAdapter.Holder) {
            // get the removed item name to display it in snack bar


            // remove the item from recycler view
            songsListAdapter.deleteSong( (viewHolder.getAdapterPosition()));

        }


    }

    class ProcessLink extends AsyncTask<String, Void,Void>
    {
        String L = "hindi|english|tamil|telugu|punjabi|marathi|gujarati|bengali|kannada|bhojpuri|malayalam|urdu|rajasthani|odia";

        ApiClient apiRequester;

        Pattern song_album = Pattern.compile("^/(s|p|search)/(song|album)/(" + L + ")?/?([^/]*/)([^/]*/)?(.*)$");
        Pattern show = Pattern.compile("^/(s|search|p|play)/show/(.*)$");
        Pattern playlist = Pattern.compile("^/(s|search)/(playlist|featured|genres)/(" + L + ")?/?([^/]*/)?([^/]*/)?(.*)$");

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            apiRequester = new ApiClient();
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
        }

        void showError()
        {
            Toast.makeText(getApplicationContext(),"Error while processing link", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(String... params)
        {
            String saavn_link = params[0].replace(host_address,"");
            Matcher matcher = song_album.matcher(saavn_link);
            if (matcher.find())
            {
                process_song_album(matcher);
            }
            else if((matcher = show.matcher(saavn_link)).find())
            {
                process_show(matcher);
            }
            else if((matcher = playlist.matcher(saavn_link)).find())
            {
                process_playlist(matcher);
            }
            return null;
        }

        void process_song_album(Matcher matcher)
        {
            String type = matcher.group(2);
            String token = matcher.group(6);
            if(token != null)
            {
                int index = token.indexOf("?");
                if(index>0)
                    token = token.substring(0, index);
            }
            if (type.equals("song"))
            {
                if (token == null || token.length() == 0)
                {
                    showError();
                    return;
                }
                else
                {
                    apiRequester.setSongToken("song", token);
                }
            }
            else if (type.equals("album"))
            {
                if (token == null || token.length() == 0)
                {
                    showError();
                    return;
                } else
                {
                    apiRequester.setSongToken("album", token);
                }
            }
            apiRequester.makeSongRequest().enqueue(callback_song_album);
        }

        void process_show(Matcher matcher)
        {
            String[] arr = matcher.group(2).split("/");
            if (arr.length == 3)
            {
                int index = arr[2].indexOf("?");
                if(index>0)
                    arr[2] = arr[2].substring(0,index);
                apiRequester.setShowToken(arr[2],arr[1],"show");
                apiRequester.makeShowRequest().enqueue(callback_show);
            }
            else if (arr.length == 4)
            {
                int index = arr[3].indexOf("?");
                if(index>0)
                    arr[3] = arr[3].substring(0,index);
                apiRequester.setShowToken(arr[3],arr[1],"episode");
                apiRequester.makeEpisodeRequest().enqueue(callback_episode);
            }
        }

        void process_playlist(Matcher matcher)
        {
            String username;
            String listname = matcher.group(2);
            String group4 = matcher.group(4);
            String group5 = matcher.group(5);
            String token = matcher.group(6);

            if (listname != null)
            {
                if(token !=null)
                {
                    int index = token.indexOf("?");
                    if(index>0)
                        token = token.substring(0, index);
                }
                if (listname.contentEquals("playlist"))
                {
                    if (group4 == null || (group5 == null && token == null)) {
                        return;
                    }
                    username = group4.replace("/", "");
                    if (group5 != null)
                    {
                        listname = group5.replace("/", "").replaceAll("[\\+\\_]", " ");
                    }
                    else
                    {
                        listname = token.replace("/", "").replaceAll("[\\+\\_]", " ");
                    }
                    apiRequester.setPlaylistToken(username,listname,token);
                }
                else if (listname.contentEquals("featured"))
                {
                    listname = group4 == null ? token : group4;
                    listname = listname.replace("/", "").replaceAll("[\\+\\_]", " ");
                    apiRequester.setPlaylistToken("username",listname,token);
                }
                apiRequester.makePlaylistRequest().enqueue(callback_playlist);
            }
        }

        Callback<List<Song>> callback_song_album = new Callback<List<Song>>()
        {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response)
            {
                List<Song> result = response.body();
                if(result.size()>0)
                    songs_list.clear();
                else
                    return;
                for (Song s : result)
                {
                    String encUrl = s.moreInfo.encryptedMediaUrl;
                    if(encUrl==null || encUrl.equals("") || encUrl.equalsIgnoreCase("us98KHesG0c=")) //check if url is null
                    {
                        continue;
                    }
                    SongInfo song = new SongInfo();
                    song.setSong_name(s.title);
                    song.setLanguage(s.language);
                    song.setYear(s.year);
                    song.setAlbum_name(s.moreInfo.album);
                    song.setDownload_folder(s.moreInfo.album);
                    if(s.moreInfo.artistMap.primaryArtists.size()>=1)
                    {
                        song.setArtist_name(s.moreInfo.artistMap.primaryArtists.get(0).name);
                    }
                    if(Boolean.parseBoolean(s.moreInfo._320kbps))
                    {
                        song.setDownload_url(encUrl,true);
                    }
                    else
                    {
                        song.setDownload_url(encUrl,false);
                    }
                    DBData.addSong(dbHelper.getWritableDatabase(),song);
                    songs_list.add(song);
                    songsListAdapter.updateAdapterList();
                }

            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t)
            {
                Toast.makeText(getApplicationContext(),"Error while processing link",Toast.LENGTH_LONG).show();
            }
        };

        Callback<Show> callback_show = new Callback<Show>()
        {
            @Override
            public void onResponse(Call<Show> call, Response<Show> response)
            {
                List<Song> result = response.body().episodes;
                if(result.size()>0)
                    songs_list.clear();
                else
                    return;
                for (Song s : result)
                {
                    String encUrl = s.moreInfo.encryptedMediaUrl;
                    if(encUrl==null || encUrl.equals("") || encUrl.equalsIgnoreCase("us98KHesG0c=")) //check if url is null
                    {
                        continue;
                    }
                    SongInfo song = new SongInfo();
                    song.setSong_name(s.title);
                    song.setLanguage(s.language);
                    song.setYear(s.year);
                    song.setAlbum_name(s.moreInfo.showTitle);
                    song.setDownload_folder(s.moreInfo.showTitle,s.moreInfo.seasonTitle);
                    if(s.moreInfo.artistMap.primaryArtists.size()>=1)
                    {
                        song.setArtist_name(s.moreInfo.artistMap.primaryArtists.get(0).name);
                    }
                    song.setDownload_url(encUrl,false);
                    DBData.addSong(dbHelper.getWritableDatabase(),song);
                    songs_list.add(song);

                    songsListAdapter.updateAdapterList();
                }

            }

            @Override
            public void onFailure(Call<Show> call, Throwable t)
            {
                showError();
            }
        };

        Callback<Song> callback_episode = new Callback<Song>()
        {
            @Override
            public void onResponse(Call<Song> call, Response<Song> response)
            {
                Song s = response.body();
                if(s != null)
                    songs_list.clear();
                else
                    return;
                String encUrl = s.moreInfo.encryptedMediaUrl;
                if(encUrl==null || encUrl.equals("") || encUrl.equalsIgnoreCase("us98KHesG0c=")) //check if url is null
                {
                    return;
                }
                SongInfo song = new SongInfo();
                song.setSong_name(s.title);
                song.setLanguage(s.language);
                song.setYear(s.year);
                song.setAlbum_name(s.moreInfo.showTitle);
                song.setDownload_folder(s.moreInfo.showTitle,s.moreInfo.seasonTitle);
                if(s.moreInfo.artistMap.primaryArtists.size()>=1)
                {
                    song.setArtist_name(s.moreInfo.artistMap.primaryArtists.get(0).name);
                }
                song.setDownload_url(encUrl,false);
                DBData.addSong(dbHelper.getWritableDatabase(),song);
                songs_list.add(song);

                songsListAdapter.updateAdapterList();

            }

            @Override
            public void onFailure(Call<Song> call, Throwable t)
            {
                showError();
            }
        };

        Callback<SongList> callback_playlist = new Callback<SongList>()
        {
            @Override
            public void onResponse(Call<SongList> call, Response<SongList> response)
            {
                String playlist_name = response.body().title;
                List<Song> result = response.body().list;
                if(result.size()>0)
                    songs_list.clear();
                else
                    return;
                for (Song s : result)
                {
                    String encUrl = s.moreInfo.encryptedMediaUrl;
                    if(encUrl==null || encUrl.equals("") || encUrl.equalsIgnoreCase("us98KHesG0c=")) //check if url is null
                    {
                        continue;
                    }
                    SongInfo song = new SongInfo();
                    song.setSong_name(s.title);
                    song.setLanguage(s.language);
                    song.setYear(s.year);
                    song.setAlbum_name(s.moreInfo.album);

                    // Set here download Folder
                    song.setDownload_folder(playlist_name);

                    if(s.moreInfo.artistMap.primaryArtists.size()>=1)
                    {
                        song.setArtist_name(s.moreInfo.artistMap.primaryArtists.get(0).name);
                    }
                    if(Boolean.getBoolean(s.moreInfo._320kbps)
                            )
                    {
                        song.setDownload_url(encUrl,true);
                    }
                    else
                    {
                        song.setDownload_url(encUrl,false);
                    }
                    DBData.addSong(dbHelper.getWritableDatabase(),song);
                    songs_list.add(song);

                    songsListAdapter.updateAdapterList();
                }}

            @Override
            public void onFailure(Call<SongList> call, Throwable t)
            {
                Toast.makeText(getApplicationContext(),"Error while processing link",Toast.LENGTH_LONG).show();
            }
        };

    }
}
