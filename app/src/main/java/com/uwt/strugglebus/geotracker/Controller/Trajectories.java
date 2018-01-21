package com.uwt.strugglebus.geotracker.Controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.uwt.strugglebus.geotracker.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * * Alex Peterson, Chris Fahlin, Josh Moore, Kyle Martens
 * <p/>
 * This class is in charge of keeping
 * track of keeping track of the users
 * location.
 */
public class Trajectories extends AppCompatActivity {

    private Context mContext;
    /**
     * {@inheritDoc}
     * <p/>
     * Ontop of the above functionality
     * this method sets up the trajectory
     * database.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.activity_trajectories);

        final SharedPreferences prefs = getSharedPreferences(getString(R.string.SHARED_PREFERENCES),
                Context.MODE_PRIVATE);
        String uid = prefs.getString("userID", "");
        Intent it = getIntent();
        long startTime = it.getLongExtra("startTime", 0);
        long endTime = it.getLongExtra("endTime", 0);

        String url = "http://450.atwebpages.com/view.php?uid=" + uid + "&start=" + startTime + "&end=" + endTime;
        new DownloadWebPageTask(this).execute(url);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trajectories, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
 * This is a private helper class that is
 * in charge of connecting to the web
 * services as an Asyncronous Task.
 */
    private static class DownloadWebPageTask extends AsyncTask<String, Void, String> {
        private final WeakReference<Trajectories> trajectoriesWeakReference;

        DownloadWebPageTask(Trajectories context) {
            trajectoriesWeakReference = new WeakReference<>(context);
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

            Trajectories activity = trajectoriesWeakReference.get();
            if(activity == null) return;

            if (result != null) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String success = obj.getString("result");
                    if (success != null && success.equals("success")) {
                        JSONArray points = new JSONArray(obj.getString("points"));
                        TableLayout table = activity.findViewById(R.id.traject_table);
                        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                        for (int i = 0; i < points.length(); i++) {
                            JSONObject point = points.getJSONObject(i);
                            TableRow row = new TableRow(activity.mContext);
                            row.setLayoutParams(rowParams);
                            row.setPadding(5, 5, 5, 5);
                            String[] values = {

                                    //round the values to the nearest value
                                    activity.round(Double.parseDouble(point.getString("lat")), 4) + "",
                                    activity.round(Double.parseDouble(point.getString("lon")), 4) + "",
                                    activity.round(Double.parseDouble(point.getString("speed")), 4) + "",
                                    activity.round(Double.parseDouble(point.getString("heading")), 4) + "",
                                    point.getString("time")
                            };

                            for (String value : values) {
                                TextView temp = new TextView(activity.mContext);
                                temp.setBackgroundColor(Color.parseColor("#BBBBBB"));
                                temp.setPadding(5, 5, 5, 5);
                                temp.setText(value, TextView.BufferType.NORMAL);
                                temp.setTextColor(Color.BLACK);
                                row.addView(temp);
                            }
                            table.addView(row);

                        }
                    } else {
                        Toast.makeText(activity.mContext, obj.getString("error"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.i("json exception", e.getMessage());
                }
            }
        }
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
