package com.mobdeve.tighee.simplemusicapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class SongAdapter extends RecyclerView.Adapter<SongViewHolder> {

    private ArrayList<Song> songs;
    private MusicService musicService;

    public SongAdapter(ArrayList<Song> songs) {
        this.songs = songs;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);

        SongViewHolder songViewHolder = new SongViewHolder(v);
        songViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When an item is clicked, inform the MediaPlayer (in MusicService) to play the
                // song
                musicService.playSongAtPosition(songViewHolder.getBindingAdapterPosition());
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

    public void setMusicService(MusicService musicService) {
        this.musicService = musicService;
    }
}
