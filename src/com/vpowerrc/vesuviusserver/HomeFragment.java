package com.vpowerrc.vesuviusserver;



import java.io.IOException;

import com.omt.remote.util.net.WifiApControl;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
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
    	
        //  Inflate the layout for this fragment    
    	View view = inflater.inflate(R.layout.fragment_home, container, false);    	
    	  
    	ToggleButton toggle = (ToggleButton) view.findViewById(R.id.serverToggleButton);    	
    	
    	Server.afterInstall();
    	Vesuvius.afterInstall();
    	
    	//set toggle button to true if server is alreayd running
    	try {
			if(Server.isServerRunning()){				
				toggle.setChecked(true);
				Log.e(TAG, "running");
				Server.stop();
				Server.start();
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
    	        	final WifiApControl apControl = Server.turnOnOffHotspot(true);  
    	        	
    	        	
    	        	Thread t = new Thread() {
    	                @Override
    	                public void run() {
    	                    try {
    	                        //check if hotspot started
    	                        while (!apControl.isWifiApEnabled()) {    	                           
    	                            Thread.sleep(1000);                     
    	                        }
    	                        if(apControl.isWifiApEnabled()){
    	                        Log.e(TAG, apControl.getIpAddress());
    	                        }

    	                    } catch (Exception e) {
    	                    }
    	                }
    	            };
    	            t.start();
    	        	
    	        
    	        } else {
    	            // The toggle is disabled
    	        	Log.e(TAG, "Server stop");
    	        	Server.stop();
    	        }
    	    }
    		
    	
    		
    	});
    	
    	
    	
    	
           	
    	
    	return view;
    
    }
	
	
}
