package com.mobdeve.tighee.simplemusicapp;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

public class MusicService extends Service {

    private final String TAG = "MusicServiceLog";

    // Our data
    private ArrayList<Song> songs;

    // The actual MediaPlayer component that handles playing audio
    private MediaPlayer player;

    // Medyo obvious, pero this is the position in the ArrayList of the current song being played.
    // Its initialized to -1 -- a value that won't be part of the indexes of an ArrayList.
    private int currSongPosition = -1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG,"onStartCommand has been called. Media initialized.");

        // Initialize the MediaPlayer and its listeners
        this.player = new MediaPlayer();
        this.player.setVolume(50,50);

        // When the MediaPlayer has been successfully prepared. See playSongAtPosition() for the
        // occurrence of preparedAsynch() being called. When ready, the MediaPlayer starts playing
        // the media specified and sends a broadcast to inform the MediaPlayerFragment that a song
        // is being played.
        this.player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.d(TAG, "MediaPlayer has been prepared: ");
                mediaPlayer.start();
                sendSongBroadcast(HelperClass.PLAY_ACTION);
            }
        });

        // This happens when a song has finished playing (i.e. completed). When a song has
        // completed, we send a broadcast to the MediaPlayerFragment that we've stopped playing the
        // song. We then check if the song is the last in the list. If it is, then we reset the
        // currSongPosition. Otherwise, we continue playing the next song.
        // This could be expanded if shuffle or repeat options are added.
        this.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d(TAG, "a song has finished (i.e. onComplete called)");
                sendSongBroadcast(HelperClass.STOP_ACTION);

                if(currSongPosition != songs.size() - 1) {
                    playSongAtPosition(currSongPosition + 1);
                } else {
                    currSongPosition = -1;
                }
            }
        });

        // As we explicitly started (and plan to end) this service, we use START_STICKY. The other
        // options (e.g. START_NOT_STICKY or START_REDELIVER_INTENT are used for services that
        // should only remain running while processing any commands sent to them. See Android
        // Documentation for more info.
        return Service.START_STICKY;
    }

    // Handles freeing up resources of the MediaPlayer when the service is to be destroyed.
    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
        player.release();
        Log.d("Service MusicService","onDestroy Stop");
    }

    // A public method called by the Main Activity to delivery the data here.
    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    /*
    * The main method handling playing a song. It handles numerous cases:
    *   1. When playing a song for the first time via the play button
    *   2. When a RecyclerView item was selected
    *       2.1. If the item is the same as what is playing, nothing happens.
    *       2.2. If the item is different from what is playing, it plays the new song.
    *   3. When a continuation play is triggered (i.e. play next from after onComplete)
    *   4. Also handles when the same item was selected by the player wasn't playing.
    *
    * The parameter position refers to the position of a song in the ArrayList.
    * */
    public void playSongAtPosition(int position) {
        Log.d(TAG, "playSongAtPosition called. Position at " + position);

        // Handles when the MediaPlayer is being played for the first time or when it has been
        // reset.
        if(position == -1) {
            position = 0;
        }

        /*
        * If the song passed in isn't the song that's loaded into the media player, we'd want to
        * (1) stop the player, (2) inform the MediaPlayerFragment via a broadcast a stop happened,
        * (3) ready the path to the new song, (4) ready the player for official preparation, (5)
        * adjust the current position pointer, and (5) prepare the media asynchronously.
        *
        * Technically, this didn't need to be done because the audio files are in the project
        * folder; however, I choose to implement a more generalizaed form that looks at the path.
        * This can be modified to look for audio files somewhere in the phone (e.g. internal memory,
        * SD card)
        * */
        if(position != this.currSongPosition) {
            if(this.player.isPlaying()) {
                this.player.stop();
                sendSongBroadcast(HelperClass.STOP_ACTION);
            }

            String path = "android.resource://" + getPackageName() + "/" + this.songs.get(position).getSongId();
            Uri songPath = Uri.parse(path);
            try {
                this.player.reset();
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
        } else if(!this.player.isPlaying()) {
            // If the song selected is the same as what is loaded, we simply play because the
            // MediaPlayer is assumed to still be prepared and readying for playing.
            this.player.start();
        }
    }

    // Bridging method meant to connect the MediaPlayerFragment to the MusicService.
    public void playSong() {
        this.playSongAtPosition(this.currSongPosition);
    }

    // Bridging method meant to connect the MediaPlayerFragment to the MusicService.
    public void pauseSong() {
        this.player.pause();
        sendSongBroadcast(HelperClass.PAUSE_ACTION);
    }

    // Bridging method meant to connect the MediaPlayerFragment to the MusicService.
    public int getProgress() {
        return this.player.getCurrentPosition();
    }

    private void sendSongBroadcast(String action) {
        Intent i = new Intent();
        i.setAction(action);
        if(action == HelperClass.PLAY_ACTION) {
            i.putExtra(HelperClass.DURATION_INTENT_KEY, this.player.getDuration());
            i.putExtra(HelperClass.TITLE_INTENT_KEY, this.songs.get(this.currSongPosition).getTitle());
            i.putExtra(HelperClass.ARTIST_INTENT_KEY, this.songs.get(this.currSongPosition).getArtist());
            i.putExtra(HelperClass.ALBUM_IMAGE_INTENT_KEY, this.songs.get(this.currSongPosition).getAlbumImageId());
        }
        sendBroadcast(i);
    }

    public void seekTo(int i) {
        this.player.seekTo(i);
    }

    // -----Start of Service Binding Components-----
    private final IBinder binder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
    // -----End of Service Binding Components-----
}
