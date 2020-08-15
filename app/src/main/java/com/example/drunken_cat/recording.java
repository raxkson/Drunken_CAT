package com.example.drunken_cat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

public class recording extends AppCompatActivity {
    MediaRecorder record;
    String filename="";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        File file = new File(getApplicationContext().getFilesDir(),"record.mp4");
        filename = file.getAbsolutePath();
        permission();

        Log.d("recording","Save file : " + filename);

        findViewById(R.id.record_start).setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                startRecord();
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
}