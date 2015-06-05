package com.uwt.strugglebus.geotracker.Model;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.uwt.strugglebus.geotracker.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
*  * * Alex Peterson, Chris Fahlin, Josh Moore, Kyle Martens
 *
 *       * TODO: Javadoc
 *
 */
public class Logger extends IntentService {

    private static final String DB_NAME = "Trajectories";
    private static final String TABLE = "Trajectories";

//    private static Context mContext;

    private final LocalBinder mBinder = new LocalBinder();

    /**
     * TODO: Javadoc
     */
    public Logger() {
        super("Logger");
    }

    /**
     * TODO: Javadoc
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Logger", "onStart");
        commitToWeb();
        return START_STICKY;
    }

    public void commitToWeb() {
        final SQLiteDatabase db = openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);

        SharedPreferences prefs = getSharedPreferences(getString(R.string.SHARED_PREFERENCES)
                , Context.MODE_PRIVATE);
        String uid = prefs.getString("userID", "");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE + "(lat REAL, lon REAL, speed REAL, heading REAL, time BIGINT, uid INT);");
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE + " WHERE uid = \"" + uid + "\";", null);

        if (cursor != null && UtilityTests.isWIFIConnected(getApplicationContext()) && UtilityTests.isCharging(getApplicationContext())) {
//          columns: (lat, lon, speed, heading, time, uid)
            String lat, lon, speed, bearing, url;
            long curTime = 0, firstTime = Integer.MAX_VALUE, lastTime = 0;
            String[] list = new String[cursor.getCount()];

            if  (cursor.moveToFirst()) {
                int i = 0;
                do {
                    double tempLat = cursor.getDouble(cursor.getColumnIndex("lat"));
                    double tempLon = cursor.getDouble(cursor.getColumnIndex("lon"));
                    float tempSpeed = cursor.getFloat(cursor.getColumnIndex("speed"));
                    float tempBearing = cursor.getFloat(cursor.getColumnIndex("heading"));

                    lat = Double.toString(tempLat).replace(".", "%2E");
                    lon = Double.toString(tempLon).replace(".", "%2E");
                    speed = Float.toString(tempSpeed).replace(".", "%2E");
                    bearing = Float.toString(tempBearing).replace(".", "%2E");

                    curTime = cursor.getLong(cursor.getColumnIndex("time"));
                    if(curTime < firstTime) {
                        firstTime = curTime;
                    }
                    if(curTime > lastTime) {
                        lastTime = curTime;
                    }

                    url = "http://450.atwebpages.com/logAdd.php?lat=" + lat +
                            "&lon=" + lon +
                            "&speed=" + speed +
                            "&heading=" + bearing +
                            "&timestamp=" + curTime +
                            "&source=" + uid;
                    list[i] = url;
                    i++;
                } while (cursor.moveToNext());
            }

            DownloadWebPageTask task = new DownloadWebPageTask();
            task.execute(list);

            String delete = "DELETE FROM " + TABLE + " WHERE time BETWEEN " + firstTime +
                    " AND " + lastTime + ";";
            db.execSQL(delete);
            Log.w("sqlTestDelete", delete);
            cursor.close();
        }
    }


    /**
     * TODO: Javadoc
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("Logger", "handle intent");
    }

    public static void setServiceAlarm(Context context, boolean isOn, int interval) {
        Intent i = new Intent(context, Tracker.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        mContext = context;

        if (isOn) {
            alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis()
                    , interval, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Binder class used for onBind
     */
    public class LocalBinder extends Binder {

        /**
         * @return the Traccker2 service attached to this Binder
         */
        public Logger getService(){
            return Logger.this;
        }
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
                    JSONObject obj = new JSONObject(result);
                    String success = obj.getString("result");
                    if(success != null && success.equals("success")) {
                        Log.i("push success", "push successful");
                    } else {
//                        Toast.makeText(mContext, obj.getString("error"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.i("json exception", e.getMessage());
                }
            }
        }
    }
}
