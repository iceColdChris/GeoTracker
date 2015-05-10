package com.uwt.strugglebus.geotracker.Controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.uwt.strugglebus.geotracker.Model.Registration;
import com.uwt.strugglebus.geotracker.R;
import com.uwt.strugglebus.geotracker.View.MyAccount;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * This class contains the logic for presenting the end user license agreement with the user.
 * They must read and accept the terms to use our app.
 */
public class Eula {

    private String EULA_PREFIX = "eula_";
    private Activity mActivity;
    private PackageInfo mVersionInfo;
    private AlertDialog.Builder mAlertBuilder;


    public Eula(Activity context) {
        mActivity = context;
    }

    private PackageInfo getPackageInfo() {
        PackageInfo pi = null;
        try {
            pi = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi;
    }

    public void download() {
        mVersionInfo = getPackageInfo();

        //Includes the updates as well so users know what changed.
        DownloadWebPageTask task = new DownloadWebPageTask();
        String url = "http://450.atwebpages.com/agreement.php";
        task.execute(url);
    }

    public void show() {
        mAlertBuilder.create().show();
        //Includes the updates as well so users know what changed.
//            String message = mActivity.getString(R.string.updates) + "\n\n" + mActivity.getString(R.string.eula);
/*        if(task.getStatus() == AsyncTask.Status.FINISHED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                    .setTitle(title)
                    .setMessage(mWebMessage)
                    .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Mark this version as read.
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("eula", true);
                            editor.apply();
                            dialogInterface.dismiss();
//                                Intent account = new Intent(mActivity, MyAccount.class);
//                                mActivity.startActivity(account);
//                                mActivity.finish();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new Dialog.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Close the activity as they have declined the EULA
                            dialog.dismiss();
                            mActivity.finish();
                        }

                    });
            builder.create().show();
        }*/
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

                    String agreement = obj.getString("agreement");
                    String title = mActivity.getString(R.string.app_name) + " v" + mVersionInfo.versionName;
                    mAlertBuilder = new AlertDialog.Builder(mActivity)
                            .setTitle(title)
                            .setMessage(agreement)
                            .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // Mark this version as read.
                                    final SharedPreferences prefs = mActivity.getSharedPreferences(mActivity.getString(R.string.SHARED_PREFERENCES), Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putBoolean(mActivity.getString(R.string.eula_accept), true);
                                    editor.apply();
                                    ((Registration) mActivity).sendData();
                                    dialogInterface.dismiss();
//                                    Intent account = new Intent(mActivity, MyAccount.class);
//                                    mActivity.startActivity(account);
//                                    mActivity.finish();
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, new Dialog.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Close the activity as they have declined the EULA
                                    dialog.dismiss();
//                                    mActivity.finish();
                                }

                            });
                    mAlertBuilder.create().show();
                } catch (JSONException e) {
                    System.out.println("JSON Exception" + e.getMessage());
                }
            }
        }
    }
}