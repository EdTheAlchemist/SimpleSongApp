package com.mobdeve.tighee.simplemusicapp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivityLog";

    // our data for the app
    private ArrayList<Song> songs;

    // RecyclerView components
    private RecyclerView recyclerView;
    private SongAdapter songAdapter;

    // Components related to the service / mediaplayer
    private boolean bound;
    private MusicService musicService;
    private Intent musicIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inflate the media player UI into the fragment container
        getSupportFragmentManager().beginTransaction()
            .setReorderingAllowed(true)
            .add(R.id.musicPlayerFcv, MediaPlayerFragment.class, new Bundle())
            .commit();

        // Generates the data
        this.songs = HelperClass.generateData();

        // Initializes the RecyclerView
        this.recyclerView = findViewById(R.id.recyclerView);
        this.songAdapter = new SongAdapter(songs);
        this.recyclerView.setAdapter(this.songAdapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Saves a copy of the intent (for when we want to start or cancel the service again)
        this.musicIntent = new Intent(this, MusicService.class);
        startService(musicIntent);
        Log.d(TAG, "onCreate called and MusicService started.");
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if a service is bound. This will always be true given our current setup, but it
        // would be useful if this were to be modified to handle playing songs outside of the app.
        if(!bound) {
            bindService(musicIntent, mConnection, BIND_AUTO_CREATE);
            Log.d(TAG, "onStart: service bound");
        }
    }

    @Override
    protected void onStop(){
        super.onStop();

        // See comment in onStart for the general sentiment.
        if(bound) {
            unbindService(mConnection);

            // Unbinding actually doesn't call onDisconnect() in the ServiceConnection; hence, why
            // we still perform a clean up here.
            bound = false;
            musicService = null;
            songAdapter.setMusicService(null);

            Log.d(TAG, "onStart: service unbound");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(musicIntent);
        Log.d(TAG, "onDestroy called and MusicService stopped.");
    }

    // Logic for when MusicService is bound to the activity.
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(TAG,"onServiceConnected: ServiceConnection made");
            // Gets the binder
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            // Gets the service from the binder
            musicService = binder.getService();
            bound = true;

            // Passes the data to the MusicService
            musicService.setSongs(songs);
            // Passes a reference of the MusicService to the adapter
            songAdapter.setMusicService(musicService);

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // I don't think we need to set the adapter's MusicService reference to null given our
            // app's set up; however, I felt like doing so in case any generalization / improvements
            // will be made.
            bound = false;
            musicService = null;
            songAdapter.setMusicService(null);
        }
    };

    // A public method meant to be called by the MediaPlayerFragment when the play button is
    // pressed. The MediaPlayerFragment doesn't have a reference to the MusicService, so this acts
    // as their bridge.
    public void playSong(Boolean isPlaying) {
        if(isPlaying) {
            this.musicService.playSong();
        } else {
            this.musicService.pauseSong();
        }
    }

    // A public method meant to be called by the MediaPlayerFragment when its trying to update the
    // SeekBar view. This is actually called off the MainThread. Is a bridging method.
    public int getCurrentProgress() {
        return this.musicService.getProgress();
    }

    // A public method meant to be called by the MediaPlayerFragment when manual changes are made
    // to the SeekBar. This changes the position of the MediaPlayer. Is a bridging method.
    public void seekTo(int i) {
        this.musicService.seekTo(i);
    }
}
