package com.uwt.strugglebus.geotracker.Model;

/**
 * Created by Josh on 5/30/2015.
 */
public class MyServices {
    private static Tracker2 mTracker = null;

    public static void setTracker(Tracker2 tracker) {
        mTracker = tracker;
    }

    public static Tracker2 getTracker() {
        return mTracker;
    }
}
