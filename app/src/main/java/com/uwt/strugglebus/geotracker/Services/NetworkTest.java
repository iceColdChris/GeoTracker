package com.uwt.strugglebus.geotracker.Services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

/**
 * Alex Peterson, Chris Fahlin, Josh Moore, Kyle Martens
 *
 * This class is a helper class that determines if
 * the phone is connected via WIFI and has a stable
 * internet connection.
 */
public class NetworkTest {

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
                Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8"); //Try pinging Google :D
                int exitValue = ipProcess.waitFor(); //Wait for a timeout
                return (exitValue == 0); //There is internet access

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return false; //The phone doesnt have internet access
        }

        return false; //The phone doesnt have internet access
    }
}
