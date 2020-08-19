package com.example.drunken_cat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import com.example.drunken_cat.CipherFunc;
import com.example.drunken_cat.adapter.LocationAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import net.daum.mf.map.api.MapView;

public class AddFriendActivity extends Fragment {

    private ImageButton btn_back;
    private String key = "DrunkenCAT";
    EditText friend_name_1, friend_phone_1,
            friend_name_2, friend_phone_2,
            friend_name_3, friend_phone_3;

    Button btn_register_friend;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String decFunc(String s) throws Exception {
        String decStr = CipherFunc.DecryptText(s, key);
        return decStr;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_addfriend, container, false);
        final File file = new File(getActivity().getFilesDir(), "Friend.txt");
        /* 귀가천사 변수 선언 및 init */

        friend_name_1 = view.findViewById(R.id.friend_name_1);
        friend_phone_1 = view.findViewById(R.id.friend_phone_1);
        friend_name_2 = view.findViewById(R.id.friend_name_2);
        friend_phone_2 = view.findViewById(R.id.friend_phone_2);
        friend_name_3 = view.findViewById(R.id.friend_name_3);
        friend_phone_3 = view.findViewById(R.id.friend_phone_3);

        btn_register_friend = (Button) view.findViewById(R.id.btn_register_friend);


        //등록된 정보 로드
        StringBuffer buffer = new StringBuffer();
        String data = null;
        FileInputStream fis = null;

        try {
            FileReader fr = new FileReader(file);
            BufferedReader buf = new BufferedReader(fr);


            String s;
            if ((s = buf.readLine()) != null) {
                String[] arr = s.split(" ");
                friend_name_1.setText(decFunc(arr[0]));
                friend_phone_1.setText(decFunc(arr[1]));
            }
            if ((s = buf.readLine()) != null) {
                String[] arr = s.split(" ");
                friend_name_2.setText(decFunc(arr[0]));
                friend_phone_2.setText(decFunc(arr[1]));
            }
            if ((s = buf.readLine()) != null) {
                String[] arr = s.split(" ");
                friend_name_3.setText(decFunc(arr[0]));
                friend_phone_3.setText(decFunc(arr[1]));
            }

            fr.close();
            buf.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //등록 버튼 클릭 시

        btn_register_friend.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View register) {

                EditText[] mEtNameArr = {friend_name_1, friend_name_2, friend_name_3};
                EditText[] mEtPhoneArr = {friend_phone_1, friend_phone_2, friend_phone_3};

                if (file.exists())
                    file.delete();

                for (int i = 0; i < 3; i++) {
                    if (mEtNameArr[i].getText().toString().equals("") || mEtPhoneArr[i].getText().toString().equals("")) {
                        Toast.makeText(getActivity(), "입력정보를 확인해주세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        AddFriend req = new AddFriend(mEtNameArr[i].getText().toString(), mEtPhoneArr[i].getText().toString());

                        try {
                            req.AddToLocalDB(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });

        return view;
    }
}