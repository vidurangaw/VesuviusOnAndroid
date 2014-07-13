package com.omt.remote.util.net;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.vpowerrc.vesuviusserver.Server;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;
 
/**
 * This class is use to handle all Hotspot related information.
 * 
 *
 * 
 */
public class WifiApControl {
    private static Method getWifiApState;
    private static Method isWifiApEnabled;
    private static Method setWifiApEnabled;
    private static Method getWifiApConfiguration;
 
    public static final String WIFI_AP_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_AP_STATE_CHANGED";
 
    public static final int WIFI_AP_STATE_DISABLED = WifiManager.WIFI_STATE_DISABLED;
    public static final int WIFI_AP_STATE_DISABLING = WifiManager.WIFI_STATE_DISABLING;
    public static final int WIFI_AP_STATE_ENABLED = WifiManager.WIFI_STATE_ENABLED;
    public static final int WIFI_AP_STATE_ENABLING = WifiManager.WIFI_STATE_ENABLING;
    public static final int WIFI_AP_STATE_FAILED = WifiManager.WIFI_STATE_UNKNOWN;
 
    public static final String EXTRA_PREVIOUS_WIFI_AP_STATE = WifiManager.EXTRA_PREVIOUS_WIFI_STATE;
    public static final String EXTRA_WIFI_AP_STATE = WifiManager.EXTRA_WIFI_STATE;
	private static final String TAG = "Vesuvius Server";
	
	private static volatile WifiApControl instance = null;
	public Context appContext;
	private WifiManager wifiManager;
	
    static {
        // lookup methods and fields not defined publicly in the SDK.
        Class<?> cls = WifiManager.class;
        for (Method method : cls.getDeclaredMethods()) {
            String methodName = method.getName();
            if (methodName.equals("getWifiApState")) {
                getWifiApState = method;
            } else if (methodName.equals("isWifiApEnabled")) {
                isWifiApEnabled = method;
            } else if (methodName.equals("setWifiApEnabled")) {
                setWifiApEnabled = method;
            } else if (methodName.equals("getWifiApConfiguration")) {
                getWifiApConfiguration = method;
            }
        }
    }
    
       
    public static boolean isApSupported() {
        return (getWifiApState != null && isWifiApEnabled != null
                && setWifiApEnabled != null && getWifiApConfiguration != null);
    }
 
    
 
    private WifiApControl(Context appContext, WifiManager wifiManager) {
    	this.appContext = appContext;
        this.wifiManager = wifiManager;
    }
    
    public static WifiApControl getInstance() {       
        return instance;
    }
    
    public static WifiApControl createInstance(Context appContext) {
        if (instance == null) {
            synchronized (Server.class) {
                // Double check
                if (instance == null) {
                	WifiManager wifiManager = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);
                    //WifiApControl apControl = WifiApControl.getApControl(wifiManager);
                    instance = new WifiApControl(appContext, wifiManager);
                }
            }
        }
        return instance;
    }
    /*
    public static WifiApControl getApControl(WifiManager wifiManager) {
        if (!isApSupported())
            return null;
        return new WifiApControl(wifiManager);
    }
    */
    
    public String getIpAddress() {
        if (!isApSupported())
            return null;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                    .hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                if (intf.getName().contains("wlan")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                            .hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress() && (inetAddress.getAddress().length == 4)) {
                            ;
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, ex.toString());
        }
        return null;
    }
    
    public boolean isWifiApEnabled() {
        try {
            return (Boolean) isWifiApEnabled.invoke(wifiManager);
        } catch (Exception e) {
            Log.v(TAG, e.toString(), e); // shouldn't happen
            return false;
        }
    }
 
    public int getWifiApState() {
        try {
            return (Integer) getWifiApState.invoke(wifiManager);
        } catch (Exception e) {
            Log.v(TAG, e.toString(), e); // shouldn't happen
            return -1;
        }
    }
 
    public WifiConfiguration getWifiApConfiguration() {
        try {
            return (WifiConfiguration) getWifiApConfiguration.invoke(wifiManager);
        } catch (Exception e) {
            Log.v(TAG, e.toString(), e); // shouldn't happen
            return null;
        }
    }
 
    public boolean setWifiApEnabled(WifiConfiguration config, boolean enabled) {
        try {
            return (Boolean) setWifiApEnabled.invoke(wifiManager, config, enabled);
        } catch (Exception e) {
            Log.v(TAG, e.toString(), e); // shouldn't happen
            return false;
        }
    }
    
    public void turnOnOffHotspot(boolean isTurnToOn) {	       
     	
        // TURN OFF YOUR WIFI BEFORE ENABLE HOTSPOT
        if (wifiManager.isWifiEnabled() && isTurnToOn) {
        	wifiManager.setWifiEnabled(false);
        }
        
        setWifiApEnabled(getWifiApConfiguration(),isTurnToOn);
     }
      
    
}