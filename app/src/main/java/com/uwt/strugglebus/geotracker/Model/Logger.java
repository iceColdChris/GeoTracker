package com.uwt.strugglebus.geotracker.Model;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
*  * * Alex Peterson, Chris Fahlin, Josh Moore, Kyle Martens
 *
 *       * TODO: Javadoc
 *
 */
public class Logger extends IntentService {

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
        return START_STICKY;
    }


    /**
     * TODO: Javadoc
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("Logger", "handle intent");
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
}
