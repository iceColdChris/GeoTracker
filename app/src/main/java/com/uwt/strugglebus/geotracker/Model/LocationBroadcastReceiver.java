package com.uwt.strugglebus.geotracker.Model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
        Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Set the alarm here.
            //context.startService(new Intent(context, Tracker2.class));
            //Tracker.setServiceAlarm(context, true);
        }
    }
}
