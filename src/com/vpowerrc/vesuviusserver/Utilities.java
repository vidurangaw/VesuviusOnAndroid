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

import com.omt.remote.util.net.WifiApControl;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.wifi.WifiManager;
import android.util.Log;

public class Utilities {
	
	public static Context appContext;
	public static String TAG = "Vesuvius Server";
	
	public static void setContext(Context appContext) {
		Utilities.appContext = appContext;

	}
	
	
	 
	public static boolean execCommand(String mCommand) {
		/*
		 * Create a new Instance of Runtime
		 */
		Runtime r = Runtime.getRuntime();
		try {
			/**
			 * Executes the command
			 */
			r.exec(mCommand);

		} catch (java.io.IOException e) {

			Log.e(TAG, "execCommand", e);

			r = null;

			return false;
		}

		return true;

	}
	
	public static String getHttpDirectory() {

		return android.os.Environment.getExternalStorageDirectory().getPath()+ "/htdocs/";

	}
	
	public static String getVesuviusDirectory() {

		return android.os.Environment.getExternalStorageDirectory().getPath()+ "/htdocs/www/vesuvius-master/vesuvius/";

	}
	
	public static boolean vesuviusInstalled() {		
		
		File confFile = new File(getVesuviusDirectory()+ "conf/sahana.conf");	
		Log.e(TAG, confFile.getAbsolutePath());
		
		if (confFile.exists()) {					
			return true;			
		}
		else{		
			return false;
		
		}
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
	            
	            bw.write("\"^/vesuvius/(?:(.*)/)?(theme.*\\.(js|ico|gif|jpg|jpeg|png|css|))$\" => \"/vesuvius/$2\",\n");	
	            bw.write("\"^/vesuvius/(?:(.*)/)?(res.*\\.(js|ico|gif|jpg|jpeg|png|css|))$\" => \"/vesuvius/$2\"\n");
	            //bw.write("\"^/vesuvius/(.+)/(.*\\.(js|ico|gif|jpg|jpeg|png|css|))$\" => \"/vesuvius/$1\",");
	            //bw.write("\"^/vesuvius/.*\\.(js|ico|gif|jpg|jpeg|png|css|)$\" => \"$0\",\n");
	            
	            bw.write(")\n\n");	
	            
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
	
	public static void deleteFolder(File folder) {
	    File[] files = folder.listFiles();
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
	                deleteFolder(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	    folder.delete();
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

}
