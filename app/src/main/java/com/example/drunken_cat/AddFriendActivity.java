package com.example.drunken_cat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class AddFriendActivity extends AppCompatActivity {

    private ImageButton btn_back;

    EditText friend_name_1, friend_phone_1,
            friend_name_2, friend_phone_2,
            friend_name_3, friend_phone_3;

    Button btn_register_friend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriend);
        final File file = new File(getApplicationContext().getFilesDir(),"Friend.txt");
        /* 귀가천사 변수 선언 및 init */

        friend_name_1 = findViewById(R.id.friend_name_1);
        friend_phone_1 = findViewById(R.id.friend_phone_1);
        friend_name_2 = findViewById(R.id.friend_name_2);
        friend_phone_2 = findViewById(R.id.friend_phone_2);
        friend_name_3 = findViewById(R.id.friend_name_3);
        friend_phone_3 = findViewById(R.id.friend_phone_3);

        btn_register_friend = findViewById(R.id.btn_register_friend);

        btn_back = findViewById(R.id.btn_back);

        //등록된 정보 로드
        StringBuffer buffer = new StringBuffer();
        String data = null;
        FileInputStream fis = null;

        try {
            FileReader fr = new FileReader(file);
            BufferedReader buf= new BufferedReader(fr);


            String s;
            if((s=buf.readLine()) != null){
                String[] arr = s.split(" ");
                friend_name_1.setText(arr[0]); friend_phone_1.setText(arr[1]);
            }
            if((s=buf.readLine()) != null){
                String[] arr = s.split(" ");
                friend_name_2.setText(arr[0]); friend_phone_2.setText(arr[1]);
            }
            if((s=buf.readLine()) != null){
                String[] arr = s.split(" ");
                friend_name_3.setText(arr[0]); friend_phone_3.setText(arr[1]);
            }

            fr.close();
            buf.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //등록 버튼 클릭 시
        btn_register_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View register) {

                EditText[] mEtNameArr = {friend_name_1, friend_name_2, friend_name_3};
                EditText[] mEtPhoneArr = {friend_phone_1, friend_phone_2, friend_phone_3};

                if(file.exists())
                    file.delete();

                for (int i = 0; i < 3; i++) {
                    if (mEtNameArr[i].getText().toString().equals("") || mEtPhoneArr[i].getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "입력정보를 확인해주세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        AddFriend req = new AddFriend(mEtNameArr[i].getText().toString(), mEtPhoneArr[i].getText().toString());

                        try {
                            req.AddToLocalDB(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
                //Button btn = (Button) findViewById(R.id.btn_register_friend);
              //  btn.setEnabled(false);



            }
        });

        //불러오기 버튼



        btn_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View AddtoMain) {
                Intent back_intent = new Intent(AddFriendActivity.this, MapActivity.class);
                startActivity(back_intent);
            }
        });

    }
}