package com.vpowerrc.vesuviusserver;
import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import com.omt.remote.util.net.WifiApControl;
import com.vpowerrc.vesuviusserver.Server;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.ToggleButton;


public class MainActivity extends ActionBarActivity{
	

	public Context appContext = this;	
	public ProgressDialog progressBar;
	public ProgressDialog progressBar2;
	public static String TAG = "Vesuvius Server";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_main);
		
		//set context in Server class
		Server.createInstance(appContext);
		WifiApControl.createInstance(appContext);				
		
	
		final Intent notificationIntent = new Intent(this, VesuviusNotificationService.class);   	
		
		try {
			if(Server.getInstance().isServerRunning()){			
				
				if(Server.getInstance().serverInstalled()){
					new Thread(new Runnable() {
				        @Override
				        
				        public void run() {
				            try {
				                //check if hotspot started
				            	WifiApControl.getInstance().turnOnOffHotspot(true);  
				            	
				                while (!WifiApControl.getInstance().isWifiApEnabled()) {    	                           
				                    Thread.sleep(500);                     
				                }
				                if(WifiApControl.getInstance().isWifiApEnabled()){
				                	
				                	final String ipAddress = WifiApControl.getInstance().getIpAddress();
				                	Log.e(TAG, ipAddress);
				                	startService(notificationIntent);
				                	
				                	runOnUiThread(new Runnable() {  
				                        @Override
				                        public void run() {
				                        	TextView text = (TextView) findViewById(R.id.textView2);
					                        text.setText("Vesuvius is running on \n\nhttp://"+ipAddress+":"+Server.serverPort+"/vesuvius");
					                        ToggleButton toggle = (ToggleButton) findViewById(R.id.serverToggleButton); 
					                        toggle.setChecked(true);			
				                        }
				                    });
				                
				                }
	
				            } catch (Exception e) {
				            }
				        }
					}).start(); 
				}
				Log.e(TAG, "running");				
				// show notification 
				this.startService(notificationIntent);

			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		//StrictMode.setThreadPolicy(policy);
		
				
		if(!Server.getInstance().serverInstalled()){			
			Server.getInstance().install(progressBar);
		}
		
		//Server.getInstance().afterInstall();	
				
		//ActionBar gets initiated
		ActionBar actionbar = getSupportActionBar();
		//Tell the ActionBar we want to use Tabs.
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		//hide title bar
	    actionbar.setDisplayShowTitleEnabled(false);
		actionbar.setDisplayShowHomeEnabled(false);
		
		//initiating both tabs and set text to it.
		ActionBar.Tab homeTab = actionbar.newTab().setText("Home");
		ActionBar.Tab settingsTab = actionbar.newTab().setText("Settings");
		ActionBar.Tab aboutTab = actionbar.newTab().setText("About");
	
		//create the two fragments we want to use for display content
		Fragment homeFragment = new HomeFragment();
		Fragment settingsFragment = new SettingsFragment();
		Fragment aboutFragment = new AboutFragment();
	
		//set the Tab listener. Now we can listen for clicks.
		homeTab.setTabListener(new MyTabsListener(homeFragment));
		settingsTab.setTabListener(new MyTabsListener(settingsFragment));
		aboutTab.setTabListener(new MyTabsListener(aboutFragment));
	
		//add the two tabs to the actionbar
		actionbar.addTab(homeTab);
		actionbar.addTab(settingsTab);
		actionbar.addTab(aboutTab);
		
		//HomeFragment.setServerObject(server);
	
	}
	
	public void addProgresBar(String message) {					
		
	}
	
	
}

class MyTabsListener implements ActionBar.TabListener {
    public Fragment fragment;

    public MyTabsListener(Fragment fragment) {
    	this.fragment = fragment;
    }   

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
    	ft.replace(R.id.fragment_container, fragment);
    
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    	ft.remove(fragment);
    }

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}

}  
