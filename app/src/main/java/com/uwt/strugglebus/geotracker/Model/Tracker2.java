package com.uwt.strugglebus.geotracker.Model;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.uwt.strugglebus.geotracker.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.Date;

/**
 *  Alex Peterson, Chris Fahlin, Josh Moore, Kyle Martens
 *
 *  Service that uses Google Play Fused Locations services to get user's geo-location every interval
 *  And saves the location points in a sqli database
 */
public class Tracker2 extends IntentService implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String DB_NAME = "Trajectories";
    private static final String TABLE = "Trajectories";

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;

    /**
     * Represents the previous geographical location.
     */
    protected Location mPrevLocation;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;

    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;

    private final IBinder mBinder = new LocalBinder();
    private SharedPreferences mPrefs;
    private boolean mTracking;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public Tracker2() {
        super("Tracker2");
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
        if(mGoogleApiClient == null) {
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
        if(mGoogleApiClient == null) {
            buildGoogleApiClient();
            mGoogleApiClient.connect();
        }
        mRequestingLocationUpdates = true;
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        Log.i("fused", "build google api");

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS * 2);
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
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    public void startLocationUpdates() {
        Log.i("fused", "starting location updates");
        mPrefs.edit().putBoolean("geoOn", true).apply();
        mTracking = true;
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
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
        Log.i("fused", "time from last update" +DateFormat.getTimeInstance().format(new Date()) + ", " + mLastUpdateTime);
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

        if (mCurrentLocation != null && mPrevLocation != null &&
                mCurrentLocation.getLatitude() != mPrevLocation.getLatitude() &&
                mCurrentLocation.getLongitude() != mPrevLocation.getLongitude()) {
            String uid = mPrefs.getString("userID", "");
            final SQLiteDatabase db = openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE + "(lat REAL, lon REAL, speed REAL, heading REAL, time BIGINT, uid VARCHAR(50));");
            String insert = "INSERT INTO " + TABLE + " VALUES (" + mCurrentLocation.getLatitude() +
                    ", " + mCurrentLocation.getLongitude() +
                    ", " + mCurrentLocation.getSpeed() +
                    ", " + mCurrentLocation.getBearing() +
                    ", " + (mCurrentLocation.getTime() / 1000) +
                    ", \"" + uid + "\");";
            db.execSQL(insert);
            Log.w("sqlTestDelete", insert);
        }
        mPrevLocation = mCurrentLocation;
    }

    /**
     * {@inheritDoc}
     * print out error if connection failed
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("fused", "connection failed");
        Log.i("error", "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());

    }

    /**
     * Change the location update time
     * @param interval time until next update in seconds
     */
    public void setInterval(int interval) {
        SharedPreferences.Editor edit = mPrefs.edit();
        edit.putInt("geoRate", interval);
        edit.apply();
        boolean isOn = mPrefs.getBoolean("geoOn", false);
        stopLocationUpdates();
        mLocationRequest.setInterval(interval * 2000);
        if(isOn && mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            //restart location request
            startLocationUpdates();
        }
    }

    /**
     * based on settings in shared preferences starts / stops the service and adjusts the interval
     */
    public void updateState() {
        boolean isOn = mPrefs.getBoolean("geoOn", false);
        int geoRate = mPrefs.getInt("geoRate", -1);
        Log.i("fused", "interval" + geoRate );
        if(geoRate >= 0) {
            if(mGoogleApiClient != null  && mGoogleApiClient.isConnected()) {
                stopLocationUpdates();
            }
            mLocationRequest.setInterval(geoRate * 1000);
            if(isOn && mGoogleApiClient != null) {
                //restart location request
                startLocationUpdates();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void toggleTracking() {
        if(mTracking) {
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
    public boolean isTracking(){
        return mTracking;
    }

    /**
     * Binder class used for onBind
     */
    public class LocalBinder extends Binder {

        /**
         * @return the Traccker2 service attached to this Binder
         */
        public Tracker2 getService(){
            return Tracker2.this;
        }
    }

    @Override
    public void onDestroy(){
        Log.i("fused", "Service go by by");
        super.onDestroy();
    }
}
