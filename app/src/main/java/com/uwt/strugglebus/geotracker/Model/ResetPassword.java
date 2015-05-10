package com.uwt.strugglebus.geotracker.Model;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * This class sets up the appropriate logic to allow the user to reset their password.
 * The user must answer their security question correctly to trigger a response
 * from the webserver.
 */
public class ResetPassword extends ActionBarActivity {

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

        Button accept = (Button) findViewById(R.id.reset_accept);
        Button cancel = (Button) findViewById(R.id.reset_cancel);

        accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String email = ((EditText) findViewById(R.id.reset_email)).getText().toString();
                    DownloadWebPageTask task = new DownloadWebPageTask();
                    String url = "http://450.atwebpages.com/reset.php?email=" + email;
                    task.execute(url);
                }
        });

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
     * services as an Asyncronous Task.
     */
    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {

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
            String response = "";
            for (String url : urls) {
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                try {
                    HttpResponse execute = client.execute(httpGet);
                    InputStream content = execute.getEntity().getContent();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s;
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return response;
        }

        /*
         * Checks if the user has
         * entered the correct credentials
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String success = obj.getString("result");
                    Context context = mActivity.getApplicationContext();
                    System.out.println(success);
                    if(success != null && success.equals("success")) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity)
                                .setTitle(R.string.title_activity_reset_password)
                                .setMessage(obj.getString("message"))
                                .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // Mark this version as read.
                                        dialogInterface.dismiss();
                                        Intent login = new Intent(mActivity, LoginActivity.class);
                                        mActivity.startActivity(login);
                                        mActivity.finish();
                                    }
                                });
                        dialog.create().show();
                    } else {
                        Toast.makeText(context, obj.getString("error"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    System.out.println("JSON Exception "+ e.getMessage());
                }
            }
        }
    }
}