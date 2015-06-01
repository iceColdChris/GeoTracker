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
    public void testTextFields() {
        solo.unlockScreen();
        solo.assertCurrentActivity("Expected Registration activity", Registration.class);

        solo.enterText(0, "alexp8@uw.edu");
        boolean textFound = solo.searchText("alexp8@uw.edu");
        assertEquals("email found", textFound);
        solo.enterText(1, "123456");
        textFound = solo.searchText("123456");
        assertEquals("password inputted", "123456");
        solo.enterText(2, "123456");
        textFound = solo.searchText("123456");
        assertEquals("confirmation password inputted", "123456");
        solo.enterText(3, "Germany");
        textFound = solo.searchText("Germany");
        assertEquals("security answer inputted", "Germany");



       }

    /**
     * Tests the buttons in the Registration activity.
     * @throws Exception
     */
    public void testButtons() throws Exception {

        Button register_button = (Button) solo.getView(R.id.reg_accept);
        Button cancel_button = (Button) solo.getView(R.id.reg_cancel);
    }

    /**
     * Tests the landscape orientation in the Registration activity.
     * @throws Exception
     */
    public void testOrientation() throws Exception {

        solo.enterText(0, "amp1993@gmail.com");
        solo.enterText(1, "password");
        solo.enterText(2, "password");
        solo.enterText(3, "Seattle");

        solo.setActivityOrientation(Solo.LANDSCAPE);

        boolean textFound = solo.searchText("amp1993@gmail.com");
        assertEquals("email found", textFound);
        textFound = solo.searchText("password");
        assertEquals("password inputted", textFound);
        textFound = solo.searchText("Seattle");
        assertEquals("security answer inputted", textFound);
    }
}