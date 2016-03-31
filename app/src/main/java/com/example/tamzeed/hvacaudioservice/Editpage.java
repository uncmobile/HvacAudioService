package com.example.tamzeed.hvacaudioservice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Random;

public class Editpage extends AppCompatActivity {

    Button button;
    String str;
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref= getSharedPreferences("myPref", Context.MODE_PRIVATE);
        setContentView(R.layout.activity_editpage);
        addListener();
    }
    public void addListener()
    {
        button= (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor myEditor= pref.edit();
                EditText ed= (EditText) findViewById(R.id.editText2);
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
                Intent intent = new Intent(Editpage.this,MainActivity.class);
                startActivity(intent);
                Editpage.this.finish();
            }
        });
    }
}
