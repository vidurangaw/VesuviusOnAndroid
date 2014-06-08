package com.vpowerrc.vesuviusserver;

import java.io.File;



import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;















import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;



public class Server {
	
	
	public static String TAG = "Vesuvius Server";
	public static Context appContext;
	public static ProgressDialog progressBar;
	private static String serverPort="80";
	
	
	public static void install(ProgressDialog progressBar){
		UnzipperAsync server_unzipper = new UnzipperAsync(appContext,progressBar,Server.class.getName());
		
		server_unzipper.execute("data.zip",Server.getAppDirectory() + "/");
		
		
		
	}
	
	
	public static void afterInstall(){
		
		
		
	}

	public static void setContext(Context appContext) {
		Server.appContext = appContext;

	}
	
	public static String getAppDirectory() {

		return appContext.getApplicationInfo().dataDir;

	}
	
	final public static String getHttpDirectory() {

		return android.os.Environment.getExternalStorageDirectory().getPath()+ "/htdocs";

	}
	
	public static boolean checkIfInstalled() {

		
		
		File mPhp = new File(getAppDirectory() + "/php-cgi");
		File mMySql = new File(getAppDirectory() + "/mysqld");
		File mLighttpd = new File(getAppDirectory() + "/lighttpd");
		File mMySqlMon = new File(getAppDirectory() + "/mysql-monitor");
		
		if (mPhp.exists() && mMySql.exists() && mLighttpd.exists()&& mMySqlMon.exists()) {
			Log.d("yes",getHttpDirectory());
			
			return true;
			
		}
		else{
			Log.d("no",getHttpDirectory());
			
			return false;
		
		}
	}
	
	final static private void setPermission() {
		try {

			execCommand("/system/bin/chmod 777 " + getAppDirectory()
					+ "/lighttpd");
			execCommand("/system/bin/chmod 777 " + getAppDirectory()
					+ "/php-cgi");
			execCommand("/system/bin/chmod 777 " + getAppDirectory()
					+ "/mysqld");
			execCommand("/system/bin/chmod 777 " + getAppDirectory()
					+ "/mysql-monitor");
			execCommand("/system/bin/chmod 777 " + getAppDirectory()
					+ "/killall");
			execCommand("/system/bin/chmod 755 " + getAppDirectory() + "/tmp");

		} catch (java.lang.Exception e) {
			Log.e(TAG, "setPermission", e);
		}

	}
	
	final private static void restoreOrCreateServerData() {

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

		mFile = null;

	}
	
	final private static void restoreConfiguration(String fileName) {

		File isConf = new File(getHttpDirectory() + "/conf/" + fileName);
		if (!isConf.exists()) {

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
				org.apache.commons.io.FileUtils.writeStringToFile(new File(
						getHttpDirectory() + "/conf/" + fileName), mString,
						"UTF-8");
			} catch (java.lang.Exception e) {
				Log.e(TAG, "Unable to copy " + fileName + " from assets", e);

			}
		}

	}
	
	final public static Boolean killProcessByName(String mProcessName) {

		return execCommand(getAppDirectory() + "/killall " +  mProcessName);
	}
	
	final public static boolean execCommand(String mCommand) {

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

	
	

	public static void start() {
		// TODO Auto-generated method stub
		System.out.println("ON");
		restoreOrCreateServerData();
		restoreConfiguration("lighttpd.conf");
		restoreConfiguration("php.ini");
		restoreConfiguration("mysql.ini");
		setPermission();
		
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


	public static  void stop() {
		// TODO Auto-generated method stub
		System.out.println("OFF");
		
		/**
		 * 
		 * Kill the Running Instances of all called process name. PHP is
		 * automatically kill when instance <strong>LIGHTTPD</strong> is
		 * destroyed. Anyway if it is unable to kill <strong>PHP</strong> lets
		 * kill by invoking <b>killall</b> command
		 */
		killProcessByName("lighttpd");
		/**
		 * see above doc why i called PHP here
		 */
		killProcessByName("php");
		killProcessByName("mysqld");
		killProcessByName("mysql-monitor");

	}
}


