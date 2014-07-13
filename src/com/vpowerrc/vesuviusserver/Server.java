package com.vpowerrc.vesuviusserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.omt.remote.util.net.WifiApControl;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.wifi.WifiManager;
import android.util.Log;

public class Server {
	
	
	public static String TAG = "Vesuvius Server";
	public Context appContext;
	//public static ProgressDialog progressBar;
	public static String serverPort="8080";
	
	private static volatile Server instance = null;
	 
    // private constructor
    private Server(Context appContext) {
    	this.appContext = appContext;
    	Utilities.setContext(appContext);
    	//install();
    }
    
    public static Server getInstance() {       
        return instance;
    }
    
    public static Server createInstance(Context appContext) {
        if (instance == null) {
            synchronized (Server.class) {
                // Double check
                if (instance == null) {
                    instance = new Server(appContext);
                }
            }
        }
        return instance;
    }
    
	public void install(ProgressDialog progressBar){
		//if(!serverInstalled()) {
			UnzipperAsync unzipper = new UnzipperAsync(appContext,progressBar);		
			unzipper.execute("data.zip",getAppDirectory() + "/","vesuvius.zip",getHttpDirectory() + "/www/");		
		//}
	}
	
	public void copyFiles(){		
		createServerDirs();
		restoreConfiguration("lighttpd.conf", getHttpDirectory() + "/conf/");
		restoreConfiguration("php.ini", getHttpDirectory() + "/conf/");
		restoreConfiguration("mysql.ini", getHttpDirectory() + "/conf/");
		restoreConfiguration("create_database.php", getHttpDirectory() + "/www/scripts/");
		restoreConfiguration("timezones.sql", getHttpDirectory() + "/www/scripts/");
		
		File tempFolder = new File(getHttpDirectory() + "/tmp/");
		Utilities.deleteFolder(tempFolder);
	}
	
	public void afterInstall(){			 
		
		copyFiles();
		setPermission();		
		
		Utilities.writeSahanaConf();		
		Utilities.convertHtacessToLighttpd();
		
		final ProgressDialog progressDialog = ProgressDialog.show(appContext, "Please wait","processig", true);
		new Thread(new Runnable() {
	       
			public void run() {   
				
	        	Server.getInstance().start();	        
	            	
				try {
					while (!Server.getInstance().isServerRunning()) {    	                           
					    Thread.sleep(100); 				    
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 		
									
				Utilities.createDatabase();
				Log.e(TAG, "php script processed");
				progressDialog.dismiss();					        	
	        	//Server.getInstance().stop();			
						            
	        }
		}).start();	
		
	}
	
	
	public String getAppDirectory() {

		return appContext.getApplicationInfo().dataDir;

	}
	
	public String getHttpDirectory() {

		return android.os.Environment.getExternalStorageDirectory().getPath()+ "/htdocs";

	}
	
	public boolean serverInstalled() {

		
		
		File mPhp = new File(getAppDirectory() + "/php-cgi");
		File mMySql = new File(getAppDirectory() + "/mysqld");
		File mLighttpd = new File(getAppDirectory() + "/lighttpd");
		File mMySqlMon = new File(getAppDirectory() + "/mysql-monitor");
		
		if (mPhp.exists() && mMySql.exists() && mLighttpd.exists()&& mMySqlMon.exists()) {					
			return true;
			
		}
		else{		
			
			return false;
		
		}
	}
	
	
	
	
	protected boolean isServerRunning() throws IOException {
		InputStream is;
		java.io.BufferedReader bf;
		boolean isRunning = false;
		try {
			is = Runtime.getRuntime().exec("ps").getInputStream();
			bf = new java.io.BufferedReader(new java.io.InputStreamReader(is));

			String r;
			while ((r = bf.readLine()) != null) {
				if (r.contains("lighttpd")) {
					isRunning = true;
					break;
				}

			}
			is.close();
			bf.close();

		} catch (IOException e) {
			e.printStackTrace();

		}
		return isRunning;

	}
	
	protected void setPermission() {
		try {

			Utilities.execCommand("/system/bin/chmod 777 " + getAppDirectory()
					+ "/lighttpd");
			Utilities.execCommand("/system/bin/chmod 777 " + getAppDirectory()
					+ "/php-cgi");
			Utilities.execCommand("/system/bin/chmod 777 " + getAppDirectory()
					+ "/mysqld");
			Utilities.execCommand("/system/bin/chmod 777 " + getAppDirectory()
					+ "/mysql-monitor");
			Utilities.execCommand("/system/bin/chmod 777 " + getAppDirectory()
					+ "/killall");
			Utilities.execCommand("/system/bin/chmod 755 " + getAppDirectory() + "/tmp");

		} catch (java.lang.Exception e) {
			Log.e(TAG, "setPermission", e);
		}

	}
	
	protected void createServerDirs() {

		File mFile = new File(getHttpDirectory() + "/conf/");
		if (!mFile.exists())
			mFile.mkdirs();

		mFile = new File(getHttpDirectory() + "/www/");
		if (!mFile.exists())
			mFile.mkdir();

		mFile = new File(getHttpDirectory() + "/logs/");
		if (!mFile.exists())
			mFile.mkdir();
		
		mFile = new File(getHttpDirectory() + "/tmp/");
		if (!mFile.exists())
			mFile.mkdir();	
		
		mFile = new File(getHttpDirectory() + "/www/scripts/");
		if (!mFile.exists())
			mFile.mkdir();
		
		
	}
	
	private void restoreConfiguration(String fileName, String location) {

		File file = new File(location + fileName);
		if (file.exists()) {
			file.delete();
		}
			try {

				String mString;

				java.io.InputStream mStream = appContext.getAssets().open(
						fileName, AssetManager.ACCESS_BUFFER);

				java.io.BufferedWriter outputStream = new java.io.BufferedWriter(
						new java.io.FileWriter(getHttpDirectory() + "/tmp/"
								+ fileName));

				int c;
				while ((c = mStream.read()) != -1) {
					outputStream.write(c);
				}
				outputStream.close();
				mStream.close();

				mString = org.apache.commons.io.FileUtils.readFileToString(
						new File(getHttpDirectory() + "/tmp/" + fileName),
						"UTF-8");

				mString = mString.replace("%app_dir%", getAppDirectory());
				mString = mString.replace("%http_dir%", getHttpDirectory());
				mString = mString.replace("%port%", serverPort);
				org.apache.commons.io.FileUtils.writeStringToFile(new File(location + fileName), mString,"UTF-8");
				
			} catch (java.lang.Exception e) {
				Log.e(TAG, "Unable to copy " + fileName + " from assets", e);

			}
			
			
		

	}
	
	

	
	

	public void start() {
		// TODO Auto-generated method stub
		System.out.println("ON");
		
		
		String[] serverCmd = { getAppDirectory() + "/lighttpd", "-f",
				getHttpDirectory() + "/conf/lighttpd.conf", "-D"

		};
		String[] mySQLCmd = { getAppDirectory() + "/mysqld",
				"--defaults-file=" + getHttpDirectory() + "/conf/mysql.ini",
				// "--pid-file=" + getHttpDirectory() + "/proc/mysqld.pid",
				"--user=root",
				"--language=" + getAppDirectory() + "/share/mysql/english" };
		try {
			(new ProcessBuilder(serverCmd)).start();
			Log.i(TAG, "LIGHTTPD is successfully running");
		} catch (java.lang.Exception e) {
			Log.e(TAG, "Unable to start LIGHTTPD", e);
		}

		try {
			(new ProcessBuilder(mySQLCmd)).start();
			Log.i(TAG, "MYSQLD is successfully running");
		} catch (Exception e) {
			Log.e(TAG, "Unable to start MYSQLD", e);
		}

		return;
	}


	public void stop() {
		// TODO Auto-generated method stub
		System.out.println("OFF");
		
		/**
		 * 
		 * Kill the Running Instances of all called process name. PHP is
		 * automatically kill when instance <strong>LIGHTTPD</strong> is
		 * destroyed. Anyway if it is unable to kill <strong>PHP</strong> lets
		 * kill by invoking <b>killall</b> command
		 */
		Utilities.execCommand(getAppDirectory() + "/killall lighttpd");		
		Utilities.execCommand(getAppDirectory() + "/killall php");
		Utilities.execCommand(getAppDirectory() + "/killall mysqld");
		
		//Utilities.execCommand(getAppDirectory() + "/killall mysql-monitor");

	}
}


