package com.vpowerrc.vesuviusserver;



import java.io.IOException;

import com.omt.remote.util.net.WifiApControl;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;


public class HomeFragment extends Fragment {
	public ProgressDialog progressBar;
	public static String TAG = "Vesuvius Server";	
	public void addProgresBar(String message) {
			
			
			
			progressBar = new ProgressDialog(getActivity());
			progressBar.setMessage(message+" ...");
			progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressBar.setProgress(0);
			progressBar.setProgressNumberFormat(null);
			progressBar.setCancelable(false);
			progressBar.setCanceledOnTouchOutside(false);
			progressBar.show();
			
	
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
        // inflate the layout for this fragment    
    	final View view = inflater.inflate(R.layout.fragment_home, container, false);    	
    	  
    	ToggleButton toggle = (ToggleButton) view.findViewById(R.id.serverToggleButton);    	
    	
    	//Server.afterInstall();
    	//Vesuvius.afterInstall();
    	
    	// create notification  	
		final Intent notificationIntent = new Intent(getActivity(), VesuviusNotificationService.class);

    	try {
			if(Server.isServerRunning()){				
				toggle.setChecked(true);
				
				final WifiApControl apControl = Server.turnOnOffHotspot(true);  
	        	
	        	
	        	Thread t = new Thread() {
	                @Override
	                
	                public void run() {
	                    try {
	                        //check if hotspot started
	                        while (!apControl.isWifiApEnabled()) {    	                           
	                            Thread.sleep(500);                     
	                        }
	                        if(apControl.isWifiApEnabled()){
	                        final String ipAddress = apControl.getIpAddress();
	                        Log.e(TAG, ipAddress);
	                        	((Activity)getActivity()).runOnUiThread(new Runnable() {  
		                            @Override
		                            public void run() {
		                            	TextView text = (TextView) view.findViewById(R.id.textView2);
		    	                        text.setText("Vesuvius is running on \n\nhttp://"+ipAddress+":"+Server.serverPort+"/vesuvius");
		                            }
		                        });
	                        
	                        }

	                    } catch (Exception e) {
	                    }
	                }
	            };
	            t.start();	            
				Log.e(TAG, "running");			
				
				// show notification 
				getActivity().startService(notificationIntent);

			}
		} catch (IOException e) {			
			e.printStackTrace();
		}
    	
    	
    	toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    	    
    		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    	        if (isChecked) {
    	            // The toggle is enabled
    	        	if(!Vesuvius.checkIfInstalled()){
    	        		Log.e(TAG, "Instsll");
    	    			addProgresBar("Installing Vesuvius");    	    			
    	    			Vesuvius.install(progressBar);
    	    		}    	        	
    	        	Log.e(TAG, "Server start");
    	        	Server.start();    	        	
    	        	// show notification    	        	       	
    	        	
    	        	final WifiApControl apControl = Server.turnOnOffHotspot(true);     	        	
    	        	
    	        	Thread t = new Thread() {
    	                @Override
    	                public void run() {
    	                    try {
    	                        //check if hotspot started
    	                        while (!apControl.isWifiApEnabled()) {    	                           
    	                            Thread.sleep(500);                     
    	                        }
    	                        if(apControl.isWifiApEnabled()){
    	                        	final String ipAddress = apControl.getIpAddress();
    		                        Log.e(TAG, ipAddress);
    		                        ((Activity)getActivity()).runOnUiThread(new Runnable() {  
    		                            @Override
    		                            public void run() {
    		                            	TextView text = (TextView) view.findViewById(R.id.textView2);
    		    	                        text.setText("Vesuvius is running on \n\nhttp://"+ipAddress+":"+Server.serverPort+"/vesuvius");
    		    	                        getActivity().startService(notificationIntent);    	 
    		                            }
    		                        });
    	                        }

    	                    } catch (Exception e) {
    	                    }
    	                }
    	            };
    	            t.start();
    	        	
    	        
    	        } else {
    	            // The toggle is disabled
    	        	
    	        	TextView text = (TextView) view.findViewById(R.id.textView2);
                    text.setText("");
    	        	Server.turnOnOffHotspot(false);  
    	        	Log.e(TAG, "Server stop");
    	        	Server.stop();
    	        	
                    
                    // hide notification
                    getActivity().stopService(notificationIntent);
                    
    	        }
    	    }
    		
    	
    		
    	});
    	
    	
    	
    	
           	
    	
    	return view;
    
    }
	
	
}
