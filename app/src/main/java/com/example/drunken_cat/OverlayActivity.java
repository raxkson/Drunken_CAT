package com.example.drunken_cat;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.snatik.storage.Storage;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;

public class OverlayActivity extends AppCompatActivity {
    private MapView mMapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overlay);

        MapView mapView = new MapView(this);
        RelativeLayout mapViewContainer = (RelativeLayout) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        Storage storage = new Storage(getApplicationContext());
        String path = storage.getInternalFilesDirectory();
        String Filepath = path + "Location.txt";


        boolean fileExists = storage.isFileExist(Filepath);
        //if(fileExists) {
            //String content = storage.readTextFile(Filepath);
            //name1☎phone1☎name2☎phone2☎name3☎phone3
           String content ="37.3629087 126.6425945 안녕\n" +
                   "37.3654128 126.6422064 하세요\n" +
                   "37.367826 126.646251 바이바이\n" +
                   "37.372848 126.645865 테스트";
            String[] text = content.split("\n");
        MapPolyline polyline = new MapPolyline();
            for(int i = 0; i < text.length; i++){
                String[] txt = text[i].split(" ");
                double latitude = Double.parseDouble(txt[0]);
                double longitude = Double.parseDouble(txt[1]);
                //카카오맵은 참고로 new MapPoint()로  생성못함. 좌표기준이 여러개라 이렇게 메소드로 생성해야함
                MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);
                MapPOIItem marker = new MapPOIItem();
                marker.setItemName(txt[2]);
                marker.setTag(0);
                marker.setMapPoint(mapPoint);
                marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

                mapView.addPOIItem(marker);


                polyline.setTag(1000);
                polyline.setLineColor(Color.argb(128, 255, 51, 0)); // Polyline 컬러 지정.

// Polyline 좌표 지정.
                polyline.addPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));

// Polyline 지도에 올리기.

            }
            mapView.addPolyline(polyline);

// 지도뷰의 중심좌표와 줌레벨을 Polyline이 모두 나오도록 조정.
        MapPointBounds mapPointBounds = new MapPointBounds(polyline.getMapPoints());
        int padding = 100; // px
        mMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding));

        //}
    }
}