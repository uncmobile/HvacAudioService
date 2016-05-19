package com.example.tamzeed.hvacaudioservice;

/**
 * Created by Tamzeed on 4/3/16.
 */
import android.media.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.os.*;
import android.util.Log;

public class AudioRecordClass {

    String fileDateTime = "";
    String rawFileFullPath = ""; //fixed file path for temporary audio recording.
    String fileNameWithDateTime = "";
    String fileAbsolutePathWithDateTime = "";

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
    private static final int CHANNELS = 1;

    private String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
    private ArrayList<String> sensorValues = new ArrayList<String>();
    Thread sensorRecordingThread = null;

    public void startRecord()
    {
        if(isrunning == false)
        {
            AUDIO_RECORDER_TEMP_FILE = System.currentTimeMillis() + ".raw"; //to avoid race condition on single raw file by consecutive Alarm tasks.
            sensorValues.clear();

            bufferSize= AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
            audioData =new byte[bufferSize];

            myRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, 10*bufferSize);

            myRecorder.startRecording();

            recordingThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    recording();
                }
            },"Audio Recorder Thread");

            sensorRecordingThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    sensorRecording();
                }
            },"Sensor Recorder Thread");

            sensorRecordingThread.start();
            recordingThread.start();
        }
    }

    public void sensorRecording()
    {
        for(int i = 0; i < Constants.AUDIO_FILE_DURATION_MS; i+=1000){

            sensorValues.add(Constants.ALL_SENSOR_STR);

            try {
                Thread.sleep(1000);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }

    public void recording()
    {
        long t1, t2;
        int bn;
        try {
            isrunning = true;

            rawFileFullPath = getTempFilename(); //it's /sdcard/MicReader/record_temp.raw
            FileOutputStream fos = new FileOutputStream(rawFileFullPath);

            t1 = System.currentTimeMillis();

            while (isrunning == true)
            {
                bn = myRecorder.read(audioData, 0, bufferSize);
                if(bn != AudioRecord.ERROR_INVALID_OPERATION) {
                    fos.write(audioData);
                }
                t2 = System.currentTimeMillis();
                if(t2-t1 >= Constants.AUDIO_FILE_DURATION_MS) { //60000 ms
                    isrunning = false;
                }
            }
            fos.close();

            myRecorder.stop();
            myRecorder.release();

            fileAbsolutePathWithDateTime = getFileAbsolutePath(); //also sets fileDateTime, fileNameWithDateTime
            copyWaveFile(rawFileFullPath, fileAbsolutePathWithDateTime); //from record_temp.raw to a datedfile
            new sendFile().execute(this.fileNameWithDateTime, Constants.experimentType, this.rawFileFullPath);

            sensorRecordingThread.join();
            sensorFile(fileDateTime);
            new sendFile().execute(Constants.deviceName + "_sensor_" + fileDateTime + ".txt", "sensor", this.rawFileFullPath);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sensorFile(String fileDateTimeParam)
    {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");

            if (!root.exists()) {
                root.mkdirs();
            }

            File gpxfile = new File(root, Constants.deviceName + "_sensor_" + fileDateTimeParam + ".txt");
            FileWriter writer = new FileWriter(gpxfile);

            for (int i = 0; i < sensorValues.size(); i++) {
                writer.append(sensorValues.get(i));
                writer.write(System.getProperty("line.separator"));
                writer.flush();
            }
            writer.write(System.getProperty("line.separator"));
            writer.flush();
            writer.close();
            //Constants.list.clear();
            sensorValues.clear();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getFileAbsolutePath() {

        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if(!file.exists()) {
            file.mkdirs();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String currentDateandTime = sdf.format(new Date());
        fileDateTime = currentDateandTime;

        if(Constants.experimentType == "human") {
            this.fileNameWithDateTime = Constants.deviceName + "_human_" +
                    currentDateandTime + AUDIO_RECORDER_FILE_EXT_WAV;
        }
        else {
            this.fileNameWithDateTime = Constants.deviceName + "_hvac_" +
                    currentDateandTime + AUDIO_RECORDER_FILE_EXT_WAV;
        }

        return (file.getAbsolutePath() + "/" + this.fileNameWithDateTime);
    }


    private String getTempFilename(){
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdirs();
        }

        return file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE;
    }


    private long copyWaveFile(String inFilename, String outFilename) {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = RECORDER_SAMPLERATE;
        int channels = CHANNELS;
        long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels/8;

        byte[] data = new byte[bufferSize];
        long ret = 0;

        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;

            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);

            ret = 0;
            while(in.read(data) != -1){
                out.write(data);
                ret++;
            }

            in.close();
            out.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ret = ret * bufferSize;
        Log.v("SN", "File Size Copied: " + ret);
        return ret;
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
