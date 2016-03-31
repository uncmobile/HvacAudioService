package com.example.tamzeed.hvacaudioservice;

/**
 * Created by Tamzeed on 3/28/16.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
public class sendFile extends AsyncTask<String, Integer, String> {

    @Override
    protected String doInBackground(String... params) {
        String uri = params[0];
        try {

            String address;
            MultipartEntity entity;
            File f;
            FileBody fb;
            entity = new MultipartEntity(
                    HttpMultipartMode.BROWSER_COMPATIBLE);
            address = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + Constants.fileName;
            f = new File(address);
            fb = new FileBody(f, "application/octect-stream");
            entity.addPart("fileUp", fb);
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uri);

            httppost.setEntity(entity);
            HttpResponse response = httpclient.execute(httppost);




            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
            StringBuffer stringBuffer = new StringBuffer("");
            String line = "";
            String LineSeparator = System.getProperty("line.separator");
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + LineSeparator);
            }
            bufferedReader.close();

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

        Log.d("RESPONSEXXXXXX: ", result);

    }
}
