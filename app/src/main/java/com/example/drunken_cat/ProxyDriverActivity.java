package com.example.drunken_cat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class ProxyDriverActivity extends AppCompatActivity {

    private ImageButton btn_back,btn_kakao;

    Button btn_proxy1,btn_proxy2,btn_proxy3,btn_proxy4,btn_proxy5,btn_proxy6,btn_proxy7,btn_proxy8,btn_proxy9,btn_proxy10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxydriver);
        btn_back = findViewById(R.id.btn_back);
        btn_kakao = findViewById(R.id.btn_kakao);
        btn_proxy1 = findViewById(R.id.btn_proxy1);
        btn_proxy2 = findViewById(R.id.btn_proxy2);
        btn_proxy3 = findViewById(R.id.btn_proxy3);
        btn_proxy4 = findViewById(R.id.btn_proxy4);
        btn_proxy5 = findViewById(R.id.btn_proxy5);
        btn_proxy6 = findViewById(R.id.btn_proxy6);
        btn_proxy7 = findViewById(R.id.btn_proxy7);
        btn_proxy8 = findViewById(R.id.btn_proxy8);
        btn_proxy9 = findViewById(R.id.btn_proxy9);
        btn_proxy10 = findViewById(R.id.btn_proxy10);



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






        btn_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View Add_Main) {
                Intent back_intent = new Intent(ProxyDriverActivity.this, MapActivity.class);
                startActivity(back_intent);
            }
        });

        btn_kakao.setOnClickListener(new View.OnClickListener() {
            public void onClick(View Proxy_Kakao) {

                try {
                    Intent kakao_intent = getPackageManager().getLaunchIntentForPackage("com.kakao.taxi");
                    kakao_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(kakao_intent);
                }
                catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "카카오T 앱이 설치되어있지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
}