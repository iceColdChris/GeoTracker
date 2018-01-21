package com.uwt.strugglebus.geotracker.Controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.uwt.strugglebus.geotracker.Model.LocationLog;
import com.uwt.strugglebus.geotracker.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * * Alex Peterson, Chris Fahlin, Josh Moore, Kyle Martens
 * <p/>
 * This class is in charge of
 * setting up the google maps
 * api as well as the view.
 * <p/>
 * This map will show the location
 * of each poll as an arrow on the
 * map.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private LocationLog mLocationLog;
    private Context mContext;
    private GoogleMap mGoogleMap;

    /**
     * {@inheritDoc}
     * <p/>
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
        mapFragment.getMapAsync(this);

        final SharedPreferences prefs = getSharedPreferences(getString(R.string.SHARED_PREFERENCES),
                Context.MODE_PRIVATE);
        String uid = prefs.getString("userID", "");

        String url = "http://450.atwebpages.com/view.php?uid=" + uid + "&start=" + 0 + "&end=" + System.currentTimeMillis();
        new DownloadWebPageTask(this).execute(url);
    }


    /**
     * {@inheritDoc}
     * <p/>
     * This method will be
     * in charge of adding the
     * location markers to the
     * map.
     *
     * @param map A google map object
     */
    @Override
    public void onMapReady(GoogleMap map) {

        mGoogleMap = map;

        if (mLocationLog != null) {

            List<Location> locations = mLocationLog.getLocationList();
            if (locations.size() > 0) {
                Location location = locations.get(0);
                LatLng firstLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                for (int i = 0; i < locations.size(); i++) {
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(locations.get(i).getLatitude()
                                    , locations.get(i).getLongitude()))
                            .title("My Locations"));
                }
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLatLng, 15));
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
    private static class DownloadWebPageTask extends AsyncTask<String, Void, String> {

        private final WeakReference<MapActivity> mapActivityReference;

        DownloadWebPageTask(MapActivity context) {
            mapActivityReference = new WeakReference<>(context);
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

            MapActivity activity = mapActivityReference.get();
            if(activity == null) return;

            if (result != null) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String success = obj.getString("result");
                    if (success != null && success.equals("success")) {
                        JSONArray points = new JSONArray(obj.getString("points"));
                        PolylineOptions line = new PolylineOptions();
                        for (int i = 0; i < points.length(); i++) {
                            JSONObject point = points.getJSONObject(i);
                            LatLng marker = new LatLng(point.getDouble("lat"), point.getDouble("lon"));
                            line.add(marker);
                            activity.mGoogleMap.addMarker(new MarkerOptions()
                                    .position(marker)
                                    .title("My Locations"));
                        }
                        LatLng lastLatLng = new LatLng(points.getJSONObject(points.length() - 1).getDouble("lat"),
                                points.getJSONObject(points.length() - 1).getDouble("lon"));
                        activity.mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, 15));
                        activity.mGoogleMap.addPolyline(line);
                    } else {
                        Toast.makeText(activity.mContext, obj.getString("error"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.i("json exception", e.getMessage());
                }
            }
        }
    }
}