package com.uwt.strugglebus.geotracker;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;

import com.robotium.solo.Solo;
import com.uwt.strugglebus.geotracker.Controller.LoginActivity;
import com.uwt.strugglebus.geotracker.Controller.MyAccount;
import com.uwt.strugglebus.geotracker.Controller.RegistrationActivity;
import com.uwt.strugglebus.geotracker.Controller.ResetPasswordActivity;

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
        solo.unlockScreen();
        solo.assertCurrentActivity("Expected Log-In activity", "LoginActivity");
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
     */
    public void testTextFields() {
        solo.waitForActivity(LoginActivity.class);
        solo.assertCurrentActivity("Wrong activity", LoginActivity.class);

        solo.enterText(0, "alexp8@uw.edu");
        boolean textFound = solo.searchText("alexp8@uw.edu");
        assertTrue("email found", textFound);
        solo.enterText(1, "123456");
        textFound = solo.searchText("123456");
        assertTrue("password inputted", textFound);
    }

    /**
     * Tests the buttons in the Log-In activity.
     */
    public void testButtons() {
        solo.assertCurrentActivity("Wrong activity", LoginActivity.class);

        //log in with bad information and go try to my account
        final Button login_button = (Button) solo.getView(R.id.login);
        solo.clickOnView(login_button);
        solo.assertCurrentActivity("wrong activity", LoginActivity.class);

        //go to registration
        final Button register_button = (Button) solo.getView(R.id.register);
        solo.clickOnView(register_button);
        solo.waitForActivity(RegistrationActivity.class);
        solo.assertCurrentActivity("wrong activity", RegistrationActivity.class);
        solo.goBack();

        //go to the reset password activity, then go back to log-in
        final Button forgot_button = (Button) solo.getView(R.id.forgot_password);
        solo.clickOnView(forgot_button);
        solo.waitForActivity(ResetPasswordActivity.class);
        solo.assertCurrentActivity("wrong activity", ResetPasswordActivity.class);
        solo.goBack();
        solo.waitForActivity(LoginActivity.class);
        solo.assertCurrentActivity("wrong activity", LoginActivity.class);

        //enter the correct information then go to my account
        solo.enterText(0, "zhirzh42@yahoo.com");
        solo.enterText(1, "123456");
        solo.clickOnView(login_button);
        solo.waitForActivity(MyAccount.class);

        //log out of the account
        final Button logout_button = (Button) solo.getView(R.id.logout);
        solo.clickOnView(logout_button);
        solo.waitForActivity(LoginActivity.class);
        solo.assertCurrentActivity("wrong activity", LoginActivity.class);


        //done
    }

    /**
     * Tests the landscape orientation in the Log-In activity.
     */
    public void testOrientation() {

        solo.assertCurrentActivity("Wrong activity", LoginActivity.class);
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