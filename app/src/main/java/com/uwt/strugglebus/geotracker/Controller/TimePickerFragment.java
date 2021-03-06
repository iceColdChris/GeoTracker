package com.uwt.strugglebus.geotracker.Controller;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import com.uwt.strugglebus.geotracker.R;

import java.util.Calendar;

/**
 * * Alex Peterson, Chris Fahlin, Josh Moore, Kyle Martens
 * <p/>
 * This class is in charge of
 * the time picker the user
 * sees when choosing what
 * time they want to show.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    /**
     * {@inheritDoc}
     * <p/>
     * On top of the above functionality
     * this method sets up a calendar
     * object as well as a time picker
     * catalog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    /**
     * {@inheritDoc}
     * <p/>
     * This method sets the user
     * defined time into the
     * system.
     *
     * @param view      the TimePicker Object
     * @param hourOfDay A numerical value representing the hour
     * @param minute    A numerical value representing the minute
     */
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextView time;

        if (getTag().equals("startTimePicker")) {
            time = (TextView) getActivity().findViewById(R.id.start_time_text);
        } else {
            time = (TextView) getActivity().findViewById(R.id.end_time_text);
        }

        if (minute > 10) {
            time.setText(hourOfDay + ":" + minute);
        } else {
            time.setText(hourOfDay + ":0" + minute);
        }
    }
}
