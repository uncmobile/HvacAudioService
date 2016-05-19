package com.example.tamzeed.hvacaudioservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;
import 	java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Tamzeed on 3/21/16.
 */
public class MyService extends Service {

    SharedPreferences pref;
    String url = "http://s200.bcn.ufl.edu/HVAC/fileUp.php";
    Timer timer = new Timer();
    int i = 0;
    String str;
    String s = "";
    MediaRecorder mediaRecorder;
    String filePath = "";

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId)
    {
        Toast.makeText(MyService.this, "Service started", Toast.LENGTH_LONG).show();
        final Handler handler = new Handler();

        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @SuppressWarnings("unchecked")
                    public void run() {
                        try {
                            new AudioRecordClass().startRecord();
                        }
                        catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };

        timer.schedule(doAsynchronousTask, 0, Constants.TIMER_INTERVAL_MS); //60000 ms
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        timer.cancel();
        super.onDestroy();
        Toast.makeText(MyService.this, "Service destroyed for ever ", Toast.LENGTH_LONG).show();
    }

}

