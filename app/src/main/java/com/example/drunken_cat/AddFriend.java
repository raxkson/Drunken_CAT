package com.example.drunken_cat;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashSet;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class AddFriend extends CipherFunc {
    HashSet<String> set = new HashSet<String>();
    String mName, mPhoneNum;


    public AddFriend(String Name, String PhoneNum) {
        this.mName = Name;
        this.mPhoneNum = PhoneNum;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void AddToLocalDB(File fos) throws Exception {
        FileWriter fw = new FileWriter(fos,true);
        BufferedWriter buff = new BufferedWriter(fw);;


        String encName = CipherFunc.EncryptText(mName,key);
        String encPhone = CipherFunc.EncryptText(mPhoneNum, key);
        System.out.println("[*]    encName :"+encName);
        System.out.println("[*]    encPhone :"+encPhone);
        buff.write(encName + " " + encPhone + "\n");

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


