package com.uwt.strugglebus.geotracker.Model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mmuppa on 4/29/15.
 */
public class LocationLog implements Parcelable {

    private List<Location> mLocationList;

    public LocationLog() {
        mLocationList = new ArrayList<Location>();
    }

    public LocationLog(Parcel in) {
        mLocationList = new ArrayList<Location>();
        mLocationList = in.readArrayList(Location.class.getClassLoader());
    }

    public void addLocation(Location location) {
        mLocationList.add(location);
    }

    public List<Location> getLocationList() {
        return mLocationList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(mLocationList);
    }

    public static final Parcelable.Creator<LocationLog> CREATOR = new Parcelable.Creator<LocationLog>() {

        public LocationLog createFromParcel(Parcel in) {
            return new LocationLog(in);
        }

        public LocationLog[] newArray(int size) {
            return new LocationLog[size];
        }
    };

}