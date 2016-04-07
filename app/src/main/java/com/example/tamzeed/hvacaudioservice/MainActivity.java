package com.example.tamzeed.hvacaudioservice;

import android.content.Intent;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.SharedPreferences;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    PowerManager powerM;
    SharedPreferences pref;
    PowerManager.WakeLock wk;
    TextView txt;
    MyService ms= new MyService();
    Button startButton, endButton, editButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        pref= getSharedPreferences("myPref", Context.MODE_PRIVATE);
        Constants.deviceName= pref.getString("id", null);
        txt = (TextView) findViewById(R.id.textView);
        txt.setText("DEVICE: " + Constants.deviceName);
        powerM= (PowerManager) getSystemService(POWER_SERVICE);
        wk = powerM.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        startButton= (Button) findViewById(R.id.button1);
        endButton= (Button) findViewById(R.id.button2);
        editButton= (Button) findViewById(R.id.editButton);

        //editButton.setEnabled(false);
        String strTemp= pref.getString("start", null);
       // txt.setText(strTemp);
        if(strTemp!= "yes")
        {

            endButton.setEnabled(false);
       //     txt = (TextView) findViewById(R.id.textView2);
         //   txt.setText("Service is Stopped!!!!");
           // editButton.setEnabled(false);


        }
        else {
          //  txt = (TextView) findViewById(R.id.textView2);
           // txt.setText("Service is Running!!!!");
            startButton.setEnabled(false);
            editButton.setEnabled(false);

        }
        addListener();
    }
    public void startService(View view) {
        txt = (TextView) findViewById(R.id.textView2);
        txt.setText("Service is Running!!!!");
        startService(new Intent(getBaseContext(), MyService.class));
        wk.acquire();
        startButton.setEnabled(false);
        endButton.setEnabled(true);
        editButton.setEnabled(false);
        SharedPreferences.Editor myEditor= pref.edit();
        myEditor.putString("start", "yes");
        myEditor.commit();

    }

    // Method to stop the service
    public void stopService(View view) {

        try {
            startButton.setEnabled(true);
            endButton.setEnabled(false);
            editButton.setEnabled(true);

            SharedPreferences.Editor myEditor= pref.edit();
            myEditor.putString("start", "no");
            myEditor.commit();
            txt = (TextView) findViewById(R.id.textView2);
            txt.setText("Service is Stopped!!!!");
            stopService(new Intent(getBaseContext(), MyService.class));
            wk.release();



        }catch (Exception e)
        {
            e.printStackTrace();
        }


    }


    public void addListener()
    {
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this, Editpage.class);
                startActivity(intent);

            }
        });


    }
}
