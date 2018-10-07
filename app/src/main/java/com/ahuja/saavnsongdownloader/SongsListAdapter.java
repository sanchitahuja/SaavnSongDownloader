package com.ahuja.saavnsongdownloader;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.ahuja.saavnsongdownloader.Database.DBData;
import com.ahuja.saavnsongdownloader.Database.DBHelper;
import com.ahuja.saavnsongdownloader.Network.models.Song;
import com.ahuja.saavnsongdownloader.Network.models.SongInfo;

import java.util.ArrayList;

/**
 * Created by Sanchit Ahuja on 9/20/2018.
 */
public class SongsListAdapter extends RecyclerView.Adapter<SongsListAdapter.Holder> {
   public static final String TAG="SongsListAdapter";
private Context context;
private ArrayList<SongInfo>songInfoArrayList;
private DBHelper dbHelper;
    public SongsListAdapter(Context context) {
        this.context = context;
        dbHelper=new DBHelper(context);
        this.songInfoArrayList = DBData.getSongs(dbHelper.getReadableDatabase());
    }

    public void updateAdapterList(){

        songInfoArrayList= DBData.getSongs(dbHelper.getReadableDatabase());
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater li= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=li.inflate(R.layout.reyclerview_layout,null,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {

        final SongInfo songInfo=songInfoArrayList.get(i);
        holder.textView.setText(songInfo.getSong_name()+"\nArtist:"+songInfo.getArtist_name());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,DownloadService.class);
                intent.putExtra(DownloadService.SONG_DOWNLOAD,songInfo);
                context.startService(intent);
            }
        });

      }

    @Override
    public int getItemCount() {

        Log.d(TAG, "getItemCount: "+songInfoArrayList.size());
        return songInfoArrayList.size();
    }
    public boolean deleteSong(int position){
        SongInfo songInfo=songInfoArrayList.get(position);
        notifyItemRemoved(position);
        return DBData.deleteSong(dbHelper.getWritableDatabase(),songInfo.getSong_name());
    }
    static class Holder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;
        RelativeLayout foreground,background;
        public Holder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.song_info_tv);
            imageView=itemView.findViewById(R.id.download_iv);
            foreground=itemView.findViewById(R.id.view_foreground);
            background=itemView.findViewById(R.id.view_background);
        }
    }
}
