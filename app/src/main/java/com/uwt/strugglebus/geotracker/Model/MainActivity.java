package com.uwt.strugglebus.geotracker.Model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.uwt.strugglebus.geotracker.R;
import com.uwt.strugglebus.geotracker.View.LoginActivity;

/**
 * * Alex Peterson, Chris Fahlin, Josh Moore, Kyle Martens
 *
 * This is the main activity class that the app launches with.
 * The class launches the registration or login activity depending if the user is new or not.
 */
public class MainActivity extends ActionBarActivity {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = getSharedPreferences(getString(R.string.SHARED_PREFERENCES), Context.MODE_PRIVATE);
        int uid = prefs.getInt("uid", -1);
        if(uid != -1) { //user exists in shared prefs
            Intent registration = new Intent(this, Registration.class);
            startActivity(registration);
        } else {
            Intent login = new Intent(this, LoginActivity.class);
            startActivity(login);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}