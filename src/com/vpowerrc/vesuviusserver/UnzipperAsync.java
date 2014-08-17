package com.vpowerrc.vesuviusserver;

import java.io.File;

import android.app.Activity;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

class UnzipperAsync extends android.os.AsyncTask<String, String, Void> {
	
	private Context appContext;
	private ProgressDialog progressBar;	
	private ProgressDialog progressDialog;		
	private Integer percentage;
	private String methodName;
	
	
	public UnzipperAsync(Context appContext,ProgressDialog progressBar, Integer percentage, String methodName) {
		// TODO Auto-generated constructor stub
		this.appContext = appContext;		
		this.progressBar = progressBar;
		this.percentage = percentage;
		this.methodName = methodName;
		Log.e(Server.TAG, methodName + "111");
	}
	
	protected void onPreExecute(){
				
	}
	
	@Override
	protected Void doInBackground(String... params) {			
		
		//String serverFile = params[0];
		//String serverFileLocation = params[1];
		
		String fileName = params[0];
		String fileLocation = params[1];
		String unzipLocation = params[2];
		
		long totalSize=(long) 0;
		long currentSize=(long) 0;
			
		Integer initialProgressbarValue = progressBar.getProgress();	
		
		try {		
			dirChecker(unzipLocation, "");			
						
			InputStream is;
			
			if (fileLocation == "assets")
				is = appContext.getAssets().open(fileName);
			else
				is = new FileInputStream(fileLocation+fileName);
			
			ZipInputStream zin = new ZipInputStream(is);				
		    ZipEntry ze = null; 		  	    
		    
		    while ((ze = zin.getNextEntry()) != null) {
		    	if (ze.isDirectory()) {
					dirChecker(unzipLocation, ze.getName());
				} 
				else {
					totalSize+=ze.getSize();					
					zin.closeEntry(); 
				}	    		    	
		    }		  
		    zin.close();		   
		    
		    
		    if (fileLocation == "assets")
				is = appContext.getAssets().open(fileName);
			else
				is = new FileInputStream(fileLocation+fileName);
		    
		    zin = new ZipInputStream(is);   
		   
			while ((ze = zin.getNextEntry()) != null) {											
				if (ze.isDirectory()) {
					dirChecker(unzipLocation, ze.getName());
				} 
				else {
					FileOutputStream fout = new FileOutputStream(unzipLocation+ ze.getName());					
					
					byte[] buffer = new byte[4096 * 10];
					int length = 0;
					while ((length = zin.read(buffer)) != -1) {						
						
						currentSize+=length;							
						fout.write(buffer, 0, (int) length);							
						publishProgress("extracting",""+(int) ((( currentSize * percentage) / totalSize) + initialProgressbarValue));		
						
					}							
					zin.closeEntry();
					fout.close();
				}				
			}
			zin.close();			
					
			Log.e(Server.TAG, methodName + " unzipping finished");
		
		} catch (java.lang.Exception e) {
			publishProgress("error","0");
		}
	
		return null;
	}
	
	@Override
	protected void onProgressUpdate(String... values) {
		
		Integer progressbarValue = Integer.parseInt(values[1]);
		
		progressBar.setProgress(progressbarValue);
		
		if (progressbarValue == 100){				
				progressBar.cancel();	
				progressDialog = ProgressDialog.show(appContext, "Please wait","processing", true);			
		}
	}
	
	@Override
	protected void onPostExecute(Void result) {
		
		try{					
			Method method = Class.forName(AsyncManager.class.getName()).getMethod("after"+methodName, ProgressDialog.class, ProgressDialog.class);
		    method.invoke(null, progressBar, progressDialog);	
		  
			
		}catch(Exception ex){
			ex.printStackTrace();
		}		
		super.onPostExecute(result);
	}
	
	private void dirChecker(String location,String dir) {
		File f = new File(location + dir);
	
		if (!f.isDirectory()) {
			f.mkdirs();
		}
	}
	
	
}

