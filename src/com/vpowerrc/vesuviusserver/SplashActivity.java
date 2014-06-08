package com.vpowerrc.vesuviusserver;


import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.ActionBarActivity;


public class SplashActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//ActionBar actionBar = getSupportActionBar();
		//actionBar.hide();
		
		super.onCreate(savedInstanceState);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		
		Thread splash = new Thread() {
            public void run() {
                 
                try {
                    // Thread will sleep for 2 seconds
                    sleep(1500);
                     
                    // After 2 seconds redirect to home activity
                    Intent i=new Intent(getBaseContext(),MainActivity.class);
                    startActivity(i);
                     
                    //Remove activity
                    finish();
                     
                } catch (Exception e) {
                 
                }
            }
        };
         
        // start thread
        splash.start();
	}

}
