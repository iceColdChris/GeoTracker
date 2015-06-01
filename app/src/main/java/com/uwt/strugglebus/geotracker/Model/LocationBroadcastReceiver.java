package com.uwt.strugglebus.geotracker.Model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.uwt.strugglebus.geotracker.Model.Tracker;

/**
 ** Alex Peterson, Chris Fahlin, Josh Moore, Kyle Martens
 *
 * Starts Tracker service on boot
 * */
public class LocationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Set the alarm here.
            Log.i("fused", "Broadcast reciever");
            context.startService(new Intent(context, Tracker2.class));
        }
    }
}
