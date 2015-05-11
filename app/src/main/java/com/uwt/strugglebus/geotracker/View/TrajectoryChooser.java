package com.uwt.strugglebus.geotracker.View;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.uwt.strugglebus.geotracker.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * This class is in charge of letting
 * the user determine what trajectory
 * they want to see.
 */
public class TrajectoryChooser extends ActionBarActivity {

    /**
     * {@inheritDoc}
     *
     * Ontop of the above functionality
     * this method sets up the trajectory
     * chooser view.
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trajectory_chooser);
        final Button startDate = (Button) findViewById(R.id.start_date_button);
        Button endDate = (Button) findViewById(R.id.end_date_button);
        final Button startTime = (Button) findViewById(R.id.start_time_button);
        final Button endTime = (Button) findViewById(R.id.end_time_button);
        Button accept = (Button) findViewById(R.id.accept_view_traject);
        Button cancel = (Button) findViewById(R.id.cancel_view_traject);

        Date d = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        final TextView startDateText = (TextView) findViewById(R.id.start_date_text);
        startDateText.setText(c.get(Calendar.MONTH) + "/" + c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.YEAR));
        final TextView startTimeText = (TextView)findViewById(R.id.start_time_text);
        startTimeText.setText(0 + "" + 0 + ":" + 0 + "" + 0);

        final TextView endDateText = (TextView) findViewById(R.id.end_date_text);
        endDateText.setText(c.get(Calendar.MONTH) + "/" + c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.YEAR));
        final TextView endTimeText = (TextView)findViewById(R.id.end_time_text);
        endTimeText.setText(c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));

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
                String[] startDateS = startDateText.getText().toString().split("/");
                String[] startTimeS = startTimeText.getText().toString().split(":");

                Calendar startCal = new GregorianCalendar(Integer.parseInt(startDateS[0]), Integer.parseInt(startDateS[0]),
                        Integer.parseInt(startDateS[0]), Integer.parseInt(startTimeS[0]),Integer.parseInt(startTimeS[1]));

                String[] endDateS = endDateText.getText().toString().split("/");
                String[] endTimeS = endTimeText.getText().toString().split(":");
                Calendar endCal = new GregorianCalendar(Integer.parseInt(startDateS[0]), Integer.parseInt(startDateS[0]) - 1,
                        Integer.parseInt(startDateS[0]), Integer.parseInt(startTimeS[0]),Integer.parseInt(startTimeS[1]));
                if(startCal.getTime().getTime() > endCal.getTime().getTime()) {
                    //TODO: put int strings
                    Toast.makeText(getApplicationContext(),"start time after end time" , Toast.LENGTH_SHORT);
                } else {
                    Intent traject = new Intent(getApplicationContext(), Trajectories.class);
                    traject.putExtra("startTime", startCal.getTime().getTime());
                    traject.putExtra("endTime", endCal.getTime().getTime());
                    startActivity(traject);
                    finish();
                }

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


    /**
     * {@inheritDoc}
     * @param menu
     * @return True/False
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trajectory_chooser, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     * @param item
     * @return
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
