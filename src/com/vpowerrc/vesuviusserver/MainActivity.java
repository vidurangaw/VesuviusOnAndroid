package com.vpowerrc.vesuviusserver;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;







import android.app.ProgressDialog;
import android.content.Context;







import com.vpowerrc.vesuviusserver.Server;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

public class MainActivity extends ActionBarActivity{
	

	public Context appContext;
	public ProgressDialog progressBar;
	public ProgressDialog progressBar2;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		appContext = MainActivity.this;
		
		
		//set context in Server class
		Server.setContext(appContext);
		Vesuvius.setContext(appContext);
		
		setContentView(R.layout.activity_main);
			
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		// Image task will only be done AFTER textViewTask is done
		
		if(!Server.checkIfInstalled()){
			addProgresBar("Installing Web Server and Database Server");
			Server.install(progressBar);
		}	//
			
			
			//vesuvius_unzipper.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
			
			//new AsynTask1()params);
			
			
			//servers_unzipper.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"data.zip",Server.getAppDirectory() + "/");
			
			//addProgresBar("Installing Vesuvius Application");
			//UnzipperAsync vesuvius_unzipper = new UnzipperAsync(appContext,progressBar);
			
			//vesuvius_unzipper.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"vesuvius.zip",Server.getHttpDirectory() + "/www/");
			
					
		//}
		
		//UnzipperAsync unzipper_async = new UnzipperAsync(appContext,progressBar);
		
		
		
		//Server server = new Server();
		
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
		
		
		
		progressBar = new ProgressDialog(appContext);
		progressBar.setMessage(message+" ...");
		progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressBar.setProgress(0);
		progressBar.setProgressNumberFormat(null);
		progressBar.setCancelable(false);
		progressBar.setCanceledOnTouchOutside(false);
		progressBar.show();

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
