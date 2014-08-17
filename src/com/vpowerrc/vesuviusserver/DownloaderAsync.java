package com.vpowerrc.vesuviusserver;

import java.io.File;

import android.app.Activity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

class DownloaderAsync extends android.os.AsyncTask<String, Integer, String> {
	
	private Context appContext;
	private ProgressDialog progressBar;	
	private ProgressDialog progressDialog;		
	private Integer percentage;
	private String methodName;
	private PowerManager.WakeLock wakeLock;
	
	
	public DownloaderAsync(Context appContext,ProgressDialog progressBar, Integer percentage, String methodName) {
		// TODO Auto-generated constructor stub
		this.appContext = appContext;		
		this.progressBar = progressBar;
		this.percentage = percentage;
		this.methodName = methodName;
		
	}

		
	@Override
	protected void onPreExecute(){
	super.onPreExecute();
	   //checkInternetConenction();
	   PowerManager pm = (PowerManager) appContext.getSystemService(Context.POWER_SERVICE);
       wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,getClass().getName());
       wakeLock.acquire();
       
	}
	
	@Override	
    protected String doInBackground(String... params) {
		
		String link = params[0];
		String fileLocation = params[1];
				
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
        	
            URL url = new URL(link);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();
            
            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(fileLocation);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                	onProgressUpdate((int) (total * percentage / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }
	
				
	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		Integer progressbarValue = values[0];
		
		progressBar.setProgress(values[0]);		
        
		if (progressbarValue == 100){				
				progressBar.cancel();	
				progressDialog = ProgressDialog.show(appContext, "Please wait","processing", true);			
		}
	}
	
	@Override
	protected void onPostExecute(String result) {
		
		wakeLock.release();
        
        if (result != null){
            Toast.makeText(appContext,"Download error : no internet connection ", Toast.LENGTH_LONG).show();
            progressBar.cancel();
        }
        else{
            //Toast.makeText(appContext,"File downloaded", Toast.LENGTH_SHORT).show();
        }
        
		try{		
			
			Method method = Class.forName(AsyncManager.class.getName()).getMethod("after"+methodName, ProgressDialog.class, ProgressDialog.class);
		    method.invoke(null, progressBar, progressDialog);
			
			
		}catch(Exception ex){
			ex.printStackTrace();
		}		
		
	}

	
	
}

