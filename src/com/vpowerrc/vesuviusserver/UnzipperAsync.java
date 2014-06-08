package com.vpowerrc.vesuviusserver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

class UnzipperAsync extends android.os.AsyncTask<String, String, Void> {
	
	public Context appContext;
	public ProgressDialog progressBar;
	public String className;
	Class loadedList = null;
	
	public UnzipperAsync(Context appContext,ProgressDialog progressBar,String className) {
		// TODO Auto-generated constructor stub
		this.appContext = appContext;
		this.progressBar = progressBar;
		this.className = className;
	}

	@Override
	protected Void doInBackground(String... params) {			
		
		String zipFile = params[0];
		String location = params[1];
		long totalSize=(long) 0;
	
		
		
		
		try {		
			dirChecker(location, "");			
			
			
			InputStream is = appContext.getAssets().open(zipFile);
			ZipInputStream zin = new ZipInputStream(is);				
		    ZipEntry ze = null; 
		  	    
		   // totalSize = (long) is.available();		    
		    while ((ze = zin.getNextEntry()) != null) {
		    	if (ze.isDirectory()) {
					dirChecker(location, ze.getName());
				} 
				else {
					totalSize+=ze.getSize();
					
					zin.closeEntry(); 
				}
		    		    	
		    	
		    }
		  
		   zin.close();
		   zin = new ZipInputStream(appContext.getAssets().open(zipFile));	      			     
		  
		   long currentSize=(long) 0;
		   
			while ((ze = zin.getNextEntry()) != null) {											
				if (ze.isDirectory()) {
					dirChecker(location, ze.getName());
				} 
				else {
					FileOutputStream fout = new FileOutputStream(location+ ze.getName());
					
					
					byte[] buffer = new byte[4096 * 10];
					int length = 0;
					while ((length = zin.read(buffer)) != -1) {						
						
						currentSize+=length;							
						fout.write(buffer, 0, (int) length);							
						publishProgress("extracting",""+(int) (( currentSize * 100) / totalSize));						
						
					}		
					
					zin.closeEntry();
					fout.close();
				}
				//Log.v("File",currentSize+" " +totalSize);
			}
			
			publishProgress("done","100");
			
			zin.close();
		
		} catch (java.lang.Exception e) {
			publishProgress("error","0");
		}
	
		return null;
	}
	
	@Override
	protected void onProgressUpdate(String... values) {
		
		
		progressBar.setProgress(Integer.parseInt(values[1]));
		
		if(values[0]=="done"){
			progressBar.cancel();				
		}

	}
	
	@Override
	protected void onPostExecute(Void result) {
		try{
			//create instance of from dynamic class name
			Class cls = Class.forName(className);
			Object obj = cls.newInstance();
	 
			//call afterInstall method 
			Method method = cls.getDeclaredMethod("afterInstall");
			method.invoke(obj, null);
		
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

