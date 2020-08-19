package com.example.drunken_cat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProxyDriverActivity extends Fragment {


    private Button btn_kakao,btn_proxy1,btn_proxy2,btn_proxy3,btn_proxy4,btn_proxy5,btn_proxy6,btn_proxy7,btn_proxy8,btn_proxy9,btn_proxy10;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_proxydriver, container, false);
        btn_kakao = view.findViewById(R.id.btn_kakao);
        btn_proxy1 = view.findViewById(R.id.btn_proxy1);
        btn_proxy2 = view.findViewById(R.id.btn_proxy2);
        btn_proxy3 = view.findViewById(R.id.btn_proxy3);
        btn_proxy4 = view.findViewById(R.id.btn_proxy4);
        btn_proxy5 = view.findViewById(R.id.btn_proxy5);
        btn_proxy6 = view.findViewById(R.id.btn_proxy6);
        btn_proxy7 = view.findViewById(R.id.btn_proxy7);
        btn_proxy8 = view.findViewById(R.id.btn_proxy8);
        btn_proxy9 = view.findViewById(R.id.btn_proxy9);
        btn_proxy10 = view.findViewById(R.id.btn_proxy10);



        btn_proxy1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View Proxy1){
                Intent call1 = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:1688-1771"));
                startActivity(call1);
            }

        });
        btn_proxy2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View Proxy2){
                Intent call2 = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:1688-9953"));
                startActivity(call2);
            }

        });
        btn_proxy3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View Proxy3){
                Intent call3 = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:1599-3938"));
                startActivity(call3);
            }
        });
        btn_proxy4.setOnClickListener(new View.OnClickListener(){
            public void onClick(View Proxy4){
                Intent call4 = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:1688-0959"));
                startActivity(call4);
            }

        });
        btn_proxy5.setOnClickListener(new View.OnClickListener(){
            public void onClick(View Proxy5){
                Intent call5 = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:1633-3679"));
                startActivity(call5);
            }
        });

        btn_proxy6.setOnClickListener(new View.OnClickListener(){
            public void onClick(View Proxy6){
                Intent call6 = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:1666-4943"));
                startActivity(call6);
            }

        });
        btn_proxy7.setOnClickListener(new View.OnClickListener(){
            public void onClick(View Proxy7){
                Intent call7 = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:1670-7833"));
                startActivity(call7);
            }
        });

        btn_proxy8.setOnClickListener(new View.OnClickListener(){
            public void onClick(View Proxy8){
                Intent call8 = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:1688-6944"));
                startActivity(call8);
            }

        });
        btn_proxy9.setOnClickListener(new View.OnClickListener(){
            public void onClick(View Proxy9){
                Intent call9 = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:1522-4488"));
                startActivity(call9);
            }
        });
        btn_proxy10.setOnClickListener(new View.OnClickListener(){
            public void onClick(View Proxy10){
                Intent call10 = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:1577-0486"));
                startActivity(call10);
            }
        });



        btn_kakao.setOnClickListener(new View.OnClickListener() {
            public void onClick(View Proxy_Kakao) {

                try {
                    Intent kakao_intent = getActivity().getPackageManager().getLaunchIntentForPackage("com.kakao.taxi");
                    kakao_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(kakao_intent);
                }
                catch (Exception e) {
                    Toast.makeText(getActivity(), "카카오T 앱이 설치되어있지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}