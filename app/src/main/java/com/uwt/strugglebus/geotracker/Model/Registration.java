package com.uwt.strugglebus.geotracker.Model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.uwt.strugglebus.geotracker.Controller.Eula;
import com.uwt.strugglebus.geotracker.R;
import com.uwt.strugglebus.geotracker.View.LoginActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class sets up the information needed for the registration process.
 * The appropriate logic is in place to check that the user has passed requirements to register.
 */
public class Registration extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //a spinner holding 4 different sequrity questions
        Spinner spinner = (Spinner) findViewById(R.id.question_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.security_questions,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Button accept = (Button) findViewById(R.id.reg_accept);
        Button cancel = (Button) findViewById(R.id.reg_cancel);
        final Activity mActivity = this;
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ((EditText) findViewById(R.id.reg_email)).getText().toString();
                String password = ((EditText) findViewById(R.id.reg_password)).getText().toString();
                String confirm_password = ((EditText) findViewById(R.id.reg_confirm_password)).getText().toString();
                String question = ((Spinner) findViewById(R.id.question_spinner)).getSelectedItem().toString();
                String answer = ((EditText) findViewById(R.id.security_answer)).getText().toString();

                //check to see if there is valid input TODO: test this if statement
                //!email.equals(null) && !password.equals(null) && !confirm_password.equals(null)
                //&& !answer.equals(null) && confirm_password.equals(password)
                String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
                Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(email);
                if(!matcher.matches()) {
                    Toast.makeText(getApplicationContext(), R.string.invalid_email_format, Toast.LENGTH_LONG).show();
                } else if(password.length() < 6) {
                    Toast.makeText(getApplicationContext(), R.string.invalid_password_format, Toast.LENGTH_LONG).show();
                } else if(!password.equals(confirm_password)) {
                    Toast.makeText(getApplicationContext(), R.string.invalid_confirm, Toast.LENGTH_LONG).show();
                } else if(answer.length() < 1) {
                    Toast.makeText(getApplicationContext(), R.string.invalid_security_a, Toast.LENGTH_LONG).show();
                }else {
                    //get unique id, and put into db
                    //save user data in shared preferences
                    SharedPreferences prefs = getSharedPreferences(getString(R.string.SHARED_PREFERENCES)
                            , Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    //change to unique id once db webservice is up
                    editor.putInt("uid",0);
                    editor.putString(getString(R.string.email), email);
                    editor.putString(getString(R.string.password), password);
                    editor.putString(getString(R.string.security_q), question);
                    editor.putString(getString(R.string.security_a), answer);
                    editor.commit();
                    //switch to eula
                    Eula eula = new Eula(mActivity);
                    eula.show();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(login);
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}