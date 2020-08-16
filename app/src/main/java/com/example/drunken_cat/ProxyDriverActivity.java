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

    Button btn_proxy1,btn_proxy2,btn_proxy3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxydriver);
        btn_back = findViewById(R.id.btn_back);
        btn_kakao = findViewById(R.id.btn_kakao);
        btn_proxy1 = findViewById(R.id.btn_proxy1);
        btn_proxy2 = findViewById(R.id.btn_proxy2);
        btn_proxy3 = findViewById(R.id.btn_proxy3);

        btn_proxy1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View Proxy1){
                Intent call1 = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:1577-1577"));
                startActivity(call1);
            }

        });
        btn_proxy2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View Proxy2){
                Intent call2 = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:1588-1588"));
                startActivity(call2);
            }

        });
        btn_proxy3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View Proxy3){
                Intent call3 = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:1599-1599"));
                startActivity(call3);
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