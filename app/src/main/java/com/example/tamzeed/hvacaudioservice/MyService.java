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
    String url= "http://s200.bcn.ufl.edu/HVAC/fileUp.php";
    Timer timer = new Timer();
    int i=0;
    String str;
    String s="";
    MediaRecorder mediaRecorder;
    String filePath="";
    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }
//    public MyService()
//    {
//        pref= getSharedPreferences("myPref", Context.MODE_PRIVATE);
//        str= pref.getString("id",null);
//    }
    public void recording()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String currentDateandTime = sdf.format(new Date());
        s= "/"+Constants.deviceName+"_recordings_"+currentDateandTime+".3gp";

        Toast.makeText(MyService.this,"recorded: "+s, Toast.LENGTH_LONG).show();
        mediaRecorder= new MediaRecorder();
        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + s;
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(filePath);
        try
        {
            mediaRecorder.prepare();
            mediaRecorder.start();
        }catch (Exception e)
        {
            e.printStackTrace();
        }


    }
//    @Override
//    public int onStartCommand(Intent intent,int flags,int startId)
//    {
//        recording();
//
//        Toast.makeText(MyService.this, "Service started", Toast.LENGTH_LONG).show();
//        final Handler handler = new Handler();
//        Timer timer = new Timer();
//        TimerTask doAsynchronousTask = new TimerTask() {
//            @Override
//            public void run() {
//                handler.post(new Runnable() {
//                    @SuppressWarnings("unchecked")
//                    public void run() {
//                        try {
//                            dest();
//                        }
//                        catch (Exception e) {
//                            // TODO Auto-generated catch block
//                        }
//                    }
//                });
//            }
//        };
//        timer.schedule(doAsynchronousTask,  60000);
//
//
//        return START_STICKY;
//    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId)
    {
        recording();

        Toast.makeText(MyService.this, "Service started", Toast.LENGTH_LONG).show();
        final Handler handler = new Handler();

        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @SuppressWarnings("unchecked")
                    public void run() {
                        try {
                            dest();
                        }
                        catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask,60000,  60000);


        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        timer.cancel();
        super.onDestroy();
        Toast.makeText(MyService.this,"Service destroyed for ever ", Toast.LENGTH_LONG).show();



    }

//    public void dest()
//    {
//        super.onDestroy();
//
//        mediaRecorder.stop();
//        mediaRecorder.reset();
//        i++;
//        Toast.makeText(MyService.this,"Service destroyed: "+i, Toast.LENGTH_LONG).show();
//        startService(new Intent(getBaseContext(), MyService.class));
//    }


    public void dest()
    {
       // super.onDestroy();

        mediaRecorder.stop();
        mediaRecorder.reset();

        Constants.fileName=s;
       new sendFile().execute(url);

        recording();
        //startService(new Intent(getBaseContext(), MyService.class));
    }

}

