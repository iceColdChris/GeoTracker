package com.uwt.strugglebus.geotracker;

import android.content.SharedPreferences;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.Button;
import android.widget.TextView;

import com.robotium.solo.Solo;
import com.uwt.strugglebus.geotracker.Controller.LoginActivity;

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
     * Test the text fields and buttons in the Log-In activity.
     * @throws Exception
     */
    public void test_MainActivityChangeTextView_hi() throws Exception {
        solo.unlockScreen();
        solo.assertCurrentActivity("Expected Log-In activity", "LoginActivity");

        Button accept_button = (Button) solo.getView(R.id.login);
        Button cancel_button = (Button) solo.getView(R.id.register);
        Button forgot_button = (Button) solo.getView(R.id.forgot_password);

        TextView email_field = (TextView) solo.getView(R.id.email);
        TextView password_field = (TextView) solo.getView(R.id.password);

        solo.enterText(0, "alexp8@uw.edu");
        assertEquals("alexp8@uw.edu", email_field.getText());
        solo.enterText(1, "123456");
        assertEquals("123456", email_field.getText());


    }

    public void testEquals() throws Exception {

    }

    public void testBoolean() throws Exception {
        assertTrue(true);
    }
}
