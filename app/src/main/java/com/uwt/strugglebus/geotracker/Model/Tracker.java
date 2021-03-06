package com.uwt.strugglebus.geotracker.Model;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.uwt.strugglebus.geotracker.R;

import java.text.DateFormat;
import java.util.Date;

/**
 * Alex Peterson, Chris Fahlin, Josh Moore, Kyle Martens
 * <p/>
 * Service that uses Google Play Fused Locations services to get user's geo-location every interval
 * And saves the location points in a sqli database
 */
public class Tracker extends IntentService implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final String DB_NAME = "Trajectories";
    private static final String TABLE = "Trajectories";
    private final IBinder mBinder = new LocalBinder();
    /**
     * Provides the entry point to Google Play services.
     */
    private GoogleApiClient mGoogleApiClient;
    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;
    /**
     * Represents a geographical location.
     */
    private Location mCurrentLocation;
    /**
     * Represents the previous geographical location.
     */
    private Location mPrevLocation;
    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    private Boolean mRequestingLocationUpdates;
    /**
     * Time when the location was updated represented as a String.
     */
    private String mLastUpdateTime;
    private SQLiteDatabase mDB;
    private SharedPreferences mPrefs;
    private boolean mTracking;

    /**
     * Creates an IntentService. Invoked by your subclass's constructor.
     */
    public Tracker() {
        super("Tracker");
    }

    /**
     * {@inheritDoc}
     * Gets shared prefs, builds the API Client, and starts the service
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mTracking = true;
        mPrefs = getSharedPreferences(getString(R.string.SHARED_PREFERENCES),
                Context.MODE_PRIVATE);
        Log.i("fused", "handle intent");
        if (mGoogleApiClient == null) {
            buildGoogleApiClient();
            mGoogleApiClient.connect();
        }
        mRequestingLocationUpdates = true;
        return START_STICKY;
    }

    /**
     * {@inheritDoc}
     * Gets shared prefs, builds the API Client, and starts the service
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        mTracking = true;
        mPrefs = getSharedPreferences(getString(R.string.SHARED_PREFERENCES),
                Context.MODE_PRIVATE);
        Log.i("fused", "handle intent");
        if (mGoogleApiClient == null) {
            buildGoogleApiClient();
            mGoogleApiClient.connect();
        }
        mRequestingLocationUpdates = true;
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        Log.i("fused", "build google api");

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        updateState();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    public void stopLocationUpdates() {
        Log.i("fused", "stopping location services");
        mPrefs.edit().putBoolean("geoOn", false).apply();
        mTracking = false;
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mDB.close();
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    private void startLocationUpdates() {
        Log.i("fused", "starting location updates");
        mPrefs.edit().putBoolean("geoOn", true).apply();
        mTracking = true;
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
        mDB = openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
        mDB.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE + "(lat REAL, lon REAL, speed REAL, heading REAL, time BIGINT, uid VARCHAR(50));");
    }


    /**
     * {@inheritDoc}
     * When onConnected message sent, if requesting locations, start location updates
     */
    @Override
    public void onConnected(Bundle bundle) {
        Log.i("fused", "Connected to google play");
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        }
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    /**
     * {@inheritDoc}
     * If connection is suspended try to reconnect
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.i("fused", "connection suspended");
        mGoogleApiClient.connect();
    }

    /**
     * {@inheritDoc}
     * update current location and last update time when the location is changed
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.i("fused", "location changed" + location.toString());
        mCurrentLocation = location;
        Log.i("fused", "time from last update" + DateFormat.getTimeInstance().format(new Date()) + ", " + mLastUpdateTime);
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

        if (mPrevLocation == null || (mCurrentLocation != null && !locationEquals(mCurrentLocation, mPrevLocation))) {
            String uid = mPrefs.getString("userID", "");
            String insert = "INSERT INTO " + TABLE + " VALUES (" + mCurrentLocation.getLatitude() +
                    ", " + mCurrentLocation.getLongitude() +
                    ", " + mCurrentLocation.getSpeed() +
                    ", " + mCurrentLocation.getBearing() +
                    ", " + (mCurrentLocation.getTime() / 1000) +
                    ", \"" + uid + "\");";
            mDB.execSQL(insert);
            Log.w("sqlTestDelete", insert);
        }
        mPrevLocation = mCurrentLocation;
    }

    /**
     * Checks if the locations are the
     * at the same point
     *
     * @param a The first location
     * @param b The second location
     * @return If the locations are the same
     */
    private boolean locationEquals(Location a, Location b) {
        return a.getLatitude() == b.getLatitude() &&
                a.getLongitude() == b.getLongitude() &&
                a.getSpeed() == b.getSpeed() &&
                a.getBearing() == b.getBearing();
    }

    /**
     * {@inheritDoc}
     * print out error if connection failed
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("fused", "connection failed");
        Log.i("error", "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());

    }

    /**
     * Change the location update time
     *
     * @param interval time until next update in seconds
     */
    public void setInterval(int interval) {
        SharedPreferences.Editor edit = mPrefs.edit();
        edit.putInt("geoRate", interval);
        edit.apply();
        boolean isOn = mPrefs.getBoolean("geoOn", false);
        stopLocationUpdates();
        mLocationRequest.setInterval(interval * 1000);
        if (isOn && mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            //restart location request
            startLocationUpdates();
        }
    }

    /**
     * based on settings in shared preferences starts / stops the service and adjusts the interval
     */
    private void updateState() {
        boolean isOn = mPrefs.getBoolean("geoOn", false);
        int geoRate = mPrefs.getInt("geoRate", -1);
        Log.i("fused", "interval" + geoRate);
        if (geoRate >= 0) {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                stopLocationUpdates();
            }
            mLocationRequest.setInterval(geoRate * 1000);
            if (isOn && mGoogleApiClient != null) {
                //restart location request
                startLocationUpdates();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Toggles whether the phone tracks
     * the user or not.
     */
    public void toggleTracking() {
        if (mTracking) {
            mTracking = false;
            stopLocationUpdates();
        } else {
            mTracking = true;
            startLocationUpdates();
        }
    }

    /**
     * @return is the service currently getting locations
     */
    public boolean isTracking() {
        return mTracking;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        Log.i("fused", "Service go by by");
        super.onDestroy();
    }

    /**
     * Binder class used for onBind
     */
    public class LocalBinder extends Binder {

        /**
         * @return the Traccker2 service attached to this Binder
         */
        public Tracker getService() {
            return Tracker.this;
        }
    }
}
