package com.example.drunken_cat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

public class AddFriend extends AddFriendActivity {
    HashSet<String> set = new HashSet<String>();
    String mName, mPhoneNum;

    // 전화번호 정규식 추가
    // 저장누르면 계속 덮붙여 써진다. 3줄이상이면 못 쓰게
    // 전화번호부 Delete
    public AddFriend(String Name, String PhoneNum) {
        this.mName = Name;
        this.mPhoneNum = PhoneNum;
    }

    public void AddToLocalDB(File fos) throws IOException {
        System.out.println(fos);
        FileWriter fw = new FileWriter(fos,true);
        BufferedWriter buff = new BufferedWriter(fw);;

        buff.write(mName + " " + mPhoneNum + "\n");

        try {
            if (buff != null || fw !=null)
                buff.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}


