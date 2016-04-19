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
import java.text.SimpleDateFormat;
import java.util.Date;

import android.telephony.*;
public class MainActivity extends AppCompatActivity implements SensorEventListener {


    private long lastUpdate = 0;
    private AlarmBroadcastReciever alarm;
    SensorManager sensorManager;


    Sensor tempSensor,humidSensor,acceleroSensor,magnetSensor,gyroscopeSensor,barometerSensor;


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



        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        Constants.deviceName="DEVICE_"+telephonyManager.getDeviceId();
        Log.d("DEVICE:", Constants.deviceName);

        textCon= this.getApplicationContext();

       // in++;


        alarm = new AlarmBroadcastReciever();
        pref= getSharedPreferences("myPref", Context.MODE_PRIVATE);
        //Constants.deviceName= pref.getString("id", null);
        txt = (TextView) findViewById(R.id.textView);
        txt.setText("DEVICE: " + Constants.deviceName);
       // txt.setText("Service is stopped!!!!"+in);
        powerM= (PowerManager) getSystemService(POWER_SERVICE);
        wk = powerM.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        startButton= (Button) findViewById(R.id.button1);
        endButton= (Button) findViewById(R.id.button2);
       // editButton= (Button) findViewById(R.id.editButton);
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
           // editButton.setEnabled(false);
            humanButton.setEnabled(false);


        }
        sensorInitializer();
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
        acceleroSensor= sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetSensor= sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gyroscopeSensor= sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        barometerSensor= sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        if(tempSensor!=null)
        {
            sensorManager.registerListener(this, tempSensor, 1000);

        }
        else
        {

            Constants.temperature="temperature: -9999";
=======
            Constants.temperature="-9999";
>>>>>>> 7331eab4c60c6838ee3e0170c115adeefeaddfd0
        }

        humidSensor= sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        if(humidSensor!=null)
        {
            sensorManager.registerListener(this, humidSensor, 1000);
        }
        else
        {

            Constants.humidity="humidity: -9999";


        }
        if(acceleroSensor!=null)
        {
            sensorManager.registerListener(this,acceleroSensor,1000);

        }
        else
        {


                Constants.acceloString="accelreometer: -9999";
        }

        if(magnetSensor!=null)
        {
            sensorManager.registerListener(this,magnetSensor,1000);

        }
        else
        {
                Constants.magnet="-9999";

        }

        if(gyroscopeSensor!=null)
        {
            sensorManager.registerListener(this,gyroscopeSensor,1000);


                
        }
        else
        {
            Constants.gyroString="-9999";
        }



        if(magnetSensor!=null)
        {
            sensorManager.registerListener(this,magnetSensor,1000);

        }
        else
        {
                Constants.magnet="magnet: -9999";

        }

        if(gyroscopeSensor!=null)
        {
            sensorManager.registerListener(this,gyroscopeSensor,1000);


        }
        else
        {
            Constants.gyroString="gyroSensor: -9999";
        }

        if(barometerSensor!=null)
        {
            sensorManager.registerListener(this,barometerSensor,1000);
        }
        else
        {

            Constants.baroString="barometer: -9999";

        }



    }

    public void onSensorChanged(SensorEvent event) {
        long curTime = System.currentTimeMillis();
        long diff=curTime - lastUpdate;
        Constants.acceloString="accelreometer: ";
        Constants.magnet="magnet: ";

        Constants.gyroString="gyroSensor: ";

        boolean flag=false;

        try {
            int currType = event.sensor.getType();
            if(currType== Sensor.TYPE_AMBIENT_TEMPERATURE)
            {
                flag=true;
                Constants.temperature="temperature: "+event.values[0];

            }
            else if(currType== Sensor.TYPE_RELATIVE_HUMIDITY)
            {
                flag=true;
                Constants.humidity= "humidity: "+event.values[0];

            }

            else if(currType== Sensor.TYPE_ACCELEROMETER)
            {
                if ((curTime - lastUpdate) > 1000)
                {

                    diff=curTime - lastUpdate;
                    lastUpdate = curTime;


                    flag=true;

                    Constants.acceloString+="["+event.values[0]+" "+event.values[1]+" "+event.values[2]+"]";


                }
            }

            else if(currType== Sensor.TYPE_MAGNETIC_FIELD)
            {
                if ((curTime - lastUpdate) > 1000)
                {
                    diff=curTime - lastUpdate;
                    lastUpdate = curTime;
                    flag=true;


                    Constants.magnet+="["+event.values[0]+" "+event.values[1]+" "+event.values[2]+"]";



                }
            }

            else if(currType== Sensor.TYPE_GYROSCOPE)
            {
                if((curTime - lastUpdate) > 1000)
                {
                    diff=curTime - lastUpdate;
                    lastUpdate = curTime;
                    flag=true;

                    Constants.gyroString+="["+event.values[0]+" "+event.values[1]+" "+event.values[2]+"]";

                }

            }

            else if(currType== Sensor.TYPE_PRESSURE)
            {
                flag=true;
                Constants.baroString="barometer: "+event.values[0];
            }



            if(flag==true)
            {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                String currentDateandTime = sdf.format(new Date());

                String str= currentDateandTime+" "+Constants.temperature+"  "+Constants.humidity+" "+Constants.baroString+" "
                        +Constants.acceloString+"  "+Constants.magnet+" "+Constants.gyroString;

                Constants.list.add(str);
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
      //  editButton.setEnabled(false);
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
       // editButton.setEnabled(false);
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
       // editButton.setEnabled(true);
        humanButton.setEnabled(true);

        SharedPreferences.Editor myEditor= pref.edit();
        myEditor.putString("start", "no");
        myEditor.commit();

    }


    // Method to stop the service
    public void stopService(View view) {

        try {
            txt.setText("Service is stopped!!!!");
            startButton.setEnabled(true);
            endButton.setEnabled(false);
           // editButton.setEnabled(true);
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
//        editButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent= new Intent(MainActivity.this, Editpage.class);
//                startActivity(intent);
//                finish();
//
//            }
//        });

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
               // editButton.setEnabled(false);
                humanButton.setEnabled(false);
                SharedPreferences.Editor myEditor= pref.edit();
                myEditor.putString("start", "yes");
                myEditor.commit();

            }
        });



    }
}
