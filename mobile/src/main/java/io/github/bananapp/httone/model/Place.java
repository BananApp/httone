package io.github.bananapp.httone.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;
import org.parceler.Parcel.Serialization;
import org.parceler.ParcelConstructor;

@Parcel(Serialization.METHOD)
public class Place {

    @SerializedName("id")
    private int mId;

    @SerializedName("latitude")
    private double mLat;

    @SerializedName("longitude")
    private double mLong;

    @SerializedName("name")
    private String mName;

    @SerializedName("radius")
    private double mRadius;

    @ParcelConstructor
    public Place(final double lat, final double aLong, final double radius, final String name,
            final String id) {

        mLat = lat;
        mLong = aLong;
        mRadius = radius;
        mName = name;
        mId = Integer.parseInt(id);
    }

    public String getId() {

        return Integer.toString(mId);
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
