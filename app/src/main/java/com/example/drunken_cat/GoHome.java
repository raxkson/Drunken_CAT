package com.example.drunken_cat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//SMS 내용 어떻게 할건지
//Thread랑 Handler 안 쓰고 위치정보 획득 가능한지 확인
//위치정보랑 시간정보 같이 저장
//도착하면 종료

public class GoHome extends AppCompatActivity {
    int value = 0;
    TextView text;
    TextView tv_loc;
    Button btn_loc;
    Thread thread;
    boolean isThread = false;
    double cur_longitude = 1000;
    double cur_latitude = 1000;
    double dst_longitude = 37.1;
    double dst_latitude = 50.3;
    double bef_distance;
    double cur_distance;

    private final int ACCESS_FINE_LOCATION = 1001, SEND_SMS = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int permissionCheckLoc = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheckSms = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);

        if (permissionCheckLoc!= PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this,"권한 승인이 필요합니다",Toast.LENGTH_LONG).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this,"위치 권한이 필요합니다.",Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        ACCESS_FINE_LOCATION);
                Toast.makeText(this,"위치 권한이 필요합니다.",Toast.LENGTH_LONG).show();

            }
        }

        if (permissionCheckSms!= PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this,"권한 승인이 필요합니다",Toast.LENGTH_LONG).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
                Toast.makeText(this,"메시지 권한이 필요합니다.",Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        SEND_SMS);
                Toast.makeText(this,"메시지 권한이 필요합니다.",Toast.LENGTH_LONG).show();

            }
        }


        text = (TextView)findViewById(R.id.text);
        tv_loc = (TextView)findViewById(R.id.tv_loc);
        btn_loc = (Button)findViewById(R.id.btn_loc);
        //text.setText(Double.toString(distanceInKilometerByHaversine(37.504186, 126.956874,35.129141, 129.101496)));

        //final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        btn_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isThread = true;
                thread =  new Thread(){
                    public void run(){
                        while(isThread){
                            try {
                                sleep(10000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            handler.sendEmptyMessage(0);
                        }


                    }
                };
                thread.start();
            }
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            long now =  System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String formatDate = sdfNow.format(date);
            try{
                List<String> list = lm.getAllProviders();
                for(int i = 0; i < list.size(); i++){
                    Toast.makeText(getApplicationContext(),list.get(i)+":"+lm.isProviderEnabled(list.get(i)), Toast.LENGTH_SHORT).show();
                }
                Location location;
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        0,
                        0,
                        mLocationListener);
                location = lm.getLastKnownLocation(lm.GPS_PROVIDER);
                cur_longitude = location.getLongitude();
                cur_latitude = location.getLatitude();
                /*lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        0,
                        0,
                        mLocationListener);
                */
                Toast.makeText(getApplicationContext(), Double.toString(cur_latitude)+", "+Double.toString(cur_longitude), Toast.LENGTH_SHORT).show();
                cur_distance = distanceInKilometerByHaversine(dst_latitude, dst_longitude, cur_latitude, cur_longitude);
                if(cur_distance < 1){//목적지 도착
                    Toast.makeText(getApplicationContext(), "목적지 도착", Toast.LENGTH_SHORT).show();
                    isThread = false;
                }
                if(cur_distance > bef_distance + 1){//SOS
                    Toast.makeText(getApplicationContext(), "경로이탈", Toast.LENGTH_SHORT).show();
                    /*try{
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage("010", null, "테스트", null, null);
                        Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                    } catch(Exception e){
                        Toast.makeText(getApplicationContext(), "SMS failed", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    */
                }
                bef_distance = cur_distance;
                lm.removeUpdates(mLocationListener);
                //Toast.makeText(getApplicationContext(), Double.toString(cur_latitude)+", "+Double.toString(cur_longitude), Toast.LENGTH_SHORT).show();

                //tv_loc.setText("Double.toString(test)");
            }catch(SecurityException ex){
                //tv_loc.setText(Double.toString(test));
            }
            //tv_loc.setText(Double.toString(test));
            //Toast.makeText(getApplicationContext(), formatDate, Toast.LENGTH_SHORT).show();
        }
    };

    private final LocationListener mLocationListener =  new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            //Toast.makeText(getApplicationContext(), Double.toString(cur_latitude)+", "+Double.toString(cur_longitude), Toast.LENGTH_SHORT).show();
            //double[] long_lati = {location.getLatitude(), location.getLongitude()};
            cur_longitude = location.getLongitude();//height
            cur_latitude = location.getLatitude();//width

            double altitude = location.getAltitude();
            float accuracy = location.getAccuracy();
            String provider = location.getProvider();


        }
    };

    public static double distanceInKilometerByHaversine(double x1, double y1, double x2, double y2) {
        double distance;
        double radius = 6371;
        double toRadian = Math.PI / 180;

        double deltaLatitude = Math.abs(x1 - x2) * toRadian;
        double deltaLongitude = Math.abs(y1 - y2) * toRadian;

        double sinDeltaLat = Math.sin(deltaLatitude / 2);
        double sinDeltaLng = Math.sin(deltaLongitude / 2);
        double squareRoot = Math.sqrt(
                sinDeltaLat * sinDeltaLat +
                        Math.cos(x1 * toRadian) * Math.cos(x2 * toRadian) * sinDeltaLng * sinDeltaLng);

        distance = 2 * radius * Math.asin(squareRoot);

        return distance;
    }





}