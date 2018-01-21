package com.uwt.strugglebus.geotracker;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;

import com.robotium.solo.Solo;
import com.uwt.strugglebus.geotracker.Controller.LoginActivity;
import com.uwt.strugglebus.geotracker.Controller.ResetPasswordActivity;

    /**
     * A test class using Robotium for the ResetPasswordActivityTest Activity.
     * Created by Alex on 6/1/2015.
     */
    public class ResetPasswordActivityTest extends ActivityInstrumentationTestCase2 {

        private Solo solo;

        public ResetPasswordActivityTest() {
            super(ResetPasswordActivity.class);
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
            solo.assertCurrentActivity("Expected ResetPasswordActivity activity", ResetPasswordActivity.class);
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
         * Test the text fields in the Reset Password activity.
         */
        public void testTextFields() {

            solo.enterText(0, "amp1993@gmail.com");
            boolean text_found = solo.searchEditText("amp1993@gmail.com");
            assertTrue("email found", text_found);



        }

        /**
         * Tests the buttons in the ResetPasswordActivity activity.
         */
        public void testButtons() {

            final Button cancel_button = (Button) solo.getView(R.id.reset_cancel);
            solo.clickOnView(cancel_button);
            solo.waitForActivity(LoginActivity.class);
            solo.assertCurrentActivity("Expected Login Activity class", LoginActivity.class);

        }

        /**
         * Tests the landscape orientation in the Reset Password activity.
         */
        public void testOrientation() {

            solo.enterText(0, "petersonalex1993@yahoo.com");
            solo.setActivityOrientation(Solo.LANDSCAPE);

            boolean text_found = solo.searchText("petersonalex1993@yahoo.com");
            assertTrue("email found in landscape orientation", text_found);
        }
}