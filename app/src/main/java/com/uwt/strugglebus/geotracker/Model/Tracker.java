package com.uwt.strugglebus.geotracker.Model;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 *
 * Service that gets current GPS location every interval and stores it in a sqlite db
 */
public class Tracker extends IntentService {

    private static final String DB_NAME = "Trajectories";
    private static final String TABLE = "Locations";
    private static final int POLL_INTERVAL = 5000;

    //current location
    private Location loc;

    public Tracker() { super("Tracker Service");}


    @Override
    public void onCreate() {

    }

    /**
     * {@inheritDoc}
     *
     * On top of the above
     * functionality this method
     * also creates the SQLlite
     * database and the trajectory objects.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.w("trackers", "start");
        int interval = 10000;
        Timer timer = new Timer();
        //connect / create local db
        final SQLiteDatabase db = openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE +"(lat REAL,lon REAL, speed REAL, heading REAL, time BIGINT);");

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
        }, 0, (long) interval);
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent i = new Intent(context, Tracker.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {
            alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis()
                    , POLL_INTERVAL, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //Log.i("Tracker", "service starting");
        return START_STICKY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
