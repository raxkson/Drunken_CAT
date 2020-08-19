package com.example.drunken_cat;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.snatik.storage.EncryptConfiguration;
import com.snatik.storage.Storage;

public class AddFriendActivity extends Fragment {

    EditText friend_name_1, friend_phone_1,
            friend_name_2, friend_phone_2,
            friend_name_3, friend_phone_3;

    Button btn_register_friend;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_addfriend, container, false);
        /* 귀가천사 변수 선언 및 init */


        friend_name_1 = view.findViewById(R.id.friend_name_1);
        friend_phone_1 = view.findViewById(R.id.friend_phone_1);
        friend_name_2 = view.findViewById(R.id.friend_name_2);
        friend_phone_2 = view.findViewById(R.id.friend_phone_2);
        friend_name_3 = view.findViewById(R.id.friend_name_3);
        friend_phone_3 = view.findViewById(R.id.friend_phone_3);

        btn_register_friend = (Button) view.findViewById(R.id.btn_register_friend);

        //encrypt and decrypt file
        String IVX = "abcdefghijklmnop"; // 16 lenght - not secret
        String SECRET_KEY = "secret1234567890"; // 16 lenght - secret
        byte[] SALT = "0000111100001111".getBytes(); // random 16 bytes array
        EncryptConfiguration configuration = new EncryptConfiguration.Builder()
                .setEncryptContent(IVX, SECRET_KEY, SALT)
                .build();

        Storage storage = new Storage(getContext());
        storage.setEncryptConfiguration(configuration);
        String path = storage.getInternalFilesDirectory();
        String Filepath = path + "Friend.txt";

        boolean fileExists = storage.isFileExist(Filepath);
        if(fileExists) {
            String content = storage.readTextFile(Filepath);
            String[] text = content.split("☎");
            friend_name_1.setText(text[0]);
            friend_phone_1.setText(text[1]);
            friend_name_2.setText(text[2]);
            friend_phone_2.setText(text[3]);
            friend_name_3.setText(text[4]);
            friend_phone_3.setText(text[5]);
        }

        //등록 버튼 클릭 시
        btn_register_friend.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View register) {
                EditText[] mEtNameArr = {friend_name_1, friend_name_2, friend_name_3};
                EditText[] mEtPhoneArr = {friend_phone_1, friend_phone_2, friend_phone_3};
                String name_phone = friend_name_1.getText() + "☎" + friend_phone_1.getText() + "☎" + friend_name_2.getText() + "☎" + friend_phone_2.getText() + "☎" + friend_name_3.getText() + "☎" + friend_phone_3.getText();
                if(fileExists){
                    storage.deleteFile(Filepath);
                }
                storage.createFile(Filepath, name_phone);
                Toast.makeText(getActivity(), "등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}