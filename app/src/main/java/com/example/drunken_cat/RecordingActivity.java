package com.example.drunken_cat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.SeekBar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class RecordingActivity extends Fragment
        implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    private ImageButton Play;
    private SeekBar progressBar;
    private TextView songTitleLabel;
    private TextView songCurrentTime;
    private TextView songTotalTime;
    private static  MediaPlayer media;
    private Handler mHandler = new Handler();
    private Utilities utils;
    private  View view;
    public String filename="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.activity_recording, container, false);
        permission();
        media = MediaPlayer.create(getActivity(), Uri.parse("uriString"));;

        File file = new File(getActivity().getFilesDir(),"record.mp4");
        filename = file.getAbsolutePath();

        Play = (ImageButton) view.findViewById(R.id.play);
        progressBar = (SeekBar) view.findViewById(R.id.seekBar3);
        songTitleLabel = (TextView) view.findViewById(R.id.songTitle);
        songCurrentTime = (TextView) view.findViewById(R.id.songCurrentTime);
        songTotalTime = (TextView) view.findViewById(R.id.songTotalTime);

        media = new MediaPlayer();
        utils = new Utilities();

        progressBar.setOnSeekBarChangeListener(this);
        media.setOnCompletionListener(this);

/*
        // start
        view.findViewById(R.id.record_start).setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                getActivity().startService(new Intent(getActivity(), VoiceBackgroundActivity.class));//추가
            }
        });
        // stop
        view.findViewById(R.id.record_stop).setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                getActivity().stopService(new Intent(getActivity(), VoiceBackgroundActivity.class));// 추가
                //stopRecord();
            }
        });
*/


        Play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (media.isPlaying()) {
                    if (media != null) {
                        media.pause();
                        Play.setImageResource(R.drawable.ic_play);
                    }
                } else {
                    if (media != null) {
                        playSong();
                        Play.setImageResource(R.drawable.ic_pause);
                    }
                }
            }
        });
        return view;
    }

    public void  playSong() {
        //media.reset();
        if(media.getDuration() == media.getCurrentPosition() || media.getCurrentPosition() == 0){
            media.reset();
            try {
                media.setDataSource(filename);
                media.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        media.start();

        Play.setImageResource(R.drawable.ic_pause);

        progressBar.setProgress(0);
        progressBar.setMax(100);

        updateProgressBar();
    }


    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = media.getDuration();
            long currentDuration = media.getCurrentPosition();

            songTotalTime.setText(""+utils.milliSecondsToTimer(totalDuration));

            songCurrentTime.setText(""+utils.milliSecondsToTimer(currentDuration));

            int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
            progressBar.setProgress(progress);


            mHandler.postDelayed(this, 100);
        }
    };


    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = media.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        media.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        if(media != null) {
            if (media.isPlaying()) {
                media.stop();
            }
            media.release();
            media = null;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        media.reset();
        Play.setImageResource(R.drawable.ic_play);
    }

    public void permission(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }
    }
}
