package com.uwt.strugglebus.geotracker;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;

import com.robotium.solo.Solo;
import com.uwt.strugglebus.geotracker.Controller.LoginActivity;
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
        solo.unlockScreen();
        solo.assertCurrentActivity("Expected Registration activity", Registration.class);
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
     */
    public void testTextFields() {

        solo.enterText(0, "alexp8@uw.edu");
        boolean textFound = solo.searchText("alexp8@uw.edu");
        assertTrue("email found", textFound);
        solo.enterText(1, "123456");
        textFound = solo.searchText("123456");
        assertTrue("password inputted", textFound);
        solo.enterText(2, "123456");
        textFound = solo.searchText("123456");
        assertTrue("confirmation password inputted", textFound);
        solo.enterText(3, "Germany");
        textFound = solo.searchText("Germany");
        assertTrue("security answer inputted", textFound);
    }

    /**
     * Tests the buttons in the Registration activity.
     */
    public void testButtons() {

        final Button register_button = (Button) solo.getView(R.id.reg_accept);
        final Button cancel_button = (Button) solo.getView(R.id.reg_cancel);

        solo.clickOnView(register_button);
        solo.waitForActivity(Registration.class);
        solo.assertCurrentActivity("Expected Registration Activity", Registration.class);

        solo.clickOnView(cancel_button);
        solo.waitForActivity(LoginActivity.class);
        solo.assertCurrentActivity("Expected Login Activity", LoginActivity.class);

    }

    /**
     * Tests the landscape orientation in the Registration activity.
     */
    public void testOrientation() {

        solo.enterText(0, "amp1993@gmail.com");
        solo.enterText(1, "password");
        solo.enterText(2, "password");
        solo.enterText(3, "Seattle");

        solo.setActivityOrientation(Solo.LANDSCAPE);

        boolean textFound = solo.searchText("amp1993@gmail.com");
        assertTrue("email found", textFound);
        textFound = solo.searchText("password");
        assertTrue("password inputted", textFound);
        textFound = solo.searchText("Seattle");
        assertTrue("security answer inputted", textFound);
    }
}