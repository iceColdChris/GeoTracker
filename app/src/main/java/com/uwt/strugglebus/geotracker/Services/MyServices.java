package com.uwt.strugglebus.geotracker.Services;

import com.uwt.strugglebus.geotracker.Model.Tracker2;

/**
 * Holds a static references to app services that are needed in multiple activities
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
