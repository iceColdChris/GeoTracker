package com.uwt.strugglebus.geotracker.View;

import android.app.Activity;
import android.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.uwt.strugglebus.geotracker.R;
import com.uwt.strugglebus.geotracker.View.DatePickerFragment;
import com.uwt.strugglebus.geotracker.View.TimePickerFragment;


public class TrajectoryChooser extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trajectory_chooser);
        Button startDate = (Button) findViewById(R.id.start_date_button);
        Button endDate = (Button) findViewById(R.id.end_date_button);
        Button startTime = (Button) findViewById(R.id.start_time_button);
        Button endTime = (Button) findViewById(R.id.end_time_button);
        final Activity that = this;
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment frag = new DatePickerFragment();
                Bundle b = new Bundle();
                frag.setArguments(b);
                frag.show(that.getFragmentManager(), "datePicker");
            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment frag = new DatePickerFragment();
                frag.show(that.getFragmentManager(), "datePicker");
            }
        });

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment frag = new TimePickerFragment();
                frag.show(that.getFragmentManager(), "timePicker");
            }
        });
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment frag = new TimePickerFragment();
                frag.show(that.getFragmentManager(), "timePicker");
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
