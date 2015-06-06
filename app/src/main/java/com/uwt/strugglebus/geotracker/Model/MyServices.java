package com.uwt.strugglebus.geotracker.Model;

/**
 * Holds a static references to app services that are needed in multiple activities
 */
public class MyServices {
    private static Tracker mTracker = null;
    private static Logger mLogger = null;

    /**
     * Return the tracker.
     *
     * @return
     */
    public static Tracker getTracker() {
        return mTracker;
    }

    /**
     * Set the tracker.
     *
     * @param tracker
     */
    public static void setTracker(Tracker tracker) {
        mTracker = tracker;
    }

    /**
     * Pizza.
     *
     * @return
     */
    public static Logger getLogger() {
        return mLogger;
    }

    /**
     * Set the logger.
     *
     * @param log
     */
    public static void setLogger(Logger log) {
        mLogger = log;
    }
}
