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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		writeSahanaConf();
		createDatabase();
		convertHtacessToLighttpd();
	}
	
	public static void convertHtacessToLighttpd(){
		
		
		File htaccessFile = new File(getVesuviusDirectory()+"www/.htaccess");
		File lighttpdConfFile = new File(getHttpDirectory()+"conf/lighttpd.conf");
		
		
		
		if (htaccessFile.exists()){			
			try {
	            //open .htaccess file 		           
				FileReader fr = new FileReader(htaccessFile);
	            BufferedReader br = new BufferedReader(fr);
	            
	             //holds a line of input
	            String iLine;  	
	            
	            //holds a line of output
	            String oLine = null;           
	            	            
	            //holds repeat rules
	            String repeatOutputText="";
	            
	            FileWriter fw = new FileWriter(lighttpdConfFile,true);
	            BufferedWriter bw = new BufferedWriter(fw);
	            
	            //write to lighttpd.conf
	            bw.write("\n\n\n\n");
	            bw.write("# Vesuvius configurations\n\n\n");	            
	            bw.write("url.rewrite-once = (\n");           	            
	            
	            bw.write("\"^/vesuvius/test/(.*\\.(js|ico|gif|jpg|jpeg|png|css|))$\" => \"/vesuvius/$1\",\n");	            
	            //bw.write("\"^/vesuvius/(.+)/(.*\\.(js|ico|gif|jpg|jpeg|png|css|))$\" => \"/vesuvius/$1\",");
	            bw.write("\"^/vesuvius/.*\\.(js|ico|gif|jpg|jpeg|png|css|)$\" => \"$0\",\n");
	            
	            bw.write(")\n");	
	            
	            // read .htaccess file
	            while ((iLine = br.readLine()) != null ) {
	               
	            	if(iLine.contains("RewriteRule ^([^/][a-z0-9]+)/(.+)$")){
	            		
	            		repeatOutputText += "\"^/vesuvius/([^/][a-z0-9]+)/([^?]+)(?:\\?(.*))?$\" => \"/vesuvius/$2?shortname=$1&$3\"";
	            	}
	            	else if (iLine.contains("RewriteRule ^")) {	
	                	
	                	
	 	               	iLine = iLine.replaceAll("\\s+", " ");
	                	
	 	                String[] splitedLines = iLine.split("\\s+");
	 	                
	            	    splitedLines[1] = splitedLines[1].substring(1);
	            	    splitedLines[1] = splitedLines[1].substring(0, splitedLines[1].length()-1);
	              
	            		
	            	   
 	            	    splitedLines[2] = splitedLines[2].substring(splitedLines[2].indexOf("?"));
 	            	
 	            	    oLine = "\"^/vesuvius/"+splitedLines[1]+"(?:\\?(.*))?$\" => \"/vesuvius/"+splitedLines[2]+"&$2\",\n";	
 	            	    
 	            	    
 	            	    repeatOutputText += oLine;
	 	               
	                }
	            		 
	            }
	       	
	            
	            bw.write("url.rewrite-repeat = (\n");	            
	            repeatOutputText = repeatOutputText.replaceAll(",\n$", "\n");            
	            bw.write(repeatOutputText+")\n\n");	            
	            
	            
	            bw.write("alias.url += (\"/vesuvius\" => http_dir + \"/www/vesuvius-master/vesuvius/www\")");
	            	            
	            br.close();            
	            bw.close();
	            Log.e(TAG, "lighttpd.conf updated");	            	
	            
	            
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }	
			
		}
		
		
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
			
	        URL phpUrl = new URL("http://localhost:"+Server.serverPort+"/scripts/create_database.php");
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
            	   
	                if (line.contains("$conf['db_name'] =")) {	                	
	                	line = line.replace("\"\"", "\"vesuvius\"");
	                }
	                if (line.contains("$conf['db_host'] =")) {	                	
	                	line = line.replace("\"\"", "\"localhost\"");
	                }
	                if (line.contains("$conf['db_user'] =")) {	                	
	                	line = line.replace("\"\"", "\"root\"");
	                }
	                if (line.contains("$conf['base_uuid'] = \"vesuvius.sahanafoundation.org/\"")) {
	                	line = "$conf['base_uuid'] = $_SERVER['SERVER_NAME'].\":\".$_SERVER['SERVER_PORT'].\"/vesuvius/\";";	                
	                }
	                if (line.contains("#$conf['https']")) {
	                	line = line.replace("#", "");	                
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
