package com.vpowerrc.vesuviusserver;
import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import com.vpowerrc.vesuviusserver.Server;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.ToggleButton;


public class MainActivity extends ActionBarActivity{
	

	public Context appContext = this;		
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
				                        	TextView text = (TextView) findViewById(R.id.statusText);
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
		//Fragment homeFragment = new HomeFragment();
		//Fragment settingsFragment = new SettingsFragment();
		//Fragment aboutFragment = new AboutFragment();
	
		//set the Tab listener. Now we can listen for clicks.
		homeTab.setTabListener(new TabsListener<HomeFragment>(this, "home", HomeFragment.class));
		settingsTab.setTabListener(new TabsListener<SettingsFragment>(this, "settings", SettingsFragment.class));
		aboutTab.setTabListener(new TabsListener<AboutFragment>(this, "about", AboutFragment.class));
	
		//add the two tabs to the actionbar
		actionbar.addTab(homeTab, true);
		actionbar.addTab(settingsTab);
		actionbar.addTab(aboutTab);
		
		//HomeFragment.setServerObject(server);
	
	}
	
	
}

class TabsListener<T extends Fragment> implements ActionBar.TabListener {
    private Fragment fragment;
    private String tag;
    private Activity activity;
    private final Class<T> clz;

    public TabsListener(Activity activity, String tag, Class<T> clz) {
    	this.clz = clz;
    	this.tag = tag;
    	this.activity = activity;
    }   

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) { 
    	Fragment preInitializedFragment = ((FragmentActivity) activity).getSupportFragmentManager().findFragmentByTag(tag);
    	if (fragment == null && preInitializedFragment == null) {    		
    		fragment = (Fragment) Fragment.instantiate(activity, clz.getName());
    		ft.add(R.id.fragment_container, fragment);
    	}
    	else{    		
    		ft.show(fragment);    		
    	}
    		
    	
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    	ft.hide(fragment);    	
    }

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub	
	}

}  
