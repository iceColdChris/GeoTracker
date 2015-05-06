package com.uwt.strugglebus.geotracker.View;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.uwt.strugglebus.geotracker.R;

import java.util.Calendar;

/**
 * Fragment popup for choosing a date
 * Created by Josh on 5/2/2015.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

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

    public void onDateSet(DatePicker view, int year, int month, int day) {
        TextView date;
        if(getTag().equals("startDatePicker")) {
            date = (TextView)getActivity().findViewById(R.id.start_date_text);

        } else {
            date = (TextView)getActivity().findViewById(R.id.end_date_text);
        }
        date.setText(""+ month + "/" + day + "/" + year);
    }
}