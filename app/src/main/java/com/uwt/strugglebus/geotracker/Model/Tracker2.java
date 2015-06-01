package com.uwt.strugglebus.geotracker.Model;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
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

import java.text.DateFormat;
import java.util.Date;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Comments
 * helper methods.
 */
public class Tracker2 extends IntentService implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

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
     *
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
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        updateState();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    public void stopLocationUpdates() {
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

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("fused", "connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("fused", "location changed" + location.toString());
        mCurrentLocation = location;
        Log.i("fused", "time from last update" +DateFormat.getTimeInstance().format(new Date()) + ", " + mLastUpdateTime);
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
    }

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

    public boolean isTracking(){
        return mTracking;
    }

    public class LocalBinder extends Binder {

        public Tracker2 getService(){
            return Tracker2.this;
        }

    }
}
