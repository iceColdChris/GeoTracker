package com.uwt.strugglebus.geotracker.View;

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
 * Created by Josh on 5/3/2015.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

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

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextView time;
        // Do something with the time chosen by the user
        if(getTag().equals("startTimePicker")) {
            time = (TextView)getActivity().findViewById(R.id.start_time_text);
        } else {
            time = (TextView)getActivity().findViewById(R.id.end_time_text);
        }
        int hour = hourOfDay;
//        String ampm = "am";
//        if(hour == 0) {
//            hour = 12;
//        } else if(hour / 12 > 0) {
//            ampm = "pm";
//            hour = hour % 12;
//        }
        if(minute > 10) {
            time.setText(hour + ":" + minute);
        } else {
            time.setText(hour + ":0" + minute);
        }
    }
}
