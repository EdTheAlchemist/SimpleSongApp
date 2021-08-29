package com.mobdeve.tighee.simplemusicapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
* I decided to go with making a fragment for the player's UI in case I felt like expanding this to
* have other functions. This way, this fragment could stay on screen while the user goes through the
* app. However, in its current form, this is simply to demonstrate how a fragment can communicate
* with a background service.
* */
public class MediaPlayerFragment extends Fragment {

    private final String TAG = "MediaPlayerFragmentLog";

    // Fragment views
    private TextView fragDisplayTv;
    private ImageView fragAlbumArtIv;
    private SeekBar fragProgressSb;
    private ImageButton fragPlayIbtn;

    // Tracks if a song is playing or not
    private Boolean isPlaying = false;

    // Components of our BroadcastReceiver
    private MyBroadcastReceiver myBroadcastReceiver;
    private IntentFilter myIntentFilter;

    public MediaPlayerFragment() {
        super(R.layout.ui_fragment_layout);
    }

    // I honestly could have placed this in the onViewCreated as both are very similar are used for
    // initialization. This is simply to point out this method exists.
    // Currently handles the initialization of the BroadcastReceiver components
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.myBroadcastReceiver = new MyBroadcastReceiver();
        this.myIntentFilter = new IntentFilter();
        this.myIntentFilter.addAction(HelperClass.PLAY_ACTION);
        this.myIntentFilter.addAction(HelperClass.PAUSE_ACTION);
        this.myIntentFilter.addAction(HelperClass.STOP_ACTION);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    // See note of onCreateView
    // Currently handles the initialization of the views
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.fragDisplayTv = view.findViewById(R.id.fragDisplayTv);
        this.fragAlbumArtIv = view.findViewById(R.id.fragAlbumArtIv);
        this.fragProgressSb = view.findViewById(R.id.fragProgressSb);
        this.fragPlayIbtn = view.findViewById(R.id.fragPlayIbtn);

        this.fragPlayIbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // The conditional statement handles the showing of the play or pause icon
                if(isPlaying) {
                    fragPlayIbtn.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    fragPlayIbtn.setImageResource(android.R.drawable.ic_media_pause);
                }
                isPlaying = !isPlaying;
                // A trigger to start playing something. If nothing has played yet or the
                // MediaPlayer has been reset, the MediaPlayer would play the first song. Otherwise,
                // it would resume, playing whatever was still loaded into the MediaPlayer
                ((MainActivity) getActivity()).playSong(isPlaying);
            }
        });

        // I only need the onStopTrackingTouch here as I just want to know the progress when the
        // user lets go of the SeekBar
        this.fragProgressSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                return;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                return;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Uses the seekTo method of MainActivity to communicate with the MusicService
                ((MainActivity) getActivity()).seekTo(seekBar.getProgress());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Registers the BroadcastReceiver
        getActivity().registerReceiver(this.myBroadcastReceiver, this.myIntentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Unregisters the BroadcastReceiver
        getActivity().unregisterReceiver(this.myBroadcastReceiver);
    }

    /*
    * Technically, I could have used interfaces with the MainActivity to communicate with the
    * MusicService; however, I wanted to demonstrate how a BroadcastReceiver setup could work
    * between the components that dealt with the MediaPlayer's UI and the MediaPlayer (in the
    * service).
    * This BroadcastReceiver handles broadcasts when a play, pause, or stop command had been
    * performed on MediaPlayer (in the MusicService)
    * */
    public class MyBroadcastReceiver extends BroadcastReceiver {
        // These components are used when updating the SeekBar with the current progress of the
        // MediaPlayer. We offload from the Main Thread because we don't want the UI thread querying
        // the MediaPlayer (in the MusicService) every second. We then use a handler associated to
        // the Main Thread to push any UI updates
        private ScheduledExecutorService executorService;
        private Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void onReceive(Context context, Intent intent) {
            // A play action was received
            if(intent.getAction() == HelperClass.PLAY_ACTION) {
                Log.d(TAG, "onReceive: Play action received.");

                // Set playing to true and adjust the play button to a pause icon
                isPlaying = true;
                fragPlayIbtn.setImageResource(android.R.drawable.ic_media_pause);

                // Get data from the intent to properly set what is currently being played.
                fragDisplayTv.setText(intent.getStringExtra(HelperClass.TITLE_INTENT_KEY) + " by " + intent.getStringExtra(HelperClass.ARTIST_INTENT_KEY));
                fragAlbumArtIv.setImageResource(intent.getIntExtra(HelperClass.ALBUM_IMAGE_INTENT_KEY, 0));
                fragProgressSb.setMax(intent.getIntExtra(HelperClass.DURATION_INTENT_KEY, 0));

                // Initialize the ExecutorService with a single thread that can be scheduled at a
                // constant rate (i.e. repeat until killed). This is done with
                // scheduleWithFixedDelay(), which takes (1) a runnable object, (2) an initial
                // delay, (3) delay time after the first delay, and (4) the time unit used.
                this.executorService = Executors.newSingleThreadScheduledExecutor();
                this.executorService.scheduleWithFixedDelay(
                    new Runnable() {
                        @Override
                        public void run() {
                            // While this isn't exactly a heavy task, we don't want to overload the
                            // Main Thread.
                            int progress = ((MainActivity) getActivity()).getCurrentProgress();
                            Log.d(TAG, "run: Inside executor service. Progress received at " + progress);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    // A UI update; hence why we push to the Main Thread
                                    fragProgressSb.setProgress(progress);
                                }
                            });
                        }
                    }, 1, 1, TimeUnit.SECONDS);
            } else {
                // This happens if a stop or pause action was received. Regardless, we know the
                // player stopped and that we have to stop monitoring the progress of the song.
                isPlaying = false;
                this.executorService.shutdownNow();

                if(intent.getAction() == HelperClass.STOP_ACTION) {
                    // If a stop action was detected, reset the views
                    Log.d(TAG, "onReceive: Stop action received.");

                    fragPlayIbtn.setImageResource(android.R.drawable.ic_media_play);
                    fragDisplayTv.setText("Please select a song and enjoy :)");
                    fragAlbumArtIv.setImageResource(android.R.color.transparent);
                    fragProgressSb.setProgress(0);
                } else {
                    Log.d(TAG, "onReceive: Pause action received.");
                }
            }
        }
    }
}
