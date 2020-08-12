package com.example.drunken_cat;
import android.content.Context;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.util.*;
import java.io.*;

public class PersonAdd extends AppCompatActivity {
    HashSet<String> set = new HashSet<String>();
    EditText et,et2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
   //     et=findViewById(R.id.et); // front 구현 되면 주석제거
    //    et2=findViewById(R.id.et2); // front 구현 되면 주석제거

    }

    public void SaveData(View view) {
        String name = et.getText().toString();
        String phone = et2.getText().toString();
        et.setText(""); et2.setText("");

        DuplicationVerify(name,phone); // 중복검사

        try {
            FileOutputStream fos = openFileOutput("Friend_Data.txt", MODE_APPEND);
            PrintWriter writer = new PrintWriter(fos);

            Iterator iter = set.iterator();
            while(iter.hasNext())
                writer.write((String)iter.next() +"\n");

            writer.flush();
            writer.close();

            //Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }


   void DuplicationVerify(String n, String p){
        String s = n+" "+p; set.add(s);

       try {
           FileInputStream InputStream = openFileInput("Friend_Data.txt");
           InputStreamReader isr= new InputStreamReader(InputStream);
           BufferedReader reader= new BufferedReader(isr);
           StringBuffer buffer= new StringBuffer();

           while(true){
               String content= reader.readLine();
               if(content==null)  break;
               else set.add(content);
           }
       } catch (FileNotFoundException e) {e.printStackTrace();} catch (IOException e) {e.printStackTrace();}

       File mfile = new File("Friend_Data.txt");
       mfile.delete();
  }


}
