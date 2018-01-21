package com.uwt.strugglebus.geotracker.Controller;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.uwt.strugglebus.geotracker.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Alex Peterson, Chris Fahlin, Josh Moore, Kyle Martens
 * <p/>
 * This class contains the logic needed for the login page.
 * The necessary buttons are initialized and the appropriate logic to let the user log-in.
 */
public class LoginActivity extends AppCompatActivity {

    private String mEmail;
    private String mPassword;
    private Context mContext;

    /**
     * {@inheritDoc}
     * <p/>
     * On top of the above
     * functionality this
     * method connects to
     * the web service to
     * monitor the users
     * login.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button login = findViewById(R.id.login);
        Button register = findViewById(R.id.register);
        Button forgot = findViewById(R.id.forgot_password);
        mContext = getApplicationContext();

        final SharedPreferences prefs = getSharedPreferences(getString(R.string.SHARED_PREFERENCES),
                Context.MODE_PRIVATE);
        String uid = prefs.getString("userID", null);
        if (uid != null) { //user exists in shared prefs
            final EditText email = findViewById(R.id.email);
            email.setText(prefs.getString(getString(R.string.email), "email"));

            final EditText pw = findViewById(R.id.password);
            pw.setText(prefs.getString(getString(R.string.password), "password"));

            Intent account = new Intent(getApplicationContext(), MyAccount.class);
            startActivity(account);
            finish();

        }

        login.setOnClickListener(new View.OnClickListener() {
            /**
             * Check Async. email/pass are in the server
             * if correct goes to account
             * if not shows error
             */
            public void onClick(View v) {
                mEmail = ((EditText) findViewById(R.id.email)).getText().toString();
                mPassword = ((EditText) findViewById(R.id.password)).getText().toString();
                String url = "http://450.atwebpages.com/login.php?email=" + mEmail + "&password=" + mPassword;
                new DownloadWebPageTask(LoginActivity.this).execute(url);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            /**
             * Go to register activity
             */
            public void onClick(View v) {
                Intent register = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(register);
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            /**
             * Got to Forgot password page
             */
            public void onClick(View v) {
                Intent forgot = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                startActivity(forgot);
            }
        });
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
     * services as an Asyncronous Task.
     */
    private static class DownloadWebPageTask extends AsyncTask<String, Void, String> {

        private final WeakReference<LoginActivity> loginActivityWeakReference;

        DownloadWebPageTask(LoginActivity context) {
            loginActivityWeakReference = new WeakReference<>(context);
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

            LoginActivity activity = loginActivityWeakReference.get();
            if(activity == null) return;

            if (result != null) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String success = obj.getString("result");
                    Log.i("login", result);
                    Log.i("login", success);
                    if (success != null && success.equals("success")) {
                        SharedPreferences prefs = activity.getSharedPreferences(activity.getString(R.string.SHARED_PREFERENCES)
                                , Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("userID", obj.getString("userid"));
                        editor.putString(activity.getString(R.string.email), activity.mEmail);
                        editor.putString(activity.getString(R.string.password), activity.mPassword);
                        editor.apply();
                        Intent account = new Intent(activity.mContext, MyAccount.class);
                        activity.startActivity(account);
                    } else {
                        Toast.makeText(activity.mContext, obj.getString("error"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(activity.getApplicationContext(), "Error connecting to service!", Toast.LENGTH_LONG).show();
                    Log.i("json exception", e.getMessage());
                }
            }
        }
    }
}