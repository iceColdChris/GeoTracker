package com.uwt.strugglebus.geotracker.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.uwt.strugglebus.geotracker.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * * Alex Peterson, Chris Fahlin, Josh Moore, Kyle Martens
 * <p/>
 * This class sets up the information needed for the registration process.
 * The appropriate logic is in place to check that the user has passed requirements to register.
 */
public class RegistrationActivity extends AppCompatActivity {

    /**
     * Variables for RegistrationActivity.
     */
    private String mEmail;
    private String mPassword;
    private Activity mActivity;

    /**
     * {@inheritDoc}
     * <p/>
     * On top of the above
     * functionality this
     * method sets up the
     * registration web
     * services.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mActivity = this;

        //a spinner holding 4 different security questions
        Spinner spinner = findViewById(R.id.question_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.security_questions,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Button accept = findViewById(R.id.reg_accept);
        Button cancel = findViewById(R.id.reg_cancel);
        final Activity mActivity = this;

        //set the user's information
        accept.setOnClickListener(new View.OnClickListener() {
            /**
             * If input is valid send registration request to server and got back to login
             */
            @Override
            public void onClick(View v) {
                String email = ((EditText) findViewById(R.id.reg_email)).getText().toString();
                String password = ((EditText) findViewById(R.id.reg_password)).getText().toString();
                String confirm_password = ((EditText) findViewById(R.id.reg_confirm_password)).getText().toString();
                String question = ((Spinner) findViewById(R.id.question_spinner)).getSelectedItem().toString();
                String answer = ((EditText) findViewById(R.id.security_answer)).getText().toString();

                String expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
                Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(email);
                if (!matcher.matches()) {
                    Toast.makeText(getApplicationContext(), R.string.invalid_email_format, Toast.LENGTH_LONG).show();
                } else if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), R.string.invalid_password_format, Toast.LENGTH_LONG).show();
                } else if (!password.equals(confirm_password)) {
                    Toast.makeText(getApplicationContext(), R.string.invalid_confirm, Toast.LENGTH_LONG).show();
                } else if (answer.length() < 1) {
                    Toast.makeText(getApplicationContext(), R.string.invalid_security_a, Toast.LENGTH_LONG).show();
                } else {
                    //get unique id, and put into db
                    //save user data in shared preferences
                    SharedPreferences prefs = getSharedPreferences(getString(R.string.SHARED_PREFERENCES)
                            , Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    //change to unique id once db webservice is up
                    editor.putInt("uid", 0);
                    editor.putString(getString(R.string.email), email);
                    editor.putString(getString(R.string.password), password);
                    editor.putString(getString(R.string.security_q), question);
                    editor.putString(getString(R.string.security_a), answer);

                    Eula eula = new Eula(mActivity);
                    eula.download();
                    mEmail = email;
                    mPassword = password;

                    editor.apply();

                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            /**
             * go back to login
             */
            @Override
            public void onClick(View v) {
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(login);
                finish();
            }
        });
    }

    /**
     * This method sends data
     * to the webservice.
     */
    public void sendData() {
        String email = ((EditText) findViewById(R.id.reg_email)).getText().toString();
        String password = ((EditText) findViewById(R.id.reg_password)).getText().toString();
        String question = ((Spinner) findViewById(R.id.question_spinner)).getSelectedItem().toString();
        String answer = ((EditText) findViewById(R.id.security_answer)).getText().toString();
        SharedPreferences prefs = getSharedPreferences(getString(R.string.SHARED_PREFERENCES)
                , Context.MODE_PRIVATE);

        if (prefs.getBoolean(getString(R.string.eula_accept), false)) {
            question = question.replaceAll(" ", "%20");
            question = question.replace("?", "%3F");
            String url = "http://450.atwebpages.com/adduser.php?email=" + email + "&password=" + password +
                    "&question=" + question + "&answer=" + answer;
            new DownloadWebPageTask(this).execute(url);
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.invalid_confirm), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    /*
     * This is a private helper class that is
     * in charge of connecting to the web
     * services as an Asynchronous Task.
     */
    private static class DownloadWebPageTask extends AsyncTask<String, Void, String> {

        private final WeakReference<RegistrationActivity> registrationReference;

        DownloadWebPageTask(RegistrationActivity context) {
            registrationReference = new WeakReference<>(context);
        }

        /*
         * Inherited from
         * AsyncTask class
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /*
         * Gets the response string
         * from the webservice.
         */
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder response = new StringBuilder();
            HttpURLConnection urlConnection;
            for (String url : urls) {
                try {
                    urlConnection = (HttpURLConnection) new URL(url).openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty("Content-length", "0");
                    urlConnection.setUseCaches(false);
                    urlConnection.setAllowUserInteraction(false);
                    urlConnection.setConnectTimeout(100000);
                    urlConnection.setReadTimeout(100000);

                    urlConnection.connect();

                    int responseCode = urlConnection.getResponseCode();

                    if(responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader buffer = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        String s;
                        while ((s = buffer.readLine()) != null) {
                            response.append(s);
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return response.toString();
        }

        /*
         * Checks if the user has
         * entered the correct credentials
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            RegistrationActivity activity = registrationReference.get();
            if(activity == null) return;

            if (result != null) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String success = obj.getString("result");
                    SharedPreferences prefs = activity.getSharedPreferences(activity.getString(R.string.SHARED_PREFERENCES)
                            , Context.MODE_PRIVATE);
                    if (success != null && success.equals("fail")) {
                        Toast.makeText(activity.getApplicationContext(), obj.getString("error"), Toast.LENGTH_LONG).show();
                    } else {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(activity.getString(R.string.email), activity.mEmail);
                        editor.putString(activity.getString(R.string.password), activity.mPassword);
                        editor.apply();
                        Intent login = new Intent(activity.getApplicationContext(), LoginActivity.class);
                        activity.startActivity(login);
                        activity.mActivity.finish();
                    }
                } catch (JSONException e) {
                    Log.i("json exception", e.getMessage());
                }
            }
        }
    }
}
