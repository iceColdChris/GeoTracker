package com.uwt.strugglebus.geotracker.View;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Button;

import com.uwt.strugglebus.geotracker.Controller.User;
import com.uwt.strugglebus.geotracker.Model.LocationLog;
import com.uwt.strugglebus.geotracker.R;

import java.util.Calendar;

/**
 * The MyAccount class holding all of the information for the user's data.
 */
public class MyAccount extends ActionBarActivity {

    private User mUser;
    private LocationLog mLocationLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationLog = new LocationLog();
        setContentView(R.layout.activity_my_account);
        TextView email = (TextView) findViewById(R.id.account_email);
//        TextView password = (TextView) findViewById(R.id.account_password);
//        TextView question = (TextView) findViewById(R.id.account_question);
//        TextView answer = (TextView) findViewById(R.id.account_answer);

        SharedPreferences prefs = getSharedPreferences(getString(R.string.SHARED_PREFERENCES),
                getApplicationContext().MODE_PRIVATE);
        int uid = prefs.getInt("uid", -1);
        String mEmail = prefs.getString(getString(R.string.email), "email");
        String mPass = prefs.getString(getString(R.string.password), "password");
        String mQuestion = prefs.getString(getString(R.string.security_q), "question");
        String mAnswer = prefs.getString(getString(R.string.security_a), "answer");

        email.setText(mEmail);
//        password.setText(mPass);
//        question.setText(mQuestion);
//        answer.setText(mAnswer);

        mUser = new User(uid, mEmail, mPass, mQuestion, mAnswer);

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
        getMenuInflater().inflate(R.menu.menu_my_account, menu);
        return true;
    }

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

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


}