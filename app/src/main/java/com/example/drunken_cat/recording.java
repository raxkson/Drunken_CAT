package com.example.drunken_cat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.MediaController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

public class recording extends AppCompatActivity {
    MediaRecorder record;
    String filename="";
    MediaPlayer player;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        File file = new File(getApplicationContext().getFilesDir(),"record.mp4");
        filename = file.getAbsolutePath();
        permission();

        Log.d("recording","Save file : " + filename);

        findViewById(R.id.record_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAudio();
            }
        });

        findViewById(R.id.record_start).setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                startRecord();
            }
        });
        findViewById(R.id.record_stop).setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                stopRecord();
            }
        });
    }

    public void permission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1);
        }
    }

    private void startRecord(){
        record = new MediaRecorder();

        record.setAudioSource(MediaRecorder.AudioSource.MIC);
        record.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        record.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        record.setOutputFile(filename);

        try{
            record.prepare();
            record.start();
            Toast.makeText(this, "Voice Recording Start...", Toast.LENGTH_SHORT).show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    private void stopRecord() {
        if (record != null) {
            record.stop();
            record.release();
            record = null;
            Toast.makeText(this, "Voice Recording Stop...", Toast.LENGTH_SHORT).show();
        }
    }
    private void playAudio() {
        try {
            player = new MediaPlayer();
            System.out.println("[*] "+filename);
            player.setDataSource(filename);
            final VideoView videoView = (VideoView) findViewById(R.id.record_view);

            videoView.setVideoPath(filename);

            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            mediaController.setPadding(0, 0, 0, 80); //상위 레이어의 바닥에서 얼마 만큼? 패딩을 줌
            videoView.setMediaController(mediaController);

            videoView.start();
       //     player.prepare();
        //    player.start();

            Toast.makeText(this, "재생중...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void closePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }



}