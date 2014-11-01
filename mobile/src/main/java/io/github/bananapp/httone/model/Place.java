package io.github.bananapp.httone.model;

import org.parceler.Parcel;
import org.parceler.Parcel.Serialization;
import org.parceler.ParcelConstructor;

@Parcel(Serialization.METHOD)
public class Place {

    private String mId;

    private double mLat;

    private double mLong;

    private String mName;

    private double mRadius;

    @ParcelConstructor
    public Place(final double lat, final double aLong, final double radius, final String name,
            final String id) {

        mLat = lat;
        mLong = aLong;
        mRadius = radius;
        mName = name;
        mId = id;
    }

    public String getId() {

        return mId;
    }

    public double getLat() {

        return mLat;
    }

    public double getLong() {

        return mLong;
    }

    public String getName() {

        return mName;
    }

    public double getRadius() {

        return mRadius;
    }
}
