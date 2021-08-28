package com.mobdeve.tighee.simplesongapp;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MusicService
        extends Service
        implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private final String TAG = "MusicServiceLog";

    private MediaPlayer player;

    private int currSongId = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Service MusicService","onCreate Initialize");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d("Service MusicService","onStartCommand Play");

        this.player = new MediaPlayer();
        this.player.setOnPreparedListener(this);
        this.player.setVolume(50,50);

        return Service.START_STICKY;
    }

    public void playSong(int songId) {
        if(this.currSongId != songId) {
            if(this.player.isPlaying()) {
                this.player.stop();
                this.player.reset();
            }

            String path = "android.resource://" + getPackageName() + "/" + songId;

            Log.d(TAG, "playSong: " + songId + " " + path);

            Uri songPath = Uri.parse(path);
            try {
                this.player.setDataSource(getApplicationContext(), songPath);
                this.player.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                );
                this.currSongId = songId;
                this.player.prepareAsync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        mediaPlayer.start();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }


}
