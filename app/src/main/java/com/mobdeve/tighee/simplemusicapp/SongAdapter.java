package com.mobdeve.tighee.simplemusicapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongViewHolder> {

    private ArrayList<Song> songs;
    private MusicService musicService;

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
                musicService.playSong(songViewHolder.getBindingAdapterPosition());
                ((MainActivity) parent.getContext()).setSongData(songs.get(songViewHolder.getBindingAdapterPosition()));
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

    public void playSong() {
        this.musicService.playSong();
    }

    public void pauseSong() {
        this.musicService.pauseSong();
    }

    public void getProgress() {

    }
}
