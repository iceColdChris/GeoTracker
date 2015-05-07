package com.uwt.strugglebus.geotracker.Model;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.uwt.strugglebus.geotracker.Model.LocationLog;

import java.util.Timer;
import java.util.TimerTask;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 *
 * Service that gets current GPS location every interval and stores it in a sqli db
 */
public class Tracker extends Service {

    private static final String DB_NAME = "Trajectories";
    private static final String TABLE = "Locations";


    //current location
    private Location loc;

    public Tracker() {}

    /**
     * TODO: comment
     */
    @Override
    public void onCreate() {
        Log.w("trackers", "start");
        int interval = 1000;
        Timer timer = new Timer();
        //connect / create local db
        final SQLiteDatabase db = openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE +"(lat REAL,lon REAL, speed REAL, heading REAL, time INT);");

        // Acquire a reference to the system Location Manager
        final LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                loc = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (loc != null) {
                    Log.w("nsa.gov", loc.toString());
                    // (lat, lon, speed, heading, time)
                    String insert = "INSERT INTO " + TABLE + " VALUES(" + loc.getLatitude() + ", " +
                            loc.getLongitude() + ", " + loc.getSpeed() + ", " + loc.getBearing()
                            + ", " + loc.getTime() + ");";
                    db.execSQL(insert);
                    Log.w("sqlTest", insert);
                }
            }
        }, 0,(long)interval);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //Log.i("Tracker", "service starting");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
