package com.vpowerrc.vesuviusserver;



import android.app.ProgressDialog;
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
