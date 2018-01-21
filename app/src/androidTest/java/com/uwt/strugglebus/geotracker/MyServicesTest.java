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

    public void setUp() {
        try {
            super.setUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new MyServices();
    }

    /**
     * Set a new tracker and test that it isn't null.
     */
    public void testSetTracker() {
        MyServices.setTracker(new Tracker());
        assertNotNull(MyServices.getTracker());
    }

    /**
     * Set a new logger and test if it's not null.
     */
    public void testSetLogger() {
        MyServices.setLogger(new Logger());
        assertNotNull(MyServices.getLogger());
    }
}
