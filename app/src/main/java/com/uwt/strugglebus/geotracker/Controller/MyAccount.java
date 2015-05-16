package com.uwt.strugglebus.geotracker.Controller;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.uwt.strugglebus.geotracker.Model.LocationLog;
import com.uwt.strugglebus.geotracker.Model.Tracker;
import com.uwt.strugglebus.geotracker.Model.LocationBroadcastReceiver;
import com.uwt.strugglebus.geotracker.R;

/**
 * * Alex Peterson, Chris Fahlin, Josh Moore, Kyle Martens
 *
 * This class is in charge of keeping track of all
 * the user's data. It is also the main screen after
 * login.
 */
public class MyAccount extends ActionBarActivity {

    private LocationLog mLocationLog;

    /**
     * {@inheritDoc}
     *
     * On top of the above functionality
     * this method sets up the account
     * page that the user sees upon logging in.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationLog = new LocationLog();
        setContentView(R.layout.activity_my_account);
        TextView email = (TextView) findViewById(R.id.account_email);

        ComponentName receiver = new ComponentName(getApplicationContext(), LocationBroadcastReceiver.class);
        PackageManager pm = getApplicationContext().getPackageManager();


        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        Tracker.setServiceAlarm(getApplicationContext(), true);

        final SharedPreferences prefs = getSharedPreferences(getString(R.string.SHARED_PREFERENCES),
                getApplicationContext().MODE_PRIVATE);
        int uid = prefs.getInt("uid", -1);
        String mEmail = prefs.getString(getString(R.string.email), "email");
        String mPass = prefs.getString(getString(R.string.password), "password");
        String mQuestion = prefs.getString(getString(R.string.security_q), "question");
        String mAnswer = prefs.getString(getString(R.string.security_a), "answer");

        email.setText(mEmail);

        Button map = (Button) findViewById(R.id.view_map);
        Button traject = (Button) findViewById(R.id.view_traject);
        Button setZones = (Button) findViewById(R.id.set_zones);
        Button changePass = (Button) findViewById(R.id.change_pass);
        Button changeSec = (Button) findViewById(R.id.change_sec_a);
        Button logout = (Button) findViewById(R.id.logout);

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent map = new Intent(getApplicationContext(), MapActivity.class);
                map.putExtra("locations", mLocationLog);
                startActivity(map);
            }
        });
        traject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent traject = new Intent(getApplicationContext(), TrajectoryChooser.class);
                startActivity(traject);
            }
        });
        setZones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent change = new Intent(getApplicationContext(), ResetPassword.class);
                startActivity(change);
            }
        });
        changeSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("userID", null);
                editor.apply();
                Tracker.setServiceAlarm(getApplicationContext(), false);

                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(login);
                finish();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_account, menu);
        return true;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_logout:
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(login);
                finish();
                break;
            case R.id.action_map:
                Intent map = new Intent(getApplicationContext(), MapActivity.class);
                map.putExtra("locations", mLocationLog);
                startActivity(map);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


}