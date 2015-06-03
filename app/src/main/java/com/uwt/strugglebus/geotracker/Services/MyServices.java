package com.uwt.strugglebus.geotracker.Services;

import com.uwt.strugglebus.geotracker.Model.Logger;
import com.uwt.strugglebus.geotracker.Model.Tracker2;

/**
 * Holds a static references to app services that are needed in multiple activities
 */
public class MyServices {
    private static Tracker2 mTracker = null;
    private static Logger mLogger = null;

    /**
     *
     * @param tracker
     */
    public static void setTracker(Tracker2 tracker) {
        mTracker = tracker;
    }

    /**
     *
     * @return
     */
    public static Tracker2 getTracker() {
        return mTracker;
    }

    /**
     *
     * @param log
     */
    public static void setLogger(Logger log) {
        mLogger = log;
    }

    /**
     *
     * @return
     */
    public static Logger getLogger() {
        return mLogger;
    }
}
