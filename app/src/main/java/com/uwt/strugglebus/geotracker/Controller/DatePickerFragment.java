package com.uwt.strugglebus.geotracker.Controller;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import com.uwt.strugglebus.geotracker.R;

import java.util.Calendar;

/**
 * * Alex Peterson, Chris Fahlin, Josh Moore, Kyle Martens
 * This class is in charge of
 * the date picker the user
 * sees when choosing what
 * date they want to show.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    /**
     * {@inheritDoc}
     * <p/>
     * On top of the above functionality
     * this method sets up a calendar
     * object as well as a date picker
     * catalog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * This method sets the user
     * defined date into the
     * system.
     *
     * @param view  A DatePicker object.
     * @param year  A numerical value representing the year
     * @param month A numerical value representing the Month
     * @param day   A numerical value representing the Day
     */
    public void onDateSet(DatePicker view, int year, int month, int day) {
        TextView date;
        if (getTag().equals("startDatePicker")) {
            date = (TextView) getActivity().findViewById(R.id.start_date_text);

        } else {
            date = (TextView) getActivity().findViewById(R.id.end_date_text);
        }
        date.setText("" + (1 + month) + "/" + day + "/" + year);
    }
}