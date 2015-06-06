package com.uwt.strugglebus.geotracker.Model;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.util.Log;

import java.io.IOException;

/**
 * Alex Peterson, Chris Fahlin, Josh Moore, Kyle Martens
 *
 * This class is a helper class that determines if
 * the phone is connected via WIFI and has a stable
 * internet connection.
 */
public class UtilityTests {

    /**
     * Checks if the phone is connected to WIFI
     * and has a stable internet connection.
     * @param mContext the context that will use this
     * @return True if the phone is connected / False if the phone is not connected
     */
    public static boolean isWIFIConnected(Context mContext) {
        //Checks all the current connections
        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        //Checks specifically for a wifi connection
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        //If the phone is connected to wifi (Not data)
        if (mWifi.isConnected()) {

            Runtime runtime = Runtime.getRuntime();
            try {
                Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8"); //Try pinging Google
                int exitValue = ipProcess.waitFor(); //Wait for a timeout
                return (exitValue == 0); //There is internet access

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return false; //The phone doesnt have internet access
        }

        return false; //The phone doesn't have internet access
    }


    /**
     * Checks the current charging state of the android device.
     * @param context the context
     * @return the charging state of the device
     */
    public static boolean isCharging(Context context) {
        boolean isPlugged= false;
        Log.w("Plugged", "" + isPlugged);
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

        //Is the android plugged in?
        isPlugged = (plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB);

        //Checks for wireless charging on supported phones
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            isPlugged = isPlugged || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS;
        }

        return isPlugged;//Returns the plugin state of the android device
    }
}
