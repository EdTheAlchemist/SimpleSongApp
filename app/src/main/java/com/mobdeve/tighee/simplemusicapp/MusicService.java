package com.mobdeve.tighee.simplemusicapp;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

public class MusicService
        extends Service
        implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private final String TAG = "MusicServiceLog";

    private MediaPlayer player;
    private ArrayList<Song> songs;

    private int currSongPosition = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Service MusicService","onCreate Initialize");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d("Service MusicService","onStartCommand Play");

        initializeMediaPlayer();

        return Service.START_STICKY;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public void playSong(int position) {
        if(this.player == null) {
            initializeMediaPlayer();
        }

        if(this.currSongPosition != position) {
            if(this.player.isPlaying()) {
                this.player.stop();
                sendSongBroadcast(HelperClass.STOP_ACTION);
            }
            this.player.reset();

            String path = "android.resource://" + getPackageName() + "/" + this.songs.get(position).getSongId();

            Log.d(TAG, "playSong: " + this.songs.get(position).getTitle() + " " + path);

            Uri songPath = Uri.parse(path);
            try {
                this.player.setDataSource(getApplicationContext(), songPath);
                this.player.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                );
                this.currSongPosition = position;
                this.player.prepareAsync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void playSong() {
        this.player.start();
        sendSongBroadcast(HelperClass.PLAY_ACTION);
    }

    public void pauseSong() {
        this.player.pause();
        sendSongBroadcast(HelperClass.STOP_ACTION);
    }

    public void playNextSong() {
        if(this.currSongPosition != this.songs.size()-1) {
            playSong(this.currSongPosition + 1);
        } else {
            this.player.release();
            this.player = null;
        }
    }

    public void playPreviousSong() {
        if(this.currSongPosition != 0) {
            playSong(this.currSongPosition - 1);
        } else {
            playSong(this.currSongPosition);
        }
    }

    public int getProgress() {
        return this.player.getCurrentPosition();
    }

    public int getDuration() {
        return this.player.getDuration();
    }

    private void initializeMediaPlayer() {
        this.player = new MediaPlayer();
        this.player.setOnPreparedListener(this);
        this.player.setOnCompletionListener(this);
        this.player.setVolume(50,50);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
        player.release();
        Log.d("Service MusicService","onDestroy Stop");
    }

    //(x) Binding related code
    private final IBinder binder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onPrepared: ");
        sendSongBroadcast(HelperClass.PLAY_ACTION);
        mediaPlayer.start();
        // for testing purposes
        //mediaPlayer.seekTo(mediaPlayer.getDuration() - 10000);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        sendSongBroadcast(HelperClass.STOP_ACTION);
        playNextSong();
    }

    private void sendSongBroadcast(String action) {
        Intent i = new Intent();
        i.setAction(action);
        sendBroadcast(i);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }
}
