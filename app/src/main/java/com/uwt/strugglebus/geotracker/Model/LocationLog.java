package com.uwt.strugglebus.geotracker.Model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * * Alex Peterson, Chris Fahlin, Josh Moore, Kyle Martens
 * <p/>
 * Created by mmuppa on 4/29/15.
 */
public class LocationLog implements Parcelable {

    /**
     * Creates a parcelable object
     */
    public static final Parcelable.Creator<LocationLog> CREATOR = new Parcelable.Creator<LocationLog>() {

        /**
         * {@inheritDoc}
         */
        public LocationLog createFromParcel(Parcel in) {
            return new LocationLog(in);
        }

        /**
         * {@inheritDoc}
         */
        public LocationLog[] newArray(int size) {
            return new LocationLog[size];
        }
    };
    private List<Location> mLocationList;

    public LocationLog() {
        mLocationList = new ArrayList<>();
    }

    /**
     * Creates a location log item
     *
     * @param in a parcel item
     */
    private LocationLog(Parcel in) {
        mLocationList = new ArrayList<>();
        mLocationList = in.readArrayList(Location.class.getClassLoader());
    }

    /**
     * Adds a location to the location log.
     *
     * @param location
     */
    public void addLocation(Location location) {
        mLocationList.add(location);
    }

    /**
     * Gets the location log
     *
     * @return the location log.
     */
    public List<Location> getLocationList() {
        return mLocationList;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(mLocationList);
    }

}