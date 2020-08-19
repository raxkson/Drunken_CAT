
package com.example.drunken_cat;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.MediaController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import 	androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class recordingActivity extends Fragment {

    MediaRecorder record;
    public String filename="";
    MediaPlayer player;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_recording, container, false);

        File file = new File(getActivity().getFilesDir(),"record.mp4");
        filename = file.getAbsolutePath();
        permission();
        Log.d("recording","Save file : " + filename);



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
        // play
        view.findViewById(R.id.record_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    player = new MediaPlayer();
                    VideoView videoView = (VideoView) getActivity().findViewById(R.id.record_view);

                    System.out.println("[*] "+filename);
                    player.setDataSource(filename);

                    videoView.setVideoPath(filename);

                    MediaController mediaController = new MediaController(getActivity());
                    mediaController.setAnchorView(videoView);
                    mediaController.setPadding(0, 0, 0, 80); //상위 레이어의 바닥에서 얼마 만큼? 패딩을 줌
                    videoView.setMediaController(mediaController);

                    videoView.start();
                    //     player.prepare();
                    //    player.start();

                    Toast.makeText( getActivity(), "재생중...", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    public void permission(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }
    }

}