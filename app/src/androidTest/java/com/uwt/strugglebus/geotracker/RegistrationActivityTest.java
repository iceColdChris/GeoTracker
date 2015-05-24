package com.uwt.strugglebus.geotracker;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;

import com.robotium.solo.Solo;
import com.uwt.strugglebus.geotracker.Controller.MyAccount;
import com.uwt.strugglebus.geotracker.Controller.Registration;

/**
 * A test class using Robotium for the Registration Activity.
 * Created by Alex on 5/23/2015.
 */
public class RegistrationActivityTest extends ActivityInstrumentationTestCase2 {

    private Solo solo;

    public RegistrationActivityTest() {
        super(Registration.class);
    }

    /**
     * Set up the activity.
     * @throws Exception
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    /**
     * Finish the activities.
     * @throws Exception
     */
    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        super.tearDown();
    }

    /**
     * Test the text fields in the Registration activity.
     * @throws Exception
     */
    public void testTextFields() throws Exception {
        solo.unlockScreen();
        solo.assertCurrentActivity("Expected Registration activity", "Registration");




    }

    /**
     * Tests the buttons in the Registration activity.
     * @throws Exception
     */
    public void testButtons() throws Exception {


    }

    /**
     * Tests the landscape orientation in the Registration activity.
     * @throws Exception
     */
    public void testOrientation() throws Exception {

        solo.enterText(0, "alexp8@uw.edu");

    }
}