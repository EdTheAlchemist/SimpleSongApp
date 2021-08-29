package com.mobdeve.tighee.simplemusicapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class SongViewHolder extends RecyclerView.ViewHolder {
    private TextView titleTv, artistTv, albumTv;
    private ImageView albumArtIv;

    public SongViewHolder(View itemView) {
        super(itemView);

        this.titleTv = itemView.findViewById(R.id.titleTv);
        this.artistTv = itemView.findViewById(R.id.artistTv);
        this.albumTv = itemView.findViewById(R.id.albumTv);
        this.albumArtIv = itemView.findViewById(R.id.albumArtIv);
    }

    public void bindData(Song s) {
        this.titleTv.setText(s.getTitle());
        this.artistTv.setText(s.getArtist());
        this.albumTv.setText(s.getAlbum());
        this.albumArtIv.setImageResource(s.getAlbumImageId());
    }
}
