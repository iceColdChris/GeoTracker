package com.uwt.strugglebus.geotracker;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.TextView;


public class MyAccount extends ActionBarActivity {

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        TextView email = (TextView) findViewById(R.id.account_email);
        TextView password = (TextView) findViewById(R.id.account_password);
        TextView question = (TextView) findViewById(R.id.account_question);
        TextView answer = (TextView) findViewById(R.id.account_answer);

        SharedPreferences prefs = getSharedPreferences(getString(R.string.SHARED_PREFERENCES),
                getApplicationContext().MODE_PRIVATE);
        int uid = prefs.getInt("uid", -1);
        String mEmail = prefs.getString(getString(R.string.email), "email");
        String mPass = prefs.getString(getString(R.string.password), "password");
        String mQuestion = prefs.getString(getString(R.string.security_q), "question");
        String mAnswer = prefs.getString(getString(R.string.security_a), "answer");

        email.setText(mEmail);
        password.setText(mPass);
        question.setText(mQuestion);
        answer.setText(mAnswer);

        mUser = new User(uid, mEmail, mPass, mQuestion, mAnswer);
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
                Intent login = new Intent(getApplicationContext(), Login.class);
                startActivity(login);
                finish();
                break;
            default:
                break;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}