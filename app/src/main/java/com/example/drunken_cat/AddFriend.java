package com.example.drunken_cat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

public class AddFriend extends AddFriendActivity {
    HashSet<String> set = new HashSet<String>();
    String mName, mPhoneNum;

    // 전화번호 정규식 추가
    // 저장누르면 계속 덮붙여 써진다. 3줄이상이면 못 쓰게

    public AddFriend(String Name, String PhoneNum) {
        this.mName = Name;
        this.mPhoneNum = PhoneNum;
    }

    public void AddToLocalDB(File fos) throws IOException {
        FileWriter fw = new FileWriter(fos,true);
        BufferedWriter buff = new BufferedWriter(fw);;

        buff.write(mName + " " + mPhoneNum + "\n");

        try {
            if (buff != null )
                buff.close();
            if( fw !=null)
                fw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


  /*  public int CheckList(File f){ // 꽉찾는지 체크

        */
}


