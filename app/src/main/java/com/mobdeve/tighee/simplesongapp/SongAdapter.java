package com.mobdeve.tighee.simplesongapp;

import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SongAdapter
        extends RecyclerView.Adapter<SongViewHolder>
        implements MediaPlayer.OnCompletionListener {

    private ArrayList<Song> songs;
    private MusicService musicService;

    private int lastPressed = -1;

    public SongAdapter(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public void setMusicService(MusicService musicService) {
        this.musicService = musicService;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);

        SongViewHolder songViewHolder = new SongViewHolder(v);
        songViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicService.playSong(songs.get(songViewHolder.getBindingAdapterPosition()).getSongId());
            }
        });

        return songViewHolder;
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        holder.bindData(this.songs.get(position));
    }

    @Override
    public int getItemCount() {
        return this.songs.size();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if(this.lastPressed != getItemCount()) {
            this.lastPressed = this.lastPressed + 1;
            musicService.playSong(this.songs.get(this.lastPressed).getSongId());
        }
    }
}
