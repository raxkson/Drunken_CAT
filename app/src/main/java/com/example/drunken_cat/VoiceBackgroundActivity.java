package com.example.drunken_cat;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

public class VoiceBackgroundActivity extends Service {

    MediaRecorder record;
    String filename="";
    MediaPlayer player;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        if (record != null) {
            record.stop();
            record.release();
            record = null;
        }
        File file = new File(getApplicationContext().getFilesDir(),"record.mp4");
        filename = file.getAbsolutePath();

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
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (record != null) {
            record.stop();
            record.release();
            record = null;
            Toast.makeText(this, "Voice Recording Stop...", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onUnbind(Intent intent){
        return super.onUnbind(intent);
    }

}
