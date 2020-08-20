package com.example.drunken_cat;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.SeekBar;

import java.io.File;
import java.io.IOException;

import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;

public class RecordingActivity extends Fragment
        implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    private ImageButton Play;
    private SeekBar progressBar;
    private TextView songCurrentTime;
    private TextView songTotalTime;
    private MediaPlayer media;
    private Handler mHandler = new Handler();
    private Utilities utils;
    private View view;
    public String filename="";
    public SharedPreferences appData;


    public static void setDefaults(String key, Boolean value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.activity_recording, container, false);
        permission();
        media = MediaPlayer.create(getActivity(), Uri.parse("uriString"));

        File file = new File(getActivity().getFilesDir(),"record.mp4");
        filename = file.getAbsolutePath();

        Play = (ImageButton) view.findViewById(R.id.play);
        progressBar = (SeekBar) view.findViewById(R.id.seekBar3);
        songCurrentTime = (TextView) view.findViewById(R.id.songCurrentTime);
        songTotalTime = (TextView) view.findViewById(R.id.songTotalTime);

        media = new MediaPlayer();
        utils = new Utilities();

        progressBar.setOnSeekBarChangeListener(this);
        media.setOnCompletionListener(this);

        if(!getDefaults("recordSwitch",getContext()))
            setDefaults("recordSwitch", false, getContext());



        LabeledSwitch labeledSwitch = view.findViewById(R.id.recordOn);
        labeledSwitch.setOn(getDefaults("recordSwitch", getContext()));
        labeledSwitch.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                setDefaults("recordSwitch", isOn, getContext());
            }
        });


        Play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (media != null) {
                    if (media.isPlaying()) {
                            media.pause();
                            Play.setImageResource(R.drawable.ic_play);
                    } else {
                            playSong();
                            Play.setImageResource(R.drawable.ic_pause);
                    }
                }
            }
        });
        return view;
    }

    public void  playSong() {

        if(media.getCurrentPosition() == 0){
            media.reset();
            try {
                media.setDataSource(filename);
                media.prepare();
            } catch (IOException e) {
                System.out.println("Error");
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
        @Override
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

        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = media.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);


        media.seekTo(currentPosition);


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
