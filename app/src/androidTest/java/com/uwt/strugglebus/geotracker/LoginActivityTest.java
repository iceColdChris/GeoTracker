package com.uwt.strugglebus.geotracker;

import android.content.SharedPreferences;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.Button;
import android.widget.TextView;

import com.robotium.solo.Solo;
import com.uwt.strugglebus.geotracker.Controller.LoginActivity;
import com.uwt.strugglebus.geotracker.Controller.MyAccount;
import com.uwt.strugglebus.geotracker.Controller.ResetPassword;

/**
 * A test class using Robotium for the Log-in Activity.
 * Created by Alex on 5/16/2015.
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2 {

    private Solo solo;

    public LoginActivityTest() {
        super(LoginActivity.class);
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
     * Test the text fields in the Log-In activity.
     * @throws Exception
     */
    public void testTextFields() throws Exception {
        solo.unlockScreen();
        solo.assertCurrentActivity("Expected Log-In activity", "LoginActivity");

        solo.enterText(0, "alexp8@uw.edu");
        boolean textFound = solo.searchText("alexp8@uw.edu");
        assertTrue("email found", textFound);
        solo.enterText(1, "123456");
        textFound = solo.searchText("123456");
        assertTrue("password inputted", textFound);


    }

    /**
     * Tests the buttons in the Log-In activity.
     * @throws Exception
     */
    public void testButtons() {

        //go to the reset password activity, then go back to log-in
        final Button forgot_button = (Button) solo.getView(R.id.forgot_password);
        solo.clickOnView(forgot_button);
        solo.waitForActivity(ResetPassword.class);
        solo.assertCurrentActivity("wrong activity", ResetPassword.class);
        solo.goBack();
        solo.waitForActivity(LoginActivity.class);
        solo.assertCurrentActivity("wrong activity", LoginActivity.class);


        //log in and go to my account
        final Button accept_button = (Button) solo.getView(R.id.login);
        solo.clickOnView(accept_button);
        solo.waitForActivity(MyAccount.class);
        solo.assertCurrentActivity("wrong activity", MyAccount.class);
        solo.goBack();
        solo.waitForActivity(LoginActivity.class);
        solo.assertCurrentActivity("wrong activity", LoginActivity.class);
        
    }

    /**
     * Tests the landscape orientation in the Log-In activity.
     * @throws Exception
     */
    public void testOrientation() throws Exception {

        solo.enterText(0, "alexp8@uw.edu");
        solo.enterText(1, "123456");

        solo.setActivityOrientation(Solo.LANDSCAPE);

        boolean textFound = solo.searchText("alexp8@uw.edu");
        assertTrue("email found", textFound);
        textFound = solo.searchText("123456");
        assertTrue("password inputted", textFound);
        solo.setActivityOrientation(Solo.PORTRAIT);
    }
}