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
import android.view.WindowManager;
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
    //private AlarmBroadcastReciever alarm;

    SensorManager sensorManager;
    Sensor tempSensor, humidSensor, acceleroSensor, magnetSensor, gyroscopeSensor, barometerSensor, lightSensor;

    Context textCon;
    PowerManager powerM;
    SharedPreferences pref;
    PowerManager.WakeLock wk;
    TextView txt, sensortxt;
    MyService ms = new MyService();
    Button startButton, endButton, humanButton;

    HvacMonitorThread hvacMonitoringThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        Constants.deviceName = "D" + telephonyManager.getDeviceId();
        Log.d("DEVICE:", Constants.deviceName);

        textCon = this.getApplicationContext();
        //alarm = new AlarmBroadcastReciever();
        pref = getSharedPreferences("myPref", Context.MODE_PRIVATE);

        txt = (TextView) findViewById(R.id.textView);
        txt.setText(Constants.deviceName);

        sensortxt = (TextView) findViewById(R.id.textViewSensors);

        powerM = (PowerManager) getSystemService(POWER_SERVICE);
        wk = powerM.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag");

        startButton = (Button) findViewById(R.id.button1);
        endButton = (Button) findViewById(R.id.button2);
        humanButton = (Button) findViewById(R.id.button3);

        String strTemp = pref.getString("start", null);

        if(strTemp != "yes") {
            endButton.setEnabled(false);
        }
        else {
            startButton.setEnabled(false);
            humanButton.setEnabled(false);
        }

        hvacMonitoringThread = new HvacMonitorThread();

        sensorInitializer();
        addListener();
    }

    public void onDestroy()
    {
        super.onDestroy();
        if(wk.isHeld())
            wk.release();
        txt.setText("Service has stopped !!!!");
    }

    public void sensorInitializer()
    {
        sensorManager= (SensorManager) getSystemService(SENSOR_SERVICE);

        tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        acceleroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        humidSensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        barometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        String senlist = "[";

        if(tempSensor != null) {
            sensorManager.registerListener(this, tempSensor, 900);
            senlist += " TEMP ";
        }

        if(humidSensor != null) {
            sensorManager.registerListener(this, humidSensor, 900);
            senlist += " HUM ";
        }

        if(acceleroSensor!=null) {
            sensorManager.registerListener(this,acceleroSensor, 900);
            senlist += " ACC ";
        }

        if(magnetSensor != null) {
            sensorManager.registerListener(this,magnetSensor, 900);
            senlist += " MAG ";
        }

        if(gyroscopeSensor != null) {
            sensorManager.registerListener(this,gyroscopeSensor, 900);
            senlist += " GYRO ";
        }

        if(barometerSensor != null) {
            sensorManager.registerListener(this,barometerSensor, 900);
            senlist += " BARO ";
        }

        if(lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, 900);
            senlist += " LHT ";
        }

        senlist += "]";
        sensortxt.setText(senlist);
    }

    public void onSensorChanged(SensorEvent event) {

        try {
            int currType = event.sensor.getType();

            if(currType == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                Constants.temperature = "" + event.values[0];
            }
            else if(currType == Sensor.TYPE_RELATIVE_HUMIDITY) {
                Constants.humidity = "" + event.values[0];
            }
            else if(currType == Sensor.TYPE_ACCELEROMETER) {
                    Constants.acceloString = event.values[0] + "\t" + event.values[1] + "\t" + event.values[2];
            }
            else if(currType == Sensor.TYPE_MAGNETIC_FIELD) {
                    Constants.magnet = event.values[0] + "\t" + event.values[1] + "\t" + event.values[2];
            }
            else if(currType == Sensor.TYPE_GYROSCOPE) {
                    Constants.gyroString = event.values[0] + "\t" + event.values[1] + "\t" + event.values[2];
            }
            else if(currType == Sensor.TYPE_PRESSURE) {
                Constants.baroString = "" + event.values[0];
            }
            else if(currType == Sensor.TYPE_LIGHT) {
                Constants.lightString = "" + event.values[0];
            }

            /*We update the list of sensor values once every second*/
            if(System.currentTimeMillis() - lastUpdate >= 1000) {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM dd HH mm ss");
                String currentDateandTime = sdf.format(new Date());

                String str = currentDateandTime + "\t" + Constants.temperature + "\t" +
                        Constants.humidity + "\t" + Constants.baroString + "\t" +
                        Constants.acceloString + "\t" + Constants.magnet +
                        "\t" + Constants.gyroString + "\t" + Constants.lightString;

                Constants.ALL_SENSOR_STR = str;
                //Constants.list.add(str);
                lastUpdate = System.currentTimeMillis();
            }

        }catch (Exception e) {
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

    public void startRepeatingTimer(View view) {

        Context context = this.getApplicationContext();
        Constants.experimentType = "normal";

        /*if(alarm != null) { alarm.SetAlarm(context); }
        else {Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();}*/

        if(hvacMonitoringThread != null) {
            hvacMonitoringThread.stopMonitoring();
            hvacMonitoringThread = null;
        }
        hvacMonitoringThread = new HvacMonitorThread();
        hvacMonitoringThread.start();

        startButton.setEnabled(false);
        endButton.setEnabled(true);
        humanButton.setEnabled(false);

        SharedPreferences.Editor myEditor = pref.edit();
        myEditor.putString("start", "yes");
        myEditor.commit();

        Toast.makeText(context, "HVAC Monitor Started", Toast.LENGTH_LONG).show();
    }

    public void cancelRepeatingTimer(View view){
        Context context = this.getApplicationContext();

        /*if(alarm != null) {alarm.CancelAlarm(context);}
        else {Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();}*/
        if(hvacMonitoringThread != null) {
            hvacMonitoringThread.stopMonitoring();
            hvacMonitoringThread = null;
        }

        startButton.setEnabled(true);
        endButton.setEnabled(false);
        humanButton.setEnabled(true);

        SharedPreferences.Editor myEditor= pref.edit();
        myEditor.putString("start", "no");
        myEditor.commit();
        Toast.makeText(context, "HVAC Monitor Stopped", Toast.LENGTH_LONG).show();
    }

    public void addListener() {

        humanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Constants.experimentType = "human";

                /*if (alarm != null) {alarm.SetAlarm(textCon);}
                else {Toast.makeText(textCon, "Alarm is null", Toast.LENGTH_SHORT).show();}*/
                if(hvacMonitoringThread != null) {
                    hvacMonitoringThread.stopMonitoring();
                    hvacMonitoringThread = null;
                }
                hvacMonitoringThread = new HvacMonitorThread();
                hvacMonitoringThread.start();

                startButton.setEnabled(false);
                endButton.setEnabled(true);
                humanButton.setEnabled(false);

                SharedPreferences.Editor myEditor = pref.edit();
                myEditor.putString("start", "yes");
                myEditor.commit();
            }
        });
    }
}
