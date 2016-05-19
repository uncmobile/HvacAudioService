package com.example.tamzeed.hvacaudioservice;

import android.util.Log;

public class HvacMonitorThread extends Thread {

    boolean isMonitoring = false;

    HvacMonitorThread() {
        isMonitoring = false;
    }

    @Override
    public void run() {
        isMonitoring = true;
        while(isMonitoring) {
            Log.v("SN", "Hvac Monitoring Thread Repeated Task Fired ...... ");
            new AudioRecordClass().startRecord();
            try {
                Thread.sleep(Constants.AUDIO_FILE_DURATION_MS);
            }catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void stopMonitoring() {
        isMonitoring = false;
    }

}