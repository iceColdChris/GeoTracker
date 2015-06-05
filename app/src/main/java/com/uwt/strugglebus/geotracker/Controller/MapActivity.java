package com.uwt.strugglebus.geotracker.Controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.uwt.strugglebus.geotracker.Model.LocationLog;
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
import java.util.List;

/**
 * * Alex Peterson, Chris Fahlin, Josh Moore, Kyle Martens
 *
 * This class is in charge of
 * setting up the google maps
 * api as well as the view.
 *
 * This map will show the location
 * of each poll as an arrow on the
 * map.
 */
public class MapActivity extends  ActionBarActivity implements OnMapReadyCallback {

    private LocationLog mLocationLog;
    private GoogleMap mGoogleMap;
    private Context mContext;

    /**
     * {@inheritDoc}
     *
     * On top of the above
     * functionality this method
     * sets up an instance of
     * google maps.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mContext = getApplicationContext();
        mLocationLog = getIntent().getParcelableExtra("locations");
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mGoogleMap = mapFragment.getMap();
        mapFragment.getMapAsync(this);

        final SharedPreferences prefs = getSharedPreferences(getString(R.string.SHARED_PREFERENCES),
                Context.MODE_PRIVATE);
        String uid = prefs.getString("userID", "");

        DownloadWebPageTask task = new DownloadWebPageTask();
        String url = "http://450.atwebpages.com/view.php?uid=" + uid + "&start=" + 0 + "&end=" + System.currentTimeMillis();
        task.execute(url);
    }


    /**
     * {@inheritDoc}
     *
     * This method will be
     * in charge of adding the
     * location markers to the
     * map.
     *
     * @param map A google map object
     */
    @Override
    public void onMapReady(GoogleMap map) {

        if (mLocationLog != null) {

            List<Location> locations = mLocationLog.getLocationList();
            if(locations.size() > 0) {
                Location location = locations.get(0);
                LatLng firstLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                for (int i = 0; i < locations.size(); i++) {
                    Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(locations.get(i).getLatitude()
                                    , locations.get(i).getLongitude()))
                            .title("My Locations"));
                }
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLatLng, 15));
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
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
        switch (id) {
            case R.id.action_logout:
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(login);
                finish();
                break;
            case R.id.action_account:
                Intent account = new Intent(getApplicationContext(), MyAccount.class);
                startActivity(account);
                break;
            default:
                break;
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
                        PolylineOptions line = new PolylineOptions();
                        for(int i = 0; i < points.length(); i++) {
                            JSONObject point = points.getJSONObject(i);
                            LatLng marker = new LatLng(point.getDouble("lat"), point.getDouble("lon"));
                            line.add(marker);
                            mGoogleMap.addMarker(new MarkerOptions()
                                    .position(marker)
                                    .title("My Locations"));
                        }
                        LatLng lastLatLng = new LatLng(points.getJSONObject(points.length() - 1).getDouble("lat"),
                                points.getJSONObject(points.length() - 1).getDouble("lon"));
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, 15));
                        mGoogleMap.addPolyline(line);
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