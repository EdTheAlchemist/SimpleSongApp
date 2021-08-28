package com.mobdeve.tighee.simplesongapp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivityLog";

    private ArrayList<Song> songs;

    private RecyclerView recyclerView;
    private SongAdapter songAdapter;

    private boolean bound;
    private MusicService musicService;
    private Intent musicIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.songs = DataGenerator.generateData();

        this.recyclerView = findViewById(R.id.recyclerView);
        this.songAdapter = new SongAdapter(songs);
        this.recyclerView.setAdapter(this.songAdapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        musicIntent = new Intent(this, MusicService.class);
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
            songAdapter.setMusicService(musicService);
            Log.d(TAG,"ServiceConnection made");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
            songAdapter.setMusicService(null);
        }
    };
}
