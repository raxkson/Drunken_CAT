package com.example.drunken_cat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.drunken_cat.Controller.AddFriend;

public class MainActivity extends AppCompatActivity {

    private ImageButton btn_back;

    EditText friend_name_1, friend_phone_1,
            friend_name_2, friend_phone_2,
            friend_name_3, friend_phone_3;

    Button btn_register_friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* 귀가천사 변수 선언 및 init */

        friend_name_1 = findViewById(R.id.friend_name_1);
        friend_phone_1 = findViewById(R.id.friend_phone_1);
        friend_name_2 = findViewById(R.id.friend_name_2);
        friend_phone_2 = findViewById(R.id.friend_phone_2);
        friend_name_3 = findViewById(R.id.friend_name_3);
        friend_phone_3 = findViewById(R.id.friend_phone_3);


        btn_register_friend = findViewById(R.id.btn_register_friend);

        btn_back = findViewById(R.id.btn_back);

        //등록 버튼 클릭 시
        btn_register_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View register) {

                EditText[] mEtNameArr = {friend_name_1, friend_name_2, friend_name_3};
                EditText[] mEtPhoneArr = {friend_phone_1, friend_phone_2, friend_phone_3};

                for (int i = 0; i < 3; i++) {
                    if (mEtNameArr[i].getText().toString().equals(null) || mEtPhoneArr[i].getText().toString().equals(null)) {
                        Toast.makeText(getApplicationContext(), "입력정보를 확인해주세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        AddFriend req = new AddFriend(mEtNameArr[i].getText().toString(), mEtPhoneArr[i].getText().toString());
                        req.AddToLocalDB();
                    }
                }

            }
        });


        btn_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View AddtoMain) {
                Intent back_intent = new Intent(MainActivity.this, back.class);
                startActivity(back_intent);
            }
        });


    }
}