package com.example.tamzeed.hvacaudioservice;

/**
 * Created by Tamzeed on 4/3/16.
 */
import android.media.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.*;
import android.util.Log;

/**
 * Created by Tamzeed on 4/3/16.
 */
public class AudioRecordClass {
    private long lastFilenum = 0;
    // private byte audioData[] = null;
    //private Thread recordingThread = null;
    String fileName="";
    String s="";
    AudioRecord myRecorder;
    int bufferSize;
    byte audioData[];
    boolean isrunning = false;
    Thread recordingThread = null;
    private static final int RECORDER_BPP = 16;
    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final String AUDIO_RECORDER_FOLDER = "MicReader";
    private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
    private static final int CHANNELS = 1;

    public void startRecord()
    {
        if(isrunning==false)
        {
            bufferSize= AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
            audioData =new byte[bufferSize];

            Log.v("SN", "After builder");
            myRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, 10*bufferSize);

            myRecorder.startRecording();
            Log.v("SN", "Before builder");
            recordingThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    recording();
                }
            },"AudioRecorder Thread");

            recordingThread.start();
        }

    }

    public void recording()
    {
        long t1, t2;
        int bn;
        try {
            isrunning = true;
            FileOutputStream fos = new FileOutputStream(getTempFilename());
            t1 = System.currentTimeMillis();
            while (isrunning==true)
            {
                bn = myRecorder.read(audioData, 0, bufferSize);
                if(bn != AudioRecord.ERROR_INVALID_OPERATION) {
                    fos.write(audioData);
                }
                t2 = System.currentTimeMillis();
                if(t2-t1 >= 60000) { //e.g., 30 sec
                    isrunning = false;
                }
            }
            fos.close();

            Log.d("TAG", "Stoppinggg");
            myRecorder.stop();
            myRecorder.release();
            copyWaveFile(s,getFilename());

            //String url= "http://s200.bcn.ufl.edu/HVAC/fileUp.php";
            new sendFile().execute(this.fileName);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private String getFilename(){
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdirs();
        }
       // lastFilenum = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String currentDateandTime = sdf.format(new Date());
        s= "/"+Constants.deviceName+"_recordings_"+currentDateandTime;
        Log.d("file name",file.getAbsolutePath() + "/" + s+ AUDIO_RECORDER_FILE_EXT_WAV);
        this.fileName=s+ AUDIO_RECORDER_FILE_EXT_WAV;
        return (file.getAbsolutePath() + "/" + this.fileName);
    }


    private String getTempFilename(){
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdirs();
        }

        File tempFile = new File(filepath,AUDIO_RECORDER_TEMP_FILE);

//        if(tempFile.exists())
//            tempFile.delete();

        lastFilenum = System.currentTimeMillis();
        s=file.getAbsolutePath() + "/" +lastFilenum+ AUDIO_RECORDER_TEMP_FILE;
        return s;
    }


    private void copyWaveFile(String inFilename,String outFilename) {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = RECORDER_SAMPLERATE;
        int channels = CHANNELS;
        long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels/8;

        byte[] data = new byte[bufferSize];

        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;



            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);

            while(in.read(data) != -1){
                out.write(data);
            }

            in.close();
            out.close();

            Log.d("FILENAME:: ",Constants.fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void WriteWaveFileHeader(
            FileOutputStream out, long totalAudioLen,
            long totalDataLen, long longSampleRate, int channels,
            long byteRate) throws IOException {

        byte[] header = new byte[44];

        header[0] = 'R';  // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';  // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;  // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (channels * RECORDER_BPP / 8);  // block align
        header[33] = 0;
        header[34] = RECORDER_BPP;  // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }


}
