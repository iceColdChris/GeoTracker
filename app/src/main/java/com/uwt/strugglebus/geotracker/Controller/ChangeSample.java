package com.uwt.strugglebus.geotracker.Controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.uwt.strugglebus.geotracker.Model.Logger;
import com.uwt.strugglebus.geotracker.Model.MyServices;
import com.uwt.strugglebus.geotracker.R;

/**
 * * Alex Peterson, Chris Fahlin, Josh Moore, Kyle Martens
 * <p/>
 * Uses a seekbar to let the user change how often their location is logged
 * and how often their data is pushed to the server
 * uses shared preferences to store the settings
 */
public class ChangeSample extends AppCompatActivity {

    private boolean mWifiChecked;

    /**
     * {@inheritDoc}
     * <p/>
     * Sets sliders and check boxes to default value, or shared prefs value if it exists
     * If the uses clicks accept and save then the new values are saved in shared prefs
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_sample);

        final int geoMin = 10;
        final SharedPreferences prefs = getSharedPreferences(getString(R.string.SHARED_PREFERENCES),
                Context.MODE_PRIVATE);
        final int current = prefs.getInt("geoRate", -1);
        final int currentPush = prefs.getInt("pushRate", -1);

        final SeekBar geoRate = (SeekBar) findViewById(R.id.geo_bar);
        final TextView geoRateText = (TextView) findViewById(R.id.rate);
        if (current > -1) {
            geoRate.setProgress(current - geoMin);
            geoRateText.setText(" " + (current) + " seconds");
        } else {
            geoRateText.setText(" " + (geoRate.getProgress() + geoMin) + " seconds");
        }
        geoRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                geoRateText.setText(" " + (progress + geoMin) + " seconds");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final SeekBar pushRate = (SeekBar) findViewById(R.id.server_bar);
        final TextView pushRateText = (TextView) findViewById(R.id.pushRate);

        if (currentPush > -1) {
            pushRate.setProgress(currentPush - 1);
            pushRateText.setText(" " + (currentPush) + " hours");
        } else {

            pushRateText.setText(" " + (pushRate.getProgress() + 1) + " hours");
        }
        pushRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * When progress changes, change the text view to reflect it
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pushRateText.setText(" " + (progress + 1) + " hours");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        final CheckBox wifi = (CheckBox) findViewById(R.id.wifi);

        mWifiChecked = prefs.getBoolean("wifi", false);
        wifi.setChecked(mWifiChecked);

        wifi.setOnClickListener(new View.OnClickListener() {
            /**
             * changes shared prefs and toggles wifi boolean
             */
            @Override
            public void onClick(View v) {
                mWifiChecked = !mWifiChecked;
                wifi.setChecked(mWifiChecked);
            }
        });

        Button accept = (Button) findViewById(R.id.accept_changes);
        Button back = (Button) findViewById(R.id.back);

        accept.setOnClickListener(new View.OnClickListener() {

            /**
             * on accept save changes to shared prefs and change settings in logger and tracker
             */
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt("geoRate", geoRate.getProgress() + geoMin);
                edit.putInt("pushRate", pushRate.getProgress() + 1);
                edit.apply();
                MyServices.getTracker().setInterval(geoRate.getProgress() + geoMin);
                Logger.setServiceAlarm(getApplicationContext(), false, pushRate.getProgress() + 1);
                Logger.setServiceAlarm(getApplicationContext(), true, pushRate.getProgress() + 1);
                MyServices.getLogger().toggleWifi(mWifiChecked);
                Intent account = new Intent(getApplicationContext(), MyAccount.class);
                startActivity(account);
                finish();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            /**
             * Go to the my account screen
             */
            @Override
            public void onClick(View v) {
                Intent account = new Intent(getApplicationContext(), MyAccount.class);
                startActivity(account);
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
        getMenuInflater().inflate(R.menu.menu_change_sample, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
