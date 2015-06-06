package com.uwt.strugglebus.geotracker;

import com.uwt.strugglebus.geotracker.Model.Logger;
import com.uwt.strugglebus.geotracker.Model.MyServices;
import com.uwt.strugglebus.geotracker.Model.Tracker;

import junit.framework.TestCase;

/**
 * This class tests the MyServices tracker and logger methods.
 * Created by Alex on 6/4/2015.
 */
public class MyServicesTest extends TestCase {

    private MyServices my_services;

    public void setUp() {
        my_services = new MyServices();
    }

    /**
     * Set a new tracker and test that it isn't null.
     */
    public void testSetTracker() {
        my_services.setTracker(new Tracker());
        assertNotNull(my_services.getTracker());
    }

    /**
     * Set a new logger and test if it's not null.
     */
    public void testSetLogger() {
        my_services.setLogger(new Logger());
        assertNotNull(my_services.getLogger());
    }
}
