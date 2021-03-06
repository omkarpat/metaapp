package com.pict.metaappui.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by tushar on 4/11/15.
 */
public class postAsync extends AsyncTask<String, Integer, Integer> {

    public interface PostExecuteInterface {
        public void postExecute(int responseCode);
    }

    String response="";
    String TAG="postAsync";
    String displayMessage;
    ProgressDialog progressDialog;
    Activity activity;
    PostExecuteInterface postExecuteInterface;

    public postAsync(String displayMessage,Activity activity,PostExecuteInterface postExecuteInterface){
        this.displayMessage=displayMessage;
        this.activity=activity;
        this.postExecuteInterface=postExecuteInterface;
        progressDialog=new ProgressDialog(activity);
    }

    @Override
    protected Integer doInBackground(String... params) {
        // TODO Auto-generated method stub
        TAG = "postAsync";
        int no_params=Integer.parseInt(params[0]);
        Map<String, String> parameters = new HashMap<String, String>();
        int i=1;
        while(i<=no_params){
            parameters.put(params[i],params[i+1]);
            i=i+2;
        }
        String endpoint = params[i];
        int responseCode=postData(endpoint, parameters);
        return responseCode;
    }

    public int postData(String endpoint,Map<String, String> params) {
        int status=404;
        try {
            URL url;
            try {
                url = new URL(endpoint);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("invalid url: " + endpoint);
            }
            StringBuilder bodyBuilder = new StringBuilder();
            Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            // constructs the POST body using the parameters
            while (iterator.hasNext()) {
                Map.Entry<String, String> param = iterator.next();
                bodyBuilder.append(param.getKey()).append('=')
                        .append(param.getValue());
                if (iterator.hasNext()) {
                    bodyBuilder.append('&');
                }
            }
            String body = bodyBuilder.toString();
            Log.v(TAG, "Posting '" + body + "' to " + url);
            byte[] bytes = body.getBytes();
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
                //conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setFixedLengthStreamingMode(bytes.length);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                conn.setRequestProperty("Accept-Encoding", "");
                if (Build.VERSION.SDK != null && Build.VERSION.SDK_INT > 13) {
                    conn.setRequestProperty("Connection", "close");
                }
                conn.connect();
                // post the request
                OutputStream out = conn.getOutputStream();
                out.write(bytes);
                out.close();
                // handle the response
                status = conn.getResponseCode();
                if (status != 200) {
                    Log.i(TAG, "Post failed with error code " + status);
                } else {
                    Log.i(TAG, "Message sent to server with response code: " + status);
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                    Log.i(TAG, "Message sent to server with response message: " + response);

                }
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return status;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setMessage(displayMessage);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Integer responseCode) {
        super.onPostExecute(responseCode);
        postExecuteInterface.postExecute(responseCode);
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }

        if(responseCode==200){
            Toast.makeText(activity,"Success",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(activity,"Failure",Toast.LENGTH_SHORT).show();
        }
    }
}