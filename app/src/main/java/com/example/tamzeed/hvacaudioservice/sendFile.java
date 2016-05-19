package com.example.tamzeed.hvacaudioservice;

/**
 * Created by Tamzeed on 3/28/16.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

public class sendFile extends AsyncTask<String, Integer, String> {

    String fileNameWithDateTime, rawFileFullPath, type;

    @Override
    protected String doInBackground(String... params) {

        String uri = "http://s200.bcn.ufl.edu/HVAC/fileUp.php";
        fileNameWithDateTime = params[0];
        rawFileFullPath = params[2];

        Log.d("SN", "doInBackground(): " + params[0] + ", " + params[1] + ", " + params[2]);

        try {

            String address;
            MultipartEntity entity;
            File f;
            FileBody fb;
            type = params[1];

            if(params[1] == "sensor") {
                entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                address = Environment.getExternalStorageDirectory().getAbsolutePath()
                        +"/"+ "Notes" + "/" + fileNameWithDateTime;
                Log.v("SN", "address = " + address);
                f = new File(address);
                fb = new FileBody(f, "application/octect-stream");
                entity.addPart("fileText", fb);
            }
            else{
                entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                address = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/" + "MicReader" + "/" + fileNameWithDateTime;
                f = new File(address);
                Log.v("SN", "address = " + address);
                fb = new FileBody(f, "application/octect-stream");
                entity.addPart("fileUp", fb);
            }

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uri);

            httppost.setEntity(entity);
            HttpResponse response = httpclient.execute(httppost);

            Log.d("XXXXXX: ", "result");

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
            StringBuffer stringBuffer = new StringBuffer("");
            String line = "";
            String LineSeparator = System.getProperty("line.separator");
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + LineSeparator);
            }
            bufferedReader.close();
            Log.d("XXXXXX: ", "resusssssslt");

            return stringBuffer.toString();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return e.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    protected void onPostExecute(String result) {

        Log.d("RESPONSE: ", result);

        if(type != "sensor") {

            String filePathWithDateTime = Environment.getExternalStorageDirectory().getAbsolutePath()
                    +"/"+ "MicReader"+"/"+ fileNameWithDateTime;

            File file = new File(filePathWithDateTime);
            boolean deleted = file.delete();
            Log.d("del", filePathWithDateTime + " deleted = " + deleted);

            File file1 = new File(rawFileFullPath);
            boolean deleted1 = file1.delete();
            Log.d("del", rawFileFullPath + " deleted = " + deleted1);
        }
        else {
            String sensor_file_address = Environment.getExternalStorageDirectory().getAbsolutePath()
                    +"/"+ "Notes" + "/" + fileNameWithDateTime;
            File file2 = new File(sensor_file_address);
            boolean deleted2 = file2.delete();
            Log.d("del", sensor_file_address + " deleted = " + deleted2);
        }
    }
}
