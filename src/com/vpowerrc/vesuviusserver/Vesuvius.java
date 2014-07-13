package com.vpowerrc.vesuviusserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class Vesuvius  extends Activity {
	public static String TAG = "Vesuvius Server";
	public static Context appContext;
	public static ProgressDialog progressBar;
	
	
	
	public static void install(ProgressDialog progressBar){
		
		//UnzipperAsync vesuvius_unzipper = new UnzipperAsync(appContext,progressBar,Vesuvius.class.getName());		
		//vesuvius_unzipper.execute("vesuvius.zip",Vesuvius.getHttpDirectory() + "/www/");		
		
	}
	
	public static void afterInstall(){
		Log.e(TAG, "after install");
		
	    //copyPhpScript();
		//writeSahanaConf();		
		//convertHtacessToLighttpd();
		//createDatabase();
	
	}
	


	public static void setContext(Context appContext) {
		Vesuvius.appContext = appContext;

	}
	
	
	
}
