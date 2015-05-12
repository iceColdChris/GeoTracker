package com.uwt.strugglebus.geotracker.Model;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.uwt.strugglebus.geotracker.R;
import com.uwt.strugglebus.geotracker.View.MyAccount;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * * Alex Peterson, Chris Fahlin, Josh Moore, Kyle Martens
 *
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 *
 * Service that gets current GPS location every interval and stores it in a sqlite db
 */
public class Tracker extends IntentService {

    private static final String DB_NAME = "Trajectories";
    private static final String TABLE = "Locations";
    private static final int POLL_INTERVAL = 2000;

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
        System.out.println("check not open");
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
//        super.onStartCommand(intent, flags, startId);
        //Log.i("Tracker", "service starting");
        System.out.println("yay start command");

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
        if (loc != null) {
            System.out.println(loc.toString());
            Log.w("nsa.gov", loc.toString());
            // (lat, lon, speed, heading, time)
            String insert = "INSERT INTO " + TABLE + " VALUES(" + loc.getLatitude() + ", " +
                    loc.getLongitude() + ", " + loc.getSpeed() + ", " + loc.getBearing()
                    + ", " + loc.getTime() + ");";
            db.execSQL(insert);
            Log.w("sqlTest", insert);

//            SharedPreferences prefs = getSharedPreferences(getString(R.string.SHARED_PREFERENCES)
//                    , Context.MODE_PRIVATE);
//            String uid = prefs.getString("userID", "");
//            System.out.println(uid);
//            DownloadWebPageTask task = new DownloadWebPageTask();
//            String url = "http://450.atwebpages.com/logAdd.php?lat=" + loc.getLatitude() +
//                            "&lon=" + loc.getLongitude() +
//                            "&speed=" + loc.getSpeed() +
//                            "&heading=" + loc.getBearing() +
//                            "&timestamp=" + loc.getTime() +
//                            "&source=" + uid;
//            task.execute(url);
        }
        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        return START_STICKY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
 * This is a private helper class that is
 * in charge of connecting to the web
 * services as an Asyncronous Task.
 */
    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {


        /*
         * Inherited from
         * AsyncTask class
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /*
         * Gets the response string
         * from the webservice.
         */
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            for (String url : urls) {
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                try {
                    HttpResponse execute = client.execute(httpGet);
                    InputStream content = execute.getEntity().getContent();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s;
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return response;
        }

        /*
         * Checks if the user has
         * entered the correct credentials
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            mProgressDialog.dismiss();
            if (result != null) {
                try {
                    System.out.println(result);
                    JSONObject obj = new JSONObject(result);
                    String success = obj.getString("result");
                    System.out.println(success);
                    if(success != null && success.equals("success")) {
                        System.out.println("YAY!");
                    } else {
                        System.out.println("BOO!");
                        System.out.println(obj.getString("error"));
//                        Toast.makeText(mContext, obj.getString("error"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    System.out.println("JSON Exception "+ e.getMessage());
                }
            }
        }
    }
}
