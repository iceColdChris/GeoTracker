package com.uwt.strugglebus.geotracker.Controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
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
 * This class contains the logic for presenting the end user license agreement with the user.
 * They must read and accept the terms to use our app.
 */
class Eula {

    private final Activity mActivity;
    private PackageInfo mVersionInfo;
    private AlertDialog.Builder mAlertBuilder;

    /**
     * Constructor for the EULA.
     *
     * @param context The activity that calls this
     */
    Eula(Activity context) {
        mActivity = context;
    }

    /**
     * Retrieves and returns the activity package info
     */
    private PackageInfo getPackageInfo() {
        PackageInfo pi = null;
        try {
            pi = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi;
    }

    /**
     * Downloads the EULA
     * from the webservice.
     */
    void download() {
        mVersionInfo = getPackageInfo();

        //Includes the updates as well so users know what changed.
        DownloadWebPageTask task = new DownloadWebPageTask(this);
        String url = "http://450.atwebpages.com/agreement.php";
        task.execute(url);
    }

    /*
     * This is a private helper class that is
     * in charge of connecting to the web
     * services as an Asynchronous Task.
     */
    private static class DownloadWebPageTask extends AsyncTask<String, Void, String> {

        private final WeakReference<Eula> eulaWeakReference;

        DownloadWebPageTask(Eula context) {
            eulaWeakReference = new WeakReference<>(context);
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

            Eula eula = eulaWeakReference.get();
            if(eula == null) return;

            if (result != null) {
                try {
                    JSONObject obj = new JSONObject(result);

                    String agreement = obj.getString("agreement");
                    String title = eula.mActivity.getString(R.string.app_name) + " v" + eula.mVersionInfo.versionName;
                    eula.mAlertBuilder = new AlertDialog.Builder(eula.mActivity)
                            .setTitle(title)
                            .setMessage(Html.fromHtml(agreement))
                            .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {
                                /**
                                 * registers account and adds shared prefs values
                                 */
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // Mark this version as read.
                                    final SharedPreferences prefs = eula.mActivity.getSharedPreferences(eula.mActivity.getString(R.string.SHARED_PREFERENCES), Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putBoolean(eula.mActivity.getString(R.string.eula_accept), true);
                                    editor.apply();
                                    ((RegistrationActivity) eula.mActivity).sendData();
                                    Toast.makeText(eula.mActivity, R.string.eula_toast, Toast.LENGTH_LONG).show();
                                    dialogInterface.dismiss();
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, new Dialog.OnClickListener() {
                                /**
                                 * closes dialog
                                 */
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Close the activity as they have declined the EULA
                                    dialog.dismiss();
                                }

                            });
                    eula.mAlertBuilder.create().show();
                } catch (JSONException e) {
                    Log.i("json exception", e.getMessage());
                }
            }
        }
    }
}
