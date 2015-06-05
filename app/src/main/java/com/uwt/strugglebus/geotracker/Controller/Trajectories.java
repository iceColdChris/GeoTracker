package com.uwt.strugglebus.geotracker.Controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.uwt.strugglebus.geotracker.R;

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
 * * Alex Peterson, Chris Fahlin, Josh Moore, Kyle Martens
 *
 * This class is in charge of keeping
 * track of keeping track of the users
 * location.
 */
public class Trajectories extends ActionBarActivity {

    private static final String DB_NAME = "Trajectories";
    private static final String TABLE = "Locations";
    private static final int ROWS = 5;

    private Context mContext;

    /**
     * {@inheritDoc}
     *
     * Ontop of the above functionality
     * this method sets up the trajectory
     * database.
     *
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.activity_trajectories);

        final  SharedPreferences prefs = getSharedPreferences(getString(R.string.SHARED_PREFERENCES),
                Context.MODE_PRIVATE);
        String uid = prefs.getString("userID", "");
        Intent it = getIntent();
        long startTime = it.getLongExtra("startTime", 0);
        long endTime = it.getLongExtra("endTime", 0);

        DownloadWebPageTask task = new DownloadWebPageTask();
        String url = "http://450.atwebpages.com/view.php?uid=" + uid + "&start=" + startTime + "&end=" + endTime;
        task.execute(url);

//        TableLayout table = (TableLayout) findViewById(R.id.traject_table);
//        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
//
//        final SQLiteDatabase db = openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
//
//        String query = "SELECT * FROM " + TABLE + ";";
//        Cursor c = db.rawQuery(query, null);
//        while(c.moveToNext()) {
//            TableRow row = new TableRow(getApplicationContext());
//            row.setLayoutParams(rowParams);
//            row.setPadding(5, 5, 5, 5);
//            String[] values = new String[ROWS];
//            values[0] = c.getFloat(0) + "";
//            values[1] = c.getFloat(1) + "";
//            values[2] = c.getFloat(2) + "";
//            values[3] = c.getFloat(3) + "";
//            values[4] = c.getInt(4) + "";
//
//            for(int i = 0; i < ROWS; i++) {
//                TextView temp = new TextView(getApplicationContext());
//                temp.setBackgroundColor(Color.parseColor("#BBBBBB"));
//                temp.setPadding(5,5,5,5);
//                temp.setText(values[i], TextView.BufferType.NORMAL);
//                temp.setTextColor(Color.BLACK);
//                row.addView(temp);
//            }
//
//
//   table.addView(row);
//        }
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
     *{@inheritDoc}
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
    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {


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
//            mProgressDialog.dismiss();
            if (result != null) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String success = obj.getString("result");
                    if(success != null && success.equals("success")) {
                        JSONArray points = new JSONArray(obj.getString("points"));
                        TableLayout table = (TableLayout) findViewById(R.id.traject_table);
                        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                        for(int i = 0; i < points.length(); i++) {
                            JSONObject point = points.getJSONObject(i);
                            TableRow row = new TableRow(mContext);
                            row.setLayoutParams(rowParams);
                            row.setPadding(5, 5, 5, 5);
                            String[] values = {
                                    point.getString("lat"),
                                    point.getString("lon"),
                                    point.getString("speed"),
                                    point.getString("heading"),
                                    point.getString("time")
                            };

                            for(int j = 0; j < values.length; j++) {
                                TextView temp = new TextView(mContext);
                                temp.setBackgroundColor(Color.parseColor("#BBBBBB"));
                                temp.setPadding(5,5,5,5);
                                temp.setText(values[j], TextView.BufferType.NORMAL);
                                temp.setTextColor(Color.BLACK);
                                row.addView(temp);
                            }
                            table.addView(row);

                        }
                    } else {
                        Toast.makeText(mContext, obj.getString("error"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.i("json exception", e.getMessage());
                }
            }
        }
    }
}
