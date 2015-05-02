package com.uwt.strugglebus.geotracker.Model;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.uwt.strugglebus.geotracker.R;
import com.uwt.strugglebus.geotracker.View.LoginActivity;

/**
 * This class sets up the appropriate logic to allow the user to reset their password.
 * The user must select the same security question as when they registered and
 * input the correct security answer.
 */
public class ResetPassword extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
//
//        final Spinner spinner = (Spinner) findViewById(R.id.reset_question_spinner);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.security_questions,
//                android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);

        final SharedPreferences prefs = getSharedPreferences(getString(R.string.SHARED_PREFERENCES), getApplicationContext().MODE_PRIVATE);
        String savedQ = prefs.getString(getString(R.string.security_q), "");

        final TextView security_question = (TextView) findViewById(R.id.security_question);
        security_question.setText(savedQ);

        final EditText email = (EditText) findViewById(R.id.reset_email);
        final EditText answer = (EditText) findViewById(R.id.reset_answer);
        final EditText password = (EditText) findViewById(R.id.reset_password);
        final EditText confirmPassword = (EditText) findViewById(R.id.reset_confirm_password);

        Button accept = (Button) findViewById(R.id.reset_accept);
        Button cancel = (Button) findViewById(R.id.reset_cancel);

        accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String savedEmail = prefs.getString(getString(R.string.email), null);
                    String savedA = prefs.getString(getString(R.string.security_a), null);



                    if( (savedEmail != null) && savedEmail.equals(email.getText().toString())
                            && savedA != null && savedA.equals(answer.getText().toString())
                            && password.getText().toString().equals(confirmPassword.getText().toString())) {
                        SharedPreferences.Editor edit = prefs.edit();
                        edit.putString(getString(R.string.password), password.getText().toString());
                        edit.commit();
                        Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(login);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.invalid), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        );

        cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(login);
                    finish();
                }
            }
        );

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reset_password, menu);
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
