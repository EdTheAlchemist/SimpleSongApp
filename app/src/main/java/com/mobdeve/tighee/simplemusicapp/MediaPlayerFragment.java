package com.mobdeve.tighee.simplemusicapp;

import android.app.Activity;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MediaPlayerFragment extends Fragment {

    private final String TAG = "MediaPlayerFragmentLog";

    private TextView fragDisplayTv;
    private ImageView fragAlbumArtIv;
    private SeekBar fragProgressSb;
    private ImageButton fragPlayIbtn;

    private Boolean isPlaying = false;


    private MyBroadcastReceiver myBroadcastReceiver;
    private IntentFilter myIntentFilter;
    
    private Handler handler;

    public MediaPlayerFragment() {
        super(R.layout.ui_fragment_layout);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.handler = new Handler(Looper.getMainLooper());

        this.myBroadcastReceiver = new MyBroadcastReceiver();
        this.myIntentFilter = new IntentFilter();
        this.myIntentFilter.addAction(HelperClass.PLAY_ACTION);
        this.myIntentFilter.addAction(HelperClass.STOP_ACTION);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.fragDisplayTv = view.findViewById(R.id.fragDisplayTv);
        this.fragAlbumArtIv = view.findViewById(R.id.fragAlbumArtIv);
        this.fragProgressSb = view.findViewById(R.id.fragProgressSb);
        this.fragPlayIbtn = view.findViewById(R.id.fragPlayIbtn);

        this.fragProgressSb.setMax(100);

        this.fragPlayIbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPlaying) {
                    fragPlayIbtn.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    fragPlayIbtn.setImageResource(android.R.drawable.ic_media_pause);
                }
                isPlaying = !isPlaying;
                ((MainActivity)getActivity()).playSong(isPlaying);
            }
        });

    }

    public void setViewsToNoPlaying() {
        this.fragDisplayTv.setText("");
    }

    public void setSongData(Song s) {
        this.isPlaying = true;
        fragPlayIbtn.setImageResource(android.R.drawable.ic_media_pause);

        this.fragDisplayTv.setText(s.getTitle() + " by " + s.getArtist());
        this.fragAlbumArtIv.setImageResource(s.getAlbumImageId());
    }

    public interface MPFragmentCommunication {
        void playSong(Boolean b);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(this.myBroadcastReceiver, this.myIntentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(this.myBroadcastReceiver);
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        private ScheduledExecutorService executorService;
        
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: ");
            if(intent.getAction() == HelperClass.PLAY_ACTION) {
                Log.d(TAG, "onReceive: PLAY");
                fragProgressSb.setMax(((MainActivity) getActivity()).getCurrentDuration());

                this.executorService = Executors.newSingleThreadScheduledExecutor();
                this.executorService.scheduleWithFixedDelay(
                    new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: inside executor service");
                            int progress = ((MainActivity) getActivity()).getCurrentProgress();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    fragProgressSb.setProgress(progress);
                                }
                            });
                        }
                    }, 1, 1, TimeUnit.SECONDS);
            } else if (intent.getAction() == HelperClass.STOP_ACTION) {
                Log.d(TAG, "onReceive: stop received");
                executorService.shutdownNow();
            }
        }
    }
}
