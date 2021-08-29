package com.mobdeve.tighee.simplemusicapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity
        extends AppCompatActivity
        implements MediaPlayerFragment.MPFragmentCommunication {

    private final String TAG = "MainActivityLog";

    private ArrayList<Song> songs;

    private RecyclerView recyclerView;
    private SongAdapter songAdapter;

    private boolean bound;
    private MusicService musicService;
    private Intent musicIntent;

    private FragmentManager fragmentManager;
    private MediaPlayerFragment mediaPlayerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.fragmentManager = getSupportFragmentManager();
        this.fragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .add(R.id.musicPlayerFcv, MediaPlayerFragment.class, new Bundle())
            .commit();

        this.songs = HelperClass.generateData();

        this.recyclerView = findViewById(R.id.recyclerView);
        this.songAdapter = new SongAdapter(songs);
        this.recyclerView.setAdapter(this.songAdapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        this.musicIntent = new Intent(this, MusicService.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!bound) {
            bindService(musicIntent, mConnection, BIND_AUTO_CREATE);
            startService(musicIntent);
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(bound) {
            unbindService(mConnection);
            stopService(musicIntent);
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            musicService = (binder.getService());
            bound = true;

            musicService.setSongs(songs);
            songAdapter.setMusicService(musicService);

            mediaPlayerFragment = (MediaPlayerFragment) fragmentManager.findFragmentById(R.id.musicPlayerFcv);
            Log.d(TAG,"ServiceConnection made");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
            songAdapter.setMusicService(null);
        }
    };

    @Override
    public void playSong(Boolean isPlaying) {
        if(isPlaying) {
            this.songAdapter.playSong();
        } else {
            this.songAdapter.pauseSong();
        }
    }

    public void setSongData(Song s) {
        this.mediaPlayerFragment.setSongData(s);
    }

    public int getCurrentProgress() {
        return this.musicService.getProgress();
    }

    public int getCurrentDuration() {
        return this.musicService.getDuration();
    }
}
