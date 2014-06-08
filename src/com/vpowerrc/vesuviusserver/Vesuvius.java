package com.vpowerrc.vesuviusserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.SQLException;
import android.os.StrictMode;
import android.util.Log;

public class Vesuvius  extends Activity {
	public static String TAG = "Vesuvius Server";
	public static Context appContext;
	public static ProgressDialog progressBar;
	
	
	
	public static void install(ProgressDialog progressBar){
		
		UnzipperAsync vesuvius_unzipper = new UnzipperAsync(appContext,progressBar,Vesuvius.class.getName());		
		vesuvius_unzipper.execute("vesuvius.zip",Vesuvius.getHttpDirectory() + "/www/");		
		
	}
	
	public static void afterInstall(){
		Log.e(TAG, "after install");
		Vesuvius.writeSahanaConf();
		
	}
	public static void createDatabase(){
		
	/*	
		String url = "http://localhost:8080/scripts/create_database.php";
		HttpClient client = new DefaultHttpClient();
	
		try {
		  client.execute(new HttpGet(url));
		  Log.e(TAG, "create db1");
		} catch(IOException e) {
		 
		}	
	*/		
		try {
			
	        URL phpUrl = new URL("http://localhost:8080/scripts/create_database.php");
	        URLConnection urlCon = phpUrl.openConnection();
	        BufferedReader br = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
	        String line;
	        while ((line = br.readLine()) != null){
	        	Log.e(TAG,line);
	        }        
	        br.close();
	     } 
		catch(Exception e) {
			e.printStackTrace();
	    }
		
		
		
	}
	public static void writeSahanaConf() {		
		
		String confPath = getVesuviusDirectory()+"conf/";		
		File exampleConfFile = new File(confPath+"sahana.conf.example");
		File confFile = new File(confPath+"sahana.conf");
		
		//create sahana.conf;
		if(!confFile.exists()){
			try {
				confFile.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}		
		}
		
		if (exampleConfFile.exists()){
			Log.e(TAG, "rename yes");
			try {
	            //open sahana.conf.example file 		           
				FileReader fr = new FileReader(exampleConfFile);
	            BufferedReader br = new BufferedReader(fr);
	            //holds a line of input
	            String line;  
	            
	            
	            FileWriter fw = new FileWriter(confFile);
	            BufferedWriter bw = new BufferedWriter(fw);
	           
	            
	            //read sahana.conf.example file
	            while ((line = br.readLine()) != null ) {
            	   Log.d(TAG, line);
	                if (line.equals("$conf['db_name'] = \"\";")) {	                	
	                	line = line.replace("\"\"", "\"vesuvius\"");
	                }
	                if (line.equals("$conf['db_host'] = \"\";")) {	                	
	                	line = line.replace("\"\"", "\"localhost\"");
	                }
	                if (line.equals("$conf['db_user'] = \"\";")) {	                	
	                	line = line.replace("\"\"", "\"root\"");
	                }
	                //write to sahana.conf
	                bw.write(line+"\n");
	            }      
	            br.close();            
	            bw.close();
	            Log.e(TAG, "sahana.conf created");	            	
	            
	            
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }	
			
		}
		else
			Log.e(TAG, "rename no");    
		

	}
	
	public static void setContext(Context appContext) {
		Vesuvius.appContext = appContext;

	}
	
	final public static String getHttpDirectory() {

		return android.os.Environment.getExternalStorageDirectory().getPath()+ "/htdocs/";

	}
	
	final public static String getVesuviusDirectory() {

		return android.os.Environment.getExternalStorageDirectory().getPath()+ "/htdocs/www/vesuvius-master/vesuvius/";

	}
	
	public static boolean checkIfInstalled() {

		
		
		File confFile = new File(getVesuviusDirectory()+ "conf/sahana.conf");
	
		Log.e(TAG, confFile.getAbsolutePath());
		
		if (confFile.exists()) {
			Log.e(TAG, "vesuvius exists");
						
			return true;
			
		}
		else{
			Log.e(TAG, "vesuvius doesnt't exists");
						
			return false;
		
		}
	}
	
}
