package com.uwt.strugglebus.geotracker.Controller;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.uwt.strugglebus.geotracker.Model.LocationBroadcastReceiver;
import com.uwt.strugglebus.geotracker.Model.LocationLog;
import com.uwt.strugglebus.geotracker.Model.Logger;
import com.uwt.strugglebus.geotracker.Services.MyServices;
import com.uwt.strugglebus.geotracker.Model.Tracker2;
import com.uwt.strugglebus.geotracker.R;

/**
 * * Alex Peterson, Chris Fahlin, Josh Moore, Kyle Martens
 *
 * This class is in charge of keeping track of all
 * the user's data. It is also the main screen after
 * login.
 */
public class MyAccount extends ActionBarActivity {

    private static final int HOUR = 3600000;

    private LocationLog mLocationLog;
    private Tracker2 mTracker;
    private Logger mLogger;
    private SharedPreferences mPrefs;

    /**
     * TODO: COMMENT
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Tracker2.LocalBinder binder = (Tracker2.LocalBinder) service;
            mTracker = binder.getService();
            MyServices.setTracker(mTracker);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * TODO: COMMENT
     */
    private ServiceConnection mLogConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Logger.LocalBinder binder = (Logger.LocalBinder) service;
            mLogger = binder.getService();
            MyServices.setLogger(mLogger);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


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
        mPrefs = getSharedPreferences(getString(R.string.SHARED_PREFERENCES),
                Context.MODE_PRIVATE);

        mLocationLog = new LocationLog();
        setContentView(R.layout.activity_my_account);
        SharedPreferences.Editor edit = mPrefs.edit();
        edit.putBoolean("geoOn", true);
        edit.apply();
        //create and bind service
        Intent i = new Intent(this, Tracker2.class);
        getApplicationContext().startService(i);
        getApplicationContext().bindService(i, mConnection, Context.BIND_AUTO_CREATE);
        //create and bind logger
        Intent il = new Intent(this, Logger.class);
        getApplicationContext().startService(il);
        getApplicationContext().bindService(il, mLogConnection, Context.BIND_AUTO_CREATE);

        TextView email = (TextView) findViewById(R.id.account_email);

        ComponentName receiver = new ComponentName(getApplicationContext(), LocationBroadcastReceiver.class);
        PackageManager pm = getApplicationContext().getPackageManager();


        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);


        //int uid = mPrefs.getInt("uid", -1);
        String mEmail = mPrefs.getString(getString(R.string.email), "email");
        //String mPass = mPrefs.getString(getString(R.string.password), "password");
        //String mQuestion = mPrefs.getString(getString(R.string.security_q), "question");
        //String mAnswer = mPrefs.getString(getString(R.string.security_a), "answer");

        email.setText(mEmail);

        Button map = (Button) findViewById(R.id.view_map);
        Button traject = (Button) findViewById(R.id.view_traject);
        Button changePass = (Button) findViewById(R.id.change_pass);
        Button logout = (Button) findViewById(R.id.logout);
        Button sample = (Button) findViewById(R.id.sample_rate);
        final Button toggleTracker = (Button) findViewById(R.id.toggle_tracker);
        Button commitToWeb = (Button) findViewById(R.id.commit_to_web);

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent map = new Intent(getApplicationContext(), MapActivity.class);
                map.putExtra("locations", mLocationLog);
                startActivity(map);
            }
        });
        toggleTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tracker2 track = MyServices.getTracker();

                if(track != null) {
                    track.toggleTracking();
                    String s = "on";
                    if (track.isTracking()) {
                        toggleTracker.setText(R.string.stop_tracker);
                        Logger.setServiceAlarm(getApplicationContext(), true, HOUR);
                    } else {
                        s = "off";
                        toggleTracker.setText(R.string.start_tracker);
                        Logger.setServiceAlarm(getApplicationContext(), false, 0);
                    }
                    Toast.makeText(getApplicationContext(), "Tracker is now " + s, Toast.LENGTH_LONG).show();
                }
            }
        });
        traject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent traject = new Intent(getApplicationContext(), TrajectoryChooser.class);
                startActivity(traject);
            }
        });
        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent change = new Intent(getApplicationContext(), ResetPassword.class);
                startActivity(change);
            }
        });
        sample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeRate = new Intent(getApplicationContext(), ChangeSample.class);
                startActivity(changeRate);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putString("userID", null);
                editor.apply();
                mTracker.stopLocationUpdates();
                Logger.setServiceAlarm(getApplication(), false, 0);
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(login);
                finish();
            }
        });
        commitToWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger log = MyServices.getLogger();
                if(log != null) {
                    log.commitToWeb();
                }
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