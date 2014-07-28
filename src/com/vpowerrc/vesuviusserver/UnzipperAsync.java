package com.vpowerrc.vesuviusserver;

import java.io.File;
import android.app.Activity;
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
	public ProgressDialog progressDialog;	
	public static String TAG = "Vesuvius Server";
	
	
	public UnzipperAsync(Context appContext,ProgressDialog progressBar) {
		// TODO Auto-generated constructor stub
		this.appContext = appContext;
		this.progressBar = progressBar;	
		
	}
	
	protected void onPreExecute(){
		progressBar = new ProgressDialog(appContext);
		progressBar.setMessage("Initializing Vesuvius server for the first time...");
		progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressBar.setProgress(0);
		progressBar.setProgressNumberFormat(null);
		progressBar.setCancelable(false);
		progressBar.setCanceledOnTouchOutside(false);
		progressBar.show();			
	}
	
	@Override
	protected Void doInBackground(String... params) {			
		
		String serverFile = params[0];
		String serverFileLocation = params[1];
		String vesuviusFile = params[2];
		String vesuviusFileLocation = params[3];
		long totalSize=(long) 0;
		long currentSize=(long) 0;
		
		
		
		try {		
			dirChecker(serverFileLocation, "");			
			InputStream is1 = appContext.getAssets().open(serverFile);
			ZipInputStream zin1 = new ZipInputStream(is1);				
		    ZipEntry ze1 = null; 		  	    
		   		    
		    while ((ze1 = zin1.getNextEntry()) != null) {
		    	if (ze1.isDirectory()) {
					dirChecker(serverFileLocation, ze1.getName());
				} 
				else {
					totalSize+=ze1.getSize();					
					zin1.closeEntry(); 
				}	    		    	
		    }		  
		    zin1.close();
		    
		    dirChecker(serverFileLocation, "");			
			InputStream is2 = appContext.getAssets().open(serverFile);
		    ZipInputStream zin2 = new ZipInputStream(is2);				
		    ZipEntry ze2 = null;
		    
		    if(!Utilities.vesuviusInstalled()){    			    					 		  	    
			   		    
			    while ((ze2 = zin2.getNextEntry()) != null) {
			    	if (ze2.isDirectory()) {
						dirChecker(serverFileLocation, ze2.getName());
					} 
					else {
						totalSize+=ze2.getSize();						
						zin2.closeEntry(); 
					}		  			    	
			    }		  
			    zin2.close();
		    }
		    
		    zin1 = new ZipInputStream(appContext.getAssets().open(serverFile));     		    
		   
			while ((ze1 = zin1.getNextEntry()) != null) {											
				if (ze1.isDirectory()) {
					dirChecker(serverFileLocation, ze1.getName());
				} 
				else {
					FileOutputStream fout = new FileOutputStream(serverFileLocation+ ze1.getName());					
					
					byte[] buffer = new byte[4096 * 10];
					int length = 0;
					while ((length = zin1.read(buffer)) != -1) {						
						
						currentSize+=length;							
						fout.write(buffer, 0, (int) length);							
						publishProgress("extracting",""+(int) (( currentSize * 100) / totalSize));						
						
					}							
					zin1.closeEntry();
					fout.close();
				}				
			}
			zin1.close();
			
			if(!Utilities.vesuviusInstalled()){
				zin2 = new ZipInputStream(appContext.getAssets().open(vesuviusFile));     
				 
				while ((ze2 = zin2.getNextEntry()) != null) {											
					if (ze2.isDirectory()) {
						dirChecker(vesuviusFileLocation, ze2.getName());
					} 
					else {
						FileOutputStream fout = new FileOutputStream(vesuviusFileLocation+ ze2.getName());					
						
						byte[] buffer = new byte[4096 * 10];
						int length = 0;
						while ((length = zin2.read(buffer)) != -1) {						
							
							currentSize+=length;							
							fout.write(buffer, 0, (int) length);							
							publishProgress("extracting",""+(int) (( currentSize * 100) / totalSize));						
							
						}							
						zin2.closeEntry();
						fout.close();
					}				
				}
				zin2.close();				
			}
			
			publishProgress("done","100");
			Log.e(TAG, "Installation finished");
		
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
			progressDialog = ProgressDialog.show(appContext, "Please wait","processing", true);
		}

	}
	
	@Override
	protected void onPostExecute(Void result) {
		try{				
			Server.getInstance().afterInstall(progressDialog);
			
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

