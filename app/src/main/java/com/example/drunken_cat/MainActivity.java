package com.example.drunken_cat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.LocationManager;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {




    private BackButtonClick BackButtonClick;



    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;

    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS};

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private MapActivity fragmentMap = new MapActivity();
    private AddFriendActivity fragmentFriend = new AddFriendActivity();
    private ProxyDriverActivity fragmentDriver = new ProxyDriverActivity();
    private RecordingActivity fragmentRecord = new RecordingActivity();
    public boolean recordSwitch = false;
    Bundle bundle = new Bundle();
    @CallSuper
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);


        BackButtonClick = new BackButtonClick(this);

        recordSwitch = fragmentRecord.getDefaults("recordSwitch", this);
        bundle.putBoolean("recordSwitch", recordSwitch);
        fragmentMap.setArguments(bundle);

        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        }else {
            checkRunTimePermission();
        }
        final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.main_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());

        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragmentMap).commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed(){
        BackButtonClick.onBackPressed();
    }

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            recordSwitch = fragmentRecord.getDefaults("recordSwitch", getApplicationContext());
            bundle.putBoolean("recordSwitch", recordSwitch);
            fragmentMap.setArguments(bundle);

            switch(menuItem.getItemId())
            {
                case R.id.bottom_nav_1:
                    transaction.replace(R.id.frameLayout, fragmentMap).commitAllowingStateLoss();
                    break;
                case R.id.bottom_nav_2:
                    transaction.remove(fragmentFriend).commitNow();
                    transaction.remove(fragmentRecord).commitNow();
                    transaction.remove(fragmentDriver).commitNow();
                    transaction.add(R.id.frameLayout, fragmentFriend, "AddFriend").commitNow();
                    break;
                case R.id.bottom_nav_3:
                    transaction.remove(fragmentDriver).commitNow();
                    transaction.remove(fragmentFriend).commitNow();
                    transaction.remove(fragmentRecord).commitNow();
                    transaction.add(R.id.frameLayout, fragmentDriver, "ProxyDriver").commitNow();
                    break;
                case R.id.bottom_nav_4:
                    transaction.remove(fragmentRecord).commitNow();
                    transaction.remove(fragmentFriend).commitNow();
                    transaction.remove(fragmentDriver).commitNow();
                    transaction.add(R.id.frameLayout, fragmentRecord, "Recording").commitNow();
                    break;
                default:
                    break;
            }
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {



            boolean check_result = true;

            for (final int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if ( !check_result ) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                    Toast.makeText(this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                }else {
                    Toast.makeText(this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    void checkRunTimePermission(){

        final int hasFineSmsPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if ((hasFineLocationPermission | hasFineSmsPermission) != PackageManager.PERMISSION_GRANTED ) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                Toast.makeText(this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                Toast.makeText(this,"메시지 권한이 필요합니다.",Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
        }
    }




    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:

                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }
                break;
            default:
                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }




}