package com.example.drunken_cat.Controller;

public class AddFriend {

    String mName, mPhoneNum;

    public AddFriend(String Name, String PhoneNum) {
        this.mName = Name;
        this.mPhoneNum = PhoneNum;

    }

    public String getmName() {
        return mName;
    }

    public String getmPhoneNum() {
        return mPhoneNum;
    }


    public void AddToLocalDB(){
        /*
        Local DB
         */
    }
}
