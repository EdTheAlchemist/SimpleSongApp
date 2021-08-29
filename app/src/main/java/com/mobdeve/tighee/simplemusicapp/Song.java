package com.mobdeve.tighee.simplemusicapp;

public class Song {
    private String artist, title, album;
    private int songId, albumImageId;

    public Song(String title, String artist, String album, int albumImageId, int songId) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.albumImageId = albumImageId;
        this.songId = songId;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public int getSongId() {
        return songId;
    }

    public int getAlbumImageId() {
        return albumImageId;
    }
}
