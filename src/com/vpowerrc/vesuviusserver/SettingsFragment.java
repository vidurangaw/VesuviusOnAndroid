package com.vpowerrc.vesuviusserver;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SettingsFragment extends Fragment {

	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    	super.onSaveInstanceState(savedInstanceState);
	        // Inflate the layout for this fragment
	        View view = inflater.inflate(R.layout.fragment_settings, container, false);
	        
	        Button updateButton = (Button) view.findViewById(R.id.updateButton);
	        updateButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	                // Do something in response to button click
	            	AlertDialog.Builder confirm = new AlertDialog.Builder(getActivity());

	            	confirm.setTitle("Confirmation");
	            	confirm.setMessage("Updating will consume about 10 Megabytes of data. Are you sure you want to proceed ?");		            	

	            	confirm.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	            	public void onClick(DialogInterface dialog, int whichButton) {	            	  
	            		//DownloaderAync downloader = new DownloaderAync(getActivity());		
	            		//downloader.execute("data.zip",getAppDirectory() + "/","vesuvius.zip",getHttpDirectory() + "/www/");	            		
	            			
		        			ProgressDialog progressBar = new ProgressDialog(getActivity());
		        			progressBar.setMessage("Updating Vesuvius...");
		        			progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		        			progressBar.setProgress(0);
		        			progressBar.setProgressNumberFormat(null);
		        			progressBar.setCancelable(false);
		        			progressBar.setCanceledOnTouchOutside(false);
		        			progressBar.show();
		        			Server.getInstance().update(progressBar);
	            		
	            		dialog.dismiss();
	            	  }
	            	});

	            	confirm.setNegativeButton("No", new DialogInterface.OnClickListener() {
	            	  public void onClick(DialogInterface dialog, int whichButton) {
	            	    // Canceled.
	            	  }
	            	});

	            	confirm.show();
	            }
	        });
	        
	        Button exportButton = (Button) view.findViewById(R.id.exportButton);
	        exportButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	                
	            	final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Please wait","processing", true);
	            	
	            	new Thread(new Runnable() {
	    		        public void run() {	        	
	    		        	
	    		        	final String result = Utilities.exportDatabase();	    		            
	    		             		            	
	    		        	Log.e(Server.TAG, result);
	    		        	
							getActivity().runOnUiThread(new Runnable() {  
	                            @Override
	                            public void run() {
									progressDialog.dismiss();
									AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

									alert.setTitle("Export Successful");
									alert.setMessage(result);		            	

									alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					            	public void onClick(DialogInterface dialog, int whichButton) {			            	  
					            		dialog.dismiss();
					            	  }
					            	});
									alert.show();
	                            }
							});
	    		        }
	    		    }).start();
	            }
	        });
	        
	        
	        return view;
	    }
}

