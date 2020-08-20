
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
import android.widget.SeekBar;
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
    boolean isPlaying = false;
    SeekBar sb;
    class MyThread extends Thread {
        @Override
        public void run() { // 쓰레드가 시작되면 콜백되는 메서드
            // 씨크바 막대기 조금씩 움직이기 (노래 끝날 때까지 반복)
            while(isPlaying) {
                sb.setProgress(player.getCurrentPosition());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_recording, container, false);

        File file = new File(getActivity().getFilesDir(),"record.mp4");
        filename = file.getAbsolutePath();
        permission();
        Log.d("recording","Save file : " + filename);

        sb = view.findViewById(R.id.seekBar1);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
                isPlaying = true;
                int ttt = seekBar.getProgress(); // 사용자가 움직여놓은 위치
                player.seekTo(ttt);
                player.start();
                new MyThread().start();
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
                isPlaying = false;
                player.pause();
            }
            public void onProgressChanged(SeekBar seekBar,int progress,boolean fromUser) {
                if (seekBar.getMax()==progress) {

                    view.findViewById(R.id.record_start).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.record_stop).setVisibility(View.INVISIBLE);
                    isPlaying = false;
                    player.stop();
                }
            }
        });


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
        view.findViewById(R.id.play_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //seekBar1

                    player = new MediaPlayer();
                    player.setDataSource(filename);
                    player.prepare();
                    player.start(); // 노래 재생 시작

                    int a = player.getDuration(); // 노래의 재생시간(miliSecond)
                    sb.setMax(a);// 씨크바의 최대 범위를 노래의 재생시간으로 설정
                    new MyThread().start(); // 씨크바 그려줄 쓰레드 시작
                    isPlaying = true; // 씨크바 쓰레드 반복 하도록

                    view.findViewById(R.id.play_start).setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.play_stop).setVisibility(View.VISIBLE);



                    /*player = new MediaPlayer();
                    VideoView videoView = (VideoView) getActivity().findViewById(R.id.record_view);

                    System.out.println("[*] "+filename);
                    player.setDataSource(filename);

                    videoView.setVideoPath(filename);

                    MediaController mediaController = new MediaController(getActivity());
                    mediaController.setAnchorView(videoView);
                    mediaController.setPadding(0, 0, 0, 80); //상위 레이어의 바닥에서 얼마 만큼? 패딩을 줌
                    videoView.setMediaController(mediaController);

                    videoView.start();*/
                    //     player.prepare();
                    //    player.start();

                    Toast.makeText( getActivity(), "재생중...", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        view.findViewById(R.id.play_stop).setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                isPlaying = false; // 쓰레드 종료

                player.stop(); // 멈춤
                player.release(); // 자원 해제

                view.findViewById(R.id.play_start).setVisibility(View.VISIBLE);
                view.findViewById(R.id.play_stop).setVisibility(View.INVISIBLE);
                sb.setProgress(0); // 씨크바 초기화
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