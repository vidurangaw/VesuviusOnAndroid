package com.vpowerrc.vesuviusserver;



import java.io.IOException;

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
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
        // inflate the layout for this fragment    
    	final View view = inflater.inflate(R.layout.fragment_home, container, false);    	
    	  
    	ToggleButton toggle = (ToggleButton) view.findViewById(R.id.serverToggleButton);   
    	
    	
    			
		if(!Server.getInstance().serverInstalled()){
			
			ProgressDialog progressBar = new ProgressDialog(getActivity());
			progressBar.setMessage("Initializing Vesuvius server for the first time...");
			progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressBar.setProgress(0);
			progressBar.setProgressNumberFormat(null);
			progressBar.setCancelable(false);
			progressBar.setCanceledOnTouchOutside(false);
			progressBar.show();
			Server.getInstance().install(progressBar);
		}
			
    	// create notification  	
		final Intent notificationIntent = new Intent(getActivity(), VesuviusNotificationService.class);   
					    	    	
    	toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    	    
    		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    			
    			final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Please wait","processing", true);
    	        if (isChecked) {    	          	    	
    	        	               	        	       	
    	           	new Thread(new Runnable() {
    	                @Override
    	                public void run() {
    	                   
    	                	WifiApControl.getInstance().turnOnOffHotspot(true);
    	        			try {
								if(!Server.getInstance().isServerRunning()) {  
									Server.getInstance().start();
									Log.e(Server.TAG, "Server start");
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
    	        			
	                        //check if hotspot started
	                        while (!WifiApControl.getInstance().isWifiApEnabled()) {    	                           
	                            try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}                     
	                        }
	                        
	                        try {
								Thread.sleep(500);
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
	                        
                        	final String ipAddress = WifiApControl.getInstance().getIpAddress();
	                        Log.e(Server.TAG, ipAddress);
	                        
	                        getActivity().runOnUiThread(new Runnable() {  
	                            @Override
	                            public void run() {
	                            	TextView text = (TextView) view.findViewById(R.id.statusText);
	    	                        text.setText("Vesuvius is running on \nhttp://"+ipAddress+":"+Server.serverPort+"/vesuvius");
	    	                        getActivity().startService(notificationIntent);    	 
	                            }
	                        });
	                        
	                        try {
								while (!Server.getInstance().isServerRunning()) {    	                           
								    Thread.sleep(500); 				    
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	                        
	                        progressDialog.dismiss();                     
    	                          	                  
    	                }
    	        	}).start();   	          
    	        	    	        
    	        } else {    	        	
    	            // The toggle is disabled	    	    
	    	        
	    	        new Thread(new Runnable() {
	    		        public void run() {	        	
	    		        	
	    		        	Server.getInstance().stop();
	    	        		Log.e(Server.TAG, "Server stop");
	    		        	WifiApControl.getInstance().turnOnOffHotspot(false);	
	    	        		getActivity().stopService(notificationIntent);   	    		            
	    		             		            	
							try {
								while (Server.getInstance().isServerRunning()) {    	                           
								    Thread.sleep(500); 				    
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							getActivity().runOnUiThread(new Runnable() {  
	                            @Override
	                            public void run() {
									TextView text = (TextView) view.findViewById(R.id.statusText);
					                text.setText("");
					                progressDialog.dismiss();
	                            }
							});
	    		        }
	    		    }).start();		    	                   
                    
    	        }
    	    }
    		
    	    		
    	}); 	    	
    	    	           	
	    	
	    	
	    
	    
		return view;
	}
	
}
