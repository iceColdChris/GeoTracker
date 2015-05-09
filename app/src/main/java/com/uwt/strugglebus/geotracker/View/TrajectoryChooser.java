package com.uwt.strugglebus.geotracker.View;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.uwt.strugglebus.geotracker.R;
import com.uwt.strugglebus.geotracker.View.DatePickerFragment;
import com.uwt.strugglebus.geotracker.View.TimePickerFragment;

import java.util.Calendar;
import java.util.Date;


public class TrajectoryChooser extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trajectory_chooser);
        Button startDate = (Button) findViewById(R.id.start_date_button);
        Button endDate = (Button) findViewById(R.id.end_date_button);
        Button startTime = (Button) findViewById(R.id.start_time_button);
        Button endTime = (Button) findViewById(R.id.end_time_button);
        Button accept = (Button) findViewById(R.id.accept_view_traject);
        Button cancel = (Button) findViewById(R.id.cancel_view_traject);

        Date d = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        ((TextView) findViewById(R.id.end_date_text)).setText(c.get(Calendar.MONTH) + "/"
                + c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.YEAR));
        ((TextView) findViewById(R.id.end_time_text)).setText(c.get(Calendar.HOUR_OF_DAY) + ":"
                + c.get(Calendar.MINUTE));
        final Activity that = this;
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment frag = new DatePickerFragment();
                frag.show(that.getFragmentManager(), "startDatePicker");
            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment frag = new DatePickerFragment();
                frag.show(that.getFragmentManager(), "endDatePicker");
            }
        });

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment frag = new TimePickerFragment();
                frag.show(that.getFragmentManager(), "startTimePicker");
            }
        });
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment frag = new TimePickerFragment();
                frag.show(that.getFragmentManager(), "endTimePicker");
            }
        });
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent traject = new Intent(getApplicationContext(), Trajectories.class);
                startActivity(traject);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent account = new Intent(getApplicationContext(), MyAccount.class);
                startActivity(account);
                finish();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trajectory_chooser, menu);
        return true;
    }

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
