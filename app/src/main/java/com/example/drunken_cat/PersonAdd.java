package com.example.drunken_cat;
import android.content.Context;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.util.*;
import java.io.*;


public class AddFriend extends AppCompatActivity{
    HashSet<String> set = new HashSet<String>();
    private String mName, mPhoneNum;

    public AddFriend(String Name, String PhoneNum) {
        this.mName = Name;
        this.mPhoneNum = PhoneNum;
    }

    public void AddToLocalDB(){

        try {
            FileOutputStream fos = openFileOutput("Friend_Data.txt", MODE_APPEND);
            PrintWriter writer = new PrintWriter(fos);

            for(int i=0; i<3; i++)
                writer.write(mName +" "+ mPhoneNum+"\n");
            writer.flush();
            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

}


