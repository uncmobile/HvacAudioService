package com.example.tamzeed.hvacaudioservice;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.SharedPreferences;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    private AlarmBroadcastReciever alarm;
    SensorManager sensorManager;

    Sensor tempSensor,humidSensor;;


    Context textCon;
    PowerManager powerM;
    SharedPreferences pref;
    PowerManager.WakeLock wk;
    TextView txt;
    MyService ms= new MyService();
    Button startButton, endButton, editButton, humanButton;
    int in=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textCon= this.getApplicationContext();

       // in++;


        alarm = new AlarmBroadcastReciever();
        pref= getSharedPreferences("myPref", Context.MODE_PRIVATE);
        Constants.deviceName= pref.getString("id", null);
        txt = (TextView) findViewById(R.id.textView);
        txt.setText("DEVICE: " + Constants.deviceName);
       // txt.setText("Service is stopped!!!!"+in);
        powerM= (PowerManager) getSystemService(POWER_SERVICE);
        wk = powerM.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        startButton= (Button) findViewById(R.id.button1);
        endButton= (Button) findViewById(R.id.button2);
        editButton= (Button) findViewById(R.id.editButton);
        humanButton = (Button) findViewById(R.id.button3);

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
            humanButton.setEnabled(false);


        }
        addListener();
    }

    public void onDestroy() {

        super.onDestroy();
        if(wk.isHeld())
            wk.release();

        txt.setText("Service is stopped!!!!");

    }

    public void sensorInitializer()
    {
        sensorManager= (SensorManager) getSystemService(SENSOR_SERVICE);
        tempSensor= sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        if(tempSensor!=null)
        {
            sensorManager.registerListener(this, tempSensor, 1000);

        }
        else
        {
            Constants.temperature="X";
        }

        humidSensor= sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        if(humidSensor!=null)
        {
            sensorManager.registerListener(this, humidSensor, 1000);
        }
        else
        {
            Constants.humidity="X";

        }

    }

    public void onSensorChanged(SensorEvent event) {
        try {
            int currType = event.sensor.getType();
            if(currType== Sensor.TYPE_AMBIENT_TEMPERATURE)
            {
                Constants.temperature="temperature: "+event.values[0];

            }
            else if(currType== Sensor.TYPE_RELATIVE_HUMIDITY)
            {
                Constants.humidity= "humidity: "+event.values[0];

            }


        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * Callback method which will be called by the system when the sensor
     * accuracy changed.
     * @param sensor
     * @param accuracy
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public void startService(View view) {
        Constants.experimentType="normal";
        txt = (TextView) findViewById(R.id.textView2);
        txt.setText("Service is Running!!!!");
        startService(new Intent(getBaseContext(), MyService.class));
       // wk.release();
        if(wk.isHeld())
        {
            wk.release();
        }
        wk.acquire();
        startButton.setEnabled(false);
        endButton.setEnabled(true);
        editButton.setEnabled(false);
        humanButton.setEnabled(false);
        SharedPreferences.Editor myEditor= pref.edit();
        myEditor.putString("start", "yes");
        myEditor.commit();

    }


    public void startRepeatingTimer(View view) {
        Context context = this.getApplicationContext();
        if(alarm != null){
            alarm.SetAlarm(context);
        }else{
            Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
        }

        startButton.setEnabled(false);
        endButton.setEnabled(true);
        editButton.setEnabled(false);
        humanButton.setEnabled(false);
        SharedPreferences.Editor myEditor= pref.edit();
        myEditor.putString("start", "yes");
        myEditor.commit();
    }

    public void cancelRepeatingTimer(View view){
        Context context = this.getApplicationContext();
        if(alarm != null){
            alarm.CancelAlarm(context);
        }else{
            Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
        }

       // txt.setText("Service is stopped!!!!");
        startButton.setEnabled(true);
        endButton.setEnabled(false);
        editButton.setEnabled(true);
        humanButton.setEnabled(true);

        SharedPreferences.Editor myEditor= pref.edit();
        myEditor.putString("start", "no");
        myEditor.commit();
        txt = (TextView) findViewById(R.id.textView2);
        txt.setText("Service is Stopped!!!!");
    }


    // Method to stop the service
    public void stopService(View view) {

        try {
            txt.setText("Service is stopped!!!!");
            startButton.setEnabled(true);
            endButton.setEnabled(false);
            editButton.setEnabled(true);
            humanButton.setEnabled(true);

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
                finish();

            }
        });

        humanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.experimentType="human";
                txt = (TextView) findViewById(R.id.textView2);
                txt.setText("Service is Running!!!!");
               // startService(new Intent(getBaseContext(), MyService.class));
                //wk.acquire();

              //  Context context = this.getApplicationContext();
                if(alarm != null){
                    alarm.SetAlarm(textCon);
                }else{
                    Toast.makeText(textCon, "Alarm is null", Toast.LENGTH_SHORT).show();
                }


                startButton.setEnabled(false);
                endButton.setEnabled(true);
                editButton.setEnabled(false);
                humanButton.setEnabled(false);
                SharedPreferences.Editor myEditor= pref.edit();
                myEditor.putString("start", "yes");
                myEditor.commit();

            }
        });



    }
}
