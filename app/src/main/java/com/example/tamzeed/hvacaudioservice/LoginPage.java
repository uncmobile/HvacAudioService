package com.example.tamzeed.hvacaudioservice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.util.*;

public class LoginPage extends AppCompatActivity {

    Button button;
    String str;
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        addListener();
        pref= getSharedPreferences("myPref", Context.MODE_PRIVATE);
        str= pref.getString("id",null);
        if(str!=null)
        {
            Intent intent = new Intent(LoginPage.this,MainActivity.class);
            startActivity(intent);
            LoginPage.this.finish();
        }
    }
    public void addListener()
    {
        try {
            button = (Button) findViewById(R.id.buttonStart);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor myEditor= pref.edit();
                    EditText ed= (EditText) findViewById(R.id.editText);
                    str= ed.getText().toString();
                    if(str!="")
                    {
                        myEditor.putString("id",str);
                        myEditor.commit();

                    }
                    else
                    {
                        Random rand= new Random();
                        int ran= rand.nextInt();
                        str="not_given"+ ran;
                        myEditor.putString("id",str);

                    }
                    Intent intent = new Intent(LoginPage.this,MainActivity.class);
                    startActivity(intent);
                    LoginPage.this.finish();
                }
            });

        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
