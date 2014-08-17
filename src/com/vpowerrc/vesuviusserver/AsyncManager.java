package com.vpowerrc.vesuviusserver;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

public class AsyncManager {
	
	public static Context appContext;
	
	public static void setContext(Context appContext) {
		AsyncManager.appContext = appContext;
		
	}
	
	public static void afterUnzipVesuvius(ProgressDialog progressBar, final ProgressDialog progressDialog){					
		
		new Thread(new Runnable() {
		       
			public void run() {   
				
				Server.getInstance().copyFiles();
				Server.getInstance().setPermission();		
				
				Utilities.writeSahanaConf();		
				Utilities.convertHtacessToLighttpd();
				
				try {
					if (!Server.getInstance().isServerRunning()){
						Server.getInstance().start();	        
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				try {
					while (!Server.getInstance().isServerRunning()) {    	                           
					    Thread.sleep(100); 				    
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 		
									
				Utilities.importDatabase();
				Log.e(Server.TAG, "php script processed");
				((Activity) appContext).runOnUiThread(new Runnable() {  
                    @Override
                    public void run() {
                    	progressDialog.dismiss();	        	
	        	    }
                });		            
	        }
		}).start();
		
		
	}
	public static void afterUnzipServer(ProgressDialog progressBar, ProgressDialog progressDialog){		 	
				
		if(!Utilities.vesuviusInstalled()){    	
			UnzipperAsync vesuviusUnzipper = new UnzipperAsync(appContext, progressBar, 50, "UnzipVesuvius");		
			vesuviusUnzipper.execute("vesuvius.zip", "assets", Utilities.getHttpDirectory() + "/www/");
		}
		else{
			progressDialog.dismiss();
		}	
	}
	
	public static void afterDownloadVesuvius(ProgressDialog progressBar, ProgressDialog progressDialog){		 	
		
		Log.e(Server.TAG, "downloaded");	
		
		if (progressBar.getProgress() == 50){
			UnzipperAsync vesuviusUnzipper = new UnzipperAsync(appContext, progressBar, 50, "UnzipDownloadedVesuvius");		
			vesuviusUnzipper.execute("vesuvius-master.zip", Utilities.getHttpDirectory() + "/www/", Utilities.getHttpDirectory() + "/www/");
		}
		
	}
	
	public static void afterUnzipDownloadedVesuvius(ProgressDialog progressBar, ProgressDialog progressDialog){		
		
		
		File srcFile = new File(Utilities.getHttpDirectory() + "/www/vesuvius");  
		File destFile = new File(Utilities.getHttpDirectory() + "/www/vesuvius-old");  		       
		boolean success = srcFile.renameTo(destFile);  		       
		if (success){  
			srcFile = new File(Utilities.getHttpDirectory() + "/www/vesuvius-master");  
			destFile = new File(Utilities.getHttpDirectory() + "/www/vesuvius");  			       
			success = srcFile.renameTo(destFile);			
			if (success){  
				Utilities.writeSahanaConf();
				File file = new File(Utilities.getHttpDirectory() + "/www/vesuvius-master.zip");
				file.delete();
			}
			else{  
				srcFile = new File(Utilities.getHttpDirectory() + "/www/vesuvius-old");  
				destFile = new File(Utilities.getHttpDirectory() + "/www/vesuvius");  				       
				success = srcFile.renameTo(destFile);  
			} 			        
		}  
		else{  
			        
		} 
		progressDialog.dismiss();
	}
}
