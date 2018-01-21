package com.uwt.strugglebus.geotracker.Controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
 * * Alex Peterson, Chris Fahlin, Josh Moore, Kyle Martens
 * <p/>
 * This class sets up the appropriate logic to allow the user to reset their password.
 * The user must answer their security question correctly to trigger a response
 * from the web server.
 */
public class ResetPasswordActivity extends AppCompatActivity {

    private Activity mActivity;

    /**
     * {@inheritDoc}
     * <p/>
     * On top of the above
     * functionality this method
     * is in charge of setting up
     * the web service in charge of
     * changing the password.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(R.layout.activity_reset_password);

        Button accept = findViewById(R.id.reset_accept);
        Button cancel = findViewById(R.id.reset_cancel);

        accept.setOnClickListener(new View.OnClickListener() {
            /**
             * send reset request to web service
             */
            @Override
            public void onClick(View v) {
                final String email = ((EditText) findViewById(R.id.reset_email)).getText().toString();
                String url = "http://450.atwebpages.com/reset.php?email=" + email;
                new DownloadWebPageTask(ResetPasswordActivity.this).execute(url);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
              /**
               * goes back to login
               */
              @Override
              public void onClick(View v) {
                  Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                  startActivity(login);
                  finish();
              }
          }
        );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reset_password, menu);
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

        private final WeakReference<ResetPasswordActivity> resetPasswordActivityWeakReference;

        DownloadWebPageTask(ResetPasswordActivity context) {
            resetPasswordActivityWeakReference = new WeakReference<>(context);
        }

        /*
         * Inherited from
         * AsyncTask class
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            mProgressDialog = ProgressDialog.show(CourseListActivity.this, "Wait", "Downloading...");
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

            ResetPasswordActivity activity = resetPasswordActivityWeakReference.get();
            if(activity == null) return;

            if (result != null) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String success = obj.getString("result");
                    Context context = activity.mActivity.getApplicationContext();
                    if (success != null && success.equals("success")) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(activity.mActivity)
                                .setTitle(R.string.title_activity_reset_password)
                                .setMessage(obj.getString("message"))
                                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                                    // Mark this version as read.
                                    dialogInterface.dismiss();
                                    Intent login = new Intent(activity.mActivity, LoginActivity.class);
                                    activity.mActivity.startActivity(login);
                                    activity.mActivity.finish();
                                });
                        dialog.create().show();
                    } else {
                        Toast.makeText(context, obj.getString("error"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.i("json exception", e.getMessage());
                }
            }
        }
    }
}