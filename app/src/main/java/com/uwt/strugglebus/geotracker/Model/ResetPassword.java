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
import com.uwt.strugglebus.geotracker.View.MyAccount;

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
 * The user must select the same security question as when they registered and
 * input the correct security answer.
 */
public class ResetPassword extends ActionBarActivity {

    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
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

        final String email = ((EditText) findViewById(R.id.reset_email)).getText().toString();
        final EditText answer = (EditText) findViewById(R.id.reset_answer);
        final EditText password = (EditText) findViewById(R.id.reset_password);
        final EditText confirmPassword = (EditText) findViewById(R.id.reset_confirm_password);

        Button accept = (Button) findViewById(R.id.reset_accept);
        Button cancel = (Button) findViewById(R.id.reset_cancel);

        accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String email = ((EditText) findViewById(R.id.reset_email)).getText().toString();
                    DownloadWebPageTask task = new DownloadWebPageTask();
                    String url = "http://450.atwebpages.com/reset.php?email=" + email;
                    task.execute(url);
/*
                    if( (savedEmail != null) && savedEmail.equals(email.getText().toString())
                            && savedA != null && savedA.equals(answer.getText().toString())
                            && password.getText().toString().equals(confirmPassword.getText().toString())) {
                        SharedPreferences.Editor edit = prefs.edit();
                        edit.putString(getString(R.string.password), password.getText().toString());
                        edit.apply();
                        Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(login);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.invalid), Toast.LENGTH_SHORT).show();
                    }*/
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
    /**
     * stuff for web services
     */
    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            mProgressDialog = ProgressDialog.show(CourseListActivity.this, "Wait", "Downloading...");
        }

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
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            mProgressDialog.dismiss();
            if (result != null) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String success = obj.getString("result");
                    Context context = mActivity.getApplicationContext();
                    System.out.println(success);
                    if(success != null && success.equals("success")) {
//                        Toast.makeText(context, obj.getString("message"), Toast.LENGTH_LONG).show();
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