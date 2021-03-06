package com.uwt.strugglebus.geotracker.Model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * * Alex Peterson, Chris Fahlin, Josh Moore, Kyle Martens
 * <p/>
 * Starts Tracker service on boot
 */
public class LocationBroadcastReceiver extends BroadcastReceiver {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.i("fused", "Broadcast reciever");
            context.startService(new Intent(context, Tracker.class));
            context.startService(new Intent(context, Logger.class));

        }
    }
}
