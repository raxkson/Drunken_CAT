package com.example.drunken_cat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.example.drunken_cat.adapter.LocationAdapter;
import com.example.drunken_cat.api.ApiClient;
import com.example.drunken_cat.api.ApiInterface;
import com.example.drunken_cat.model.category_search.CategoryResult;
import com.example.drunken_cat.model.category_search.Document;
import com.example.drunken_cat.utils.BusProvider;
import com.example.drunken_cat.utils.IntentKey;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.snatik.storage.EncryptConfiguration;
import com.snatik.storage.Storage;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.view.inputmethod.InputMethodManager;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class MapActivity extends Fragment implements MapView.MapViewEventListener, MapView.POIItemEventListener, MapView.OpenAPIKeyAuthenticationResultListener, View.OnClickListener, MapView.CurrentLocationEventListener {
    final static String TAG = "MapTAG";


    public static final String NOTIFICATION_CHANNEL_ID = "11111";
    private int count;


    public String locationString = "";
    String IVX = "abcdefghijklmnop";
    String SECRET_KEY;
    byte[] SALT = "0000111100001111".getBytes();
    EncryptConfiguration configuration;
    MapView mMapView;
    ViewGroup mMapViewContainer;
    EditText mSearchEdit;
    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2, fab3, searchDetailFab, stopTrackingFab, goHome, stopGoHomeFab, overlayFab, eraseMarker;
    RelativeLayout mLoaderLayout;
    RecyclerView recyclerView;

    MapPoint currentMapPoint;
    private double mCurrentLng;
    private double mCurrentLat;
    private double mSearchLng = -1;
    private double mSearchLat = -1;
    private String mSearchName;
    boolean isTrackingMode;
    Bus bus = BusProvider.getInstance();

    ArrayList<Document> bigMartList = new ArrayList<>();
    ArrayList<Document> gs24List = new ArrayList<>();
    ArrayList<Document> schoolList = new ArrayList<>();
    ArrayList<Document> academyList = new ArrayList<>();
    ArrayList<Document> subwayList = new ArrayList<>();
    ArrayList<Document> bankList = new ArrayList<>();
    ArrayList<Document> hospitalList = new ArrayList<>();
    ArrayList<Document> pharmacyList = new ArrayList<>();
    ArrayList<Document> cafeList = new ArrayList<>();

    ArrayList<Document> documentArrayList = new ArrayList<>();

    MapPOIItem searchMarker = new MapPOIItem();

    private InputMethodManager imm;
    private View view;


    private Handler mmHandler = new Handler();
    double bef_longitude = 2000;
    double bef_latitude = 2000;
    double cur_longitude = 1000;
    double cur_latitude = 1000;
    double dst_longitude = 126.644383;
    double dst_latitude = 37.386208;
    double bef_distance = 100000000;
    double cur_distance;
    double tmp_distance;
    boolean record_switch = false;

    Storage storage;
    String path;
    String Filepath;
    boolean fileExists = false;



    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SECRET_KEY = android.provider.Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        configuration = new EncryptConfiguration.Builder()
                .setEncryptContent(IVX, SECRET_KEY, SALT)
                .build();
        view = inflater.inflate(R.layout.activity_map, container, false);
        if(this.getArguments() != null){
            record_switch = this.getArguments().getBoolean("recordSwitch");
        }

        bus.register(this);
        mSearchEdit = view.findViewById(R.id.map_et_search);
        fab_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);
        fab = view.findViewById(R.id.fab);
        fab1 = view.findViewById(R.id.fab1);
        fab2 = view.findViewById(R.id.fab2);
        fab3 = view.findViewById(R.id.fab3);
        goHome = view.findViewById(R.id.gohome);
        overlayFab = view.findViewById(R.id.overlay);

        searchDetailFab = view.findViewById(R.id.fab_detail);
        stopTrackingFab = view.findViewById(R.id.fab_stop_tracking);
        stopGoHomeFab = view.findViewById(R.id.fab_stop_goHome);
        eraseMarker = view.findViewById(R.id.fab_erase_marker);
        mLoaderLayout = view.findViewById(R.id.loaderLayout);
        mMapView = new MapView(getActivity());
        mMapViewContainer = view.findViewById(R.id.map_mv_mapcontainer);
        mMapViewContainer.addView(mMapView);
        recyclerView = view.findViewById(R.id.map_recyclerview);
        initView();
        return view;
    }


    private void initView() {

        LocationAdapter locationAdapter = new LocationAdapter(documentArrayList, mSearchEdit, recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(locationAdapter);


        mMapView.setMapViewEventListener(this);
        mMapView.setPOIItemEventListener(this);
        mMapView.setOpenAPIKeyAuthenticationResultListener(this);


        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);
        goHome.setOnClickListener(this);
        overlayFab.setOnClickListener(this);
        searchDetailFab.setOnClickListener(this);
        stopTrackingFab.setOnClickListener(this);
        stopGoHomeFab.setOnClickListener(this);
        eraseMarker.setOnClickListener(this);


        mMapView.setCurrentLocationEventListener(this);

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

        mLoaderLayout.setVisibility(View.VISIBLE);


        mSearchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() >= 1) {


                    documentArrayList.clear();
                    locationAdapter.clear();
                    locationAdapter.notifyDataSetChanged();
                    ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                    Call<CategoryResult> call = apiInterface.getSearchLocation(getString(R.string.restapi_key), charSequence.toString(), 15);
                    call.enqueue(new Callback<CategoryResult>() {
                        @Override
                        public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                for (Document document : response.body().getDocuments()) {
                                    locationAdapter.addItem(document);
                                }
                                locationAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<CategoryResult> call, @NotNull Throwable t) {

                        }
                    });


                } else {
                    if (charSequence.length() <= 0) {
                        recyclerView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mSearchEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                } else {
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });
        mSearchEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FancyToast.makeText(getActivity(), "검색리스트에서 장소를 선택해주세요", FancyToast.LENGTH_SHORT, FancyToast.INFO, true).show();
            }
        });
    }

    public void updateLocation() {
        mmHandler.postDelayed(updateLocationtask, 30000);
    }


    @Override
    public void onClick(View v) {

        if(this.getArguments() != null){
            record_switch = this.getArguments().getBoolean("recordSwitch");
        }
        int id = v.getId();
        switch (id) {
            case R.id.fab:
                FancyToast.makeText(getActivity(), "1번 버튼: 검색좌표 기준으로 1km 검색" +
                        "\n2번 버튼: 현재위치 기준으로 주변환경 검색" +
                        "\n3번 버튼: 현재위치 추적 및 업데이트", FancyToast.LENGTH_SHORT, FancyToast.INFO, true).show();
                anim();
                break;
            case R.id.fab1:
                FancyToast.makeText(getActivity(), "현재위치 추적 시작", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
                searchDetailFab.setVisibility(View.GONE);
                mLoaderLayout.setVisibility(View.VISIBLE);
                isTrackingMode = true;
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
                anim();
                stopTrackingFab.setVisibility(View.VISIBLE);
                mLoaderLayout.setVisibility(View.GONE);
                break;
            case R.id.fab2:
                isTrackingMode = false;
                FancyToast.makeText(getActivity(), "현재위치기준 1km 검색 시작", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
                stopTrackingFab.setVisibility(View.GONE);
                eraseMarker.setVisibility(View.VISIBLE);
                mLoaderLayout.setVisibility(View.VISIBLE);
                anim();

                mMapView.removeAllPOIItems();
                mMapView.removeAllCircles();
                requestSearchLocal(mCurrentLng, mCurrentLat);
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
                break;
            case R.id.fab3:
                isTrackingMode = false;
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                mLoaderLayout.setVisibility(View.VISIBLE);
                anim();
                if (mSearchLat != -1 && mSearchLng != -1) {
                    mMapView.removeAllPOIItems();
                    mMapView.removeAllCircles();
                    mMapView.addPOIItem(searchMarker);
                    requestSearchLocal(mSearchLng, mSearchLat);
                    eraseMarker.setVisibility(View.VISIBLE);
                } else {
                    FancyToast.makeText(getActivity(), "검색 먼저 해주세요", FancyToast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                }
                mLoaderLayout.setVisibility(View.GONE);
                break;
            case R.id.gohome:
                if(record_switch)
                    getActivity().startService(new Intent(getActivity(), VoiceBackgroundActivity.class));
                isTrackingMode = true;
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
                storage = new Storage(getContext());
                storage.setEncryptConfiguration(configuration);
                path = storage.getInternalFilesDirectory();
                Filepath = path + "Destination.txt";
                boolean fileExists = storage.isFileExist(Filepath);
                if (fileExists) {
                    String[] arr = storage.readTextFile(Filepath).split(" ");
                    dst_latitude = Double.parseDouble(arr[0]);
                    dst_longitude = Double.parseDouble(arr[1]);

                    searchDetailFab.setVisibility(View.GONE);
                    mLoaderLayout.setVisibility(View.VISIBLE);
                    FancyToast.makeText(getActivity(), arr[2] + "로 안전 귀가 서비스가 정상 작동합니다.", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
                    anim();
                    stopGoHomeFab.setVisibility(View.VISIBLE);
                    mLoaderLayout.setVisibility(View.GONE);

                    updateLocation();
                } else {
                    FancyToast.makeText(getActivity(), "목적지를 등록해주세요", FancyToast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                }
                break;
            case R.id.overlay:
                mLoaderLayout.setVisibility(View.VISIBLE);
                eraseMarker.setVisibility(View.VISIBLE);
                anim();
                mLoaderLayout.setVisibility(View.GONE);

                storage = new Storage(getContext());
                path = storage.getInternalFilesDirectory();
                Filepath = path + "Location.txt";
                fileExists = storage.isFileExist(Filepath);
                String content;
                if(fileExists) {
                    content = storage.readTextFile(Filepath);
                    System.out.println(content);
                    String[] text = content.split("\n");
                    MapPolyline polyline = new MapPolyline();
                    for(int i = 0; i < text.length; i++){
                        String[] txt = text[i].split("★");
                        double latitude = Double.parseDouble(txt[0]);
                        double longitude = Double.parseDouble(txt[1]);

                        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);
                        MapPOIItem marker = new MapPOIItem();
                        marker.setItemName(txt[2]);
                        marker.setTag(0);
                        marker.setMapPoint(mapPoint);
                        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
                        mMapView.addPOIItem(marker);
                        polyline.setTag(1000);
                        polyline.setLineColor(Color.argb(128, 255, 51, 0));

                        polyline.addPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));


                    }
                    mMapView.addPolyline(polyline);


                    MapPointBounds mapPointBounds = new MapPointBounds(polyline.getMapPoints());
                    int padding = 100;
                    mMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding));
                }

                break;
            case R.id.fab_detail:
                FancyToast.makeText(getActivity(), "검색결과 상세보기", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
                Intent detailIntent = new Intent(getActivity(), MapSearchDetailActivity.class);
                detailIntent.putParcelableArrayListExtra(IntentKey.CATEGOTY_SEARCH_MODEL_EXTRA1, bigMartList);
                detailIntent.putParcelableArrayListExtra(IntentKey.CATEGOTY_SEARCH_MODEL_EXTRA2, gs24List);
                detailIntent.putParcelableArrayListExtra(IntentKey.CATEGOTY_SEARCH_MODEL_EXTRA3, schoolList);
                detailIntent.putParcelableArrayListExtra(IntentKey.CATEGOTY_SEARCH_MODEL_EXTRA4, academyList);
                detailIntent.putParcelableArrayListExtra(IntentKey.CATEGOTY_SEARCH_MODEL_EXTRA5, subwayList);
                detailIntent.putParcelableArrayListExtra(IntentKey.CATEGOTY_SEARCH_MODEL_EXTRA6, bankList);
                detailIntent.putParcelableArrayListExtra(IntentKey.CATEGOTY_SEARCH_MODEL_EXTRA7, hospitalList);
                detailIntent.putParcelableArrayListExtra(IntentKey.CATEGOTY_SEARCH_MODEL_EXTRA8, pharmacyList);
                detailIntent.putParcelableArrayListExtra(IntentKey.CATEGOTY_SEARCH_MODEL_EXTRA9, cafeList);

                startActivity(detailIntent);
                Log.d(TAG, "fab_detail");
                break;
            case R.id.fab_stop_tracking:
                isTrackingMode = false;
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                stopTrackingFab.setVisibility(View.GONE);
                FancyToast.makeText(getActivity(), "현재위치 추적 종료", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
                break;
            case R.id.fab_stop_goHome:

                storage = new Storage(getContext());
                path = storage.getInternalFilesDirectory();
                Filepath = path + "Location.txt";
                fileExists = storage.isFileExist(Filepath);
                if(fileExists)
                    storage.deleteFile(Filepath);
                if(!locationString.equals(""))
                    storage.createFile(Filepath, locationString);
                if(record_switch)
                    getActivity().stopService(new Intent(getActivity(), VoiceBackgroundActivity.class));
                mmHandler.removeCallbacksAndMessages(null);
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                stopGoHomeFab.setVisibility(View.GONE);
                FancyToast.makeText(getActivity(), "귀가 서비스 종료", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
                break;
            case R.id.fab_erase_marker:
                mMapView.removeAllPolylines();
                mMapView.removeAllPOIItems();
                mMapView.removeAllCircles();
                eraseMarker.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }


    private final Runnable updateLocationtask = new Runnable() {
        @Override
        public void run() {

            storage = new Storage(getContext());
            storage.setEncryptConfiguration(configuration);
            path = storage.getInternalFilesDirectory();
            Filepath = path + "Location.txt";
            fileExists = false;
            boolean GPSEnabled = false;
            boolean NetworkEnabled = false;
            boolean PassiveEnabled = false;
            final LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (lm == null) {
                System.out.println("nullcheck");
            } else {
                GPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                NetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                PassiveEnabled = lm.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
            }
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String formatDate = sdfNow.format(date);


            Location location = null;

            if (GPSEnabled) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        0,
                        0,
                        mLocationListener);
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    cur_longitude = location.getLongitude();
                    cur_latitude = location.getLatitude();
                    Toast.makeText(getContext(), "GPS", Toast.LENGTH_SHORT).show();
                }
            }

            if (NetworkEnabled) {
                if (location == null) {
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            0,
                            0,
                            mLocationListener);
                    location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        cur_longitude = location.getLongitude();
                        cur_latitude = location.getLatitude();
                        Toast.makeText(getContext(), "NET", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            if (PassiveEnabled) {
                if (location == null) {
                    lm.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,
                            0,
                            0,
                            mLocationListener);
                    location = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    if (location != null) {
                        cur_longitude = location.getLongitude();
                        cur_latitude = location.getLatitude();
                        Toast.makeText(getContext(), "PASSIVE", Toast.LENGTH_SHORT).show();
                    }
                }
            }


            locationString += cur_latitude + "★" + cur_longitude + "★" + formatDate + "\n";
            System.out.println(locationString);
            cur_distance = distanceInKilometerByHaversine(dst_latitude, dst_longitude, cur_latitude, cur_longitude);
            Toast.makeText(getContext(), Double.toString(cur_distance), Toast.LENGTH_SHORT).show();

            if (cur_distance < 0.5) {
                NotificationSomethings("목적지에 도착하였습니다");
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                stopGoHomeFab.setVisibility(View.GONE);

                storage = new Storage(getContext());
                path = storage.getInternalFilesDirectory();
                Filepath = path + "Location.txt";
                fileExists = storage.isFileExist(Filepath);
                if (fileExists)
                    storage.deleteFile(Filepath);
                if (!locationString.equals(""))
                    storage.createFile(Filepath, locationString);

                if (record_switch)
                    getActivity().stopService(new Intent(getActivity(), VoiceBackgroundActivity.class));
                lm.removeUpdates(mLocationListener);
            }

            if (cur_distance > bef_distance + 0.5) {
                NotificationSomethings("경로를 이탈하여 SMS를 전송합니다");


                Filepath = path + "Friend.txt";
                fileExists = storage.isFileExist(Filepath);
                if (fileExists) {
                    String content = storage.readTextFile(Filepath);

                    int length = 0;
                    String[] text = content.split("☎");
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        length = text.length;
                        for (int i = 1; i < length; i += 2) {
                            smsManager.sendTextMessage(text[i], null, "지인이 경로를 이탈했습니다!", null, null);
                            Toast.makeText(getContext(), text[i], Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(getContext(), Integer.toString(length / 2) + "명의 지인에게 SMS를 전송하였습니다", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), Integer.toString(length / 2) + "명의 지인에게 SMS를 전송하였습니다", Toast.LENGTH_SHORT).show();
                        System.out.println("Error");
                    }
                } else {
                    FancyToast.makeText(getContext(), "지인 등록이 되어있지 않습니다. 지인을 등록해주세요", FancyToast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                }

            }

            tmp_distance = distanceInKilometerByHaversine(bef_latitude, bef_longitude, cur_latitude, cur_longitude);
            if (tmp_distance < 0.01 && bef_latitude != 2000 && bef_longitude != 2000) {
                NotificationSomethings("움직임이 감지되지 않아 SMS를 전송합니다");


                Filepath = path + "Friend.txt";


                fileExists = storage.isFileExist(Filepath);
                if (fileExists) {
                    String content = storage.readTextFile(Filepath);

                    int length = 0;
                    String[] text = content.split("☎");
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        length = text.length;
                        for (int i = 1; i < length; i += 2) {
                            smsManager.sendTextMessage(text[i], null, "지인의 움직임이 감지되지 않습니다", null, null);

                        }
                        Toast.makeText(getContext(), Integer.toString(length / 2) + "명의 지인에게 SMS를 전송하였습니다", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), Integer.toString(length / 2) + "명의 지인에게 SMS를 전송하였습니다", Toast.LENGTH_SHORT).show();
                        System.out.println("Error");
                    }
                } else {
                    FancyToast.makeText(getContext(), "지인 등록이 되어있지 않습니다. 지인을 등록해주세요", FancyToast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                }

            }
            bef_distance = cur_distance;
            bef_latitude = cur_latitude;
            bef_longitude = cur_longitude;
            mmHandler.postDelayed(this, 30000);
        }

    };

    private final LocationListener mLocationListener =  new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            cur_longitude = location.getLongitude();
            cur_latitude = location.getLatitude();

        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

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

    private void requestSearchLocal(double x, double y) {
        bigMartList.clear();
        gs24List.clear();
        schoolList.clear();
        academyList.clear();
        subwayList.clear();
        bankList.clear();
        hospitalList.clear();
        pharmacyList.clear();
        cafeList.clear();
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<CategoryResult> call = apiInterface.getSearchCategory(getString(R.string.restapi_key), "MT1", x + "", y + "", 1000);
        call.enqueue(new Callback<CategoryResult>() {
            @Override
            public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getDocuments() != null) {
                        Log.d(TAG, "bigMartList Success");
                        bigMartList.addAll(response.body().getDocuments());
                    }
                    call = apiInterface.getSearchCategory(getString(R.string.restapi_key), "CS2", x + "", y + "", 1000);
                    call.enqueue(new Callback<CategoryResult>() {
                        @Override
                        public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                Log.d(TAG, "gs24List Success");
                                gs24List.addAll(response.body().getDocuments());
                                call = apiInterface.getSearchCategory(getString(R.string.restapi_key), "SC4", x + "", y + "", 1000);
                                call.enqueue(new Callback<CategoryResult>() {
                                    @Override
                                    public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                                        if (response.isSuccessful()) {
                                            assert response.body() != null;
                                            Log.d(TAG, "schoolList Success");
                                            schoolList.addAll(response.body().getDocuments());
                                            call = apiInterface.getSearchCategory(getString(R.string.restapi_key), "AC5", x + "", y + "", 1000);
                                            call.enqueue(new Callback<CategoryResult>() {
                                                @Override
                                                public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                                                    if (response.isSuccessful()) {
                                                        assert response.body() != null;
                                                        Log.d(TAG, "academyList Success");
                                                        academyList.addAll(response.body().getDocuments());
                                                        call = apiInterface.getSearchCategory(getString(R.string.restapi_key), "SW8", x + "", y + "", 1000);
                                                        call.enqueue(new Callback<CategoryResult>() {
                                                            @Override
                                                            public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                                                                if (response.isSuccessful()) {
                                                                    assert response.body() != null;
                                                                    Log.d(TAG, "subwayList Success");
                                                                    subwayList.addAll(response.body().getDocuments());
                                                                    call = apiInterface.getSearchCategory(getString(R.string.restapi_key), "BK9", x + "", y + "", 1000);
                                                                    call.enqueue(new Callback<CategoryResult>() {
                                                                        @Override
                                                                        public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                                                                            if (response.isSuccessful()) {
                                                                                assert response.body() != null;
                                                                                Log.d(TAG, "bankList Success");
                                                                                bankList.addAll(response.body().getDocuments());
                                                                                call = apiInterface.getSearchCategory(getString(R.string.restapi_key), "HP8", x + "", y + "", 1000);
                                                                                call.enqueue(new Callback<CategoryResult>() {
                                                                                    @Override
                                                                                    public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                                                                                        if (response.isSuccessful()) {
                                                                                            assert response.body() != null;
                                                                                            Log.d(TAG, "hospitalList Success");
                                                                                            hospitalList.addAll(response.body().getDocuments());
                                                                                            call = apiInterface.getSearchCategory(getString(R.string.restapi_key), "PM9", x + "", y + "", 1000);
                                                                                            call.enqueue(new Callback<CategoryResult>() {
                                                                                                @Override
                                                                                                public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                                                                                                    if (response.isSuccessful()) {
                                                                                                        assert response.body() != null;
                                                                                                        Log.d(TAG, "pharmacyList Success");
                                                                                                        pharmacyList.addAll(response.body().getDocuments());
                                                                                                        call = apiInterface.getSearchCategory(getString(R.string.restapi_key), "CE7", x + "", y + "", 1000);
                                                                                                        call.enqueue(new Callback<CategoryResult>() {
                                                                                                            @Override
                                                                                                            public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                                                                                                                if (response.isSuccessful()) {
                                                                                                                    assert response.body() != null;
                                                                                                                    Log.d(TAG, "cafeList Success");
                                                                                                                    cafeList.addAll(response.body().getDocuments());

                                                                                                                    MapCircle circle1 = new MapCircle(
                                                                                                                            MapPoint.mapPointWithGeoCoord(y, x),
                                                                                                                            1000,
                                                                                                                            Color.argb(128, 255, 0, 0),
                                                                                                                            Color.argb(128, 0, 255, 0)
                                                                                                                    );
                                                                                                                    circle1.setTag(5678);
                                                                                                                    mMapView.addCircle(circle1);
                                                                                                                    Log.d("SIZE1", bigMartList.size() + "");
                                                                                                                    Log.d("SIZE2", gs24List.size() + "");
                                                                                                                    Log.d("SIZE3", schoolList.size() + "");
                                                                                                                    Log.d("SIZE4", academyList.size() + "");
                                                                                                                    Log.d("SIZE5", subwayList.size() + "");
                                                                                                                    Log.d("SIZE6", bankList.size() + "");

                                                                                                                    int tagNum = 10;
                                                                                                                    for (Document document : bigMartList) {
                                                                                                                        MapPOIItem marker = new MapPOIItem();
                                                                                                                        marker.setItemName(document.getPlaceName());
                                                                                                                        marker.setTag(tagNum++);
                                                                                                                        double x = Double.parseDouble(document.getY());
                                                                                                                        double y = Double.parseDouble(document.getX());

                                                                                                                        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(x, y);
                                                                                                                        marker.setMapPoint(mapPoint);
                                                                                                                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                                                                                                                        marker.setCustomImageResourceId(R.drawable.ic_big_mart_marker);
                                                                                                                        marker.setCustomImageAutoscale(false);
                                                                                                                        marker.setCustomImageAnchor(0.5f, 1.0f);
                                                                                                                        mMapView.addPOIItem(marker);
                                                                                                                    }
                                                                                                                    for (Document document : gs24List) {
                                                                                                                        MapPOIItem marker = new MapPOIItem();
                                                                                                                        marker.setItemName(document.getPlaceName());
                                                                                                                        marker.setTag(tagNum++);
                                                                                                                        double x = Double.parseDouble(document.getY());
                                                                                                                        double y = Double.parseDouble(document.getX());

                                                                                                                        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(x, y);
                                                                                                                        marker.setMapPoint(mapPoint);
                                                                                                                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                                                                                                                        marker.setCustomImageResourceId(R.drawable.ic_24_mart_marker);
                                                                                                                        marker.setCustomImageAutoscale(false);
                                                                                                                        marker.setCustomImageAnchor(0.5f, 1.0f);
                                                                                                                        mMapView.addPOIItem(marker);
                                                                                                                    }
                                                                                                                    for (Document document : schoolList) {
                                                                                                                        MapPOIItem marker = new MapPOIItem();
                                                                                                                        marker.setItemName(document.getPlaceName());
                                                                                                                        marker.setTag(tagNum++);
                                                                                                                        double x = Double.parseDouble(document.getY());
                                                                                                                        double y = Double.parseDouble(document.getX());

                                                                                                                        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(x, y);
                                                                                                                        marker.setMapPoint(mapPoint);
                                                                                                                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                                                                                                                        marker.setCustomImageResourceId(R.drawable.ic_school_marker);
                                                                                                                        marker.setCustomImageAutoscale(false);
                                                                                                                        marker.setCustomImageAnchor(0.5f, 1.0f);
                                                                                                                        mMapView.addPOIItem(marker);
                                                                                                                    }
                                                                                                                    for (Document document : academyList) {
                                                                                                                        MapPOIItem marker = new MapPOIItem();
                                                                                                                        marker.setItemName(document.getPlaceName());
                                                                                                                        marker.setTag(tagNum++);
                                                                                                                        double x = Double.parseDouble(document.getY());
                                                                                                                        double y = Double.parseDouble(document.getX());

                                                                                                                        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(x, y);
                                                                                                                        marker.setMapPoint(mapPoint);
                                                                                                                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                                                                                                                        marker.setCustomImageResourceId(R.drawable.ic_academy_marker);
                                                                                                                        marker.setCustomImageAutoscale(false);
                                                                                                                        marker.setCustomImageAnchor(0.5f, 1.0f);
                                                                                                                        mMapView.addPOIItem(marker);
                                                                                                                    }
                                                                                                                    for (Document document : subwayList) {
                                                                                                                        MapPOIItem marker = new MapPOIItem();
                                                                                                                        marker.setItemName(document.getPlaceName());
                                                                                                                        marker.setTag(tagNum++);
                                                                                                                        double x = Double.parseDouble(document.getY());
                                                                                                                        double y = Double.parseDouble(document.getX());

                                                                                                                        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(x, y);
                                                                                                                        marker.setMapPoint(mapPoint);
                                                                                                                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                                                                                                                        marker.setCustomImageResourceId(R.drawable.ic_subway_marker);
                                                                                                                        marker.setCustomImageAutoscale(false);
                                                                                                                        marker.setCustomImageAnchor(0.5f, 1.0f);
                                                                                                                        mMapView.addPOIItem(marker);
                                                                                                                    }
                                                                                                                    for (Document document : bankList) {
                                                                                                                        MapPOIItem marker = new MapPOIItem();
                                                                                                                        marker.setItemName(document.getPlaceName());
                                                                                                                        marker.setTag(tagNum++);
                                                                                                                        double x = Double.parseDouble(document.getY());
                                                                                                                        double y = Double.parseDouble(document.getX());

                                                                                                                        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(x, y);
                                                                                                                        marker.setMapPoint(mapPoint);
                                                                                                                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                                                                                                                        marker.setCustomImageResourceId(R.drawable.ic_bank_marker);
                                                                                                                        marker.setCustomImageAutoscale(false);
                                                                                                                        marker.setCustomImageAnchor(0.5f, 1.0f);
                                                                                                                        mMapView.addPOIItem(marker);
                                                                                                                    }
                                                                                                                    for (Document document : hospitalList) {
                                                                                                                        MapPOIItem marker = new MapPOIItem();
                                                                                                                        marker.setItemName(document.getPlaceName());
                                                                                                                        marker.setTag(tagNum++);
                                                                                                                        double x = Double.parseDouble(document.getY());
                                                                                                                        double y = Double.parseDouble(document.getX());

                                                                                                                        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(x, y);
                                                                                                                        marker.setMapPoint(mapPoint);
                                                                                                                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                                                                                                                        marker.setCustomImageResourceId(R.drawable.ic_hospital_marker);
                                                                                                                        marker.setCustomImageAutoscale(false);
                                                                                                                        marker.setCustomImageAnchor(0.5f, 1.0f);
                                                                                                                        mMapView.addPOIItem(marker);
                                                                                                                    }
                                                                                                                    for (Document document : pharmacyList) {
                                                                                                                        MapPOIItem marker = new MapPOIItem();
                                                                                                                        marker.setItemName(document.getPlaceName());
                                                                                                                        marker.setTag(tagNum++);
                                                                                                                        double x = Double.parseDouble(document.getY());
                                                                                                                        double y = Double.parseDouble(document.getX());

                                                                                                                        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(x, y);
                                                                                                                        marker.setMapPoint(mapPoint);
                                                                                                                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                                                                                                                        marker.setCustomImageResourceId(R.drawable.ic_pharmacy_marker);
                                                                                                                        marker.setCustomImageAutoscale(false);
                                                                                                                        marker.setCustomImageAnchor(0.5f, 1.0f);
                                                                                                                        mMapView.addPOIItem(marker);

                                                                                                                        mLoaderLayout.setVisibility(View.GONE);
                                                                                                                        searchDetailFab.setVisibility(View.VISIBLE);
                                                                                                                    }
                                                                                                                    for (Document document : cafeList) {
                                                                                                                        MapPOIItem marker = new MapPOIItem();
                                                                                                                        marker.setItemName(document.getPlaceName());
                                                                                                                        marker.setTag(tagNum++);
                                                                                                                        double x = Double.parseDouble(document.getY());
                                                                                                                        double y = Double.parseDouble(document.getX());

                                                                                                                        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(x, y);
                                                                                                                        marker.setMapPoint(mapPoint);
                                                                                                                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                                                                                                                        marker.setCustomImageResourceId(R.drawable.ic_cafe_marker);
                                                                                                                        marker.setCustomImageAutoscale(false);
                                                                                                                        marker.setCustomImageAnchor(0.5f, 1.0f);
                                                                                                                        mMapView.addPOIItem(marker);

                                                                                                                        mLoaderLayout.setVisibility(View.GONE);
                                                                                                                        searchDetailFab.setVisibility(View.VISIBLE);
                                                                                                                    }
                                                                                                                }
                                                                                                            }

                                                                                                            @Override
                                                                                                            public void onFailure(@NotNull Call<CategoryResult> call, @NotNull Throwable t) {

                                                                                                            }
                                                                                                        });
                                                                                                    }
                                                                                                }

                                                                                                @Override
                                                                                                public void onFailure(@NotNull Call<CategoryResult> call, Throwable t) {

                                                                                                }
                                                                                            });
                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onFailure(@NotNull Call<CategoryResult> call, @NotNull Throwable t) {

                                                                                    }
                                                                                });
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onFailure(@NotNull Call<CategoryResult> call, @NotNull Throwable t) {

                                                                        }
                                                                    });
                                                                }
                                                            }

                                                            @Override
                                                            public void onFailure(@NotNull Call<CategoryResult> call, @NotNull Throwable t) {

                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onFailure(@NotNull Call<CategoryResult> call, @NotNull Throwable t) {

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NotNull Call<CategoryResult> call, @NotNull Throwable t) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<CategoryResult> call, @NotNull Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call<CategoryResult> call, @NotNull Throwable t) {
                Log.d(TAG, "FAIL");
            }
        });
    }

    public void anim() {
        if (isFabOpen) {
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            goHome.startAnimation(fab_close);
            overlayFab.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            goHome.setClickable(false);
            overlayFab.setClickable(false);
            isFabOpen = false;
        } else {
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            goHome.startAnimation(fab_open);
            overlayFab.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            goHome.setClickable(true);
            overlayFab.setClickable(true);
            isFabOpen = true;
        }
    }


    @Override
    public void onMapViewInitialized(MapView mapView) {
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {
    }


    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onDaumMapOpenAPIKeyAuthenticationResult(MapView mapView, int i, String s) {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }


    public void showMap(Uri geoLocation) {
        Intent intent;
        try {
            FancyToast.makeText(getActivity(), "카카오맵으로 길찾기를 시도합니다.", FancyToast.LENGTH_SHORT, FancyToast.INFO, true).show();
            intent = new Intent(Intent.ACTION_VIEW, geoLocation);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            FancyToast.makeText(getActivity(), "길찾기에는 카카오맵이 필요합니다. 다운받아주시길 바랍니다.", FancyToast.LENGTH_SHORT, FancyToast.INFO, true).show();
            intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://play.google.com/store/apps/details?id=net.daum.android.map&hl=ko"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
    }


    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
        double lat = mapPOIItem.getMapPoint().getMapPointGeoCoord().latitude;
        double lng = mapPOIItem.getMapPoint().getMapPointGeoCoord().longitude;
        Toast.makeText(getActivity(), mapPOIItem.getItemName(), Toast.LENGTH_SHORT).show();
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getActivity());
        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
        builder.setTitle("선택해주세요");
        builder.setSingleChoiceItems(new String[]{"장소 정보", "길찾기", "목적지 등록"}, 3, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int index) {
                if (index == 0) {
                    mLoaderLayout.setVisibility(View.VISIBLE);
                    ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                    Call<CategoryResult> call = apiInterface.getSearchLocationDetail(getString(R.string.restapi_key), mapPOIItem.getItemName(), String.valueOf(lat), String.valueOf(lng), 1);
                    call.enqueue(new Callback<CategoryResult>() {
                        @Override
                        public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                            mLoaderLayout.setVisibility(View.GONE);
                            if (response.isSuccessful()) {
                                Intent intent = new Intent(getActivity(), PlaceDetailActivity.class);
                                assert response.body() != null;
                                intent.putExtra(IntentKey.PLACE_SEARCH_DETAIL_EXTRA, response.body().getDocuments().get(0));
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onFailure(Call<CategoryResult> call, Throwable t) {
                            FancyToast.makeText(getActivity(), "해당장소에 대한 상세정보는 없습니다.", FancyToast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                            mLoaderLayout.setVisibility(View.GONE);
                            Intent intent = new Intent(getActivity(), PlaceDetailActivity.class);
                            startActivity(intent);
                        }
                    });
                } else if (index == 1) {
                    showMap(Uri.parse("daummaps://route?sp=" + mCurrentLat + "," + mCurrentLng + "&ep=" + lat + "," + lng + "&by=FOOT"));
                } else if (index == 2) {
                    FancyToast.makeText(getActivity(), "목적지가 등록되었습니다.", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
                    mLoaderLayout.setVisibility(View.GONE);
                    dialogInterface.dismiss();



                    Storage storage = new Storage(getContext());
                    storage.setEncryptConfiguration(configuration);
                    String path = storage.getInternalFilesDirectory();
                    String Filepath = path + "Destination.txt";
                    String dest = lat + " " + lng + " " + mapPOIItem.getItemName();
                    boolean fileExists = storage.isFileExist(Filepath);
                    if(fileExists){
                        storage.deleteFile(Filepath);
                    }
                    storage.createFile(Filepath, dest);
                }
            }
        });
        builder.addButton("취소", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();

    }


    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        mSearchName = "드래그한 장소";
        mSearchLng = mapPointGeo.longitude;
        mSearchLat = mapPointGeo.latitude;
        mMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(mSearchLat, mSearchLng), true);
        searchMarker.setItemName(mSearchName);
        MapPoint mapPoint2 = MapPoint.mapPointWithGeoCoord(mSearchLat, mSearchLng);
        searchMarker.setMapPoint(mapPoint2);
        searchMarker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        searchMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        searchMarker.setDraggable(true);
        mMapView.addPOIItem(searchMarker);
    }


    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float accuracyInMeters) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        Log.i(TAG, String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, accuracyInMeters));
        currentMapPoint = MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude, mapPointGeo.longitude);

        mMapView.setMapCenterPoint(currentMapPoint, true);

        mCurrentLat = mapPointGeo.latitude;
        mCurrentLng = mapPointGeo.longitude;
        Log.d(TAG, "현재위치 => " + mCurrentLat + "  " + mCurrentLng);
        mLoaderLayout.setVisibility(View.GONE);

        if(!isTrackingMode)
            mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {
        Log.i(TAG, "onCurrentLocationUpdateFailed");
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {
        Log.i(TAG, "onCurrentLocationUpdateCancelled");
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
    }

    @Subscribe
    public void search(Document document) {
        FancyToast.makeText(getActivity(), document.getPlaceName() + " 검색", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
        mSearchName = document.getPlaceName();
        mSearchLng = Double.parseDouble(document.getX());
        mSearchLat = Double.parseDouble(document.getY());
        mMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(mSearchLat, mSearchLng), true);
        mMapView.removePOIItem(searchMarker);
        searchMarker.setItemName(mSearchName);
        searchMarker.setTag(10000);
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(mSearchLat, mSearchLng);
        searchMarker.setMapPoint(mapPoint);
        searchMarker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        searchMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);

        searchMarker.setDraggable(true);
        mMapView.addPOIItem(searchMarker);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    @Override
    public void onDestroyView(){
        super.onDestroyView();
        bus.unregister(this);

        if (isTrackingMode) {
            mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            mMapView.setShowCurrentLocationMarker(false);
        }
    }


    public void NotificationSomethings(String content_msg) {

        NotificationManager notificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(getActivity(), NotificationActivity.class);
        notificationIntent.putExtra("notificationId", count);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), NOTIFICATION_CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground))
                .setContentTitle("The Day I Drunk")
                .setContentText(content_msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.ic_launcher_foreground);
            CharSequence CN  = "notification channel";

            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CN , importance);

            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

        }else builder.setSmallIcon(R.mipmap.ic_launcher);

        assert notificationManager != null;
        notificationManager.notify(10000, builder.build());
    }


}