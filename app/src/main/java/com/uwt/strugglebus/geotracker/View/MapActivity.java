package com.uwt.strugglebus.geotracker.View;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.uwt.strugglebus.geotracker.Model.LocationLog;
import com.uwt.strugglebus.geotracker.R;

import java.util.List;


public class MapActivity extends  ActionBarActivity implements OnMapReadyCallback {

    private LocationLog mLocationLog;
    private GoogleMap mGoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mLocationLog = getIntent().getParcelableExtra("locations");
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mGoogleMap = mapFragment.getMap();
        mapFragment.getMapAsync(this);

    }


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

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

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}